<template>
  <div class="container-fluid">
        <div class="col-12">
            <UserSidebar />
        </div>

  <div class="container">
    <h2 class="page-title">我的報名</h2>
    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
      <p>加載中...</p>
    </div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    <div v-else-if="filteredRegistrations.length === 0" class="empty-state">
      您還沒有報名任何活動，或所有報名的活動已過期。
    </div>
    <div class="row" v-else>
      <div class="col-md-4 col-sm-6 mb-4" v-for="registration in filteredRegistrations" :key="registration.entryId">
        <div class="card">
          <router-link 
            :to="{ name: 'briefactivities-link', params: { activitiesId: registration.activityId } }" 
            class="card-link"
          >
            <img class="card-img" :src="getActivityImage(registration.activityId)" :alt="registration.activityName" @error="handleImageError">
            <div class="card-img-overlay">
              <div class="card-content">
                <h5 class="card-title text-white">{{ registration.activityName }}</h5>
                <p class="card-text text-white"><i class="fas fa-calendar-alt"></i> {{ formatDateRange(registration.activitiesStartDate, registration.activitiesEndDate) }}</p>
                <p class="card-text text-white"><i class="fas fa-map-marker-alt"></i> {{ registration.activitiesLocation }}</p>
                <p class="card-text text-white">
                  <i class="fas" :class="registration.isApproved ? 'fa-check-circle' : 'fa-hourglass-half'"></i>
                  審核狀態: 
                  <span :class="{'approved': registration.isApproved, 'pending': !registration.isApproved}">
                    {{ registration.isApproved ? '已通過' : '待審核' }}
                  </span>
                </p>
              </div>
            </div>
          </router-link>
          <div class="card-actions">
            <button @click="confirmCancelRegistration(registration.entryId)" 
                    :disabled="cancelling" 
                    class="btn btn-danger">
              <i class="fas fa-times-circle"></i>
              {{ cancelling ? '取消中...' : '取消報名' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axiosapi from '@/plugins/axios.js';
import userStore from '@/stores/user';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import UserSidebar from '@/components/02-user/UserSidebar.vue';

const router = useRouter();
const registrations = ref([]);
const loading = ref(true);
const error = ref(null);
const successMessage = ref('');
const cancelling = ref(false);
const user = userStore();
//
const fetchRegistrations = async () => {
  try {
    loading.value = true;
    error.value = null;
    successMessage.value = '';
    const response = await axiosapi.get('/attendees/registrations', {
      headers: {
        'Authorization': user.token
      }
    });
    registrations.value = response.data;
    // 獲取每個活動的圖片
    registrations.value.forEach(registration => {
      fetchActivityImage(registration.activityId);
    });

  } catch (err) {
    console.error('Error fetching registrations:', err);
    error.value = '獲取報名資料時出錯: ' + (err.response?.data || err.message);
  } finally {
    loading.value = false;
  }
};
const activityImages = ref({});

const fetchActivityImage = async (activityId) => {
  try {
    const response = await axiosapi.get(`/photos/download/${activityId}`, { responseType: 'blob' });
    const imageUrl = URL.createObjectURL(response.data);
    activityImages.value[activityId] = imageUrl;
  } catch (error) {
    console.error(`Error fetching image for activity ${activityId}:`, error);
  }
};


const getActivityImage = (activityId) => {
  return activityImages.value[activityId] || '/default-activity-image.png';
};

const handleImageError = (event) => {
  event.target.src = '/default-activity-image.png';
};

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-TW', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  });
};

const formatDateRange = (startDate, endDate) => {
  const start = new Date(startDate);
  const end = new Date(endDate);
  return `${start.toLocaleDateString('zh-TW')} - ${end.toLocaleDateString('zh-TW')}`;
};

const confirmCancelRegistration = async (entryId) => {
  const result = await Swal.fire({
    title: '確定要取消報名嗎？',
    text: "取消後將無法恢復！",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '是的，取消報名',
    cancelButtonText: '不，我再想想'
  });

  if (result.isConfirmed) {
    await cancelRegistration(entryId);
  }
};

const cancelRegistration = async (entryId) => {
  try {
    cancelling.value = true;
    error.value = null;
    successMessage.value = '';
    await axiosapi.delete(`/attendees/cancel/${entryId}`, {
      headers: {
        'Authorization': user.token
      }
    });
    Swal.fire(
      '取消成功！',
      '您已成功取消報名。',
      'success'
    );
    await fetchRegistrations();
  } catch (err) {
    console.error('取消報名時出錯:', err);
    Swal.fire(
      '出錯了！',
      '取消報名時出錯: ' + (err.response?.data || err.message),
      'error'
    );
  } finally {
    cancelling.value = false;
  }
};


const filteredRegistrations = computed(() => {
  const now = new Date();
  return registrations.value.filter(registration => {
    const endDate = new Date(registration.activitiesEndDate);
    // 只返回結束日期在當前時間之後且 activitiesReportStatus 為 "N" 的活動
    return endDate > now && registration.activitiesReportStatus === "N";
  });
});

onMounted(fetchRegistrations);
</script> 


<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  color: #2c3e50;
  text-align: center;
  margin-bottom: 30px;
  font-size: 2.5rem;
}

.card {
  border: none;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
  height: 400px;
  width: 100%;
  margin-bottom: 20px;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
}

.card-img-overlay {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0.3) 50%, rgba(0,0,0,0) 100%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 1.5rem;
}

.card-content {
  color: white;
  z-index: 1;
  padding-bottom: 40px;
}

.card-actions {
  position: absolute;
  bottom: 15px;
  right: 15px;
  z-index: 2;
}

.card-title {
  font-size: 1.4rem;
  font-weight: bold;
  margin-bottom: 0.75rem;
  text-shadow: 1px 1px 3px rgba(0,0,0,0.6);
}

.card-text {
  font-size: 1rem;
  margin-bottom: 0.5rem;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.6);
}

.btn {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
  border-radius: 5px;
  transition: all 0.3s ease;
}

.btn-danger {
  background-color: #dc3545;
  border-color: #dc3545;
}

.btn-danger:hover {
  background-color: #c82333;
  border-color: #bd2130;
}

.loading-spinner, .error-message, .success-message, .empty-state {
  text-align: center;
  padding: 20px;
  margin-bottom: 20px;
}

.approved {
  color: #28a745;
}

.pending {
  color: #ffc107;
}

.container-fluid {
    padding-top: 20px;
}

</style>