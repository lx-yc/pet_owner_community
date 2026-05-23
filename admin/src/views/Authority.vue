<template>
    <div class="authority-page">
        <div class="page-header">
            <div class="header-content">
                <h1 class="page-title">
                    <svg class="title-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path
                            d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z">
                        </path>
                    </svg>
                    官方公告与资讯
                </h1>
                <p class="page-subtitle">发布官方活动、社区规范及重要通知</p>
            </div>
        </div>

        <!-- 内容区域 -->
        <div class="content-container">
            <!-- 加载状态 -->
            <div v-if="loading && posts.length === 0" class="loading-state">
                <div class="spinner"></div>
                <p>正在加载官方资讯...</p>
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-state">
                <p>{{ error }}</p>
                <button @click="fetchPosts()" class="btn-retry">重试</button>
            </div>

            <!-- 空状态 -->
            <div v-else-if="!loading && posts.length === 0" class="empty-state">
                <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path
                        d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z">
                    </path>
                </svg>
                <p>暂无官方公告</p>
            </div>

            <!-- 文章列表 -->
            <div v-else class="posts-list">
                <article v-for="post in posts" :key="post.id" class="official-card" @click="handlePostClick(post)">
                    <!-- 左侧：封面图 (如果有) -->
                    <div class="card-media" v-if="getPostImage(post)">
                        <img :src="getPostImage(post)" alt="官方封面" class="card-img" loading="lazy" />
                    </div>

                    <!-- 无图时的占位图标 -->
                    <div class="card-media placeholder" v-else>
                        <svg class="placeholder-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="1.5">
                            <path
                                d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z">
                            </path>
                        </svg>
                    </div>

                    <!-- 右侧：内容区 -->
                    <div class="card-content">
                        <div class="card-header">
                            <div class="header-left">
                                <span class="official-tag">
                                    <svg class="tag-icon" viewBox="0 0 24 24" fill="currentColor">
                                        <path
                                            d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z">
                                        </path>
                                    </svg>
                                    官方发布
                                </span>
                                <span class="season-tag" v-if="post.season">{{ getSeasonName(post.season) }}</span>
                            </div>
                            <span class="time-text">{{ formatTime(post.createTime) }}</span>
                        </div>

                        <h3 class="card-title">{{ post.title || '暂无标题' }}</h3>

                        <p class="card-excerpt">{{ post.content }}</p>

                        <div class="card-footer">
                            <div class="meta-stats">
                                <!-- 浏览数 -->
                                <span class="stat-item">
                                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                    {{ formatNumber(post.viewCount) }}
                                </span>

                                <!-- 点赞数 (添加点击事件和动态样式) -->
                                <span class="stat-item like-item" :class="{ 'active': post.isLiked }"
                                    @click.stop="debouncedHandleLike(post, $event)">
                                    <svg class="icon" viewBox="0 0 24 24" :fill="post.isLiked ? 'currentColor' : 'none'"
                                        stroke="currentColor" stroke-width="2">
                                        <path
                                            d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z">
                                        </path>
                                    </svg>
                                    {{ formatNumber(post.likeCount) }}
                                </span>

                                <!-- 评论量 -->
                                <span class="stat-item">
                                    <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path
                                            d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z">
                                        </path>
                                    </svg>
                                    {{ formatNumber(post.commentCount) }}
                                </span>

                                <!-- 收藏量 (添加点击事件和动态样式) -->
                                <span class="stat-item collect-item" :class="{ 'active': post.isCollected }"
                                    @click.stop="debouncedHandleCollect(post, $event)">
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
                            <span class="read-more">查看详情 &rarr;</span>
                        </div>
                    </div>
                </article>
            </div>

            <!-- 加载更多指示器 -->
            <div v-if="loading && posts.length > 0" class="loading-more">
                <div class="loading-spinner"></div>
                <span>加载中...</span>
            </div>

            <!-- 没有更多数据 -->
            <div v-if="!hasMore && posts.length > 0" class="no-more">
                <div class="no-more-divider">
                    <span class="divider-line"></span>
                    <span class="divider-text">没有更多官方公告了</span>
                    <span class="divider-line"></span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getPostListApi, likePostApi, collectPostApi, viewPostApi } from '../api/post.js'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useAppStore } from '../stores/app'
import { ElMessage } from 'element-plus'
import { debounce } from '../utils/debounce.js'

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const loading = ref(false)
const error = ref(null)
const posts = ref([])
const hasMore = ref(true)
const cursorId = ref(null)
const cursorTime = ref(null)
const cursorSeasonPriority = ref(null)
const pageSize = ref(10)

const fetchParams = {
    sortType: 'official',
}

const getSeasonName = (season) => {
    const seasonMap = {
        'spring': '春季',
        'summer': '夏季',
        'autumn': '秋季',
        'winter': '冬季'
    }
    return seasonMap[season] || ''
}

const getPostImage = (post) => {
    if (!post.images) return null
    try {
        if (Array.isArray(post.images)) {
            return post.images.length > 0 ? post.images[0] : null
        }
        const parsed = JSON.parse(post.images)
        return Array.isArray(parsed) && parsed.length > 0 ? parsed[0] : null
    } catch (e) {
        return typeof post.images === 'string' && post.images.startsWith('http') ? post.images : null
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
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const fetchPosts = async (isLoadMore = false) => {
    try {
        loading.value = true
        error.value = null

        const params = {
            ...fetchParams,
            pageSize: pageSize.value,
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

            newList.forEach(post => {
                if (post.isLiked === undefined) post.isLiked = false
                if (post.isCollected === undefined) post.isCollected = false
                if (post.likeCount === undefined) post.likeCount = 0
                if (post.favoriteCount === undefined) post.favoriteCount = 0
                if (post.viewCount === undefined) post.viewCount = 0
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

const handleScroll = () => {
    if (loading.value || !hasMore.value) return
    
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop
    const windowHeight = window.innerHeight
    const documentHeight = document.documentElement.scrollHeight
    
    if (scrollTop + windowHeight >= documentHeight - 200) {
        fetchPosts(true)
    }
}

const handleLike = async (post, event) => {
    event.stopPropagation()
    if (!userStore.isLoggedIn) {
        ElMessage.warning('还未登录，请先登录');
        appStore.openLoginModal();
        return;
    }

    const originalIsLiked = post.isLiked
    const originalLikeCount = post.likeCount

    post.isLiked = !post.isLiked
    post.likeCount = post.isLiked ? (originalLikeCount + 1) : (originalLikeCount - 1)

    try {
        await likePostApi(post.id)
    } catch (err) {
        console.error('点赞操作失败:', err)
        post.isLiked = originalIsLiked
        post.likeCount = originalLikeCount
        ElMessage.error('操作失败，请重试')
    }
}

const debouncedHandleLike = debounce(handleLike, 500)

const handleCollect = async (post, event) => {
    event.stopPropagation()
    if (!userStore.isLoggedIn) {
        ElMessage.warning('还未登录，请先登录');
        appStore.openLoginModal();
        return;
    }

    const originalIsCollected = post.isCollected
    const originalFavoriteCount = post.favoriteCount

    post.isCollected = !post.isCollected
    post.favoriteCount = post.isCollected ? (originalFavoriteCount + 1) : (originalFavoriteCount - 1)

    try {
        await collectPostApi(post.id)
    } catch (err) {
        console.error('收藏操作失败:', err)
        post.isCollected = originalIsCollected
        post.favoriteCount = originalFavoriteCount
        ElMessage.error('操作失败，请重试')
    }
}

const debouncedHandleCollect = debounce(handleCollect, 500)

const handlePostClick = async (post) => {
    viewPostApi(post.id).catch(err => {
        console.warn('浏览计数上报失败:', err)
    })

    window.open(`/post/${post.id}`, '_blank')
}

onMounted(() => {
    fetchPosts(false)
    window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.authority-page {
    min-height: 100vh;
    background: linear-gradient(to bottom, #f8fafc 0%, #f1f5f9 100%);
    padding-bottom: 60px;
    /* padding-top: 80px; */
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
    .authority-page {
        padding-top: 70px;
    }
}

@media screen and (max-width: 480px) {
    .authority-page {
        padding-top: 60px;
    }
}

.page-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 50px 20px;
    margin-bottom: 40px;
    box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
    position: relative;
    overflow: hidden;
}

.page-header::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
    opacity: 0.3;
}

.header-content {
    max-width: 1200px;
    margin: 0 auto;
    position: relative;
    z-index: 1;
}

.page-title {
    font-size: 2.2rem;
    font-weight: 800;
    margin: 0 0 12px 0;
    display: flex;
    align-items: center;
    gap: 14px;
    letter-spacing: -0.5px;
}

.title-icon {
    width: 36px;
    height: 36px;
    color: #fbbf24;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.page-subtitle {
    margin: 0;
    opacity: 0.9;
    font-size: 1.05rem;
    font-weight: 300;
    letter-spacing: 0.3px;
}

.content-container {
    max-width: 1100px;
    margin: 0 auto;
    padding-left: 24px;
    padding-right: 24px;
}

.posts-list {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.official-card {
    background: #fff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06), 0 1px 3px rgba(0, 0, 0, 0.04);
    transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    display: flex;
    flex-direction: row;
    border: 1px solid rgba(226, 232, 240, 0.6);
    min-height: 240px;
    position: relative;
}

.official-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
    background: linear-gradient(to bottom, #667eea, #764ba2);
    opacity: 0;
    transition: opacity 0.35s ease;
}

.official-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(102, 126, 234, 0.15), 0 6px 12px rgba(0, 0, 0, 0.08);
    border-color: rgba(102, 126, 234, 0.3);
}

.official-card:hover::before {
    opacity: 1;
}

.card-media {
    width: 300px;
    flex-shrink: 0;
    position: relative;
    background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
    overflow: hidden;
}

.card-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.official-card:hover .card-img {
    transform: scale(1.08);
}

.placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 240px;
}

.placeholder-icon {
    width: 56px;
    height: 56px;
    color: #cbd5e1;
}

.card-content {
    flex: 1;
    padding: 28px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 10px;
}

.official-tag {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    font-size: 0.75rem;
    font-weight: 700;
    padding: 5px 12px;
    border-radius: 20px;
    letter-spacing: 0.5px;
    box-shadow: 0 2px 6px rgba(102, 126, 234, 0.3);
}

.tag-icon {
    width: 13px;
    height: 13px;
}

.season-tag {
    display: inline-flex;
    align-items: center;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    font-size: 0.7rem;
    font-weight: 600;
    padding: 4px 10px;
    border-radius: 16px;
    letter-spacing: 0.3px;
    box-shadow: 0 2px 4px rgba(240, 147, 251, 0.25);
}

.time-text {
    font-size: 0.85rem;
    color: #94a3b8;
    font-weight: 500;
}

.card-title {
    font-size: 1.3rem;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 14px 0;
    line-height: 1.45;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    letter-spacing: -0.3px;
}

.card-excerpt {
    font-size: 0.95rem;
    color: #64748b;
    line-height: 1.7;
    margin: 0 0 18px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    line-clamp: 3;
    overflow: hidden;
    flex-grow: 1;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 18px;
    border-top: 1px solid #f1f5f9;
}

.meta-stats {
    display: flex;
    gap: 18px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 0.85rem;
    color: #94a3b8;
    transition: all 0.2s ease;
    cursor: pointer;
}

.stat-item:hover {
    color: #64748b;
    transform: scale(1.05);
}

.like-item.active {
    color: #ef4444;
}

.like-item.active:hover {
    color: #dc2626;
    transform: scale(1.1);
}

.collect-item.active {
    color: #f59e0b;
}

.collect-item.active:hover {
    color: #d97706;
    transform: scale(1.1);
}

.icon {
    width: 17px;
    height: 17px;
}

.read-more {
    font-size: 0.9rem;
    color: #667eea;
    font-weight: 600;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    gap: 4px;
}

.official-card:hover .read-more {
    color: #764ba2;
    gap: 8px;
}

.loading-state,
.error-state,
.empty-state {
    text-align: center;
    padding: 80px 0;
    color: #64748b;
}

.spinner {
    width: 48px;
    height: 48px;
    border: 4px solid #e2e8f0;
    border-top-color: #667eea;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    margin: 0 auto 18px;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.loading-more {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12px;
    padding: 30px 0;
    color: #64748b;
    font-size: 0.95rem;
}

.loading-spinner {
    width: 24px;
    height: 24px;
    border: 3px solid #e2e8f0;
    border-top-color: #667eea;
    border-radius: 50%;
    animation: spin 0.6s linear infinite;
}

.empty-icon {
    width: 72px;
    height: 72px;
    color: #cbd5e1;
    margin-bottom: 18px;
}

.btn-retry {
    background-color: #fff;
    color: #667eea;
    border: 2px solid #667eea;
    padding: 12px 32px;
    border-radius: 8px;
    font-size: 0.95rem;
    cursor: pointer;
    transition: all 0.25s;
    font-weight: 600;
    margin-top: 16px;
}

.btn-retry:hover {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-color: transparent;
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(102, 126, 234, 0.3);
}

.no-more {
    text-align: center;
    padding: 50px 0;
}

.no-more-divider {
    display: flex;
    align-items: center;
    gap: 16px;
}

.divider-line {
    flex: 1;
    height: 1px;
    background: linear-gradient(to right, transparent, #cbd5e1, transparent);
}

.divider-text {
    font-size: 0.9rem;
    color: #94a3b8;
    font-weight: 500;
    white-space: nowrap;
    letter-spacing: 0.5px;
}

@media screen and (max-width: 900px) {
    .page-header {
        padding: 40px 20px;
        margin-bottom: 30px;
    }

    .page-title {
        font-size: 1.8rem;
    }

    .content-container {
        padding-left: 16px;
        padding-right: 16px;
    }

    .official-card {
        flex-direction: column;
        min-height: auto;
    }

    .card-media {
        width: 100%;
        height: 200px;
    }

    .card-content {
        padding: 20px;
    }

    .card-title {
        font-size: 1.15rem;
        white-space: normal;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        display: -webkit-box;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }

    .card-excerpt {
        -webkit-line-clamp: 2;
        line-clamp: 2;
    }
}

@media screen and (max-width: 480px) {
    .page-title {
        font-size: 1.5rem;
    }

    .page-subtitle {
        font-size: 0.9rem;
    }

    .card-media {
        height: 180px;
    }

    .meta-stats {
        gap: 12px;
    }

    .stat-item {
        font-size: 0.8rem;
    }
}
</style>