<template>
  <li class="row game" v-if="game">
    <div class="col-2 tb-wrapper my-auto">
      <img class="tb" :src="game.thumbnailUrl" alt="game logo" @click="showModal"/>
    </div>
    <h3 class="col-7 my-auto">{{ game.name }}</h3>
    <p class="col-1 my-auto">{{ game.avgRating?.toFixed(1) }}</p>
    <p class="col-1 my-auto">{{ game.avgWeight?.toFixed(1) }}</p>
    <a v-if="getHfgGame" target="_blank" :href="hfgGameDetailsUrl" class="col-1 my-auto">HFG</a>
  </li>
</template>

<script lang="ts">
import type {Game, HfgGame} from "@/stores/game";
import type {PropType} from "vue";
import {mapState, mapStores} from "pinia";
import {useGamesStore} from "@/stores/games";
import {HFG_GAME_DETAIL_URL} from "@/stores/api";

export default {
  name: "GameItem",
  props: {
    game: {
      type: Object as PropType<Game>,
      required: true,
      validator: (game: Game) => !!game.bggId
    }
  },
  computed: {
    ...mapState(useGamesStore, ['hfgGames']),
    getHfgGame(): HfgGame | undefined {
      return this.hfgGames.get(this.game.bggId!);
    },
    hfgGameDetailsUrl() {
      const hfgGame = this.getHfgGame;
      return hfgGame ? HFG_GAME_DETAIL_URL + hfgGame.id! : undefined;
    }
  },
  emits: {
    click(game: Game) {
      return !!game.bggId;
    }
  },
  methods: {
    showModal() {
      this.$emit("click", this.game);
    },
  }
};
</script>

<style scoped>
.game {
  margin-bottom: 0.2rem;
  background-color: #f2f2f2;
}

.tb-wrapper {
  text-align: center;
  max-width: 150px;
}

.tb {
  max-height: 60px;
  cursor: pointer;
}

h3 {
  display: inline-block;
  text-align: left;
  font-size: large;
}

p {
  display: inline-block;
  text-align: left;
}
</style>
