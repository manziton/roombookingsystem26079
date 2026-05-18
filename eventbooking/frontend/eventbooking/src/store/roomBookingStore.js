import { defineStore } from 'pinia';
import axios from 'axios';

const API_BASE = '/api';

export const useRoomBookingStore = defineStore('roomBooking', {
  state: () => ({
    rooms: [],
    bookings: [],
    loading: false,
    errorMessage: '',
    successMessage: '',
  }),

  actions: {
    clearMessages() {
      this.errorMessage = '';
      this.successMessage = '';
    },

    setTemporaryError(msg) {
      this.errorMessage = msg;
      this.successMessage = '';
      setTimeout(() => {
        if (this.errorMessage === msg) {
          this.errorMessage = '';
        }
      }, 5000);
    },

    setTemporarySuccess(msg) {
      this.successMessage = msg;
      this.errorMessage = '';
      setTimeout(() => {
        if (this.successMessage === msg) {
          this.successMessage = '';
        }
      }, 5000);
    },

    // --- ROOMS ---
    async fetchRooms() {
      this.loading = true;
      try {
        const response = await axios.get(`${API_BASE}/rooms`);
        this.rooms = response.data;
      } catch (error) {
        console.error('Error fetching rooms:', error);
        this.setTemporaryError('Failed to load rooms. Make sure backend is running.');
      } finally {
        this.loading = false;
      }
    },

    async fetchRoomById(id) {
      this.loading = true;
      try {
        const response = await axios.get(`${API_BASE}/rooms/${id}`);
        return response.data;
      } catch (error) {
        console.error(`Error fetching room ${id}:`, error);
        this.setTemporaryError('Failed to load room details.');
        return null;
      } finally {
        this.loading = false;
      }
    },

    async addRoom(roomData) {
      this.loading = true;
      try {
        const response = await axios.post(`${API_BASE}/rooms`, roomData);
        this.rooms.push(response.data);
        this.setTemporarySuccess(`Room '${response.data.name}' created successfully!`);
        return true;
      } catch (error) {
        console.error('Error adding room:', error);
        const backendMsg = error.response?.data?.message || 'Failed to create room.';
        this.setTemporaryError(backendMsg);
        return false;
      } finally {
        this.loading = false;
      }
    },

    async updateRoom(id, roomData) {
      this.loading = true;
      try {
        const response = await axios.put(`${API_BASE}/rooms/${id}`, roomData);
        const index = this.rooms.findIndex(r => r.id === id);
        if (index !== -1) {
          this.rooms[index] = response.data;
        }
        this.setTemporarySuccess(`Room '${response.data.name}' updated successfully!`);
        return true;
      } catch (error) {
        console.error('Error updating room:', error);
        const backendMsg = error.response?.data?.message || 'Failed to update room.';
        this.setTemporaryError(backendMsg);
        return false;
      } finally {
        this.loading = false;
      }
    },

    // --- BOOKINGS ---
    async fetchBookings() {
      this.loading = true;
      try {
        const response = await axios.get(`${API_BASE}/bookings`);
        this.bookings = response.data;
      } catch (error) {
        console.error('Error fetching bookings:', error);
        this.setTemporaryError('Failed to load bookings.');
      } finally {
        this.loading = false;
      }
    },

    async bookRoom(roomId, bookingRequest) {
      this.loading = true;
      this.clearMessages();
      try {
        const response = await axios.post(`${API_BASE}/bookings/${roomId}`, bookingRequest);
        this.bookings.push(response.data);
        await this.fetchRooms(); // refresh room availability/details if needed
        this.setTemporarySuccess(`Successfully booked room '${response.data.room?.name}' for ${bookingRequest.studentName}!`);
        return true;
      } catch (error) {
        console.error('Error booking room:', error);
        const backendMsg = error.response?.data?.message || 'Failed to complete room booking.';
        this.setTemporaryError(backendMsg);
        return false;
      } finally {
        this.loading = false;
      }
    },

    async cancelBooking(bookingId) {
      this.loading = true;
      try {
        await axios.delete(`${API_BASE}/bookings/${bookingId}`);
        this.bookings = this.bookings.filter(b => b.id !== bookingId);
        await this.fetchRooms(); // refresh room stats/states
        this.setTemporarySuccess('Booking cancelled successfully.');
        return true;
      } catch (error) {
        console.error('Error cancelling booking:', error);
        this.setTemporaryError('Failed to cancel booking.');
        return false;
      } finally {
        this.loading = false;
      }
    },

    async updateBooking(bookingId, bookingData) {
      this.loading = true;
      try {
        const response = await axios.put(`${API_BASE}/bookings/${bookingId}`, bookingData);
        const index = this.bookings.findIndex(b => b.id === bookingId);
        if (index !== -1) {
          this.bookings[index] = response.data;
        }
        await this.fetchRooms(); // sync room stats
        this.setTemporarySuccess('Booking updated successfully.');
        return true;
      } catch (error) {
        console.error('Error updating booking:', error);
        const backendMsg = error.response?.data?.message || 'Failed to update booking.';
        this.setTemporaryError(backendMsg);
        return false;
      } finally {
        this.loading = false;
      }
    }
  }
});
