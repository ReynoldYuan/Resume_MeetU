<template>
    <div class="change-password-page">
        <div class="ts-divider" style="margin: 0px 290px;"></div>
        <div class="ts-content is-tertiary is-vertically-padded" style="margin: 0px 290px;">
            <div class="ts-container is-very-narrow has-vertically-spaced">
                <div class="ts-header is-big is-heavy">修改密碼</div>
                <div class="ts-text is-secondary">忘記密碼了嗎?那就建立新的密碼取代舊的密碼吧!</div>
            </div>
        </div>
        <div class="ts-divider" style="margin: 0px 290px;"></div>
        <div class="ts-container is-very-narrow has-vertically-padded-big">
            <div class="ts-text is-label has-top-spaced-large">密碼</div>
            <div class="ts-input is-underlined has-top-spaced">
                <input type="password" v-model="newPwd"/>
            </div>
            <div class="ts-text is-label has-top-spaced-large">確認密碼</div>
            <div class="ts-input is-underlined has-top-spaced">
                <input type="password" v-model="checkPwd"/>
            </div>
            <div class="ts-text is-description has-top-spaced-small">僅能是英文字母和數字，最多 100 字。</div>
            <button class="ts-button is-fluid has-top-spaced-large" @click="login">下一步</button>
            <button class="btn btn-primary rounded-pill py-2 px-4" @click="doInput">一鍵輸入密碼</button>
        </div>
    </div>
</template>
    
<script setup>
    import { ref } from 'vue';
    import { useRouter, useRoute } from 'vue-router';
    import axiosapi from '@/plugins/axios.js';
    import Swal from 'sweetalert2';

    const router = useRouter();
    const route = useRoute();
    const userMail = route.query.userMail;
    const newPwd = ref("");
    const checkPwd = ref("");

    function login(){
        axiosapi.defaults.headers.authorization = "";

        Swal.fire({
            text:"Loading......",
            allowOutsideClick:false,
            showConfirmButton:false
        });

        if(newPwd.value == checkPwd.value && newPwd.value.length <= 100 && checkPwd.value.length <= 100){
            let request = {
                userMail: userMail,
                userPwd: checkPwd.value
            }

            axiosapi.put("/users/checkPassword", request).then(function(response){
                if(response.data.success){
                    Swal.fire({
                            icon:"success",
                            text: response.data.message,
                            allowOutsideClick: false
                    }).then(function(result){
                        router.push("/login");
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
        else{
            Swal.fire({
                icon: 'error',
                title: '錯誤',
                text: '修改失敗，密碼錯誤或長度過長。'
            });
        }
    }

    function doInput(){
        newPwd.value = 'demo';
        checkPwd.value = 'demo';
    }
</script>
    
<style>
    
</style>