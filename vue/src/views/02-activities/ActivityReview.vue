<template>
  <div class="container-fluid">
        <div class="col-12">
            <UserSidebar />
        </div>

  <div class="hosted-activities-list">
    <div v-if="loading">加載中...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="successMessage" class="success">{{ successMessage }}</div>

    <div v-if="!loading">
      <div class="collapsible-section">
        <h2 @click="toggleActiveSection">Coming Soon <span>{{ activeSectionOpen ? '▲' : '▼' }}</span></h2>
        <div v-if="activeSectionOpen">
          <div v-if="activeActivities.length === 0">您目前沒有進行中的活動。</div>
          <ul v-else>
            <li v-for="activity in activeActivities" :key="activity.activityId" class="activity-item">
              <ActivityDetails 
                :activity="activity" 
                
                @refreshActivity="fetchHostedActivities"
                :showApproveButton="true"
                :isPastActivity="false"
              />
            </li>
          </ul>
        </div>
      </div>

      <div class="collapsible-section">
        <h2 @click="togglePastSection">已結束的活動 <span>{{ pastSectionOpen ? '▲' : '▼' }}</span></h2>
        <div v-if="pastSectionOpen">
          <div v-if="pastActivities.length === 0">您沒有已結束的活動。</div>
          <ul v-else>
            <li v-for="activity in pastActivities" :key="activity.activityId" class="activity-item">
              <ActivityDetails 
                :activity="activity" 
                :isPastActivity="true" 
                
                @refreshActivity="fetchHostedActivities"
              />
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axiosapi from '@/plugins/axios.js';
import useUserStore from "@/stores/user.js"
import ActivityDetails from '@/components/04-activities/ActivityDetails.vue';
import Swal from 'sweetalert2';
import UserSidebar from "@/components/02-user/UserSidebar.vue";

const userStore = useUserStore();

const activities = ref([]);
const loading = ref(true);
const error = ref(null);
const successMessage = ref('');

const activeSectionOpen = ref(true);
const pastSectionOpen = ref(false);

const toggleActiveSection = () => {
  activeSectionOpen.value = !activeSectionOpen.value;
};

const togglePastSection = () => {
  pastSectionOpen.value = !pastSectionOpen.value;
};

// const activeActivities = computed(() => {
//   const now = new Date();
//   return activities.value.filter(activity => new Date(activity.activitiesEndDate) >= now);
// });

// const pastActivities = computed(() => {
//   const now = new Date();
//   return activities.value.filter(activity => new Date(activity.activitiesEndDate) < now);
// });

// const fetchHostedActivities = async () => {
//   try {
//     loading.value = true;
//     error.value = null;
//     successMessage.value = '';
//     const response = await axiosapi.get('/attendees/host-activities', {
//       headers: {
//         Authorization: userStore.token
//       }
//     });
//     activities.value = response.data;
//     // console.log('xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx',activities)
//   } catch (err) {
//     console.error('獲取活動資料時出錯:', err);
//     error.value = '獲取活動資料時出錯: ' + (err.response?.data || err.message);
//   } finally {
//     loading.value = false;
//   }
// };

// onMounted(fetchHostedActivities);

const filteredActivities = computed(() => {
  return activities.value.filter(activity => 
    activity.activitiesReportStatus !== 'D' && activity.activitiesReportStatus !== 'R'
  );
});

const activeActivities = computed(() => {
  const now = new Date();
  return filteredActivities.value.filter(activity => new Date(activity.activitiesEndDate) >= now);
});

const pastActivities = computed(() => {
  const now = new Date();
  return filteredActivities.value.filter(activity => new Date(activity.activitiesEndDate) < now);
});

const fetchHostedActivities = async () => {
  try {
    loading.value = true;
    error.value = null;
    successMessage.value = '';
    const response = await axiosapi.get('/attendees/host-activities', {
      headers: {
        Authorization: userStore.token
      }
    });
    activities.value = response.data;
  } catch (err) {
    console.error('獲取活動資料時出錯:', err);
    error.value = '獲取活動資料時出錯: ' + (err.response?.data || err.message);
  } finally {
    loading.value = false;
  }
};

onMounted(fetchHostedActivities);




// const handleAttendeeApproved = async (activityId, attendeeId) => {
//   try {
//     const response = await axiosapi.post('/attendees/approve', {
//       activityId,
//       attendeeId
//     }, {
//       headers: {
//         'Authorization': userStore.token
//       }
//     });

//     // 更新本地活動數據
//     const activityIndex = activities.value.findIndex(a => a.activityId === activityId);
//     if (activityIndex !== -1) {
//       const attendeeIndex = activities.value[activityIndex].attendees.findIndex(a => a.attendeeId === attendeeId);
//       if (attendeeIndex !== -1) {
//         activities.value[activityIndex].attendees[attendeeIndex].isApproved = true;
//       }
//     }

//     await Swal.fire({
//       title: '審核成功',
//       text: '已通過審核並發送通知給申請人',
//       icon: 'success',
//       confirmButtonText: '確定'
//     });

//     await fetchHostedActivities();
//   } catch (error) {
//     console.error('處理審核通過時出錯:', error);
//     await Swal.fire({
//       title: '錯誤',
//       text: '處理審核時出現問題，請稍後再試',
//       icon: 'error',
//       confirmButtonText: '確定'
//     });
//   }
// };

// const handleConfirmAttendance = async (activityId, attendeeId) => {
//   try {
//     error.value = null;
//     successMessage.value = '';
//     await axiosapi.post('/attendees/confirm-attendance', 
//       { activityId, attendeeId },
//       {
//         headers: {
//           Authorization: userStore.token
//         }
//       }
//     );
//     successMessage.value = '確認參加成功';
//     await fetchHostedActivities();
//   } catch (err) {
//     console.error('確認參加時出錯:', err);
//     error.value = '確認參加時出錯: ' + (err.response?.data || err.message);
//   }
// };
</script>


<style scoped>
.hosted-activities-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.collapsible-section {
  margin-bottom: 20px;
}

.collapsible-section h2 {
  cursor: pointer;
  background-color: #f0f0f0;
  padding: 10px;
  border-radius: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-item {
  border: 1px solid #ddd;
  padding: 15px;
  margin-bottom: 15px;
  border-radius: 5px;
}

.activity-header {
  display: flex;
  margin-bottom: 15px;
}

.activity-image-link {
  width: 150px;
  height: 150px;
  margin-right: 15px;
  text-decoration: none;
}

.activity-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 5px;
}

.activity-info {
  flex: 1;
}

.activity-info h3 {
  margin-top: 0;
  color: #007bff;
}

.attendees-section {
  margin-bottom: 15px;
}

.attendees ul {
  list-style-type: none;
  padding: 0;
}

.attendees li {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

button {
  padding: 5px 10px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

button:hover {
  background-color: #218838;
}

button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.error {
  color: red;
  margin-bottom: 10px;
}

.success {
  color: green;
  margin-bottom: 10px;
}

h2, h4 {
  color: #333;
}

ul {
  list-style-type: none;
  padding: 0;
}

p {
  margin: 5px 0;
}

.container-fluid {
    padding-top: 20px;
}
</style>