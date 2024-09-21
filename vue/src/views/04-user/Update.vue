<template>
    <div class="container-fluid">
        <div class="col-12">
            <UserSidebar />
        </div>

    <div v-if="security">
    <div class="ts-divider has-top-spaced" style="margin: 0px 290px;"></div>
    <!-- <div class="ts-content is-tertiary is-vertically-padded" style="margin: 0px 290px;"> -->
    <div class="ts-content is-tertiary" style="margin: 0px 290px;">
        <div class="ts-container is-very-narrow has-vertically-spaced">
            <div class="ts-header is-big is-heavy">修改個人資料</div>
            <div class="ts-text is-secondary">修改資料</div>
        </div>
    </div>
    <div class="ts-divider" style="margin: 0px 290px;"></div>
    <div class="ts-container is-very-narrow has-vertically-padded-small">
    <form @submit.prevent="callUpdate">
        <fieldset class="ts-fieldset" style="background-color: white">
        <legend>基本資料</legend>
        <div class="ts-grid is-relaxed is-2-columns">
            <div class="column">
                <div class="ts-text is-label">使用者帳號</div>
                <div class="ts-input is-basic has-top-spaced">
                    <input type="email" style="font-size: 16px;" v-model="userJson.userMail" required readonly>
                </div>
            </div>
            <div class="column">
                <div class="ts-text is-label is-required">姓名</div>
                <div class="ts-input is-underlined has-top-spaced">
                    <input type="text" v-model="userJson.usersProfile.userName" required />
                </div>
                <div class="ts-text is-description has-top-spaced-small">不得超過20個字。</div>
            </div>
        </div>
        <div class="ts-text is-label has-top-spaced-large is-required">密碼</div>
            <div class="ts-input is-underlined has-top-spaced">
                <input type="password" v-model="userJson.userPwd" required>
            </div>
            <div class="ts-text is-description has-top-spaced-small">僅能是英文字母和數字，最多 100 字。</div>
        <div class="ts-grid is-relaxed is-2-columns has-top-spaced-large">
            <div class="column">
                <div class="ts-text is-label is-required">生日</div>
                <div class="has-top-spaced">
                    <div class="column">
                        <div class="ts-input is-underlined is-fluid">
                            <input type="date" v-model="userJson.usersProfile.userBirth" required>
                        </div>
                        <div class="ts-text is-description has-top-spaced-small">要滿18歲以上</div>
                    </div>
                </div>
            </div>
            <div class="column">
                <div class="ts-text is-label is-required">性別</div>
                <div class="ts-wrap is-center-aligned has-top-spaced">
                    <label class="ts-radio">
                        <input name="gender" type="radio" value="M" v-model="userJson.usersProfile.userGender" required />
                        男性
                    </label>
                    <label class="ts-radio">
                        <input name="gender" type="radio" value="F" v-model="userJson.usersProfile.userGender" required />
                        女性
                    </label>
                </div>
            </div>
        </div>
        </fieldset>
        <fieldset class="ts-fieldset has-top-spaced-large" style="background-color: white">
        <legend>詳細資料</legend>
        <div class="ts-grid is-relaxed is-2-columns has-top-spaced-large">
            <div class="column">
                <div class="ts-text is-label is-required">工作</div>
                <div class="has-top-spaced">
                    <div class="ts-select is-underlined is-fluid">
                        <select v-model="userJson.usersProfile.userJob" required>
                            <option value="經營／人資類">經營／人資類</option>
                            <option value="行政／總務／法務類">行政／總務／法務類</option>
                            <option value="財會／金融專業類">財會／金融專業類</option>
                            <option value="行銷／企劃／專案管理類">行銷／企劃／專案管理類</option>
                            <option value="客服／門市／業務／貿易類">客服／門市／業務／貿易類</option>
                            <option value="餐飲／旅遊 ／美容美髮類">餐飲／旅遊 ／美容美髮類</option>
                            <option value="資訊軟體系統類">資訊軟體系統類</option>
                            <option value="操作／技術／維修類">操作／技術／維修類</option>
                            <option value="資材／物流／運輸類">資材／物流／運輸類</option>
                            <option value="營建／製圖類">營建／製圖類</option>
                            <option value="傳播藝術／設計類">傳播藝術／設計類</option>
                            <option value="文字／傳媒工作類">文字／傳媒工作類</option>
                            <option value="醫療／保健服務類">醫療／保健服務類</option>
                            <option value="學術／教育／輔導類">學術／教育／輔導類</option>
                            <option value="研發相關類">研發相關類</option>
                            <option value="生產製造／品管／環衛類">生產製造／品管／環衛類</option>
                            <option value="軍警消／保全類">軍警消／保全類</option>
                            <option value="其他職類">其他職類</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="column">
                <div class="ts-text is-label is-required">職務</div>
                <div class="has-top-spaced">
                    <div class="ts-select is-underlined is-fluid">
                        <select v-model="userJson.usersProfile.userJobPosi" required>
                            <option value="老闆">老闆</option>
                            <option value="首席執行官 (CEO)">首席執行官 (CEO)</option>
                            <option value="首席技術官 (CTO)">首席技術官 (CTO)</option>
                            <option value="首席財務官 (CFO)">首席財務官 (CFO)</option>
                            <option value="總經理">總經理</option>
                            <option value="總監">總監</option>
                            <option value="經理">經理</option>
                            <option value="主管">主管</option>
                            <option value="主任">主任</option>
                            <option value="組長">組長</option>
                            <option value="工程師">工程師</option>
                            <option value="一般職員">一般職員</option>
                            <option value="學生">學生</option>
                            <option value="其他">其他</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
        <div class="ts-grid is-relaxed is-2-columns has-top-spaced-large">
            <div class="column">
                <div class="ts-text is-label is-required">居住地</div>
                <div class="has-top-spaced">
                    <div class="ts-select is-underlined is-fluid">
                        <select v-model="userJson.usersProfile.userLocation" required>
                            <option value="基隆市">基隆市</option>
                            <option value="新北市">新北市</option>
                            <option value="台北市">台北市</option>
                            <option value="桃園市">桃園市</option>
                            <option value="新竹縣">新竹縣</option>
                            <option value="苗栗縣">苗栗縣</option>
                            <option value="台中市">台中市</option>
                            <option value="南投縣">南投縣</option>
                            <option value="彰化縣">彰化縣</option>
                            <option value="雲林縣">雲林縣</option>
                            <option value="嘉義縣">嘉義縣</option>
                            <option value="台南市">台南市</option>
                            <option value="高雄市">高雄市</option>
                            <option value="屏東縣">屏東縣</option>
                            <option value="宜蘭縣">宜蘭縣</option>
                            <option value="花蓮縣">花蓮縣</option>
                            <option value="台東縣">台東縣</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="column">
                <div class="ts-text is-label is-required">感興趣的聚會</div>
                <div class="ts-wrap is-center-aligned has-top-spaced">
                    <label class="ts-radio">
                        <input type="radio" name="preferAct" value="I" v-model="userJson.usersProfile.userPreferAct" required />
                        室內
                    </label>
                    <label class="ts-radio">
                        <input type="radio" name="preferAct" value="O" v-model="userJson.usersProfile.userPreferAct" required />
                        室外
                    </label>
                    <label class="ts-radio">
                        <input type="radio" name="preferAct" value="M" v-model="userJson.usersProfile.userPreferAct" required />
                        混合
                    </label>
                </div>
            </div>
        </div>
        <div class="ts-grid is-relaxed is-2-columns has-top-spaced-large">
            <div class="column">
                <div class="ts-text is-label">想配對的性別</div>
                <div class="ts-wrap is-center-aligned has-top-spaced">
                    <label class="ts-radio">
                        <input type="radio" name="preferGen" value="M" v-model="userJson.usersProfile.userPreferGen" required />
                        男
                    </label>
                    <label class="ts-radio">
                        <input type="radio" name="preferGen" value="F" v-model="userJson.usersProfile.userPreferGen" required />
                        女
                    </label>
                    <label class="ts-radio">
                        <input type="radio" name="preferGen" value="N" v-model="userJson.usersProfile.userPreferGen" required />
                        不限
                    </label>
                </div>
            </div>
            <div class="column">
                <div class="ts-text is-label">交友偏好</div>
                <div class="ts-wrap is-center-aligned has-top-spaced">
                    <label class="ts-radio">
                        <input type="radio" name="find" value="E" v-model="userJson.usersProfile.userFind" required />
                        另外一半
                    </label>
                    <label class="ts-radio">
                        <input type="radio" name="find" value="C" v-model="userJson.usersProfile.userFind" required />
                        同好
                    </label>
                    <label class="ts-radio">
                        <input type="radio" name="find" value="N" v-model="userJson.usersProfile.userFind" required />
                        不限
                    </label>
                </div>
            </div>
        </div>
        <div class="ts-wrap is-vertical has-top-spaced-large">
            <div class="ts-text is-label">興趣</div>
            <div class="ts-wrap">
                <label class="ts-checkbox"><input type="checkbox" value="游泳" v-model="userJson.usersProfile.userHobby" />游泳</label>
                <label class="ts-checkbox"><input type="checkbox" value="旅遊" v-model="userJson.usersProfile.userHobby" />旅遊</label>
                <label class="ts-checkbox"><input type="checkbox" value="畫畫" v-model="userJson.usersProfile.userHobby" />畫畫</label>
                <label class="ts-checkbox"><input type="checkbox" value="爬山" v-model="userJson.usersProfile.userHobby" />爬山</label>
                <label class="ts-checkbox"><input type="checkbox" value="閱讀" v-model="userJson.usersProfile.userHobby" />閱讀</label>
                <label class="ts-checkbox"><input type="checkbox" value="電影" v-model="userJson.usersProfile.userHobby" />電影</label>
                <label class="ts-checkbox"><input type="checkbox" value="音樂" v-model="userJson.usersProfile.userHobby" />音樂</label>
                <label class="ts-checkbox"><input type="checkbox" value="烹飪" v-model="userJson.usersProfile.userHobby" />烹飪</label>
                <label class="ts-checkbox"><input type="checkbox" value="攝影" v-model="userJson.usersProfile.userHobby" />攝影</label>
                <label class="ts-checkbox"><input type="checkbox" value="遊戲" v-model="userJson.usersProfile.userHobby" />遊戲</label>
                <label class="ts-checkbox"><input type="checkbox" value="科技" v-model="userJson.usersProfile.userHobby" />科技</label>
                <label class="ts-checkbox"><input type="checkbox" value="健身" v-model="userJson.usersProfile.userHobby" />健身</label>
                <label class="ts-checkbox"><input type="checkbox" value="動物照顧" v-model="userJson.usersProfile.userHobby" />動物照顧</label>
                <label class="ts-checkbox"><input type="checkbox" value="語言學習" v-model="userJson.usersProfile.userHobby" />語言學習</label>
                <label class="ts-checkbox"><input type="checkbox" value="時尚" v-model="userJson.usersProfile.userHobby" />時尚</label>
                <label class="ts-checkbox"><input type="checkbox" value="園藝" v-model="userJson.usersProfile.userHobby" />園藝</label>
                <label class="ts-checkbox"><input type="checkbox" value="寫作" v-model="userJson.usersProfile.userHobby" />寫作</label>
                <label class="ts-checkbox"><input type="checkbox" value="手工藝" v-model="userJson.usersProfile.userHobby" />手工藝</label>
            </div>
        </div>
        <div class="mb-3 has-top-spaced-large">
            <label for="InputMaxAge1" class="form-label">想配對的最大年齡</label>
            <input type="range" class="form-range" min="18" max="100" v-model="userJson.usersProfile.userPreferAgeMax" id="InputMaxAge1">
            <span>{{ userJson.usersProfile.userPreferAgeMax }}</span>
        </div>
        <div class="mb-3">
            <label for="InputMinAge1" class="form-label">想配對的最小年齡</label>
            <input type="range" class="form-range" min="18" max="100" v-model="userJson.usersProfile.userPreferAgeMin" id="InputMinAge1">
            <span>{{ userJson.usersProfile.userPreferAgeMin }}</span>
        </div>
        <div class="ts-text is-label has-top-spaced-large">自我介紹</div>
        <div class="ts-input is-resizable has-top-spaced">
            <textarea placeholder="大家好，我是..." v-model="userJson.usersProfile.userIntroduction"></textarea>
        </div>
        <div class="ts-text is-label has-top-spaced-large is-required">個人照片</div>
        <div class="ts-file is-underlined has-top-spaced">
            <input type="file" @change="handleFileUpload" accept="image/*" />
        </div>
        <div class="ts-image is-circular is-bordered has-top-spaced-large">
            <img src="../../assets/images/noimage.png" ref="img" width="150">
        </div>
        </fieldset>
        <button type="submit" class="ts-button is-fluid has-top-spaced-large">儲存</button>
        <RouterLink :to="loggedInItems.path" class="ts-button is-fluid has-top-spaced-large">返回</RouterLink>
    </form>
    </div>
    </div>
    <div v-else>
        <h1>sorry...你沒有權限</h1>
    </div>
    </div>
</template>
    
<script setup>
    import useUserStore from "@/stores/user.js"
    import { ref, onMounted } from 'vue';
    import axiosapi from '@/plugins/axios.js';
    import Swal from 'sweetalert2';
    import { useRoute } from "vue-router";
    import { useRouter } from 'vue-router';
    import UserSidebar from "@/components/02-user/UserSidebar.vue";

    const route = useRoute();
    const userId = ref(0);
    const security = ref(true);

    onMounted(function(){
        userId.value = parseInt(route.params.userId);
        fetchUser();
    });
    
    function fetchUser(){
        let request = {
            headers : {
                'Authorization' : userStore.token
            }
        }
        if(userId.value == userStore.id){
            security.value = true;
            axiosapi.get("/users/profile/" + userId.value, request).then(function(response){
                userJson.value.userMail = response.data.userMail;
                userJson.value.userPwd = response.data.userPwd;
                userJson.value.usersProfile.userName = response.data.usersProfile.userName;
                userJson.value.usersProfile.userGender = response.data.usersProfile.userGender;
                userJson.value.usersProfile.userBirth = response.data.usersProfile.userBirth;
                userJson.value.usersProfile.userLocation = response.data.usersProfile.userLocation;
                userJson.value.usersProfile.userJob = response.data.usersProfile.userJob;
                userJson.value.usersProfile.userJobPosi = response.data.usersProfile.userJobPosi;
                userJson.value.usersProfile.userHobby = response.data.usersProfile.userHobby.split(',');
                userJson.value.usersProfile.userPreferAct = response.data.usersProfile.userPreferAct;
                userJson.value.usersProfile.userPreferGen = response.data.usersProfile.userPreferGen;
                userJson.value.usersProfile.userFind = response.data.usersProfile.userFind;
                userJson.value.usersProfile.userPreferAgeMax = response.data.usersProfile.userPreferAgeMax;
                userJson.value.usersProfile.userPreferAgeMin = response.data.usersProfile.userPreferAgeMin;
                userJson.value.usersProfile.userIntroduction = response.data.usersProfile.userIntroduction;
                img.value.src = response.data.usersProfile.userPics;
            }).catch(function (error) {
                console.log(userId.value);
                console.log("callFindById error", error);
                router.push({ name: 'forbidden-link' });
            });
        }
        else{
            console.log(userStore.id);
            security.value = false;
        }
    }

    const userStore = useUserStore();
    const loggedInItems = { icon: ['fas', 'user'], path: { name: 'myprofile-link', params: { userId: userStore.id } } };

    // 用於保存表單數據的 ref
    const userJson = ref({
        userId: userStore.id,
        userMail: "",
        userPwd: "",
        usersProfile: {
            userId: userStore.id,
            userName: "",
            userGender: "",
            userBirth: "",
            userLocation: "",
            userJob: "",
            userJobPosi: "",
            userHobby: [],
            userPreferAct: "",
            userPreferGen: "",
            userFind: "",
            userPreferAgeMax: "",
            userPreferAgeMin: "",
            userIntroduction: "",
        }
    });

    const file = ref(null);
    const img = ref(null);

    function handleFileUpload(event) {
        const input = event.target;
        if (input.files && input.files.length > 0) {
            file.value = input.files[0];
            const tempURL = URL.createObjectURL(file.value);
            img.value.src = tempURL;
        }
    }

    const router = useRouter();

    async function callUpdate() {
        Swal.fire({
            icon: "warning",
            title: "確定要修改資料嗎?",
            showDenyButton: true,
            confirmButtonText: "修改",
            denyButtonText: `不修改`
        }).then((result) => {
        const today = new Date();
        const birth = new Date(userJson.value.usersProfile.userBirth);

        const birthYear = birth.getFullYear();
        const birthMonth = birth.getMonth(); // 注意：月份從0開始，所以1月是0
        const birthDay = birth.getDate();
        
        const currentYear = today.getFullYear();
        const currentMonth = today.getMonth();
        const currentDay = today.getDate();

        let age = currentYear - birthYear;
  
        // 調整年齡以考慮是否已經過了今年的生日
        if (currentMonth < birthMonth || (currentMonth === birthMonth && currentDay < birthDay)) {
            age--;
            if(age < 18){
                Swal.fire({
                    icon: 'error',
                    title: '錯誤',
                    text: '修改失敗，年齡小於18歲。'
                });
            }
        }
        else if(userJson.value.userPwd.length > 100){
            Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '修改失敗，密碼錯誤或長度過長。'
            });
        }
        else if(userJson.value.usersProfile.userName.length > 20){
            Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '修改失敗，姓名長度超過20字。'
            });
        }
        else{
                /* Read more about isConfirmed, isDenied below */
                if (result.isConfirmed) {
                    // 準備發送表單數據
                    // 將表單數據轉換為 JSON 字串
                    userJson.value.usersProfile.userHobby = userJson.value.usersProfile.userHobby.toString();
                    const jsonPart = JSON.stringify(userJson.value);
                    const formDataToSend = new FormData();
            
                    // 添加 JSON 字串和文件到 FormData
                    formDataToSend.append('userJson', jsonPart);
                    formDataToSend.append('userPics', file.value);
            
                    try {
                        // 發送 POST 請求到後端
                        axiosapi.put('/users/profile', formDataToSend, {
                            headers: {
                                'Content-Type': 'multipart/form-data'
                            }
                        }).then(function(response){
                            console.log(response);
                            // 顯示成功提示
                            Swal.fire({
                                icon: 'success',
                                title: '成功',
                                text: '修改成功！'
                            }).then(function(){
                                userStore.name = userJson.value.usersProfile.userName;
                                userStore.pics = img.value.src;
                                router.push({name: 'myprofile-link', params: { userId: userStore.id }});
                            });
                        })
                    } catch (error) {
                        // 顯示錯誤提示
                        console.error('錯誤信息:', error.response?.data || error.message);
                        Swal.fire({
                            icon: 'error',
                            title: '修改失敗',
                            text: error.message
                        });
                    }
                } else if (result.isDenied) {
                    Swal.fire("Changes are not saved", "", "info");
                }
            }
        });
    }
</script>
    
<style>
.container-fluid {
    padding-top: 20px;
}

</style>