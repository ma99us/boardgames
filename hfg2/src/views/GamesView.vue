<template>
  <div class="games">
    <div>
      <PageComp/>
      <ul ref="scrollComponent">
        <GameItem v-for="game in games" key="game.bggId" :game="game"/>
      </ul>
      <LoadingComp v-if="isLoading || isDone" :isDone="isDone"/>
    </div>
  </div>
</template>

<script lang="ts">

import {useGamesStore} from "@/stores/games";
import GameItem from "@/components/GameItem.vue";
import PageComp from "@/components/PageComp.vue";
import {mapState, mapStores} from "pinia";
import LoadingComp from "@/components/LoadingComp.vue";

export default {
  data() {
    return {
      isLoading: false
    }
  },
  components: {
    GameItem,
    PageComp,
    LoadingComp
  },
  computed: {
    ...mapStores(useGamesStore),
    ...mapState(useGamesStore, ['games', 'isDone'])
  },
  methods: {
    handleScroll(e: any) {
      const element = this.$refs.scrollComponent as HTMLElement;
      if (element.getBoundingClientRect().bottom < window.innerHeight && !this.isLoading && !this.isDone) {
        this.isLoading = true;
        this.gamesStore.fetchMoreGames()
            .finally(() => {
              this.isLoading = false;
            });
      }
    }
  },
  mounted() {
    window.addEventListener("scroll", this.handleScroll)

    this.isLoading = true;
    this.gamesStore.fetchGames()
        .finally(() => {
          this.isLoading = false;
        });
  },
  unmounted() {
    window.removeEventListener("scroll", this.handleScroll)
  }
}
</script>

<style>
.games {
  min-height: 100vh;
  text-align: center;
}
</style>
