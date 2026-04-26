<template>
    <div id="app">
        <!-- 路由视图：渲染 Home 等页面 -->
        <router-view></router-view>

        <!-- 【全局挂载】登录弹窗 -->
        <!-- 使用 v-model:visible 双向绑定 appStore 的状态 -->
        <LoginModal :visible="appStore.isLoginModalVisible" 
        @update:visible="appStore.isLoginModalVisible = $event"
            @onGuestAccess="handleGuestAccess" />
    </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useAppStore } from './stores/app.js'
import { useUserStore } from './stores/user.js'
import LoginModal from './components/LoginModal.vue'

const appStore = useAppStore()
const userStore = useUserStore()

// 监听 storage 事件，实现跨标签页同步用户信息
onMounted(() => {
    window.addEventListener('storage', userStore.handleStorageChange)
})

onUnmounted(() => {
    window.removeEventListener('storage', userStore.handleStorageChange)
})

// 【必须补充】处理游客访问的回调
const handleGuestAccess = () => {
    console.log('用户选择以游客身份访问')
    // 可选：如果你希望用户选择游客后，刷新页面不再自动弹窗，可以这样做：
    // sessionStorage.setItem('guest_mode', 'true')
}
</script>

<style>
/* 建议添加基础样式重置 */
body {
    margin: 0;
    padding: 0;
}

#app {
    min-height: 100vh;
    background-color: #f9fafb;
    /* 与 Home.vue 背景保持一致 */
}
</style>