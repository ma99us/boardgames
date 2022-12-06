package org.maggus;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.maggus.BggConfig.BGG_PASSWORD_HASH;
import static org.maggus.BggConfig.BGG_USER_NAME;
import static org.maggus.ParsingUtils.*;

@Slf4j
public class DumpBasicGamesApp {

    public final String BGG_ALL_GAMES_URL = "https://boardgamegeek.com/browse/boardgame/page/";     // append X - page number
    public static final String OUT_FILE_NAME = "ranked-bgg-games.json";


    public List<BasicGame> allGames = new ArrayList<>();
    private long ts0 = 0, ts1 = 0;

    public void dumpToFile(String fileName) throws IOException {
        log.info("writing to file: {}", fileName);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(fileName), allGames);
        ts1 = System.currentTimeMillis();
        log.info("Done: in {}ms", ts1 - ts0);
    }

    public void loadAllGames() throws IOException {
        ts0 = System.currentTimeMillis();
        log.info("*** loading all games...");
        int page = 1;
        while (true) {
            List<BasicGame> games = loadGamesPage(page);
            if (games.isEmpty()) {
                log.info("Last page: {}; total games: {}", page, allGames.size());
                break;
            }
            allGames.addAll(games);
            page++;
        }
    }

    public List<BasicGame> loadGamesPage(int pageNum) throws IOException {
        Document doc = Jsoup.connect(BGG_ALL_GAMES_URL + pageNum)
                .cookie("bggusername", BGG_USER_NAME)
                .cookie("bggpassword", BGG_PASSWORD_HASH)
                .get();
        String title = doc.title();
        log.debug("Page #{} - {}", pageNum, title);

        ArrayList<BasicGame> games = new ArrayList<>();
        Elements rows = doc.select("tr#row_");
        if (rows.isEmpty()) {
            log.warn("No games on page #{}", pageNum);
            return games;
        }
        rows.stream().forEach(row -> {
            Integer rank = safeParseInteger(selectFirstText(row, "td.collection_rank"));
            Long id = parseBggId(select(row, "td.collection_thumbnail > a").map(els -> els.first().attr("href")).orElse(null));
            String thumbUrl = select(row,"td.collection_thumbnail > a > img").map(els -> els.first().attr("src")).orElse(null);
            String name = selectFirstText(row, "td.collection_objectname > div > a");
            Integer year = parseBggYear(selectFirstText(row, "td.collection_objectname > div > span"));
            String shortDesc = selectFirstText(row, "td.collection_objectname > p");
            Double geekRating = safeParseDouble(select(row, "td.collection_bggrating").map(els -> els.get(0).text()).orElse(null));
            Double avgRating = safeParseDouble(select(row, "td.collection_bggrating").map(els -> els.get(1).text()).orElse(null));
            Integer numVotes = safeParseInteger(select(row, "td.collection_bggrating").map(els -> els.get(2).text()).orElse(null));
//            log.debug("{}-{}-{}-{}-{}-{}-{}-{}", rank, id, thumbUrl, name, year, geekRating, avgRating, numVotes);

            if (rank == null) {
                // if game has no rank, don't bother with it
                log.warn("Ignoring game: {}", name);
                return;
            }
            if (id == null || name == null) {
                log.error("! Error parsing game {} - {}", id, name);
            }
            games.add(new BasicGame(rank, id, thumbUrl, name, shortDesc, year, geekRating, avgRating, numVotes));
        });
        if (games.isEmpty()) {
            log.warn("No parsed games on page #{}", pageNum);
        } else {
            log.debug("{} games parsed on page #{}", games.size(), pageNum);
        }
        return games;
    }

    private Long parseBggId(String idHref) {
        if (idHref == null) {
            return null;
        }
        final String TAG_0 = "/boardgame/";
        final String TAG_1 = "/";
        int p0 = idHref.indexOf(TAG_0);
        if (p0 < 0) {
            return null;
        }
        String sub = idHref.substring(p0 + TAG_0.length());
        int p1 = sub.indexOf(TAG_1);
        if (p1 < 0) {
            p1 = sub.length();
        }
        String idStr = sub.substring(0, p1);
        return safeParseLong(idStr);
    }

    private Integer parseBggYear(String yearStr) {
        if (yearStr == null) {
            return null;
        }
        String sub = yearStr.replaceAll("[^\\d.]", "");
        return safeParseInteger(sub);
    }

    private String selectFirstText(Element parent, String selector) {
        return select(parent, selector).map(els -> els.first().text()).orElse(null);
    }

    private Optional<Elements> select(Element parent, String selector) {
        Elements el = parent.select(selector);
        return Optional.ofNullable(!el.isEmpty() ? el : null);
    }

    public static void main(String[] args) {
        try {
            DumpBasicGamesApp app = new DumpBasicGamesApp();
            app.loadAllGames();
//            app.allGames.addAll(app.loadGamesPage(30));   // TEST

            app.dumpToFile(OUT_FILE_NAME);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
