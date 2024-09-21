<template>
  <div class="container">
    <router-link 
      :to="{ name: 'addActPage-link' }" 
      @click.prevent="checkAuthentication" 
      class="btn btn-lg btn-primary btn-lg-square add-activity-btn"
      title="新增活動"
    >
      <font-awesome-icon :icon="['fas', 'plus']" />
    </router-link>

    <br>
    <div class="search-and-buttons-container mb-4">
      <div class="buttons-group">
  <button 
    class="btn me-2" 
    :class="{'btn-gray': currentFilter !== 'all', 'btn-primary': currentFilter === 'all'}" 
    @click="filterActivities('all')"
  >
    全部
  </button>
  <button 
    class="btn me-2" 
    :class="{'btn-gray': currentFilter !== 'O', 'btn-primary': currentFilter === 'O'}" 
    @click="filterActivities('O')"
  >
    戶外
  </button>
  <button 
    class="btn me-2" 
    :class="{'btn-gray': currentFilter !== 'I', 'btn-primary': currentFilter === 'I'}" 
    @click="filterActivities('I')"
  >
    室內
  </button>
  <button 
    class="btn me-2" 
    :class="{'btn-gray': currentFilter !== 'M', 'btn-primary': currentFilter === 'M'}" 
    @click="filterActivities('M')"
  >
    混合
  </button>
</div>
      
<div class="center-search">
  <div class="search-group">
    <input 
      type="text" 
      v-model="searchQuery" 
      :class="{'no-results': noResultsMessage}"
      class="form-control" 
      :placeholder="noResultsMessage || '來探索有趣的聚會吧...'"
      @keyup.enter="searchActivities"
      @input="onSearchInput"
    >
    <button class="btn btn-primary" @click="searchActivities">
      <font-awesome-icon :icon="['fas', 'search']" />
    </button>
  </div>
  <div v-if="noResultsMessage" class="no-results-message">
    {{ noResultsMessage }}
  </div>
</div>
 

    </div>
    </div>

    <div class="row">
      <div class="col-md-4 col-sm-6 mb-4" v-for="activity in validActivities" :key="activity.activitiesId">
        <div class="card">
          <router-link :to="{ name: 'briefactivities-link', params: { activitiesId: activity.activitiesId } }" class="card-link">
            <img class="card-img" :src="activityImages[activity.activitiesId]" alt="Activity Image">
            <div class="activity-type-badge">
              <font-awesome-icon :icon="['far', 'credit-card']" class="credit-card-icon" />
              <span>{{ getActivitySharingText(activity.activitiesSharing) }}</span>
            </div>


            <div class="card-img-overlay">
              <div class="card-content">
                <div class="user-info d-flex align-items-center mb-2">
                  <img :src="getProfilePicUrl(activity.users?.usersProfile?.userPics ?? '')" 
                       alt="User Photo" 
                       class="rounded-circle user-avatar"
                       >
                  <span class="ml-2 text-white">{{ activity.users.usersProfile.userName }}</span>
                </div>
                <h5 class="card-title text-white">{{ activity.activitiesTitle }}</h5>
                <h4 class="card-text text-white">
                  <font-awesome-icon :icon="['fas', 'location-dot']" />
                  {{ activity.activitiesLocation }}</h4>
                <span class="card-text text-white">{{ formatDate(activity.activitiesStartDate) }}</span>
                <span> - </span>
                <span class="card-text text-white">{{ formatDate(activity.activitiesEndDate) }}</span>
              </div>
            </div>
          </router-link>
        </div>
      </div>
    </div>
  
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axiosapi from '@/plugins/axios.js';
import { useRouter } from 'vue-router';
import userStore from '@/stores/user';

const activities = ref([]);
const filteredActivities = ref([]);
const currentFilter = ref('all');

const activityImages = ref({});
const router = useRouter();
const user = userStore();

const searchQuery = ref('');
const searchResults = ref([]);
const noResultsMessage = ref('');


onMounted(() => {
  fetchActivities();
});

async function searchActivities() {
  if (searchQuery.value.trim() === '') {
    searchResults.value = [];
    noResultsMessage.value = '';
    return;
  }

  try {
    const response = await axiosapi.get('/activities/search', {
      params: { query: searchQuery.value }
    });
    searchResults.value = response.data;
    if (searchResults.value.length === 0) {
      noResultsMessage.value = '無相關活動';
    } else {
      noResultsMessage.value = '';
    }
  } catch (error) {
    console.error('Error searching activities:', error);
    searchResults.value = [];
    noResultsMessage.value = '搜尋出錯，請稍後再試';
  }
}

function onSearchInput() {
  if (searchQuery.value.trim() === '') {
    noResultsMessage.value = '';
    searchResults.value = [];
  }
}

function checkAuthentication(event) {
  if (user.isLogedin) {
    router.push({ name: 'addActPage-link' });
  } else {
    event.preventDefault();
    redirectToLogin();
  }
}


function redirectToLogin() {
  router.push({ name: 'login' });
}

function filterActivities(type) {
  currentFilter.value = type;
  if (type === 'all') {
    filteredActivities.value = activities.value;
  } else {
    filteredActivities.value = activities.value.filter(activity => 
      activity.activitiesType === type &&
      activity.activitiesReportStatus !== 'R' &&
      activity.activitiesReportStatus !== 'D'
    );
  }
}

const validActivities = computed(() => {
  const now = new Date();
  const activeActivities = (searchResults.value.length > 0 ? searchResults.value : filteredActivities.value)
    .filter(activity => 
      new Date(activity.activitiesEndDate) > now &&
      activity.activitiesReportStatus !== 'R' &&
      activity.activitiesReportStatus !== 'D'
    );
  return activeActivities;
});

function fetchActivities() {
  axiosapi.get('/activities')
    .then(response => {
      activities.value = response.data.filter(activity => 
        activity.activitiesReportStatus !== 'R' &&
        activity.activitiesReportStatus !== 'D'
        
      );
      filterActivities('all'); // 初始顯示全部活動

       // 為每個活動獲取圖片
       activities.value.forEach(activity => {
        fetchActivityImage(activity.activitiesId);
       });
    })
    .catch(error => {
      console.error('Error fetching activities:', error);
    });
}


function fetchActivityImage(activityId) {
  axiosapi.get(`/photos/download/${activityId}`, { responseType: 'blob' })
    .then(response => {
      const imageUrl = URL.createObjectURL(response.data);
      activityImages.value[activityId] = imageUrl;
    })
    .catch(error => {
      console.error(`Error fetching image for activity ${activityId}:`, error);
    });
}

function getProfilePicUrl(picPath) {
  if (!picPath) return 'http://localhost:8080/meetu/images/userPics/default-pic.png';
  if (picPath.startsWith('/')) return `http://localhost:8080/meetu${picPath}`;
}

function formatDate(dateString) {
  const date = new Date(dateString);
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`;
}
const getActivitySharingText = (sharingType) => {
  switch(sharingType) {
    case 'I':
      return '我請客';
    case 'Y':
      return '你付錢';
    case 'S':
      return '各付各';
    default:
      return sharingType || '未指定';
  }
};


</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
  height: 400px;
  width: 100%; 
  margin-bottom: 20px;
}

.activity-type-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background-color:#FFD700;
  color: 	#6C6C6C;
  padding: 5px 10px;
  border-radius: 5px;
  font-size: 1.1em;
  z-index: 10;
}

.activity-type-badge .fa-credit-card {
  margin-right: 5px;
}

.activity-type-badge .credit-card-icon {
  margin-right: 5px;
  color:	#8E8E8E;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  height: 400px;
}

.card-img-overlay {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0) 70%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 1.5rem;
}

.card-content {
  color: white;
}

.user-avatar {
  width: 30px;
  height: 30px;
  object-fit: cover;
}

.card-title {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.card-text {
  font-size: 0.9rem;
}

.card-link {
  text-decoration: none;
  color: inherit;
}

.add-icon {
  font-size: 3rem; 
  color: #86b817;
  text-decoration: none;
}

.add-activity-btn {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  z-index: 1000;
  background-color: #FFD700; 
  border: none; 
  color:	#8E8E8E; 
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
  transition: all 0.3s ease;
}

.add-activity-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
  background-color: #F0C200;
}


.icon-style {
  font-size: 3rem; 
  color: 	#E0E0E0; 
  margin-right: 1rem; 
}
.search-and-buttons-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: nowrap;
}

.left-buttons {
  width: 100%;
}

.buttons-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.center-search {
  flex: 0 1 300px;
  min-width: 200px;
}

.search-group input {
  flex-grow: 1;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  transition: all 0.3s ease;
}

.search-group button {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.search-group {
  position: relative;
  display: flex;
  width: 100%;
}

.search-group input::placeholder {
  color: #999;
  transition: color 0.3s ease;
}

.search-group input:focus::placeholder {
  color: transparent;
}

.search-group input.no-results {
  border-color: #d9534f;
  color: #d9534f;
}

.search-group input.no-results::placeholder {
  color: #d9534f;
}

.btn-gray {
  background-color: #f0f0f0;
  color: #6C6C6C;
  border: none;
  transition: all 0.3s ease;
}

.btn-gray:hover {
  background-color: #86b817;
  color: #6C6C6C;
}
.btn-primary {
  background-color: #86b817;
  color: white;
  border: none;
  transition: all 0.3s ease;
}
.btn-primary:hover {
  background-color: #749f14;
  color: white;
}

.alert {
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}
.no-results-message {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  padding: 5px;
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 0 0 4px 4px;
  font-size: 0.9em;
  z-index: 10;
}

</style>