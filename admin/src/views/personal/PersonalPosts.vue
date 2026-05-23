<template>
    <div class="personal-posts" ref="containerRef">
        <!-- 帖子列表 -->
        <div class="posts-grid" v-if="posts.length > 0">
            <div v-for="post in posts" :key="post.id" class="post-item">
                <div class="image-wrapper" @click="handlePostClick(post)">
                    <template v-if="post.images && post.images !== '' && JSON.parse(post.images || '[]').length > 0">
                        <img :src="JSON.parse(post.images)[0]"
                            alt="帖子图片" class="post-image" loading="lazy" />
                    </template>
                    <template v-else>
                        <div class="no-image-placeholder">
                            <div class="placeholder-content">
                                <p class="placeholder-text">{{ post.content ? post.content.substring(0, 80) + (post.content.length > 80 ? '...' : '') : '暂无内容' }}</p>
                            </div>
                        </div>
                    </template>
                    <div v-if="!isOtherUser" class="view-count-badge">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                            <circle cx="12" cy="12" r="3"></circle>
                        </svg>
                        <span>{{ formatNumber(post.viewCount) }}</span>
                    </div>
                    <div v-else class="like-count-badge" @click.stop="debouncedHandleLike(post, $event)">
                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" 
                            :fill="post.isLiked ? 'currentColor' : 'none'"
                            :class="{ 'liked': post.isLiked }"
                            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                        </svg>
                        <span>{{ formatNumber(post.likeCount) }}</span>
                    </div>
                    <div v-if="!isOtherUser" class="post-actions">
                        <div class="edit-button" @click.stop="handleEdit(post)">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" 
                                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                            </svg>
                        </div>
                        <div class="delete-button" @click.stop="handleDelete(post)">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" 
                                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="3 6 5 6 21 6"></polyline>
                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                            </svg>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-state">
            <p>暂无帖子</p>
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

        <!-- 自定义删除确认框 -->
        <ConfirmDialog
            :visible="deleteDialogVisible"
            title="删除确认"
            message="确定要删除这篇帖子吗？此操作不可撤销。"
            type="danger"
            confirm-text="确定"
            cancel-text="取消"
            @confirm="handleDeleteConfirm"
            @cancel="handleDeleteCancel"
        />
    </div>
</template>

<script setup>
import { ref, onMounted, computed, inject, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMyPostListApi, getUserPostListApi, viewPostApi, likePostApi, deletePostApi } from '../../api/post.js'
import { useUserStore } from '../../stores/user.js'
import { ElMessage } from 'element-plus'
import ConfirmDialog from '../../components/ConfirmDialog.vue'
import { debounce } from '../../utils/debounce.js'

const route = useRoute()
const userStore = useUserStore()
const openLoginModal = inject('openLoginModal', () => {})
const containerRef = ref(null)
const sentinelRef = ref(null)
const posts = ref([])
const cursorId = ref(null)
const cursorTime = ref(null)
const isLast = ref(false)
const loading = ref(false)
const deleteDialogVisible = ref(false)
const currentPost = ref(null)
let observer = null

const handleVisibilityChange = () => {
    if (!document.hidden) {
        resetList()
        fetchPosts()
    }
}

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
            sortType: 'latest',
            pageSize: 10,
            cursorId: cursorId.value,
            cursorTime: cursorTime.value
        }

        let res
        if (isOtherUser.value) {
            res = await getUserPostListApi(otherUserId.value, params)
        } else {
            res = await getMyPostListApi(params)
        }
        
        if (res.code === 0) {
            const data = res.data
            if (data.list && data.list.length > 0) {
                posts.value.push(...data.list)
                cursorId.value = data.nextCursorId
                cursorTime.value = data.nextCursorTime
                isLast.value = data.isLast
            } else {
                isLast.value = true
            }
        }
    } catch (error) {
        console.error('获取帖子失败:', error)
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

// 处理点赞
const handleLike = async (post, event) => {
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

const debouncedHandleLike = debounce(handleLike, 500)

// 处理编辑
const handleEdit = (post) => {
    if (!checkAuth()) return
    
    window.open(`/submit?edit=${post.id}`, '_blank')
}

// 处理删除
const handleDelete = (post) => {
    if (!checkAuth()) return
    
    currentPost.value = post
    deleteDialogVisible.value = true
}

// 确认删除
const handleDeleteConfirm = async () => {
    if (!currentPost.value) return
    
    try {
        const res = await deletePostApi(currentPost.value.id)
        if (res.code === 0) {
            // 从列表中移除删除的帖子
            posts.value = posts.value.filter(p => p.id !== currentPost.value.id)
            ElMessage.success('帖子删除成功')
        } else {
            ElMessage.error(res.message || '删除失败')
        }
    } catch (error) {
        console.error('删除帖子失败:', error)
        ElMessage.error('网络错误，删除失败')
    } finally {
        deleteDialogVisible.value = false
        currentPost.value = null
    }
}

// 取消删除
const handleDeleteCancel = () => {
    deleteDialogVisible.value = false
    currentPost.value = null
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

// 重置列表
const resetList = () => {
    posts.value = []
    cursorId.value = null
    cursorTime.value = null
    isLast.value = false
    loading.value = false
}

// 监听路由变化
const unwatchRoute = route.params.id ? null : null

// 页面挂载时加载初始数据
onMounted(() => {
    fetchPosts()
    setupObserver()
    window.addEventListener('visibilitychange', handleVisibilityChange)
})

onUnmounted(() => {
    if (observer) {
        observer.disconnect()
    }
    window.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped>
.personal-posts {
    /* 移除最大宽度限制，让其在容器内自适应，或者根据需要保留 */
    width: 100%;
    min-height: 50vh;
    background-color: #f9f9f9;
    /* 浅色背景衬托卡片 */
}

/* 网格布局 */
.posts-grid {
    display: grid;
    gap: 10px;
    padding: 10px;
    /* 默认移动端：双列布局 */
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

/* PC端悬停效果 */
@media (hover: hover) {
    .post-item:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    }
}

.image-wrapper {
    position: relative;
    width: 100%;
    /* 移动端使用正方形比例，看起来更整齐 */
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

.no-image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: relative;
    overflow: hidden;
}

.no-image-placeholder::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
    animation: pulse 4s ease-in-out infinite;
}

@keyframes pulse {
    0%, 100% {
        transform: scale(1);
        opacity: 0.5;
    }
    50% {
        transform: scale(1.1);
        opacity: 0.8;
    }
}

.placeholder-content {
    position: relative;
    z-index: 1;
    padding: 20px;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
}

.placeholder-text {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.95);
    text-align: center;
    line-height: 1.6;
    word-break: break-all;
    margin: 0;
    font-weight: 500;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 悬停时图片轻微放大 */
@media (hover: hover) {
    .post-item:hover .post-image {
        transform: scale(1.05);
    }
}

/* 播放量徽章 */
.view-count-badge {
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
    pointer-events: none;
}

.view-count-badge svg {
    width: 14px;
    height: 14px;
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

/* 帖子操作按钮组 */
.post-actions {
    position: absolute;
    top: 8px;
    right: 8px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    z-index: 10;
    opacity: 0;
    visibility: hidden;
    transition: opacity 0.3s ease, visibility 0.3s ease;
}

/* PC端悬停时显示操作按钮 */
@media (hover: hover) {
    .post-item:hover .post-actions {
        opacity: 1;
        visibility: visible;
    }
}

/* 编辑按钮 */
.edit-button {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background-color: rgba(102, 126, 234, 0.8);
    backdrop-filter: blur(2px);
    color: white;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.edit-button:hover {
    background-color: rgba(118, 75, 162, 1);
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.edit-button:active {
    transform: scale(0.95);
}

/* 删除按钮 */
.delete-button {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background-color: rgba(239, 68, 68, 0.8);
    backdrop-filter: blur(2px);
    color: white;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.delete-button:hover {
    background-color: rgba(220, 38, 38, 1);
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
}

.delete-button:active {
    transform: scale(0.95);
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



/* ================= 响应式媒体查询 ================= */

/* 平板及以上 (>= 768px) */
@media (min-width: 768px) {
    .posts-grid {
        /* 3列或4列，根据屏幕宽度自动填充 */
        grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
        gap: 16px;
        padding: 20px;
    }

    .image-wrapper {
        /* PC端可以使用固定高度或更大比例，例如 4:3 或 3:4 */
        aspect-ratio: 3 / 4;
    }

    .view-count-badge {
        font-size: 12px;
        padding: 5px 10px;
    }

    .like-count-badge {
        font-size: 12px;
        padding: 5px 10px;
    }
}

/* 大屏电脑 (>= 1200px) */
@media (min-width: 1200px) {
    .posts-grid {
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 20px;
        max-width: 1200px;
        margin: 0 auto;
        /* 居中显示 */
    }
}
</style>