import {defineStore} from "pinia";
import type {Game, HfgGame} from "@/stores/game";
import {api, BGG_SNAPSHOT_URL, HFG_GAMES_API} from "@/stores/api";

export const useGamesStore = defineStore("games", {
  state: () => {
    return {
      games: [] as Game[],
      hfgGames: new Map<number, HfgGame>(),
      hfgOwned: true,
      page: 1,
      sortBy: "n",
      minR: 6.0,
      minW: 3.0,
      limit: 100,
      isDone: false,
      error: null as any | null
    };
  },
  getters: {
    // notWarGames: (state) => state.games.filter(game => game.type !=== 'war'),
  },
  actions: {
    async loadHfgGames(): Promise<Map<number, HfgGame>> {
      if (this.hfgGames.size) {
        return this.hfgGames;
      }
      console.log("loading HFG games");
      this.error = null;
      try {
        const response = await api.get<{ games: HfgGame[] }>(HFG_GAMES_API);
        const games = response.data?.games || [];
        this.hfgGames = games
          .filter(game => game.bggId)
          .reduce((map, game) => map.set(game.bggId!, game), new Map<number, HfgGame>());
        return this.hfgGames
      } catch (error) {
        this.error = error;
        console.error(error);
        return new Map<number, HfgGame>();
      }
    },

    async loadGameDetails(bggId: number): Promise<Game | null> {
      console.log("loading game #" + bggId + " details");
      this.error = null;
      try {
        const data = {
          query: `query GameQuery {game(id:${bggId}) 
                    {bggId, name, description, thumbnailUrl, imageUrl, minPlayers, maxPlayers, yearPublished, publisher, designer, artist, honors, bestPlayers, goodPlayers, avgRating, avgWeight}}`,
        };
        const response = await api.post<{ data: { game: Game } }>(
          BGG_SNAPSHOT_URL,
          data
        );
        return response.data?.data?.game;
      } catch (error) {
        this.error = error;
        console.error(error);
        return null;
      }
    },

    async loadSomeGames(): Promise<Game[]> {
      this.error = null;
      try {
        const offset = (this.page - 1) * this.limit;
        const data = {
          query: `query AllGamesQuery {games(minR:${this.minR}, minW:${this.minW}, sortBy:"${this.sortBy}", offset:${offset}, limit:${this.limit}) 
                    {bggId, name, thumbnailUrl, avgRating, avgWeight}}`,
        };
        const response = await api.post<{ data: { games: Game[] } }>(
          BGG_SNAPSHOT_URL,
          data
        );
        return response.data?.data?.games || [];
      } catch (error) {
        this.error = error;
        console.error(error);
        return [];
      }
    },

    /**
     * Load first page of games with new search params
     */
    async fetchGames({
                       sortBy = undefined,
                       minR = undefined,
                       minW = undefined,
                       limit = undefined,
                     }: { sortBy?: string; minR?: number; minW?: number; limit?: number } = {}) {
      this.isDone = false;
      this.page = 1;
      this.sortBy = sortBy || this.sortBy;
      this.minR = minR || this.minR;
      this.minW = minW || this.minW;
      this.limit = limit || this.limit;

      console.log("loading games page 1");
      const games = await this.loadSomeGames();
      this.isDone = !games.length;
      if (this.hfgOwned) {
        this.games = games.filter(game => this.hfgGames.has(game.bggId!));
      } else {
        this.games = games;
      }

      while (!this.isDone && this.hfgOwned) {  // load all games if we are only looking at HFG owned ones
        await this.fetchMoreGames()
      }

      return this.games.length;
    },

    /**
     * Load next page of games with the current params
     */
    async fetchMoreGames() {
      this.page++;

      console.log("loading games page " + this.page);
      const games = await this.loadSomeGames();
      this.isDone = !games.length;
      if (!games.length) {
        this.page--; // do not advance page if nothing got loaded
      }
      if (this.hfgOwned) {
        this.games.push(...games.filter(game => this.hfgGames.has(game.bggId!)));
      } else {
        this.games.push(...games);
      }

      return games.length;
    },
  },
});
