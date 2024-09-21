<template>
  <div class="container">
    <div class="row">
      <div class="col-7">
        <div class="activity-carousel">
          <swiper v-if="activities.length > 0" :modules="modules" :slides-per-view="1" :space-between="30"
            :pagination="{ clickable: true }" :navigation="true"
            :autoplay="{ delay: 3000, disableOnInteraction: false }">
            <swiper-slide v-for="activity in activities" :key="activity.activitiesId">
              <div class="activity-slide">
                <div class="image-container"
                  :style="{ backgroundImage: `url(${getActivityImage(activity.activitiesId)})` }"></div>
                <div class="activity-info">
                  <div class="info-content">
                    <div class="user-info">
                      <img :src="getProfilePicUrl(activity.users?.usersProfile?.userPics ?? '')" alt="User Photo"
                        class="user-avatar" @error="handleImageError">
                      <span class="user-name">{{ activity.users.usersProfile.userName }}</span>
                    </div>
                    <h3 class="activity-title">{{ activity.activitiesTitle }}</h3>
                    <p class="activity-date">
                      <font-awesome-icon icon="fa-regular fa-clock" />
                      {{ formatDate(activity.activitiesStartDate) }} - {{
                      formatDate(activity.activitiesEndDate) }}</p>
                    <p class="activity-location">
                      <font-awesome-icon :icon="['fas', 'location-dot']" />
                      {{ activity.activitiesLocation }}</p>
                  </div>
                  <router-link :to="{ name: 'briefactivities-link', params: { activitiesId: activity.activitiesId } }"
                    class="details-link">
                    探索活動
                  </router-link>
                </div>
              </div>
            </swiper-slide>
          </swiper>
          <div v-else class="loading">加載中...</div>
        </div>
      </div>
      <div class="col slogan-container">
        <div class="slogan-content">
        <h1 class="mb-4">歡迎來到<span class="text-primary">MeetU 覓友.com</span></h1>
        <h3>無論想聚餐、運動或旅行，<br>
          這裡為你提供所有可能！</h3>
        <h5>在這裡，結識與你志同道合的夥伴，打造專屬的回憶</h5>
        <h5>無論是興趣還是愛好，這裡都是你與新朋友相遇的起點</h5>
      </div>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Pagination, Navigation, Autoplay } from 'swiper/modules';
import 'swiper/css';
import 'swiper/css/pagination';
import 'swiper/css/navigation';
import axiosapi from '@/plugins/axios.js';

const activities = ref([]);
const activityImages = ref({});
const modules = [Pagination, Navigation, Autoplay];

onMounted(() => {
  fetchActivities();
});

function fetchActivities() {
  axiosapi.get('/activities')
    .then(response => {
      activities.value = response.data;
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
      activityImages.value[activityId] = '/Bob.png';
    });
}

function getActivityImage(activityId) {
  return activityImages.value[activityId] || '/Bob.png';
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
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
}
</script>

<style scoped>

h1, h2, h3 {
  font-family: "Nunito", sans-serif;
  font-optical-sizing: auto;
  font-weight: 700;
  font-style: normal;
}

.activity-carousel {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  box-shadow: 0 4px 20px rgba(122, 12, 12, 0.1);
  border-radius: 15px;
  overflow: hidden;
}

.activity-slide {
  position: relative;
  height: 400px;
}

.image-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  transition: transform 0.3s ease;
}

.activity-slide:hover .image-container {
  transform: scale(1.05);
}

.activity-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;

  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  transition: background 0.3s ease;
}



.info-content {
  flex-grow: 1;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 50%;
  border: 2px solid white;
  margin-right: 10px;
}

.user-name {
  font-size: 1rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

.activity-title {
  font-size: 1.8rem;
  font-weight: bold;
  margin: 10px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

.activity-date,
.activity-location {
  font-size: 1rem;
  margin: 5px 0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}

.details-link {
  display: inline-block;
  padding: 12px 24px;
  background: 		#F0F0F0;
  color: #333;
  text-decoration: none;
  border-radius: 30px;
  transition: all 0.3s ease;  
  font-size: 1.1rem;
  font-weight: bold;
  text-transform: uppercase;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.details-link:hover {
  background: #F0F0F0;
  transform: translateY(-2px);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
}

.loading {
  height: 600px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.2rem;
  color: #666;
}

/* Swiper 自定義樣式 */
:deep(.swiper-button-next),
:deep(.swiper-button-prev) {
  color: white;
  background: rgba(0, 0, 0, 0.5);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

:deep(.swiper-button-next:hover),
:deep(.swiper-button-prev:hover) {
  background: rgba(0, 0, 0, 0.8);
  transform: scale(1.1);
}

:deep(.swiper-pagination-bullet) {
  background: white;
  opacity: 0.6;
  width: 12px;
  height: 12px;
}

:deep(.swiper-pagination-bullet-active) {
  opacity: 1;
  background: white;
}

.slogan-container {
  display: flex;
  align-items: center;
  height: 100%; /* 確保容器高度填滿父元素 */
  color: #2C3E50;
  padding-left: 30px;
}

.slogan-content {
  width: 100%; /* 確保內容寬度填滿容器 */
}
.col {
  height: 400px; /* 或者其他適合你佈局的高度 */
}

</style>