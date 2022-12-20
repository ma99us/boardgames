import axios from "axios";

export const BGG_SNAPSHOT_URL = "https://4ovtpf6sj9.execute-api.us-east-1.amazonaws.com";

export const HFG_GAMES_API = "https://hfgclub.ca/games_api?games=all";  // get all games owned by HFG club

export const HFG_GAME_DETAIL_URL = "https://hfgclub.ca/games?game_id="; // append HFG game id to it

export const api = axios.create({
  headers: {
    "Content-Type": "application/json",
  },
});
