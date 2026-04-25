<!-- e:\java东软实训项目\pet_owner_community\admin\src\views\PersonalHome.vue -->
<template>
    <div class="personal-home-container">
        <LoginModal :visible="isLoginModalVisible" @update:visible="val => isLoginModalVisible = val" />
        <ProfileEditModal :visible="isProfileEditVisible" @update:visible="val => isProfileEditVisible = val" @saved="initPageData" />
        
        <TopNavbar :show-nav-links="false" :show-search="false">
            <template #actions>
                <div class="navbar-actions">
                    <template v-if="isSelf">
                        <NotificationDropdown />

                        <div class="submit-icon-wrapper" @click="handleSubmitPost">
                            <svg viewBox="0 0 24 24" width="22" height="22" stroke="currentColor" stroke-width="2"
                                fill="none" stroke-linecap="round" stroke-linejoin="round" class="submit-svg">
                                <path d="M12 20h9"></path>
                                <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path>
                            </svg>
                        </div>
                    </template>

                    <template v-if="isSelf">
                        <UserAvatarDropdown />
                    </template>
                    <template v-else>
                        <div class="other-user-avatar" @click="window.open('/personalHome', '_blank')">
                            <img :src="userInfo.avatar || defaultAvatar" alt="User" class="avatar" />
                            <span class="user-nickname">{{ userInfo.nickname || '宠友用户' }}</span>
                        </div>
                    </template>

                    <button v-if="!isSelf && userInfo" 
                            class="btn-follow-advanced" 
                            :class="{ 
                                'followed': isFollowing,
                                'animating': followLoading,
                                'follow-animation': showFollowAnimation 
                            }" 
                            @click="handleFollow" 
                            :disabled="followLoading">
                        <span class="btn-icon">
                            <svg v-if="!isFollowing" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                <circle cx="8.5" cy="7" r="4"></circle>
                                <line x1="20" y1="8" x2="20" y2="14"></line>
                                <line x1="23" y1="11" x2="17" y2="11"></line>
                            </svg>
                            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                <circle cx="8.5" cy="7" r="4"></circle>
                                <polyline points="17 11 19 13 23 9"></polyline>
                            </svg>
                        </span>
                        <span class="btn-text">{{ followLoading ? '处理中...' : (isFollowing ? '已关注' : '+ 关注') }}</span>
                        <span class="btn-particles" v-if="showFollowAnimation"></span>
                    </button>
                </div>
            </template>
        </TopNavbar>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-wrapper">
            <div class="spinner"></div>
            <p>加载中...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-wrapper">
            <div class="error-icon">⚠️</div>
            <p>{{ error }}</p>
            <button @click="initPageData" class="btn-retry">重试</button>
        </div>

        <!-- 用户信息卡片 (数据加载成功后显示) -->
        <div v-else-if="userInfo" class="profile-card">
            <!-- 封面背景 -->
            <div class="profile-cover"></div>
            
            <div class="profile-header">
                <div class="avatar-wrapper">
                    <img :src="userInfo.avatar || defaultAvatar" alt="Avatar" class="avatar" />
                    <div class="avatar-ring"></div>
                    <button v-if="isSelf" class="btn-edit-avatar" @click="isProfileEditVisible = true" title="编辑资料">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                        </svg>
                    </button>
                </div>

                <div class="user-info">
                    <h2 class="nickname">{{ userInfo.nickname || '宠友用户' }}</h2>
                    <p class="bio">{{ userInfo.signature || '这个人很懒，什么都没写~' }}</p>
                    <div class="user-meta">
                        <!-- 情绪状态标签 -->
                        <span v-if="userInfo.moodStatus" class="meta-item mood-tag">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <path d="M8 14s1.5 2 4 2 4-2 4-2"></path>
                                <line x1="9" y1="9" x2="9.01" y2="9"></line>
                                <line x1="15" y1="9" x2="15.01" y2="9"></line>
                            </svg>
                            {{ userInfo.moodStatus }}
                        </span>
                        
                        <!-- 心理标签 -->
                        <span v-if="userInfo.mentalTag" class="meta-item mental-tag">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                            </svg>
                            {{ userInfo.mentalTag }}
                        </span>
                    </div>
                </div>

                <div class="stats-container">
                    <div class="stat-item" @click="goToTab('posts')">
                        <span class="stat-value">{{ stats.posts }}</span>
                        <span class="stat-label">作品</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-value">{{ stats.likeCount }}</span>
                        <span class="stat-label">获赞</span>
                    </div>
                    <div class="stat-item" @click="goToFollowing">
                        <span class="stat-value">{{ stats.followCount }}</span>
                        <span class="stat-label">关注</span>
                    </div>
                    <div class="stat-item" @click="goToFollowers">
                        <span class="stat-value">{{ stats.fansCount }}</span>
                        <span class="stat-label">粉丝</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 导航栏 Tab -->
        <div class="nav-tabs">
            <router-link :to="isSelf ? '/personalHome/personalPosts' : `/user/${route.params.id}/posts`" active-class="active-tab" class="tab-item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                    <polyline points="14 2 14 8 20 8"></polyline>
                    <line x1="16" y1="13" x2="8" y2="13"></line>
                    <line x1="16" y1="17" x2="8" y2="17"></line>
                    <polyline points="10 9 9 9 8 9"></polyline>
                </svg>
                {{ isSelf ? '我的作品' : 'TA的作品' }}
                <span class="tab-count">({{ stats.posts }})</span>
            </router-link>
            <router-link :to="isSelf ? '/personalHome/personalLikes' : `/user/${route.params.id}/likes`" active-class="active-tab" class="tab-item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
                {{ isSelf ? '我的喜欢' : 'TA的喜欢' }}
                <span class="tab-count">({{ stats.likes }})</span>
            </router-link>
            <router-link :to="isSelf ? '/personalHome/personalFavorite' : `/user/${route.params.id}/favorites`" active-class="active-tab" class="tab-item">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2z"></path>
                </svg>
                {{ isSelf ? '我的收藏' : 'TA的收藏' }}
                <span class="tab-count">({{ stats.favorites }})</span>
            </router-link>
        </div>

        <!-- 未登录提示卡片 (仅自己主页未登录时显示) -->
        <div v-if="isSelf && !userStore.isLoggedIn" class="login-prompt-overlay">
            <div class="login-prompt-card">
                <div class="prompt-icon">
                    <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                </div>
                <h3 class="prompt-title">请先登录</h3>
                <p class="prompt-desc">登录后可以查看详细内容、进行互动操作</p>
                <button class="prompt-login-btn" @click="handleLoginPrompt">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
                        <polyline points="10 17 15 12 10 7"></polyline>
                        <line x1="15" y1="12" x2="3" y2="12"></line>
                    </svg>
                    立即登录
                </button>
            </div>
        </div>

        <!-- 内容区域 -->
        <div class="content-area" :class="{ 'content-blurred': isSelf && !userStore.isLoggedIn }">
            <router-view name="default" />
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch, onUnmounted, provide } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../stores/user';
import { getUserByIdApi, followUserApi } from '../api/user';
import { getMyPostStatsApi, getUserPostStatsApi } from '../api/post';
import NotificationDropdown from '../components/NotificationDropdown.vue';
import UserAvatarDropdown from '../components/UserAvatarDropdown.vue';
import LoginModal from '../components/LoginModal.vue';
import ProfileEditModal from '../components/ProfileEditModal.vue';
import { ElMessage } from 'element-plus';
import { useAppStore } from '../stores/app';
import TopNavbar from '../components/TopNavbar.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const appStore = useAppStore();

const isLoginModalVisible = ref(false)
const isProfileEditVisible = ref(false)

const handleLoginPrompt = () => {
    appStore.openLoginModal()
}

// 同步appStore的登录弹窗状态
watch(() => appStore.isLoginModalVisible, (val) => {
    isLoginModalVisible.value = val
})

// 监听本地弹窗状态同步到appStore
watch(isLoginModalVisible, (val) => {
    if (val !== appStore.isLoginModalVisible) {
        if (val) {
            appStore.openLoginModal()
        } else {
            appStore.closeLoginModal()
        }
    }
})

// 登录成功后关闭弹窗
watch(() => userStore.isLoggedIn, (val) => {
    if (val) {
        appStore.closeLoginModal()
        isLoginModalVisible.value = false
    }
})

// 提供给子组件打开登录弹窗的方法
provide('openLoginModal', () => {
    appStore.openLoginModal()
})

// 状态定义
const loading = ref(false);
const error = ref(null);
const defaultAvatar = ''; // 默认头像URL

// 用户信息对象
const userInfo = reactive({
    id: null,
    nickname: '',
    avatar: '',
    signature: '',
    moodStatus: '',
    mentalTag: ''
});

// 统计数据
const stats = reactive({
    posts: 0,
    likeCount: 0,
    fansCount: 0,
    followCount: 0,
    likes: 0,
    favorites: 0
});

// 关注状态 (仅针对他人主页)
const isFollowing = ref(false);
const followLoading = ref(false);
const showFollowAnimation = ref(false);

// 格式化日期
const formatDate = (dateStr) => {
    if (!dateStr) return '未知';
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 【核心逻辑】判断是否是当前登录用户自己
// 比较路由参数中的 id 和 store 中的用户 id
const isSelf = computed(() => {
    // 如果路由没有 params.id (即访问 /personalHome)，或者是自己的ID
    const targetId = route.params.id;
    const currentId = userStore.userInfo?.id;

    if (!targetId) return true; // 访问 /personalHome 默认为自己
    if (!currentId) return false; // 未登录情况下访问他人主页

    return String(targetId) === String(currentId);
});

// 初始化页面数据
const initPageData = async () => {
    loading.value = true;
    error.value = null;

    try {
        if (isSelf.value) {
            // --- 场景1：查看自己 ---
            // 直接从 Pinia Store 获取数据
            if (userStore.userInfo) {
                userInfo.id = userStore.userInfo.id;
                userInfo.nickname = userStore.userInfo.nickname || '宠友用户';
                userInfo.avatar = userStore.userInfo.avatar || defaultAvatar;
                userInfo.signature = userStore.userInfo.signature || '这个人很懒，什么都没写~';
                userInfo.moodStatus = userStore.userInfo.moodStatus || '';
                userInfo.mentalTag = userStore.userInfo.mentalTag || '';

                stats.likeCount = userStore.userInfo.likeCount || 0;
                stats.fansCount = userStore.userInfo.fansCount || 0;
                stats.followCount = userStore.userInfo.followCount || 0;
            } else {
                // 如果 Store 没数据，可能未登录或数据丢失，提示登录或刷新
                throw new Error('用户信息缺失，请重新登录');
            }
        } else {
            // --- 场景2：查看他人 ---
            // 调用 API 获取数据
            const targetUserId = route.params.id;
            const res = await getUserByIdApi(targetUserId);

            if (res.code === 0) {
                const data = res.data;
                userInfo.id = data.id;
                userInfo.nickname = data.nickname || '未知用户';
                userInfo.avatar = data.avatar || defaultAvatar;
                userInfo.signature = data.signature || '这个人很懒，什么都没写~';
                userInfo.moodStatus = data.moodStatus || '';
                userInfo.mentalTag = data.mentalTag || '';

                stats.likeCount = data.likeCount || 0;
                stats.fansCount = data.fansCount || 0;
                stats.followCount = data.followCount || 0;

                isFollowing.value = data.isFollowed || false;
            } else {
                throw new Error(res.message || '获取用户信息失败');
            }
        }

        // 获取帖子统计数据
        await fetchPostStats();
    } catch (err) {
        console.error(err);
        error.value = err.message || '网络请求失败';
    } finally {
        loading.value = false;
    }
};

// 获取帖子统计数据
const fetchPostStats = async () => {
    try {
        let res;
        if (isSelf.value) {
            res = await getMyPostStatsApi();
        } else {
            res = await getUserPostStatsApi(userInfo.id);
        }
        if (res.code === 0) {
            const data = res.data;
            stats.posts = data.postCount || 0;
            stats.likes = data.likeCount || 0;
            stats.favorites = data.favoriteCount || 0;
        }
    } catch (err) {
        console.error('获取帖子统计数据失败:', err);
    }
};

// 处理关注点击
const handleFollow = async () => {
    if (!userStore.isLoggedIn) {
        isLoginModalVisible.value = true
        return
    }
    
    const originalIsFollowing = isFollowing.value;
    const originalFansCount = stats.fansCount;
    
    isFollowing.value = !isFollowing.value;
    stats.fansCount = isFollowing.value ? originalFansCount + 1 : originalFansCount - 1;
    
    if (isFollowing.value) {
        showFollowAnimation.value = true;
        setTimeout(() => {
            showFollowAnimation.value = false;
        }, 600);
    }
    
    followLoading.value = true;
    try {
        const res = await followUserApi(userInfo.id);
        if (res.code !== 0) {
            throw new Error(res.message || '操作失败');
        }
        
        if (userStore.userInfo) {
            userStore.updateUserInfo({
                followCount: (userStore.userInfo.followCount || 0) + (isFollowing.value ? 1 : -1)
            });
        }
    } catch (err) {
        console.error(err);
        isFollowing.value = originalIsFollowing;
        stats.fansCount = originalFansCount;
        alert(err.message || '网络请求失败');
    } finally {
        followLoading.value = false;
    }
};

// 监听路由变化，当从一个人主页跳到另一个人主页时，重新加载数据
watch(() => route.params.id, () => {
    initPageData();
});

// 监听 userStore.userInfo 变化，实时更新个人主页的关注数和粉丝数
watch(() => userStore.userInfo, (newUserInfo) => {
    if (isSelf.value && newUserInfo) {
        stats.fansCount = newUserInfo.fansCount || 0;
        stats.followCount = newUserInfo.followCount || 0;
        stats.likeCount = newUserInfo.likeCount || 0;
    }
}, { deep: true });

// 更新页面标题
const updatePageTitle = () => {
    if (isSelf.value) {
        document.title = '我的主页 - PetCommunity';
    } else {
        document.title = `${userInfo.nickname || '用户'}的主页 - PetCommunity`;
    }
};

// 跳转到关注列表
const goToFollowing = () => {
    const targetPath = isSelf.value ? '/personalHome/following' : `/user/${route.params.id}/following`;
    router.push(targetPath);
};

// 跳转到粉丝列表
const goToFollowers = () => {
    const targetPath = isSelf.value ? '/personalHome/followers' : `/user/${route.params.id}/followers`;
    router.push(targetPath);
};

// 跳转到指定Tab
const goToTab = (tab) => {
    if (tab === 'posts') {
        const targetPath = isSelf.value ? '/personalHome/personalPosts' : `/user/${route.params.id}/posts`;
        router.push(targetPath);
    }
};

// 处理投稿点击
const handleSubmitPost = () => {
    if (!userStore.isLoggedIn) {
        ElMessage.warning('请先登录');
        appStore.openLoginModal();
        return;
    }
    window.open('/submit', '_blank');
};

// 处理导航点击
const handleNavigate = (path) => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
        return;
    } else {
        window.open(path, '_blank');
    }
};

// 处理退出登录
const handleAuthAction = () => {
    if (userStore.isLoggedIn) {
        userStore.logout();
        router.push('/');
        window.location.reload();
    } else {
        appStore.openLoginModal();
    }
};

// 监听路由变化更新标题
watch(() => route.path, () => {
    updatePageTitle();
});

onMounted(() => {
    initPageData();
    updatePageTitle();
});

onUnmounted(() => {
    document.title = 'PetCommunity';
});
</script>

<style scoped>
.personal-home-container {
    min-height: 100vh;
    background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    padding-top: 80px; /* 为固定导航栏留出空间 */
}

.navbar-actions {
    display: flex;
    align-items: center;
    gap: 12px;
}

.submit-icon-wrapper {
    cursor: pointer;
    color: #64748b;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    align-items: center;
    padding: 10px;
    border-radius: 12px;
}

.submit-icon-wrapper:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.08);
    transform: translateY(-2px);
}

.submit-svg {
    display: block;
}

.other-user-avatar {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 12px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.other-user-avatar:hover {
    background: rgba(102, 126, 234, 0.08);
}

.other-user-avatar .avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-nickname {
    font-size: 0.9rem;
    color: #374151;
    font-weight: 500;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.btn-follow-advanced {
    position: relative;
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
    overflow: hidden;
}

.btn-follow-advanced:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.btn-follow-advanced.followed {
    background: #f3f4f6;
    color: #64748b;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.btn-follow-advanced.followed:hover {
    background: #e5e7eb;
}

.btn-follow-advanced:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-icon {
    display: flex;
    align-items: center;
}

.btn-particles {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100%;
    height: 100%;
    transform: translate(-50%, -50%);
    background: radial-gradient(circle, rgba(255,255,255,0.3) 0%, transparent 70%);
    animation: particleBurst 0.6s ease-out;
}

@keyframes particleBurst {
    0% {
        opacity: 1;
        transform: translate(-50%, -50%) scale(0);
    }
    100% {
        opacity: 0;
        transform: translate(-50%, -50%) scale(2);
    }
}

.loading-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80px 20px;
    color: #6b7280;
}

.spinner {
    width: 48px;
    height: 48px;
    border: 4px solid #e5e7eb;
    border-top-color: #667eea;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    margin-bottom: 16px;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.error-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80px 20px;
    color: #6b7280;
}

.error-icon {
    font-size: 3rem;
    margin-bottom: 16px;
}

.btn-retry {
    margin-top: 16px;
    padding: 10px 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s ease;
}

.btn-retry:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.profile-card {
    position: relative;
    background: #fff;
    border-radius: 20px;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
    padding: 0;
    margin: 24px auto;
    max-width: 1100px;
    overflow: hidden;
}

.profile-cover {
    height: 160px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    position: relative;
}

.profile-cover::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 60px;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.1), transparent);
}

.profile-header {
    display: flex;
    align-items: flex-start;
    gap: 32px;
    padding: 0 40px 32px;
    position: relative;
    flex-wrap: wrap;
}

.avatar-wrapper {
    position: relative;
    margin-top: -50px;
    flex-shrink: 0;
}

.avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    object-fit: cover;
    border: 5px solid #fff;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    position: relative;
    z-index: 2;
}

.avatar-ring {
    position: absolute;
    top: -8px;
    left: -8px;
    right: -8px;
    bottom: -8px;
    border-radius: 50%;
    border: 2px solid transparent;
    background: linear-gradient(135deg, #667eea, #764ba2) border-box;
    -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: destination-out;
    mask-composite: exclude;
    animation: rotate 3s linear infinite;
}

.btn-edit-avatar {
    position: absolute;
    bottom: 4px;
    right: 4px;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: 3px solid #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    z-index: 3;
}

.btn-edit-avatar:hover {
    transform: scale(1.15) rotate(8deg);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.btn-edit-avatar:active {
    transform: scale(1.05);
}

@keyframes rotate {
    to {
        transform: rotate(360deg);
    }
}

.user-info {
    flex: 1;
    padding-top: 16px;
}

.nickname {
    font-size: 1.75rem;
    font-weight: 800;
    color: #1f2937;
    margin: 0 0 8px 0;
    letter-spacing: -0.5px;
}

.bio {
    font-size: 0.95rem;
    color: #6b7280;
    margin: 0 0 12px 0;
    line-height: 1.5;
}

.user-meta {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 0.85rem;
    color: #9ca3af;
    padding: 4px 12px;
    border-radius: 16px;
    background: rgba(102, 126, 234, 0.08);
    transition: all 0.3s ease;
}

.meta-item:hover {
    background: rgba(102, 126, 234, 0.12);
    transform: translateY(-1px);
}

.meta-item.mood-tag {
    background: linear-gradient(135deg, rgba(252, 211, 77, 0.15), rgba(245, 158, 11, 0.15));
    color: #f59e0b;
    border: 1px solid rgba(245, 158, 11, 0.3);
}

.meta-item.mental-tag {
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(37, 99, 235, 0.15));
    color: #3b82f6;
    border: 1px solid rgba(59, 130, 246, 0.3);
}

.stats-container {
    display: flex;
    gap: 24px;
    padding-top: 16px;
    flex-shrink: 0;
}

.stat-item {
    text-align: center;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
}

.stat-item:hover {
    opacity: 0.8;
}

.stat-item:active {
    transform: scale(0.95);
}

.stat-value {
    display: block;
    font-size: 1.25rem;
    font-weight: 700;
    color: #1f2937;
}

.stat-label {
    display: block;
    font-size: 0.75rem;
    color: #9ca3af;
    margin-top: 2px;
    font-weight: 500;
}

/* 导航栏 Tab */
.nav-tabs {
    display: flex;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
    margin: 0 auto 24px;
    max-width: 1100px;
    overflow: hidden;
    position: sticky;
    top: 64px;
    z-index: 90;
    border: 1px solid #e5e7eb;
}

.tab-item {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    text-align: center;
    padding: 18px 0;
    text-decoration: none;
    color: #6b7280;
    font-weight: 600;
    font-size: 0.95rem;
    border-bottom: 3px solid transparent;
    cursor: pointer;
    transition: all 0.2s ease;
}

.tab-count {
    font-size: 0.75rem;
    color: #9ca3af;
    font-weight: 500;
}

.tab-item:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.04);
}

.tab-item svg {
    transition: transform 0.2s ease;
}

.tab-item:hover svg {
    transform: scale(1.1);
}

.tab-item.active-tab {
    color: #667eea;
    border-bottom-color: #667eea;
    background: linear-gradient(180deg, rgba(102, 126, 234, 0.08) 0%, transparent 100%);
}

.tab-item.active-tab svg {
    transform: scale(1.1);
}

/* 内容区域 */
.content-area {
    background: #fff;
    border-radius: 20px;
    min-height: 500px;
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
    padding: 24px;
    max-width: 1100px;
    margin: 0 auto 40px;
}

.content-blurred {
    position: relative;
    pointer-events: none;
    user-select: none;
}

.content-blurred::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(2px);
    border-radius: 20px;
    z-index: 1;
}

/* 未登录提示遮罩 */
.login-prompt-overlay {
    position: relative;
    margin-top: -480px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    z-index: 10;
}

.login-prompt-card {
    background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
    border-radius: 24px;
    padding: 48px 40px;
    text-align: center;
    max-width: 420px;
    width: 100%;
    box-shadow: 
        0 20px 60px rgba(102, 126, 234, 0.2),
        0 8px 20px rgba(118, 75, 162, 0.1),
        0 0 0 1px rgba(102, 126, 234, 0.1);
    animation: promptSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes promptSlideIn {
    from {
        opacity: 0;
        transform: translateY(20px) scale(0.95);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.prompt-icon {
    width: 96px;
    height: 96px;
    margin: 0 auto 24px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #667eea;
}

.prompt-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 12px 0;
    letter-spacing: -0.3px;
}

.prompt-desc {
    font-size: 0.95rem;
    color: #6b7280;
    margin: 0 0 32px 0;
    line-height: 1.6;
}

.prompt-login-btn {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    padding: 14px 32px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 14px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 
        0 8px 24px rgba(102, 126, 234, 0.35),
        0 4px 12px rgba(118, 75, 162, 0.25);
    letter-spacing: 0.3px;
}

.prompt-login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 
        0 12px 32px rgba(102, 126, 234, 0.4),
        0 6px 16px rgba(118, 75, 162, 0.3);
}

.prompt-login-btn:active {
    transform: translateY(0) scale(0.98);
}

.prompt-login-btn svg {
    transition: transform 0.3s ease;
}

.prompt-login-btn:hover svg {
    transform: translateX(4px);
}

/* 高级关注按钮样式 */
.btn-follow-advanced {
    position: relative;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 10px 20px;
    width: 120px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 25px;
    cursor: pointer;
    font-weight: 600;
    font-size: 14px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    overflow: hidden;
    outline: none;
    transform-origin: center center;
    flex-shrink: 0;
    contain: layout style paint;
}

.btn-follow-advanced::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
}

.btn-follow-advanced:hover:not(:disabled)::before {
    opacity: 1;
}

.btn-follow-advanced:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.btn-follow-advanced:active:not(:disabled) {
    transform: translateY(0);
    box-shadow: 0 2px 10px rgba(102, 126, 234, 0.4);
}

.btn-follow-advanced .btn-icon {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.3s ease;
}

.btn-follow-advanced:hover:not(:disabled) .btn-icon {
    transform: scale(1.1);
}

.btn-follow-advanced .btn-text {
    position: relative;
    z-index: 1;
    letter-spacing: 0.5px;
}

/* 已关注状态 */
.btn-follow-advanced.followed {
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
    color: #6b7280;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.btn-follow-advanced.followed::before {
    background: linear-gradient(135deg, #e4e8eb 0%, #f5f7fa 100%);
}

.btn-follow-advanced.followed:hover:not(:disabled) {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.btn-follow-advanced.followed .btn-icon svg {
    color: #10b981;
}

/* 加载动画 */
.btn-follow-advanced.animating {
    pointer-events: none;
}

.btn-follow-advanced.animating .btn-icon {
    animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
    0%, 100% {
        transform: scale(1);
    }
    50% {
        transform: scale(1.2);
    }
}

/* 关注成功动画 */
.btn-follow-advanced.follow-animation {
    animation: followSuccess 0.6s ease-out;
    will-change: transform;
}

@keyframes followSuccess {
    0% {
        transform: scale(1);
    }
    20% {
        transform: scale(1.15);
    }
    40% {
        transform: scale(0.95);
    }
    60% {
        transform: scale(1.05);
    }
    80% {
        transform: scale(0.98);
    }
    100% {
        transform: scale(1);
    }
}

/* 粒子效果容器 */
.btn-follow-advanced .btn-particles {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 100%;
    height: 100%;
    transform: translate(-50%, -50%);
    pointer-events: none;
}

.btn-follow-advanced .btn-particles::before,
.btn-follow-advanced .btn-particles::after {
    content: '';
    position: absolute;
    width: 6px;
    height: 6px;
    background: #10b981;
    border-radius: 50%;
    animation: particleBurst 0.6s ease-out forwards;
}

.btn-follow-advanced .btn-particles::before {
    top: 20%;
    left: 30%;
}

.btn-follow-advanced .btn-particles::after {
    top: 30%;
    right: 25%;
    animation-delay: 0.1s;
}

@keyframes particleBurst {
    0% {
        transform: scale(0);
        opacity: 1;
    }
    100% {
        transform: scale(3);
        opacity: 0;
    }
}

/* 禁用状态 */
.btn-follow-advanced:disabled {
    cursor: not-allowed;
    opacity: 0.7;
}

/* 添加光泽效果 */
.btn-follow-advanced::after {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(
        45deg,
        transparent 30%,
        rgba(255, 255, 255, 0.1) 50%,
        transparent 70%
    );
    transform: rotate(45deg);
    transition: all 0.5s ease;
}

.btn-follow-advanced:hover:not(:disabled)::after {
    left: 100%;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
    .personal-home-container {
        padding: 0;
    }

    .navbar-content {
        padding: 0 16px;
    }

    .profile-card {
        /* margin: 24px; */
        border-radius: 0;
    }

    .profile-cover {
        height: 120px;
    }

    .profile-header {
        flex-direction: column;
        align-items: center;
        text-align: center;
        gap: 16px;
        padding: 0 20px 24px;
    }

    .avatar-wrapper {
        margin-top: -40px;
    }

    .avatar {
        width: 80px;
        height: 80px;
    }

    .user-info {
        padding-top: 8px;
    }

    .nickname {
        font-size: 1.5rem;
    }

    .bio {
        font-size: 0.875rem;
    }

    .user-meta {
        justify-content: center;
    }

    .stats-container {
        width: 100%;
        justify-content: space-around;
        gap: 8px;
        margin-top: 8px;
    }

    .stat-item {
        padding: 8px 4px;
        min-width: 60px;
    }

    .stat-value {
        font-size: 1.125rem;
    }

    .stat-label {
        font-size: 0.7rem;
    }

    .nav-tabs {
        border-radius: 0;
        margin: 0;
        position: sticky;
        top: 60px;
    }

    .tab-item {
        padding: 14px 8px;
        font-size: 0.85rem;
        gap: 4px;
    }

    .tab-count {
        font-size: 0.7rem;
    }

    .content-area {
        padding: 16px;
        border-radius: 0;
        margin: 0;
    }

    .btn-follow-advanced {
        padding: 8px 16px;
        font-size: 13px;
    }
}

@media screen and (max-width: 480px) {
    .brand-name {
        font-size: 1.1rem;
    }

    .profile-cover {
        height: 100px;
    }

    .avatar {
        width: 70px;
        height: 70px;
    }

    .nickname {
        font-size: 1.25rem;
    }

    .bio {
        font-size: 0.8rem;
    }

    .stats-container {
        gap: 4px;
    }

    .stat-value {
        font-size: 1rem;
    }

    .stat-label {
        font-size: 0.65rem;
    }

    .tab-item {
        padding: 12px 4px;
        font-size: 0.75rem;
        gap: 2px;
    }

    .tab-item svg {
        width: 14px;
        height: 14px;
    }

    .tab-count {
        display: none;
    }
}

/* 投稿图标样式 */
.submit-icon-wrapper {
    cursor: pointer;
    color: #6b7280;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    padding: 8px;
    border-radius: 50%;
}

.submit-icon-wrapper:hover {
    color: #4f46e5;
    background-color: #f3f4f6;
}

.submit-svg {
    display: block;
}

/* 他人头像样式 */
.other-user-avatar {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 20px;
    transition: background-color 0.2s;
    flex-shrink: 0;
    min-width: 110px;
}

.other-user-avatar:hover {
    background-color: #f3f4f6;
}

.other-user-avatar .avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.other-user-avatar .user-nickname {
    font-size: 0.9rem;
    color: #374151;
    font-weight: 500;
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* actions 区域样式 */
.actions {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
}
</style>