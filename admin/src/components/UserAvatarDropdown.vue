<template>
    <div class="user-avatar-dropdown" :class="{ 'compact': compact }" @mouseenter="isOpen = true" @mouseleave="isOpen = false">
        <div class="avatar-wrapper" @click="handleAvatarClick">
            <div class="avatar-container">
                <div class="avatar-glow"></div>
                <img :src="avatar" alt="User" class="avatar" />
                <div class="avatar-ring"></div>
            </div>
            <span v-if="showNickname" class="nickname">{{ nickname }}</span>
            <svg v-if="showDropdown" class="dropdown-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
        </div>

        <!-- 下拉菜单 -->
        <transition name="dropdown-fade">
            <div v-show="isOpen" class="dropdown-menu" @click.stop>
                <template v-if="userStore.isLoggedIn">
                    <div class="menu-body">
                        <div class="menu-section">
                            <div class="section-label">个人中心</div>
                            <div class="menu-items">
                                <div class="menu-item" @click="handleMenuClick('/personalHome/personalPosts')">
                                    <div class="menu-item-icon">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                            <polyline points="14 2 14 8 20 8"></polyline>
                                            <line x1="16" y1="13" x2="8" y2="13"></line>
                                            <line x1="16" y1="17" x2="8" y2="17"></line>
                                            <polyline points="10 9 9 9 8 9"></polyline>
                                        </svg>
                                    </div>
                                    <div class="menu-item-content">
                                        <div class="menu-item-label">我的作品</div>
                                        <div class="menu-item-desc">查看我发布的帖子</div>
                                    </div>
                                    <svg class="menu-item-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <polyline points="9 18 15 12 9 6"></polyline>
                                    </svg>
                                </div>

                                <div class="menu-item" @click="handleMenuClick('/personalHome/personalLikes')">
                                    <div class="menu-item-icon">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                                        </svg>
                                    </div>
                                    <div class="menu-item-content">
                                        <div class="menu-item-label">我的喜欢</div>
                                        <div class="menu-item-desc">查看我点赞的内容</div>
                                    </div>
                                    <svg class="menu-item-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <polyline points="9 18 15 12 9 6"></polyline>
                                    </svg>
                                </div>

                                <div class="menu-item" @click="handleMenuClick('/personalHome/personalFavorite')">
                                    <div class="menu-item-icon">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                        </svg>
                                    </div>
                                    <div class="menu-item-content">
                                        <div class="menu-item-label">我的收藏</div>
                                        <div class="menu-item-desc">查看我收藏的帖子</div>
                                    </div>
                                    <svg class="menu-item-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <polyline points="9 18 15 12 9 6"></polyline>
                                    </svg>
                                </div>

                                <div class="menu-item" @click="handleMenuClick('/personalHome')">
                                    <div class="menu-item-icon">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                            <circle cx="12" cy="7" r="4"></circle>
                                        </svg>
                                    </div>
                                    <div class="menu-item-content">
                                        <div class="menu-item-label">个人主页</div>
                                        <div class="menu-item-desc">查看我的个人主页</div>
                                    </div>
                                    <svg class="menu-item-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <polyline points="9 18 15 12 9 6"></polyline>
                                    </svg>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="menu-footer">
                        <div class="menu-item logout" @click="handleLogout">
                            <div class="menu-item-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                    <polyline points="16 17 21 12 16 7"></polyline>
                                    <line x1="21" y1="12" x2="9" y2="12"></line>
                                </svg>
                            </div>
                            <div class="menu-item-content">
                                <div class="menu-item-label">退出登录</div>
                            </div>
                        </div>
                    </div>
                </template>
                <template v-else>
                    <div class="menu-body">
                        <div class="menu-items">
                            <div class="menu-item login-action" @click="handleLoginClick">
                                <div class="menu-item-icon">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
                                        <polyline points="10 17 15 12 10 7"></polyline>
                                        <line x1="15" y1="12" x2="3" y2="12"></line>
                                    </svg>
                                </div>
                                <div class="menu-item-content">
                                    <div class="menu-item-label">立即登录</div>
                                    <div class="menu-item-desc">登录以使用完整功能</div>
                                </div>
                                <svg class="menu-item-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="9 18 15 12 9 6"></polyline>
                                </svg>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
        </transition>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useAppStore } from '../stores/app'

const props = defineProps({
    showDropdown: {
        type: Boolean,
        default: true
    },
    showNickname: {
        type: Boolean,
        default: true
    },
    compact: {
        type: Boolean,
        default: false
    }
})

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const isOpen = ref(false)

const avatar = computed(() => userStore.userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')
const nickname = computed(() => userStore.userInfo?.nickname || '登录')

const handleAvatarClick = () => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal()
        return
    }
    window.open('/personalHome', '_blank')
}

const handleMenuClick = (path) => {
    isOpen.value = false
    window.open(path, '_blank')
}

const handleLoginClick = () => {
    isOpen.value = false
    appStore.openLoginModal()
}

const handleLogout = () => {
    isOpen.value = false
    userStore.logout()
    router.push('/')
    window.location.reload()
}
</script>

<style scoped>
.user-avatar-dropdown {
    position: relative;
    display: inline-flex;
    align-items: center;
}

.avatar-wrapper {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 6px 14px;
    border-radius: 28px;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.avatar-wrapper:hover {
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
    transform: translateY(-2px);
}

.avatar-container {
    position: relative;
    width: 38px;
    height: 38px;
    flex-shrink: 0;
}

.avatar-glow {
    position: absolute;
    inset: -4px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    opacity: 0;
    filter: blur(8px);
    transition: opacity 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    animation: glow-pulse 3s ease-in-out infinite;
}

.avatar-wrapper:hover .avatar-glow {
    opacity: 0.6;
}

@keyframes glow-pulse {
    0%, 100% {
        transform: scale(1);
        opacity: 0.4;
    }
    50% {
        transform: scale(1.1);
        opacity: 0.6;
    }
}

.avatar {
    position: relative;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
    border: 2.5px solid #fff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    backface-visibility: hidden;
    -webkit-backface-visibility: hidden;
    z-index: 1;
}

.avatar-ring {
    position: absolute;
    inset: -2px;
    border-radius: 50%;
    border: 2px solid transparent;
    background: linear-gradient(135deg, #667eea, #764ba2) border-box;
    -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
    mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: destination-out;
    mask-composite: exclude;
    opacity: 0;
    transition: opacity 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.avatar-wrapper:hover .avatar {
    transform: scale(1.08);
}

.avatar-wrapper:hover .avatar-ring {
    opacity: 1;
}

.nickname {
    font-size: 0.9rem;
    color: #1f2937;
    font-weight: 600;
    max-width: 160px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    letter-spacing: 0.02em;
}

.dropdown-arrow {
    width: 18px;
    height: 18px;
    color: #6b7280;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.avatar-wrapper:hover .dropdown-arrow {
    transform: rotate(180deg);
    color: #667eea;
}

/* 下拉菜单 */
.dropdown-menu {
    position: absolute;
    top: calc(100% + 12px);
    right: 0;
    width: 280px;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    box-shadow: 
        0 24px 64px rgba(0, 0, 0, 0.12),
        0 0 0 1px rgba(102, 126, 234, 0.08),
        0 8px 24px rgba(102, 126, 234, 0.08);
    overflow: hidden;
    z-index: 1100;
    pointer-events: auto;
}

/* 菜单主体 */
.menu-body {
    padding: 8px;
}

.menu-section {
    margin-bottom: 4px;
}

.section-label {
    padding: 8px 12px 6px;
    font-size: 0.7rem;
    font-weight: 600;
    color: #9ca3af;
    text-transform: uppercase;
    letter-spacing: 0.08em;
}

.menu-items {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.menu-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
}

.menu-item::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
    opacity: 0;
    transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-item:hover::before {
    opacity: 1;
}

.menu-item:hover {
    transform: translateX(4px);
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.12);
}

.menu-item-icon {
    position: relative;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, rgba(118, 75, 162, 0.12) 100%);
    border-radius: 10px;
    color: #667eea;
    flex-shrink: 0;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-item-icon svg {
    width: 18px;
    height: 18px;
    stroke-width: 2;
}

.menu-item:hover .menu-item-icon {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    transform: scale(1.08);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.menu-item-content {
    flex: 1;
    min-width: 0;
    position: relative;
    z-index: 1;
}

.menu-item-label {
    font-size: 0.875rem;
    font-weight: 600;
    color: #1f2937;
    margin-bottom: 2px;
    transition: color 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.menu-item:hover .menu-item-label {
    color: #667eea;
}

.menu-item-desc {
    font-size: 0.725rem;
    color: #9ca3af;
    line-height: 1.3;
}

.menu-item-arrow {
    position: relative;
    width: 16px;
    height: 16px;
    color: #d1d5db;
    opacity: 0;
    transform: translateX(-8px);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    flex-shrink: 0;
}

.menu-item:hover .menu-item-arrow {
    opacity: 1;
    transform: translateX(0);
    color: #667eea;
}

/* 菜单底部 */
.menu-footer {
    padding: 8px;
    border-top: 1px solid rgba(102, 126, 234, 0.08);
    background: linear-gradient(180deg, transparent 0%, rgba(102, 126, 234, 0.02) 100%);
}

.menu-item.logout {
    margin: 0;
}

.menu-item.logout::before {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.08) 0%, rgba(220, 38, 38, 0.08) 100%);
}

.menu-item.logout:hover {
    box-shadow: 0 4px 16px rgba(239, 68, 68, 0.12);
}

.menu-item.logout .menu-item-icon {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.12) 0%, rgba(220, 38, 38, 0.12) 100%);
    color: #ef4444;
}

.menu-item.logout:hover .menu-item-icon {
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.menu-item.logout .menu-item-label {
    color: #ef4444;
}

.menu-item.logout .menu-item-label {
    color: #ef4444;
}

.menu-item.login-action {
    margin: 4px;
}

.menu-item.login-action::before {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, rgba(118, 75, 162, 0.12) 100%);
}

.menu-item.login-action .menu-item-icon {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
    color: #667eea;
}

.menu-item.login-action:hover .menu-item-icon {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
}

.menu-item.login-action .menu-item-label {
    color: #667eea;
    font-weight: 700;
}

/* 动画 */
.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
    transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-fade-enter-from {
    opacity: 0;
    transform: translateY(-12px) scale(0.96);
    transform-origin: top right;
}

.dropdown-fade-leave-to {
    opacity: 0;
    transform: translateY(-12px) scale(0.96);
    transform-origin: top right;
}

/* 紧凑模式 */
.compact .avatar-wrapper {
    padding: 4px;
    background: transparent;
    border: none;
}

.compact .avatar-wrapper:hover {
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
}

.compact .nickname {
    display: none;
}

.compact .dropdown-arrow {
    display: none;
}

.compact .avatar-container {
    width: 36px;
    height: 36px;
}

@media screen and (max-width: 768px) {
    .nickname {
        display: none;
    }
    
    .dropdown-arrow {
        display: none;
    }
    
    .avatar-wrapper {
        padding: 4px;
        background: transparent;
        border: none;
    }
    
    .avatar-wrapper:hover {
        background: rgba(255, 255, 255, 0.9);
        box-shadow: 0 8px 32px rgba(102, 126, 234, 0.15);
    }
    
    .avatar-container {
        width: 34px;
        height: 34px;
    }
}

@media screen and (max-width: 480px) {
    .avatar-container {
        width: 32px;
        height: 32px;
    }
}
</style>
