<template>
  <li class="row game" v-if="game">
    <div class="col-2 tb-wrapper my-auto">
      <img class="tb" :src="game.thumbnailUrl" alt="game logo" @click="showModal" />
    </div>
    <h3 class="col-8 my-auto">{{ game.name }}</h3>
    <p class="col-1 my-auto">{{ game.avgRating?.toFixed(1) }}</p>
    <p class="col-1 my-auto">{{ game.avgWeight?.toFixed(1) }}</p>
  </li>
</template>

<script lang="ts">
import type { Game } from "@/stores/game";

export default {
  name: "GameItem",
  props: {
    game: {} as Game
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
