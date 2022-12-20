<template>
  <div class="row page-comp">
    <div class="col-2 tb"></div>
    <div class="col-7 title">
      <a href="#" @click="onSortByClick('n')"><h5>Title</h5></a
      ><span v-if="sortBy === 'n'"> *</span>
    </div>
    <div class="col-1">
      <a href="#" @click="onSortByClick('r')"><h5>Rating</h5></a
      ><span v-if="sortBy === 'r'"> *</span>
    </div>
    <div class="col-1">
      <a href="#" @click="onSortByClick('w')"><h5>Weight</h5></a
      ><span v-if="sortBy === 'w'"> *</span>
    </div>
    <div class="col-1">
      <input type="checkbox" id="hfgOwned" name="hfgOwned" :value="hfgOwned" v-model="hfgOwned"  @change="onHfgOwnedChange">
      <a href="#"><label for="hfgOwned"><h5>HFG Owned</h5></label></a>
    </div>
  </div>
</template>

<script lang="ts">
import { mapState, mapStores } from "pinia";
import { useGamesStore } from "@/stores/games";

export default {
  name: "PageComp",
  computed: {
    ...mapStores(useGamesStore),
    ...mapState(useGamesStore, ["sortBy", "hfgOwned"]),
  },
  methods: {
    onSortByClick(sortBy: string) {
      this.gamesStore.fetchGames({ sortBy });
    },
    onHfgOwnedChange() {
      this.gamesStore.hfgOwned = !this.hfgOwned;
      this.gamesStore.fetchGames();
    }
  },
};
</script>

<style scoped>
.tb {
  max-width: 150px;
}
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
