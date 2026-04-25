<template>
    <header class="top-navbar">
        <div class="navbar-bg-effects">
            <div class="gradient-orb orb-1"></div>
            <div class="gradient-orb orb-2"></div>
            <div class="gradient-orb orb-3"></div>
        </div>
        
        <div class="navbar-content">
            <div class="navbar-left">
                <div class="brand" @click="handleBrandClick">
                    <div class="brand-icon-wrapper">
                        <span class="brand-icon">🐾</span>
                        <div class="icon-glow"></div>
                    </div>
                    <span class="brand-name">PetCommunity</span>
                </div>

                <nav v-if="showNavLinks" class="nav-links">
                    <router-link v-for="link in navLinks" :key="link.path" :to="link.path" active-class="active" class="nav-link">
                        <span class="link-icon" v-html="link.icon"></span>
                        <span class="link-text">{{ link.text }}</span>
                        <span class="link-indicator"></span>
                    </router-link>
                </nav>
            </div>

            <div class="navbar-right">
                <slot name="actions">
                    <div class="default-actions">
                        <div v-if="showSearch" class="search-box" :class="{ 'is-expanded': isSearchOpen }">
                            <div class="search-icon-wrapper" @click="toggleSearch">
                                <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2"
                                    fill="none" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="11" cy="11" r="8"></circle>
                                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                                </svg>
                            </div>
                            <input ref="searchInputRef" type="text" placeholder="搜索感兴趣的内容..." class="search-input"
                                v-model="searchQuery" @blur="handleSearchBlur" @keyup.enter="handleSearch" />
                        </div>

                        <div class="desktop-actions">
                            <NotificationDropdown />

                            <div class="action-icon-wrapper" @click="handleSubmitPost" title="发布帖子">
                                <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2"
                                    fill="none" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                                    <polyline points="17 8 12 3 7 8"></polyline>
                                    <line x1="12" y1="3" x2="12" y2="15"></line>
                                </svg>
                            </div>

                            <UserAvatarDropdown />
                        </div>

                        <div class="mobile-menu-btn" @click="toggleMobileMenu">
                            <span class="menu-icon" :class="{ 'is-active': isMobileMenuOpen }">
                                <span></span>
                                <span></span>
                                <span></span>
                            </span>
                        </div>
                    </div>
                </slot>
            </div>
        </div>

        <transition name="slide-down">
            <div v-if="isMobileMenuOpen && showNavLinks" class="mobile-menu">
                <nav class="mobile-nav-links">
                    <router-link v-for="link in navLinks" :key="link.path" :to="link.path" active-class="active" 
                        class="mobile-nav-link" @click="closeMobileMenu">
                        <span class="mobile-link-icon" v-html="link.icon"></span>
                        <span class="mobile-link-text">{{ link.text }}</span>
                    </router-link>
                </nav>
                <div class="mobile-actions">
                    <NotificationDropdown />
                    <div class="action-icon-wrapper" @click="handleSubmitPost">
                        <svg viewBox="0 0 24 24" width="20" height="20" stroke="currentColor" stroke-width="2"
                            fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                            <polyline points="17 8 12 3 7 8"></polyline>
                            <line x1="12" y1="3" x2="12" y2="15"></line>
                        </svg>
                        <span class="action-text">发布</span>
                    </div>
                    <UserAvatarDropdown />
                </div>
            </div>
        </transition>
    </header>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '../stores/user';
import { useAppStore } from '../stores/app';
import { ElMessage } from 'element-plus';
import NotificationDropdown from '../components/NotificationDropdown.vue';
import UserAvatarDropdown from '../components/UserAvatarDropdown.vue';

const props = defineProps({
    showNavLinks: {
        type: Boolean,
        default: true
    },
    showSearch: {
        type: Boolean,
        default: true
    },
    customNavLinks: {
        type: Array,
        default: null
    },
    brandClickPath: {
        type: String,
        default: '/'
    }
});

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const appStore = useAppStore();

const isSearchOpen = ref(false);
const searchQuery = ref('');
const searchInputRef = ref(null);
const isMobileMenuOpen = ref(false);

const toggleMobileMenu = () => {
    isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

const closeMobileMenu = () => {
    isMobileMenuOpen.value = false;
};

const defaultNavLinks = [
    { 
        path: '/authority', 
        text: '官方资讯', 
        icon: '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path></svg>' 
    },
    { 
        path: '/community', 
        text: '宠友社区', 
        icon: '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>' 
    },
    { 
        path: '/pets', 
        text: '萌宠图鉴', 
        icon: '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><path d="M8 14s1.5 2 4 2 4-2 4-2"></path><line x1="9" y1="9" x2="9.01" y2="9"></line><line x1="15" y1="9" x2="15.01" y2="9"></line></svg>' 
    }
];

const navLinks = computed(() => props.customNavLinks || defaultNavLinks);

const handleBrandClick = () => {
    router.push(props.brandClickPath);
};

const handleSubmitPost = () => {
    if (!userStore.isLoggedIn) {
        ElMessage.warning('请先登录');
        appStore.openLoginModal();
        return;
    }
    window.open('/submit', '_blank');
};

const toggleSearch = () => {
    if (window.innerWidth > 768) {
        searchInputRef.value?.focus();
    } else {
        isSearchOpen.value = !isSearchOpen.value;
        if (isSearchOpen.value) {
            nextTick(() => searchInputRef.value?.focus());
        }
    }
};

const handleSearchBlur = () => {
    if (window.innerWidth <= 768) {
        setTimeout(() => {
            isSearchOpen.value = false;
        }, 200);
    }
};

const handleSearch = () => {
    console.log('Searching for:', searchQuery.value);
};
</script>

<style scoped>
.top-navbar {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
    background: rgba(255, 255, 255, 0.82);
    backdrop-filter: blur(24px) saturate(180%);
    -webkit-backdrop-filter: blur(24px) saturate(180%);
    border-bottom: 1px solid rgba(255, 255, 255, 0.3);
    box-shadow: 
        0 1px 3px rgba(0, 0, 0, 0.04),
        0 8px 16px rgba(0, 0, 0, 0.02);
}

.navbar-bg-effects {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    overflow: hidden;
}

.gradient-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.15;
    animation: orbFloat 20s ease-in-out infinite;
}

.orb-1 {
    width: 300px;
    height: 300px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    top: -150px;
    left: -50px;
    animation-delay: 0s;
}

.orb-2 {
    width: 250px;
    height: 250px;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    top: -100px;
    right: 100px;
    animation-delay: -7s;
}

.orb-3 {
    width: 200px;
    height: 200px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    bottom: -100px;
    left: 50%;
    animation-delay: -14s;
}

@keyframes orbFloat {
    0%, 100% {
        transform: translate(0, 0) scale(1);
    }
    33% {
        transform: translate(30px, -20px) scale(1.1);
    }
    66% {
        transform: translate(-20px, 20px) scale(0.9);
    }
}

.navbar-content {
    position: relative;
    z-index: 1;
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 32px;
    height: 72px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-sizing: border-box;
}

.navbar-left {
    display: flex;
    align-items: center;
    gap: 48px;
}

.brand {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    user-select: none;
}

.brand:hover {
    transform: scale(1.02);
}

.brand-icon-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    box-shadow: 
        0 4px 12px rgba(102, 126, 234, 0.3),
        0 2px 4px rgba(0, 0, 0, 0.1);
}

.brand-icon {
    font-size: 1.5rem;
    position: relative;
    z-index: 1;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.icon-glow {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12px;
    transform: translate(-50%, -50%);
    filter: blur(8px);
    opacity: 0.5;
    animation: glowPulse 3s ease-in-out infinite;
}

@keyframes glowPulse {
    0%, 100% {
        opacity: 0.5;
        transform: translate(-50%, -50%) scale(1);
    }
    50% {
        opacity: 0.8;
        transform: translate(-50%, -50%) scale(1.2);
    }
}

.brand-name {
    font-size: 1.4rem;
    font-weight: 800;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    letter-spacing: -0.5px;
}

.nav-links {
    display: none;
    align-items: center;
    gap: 8px;
}

.nav-link {
    position: relative;
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 18px;
    text-decoration: none;
    color: #64748b;
    font-weight: 500;
    font-size: 0.95rem;
    border-radius: 12px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    overflow: hidden;
}

.nav-link::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
    border-radius: 12px;
}

.nav-link:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.08);
}

.nav-link:hover::before {
    opacity: 0;
}

.nav-link.active {
    color: #ffffff;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    font-weight: 600;
    box-shadow: 
        0 4px 12px rgba(102, 126, 234, 0.3),
        0 2px 4px rgba(0, 0, 0, 0.1);
}

.link-icon {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
}

.link-text {
    position: relative;
    z-index: 1;
}

.link-indicator {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%) scaleX(0);
    width: 60%;
    height: 3px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    border-radius: 2px;
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-link.active .link-indicator {
    display: none;
}

.navbar-right {
    display: flex;
    align-items: center;
    gap: 16px;
}

.default-actions {
    display: flex;
    align-items: center;
    gap: 12px;
}

.desktop-actions {
    display: flex;
    align-items: center;
    gap: 12px;
}

.mobile-menu-btn {
    display: none;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    cursor: pointer;
    border-radius: 12px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.mobile-menu-btn:hover {
    background: rgba(102, 126, 234, 0.08);
}

.menu-icon {
    display: flex;
    flex-direction: column;
    gap: 5px;
    width: 20px;
    transition: all 0.3s ease;
}

.menu-icon span {
    display: block;
    width: 100%;
    height: 2px;
    background: #64748b;
    border-radius: 2px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    transform-origin: center;
}

.menu-icon.is-active span:nth-child(1) {
    transform: translateY(7px) rotate(45deg);
}

.menu-icon.is-active span:nth-child(2) {
    opacity: 0;
    transform: scaleX(0);
}

.menu-icon.is-active span:nth-child(3) {
    transform: translateY(-7px) rotate(-45deg);
}

.mobile-menu {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border-top: 1px solid rgba(0, 0, 0, 0.06);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    padding: 16px 20px;
    z-index: 1001;
}

.mobile-nav-links {
    display: flex;
    flex-direction: column;
    gap: 4px;
    margin-bottom: 16px;
}

.mobile-nav-link {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    text-decoration: none;
    color: #64748b;
    font-weight: 500;
    font-size: 1rem;
    border-radius: 12px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.mobile-nav-link:hover {
    background: rgba(102, 126, 234, 0.08);
    color: #667eea;
}

.mobile-nav-link.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    font-weight: 600;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.mobile-link-icon {
    display: flex;
    align-items: center;
}

.mobile-actions {
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding: 12px 0;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.mobile-actions .action-icon-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 16px;
}

.action-text {
    font-size: 0.9rem;
    font-weight: 500;
    color: #64748b;
}

.slide-down-enter-active,
.slide-down-leave-active {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-down-enter-from,
.slide-down-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}

.search-box {
    position: relative;
    display: flex;
    align-items: center;
}

.search-icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    color: #64748b;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-icon-wrapper:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.08);
}

.search-input {
    width: 280px;
    padding: 10px 16px 10px 44px;
    border: 1px solid rgba(0, 0, 0, 0.08);
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.6);
    font-size: 0.9rem;
    color: #1e293b;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    outline: none;
}

.search-input:focus {
    width: 320px;
    background: #ffffff;
    border-color: #667eea;
    box-shadow: 
        0 0 0 4px rgba(102, 126, 234, 0.1),
        0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-input::placeholder {
    color: #94a3b8;
}

.search-icon-wrapper {
    position: absolute;
    left: 10px;
    z-index: 1;
    pointer-events: auto;
}

.action-icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    color: #64748b;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-icon-wrapper:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.08);
    transform: translateY(-2px);
}

@media screen and (min-width: 1025px) {
    .nav-links {
        display: flex;
    }
    
    .link-text {
        display: inline;
    }
}

@media screen and (min-width: 769px) and (max-width: 1024px) {
    .nav-links {
        display: flex;
    }
    
    .link-text {
        display: none;
    }
    
    .nav-link {
        padding: 10px 14px;
    }
}

@media screen and (max-width: 768px) {
    .navbar-content {
        height: 60px;
        padding: 0 12px;
    }

    .navbar-left {
        gap: 12px;
    }

    .brand-name {
        display: none;
    }

    .brand-icon-wrapper {
        width: 32px;
        height: 32px;
        border-radius: 10px;
    }

    .brand-icon {
        font-size: 1.2rem;
    }

    .nav-links {
        display: flex !important;
        gap: 4px;
    }

    .link-text {
        display: none;
    }

    .nav-link {
        padding: 8px 10px;
        gap: 0;
    }

    .link-icon {
        margin: 0;
    }

    .link-icon svg {
        width: 18px;
        height: 18px;
    }

    .desktop-actions {
        display: flex;
        gap: 6px;
    }

    .desktop-actions .action-icon-wrapper {
        width: 36px;
        height: 36px;
    }

    .desktop-actions .action-icon-wrapper svg {
        width: 18px;
        height: 18px;
    }

    .mobile-menu-btn {
        display: none;
    }

    .search-box {
        display: none;
    }

    .default-actions {
        gap: 8px;
    }
}

@media screen and (max-width: 480px) {
    .navbar-content {
        padding: 0 8px;
    }

    .navbar-left {
        gap: 8px;
    }

    .brand-icon-wrapper {
        width: 30px;
        height: 30px;
    }

    .nav-links {
        gap: 2px;
    }

    .nav-link {
        padding: 6px 8px;
    }

    .link-icon svg {
        width: 16px;
        height: 16px;
    }

    .desktop-actions {
        gap: 4px;
    }

    .desktop-actions .action-icon-wrapper {
        width: 32px;
        height: 32px;
    }

    .desktop-actions .action-icon-wrapper svg {
        width: 16px;
        height: 16px;
    }

    .default-actions {
        gap: 6px;
    }
}
</style>
