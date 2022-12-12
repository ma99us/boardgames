<template>
  <div class="row page-comp">
    <div class="col-2"></div>
    <div class="col-6 title"><a href="#" @click="onSortByClick('n')"><h5>Title</h5></a><span v-if="sortBy === 'n'"> *</span></div>
    <div class="col-2"><a href="#" @click="onSortByClick('r')"><h5>Rating</h5></a><span v-if="sortBy === 'r'"> *</span></div>
    <div class="col-2"><a href="#" @click="onSortByClick('w')"><h5>Weight</h5></a><span v-if="sortBy === 'w'"> *</span></div>
  </div>
</template>

<script lang="ts">
import {mapState, mapStores} from "pinia";
import {useGamesStore} from "@/stores/games";

export default {
  computed: {
    ...mapStores(useGamesStore),
    ...mapState(useGamesStore, ['sortBy']),
  },
  methods: {
    onSortByClick(sortBy: string) {
      this.gamesStore.fetchGames({sortBy});
    }
  }
}
</script>

<style scoped>
div {
  white-space: nowrap;
}
h5 {
  display: inline-block;
  margin-bottom: 0.5rem;
  margin-top: 0.5rem;
  font-size: medium;
}
span {
  font-size: larger;
  font-weight: bolder;
  color: blue;
}
.page-comp {
  text-align: left;
  background-color: lightgray;
  margin-bottom: 0.5rem;
}
.title {
  text-align: center;
}
</style>