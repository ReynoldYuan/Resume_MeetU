import { ref } from "vue";
import { defineStore } from "pinia";

const user = defineStore("user", function(){
    const token = ref("");
    function setToken(data){
        token.value = data;
    }
    
    const id = ref("");
    function setId(data){
        id.value = data;
    }
    
    const email = ref("");
    function setEmail(data){
        email.value = data;
    }

    const pwd = ref("");
    function setPwd(data){
        pwd.value = data;
    }

    const vip = ref("");
    function setVip(data){
        vip.value = data;
    }

    const ban = ref("");
    function setBan(data){
        ban.value = data;
    }

    const name = ref("");
    function setName(data){
        name.value = data;
    }

    const gender = ref("");
    function setGender(data){
        gender.value = data;
    }

    const pics = ref("");
    function setPics(data){
        pics.value = data;
    }

    const birth = ref("");
    function setBirth(data){
        birth.value = data;
    }

    const location = ref("");
    function setLocation(data){
        location.value = data;
    }

    const job = ref("");
    function setJob(data){
        job.value = data;
    }

    const jobPosi = ref("");
    function setJobPosi(data){
        jobPosi.value = data;
    }

    const introduction = ref("");
    function setIntroduction(data){
        introduction.value = data;
    }

    const preferAct = ref("");
    function setPreferAct(data){
        preferAct.value = data;
    }

    const preferGen = ref("");
    function setPreferGen(data){
        preferGen.value = data;
    }

    const find = ref("");
    function setFind(data){
        find.value = data;
    }

    const preferAgeMax = ref("");
    function setPreferAgeMax(data){
        preferAgeMax.value = data;
    }

    const preferAgeMin = ref("");
    function setPreferAgeMin(data){
        preferAgeMin.value = data;
    }

    const hobby = ref("");
    function setHobby(data){
        hobby.value = data;
    }

    const isLogedin = ref(false);
    function setLogedin(data){
        isLogedin.value = data;
    }

    return {token, setToken, id, setId, email, setEmail, pwd, setPwd, vip, setVip, ban, setBan, name, setName, gender, setGender, pics, setPics, birth, setBirth, location, setLocation, job, setJob, jobPosi, setJobPosi, introduction, setIntroduction, preferAct, setPreferAct, preferGen, setPreferGen, find, setFind, preferAgeMax, setPreferAgeMax, preferAgeMin, setPreferAgeMin, hobby, setHobby, isLogedin, setLogedin};
},
{
    persist:{
        storage: sessionStorage,
        paths: ["token", "id", "email", "pwd", "vip", "ban", "name", "gender", "pics", "birth", "location", "job", "jobPosi", "introduction", "preferAct", "preferGen", "find", "preferAgeMax", "preferAgeMin", "hobby", "isLogedin"]
    }
});

export default user;