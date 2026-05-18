import { createRouter, createWebHistory } from 'vue-router';
import DashboardView from '../views/DashboardView.vue';
import RoomsView from '../views/RoomsView.vue';
import BookingsView from '../views/BookingsView.vue';

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: DashboardView,
  },
  {
    path: '/rooms',
    name: 'Rooms',
    component: RoomsView,
  },
  {
    path: '/bookings',
    name: 'Bookings',
    component: BookingsView,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
