<template>
  <div class="app-container">
    <!-- Top Sticky Header & Navigation -->
    <header class="app-header">
      <div class="nav-container">
        <router-link to="/" class="nav-logo">
          <span class="logo-emoji"></span>
          <span class="logo-text">StudyRoom Hub</span>
        </router-link>
        
        <nav class="nav-links" aria-label="Main Navigation">
          <router-link to="/" class="nav-link" active-class="active" exact>
             Dashboard
          </router-link>
          <router-link to="/rooms" class="nav-link" active-class="active">
             Study Rooms
          </router-link>
          <router-link to="/bookings" class="nav-link" active-class="active">
             Bookings
          </router-link>
        </nav>
      </div>
    </header>

    <!-- Main View Transition Wrapper -->
    <main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Modern App Footer -->
    <footer class="app-footer">
      <p>&copy; 2026 Campus Study Room Booking System. Developed with Vue 3, Pinia, and Spring Boot.</p>
    </footer>
  </div>
</template>

<script setup>
// Application container setup
</script>

<style>
/* Page transition animation */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>
