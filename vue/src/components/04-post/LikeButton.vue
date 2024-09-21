<template>
  <button @click="toggleLike" class="like-button" :disabled="isLoading">
    <!-- 使用 Bootstrap Icons 替代 FontAwesome 圖標 -->
    <svg
      v-if="isLiked"
      xmlns="http://www.w3.org/2000/svg"
      width="30"
      height="30"
      fill="currentColor"
      class="bi bi-heart-fill"
      viewBox="0 0 16 16"
    >
      <path
        fill-rule="evenodd"
        d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314"
      />
    </svg>
    <svg
      v-else
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      fill="currentColor"
      class="bi bi-heart"
      viewBox="0 0 16 16"
    >
      <path
        d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15"
      />
    </svg>
    {{ likeCount }}
  </button>
</template>

<script setup>
import { ref,watch } from 'vue';
import userStore from '@/stores/user';
import axiosapi from '@/plugins/axios';

const props = defineProps({
  type: {
    type: String,
    required: true,
    validator: (value) => ['post', 'comment'].includes(value),
  },
  id: {
    type: Number,
    required: true,
  },
  initialLiked: Boolean,
  initialLikeCount: Number,
});

const emit = defineEmits(['likeToggled']);

const user = userStore();
const isLiked = ref(props.initialLiked);
const likeCount = ref(props.initialLikeCount);
const isLoading = ref(false);

const getToken = () => {
  let token = user.token;
  if (!token) {
    console.error('No token found in user store');
    return null;
  }
  return token.startsWith('Bearer ') ? token : `Bearer ${token}`;
};

// 按讚操作
const toggleLike = async () => {
  if (isLoading.value) return;
  isLoading.value = true;
  try {
    const response = await axiosapi.post(`/api/likes/${props.type}/${props.id}/toggle`, {}, {
      headers: { Authorization: getToken() },
    });

    if (response.data) {
      isLiked.value = response.data.liked;
      likeCount.value = response.data.likeCount;

      emit('likeToggled', { id: props.id, likeCount: response.data.likeCount, isLiked: response.data.liked });
    }
  } catch (error) {
    console.error('Error toggling like:', error);
  } finally {
    isLoading.value = false;
  }
};

watch(() => props.initialLiked, (newValue) => {
  isLiked.value = newValue;
});

watch(() => props.initialLikeCount, (newValue) => {
  likeCount.value = newValue;
});
</script>

<style scoped>
.like-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  gap: 5px;
}

.like-button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

/* Bootstrap Icons 樣式 */
.bi-heart,
.bi-heart-fill {
  transition: color 0.3s ease;
}

.bi-heart-fill {
  color: #ff4136; /* 按讚後的愛心顏色 */
}

.bi-heart {
  color: #ccc; /* 未按讚的愛心顏色 */
}
</style>
