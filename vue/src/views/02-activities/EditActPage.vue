<template>
  <div class="container py-5">
    <h1 class="text-center mb-5">更新活動</h1>
    <div class="card shadow">
      <div class="card-header custom-green text-white">
        <h3 class="mb-0">請修改活動訊息</h3>
      </div>

      <div class="card-body position-relative">
        <small class="form-label required-fields-note">(*為必填項目)</small>
        <form @submit.prevent="updateActivity" novalidate>
          <div class="row">
            <div class="col-md-6 mb-3">
              <div><label class="form-label">活動類型*</label></div>
              <select class="form-select" v-model="activity.activitiesType" required>
                <option value="">請選擇活動類型</option>
                <option value="I">室內</option>
                <option value="O">戶外</option>
                <option value="M">混合</option>
              </select>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">活動標題*</label>
              <input class="form-control" v-model="activity.activitiesTitle" required />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">活動圖片*</label>
            <input type="file" class="form-control" @change="handleFileUpload" />
            <div v-if="activity.activitiesPicsPreview" class="mt-2">
              <img :src="activity.activitiesPicsPreview" alt="活動圖片預覽" class="img-thumbnail" style="max-width: 200px;" />
            </div>
          </div>

          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">活動開始日期* <small class="text-muted">(24小時後)</small></label>
              <input type="datetime-local" class="form-control" v-model="activity.activitiesStartDate" :min="minDateTime" required />
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">活動結束日期* <small class="text-muted">(開始後1小時)</small></label>
              <input type="datetime-local" class="form-control" v-model="activity.activitiesEndDate" :min="activity.activitiesStartDate" required />
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">審核日期* <small class="text-muted">(開始前1天)</small></label>
              <input type="datetime-local" class="form-control" v-model="activity.activitiesVerifyDate" :min="minVerifyDateTime" :max="maxVerifyDateTime" required />
            </div>
          </div>

          <!-- 其他表單欄位 -->
          <div class="mb-3">
            <label class="form-label">活動地點*</label>
            <input class="form-control" v-model="activity.activitiesLocation" required />
          </div>

          <div class="mb-3">
            <label class="form-label">活動內容*</label>
            <textarea class="form-control" v-model="activity.activitiesContent" rows="4" required></textarea>
          </div>

          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">分攤方式*</label>
              <select class="form-select" v-model="activity.activitiesSharing" required>
                <option value="">請選擇分攤方式</option>
                <option value="I">我請客</option>
                <option value="Y">你付錢</option>
                <option value="S">各付各</option>
              </select>
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">活動金額*(免費填0)</label>
              <input type="number" class="form-control" v-model="activity.activitiesAmt" required />
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">最大參加人數*</label>
              <input type="number" class="form-control" v-model="activity.activitiesMaxPeo" required />
            </div>
          </div>

          <div class="text-center mt-4">
            <button type="submit" class="btn custom-green-btn btn-lg">更新活動</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import instance from '@/plugins/axios';
import  useUserStore  from "@/stores/user.js"; 
import Swal from 'sweetalert2';

const userStore = useUserStore();

const router = useRouter();
const route = useRoute();

const activity = reactive({
  activitiesType: '',
  activitiesTitle: '',
  activitiesStartDate: '',
  activitiesEndDate: '',
  activitiesLocation: '',
  activitiesVerifyDate: '',
  activitiesContent: '',
  activitiesSharing: '',
  activitiesAmt: '',
  activitiesMaxPeo: '',
  activitiesPics: null,
  activitiesPicsPreview: ''
});

const minDateTime = ref('');
const minVerifyDateTime = ref('');
const maxVerifyDateTime = ref('');

onMounted(() => {
  const now = new Date();
  minDateTime.value = now.toISOString().slice(0, 16);
  minVerifyDateTime.value = now.toISOString().slice(0, 16);
  fetchActivity();
});

watch(() => activity.activitiesStartDate, (newStartDate) => {
  if (newStartDate) {
    const startDate = new Date(newStartDate);
    maxVerifyDateTime.value = new Date(startDate.getTime() - 60000).toISOString().slice(0, 16);
  } else {
    maxVerifyDateTime.value = minDateTime.value;
  }
});

// 保留原有的圖片獲取方法
const fetchActivityImage = async (aid) => {
  try {
    const response = await instance.get(`/photos/download/${aid}`, { responseType: 'blob' });
    activity.activitiesPicsPreview = URL.createObjectURL(response.data);
  } catch (error) {
    console.error(`Error fetching image for activity ${aid}:`, error);
  }
};

const fetchActivity = async () => {
  try {
    const { data } = await instance.get(`/activities/${route.params.activitiesId}`);
    console.log('Fetched Data:', data);

    // 處理日期格式
    ['activitiesStartDate', 'activitiesEndDate', 'activitiesVerifyDate'].forEach(dateField => {
      if (data.activity[dateField]) {
        data.activity[dateField] = data.activity[dateField].replace(' ', 'T');
      }
    });

    Object.assign(activity, data.activity);

    // 呼叫 fetchActivityImage 來抓取圖片
    if (data.activity.activitiesPics) {
      await fetchActivityImage(route.params.activitiesId);
    }
  } catch (error) {
    console.error('Error fetching activity:', error);
  }
};

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  activity.activitiesPics = file;

  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      activity.activitiesPicsPreview = e.target.result;
    };
    reader.readAsDataURL(file);
  } else {
    activity.activitiesPicsPreview = '';
  }
};

const updateActivity = async () => {
  try {
    const formData = new FormData();
    formData.append('activitiesType', activity.activitiesType);
    formData.append('activitiesTitle', activity.activitiesTitle);
    formData.append('activitiesStartDate', activity.activitiesStartDate);
    formData.append('activitiesEndDate', activity.activitiesEndDate);
    formData.append('activitiesLocation', activity.activitiesLocation);
    formData.append('activitiesVerifyDate', activity.activitiesVerifyDate);
    formData.append('activitiesContent', activity.activitiesContent);
    formData.append('activitiesSharing', activity.activitiesSharing);
    formData.append('activitiesAmt', activity.activitiesAmt);
    formData.append('activitiesMaxPeo', activity.activitiesMaxPeo);

    // 只有當用戶選擇了新的照片時，才將照片添加到 FormData 中
    if (activity.activitiesPics instanceof File) {
      formData.append('activitiesPics', activity.activitiesPics);
    } else {
      // 如果用戶沒有選擇新照片，我們發送一個標記告訴後端保留原有照片
      formData.append('keepOriginalPic', 'true');
    }

    const response = await instance.put(`/activities/${route.params.activitiesId}`, formData, {
      headers: {
        'Authorization': userStore.token,
        'Content-Type': 'multipart/form-data'
      }
    });

    console.log('Activity updated successfully:', response.data);
    await Swal.fire({
      title: '成功',
      text: '活動更新成功',
      icon: 'success',
      confirmButtonText: '確定'
    });
    router.push('/usersActList');
  } catch (error) {
    console.error('Error updating activity:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
      console.error('Response status:', error.response.status);
    }
    await Swal.fire({
      title: '錯誤',
      text: '更新活動失敗',
      icon: 'error',
      confirmButtonText: '確定'
    });
  }
};
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: auto;
}
.card {
  border: none;
  border-radius: 15px;
}
.card-header {
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
}
.form-label {
  font-weight: bold;
}
.custom-green {
  background-color: #8cc63f;
}
.custom-green-btn {
  background-color: #8cc63f;
  border-color: #8cc63f;
  color: white;
}
.custom-green-btn:hover {
  background-color: #7ab33a;
  border-color: #7ab33a;
}
.required-fields-note {
  position: absolute;
  top: 10px;
  right: 15px;
  color: red;
}
.img-thumbnail {
  max-width: 100%;
  height: auto;
}
</style>