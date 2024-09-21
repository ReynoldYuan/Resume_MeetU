<template>
  <div class="activity-page-layout">
    <div :key="route.params.activitiesId">
    <div v-if="isLoading" class="loading">Loading...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="activity-container">
      <div v-if="activity" class="activity-content">
        <div class="activity-header">
    <div 
      class="image-container" 
      :style="{ backgroundImage: `url(${getActivityImage(activity.activitiesId)})` }"
      @error="handleImageError"
    >
    </div>
  </div>
      <div class="activity-main-info">
        <div class="activity-header-vertical">
      <div class="user-info">
        <router-link :to="{ name: 'userprofile-link', params: { userId: activity.users.userId } }" class="user-avatar-link">
   <img 
    :src="`http://localhost:8080/meetu${activity.users.usersProfile.userPics}` || 'http://localhost:8080/meetu/images/userPics/default-pic.png'" 
    alt="User Photo" 
    class="user-avatar"
  > 
</router-link>

        <span class="user-name">{{ activity.users.usersProfile.userName }}</span>
      </div>
      <h1 class="activity-title">{{ activity.activitiesTitle }}
        <button class="btn btn-primary ms-5" @click="startReport" :disabled="ifReport">{{ ifReport ? '檢舉審查中' : '檢舉' }}</button></h1>
      
    </div>
      
    <div class="activity-meta">
      <span class="meta-item">
        <font-awesome-icon :icon="['fas', 'location-dot']" />
        <span>{{ activity.activitiesLocation }}</span>
      </span>
    </div>
        <div class="activity-meta">
          <span class="meta-item">
          <font-awesome-icon icon="fa-regular fa-clock" /> 
          <span class="activity-date">{{ formatDate(activity.activitiesStartDate) }}</span>
          <span class="activity-time">{{ formatTime(activity.activitiesStartDate) }}</span>
          <span>~</span>
          <span class="activity-date">{{ formatDate(activity.activitiesEndDate) }}</span>
          <span class="activity-time">{{ formatTime(activity.activitiesEndDate) }}</span>
        </span>
          
        </div>
        <div class="activity-meta">
      <span class="meta-item">
        <font-awesome-icon :icon="['fas', 'ban']" />
        <span>最後審核{{ formatDate(activity.activitiesVerifyDate) }}</span>
        <span>{{ formatTime(activity.activitiesVerifyDate) }}</span>
      </span>
    </div>
    
      </div>

      <div class="activity-details">
        <div class="activity-stats">
          <div class="stat-item">

            <font-awesome-icon :icon="['far', 'credit-card']" />
            <span>{{ activitySharingText }}</span>
          </div>
          <div class="stat-item">
 
            <font-awesome-icon :icon="['fas', 'dollar-sign']" />
            <span>{{ activity.activitiesAmt }}</span>
          </div>
          <div class="stat-item">

            <font-awesome-icon :icon="['fas', 'user-group']" />
            <span>{{ activity.activitiesMaxPeo }}人</span>
          </div>
        </div>

        <p class="activity-description">{{ activity.activitiesContent }}</p>

      <div class="highlight-stats">
    <div class="highlight-stat">
      <div class="stat-info" @click="showAttendeeDetails">
        <span class="stat-count">{{ totalAttendees }}</span>
        <span class="stat-label">人報名</span>
      </div>
    </div>
    <div class="highlight-stat">
      <div class="stat-info" @click="showCollectorDetails">
        <span class="stat-count">{{ activity?.activitiesRecommend || 0 }}</span>
        <span class="stat-label">人收藏</span>
      </div>
    </div>
  </div>
  <div class="action-buttons">
  <button 
    @click="handleSignUpAction"
    :disabled="isProcessing || (isSignedUp && isApproved)"
    class="btn-signup"
    :class="{
      'btn-not-signed': !isSignedUp,
      'btn-pending': isSignedUp && !isApproved,
      'btn-approved': isSignedUp && isApproved
    }"
  >
    {{ buttonText }}
  </button>


          <button 
      class="btn-favorite"
      :class="{ 'btn-collected': isCollected }"
      @click="toggleCollect"
    >
      {{ isCollected ? '已收藏' : '收藏' }}
    </button>
        </div>
      </div>

        <!-- 評論部分 :src="getFullImageUrl(comment.userPics)"-->
        <div class="comments-section">
    <h3>留言</h3>
    <div v-if="comments.length > 0" class="comment-list">
      <div v-for="comment in comments" :key="comment.activitiesCommentId" class="comment-item">

        <router-link :to="{ name: 'userprofile-link', params: { userId: comment.userId } }" class="user-avatar-link">
          <img 
        
     
    :src="`http://localhost:8080/meetu${comment.userPics}` || 'http://localhost:8080/meetu/images/userPics/default-pic.png'"
    :alt="comment.userName"
    @error="handleAvatarError"
    class="avatar-image rounded-circle"
  >
      </router-link>

        <div class="comment-content">
          <div class="comment-header">
            <p class="comment-user">{{ comment.userName }}</p>

            <div v-if="!isCurrentUserComment(comment)">
              <button 
                @click="startReportComment(comment.activitiesCommentId)" 
                class="btn-report"
                :disabled="comment.isReported" >
                {{ comment.isReported ? '檢舉審查中' : '檢舉' }}
              </button>
            </div>
            

            <font-awesome-icon 
              v-if="isCurrentUserComment(comment)"
              icon="trash" 
              class="delete-icon" 
              @click="deleteComment(comment.activitiesCommentId)"
            />
            
          </div>
          <p class="comment-text">{{ comment.messageContent }}</p>
          <p class="comment-time">{{ formatCommentTime(comment.messageTime) }}</p>
        </div>
      </div>
    </div>
    <div v-else class="no-comments">
      <p>目前沒有留言。</p>
    </div>

    <!-- 添加評論表單 -->
    <form @submit.prevent="addComment" class="add-comment-form">
      <textarea 
        v-model="newComment.messageContent" 
        placeholder="在想什麼.." 
        required 
        class="comment-textarea"
      ></textarea>
      <button type="submit" class="btn-comment">留言</button>
    </form>
    <button @click="doInput">一鍵輸入</button>
  </div>
    </div>
    <div v-else class="loading">
      <p>活動搜尋中...</p>
    </div>
  </div>

 
</div>
  <div class="activity-sidebar">
    <div class="sidebar-section my-signups">
      <h3 class="section-title">我的報名</h3>
      <div v-if="mySignups.length" class="activity-list">
        <div 
          v-for="signup in filteredSignups" 
          :key="signup.entryId" 
          class="activity-item"
          @click="navigateToActivity(signup.activityId)"
        >
          <img 
            :src="getActivityImage(signup.activityId)" 
            @error="handleImageError"
            alt="活動圖片" 
            class="activity-thumb"
          >
          <div class="activity-details">
            <h4>{{ signup.activityName }}</h4>
            <p class="activity-date">活動時間:{{ formatDate(signup.activitiesStartDate) }}</p>
            <span class="status" :class="{ 'approved': signup.isApproved, 'pending': !signup.isApproved }">
              {{ signup.isApproved ? '已審核' : '待審核' }}
            </span>
          </div>
        </div>
      </div>
      <p v-else>暫無報名活動</p>
    </div>
    
    <div class="sidebar-section my-favorites">
      <h3 class="section-title">我的收藏</h3>
      <div v-if="myFavorites.length" class="activity-list">
        <div 
          v-for="favorite in myFavorites" 
          :key="favorite.actCollectId" 
          class="activity-item"
          @click="navigateToActivity(favorite.activitiesId)"
        >
          <img 
            :src="getActivityImage(favorite.activitiesId)" 
            @error="handleImageError"
            alt="活動圖片" 
            class="activity-thumb"
          >
          <div class="activity-details">
            <h4>{{ favorite.activitiesTitle }}</h4>
            <p class="activity-date">活動時間:{{ formatDate(favorite.activitiesStartDate) }}</p>
          </div>
        </div>
      </div>
      <p v-else>暫無收藏活動</p>
    </div>
  </div>
  </div>
  <!-- 報名者彈出窗口 "getFullImageUrl(attendee.userPics)"-->
  <div v-if="showPopup" class="popup-overlay" @click="closePopup">
      <div class="attendee-popup" @click.stop>
        <h3>報名者詳情</h3>
        <ul class="attendee-list">
    <li v-for="attendee in attendeeDetails" :key="attendee.userId" class="attendee-item">
      <div class="user-info">
        <router-link :to="{ name: 'userprofile-link', params: { userId: attendee.userId } }" class="user-avatar-link">
          <img 
          
    :src="`http://localhost:8080/meetu${attendee.userPics}` || 'http://localhost:8080/meetu/images/userPics/default-pic.png'"
    @error="handleAvatarError"
    :alt="attendee.userName" 
    class="user-avatar"
  >
        </router-link>
        <span class="user-name">{{ attendee.userName }}</span>
      </div>
      <span class="approval-status" :class="{ 'approved': attendee.isApproved }">
        {{ attendee.isApproved ? '已審核' : '待審核' }}
      </span>
    </li>
  </ul>
      </div>
    </div>

     <!-- 收藏者彈出窗口 -->
  <div v-if="showCollectorPopup" class="popup-overlay" @click="closeCollectorPopup">
    <div class="attendee-popup" @click.stop>
      <h3>收藏者詳情</h3>
      <ul class="attendee-list">
        <li v-for="collector in collectorDetails" :key="collector.userId" class="attendee-item">
          <div class="user-info">
            <router-link :to="{ name: 'userprofile-link', params: { userId: collector.userId } }" class="user-avatar-link">
              <img 
                :src="getAttendeeAvatarUrl(collector.userPics)"
                @error="handleAvatarError"
                :alt="collector.userName" 
                class="user-avatar"
              >
            </router-link>
            <span class="user-name">{{ collector.userName }}</span>
          </div>
        </li>
      </ul>
    </div>
  </div>


  


  <!-- 活動檢舉彈出視窗 -->
  <div v-if="showReportModal" class="report-modal">
    <div v-if="reportStep === 1" class="modal-content">
      <h3>請選擇檢舉類型</h3>
      <ul>
        <li v-for="(type, index) in reportTypes" :key="index">
          <button @click="selectReportType(type)">{{ type }}</button>
        </li>
      </ul>
      <button @click="cancelReport" class="cancel-button">取消</button>
    </div>
    <div v-else-if="reportStep === 2" class="modal-content">
      <h3>請選擇檢舉原因</h3>
      <ul>
        <li v-for="(reason, index) in reportReasons" :key="index">
          <button @click="selectReportReason(reason)">{{ reason }}</button>
        </li>
      </ul>
      <button @click="cancelReport" class="cancel-button">取消</button>
    </div>
    <!-- <div v-else-if="reportStep === 3" class="modal-content">
      <h3>檢舉成功</h3>
      <h5>我們會盡快審核您的檢舉</h5>
      <button @click="finishReport">完成</button>
    </div> -->
  </div>

  <!-- 留言檢舉彈出視窗 -->
  <div v-if="showCommentReportModal" class="report-modal">
    <div v-if="commentReportStep === 1" class="modal-content">
      <h3>請選擇檢舉類型</h3>
      <ul>
        <li v-for="(type, index) in commentReportTypes" :key="index">
          <button @click="selectCommentReportType(type)">{{ type }}</button>
        </li>
      </ul>
      <button @click="cancelCommentReport" class="cancel-button">取消</button>
    </div>
    <div v-else-if="commentReportStep === 2" class="modal-content">
      <h3>請選擇檢舉原因</h3>
      <ul>
        <li v-for="(reason, index) in commentReportReasons" :key="index">
          <button @click="selectCommentReportReason(reason)">{{ reason }}</button>
        </li>
      </ul>
      <button @click="cancelCommentReport" class="cancel-button">取消</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axiosapi from '@/plugins/axios.js';
import useUserStore from "@/stores/user.js";
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const activity = ref(null);
const comments = ref([]);
const newComment = ref({
  messageContent: '',
});

const isLoading = ref(true);
const error = ref(null);

const totalAttendees = ref(0);
const attendeeDetails = ref([]);
const showPopup = ref(false);

const mySignups = ref([]);
const myFavorites = ref([]);
const activityImages = ref({});

const isSignedUp = ref(false);
const isApproved = ref(false);
const isProcessing = ref(false);
const entryId = ref(null);

const isCollected = ref(false);

// 新增這兩行
const showCollectorPopup = ref(false);
const collectorDetails = ref([]);

const baseUrl = 'http://localhost:8080/meetu';

const defaultAvatarUrl = `${baseUrl}/images/userPics/default-pic.png`;


// 活動檢舉變數
const showReportModal = ref(false);
const reportStep = ref(1);
const selectedReportType = ref('');
const selectedReportReason = ref('');
const ifReport = ref(false);

const reportTypes = [
  '詐騙活動', '違法活動', '不當內容',
  '虛假資訊', '侵犯隱私', '其他問題'
];

const reportReasons = [
  '活動內容涉及詐騙', '活動違反法律法規',
  '活動包含不當或冒犯性內容', '活動信息虛假或誤導',
  '活動侵犯個人隱私', '其他問題'
];


// 留言檢舉變數
const showCommentReportModal = ref(false);
const commentReportStep = ref(1);
const selectedCommentReportType = ref('');
const selectedCommentReportReason = ref('');
const reportingCommentId = ref(null);
// const ifCommentReport = ref(false);

const commentReportTypes = [
  '垃圾訊息', '騷擾或霸凌',
  '仇恨言論', '不當內容',
  '個人資料外洩', '其他問題'
];

const commentReportReasons = [
  '留言包含垃圾訊息或廣告',
  '留言涉及騷擾、威脅或霸凌行為',
  '留言包含仇恨、歧視或極端言論',
  '留言包含色情、暴力或其他不當內容',
  '留言洩露他人個人資料',
  '其他違反社群準則的問題'
];

// 新增一個函數來重新獲取所有需要更新的數據
async function refreshActivityData() {
  await fetchActivityDetails(route.params.activitiesId);
  await fetchAttendeeInfo(route.params.activitiesId);
  await fetchMySignups();
  await fetchMyFavorites();
}

// 監聽路由參數變化
watch(() => route.params.activitiesId, (newId, oldId) => {
  if (newId !== oldId) {
    fetchActivityDetails(newId);
    if (userStore.isLogedin) {
      checkIfCollected(newId);
    }
    fetchAttendeeInfo(newId);
  }
  });

onMounted(async () => {
  const aid = route.params.activitiesId;
  if (aid) {
    await fetchActivityDetails(aid);
    await fetchActivityImage(aid);
    if (userStore.isLogedin) {
      // checkIfSignedUp(aid);
      checkIfCollected(aid);
      await fetchMySignups();
      await fetchMyFavorites();
      await checkReportStatus();
      // await checkCommentReportStatus();
      
    }
    await fetchAttendeeInfo(aid);
  } else {
    console.error('Activity ID is missing.');
    error.value = 'Activity ID is missing.';
    isLoading.value = false;
  }
});

function getFullImageUrl(path) {
  if (!path) return defaultAvatarUrl;
  if (path.startsWith('http')) {
    // 如果已經是完整 URL，則從中提取相對路徑
    const urlObject = new URL(path);
    return `${baseUrl}${urlObject.pathname}`;
  }
  return `${baseUrl}${path}`;
}

const getAttendeeAvatarUrl = (userPics) => {
  return getFullImageUrl(userPics);
};

const handleAvatarError = (event) => {
  event.target.src = defaultAvatarUrl;
};

async function fetchMySignups() {
  try {
    const response = await axiosapi.get('/attendees/registrations', {
      headers: { 'Authorization': userStore.token }
    });
    mySignups.value = response.data;
    mySignups.value.forEach(signup => {
      fetchActivityImage(signup.activityId);
    });
  } catch (error) {
    console.error('Error fetching user signups:', error);
  }
}

const filteredSignups = computed(() => {
  const now = new Date();
  return mySignups.value.filter(signup => {
    const endDate = new Date(signup.activitiesEndDate);
    return endDate > now && signup.activitiesReportStatus === "N";
  });
});

async function fetchMyFavorites() {
  try {
    const response = await axiosapi.get('/collect', {
      headers: { 'Authorization': userStore.token }
    });
    myFavorites.value = response.data;
    myFavorites.value.forEach(favorite => {
      fetchActivityImage(favorite.activitiesId);
    });
  } catch (error) {
    console.error('Error fetching user favorites:', error);
  }
}

async function fetchActivityDetails(aid) {
  try {
    isLoading.value = true;
    error.value = null;
    const [activityResponse, collectorsResponse] = await Promise.all([
      axiosapi.get(`/activities/${aid}`),
      axiosapi.get(`/collect/activity/${aid}`)
    ]);
    
    activity.value = activityResponse.data.activity || null;
    comments.value = activityResponse.data.comments || [];

    // console.log('Activity data:', activity.value);
    // console.log('User profile:', activity.value?.users?.usersProfile);
    // console.log('~~~~~~~~~~~~~',comments)
   
    // 更新收藏數量和收藏者詳情
    if (activity.value) {
      activity.value.activitiesRecommend = collectorsResponse.data.totalCollectors || 0;
      collectorDetails.value = collectorsResponse.data.collectors || [];
    }

    if (userStore.isLogedin) {
      for (let comment of comments.value) {
        comment.isReported = await checkCommentReportStatus(comment.activitiesCommentId);
      }
      await checkSignUpStatus(aid);
      await checkIfCollected(aid);
    }
  } catch (err) {
    console.error(`Error fetching details for activity ${aid}:`, err);
    error.value = '獲取活動詳情時發生錯誤';
  } finally {
    isLoading.value = false;
  }
}

function showCollectorDetails() {
  showCollectorPopup.value = true;
}

function closeCollectorPopup() {
  showCollectorPopup.value = false;
}

async function fetchActivityImage(activityId) {
  try {
    const response = await axiosapi.get(`/photos/download/${activityId}`, { responseType: 'blob' });
    const imageUrl = URL.createObjectURL(response.data);
    activityImages.value[activityId] = imageUrl;
  } catch (error) {
    console.error(`Error fetching image for activity ${activityId}:`, error);
  }
}

function getActivityImage(activityId) {
  return activityImages.value[activityId] || '/default-activity-image.png';
}

function handleImageError(event) {
  event.target.src = '/default-activity-image.png';
  event.target.style.backgroundImage = 'url(/default-activity-image.png)';
}


function addComment() {
  // console.log(activity.value.activitiesId)
  if (!activity.value || !activity.value.activitiesId) {
    console.error('Activity or Activity ID is missing');
    Swal.fire({
            title: '無法添加評論：活動信息不完整',
            text: '請稍後再試',
            icon: 'error'
          });

    return;
  }

  if (!userStore.isLogedin) {
    Swal.fire({
      title: '請先登入',
      text: '請先登入後再進行收藏',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '前往登入',
      cancelButtonText: '取消'
    })
    router.push({ name: 'login' });
    return;
  }

  const commentData = {
    activitiesReportStatus: 'N',
    messageContent: newComment.value.messageContent,
  };

  axiosapi.post(`/aComment?activitiesId=${activity.value.activitiesId}`, commentData, {
    headers: {
      'Authorization': userStore.token
    }
  })
  .then(response => {
    newComment.value.messageContent = '';
    fetchActivityDetails(activity.value.activitiesId);
  })
  .catch(error => {
    console.error('Error adding comment:', error);
    if (error.response) {
      alert(`添加評論失敗: ${error.response.data}`);
    } else if (error.request) {
      alert('無法連接到服務器，請稍後再試');
    } else {
      alert('發生錯誤，請稍後再試');
    }
  });
}

const buttonText = computed(() => {
  if (isProcessing.value) return '處理中...';
  if (!isSignedUp.value) return '報名';
  if (isSignedUp.value && !isApproved.value) return '審核中';
  return '已審核';
});

const handleSignUpAction = async () => {
  if (!userStore.isLogedin) {
    redirectToLogin();
    return;
  }

  if (activity.value.users.userId === parseInt(userStore.id)) {
    Swal.fire('無法報名', '您不能報名自己舉辦的活動', 'info');
    return;
  }

  isProcessing.value = true;

  try {
    if (!isSignedUp.value) {
      await signUpForActivity();
    } else if (!isApproved.value) {
      await cancelSignUp();
    }
  } catch (error) {
    console.error('操作失敗:', error);
    Swal.fire('操作失敗', '請稍後再試', 'error');
  } finally {
    isProcessing.value = false;
    await checkSignUpStatus(activity.value.activitiesId);
  }
};

const signUpForActivity = async () => {
  if (!activity.value) return;
  
  const requestData = {
    activitiesId: activity.value.activitiesId 
  };

  const requestConfig = {
    headers: {
      'Authorization': userStore.token,
      'Content-Type': 'application/json'
    }
  };

  try {
    const response = await axiosapi.post('/attendees', requestData, requestConfig);
    // console.log('66666666666666666666666666666報名響應:', response.data);
    Swal.fire('報名成功', '等待審核中', 'success');
    sendNotificationToHost(activity.value.activitiesId, userStore.id);
    await refreshActivityData();
    return response;
  } catch (error) {
    handleSignUpError(error);
  }
};


const cancelSignUp = async () => {
  if (!entryId.value) {
    console.error('No entryId available for cancellation');
    return;
  }

  try {
    await axiosapi.delete(`/attendees/cancel/${entryId.value}`, {
      headers: {
        'Authorization': userStore.token
      }
    });
    Swal.fire('取消報名成功', '您已成功取消報名', 'success');
    await refreshActivityData();
  } catch (error) {
    console.error('取消報名失敗:', error);
    Swal.fire('取消報名失敗', '請稍後再試', 'error');
  }
};

const checkSignUpStatus = async (aid) => {
  if (userStore.isLogedin) {
    try {
      const response = await axiosapi.get(`/attendees/check/${aid}`, {
        headers: {
          'Authorization': userStore.token,
        }
      });
      /*console.log("Response from /attendees/check/:", response.data);*/

      isSignedUp.value = response.data.isSignedUp;
      isApproved.value = response.data.isApproved;
      entryId.value = response.data.entryId;

    } catch (error) {
      console.error(`Error checking signup status for activity ${aid}:`, error);
      isSignedUp.value = false;
      isApproved.value = false;
      entryId.value = null;
    }
  } else {
    isSignedUp.value = false;
    isApproved.value = false;
    entryId.value = null;
  }
};

onMounted(async () => {
  const aid = route.params.activitiesId;
  if (aid) {
    await checkSignUpStatus(aid);
  }
});

watch(() => route.params.activitiesId, async (newId) => {
  if (newId) {
    await checkSignUpStatus(newId);
  }
});

const handleSignUpError = (error) => {
  if (error.response) {
    if (error.response.status === 409) {
      Swal.fire('報名失敗', '你已經報名該活動！', 'warning');
    } else if (error.response.status === 403) {
      Swal.fire('報名失敗', '你不能報名自己舉辦的活動！', 'error');
    } else {
      Swal.fire('報名失敗', '請稍後再試！', 'error');
    }
  } else {
    Swal.fire('報名失敗', '請稍後再試！', 'error');
  }
};

const redirectToLogin = () => {
  router.push({ name: 'login' });
};

function formatDate(dateString) {
  const date = new Date(dateString);
  return `${date.getMonth() + 1}/${date.getDate()} (${['日', '一', '二', '三', '四', '五', '六'][date.getDay()]})`;
}

function formatTime(dateString) {
  const date = new Date(dateString);
  return date.toLocaleTimeString('zh-TW', { hour: '2-digit', minute: '2-digit' });
}

function formatCommentTime(dateString) {
  const date = new Date(dateString);
  return date.toLocaleString('zh-TW', { month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' });
}

async function fetchAttendeeInfo(aid) {
  try {
    const response = await axiosapi.get(`/attendees/activity/${aid}`);
    totalAttendees.value = response.data.totalAttendees;
    attendeeDetails.value = response.data.attendees;
    // console.log('xxxxxxxxxxxxxxx',attendeeDetails)
  } catch (error) {
    console.error('Error fetching attendee info:', error);
  }
}

function showAttendeeDetails() {
  showPopup.value = true;
}

function closePopup() {
  showPopup.value = false;
}
const activitySharingText = computed(() => {
  switch(activity.value?.activitiesSharing) {
    case 'I':
      return '我請客';
    case 'Y':
      return '你付錢';
    case 'S':
      return '各付各';
    default:
      return activity.value?.activitiesSharing || '未指定';
  }
});

function toggleCollect() {
  if (userStore.isLogedin) {
    const requestData = {
      activitiesId: activity.value.activitiesId 
    };

    const requestConfig = {
      headers: {
        'Authorization': userStore.token,
        'Content-Type': 'application/json'
      }
    };

    if (isCollected.value) {
      axiosapi.delete(`/collect/${activity.value.activitiesId}`, requestConfig)
        .then(async () => {
          isCollected.value = false;
          Swal.fire({
            title: '已移除收藏',
            text: '活動已從收藏中移除',
            icon: 'success'
          });
          await refreshActivityData();
        })
        .catch(error => {
          console.error('Error removing from collection:', error);
          Swal.fire({
            title: '移除收藏失敗',
            text: '請稍後再試',
            icon: 'error'
          });
        });
    } else {
      axiosapi.post('/collect', requestData, requestConfig)
        .then(async () => {
          isCollected.value = true;
          Swal.fire({
            title: '收藏成功',
            text: '活動已成功加入收藏',
            icon: 'success'
          });
          await refreshActivityData();
        })
        .catch(error => {
          console.error('Error adding to collection:', error);
          Swal.fire({
            title: '加入收藏失敗',
            text: '請稍後再試',
            icon: 'error'
          });
        });
    }
  } else {
    Swal.fire({
      title: '請先登入',
      text: '請先登入後再進行收藏',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '前往登入',
      cancelButtonText: '取消'
    }).then((result) => {
      if (result.isConfirmed) {
        redirectToLogin();
      }
    });
  }
}


function checkIfCollected(aid) {
  if (userStore.isLogedin) {
    const requestConfig = {
      headers: {
        'Authorization': userStore.token,
      }
    };

    axiosapi.get(`/collect/check/${aid}`, requestConfig)
      .then(response => {
        isCollected.value = response.data;
      })
      .catch(error => {
        console.error(`Error checking collection status for activity ${aid}:`, error);
      });
  }
}
function isCurrentUserComment(comment) {
  const currentUserId = parseInt(userStore.id);
  const commentUserId = parseInt(comment.userId);  // 使用新的 userId 屬性
  
  // console.log('Current user ID:', currentUserId);
  // console.log('Comment user ID:', commentUserId);

  return currentUserId === commentUserId;
}

function deleteComment(commentId) {
  Swal.fire({
    title: '確定要刪除這條留言嗎？',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '是的',
    cancelButtonText: '取消'
  }).then((result) => {
    if (result.isConfirmed) {
      axiosapi.delete(`/aComment/${commentId}`, {
        headers: {
          'Authorization': userStore.token
        }
      })
      .then(() => {
        fetchActivityDetails(activity.value.activitiesId);
        Swal.fire(
          '已刪除!',
          '您的留言已被刪除。',
          'success'
        );
      })
      .catch(error => {
        if (error.response && error.response.status === 403) {
          console.error('Token expired or invalid. Redirecting to login.');
          userStore.logout();
          router.push('/login');
        } else {
          console.error('Error deleting comment:', error);
          Swal.fire(
            '刪除失敗',
            '刪除留言失敗，請稍後再試',
            'error'
          );
        }
      });
    }
  });
}


function navigateToActivity(activitiesId) {
  if (route.params.activitiesId === activitiesId.toString()) {
    // 如果點擊的是當前活動，直接重新加載數據
    fetchActivityDetails(activitiesId);
    if (userStore.isLogedin) {
      checkIfSignedUp(activitiesId);
      checkIfCollected(activitiesId);
    }
    fetchAttendeeInfo(activitiesId);
  } else {
    // 否則，導航到新的活動頁面
    router.push({
      name: 'briefactivities-link',
      params: { activitiesId: activitiesId }
    });
  }
}

//// 活動檢舉功能
const checkReportStatus = async () => {
  try {
    const response = await axiosapi.get(`/report/status/A/${route.params.activitiesId}`, {
      headers: { 'Authorization': userStore.token }
    });
    ifReport.value = response.data.ifReport;
  } catch (error) {
    console.error("Error checking report status:", error);
  }
};

const startReport = () => {
  if (!userStore.isLogedin) {
    Swal.fire({
      title: '請先登入',
      text: '請先登入後再進行檢舉',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '前往登入',
      cancelButtonText: '取消'
    }).then((result) => {
      if (result.isConfirmed) {
        redirectToLogin();
      }
    });
    return;
  }

  if (activity.value.users.userId === parseInt(userStore.id)) {
    Swal.fire('無法檢舉', '您不能檢舉自己舉辦的活動', 'info');
    return;
  }

  if (!ifReport.value) {
    showReportModal.value = true;
    reportStep.value = 1;
  }
};

const selectReportType = (type) => {
  selectedReportType.value = type;
  reportStep.value = 2;
};

const selectReportReason = (reason) => {
  selectedReportReason.value = reason;
  // reportStep.value = 3;
  finishReport();
};

const finishReport = async () => {
  showReportModal.value = false;
  const reportData = {
    reportItem: 'A',
    reportItemId: route.params.activitiesId,
    reportType: selectedReportType.value,
    reportReason: selectedReportReason.value
  };

  try {
    const response = await axiosapi.post('/report/add', reportData, {
      headers: { 
        'Authorization': userStore.token,
        'Content-Type': 'application/json'
      }
    });
    // console.log("Report response:", response.data);
    ifReport.value = true;
    Swal.fire("檢舉已送出", "我們會盡快審核您的檢舉", "success");
  } catch (error) {
    Swal.fire("檢舉失敗", `請稍後再試。錯誤: ${error.message}`, "error");
  }
};

const cancelReport = () => {
    showReportModal.value = false;
    reportStep.value = 1;
};


//// 留言檢舉功能

const checkCommentReportStatus = async (commentId) => {
  if (!userStore.isLogedin || !commentId) return false;
  try {
    const response = await axiosapi.get(`/report/comment/status/AC/${commentId}`, {
      headers: { 
        'Authorization': userStore.token
      }
    });
    return response.data.ifCommentReport;
  } catch (error) {
    console.error("Error checking comment report status:", error);
    return false;
  }
};

const startReportComment = async (commentId) => {
  const isReported = await checkCommentReportStatus(commentId);
  if(!isReported){
  reportingCommentId.value = commentId;
  showCommentReportModal.value = true;
  commentReportStep.value = 1;
  }
};

const selectCommentReportType = (type) => {
  selectedCommentReportType.value = type;
  commentReportStep.value = 2;
};

const selectCommentReportReason = async (reason) => {
  selectedCommentReportReason.value = reason;
  finishCommentReport();
};

const finishCommentReport = async () => {
  showCommentReportModal.value = false;
  const reportData = {
    reportItem: 'AC',
    reportItemId: reportingCommentId.value,
    reportType: selectedCommentReportType.value,
    reportReason: selectedCommentReportReason.value
  };

  try {
    const response = await axiosapi.post('/report/add', reportData, {
      headers: { 
        'Authorization': userStore.token,
      }
    });
    // console.log("Comment report response:", response.data);
    // ifCommentReport.value = true;
    const index = comments.value.findIndex(c => c.activitiesCommentId === reportingCommentId.value);
    if (index !== -1) {
      comments.value[index].isReported = true;
    }
    Swal.fire("檢舉成功", "我們會盡快審核您的檢舉", "success");
  } catch (error) {
    console.error("Error reporting comment:", error);
    Swal.fire("檢舉失敗", `請稍後再試。錯誤: ${error.message}`, "error");
  }
};

const cancelCommentReport = () => {
  showCommentReportModal.value = false;
  commentReportStep.value = 1;
  reportingCommentId.value = null;
};

// 發送通知功能
async function sendNotificationToHost(activitiesId, userId) {
  try {
    await axiosapi.post('/notifications/signup', {
      activitiesId: activitiesId,
      userId: userId
    }, {
      headers: {
        'Authorization': userStore.token
      }
    });
    console.log('通知已發送給活動舉辦人');
  } catch (error) {
    console.error('發送通知失敗:', error);
  }
}

function doInput() {
  newComment.value.messageContent = "活動看起來很有趣，希望能參加！";
}

</script>

<style scoped>
.activity-page-layout {
  display: flex;
  max-width: 1300px;
  margin: 0 auto;
  gap: 30px;
}
.activity-sidebar {
  width: 350px;
  padding: 20px;
  background-color: #f8f8f8;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.section-title {
  font-size: 1.4em;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #3498db;
  text-align: center;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.sidebar-section {
  margin-bottom: 30px;
}
.sidebar-section h3 {
  font-size: 1.2em;
  margin-bottom: 15px;
  color: #333;
}

.sidebar-section ul {
  list-style-type: none;
  padding: 0;
}

.sidebar-section li {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  font-size: 0.9em;
  color: #666;
}

.browse-history {
  max-height: 300px;
  overflow-y: auto;
}


.activity-container {
  flex: 1;
  max-width: 900px;
}

.activity-header {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
  background-color: #f0f0f0; 
}

.activity-img {
  width: 100%;
  height: 100%;
  object-fit: cover; 
  object-position: center;
}

.activity-main-info {
  padding: 15px;
  background-color: #f8f8f8;
}
.activity-header-vertical {
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}
.activity-header-compact {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
}

.user-name {
  font-weight: bold;
}

.activity-title {
  font-size: 1.4em;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.activity-meta {
  font-size: 0.9em;
  color: #666;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.activity-details {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background-color: #f8f8f8;
}
.activity-details h4 {
  font-size: 1.1em; 
  margin-bottom: 5px;
}


.activity-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  font-size: 1em;  
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.activity-item {
  cursor: pointer;
  display: flex;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  text-decoration: none; 
  color: inherit;
}
.activity-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.activity-thumb {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.activity-date {
  font-size: 0.9em;
}

.meta-item {
  display: flex;
  align-items: center;
}
.meta-item svg {
  margin-right: 5px;  
}
.stat-item {
  display: flex;
  align-items: center;
}

.stat-item svg {
  margin-right: 5px;
}

.activity-description {
  font-size: 1.1em;
  line-height: 1.6;
  margin-bottom: 20px;
}
.highlight-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 15px;
  padding: 10px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.highlight-stat {
  display: flex;
  align-items: center;
  font-size: 1em;
}

.highlight-stat svg {
  font-size: 1.3em;
  margin: 0 8px;
}

.stat-count {
  font-weight: bold;
  font-size: 1.1em;
}
.stat-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: 0.75em;
  color: #666;
}
.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.btn-signup, .btn-favorite {
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  font-size: 1.1em;
  font-weight: bold;
  cursor: pointer;
}

.btn-signup {
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  font-size: 1.1em;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-not-signed {
  background-color: #4CAF50;
  color: white;
}

.btn-signed {
  background-color: #4CAF50;
  color: white;
}

.btn-favorite {
  background-color: pink;
  color: #333;
}

.btn-collected {
  background-color: #FFD700;
  color: #333;
}
.btn-pending {
  background-color: #FFA500;
  color: white;
}
.btn-approved {
  background-color: #808080; 
  color: white;
}
.btn-cancel {
  background-color: #FF4136;
  color: white;
}
.btn-signup:hover:not(:disabled) {
  opacity: 0.8;
}
.btn-signup:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.comments-section {
  padding: 20px;
  background-color: #f8f8f8;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  margin-bottom: 20px;
  background-color: #ffffff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.comment-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 15px;
}

.comment-content {
  flex: 1;
}

.comment-user {
  font-weight: bold;
  margin: 0;
  font-size: 1.1em;
}

.comment-text {
  margin: 8px 0;
  font-size: 1em;
}

.comment-time {
  font-size: 0.9em;
  color: #777;
  margin: 0;
}

.add-comment-form {
  margin-top: 20px;
}

.comment-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: vertical;
  font-size: 1em;
}

.btn-comment {
  margin-top: 10px;
  padding: 10px 20px;
  background-color: #1DA1F2;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1em;
}

.loading {
  text-align: center;
  padding: 40px;
  font-size: 1.2em;
}

.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.attendee-popup {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 80%;
  max-height: 80%;
  overflow-y: auto;
}
.attendee-popup h3 {
  margin-top: 0;
  margin-bottom: 15px;
}

.attendee-popup ul {
  list-style-type: none;
  padding: 0;
}

.attendee-popup li {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.delete-icon {
  cursor: pointer;
  color: #888;
  transition: color 0.3s ease;
}

.delete-icon:hover {
  color: #ff4444;
}
.status {
  font-size: 0.8em;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: bold;
  align-self: flex-start;
}

.status.approved {
  background-color: #e6f7e6;
  color: #2e7d32;
}

.status.pending {
  background-color: #fff3e0;
  color: #e65100;
}
.approval-status {
  font-size: 0.9em;
  padding: 2px 6px;
  border-radius: 4px;
  background-color: #f0f0f0;
}

.approval-status.approved {
  background-color: #d4edda;
  color: #155724;
}
.image-container {
  width: 800px;
  height: 600px;
  max-width: 100%;
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #ffffff;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  border-radius: 8px;
}

.attendee-list {
  list-style-type: none;
  padding: 0;
}
.attendee-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 5px;
  border-bottom: 1px solid #eee;
}

.avatar-image {
  width: 40px;
  height: 40px;
  object-fit: cover;
  object-position: center;
}

/* 響應式設計 */
@media (max-width: 900px) {
  .activity-page-layout {
    flex-direction: column;
  }
  
  .activity-sidebar {
    width: 100%;
    max-width: 900px;
    margin: 20px auto 0;
  }

  .activity-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }
  .image-container {
     width: 90%; 
    height: 450px;
  }
}


/* 檢舉視窗 */

.report-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 5px;
    max-width: 500px;
    width: 100%;
}

.modal-content ul {
    list-style-type: none;
    padding: 0;
}

.modal-content li {
    margin-bottom: 10px;
}

.modal-content button {
    width: 100%;
    padding: 10px;
    background-color: #f0f0f0;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.modal-content button:hover {
    background-color: #e0e0e0;
}

.report-icon {
  cursor: pointer;
  color: #888;
  transition: color 0.3s ease;
}

.report-icon:hover {
  color: #ff4444;
}

.btn-report {
  padding: 5px 10px;
  background-color: #888;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.3s;
}

.btn-report:hover {
  background-color: #d32f2f;
}

.btn-report:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

</style>