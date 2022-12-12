<template>
  <li class="row game h-100" v-if="game">
    <div class="col-2 tb-wrapper my-auto">
      <img class="tb" :src="game.thumbnailUrl" alt="game logo"/>
    </div>
    <h3 class="col-6 my-auto">{{game.name}}</h3>
    <p class="col-2 my-auto">R: {{game.avgRating?.toFixed(1)}}</p>
    <p class="col-2 my-auto">W: {{game.avgWeight?.toFixed(1)}}</p>
  </li>
</template>

<script lang="ts">
import type {Game} from "@/stores/game";

export default {
  props: {
    game: {} as typeof Game
  }
}
</script>

<style scoped>
.game {
  margin-bottom: 0.2rem;
  background-color: #f2f2f2;
}

.tb-wrapper{
  text-align: center;
  width: 150px;
}

.tb{
  max-height: 60px;
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
