<template>
  <div class="home">
    <h1>No one's Home yet</h1>
    <p>Just look at <RouterLink to="/games">Games</RouterLink> for now.</p>
  </div>
</template>

<script lang="ts">
export default {
  name: 'HomeView'
}
</script>

<style>
.home {
  min-height: 100vh;
  text-align: center;
}
</style>