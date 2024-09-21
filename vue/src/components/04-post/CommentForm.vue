<template>
  <div class="comment-form">
    <textarea v-model="commentContent" :placeholder="isEditMode ? '編輯你的留言...' : '寫下你的留言...'" rows="3"></textarea>
    <button @click="submitComment" :disabled="isSubmitting">{{ isEditMode ? '更新留言' : '提交' }}</button>
    <button v-if="isEditMode" @click="cancelEdit">取消編輯</button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2'; // 引入 SweetAlert2
import userStore from '@/stores/user'; // 使用 Pinia 狀態管理
import axiosapi from '@/plugins/axios';

// 定義組件接收的 props
const props = defineProps({
  postId: Number,  // 用於新增留言
  comment: Object,  // 用於編輯或刪除留言
  isEditMode: Boolean,  // 是否處於編輯模式
});

// 定義 emit 方法來發送事件
const emit = defineEmits(['commentsUpdated', 'editCancelled', 'commentCountUpdated']); // 新增 commentCountUpdated 事件

const router = useRouter();
const user = userStore();
const commentContent = ref('');
const isSubmitting = ref(false);
const comments = ref([]); // 定義 comments 用來存儲評論列表

// 當進入編輯模式時，初始化留言內容
watch(
  () => props.comment,
  () => {
    if (props.isEditMode && props.comment) {
      commentContent.value = props.comment.commentContent;
    } else {
      commentContent.value = ''; // 清空留言框
    }
  },
  { immediate: true }
);

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

// 提交評論或編輯評論
const submitComment = async () => {
  if (!commentContent.value.trim()) {
    Swal.fire({
      title: '錯誤',
      text: '留言內容不能為空！',
      icon: 'error',
    });
    return;
  }

  if (!user.token) {
    Swal.fire({
      title: '未登入',
      text: '你需要先登入才能留言。',
      icon: 'warning',
    }).then(() => {
      router.push('/login');
    });
    return;
  }

  isSubmitting.value = true;
  try {
    const token = getToken();
    if (!token) {
      console.error('No token found');
      return;
    }

    let response;
    if (props.isEditMode && props.comment) {
      // 編輯模式
      response = await axiosapi.put(
        `/api/comments/${props.comment.commentId}`,
        { commentContent: commentContent.value },
        {
          headers: { Authorization: token },
        }
      );

      Swal.fire({
        title: '成功',
        text: '留言已更新！',
        icon: 'success',
      });

      emit('commentsUpdated', response.data); // 傳遞更新後的留言列表
      emit('editCancelled'); // 結束編輯模式
    } else {
      // 新增模式
      const requestData = {
        postId: props.postId,
        commentContent: commentContent.value,
      };

      response = await axiosapi.post('/api/comments', requestData, {
        headers: { Authorization: token },
      });

      Swal.fire({
        title: '成功',
        text: '留言成功！',
        icon: 'success',
      });

      commentContent.value = ''; // 清空留言框

      emit('commentsUpdated', response.data); // 傳遞更新後的留言列表
    }
  } catch (error) {
    console.error('Error during submitComment:', error);
    Swal.fire({
      title: '錯誤',
      text: '操作失敗，請稍後再試。',
      icon: 'error',
    });
  } finally {
    isSubmitting.value = false;
  }
};

// 刪除評論（軟刪除）
const deleteComment = async (commentId) => {
  Swal.fire({
    title: '確定要刪除此留言嗎？',
    text: '此操作將無法撤銷！',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: '是的，刪除它！',
    cancelButtonText: '取消'
  }).then(async (result) => {
    if (result.isConfirmed) {
      try {
        const token = getToken();
        if (!token) {
          console.error('No token found');
          return;
        }

        // 使用 PUT 方法進行軟刪除
        const response = await axiosapi.put(`/api/comments/${commentId}/soft-delete`, null, {
          headers: { Authorization: token },
        });

        const updatedComments = response.data;
        console.log('Received updated comments after deletion:', updatedComments); // 調試信息

        Swal.fire({
          title: '成功',
          text: '留言已刪除！',
          icon: 'success',
        });

        // 發送事件通知父組件更新評論列表和數量
        emit('commentsUpdated', updatedComments); // 更新評論列表
        const normalCommentsCount = updatedComments.filter(comment => comment.commentReportStatus === 'N').length;
        emit('commentCountUpdated', props.postId, normalCommentsCount); // 更新評論數量

      } catch (error) {
        console.error('Error deleting comment:', error);
        Swal.fire({
          title: '錯誤',
          text: '刪除留言時發生錯誤。',
          icon: 'error',
        });
      }
    }
  });
};


// 取消編輯
const cancelEdit = () => {
  emit('editCancelled');
};
</script>

<style scoped>
.comment-form {
  margin-bottom: 1rem;
}

textarea {
  width: 100%;
  margin-bottom: 0.5rem;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #ddd;
  resize: none; /* 禁止調整大小 */
}

button {
  padding: 0.5rem 1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

button:hover:not(:disabled) {
  background-color: #45a049;
}

.delete-button {
  background-color: #e74c3c; /* 刪除按鈕的顏色 */
}

.delete-button:hover:not(:disabled) {
  background-color: #c0392b; /* 懸停時的刪除按鈕顏色 */
}
</style>
