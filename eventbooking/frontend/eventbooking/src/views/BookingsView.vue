<template>
  <div class="bookings-view">
    <header class="view-header">
      <div class="header-content">
        <h1>Room Bookings & Reservations</h1>
        <p class="subtitle">Reserve campus study rooms and manage active bookings</p>
      </div>
      <button @click="resetForm" class="btn btn-secondary btn-sm" v-if="isEditing">
        New Reservation
      </button>
    </header>

    <!-- Global Pinia Messages -->
    <div v-if="store.errorMessage" class="alert alert-error" role="alert" id="booking-error-alert">
      <span class="alert-icon"></span>
      <span class="alert-message">{{ store.errorMessage }}</span>
      <button @click="store.clearMessages" class="alert-close">&times;</button>
    </div>
    <div v-if="store.successMessage" class="alert alert-success" role="alert" id="booking-success-alert">
      <span class="alert-icon"></span>
      <span class="alert-message">{{ store.successMessage }}</span>
      <button @click="store.clearMessages" class="alert-close">&times;</button>
    </div>

    <main class="split-layout">
      <!-- Registration Form Card -->
      <section class="form-card-container">
        <div class="premium-card">
          <h2 class="card-title">{{ isEditing ? 'Modify Reservation' : ' Book a Study Room' }}</h2>

          <div v-if="bookableRooms.length === 0 && !isEditing" class="alert alert-warning" role="alert">
            <span class="alert-icon"></span>
            <span class="alert-message">No rooms are currently available for booking. Register a room first or set one as available.</span>
          </div>

          <form v-else @submit.prevent="handleSubmit" class="styled-form">
            <!-- Room Select -->
            <div class="form-group">
              <label for="booking-room">Select Study Room</label>
              <select id="booking-room" v-model="form.roomId" required :disabled="isEditing">
                <option value="" disabled>-- Choose Study Room --</option>
                <option v-for="room in (isEditing ? store.rooms : bookableRooms)" :key="room.id" :value="room.id">
                  {{ room.name }} (Capacity: {{ room.capacity }} students, {{ room.location }})
                </option>
              </select>
            </div>

            <!-- Student Representative Name -->
            <div class="form-group">
              <label for="student-name">Student Representative Name</label>
              <input 
                id="student-name"
                v-model="form.studentName" 
                type="text" 
                required 
                placeholder="e.g. Hillary Hillary"
              />
            </div>

            <!-- Student Representative ID -->
            <div class="form-group">
              <label for="student-id">Institutional Student ID</label>
              <input 
                id="student-id"
                v-model="form.studentId" 
                type="text" 
                required 
                placeholder="e.g. STD12345"
              />
            </div>

            <!-- Booking Date -->
            <div class="form-group">
              <label for="booking-date">Date of Booking</label>
              <input 
                id="booking-date"
                v-model="form.bookingDate" 
                type="date" 
                required 
              />
            </div>

            <!-- Released Status (only shown when editing) -->
            <div class="form-group checkbox-group" v-if="isEditing" style="display: flex; align-items: center; gap: 10px; margin-top: 15px;">
              <input 
                id="booking-released"
                v-model="form.released" 
                type="checkbox"
                style="width: auto; margin: 0;"
              />
              <label for="booking-released" style="margin: 0; cursor: pointer;">Released (Active Booking)</label>
            </div>

            <div class="form-actions" style="margin-top: 20px;">
              <button 
                type="submit" 
                class="btn btn-primary" 
                :disabled="store.loading"
              >
                <span v-if="store.loading" class="spinner"></span>
                <span>{{ isEditing ? 'Update Booking' : 'Complete Booking' }}</span>
              </button>
              <button type="button" @click="resetForm" class="btn btn-ghost" v-if="isEditing">
                Cancel
              </button>
            </div>
          </form>
        </div>
      </section>

      <!-- Active Bookings Registry List -->
      <section class="list-container">
        <div class="premium-card">
          <div class="card-header-row">
            <h2 class="card-title">All Reservations ({{ store.bookings.length }})</h2>
            <div class="search-box">
              <input v-model="searchQuery" type="text" placeholder="Filter bookings..." class="search-input" />
            </div>
          </div>

          <div v-if="store.loading && store.bookings.length === 0" class="loading-state">
            <div class="spinner large"></div>
            <p>Loading booking registry...</p>
          </div>

          <div v-else-if="filteredBookings.length === 0" class="empty-state">
            <div class="empty-icon"></div>
            <p class="empty-text">No active study room bookings found.</p>
            <p class="empty-subtext" v-if="store.bookings.length > 0">Try altering your search filters.</p>
            <p class="empty-subtext" v-else>Use the form on the left to make your first study room booking!</p>
          </div>

          <div v-else class="table-responsive">
            <table class="styled-table">
              <thead>
                <tr>
                  <th>Booking ID</th>
                  <th>Room Info</th>
                  <th>Student Representative</th>
                  <th>Booking Date</th>
                  <th>Status</th>
                  <th class="actions-col">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="booking in filteredBookings" :key="booking.id">
                  <td><span class="id-badge" :title="booking.id">#{{ booking.id.substring(0, 8) }}...</span></td>
                  <td>
                    <div class="event-profile">
                      <strong>{{ booking.room?.name || 'Deleted Room' }}</strong>
                      <span class="subtext"> {{ booking.room?.location }}</span>
                    </div>
                  </td>
                  <td>
                    <div class="student-profile">
                      <div class="profile-info">
                        <strong>{{ booking.studentName }}</strong>
                        <span class="subtext">ID: {{ booking.studentId }}</span>
                      </div>
                    </div>
                  </td>
                  <td class="date-col">
                    <strong>{{ booking.bookingDate }}</strong>
                  </td>
                  <td>
                    <span v-if="booking.released" class="badge badge-success">Released/Active</span>
                    <span v-else class="badge badge-danger">Inactive</span>
                  </td>
                  <td class="actions-col" style="display: flex; gap: 8px;">
                    <button 
                      @click="editBooking(booking)" 
                      class="btn btn-secondary btn-sm"
                      title="Edit Booking"
                    >
                       Edit
                    </button>
                    <button 
                      @click="cancelBooking(booking)" 
                      class="btn btn-ghost btn-sm text-danger" 
                      title="Cancel Booking"
                    >
                       Cancel
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
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
  roomId: '',
  studentName: '',
  studentId: '',
  bookingDate: '',
  released: true,
});

onMounted(() => {
  store.fetchRooms();
  store.fetchBookings();
});

const bookableRooms = computed(() => {
  return store.rooms.filter(r => r.available);
});

const filteredBookings = computed(() => {
  const q = searchQuery.value.toLowerCase().trim();
  if (!q) return store.bookings;
  return store.bookings.filter(b => 
    b.studentName.toLowerCase().includes(q) ||
    b.studentId.toLowerCase().includes(q) ||
    (b.room?.name && b.room.name.toLowerCase().includes(q)) ||
    (b.room?.location && b.room.location.toLowerCase().includes(q)) ||
    b.bookingDate.includes(q)
  );
});

const resetForm = () => {
  form.value = {
    id: null,
    roomId: '',
    studentName: '',
    studentId: '',
    bookingDate: '',
    released: true,
  };
  isEditing.value = false;
  store.clearMessages();
};

const editBooking = (booking) => {
  form.value = {
    id: booking.id,
    roomId: booking.room?.id || '',
    studentName: booking.studentName,
    studentId: booking.studentId,
    bookingDate: booking.bookingDate,
    released: booking.released,
  };
  isEditing.value = true;
  store.clearMessages();
};

const handleSubmit = async () => {
  let success = false;
  
  if (isEditing.value) {
    const payload = {
      id: form.value.id,
      studentName: form.value.studentName,
      studentId: form.value.studentId,
      bookingDate: form.value.bookingDate,
      released: form.value.released,
      room: store.rooms.find(r => r.id === form.value.roomId)
    };
    success = await store.updateBooking(form.value.id, payload);
  } else {
    const payload = {
      studentName: form.value.studentName,
      studentId: form.value.studentId,
      bookingDate: form.value.bookingDate,
    };
    success = await store.bookRoom(form.value.roomId, payload);
  }

  if (success) {
    resetForm();
  }
};

const cancelBooking = (booking) => {
  const roomName = booking.room?.name || 'Study Room';
  const repName = booking.studentName || 'Representative';
  if (confirm(`Are you sure you want to cancel the reservation of '${roomName}' for representative '${repName}' on ${booking.bookingDate}?`)) {
    store.cancelBooking(booking.id);
    if (isEditing.value && form.value.id === booking.id) {
      resetForm();
    }
  }
};
</script>
