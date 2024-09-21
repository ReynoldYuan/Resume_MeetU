//0. 引用Vue Router
import { createRouter, createWebHistory } from "vue-router";

//1. 引用元件
import Home from "@/views/Home.vue";
import NotFound from "@/views/NotFound.vue";
import Forbidden from "@/views/Forbidden.vue";
import AboutUs from "@/views/99-other/AboutUs.vue";
import DailyMatching from "@/views/01-matching/DailyMatching.vue";
import ActivitiesList from "@/views/02-activities/ActivitiesList.vue";
import PostList from "@/views/03-post/PostList.vue";
import ContactUs from "@/views/99-other/Contact.Us.vue";
import Login from "@/views/04-user/Login.vue";
import MyProfile from "@/views/04-user/MyProfile.vue";
import Chatroom from "@/views/07-chat/Chatroom.vue";
import UserProfile from "@/views/04-user/UserProfile.vue";
import Logout from "@/views/04-user/Logout.vue";
import ShowNotiPage from "@/views/05-notification/showNotiPage.vue";
import AddNotiPage from "@/views/05-notification/addNotiPage.vue";
import EditNotiPage from "@/views/05-notification/editNotiPage.vue";
import Register from "@/views/04-user/Register.vue"
import Update from "@/views/04-user/Update.vue";
import EditPostPage from "@/views/03-post/EditPostPage.vue";
import OneNotiPage from "@/views/05-notification/OneNotiPage.vue";
import Account from "@/views/04-user/Account.vue";
import UpdatePwd from "@/views/04-user/UpdatePwd.vue";

import AddActPage from "@/views/02-activities/AddActPage.vue";
import EditActPage from "@/views/02-activities/EditActPage.vue";
import AddPostPage from "@/views/03-post/AddPostPage.vue";
import BriefActivites from "@/views/02-activities/BriefActivites.vue";
import UserActList from "@/views/02-activities/UserActList.vue";
import MyPosts from "@/views/03-post/MyPosts.vue";
import MyRegistrations from "@/views/02-activities/MyRegistrations.vue";
import ActivityReview from "@/views/02-activities/ActivityReview.vue";
import MyFavorite from "@/views/02-activities/MyFavorite.vue";
import MyCompleted from "@/views/02-activities/MyCompleted.vue";



// 2. 定義網頁路由
const routes = [
  { name: "home-link", path: "/", component: Home },
  { name: "notfound-link", path: "/:pathMatch(.*)*", component: NotFound },
  { name: "forbidden-link", path: "/403", component: Forbidden },
  { name: "aboutus-link", path: "/aboutus", component: AboutUs },
  { name: "dailymatching-link", path: "/dailymatching", component: DailyMatching },
  { name: "activities-link", path: "/activitieslist", component: ActivitiesList },
  { name: "postlist-link", path: "/postlist", component: PostList },
  { name: "contactus-link", path: "/contactus", component: ContactUs },
  { name: "login", path: "/login", component: Login },
  { name: "myprofile-link", path: "/myprofile/:userId", component: MyProfile },
  { name: "chatroom-link", path: "/chatroom", component: Chatroom },
  { name: "userprofile-link", path: "/userprofile/:userId", component: UserProfile },

  { name: "logout-link", path: "/logout", component: Logout},
  { name: "register-link", path: "/register", component: Register},
  { name: "update-link", path: "/update/:userId", component: Update},
  { name: "account-link", path: "/account", component: Account},
  { name: "updatePwd-link", path: "/updatePwd", component: UpdatePwd},
  
  { name: "showNotiPage-link", path: "/notification/list", component: ShowNotiPage},
  { name: "addNotiPage-link", path: "/notification/add", component: AddNotiPage},
  { name: "editNotiPage-link", path: "/notification/update/:id", component: EditNotiPage},
  { name: "oneNotiPage-link", path: "/notification/findNotiById/:nid", component: OneNotiPage},
  // { name: "oneNotiPage-link", path: "/", component: OneNotiPage},

  { name: "editPost-link", path: "/posts/edit/:id", component: EditPostPage},
  { name: "addActPage-link", path: "/activities/add", component: AddActPage},
  { name: "editActPage-link", path: "/activities/update/:activitiesId", component: EditActPage},
  { name: "addPost-link", path: "/posts/add", component: AddPostPage},
  { name: "briefactivities-link", path: "/briefactivities/:activitiesId", component: BriefActivites },
  { name: "usersActList-link", path: "/usersActList", component: UserActList },
  { name: "myPosts-link", path: "/my-posts", component: MyPosts},
  { name: "MyRegistrations-link", path: "/MyRegistrations", component: MyRegistrations },
  { name: "ActivityReview-link", path: "/ActivityReview", component: ActivityReview },
  { name: "MyFavorite-link", path: "/myFavorite", component: MyFavorite },
  { name: "MyCompleted-link", path: "/myCompleted", component: MyCompleted },


  

];

// 3. 產生路由物件
const router = createRouter({
  history: createWebHistory(),
  routes: routes,
})

// 4. 導出路由物件以利其他元件導入
export default router


