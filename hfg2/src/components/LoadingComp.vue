<template>
  <footer class="row loading-comp">
    <h5 v-if="!isDone">Loading...</h5>
    <h5 v-if="isDone">Done. ({{games.length}} games) <a href="#" @click="scrollToTop"> - To top</a></h5>
  </footer>
</template>

<script lang="ts">
import {mapState} from "pinia";
import {useGamesStore} from "@/stores/games";

export default {
  name: "LoadingComp",
  props: {
    isDone: Boolean,
  },
  computed: {
    ...mapState(useGamesStore, ['games'])
  },
  methods: {
    scrollToTop() {
      window.scrollTo({ top: 0, behavior: "smooth" });
    },
  },
};
</script>

<style scoped>
.loading-comp {
  display: flex;
  vertical-align: center;
  text-align: center;
  background-color: lightgray;
  margin-bottom: 0.5rem;
}
h5 {
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: medium;
}
</style>
