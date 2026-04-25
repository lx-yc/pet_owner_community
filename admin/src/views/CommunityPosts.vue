<template>
    <div class="community-page">
        <div class="page-header">
            <h1 class="page-title">宠友社区</h1>
        </div>

        <!-- 筛选标签栏 (独立出来以便吸顶) -->
        <div class="sticky-filter-bar">
            <div class="filter-tabs-wrapper">
                <div class="filter-controls">
                    <!-- 左侧：排序标签 -->
                    <div class="filter-tabs sort-tabs">
                        <span :class="{ active: sortType === 'hot' }" @click="changeSort('hot')">热门</span>
                        <span :class="{ active: sortType === 'latest' }" @click="changeSort('latest')">最新</span>
                    </div>

                    <!-- 中间：帖子类型标签 -->
                    <div class="filter-tabs type-tabs">
                        <span :class="{ active: postType === 'all' }" @click="changeType('all')">全部</span>
                        <span :class="{ active: postType === 'normal' }" @click="changeType('normal')">日常</span>
                        <span :class="{ active: postType === '宠物分享' }" @click="changeType('宠物分享')">宠物分享</span>
                        <span :class="{ active: postType === '心理倾诉' }" @click="changeType('心理倾诉')">心理倾诉</span>
                    </div>

                    <!-- 右侧：布局切换按钮 -->
                    <div class="layout-switcher" @click="toggleLayout" title="切换布局密度">
                        <svg v-if="columnCount === 1" class="layout-icon" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2">
                            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="9" y1="3" x2="9" y2="21"></line>
                        </svg>
                        <svg v-else-if="columnCount === 2" class="layout-icon" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2">
                            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="12" y1="3" x2="12" y2="21"></line>
                        </svg>
                        <svg v-else class="layout-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2">
                            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="9" y1="3" x2="9" y2="21"></line>
                            <line x1="15" y1="3" x2="15" y2="21"></line>
                        </svg>
                    </div>
                </div>
            </div>
        </div>

        <!-- 文章列表容器 -->
        <div class="posts-container" :class="`grid-cols-${columnCount}`">
            <!-- 加载状态 -->
            <div v-if="loading && posts.length === 0" class="loading-state">
                <div class="spinner"></div>
                <p>正在加载精彩内容...</p>
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-state">
                <p>{{ error }}</p>
                <button @click="fetchPosts()" class="btn-retry">重试</button>
            </div>

            <!-- 文章卡片列表 -->
            <div v-else class="posts-grid">
                <article v-for="post in posts" :key="post.id" class="post-card" @click="handlePostClick(post)">
                    <!-- 封面图 (如果有) -->
                    <div class="card-image-wrapper" v-if="post.images && post.images.length > 0">
                        <img :src="JSON.parse(post.images)[0]" alt="封面" class="card-img" loading="lazy" />
                        <span class="type-tag" :class="post.type === 'OFFICIAL' ? 'official' : 'community'">
                            {{ post.type === 'OFFICIAL' ? '官方' : '分享' }}
                        </span>
                    </div>

                    <!-- 卡片内容区 -->
                    <div class="card-body">
                        <h3 class="card-title">{{ post.title || '暂无标题' }}</h3>
                        <p class="card-excerpt">{{ post.content }}</p>

                        <!-- 用户信息 -->
                        <div class="user-info" v-if="(post.userNickname || post.userAvatar) && !post.isAnonymous" @click.stop="goToUserProfile(post.userId)">
                            <img :src="post.userAvatar" alt="头像" class="avatar" />
                            <span class="nickname">{{ post.userNickname }}</span>
                        </div>
                        <div class="user-info" v-else-if="post.isAnonymous">
                            <img src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="匿名头像" class="avatar" />
                            <span class="nickname">匿名用户</span>
                        </div>

                        <!-- 标签区域 -->
                        <div class="tags-container">
                            <template v-if="post.tags">
                                <span v-for="(tag, index) in post.tags.split(',')" :key="index" class="tag-item">
                                    #{{ tag.trim() }}
                                </span>
                            </template>
                            <span v-else class="tag-item tag-empty">#无</span>
                        </div>

                        <!-- 底部数据栏 -->
                        <div class="card-footer">
                            <div class="meta-stats">
                                <!-- 浏览量 -->
                                <span class="stat-item">
                                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                    {{ formatNumber(post.viewCount) }}
                                </span>

                                <!-- 点赞量 (点击事件 + 动态样式) -->
                                <span class="stat-item like-item" :class="{ 'active': post.isLiked }"
                                    @click.stop="handleLike(post, $event)">
                                    <svg class="icon" viewBox="0 0 24 24" :fill="post.isLiked ? 'currentColor' : 'none'"
                                        stroke="currentColor" stroke-width="2">
                                        <path
                                            d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z">
                                        </path>
                                    </svg>
                                    {{ formatNumber(post.likeCount) }}
                                </span>

                                <!-- 评论量 -->
                                <span class="stat-item" @click.stop="goToPostWithCommentScroll(post)">
                                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path
                                            d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z">
                                        </path>
                                    </svg>
                                    {{ formatNumber(post.commentCount) }}
                                </span>

                                <!-- 收藏量 (点击事件 + 动态样式) -->
                                <span class="stat-item collect-item" :class="{ 'active': post.isCollected }"
                                    @click.stop="handleCollect(post, $event)">
                                    <svg class="icon" viewBox="0 0 24 24"
                                        :fill="post.isCollected ? 'currentColor' : 'none'" stroke="currentColor"
                                        stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path
                                            d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2z">
                                        </path>
                                    </svg>
                                    {{ formatNumber(post.favoriteCount) }}
                                </span>
                            </div>
                            <span class="time-text">{{ formatTime(post.createTime) }}</span>
                        </div>
                    </div>
                </article>
            </div>

            <!-- 加载更多 -->
            <div v-if="hasMore && !loading" class="load-more-container">
                <button @click="loadMorePosts()" class="btn-load-more" :disabled="loading">
                    {{ loading ? '加载中...' : '加载更多' }}
                </button>
            </div>

            <!-- 没有更多数据 -->
            <div v-if="!hasMore && posts.length > 0" class="no-more">
                - 已经到底啦 -
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onActivated, onBeforeUnmount } from 'vue'
import { getPostListApi, likePostApi, collectPostApi, viewPostApi } from '../api/post.js'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useAppStore } from '../stores/app'
import { ElMessage } from 'element-plus'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const loading = ref(false)
const error = ref(null)
const posts = ref([])
const hasMore = ref(true)
const cursorId = ref(null)
const cursorTime = ref(null)
const cursorSeasonPriority = ref(null)
const pageSize = ref(10)
const sortType = ref('latest')
const postType = ref('all')
const columnCount = ref(3)

// 初始化列数
const initColumnCount = () => {
    if (window.innerWidth <= 768) {
        columnCount.value = 1
    } else {
        columnCount.value = 2
    }
}

const changeSort = (type) => {
    if (sortType.value === type) return
    sortType.value = type
    cursorId.value = null
    cursorTime.value = null
    cursorSeasonPriority.value = null
    posts.value = []
    hasMore.value = true
    fetchPosts(false)
}

const changeType = (type) => {
    if (postType.value === type) return
    postType.value = type
    cursorId.value = null
    cursorTime.value = null
    cursorSeasonPriority.value = null
    posts.value = []
    hasMore.value = true
    fetchPosts(false)
}

const toggleLayout = () => {
    const isMobile = window.innerWidth <= 768
    if (isMobile) {
        columnCount.value = columnCount.value === 2 ? 1 : 2
    } else {
        columnCount.value = columnCount.value === 3 ? 2 : 3
    }
}

const formatNumber = (num) => {
    if (!num && num !== 0) return 0
    return num > 999 ? (num / 1000).toFixed(1) + 'k' : num
}

const formatTime = (timestamp) => {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    const now = new Date()
    const diff = now - date
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
    if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
    return `${date.getMonth() + 1}-${date.getDate()}`
}

const fetchPosts = async (isLoadMore = false) => {
    try {
        loading.value = true
        error.value = null

        const params = {
            sortType: sortType.value,
            pageSize: pageSize.value,
        }

        if (postType.value !== 'all') {
            params.postType = postType.value
        }

        if (isLoadMore && cursorId.value !== null && cursorTime.value !== null) {
            params.cursorId = cursorId.value
            params.cursorTime = cursorTime.value
            if (cursorSeasonPriority.value !== null) {
                params.cursorSeasonPriority = cursorSeasonPriority.value
            }
        }

        const data = await getPostListApi(params)

        if (data.code === 0) {
            const newList = data.data.list || []

            // 【关键】后端已返回 isLiked 和 isCollected，直接使用即可
            // 这里做一个防御性编程，防止后端个别字段缺失导致前端报错
            newList.forEach(post => {
                if (post.isLiked === undefined) post.isLiked = false
                if (post.isCollected === undefined) post.isCollected = false
                if (post.likeCount === undefined) post.likeCount = 0
                if (post.favoriteCount === undefined) post.favoriteCount = 0
            })

            if (isLoadMore) {
                posts.value.push(...newList)
            } else {
                posts.value = newList
            }

            if (data.data.nextCursorId !== null && data.data.nextCursorTime !== null) {
                cursorId.value = data.data.nextCursorId
                cursorTime.value = data.data.nextCursorTime
                if (data.data.nextCursorSeasonPriority !== undefined && data.data.nextCursorSeasonPriority !== null) {
                    cursorSeasonPriority.value = data.data.nextCursorSeasonPriority
                }
                hasMore.value = true
            } else {
                hasMore.value = false
            }
        } else {
            throw new Error(data.message || '获取失败')
        }
    } catch (err) {
        error.value = err.message
    } finally {
        loading.value = false
    }
}

// 处理点赞逻辑
const handleLike = async (post, event) => {
    event.stopPropagation()
    // 未登录提示并打开登录弹窗
    if (!userStore.isLoggedIn) {
        ElMessage.warning('还未登录，请先登录');
        // 打开登录弹窗
        appStore.openLoginModal();
        return;
    }

    // 1. 保存原始状态
    const originalIsLiked = post.isLiked
    const originalLikeCount = post.likeCount

    // 2. 乐观更新 UI
    post.isLiked = !post.isLiked
    post.likeCount = post.isLiked ? (originalLikeCount + 1) : (originalLikeCount - 1)

    try {
        // 3. 调用后端接口
        await likePostApi(post.id)
        // 成功则保持 UI 状态
    } catch (err) {
        // 4. 失败则回滚
        console.error('点赞操作失败:', err)
        post.isLiked = originalIsLiked
        post.likeCount = originalLikeCount
        // ElMessage.error('操作失败，请重试')
    }
}

// 【新增】跳转到帖子详情并定位评论区
const goToPostWithCommentScroll = (post) => {
    // 同样先上报浏览（可选，因为详情页也会上报，这里不上报也可以，避免重复）
    // viewPostApi(post.id).catch(err => console.warn(err))

    // 新标签页打开，并携带 scrollToComments 参数让详情页自动滚动到评论区
    window.open(`/post/${post.id}?scrollToComments=true`, '_blank')
}

// 处理收藏逻辑
const handleCollect = async (post, event) => {
    event.stopPropagation()

    // 未登录提示并打开登录弹窗
    if (!userStore.isLoggedIn) {
        ElMessage.warning('还未登录，请先登录');
        // 打开登录弹窗
        appStore.openLoginModal();
        return;
    }

    // 1. 保存原始状态
    const originalIsCollected = post.isCollected
    const originalFavoriteCount = post.favoriteCount

    // 2. 乐观更新 UI
    post.isCollected = !post.isCollected
    post.favoriteCount = post.isCollected ? (originalFavoriteCount + 1) : (originalFavoriteCount - 1)

    try {
        // 3. 调用后端接口
        await collectPostApi(post.id)
        // 成功则保持 UI 状态
    } catch (err) {
        // 4. 失败则回滚
        console.error('收藏操作失败:', err)
        post.isCollected = originalIsCollected
        post.favoriteCount = originalFavoriteCount
        // ElMessage.error('操作失败，请重试')
    }
}

const loadMorePosts = () => {
    fetchPosts(true)
}

// 处理帖子点击（增加浏览量）
const handlePostClick = async (post) => {
    // 异步上报浏览，不阻塞跳转
    viewPostApi(post.id).catch(err => {
        console.warn('浏览计数上报失败:', err)
    })

    // 打开新标签页
    window.open(`/post/${post.id}`, '_blank')
}

const goToUserProfile = (userId) => {
    const currentId = userStore.userInfo?.id;
    if (currentId && String(currentId) === String(userId)) {
        window.open('/personalHome', '_blank');
    } else {
        window.open(`/user/${userId}`, '_blank');
    }
}

const handleScroll = () => {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop
    const windowHeight = window.innerHeight
    const documentHeight = document.documentElement.scrollHeight

    if (scrollTop + windowHeight >= documentHeight - 200) {
        if (hasMore.value && !loading.value) {
            loadMorePosts()
        }
    }
}

onMounted(() => {
    initColumnCount()
    fetchPosts(false)
    window.addEventListener('scroll', handleScroll)
    window.addEventListener('visibilitychange', () => {
        if (!document.hidden) {
            fetchPosts(false)
        }
    })
})

onActivated(() => {
    initColumnCount()
    fetchPosts(false)
    window.addEventListener('scroll', handleScroll)
})

onBeforeUnmount(() => {
    window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.community-page {
    min-height: 100vh;
    background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 50%, #f8f9ff 100%);
    padding-bottom: 40px;
    position: relative;
}

.community-page::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
        radial-gradient(circle at 20% 50%, rgba(102, 126, 234, 0.03) 0%, transparent 50%),
        radial-gradient(circle at 80% 80%, rgba(118, 75, 162, 0.03) 0%, transparent 50%);
    pointer-events: none;
    z-index: 0;
}

.page-header {
    max-width: 1400px;
    margin: 0 auto;
    padding: 24px 20px 12px 20px;
    position: relative;
    z-index: 1;
}

.page-title {
    font-size: 2rem;
    font-weight: 800;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0;
    position: relative;
    display: inline-block;
}

.page-title::after {
    content: '🐾';
    position: absolute;
    right: -40px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 1.5rem;
    opacity: 0.6;
}

.sticky-filter-bar {
    position: sticky;
    top: 72px;
    z-index: 50;
    background: rgba(248, 249, 255, 0.95);
    backdrop-filter: blur(20px);
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.06);
    width: 100%;
    padding: 14px 0;
    transition: all 0.3s ease;
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);
}

.filter-tabs-wrapper {
    max-width: 1400px;
    margin: 0 auto;
    padding-left: 20px;
    padding-right: 20px;
}

.filter-controls {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
}

.sort-tabs {
    flex-shrink: 0;
}

.type-tabs {
    flex: 1;
    display: inline-flex;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
}

.type-tabs::-webkit-scrollbar {
    display: none;
}

.type-tabs span {
    white-space: nowrap;
}

.filter-tabs {
    display: inline-flex;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.06));
    padding: 5px;
    border-radius: 28px;
    box-shadow: 0 2px 12px rgba(102, 126, 234, 0.08);
    gap: 4px;
    border: 1px solid rgba(102, 126, 234, 0.12);
}

.filter-tabs span {
    cursor: pointer;
    font-size: 0.9rem;
    color: #6b7280;
    font-weight: 500;
    padding: 7px 18px;
    border-radius: 24px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    user-select: none;
    -webkit-tap-highlight-color: transparent;
    position: relative;
}

.filter-tabs span:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.12);
}

.filter-tabs span.active {
    color: #ffffff;
    background: linear-gradient(135deg, #667eea, #764ba2);
    font-weight: 600;
    box-shadow: 
        0 3px 10px rgba(102, 126, 234, 0.3),
        0 0 0 2px rgba(102, 126, 234, 0.2);
    transform: scale(1.05);
}

.layout-switcher {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.06));
    border-radius: 50%;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.12);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    color: #667eea;
    border: 1px solid rgba(102, 126, 234, 0.15);
}

.layout-switcher:hover {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    transform: rotate(90deg) scale(1.1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.layout-icon {
    width: 20px;
    height: 20px;
}

.posts-container {
    max-width: 1400px;
    margin: 0 auto;
    padding-left: 20px;
    padding-right: 20px;
    margin-top: 20px;
    position: relative;
    z-index: 1;
}

.posts-grid {
    display: grid;
    gap: 28px;
    grid-template-columns: repeat(3, 1fr);
    transition: all 0.3s ease;
}

.grid-cols-1 .posts-grid {
    grid-template-columns: repeat(1, 1fr);
}

.grid-cols-2 .posts-grid {
    grid-template-columns: repeat(2, 1fr);
}

.grid-cols-3 .posts-grid {
    grid-template-columns: repeat(3, 1fr);
}

.post-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 
        0 4px 16px rgba(0, 0, 0, 0.06),
        0 2px 6px rgba(0, 0, 0, 0.04);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    display: flex;
    flex-direction: column;
    border: 1px solid rgba(255, 255, 255, 0.6);
    height: 100%;
    position: relative;
}

.post-card::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.02), rgba(118, 75, 162, 0.02));
    opacity: 0;
    transition: opacity 0.4s;
    pointer-events: none;
}

.post-card:hover::before {
    opacity: 1;
}

.post-card:hover {
    transform: translateY(-8px);
    box-shadow: 
        0 16px 32px rgba(102, 126, 234, 0.15),
        0 6px 16px rgba(102, 126, 234, 0.1);
    border-color: rgba(102, 126, 234, 0.3);
}

.card-image-wrapper {
    position: relative;
    width: 100%;
    padding-top: 60%;
    background: linear-gradient(135deg, #f8f9ff, #f0f2ff);
    overflow: hidden;
}

.card-img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-card:hover .card-img {
    transform: scale(1.1);
}

.type-tag {
    position: absolute;
    top: 14px;
    left: 14px;
    padding: 5px 12px;
    border-radius: 14px;
    font-size: 0.75rem;
    font-weight: 600;
    backdrop-filter: blur(8px);
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    letter-spacing: 0.02em;
}

.type-tag.official {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.95), rgba(118, 75, 162, 0.95));
    color: white;
}

.type-tag.community {
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(248, 249, 255, 0.95));
    color: #667eea;
    border: 1px solid rgba(102, 126, 234, 0.3);
}

.post-card:hover .type-tag {
    transform: scale(1.05);
}

.card-body {
    padding: 22px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

.card-title {
    font-size: 1.2rem;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0 0 12px 0;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    transition: all 0.3s;
}

.post-card:hover .card-title {
    transform: translateX(2px);
}

.card-excerpt {
    font-size: 0.95rem;
    color: #6b7280;
    line-height: 1.7;
    margin: 0 0 18px 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    flex-grow: 1;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 10px;
    font-size: 0.85rem;
    color: #6b7280;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    padding: 8px 12px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
    border-radius: 16px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-card:hover .user-info {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
}

.avatar {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid white;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-card:hover .avatar {
    transform: scale(1.1) rotate(5deg);
}

.nickname {
    font-weight: 600;
    color: #374151;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: calc(100% - 36px);
}

.tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 8px;
    margin-bottom: 8px;
    font-size: 0.8rem;
    color: #667eea;
    height: 26px;
    overflow: hidden;
    white-space: nowrap;
    mask-image: linear-gradient(to right, black 80%, transparent 100%);
}

.tag-item {
    padding: 3px 8px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.06));
    border-radius: 14px;
    white-space: nowrap;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    flex-shrink: 0;
    border: 1px solid rgba(102, 126, 234, 0.15);
}

.tag-item:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.12), rgba(118, 75, 162, 0.12));
    color: #764ba2;
    transform: translateY(-2px);
    box-shadow: 0 3px 8px rgba(102, 126, 234, 0.15);
}

.tag-empty {
    color: #d1d5db;
    background: rgba(209, 213, 219, 0.2);
    border-color: rgba(209, 213, 219, 0.3);
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 18px;
    border-top: 2px solid transparent;
    border-image: linear-gradient(to right, transparent, rgba(102, 126, 234, 0.15), transparent) 1;
}

.meta-stats {
    display: flex;
    gap: 18px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 0.9rem;
    color: #9ca3af;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    padding: 6px 10px;
    border-radius: 12px;
    background: rgba(0, 0, 0, 0.02);
}

.stat-item:hover {
    color: #6b7280;
    background: rgba(0, 0, 0, 0.04);
    transform: translateY(-2px);
}

.stat-item svg {
    width: 16px;
    height: 16px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-item:hover svg {
    transform: scale(1.15);
}

.like-item.active {
    color: #ff6b6b;
    background: rgba(255, 107, 107, 0.08);
}

.like-item.active:hover {
    color: #dc2626;
    background: rgba(255, 107, 107, 0.12);
}

.like-item.active svg {
    animation: heartBeat 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes heartBeat {
    0%, 100% { transform: scale(1); }
    25% { transform: scale(1.3); }
    50% { transform: scale(0.95); }
    75% { transform: scale(1.15); }
}

.collect-item.active {
    color: #ffa726;
    background: rgba(255, 167, 38, 0.08);
}

.collect-item.active:hover {
    color: #d97706;
    background: rgba(255, 167, 38, 0.12);
}

.collect-item.active svg {
    animation: starTwinkle 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes starTwinkle {
    0%, 100% { transform: scale(1) rotate(0deg); }
    25% { transform: scale(1.3) rotate(15deg); }
    50% { transform: scale(0.9) rotate(-10deg); }
    75% { transform: scale(1.1) rotate(5deg); }
}

.icon {
    width: 16px;
    height: 16px;
}

.time-text {
    font-size: 0.8rem;
    color: #d1d5db;
    font-weight: 500;
}

.loading-state,
.error-state,
.no-more {
    text-align: center;
    padding: 48px 0;
    color: #9ca3af;
    font-size: 0.95rem;
}

.loading-state::before {
    content: '🐾';
    display: block;
    font-size: 52px;
    margin-bottom: 18px;
    animation: pawBounce 1.5s ease-in-out infinite;
}

@keyframes pawBounce {
    0%, 100% { transform: translateY(0) scale(1); }
    50% { transform: translateY(-12px) scale(1.1); }
}

.spinner {
    width: 36px;
    height: 36px;
    border: 3px solid rgba(102, 126, 234, 0.15);
    border-top-color: #667eea;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    margin: 0 auto 14px;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.error-state::before {
    content: '😿';
    display: block;
    font-size: 48px;
    margin-bottom: 16px;
}

.no-more::before {
    content: '✨';
    display: block;
    font-size: 32px;
    margin-bottom: 12px;
    opacity: 0.6;
}

.btn-retry,
.btn-load-more {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border: none;
    padding: 10px 28px;
    border-radius: 24px;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-retry:hover,
.btn-load-more:hover {
    background: linear-gradient(135deg, #5568d8, #6a3f92);
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.btn-retry:active,
.btn-load-more:active {
    transform: translateY(0);
}

.btn-load-more:disabled {
    background: linear-gradient(135deg, #d1d5db, #e5e7eb);
    cursor: not-allowed;
    box-shadow: none;
}

.load-more-container {
    text-align: center;
    margin-top: 36px;
}

/* ================= 移动端适配 ================= */
@media screen and (max-width: 768px) {
    .page-header {
        padding: 14px 12px 6px 12px;
    }

    .page-title {
        font-size: 1.5rem;
    }

    .page-title::after {
        right: -32px;
        font-size: 1.2rem;
    }

    .sticky-filter-bar {
        top: 60px;
        padding: 8px 0;
        background: rgba(248, 249, 255, 0.98);
        border-bottom: 1px solid rgba(102, 126, 234, 0.08);
    }

    .filter-tabs-wrapper {
        padding-left: 12px;
        padding-right: 12px;
    }

    .filter-controls {
        flex-wrap: nowrap;
        gap: 8px;
    }

    .sort-tabs {
        flex-shrink: 0;
    }

    .type-tabs {
        flex: 1;
        overflow-x: auto;
        scrollbar-width: none;
    }

    .filter-tabs {
        padding: 3px;
    }

    .filter-tabs.sort-tabs span {
        font-size: 0.8rem;
        padding: 5px 12px;
    }

    .filter-tabs.type-tabs span {
        font-size: 0.75rem;
        padding: 5px 10px;
    }

    .layout-switcher {
        width: 32px;
        height: 32px;
        flex-shrink: 0;
    }

    .layout-icon {
        width: 16px;
        height: 16px;
    }

    .posts-container {
        padding-left: 10px;
        padding-right: 10px;
        margin-top: 12px;
    }

    .posts-grid {
        gap: 10px;
    }

    .post-card {
        border-radius: 14px;
    }

    /* 手机端默认单列横向布局 */
    .grid-cols-1 .post-card {
        flex-direction: row;
        height: 160px;
        align-items: stretch;
    }

    .grid-cols-1 .card-image-wrapper {
        width: 140px;
        flex-shrink: 0;
        padding-top: 0;
        height: 100%;
    }

    .grid-cols-1 .card-img {
        position: relative;
        width: 100%;
        height: 100%;
        transform: none;
    }

    .grid-cols-1 .post-card:hover .card-img {
        transform: scale(1.03);
    }

    .grid-cols-1 .card-body {
        padding: 10px 12px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        overflow: hidden;
    }

    .grid-cols-1 .card-title {
        font-size: 0.95rem;
        margin-bottom: 4px;
        -webkit-line-clamp: 1;
        line-clamp: 1;
        font-weight: 700;
    }

    .grid-cols-1 .card-excerpt {
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        text-overflow: ellipsis;
        font-size: 0.75rem;
        color: #6b7280;
        margin-bottom: 6px;
        line-height: 1.4;
        flex-grow: 0;
    }

    .grid-cols-1 .user-info {
        display: flex;
        align-items: center;
        gap: 6px;
        margin-top: 0;
        margin-bottom: 4px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        padding: 5px 8px;
    }

    .grid-cols-1 .avatar {
        width: 18px;
        height: 18px;
        flex-shrink: 0;
    }

    .grid-cols-1 .nickname {
        font-size: 0.7rem;
        color: #9ca3af;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .grid-cols-1 .tags-container {
        display: flex;
        flex-wrap: nowrap;
        gap: 4px;
        margin-top: 0;
        margin-bottom: 6px;
        font-size: 0.65rem;
        color: #667eea;
        height: 18px;
        overflow: hidden;
        white-space: nowrap;
        mask-image: linear-gradient(to right, black 85%, transparent 100%);
        -webkit-mask-image: linear-gradient(to right, black 85%, transparent 100%);
    }

    .grid-cols-1 .tag-item {
        padding: 1px 5px;
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.06));
        border-radius: 10px;
        flex-shrink: 0;
    }

    .grid-cols-1 .card-footer {
        padding-top: 6px;
        border-top: 2px solid transparent;
        border-image: linear-gradient(to right, transparent, rgba(102, 126, 234, 0.1), transparent) 1;
        margin-top: auto;
    }

    .grid-cols-1 .meta-stats {
        gap: 10px;
    }

    .grid-cols-1 .stat-item {
        font-size: 0.7rem;
        padding: 3px 6px;
    }

    .grid-cols-1 .stat-item svg {
        width: 12px;
        height: 12px;
    }

    .grid-cols-1 .time-text {
        font-size: 0.65rem;
    }

    /* 双列模式（手机端可选） */
    .grid-cols-2 .posts-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 8px;
    }

    .grid-cols-2 .post-card {
        border-radius: 12px;
        flex-direction: column;
        height: auto;
    }

    .grid-cols-2 .card-image-wrapper {
        padding-top: 65%;
    }

    .grid-cols-2 .card-body {
        padding: 8px;
    }

    .grid-cols-2 .card-title {
        font-size: 0.85rem;
        margin-bottom: 4px;
        -webkit-line-clamp: 1;
        line-clamp: 1;
    }

    .grid-cols-2 .card-excerpt {
        font-size: 0.7rem;
        -webkit-line-clamp: 1;
        line-clamp: 1;
        margin-bottom: 6px;
        line-height: 1.3;
    }

    .grid-cols-2 .user-info {
        padding: 4px 6px;
        margin-top: 4px;
        margin-bottom: 4px;
    }

    .grid-cols-2 .avatar {
        width: 16px;
        height: 16px;
    }

    .grid-cols-2 .nickname {
        font-size: 0.65rem;
    }

    .grid-cols-2 .tags-container {
        display: none;
    }

    .grid-cols-2 .card-footer {
        padding-top: 6px;
    }

    .grid-cols-2 .meta-stats {
        gap: 6px;
    }

    .grid-cols-2 .stat-item {
        font-size: 0.65rem;
        padding: 2px 4px;
    }

    .grid-cols-2 .stat-item svg {
        width: 10px;
        height: 10px;
    }

    .grid-cols-2 .time-text {
        font-size: 0.6rem;
    }

    .loading-state,
    .error-state,
    .no-more {
        padding: 32px 0;
        font-size: 0.85rem;
    }

    .loading-state::before {
        font-size: 40px;
    }

    .error-state::before {
        font-size: 36px;
    }

    .btn-retry,
    .btn-load-more {
        padding: 8px 20px;
        font-size: 0.85rem;
    }
}

@media screen and (max-width: 480px) {
    .page-header {
        padding: 12px 10px 4px 10px;
    }

    .page-title {
        font-size: 1.3rem;
    }

    .page-title::after {
        right: -28px;
        font-size: 1rem;
    }

    .sticky-filter-bar {
        top: 60px;
        padding: 6px 0;
    }

    .filter-tabs-wrapper {
        padding-left: 10px;
        padding-right: 10px;
    }

    .filter-controls {
        gap: 6px;
    }

    .filter-tabs.sort-tabs span {
        font-size: 0.75rem;
        padding: 4px 10px;
    }

    .filter-tabs.type-tabs span {
        font-size: 0.7rem;
        padding: 4px 8px;
    }

    .layout-switcher {
        width: 28px;
        height: 28px;
    }

    .layout-icon {
        width: 14px;
        height: 14px;
    }

    .posts-container {
        padding-left: 8px;
        padding-right: 8px;
        margin-top: 10px;
    }

    .posts-grid {
        gap: 8px;
    }

    .grid-cols-1 .post-card {
        height: 145px;
        border-radius: 12px;
    }

    .grid-cols-1 .card-image-wrapper {
        width: 120px;
    }

    .grid-cols-1 .card-body {
        padding: 8px 10px;
    }

    .grid-cols-1 .card-title {
        font-size: 0.9rem;
        margin-bottom: 3px;
    }

    .grid-cols-1 .card-excerpt {
        font-size: 0.7rem;
        margin-bottom: 4px;
        line-height: 1.35;
    }

    .grid-cols-1 .user-info {
        padding: 4px 6px;
        margin-bottom: 3px;
        gap: 5px;
    }

    .grid-cols-1 .avatar {
        width: 16px;
        height: 16px;
    }

    .grid-cols-1 .nickname {
        font-size: 0.65rem;
    }

    .grid-cols-1 .tags-container {
        font-size: 0.6rem;
        height: 16px;
        margin-bottom: 4px;
    }

    .grid-cols-1 .tag-item {
        padding: 1px 4px;
    }

    .grid-cols-1 .card-footer {
        padding-top: 5px;
    }

    .grid-cols-1 .meta-stats {
        gap: 8px;
    }

    .grid-cols-1 .stat-item {
        font-size: 0.65rem;
        padding: 2px 4px;
    }

    .grid-cols-1 .stat-item svg {
        width: 11px;
        height: 11px;
    }

    .grid-cols-1 .time-text {
        font-size: 0.6rem;
    }

    .loading-state,
    .error-state,
    .no-more {
        padding: 28px 0;
        font-size: 0.8rem;
    }

    .loading-state::before {
        font-size: 36px;
    }

    .error-state::before {
        font-size: 32px;
    }

    .btn-retry,
    .btn-load-more {
        padding: 7px 18px;
        font-size: 0.8rem;
    }
}

@media screen and (max-width: 360px) {
    .page-title {
        font-size: 1.2rem;
    }

    .filter-tabs.sort-tabs span {
        font-size: 0.7rem;
        padding: 3px 8px;
    }

    .filter-tabs.type-tabs span {
        font-size: 0.65rem;
        padding: 3px 6px;
    }

    .grid-cols-1 .post-card {
        height: 135px;
    }

    .grid-cols-1 .card-image-wrapper {
        width: 110px;
    }

    .grid-cols-1 .card-body {
        padding: 6px 8px;
    }

    .grid-cols-1 .card-title {
        font-size: 0.85rem;
    }

    .grid-cols-1 .card-excerpt {
        font-size: 0.65rem;
    }

    .grid-cols-1 .nickname {
        font-size: 0.6rem;
    }

    .grid-cols-1 .tags-container {
        display: none;
    }

    .grid-cols-1 .stat-item {
        font-size: 0.6rem;
    }

    .grid-cols-1 .time-text {
        font-size: 0.55rem;
    }
}
</style>