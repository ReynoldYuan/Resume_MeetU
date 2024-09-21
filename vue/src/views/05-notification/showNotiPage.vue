<template>
    <div class="container">
    <h1>我的通知
        <!-- <RouterLink :to="{ name: 'addNotiPage-link' }"><font-awesome-icon :icon="['fas', 'file-circle-plus']" style="color: #86b817;" /></RouterLink> -->
    </h1>
    <div v-if="notiList.length === 0">目前沒有通知</div>
    <div class="accordion" id="accordionExample">
        <div v-for="(oneNoti, index) in notiList" :key="oneNoti.notificationId" class="accordion-item">
        <h2 class="accordion-header" :id="'heading' + index">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                    :data-bs-target="'#collapse' + index" aria-expanded="false" :aria-controls="'collapse' + index">
            <!-- <span class="me-5">收件人: {{ oneNoti.userName }}</span> -->
            <span class="me-5">標題: {{ oneNoti.notificationTitle }}</span>
            <span>通知時間: {{ formatDate(oneNoti.notificationTime) }}</span>
            </button>
        </h2>
        <div :id="'collapse' + index" class="accordion-collapse collapse"
                :aria-labelledby="'heading' + index" data-bs-parent="#accordionExample">
            <div class="accordion-body position-relative">
            <p>{{ oneNoti.notificationContent }}</p>
            <!-- <div class="button-container">
                <router-link :to="{ name: 'editNotiPage-link', params: { id: oneNoti.notificationId } }" class="btn btn-outline-secondary btn-sm">編輯</router-link>
                <button @click="deleteNoti(oneNoti.notificationId)" class="btn btn-outline-danger btn-sm">刪除</button>
            </div> -->
            </div>
        </div>
        </div>
    </div>
    </div>    
</template>

<script setup>
import { ref, onMounted } from 'vue';
import instance from '@/plugins/axios';
import useUserStore from "@/stores/user.js";

const notiList = ref([]);
const userStore = useUserStore();


// const fetchNotifications = async () => {
//     try {
//         const response = await instance.get('/notification/list');
//         notiList.value = response.data.sort((a, b) => new Date(b.notificationTime) - new Date(a.notificationTime));
//     } catch (error) {
//         console.error('獲取通知時出錯:', error);
//     }
// };

const fetchNotifications = async () => {
    if (!userStore.isLogedin || !userStore.id) {
        console.log("用戶未登錄");
        return;
    }
    try {
        const response = await instance.get(`/secure/notification/list/${userStore.id}`, {
            headers: {
                'Authorization': userStore.token
            }
        });
        notiList.value = response.data.sort((a, b) => new Date(b.notificationTime) - new Date(a.notificationTime));
    } catch (err) {
        console.error('獲取通知時出錯:', err);
    }
};

const deleteNoti = async (id) => {
    try {
        await instance.delete(`/notification/delete/${id}`);
        await fetchNotifications();
    } catch (error) {
        console.error('Error deleting notification:', error);
    }
};

const formatDate = (date) => {
    return new Date(date).toLocaleString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        weekday: 'long'
    });
};

onMounted(fetchNotifications);

</script>