<template>
    <Teleport to="body">
        <transition name="modal-fade">
            <div v-if="visible" class="modal-overlay" @click.self="handleClose">
                <div class="login-card">
                    <div class="card-decoration">
                        <div class="decoration-circle circle-1"></div>
                        <div class="decoration-circle circle-2"></div>
                        <div class="decoration-circle circle-3"></div>
                    </div>
                    
                    <div class="header">
                        <div class="header-content">
                            <div class="logo-icon">
                                <span class="paw-icon">🐾</span>
                            </div>
                            <h3>{{ mode === 'login' ? '欢迎回来' : '加入我们' }}</h3>
                            <p class="subtitle">{{ mode === 'login' ? '登录你的宠物社区账号' : '开启你的宠物社区之旅' }}</p>
                        </div>
                        <span class="close-btn" @click="handleClose">&times;</span>
                    </div>

                    <div class="content">
                        <div class="form-container">
                            <div class="input-group">
                                <label class="input-label">用户名</label>
                                <div class="input-wrapper">
                                    <span class="input-icon">
                                        <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2"
                                            fill="none" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                            <circle cx="12" cy="7" r="4"></circle>
                                        </svg>
                                    </span>
                                    <input 
                                        v-model="form.username" 
                                        placeholder="请输入用户名/手机号" 
                                        class="custom-input"
                                        type="text"
                                        autocomplete="username"
                                        inputmode="text"
                                    />
                                </div>
                            </div>

                            <div class="input-group">
                                <label class="input-label">密码</label>
                                <div class="input-wrapper">
                                    <span class="input-icon">
                                        <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2"
                                            fill="none" stroke-linecap="round" stroke-linejoin="round">
                                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                                            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                                        </svg>
                                    </span>
                                    <input 
                                        v-model="form.password" 
                                        type="password" 
                                        placeholder="请输入密码" 
                                        class="custom-input"
                                        autocomplete="current-password"
                                    />
                                </div>
                            </div>

                            <div class="expand-wrapper" :class="{ 'is-expanded': mode === 'register' }">
                                <div class="expand-content">
                                    <div class="input-group">
                                        <label class="input-label">确认密码</label>
                                        <div class="input-wrapper">
                                            <span class="input-icon">
                                                <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor"
                                                    stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                                                </svg>
                                            </span>
                                            <input 
                                                v-model="form.confirmPassword" 
                                                type="password" 
                                                placeholder="请再次输入密码"
                                                class="custom-input"
                                                autocomplete="new-password"
                                            />
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <transition name="fade-fast">
                                <div v-if="errorMsg" class="error-alert">
                                    <svg class="error-icon-svg" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                                        <circle cx="12" cy="12" r="10"></circle>
                                        <line x1="12" y1="8" x2="12" y2="12"></line>
                                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                                    </svg>
                                    <span class="error-text">{{ errorMsg }}</span>
                                </div>
                            </transition>
                        </div>
                    </div>

                    <div class="footer">
                        <button 
                            class="submit-btn" 
                            @click="handleSubmit" 
                            :disabled="loading"
                            type="button"
                        >
                            <span v-if="loading" class="loading-spinner"></span>
                            <span class="btn-text">{{ loading ? (mode === 'login' ? '登录中...' : '注册中...') : (mode === 'login' ? '立即登录' : '立即注册') }}</span>
                            <span class="btn-shine"></span>
                        </button>

                        <button class="switch-mode-btn" @click="toggleMode" type="button">
                            <span class="switch-text">{{ mode === 'login' ? '还没有账号？' : '已有账号？' }}</span>
                            <span class="switch-link">{{ mode === 'login' ? '立即注册' : '去登录' }}</span>
                            <svg class="switch-arrow" viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M5 12h14M12 5l7 7-7 7"></path>
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
        </transition>
    </Teleport>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '../stores/user'
import { loginApi, registerApi } from '../api/user'
import { ElMessage } from 'element-plus'

const props = defineProps({
    visible: Boolean
})

const emit = defineEmits(['update:visible'])

const userStore = useUserStore()
const loading = ref(false)
const errorMsg = ref('')
const mode = ref('login')

const form = reactive({
    username: '',
    password: '',
    confirmPassword: ''
})

// 关闭弹窗
const handleClose = () => {
    emit('update:visible', false)
    setTimeout(() => {
        errorMsg.value = ''
    }, 300)
}

// 切换登录/注册模式
const toggleMode = () => {
    mode.value = mode.value === 'login' ? 'register' : 'login'
    errorMsg.value = ''
    form.password = ''
    form.confirmPassword = ''
}

// 提交处理
const handleSubmit = async () => {
    if (!form.username || !form.password) {
        errorMsg.value = '用户名和密码不能为空'
        return
    }

    if (mode.value === 'register') {
        if (form.password !== form.confirmPassword) {
            errorMsg.value = '两次输入的密码不一致'
            return
        }
        await handleRegister()
    } else {
        await handleLogin()
    }
}

// 登录逻辑
const handleLogin = async () => {
    loading.value = true
    errorMsg.value = ''

    try {
        const res = await loginApi({
            username: form.username,
            password: form.password
        })

        if (res.code === 0) {
            userStore.setLoginSuccess(res.data)
            handleClose()
            form.username = ''
            form.password = ''
            ElMessage.success('登录成功')
            window.location.reload()
        } else {
            errorMsg.value = res.msg || '登录失败'
            // ElMessage.error(errorMsg.value)
        }
    } catch (err) {
        console.error(err)
        errorMsg.value = '网络异常，请稍后重试'
    } finally {
        loading.value = false
    }
}

// 注册逻辑
const handleRegister = async () => {
    loading.value = true
    errorMsg.value = ''

    try {
        const res = await registerApi({
            username: form.username,
            password: form.password
        })

        if (res.code === 0) {
            mode.value = 'login'
            errorMsg.value = ''
            form.password = ''
            form.confirmPassword = ''
            ElMessage.success('注册成功，请登录')
        } else {
            errorMsg.value = res.msg || '注册失败'
        }
    } catch (err) {
        console.error(err)
        errorMsg.value = '网络异常，注册失败'
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.modal-fade-enter-active,
.modal-fade-leave-active {
    transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-fade-enter-from,
.modal-fade-leave-to {
    opacity: 0;
}

.modal-fade-enter-active .login-card {
    transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.3s ease;
}

.modal-fade-leave-active .login-card {
    transition: transform 0.2s ease-in, opacity 0.2s ease;
}

.modal-fade-enter-from .login-card {
    transform: scale(0.9) translateY(20px);
}

.modal-fade-leave-to .login-card {
    transform: scale(0.95) translateY(-10px);
}

.fade-fast-enter-active,
.fade-fast-leave-active {
    transition: opacity 0.25s ease, transform 0.25s ease;
}
.fade-fast-enter-from,
.fade-fast-leave-to {
    opacity: 0;
    transform: translateY(-8px);
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.2) 100%);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    z-index: 999;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    box-sizing: border-box;
}

.login-card {
    background: linear-gradient(135deg, #ffffff 0%, #fafbff 100%);
    padding: 0;
    border-radius: 24px;
    width: 100%;
    max-width: 420px;
    max-height: calc(100vh - 40px);
    overflow-y: auto;
    box-shadow: 
        0 20px 60px rgba(102, 126, 234, 0.25),
        0 8px 20px rgba(118, 75, 162, 0.15),
        0 0 0 1px rgba(255, 255, 255, 0.8) inset;
    position: relative;
    box-sizing: border-box;
    touch-action: manipulation;
    will-change: transform;
    overflow: hidden;
}

.card-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 180px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    clip-path: ellipse(85% 100% at 50% 0%);
    pointer-events: none;
    z-index: 0;
}

.decoration-circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 6s ease-in-out infinite;
}

.circle-1 {
    width: 120px;
    height: 120px;
    top: -40px;
    right: -30px;
    animation-delay: 0s;
}

.circle-2 {
    width: 80px;
    height: 80px;
    top: 20px;
    left: -20px;
    animation-delay: 2s;
}

.circle-3 {
    width: 60px;
    height: 60px;
    top: 80px;
    right: 40px;
    animation-delay: 4s;
}

@keyframes float {
    0%, 100% {
        transform: translateY(0) scale(1);
    }
    50% {
        transform: translateY(-10px) scale(1.05);
    }
}

.header {
    position: relative;
    z-index: 1;
    padding: 36px 32px 28px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
}

.header-content {
    flex: 1;
}

.logo-icon {
    width: 56px;
    height: 56px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
    box-shadow: 
        0 8px 24px rgba(0, 0, 0, 0.12),
        0 0 0 1px rgba(255, 255, 255, 0.5);
    transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.logo-icon:hover {
    transform: rotate(-10deg) scale(1.1);
}

.paw-icon {
    font-size: 28px;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.header h3 {
    margin: 0 0 6px 0;
    font-size: 26px;
    color: #ffffff;
    font-weight: 700;
    letter-spacing: -0.5px;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.subtitle {
    margin: 0;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.9);
    font-weight: 400;
    letter-spacing: 0.2px;
}

.close-btn {
    font-size: 32px;
    color: rgba(255, 255, 255, 0.9);
    cursor: pointer;
    line-height: 1;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    user-select: none;
    padding: 4px;
    margin: -8px -8px 0 0;
    min-width: 44px;
    min-height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(8px);
}

.close-btn:hover {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.25);
    transform: rotate(90deg);
}

.content {
    position: relative;
    z-index: 1;
    padding: 0 32px 24px;
}

.form-container {
    background: #ffffff;
    border-radius: 20px;
    padding: 24px;
    box-shadow: 
        0 4px 20px rgba(0, 0, 0, 0.06),
        0 0 0 1px rgba(0, 0, 0, 0.03);
}

.input-group {
    position: relative;
    margin-bottom: 20px;
    width: 100%;
}

.input-group:last-of-type {
    margin-bottom: 0;
}

.input-label {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: #374151;
    margin-bottom: 8px;
    letter-spacing: 0.3px;
    text-transform: uppercase;
}

.input-wrapper {
    position: relative;
}

.input-icon {
    position: absolute;
    left: 14px;
    top: 50%;
    transform: translateY(-50%);
    color: #9ca3af;
    display: flex;
    align-items: center;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    pointer-events: none;
    z-index: 2;
}

.custom-input {
    width: 100%;
    height: 48px;
    padding: 0 14px 0 44px;
    border: 2px solid #e5e7eb;
    border-radius: 12px;
    font-size: 15px;
    color: #1f2937;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    outline: none;
    background-color: #f9fafb;
    box-sizing: border-box;
    -webkit-appearance: none;
    appearance: none;
    -webkit-tap-highlight-color: transparent;
    font-weight: 500;
}

.custom-input:focus {
    border-color: #667eea;
    background-color: #ffffff;
    box-shadow: 
        0 0 0 4px rgba(102, 126, 234, 0.1),
        0 2px 8px rgba(102, 126, 234, 0.08);
}

.custom-input:focus + .input-icon,
.input-group:focus-within .input-icon {
    color: #667eea;
    transform: translateY(-50%) scale(1.1);
}

.custom-input::placeholder {
    color: #9ca3af;
    font-weight: 400;
}

.expand-wrapper {
    display: grid;
    grid-template-rows: 0fr;
    transition: grid-template-rows 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    opacity: 0;
}

.expand-wrapper.is-expanded {
    grid-template-rows: 1fr;
    opacity: 1;
}

.expand-content {
    overflow: hidden;
}

.error-alert {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 16px;
    background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
    border: 1px solid #fecaca;
    border-radius: 12px;
    margin-top: 16px;
    box-shadow: 0 2px 8px rgba(239, 68, 68, 0.08);
}

.error-icon-svg {
    color: #dc2626;
    flex-shrink: 0;
}

.error-text {
    color: #991b1b;
    font-size: 13px;
    font-weight: 500;
    line-height: 1.4;
}

.footer {
    position: relative;
    z-index: 1;
    padding: 0 32px 32px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding-bottom: env(safe-area-inset-bottom);
}

.submit-btn {
    width: 100%;
    height: 52px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 14px;
    font-size: 16px;
    font-weight: 600;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    box-shadow: 
        0 8px 24px rgba(102, 126, 234, 0.35),
        0 4px 12px rgba(118, 75, 162, 0.25);
    -webkit-tap-highlight-color: transparent;
    touch-action: manipulation;
    position: relative;
    overflow: hidden;
    letter-spacing: 0.3px;
}

.btn-shine {
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left 0.5s ease;
}

.submit-btn:hover:not(:disabled) .btn-shine {
    left: 100%;
}

.submit-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 
        0 12px 32px rgba(102, 126, 234, 0.4),
        0 6px 16px rgba(118, 75, 162, 0.3);
}

.submit-btn:active:not(:disabled) {
    transform: translateY(0) scale(0.98);
}

.submit-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background: linear-gradient(135deg, #a0aec0 0%, #718096 100%);
    box-shadow: none;
}

.loading-spinner {
    width: 18px;
    height: 18px;
    border: 2.5px solid rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    border-top-color: #fff;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.switch-mode-btn {
    background: transparent;
    border: none;
    cursor: pointer;
    font-size: 14px;
    align-self: center;
    padding: 12px 16px;
    min-height: 44px;
    display: flex;
    align-items: center;
    gap: 6px;
    -webkit-tap-highlight-color: transparent;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    border-radius: 10px;
}

.switch-text {
    color: #6b7280;
    font-weight: 500;
}

.switch-link {
    color: #667eea;
    font-weight: 600;
    transition: all 0.2s ease;
}

.switch-arrow {
    color: #667eea;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.switch-mode-btn:hover {
    background: rgba(102, 126, 234, 0.06);
}

.switch-mode-btn:hover .switch-link {
    color: #764ba2;
}

.switch-mode-btn:hover .switch-arrow {
    transform: translateX(4px);
    color: #764ba2;
}

@media screen and (max-width: 480px) {
    .login-card {
        max-width: 100%;
        border-radius: 20px;
    }
    
    .header {
        padding: 28px 24px 24px;
    }
    
    .header h3 {
        font-size: 22px;
    }
    
    .content {
        padding: 0 24px 20px;
    }
    
    .form-container {
        padding: 20px;
    }
    
    .footer {
        padding: 0 24px 24px;
    }
    
    .logo-icon {
        width: 48px;
        height: 48px;
        border-radius: 14px;
    }
    
    .paw-icon {
        font-size: 24px;
    }
}

@media screen and (max-width: 320px) {
    .header {
        padding: 24px 20px 20px;
    }
    
    .header h3 {
        font-size: 20px;
    }
    
    .content {
        padding: 0 20px 16px;
    }
    
    .form-container {
        padding: 16px;
    }
    
    .footer {
        padding: 0 20px 20px;
    }
}
</style>