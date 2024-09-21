<template>
  <div class="container-fluid">
    <div class="row">
      <!-- UserSidebar 部分 -->
      <div class="col-12">
        <UserSidebar />
      </div>
    </div>

    <!-- 新增貼文區域 -->
    <div class="row justify-content-center mt-4"> <!-- 添加了 mt-4 類別 -->
      <!-- 中心對齊 -->
      <div class="col-8">
        <!-- 將表單置於更小的區域中，與上方選項區域對齊 -->
        <div class="add-post-container bg-white p-4 rounded shadow-sm">
          <h2>新增貼文</h2>
          <form @submit.prevent="submitPost">
            <!-- 文字內容輸入框 -->
            <div class="form-group">
              <label for="caption">貼文內容</label>
              <textarea
                id="caption"
                v-model="post.caption"
                rows="4"
                placeholder="請輸入貼文內容..."
                class="form-control"
              ></textarea>
            </div>

            <!-- 圖片上傳區域 -->
            <div class="form-group">
              <label for="imageUrl">圖片上傳</label>
              <input
                type="file"
                id="imageUrl"
                @change="handleImageUpload"
                accept="image/*"
                class="form-control-file"
              />
              <!-- 圖片預覽區域 -->
              <div class="preview-container mt-3">
                <img
                  v-for="(image, index) in imagePreviews"
                  :key="index"
                  :src="image"
                  alt="Image Preview"
                  class="preview-image"
                />
              </div>
            </div>

            <!-- 影片上傳區域 -->
            <div class="form-group">
              <label for="videoUrl">影片上傳</label>
              <input
                type="file"
                id="videoUrl"
                @change="handleVideoUpload"
                accept="video/*"
                class="form-control-file"
              />
              <!-- 影片預覽區域 -->
              <div class="preview-container mt-3">
                <video
                  v-for="(video, index) in videoPreviews"
                  :key="index"
                  :src="video"
                  controls
                  class="preview-video"
                ></video>
              </div>
            </div>

            <!-- 提交按鈕 -->
            <button
              type="submit"
              :disabled="isSubmitting"
              class="btn btn-success"
            >
              {{ isSubmitting ? '提交中...' : '新增貼文' }}
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>




<script>

import { ref } from 'vue';
import userStore from '@/stores/user';
import axiosapi from '@/plugins/axios';
import { useRouter } from 'vue-router';
import UserSidebar from "@/components/02-user/UserSidebar.vue";

export default {
  components:{
    UserSidebar
  },
  setup() {
    const user = userStore();
    const post = ref({
      caption: '',
    });
    const imageFiles = ref([]);
    const videoFiles = ref([]);
    const imagePreviews = ref([]);
    const videoPreviews = ref([]);
    const isSubmitting = ref(false);

    const router = useRouter();

    const checkAuthentication = () => {
      if (!user.token) {
        redirectToLogin('用戶未登入，重定向至登入頁面');
        return false;
      }
      return true;
    };

    const redirectToLogin = (message) => {
      console.log(message);
      router.push({ name: 'login' });
    };

    const handleImageUpload = (event) => {
      if (event.target.files.length > 0) {
        imageFiles.value = Array.from(event.target.files).slice(0, 1); // 只允許選擇一個圖片文件
        imagePreviews.value = imageFiles.value.map(file => URL.createObjectURL(file));
      }
    };

    const handleVideoUpload = (event) => {
      if (event.target.files.length > 0) {
        videoFiles.value = Array.from(event.target.files).slice(0, 1); // 只允許選擇一個影片文件
        videoPreviews.value = videoFiles.value.map(file => URL.createObjectURL(file));
      }
    };

    const submitPost = async () => {
      if (!checkAuthentication()) return;

      isSubmitting.value = true;

      try {
        const formData = new FormData();
        formData.append('caption', post.value.caption);

        // 使用正確的字段名稱，並確保只附加一個文件
        if (imageFiles.value.length > 0) {
          formData.append('imageFile', imageFiles.value[0]); // 使用 'imageFile' 作為字段名稱
        }
        if (videoFiles.value.length > 0) {
          formData.append('videoFile', videoFiles.value[0]); // 使用 'videoFile' 作為字段名稱
        }

        let token = user.token;
        if (!token.startsWith("Bearer ")) {
          token = `Bearer ${token}`;
        }

        const response = await axiosapi.post('/api/posts', formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': token
          },
        });

        console.log('提交成功:', response);
        router.push("/my-posts");
      } catch (error) {
        handlePostError(error);
      } finally {
        isSubmitting.value = false;
      }
    };

    const handlePostError = (error) => {
      console.error('提交貼文時發生錯誤:', error);
      if (error.response) {
        console.error('伺服器響應狀態:', error.response.status);
        console.error('伺服器響應內容:', error.response.data);
        if (error.response.status === 401) {
          alert('登入已過期，請重新登入');
          router.push({ name: 'login' });
        } else {
          alert(`提交貼文失敗: ${error.response.data}`);
        }
      } else if (error.request) {
        console.error('未收到伺服器響應:', error.request);
        alert('無法連接到伺服器，請檢查網絡連接');
      } else {
        console.error('請求設置錯誤:', error.message);
        alert('提交貼文時發生未知錯誤。');
      }
    };

    return {
      post,
      imageFiles,
      videoFiles,
      imagePreviews,
      videoPreviews,
      isSubmitting,
      submitPost,
      handleImageUpload,
      handleVideoUpload,
    };
  }
};
</script>


<style scoped>
.add-post-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

textarea,
input[type="file"] {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

button {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #45a049;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.preview-container {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.preview-image, .preview-video {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.container-fluid {
    padding-top: 20px;
}

</style>

