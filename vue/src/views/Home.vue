<template>

  <Carousel />
  <div style="margin-top: 30px;">
    <ActivityCarousel />
  </div>
  <div style="margin-top: 30px;"></div>

  <div class="row">
    <div class="col-6 text-column">
      <div class="text-content" style="text-align: right;">
        <h1 class="mb-4"><span class="text-primary">每日配對</span>，<br>找到與你心靈契合的另一半</h1>
        <h3>心動即時，幸福只需輕輕一滑！</h3>
        <h5>(偷偷告訴你，互相追蹤也可以彼此聊天唷)</h5>
      </div>
    </div>
    <div class="col-6 image-column">
      <div class="image-container">
        <img src="http://localhost:8080/meetu/images/userPics/home-matching.jpeg" alt="MeetU Home Matching">
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-6 image-column">
      <div class="image-container">
        <img src="http://localhost:8080/meetu/images/userPics/home-post.webp" alt="MeetU Home Post">
      </div>
    </div>
    <div class="col-6 text-column">
      <div class="text-content">
        <h1 class="mb-4">發表你的第一則<span class="text-primary">心情貼文</span>吧!</h1>
        <h3>捕捉每一刻<br>分享你獨一無二生活動態</h3>
        <h5>每一段影片都是你生活的一部分，現在就讓世界看到</h5>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-6 text-column">
      <div class="text-content" style="text-align: right;">
        <h1 class="mb-4">真實交友，<span class="text-primary">拒絕詐騙</span></h1>
        <h3>我們是你最忠實的交友守門員！</h3>
        <h3>檢舉詐騙或惡意活動，審核立即下架</h3>
      </div>
    </div>
    <div class="col-6 image-column">
      <div class="image-container">
        <img src="http://localhost:8080/meetu/images/userPics/home-report.webp" alt="MeetU Home Matching">
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-6 image-column">
      <div class="image-container">
        <img src="http://localhost:8080/meetu/images/userPics/home-noti.webp" alt="MeetU Home Post">
      </div>
    </div>
    <div class="col-6 text-column">
      <div class="text-content">
        <h1 class="mb-4"><span class="text-primary">即時通知</span>不漏接</h1>
        <h3>掌握活動最新消息，精彩不漏接</h3>
      </div>
    </div>
  </div>

</template>

<script setup>
import Carousel from './00-common/Carousel.vue';
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import useUserStore from '@/stores/user.js';
import ActivityCarousel from './02-activities/ActivityCarousel.vue';

const route = useRoute()
const userStore = useUserStore();

onMounted(() => {
  // Check if there's userData in the URL (from OAuth callback)
  const encodedUserData = route.query.userData

  if (encodedUserData) {
    try {
      const decodedUserData = decodeURIComponent(encodedUserData);
      const userData = JSON.parse(decodedUserData);

      // Store user data in Pinia store
      userStore.setToken(userData.token);
      userStore.setId(userData.googleId);
      userStore.setEmail(userData.email);
      userStore.setName(userData.name);
      userStore.setPics(userData.picture);
      userStore.setLogedin(true);

      // Also save to SessionStorage as a backup
      //       Object.entries(userData).forEach(([key, value]) => {
      //         sessionStorage.setItem(key, value)
      //       })

      console.log('User data saved from OAuth callback')
    } catch (error) {
      console.error('Error parsing user data:', error)
    }
  } else if (!userStore.email && sessionStorage.getItem('user')) {
    // If no userData in URL, check SessionStorage
    userStore.setId(sessionStorage.getItem('user').id);
    userStore.setEmail(sessionStorage.getItem('user').email);
    userStore.setName(sessionStorage.getItem('user').name);
    userStore.setPics(sessionStorage.getItem('user').pics);
    userStore.setLogedin(true);

    console.log('User data loaded from SessionStorage');
  } else if (userStore.email && sessionStorage.getItem('user')) {
    console.log('There has a User data in SessionStorage');
  } else {
    console.log('There is no User data in SessionStorage');
  }
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px; /* 添加这行 */
}

.row {
  display: flex;
  height: 400px;
  margin-bottom: 30px;
}

.image-column,
.text-column {
  width: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-container {
  width: calc(100% - 20px); /* 修改这行 */
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

/* 添加这些新的样式 */
.row:nth-child(odd) .image-container {
  margin-left: 20px;
}

.row:nth-child(even) .image-container {
  margin-right: 20px;
}

.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 15px;
}


.text-content {
  padding: 20px;
  text-align: left;
}

h1,
h2,
h3,
h5 {
  font-family: "Nunito", sans-serif;
  font-optical-sizing: auto;
  font-weight: 700;
  font-style: normal;
  color: #2C3E50;
}

.text-primary {
  color: #007bff;
}
</style>