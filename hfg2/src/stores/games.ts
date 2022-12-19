import { defineStore } from "pinia";
import type { Game } from "@/stores/game";
import { api, BGG_SNAPSHOT_URL } from "@/stores/api";

export const useGamesStore = defineStore("games", {
  state: () => {
    return {
      games: [] as Game[],
      page: 1,
      sortBy: "n",
      minR: 6.0,
      minW: 3.0,
      limit: 100,
      isDone: false,
    };
  },
  getters: {
    // notWarGames: (state) => state.games.filter(game => game.type !=== 'war'),
  },
  actions: {
    async loadGameDetails(bggId: number): Promise<Game | null>{
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
        //FIXME: report errors better
        // alert(error)
        console.error(error);
        return null;
      }
    },
    async loadSomeGames(): Promise<Game[]> {
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
        //FIXME: report errors better
        // alert(error)
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
      this.games = await this.loadSomeGames();
      this.isDone = !this.games.length;
      return this.games.length;
    },
    /**
     * Load next page of games with the current params
     */
    async fetchMoreGames() {
      this.page++;

      console.log("loading games page " + this.page);
      const games = await this.loadSomeGames();
      if (!games.length) {
        this.page--; // do not advance page if nothing loads
      }
      this.isDone = !games.length;
      this.games.push(...games);
      return games.length;
    },
  },
});
