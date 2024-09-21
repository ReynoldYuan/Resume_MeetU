<template>
  <div class="container-fluid">
    <div class="col-12">
      <UserSidebar />
    </div>
    
    <div class="container">
      <h2 class="page-title">已參加的活動</h2>
      <div v-if="loading" class="loading-spinner">
        <div class="spinner"></div>
        <p>加載中...</p>
      </div>
      <div v-else-if="error" class="error-message">{{ error }}</div>
      <div v-else-if="filteredActivities.length === 0" class="empty-state">
        <p>您尚未參加過活動，或所有參加的活動已被刪除或報告。</p>
        <button @click="goToExploreActivities" class="btn btn-primary explore-btn">
          探索活動
        </button>
      </div>
      <div class="row" v-else>
        <div class="col-md-4 col-sm-6 mb-4" v-for="activity in filteredActivities" :key="activity.entryId">
          <div class="card">
            <router-link 
              :to="{ name: 'briefactivities-link', params: { activitiesId: activity.activityId } }" 
              class="card-link"
            >
              <img class="card-img" :src="getActivityImage(activity.activityId)" :alt="activity.activityName" @error="handleImageError">
              <div class="card-img-overlay">
                <div class="card-content">
                  <h5 class="card-title text-white">{{ activity.activityName }}</h5>
                  <p class="card-text text-white"><i class="fas fa-calendar-alt"></i> {{ formatDateRange(activity.activitiesStartDate, activity.activitiesEndDate) }}</p>
                  <p class="card-text text-white"><i class="fas fa-map-marker-alt"></i> {{ activity.activitiesLocation || '地點未提供' }}</p>
                  <p class="card-text text-white">
                    <i class="fas fa-check-circle"></i>
                    參加狀態: <span class="completed">已參加</span>
                  </p>
                </div>
              </div>
            </router-link>
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
import UserSidebar from "@/components/02-user/UserSidebar.vue";
import { useRouter } from 'vue-router';

const allActivities = ref([]);
const loading = ref(true);
const error = ref(null);
const user = userStore();
const activityImages = ref({});
const router = useRouter();

const filteredActivities = computed(() => {
  const now = new Date();
  return allActivities.value.filter(activity => 
    new Date(activity.activitiesEndDate) < now && 
    activity.activitiesReportStatus === "N"
  );
});

const fetchAllActivities = async () => {
  try {
    loading.value = true;
    error.value = null;

    const response = await axiosapi.get('/attendees/past-activities', {
      headers: {
        'Authorization': user.token
      }
    });

    if (Array.isArray(response.data) && response.data.length > 0) {
      allActivities.value = response.data;
    } else if (response.data && Array.isArray(response.data.activities) && response.data.activities.length > 0) {
      allActivities.value = response.data.activities;
    }

    allActivities.value.forEach(activity => {
      fetchActivityImage(activity.activityId);
    });
  } catch (err) {
    console.error('Error fetching activities:', err);
    error.value = '獲取活動資料時出錯: ' + (err.response?.data || err.message);
  } finally {
    loading.value = false;
  }
};

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

const formatDateRange = (startDate, endDate) => {
  const start = new Date(startDate);
  const end = new Date(endDate);
  return `${start.toLocaleDateString('zh-TW')} - ${end.toLocaleDateString('zh-TW')}`;
};

const goToExploreActivities = () => {
  router.push({ name: "activities-link" }); 
};

onMounted(() => {
  console.log('Component mounted');
  fetchAllActivities();
});
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

.loading-spinner, .error-message, .empty-state {
  text-align: center;
  padding: 20px;
  margin-bottom: 20px;
}

.completed {
  color: #28a745;
  font-weight: bold;
}

.explore-btn {
  margin-top: 15px;
  padding: 10px 20px;
  font-size: 1em;
  background-color: #86b817;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.explore-btn:hover {
  background-color: #28a745;
}

@media (max-width: 768px) {
  .card {
    height: 300px;
  }
}

.container-fluid {
  padding-top: 20px;
}
</style>