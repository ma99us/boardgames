import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      // Static-loaded when app loads
      component: HomeView,
    },
    {
      path: "/games",
      name: "games",
      // Lazy-loaded when the route is visited.
      component: () => import("../views/GamesView.vue"),
    },
  ],
});

export default router;
