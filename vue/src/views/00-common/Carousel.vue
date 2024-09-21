<template>

        <div id="carouselExampleIndicators" class="carousel slide" data-bs-ride="carousel">
                <div class="carousel-inner">
                        <div class="carousel-item active">
                                <img src="http://localhost:8080/meetu/images/userPics/home1.jpeg" class="d-block w-100"
                                        alt="...">
                                <div class="carousel-caption d-none d-md-block">
                                        <p></p>
                                        <h1 style="font-size: 80px;">Welcome to MeetU.com !</h1>
                                        <h3 class="background">快開始你的覓友大冒險吧</h3>
                                        <p></p>
                                </div>
                        </div>
                        <div class="carousel-item">
                                <img src="http://localhost:8080/meetu/images/userPics/home2.webp" class="d-block w-100"
                                        alt="...">
                                <div class="carousel-caption d-none d-md-block background">
                                        <h1 style="font-size: 80px;">參加活動</h1>
                                        <h1 style="font-size: 80px;">分享你的心情故事</h1>
                                </div>
                        </div>
                        <div class="carousel-item">
                                <img src="http://localhost:8080/meetu/images/userPics/home3.jpeg" class="d-block w-100"
                                        alt="...">
                                <div class="carousel-caption d-none d-md-block background">
                                        <h1 style="font-size: 80px;">每日隨機配對</h1>
                                        <h1 style="font-size: 80px;">大膽送出你的喜歡</h1>
                                </div>
                        </div>
                </div>
                <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleIndicators"
                        data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                </button>
                <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleIndicators"
                        data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                </button>
        </div>
</template>

<script setup>

</script>

<style scoped>
.carousel-inner {
        height: 600px;
        /* 保持原有的高度設置 */
        overflow: hidden;
        border-radius: 15px;
}

.carousel-item {
        height: 100%;
        position: relative;
        /* 為絕對定位的子元素提供參考 */
}

.carousel-item img {
        object-fit: cover;
        object-position: center;
        width: 100%;
        height: 100%;
}

/* 修改 carousel-caption 的樣式 */
.carousel-caption {
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        bottom: auto;
        right: auto;
        text-align: center;
        padding: 30px;
        width: 80%;
        /* 擴大背景寬度 */
        max-width: 800px;
        /* 設置最大寬度，避免在大螢幕上過寬 */
}

.carousel-caption .background {
        background-color: rgba(0, 0, 0, 0.2);
        padding: 20px;
        /* 稍微加深背景 */
}

.carousel-caption h5 {
        font-size: 36px;
        /* 增大標題字體 */
        font-weight: bold;
        margin-bottom: 15px;
}

.carousel-caption p {
        font-size: 24px;
        /* 增大內容字體 */
        line-height: 1.4;
        /* 調整行高以提高可讀性 */
}

/* 確保在小螢幕上也能看到 caption */
@media (max-width: 768px) {
        .carousel-caption {
                display: block !important;
                padding: 20px;
        }

        .carousel-caption h5 {
                font-size: 28px;
        }

        .carousel-caption p {
                font-size: 18px;
        }
}
</style>