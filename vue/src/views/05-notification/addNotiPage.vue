<template>
    <div class="container">
        <h1>新增通知</h1>
        <div class="card">
            <div class="card-header">請填入新訊息</div>
            <div class="card-body">
            <form @submit.prevent="addNotification">
                <div class="mb-3">
                <label class="form-label">接收人</label>
                <select class="form-select" v-model="notification.notificationUserId">
                    <option value="">請選擇接收人</option>
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
            </form>
            </div>
        </div>
    </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue';
    import { useRouter } from 'vue-router';
    import instance from '@/plugins/axios';
    
    const router = useRouter();
    
    const notification = reactive({
        notificationUserId: '',
        notificationTitle: '',
        notificationContent: ''
    });
    
    const usersList = ref([]);
    
    const addNotification = async () => {
        try {
            const formData = new FormData();
            formData.append('notificationUserId', notification.notificationUserId);
            formData.append('notificationTitle', notification.notificationTitle);
            formData.append('notificationContent', notification.notificationContent);
        
            await instance.post('/notification/add', formData);
            router.push('/notification/list');
        } catch (error) {
            console.error('Error adding notification:', error);
        }
    };
    
    const fetchUsers = async () => {
        try {
            const { data } = await instance.get('/notification/users');
            usersList.value = data;
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };
    
    onMounted(fetchUsers);
</script>