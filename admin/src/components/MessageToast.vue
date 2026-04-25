<template>
    <div class="toast-container">
        <TransitionGroup name="toast-list" tag="div" class="toast-wrapper">
            <div v-for="msg in messages" :key="msg.id" class="toast-card" @mouseenter="pauseTimer(msg.id)"
                @mouseleave="resumeTimer(msg.id)" @click="removeToast(msg.id)">

                <div class="card-content">
                    <img :src="getAvatarUrl(msg.avatar)" class="avatar" alt="avatar" @error="handleImageError" />
                    <div class="text-area">
                        <div class="header-row">
                            <span class="username">{{ msg.userName }}</span>
                            <span class="time-badge">{{ formatTime(msg.createTime) }}</span>
                        </div>
                        <p class="message-body">{{ msg.content }}</p>
                    </div>
                </div>

                <div class="close-hint">×</div>
            </div>
        </TransitionGroup>
    </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const messages = ref([])
const timers = new Map()

const getAvatarUrl = (avatar) => {
    if (!avatar) return defaultAvatar
    if (avatar.startsWith('http://') || avatar.startsWith('https://')) return avatar
    return `http://localhost:8080${avatar.startsWith('/') ? '' : '/'}${avatar}`
}

const handleImageError = (e) => {
    e.target.src = defaultAvatar
}

// 检测是否为移动端
const isMobile = () => window.innerWidth <= 768

// 添加通知
const addToast = (msg) => {
    if (!msg.id) msg.id = Date.now() + Math.random().toString(36).substr(2, 9)

    // 移动端逻辑：最多只允许存在一条消息
    if (isMobile()) {
        if (messages.value.length > 0) {
            // 如果已有消息，先移除最旧的（或者直接清空，视需求而定，这里移除最旧以保持队列逻辑一致）
            removeToast(messages.value[0].id)
        }
    } else {
        // PC端逻辑：最多3条
        if (messages.value.length >= 3) {
            removeToast(messages.value[0].id)
        }
    }

    messages.value.push(msg)
    startTimer(msg.id)
}

// 定时器管理
const startTimer = (id) => {
    if (timers.has(id)) clearTimeout(timers.get(id))
    // 移动端阅读速度可能较慢，适当延长显示时间，或者保持统一
    const duration = isMobile() ? 3000 : 3000
    const timerId = setTimeout(() => removeToast(id), duration)
    timers.set(id, timerId)
}

const pauseTimer = (id) => {
    if (timers.has(id)) {
        clearTimeout(timers.get(id))
        timers.delete(id)
    }
}

const resumeTimer = (id) => {
    if (!timers.has(id)) {
        const timerId = setTimeout(() => removeToast(id), 2000)
        timers.set(id, timerId)
    }
}

const removeToast = (id) => {
    if (timers.has(id)) {
        clearTimeout(timers.get(id))
        timers.delete(id)
    }
    const index = messages.value.findIndex(m => m.id === id)
    if (index > -1) {
        messages.value.splice(index, 1)
    }
}

const formatTime = (timestamp) => {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    const now = new Date()
    const diff = now - date
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

onUnmounted(() => {
    timers.forEach(id => clearTimeout(id))
    timers.clear()
})

defineExpose({ addToast })
</script>

<style scoped>
.toast-container {
    position: fixed;
    top: 80px;
    right: 24px;
    z-index: 9999;
    width: 360px;
    pointer-events: none;
}

.toast-wrapper {
    display: flex;
    flex-direction: column;
    gap: 12px;
    pointer-events: auto;
}

.toast-card {
    position: relative;
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 
        0 2px 8px rgba(0, 0, 0, 0.04),
        0 8px 24px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(0, 0, 0, 0.04);
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.toast-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, #667eea, #764ba2);
}

.toast-card:hover {
    transform: translateY(-2px);
    box-shadow: 
        0 4px 12px rgba(0, 0, 0, 0.06),
        0 12px 32px rgba(102, 126, 234, 0.12);
}

.card-content {
    padding: 16px 18px 16px 16px;
    display: flex;
    align-items: flex-start;
    gap: 12px;
}

.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
}

.text-area {
    flex: 1;
    min-width: 0;
}

.header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
}

.username {
    font-size: 14px;
    font-weight: 600;
    color: #1a1a2e;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.time-badge {
    font-size: 11px;
    color: #9ca3af;
    flex-shrink: 0;
    margin-left: 8px;
}

.message-body {
    font-size: 13px;
    color: #6b7280;
    line-height: 1.5;
    margin: 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    word-break: break-word;
}

.close-hint {
    position: absolute;
    top: 10px;
    right: 12px;
    font-size: 18px;
    color: #d1d5db;
    opacity: 0;
    transition: all 0.2s ease;
    line-height: 1;
    cursor: pointer;
}

.toast-card:hover .close-hint {
    opacity: 1;
}

.close-hint:hover {
    color: #667eea;
}

.toast-list-enter-active {
    transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.toast-list-enter-from {
    opacity: 0;
    transform: translateX(40px) translateY(8px);
}

.toast-list-leave-active {
    transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1);
    position: absolute;
    width: 100%;
}

.toast-list-leave-to {
    opacity: 0;
    transform: translateX(100%);
    margin-top: 0;
    margin-bottom: 0;
}

.toast-list-move {
    transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@media screen and (max-width: 768px) {
    .toast-container {
        top: 65px;
        right: 16px;
        left: 16px;
        width: auto;
    }

    .toast-wrapper {
        gap: 10px;
    }

    .toast-card {
        border-radius: 12px;
    }

    .toast-list-leave-active {
        width: calc(100% - 0px);
        left: 0;
    }

    .card-content {
        padding: 14px 16px 14px 14px;
    }

    .avatar {
        width: 36px;
        height: 36px;
    }

    .username {
        font-size: 13px;
    }

    .message-body {
        font-size: 12px;
    }
}
</style>