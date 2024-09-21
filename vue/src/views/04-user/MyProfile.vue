<template>
    <template v-if="userStore.isLogedin && myProfile">
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
            <UserSidebar @update="update" @delete="dodelete" @showBanList="showBanList" @showReportList="showReportList" />
            </div>
        </div>
        <div class="row mt-2">
            <div class="col-md-8 offset-md-2">
                <UserCard :user-profile="userProfile" :user-id="userId" :user-age="userAge" :star-sign="starSign"
                    :user-prefer-act="userPreferAct" :user-prefer-gen="userPreferGen" :if-following="ifFollowing"
                    :my-profile="myProfile" :if-ban="ifBan" :if-banned-me="ifBannedMe" :user-find="userFind" :if-report="ifReport"
                    @showBanList="showBanList" @showBanListSpare="showBanListSpare" @report="report" @showMutualMatching="showMutualMatching"
                    @showFolloweeList="showFolloweeList" @showFollowerList="showFollowerList" />
            </div>
        </div>
    </div>
    </template>

    <BanList v-if="showBanListModal" :show="showBanListModal" :user-details="userDetails" :list-type="listType"
        :listMessage="listMessage" :show-banList-modal="showBanListModal" @close="closeBanList" @unban="unban" @ban="ban"/>

    <ReportList v-if="showReportListModal" :show="showReportListModal" :reported-items="reportedItems"
        @close="closeReportList" @cancel-report="cancelReport" />
</template>

<script setup>
import UserCard from '@/components/02-user/UserCard.vue';
import { reactive, ref, onMounted, watch } from 'vue';
import axiosapi from '@/plugins/axios.js';
import { useRoute } from 'vue-router';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';
import BanList from './BanList.vue';
import ReportList from './ReportList.vue';
import UserSidebar from '@/components/02-user/UserSidebar.vue';


const route = useRoute();
const router = useRouter();

import useUserStore from "@/stores/user.js"
import MyPosts from '../03-post/MyPosts.vue';
const userStore = useUserStore();

const emit = defineEmits(["update", "delete", "showBanList","showMutualMatching","showFolloweeList","showFollowerList"]);

const userId = ref(0);
const userProfile = reactive({ data: "" });
const userAge = ref(0);
const starSign = ref("");
const userPreferAct = ref("");
const userPreferGen = ref("");
const userFind = ref("");
const ifFollowing = ref("");
const userPics = ref("");
const myProfile = ref("");
const ifBan = ref("");
const ifBannedMe = ref("");
const showBanListModal = ref(false);
const bannedUsers = ref([]);
const userDetails = ref([]);
const ifReport = ref(false);
const showReportListModal = ref(false);
const reportedItems = ref([]);
const listName = ref("");
const listType = ref("封鎖列表");
const listMessage = ref("目前沒有封鎖任何用戶");
const listUrl = ref("");

function initializeProfile() {
    userId.value = route.params.userId;
    findById();
    if (userStore && userStore.id) {
        console.log("請求追蹤狀態");
        ifFollow();
        checkBanStatus();
        checkReportStatus();
    } else {
        console.log("無法請求追蹤狀態");
    }
}


onMounted(initializeProfile);


watch(() => route.params.userId, (newUserId, oldUserId) => {
    if (newUserId && newUserId !== oldUserId) {
        console.log("用戶ID變更，重新初始化個人資料");
        initializeProfile();
    }
});

function findById() {
    axiosapi.get('/users/' + userId.value).then(function (response) {
        userProfile.data = response.data;
        console.log("向後端請求使用者資料成功!");
        userAge.value = calculateAge(userProfile.data.userBirth);
        starSign.value = getChineseZodiac(userProfile.data.userBirth);
        userPreferAct.value = getUserPreferAct(userProfile.data.userPreferAct);
        userPreferGen.value = getUserPreferGen(userProfile.data.userPreferGen);
        userFind.value = getUserFind(userProfile.data.userFind);
        userPics.value = "C:/springboot/userPics" + userProfile.data.userPics;
    }).catch(function (error) {
        console.log("向後端請求使用者資料失敗!");
        router.push({ name: 'notfound-link' })

    });
}

function calculateAge(birthdate) {
    const today = new Date();
    const birthDate = new Date(birthdate);

    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDifference = today.getMonth() - birthDate.getMonth();

    if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }

    return age;
}

function getChineseZodiac(date) {
    const birthDate = new Date(date);
    const month = birthDate.getMonth() + 1; // getMonth() 返回 0-11
    const day = birthDate.getDate();

    if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "白羊座";
    if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "金牛座";
    if ((month == 5 && day >= 21) || (month == 6 && day <= 21)) return "雙子座";
    if ((month == 6 && day >= 22) || (month == 7 && day <= 22)) return "巨蟹座";
    if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "獅子座";
    if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "處女座";
    if ((month == 9 && day >= 23) || (month == 10 && day <= 23)) return "天秤座";
    if ((month == 10 && day >= 24) || (month == 11 && day <= 22)) return "天蠍座";
    if ((month == 11 && day >= 23) || (month == 12 && day <= 21)) return "射手座";
    if ((month == 12 && day >= 22) || (month == 1 && day <= 19)) return "摩羯座";
    if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) return "水瓶座";
    if ((month === 2 && day >= 19) || (month === 3 && day <= 20)) return "雙魚座";
    return "未知星座";
}

function getUserPreferAct(data) {
    if (data == "I") return "室內";
    if (data == "O") return "戶外";
    if (data == "M") return "混合";
}

function getUserPreferGen(data) {
    if (data == "F") return "女性";
    if (data == "M") return "男性";
    if (data == "N") return "不限";
}

function getUserFind(data) {
    if (data == "E") return "另一半";
    if (data == "C") return "同好";
    if (data == "N") return "不限";
}

function ifFollow() {
    const request = {
        headers: {
            'Authorization': userStore.token
        }
    }
    axiosapi.get('/secure/users/follow/' + userId.value, request
    ).then(function (response) {
        console.log("成功確認是否追蹤!");
        ifFollowing.value = response.data.ifFollowing;
        console.log("成功查看追蹤" + ifFollowing.value);
        myProfile.value = response.data.myProfile;
        console.log("是否是否為本人:" + myProfile.value)

    }).catch(function (error) {
        console.log("確認是否追蹤失敗!" + error);
        ifFollowing.value = "";
        console.log("失敗查看追蹤" + ifFollowing.value);
    });
}

function update() {
    router.push({ name: 'update-link', params: { userId: userId.value } });
}

function dodelete() {
    Swal.fire({
        title: "確定要註銷帳號?",
        text: "刪除後將無法復原",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "確定",
        cancelButtonText: "取消"
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                text: "Loading......",
                allowOutsideClick: false,
                showConfirmButton: false
            });

            let request = {
                headers: {
                    'Authorization': userStore.token
                }
            }

            axiosapi.delete('/users/profile/' + userId.value, request).then(function (response) {
                console.log(response.data);
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
                    title: "註銷成功!",
                    text: "帳號已註銷",
                    icon: "success"
                }).then(function () {
                    sessionStorage.removeItem("user");
                    router.push('/');
                });
            }).catch(function (error) {
                Swal.fire({
                    icon: "error",
                    title: "刪除失敗",
                    text: error.message
                });
            })
        }
    });
}

function checkBanStatus() {
    const request = {
        headers: {
            'Authorization': userStore.token
        }
    }

    axiosapi.get('/users/ban/' + userId.value, request)
        .then(function (response) {
            // console.log("已確認是否封鎖");
            ifBan.value = response.data.ifBan;
            // console.log('成功查看封鎖' + ifBan.value);
            ifBannedMe.value = response.data.ifBannedMe;
            myProfile.value = response.data.myProfile;
            // console.log('確認是否為本人' + myProfile.value)
        }).catch(function (error) {
            console.log("無法確認是否追蹤" + error);
            ifBan.value = "";
            ifBannedMe.value = "";
            console.log("查看封鎖失敗" + ifBan.value);
        });
}

function ban() {
    Swal.fire({
        title: "確定要封鎖嗎?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "確定"
    }).then((result) => {

        if (result.isConfirmed) {
            const request = {
                headers: {
                    'Authorization': userStore.token
                },
                data: {
                    "banedUserId": userId.value
                }
            }

            axiosapi.post('/users/ban/', request.data, { headers: request.headers })
                .then(function (response) {
                    // console.log("封鎖成功");
                    ifBan.value = response.data.ifBan;
                    Swal.fire("封鎖成功", "", "success");
                }).catch(function (error) {
                    console.log("封鎖失敗" + error);
                    Swal.fire("封鎖失敗", "", "error");
                })
        }
    });
}

function unban(banedUserId) {
    Swal.fire({
        title: "是否要解除封鎖?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "確定"
    }).then((result) => {
        if (result.isConfirmed) {
            const request = {
                headers: {
                    'Authorization': userStore.token
                },
                data: {
                    "banedUserId": banedUserId
                }
            }

            axiosapi.delete('/users/ban/', {
                headers: request.headers,
                data: request.data
            })
                .then(function (response) {
                    // console.log("成功取消封鎖");
                    userDetails.value = userDetails.value.filter(user => user.id !== banedUserId)
                    ifBan.value = response.data.ifBan;
                    Swal.fire("解除封鎖成功", "", "success");
                }).catch(function (error) {
                    console.log("取消封鎖失敗" + error);
                    Swal.fire("解除封鎖失敗", "", "error");
                });
        }
    });
}

//查詢好友、追蹤、粉絲清單、封鎖列表都共用showList()，用參數辨別 

function showList(listUrl, listName) {
    const request = {
        headers: {
            'Authorization': userStore.token
        }
    };
    axiosapi.get(listUrl.value, request)
        .then(function (response) {
            //bannedUser改userDetails
            userDetails.value = response.data.map(detail => ({
                id: detail.oppositeId,
                name: detail.userName || `User ${detail.oppositeId}`,
                pic: detail.userPic
            }));
            showBanListModal.value = true;
            console.log(response.data);

            switch (listName.value) {
                case "showMutualMatching":
                    listType.value = "好友列表";
                    listMessage.value = "目前還沒有好友唷";
                    break;
                case "showFolloweeList":
                    listType.value = "追蹤列表";
                    listMessage.value = "目前還沒有追蹤任何人";
                    break;
                case "showFollowerList":
                    listType.value = "粉絲列表";
                    listMessage.value = "目前還沒有任何粉絲";
                    break;
                case "showBanList":
                    listType.value = "封鎖列表";
                    listMessage.value = "目前沒有封鎖任何用戶";
                    break;
            }
        })
        .catch(function (error) {
            // console.error("獲取封鎖列表失敗:", error);
            Swal.fire("錯誤", `無法獲取${listName.value}`, "error");
        });
}

function showBanList() {
    listUrl.value = '/users/ban/list';
    listName.value = 'showBanList';
        showList(listUrl, listName)
}

function showMutualMatching(){
    listUrl.value = '/secure/users/mutualmatching';
    listName.value = 'showMutualMatching';
        showList(listUrl, listName)
}

function showFolloweeList(){
    listUrl.value = '/secure/users/followeeList'; 
    listName.value = 'showFolloweeList';
        showList(listUrl, listName)
}

function showFollowerList(){
    listUrl.value = '/secure/users/followerList';
    listName.value = 'showFollowerList';
        showList(listUrl, listName)
}

function showBanListSpare() {
    const request = {
        headers: {
            'Authorization': userStore.token
        }
    };

    axiosapi.get('/users/ban/list', request)
        .then(function (response) {
            bannedUsers.value = response.data.map(ban => ({
                id: ban.banedUserId,
                name: ban.banedUserName || `User ${ban.banedUserId}`,
                pic: ban.banedUserPic
            }));
            showBanListModal.value = true;
            console.log(response.data);
        })
        .catch(function (error) {
            // console.error("獲取封鎖列表失敗:", error);
            Swal.fire("錯誤", "無法獲取封鎖列表", "error");
        });
}

function closeBanList() {
    showBanListModal.value = false;
}

const checkReportStatus = async () => {
    try {
        const response = await axiosapi.get(`/report/status/U/${userId.value}`, {
            headers: { 'Authorization': userStore.token }
        });
        ifReport.value = response.data.ifReport;
        // console.log('ifReport.value:'+ifReport.value)
        // console.log('userId.value:'+userId.value)
    } catch (error) {
        console.error("Error checking report status:", error);
    }
};

const report = async (reportData) => {
    try {
        const requestData = {
            reportItem: 'U',
            reportItemId: reportData.reportedUserId,
            reportType: reportData.reportType,
            reportReason: reportData.reportReason
            // reportStatus: reportData.reportStatus
        };
        console.log("Sending report data:", requestData);

        const response = await axiosapi.post('/report/add', requestData, {
            headers: {
                'Authorization': userStore.token,
                'Content-Type': 'application/json'
            }
        });
        console.log("Report response:", response.data);
        ifReport.value = true;
        // Swal.fire("檢舉成功", "我們會盡快審核您的檢舉", "success");
    } catch (error) {
        Swal.fire("檢舉失敗", `請稍後再試。錯誤: ${error.message}`, "error");
    }
};

function showReportList() {
    const request = {
        headers: {
            'Authorization': userStore.token
        }
    };

    axiosapi.get('/user/report/list', request)
        .then(function (response) {
            if (Array.isArray(response.data)) {
                reportedItems.value = response.data.map(item => ({
                    ...item,
                    reportedContent: item.reportedContent
                }));
            } else {
                reportedItems.value = [];
            }
            showReportListModal.value = true;
        })
        .catch(function (error) {
            console.error("獲取檢舉列表失敗:", error);
            Swal.fire("錯誤", "無法獲取檢舉列表", "error");
        });
}

function closeReportList() {
    showReportListModal.value = false;
}

function cancelReport(reportItemId, reportItem) {
    const request = {
        headers: {
            'Authorization': userStore.token
        },
        data: {
            reportItemId: reportItemId,
            reportItem: reportItem
        }
    };

    axiosapi.delete('/user/report/cancel', {
        headers: request.headers,
        data: request.data
    })
        .then(function () {
            console.log("成功取消檢舉");
            reportedItems.value = reportedItems.value.filter(item => !(item.reportItemId === reportItemId && item.reportItem === reportItem));
            Swal.fire("取消檢舉成功", "", "success");
        })
        .catch(function (error) {
            console.log("取消檢舉失敗" + error);
            Swal.fire("取消檢舉失敗", "", "error");
        });
}

//activity的
function createActivity() {
    console.log("創建新活動");
    // 實現創建新活動的邏輯
}

function manageActivities() {
    console.log("管理我的活動");
    // 實現管理活動的邏輯
}

function checkMutualMatching() {

}

</script>

<style scoped>
/* .my-profile-nav-left-menu {
    position: fixed;
    float: left;
    height: 100%;
    left: 30px;
    margin-top: 50px;
} */

.container-fluid {
    padding-top: 20px;
}

/*activity*/
.nav-item.dropdown:hover>.dropdown-menu {
    display: block;
}

.dropdown-menu {
    margin-top: 0;
}

.btn-outline-secondary {
    width: 100%;
    text-align: left;
}

.dropdown-item {
    cursor: pointer;
}

.dropdown-toggle::after {
    float: right;
    margin-top: 8px;
}
</style>
