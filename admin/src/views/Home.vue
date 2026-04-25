<template>
    <div class="blog-layout">
        <TopNavbar />

        <main class="main-container">
            <div class="content-wrapper">
                <transition name="fade" mode="out-in">
                    <router-view />
                </transition>
            </div>
        </main>
    </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useUserStore } from '../stores/user';
import { useAppStore } from '../stores/app';
import TopNavbar from '../components/TopNavbar.vue';

const userStore = useUserStore();
const appStore = useAppStore();

const LOGIN_CHECKED_FLAG = 'has_checked_login_on_mount';

onMounted(() => {
    const hasChecked = sessionStorage.getItem(LOGIN_CHECKED_FLAG);

    if (!userStore.isLoggedIn && !hasChecked) {
        appStore.openLoginModal();
        sessionStorage.setItem(LOGIN_CHECKED_FLAG, 'true');
    }
});
</script>

<style scoped>
.blog-layout {
    min-height: 100vh;
    background-color: #f9fafb;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    color: #374151;
}

.main-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 120px 24px 32px;
    min-height: calc(100vh - 72px);
}

.content-wrapper {
    max-width: 1000px;
    margin: 0 auto;
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

@media screen and (max-width: 768px) {
    .main-container {
        padding: 16px 12px;
        min-height: calc(100vh - 64px);
    }

    .content-wrapper {
        max-width: 100%;
    }
}

@media screen and (max-width: 480px) {
    .main-container {
        padding: 12px 8px;
    }
}
</style>