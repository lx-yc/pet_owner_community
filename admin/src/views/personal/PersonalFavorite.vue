<template>
    <div class="personal-favorite" ref="containerRef">
        <!-- 帖子列表 -->
        <div class="posts-grid" v-if="posts.length > 0">
            <div v-for="post in posts" :key="post.id" class="post-item">
                <!-- 图片区域 -->
                <div class="image-wrapper" @click="handlePostClick(post)">
                    <img :src="post.images && post.images.length > 0 ? JSON.parse(post.images)[0] : 'https://via.placeholder.com/300x300?text=No+Image'"
                        alt="帖子图片" class="post-image" loading="lazy" />
                    <!-- 左下角点赞按钮 -->
                    <div class="like-count-badge" @click.stop="handleLikeClick(post, $event)">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" 
                            :fill="post.isLiked ? 'currentColor' : 'none'"
                            :class="{ 'liked': post.isLiked }"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                        </svg>
                        <span>{{ formatNumber(post.likeCount || 0) }}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-state">
            <p>暂无收藏</p>
        </div>

        <!-- 底部加载状态指示器 -->
        <div ref="sentinelRef" class="scroll-sentinel">
            <div v-if="loading" class="loading-indicator">
                <div class="spinner"></div>
                <span>加载中...</span>
            </div>
            <div v-else-if="isLast && posts.length > 0" class="end-message">
                <span>已经到底啦~</span>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, inject, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMyFavoriteListApi, getUserFavoriteListApi, viewPostApi, likePostApi } from '../../api/post.js'
import { useUserStore } from '../../stores/user.js'

const route = useRoute()
const userStore = useUserStore()
const openLoginModal = inject('openLoginModal', () => {})
const containerRef = ref(null)
const sentinelRef = ref(null)
const posts = ref([])
const lastId = ref(null)
const isLast = ref(false)
const loading = ref(false)
let observer = null

const checkAuth = () => {
    if (!userStore.isLoggedIn) {
        openLoginModal()
        return false
    }
    return true
}

// 判断是否是他人主页
const isOtherUser = computed(() => {
    return route.path.startsWith('/user/')
})

// 获取他人用户ID
const otherUserId = computed(() => {
    if (isOtherUser.value) {
        return route.params.id
    }
    return null
})

// 格式化数字 (例如: 1200 -> 1.2k)
const formatNumber = (num) => {
    if (!num) return 0
    return num >= 1000 ? (num / 1000).toFixed(1) + 'k' : num
}

// 初始化获取数据
const fetchPosts = async () => {
    if (loading.value || isLast.value) return
    
    loading.value = true
    try {
        const params = {
            pageSize: 10,
            lastId: lastId.value
        }

        let res
        if (isOtherUser.value) {
            res = await getUserFavoriteListApi(otherUserId.value, params)
        } else {
            res = await getMyFavoriteListApi(params)
        }
        
        if (res.code === 0) {
            const data = res.data
            if (data.list && data.list.length > 0) {
                posts.value.push(...data.list)
                lastId.value = data.nextCursorId
                isLast.value = data.isLast
            } else {
                isLast.value = true
            }
        }
    } catch (error) {
        console.error('获取收藏列表失败:', error)
    } finally {
        loading.value = false
    }
}

// 处理帖子点击（增加浏览量）
const handlePostClick = async (post) => {
    if (!checkAuth()) return
    
    viewPostApi(post.id).catch(err => {
        console.warn('浏览计数上报失败:', err)
    })

    window.open(`/post/${post.id}`, '_blank')
}

// 处理点赞/取消点赞
const handleLikeClick = async (post, event) => {
    event.stopPropagation()
    
    if (!checkAuth()) return
    
    const originalIsLiked = post.isLiked
    const originalLikeCount = post.likeCount || 0
    
    post.isLiked = !post.isLiked
    post.likeCount = post.isLiked ? originalLikeCount + 1 : originalLikeCount - 1
    
    try {
        await likePostApi(post.id)
    } catch (error) {
        console.error('点赞操作失败:', error)
        post.isLiked = originalIsLiked
        post.likeCount = originalLikeCount
    }
}

// 设置IntersectionObserver监听
const setupObserver = () => {
    if (observer) {
        observer.disconnect()
    }
    
    observer = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !loading.value && !isLast.value) {
            fetchPosts()
        }
    }, {
        root: null,
        rootMargin: '200px',
        threshold: 0
    })
    
    if (sentinelRef.value) {
        observer.observe(sentinelRef.value)
    }
}

// 页面挂载时加载初始数据
onMounted(() => {
    fetchPosts()
    setupObserver()
})

onUnmounted(() => {
    if (observer) {
        observer.disconnect()
    }
})
</script>

<style scoped>
.personal-favorite {
    width: 100%;
    min-height: 50vh;
    background-color: #f9f9f9;
    padding-top: 80px;
}

.posts-grid {
    display: grid;
    gap: 10px;
    padding: 10px;
    grid-template-columns: repeat(2, 1fr);
    cursor: pointer;
}

.post-item {
    position: relative;
    border-radius: 8px;
    overflow: hidden;
    background: #fff;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    transition: transform 0.2s, box-shadow 0.2s;
}

@media (hover: hover) {
    .post-item:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    }
}

.image-wrapper {
    position: relative;
    width: 100%;
    aspect-ratio: 1 / 1;
    overflow: hidden;
}

.post-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 0.3s ease;
}

@media (hover: hover) {
    .post-item:hover .post-image {
        transform: scale(1.05);
    }
}

.like-count-badge {
    position: absolute;
    bottom: 8px;
    left: 8px;
    display: flex;
    align-items: center;
    gap: 4px;
    background-color: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(2px);
    color: white;
    padding: 5px 10px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    z-index: 10;
}

.like-count-badge:hover {
    background-color: rgba(0, 0, 0, 0.8);
    transform: scale(1.05);
}

.like-count-badge:active {
    transform: scale(0.95);
}

.like-count-badge svg {
    width: 14px;
    height: 14px;
    transition: all 0.2s ease;
}

.like-count-badge svg.liked {
    color: #ef4444;
    animation: likeIconPulse 0.3s ease;
}

.like-count-badge:hover svg {
    transform: scale(1.1);
}

@keyframes likeIconPulse {
    0%, 100% {
        transform: scale(1);
    }
    50% {
        transform: scale(1.3);
    }
}

/* 底部加载状态指示器 */
.scroll-sentinel {
    display: flex;
    justify-content: center;
    padding: 20px;
    min-height: 40px;
}

/* 加载动画 */
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
    border-top-color: #007bff;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

/* 空状态 */
.empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200px;
    color: #999;
    font-size: 14px;
}

/* 到底提示 */
.end-message {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
    color: #999;
    font-size: 13px;
}

@media (min-width: 768px) {
    .posts-grid {
        grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
        gap: 16px;
        padding: 20px;
    }

    .image-wrapper {
        aspect-ratio: 3 / 4;
    }

    .like-count-badge {
        font-size: 12px;
        padding: 5px 10px;
    }
}

@media (min-width: 1200px) {
    .posts-grid {
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 20px;
        max-width: 1200px;
        margin: 0 auto;
    }
}
</style>
