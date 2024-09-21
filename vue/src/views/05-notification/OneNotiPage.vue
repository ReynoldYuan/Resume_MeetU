<template>
    <div class="container">
        <h1>系統通知<span class="ts-icon is-bullhorn-icon"></span></h1>
        <br>
        <div v-if="notification" class="ts-box is-raised is-gray is-top-indicated" >
            <div class="ts-content is-dense has-light">
                <div class="notification-header">
                    <span class="notification-title">{{ notification.notificationTitle }}</span>
                    <span class="notification-time">最後更新時間：{{ formatDate(notification.notificationTime) }}</span>
                </div>
                <div class="notification-type">
                    {{ notification.isGlobal ? '全體通知' : '個人通知' }}
                </div>
            </div>
        <div class="ts-divider"></div>
        <div class="notification-content-wrapper">
          <div class="ts-content notification-content">
            <!-- <div class="ts-content" style="height: 200px;"> -->
                {{ notification.notificationContent }}
            </div>
            <div v-if="isActivitySignupNotification" class="notification-actions">
                <button @click="goToActivityReview" class="btn-review">前往➔</button>
            </div>
            <div v-if="isAttendeeApproveNotification" class="notification-actions">
                <button @click="goToBriefActivities(activityId)" class="btn-review">前往➔</button>
            </div>
        </div>
    </div>
  </div>
</template>
    
<script setup>
import { ref, onMounted, watchEffect, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import instance from '@/plugins/axios';
import useUserStore from "@/stores/user.js";

const route = useRoute();
const router = useRouter();
const notification = ref(null);
const userStore = useUserStore();

const fetchNotification = async () => {
  try {
    const response = await instance.get(`/notification/findNotiById/${route.params.nid}`);
    notification.value = response.data;

    //標示為已讀
    if (notification.value.notificationRead === '0') {
      await markAsRead(route.params.nid);
    }
  } catch (err) {
    console.error('獲取通知詳情時出錯:', err);
  }
};

const markAsRead = async (notificationId) => {
  try {
    await instance.put(`/secure/notification/markAsRead/${notificationId}`, {}, {
      headers: {
        'Authorization': userStore.token
      }
    });
    // 更新本地通知狀態
    if (notification.value) {
      notification.value.notificationRead = '1';
    }
  } catch (err) {
    console.error('標記通知為已讀時出錯:', err);
  }
};


const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    weekday: 'long'
  });
};

const isActivitySignupNotification = computed(() => {
  return notification.value && notification.value.notificationTitle === "~有人報名你的活動囉~";
});

const goToActivityReview = () => {
  router.push({ name: "ActivityReview-link" });
}

const isAttendeeApproveNotification = computed(() => {
  return notification.value && notification.value.notificationTitle === "~活動審核通知~";
});

const goToBriefActivities = async () => {
    if (notification.value && notification.value.notificationContent) {
        const activityTitle = extractActivityTitle(notification.value.notificationContent);
        if (activityTitle) {
            try {
                const response = await instance.get('/activities');
                const activities = response.data;
                const activity = activities.find(act => act.activitiesTitle === activityTitle);
                if (activity) {
                    router.push({ name: "briefactivities-link", params: { activitiesId: activity.activitiesId } });
                } else {
                    console.error("找不到匹配的活動");
                }
            } catch (error) {
                console.error("獲取活動列表時出錯:", error);
            }
        } else {
            console.error("無法從通知內容中提取活動標題");
        }
    }
};

const extractActivityTitle = (content) => {
    const match = content.match(/你申請報名\s*(.+?)\s*的活動/);
    return match ? match[1] : null;
};

watchEffect(() => {
  if(route.params.nid){
    // fetchNotification(route.params.nid);
    fetchNotification();
  }
});

onMounted(() => {
  if(route.params.nid){
    // fetchNotification(route.params.nid);
    fetchNotification();
  }
});

</script>
    
<style scoped>

.ts-content.is-dense{
  /* background-color: rgb(134, 184, 23); */
  background-color: rgb(208, 209, 208);
  color: black;
  font-weight: bolder;
}

.ts-icon.is-bullhorn-icon{
  color: rgb(134, 184, 23);
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-title {
  font-size: 1.2rem;
  font-weight: bold;
}

.notification-time {
  font-size: 0.9rem;
  color: #6c757d;
}

.notification-type {
  font-size: 0.9rem;
  color: #007bff;
  margin-top: 5px;
}

.notification-content-wrapper {
    display: flex;
    align-items: stretch;
}

.notification-content {
    flex-grow: 1;
    padding: 20px;
    min-height: 200px;
}

.notification-actions {
    display: flex;
    flex-direction: column;
    justify-content: end;
    padding: 0 20px;
    margin-bottom: 18px;
}

.btn-review {
    padding: 8px 20px;
    background-color: rgb(134, 184, 23);
    color: white;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: 17px;
    white-space: nowrap;
    align-items: center;
    font-weight: 700;
}

.btn-review:hover {
    background-color: rgb(160, 195, 85);
}
</style>
