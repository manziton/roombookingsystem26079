<template>
  <div class="rooms-view">
    <header class="view-header">
      <div class="header-content">
        <h1>Campus Study Rooms</h1>
        <p class="subtitle">Register, update, and manage study rooms across campus</p>
      </div>
      <button @click="resetForm" class="btn btn-secondary btn-sm" v-if="isEditing">
        Add New Room
      </button>
    </header>

    <!-- Global Messages -->
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

    <main class="split-layout">
      <!-- Form Side -->
      <section class="form-card-container">
        <div class="premium-card">
          <h2 class="card-title">{{ isEditing ? 'Modify Room Details' : ' Register Study Room' }}</h2>
          
          <form @submit.prevent="handleSubmit" class="styled-form">
            <div class="form-group">
              <label for="room-name">Room Name</label>
              <input 
                id="room-name"
                v-model="form.name" 
                type="text" 
                required 
                placeholder="e.g. ROM104"
              />
            </div>

            <div class="form-group">
              <label for="room-cap">Maximum Student Capacity</label>
              <input 
                id="room-cap"
                v-model.number="form.capacity" 
                type="number" 
                min="1"
                required 
                placeholder="e.g. 6"
              />
            </div>

            <div class="form-group">
              <label for="room-loc">Building or Floor Location</label>
              <input 
                id="room-loc"
                v-model="form.location" 
                type="text" 
                required
                placeholder="e.g. Library Ground Floor"
              />
            </div>

            <div class="form-group checkbox-group" style="display: flex; align-items: center; gap: 10px; margin-top: 15px;">
              <input 
                id="room-available"
                v-model="form.available" 
                type="checkbox"
                style="width: auto; margin: 0;"
              />
              <label for="room-available" style="margin: 0; cursor: pointer;">Available for Booking</label>
            </div>

            <div class="form-actions" style="margin-top: 20px;">
              <button type="submit" class="btn btn-primary" :disabled="store.loading">
                <span v-if="store.loading" class="spinner"></span>
                <span>{{ isEditing ? 'Update Room' : 'Add Room' }}</span>
              </button>
              <button type="button" @click="resetForm" class="btn btn-ghost" v-if="isEditing">
                Cancel
              </button>
            </div>
          </form>
        </div>
      </section>

      <!-- List Side -->
      <section class="list-container">
        <div class="premium-card">
          <div class="card-header-row">
            <h2 class="card-title">Registered Rooms ({{ store.rooms.length }})</h2>
            <div class="search-box">
              <input v-model="searchQuery" type="text" placeholder="Search rooms..." class="search-input" />
            </div>
          </div>

          <div v-if="store.loading && store.rooms.length === 0" class="loading-state">
            <div class="spinner large"></div>
            <p>Loading campus study rooms...</p>
          </div>

          <div v-else-if="filteredRooms.length === 0" class="empty-state" id="no-rooms-message">
            <div class="empty-icon"></div>
            <p class="empty-text">No study rooms found.</p>
            <p class="empty-subtext" v-if="store.rooms.length > 0">Try refining your search query.</p>
            <p class="empty-subtext" v-else>Register a study room on the left side to get started!</p>
          </div>

          <div v-else class="event-cards-grid">
            <article 
              v-for="room in filteredRooms" 
              :key="room.id" 
              class="event-detail-card"
              :class="{ 'card-editing': isEditing && form.id === room.id }"
            >
              <div class="event-card-header">
                <span class="id-badge" :title="room.id">ID: {{ room.id.substring(0, 8) }}...</span>
                <span v-if="room.available" class="badge badge-success">Bookable</span>
                <span v-else class="badge badge-danger">Not Bookable</span>
              </div>

              <div class="event-card-body">
                <h3 style="margin-bottom: 12px;">{{ room.name }}</h3>
                
                <div class="event-meta-info" style="margin-bottom: 0;">
                  <div class="meta-row">
                    <span class="meta-icon"></span>
                    <span class="meta-val"><strong>Location:</strong> {{ room.location }}</span>
                  </div>
                  <div class="meta-row">
                    <span class="meta-icon"></span>
                    <span class="meta-val"><strong>Max Capacity:</strong> {{ room.capacity }} students</span>
                  </div>
                </div>
              </div>

              <div class="event-card-actions" style="margin-top: 15px;">
                <button @click="editRoom(room)" class="btn btn-secondary btn-sm" style="flex: 1;">
                   Edit
                </button>
              </div>
            </article>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoomBookingStore } from '../store/roomBookingStore';

const store = useRoomBookingStore();
const searchQuery = ref('');
const isEditing = ref(false);

const form = ref({
  id: null,
  name: '',
  capacity: 6,
  location: '',
  available: true,
});

onMounted(() => {
  store.fetchRooms();
});

const filteredRooms = computed(() => {
  const q = searchQuery.value.toLowerCase().trim();
  if (!q) return store.rooms;
  return store.rooms.filter(r => 
    r.name.toLowerCase().includes(q) || 
    r.location.toLowerCase().includes(q)
  );
});

const resetForm = () => {
  form.value = {
    id: null,
    name: '',
    capacity: 6,
    location: '',
    available: true,
  };
  isEditing.value = false;
  store.clearMessages();
};

const editRoom = (room) => {
  form.value = { 
    id: room.id,
    name: room.name,
    capacity: room.capacity,
    location: room.location,
    available: room.available,
  };
  isEditing.value = true;
  store.clearMessages();
};

const handleSubmit = async () => {
  let success = false;
  const payload = {
    name: form.value.name,
    capacity: parseInt(form.value.capacity),
    location: form.value.location,
    available: form.value.available,
  };

  if (isEditing.value) {
    success = await store.updateRoom(form.value.id, payload);
  } else {
    success = await store.addRoom(payload);
  }

  if (success) {
    resetForm();
  }
};
</script>
