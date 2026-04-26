import { defineStore } from 'pinia'
import { ref, computed, onMounted, onUnmounted } from 'vue'

export const useUserStore = defineStore('user', () => {
    // 安全读取 localStorage
    const getLocalItem = (key, defaultValue) => {
        try {
            const item = localStorage.getItem(key)
            if (!item) return defaultValue
            return JSON.parse(item)
        } catch {
            return defaultValue
        }
    }

    // 状态（安全版）
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(getLocalItem('userInfo', null))

    // 计算属性
    const isLoggedIn = computed(() => !!token.value)

    // storage 事件处理函数（用于跨标签页同步）
    const handleStorageChange = (e) => {
        if (e.key === 'userInfo') {
            try {
                userInfo.value = e.newValue ? JSON.parse(e.newValue) : null
            } catch {
                userInfo.value = null
            }
        }
        if (e.key === 'token') {
            token.value = e.newValue || ''
        }
    }

    // 登录成功
    function setLoginSuccess(data) {
        token.value = data.token
        userInfo.value = {
            id: data.id,
            username: data.username,
            nickname: data.nickname,
            avatar: data.avatar,
            role: data.role,
            signature: data.signature,
            level: data.level,
            points: data.points,
            followCount: data.followCount,
            fansCount: data.fansCount,
            likeCount: data.likeCount,
            moodStatus: data.moodStatus,
            mentalTag: data.mentalTag,
            lastLoginTime: data.lastLoginTime
        }

        localStorage.setItem('token', data.token)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }

    // 登出
    function logout() {
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
    }

    // 更新用户统计数据
    function updateUserInfo(updates) {
        if (userInfo.value) {
            Object.assign(userInfo.value, updates)
            localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        }
    }

    // 🔥 防溢出：重置所有状态
    function $reset() {
        token.value = ''
        userInfo.value = null
        logout()
    }

    return {
        token,
        userInfo,
        isLoggedIn,
        setLoginSuccess,
        logout,
        updateUserInfo,
        $reset, // 必须返回
        handleStorageChange // 导出 storage 事件处理函数
    }
})