<template>
  <div class="post-detail-modal" v-if="post" @click.self="closeModal">
    <!-- Instagram風格卡片 -->
    <div :class="['instagram-style-card', { 'single-column': !hasMedia }]">
      <!-- 左側：媒體內容 -->
      <div v-if="hasMedia" class="media-container">
        <Swiper
          :modules="modules"
          :cssMode="true"
          :pagination="{ clickable: true }"
          :navigation="true"
          :mousewheel="true"
          :keyboard="true"
          class="media-swiper"
        >
          <SwiperSlide v-for="(media, index) in post.media" :key="index">
            <img
              v-if="media.type === 'image'"
              :src="`http://localhost:8080/meetu${media.url}`"
              alt="Post Image"
              class="post-media"
            />
            <video
              v-else-if="media.type === 'video'"
              controls
              :src="`http://localhost:8080/meetu${media.url}`"
              class="post-video"
            ></video>
          </SwiperSlide>
        </Swiper>
      </div>

      <!-- 右側：用戶信息和互動區域 -->
      <div class="content-container">
        <!-- 卡片頭部：用戶信息和發布時間 -->
        <div class="card-header">
          <div class="user-info">
            <img
              :src="post.userPics ? `http://localhost:8080/meetu${post.userPics}` : 'http://localhost:8080/meetu/images/default-profile.png'"
              alt="User Profile"
              class="user-profile-image"
            />
            <div class="user-details">
              <h3 class="username">{{ post.userName }}</h3>
              <span class="post-time">{{ formatDate(post.postCreatedAt) }}</span>
            </div>
          </div>
        </div>

        <!-- 卡片內容：標題、描述 -->
        <div class="post-caption">
          <p>{{ post.caption }}</p>
        </div>

        <!-- 卡片底部：按讚和留言功能 -->
        <div class="card-footer">
          <!-- 使用 LikeButton 並確保其依賴最新的 isLiked 狀態 -->
          <LikeButton 
            :type="'post'" 
            :id="post.postId" 
            :initialLiked="post.isLiked" 
            :initialLikeCount="post.likeCount" 
            @likeToggled="handleLikeToggled" 
            :key="post.isLiked"
          />

          <div class="comment-section">
            <!-- 監聽 commentsUpdated 事件，傳遞完整留言列表 -->
            <CommentForm 
              :postId="props.postId" 
              @commentsUpdated="handleCommentsUpdated" 
              @commentCountUpdated="updateCommentCount"
            />

            <!-- 使用 ref 引用 CommentList 的實例 -->
            <CommentList 
              ref="commentListRef" 
              :postId="post.postId" 
              @commentCountUpdated="updateCommentCount"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import axiosapi from '@/plugins/axios';
import LikeButton from './LikeButton.vue';
import CommentForm from './CommentForm.vue';
import CommentList from './CommentList.vue';
import { Swiper, SwiperSlide } from 'swiper/vue';
import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import { Navigation, Pagination, Mousewheel, Keyboard } from 'swiper/modules';
import dayjs from 'dayjs';

const emit = defineEmits(['commentCountUpdated', 'likeCountUpdated', 'close']);

const props = defineProps({
  postId: {
    type: Number,
    required: true,
  },
});

const post = ref(null);
const modules = [Navigation, Pagination, Mousewheel, Keyboard];
const commentListRef = ref(null);

const loadPostDetail = async () => {
  try {
    const response = await axiosapi.get(`/api/posts/${props.postId}`);
    post.value = {
      ...response.data,
      media: [
        ...(response.data.imageUrl ? [{ type: 'image', url: response.data.imageUrl }] : []),
        ...(response.data.videoUrl ? [{ type: 'video', url: response.data.videoUrl }] : []),
      ],
      commentCount: response.data.commentCount || 0,
    };
    // 初始加載留言
    if (commentListRef.value) {
      commentListRef.value.loadComments();
    }
  } catch (error) {
    console.error('Error loading post detail:', error);
  }
};

onMounted(() => {
  loadPostDetail();
});

function formatDate(dateString) {
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
}

const hasMedia = computed(() => post.value && post.value.media && post.value.media.length > 0);

const refreshComments = () => {
  if (commentListRef.value) {
    commentListRef.value.loadComments();
  }
};

// 處理評論更新
const handleCommentsUpdated = async (updatedComments) => {
  if (post.value) {
    if (commentListRef.value) {
      commentListRef.value.updateComments(updatedComments);
    }

    const normalCommentsCount = updatedComments.filter(comment => comment.commentReportStatus === 'N').length;
    console.log('Filtered normal comments count:', normalCommentsCount); // 調試信息

    post.value.commentCount = normalCommentsCount; // 更新 post 的 commentCount
    console.log('Updating comment count in PostDetail:', post.value.commentCount); // 調試信息

    // 發送事件通知父組件
    emit('commentCountUpdated', props.postId, post.value.commentCount);
  }
};

// 更新評論數量並通知父組件
const updateCommentCount = (postId, newCount) => {
  if (post.value && post.value.postId === postId) {
    post.value.commentCount = newCount;  // 總是更新評論數量
    console.log('Updating comment count in PostDetail:', newCount);
    emit('commentCountUpdated', postId, newCount);  // 發送更新評論數量事件到父組件（貼文牆）
  }
};

// 更新按讚狀態並發射事件
const handleLikeToggled = ({ id, likeCount, isLiked }) => {
  if (post.value) {
    post.value.likeCount = likeCount;
    post.value.isLiked = isLiked;  // 確保即時更新 isLiked 狀態
    emit('likeCountUpdated', { id, likeCount, isLiked }); // 發射事件給父組件
  }
};

// 關閉模態框並發送最新的數據到父組件（例如貼文牆）
const closeModal = () => {
  // 在關閉模態框時檢查是否有變更，並更新父組件
  emit('close');  // 發送關閉模態框事件
  if (post.value && post.value.commentCount !== undefined) {
    emit('commentCountUpdated', post.value.postId, post.value.commentCount);  // 發送評論數量更新事件
  }
  if (post.value && post.value.likeCount !== undefined) {
    emit('likeCountUpdated', { id: post.value.postId, likeCount: post.value.likeCount, isLiked: post.value.isLiked });  // 發送按讚數量更新事件
  }
};

// 監聽 post 的變化並發射更新事件
watch(
  () => post.value,
  (newPost) => {
    if (newPost) {
      emit('likeCountUpdated', {
        id: newPost.postId,
        likeCount: newPost.likeCount,
        isLiked: newPost.isLiked,
      });
    }
  },
  { deep: true }
);

</script>


<style scoped>
.instagram-style-card {
  display: flex; /* 使用Flexbox將左右兩側分開 */
  max-width: 1200px; /* 調整卡片的最大寬度 */
  margin: auto;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 1px 0 rgba(0, 0, 0, 0.5), 0 1px 10px 0 rgba(0, 0, 0, 0.15);
  overflow: hidden;
  align-items: stretch; /* 讓兩邊等高 */
  height: 80vh; /* 固定卡片的高度 */
  width: 90vw; /* 調整卡片的寬度為視窗寬度的90% */
}

.instagram-style-card.single-column {
  flex-direction: column; /* 單列模式 */
  max-width: 500px;
  height: auto; /* 自適應高度 */
}

.media-container {
  flex: 3; /* 左側佔更大部分 */
  border-right: 1px solid #ddd; /* 分隔左右 */
  display: flex;
  align-items: center; /* 垂直置中 */
  justify-content: center; /* 水平置中 */
  width: 100%; /* 讓容器佔滿父容器寬度 */
  height: 100%; /* 讓高度填充父容器 */
  overflow: hidden; /* 隱藏超出部分 */
  background-color: #f0f0f0; /* 添加背景顏色以區分圖片區域 */
  position: relative; /* 確保容器的子元素絕對定位基於這個容器 */
}

.post-media {
  width: 100%; /* 設定寬度為100% */
  height: 100%; /* 設定高度為100% */
  object-fit: cover; /* 保持圖片比例並填滿容器 */
  object-position: center; /* 使圖片內容居中顯示 */
}

.post-video {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持內容比例並填滿容器 */
  object-position: center; /* 確保影片內容居中 */
  border-radius: 8px; /* 選擇性：如果你想要影片邊角圓角 */
  background-color: #000; /* 選擇性：為了使影片周圍不顯得突兀 */
}

.content-container {
  flex: 2; /* 右側部分也稍微加寬 */
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* 讓內容合理分佈 */
  padding: 20px;
  height: 100%; /* 讓高度填充父容器 */
  overflow-y: auto; /* 當內容超出時允許滾動 */
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.user-profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 14px;
  object-fit: cover; /* 新增此行來保持圖片比例 */
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  margin: 0;
  color: #444444;
  font-size: 16px;
  font-weight: bold;
}

.post-time {
  font-size: 12px;
  color: #888;
}

.post-caption {
  color: #444444;
  font-size: 14px;
  margin: 10px 0;
}

.card-footer {
  border-top: 1px solid #ddd;
  padding-top: 16px;
  margin-top: auto; /* 讓留言區域在底部 */
}

.media-swiper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  overflow: hidden; /* 確保圖片不會超出容器 */
}

.swiper-slide {
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  height: 100%; /* 確保 Swiper Slide 滿高 */
}
</style>
