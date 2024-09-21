<template>
    <div style="margin-top: 30px;"></div>
    <!-- Footer Start -->
    <div class="container-fluid bg-dark text-light footer pt-3 mt-5 wow fadeIn" data-wow-delay="0.1s">
        <div class="container py-3">
            <div class="row g-5">
                <div class="col-lg-3 col-md-6">
                    <h4 class="text-white mb-3">Team Member</h4>
                    <a class="btn btn-link" href="">Jayson</a>
                    <a class="btn btn-link" href="">Reynold</a>
                    <a class="btn btn-link" href="">Benson</a>
                    <a class="btn btn-link" href="">Emerald</a>
                    <a class="btn btn-link" href="">Noel</a>
                </div>
                <div class="col-lg-3 col-md-6">
                    <h4 class="text-white mb-3">Contact</h4>
                    <p class="mb-2"><i class="fa fa-map-marker-alt me-3"></i>Kaohsiung City, Taiwan (R.O.C.)</p>
                    <p class="mb-2"><i class="fa fa-phone-alt me-3"></i>+886 1 456 789</p>
                    <p class="mb-2"><i class="fa fa-envelope me-3"></i>example@email.com</p>
                    <!-- <div class="d-flex pt-2">
                        <a class="btn btn-outline-light btn-social" href=""><i class="fab fa-twitter"></i></a>
                        <a class="btn btn-outline-light btn-social" href=""><i class="fab fa-facebook-f"></i></a>
                        <a class="btn btn-outline-light btn-social" href=""><i class="fab fa-youtube"></i></a>
                        <a class="btn btn-outline-light btn-social" href=""><i class="fab fa-linkedin-in"></i></a>
                    </div> -->
                </div>

            </div>
        </div>
        <div class="container">
            <div class="copyright">
                <div class="row">
                    <div class="col-md-6 text-center text-md-start mb-3 mb-md-0">
                        &copy; <a class="border-bottom" href="#">MeetU.com</a>, All Right Reserved.
                        Designed By <a class="border-bottom" href="https://htmlcodex.com">Our team member</a>
                    </div>
                    <div class="col-md-6 text-center text-md-end">
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Footer End -->
</template>

<script setup>

</script>

<style scoped>
/* 保留原有的樣式 */
:root {
    --primary: #86B817;
    --secondary: #FE8800;
    --light: #F5F5F5;
    --dark: #14141F;
}

/* Footer 樣式 */
.footer .btn.btn-social {
    margin-right: 5px;
    width: 35px;
    height: 35px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--light);
    font-weight: normal;
    border: 1px solid #FFFFFF;
    border-radius: 35px;
    transition: .3s;
}

.footer .btn.btn-social:hover {
    color: var(--primary);
}

.footer .btn.btn-link {
    display: block;
    margin-bottom: 5px;
    padding: 0;
    text-align: left;
    color: #FFFFFF;
    font-size: 15px;
    font-weight: normal;
    text-transform: capitalize;
    transition: .3s;
}

.footer .btn.btn-link::before {
    position: relative;
    content: "\f105";
    font-family: "Font Awesome 5 Free";
    font-weight: 900;
    margin-right: 10px;
}

.footer .btn.btn-link:hover {
    letter-spacing: 1px;
    box-shadow: none;
}

.footer .copyright {
    padding: 25px 0;
    font-size: 15px;
    border-top: 1px solid rgba(256, 256, 256, .1);
}

.footer .copyright a {
    color: var(--light);
}

.footer .footer-menu a {
    margin-right: 15px;
    padding-right: 15px;
    border-right: 1px solid rgba(255, 255, 255, .1);
}

.footer .footer-menu a:last-child {
    margin-right: 0;
    padding-right: 0;
    border-right: none;
}
</style>