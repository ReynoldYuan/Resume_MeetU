<template>
    <div class="container">
        <h1>編輯通知</h1>
        <form @submit.prevent="updateNotification" v-if="notification">
            <div class="mb-3">
            <label class="form-label">接收人</label>
            <select class="form-select" v-model="notification.notificationUserId">
                <option v-for="user in usersList" :key="user.userId" :value="user.userId">
                {{ user.userMail }}
                </option>
            </select>
            </div>
            <div class="mb-3">
            <label class="form-label">通知標題</label>
            <input class="form-control" v-model="notification.notificationTitle" />
            </div>
            <div class="mb-3">
            <label class="form-label">通知內容</label>
            <textarea class="form-control" v-model="notification.notificationContent"></textarea>
            </div>
            <button type="submit" class="btn btn-primary">送出</button>
            <router-link to="/notification/list" class="btn btn-secondary">取消</router-link>
        </form>
    </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import axios from 'axios';
    import instance from '@/plugins/axios';
    
    const route = useRoute();
    const router = useRouter();
    const notification = ref(null);
    const usersList = ref([]);
    
    const fetchNotification = async () => {
        try {
            const url = (`/notification/findNotiById/${route.params.id}`);
            // console.log('Requesting URL:',instance.defaults.baseURL+url);
            const response = await instance.get(url);
            // console.log('API response:', response.data);
            notification.value = response.data;
        } catch (err) {
            console.error('Error fetching notification:', err);
        }
    };


    const fetchUsers = async () => {
        try {
        const response = await instance.get('/notification/users');
        usersList.value = response.data;
        } catch (error) {
        console.error('No result:', error);
        }
    };

    const updateNotification = async () => {
        try {
            let data = JSON.stringify({
            "notificationId": notification.value.notificationId,
            "notificationUserId": notification.value.notificationUserId,
            "notificationTitle": notification.value.notificationTitle,
            "notificationContent": notification.value.notificationContent
            });
    
            await axios.put(
                `http://localhost:8080/meetu/notification/update/${notification.value.notificationId}`,
                data,
                {
                    headers: { 'Content-Type': 'application/json' }
                }
            );

            router.push('/notification/list');
        } catch (error) {
            console.error('Error updating notification:', error);
        }
    }

    onMounted(async () => {
        await fetchUsers();
        await fetchNotification();
    });
</script>