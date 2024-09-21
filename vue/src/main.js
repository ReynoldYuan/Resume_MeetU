import { createApp } from "vue";
import App from "./App.vue";
import router from "./router/router";
import FontAwesomeIcon from "./plugins/fontawesome.js";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import '@fortawesome/fontawesome-free/css/all.css';
import '@fortawesome/fontawesome-free/js/all.js';
import 'bootstrap-icons/font/bootstrap-icons.css';



const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

createApp(App)
.use(router)
.use(pinia)
.component('font-awesome-icon', FontAwesomeIcon)
.mount("#app");
