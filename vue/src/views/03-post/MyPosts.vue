<template>
  <div class="container-fluid">
    <div class="col-12">
      <UserSidebar />
    </div>
    <div class="my-posts-container">
      <div v-if="loading" class="text-center my-5">
        <div class="spinner-border" role="status">
          <span class="sr-only">載入中...</span>
        </div>
      </div>
      <div v-if="error" class="alert alert-danger">{{ error }}</div>

      <div v-if="posts.length > 0" class="post-container">
        <div v-for="post in sortedPosts" :key="post.postId" class="post-item">
          <!-- Post header -->
          <div class="post-header">
            <div class="user-info">
              <!-- 點擊頭像時跳轉到用戶頁面 -->
              <router-link
                v-if="post.userId"
                :to="{ name: 'userprofile-link', params: { userId: post.userId } }"
                class="user-avatar-link"
                @click.stop
              >
                <img
                  :src="post.userPics ? `http://localhost:8080/meetu${post.userPics}` : 'http://localhost:8080/meetu/images/default-profile.png'"
                  alt="User Profile"
                  class="user-profile-image"
                  @error="handleImageError"
                />
              </router-link>
              <!-- 點擊用戶名稱時跳轉到用戶頁面 -->
              <div class="user-name-date">
                <router-link
                  v-if="post.userId"
                  :to="{ name: 'userprofile-link', params: { userId: post.userId } }"
                  class="user-name-link"
                  @click.stop
                >
                  <h3 class="user-name">{{ post.userName }}</h3>
                </router-link>
                <div class="post-time-container">
                  <small class="text-muted">創建於: {{ formatDate(post.postCreatedAt) }}</small>
                  <span v-if="post.postUpdatedAt !== post.postCreatedAt" class="d-block text-muted">
                    更新於: {{ formatDate(post.postUpdatedAt) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Post body -->
<div class="post-body" @click="showDetail(post)">
  <div class="post-content">
    <!-- 根據 post.showMore 狀態顯示部分或全部描述 -->
    <p class="card-text">
      {{ post.caption.length > 100 ? post.caption.substring(0, 100) + '...' : post.caption }}
    </p>
    <!-- 顯示「顯示更多」鏈接，點擊時跳轉到詳細頁面 -->
    <a v-if="post.caption.length > 100" class="show-more-link" @click.stop="toggleShowMore(post)">
      顯示更多
    </a>
  </div>

            <!-- Media display (images or videos) -->
            <div class="media-container">
              <Swiper :pagination="{ clickable: true }" :navigation="true" :modules="modules" class="media-swiper">
                <SwiperSlide v-for="(media, index) in post.media" :key="index" @click.stop>
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
                    class="post-media"
                    @error="handleVideoError"
                  ></video>
                </SwiperSlide>
              </Swiper>
            </div>
          </div>

          <!-- Post interactions -->
          <div class="post-interactions">
            <LikeButton
              :type="'post'"
              :id="post.postId"
              :initialLiked="post.isLiked"
              :initialLikeCount="post.likeCount"
              @click.stop
              @likeToggled="handleLikeToggled"
            />
            <button class="comment-button" @click="showDetail(post)">
              <i class="bi bi-chat" :class="{ 'bi bi-chat-fill': post.commentCount > 0 }"></i>
              <span class="comment-count">{{ post.commentCount }}</span>
            </button>

            <!-- 將編輯和刪除按鈕移動到右側 -->
            <div class="action-buttons">
              <button @click.stop="editPost(post)" class="edit-button btn btn-primary">
                <i class="bi bi-pencil"></i> 編輯
              </button>
              <button @click.stop="deletePost(post.postId)" class="delete-button btn btn-danger">
                <i class="bi bi-trash"></i> 刪除
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="!loading && !error">
        <p class="text-center mt-5">目前沒有貼文。</p>
      </div>

      <!-- Modal for post details -->
      <div v-if="selectedPost && selectedPost.postId" class="modal" @click.self="closeDetail">
        <PostDetailCard
          :postId="selectedPost.postId"
          @close="closeDetail"
          @post-updated="handlePostUpdate"
          @commentCountUpdated="updateCommentCount"
          @likeCountUpdated="handleLikeToggled"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, computed, onBeforeUnmount } from 'vue';
import userStore from '@/stores/user';
import axiosapi from '@/plugins/axios';
import { useRouter } from 'vue-router';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Navigation, Pagination } from 'swiper/modules';
import LikeButton from '@/components/04-post/LikeButton.vue';
import PostDetailCard from '@/components/04-post/PostDetailCard.vue';
import UserSidebar from '@/components/02-user/UserSidebar.vue';
import Swal from 'sweetalert2';
import _ from 'lodash'; // 確保已導入 lodash

import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';

export default {
  components: {
    Swiper,
    SwiperSlide,
    LikeButton,
    PostDetailCard,
    UserSidebar,
  },
  setup() {
    const posts = ref([]);
    const loading = ref(true);
    const error = ref(null);
    const user = userStore();
    const router = useRouter();
    const modules = [Navigation, Pagination];
    const selectedPost = ref(null);
    const page = ref(0); // 頁碼初始為0
    const isFetching = ref(false); // 加載狀態

    // 貼文排序：按照最新時間排序貼文（初始及後續加載時）
    const sortedPosts = computed(() => {
      return posts.value; // 直接使用已排序的 posts
    });

    onMounted(async () => {
      await fetchMyPosts(); // 初始化加載貼文
      window.addEventListener('scroll', debouncedHandleScroll); // 使用防抖函數監聽滾動事件
    });

    onBeforeUnmount(() => {
      window.removeEventListener('scroll', debouncedHandleScroll); // 移除滾動事件監聽器
    });

    // 使用 lodash 防抖處理滾動事件
    const debouncedHandleScroll = _.debounce(() => {
      handleScroll();
    }, 200);

    const handleScroll = () => {
      if (isFetching.value || loading.value) return; // 防止重複加載
      const scrollTop = window.scrollY; // 滾動距離
      const windowHeight = window.innerHeight; // 視窗高度
      const documentHeight = document.documentElement.scrollHeight; // 文檔總高度

      // 檢查是否滾動到接近頁面底部
      if (scrollTop + windowHeight >= documentHeight - 100) {
        // 100px 緩衝區
        fetchMorePosts(); // 加載更多貼文
      }
    };

    const fetchMyPosts = async () => {
      try {
        if (!user.isLogedin) {
          error.value = '請先登入。';
          return;
        }

        let token = user.token;
        if (!token.startsWith('Bearer ')) {
          token = `Bearer ${token}`;
        }

        const response = await axiosapi.get('/api/posts/my-posts', {
          headers: {
            Authorization: token,
          },
          params: {
            page: page.value, // 載入當前頁面
            size: 10, // 每頁顯示的貼文數量
            sort: 'postUpdatedAt,desc', // 確保後端返回時已經按照最新時間降序排列
          },
        });

        posts.value = response.data.map((post) => ({
          ...post,
          imageUrl: post.imageUrl ? `http://localhost:8080/meetu${post.imageUrl}` : null,
          videoUrl: post.videoUrl ? `http://localhost:8080/meetu${post.videoUrl}` : null,
          media: [
            ...(post.imageUrl ? [{ type: 'image', url: post.imageUrl }] : []),
            ...(post.videoUrl ? [{ type: 'video', url: post.videoUrl }] : []),
          ],
          commentCount: post.commentCount || 0,
          likeCount: post.likeCount || 0,
          showMore: false,
        }));

        await nextTick(); // 等待 DOM 更新
      } catch (err) {
        console.error('Error fetching posts:', err);
        error.value = err.response ? err.response.data : '無法連接到伺服器';
      } finally {
        loading.value = false;
      }
    };

    const fetchMorePosts = async () => {
      if (isFetching.value || loading.value) return; // 防止多次請求
      isFetching.value = true;
      try {
        page.value++; // 增加頁碼
        let token = user.token;
        if (!token.startsWith('Bearer ')) {
          token = `Bearer ${token}`;
        }

        const response = await axiosapi.get('/api/posts/my-posts', {
          headers: {
            Authorization: token,
          },
          params: {
            page: page.value, // 加載當前頁碼的貼文
            size: 10, // 每頁顯示的貼文數量
            sort: 'postUpdatedAt,desc', // 確保後端返回時已經按照最新時間降序排列
          },
        });

        const existingPostIds = new Set(posts.value.map((post) => post.postId)); // 獲取當前已載入的貼文ID集合

        const morePosts = response.data
          .filter((post) => !existingPostIds.has(post.postId)) // 過濾掉已存在的貼文
          .map((post) => ({
            ...post,
            imageUrl: post.imageUrl ? `http://localhost:8080/meetu${post.imageUrl}` : null,
            videoUrl: post.videoUrl ? `http://localhost:8080/meetu${post.videoUrl}` : null,
            media: [
              ...(post.imageUrl ? [{ type: 'image', url: post.imageUrl }] : []),
              ...(post.videoUrl ? [{ type: 'video', url: post.videoUrl }] : []),
            ],
            commentCount: post.commentCount || 0,
            likeCount: post.likeCount || 0,
            showMore: false,
          }));

        posts.value.push(...morePosts); // 只將新的貼文添加到列表中
      } catch (err) {
        console.error('Error fetching more posts:', err);
        error.value = err.response ? err.response.data : '無法連接到伺服器';
      } finally {
        isFetching.value = false;
      }
    };

    const formatDate = (dateString) => {
      if (!dateString) return '';
      const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(dateString).toLocaleDateString(undefined, options);
    };

    const editPost = (post) => {
      router.push({ name: 'editPost-link', params: { id: post.postId } });
    };

    const deletePost = async (postId) => {
      const result = await Swal.fire({
        title: '確定要刪除這篇貼文嗎？',
        text: '此操作無法撤銷！',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: '是的，刪除它！',
        cancelButtonText: '取消',
      });

      if (result.isConfirmed) {
        try {
          let token = user.token.trim();
          if (!token.startsWith('Bearer ')) {
            token = `Bearer ${token}`;
          }

          await axiosapi.put(`/api/posts/soft-delete/${postId}`, null, {
            headers: {
              Authorization: token,
            },
          });

          posts.value = posts.value.filter((post) => post.postId !== postId);

          Swal.fire('已刪除！', '您的貼文已成功刪除。', 'success');
        } catch (err) {
          Swal.fire('錯誤', '刪除貼文時發生錯誤。', 'error');
          console.error(err);
        }
      }
    };

    const showDetail = (post) => {
      selectedPost.value = { ...post };
    };

    const closeDetail = () => {
  if (selectedPost.value) {
    const index = posts.value.findIndex((p) => p.postId === selectedPost.value.postId);
    if (index !== -1) {
      posts.value[index] = { ...selectedPost.value };
    }
  }
  selectedPost.value = null; // 清空選中的貼文
};

    const handlePostUpdate = (updatedPost) => {
      const index = posts.value.findIndex((p) => p.postId === updatedPost.postId);
      if (index !== -1) {
        posts.value[index] = { ...posts.value[index], ...updatedPost };
        if ('likeCount' in updatedPost) {
          posts.value[index].likeCount = updatedPost.likeCount;
        }
        if ('isLiked' in updatedPost) {
          posts.value[index].isLiked = updatedPost.isLiked;
        }
      }
      if (selectedPost.value && selectedPost.value.postId === updatedPost.postId) {
        selectedPost.value = { ...selectedPost.value, ...updatedPost };
      }
    };

    const updateCommentCount = (postId, newCount) => {
      const postIndex = posts.value.findIndex((post) => post.postId === postId);
      if (postIndex !== -1) {
        posts.value = [
          ...posts.value.slice(0, postIndex),
          { ...posts.value[postIndex], commentCount: newCount },
          ...posts.value.slice(postIndex + 1),
        ];
      }
      if (selectedPost.value && selectedPost.value.postId === postId) {
        selectedPost.value = { ...selectedPost.value, commentCount: newCount };
      }
    };

    const handleImageError = (event) => {
      event.target.src = 'http://localhost:8080/meetu/images/default-profile.png';
    };

    const toggleShowMore = (post) => {
  showDetail(post); 
};

    const handleLikeToggled = ({ id, likeCount, isLiked }) => {
  const postIndex = posts.value.findIndex((post) => post.postId === id);
  if (postIndex !== -1) {
    posts.value[postIndex] = {
      ...posts.value[postIndex],
      likeCount,
      isLiked,
    };
  }
  if (selectedPost.value && selectedPost.value.postId === id) {
    selectedPost.value = { ...selectedPost.value, likeCount, isLiked };
  }
};

    return {
      posts,
      sortedPosts,
      loading,
      error,
      formatDate,
      editPost,
      deletePost,
      showDetail,
      closeDetail,
      handlePostUpdate,
      handleImageError,
      toggleShowMore,
      modules,
      selectedPost,
      page,
      isFetching,
      handleScroll,
      fetchMorePosts,
      updateCommentCount,
      handleLikeToggled,
    };
  },
};
</script>



<style scoped>
.container-fluid {
  padding-top: 20px;
}

.my-posts-container {
  margin-left: 0px; /* 根據需要調整的左側邊距 */
  padding-top: 20px;
  margin-top: 20px; /* 添加上外邊距以保持與 UserSidebar 的距離 */
}

.post-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 100%; /* 調整為貼文牆寬度的 95% */
  margin: 0 auto; /* 確保貼文內容居中 */
}

.post-item {
  background-color: #ffffff;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  padding: 20px;
  cursor: pointer;
}

.post-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-profile-image {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 15px;
  object-fit: cover;
}

.user-name-date {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  color: #1a1a1a;
  margin: 0 0 5px 0;
}

.post-time-container {
  font-size: 14px;
  color: #666;
}

.post-body {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.post-content {
  flex: 1;
  margin-right: 20px;
  font-size: 16px;
  color: #333;
  line-height: 1.6;
}

.show-more-link {
  color: #1877f2;
  cursor: pointer;
  font-size: 14px;
}

.media-container {
  width: 200px; /* 調整為與貼文牆相同大小 */
  height: 200px; /* 調整為與貼文牆相同大小 */
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 10px;
  position: relative;
}

.post-media {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.post-interactions {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 讓兩邊的按鈕分開 */
  gap: 20px;
  margin-top: 10px;
  padding: 10px 15px;
}

.comment-button,
.like-button {
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  font-size: 16px;
  display: flex; /* 使用 Flexbox 對齊圖標和文本 */
  align-items: center; /* 垂直居中對齊 */
  gap: 5px; /* 控制圖標和文字之間的間距 */
  margin: 0; /* 確保按鈕不會有額外的垂直間距 */
}

.comment-button i,
.like-button i {
  font-size: 20px; /* 調整圖標的大小 */
  margin-right: 5px; /* 控制圖標和文字之間的距離 */
  display: inline-flex; /* 使用 inline-flex 使圖標和文本更容易對齊 */
  align-items: center; /* 垂直居中圖標 */
}

.comment-count,
.like-count {
  font-size: 16px; /* 確保文字的大小與圖標一致 */
  line-height: 1; /* 確保文本垂直居中 */
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto; /* 將按鈕推到右側 */
}

.edit-button, .delete-button {
  padding: 8px 16px;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  font-size: 0.9rem;
  margin-left: 10px;
}

.edit-button {
  background-color: #4CAF50;
  color: white;
}

.delete-button {
  background-color: #f44336;
  color: white;
}

.error {
  color: red;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: #ffffff;
  border-radius: 15px;
  padding: 30px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.media-swiper {
  width: 100%;
  height: 100%;
}

/* 修改 Swiper 導航按鈕的大小 */
:deep(.swiper-button-next),
:deep(.swiper-button-prev) {
  color: #ffffff !important; /* 修改箭頭顏色 */
  background-color: rgba(0, 0, 0, 0.3) !important; /* 半透明黑色背景 */
  width: 40px !important; /* 設置按鈕的寬度 */
  height: 40px !important; /* 設置按鈕的高度 */
  padding: 0 !important; /* 確保沒有內部填充 */
  border-radius: 50% !important; /* 圓形按鈕 */
  line-height: 40px !important; /* 確保箭頭在按鈕中間 */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2) !important; /* 添加陰影 */
  transition: background-color 0.3s ease, transform 0.2s ease !important; /* 過渡效果 */
  z-index: 10 !important; /* 保持在前面 */
}

/* 自定義箭頭圖標大小 */
:deep(.swiper-button-next::after),
:deep(.swiper-button-prev::after) {
  font-size: 20px !important; /* 調整箭頭圖標的大小 */
  color: #ffffff !important; /* 修改箭頭顏色 */
}

/* 懸停效果 */
:deep(.swiper-button-next:hover),
:deep(.swiper-button-prev:hover) {
  background-color: rgba(0, 0, 0, 0.5) !important; /* 懸停時加深背景顏色 */
  transform: scale(1.1) !important; /* 懸停時放大 */
}

/* 自定義 Swiper 分頁點的樣式 */
:deep(.swiper-pagination-bullet) {
  background: #fff !important;
  opacity: 0.8 !important;
}

:deep(.swiper-pagination-bullet-active) {
  background: #16191c !important;
  opacity: 1 !important;
}
</style>
