<template>

    <div v-if="userStore.isLogedin" class="top-img" @click="openModal">
        <img src="http://localhost:8080/meetu/images/userPics/dailyMatching.webp" class="d-block w-100" alt="點擊開始配對">
        <div class="carousel-caption d-none d-md-block">
            <h1 style="font-size: 80px;">開始每日配對</h1>
            <h1 style="font-size: 80px;">尋找命定真愛</h1>
        </div>
    </div>

    <div v-else class="top-img">
        <RouterLink to="/login">
            <img src="http://localhost:8080/meetu/images/userPics/dailyMatching-nonlogin.jpeg" class="d-block w-100"
                alt="請先登入">
            <div class="carousel-caption d-none d-md-block">
                <h1 style="font-size: 50px;">　</h1>
                <h1 style="font-size: 80px;">先登入再開啟旅程！</h1>
            </div>
        </RouterLink>
    </div>

    <!-- Modal -->
    <teleport to="body">
        <div v-if="isModalOpen" @click="handleOutsideClick" class="modal fade show" id="exampleModal" tabindex="-1"
            aria-labelledby="exampleModalLabel" aria-hidden="true"
            style="display: block; background-color: rgba(0,0,0,0.5);">
            <div class="modal-dialog modal-xl">
                <div class="modal-content" @click.stop>
                    <div class="modal-header border-bottom-0">
                        <button type="button" class="btn-close" @click="closeModal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="container-fluid">
                            <div v-if="ifHaventFinish === true" class="row align-items-center">
                                <div class="col dislike" @click="dislike">
                                    <font-awesome-icon :icon="['fas', 'xmark']" />
                                </div>
                                <div class="col-7">
                                    <div v-if="currentCardIndex < matchingCards.length
                                        && ifHaventFinish === true">
                                        <UserCard :user-profile="{ data: matchingCards[currentCardIndex] }"
                                            :user-id="matchingCards[currentCardIndex].userId"
                                            :user-age="calculateAge(matchingCards[currentCardIndex].userBirth)"
                                            :star-sign="getChineseZodiac(matchingCards[currentCardIndex].userBirth)"
                                            :user-prefer-act="getUserPreferAct(matchingCards[currentCardIndex].userPreferAct)"
                                            :user-prefer-gen="getUserPreferGen(matchingCards[currentCardIndex].userPreferGen)"
                                            :if-following="null" :my-profile="null" :if-ban="null" :if-banned-me="null"
                                            :user-find="getUserFind(matchingCards[currentCardIndex].userFind)"
                                            :user-for-daily-matching="true" />
                                    </div>

                                </div>
                                <div v-if="ifHaventFinish === true" class="col like" @click="like">
                                    <font-awesome-icon :icon="['fas', 'heart']" />
                                </div>
                            </div>

                            <div v-if="ifHaventFinish === false || !userStore.isLogedin" class="row align-items-center text-center">
                                <div v-if="ifHaventFinish === false">
                                    <h1>今日配對已完成囉~</h1>
                                </div>
                                <div v-if="!userStore.isLogedin">
                                    <h1>要先登入才可以配對唷!!!</h1>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </teleport>

    <MatchingSuccessfully :show-match-success-modal="showMatchSuccessModal" @close="closeMatchSuccessModal" />


</template>



<script setup>
import UserCard from '@/components/02-user/UserCard.vue';
import { ref, onMounted } from 'vue';
import axiosapi from '@/plugins/axios.js';
import MatchingSuccessfully from '@/components/06-matching/MatchingSuccessfully.vue';

const showMatchSuccessModal = ref(false);
const closeMatchSuccessModal = () => {
    showMatchSuccessModal.value = false;
};


import useUserStore from "@/stores/user.js"
const userStore = useUserStore();


const matchingCards = ref([]);
const currentCardIndex = ref(0);
const ifHaventFinish = ref("");


onMounted(function () {
    if (userStore.id) {
        getMatchingCards();
    }
});

const isModalOpen = ref(false);

const openModal = () => {
    isModalOpen.value = true;
};

const closeModal = () => {
    isModalOpen.value = false;
};

function getMatchingCards() {
    const request = {
        headers: {
            'Authorization': userStore.token
        }
    }
    axiosapi.get('/secure/users/matchingcards', request
    ).then(function (response) {
        console.log("取得配對清單成功!");
        console.log(response)
        matchingCards.value = response.data.matchingCards;
        ifHaventFinish.value = response.data.ifHaventFinish;
        console.log(matchingCards.value)

    }).catch(function (error) {
        console.log("取得配對清單失敗!");
        console.log(error)

    });
}

const like = () => {
    matching(true);
};

const dislike = () => {
    matching(false);
};

const nextCard = () => {
    if (currentCardIndex.value < matchingCards.value.length - 1) {
        currentCardIndex.value++;
    } else {
        // 所有卡片都已显示，可以在这里添加处理逻辑
        ifHaventFinish.value = false;
        console.log("No more cards to show");
    }
};


function matching(likeOrNot) {
    const currentCard = matchingCards.value[currentCardIndex.value];
    const request = {
        headers: {
            'Authorization': userStore.token,
            'Content-Type': 'application/json'
        },
        data: {
            "userPreferId": currentCard.userId,
            "likeOrNot": likeOrNot,
            "matchedDate": new Date().toISOString().split('T')[0]
        }
    }
    axiosapi.post('/secure/users/matchingcards', request.data, { headers: request.headers }
    ).then(function (response) {
        console.log("發送配對結果成功!");
        console.log(response.data);
        if (response.data.MatchedDate !== null || response.data.MatchedDate !== '') {
            if (response.data.MatchedSuccessfullyDate && response.data.MatchedSuccessfullyDate.trim() !== '') {
                console.log("匹配成功，顯示成功訊息");
                showMatchSuccessModal.value = true;
            }
            nextCard();
        }
    }).catch(function (error) {
        console.log("發送配對結果失敗!");
        console.log(error);
    });
}

const handleOutsideClick = (event) => {
    if (event.target.classList.contains('modal')) {
        closeModal();
    }
};


// 處理取得後端數據後的顯示字串

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




</script>
<style scoped>
.top-img {
    position: relative;
    border-radius: 15px;
    cursor: pointer;
    overflow: hidden;
    /* 確保偽元素不會溢出 */
}

.top-img::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    color: rgba(255, 255, 255, 0.634);
    background-color: rgba(255, 192, 203, 0.3);
    /* 透明的粉紅色 */
    opacity: 0;
    /* 初始狀態下不可見 */
    transition: opacity 0.3s ease;
    /* 平滑過渡效果 */
}

.top-img:hover::after {
    opacity: 1;
    /* hover 時顯示覆蓋層 */
}

.top-img img {
    object-fit: cover;
    object-position: center;
    width: 100%;
    height: 600px;
    transition: transform 0.3s ease;
    /* 添加過渡效果 */
}

.top-img:hover img {
    transform: scale(1.05);
    /* hover 時輕微放大圖片 */
}

.carousel-caption {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    padding: 30px;
    width: 80%;
    max-width: 800px;
    z-index: 1;
    /* 確保文字在覆蓋層之上 */
}

/* 其餘的 CSS 保持不變 */
.modal.show {
    display: block;
}

.modal-body {
    padding-top: 0px;
}

.row {
    min-height: 400px;
}

.col.dislike,
.col.like {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 700px;
    transition: all 0.3s ease;
}

.col.dislike {
    color: grey;
}

.col.like {
    color: pink;
}

.col.dislike:hover {
    color: white;
    background-color: rgb(122, 122, 122);
}

.col.like:hover {
    background-color: rgb(239, 95, 109);
    color: white;
}

.dislike,
.like {
    font-size: 80px;
    cursor: pointer;
}
</style>