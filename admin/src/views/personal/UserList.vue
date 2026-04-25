<template>
    <div class="user-list-container">
        <div class="user-list-header">
            <h2 class="list-title">{{ title }}</h2>
            <span class="list-count">共 {{ total }} 人</span>
        </div>

        <div class="user-list" v-if="userList.length > 0">
            <div v-for="user in userList" :key="user.id" class="user-card">
                <div class="user-info" @click="goToUserProfile(user.id)">
                    <div class="avatar-container">
                        <img :src="user.avatar || defaultAvatar" alt="头像" class="user-avatar" />
                        <div class="avatar-status" v-if="user.isOnline"></div>
                    </div>
                    <div class="user-details">
                        <h3 class="user-nickname">
                            {{ user.nickname }}
                            <span v-if="String(user.id) === String(currentUserId)" class="self-badge">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                我
                            </span>
                        </h3>
                        <p class="user-bio">{{ user.signature || '这个人很懒，什么都没写~' }}</p>
                        <div class="user-stats">
                            <span class="stat-item">
                                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="9" cy="7" r="4"></circle>
                                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                </svg>
                                {{ user.fansCount || 0 }} 粉丝
                            </span>
                            <span class="stat-item" v-if="user.postCount">
                                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                    <polyline points="14 2 14 8 20 8"></polyline>
                                </svg>
                                {{ user.postCount }} 帖子
                            </span>
                        </div>
                    </div>
                </div>
                
                <!-- 关注按钮区域 -->
                <div v-if="String(user.id) !== String(currentUserId)" class="follow-action">
                    <!-- 关注列表：显示已关注状态，可取消关注 -->
                    <button v-if="listType === 'following'" 
                            class="btn-follow-advanced" 
                            :class="{ 
                                'followed': user.isFollowed,
                                'animating': user.followLoading,
                                'follow-animation': user.showFollowAnimation 
                            }" 
                            @click.stop="handleFollow(user)" 
                            :disabled="user.followLoading">
                        <span class="btn-icon">
                            <svg v-if="!user.isFollowed" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
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
                        <span class="btn-text">{{ user.followLoading ? '处理中...' : (user.isFollowed ? '已关注' : '+ 关注') }}</span>
                        <span class="btn-particles" v-if="user.showFollowAnimation"></span>
                    </button>
                    
                    <!-- 粉丝列表：显示回关或互相关注 -->
                    <button v-else-if="listType === 'followers'" 
                            class="btn-follow-advanced" 
                            :class="{ 
                                'followed': user.isFollowed || user.isMutualFollow,
                                'mutual-follow': user.isMutualFollow,
                                'animating': user.followLoading,
                                'follow-animation': user.showFollowAnimation 
                            }" 
                            @click.stop="handleFollow(user)" 
                            :disabled="user.followLoading">
                        <span class="btn-icon">
                            <svg v-if="user.isMutualFollow" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                <circle cx="8.5" cy="7" r="4"></circle>
                                <path d="M20 8v6"></path>
                                <path d="M23 11h-6"></path>
                                <path d="M17 11l2 2 4-4"></path>
                            </svg>
                            <svg v-else-if="user.isFollowed" width="18" height="18" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                <circle cx="8.5" cy="7" r="4"></circle>
                                <polyline points="17 11 19 13 23 9"></polyline>
                            </svg>
                            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                <circle cx="8.5" cy="7" r="4"></circle>
                                <line x1="20" y1="8" x2="20" y2="14"></line>
                                <line x1="23" y1="11" x2="17" y2="11"></line>
                            </svg>
                        </span>
                        <span class="btn-text">{{ user.followLoading ? '处理中...' : (user.isMutualFollow ? '互相关注' : (user.isFollowed ? '已关注' : '回关')) }}</span>
                        <span class="btn-particles" v-if="user.showFollowAnimation"></span>
                    </button>
                </div>
                <div v-else class="self-tag">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                    </svg>
                </div>
            </div>
        </div>

        <div v-else-if="!loading" class="empty-state">
            <p>{{ emptyText }}</p>
        </div>

        <div v-if="loading" class="loading-indicator">
            <div class="spinner"></div>
            <span>加载中...</span>
        </div>

        <div v-if="hasMore && !loading" class="load-more-container">
            <button @click="loadMore" class="load-more-btn">加载更多</button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, onUnmounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { getFollowingListApi, getFollowerListApi, followUserApi } from '../../api/user.js'

const props = defineProps({
    listType: {
        type: String,
        required: true,
        validator: (value) => ['following', 'followers'].includes(value)
    }
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const openLoginModal = inject('openLoginModal', () => {})

const checkAuth = () => {
    if (!userStore.isLoggedIn) {
        openLoginModal()
        return false
    }
    return true
}

const userList = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 20
const total = ref(0)
const hasMore = ref(true)
const defaultAvatar = ''

const currentUserId = computed(() => userStore.userInfo?.id)

const isOtherUser = computed(() => route.path.startsWith('/user/'))

const targetUserId = computed(() => {
    if (isOtherUser.value) {
        return route.params.id
    }
    return userStore.userInfo?.id
})

const isSelf = computed(() => {
    return String(targetUserId.value) === String(userStore.userInfo?.id)
})

const title = computed(() => {
    const isFollowing = props.listType === 'following'
    return isFollowing ? (isOtherUser.value ? 'TA的关注' : '我的关注') : (isOtherUser.value ? 'TA的粉丝' : '我的粉丝')
})

const emptyText = computed(() => {
    const isFollowing = props.listType === 'following'
    return isFollowing ? '暂无关注' : '暂无粉丝'
})

const fetchUsers = async () => {
    userList.value = []
    page.value = 1
    hasMore.value = true
    loading.value = true
    try {
        const params = {
            page: page.value,
            pageSize: pageSize
        }

        let res
        if (props.listType === 'following') {
            res = await getFollowingListApi(targetUserId.value, params)
        } else {
            res = await getFollowerListApi(targetUserId.value, params)
        }

        if (res.code === 0) {
            const data = res.data
            if (data.list && data.list.length > 0) {
                const processedList = data.list.map(user => ({
                    ...user,
                    followLoading: false,
                    showFollowAnimation: false,
                    isFollowed: user.isFollowed || false,
                    isMutualFollow: user.isMutualFollow || false,
                    fansCount: user.fansCount || 0,
                    followCount: user.followCount || 0
                }))
                userList.value.push(...processedList)
                total.value = data.total
                hasMore.value = data.hasMore
                page.value++
            } else {
                hasMore.value = false
            }
        }
    } catch (error) {
        console.error('获取用户列表失败:', error)
    } finally {
        loading.value = false
    }
}

watch(() => route.path, () => {
    fetchUsers()
    updatePageTitle()
})

const loadMore = () => {
    if (!loading.value && hasMore.value) {
        fetchUsers()
    }
}

const goToUserProfile = (userId) => {
    if (String(userId) === String(userStore.userInfo?.id)) {
        window.open('/personalHome', '_blank');
    } else {
        window.open(`/user/${userId}`, '_blank');
    }
}

const handleFollow = async (user) => {
    if (!checkAuth()) return

    if (String(user.id) === String(userStore.userInfo?.id)) {
        alert('不能关注自己')
        return
    }

    const originalIsFollowed = user.isFollowed
    const originalIsMutualFollow = user.isMutualFollow
    const originalUserFansCount = user.fansCount || 0
    const originalCurrentUserFollowCount = userStore.userInfo?.followCount || 0
    const originalCurrentUserFansCount = userStore.userInfo?.fansCount || 0
    
    user.followLoading = true
    
    try {
        const res = await followUserApi(user.id)
        if (res.code === 0) {
            if (props.listType === 'following') {
                const isNowFollowed = !user.isFollowed
                user.isFollowed = isNowFollowed
                
                if (isNowFollowed) {
                    user.isMutualFollow = false
                    user.showFollowAnimation = true
                    user.fansCount = originalUserFansCount + 1
                    setTimeout(() => {
                        user.showFollowAnimation = false
                    }, 600)
                } else {
                    user.isMutualFollow = false
                    user.fansCount = originalUserFansCount - 1
                }
                
                if (userStore.userInfo && isSelf.value) {
                    userStore.updateUserInfo({
                        followCount: originalCurrentUserFollowCount + (isNowFollowed ? 1 : -1)
                    })
                }
            } else {
                const wasFollowed = user.isFollowed
                user.isFollowed = !user.isFollowed
                
                if (user.isFollowed) {
                    user.isMutualFollow = true
                    user.showFollowAnimation = true
                    user.fansCount = originalUserFansCount + 1
                    setTimeout(() => {
                        user.showFollowAnimation = false
                    }, 600)
                } else {
                    user.isMutualFollow = false
                    user.fansCount = originalUserFansCount - 1
                }
                
                if (userStore.userInfo && isSelf.value) {
                    if (wasFollowed) {
                        userStore.updateUserInfo({
                            followCount: originalCurrentUserFollowCount - 1
                        })
                    } else {
                        userStore.updateUserInfo({
                            followCount: originalCurrentUserFollowCount + 1
                        })
                    }
                }
            }
        } else {
            throw new Error(res.message || '操作失败')
        }
    } catch (error) {
        console.error('关注操作失败:', error)
        user.isFollowed = originalIsFollowed
        user.isMutualFollow = originalIsMutualFollow
        user.fansCount = originalUserFansCount
        if (userStore.userInfo && isSelf.value) {
            userStore.updateUserInfo({
                followCount: originalCurrentUserFollowCount,
                fansCount: originalCurrentUserFansCount
            })
        }
        alert(error.message || '操作失败，请重试')
    } finally {
        user.followLoading = false
    }
}

onMounted(() => {
    fetchUsers()
    updatePageTitle()
})

onUnmounted(() => {
    document.title = 'PetCommunity'
})

const updatePageTitle = () => {
    const isFollowing = props.listType === 'following'
    const listName = isFollowing ? '关注' : '粉丝'
    if (isOtherUser.value) {
        document.title = `TA的${listName} - PetCommunity`
    } else {
        document.title = `我的${listName} - PetCommunity`
    }
}
</script>

<style scoped>
.user-list-container {
    padding: 20px;
    max-width: 800px;
    margin: 0 auto;
    width: 100%;
    box-sizing: border-box;
}

.user-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #e5e7eb;
}

.list-title {
    font-size: 1.25rem;
    font-weight: 700;
    color: #1f2937;
    margin: 0;
}

.list-count {
    font-size: 0.875rem;
    color: #6b7280;
}

.user-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.user-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border-radius: 16px;
    border: 1px solid #e5e7eb;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
}

.user-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
}

.user-card:hover {
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
    border-color: #667eea;
    transform: translateY(-2px);
}

.user-card:hover::before {
    opacity: 1;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 16px;
    flex: 1;
    cursor: pointer;
}

.avatar-container {
    position: relative;
    flex-shrink: 0;
}

.user-avatar {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    object-fit: cover;
    border: 3px solid #fff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
}

.user-card:hover .user-avatar {
    transform: scale(1.05);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.25);
}

.avatar-status {
    position: absolute;
    bottom: 2px;
    right: 2px;
    width: 12px;
    height: 12px;
    background: #10b981;
    border: 2px solid #fff;
    border-radius: 50%;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-details {
    flex: 1;
    min-width: 0;
}

.user-nickname {
    font-size: 1rem;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 4px 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: flex;
    align-items: center;
    gap: 6px;
}

.self-badge {
    display: inline-flex;
    align-items: center;
    gap: 3px;
    padding: 2px 8px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 12px;
    font-size: 0.7rem;
    font-weight: 600;
    white-space: nowrap;
    flex-shrink: 0;
}

.self-tag {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 50%;
    flex-shrink: 0;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.self-tag svg {
    width: 18px;
    height: 18px;
}

.user-bio {
    font-size: 0.875rem;
    color: #6b7280;
    margin: 0 0 8px 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.user-stats {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 0.8rem;
    color: #6b7280;
    padding: 4px 10px;
    background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
    border-radius: 12px;
    font-weight: 500;
    transition: all 0.2s ease;
}

.stat-item:hover {
    background: linear-gradient(135deg, #e5e7eb 0%, #d1d5db 100%);
    transform: translateY(-1px);
}

.stat-item svg {
    color: #667eea;
}

.follow-action {
    flex-shrink: 0;
}

/* 高级关注按钮样式 */
.btn-follow-advanced {
    position: relative;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
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

/* 互相关注状态 - 与已关注样式一致，仅文字不同 */
.btn-follow-advanced.mutual-follow {
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
    color: #6b7280;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.btn-follow-advanced.mutual-follow::before {
    background: linear-gradient(135deg, #e4e8eb 0%, #f5f7fa 100%);
}

.btn-follow-advanced.mutual-follow:hover:not(:disabled) {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.btn-follow-advanced.mutual-follow .btn-icon svg {
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

.empty-state {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 300px;
    color: #9ca3af;
    font-size: 14px;
    background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
    border-radius: 16px;
    border: 2px dashed #e5e7eb;
}

.empty-state::before {
    content: '🐾';
    font-size: 3rem;
    margin-bottom: 16px;
    opacity: 0.5;
}

.loading-indicator {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    padding: 20px;
    color: #666;
    font-size: 13px;
}

.spinner {
    width: 16px;
    height: 16px;
    border: 2px solid #ddd;
    border-top-color: #667eea;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.load-more-container {
    display: flex;
    justify-content: center;
    padding: 20px;
}

.load-more-btn {
    background-color: #fff;
    color: #333;
    border: 1px solid #ddd;
    padding: 8px 24px;
    border-radius: 20px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.load-more-btn:hover {
    background-color: #f0f0f0;
    border-color: #ccc;
    color: #000;
}

@media (max-width: 768px) {
    .user-list-container {
        padding: 16px;
    }

    .user-list-header {
        margin-bottom: 16px;
        padding-bottom: 12px;
    }

    .list-title {
        font-size: 1.125rem;
    }

    .list-count {
        font-size: 0.8rem;
    }

    .user-card {
        padding: 16px;
    }

    .user-info {
        gap: 12px;
    }

    .avatar-container {
        flex-shrink: 0;
    }

    .user-avatar {
        width: 48px;
        height: 48px;
    }

    .avatar-status {
        width: 10px;
        height: 10px;
    }

    .user-nickname {
        font-size: 0.9rem;
    }

    .self-badge {
        padding: 2px 6px;
        font-size: 0.65rem;
    }

    .self-badge svg {
        width: 12px;
        height: 12px;
    }

    .user-bio {
        font-size: 0.75rem;
    }

    .user-stats {
        gap: 8px;
    }

    .stat-item {
        font-size: 0.7rem;
        padding: 3px 8px;
    }

    .stat-item svg {
        width: 10px;
        height: 10px;
    }

    .self-tag {
        width: 32px;
        height: 32px;
    }

    .self-tag svg {
        width: 16px;
        height: 16px;
    }

    .btn-follow-advanced {
        padding: 8px 16px;
        font-size: 13px;
        gap: 6px;
    }

    .btn-icon svg {
        width: 16px;
        height: 16px;
    }

    .btn-text {
        font-size: 0.8rem;
    }

    .empty-state {
        height: 250px;
    }

    .empty-state::before {
        font-size: 2.5rem;
    }
}

@media (max-width: 480px) {
    .user-list-container {
        padding: 12px;
    }

    .user-list-header {
        margin-bottom: 12px;
        padding-bottom: 10px;
    }

    .list-title {
        font-size: 1rem;
    }

    .list-count {
        font-size: 0.75rem;
    }

    .user-card {
        padding: 12px;
    }

    .user-info {
        gap: 10px;
        flex: 1;
        min-width: 0;
    }

    .user-avatar {
        width: 40px;
        height: 40px;
    }

    .avatar-status {
        width: 8px;
        height: 8px;
    }

    .user-nickname {
        font-size: 0.85rem;
    }

    .self-badge {
        padding: 1px 5px;
        font-size: 0.6rem;
    }

    .self-badge svg {
        width: 10px;
        height: 10px;
    }

    .user-bio {
        font-size: 0.7rem;
        margin-bottom: 6px;
    }

    .user-stats {
        gap: 6px;
    }

    .stat-item {
        font-size: 0.65rem;
        padding: 2px 6px;
    }

    .stat-item svg {
        width: 9px;
        height: 9px;
    }

    .self-tag {
        width: 28px;
        height: 28px;
    }

    .self-tag svg {
        width: 14px;
        height: 14px;
    }

    .btn-follow-advanced {
        padding: 6px 12px;
        font-size: 12px;
        gap: 4px;
    }

    .btn-icon svg {
        width: 14px;
        height: 14px;
    }

    .btn-text {
        font-size: 0.75rem;
    }

    .load-more-btn {
        padding: 6px 20px;
        font-size: 13px;
    }

    .empty-state {
        height: 200px;
    }

    .empty-state::before {
        font-size: 2rem;
    }
}
</style>
