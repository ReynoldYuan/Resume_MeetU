<template>


    <div class="container">

        <div class="profile-card-4">
            <div class="image-container">
                <img :src=userProfile.data.userPics alt="用戶頭像" class="img img-responsive">
            </div>
            <div class="profile-content">
                <div class="profile-name">
                    {{ userProfile.data.userName + " " }}
                    <span class="user-age">{{ userAge }}</span>
                    <template v-if="userStore.isLogedin">
                        <template v-if="userStore.id != userId">
                            <template v-if="ifBan === false || ifBan === true">
                                <span v-if="ifBan === false" class="user-age"><button class="follow"
                                        @click="emits('ban', userId)">封鎖</button></span>
                                <span v-if="ifBan === true" class="user-age"><button class="follow"
                                        @click="emits('unban', userId)">解除封鎖</button></span>
                            </template>
                            <template v-else="ifBan == null || ifBan !== ''">
                            </template>

                            <!-- UserCard在每日配對時不顯示 -->
                            <template v-if="userForDailyMatching">

                            </template>
                            <template v-else>
                                <!-- 檢舉按鈕 -->
                                <span v-if="!props.ifBan && !props.myProfile" class="user-age">
                                    <button class="follow" @click="startReport" :disabled="props.ifReport">
                                        {{ props.ifReport ? '檢舉審查中' : '檢舉' }}
                                    </button>
                                </span>
                                <!-- <template v-if="ifReport === false || ifReport === true">
                                <span v-if="ifReport === false" class="user-age"><button class="follow"
                                    @click="emits('report', userId)">檢舉</button></span>
                                <span v-if="ifReport === true" class="user-age"><button class="follow">檢舉審查中</button></span>
                            </template> -->
                            </template>

                        </template>
                        <!-- <template v-if="userStore.id != userId"> -->
                        <template v-if="!ifBan && !ifBannedMe">

                            <template v-if="(ifFollowing === false || ifFollowing === true) && myProfile !== true">
                                <span v-if="ifFollowing === false" class="user-age"><button class="follow"
                                        @click="emits('follow', userId)">追蹤</button></span>
                                <span v-if="ifFollowing === true" class="user-age"><button class="follow"
                                        @click="emits('unfollow', userId)">追蹤中</button></span>
                            </template>

                        </template>
                        <template v-else="ifFollowing == null || ifFollowing !== ''">
                        </template>
                        <!-- </template> -->
                        <!-- <template v-if="myProfile === false || myProfile === true">
                            <span v-if="myProfile === true" class="user-age"><button class="follow"
                                    @click="emits('update', userId)">修改個人資料</button></span>
                            <span v-if="myProfile === true" class="user-age"><button class="follow"
                                    @click="emits('delete', userId)">註銷會員</button></span>
                            <span v-if="myProfile === true" class="user-age"><button class="follow"
                                    @click="showBanList')">封鎖列表</button></span>
                        </template> -->
                    </template>
                </div>
                <div class="profile-starsign  text-center">
                    <p>{{ starSign }}</p>
                </div>

                <!-- 封鎖對方後 以下資料要鎖起來 -->
                <template v-if="!ifBan && !ifBannedMe || myProfile">
                <div class="profile-description">
                    <div class="items">
                        <span>{{ userProfile.data.userGender === 'F' ? '女' : (userProfile.data.userGender === 'M' ? '男'
                            :
                            userProfile.data.userGender) }}</span>
                        <span>{{ userProfile.data.userLocation }}</span>
                        <span>{{ userProfile.data.userJob }}</span>
                        <span>{{ userProfile.data.userJobPosi }}</span>
                    </div>
                    <div class="details">
                        <p>{{ userProfile.data.userIntroduction }}</p>
                        <p>性向：{{ userPreferGen }}</p>
                        <p>交友偏好：{{ userFind }}</p>
                        <p>嗜好：{{ userProfile.data.userHobby }}</p>
                        <p>感興趣的聚會：{{ userPreferAct }}</p>
                    </div>
                </div>

                <!-- UserCard在每日配對時不顯示 -->
                <!-- <template v-if="userForDailyMatching"> -->
                <template v-if="!userForDailyMatching">

                <!-- </template> -->
                <!-- <template v-else> -->

                    <hr>
                    <!-- 封鎖對方後 以下資料要鎖起來 -->
                    <!-- <template v-if="!ifBan && !ifBannedMe || myProfile"> -->
                        <div class="row  text-center">
                            <!-- <div class="col-3"> -->
                                <!-- @click="$emit('checkActHistory')" -->
                                <!-- <div :class="['profile-overview', { 'clickable-overview': myProfile }]">
                                    <p>聚會</p>
                                    <h4>???</h4>
                                </div>
                            </div> -->
                            <div class="col-4">
                                <div :class="['profile-overview', { 'clickable-overview': myProfile }]"
                                    @click="myProfile && $emit('showMutualMatching')">
                                    <p>好友</p>
                                    <h4>{{ userProfile.data.mutualMatchingQty }}</h4>
                                </div>
                            </div>
                            <div class="col-4">
                                <div :class="['profile-overview', { 'clickable-overview': myProfile }]"
                                    @click="myProfile && $emit('showFolloweeList')">
                                    <p>追蹤中</p>
                                    <h4>{{ userProfile.data.followeeNum }}</h4>
                                </div>
                            </div>
                            <div class="col-4">
                                <!-- @click="showFollowerList" -->
                                <div :class="['profile-overview', { 'clickable-overview': myProfile }]"
                                    @click="myProfile && $emit('showFollowerList')">
                                    <p>粉絲</p>
                                    <h4>{{ userProfile.data.followerNum }}</h4>
                                </div>
                            </div>
                        </div>
                        <hr>
                    </template>
                        <!-- <template v-if="myProfile && !ifBan && !ifBannedMe"> -->
                        <template v-if="myProfile">
                            <div class="profile-description">
                                <div class="setting">
                                    <p>配對設定</p>
                                    <p>配對年紀：{{ userProfile.data.userPreferAgeMin + "~" +
                                        userProfile.data.userPreferAgeMax }}
                                    </p>
                                </div>
                            </div>
                        </template>
                    </template>

                    <template v-else>
                        <div class="locked-profile">
                            <div class="locked-content">
                                <font-awesome-icon icon="lock" size="4x" />
                                <div class="locked-text">無法顯示此帳戶資訊</div>
                            </div>
                        </div>
                    </template>

                <!-- </template> -->
                <!-- 封鎖對方後 以上資料要鎖起來 -->

                <!-- 檢舉彈出視窗 -->
                <div v-if="showReportModal" class="report-modal">
                    <div v-if="reportStep === 1" class="modal-content">
                        <h3>請選擇檢舉類型</h3>
                        <ul>
                            <li v-for="(type, index) in reportTypes" :key="index">
                                <button @click="selectReportType(type)">{{ type }}</button>
                            </li>
                        </ul>
                        <button @click="cancelReport" class="cancel-button">取消</button>
                    </div>
                    <div v-else-if="reportStep === 2" class="modal-content">
                        <h3>請選擇檢舉原因</h3>
                        <ul>
                            <li v-for="(reason, index) in reportReasons" :key="index">
                                <button @click="selectReportReason(reason)">{{ reason }}</button>
                            </li>
                        </ul>
                        <button @click="cancelReport" class="cancel-button">取消</button>
                    </div>
                    <!-- <div v-else-if="reportStep === 3" class="modal-content">
                    <h3>檢舉成功</h3>
                    <h5>我們會盡快審核您的檢舉</h5>
                    <button @click="finishReport">完成</button>
                </div> -->
                </div>
            </div>
        </div>
    </div>

</template>

<script setup>
import { ref } from "vue";
import Swal from 'sweetalert2';

const props = defineProps({
    userProfile: Object,
    // userId: BigInt,
    userId: [Number, String],
    userAge: Number,
    starSign: String,
    userPreferAct: String,
    userPreferGen: String,
    userFind: String,
    ifFollowing: Boolean,
    myProfile: Boolean,
    ifBan: Boolean,
    ifBannedMe: Boolean,
    userForDailyMatching: Boolean,
    ifReport: Boolean
});
// const emits = defineEmits(["follow", "unfollow", "update", "delete", "ban", "unban", "showBanList"]);
const emits = defineEmits(["follow", "unfollow", "ban", "unban", "report",
    "showMutualMatching", "showFolloweeList", "showFollowerList"
]);



import useUserStore from "@/stores/user.js"
const userStore = useUserStore();
const showReportModal = ref(false);
const reportStep = ref(1);
const selectedReportType = ref('');
const selectedReportReason = ref('');

const reportTypes = [
    '詐騙帳號', '騷擾或霸凌', '暴力或仇恨',
    '自殺或自我傷害', '不實資訊', '成人內容'
];

const reportReasons = [
    '構成詐欺或詐騙', '此人假冒他人進行不當的行為',
    '霸凌與騷擾他人的接觸', '內含自殺或自殘的行為',
    '煽動暴力或仇恨的字眼', '內容不實，易誤導他人',
    '內容含有未成年不宜觀看的資訊'
];

const startReport = () => {
    if (!props.ifReport) {
        showReportModal.value = true;
        reportStep.value = 1;
    }
};

const selectReportType = (type) => {
    selectedReportType.value = type;
    reportStep.value = 2;
};

const selectReportReason = (reason) => {
    selectedReportReason.value = reason;
    // reportStep.value = 3;
    finishReport();
};

const finishReport = () => {
    showReportModal.value = false;
    const reportData = {
        reportType: selectedReportType.value,
        reportReason: selectedReportReason.value,
        reportedUserId: props.userId
    };
    console.log("Emitting report event with data:", reportData);
    emits('report', reportData);
    Swal.fire({ title: "檢舉成功", text: "我們會盡快審核您的檢舉", icon: "success" });
};

const cancelReport = () => {
    showReportModal.value = false;
    reportStep.value = 1;
};


</script>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Montserrat:400,500,600,700,800,900|Open Sans:400,600,800');

h1,
h2,
h3,
h4,
h5,
h6,
div,
input,
p,
a {
    font-family: "Open Sans";
    margin: 0px;
}

.profile-card-4 {
    /* max-width: 800px; */
    width: 600px;
    background-color: #FFF;
    border-radius: 5px;
    box-shadow: 0px 0px 25px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    position: relative;
    margin: 10px auto;
}

.profile-card-4 .image-container {
    /* width: 500px;
    height: 500px; */
    width: 600px;
    height: 450px;
    overflow: hidden;
    position: relative;
}

.profile-card-4 .image-container img {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    max-width: 600px;
    min-height: 450px;
    width: 600px;
    height: auto;
    object-fit: cover;
}

.profile-card-4 .profile-content {
    position: relative;
    padding: 15px;
    background-color: #FFF;
}

.profile-card-4 .profile-name {
    position: absolute;
    left: 30px;
    top: -70px;
    font-size: 40px;
    color: #FFF;
    text-shadow: 0px 0px 20px rgba(0, 0, 0, 0.5);
    font-weight: bold;
}

.profile-card-4 .profile-name .user-age {
    font-size: 0.65em;
}

.profile-card-4 .profile-name .user-age .follow {
    margin: 10px;
    font-size: 15px;
}

.profile-card-4 .profile-starsign {
    position: absolute;
    right: 15px;
    top: -60px;
    font-size: 18px;
    color: #FFF;
    text-shadow: 0px 0px 20px rgba(0, 0, 0, 0.5);
    font-weight: bold;
}

.profile-card-4 .profile-starsign p {
    margin: 3px;
    padding: 8px;
    background-color: #e4e3e3;
    border-radius: 20px;
}

.profile-card-4 .profile-description p {
    color: #777;
    font-size: 18px;
    margin: 5px;
}

.profile-card-4 .profile-description .items {
    margin-top: 5px;
}

.profile-card-4 .profile-description .details {
    margin-top: 20px;
    padding-left: 10px;
}

.profile-card-4 .profile-description .setting {
    margin-top: 20px;
    padding-left: 10px;
    border: 1px solid #e9dddd;
    border-radius: 20px;
}

.profile-card-4 .profile-description .setting p {
    font-size: 16px;
}

.profile-card-4 .profile-description span {
    background-color: #e4e3e3dd;
    text-shadow: 0px 0px 20px rgba(0, 0, 0, 0.5);
    margin: 5px;
    border-radius: 20px;
    color: #ffffffde;
    font-size: 18px;
    padding: 8px;
}

.profile-card-4 .profile-overview {
    padding: 15px 0px;
}

.clickable-overview:hover {
    cursor: pointer;
}

.profile-card-4 .profile-overview h4 {
    margin-top: 0;
    margin-bottom: 0;
}

.profile-card-4 .profile-overview p {
    font-size: 15px;
    font-weight: 600;
    color: #777;
    margin-bottom: 0;
}

.profile-card-4 .profile-overview h4 {
    color: #273751;
    font-weight: bold;
}

.locked-profile {
    height: 200px;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f8f9fa;
    border-radius: 8px;
}

.locked-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
}

.locked-text {
    margin-top: 16px;
    font-size: 18px;
    color: #6c757d;
}

.report-modal {
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
    max-width: 500px;
    width: 100%;
}

.modal-content ul {
    list-style-type: none;
    padding: 0;
}

.modal-content li {
    margin-bottom: 10px;
}

.modal-content button {
    width: 100%;
    padding: 10px;
    background-color: #f0f0f0;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.modal-content button:hover {
    background-color: #e0e0e0;
}

.cancel-button {
    margin-top: 10px;
    background-color: #f44336;
    color: 6c757d;
}

.cancel-button:hover {
    background-color: #d32f2f;
}
</style>