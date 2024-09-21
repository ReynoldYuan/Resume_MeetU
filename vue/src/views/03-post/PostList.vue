<template>
  <div class="post-wall">
    <div class="post-wall-header">
      <!-- 搜索框移到頂部 -->
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜尋貼文..."
          @keyup.enter="handleSearch"
        />
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      </div>

      <router-link 
        :to="{ name: 'addPost-link' }" 
        class="btn btn-lg btn-primary btn-lg-square add-post-btn"
        title="新增貼文"
      >
        <font-awesome-icon :icon="['fas', 'plus']" />
      </router-link>

      <!-- 選擇框移到右上角 -->
      <div class="filter-dropdown">
        <select v-model="selectedFilter" @change="handleFilterChange">
          <option value="popular">熱門程度</option>
          <option value="latest">最新動態</option>
        </select>
      </div>
    </div>

    <!-- 貼文顯示區域 -->
    <div v-if="posts.length === 0 && !errorMessage">No posts found</div>
    <div class="post-container">
      <div
        v-for="post in posts"
        :key="post.postId"
        class="post-item"
        @click="showDetail(post)"
      >
        <!-- 貼文頭部：顯示用戶名稱和發布時間 -->
        <div class="post-header">
          <div class="user-info">
            <!-- 用戶頭像 -->
            <router-link
              v-if="post.userId"
              :to="{ name: 'userprofile-link', params: { userId: post.userId } }"
              class="user-avatar-link"
              @click.stop
            >
              <img
                :src="
                  post.userPics
                    ? `http://localhost:8080/meetu${post.userPics}`
                    : 'http://localhost:8080/meetu/images/default-profile.png'
                "
                alt="User Profile"
                class="user-profile-image"
                @error="handleImageError"
              />
            </router-link>
            <!-- 用戶名稱和日期 -->
            <div class="user-name-date">
              <!-- 用戶名稱 -->
              <router-link
                v-if="post.userId"
                :to="{ name: 'userprofile-link', params: { userId: post.userId } }"
                class="user-name-link"
                @click.stop
              >
                <h3 class="user-name">{{ post.userName }}</h3>
              </router-link>
              <h3 v-else class="user-name">{{ post.userName }}</h3>

              <!-- 發布時間和編輯標籤 -->
              <div class="post-time-container">
                <span class="post-time">{{ formatDate(post.postUpdatedAt) }}</span>
                <span
                  v-if="post.postUpdatedAt !== post.postCreatedAt"
                  class="edited-tag"
                >
                  （已編輯）
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 貼文描述區域 -->
        <div class="post-body">
          <!-- 貼文內容描述 -->
          <div class="post-content">
            <div class="caption" :class="{ 'show-more': post.showMore }">
              {{ post.caption.length > 50
                ? post.caption.slice(0, 50) + '...'
                : post.caption }}
            </div>
            <!-- 顯示更多的連結 -->
            <a
              v-if="post.caption.length > 50"
              class="show-more-link"
              @click.stop="showDetail(post)"
            >
              {{ post.showMore ? '顯示更少' : '顯示更多' }}
            </a>
          </div>

          <!-- 使用 Swiper 顯示圖片或影片 -->
          <div class="media-container" @click.stop>
            <!-- 添加 @click.stop 到 media-container -->
            <Swiper
              :cssMode="true"
              :pagination="{ clickable: true }"
              :navigation="true"
              :mousewheel="true"
              :keyboard="true"
              :modules="modules"
              class="media-swiper"
            >
              <SwiperSlide v-for="(media, index) in post.media" :key="index">
                <img
                  v-if="media.type === 'image'"
                  :src="`http://localhost:8080/meetu${media.url}`"
                  alt="Post Image"
                  class="post-media"
                  @click.stop
                />
                <video
                  v-else
                  controls
                  :src="`http://localhost:8080/meetu${media.url}`"
                  class="post-media"
                  @click.stop
                />
              </SwiperSlide>
            </Swiper>
          </div>
        </div>

        <!-- 分隔線 -->
        <hr class="divider" />

        <!-- 貼文互動區域 -->
        <div class="post-interactions">
          <!-- 使用原來的 LikeButton 組件，但加上 @click.stop 來防止冒泡 -->
          <LikeButton
            :type="'post'"
            :id="post.postId"
            :initialLiked="post.isLiked"
            :initialLikeCount="post.likeCount"
            @click.stop
            @likeToggled="handleLikeToggled"
          />

          <!-- 留言按鈕使用 Bootstrap Icons -->
          <button class="comment-button" @click.stop="showDetail(post)">
            <i :class="post.commentCount > 0 ? 'bi bi-chat-fill' : 'bi bi-chat'"></i>
            <span class="comment-count">{{ post.commentCount }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 模態框顯示詳細內容 -->
    <div v-if="selectedPost" class="modal" @click.self="closeModal">
      <PostDetailCard 
        :postId="selectedPost.postId" 
        @close="closeModal" 
        @commentCountUpdated="updateCommentCount" 
        @likeCountUpdated="updateLikeCount"  
      />
    </div>
  </div>
</template>

<script>
import axiosapi from '@/plugins/axios';
import { Swiper, SwiperSlide } from 'swiper/vue';
import LikeButton from '@/components/04-post/LikeButton.vue';
import PostDetailCard from '@/components/04-post/PostDetailCard.vue';
import _ from 'lodash'; // 正確導入 Lodash

import 'swiper/css';
import 'swiper/css/navigation';
import 'swiper/css/pagination';

import { Navigation, Pagination, Mousewheel, Keyboard } from 'swiper/modules';

export default {
  components: {
    Swiper,
    SwiperSlide,
    LikeButton,
    PostDetailCard,
  },
  data() {
    return {
      posts: [],
      selectedPost: null,
      selectedFilter: 'popular',
      searchQuery: '',
      errorMessage: '',
      page: 0,
      size: 10,
      isLoading: false,
      isFetchingMore: false,
      isSearching: false, // 新增搜尋狀態標誌
      modules: [Navigation, Pagination, Mousewheel, Keyboard],
    };
  },
  mounted() {
    this.fetchPosts();
    window.addEventListener('scroll', this.debouncedHandleScroll); // 使用防抖函數
  },
  activated() {
    this.fetchPosts(); // 當組件被重新顯示時重新加載貼文
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.debouncedHandleScroll);
  },
  methods: {
    async fetchPosts() {
      if (this.isLoading) return; // 防止多次請求
      this.isLoading = true; // 開始加載
      console.log('Fetching posts...'); // 調試信息
      try {
        let endpoint = '/api/posts';

        // 如果不是在搜尋狀態下，使用篩選條件載入資料
        if (!this.isSearching) {
          if (this.selectedFilter === 'latest') {
            endpoint = '/api/posts/latest';
          } else if (this.selectedFilter === 'popular') {
            endpoint = '/api/posts/popular';
          }
        } else {
          // 如果在搜尋狀態下，使用搜尋的 API
          endpoint = '/api/posts/search';
        }

        const params = {
          page: this.page,
          size: this.size,
        };

        // 在搜尋狀態下，需要傳遞搜尋的關鍵字
        if (this.isSearching) {
          params.keyword = this.searchQuery;
        }

        const response = await axiosapi.get(endpoint, { params });

        if (response.data.length > 0) {
          this.processPosts(response.data);
          this.page++; // 增加頁碼，準備加載下一頁
          console.log(`Loaded page ${this.page}`);
        } else {
          this.errorMessage = 'No more posts available.';
        }
        this.errorMessage = '';
      } catch (error) {
        console.error('Error fetching posts:', error);
        this.errorMessage = '無法加載貼文，請稍後再試。';
      } finally {
        this.isLoading = false; // 結束加載
      }
    },

    async handleSearch() {
      this.errorMessage = ''; // 清空錯誤消息
      this.page = 0; // 重置頁碼
      this.posts = []; // 清空當前的貼文列表
      this.isSearching = true; // 開啟搜尋狀態

      // 檢查搜尋關鍵字是否有效
      if (!this.searchQuery.trim()) {
        this.errorMessage = '請輸入有效的關鍵字。';
        this.isSearching = false; // 清空搜尋狀態
        this.fetchPosts(); // 如果沒有關鍵字，則加載所有貼文
        return;
      }

      if (this.searchQuery.length > 5) {
        this.errorMessage = '關鍵字不能超過五個字。';
        return;
      }

      try {
        // 發送搜尋請求
        const response = await axiosapi.get(`/api/posts/search`, {
          params: {
            keyword: this.searchQuery,
            page: this.page,
            size: this.size,
          },
        });

        // 檢查搜尋結果
        if (response.data.length === 0) {
          this.errorMessage = '沒有找到相關內容。';
        } else {
          this.processPosts(response.data); // 處理獲得的數據
        }
      } catch (error) {
        console.error('Error searching posts:', error);
        this.errorMessage = '搜尋過程中出錯，請稍後再試。'; // 顯示錯誤信息
      }
    },

    debouncedHandleScroll: _.debounce(function () {
      this.handleScroll();
    }, 100), // 調整為 100 毫秒防抖

    handleScroll() {
      // 檢查是否滾動到接近底部位置
      const scrollPosition = window.innerHeight + window.scrollY;
      const bottomPosition = document.body.offsetHeight - 200; // 到達底部 200px 內時觸發
      if (scrollPosition >= bottomPosition) {
        console.log('Reached bottom, loading more posts...'); // 調試信息
        this.fetchMorePosts(); // 根據狀態加載更多內容
      }
    },

    async fetchMorePosts() {
      if (this.isFetchingMore || this.isLoading) return; // 防止多次請求
      this.isFetchingMore = true; // 開始加載更多
      try {
        await this.fetchPosts(); // 使用已有的 fetchPosts 方法
      } finally {
        this.isFetchingMore = false; // 結束加載更多
      }
    },

    processPosts(data) {
      const existingPostIds = new Set(this.posts.map(post => post.postId)); // 獲取當前已載入的貼文ID集合

      const newPosts = data
        .filter(post => !existingPostIds.has(post.postId)) // 過濾掉已存在的貼文
        .map(post => ({
          ...post,
          media: [
            ...(post.imageUrl ? [{ type: 'image', url: post.imageUrl }] : []),
            ...(post.videoUrl ? [{ type: 'video', url: post.videoUrl }] : []),
          ],
          likeCount: post.likeCount || 0,
          commentCount: post.commentCount || 0,
          isLiked: post.isLiked || false,
          postCreatedAt: post.postCreatedAt,
          postUpdatedAt: post.postUpdatedAt,
          showMore: false,
        }));

      this.posts.push(...newPosts); // 只將新的貼文添加到列表
    },

    handleFilterChange() {
      this.page = 0;
      this.posts = [];
      this.isSearching = false; // 清空搜尋狀態
      this.fetchPosts();
    },

    handleLikeToggled({ id, likeCount, isLiked }) {
      console.log('PostList: handleLikeToggled called', { id, likeCount, isLiked });
      const postIndex = this.posts.findIndex(post => post.postId === id);
      if (postIndex !== -1) {
        // 直接修改數組中的對象
        this.posts[postIndex] = {
          ...this.posts[postIndex],
          likeCount: likeCount,
          isLiked: isLiked,
        };
      }
    },

    closeModal() {
      // 在關閉模態框時更新選中的貼文
      if (this.selectedPost) {
        const updatedPost = this.posts.find(p => p.postId === this.selectedPost.postId);
        if (updatedPost) {
          this.selectedPost = { ...updatedPost };
        }
      }
      this.selectedPost = null;
    },

    handleImageError(event) {
      event.target.src =
        'http://localhost:8080/meetu/images/default-profile.png';
    },

    formatDate(dateString) {
      const options = {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },

    showDetail(post) {
      this.selectedPost = post;
    },

    updateCommentCount(postId, count) {
  const postIndex = this.posts.findIndex(post => post.postId === postId);
  console.log('Received commentCountUpdated for postId:', postId, 'new count:', count); // 添加調試信息
  if (postIndex !== -1) {
    this.posts[postIndex] = {
      ...this.posts[postIndex],
      commentCount: count,
    };
    console.log('Post updated in list:', this.posts[postIndex]); // 確認數據已更新
  } else {
    console.error('Post not found for updating comment count:', postId); // 調試信息
  }
},

    updateLikeCount({ postId, likeCount, isLiked }) {
      const postIndex = this.posts.findIndex(post => post.postId === postId);
      if (postIndex !== -1) {
        this.posts[postIndex].likeCount = likeCount;
        this.posts[postIndex].isLiked = isLiked;
      }
    },
  },
};
</script>


<style scoped>
.post-wall {
  max-width: 1000px;
  margin: 20px 0 20px 20px;
  padding: 20px;
  background-color: #f0f2f5;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  position: relative;
}

.post-wall-header {
  display: flex;
  flex-direction: column;
  margin-bottom: 30px;
}

.post-wall-header h1 {
  font-size: 24px;
  color: #1a1a1a;
  margin-bottom: 20px;
}

.search-bar {
  width: 100%;
  max-width: 500px;
  margin-bottom: 15px;
}

.search-bar input {
  width: 100%;
  padding: 12px 20px;
  border: none;
  border-radius: 30px;
  font-size: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.search-bar input:focus {
  outline: none;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.2);
}

.filter-dropdown {
  position: absolute;
  top: 20px;
  right: 20px;
}

.filter-dropdown select {
  padding: 10px 20px;
  border: none;
  border-radius: 20px;
  background-color: #ffffff;
  font-size: 16px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.filter-dropdown select:hover {
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.2);
}

.post-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 960px; /* 調整為貼文牆寬度的 95% */
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

.user-name-link {
  text-decoration: none;
  color: inherit;
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
  width: 200px;
  height: 200px;
  border-radius: 10px;
  overflow: hidden;
}

.post-media {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.divider {
  border: none;
  border-top: 1px solid #e0e0e0;
  margin: 15px 0;
  width: 100%;
}

.post-interactions {
  display: flex;
  align-items: center;
  gap: 20px;
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
  font-size: 24px; /* 調整圖標的大小 */
  margin-right: 5px; /* 控制圖標和文字之間的距離 */
  display: inline-flex; /* 使用 inline-flex 使圖標和文本更容易對齊 */
  align-items: center; /* 垂直居中圖標 */
}

.comment-count,
.like-count {
  font-size: 16px; /* 確保文字的大小與圖標一致 */
  line-height: 1; /* 確保文本垂直居中 */
}

.error-message {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 10px;
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

.add-icon {
  font-size: 3rem; 
  color: #86b817;
  text-decoration: none;
}

.add-post-btn {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  z-index: 1000;
  background-color: #86b817; /* 更新為金色 */
  border: none; /* 移除邊框 */
  color:	#f7f5f5; /* 將"+"符號改為淺灰色 */
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
  transition: all 0.3s ease;
}

.add-post-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  background-color: #F0C200; /* 懸停時稍微深一點的金色 */
}

</style>
