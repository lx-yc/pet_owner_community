// stores/app.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
    // 控制全局登录弹窗的显示状态
    const isLoginModalVisible = ref(false)

    function openLoginModal() {
        isLoginModalVisible.value = true
    }

    function closeLoginModal() {
        isLoginModalVisible.value = false
    }

    return {
        isLoginModalVisible,
        openLoginModal,
        closeLoginModal
    }
})