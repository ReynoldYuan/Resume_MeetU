<template>
  <!-- <div class="container"> -->
    <Navbar></Navbar>

    <div class="content-wrapper">
      <!-- <div class="container"> -->
      <RouterView></RouterView>
    <!-- </div> -->
    
    <div>
    <Footer></Footer>
  </div>
  </div>
</template>

<script setup>
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'tocas/dist/tocas.min.css'
import Navbar from './views/Navbar.vue';
import UserSidebar from './components/02-user/UserSidebar.vue';
import Footer from './views/Footer.vue';

</script>

<style>
.content-wrapper {
  padding-top: 6%;
  
  
}
</style>