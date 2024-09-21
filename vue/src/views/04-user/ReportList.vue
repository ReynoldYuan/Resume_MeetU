<template>
    <div v-if="show" class="report-list-modal">
        <div class="modal-content">
            <h3 class="modal-title">檢舉列表</h3><div v-if="reportedItems.length === 0" class="empty-list-message">
        目前沒有任何檢舉
        </div>
        <div v-else class="report-items">
            <div class="report-header">
                <span>檢舉項目</span>
                <span>檢舉內容</span>
                <span>檢舉類型</span>
                <span>檢舉原因</span>
                <span>審核狀態</span>
                <span>操作</span>
            </div>
        <div v-for="item in reportedItems" :key="item.reportId" class="report-item">
            <span>{{ getReportTypeName(item.reportItem) }}</span>
            <span>
                <router-link 
                v-if="item.reportItem === 'U'" 
                :to="{ name: 'userprofile-link', params: { userId: item.reportItemId } }" 
                @click.native="closeModal">
                {{ item.reportedContent }}
                </router-link>
                <router-link 
                v-else-if="item.reportItem === 'A' || (item.reportItem === 'AC' && item.associatedActivityId)" 
                :to="{ name: 'briefactivities-link', params: { activitiesId: item.reportItem === 'A' ? item.reportItemId : item.associatedActivityId } }" 
                @click.native="closeModal">
                {{ item.reportedContent }}
                </router-link>
                <span v-else>{{ item.reportedContent }}</span>
            </span>
            <span>{{ item.reportType }}</span>
            <span>{{ item.reportReason }}</span>
            <span :class="getStatusClass(item.reportStatus)">{{ getReportStatus(item.reportStatus) }}</span>
            <span>
                <button v-if="item.reportStatus === 'P'" 
                        @click="cancelReport(item.reportItemId, item.reportItem)" 
                        class="cancel-report-btn">
                取消檢舉
                </button>
            </span>
        </div>
        </div>
            <button @click="closeModal" class="close-btn">關閉</button>
        </div>
    </div>
</template>

<script setup>
import { watch, toRef } from 'vue';

const props = defineProps({
    show: Boolean,
    reportedItems: Array
});

const emit = defineEmits(['close', 'cancel-report']);

const reportedItemsRef = toRef(props, 'reportedItems');

watch(reportedItemsRef, (newItems) => {
    console.log('ReportList received new items:', newItems);
}, { deep: true });


function getReportTypeName(type) {
    const types = {
    'U': '使用者',
    'P': '貼文',
    'A': '活動',
    'PC': '貼文留言',
    'AC': '活動留言',
    };
    return types[type] || '未知類型';
}

function getReportStatus(status) {
    const types = {
    'P': '審核中',
    'N': '檢舉不成立',
    'Y': '檢舉成立',
    };
    return types[status] || '未知檢舉狀態';
}

function getStatusClass(status) {
    if (status === 'Y') return 'status-approved';
    if (status === 'N') return 'status-rejected';
    return '';
}

function cancelReport(reportItemId, reportItem) {
    emit('cancel-report', reportItemId, reportItem);
}

function closeModal() {
    emit('close');
}

</script>

<style scoped>
.report-list-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 5px;
    max-width: 90%;
    width: 1000px;
    max-height: 80vh;
    overflow-y: auto;
}

.modal-title {
    color: #273751;
    font-weight: bold;
    margin-bottom: 20px;
    text-align: center;
}

.empty-list-message {
    text-align: center;
    color: #777;
    font-size: 16px;
    margin: 20px 0;
}

.report-items {
    display: flex;
    flex-direction: column;
}

.report-header, .report-item {
    display: flex;
    padding: 10px 0;
    border-bottom: 1px solid #e0e0e0;
}

.report-header {
    font-weight: bold;
    background-color: #f5f5f5;
}

.report-header span, .report-item span {
    flex: 1;
    padding: 0 10px;
    display: flex;
    align-items: center;
}

.report-item:hover {
    background-color: #f0f0f0;
}

.cancel-report-btn {
    padding: 5px 10px;
    background-color: #f44336;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.3s;
}

.cancel-report-btn:hover {
    background-color: #d32f2f;
}

.close-btn {
    width: auto;
    padding: 5px 10px;
    margin-top: 20px;
    background-color: #86BB17;
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.3s;
    text-align: center;
}

.close-btn:hover {
    background-color: #2c3e50;
}

.status-approved {
    color: blue;
}

.status-rejected {
    color: red;
}

a {
    color: #0066cc;
    text-decoration: none;
}

a:hover {
    text-decoration: underline;
}

</style>