<template>
    <!-- Match Success Modal -->
    <teleport to="body">
        <div v-if="showMatchSuccessModal" @click="closeModal" class="match-success-modal">
            <div class="match-success-content" @click.stop>
                <h3 style="font-size: 100px; color: pink ;"><font-awesome-icon :icon="['fas', 'heart']" /></h3>
                <h3>恭喜您，現在你們是好友啦～</h3>
                <p></p>
                <p>快去聊天室聊天吧！</p>
                <button @click="closeModal">關閉</button>
            </div>
        </div>
    </teleport>
</template>

<script setup>
import { ref } from "vue";

const props = defineProps({
  showMatchSuccessModal: Boolean
});

const emit = defineEmits(['close']);

const closeModal = () => {
  emit('close');
};

</script>

<style scoped>
.match-success-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1100;
    /* 確保這個值比主要 modal 的 z-index 高 */
}

.match-success-content {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
    z-index: 1101;
    /* 確保內容在背景之上 */
}


.match-success-content button {
    margin-top: 10px;
    padding: 5px 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.match-success-content button:hover {
    background-color: #0056b3;
}
</style>