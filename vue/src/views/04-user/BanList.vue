<template>
    <div v-if="show" class="ban-list-overlay">
      <div class="ban-list-modal">
        <h2 class="text-center">{{ listType }}</h2>
        <ul v-if="userDetails.length > 0">
          <li v-for="user in userDetails" :key="user.id">
            <div class="user-row" @click="navigateToProfile(user.id)">
              <img :src="user.pic" :alt="`${user.name}的頭像`">
              <span class="user-name">{{ user.name }}</span>
              <button v-if="listType==='封鎖列表'" class="unban-button" @click.stop="handleUnban(user.id)">解除封鎖</button>
            </div>
          </li>
        </ul>
        <p v-else class="text-center">{{ listMessage }}</p>
        <p class="text-center" style="margin-bottom: 0px;"><button @click="handleClose">關閉</button></p>    
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  
  const props = defineProps({
    show: Boolean,
    userDetails: Array,
    listType: String,
    listMessage: String,
    showBanListModal: Boolean
  });
  
  const emit = defineEmits(['close', 'unban']);

  const handleClose = () => {
    emit('close');
  };
  
  const handleUnban = (userId) => {
  emit('unban', userId);
};
  
  const navigateToProfile = (userId) => {
    handleClose();
    router.push({ name: 'userprofile-link', params: { userId: userId } });
  }
  </script>
  
  <style scoped>
  .ban-list-overlay {
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
  
  .ban-list-modal {
    background-color: white;
    padding: 20px;
    border-radius: 5px;
    max-width: 500px;
    width: 100%;
    
  }
  
  ul {
    list-style-type: none;
    padding: 0;
  }
  
  li {
    margin-bottom: 10px;
  }

  li :hover{
    background-color: #f0f0f0;
    border-radius: 15px;
  }

  
  .user-row {
    display: flex;
    align-items: center;
    cursor: pointer;
  }


  
  img {
    border-radius: 50%;
    width: 50px;
    height: 50px;
    min-width: 50px;
    min-height: 50px;
    object-fit: cover;
  }
  
  .user-name {
    margin-left: 20px;
    margin-right: auto;
    width: 1100px;
  }
  
  .unban-button {
    padding: 10px;
    background-color: #f0f0f0;
    border: none;
    border-radius: 3px;
    cursor: pointer;
    width: 300px;
  }
  
  button {
    width: 100%;
    padding: 5px 10px;
    background-color: var(--primary);
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #2C3E50;
  }

  </style>