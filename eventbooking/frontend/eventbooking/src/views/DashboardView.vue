<template>
  <div class="dashboard">
    <header class="view-header">
      <div class="header-content">
        <h1>Campus Study Room Booking System</h1>
        <p class="subtitle">Efficient campus room reservation for student class representatives</p>
      </div>
      <div class="date-badge">
        <span class="icon"></span>
        <span class="date">{{ currentDate }}</span>
      </div>
    </header>

    <!-- Global Messages from Pinia -->
    <div v-if="store.errorMessage" class="alert alert-error" role="alert">
      <span class="alert-icon"></span>
      <span class="alert-message">{{ store.errorMessage }}</span>
      <button @click="store.clearMessages" class="alert-close">&times;</button>
    </div>
    <div v-if="store.successMessage" class="alert alert-success" role="alert">
      <span class="alert-icon"></span>
      <span class="alert-message">{{ store.successMessage }}</span>
      <button @click="store.clearMessages" class="alert-close">&times;</button>
    </div>

    <main class="dashboard-content">
      <!-- Stat Cards Grid -->
      <section class="stats-grid">
        <article class="stat-card blue">
          <div class="stat-icon"></div>
          <div class="stat-info">
            <h3>Total Rooms</h3>
            <p class="stat-number">{{ store.rooms.length }}</p>
          </div>
          <div class="stat-footer">
            <router-link to="/rooms">Manage Rooms &rarr;</router-link>
          </div>
        </article>

        <article class="stat-card purple">
          <div class="stat-icon"></div>
          <div class="stat-info">
            <h3>Bookable Rooms</h3>
            <p class="stat-number">{{ bookableRoomsCount }}</p>
          </div>
          <div class="stat-footer">
            <router-link to="/rooms">View Availability &rarr;</router-link>
          </div>
        </article>

        <article class="stat-card green">
          <div class="stat-icon"></div>
          <div class="stat-info">
            <h3>Total Bookings</h3>
            <p class="stat-number">{{ store.bookings.length }}</p>
          </div>
          <div class="stat-footer">
            <router-link to="/bookings">Book a Room &rarr;</router-link>
          </div>
        </article>
      </section>

      <!-- Layout Split: Quick Booking & Upcoming Events -->
      <div class="dashboard-split">
        <!-- Study Rooms Status section -->
        <section class="dashboard-section events-summary">
          <div class="section-header">
            <h2> Study Rooms Overview</h2>
            <router-link to="/rooms" class="btn-link">View all</router-link>
          </div>
          <div v-if="store.rooms.length === 0" class="empty-state">
            <p class="empty-text">No study rooms are currently registered.</p>
            <router-link to="/rooms" class="btn btn-primary btn-sm">Add Room</router-link>
          </div>
          <div v-else class="event-mini-list">
            <article v-for="room in store.rooms.slice(0, 3)" :key="room.id" class="mini-event-card">
              <div class="mini-event-body">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <h4>{{ room.name }}</h4>
                  <span v-if="room.available" class="badge badge-success">Bookable</span>
                  <span v-else class="badge badge-danger">Not Bookable</span>
                </div>
                <p class="event-meta" style="margin-top: 8px;">
                  <span> {{ room.location }}</span>
                  <span> Capacity: {{ room.capacity }} students</span>
                </p>
              </div>
            </article>
          </div>
        </section>

        <!-- Quick actions / stats summary -->
        <section class="dashboard-section quick-guide">
          <h2> Representative Guide</h2>
          <div class="guide-steps">
            <div class="guide-step">
              <span class="step-num">1</span>
              <div>
                <strong>Explore Rooms</strong>
                <p>Browse the list of available rooms on campus, check their capacity, and floor locations.</p>
              </div>
            </div>
            <div class="guide-step">
              <span class="step-num">2</span>
              <div>
                <strong>Reserve a Room</strong>
                <p>Select an available room, specify a date, and enter your class representative details to submit.</p>
              </div>
            </div>
            <div class="guide-step">
              <span class="step-num">3</span>
              <div>
                <strong>Manage Bookings</strong>
                <p>Modify reservation details, delete bookings, or release the room when finished.</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue';
import { useRoomBookingStore } from '../store/roomBookingStore';

const store = useRoomBookingStore();

onMounted(() => {
  store.fetchRooms();
  store.fetchBookings();
});

const currentDate = computed(() => {
  const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
  return new Date().toLocaleDateString('en-US', options);
});

const bookableRoomsCount = computed(() => {
  return store.rooms.filter(r => r.available).length;
});
</script>
