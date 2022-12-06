package org.maggus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.maggus.ParsingUtils.safeIntegerListContains;

@Slf4j
public class ExtractGamesApp {

    public static final String IN_FILE_NAME = "ranked-bgg-games-details.json";
    public static final String OUT_FILE_NAME = "extract-bgg-games-details.json";

    public final Double MIN_RATING = 6.0;       // minimal game rating (popularity)
    public final Double MIN_WEIGHT = 3.0;       // minimal game weight (complexity)
    public final Integer PLAYERS_NUM = 4;       // desired number of players
    public final boolean BEST_PLAYERS = true;   // include games "Best" for number of players
    public final boolean GOOD_PLAYERS = true;   // include games "Recommended" for number of players

    public List<GameDetails> allGames = new ArrayList<>();
    private long ts0 = 0, ts1 = 0;

    public void dumpToFile(String fileName) throws IOException {
        log.info("Writing to file: {}", fileName);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(fileName), allGames);
        ts1 = System.currentTimeMillis();
        log.info("Done: in {}ms", ts1 - ts0);
    }

    public void extractAllGamesDetails(String fileNameIn) throws IOException {
        ts0 = System.currentTimeMillis();
        log.info("*** extracting games...");

        // load game ids from json file.
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(fileNameIn);
        if (!file.canRead()) {
            log.error("Can not find input file {}. Please dump Games Details Info first.", fileNameIn);
        }
        List<GameDetails> games = mapper.readValue(file, new TypeReference<List<GameDetails>>() {
        });
        log.info("Loaded file: {}, found games: {}", fileNameIn, games.size());

        // query game details for each id
        for (GameDetails game : games) {
            if (game.getBggId() == null || game.getName() == null) {
                log.error("! Error in game info {} - {}", game.getBggId(), game.getName());
                continue;
            }
            if (filterGame(game)) {
                allGames.add(game);
            }
        }
        log.info("Total extracted games: {}", allGames.size());
    }

    private boolean filterGame(GameDetails game) {
        if (MIN_RATING != null && (game.getAvgRating() == null || game.getAvgRating() < MIN_RATING)) {
            return false;
        }
        if (MIN_WEIGHT != null && (game.getAvgWeight() == null || game.getAvgWeight() < MIN_WEIGHT)) {
            return false;
        }
        boolean isBest = BEST_PLAYERS && safeIntegerListContains(PLAYERS_NUM, game.getBestPlayers());
        boolean isGood = GOOD_PLAYERS && (isBest || safeIntegerListContains(PLAYERS_NUM, game.getGoodPlayers()));
        if (PLAYERS_NUM != null && !isBest && !isGood) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            ExtractGamesApp app = new ExtractGamesApp();
            app.extractAllGamesDetails(IN_FILE_NAME);

            app.dumpToFile(OUT_FILE_NAME);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
