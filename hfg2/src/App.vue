<template>
  <header>
    <img alt="Vue logo" class="logo" src="@/assets/logo.svg" width="125" height="125"/>

    <div class="wrapper">
      <nav>
        <RouterLink to="/"><h3>Home</h3></RouterLink>
        <RouterLink to="/games"><h3>Games</h3></RouterLink>
      </nav>
    </div>
  </header>

  <RouterView class="content"/>
</template>

<script lang="ts">
import {RouterLink, RouterView} from 'vue-router'

export default {
  components: {
    RouterLink,
    RouterView,
  }
}
</script>

<style scoped>
header {
  max-height: 100vh;
  width: 100%;
  display: flex;
  margin-bottom: 1rem;
}

.logo {
  display: inline-block;
  max-height: 50px;
  padding-top: 1vh;
}

.wrapper {
  flex-grow: 0.8;
  text-align: center;
  vertical-align: center;
}

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 0.5rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
  font-weight: bold;
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
  font-weight: bolder;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

.content {
  border-top: 1px dashed lightgray;
}
</style>
