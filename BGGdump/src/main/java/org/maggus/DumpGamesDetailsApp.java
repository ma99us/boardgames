package org.maggus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.maggus.ParsingUtils.*;

@Slf4j
public class DumpGamesDetailsApp {

    public final String BGG_GAMES_DETAILS_API = "https://api.geekdo.com/xmlapi/boardgame/";     // append X,Y,Z - games ids
    public final String REQ_QUERY = "?stats=1";

    public static final String IN_FILE_NAME = "ranked-bgg-games.json";
    public static final String OUT_FILE_NAME = "ranked-bgg-games-details.json";
    public final int IDS_PER_REQ = 100;
    public final int MIN_VOTERS = 5;

    public List<GameDetails> allGames = new ArrayList<>();
    private long ts0 = 0, ts1 = 0;

    public void dumpToFile(String fileName) throws IOException {
        log.info("Writing to file: {}", fileName);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(fileName), allGames);
        ts1 = System.currentTimeMillis();
        log.info("Done: in {}ms", ts1 - ts0);
    }

    public void loadAllGamesDetails(String fileNameIn) throws IOException {
        ts0 = System.currentTimeMillis();
        log.info("*** loading all games details...");

        // load game ids from json file.
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(fileNameIn);
        if (!file.canRead()) {
            log.error("Can not find input file {}. Please dump Basic Game Info first.", fileNameIn);
        }
        List<BasicGame> basicGames = mapper.readValue(file, new TypeReference<List<BasicGame>>() {
        });
        log.info("Loaded file: {}, found games: {}", fileNameIn, basicGames.size());

        // query game details for each id
        ArrayList<Long> ids = new ArrayList<>(IDS_PER_REQ);
        for (BasicGame game : basicGames) {
            if (game.getBggId() == null || game.getName() == null) {
                log.error("! Error in game info {} - {}", game.getBggId(), game.getName());
                continue;
            }

            ids.add(game.getBggId());
            if (ids.size() >= IDS_PER_REQ) {
                List<GameDetails> gameDetails = loadGamesDetails(ids.stream().mapToLong(l -> l).toArray());
                //FIXME: maybe save into separate files instead?
                allGames.addAll(gameDetails);
                ids.clear();
            }
        }
        log.info("Total queried games: {}", allGames.size());
    }

    public List<GameDetails> loadGamesDetails(long... gameIds) throws IOException {
        log.info("Querying games: {}", Arrays.toString(gameIds));
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(BGG_GAMES_DETAILS_API + joinLongArray(",", gameIds) + REQ_QUERY).openStream());
            ArrayList<GameDetails> games = new ArrayList<>();
            NodeList boardgames = doc.getElementsByTagName("boardgame");
            for (int i = 0; i < boardgames.getLength(); i++) {
                Node node = boardgames.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element boardgame = (Element) node;
                Long objectid = safeParseLong(boardgame.getAttribute("objectid"));
                if (!containsLongArray(objectid, gameIds)) {
                    log.warn("Ignoring objectid: {}, not requested in: {}", objectid, Arrays.toString(gameIds));
                    continue;
                }
                String name = getFirstNodeText(boardgame.getElementsByTagName("name"), "primary");
                String description = getFirstNodeText(boardgame.getElementsByTagName("description"), null);
                String thumbnail = getFirstNodeText(boardgame.getElementsByTagName("thumbnail"), null);
                String image = getFirstNodeText(boardgame.getElementsByTagName("image"), null);
                Integer minplayers = safeParseInteger(getFirstNodeText(boardgame.getElementsByTagName("minplayers"), null));
                Integer maxplayers = safeParseInteger(getFirstNodeText(boardgame.getElementsByTagName("maxplayers"), null));
                Integer yearpublished = safeParseInteger(getFirstNodeText(boardgame.getElementsByTagName("yearpublished"), null));
                String boardgamepublisher = getFirstNodeText(boardgame.getElementsByTagName("boardgamepublisher"), null);
                String boardgamedesigner = getAllNodesText(boardgame.getElementsByTagName("boardgamedesigner"));
                String boardgameartist = getAllNodesText(boardgame.getElementsByTagName("boardgameartist"));
                String boardgamehonor = getFirstNodeText(boardgame.getElementsByTagName("boardgamehonor"), null);
                Element playersNode = getNodeByAtr(boardgame.getElementsByTagName("poll"), "name", "suggested_numplayers");
                List<Integer> bestPlayers = parseSuggestedPlayers(playersNode, "Best");
                List<Integer> goodPlayers = parseSuggestedPlayers(playersNode, "Recommended");
                Integer usersrated = safeParseInteger(getFirstNodeText(boardgame.getElementsByTagName("usersrated"), null));
                Double avgRating = usersrated >= MIN_VOTERS ? safeParseDouble(getFirstNodeText(boardgame.getElementsByTagName("average"), null)) : null;
                Integer numweights = safeParseInteger(getFirstNodeText(boardgame.getElementsByTagName("numweights"), null));
                Double avgWeight = numweights >= MIN_VOTERS ? safeParseDouble(getFirstNodeText(boardgame.getElementsByTagName("averageweight"), null)): null;
//                log.debug("{}-{}-B:{}-G:{}-R:{}-W:{}", objectid, name, bestPlayers, goodPlayers, avgRating, avgWeight);

                games.add(new GameDetails(objectid, name, description, thumbnail, image, minplayers, maxplayers, yearpublished,
                        boardgamepublisher, boardgamedesigner, boardgameartist, boardgamehonor, bestPlayers, goodPlayers,
                        avgRating, avgWeight));
            }
            return games;
        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException(ex);
        }
    }

    private List<Integer> parseSuggestedPlayers(Element playersNode, String mode) {
        ArrayList<Integer> players = new ArrayList<>();
        NodeList results = playersNode.getElementsByTagName("results");
        for (int i = 0; i < results.getLength(); i++) {
            Node node = results.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element resultsEl = (Element) node;
            Integer numplayers = safeParseInteger(resultsEl.getAttribute("numplayers"));
            NodeList resultList = resultsEl.getElementsByTagName("result");
            String resMaxValue = null;
            int resMaxVotes = 0;
            for (int ii = 0; ii < resultList.getLength(); ii++) {
                Node resNode = resultList.item(ii);
                if (resNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element resultEl = (Element) resNode;
                String resValue = resultEl.getAttribute("value");
                Integer resNumVotes = safeParseInteger(resultEl.getAttribute("numvotes"));
                if (resNumVotes != null && resNumVotes > resMaxVotes) {
                    resMaxVotes = resNumVotes;
                    resMaxValue = resValue;
                }
            }
            if (mode.equals(resMaxValue)) {
                players.add(numplayers);
            }
        }
        return players;
    }

    private String getAllNodesText(NodeList nodes) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) node;
            strings.add(el.getTextContent());
        }
        return !strings.isEmpty() ? String.join(", ", strings) : null;
    }

    private String getFirstNodeText(NodeList nodes, String attrib) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) node;
            if (attrib == null || !el.getAttribute(attrib).isEmpty()) {
                return el.getTextContent();
            }
        }
        return null;
    }

    private Element getNodeByAtr(NodeList nodes, String attrib, String atrValue) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) node;
            if (el.getAttribute(attrib).equals(atrValue)) {
                return el;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            DumpGamesDetailsApp app = new DumpGamesDetailsApp();
            app.loadAllGamesDetails(IN_FILE_NAME);
//            app.allGames.addAll(app.loadGamesDetails(822,174430));   // TEST

            app.dumpToFile(OUT_FILE_NAME);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
