<template>
  <div class="games">
    <GamePopup :game="selectedGame" />
    <div>
      <PageComp />
      <ul class="list-group" ref="scrollComponent">
        <GameItem
          v-for="game in games"
          :key="game.bggId"
          :game="game"
          @click="onGameClicked(game)"
        />
      </ul>
    </div>
  </div>
  <LoadingComp v-if="isLoading || isDone" :isDone="isDone" />
</template>

<script lang="ts">
import {useGamesStore} from "@/stores/games";
import {mapState, mapStores} from "pinia";
import GameItem from "@/components/GameItem.vue";
import PageComp from "@/components/PageComp.vue";
import LoadingComp from "@/components/LoadingComp.vue";
import GamePopup from "@/components/GamePopup.vue";
import type {Game} from "@/stores/game";

export default {
  name: "GameView",
  data() {
    return {
      isLoading: false,
      modalShow: false,
      showPopup: false,
      selectedGame: undefined
    };
  },
  components: {
    GameItem,
    PageComp,
    LoadingComp,
    GamePopup,
  },
  computed: {
    ...mapStores(useGamesStore),
    ...mapState(useGamesStore, ["games", "isDone"]),
  },
  methods: {
    handleScroll() {
      const element = this.$refs.scrollComponent as HTMLElement;
      if (
        element.getBoundingClientRect().bottom < window.innerHeight &&
        !this.isLoading &&
        !this.isDone
      ) {
        this.isLoading = true;
        this.gamesStore.fetchMoreGames().finally(() => {
          this.isLoading = false;
        });
      }
    },
    onGameClicked(game: Game) {
      this.selectedGame = {...game};  // clone to trigger 'watch' in the GamePopup
    }
  },
  mounted() {
    window.addEventListener("scroll", this.handleScroll);

    this.isLoading = true;
    this.gamesStore.fetchGames().finally(() => {
      this.isLoading = false;
    });
  },
  unmounted() {
    window.removeEventListener("scroll", this.handleScroll);
  },
};
</script>

<style>
.games {
  min-height: 100vh;
  text-align: center;
}
</style>
