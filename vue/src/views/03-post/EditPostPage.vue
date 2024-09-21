<template>
  <div class="edit-post-container">
    <h1>編輯貼文</h1>
    <form @submit.prevent="submitEdit">
      <div class="form-group">
        <label for="caption">內容:</label>
        <textarea v-model="post.caption" id="caption" rows="4" required></textarea>
      </div>

      <div class="media-preview">
        <div class="media-preview-item" v-if="post.imageUrl || updatedImage">
          <img v-if="post.imageUrl && !updatedImage" :src="`http://localhost:8080/meetu${post.imageUrl}`" alt="Current Image" />
          <img v-else-if="updatedImage" :src="imagePreview" alt="New Image Preview" />
          <button class="remove-button" @click.prevent="removeImage">刪除圖片</button>
        </div>
        <div class="media-preview-item" v-if="post.videoUrl || updatedVideo">
          <video v-if="post.videoUrl && !updatedVideo" :src="`http://localhost:8080/meetu${post.videoUrl}`" controls></video>
          <video v-else-if="updatedVideo" :src="videoPreview" controls></video>
          <button class="remove-button" @click.prevent="removeVideo">刪除影片</button>
        </div>
      </div>

      <div class="form-group">
        <label for="imageFile">更新圖片:</label>
        <input type="file" @change="handleImageChange" id="imageFile" accept="image/*" />
      </div>
      <div class="form-group">
        <label for="videoFile">更新影片:</label>
        <input type="file" @change="handleVideoChange" id="videoFile" accept="video/*" />
      </div>

      <div class="post-info">
        <p>創建時間: {{ formatDate(post.postCreatedAt) }}</p>
        <p>上次更新: {{ formatDate(post.postUpdatedAt) }}</p>
      </div>

      <div class="form-actions">
        <button type="submit">保存更改</button>
        <button type="button" @click="cancelEdit">取消</button>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axiosapi from '@/plugins/axios';
import userStore from '@/stores/user';
import Swal from 'sweetalert2';

export default {
  setup() {
    const router = useRouter();
    const route = useRoute();
    const user = userStore();

    const post = ref({
      postId: null,
      caption: '',
      imageUrl: '',
      videoUrl: '',
      postCreatedAt: '',
      postUpdatedAt: ''
    });

    const updatedImage = ref(null);
    const updatedVideo = ref(null);
    const imagePreview = ref(null);
    const videoPreview = ref(null);
    const deleteImage = ref(false); // 用於標記是否刪除圖片
    const deleteVideo = ref(false); // 用於標記是否刪除影片
    const isLoading = ref(false); // 用於顯示加載狀態

    onMounted(() => {
      const postId = route.params.id;
      if (postId) {
        post.value.postId = postId;
        fetchPostData(postId);
      } else {
        console.error('無效的 postId');
      }
    });

    const fetchPostData = async (postId) => {
      try {
        const response = await axiosapi.get(`/api/posts/${postId}`, {
          headers: {
            'Authorization': user.token,
          },
        });
        post.value = response.data;
      } catch (error) {
        console.error('獲取貼文數據時出錯:', error.message);
      }
    };

    const handleImageChange = (event) => {
      updatedImage.value = event.target.files[0];
      imagePreview.value = URL.createObjectURL(updatedImage.value);
      deleteImage.value = false; // 如果用戶選擇了新圖片，則重置刪除標記
    };

    const handleVideoChange = (event) => {
      updatedVideo.value = event.target.files[0];
      videoPreview.value = URL.createObjectURL(updatedVideo.value);
      deleteVideo.value = false; // 如果用戶選擇了新影片，則重置刪除標記
    };

    const submitEdit = async () => {
      if (!post.value.postId) {
        console.error('貼文 ID 為 null，無法更新貼文。');
        return;
      }

      isLoading.value = true; // 開始加載狀態
      const formData = new FormData();
      formData.append('caption', post.value.caption);

      if (updatedImage.value) {
        formData.append('imageFile', updatedImage.value);
      } else if (deleteImage.value) {
        formData.append('removeImage', 'true'); // 通知後端刪除圖片
      }

      if (updatedVideo.value) {
        formData.append('videoFile', updatedVideo.value);
      } else if (deleteVideo.value) {
        formData.append('removeVideo', 'true'); // 通知後端刪除影片
      }

      try {
        const response = await axiosapi.put(`/api/posts/${post.value.postId}`, formData, {
          headers: {
            'Authorization': user.token,
          },
        });

        console.log('貼文更新成功:', response.data);

        // 更新前端的 postUpdatedAt
        post.value.postUpdatedAt = new Date().toISOString();

        Swal.fire({
          title: '編輯成功',
          text: '貼文已更新。',
          icon: 'success',
          timer: 1500,
          showConfirmButton: false
        });

        router.push({ name: 'myPosts-link' });
      } catch (error) {
        console.error('更新貼文時出錯:', error.message);

        Swal.fire({
          title: '編輯失敗',
          text: '無法更新貼文，請稍後再試。',
          icon: 'error',
          timer: 1500,
          showConfirmButton: false
        });
      } finally {
        isLoading.value = false; // 結束加載狀態
      }
    };

    const removeImage = () => {
      if (post.value.imageUrl || updatedImage.value) {
        post.value.imageUrl = null;
        updatedImage.value = null;
        imagePreview.value = null;
        deleteImage.value = true; // 設置刪除標記
        Swal.fire({
          title: '圖片已標記刪除',
          text: '編輯時將刪除圖片。',
          icon: 'info',
          timer: 1500,
          showConfirmButton: false
        });
      }
    };

    const removeVideo = () => {
      if (post.value.videoUrl || updatedVideo.value) {
        post.value.videoUrl = null;
        updatedVideo.value = null;
        videoPreview.value = null;
        deleteVideo.value = true; // 設置刪除標記
        Swal.fire({
          title: '影片已標記刪除',
          text: '編輯時將刪除影片。',
          icon: 'info',
          timer: 1500,
          showConfirmButton: false
        });
      }
    };

    const cancelEdit = () => {
      router.push({ name: 'myPosts-link' });
    };

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A';
      const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(dateString).toLocaleDateString(undefined, options);
    };

    return {
      post,
      updatedImage,
      updatedVideo,
      imagePreview,
      videoPreview,
      handleImageChange,
      handleVideoChange,
      submitEdit,
      cancelEdit,
      removeImage,
      removeVideo,
      formatDate,
      deleteImage, // 返回刪除標記
      deleteVideo, // 返回刪除標記
      isLoading, // 加載狀態
    };
  }
};
</script>



<style scoped>
.edit-post-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input[type="text"],
.form-group textarea,
.form-group input[type="file"] {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.media-preview {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.media-preview-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.media-preview-item img,
.media-preview-item video {
  max-width: 200px;
  max-height: 200px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.remove-button {
  background-color: #ff6b6b;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 5px;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: background-color 0.3s, box-shadow 0.3s;
}

.remove-button:hover {
  background-color: #e74c3c;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.form-actions {
  display: flex;
  justify-content: space-between;
}

.form-actions button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.form-actions button[type="button"] {
  background-color: #6c757d;
}

.form-actions button:hover {
  background-color: #0056b3;
}

.form-actions button[type="button"]:hover {
  background-color: #5a6268;
}

.post-info {
  background-color: #e9ecef;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 20px;
}

.post-info p {
  margin: 5px 0;
  font-size: 0.9em;
  color: #495057;
}
</style>