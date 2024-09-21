<template>
  <div v-if="userOneToOneChatroom.length > 0" class="container">

    <div class="sidebar">
      <div class="chatroom-header">
        <span @click="choosechatType('onetoone')" :class="{ 'selected-type': chatroomType === 'onetoone' }">個人</span>
        <span @click="choosechatType('act')" :class="{ 'selected-type': chatroomType === 'act' }">活動</span>
      </div>

      <div v-if="chatroomType === 'onetoone'">
        <table v-if="userOneToOneChatroom.length" class="chatroom-table">
          <tbody>
            <tr v-for="room in userOneToOneChatroom" :key="room.chatroomId" class="chatroom-row"
              :class="{ 'selected-chatroom': isSelected(room) }" @click="selectChatRoom(room)">
              <td class="avatar-cell">
                <img :src=room.otherUserPics :alt="room.otherUserName + '的头像'"
                  @click="navigateToProfile(room.otherUserId)" />
              </td>
              <td class="info-cell">
                <div class="user-name">{{ room.otherUserName }}</div>
                <div class="last-message">{{ room.lastMessageContent }}</div>
              </td>
              <td class="badge-cell">
                <span v-if="room.unreadQty > 0" class="badge text-bg-primary rounded-pill">{{ room.unreadQty }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="chatroomType === 'act'">
        <table v-if="userOneToOneChatroom.length" class="chatroom-table">
          <tbody>
            <tr v-for="room in userOneToOneChatroom" :key="room.chatroomId" class="chatroom-row"
              :class="{ 'selected-chatroom': isSelected(room) }" @click="selectChatRoom(room)">
              <td class="avatar-cell">
                <img :src=activityImages[room.actId] alt=""
                  @click="showMember(room.chatroomId, room.activitiesTitle, room.userCount)" />
              </td>
              <td class="info-cell">
                <div class="user-name">{{ room.activitiesTitle + " (" + room.userCount + ")" }}</div>
                <div class="last-message">{{ room.lastMessageContent }}</div>
              </td>
              <td class="badge-cell">
                <span v-if="room.unreadQty > 0" class="badge text-bg-primary rounded-pill">{{ room.unreadQty }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>

    <div class="websocket-component">
      <div class="messages-container" @click="readMsg(selectedUser.chatroomId)">
        <template v-if="chatHistory && chatHistory.length > 0">
          <p v-for="message in chatHistory" :key="message.messageId"
            :class="{ 'my-message': message.senderId === userId }">
          <div v-if="message.senderId !== userId">
            <img :src="message.userPics" :alt="message.userName + '的头像'" />
            <span class="other-message-content">
              {{ message.content }}
            </span>
            <span class="other-message-time">{{ formatTimestamp(message.timestamp) }}</span>
          </div>
          <div v-else>
            <span class="my-message-time">{{ formatTimestamp(message.timestamp) }}</span>
            <span class="my-message-content">
              {{ message.content }}
            </span>
          </div>
          </p>
        </template>

        <template v-else>
          <p></p>
          <p></p>
          <p v-if="chatroomType === 'onetoone'" class="firstChat">-------- 現在開始你們是朋友啦!!說點什麼吧~ --------</p>
          <p v-if="chatroomType === 'act'" class="firstChat">-------- 還沒有人說話唷!說點什麼吧~ --------</p>
        </template>

        <p v-for="message in messages" :key="message.timestamp" :class="{ 'my-message': message.senderId === userId }">
        <div v-if="message.chatroomId === selectedUser.chatroomId">
          <div v-if="message.senderId !== userId">
            <img :src="userInfoMap[message.senderId]?.userPics || '/path/to/default/avatar.png'"
              :alt="userInfoMap[message.senderId]?.userName + '的头像'" />
            <span class="other-message-content">
              {{ message.content }}
            </span>
            <span class="other-message-time">
              {{ formatTimestamp(message.timestamp) }}
            </span>
          </div>
          <div v-else>
            <span class="my-message-time">
              {{ formatTimestamp(message.timestamp) }}
            </span>
            <span class="my-message-content">
              {{ message.content }}
            </span>
          </div>
        </div>
        </p>
      </div>

      <div class="message-input"@click="readMsg(selectedUser.chatroomId)" >
        <div class="input-wrapper" >
          <input v-model="userMessage" @keyup.enter="sendMessage" placeholder="Type a message..."
            class="message-input-field" />
          <button @click="sendMessage" class="btn btn-primary rounded-pill py-2 px-4 send-button">
            送出
          </button>
        </div>
      </div>
    </div>
  </div>

  <div v-else style="margin-bottom: 300px;">
    <p style="font-size: 50px;"><br></p>
    <p class="firstChat">你目前還沒有好友們可以聊天，先去探索一下吧~</p>
  </div>


  <BanList v-if="showBanListModal" :show="showBanListModal" :user-details="userDetails" :list-type="listType"
    :listMessage="listMessage" :show-banList-modal="showBanListModal" @close="closeBanList" m />


</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { socket } from "@/plugins/websocket.js"
import axiosapi from '@/plugins/axios.js';
import useUserStore from "@/stores/user.js"
import router from '@/router/router';
import BanList from '../04-user/BanList.vue';
const userStore = useUserStore();
const userId = userStore.id

const loading = ref(true)
const websocketCount = ref(-1)
const messages = ref([])
const connectionStatus = ref('Disconnected')

const userMessage = ref('')

const queryCondition = reactive({
  type: "message"
})

const userOneToOneChatroom = ref([]);
const activityImages = ref({});
const selectedUser = ref(null);

const chatHistory = ref([]);
const chatroomType = ref("");
const userInfoMap = ref({})

const showBanListModal = ref(false);
const userDetails = ref([]);
const listType = ref("封鎖列表");
const listMessage = ref("目前沒有封鎖任何用戶");
const listUrl = ref("");

onMounted(async () => {
  chatroomType.value = "onetoone"

  try {
    // 等待 getChatroomList 完成
    await getChatroomList(chatroomType, true)

    // 初始化 WebSocket
    socket.initWebSocket(
      `ws://localhost:8080/meetu/notice/${userId}`
    )
    socket.websocket.onmessage = websocketOnMessage

    // getChatroomList 完成後執行 getChatroomHistory
    await getChatroomHistory()
  } catch (error) {
    console.error("Error in onMounted:", error)
  }
})

const init = () => {
  // 註釋 2: 這裡發送第二條消息 {"type":"message"}
  queryCondition.type = "message"
  socket.sendMsg(JSON.stringify(queryCondition))
}

const websocketOnMessage = (event) => {
  websocketCount.value += 1
  if (websocketCount.value === 0) {
    init()
  }
  let info = JSON.parse(event.data)
  switch (info.type) {
    case "heartbeat":
      socket.state = true
      connectionStatus.value = 'Connected'
      console.log("websocket:" + info.type + "-" + connectionStatus.value)
      break
    case "message":
      loading.value = true
      console.log("websocket:" + info.type + "-" + connectionStatus.value)
      nextTick(() => {
        consumeMessage(info)
      })
      // getChatroomList();
      break
    case "error":
      loading.value = false
      connectionStatus.value = 'Error'
      console.log("websocket:" + info.type + "-" + connectionStatus.value)
      break
  }
}

const consumeMessage = (info) => {
  messages.value.push(info);
  loading.value = false;
  nextTick(() => {
    scrollToBottom();
    getChatroomList(chatroomType, false);
  });
}

// 新增：格式化時間戳的函數
const formatTimestamp = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleString('zh-TW', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  });
}

const sendMessage = async () => {
  if (userMessage.value.trim()) {
    const messageToSend = {
      chatroomId: selectedUser.value.chatroomId,
      receiverId: selectedUser.value.userId,
      chatroomType: selectedUser.value.chatType,
      type: "message",
      content: userMessage.value,
      timestamp: new Date().toISOString()
      //將時間戳作為ISO 8601字符串發送, 是一種標準格式,適用於不同的系統
      //ISO 8601字符串與時區無關,避免了與時區相關的問題
      // Java的Instant.parse()方法可以直接解析ISO 8601字符串
    };
    console.log(messageToSend)

    // 發送消息
    socket.sendMsg(JSON.stringify(messageToSend));
    userMessage.value = ''; // 清空輸入框

    //全部已讀
    readMsg(selectedUser.value.chatroomId);
    getChatroomList(chatroomType, false);

    nextTick(() => {
      scrollToBottom();
    });
  }

};


async function getChatroomList(chatroomType, isInitialLoad = false) {
  try {
    const request = {
      headers: {
        'Authorization': userStore.token
      }
    };

    const response = await axiosapi.get(`/secure/users/chatroom/${chatroomType.value}`, request);
    console.log("調取聊天室基礎資料成功!");
    console.log(response.data)

    const sortedChatrooms = response.data.sort((a, b) => {
      return new Date(b.lastMessageTimestamp) - new Date(a.lastMessageTimestamp);
    });

    if (sortedChatrooms.length > 0) {
      if (isInitialLoad || !selectedUser.value) {
        const firstChatroom = sortedChatrooms[0];
        selectedUser.value = {
          userId: firstChatroom.otherUserId,
          userName: firstChatroom.otherUserName,
          userPics: firstChatroom.otherUserPics,
          chatroomId: firstChatroom.chatroomId,
          actId: firstChatroom.actId,
          chatType: firstChatroom.chatType
        };
        if (firstChatroom.chatType == 'G') {
          sortedChatrooms.forEach(chatroom => {
            if (chatroom.actId != null) {
              console.log("正在獲取活動圖片，actId:", chatroom.actId);
              fetchActivityImage(chatroom.actId);
            }
          });
        }
      }
      userOneToOneChatroom.value = sortedChatrooms;



    }
  } catch (error) {
    console.log("調取聊天室基礎資料失敗!" + error);
    if (error.response && error.response.status === 403) {
      console.log("收到403狀態，正在導向禁止訪問頁面");
      router.push({ name: "forbidden-link" });
    }
  }
}

function fetchActivityImage(activityId) {
  axiosapi.get(`/photos/download/${activityId}`, { responseType: 'blob' })
    .then(response => {
      const imageUrl = URL.createObjectURL(response.data);
      activityImages.value[activityId] = imageUrl;
    })
    .catch(error => {
      console.error(`Error fetching image for activity ${activityId}:`, error);
    });
}

const isSelected = computed(() => (room) => {
  return selectedUser.value && selectedUser.value.chatroomId === room.chatroomId;
});

const selectChatRoom = (room) => {
  messages.value = []; //重新清空對話視窗
  selectedUser.value = {
    userId: room.otherUserId,
    userName: room.otherUserName,
    userPics: room.otherUserPics,
    chatroomId: room.chatroomId,
    chatType: room.chatType,
    actId: room.actId
  };
  getChatroomHistory();
  readMsg(room.chatroomId);
  room.unreadQty = 0;

  nextTick(() => {
    userOneToOneChatroom.value = [...userOneToOneChatroom.value];
  });
};

const getChatroomHistory = async () => {
  if (!selectedUser.value || !selectedUser.value.chatroomId) {
    console.error('No selected chatroom');
    return;
  }

  try {
    // 獲取聊天室用戶信息
    const usersResponse = await axiosapi.get(`/secure/users/chatroom/${selectedUser.value.chatroomId}/users`);
    const users = usersResponse.data;

    // 更新 userInfoMap
    userInfoMap.value = users.reduce((map, user) => {
      map[user.userId] = {
        userName: user.userName,
        userPics: user.userPics,
        userStatus: user.userStatus
      };
      return map;
    }, {});

    // 獲取聊天記錄
    const historyResponse = await axiosapi.get(`/secure/users/${selectedUser.value.chatroomId}/chatroomHistory`);
    const history = historyResponse.data.chatHistory;

    // 將用戶信息添加到聊天記錄中
    chatHistory.value = await history.map(message => ({
      ...message,
      userName: userInfoMap.value[message.senderId]?.userName || 'Unknown User',
      userPics: userInfoMap.value[message.senderId]?.userPics || '/path/to/default/avatar.png'
    }));

    console.log("成功獲取聊天室歷史記錄");
    nextTick(() => {
      scrollToBottom();
    });
  } catch (error) {
    console.error("獲取聊天室歷史記錄失敗", error);
    if (error.response) {
      console.error('Error response:', error.response.data);
      console.error('Error status:', error.response.status);
    } else if (error.request) {
      console.error('Error request:', error.request);
    } else {
      console.error('Error message:', error.message);
    }
    // 這裡可以添加錯誤處理邏輯，比如顯示錯誤消息給用戶
  }
};

const scrollToBottom = () => {
  const messagesContainer = document.querySelector('.messages-container');
  messagesContainer.scrollTop = messagesContainer.scrollHeight;
};

async function choosechatType(type) {
  chatroomType.value = type;
  await getChatroomList(chatroomType, true);
  await getChatroomHistory();
}

async function readMsg(chatroomId) {
  try {
    const request = {
      headers: {
        'Authorization': userStore.token,
      }
    };
    const response = await axiosapi.get(`/secure/users/chatroom/${chatroomId}/read`, request);
    console.log("chatroomId:" + chatroomId + ",訊息數量:" + response.data.readQty + ",全數已讀");

    // 更新對應聊天室的 unreadQty
    const updatedRoom = userOneToOneChatroom.value.find(room => room.chatroomId === chatroomId);
    if (updatedRoom) {
      updatedRoom.unreadQty = 0;
    }
  } catch (error) {
    console.log("全數已讀失敗!");
    console.log(error);
  }
}

function showMember(chatroomId, activitiesTitle, userCount) {
  listUrl.value = `/secure/users/chatroom/${chatroomId}/memberlist`;
  listType.value = `${activitiesTitle}(${userCount})`
  showList(listUrl)
}

//還沒寫完
function showList(listUrl) {
  axiosapi.get(listUrl.value)
    .then(function (response) {
      console.log(response)
      userDetails.value = response.data.map(detail => ({
        id: detail.userId,
        name: detail.userName || `User ${detail.userId}`,
        pic: detail.userPic
      }));
      showBanListModal.value = true;
      console.log(response.data);

      listMessage.value = "目前還沒有好友唷";

    })
    .catch(function (error) {
      // console.error("獲取封鎖列表失敗:", error);
      Swal.fire("錯誤", `無法獲取${listName.value}`, "error");
    });
}

function closeBanList() {
  showBanListModal.value = false;
}

const navigateToProfile = (userId) => {
  router.push({ name: 'userprofile-link', params: { userId: userId } });
}


</script>

<style scoped>
/* 容器樣式 */
.container {
  display: flex;
  width: 100%;
  height: 100vh;
}

/* 側邊欄樣式 */
.sidebar {
  width: 300px;
  flex: 0 0 250px;
  padding: 20px;
  border-right: 1px solid #e0e0e0;
  box-sizing: border-box;
}

/* 聊天室表格樣式 */
.chatroom-table {
  width: 100%;
  border-collapse: collapse;
}

.chatroom-header {
  display: flex;
  background-color: #f8f9fa;
  margin-bottom: 0px;
  width: 100%;
}

.chatroom-header span {
  flex: 1;
  width: 125px;
  /* 變更為 125px，剛好是 250px 的一半 */
  text-align: center;
  padding: 10px 0;
  font-weight: bold;
  cursor: pointer;
  border-top: 2px solid #dee2e6;
  border-bottom: 2px solid #dee2e6;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.chatroom-header span:first-child {
  border-left: 2px solid #dee2e6;
  border-right: 1px solid #dee2e6;
}

.chatroom-header span:last-child {
  border-right: 2px solid #dee2e6;
  border-left: 1px solid #dee2e6;
}

.chatroom-header span:hover {
  box-shadow: 0 0 0 2px #d0d0d0;
  background-color: #f0f0f0;
}

.chatroom-header span.selected-type {
  background-color: #ddfec6;
  box-shadow: 0 0 0 0.5px #d0d0d0;
}

.chatroom-row {
  box-shadow: 0 0 0 1px #e0e0e0;
  transition: box-shadow 0.3s ease;
  cursor: pointer;
}

.chatroom-row:hover {
  box-shadow: 0 0 0 2px #d0d0d0;
  background-color: #ddfec6;
}

.chatroom-row td {
  background-color: #fff;
  padding: 10px;
  border: none;
}

.chatroom-row td:first-child {
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
}

.chatroom-row td:last-child {
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
}

.avatar-cell {
  width: 60px;
  max-width: 60px;
}

.info-cell {
  width: 190px;
  /* 增加到 190px 以適應新的總寬度 */
  max-width: 150px;
  padding: 0 10px;
  overflow: hidden;
}

.badge-cell {
  width: 40px;
  max-width: 40px;
  text-align: right;
}

/* 頭像樣式 */
img {
  border-radius: 50%;
  width: 50px;
  height: 50px;
  object-fit: cover;
}

/* 用戶名和最後消息樣式 */
.user-name,
.last-message {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}

.user-name {
  font-weight: bold;
}

.last-message {
  font-size: 0.9em;
  color: #666;
}

/* 徽章樣式 */
.badge {
  font-size: 0.8em;
  background-color: #007bff;
  color: white;
  padding: 3px 7px;
  border-radius: 10px;
  white-space: nowrap;
}

/* WebSocket組件樣式 */
.websocket-component {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 80%;
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.messages-container p {
  margin: 5px;
}

.messages-container .other-message-content {
  background-color: #efeff0;
  padding: 10px;
  margin: 10px;
  border-radius: 20px;
}

.messages-container .other-message-time {
  margin-left: 10px;
  font-size: x-small;
}

.firstChat {
  text-align: center;
  color: #a5a4a4;
  font-size: 20px;
}

.my-message {
  text-align: right;
  margin: 15px !important;
}

.messages-container .my-message-content {
  background-color: #aae97e;
  padding: 10px;
  margin: 10px;
  border-radius: 20px;
}

.messages-container .my-message-time {
  margin-left: 10px;
  font-size: x-small;
}

.message-input {
  padding: 20px;
  background-color: #f8f9fa;
  border-top: 1px solid #e0e0e0;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-input-field {
  flex-grow: 1;
  height: 40px;
  padding: 0 15px;
  border: 1px solid #ced4da;
  font-size: 14px;
}

.send-button {
  height: 40px;
  min-width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}


/* 修改 .selected-chatroom 樣式 */
.chatroom-row.selected-chatroom {
  background-color: #d6f1c3 !important;
}

.chatroom-row.selected-chatroom td {
  background-color: #d6f1c3 !important;
}

/* 確保懸停效果不會覆蓋選中效果 */
.chatroom-row.selected-chatroom:hover {
  box-shadow: 0 0 0 2px #d0d0d0;
  background-color: #d6f1c3 !important;
}
</style>