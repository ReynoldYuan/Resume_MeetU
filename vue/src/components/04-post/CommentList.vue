<template>
  <div class="comment-list">
    <div v-if="comments.length === 0 && !isLoading">尚無留言</div>

    <!-- 顯示現有的留言 -->
    <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
      <div class="comment-header">
        <img 
          :src="comment.userPics ? `http://localhost:8080/meetu${comment.userPics}` : 'http://localhost:8080/meetu/images/default-profile.png'" 
          alt="User Avatar" 
          class="user-avatar" 
          @error="handleImageError"
        />
        <span class="user-name">{{ comment.userName }}</span>
      </div>
      
      <!-- 留言內容或編輯框 -->
      <p v-if="!isEditingComment(comment.commentId)">{{ comment.commentContent }} <span v-if="isCommentEdited(comment)" class="edited-label">(已編輯)</span></p>
      
      <!-- 使用 CommentForm 來進行編輯 -->
      <CommentForm
        v-if="isEditingComment(comment.commentId)"
        :postId="props.postId"
        :comment="comment"
        :isEditMode="true"
        @commentsUpdated="loadComments"
        @editCancelled="resetEdit"
      />

      <!-- 留言操作區域 -->
      <div class="comment-actions">
        <span class="comment-time">{{ formatTime(comment.commentCreatedAt) }}</span>
        <LikeButton 
          :type="'comment'" 
          :id="comment.commentId" 
          :initialLiked="comment.isLiked" 
          :initialLikeCount="comment.likeCount"
          @likeToggled="handleLikeToggled"
        />
        <div class="action-buttons">
          <!-- 編輯按鈕：僅當前用戶可見 -->
          <button v-if="isCurrentUser(comment.userId) && !isEditingComment(comment.commentId)" @click="startEdit(comment.commentId)" class="edit-button svg-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" class="bi bi-pencil-square" viewBox="0 0 16 16">
              <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
              <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
            </svg>
          </button>

          <!-- 刪除按鈕：僅當前用戶可見 -->
          <button v-if="isCurrentUser(comment.userId) && !isEditingComment(comment.commentId)" @click="confirmDelete(comment.commentId)" class="delete-button svg-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" class="bi bi-trash" viewBox="0 0 16 16">
              <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5m3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0z"/>
              <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4zM2.5 3h11V2h-11z"/>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 使用 Bootstrap 的 bi-plus-circle 圖標來加載更多留言 -->
    <div v-if="!allCommentsLoaded" class="load-more">
      <svg 
        xmlns="http://www.w3.org/2000/svg" 
        width="32" 
        height="32" 
        fill="currentColor" 
        class="bi bi-plus-circle" 
        viewBox="0 0 16 16"
        @click="loadMoreComments" 
        :class="{ 'loading': isLoading }"
      >
        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
        <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4"/>
      </svg>
    </div>

    <!-- 已加載所有留言的提示 -->
    <div v-else class="no-more-comments">已加載所有留言</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import userStore from '@/stores/user'; // 使用正確的 userStore 路徑
import axiosapi from '@/plugins/axios';
import LikeButton from './LikeButton.vue';
import CommentForm from './CommentForm.vue'; // 引入 CommentForm 組件
import dayjs from 'dayjs';
import Swal from 'sweetalert2'; // 引入 SweetAlert2

const props = defineProps({
  postId: {
    type: Number,
    required: true,
  },
});

const user = userStore(); // 初始化 userStore

const comments = ref([]);
const page = ref(0);
const size = ref(5);  // 每次加載的留言數量
const totalPages = ref(1);
const isLoading = ref(false);
const allCommentsLoaded = ref(false);  // 判斷是否已加載所有留言
const editingCommentId = ref(null); // 當前正在編輯的留言ID
const emit = defineEmits(['commentCountUpdated']);

// 加載留言的函數
const loadComments = async (reset = false) => {
  try {
    console.log('重新加載評論列表...');  // 調試信息
    console.log('Post ID:', props.postId, 'Page:', page.value, 'Size:', size.value); // 調試參數信息
    isLoading.value = true;  // 開始加載

    if (reset) {
      comments.value = []; // 重置留言列表
      page.value = 0; // 重置頁碼
      allCommentsLoaded.value = false; // 重置已加載所有留言的標誌
    }

    const { data } = await axiosapi.get(`/api/comments/post/${props.postId}/sorted`, {
      params: { page: page.value, size: size.value },  // 設置分頁參數
    });

    console.log('API 請求成功，返回數據:', data);  // 調試API響應信息

    comments.value.push(...data.content);  // 將新留言添加到現有留言列表中
    totalPages.value = data.totalPages || 1;  // 更新總頁數

    if (page.value >= totalPages.value - 1) {
      allCommentsLoaded.value = true;  // 如果已加載完所有頁面，設置標誌
    }
    
    updateCommentCount(); // 在加載評論後更新評論數量
  } catch (error) {
    console.error('載入留言時發生錯誤：', error);
    alert('無法載入留言，請檢查您的網路連線或稍後再試。');
  } finally {
    isLoading.value = false;  // 結束加載
  }
};

const updateComments = (newComments) => {
  comments.value = [...newComments]; // 使用新的評論列表替換現有的評論列表
  updateCommentCount(); // 更新評論列表後重新計算評論數量
  console.log('Comments updated in CommentList:', comments.value); // 調試信息
};

// 更新評論數量
const updateCommentCount = () => {
  const normalCommentsCount = comments.value.filter(comment => comment.commentReportStatus === 'N').length;
  console.log('Updated comment count in CommentList:', normalCommentsCount); // 調試信息
  emit('commentCountUpdated', props.postId, normalCommentsCount); // 發送更新評論數量事件
};

// 暴露 loadComments 方法給父組件
defineExpose({ loadComments, updateComments });

// 第一次加載留言
onMounted(() => {
  loadComments(); // 初始加載，這裡不需要重置
});

// 加載更多留言的函數，用於滾動加載或點擊加載更多
const loadMoreComments = () => {
  if (!allCommentsLoaded.value) {
    page.value++;  // 增加頁碼
    loadComments();  // 加載下一頁留言，不重置現有留言
  }
};


const formatTime = (dateString) => {
  const postDate = dayjs(dateString);
  const now = dayjs();
  const diffInMinutes = now.diff(postDate, 'minute');

  if (diffInMinutes < 60) {
    return `${diffInMinutes} 分鐘前`;
  }

  const diffInHours = now.diff(postDate, 'hour');
  if (diffInHours < 24) {
    return `${diffInHours} 小時前`;
  }

  const diffInDays = now.diff(postDate, 'day');
  return `${diffInDays} 天前`;
};

const handleLikeToggled = ({ id, likeCount, isLiked }) => {
  const comment = comments.value.find(comment => comment.commentId === id);
  if (comment) {
    comment.likeCount = likeCount;
    comment.isLiked = isLiked;
  } else {
    console.warn('找不到指定的評論，ID:', id);
  }
};

const handleImageError = (event) => {
  event.target.src = 'http://localhost:8080/meetu/images/default-profile.png';
};

// 判斷是否為當前用戶
const isCurrentUser = (commentUserId) => {
  console.log('Current user ID:', user.id, 'Comment user ID:', commentUserId);
  return user.id === commentUserId; // 使用 userStore 來檢查當前用戶
};

// 開始編輯留言
const startEdit = (commentId) => {
  editingCommentId.value = commentId;
};

// 重置編輯狀態
const resetEdit = () => {
  editingCommentId.value = null;
};

const confirmDelete = (commentId) => {
  Swal.fire({
    title: '確認刪除',
    text: '你確定要刪除此留言嗎？',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '是的，刪除!',
    cancelButtonText: '取消'
  }).then((result) => {
    if (result.isConfirmed) {
      deleteComment(commentId);  // 確認刪除後調用 deleteComment 方法
    }
  });
};

const deleteComment = async (commentId) => {
  Swal.fire({
    title: '確定要刪除此留言嗎？',
    text: '此操作無法撤銷！',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '是否刪除?',
    cancelButtonText: '取消'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        const token = getToken();
        await axiosapi.put(`/api/comments/${commentId}/soft-delete`, null, {
          headers: { Authorization: token },
        });
        comments.value = comments.value.filter(comment => comment.commentId !== commentId);
        loadComments(true); // 刪除後重新加載評論

        const updatedCommentCount = comments.value.filter(comment => comment.commentReportStatus === 'N').length;
        emit('commentCountUpdated', props.postId, updatedCommentCount);

        Swal.fire('刪除成功！', '留言已成功刪除。', 'success');
      } catch (error) {
        console.error('刪除留言時發生錯誤：', error);
        Swal.fire({
          title: '錯誤',
          text: '無法刪除留言，請稍後再試。',
          icon: 'error'
        });
      }
    }
  });
};

const isCommentEdited = (comment) => {
  // 如果 commentUpdatedAt 比 commentCreatedAt 晚，則表示已編輯
  return comment.commentUpdatedAt && comment.commentUpdatedAt !== comment.commentCreatedAt;
};


// 獲取Token
const getToken = () => {
  let token = user.token;
  if (!token) {
    console.error('No token found in user store');
    return null;
  }
  if (!token.startsWith('Bearer ')) {
    token = `Bearer ${token}`;
  }
  return token;
};

// 判斷是否正在編輯指定的留言
const isEditingComment = (commentId) => {
  return editingCommentId.value === commentId;
};
</script>

<style scoped>
.comment-list {
  margin-top: 1rem;
}

.comment-item {
  padding: 1rem;
  border-bottom: 1px solid #ddd;
  margin-bottom: 1rem;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 0.5rem;
  object-fit: cover;
}

.user-name {
  font-weight: bold;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 0.5rem;
}

.load-more {
  text-align: center;
  margin: 1rem 0;
}

.load-more .bi {
  cursor: pointer;
  font-size: 2rem; /* 圖標的大小 */
  color: #090b0e; /* 圖標的顏色 */
  transition: transform 0.2s;
}

.load-more .bi:hover {
  transform: scale(1.2); /* 鼠標懸停時放大效果 */
}

.no-more-comments {
  text-align: center;
  margin: 1rem 0;
}

.edit-button, .delete-button, .confirm-edit-button, .cancel-edit-button {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
}

.confirm-edit-button {
  color: green;
}

.cancel-edit-button {
  color: red;
}

.svg-button svg {
  fill: rgb(4, 4, 7); /* 設定圖標的顏色，例如藍色 */
  transition: fill 0.2s; /* 添加平滑過渡效果 */
}

.svg-button svg:hover {
  fill: rgb(10, 8, 8); /* 懸停時設置圖標的顏色，例如紅色 */
}

.comment-actions {
  display: flex; /* 使用 Flexbox 佈局 */
  align-items: center; /* 垂直居中 */
  justify-content: space-between; /* 兩端對齊 */
  margin-top: 0.5rem; /* 添加上間距 */
}

.action-buttons {
  display: flex; /* 使用 Flexbox 佈局 */
  gap: 10px; /* 按鈕之間的間距 */
  margin-left: auto; /* 推動到最右側 */
}

.svg-button {
  background: none; /* 移除背景 */
  border: none; /* 移除邊框 */
  cursor: pointer; /* 指針樣式 */
}

.edited-label {
  color: #888;
  font-size: 12px;
  margin-left: 5px;
}
</style>
