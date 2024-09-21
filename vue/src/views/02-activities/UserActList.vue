<template>
  <div class="container-fluid">
        <div class="col-12">
            <UserSidebar />
        </div>

  <div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div class="d-flex align-items-center">
        <button
          @click="checkAuthentication"
          class="btn btn-success me-2"
          title="新增活動"
        >
          <font-awesome-icon :icon="['fas', 'plus']"/> 創建活動
        </button>
      
        <button
          @click="$router.push({ name: 'ActivityReview-link' })"
          class="btn btn-success"
          title="審核活動"
        >
          <font-awesome-icon icon="fa-solid fa-user-check"/> 活動審核
        </button>
      </div>
    </div>



    <h2>Coming Soon</h2>
    <div class="row">
      <div class="col-md-4 col-sm-6 mb-4" v-for="activity in upcomingActivities" :key="activity.activitiesId">
        <div class="card" :class="{ 'reported': activity.activitiesReportStatus === 'R' }">
          <router-link 
            :to="{ name: 'briefactivities-link', params: { activitiesId: activity.activitiesId } }" 
            class="card-link"
            :class="{ 'disabled': activity.activitiesReportStatus === 'R' }"
            @click.prevent="handleActivityClick(activity)"
          >
            <img class="card-img" :src="activityImages[activity.activitiesId]" alt="Activity Image">
            <div class="card-img-overlay">
              <div class="card-content">
                <div class="user-info d-flex align-items-center mb-2">
                  <img :src="getProfilePicUrl(activity.users?.usersProfile?.userPics ?? '')" 
                       alt="User Photo" 
                       class="rounded-circle user-avatar"
                       @error="handleImageError">
                  <span class="ml-2 text-white">{{ activity.users.usersProfile.userName }}</span>
                </div>
                <h5 class="card-title text-white">{{ activity.activitiesTitle }}</h5>
                <span class="card-text text-white">{{ formatDate(activity.activitiesStartDate) }}</span>
                <span> - </span>
                <span class="card-text text-white">{{ formatDate(activity.activitiesEndDate) }}</span>
              </div>
            </div>
          </router-link>
          
          <div class="card-actions" v-if="activity.activitiesReportStatus !== 'R'">
            <router-link :to="{ name: 'editActPage-link', params: { activitiesId: activity.activitiesId } }">
              <button type="button" class="btn btn-primary">編輯</button>
            </router-link>
            <button type="button" class="btn btn-danger" @click="deleteActivity(activity.activitiesId)">刪除</button>
          </div>
          <div class="reported-message" v-if="activity.activitiesReportStatus === 'R'">
            此活動已被檢舉
          </div>
        </div>
      </div>
    </div>

    <h2>已結束的活動</h2>
    <div class="row">
      <div class="col-md-4 col-sm-6 mb-4" v-for="activity in pastActivities" :key="activity.activitiesId">
        <div class="card past-activity">
          <router-link :to="{ name: 'briefactivities-link', params: { activitiesId: activity.activitiesId } }" class="card-link">
            <img class="card-img" :src="activityImages[activity.activitiesId]" alt="Activity Image">
            <div class="card-img-overlay">
              <div class="card-content">
                <div class="user-info d-flex align-items-center mb-2">
                  <img :src="getProfilePicUrl(activity.users?.usersProfile?.userPics ?? '')" 
                       alt="User Photo" 
                       class="rounded-circle user-avatar"
                       @error="handleImageError">
                  <span class="ml-2 text-white">{{ activity.users.usersProfile.userName }}</span>
                </div>
                <h5 class="card-title text-white">{{ activity.activitiesTitle }}</h5>
                <span class="card-text text-white">{{ formatDate(activity.activitiesStartDate) }}</span>
                <span> - </span>
                <span class="card-text text-white">{{ formatDate(activity.activitiesEndDate) }}</span>
              </div>
            </div>
          </router-link>
          
          <div class="card-actions">
            <button type="button" class="btn btn-danger" @click="deleteActivity(activity.activitiesId)">刪除</button>
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
import { useRouter } from 'vue-router';
import userStore from '@/stores/user';
import Swal from 'sweetalert2';
import UserSidebar from '@/components/02-user/UserSidebar.vue';

const upcomingActivities = ref([]);
const pastActivities = ref([]);
// const userActivities = ref([]);
const activityImages = ref({});
const router = useRouter();
const user = userStore();

onMounted(() => {
  fetchUserActivities();
});

function checkAuthentication() {
  if (user.isLogedin) {
    router.push({ name: 'addActPage-link' });
  } else {
    redirectToLogin();
  }
}

function redirectToLogin() {
  router.push({ name: 'login' });
}

function fetchUserActivities() {
  axiosapi.get('/usersActList', {
    headers: {
      'Authorization': user.token
    }
  })
  .then(response => {
        // 過濾掉 activitiesReportStatus 為 "D" 的活動
  // upcomingActivities.value = response.data.upcomingActivities.filter(activity => activity.activitiesReportStatus !== 'D');
  // pastActivities.value = response.data.pastActivities.filter(activity => activity.activitiesReportStatus !== 'D');
    upcomingActivities.value = response.data.upcomingActivities;
    pastActivities.value = response.data.pastActivities;
    [...upcomingActivities.value, ...pastActivities.value].forEach(activity => {
      fetchActivityImage(activity.activitiesId);
    });
  })
  .catch(error => {
    console.error('抓取活動錯誤:', error);
  });
}

function handleActivityClick(activity) {
  if (activity.activitiesReportStatus === 'R') {
    Swal.fire('無法訪問', '此活動已被檢舉，無法查看詳情。', 'warning');
  } else {
    router.push({ name: 'briefactivities-link', params: { activitiesId: activity.activitiesId } });
  }
}



function fetchActivityImage(activityId) {
  axiosapi.get(`/photos/download/${activityId}`, { responseType: 'blob' })
    .then(response => {
      const imageUrl = URL.createObjectURL(response.data);
      activityImages.value[activityId] = imageUrl;
    })
    .catch(error => {
      console.error(`取得活動 ${activityId} 圖片時發生錯誤:`, error);
    });
}

function getProfilePicUrl(picPath) {
  if (!picPath) return 'http://localhost:8080/meetu/images/userPics/default-pic.png';
  if (picPath.startsWith('/')) return `http://localhost:8080/meetu${picPath}`;
}

function handleImageError(event) {
  event.target.src = '/Bob.png';
}

function formatDate(dateString) {
  const date = new Date(dateString);
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`;
}

function deleteActivity(activityId) {
  Swal.fire({
    title: '確定要刪除活動嗎？',
    text: "刪除後將無法恢復！",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '確定刪除',
    cancelButtonText: '取消'
  }).then((result) => {
    if (result.isConfirmed) {
      axiosapi.put(`/activities/${activityId}/delete`, {}, {
        headers: {
          Authorization: user.token,
        }
      })
      .then(response => {
        if (response.data === 'Activity marked as deleted') {
          upcomingActivities.value = upcomingActivities.value.filter(activity => activity.activitiesId !== activityId);
          pastActivities.value = pastActivities.value.filter(activity => activity.activitiesId !== activityId);
          Swal.fire('已刪除!', '您的活動已成功刪除。', 'success');
        } else {
          Swal.fire('刪除失敗', '無法刪除活動，請稍後再試。', 'error');
        }
      })
      .catch(error => {
        console.error('刪除活動時發生錯誤:', error);
        Swal.fire('刪除失敗', '發生錯誤，請稍後再試。', 'error');
      });
    }
  });
}


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
  padding-bottom: 60px; 
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
  margin-bottom: 3rem; 
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

.card-actions {
  position: absolute;
  bottom: 10px; 
  left: 10px;
  right: 10px;
  display: flex;
  justify-content: space-between;
  padding: 0 1rem;
  z-index: 10; 
}

.card-actions .btn {
  margin: 0;
}

.add-icon {
  font-size: 3rem; 
  color: #86b817;
  text-decoration: none;
}

.icon-style {
  font-size: 3rem; 
  color:	#E0E0E0; 
  margin-right: 1rem; 
}

.mb-4 {
  margin-bottom: 1.5rem;
}

.reported {
  opacity: 0.7;
  pointer-events: none;
}

.reported-message {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(255, 0, 0, 0.7);
  color: white;
  padding: 10px;
  border-radius: 5px;
}

.past-activity {
  opacity: 0.8;
}

.disabled {
  pointer-events: none;
}
.btn-success {
  background-color: #86b817;
  border-color: #86b817;
}

.btn-success:hover {
  background-color: #6a9313;
  border-color: #6a9313;
}



.container-fluid {
    padding-top: 20px;
}
</style>
