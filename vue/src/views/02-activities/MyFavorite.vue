<template>
  <div class="container-fluid">
        <div class="col-12">
            <UserSidebar />
        </div>

  <div class="container">
    <h2 class="page-title">我的收藏</h2>
    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
      <p>加載中...</p>
    </div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    <div v-else-if="filteredFavorites.length === 0" class="empty-state">
      <p>您尚未收藏任何活動，趕快去探索活動吧！</p>
      <button @click="goToExploreActivities" class="btn btn-primary explore-btn">
        探索活動
      </button>
    </div>
    <div class="row" v-else>
      <div class="col-md-4 col-sm-6 mb-4" v-for="favorite in filteredFavorites" :key="favorite.actCollectId">
        <div class="card">
          <router-link 
            :to="{ name: 'briefactivities-link', params: { activitiesId: favorite.activitiesId } }" 
            class="card-link"
          >
            <img class="card-img" :src="getActivityImage(favorite.activitiesId)" :alt="favorite.activitiesTitle" @error="handleImageError">
            <div class="card-img-overlay">
              <div class="card-content">
                <h5 class="card-title text-white">{{ favorite.activitiesTitle }}</h5>
                <p class="card-text text-white"><i class="fas fa-calendar-alt"></i> {{ formatDateRange(favorite.activitiesStartDate, favorite.activitiesEndDate) }}</p>
                <p class="card-text text-white"><i class="fas fa-map-marker-alt"></i> {{ favorite.activitiesLocation }}</p>
              </div>
            </div>
          </router-link>
          <div class="card-actions">
            <button @click="confirmRemoveFavorite(favorite.activitiesId)" 
                    :disabled="removing" 
                    class="btn btn-danger">
              <i class="fas fa-heart-broken"></i>
              {{ removing ? '移除中...' : '移除收藏' }}
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
import Swal from 'sweetalert2';
import UserSidebar from "@/components/02-user/UserSidebar.vue";
import { useRouter } from 'vue-router';

const router = useRouter();
const favorites = ref([]);
const loading = ref(true);
const error = ref(null);
const successMessage = ref('');
const removing = ref(false);
const user = userStore();

const fetchFavorites = async () => {
  try {
    loading.value = true;
    error.value = null;
    successMessage.value = '';
    const response = await axiosapi.get('/collect', {
      headers: {
        'Authorization': user.token
      }
    });
    favorites.value = response.data;
    favorites.value.forEach(favorites => {
      fetchActivityImage(favorites.activitiesId);
    });
  } catch (err) {
    console.error('Error fetching favorites:', err);
    error.value = '獲取收藏資料時出錯: ' + (err.response?.data || err.message);
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

const formatDateRange = (startDate, endDate) => {
  if (!startDate || !endDate) return '日期未定';
  const start = new Date(startDate);
  const end = new Date(endDate);
  return `${start.toLocaleDateString('zh-TW')} - ${end.toLocaleDateString('zh-TW')}`;
};

const confirmRemoveFavorite = async (activityId) => {
  const result = await Swal.fire({
    title: '確定要移除這個收藏嗎？',
    text: "移除後將無法恢復！",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '是的，移除收藏',
    cancelButtonText: '不，我再想想'
  });

  if (result.isConfirmed) {
    await removeFavorite(activityId);
  }
};


const removeFavorite = async (activityId) => {
  try {
    removing.value = true;
    error.value = null;
    successMessage.value = '';
    await axiosapi.delete(`/collect/${activityId}`, {
      headers: {
        'Authorization': user.token
      }
    });
    await Swal.fire(
      '移除成功！',
      '您已成功移除收藏。',
      'success'
    );
    await fetchFavorites();
  } catch (err) {
    console.error('移除收藏時出錯:', err);
    await Swal.fire(
      '出錯了！',
      '移除收藏時出錯: ' + (err.response?.data || err.message),
      'error'
    );
  } finally {
    removing.value = false;
  }
};

const filteredFavorites = computed(() => {
  return favorites.value
    .filter(favorite => favorite.activitiesReportStatus === "N")
    .sort((a, b) => new Date(b.activitiesStartDate) - new Date(a.activitiesStartDate));
});

const goToExploreActivities = () => {
  router.push({ name: "activities-link" });
};

onMounted(fetchFavorites);
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
p {
    margin: 5px 0;
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

.container-fluid {
    padding-top: 20px;
}
</style>