<template>
    <div class="change-password-page">
        <div class="ts-divider" style="margin: 0px 290px;"></div>
        <div class="ts-content is-tertiary is-vertically-padded" style="margin: 0px 290px;">
            <div class="ts-container is-very-narrow has-vertically-spaced">
                <div class="ts-header is-big is-heavy">修改密碼</div>
                <div class="ts-text is-secondary">請輸入您的帳號</div>
            </div>
        </div>
        <div class="ts-divider" style="margin: 0px 290px;"></div>
        <div class="ts-container is-very-narrow has-vertically-padded-big">
            <div class="ts-text is-label has-top-spaced-large">帳號</div>
            <div class="ts-input is-underlined has-top-spaced">
                <input type="text" v-model="username"/>
            </div>
            <div class="ts-text is-description has-top-spaced-small">請輸入您的帳號</div>
            <div v-if="showInput">
                <div class="ts-text is-label has-top-spaced-large">驗證碼</div>
                <div class="ts-input is-underlined has-top-spaced">
                    <input type="text" v-model="captcha"/>
                </div>
                發送成功! 請輸入收到的驗證碼!<br>
                沒收到驗證碼 ? 那就在發送一次 => <button @click="sendCaptchaMail" style="color: red;">重新發送驗證碼</button>
            </div>
            <button class="has-top-spaced-large" v-else @click="sendCaptchaMail" style="color: red;">發送驗證碼</button>
            <button class="ts-button is-fluid has-top-spaced-large" @click="login" v-if="showInput">下一步</button>
            <button class="ts-button is-fluid has-top-spaced-large" @click="login" disabled v-else>下一步</button>
            <button class="btn btn-primary rounded-pill py-2 px-4" @click="doInput">一鍵輸入帳號</button>
        </div>
    </div>
</template>
    
<script setup>
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';
    import axiosapi from '@/plugins/axios.js';
    import Swal from 'sweetalert2';

    const router = useRouter();
    const username = ref("");
    const captcha = ref("");
    const showInput = ref(false);

    function login(){
        axiosapi.defaults.headers.authorization = "";

        Swal.fire({
            text:"Loading......",
            allowOutsideClick:false,
            showConfirmButton:false
        });

        if(username.value == ""){
            username.value = null;
        }
        if(captcha.value == ""){
            captcha.value = null;
        }
        let request = {
            userMail: username.value,
            captcha: captcha.value
        }
        axiosapi.post("/users/checkMailAndCaptcha", request).then(function(response){
            if(response.data.success){
                Swal.fire({
                        icon:"success",
                        text: response.data.message,
                        allowOutsideClick: false
                }).then(function(result){
                    router.push({path: "/updatePwd", query: { userMail: username.value } });
                })
            }
            else{
                Swal.fire({
                        icon:"error",
                        text: response.data.message
                })
            }
        }).catch(function(error){
            Swal.fire({
                icon:"error",
                text:"失敗:"+error.message
            })
        })
    }

    function sendCaptchaMail(){
        axiosapi.get("/users/sendMail").then(function(response){
            if(response.data === "發送成功"){
                showInput.value = true;
            }
        }).catch(function(error){
            Swal.fire({
                icon:"error",
                text:"失敗:"+error.message
            })
        })
    }

    function doInput(){
        username.value = 'demo@gmail.com'
    }
</script>
    
<style scoped>
    
</style>