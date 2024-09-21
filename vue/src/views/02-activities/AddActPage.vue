<template>
  <div class="container-fluid">
        <div class="col-12">
            <UserSidebar />
        </div>

  <div class="container py-5">
    <h1 class="text-center mb-5">創建活動</h1>
    <div class="card shadow">
      <div class="card-header custom-green text-white">
        <h3 class="mb-0">請填入活動訊息</h3>
        
      </div>
      <button @click="doInput">一鍵輸入</button>

      <div class="card-body position-relative">
        <small class="form-label required-fields-note">(*為必填項目)</small>
        <form @submit.prevent="submitActivity" novalidate>
          <div class="row">
            <div class="col-md-6 mb-3">
              <div><label class="form-label">活動類型*</label></div>
              <select class="form-select" v-model="activity.activitiesType" required>
                <option value="">請選擇活動類型</option>
                <option value="I">室內</option>
                <option value="O">戶外</option>
                <option value="M">混合</option>
              </select>
              <div class="invalid-feedback" v-if="errors.activitiesType">{{ errors.activitiesType }}</div>
            </div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">活動標題*</label>
              <input class="form-control" v-model="activity.activitiesTitle" required />
              <div class="invalid-feedback" v-if="errors.activitiesTitle">{{ errors.activitiesTitle }}</div>
            </div>
          

          <div class="mb-3">
            <label class="form-label">活動圖片*</label>
            <input type="file" class="form-control" @change="handleFileUpload" required />
            <div v-if="activity.activitiesPicsPreview" class="mt-2">
              <img :src="activity.activitiesPicsPreview" alt="活動圖片預覽" class="img-thumbnail" style="max-width: 200px;" />
            </div>
            <div class="invalid-feedback" v-if="errors.activitiesPics">{{ errors.activitiesPics }}</div>
          </div>

          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">活動開始日期* <small class="text-muted">(24小時後)</small></label>
              <input type="datetime-local" class="form-control" v-model="activity.activitiesStartDate" :min="minStartDate" required />
              <div class="invalid-feedback" v-if="errors.activitiesStartDate">{{ errors.activitiesStartDate }}</div>
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">活動結束日期* <small class="text-muted">(開始後1小時)</small></label>
              <input type="datetime-local" class="form-control" v-model="activity.activitiesEndDate" :min="minEndDate" required />
              <div class="invalid-feedback" v-if="errors.activitiesEndDate">{{ errors.activitiesEndDate }}</div>
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">審核日期* <small class="text-muted">(開始前1天)</small></label>
              <input type="datetime-local" class="form-control" v-model="activity.activitiesVerifyDate" :min="minVerifyDate" :max="maxVerifyDate" required />
              <div class="invalid-feedback" v-if="errors.activitiesVerifyDate">{{ errors.activitiesVerifyDate }}</div>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">活動地點*</label>
            <input class="form-control" v-model="activity.activitiesLocation" required />
            <div class="invalid-feedback" v-if="errors.activitiesLocation">{{ errors.activitiesLocation }}</div>
          </div>

          <div class="mb-3">
            <label class="form-label">活動內容*</label>
            <textarea class="form-control" v-model="activity.activitiesContent" rows="4" required></textarea>
            <div class="invalid-feedback" v-if="errors.activitiesContent">{{ errors.activitiesContent }}</div>
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
              <div class="invalid-feedback" v-if="errors.activitiesSharing">{{ errors.activitiesSharing }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">活動金額(免費也幫我填"0"哦)</label>
              <input type="number" class="form-control" v-model="activity.activitiesAmt" required />
              <div class="invalid-feedback" v-if="errors.activitiesAmt">{{ errors.activitiesAmt }}</div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">最大參加人數*</label>
              <input type="number" class="form-control" v-model="activity.activitiesMaxPeo" required />
              <div class="invalid-feedback" v-if="errors.activitiesMaxPeo">{{ errors.activitiesMaxPeo }}</div>
            </div>
          </div>

          <div class="text-center mt-4">
            <button type="submit" class="btn custom-green-btn btn-lg">新增活動</button>
          </div>
        </form>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import axiosapi from '@/plugins/axios';
import useUserStore from "@/stores/user.js"
import Swal from 'sweetalert2';
import UserSidebar from '@/components/02-user/UserSidebar.vue';

const userStore = useUserStore();
const router = useRouter();

const activity = reactive({
  activitiesType: '',
  activitiesPics: null,
  activitiesPicsPreview: '', // 圖片預覽
  activitiesTitle: '',
  activitiesStartDate: '',
  activitiesEndDate: '',
  activitiesLocation: '',
  activitiesVerifyDate: '',
  activitiesContent: '',
  activitiesSharing: '',
  activitiesAmt: 0,
  activitiesMaxPeo: 0
});

const errors = reactive({});

// 計算屬性：最小開始日期（當前時間後24小時）
const minStartDate = computed(() => {
  const date = new Date();
  date.setHours(date.getHours() + 24);
  return date.toISOString().slice(0, 16);
});

// 計算屬性：最小結束日期（開始時間後1小時）
const minEndDate = computed(() => {
  if (!activity.activitiesStartDate) return '';
  const date = new Date(activity.activitiesStartDate);
  date.setHours(date.getHours() + 1);
  return date.toISOString().slice(0, 16);
});

// 計算屬性：最大審核日期（開始時間前1天）
const maxVerifyDate = computed(() => {
  if (!activity.activitiesStartDate) return '';
  const date = new Date(activity.activitiesStartDate);
  date.setDate(date.getDate() - 1);
  return date.toISOString().slice(0, 16);
});

// 計算屬性：最小審核日期（當前時間）
const minVerifyDate = computed(() => {
  return new Date().toISOString().slice(0, 16);
});


// 監聽開始日期的變化
watch(() => activity.activitiesStartDate, (newStartDate) => {
  if (newStartDate) {
    const startDate = new Date(newStartDate);
    const currentDate = new Date();

    // 確保開始日期至少在當前時間24小時後
    if (startDate < new Date(currentDate.getTime() + 24 * 60 * 60 * 1000)) {
      activity.activitiesStartDate = minStartDate.value;
      errors.activitiesStartDate = '開始日期必須至少在當前時間24小時後';
    } else {
      errors.activitiesStartDate = '';
    }

    // 確保結束日期至少在開始日期後1小時
    if (activity.activitiesEndDate) {
      const endDate = new Date(activity.activitiesEndDate);
      if (endDate <= new Date(startDate.getTime() + 60 * 60 * 1000)) {
        activity.activitiesEndDate = minEndDate.value;
        errors.activitiesEndDate = '結束日期必須至少在開始日期1小時後';
      } else {
        errors.activitiesEndDate = '';
      }
    }

    // 確保審核日期在開始日期之前至少1天
    if (activity.activitiesVerifyDate) {
      const verifyDate = new Date(activity.activitiesVerifyDate);
      if (verifyDate >= new Date(startDate.getTime() - 24 * 60 * 60 * 1000)) {
        activity.activitiesVerifyDate = maxVerifyDate.value;
        errors.activitiesVerifyDate = '審核日期必須至少在開始日期1天前';
      } else {
        errors.activitiesVerifyDate = '';
      }
    }
  }
});

// 監聽結束日期的變化
watch(() => activity.activitiesEndDate, (newEndDate) => {
  if (newEndDate && activity.activitiesStartDate) {
    const startDate = new Date(activity.activitiesStartDate);
    const endDate = new Date(newEndDate);

    if (endDate <= new Date(startDate.getTime() + 60 * 60 * 1000)) {
      errors.activitiesEndDate = '結束日期必須至少在開始日期1小時後';
    } else {
      errors.activitiesEndDate = '';
    }
  }
});

// 監聽審核日期的變化
watch(() => activity.activitiesVerifyDate, (newVerifyDate) => {
  if (newVerifyDate && activity.activitiesStartDate) {
    const startDate = new Date(activity.activitiesStartDate);
    const verifyDate = new Date(newVerifyDate);

    if (verifyDate >= new Date(startDate.getTime() - 24 * 60 * 60 * 1000)) {
      errors.activitiesVerifyDate = '審核日期必須至少在開始日期1天前';
    } else {
      errors.activitiesVerifyDate = '';
    }
  }
});

// 圖片上傳生成預覽
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  activity.activitiesPics = file;

  // 圖片預覽網址
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

const validateForm = () => {
  errors.activitiesType = activity.activitiesType ? '' : '請選擇活動類型';
  errors.activitiesTitle = activity.activitiesTitle ? '' : '請輸入活動標題';
  errors.activitiesPics = activity.activitiesPics ? '' : '請上傳活動圖片';
  errors.activitiesStartDate = activity.activitiesStartDate ? '' : '請選擇活動開始日期';
  errors.activitiesEndDate = activity.activitiesEndDate ? '' : '請選擇活動結束日期';
  errors.activitiesLocation = activity.activitiesLocation ? '' : '請輸入活動地點';
  errors.activitiesVerifyDate = activity.activitiesVerifyDate ? '' : '請選擇審核日期';
  errors.activitiesContent = activity.activitiesContent ? '' : '請輸入活動內容';
  errors.activitiesSharing = activity.activitiesSharing ? '' : '請選擇分攤方式';
  errors.activitiesAmt = activity.activitiesAmt !== null ? '' : '請輸入活動金額(免費填0)';
  errors.activitiesMaxPeo = activity.activitiesMaxPeo ? '' : '請輸入最大參加人數';

  return Object.values(errors).every(error => error === '');
};



const submitActivity = async () => {
  if (!validateForm()) {
    await Swal.fire({
      title: '表單驗證失敗',
      text: '請檢查並填寫所有必要的欄位。',
      icon: 'error',
      confirmButtonText: '確定'
    });
    return;
  }
  try {
    const formData = new FormData();
    formData.append('activitiesType', activity.activitiesType);
    formData.append('activitiesPics', activity.activitiesPics);
    formData.append('activitiesTitle', activity.activitiesTitle);
    formData.append('activitiesStartDate', activity.activitiesStartDate);
    formData.append('activitiesEndDate', activity.activitiesEndDate);
    formData.append('activitiesLocation', activity.activitiesLocation);
    formData.append('activitiesVerifyDate', activity.activitiesVerifyDate);
    formData.append('activitiesContent', activity.activitiesContent);
    formData.append('activitiesSharing', activity.activitiesSharing);
    formData.append('activitiesAmt', activity.activitiesAmt);
    formData.append('activitiesMaxPeo', activity.activitiesMaxPeo);

    const response = await axiosapi.post('/activities', formData, {
      headers: {
        'Authorization': userStore.token, 
        'Content-Type': 'multipart/form-data'
      }
    });

    console.log('Activity submitted successfully:', response.data);
    await Swal.fire({
      title: '成功',
      text: '成功創建活動',
      icon: 'success',
      confirmButtonText: '確定'
    });
    router.push('/activitieslist'); 
  } catch (error) {
    console.error('Error submitting activity:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
      console.error('Response status:', error.response.status);
    }
    await Swal.fire({
      title: '錯誤',
      text: '創建活動失敗',
      icon: 'error',
      confirmButtonText: '確定'
    });
  }
};

function doInput() {
  activity.activitiesType = 'I';
  activity.activitiesTitle = '伯虎點秋香大聯誼!!!!!!';
  activity.activitiesStartDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().slice(0, 16);
  activity.activitiesEndDate = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000 + 3 * 60 * 60 * 1000).toISOString().slice(0, 16);
  activity.activitiesVerifyDate = new Date(Date.now() + 6 * 24 * 60 * 60 * 1000).toISOString().slice(0, 16);
  activity.activitiesLocation = '桃花庵情緣閣';
  activity.activitiesContent = '唐伯虎親自主持！秋香都點名參加的聯誼盛宴！';
  activity.activitiesSharing = 'S';
  activity.activitiesAmt = 200;
  activity.activitiesMaxPeo = 8;
}
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
.invalid-feedback {
  display: block;
  color: #dc3545;
}
.required-fields-note {
  position: absolute;
  top: 10px;
  right: 15px;
  color: red;
}

.container-fluid {
    padding-top: 20px;
}
</style>