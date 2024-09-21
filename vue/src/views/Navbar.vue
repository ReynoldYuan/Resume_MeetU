<!-- Navbar.vue -->
<template>

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Nunito:ital,wght@0,700;1,700&display=swap" rel="stylesheet">
<!-- <link href="/src/assets/images/meetU.png"> -->

    <div class="container-fluid position-relative p-0">
        <nav class="navbar navbar-expand-lg navbar-light px-4 px-lg-5 py-3 py-lg-0 fixed-top">
            <RouterLink to="/" class="navbar-brand p-0">
                <!-- <h1 class="text-primary m-0"><font-awesome-icon :icon="['fas', 'shield-cat']" /> MeetU</h1> -->
                <h1 class="text-primary m-0"><font-awesome-icon icon="champagne-glasses" style="color: #86b817;" /> MeetU</h1>
            </RouterLink>
            <button class="navbar-toggler" type="button" @click="toggleNavbar">
                <span class="fa fa-bars"></span>
            </button>
            <div :class="['collapse', 'navbar-collapse', { 'show': isNavbarOpen }]" id="navbarCollapse">
                <div class="navbar-nav ms-auto py-0">
                    <RouterLink v-for="item in navItems" :key="item.path" :to="item.path" class="nav-item nav-link" active-class="active">
                        {{ item.name }}
                    </RouterLink>

                    <!-- 待處理登入後才顯示 -->
                    <template v-if="userStore.isLogedin">
                        <div class="nav-item dropdown" @mouseenter="showNotifications" @mouseleave="hideNotifications">
                            <a href="#" class="nav-link dropdown-toggle position-relative">
                                <!-- <font-awesome-icon :icon="['fas', 'bell']" />
                                <span class="ts-badge">!</span> -->
                                <span class="fa-layers fa-fw">
                                    <font-awesome-icon :icon="['fas', 'bell']" />
                                    <span v-if="unreadNotificationsCount > 0" class="fa-layers-counter" style="background:orangered">{{ unreadNotificationsCount }}</span>
                                </span>
                            </a>
                            <div :class="['dropdown-menu', 'dropdown-menu-end', 'bg-light', 'border-0', 'rounded-0', 'rounded-bottom', 'm-0', { 'show': isNotificationsVisible }]">
                                <template v-if="latestNotifications.length === 0">
                                    <a href="#" class="dropdown-item">暫無通知</a>
                                </template>
                                <template v-else>
                                    <!-- <a v-for="noti in latestNotifications" :key="noti.notificationId" :to="{ name: 'oneNotiPage-link', params: { nid: noti.notificationId } }" class="dropdown-item notification-item"> -->
                                    <!-- <a v-for="noti in latestNotifications" :key="noti.notificationId" href="#" class="dropdown-item notification-item" @click.prevent="nextNotification(noti.notificationId)"> -->
                                        <a v-for="noti in latestNotifications" :key="noti.notificationId" href="#" class="dropdown-item notification-item" :class="{ 'unread': noti.notificationRead === '0' }" @click.prevent="handleNotificationClick(noti)">
                                        <!-- <RouterLink :to="{ name: 'oneNotiPage-link' }" style="text-decoration: none;"> -->
                                            <div class="notification-content">
                                                <span class="notification-title">{{ noti.notificationTitle }}</span>
                                                <span class="notification-time">{{ formatDate(noti.notificationTime) }}</span>
                                            </div>
                                        <!-- </RouterLink> -->
                                    </a>
                                    <hr class="dropdown-divider">
                                </template>
                                <RouterLink to="/notification/list" class="dropdown-item text-center">查看所有通知</RouterLink>
                            </div>
                        </div>

                        <RouterLink :to="loggedInItems[0].path" class="nav-item nav-link" active-class="active">
                            <font-awesome-icon :icon="loggedInItems[0].icon" />
                        </RouterLink>
                    </template>
                </div>
                <div class="nav-item dropdown" @mouseenter="showDropdown" @mouseleave="hideDropdown" v-if="userStore.isLogedin">
                    <a href="#" class="nav-link dropdown-toggle">
                        <img class="avatar-image rounded-circle me-lg-2" :src="userStore.pics" alt="">
                        <span class="d-none d-lg-inline-flex">{{ userStore.name }}</span>
                    </a>
                    <div :class="['dropdown-menu', 'm-0', { 'show': isDropdownVisible }]">
                        <RouterLink :to="loggedInItems[1].path" class="dropdown-item">個人資料</RouterLink>
                        <button class="dropdown-item" @click="logout">Log Out</button>
                    </div>
                </div>
                <RouterLink to="/login" class="btn btn-primary rounded-pill py-2 px-4" v-show="!userStore.isLogedin" v-else>
                    登入
                </RouterLink>

            </div>
        </nav>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import '@/assets/styles/styles.css';
import useUserStore from "@/stores/user.js"
import Swal from 'sweetalert2';
import { useRouter } from 'vue-router';
import instance from '@/plugins/axios.js';

const userStore = useUserStore();
const router = useRouter();

const isDropdownVisible = ref(false);
const isNavbarOpen = ref(false);
const forceUpdate = ref(0);
const isNotificationsVisible = ref(false);
const latestNotifications = ref([]);
const unreadNotificationsCount = ref(0); //用於未讀通知數量

const navItems = [
    { name: '首頁', path: '/' },
    { name: '覓友教學', path: '/aboutus' },
    { name: '每日配對', path: '/dailymatching' },
    { name: '活動探索', path: '/activitieslist' },
    { name: '貼文牆', path: '/postlist' },
    
];

const loggedInItems = computed(() => [
    
    { icon: ['fas', 'comments'], path: '/chatroom' },
    { icon: ['fas', 'user'], path: { name: 'myprofile-link', params: { userId: userStore.id } } },
    
]);

watch(() => userStore.id, (newId, oldId) => {
    if (newId !== oldId) {
        // 強制組件重新渲染
        forceUpdate.value += 1;
    }
});

const dropdownItems = [
    { name: '室內', path: '/indoor' },
    { name: '戶外', path: '/outdoor' },
    { name: '混合', path: '/mixed' },
];

const showDropdown = () => {
    isDropdownVisible.value = true;
};

const hideDropdown = () => {
    isDropdownVisible.value = false;
};

const toggleNavbar = () => {
    isNavbarOpen.value = !isNavbarOpen.value;
};

function logout(){
    Swal.fire({
        title: "確定要登出?",
        showCancelButton: true,
        icon: "question"
    }).then((result) => {
        if (result.isConfirmed){
            userStore.setId("");
            userStore.setEmail("");
            userStore.setPwd("");
            userStore.setVip("");
            userStore.setBan("");
            userStore.setName("");
            userStore.setGender("");
            userStore.setPics("");
            userStore.setBirth("");
            userStore.setLocation("");
            userStore.setJob("");
            userStore.setJobPosi("");
            userStore.setIntroduction("");
            userStore.setPreferAct("");
            userStore.setPreferGen("");
            userStore.setFind("");
            userStore.setPreferAgeMax("");
            userStore.setPreferAgeMin("");
            userStore.setHobby("");
            userStore.setLogedin(false);
            userStore.setToken("");
            Swal.fire({
                    icon:"success",
                    title: "登出成功",
                    allowOutsideClick: false
            }).then(function(){
                sessionStorage.removeItem("user");
                router.push("/");
            });
        }
    });
}


const showNotifications = () => {
    isNotificationsVisible.value = true;
    fetchLatestNotifications();
};

const hideNotifications = () => {
    isNotificationsVisible.value = false;
};

const fetchLatestNotifications = async () => {
    if (!userStore.isLogedin || !userStore.id) {
        console.log("用戶未登入");
        return;
    }
    try {
        const response = await instance.get(`/secure/notification/latest/${userStore.id}`, {
        headers: {
            'Authorization': userStore.token
        }
        });
        latestNotifications.value = response.data;
        unreadNotificationsCount.value = latestNotifications.value.filter(noti => noti.notificationRead === '0').length; //用於未讀通知數量
    } catch (err) {
        console.error('獲取最新通知時出錯:', err);
    }
};

const formatDate = (date) => {
    const now = new Date();
    const notificationDate = new Date(date);
    const diffTime = Math.abs(now - notificationDate);
    const diffHours = Math.floor(diffTime / (1000 * 60 * 60));
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays < 1) {
        if (diffHours === 0){
            return '剛剛';
        }else{
            return `${diffHours} 小時前`;
        }
    }else{
        return `${diffDays} 天前`;
    }
};

// const nextNotification = (notificationId) => {
//     router.push({ name: 'oneNotiPage-link', params: { nid: notificationId }});
//     isNotificationsVisible.value = false;
// }

const handleNotificationClick = async (noti) => {
    try {
        if (noti.notificationRead === '0') {
        await instance.put(`/secure/notification/markAsRead/${noti.notificationId}`, {}, {
            headers: {
            'Authorization': userStore.token
            }
        });
        // 更新已讀
        noti.notificationRead = '1';
        // 更新未讀通知計數
        unreadNotificationsCount.value -= 1;
        }
        
        router.push({ name: 'oneNotiPage-link', params: { nid: noti.notificationId }});
        isNotificationsVisible.value = false;
    } catch (err) {
        console.error('標記通知為已讀時出錯:', err);
    }
};

let pollInterval;

onMounted(() => {
    if (userStore.isLogedin) {
        fetchLatestNotifications();
        // 檢查新通知的時間
        pollInterval = setInterval(fetchLatestNotifications, 600000);
    }
});

onUnmounted(() => {
    if (pollInterval) {
        clearInterval(pollInterval);
    }
});

</script>

<style scoped>
.dropdown-menu {
    display: block;
    visibility: hidden;
    opacity: 0;
    transition: visibility 0s, opacity 0.3s linear;
}

.dropdown-menu.show {
    visibility: visible;
    opacity: 1;
}

.navbar.fixed-top {
    background-color: rgba(255, 255, 255, 0.9);
    transition: background-color 0.3s ease-in-out;
}

/* 適應固定導航欄的內容位置 */
body {
    padding-top: 90px;
}

@media (max-width: 991.98px) {
    body {
        padding-top: 75px;
    }
}

.notification-item {
    padding: 10px 15px;
    transition: background-color 0.3s ease;
}

.notification-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    text-decoration: none;
    color: #6c757d;
    
}

.notification-title {
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-right: 12px;
    font-size: 1.1rem;
    font-weight: 500;
    color: black;
}

.notification-time {
    flex-shrink: 0;
    font-size: 0.8rem;
    color: #6c757d;
}

.notification-item.unread {
    background-color: #e9ecef;
}

.notification-item.unread .notification-title {
    font-weight: bold;
}

.notification-item:hover {
    background-color: #86BB17;
    cursor: pointer;
}

.notification-item:hover .notification-title,
.notification-item:hover .notification-time {
    color: white;
}

.navbar-logo {
    height: 60px;
    margin-right: 1px;
    vertical-align: middle;
}

.fa-layers-counter {
    font-family: Arial, sans-serif;
    font-size: 3em;
    font-weight: bold;
    color: white;
    border-radius: 50%;
    padding: 2px 5px;
    position: absolute;
    top: -5px;
    right: -5px;
}

.avatar-image {
  width: 40px;
  height: 40px;
  object-fit: cover;
  object-position: center;
}
</style>