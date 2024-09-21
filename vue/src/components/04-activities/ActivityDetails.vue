<template>
  <div class="activity-details">
    <div class="activity-header">
      <router-link :to="{ name: 'briefactivities-link', params: { activitiesId: activity.activityId } }" class="activity-image-link">
        <div class="activity-image">
          <img :src="getActivityImage(activity.activityId)" :alt="activity.activityName" @error="handleImageError">
        </div>
      </router-link>
      <div class="activity-info">
        <h3>{{ activity.activityName }}</h3>
        <p><i class="fas fa-calendar-alt"></i> {{ formatDateRange(activity.activitiesStartDate, activity.activitiesEndDate) }}</p>
        <p><i class="fas fa-map-marker-alt"></i> {{ activity.activitiesLocation }}</p>
      </div>
    </div>
    
    <div v-if="!isPastActivity" class="attendees-container">
      <div v-if="showApproveButton" class="attendees-section pending">
        <h4><i class="fas fa-user-clock"></i> 待審核名單</h4>
        <ul v-if="pendingAttendees.length > 0">
          <li v-for="attendee in pendingAttendees" :key="attendee.attendeeId" class="attendee-item">
            <div class="user-info">
              <router-link :to="{ name: 'userprofile-link', params: { userId: attendee.attendeeId } }" class="user-avatar-link">
                <img :src="getAttendeeAvatarUrl(attendee.userPics)" @error="handleAvatarError" :alt="attendee.attendeeName" class="user-avatar">
              </router-link>
              <div class="attendee-details">
                <span class="user-name">{{ attendee.attendeeName }}</span>
                <span class="registration-date">{{ formatDate(attendee.registrationDate) }}</span>
              </div>
            </div>
            <button @click="handleApprove(activity.activityId, attendee.attendeeId)" :disabled="approving" class="approve-btn">
              {{ approving ? '審核中...' : '審核通過' }}
            </button>
          </li>
        </ul>
        <p v-else class="no-attendees">目前沒有待審核的參加者。</p>
      </div>
      
      <div class="attendees-section approved">
        <h4><i class="fas fa-user-check"></i> 已審核名單</h4>
        <ul v-if="approvedAttendees.length > 0">
          <li v-for="attendee in approvedAttendees" :key="attendee.attendeeId" class="attendee-item">
            <div class="user-info">
              <router-link :to="{ name: 'userprofile-link', params: { userId: attendee.attendeeId } }" class="user-avatar-link">
                <img :src="getAttendeeAvatarUrl(attendee.userPics)" @error="handleAvatarError" :alt="attendee.attendeeName" class="user-avatar">
              </router-link>
              <div class="attendee-details">
                <span class="user-name">{{ attendee.attendeeName }}</span>
                <span class="registration-date">{{ formatDate(attendee.registrationDate) }}</span>
              </div>
            </div>
            <button @click="handleCancelApproval(activity.activityId, attendee.attendeeId)" class="cancel-approval-btn">
              取消審核
            </button>
          </li>
        </ul>
        <p v-else class="no-attendees">目前沒有已審核的參加者。</p>
      </div>
    </div>
    
    <div v-else class="attendees-container">
    <h4><i class="fas fa-users"></i> 參加者名單</h4>
    <ul v-if="approvedAttendees.length > 0">
      <li v-for="attendee in approvedAttendees" :key="attendee.attendeeId" class="attendee-item">
        <div class="user-info">
          <router-link :to="{ name: 'userprofile-link', params: { userId: attendee.attendeeId } }" class="user-avatar-link">
            <img :src="getAttendeeAvatarUrl(attendee.userPics)" @error="handleAvatarError" :alt="attendee.attendeeName" class="user-avatar">
          </router-link>
          <span class="user-name">{{ attendee.attendeeName }}</span>
        </div>
        <div class="button-group">
  <button 
    v-if="!attendee.isCompleted"
    @click="handleConfirmAttendance(activity.activityId, attendee.attendeeId)" 
    class="confirm-btn"
  >
    確認參加
  </button>
  <template v-else>
    <span class="confirmed-text">已確認</span>
    <button 
      @click="handleCancelConfirmation(activity.activityId, attendee.attendeeId)" 
      class="cancel-confirmation-btn"
    >
      未參加
    </button>
  </template>
</div>
      </li>
    </ul>
    <p v-else class="no-attendees">此活動沒有已審核的參加者。</p>
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import axiosapi from '@/plugins/axios.js';
import useUserStore from "@/stores/user.js";
import Swal from 'sweetalert2';

const userStore = useUserStore();

const props = defineProps({
  activity: {
    type: Object,
    required: true
  },
  isPastActivity: {
    type: Boolean,
    default: false
  },
  showApproveButton: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['attendeeApproved', 'approvalCancelled', 'confirmAttendance', 'refreshActivity']);

const approving = ref(false);
const activityImages = ref({});

const pendingAttendees = computed(() => {
  return props.activity.attendees ? props.activity.attendees.filter(a => !a.isApproved) : [];
});

const approvedAttendees = computed(() => {
  return props.activity.attendees ? props.activity.attendees.filter(a => a.isApproved).map(a => ({
    ...a,
    isCompleted: a.isCompleted || false  // 確保 isCompleted 屬性存在
  })) : [];
});

const getAttendeeAvatarUrl = (userPics) => {
  if (!userPics) return 'http://localhost:8080/meetu/images/userPics/default-pic.png';
  return `http://localhost:8080/meetu${userPics}`;
};

const handleAvatarError = (event) => {
  event.target.src = 'http://localhost:8080/meetu/images/userPics/default-pic.png';
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

const handleApprove = async (activityId, attendeeId) => {
  approving.value = true;
  try {
    const response = await axiosapi.post('/attendees/approve', {
      activityId,
      attendeeId
    }, {
      headers: {
        'Authorization': userStore.token
      }
    });
    
    console.log('審核通過並發送通知:', response.data);
    emit('attendeeApproved', activityId, attendeeId);
    emit('refreshActivity');
  } catch (error) {
    console.error('審核或發送通知時出錯:', error);
    Swal.fire({
      title: '錯誤',
      text: '審核過程中發生錯誤，請稍後再試。',
      icon: 'error',
      confirmButtonText: '確定'
    });
  } finally {
    approving.value = false;
  }
};

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

const handleCancelApproval = async (activityId, attendeeId) => {
  try {
    const response = await axiosapi.post('/attendees/cancel-approval', {
      activityId,
      attendeeId
    }, {
      headers: {
        'Authorization': userStore.token
      }
    });
    
    console.log('取消審核成功:', response.data);
    emit('approvalCancelled', activityId, attendeeId);
    emit('refreshActivity');
  } catch (error) {
    console.error('取消審核時出錯:', error);
    Swal.fire({
      title: '錯誤',
      text: '取消審核過程中發生錯誤，請稍後再試。',
      icon: 'error',
      confirmButtonText: '確定'
    });
  }
};

const handleConfirmAttendance = async (activityId, attendeeId) => {
  try {
    const response = await axiosapi.post('/attendees/confirm-attendance', {
      activityId,
      attendeeId
    }, {
      headers: {
        'Authorization': userStore.token
      }
    });

    console.log('確認參加響應:', response);
    
    if (response.status === 200) {
      const attendeeIndex = approvedAttendees.value.findIndex(a => a.attendeeId === attendeeId);
      if (attendeeIndex !== -1) {
        approvedAttendees.value[attendeeIndex].isCompleted = true;
      }
      emit('confirmAttendance', activityId, attendeeId);
      emit('refreshActivity');
      
      Swal.fire({
        title: '成功',
        text: '已確認參加',
        icon: 'success',
        confirmButtonText: '確定'
      });
    } else {
      throw new Error('確認參加失敗');
    }
  } catch (error) {
    console.error('確認參加時出錯:', error);
    Swal.fire({
      title: '錯誤',
      text: '確認參加過程中發生錯誤，請稍後再試。',
      icon: 'error',
      confirmButtonText: '確定'
    });
  }
};

const handleCancelConfirmation = async (activityId, attendeeId) => {
  try {
    const response = await axiosapi.post('/attendees/cancel-confirmation', {
      activityId,
      attendeeId
    }, {
      headers: {
        'Authorization': userStore.token
      }
    });

    console.log('取消確認響應:', response);
    
    if (response.status === 200) {
      const attendeeIndex = approvedAttendees.value.findIndex(a => a.attendeeId === attendeeId);
      if (attendeeIndex !== -1) {
        approvedAttendees.value[attendeeIndex].isCompleted = false;
      }
      emit('confirmAttendance', activityId, attendeeId);
      emit('refreshActivity');
      
      Swal.fire({
        title: '成功',
        text: '已標記未參加',
        icon: 'success',
        confirmButtonText: '確定'
      });
    } else {
      throw new Error('取消確認失敗');
    }
  } catch (error) {
    console.error('取消確認時出錯:', error);
    Swal.fire({
      title: '錯誤',
      text: '取消確認過程中發生錯誤，請稍後再試。',
      icon: 'error',
      confirmButtonText: '確定'
    });
  }
};

onMounted(() => {
  if (props.activity && props.activity.activityId) {
    fetchActivityImage(props.activity.activityId);
  }
});

watch(() => props.activity, () => {
  if (props.activity && props.activity.activityId) {
    fetchActivityImage(props.activity.activityId);
  }
}, { deep: true });
</script>

<style scoped>
.activity-details {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.activity-header {
  display: flex;
  margin-bottom: 20px;
}

.activity-image-link {
  width: 200px;
  height: 200px;
  margin-right: 20px;
  overflow: hidden;
  border-radius: 8px;
}

.activity-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.activity-info {
  flex: 1;
}

.activity-info h3 {
  margin-top: 0;
  color: #2c3e50;
  font-size: 24px;
}

.activity-info p {
  color: #34495e;
  margin: 5px 0;
}

.attendees-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.attendees-section {
  flex: 1;
  min-width: 300px;
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
}

.attendees-section.pending {
  border-left: 5px solid #ffc107;
}

.attendees-section.approved {
  border-left: 5px solid #28a745;
}

.attendees-section h4 {
  color: #2c3e50;
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 18px;
}

.attendee-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffffff;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
}

.attendee-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: bold;
  color: #2c3e50;
}

.registration-date {
  font-size: 0.8em;
  color: #6c757d;
}

.approve-btn, .cancel-approval-btn, .confirm-btn {
  padding: 5px 10px;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.approve-btn {
  background-color: #007bff;
}

.approve-btn:hover {
  background-color: #0056b3;
}

.cancel-approval-btn {
  background-color: #dc3545;
}

.cancel-approval-btn:hover {
  background-color: #c82333;
}

.button-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.confirm-btn {
  background-color: #28a745;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.confirm-btn:hover {
  background-color: #218838;
}
.cancel-confirmation-btn {
  background-color: #dc3545;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.cancel-confirmation-btn:hover {
  background-color: #c82333;
}

.confirm-btn.confirmed {
  background-color: #ffc107;
  color: #212529;
}

.confirm-btn.confirmed:hover {
  background-color: #e0a800;
}
.confirmed-text {
  color: #28a745;
  font-weight: bold;
}

.approve-btn:disabled, .confirm-btn:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.no-attendees {
  color: #6c757d;
  font-style: italic;
}

</style>