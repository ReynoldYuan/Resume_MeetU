<!-- UserSidebar.vue

**** Sidebar設計 ****
<template>
    <div class="ts-box" style="width: 210px">
        <div class="ts-content">
            <div class="ts-menu is-start-icon is-separated">
            <a class="item" @click="$emit('update')">
                <span class="ts-icon is-pen-icon"></span> 修改個人資料
            </a>
            <a class="item" @click="$emit('delete')">
                <span class="ts-icon is-user-xmark-icon"></span> 註銷會員
            </a>
            <a class="item" @click="$emit('showBanList')">
                <span class="ts-icon is-ban-icon"></span> 封鎖列表
            </a>
            <a class="item" @click="$emit('showReportList')">
                <span class="ts-icon is-flag-icon"></span> 檢舉列表
            </a>
            <div class="ts-divider"></div>
            <div class="item" @click="toggleActivityMenu">
                <span class="ts-icon is-calendar-icon"></span> 活動管理
                <span class="ts-icon is-caret-down-icon" :class="{ 'is-rotated': isActivityMenuOpen }"></span>
            </div>
            <div v-if="isActivityMenuOpen" class="ts-menu is-subtle">
                <router-link :to="{ name: 'addActPage-link' }" class="item">
                <span class="ts-icon is-plus-icon"></span> 新增活動
                </router-link>
                <router-link :to="{ name: 'usersActList-link' }" class="item">
                <span class="ts-icon is-list-icon"></span> 我的活動
                </router-link>
                <router-link :to="{ name: 'MyRegistrations-link' }" class="item">
                <span class="ts-icon is-clipboard-list-icon"></span> 我的報名
                </router-link>
                <router-link :to="{ name: 'MyFavorite-link' }" class="item">
                <span class="ts-icon is-heart-icon"></span> 我的收藏
                </router-link>
                <router-link :to="{ name: 'ActivityReview-link' }" class="item">
                <span class="ts-icon is-check-double-icon"></span> 活動審核
                </router-link>
                <router-link :to="{ name: 'MyCompleted-link' }" class="item">
                <span class="ts-icon is-trophy-icon"></span> 已參加活動
                </router-link>
            </div>
            <div class="ts-divider"></div>
            <div class="item" @click="togglePostMenu">
                <span class="ts-icon is-calendar-icon"></span> 貼文管理
                <span class="ts-icon is-caret-down-icon" :class="{ 'is-rotated': isPostMenuOpen }"></span>
            </div>
            <div v-if="isPostMenuOpen" class="ts-menu is-subtle">
                <router-link :to="{ name: 'addPost-link' }" class="item">
                    <span class="ts-icon is-plus-icon"></span> 新增貼文
                </router-link>
                <router-link to="/my-posts" class="item">
                    <span class="ts-icon is-list-icon"></span> 我的貼文
                </router-link>
            </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

const emit = defineEmits(['update', 'delete', 'showBanList', 'showReportList']);
const isActivityMenuOpen = ref(false);
const isPostMenuOpen = ref(false);

const toggleActivityMenu = () => {
    isActivityMenuOpen.value = !isActivityMenuOpen.value;
};

const togglePostMenu = () => {
    isPostMenuOpen.value = !isPostMenuOpen.value;
};
</script>

<style scoped>
.ts-box {
    position: sticky;
    top: 100px;
    height: calc(100vh - 120px);
    overflow-y: auto;
}

.ts-menu .item {
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.ts-menu .item:hover {
    background-color: var(--ts-gray-50);
}

.ts-icon.is-rotated {
    transform: rotate(180deg);
}

.ts-menu.is-subtle {
    margin-left: 1.5rem;
}

.ts-menu.is-subtle .item {
    padding-left: 1rem;
}
</style> -->

<!-- UserSidebar.vue -->


<!-- 分頁籤設計 -->
<template>
    <div class="ts-tab user-sidebar">
        <div class="tab-item" @mouseenter="activeTab = 'profile'" @mouseleave="activeTab = null">
            <a class="item">
                <span class="ts-icon is-user-icon"></span>
                個人資料
            </a>
            <div class="dropdown-content" v-show="activeTab === 'profile'">
                <router-link :to="{ name: 'myprofile-link', params: { userId: userStore.id } }" class="dropdown-item">
                    <span class="ts-icon is-eye-icon"></span> 查看我的檔案
                </router-link>
                <a class="dropdown-item" @click="$emit('update')">
                    <span class="ts-icon is-pen-icon"></span> 修改個人資料
                </a>
                <a class="dropdown-item" @click="$emit('delete')">
                    <span class="ts-icon is-user-xmark-icon"></span> 註銷會員
                </a>
            </div>
        </div>

        <div class="tab-item" @mouseenter="activeTab = 'activities'" @mouseleave="activeTab = null">
            <a class="item">
                <span class="ts-icon is-calendar-icon"></span>
                活動管理
            </a>
            <div class="dropdown-content" v-show="activeTab === 'activities'">
            <router-link :to="{ name: 'addActPage-link' }" class="dropdown-item">
                <span class="ts-icon is-plus-icon"></span> 新增活動
            </router-link>
            <router-link :to="{ name: 'ActivityReview-link' }" class="dropdown-item">
                <span class="ts-icon is-check-double-icon"></span> 活動審核
            </router-link>
            <router-link :to="{ name: 'usersActList-link' }" class="dropdown-item">
                <span class="ts-icon is-list-icon"></span> 我的活動
            </router-link>
            <router-link :to="{ name: 'MyRegistrations-link' }" class="dropdown-item">
                <span class="ts-icon is-clipboard-list-icon"></span> 我的報名
            </router-link>
            <router-link :to="{ name: 'MyFavorite-link' }" class="dropdown-item">
                <span class="ts-icon is-heart-icon"></span> 我的收藏
            </router-link>
            <router-link :to="{ name: 'MyCompleted-link' }" class="dropdown-item">
                <span class="ts-icon is-trophy-icon"></span> 已參加活動
            </router-link>
            </div>
        </div>

        <div class="tab-item" @mouseenter="activeTab = 'posts'" @mouseleave="activeTab = null">
            <a class="item">
                <span class="ts-icon is-file-lines-icon"></span>
                貼文管理
            </a>
            <div class="dropdown-content" v-show="activeTab === 'posts'">
            <router-link :to="{ name: 'addPost-link' }" class="dropdown-item">
                <span class="ts-icon is-plus-icon"></span> 新增貼文
            </router-link>
            <router-link to="/my-posts" class="dropdown-item">
                <span class="ts-icon is-list-icon"></span> 我的貼文
            </router-link>
            </div>
        </div>

        <div class="tab-item" @mouseenter="activeTab = 'lists'" @mouseleave="activeTab = null">
            <a class="item">
                <span class="ts-icon is-list-icon"></span>
                列表查詢
            </a>
            <div class="dropdown-content" v-show="activeTab === 'lists'">
            <a class="dropdown-item" @click="$emit('showBanList')">
                <span class="ts-icon is-ban-icon"></span> 封鎖列表
            </a>
            <a class="dropdown-item" @click="$emit('showReportList')">
                <span class="ts-icon is-flag-icon"></span> 檢舉列表
            </a>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import useUserStore from "@/stores/user.js";

const emit = defineEmits(['update', 'delete', 'showBanList', 'showReportList']);
const activeTab = ref('null');
const userStore = useUserStore();

</script>

<style scoped>

.user-sidebar {
    position: relative;
    display: flex;
    justify-content: space-around;
    background-color: var(--ts-gray-100);
}

.tab-item {
    position: relative;
}

.item {
    padding: 15px 15px;
    cursor: pointer;
    display: flex;
    align-items: center;
    color: var(--ts-gray-700);
}

.item:hover {
    color: var(--ts-blue-500);
}

.ts-icon {
    margin-right: 5px;
}

.dropdown-content {
    position: absolute;
    top: 100%;
    left: 0;
    background-color: white;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    min-width: 200px;
}

.dropdown-item {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    color: var(--ts-gray-700);
    text-decoration: none;
}

.dropdown-item:hover {
    background-color: var(--ts-gray-100);
    color: var(--ts-blue-500);
}
</style>