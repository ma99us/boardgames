<template>
  <div v-if="isVisible && game" class="modal fade" :class="{'show': isVisible}"
       data-backdrop="static" tabindex="-1" aria-labelledby="gameModalLabel" :aria-modal="isVisible" role="dialog"
       style="display:block">
    <div class="modal-dialog modal-dialog-centered modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title">{{ game.name }}</h1>
          <button type="button" @click="onClose" class="btn-close"></button>
        </div>
        <div class="modal-body">
          <div v-if="loading">
            <h6>Loading...</h6>
          </div>
          <div v-if="!loading">
            <div class="row justify-content-center align-items-center">
              <img class="my-auto game-img" v-if="details.imageUrl" :src="details.imageUrl" alt="game box image"/>
            </div>
            <div class="row">
              <div class="col-auto description" v-html="details.description"></div>
            </div>
            <div class="row ratings">
              <div class="col-6">
                <h6>Rating: {{ details.avgRating?.toFixed(1) }}</h6>
              </div>
              <div class="col-6">
                <h6>Weight: {{ details.avgWeight?.toFixed(1) }}</h6>
              </div>
            </div>
            <div class="row justify-content-start">
              <div class="col-auto">
                <h6>Players:</h6> {{ details.minPlayers }} - {{ details.maxPlayers }}, <h6>Good for:</h6>
                {{ details.goodPlayers?.join(', ') }}, <h6>Best for:</h6> {{ details.bestPlayers?.join(', ') }}
              </div>
            </div>
            <div class="row justify-content-start">
              <div class="col-auto">
                <h6 v-if="details.designer">Game by:</h6> {{ details.designer }}, <h6 v-if="details.artist">Art by:</h6>
                {{ details.artist }}
              </div>
            </div>
            <div class="row justify-content-start">
              <div class="col-auto">
                <h6>Published by:</h6> {{ details.publisher }} (<h6>{{ details.yearPublished }}</h6>)
              </div>
            </div>
            <div class="row justify-content-start" v-if="details.honors">
              <div class="col-auto">
                <h6>Honors:</h6> {{ details.honors }}
              </div>
            </div>
            <div class="row justify-content-start footnote">
              <div class="col-auto">
                <a target="_blank" :href="`https://boardgamegeek.com/boardgame/${game.bggId}`">...more info on BGG.</a>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" @click="onClose" class="btn btn-success">Close</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import type {Game} from "@/stores/game";
import {mapStores} from "pinia";
import {useGamesStore} from "@/stores/games";
import type {PropType} from "vue";

export default {
  name: "GamePopup",
  props: {
    game: {
      type: Object as PropType<Game> | undefined,
      required: false,
      validator: (game: Game | undefined) => (!game || !!game.bggId)
    }
  },
  data() {
    return {
      details: {} as Game,
      isVisible: false,
      loading: false
    }
  },
  computed: {
    ...mapStores(useGamesStore)
  },
  methods: {
    async loadDetails(): Promise<Game> {
      if (!this.game) {
        this.details = {};
        return this.details;
      }

      const game = await this.gamesStore.loadGameDetails(this.game.bggId!);

      this.details = game ? {...game} : {};
      return this.details;
    },
    onClose() {
      this.isVisible = false;
    },
  },
  watch: {
    game(newVal, oldVal) { // watch game property change
      this.isVisible = !!newVal;
      this.loading = true;
      this.loadDetails()
          .finally(() => {
            this.loading = false;
          });
    }
  }
};
</script>

<style scoped>
.game-img {
  max-width: 600px;
  padding-bottom: 0.5rem;
}

.description {
  text-align: left;
}

.ratings {
  padding-bottom: 0.5rem;
}

h6 {
  display: inline-block;
}

.footnote {
  padding-top: 0.5rem;
}
</style>