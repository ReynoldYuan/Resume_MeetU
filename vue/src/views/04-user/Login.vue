<template>
        <div class="ts-app-center">
            <div class="content">
                <div class="ts-header is-large is-heavy is-icon has-top-spaced-large">
                    <div class="ts-icon">
                        <font-awesome-icon icon="champagne-glasses" bounce  style="color: #86b817; margin: 20px;" />
                        <h1 style="color: #86b817;">MeetU</h1>
                        
                    </div>
                    
                </div>
                <div class="ts-box has-top-spaced-large" style="width: 260px">
                    <div class="ts-content">
                        <div class="ts-wrap is-vertical">
                            <div class="ts-text is-label">使用者帳號</div>
                            <div class="ts-input is-start-icon is-underlined">
                                <span class="ts-icon is-user-icon"></span>
                                <input type="email" name="username" v-model="username" @change="callresponse">
                            </div>
                            <div class="error" style="color: red;" v-if="message == '帳號不存在'">{{ message }}</div>
                            <div class="ts-text is-label">密碼</div>
                            <div class="ts-input is-start-icon is-underlined">
                                <span class="ts-icon is-lock-icon"></span>
                                <input type="password" name="password" v-model="password">
                            </div>
                            <button class="btn btn-primary rounded-pill py-2 px-4" @click="login">登入</button>
                            <RouterLink to="/register" class="btn btn-primary rounded-pill py-2 px-4">註冊</RouterLink>
                            <div class="ts-divider is-center-text">
                                <div class="ts-text is-description">
                                或是透過下列方式登入
                                </div>
                            </div>
                            <button class="btn btn-primary rounded-pill py-2 px-4" style="background-color: red;color: white; border-radius: 20px; outline: none; border: none;" @click="gotoGoogleLoginPage">
                                <span class="ts-icon is-google-icon"></span> Google
                            </button>
                            <RouterLink to="/account">忘記密碼?</RouterLink>
                        </div>

                         <!-- This is for demo use -->
                        <button class="btn btn-outline-success" style="margin-right: 10px;" @click="doInput">華夫人</button>
                        <button class="btn btn-outline-success" style="margin-right: 10px;" @click="doInput2">伯虎帳號</button>
                        <button class="btn btn-outline-success" @click="doInput3">秋香帳號</button>
                    </div>
                </div>
            </div>
        </div>
</template>
    
<script setup>
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';
    import axiosapi from '@/plugins/axios.js';
    import Swal from 'sweetalert2';
    import useUserStore from '@/stores/user.js';

    const router = useRouter();
    const username = ref("");
    const password = ref("");
    const message = ref("");
    const userStore = useUserStore();

    function login(){
        message.value = "";
        userStore.setEmail("");
        userStore.setLogedin(false);
        axiosapi.defaults.headers.authorization = "";

        Swal.fire({
                text:"Loading......",
                allowOutsideClick:false,
                showConfirmButton:false
        });

        if(username.value == ""){
                username.value = null;
        }
        if(password.value == ""){
                password.value = null;
        }
        let request = {
                userMail: username.value,
                userPwd: password.value
        }
        axiosapi.post("/users/loginPost", request).then(function(response){
                if(response.data.success){
                        Swal.fire({
                                icon:"success",
                                text: response.data.message,
                                allowOutsideClick: false
                        }).then(function(result){
                                axiosapi.defaults.headers.authorization = "Bearer " + response.data.token;
                                userStore.setToken(axiosapi.defaults.headers.authorization);
                                userStore.setId(response.data.userId);
                                userStore.setEmail(response.data.userMail);
                                userStore.setPwd(response.data.userPwd);
                                userStore.setVip(response.data.vip);
                                userStore.setBan(response.data.userIsBan);
                                userStore.setName(response.data.userName);
                                userStore.setGender(response.data.userGender);
                                userStore.setPics(response.data.userPics);
                                userStore.setBirth(response.data.userBirth);
                                userStore.setLocation(response.data.userLocation);
                                userStore.setJob(response.data.userJob);
                                userStore.setJobPosi(response.data.userJobPosi);
                                userStore.setIntroduction(response.data.userIntroduction);
                                userStore.setPreferAct(response.data.userPreferAct);
                                userStore.setPreferGen(response.data.userPreferGen);
                                userStore.setFind(response.data.userFind);
                                userStore.setPreferAgeMax(response.data.userPreferAgeMax);
                                userStore.setPreferAgeMin(response.data.userPreferAgeMin);
                                userStore.setHobby(response.data.userHobby);
                                userStore.setLogedin(true);
                                router.push("/");
                        })
                }
                else{
                        message.value = response.data.message;
                        Swal.fire({
                                icon:"error",
                                text: response.data.message + "，密碼錯誤"
                        })
                }
        }).catch(function(error){
                Swal.fire({
                        icon:"error",
                        text:"登入失敗:"+error.message
                })
        })
    }

    function gotoGoogleLoginPage(){
        window.location.href = "http://localhost:8080/meetu/google-login";
    }

    function callresponse(){
        let request = {
                userMail: username.value
        }
        axiosapi.post("/users/checkMail", request).then(function(response){
                if(response.data.success){
                    message.value = response.data.message;
                }
                else{
                    message.value = response.data.message;
                }
        }).catch(function(error){
                message.value = error.message
        })
    }


    // This is for demo use
    function doInput(){
        username.value = 'demo@gmail.com'
        password.value = 'demo'
    }
    function doInput2(){
        username.value = 'abc123@email.com'
        password.value = 'password1'
    }
    function doInput3(){
        username.value = 'def123@email.com'
        password.value = 'password1'
    }
</script>
    
<style scoped>
    
</style>