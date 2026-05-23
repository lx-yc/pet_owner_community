<template>
    <div class="post-detail-page">
        <TopNavbar :show-nav-links="false" />

        <div class="main-content-wrapper" ref="mainContentRef">
            <!-- 加载状态 -->
            <div v-if="loading" class="loading-container">
                <div class="spinner"></div>
                <p>加载中...</p>
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-container">
                <p>{{ error }}</p>
                <button @click="fetchPostDetail" class="btn-retry">重试</button>
            </div>

            <!-- 详情内容 -->
            <div v-else-if="post" class="detail-container">
                <article class="post-article">
                    <!-- 头部信息 -->
                    <header class="post-header">
                        <h1 class="post-title">{{ post.title }}</h1>
                        <div class="post-meta">
                            <span class="author-info" v-if="!post.isAnonymous" @click="goToAuthorHome(post)" style="cursor: pointer;">
                                <img v-if="post.userAvatar" :src="post.userAvatar" class="post-author-avatar" />
                                <span class="author-name">{{ post.userNickname || '未知用户' }}</span>
                                <span v-if="currentUserId === post.userId" class="me-tag">我</span>
                            </span>
                            <span class="author-info" v-else-if="post.isAnonymous">
                                <img src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" class="post-author-avatar" />
                                <span class="author-name">匿名用户</span>
                            </span>
                            <span class="publish-time">{{ formatTime(post.createTime) }}</span>
                            <span class="type-tag" :class="post.type === 'OFFICIAL' ? 'official' : 'community'">
                                {{ post.type === 'OFFICIAL' ? '官方' : '分享' }}
                            </span>
                        </div>
                        
                        <!-- 三个圆点菜单按钮（仅当前用户的帖子显示） -->
                        <div v-if="currentUserId === post.userId" class="post-menu-wrapper">
                            <button class="post-menu-btn" @click.stop="togglePostMenu">
                                <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
                                    <circle cx="6" cy="12" r="2"></circle>
                                    <circle cx="12" cy="12" r="2"></circle>
                                    <circle cx="18" cy="12" r="2"></circle>
                                </svg>
                            </button>
                            
                            <!-- 下拉菜单 -->
                            <div v-show="showPostMenu" class="post-menu-dropdown" @click.stop>
                                <div class="menu-item" @click="handleEditPost">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                    </svg>
                                    <span>编辑</span>
                                </div>
                                <div class="menu-item delete-item" @click="handleDeletePost">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <polyline points="3 6 5 6 21 6"></polyline>
                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                    </svg>
                                    <span>删除</span>
                                </div>
                            </div>
                        </div>
                    </header>

                    <!-- 封面图/图片列表 -->
                    <div v-if="post.images && post.images.length > 0" class="post-images-wrapper">
                        <div v-if="JSON.parse(post.images).length === 1" class="single-image-container">
                            <img :src="JSON.parse(post.images)[0]" alt="帖子图片" class="detail-img" />
                        </div>
                        <div v-else class="post-images-carousel" @mouseenter="isHoveringCarousel = true" @mouseleave="isHoveringCarousel = false">
                            <div class="carousel-track" :style="{ transform: `translateX(calc(-${currentImageIndex * 100}% + ${dragOffset}px))`, transition: isDragging ? 'none' : 'transform 0.3s ease-out' }"
                                @touchstart="handleTouchStart" @touchmove="handleTouchMove" @touchend="handleTouchEnd"
                                @mousedown="handleMouseDown" @mousemove="handleMouseMove" @mouseup="handleMouseUp"
                                @mouseleave="handleMouseUp" @click.prevent>
                                <div v-for="(img, index) in JSON.parse(post.images)" :key="index" class="carousel-slide">
                                    <img :src="img" alt="帖子图片" class="detail-img" @click.stop />
                                </div>
                            </div>

                            <button class="carousel-btn carousel-prev" :class="{ 'visible': isHoveringCarousel }" @click="prevImage">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="15 18 9 12 15 6"></polyline>
                                </svg>
                            </button>
                            <button class="carousel-btn carousel-next" :class="{ 'visible': isHoveringCarousel }" @click="nextImage">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="9 18 15 12 9 6"></polyline>
                                </svg>
                            </button>
                        </div>
                        <div v-if="JSON.parse(post.images).length > 1" class="carousel-indicators-outside">
                            <span v-for="(img, index) in JSON.parse(post.images)" :key="index"
                                :class="['indicator-dot-outside', { active: index === currentImageIndex }]"
                                @click="goToImage(index)">
                            </span>
                        </div>
                    </div>

                    <!-- 正文内容 -->
                    <div class="post-content" v-html="post.content"></div>

                    <!-- 标签区域 -->
                    <div class="tags-section" v-if="post.tags">
                        <span v-for="(tag, index) in post.tags.split(',')" :key="index" class="detail-tag-item">
                            #{{ tag.trim() }}
                        </span>
                    </div>

                    <!-- 底部互动栏 -->
                    <footer class="post-footer">
                        <div class="stats-row">
                            <div class="stat-item">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                    <circle cx="12" cy="12" r="3"></circle>
                                </svg>
                                <span>{{ post.viewCount || 0 }} 浏览</span>
                            </div>

                            <div class="stat-item like-item" :class="{ 'active': post && post.isLiked }"
                                @click="debouncedHandleLike">
                                <svg viewBox="0 0 24 24" :fill="post && post.isLiked ? 'currentColor' : 'none'"
                                    stroke="currentColor" stroke-width="2">
                                    <path
                                        d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z">
                                    </path>
                                </svg>
                                <span>{{ post.likeCount || 0 }} 点赞</span>
                            </div>

                            <!-- 修改：添加点击事件，跳转到评论区 -->
                            <div class="stat-item comment-stat-item" @click="scrollToComments">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path
                                        d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z">
                                    </path>
                                </svg>
                                <span>{{ post.commentCount || 0 }} 评论</span>
                            </div>

                            <div class="stat-item collect-item" :class="{ 'active': post && post.isCollected }"
                                @click="debouncedHandleCollect">
                                <svg viewBox="0 0 24 24" :fill="post && post.isCollected ? 'currentColor' : 'none'"
                                    stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                    stroke-linejoin="round">
                                    <path
                                        d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2z">
                                    </path>
                                </svg>
                                <span>{{ post.favoriteCount || 0 }} 收藏</span>
                            </div>
                        </div>
                    </footer>
                </article>

                <!-- ================= 评论区 ================= -->
                <div class="comments-section" id="comments-section" ref="commentsSectionRef">
                    <h3>评论</h3>

                    <!-- 【位置 A】默认位置的输入框：位于评论区顶部 -->
                    <!-- 当 isInputFixed 为 false 时显示，为 true 时隐藏（由固定位接管） -->
                    <div class="input-area-container">

                        <!-- 情况1：未固定时，显示真实输入框 -->
                        <div v-show="!isInputFixed" class="inline-comment-input-container">
                            <div class="compact-input-wrapper">
                                <img :src="avator" alt="avatar" class="compact-avatar" />
                                <input ref="inlineInputRef" v-model="commentContent" type="text"
                                    :placeholder="currentReplyTarget ? `回复 ${currentReplyTarget.nickname}...` : '写下你的评论...'"
                                    @keyup.enter="handlePublishComment" class="compact-input" />
                                <button @click="handlePublishComment" :disabled="!commentContent.trim()"
                                    class="btn-compact-publish">
                                    发布
                                </button>
                            </div>
                            <!-- 回复提示条 -->
                            <div v-if="currentReplyTarget" class="replying-banner">
                                <span>正在回复: {{ currentReplyTarget.nickname }}</span>
                                <button @click="cancelReply" class="btn-cancel-reply">×</button>
                            </div>
                        </div>

                        <!-- 情况2：固定时，显示占位符 (防止下方内容上移) -->
                        <!-- 注意：这里需要确保占位符的高度与上面的输入框一致 -->
                        <div v-show="isInputFixed" class="input-placeholder"></div>

                    </div>

                    <!-- 首次加载状态 -->
                    <div v-if="loadingTop && topComments.length === 0" class="loading-container">
                        <div class="spinner"></div>
                        <p>加载中...</p>
                    </div>

                    <!-- 无评论状态 -->
                    <div v-else-if="!loadingTop && topComments.length === 0" class="empty-comments">
                        <p>暂无评论，快来抢沙发吧~</p>
                    </div>

                    <!-- 评论列表 -->
                    <div v-else>
                        <div v-for="(comment) in topComments" :key="comment.id" class="comment-item">
                            <!-- 一级评论头部 -->
                            <div class="comment-header">
                                <img :src="comment.avatar" alt="avatar" class="comment-avatar"
                                    @click="handleCommentUserClick(comment)" style="cursor: pointer;" />
                                <div class="comment-user-info">
                                    <div class="nickname-row" @click="handleCommentUserClick(comment)"
                                        style="cursor: pointer;">
                                        <span class="nickname">{{ comment.nickname }}</span>
                                        <!-- 作者标识 -->
                                        <span v-if="comment.userId === post.userId && !post.isAnonymous" class="author-badge">作者</span>
                                    </div>
                                </div>
                            </div>

                            <!-- 一级评论内容 -->
                            <div class="comment-content">{{ comment.content }}</div>

                            <!-- 底部操作栏：时间 + 点赞 + 回复 -->
                            <div class="comment-footer">
                                <span class="time-text">{{ formatRelativeTime(comment.createTime) }}</span>

                                <div class="action-group">
                                    <!-- 点赞按钮 -->
                                    <button @click="handleLikeComment(comment.id)" class="action-btn like-action"
                                        :class="{ 'active': likedCommentIds.has(comment.id) }">
                                        <svg viewBox="0 0 24 24"
                                            :fill="likedCommentIds.has(comment.id) ? 'currentColor' : 'none'"
                                            stroke="currentColor" stroke-width="2">
                                            <path
                                                d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                                        </svg>
                                        <span>{{ comment.likeCount || 0 }}</span>
                                    </button>

                                    <!-- 回复按钮 -->
                                    <button class="action-btn reply-action"
                                        :class="{ 'active': repliedCommentIds.has(comment.id) }"
                                        @click="startReply(comment)">
                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path
                                                d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z">
                                            </path>
                                        </svg>
                                        <span>回复</span>
                                    </button>
                                </div>
                            </div>

                            <!-- 子评论区域 -->
                            <div class="replies-section">
                                <div v-if="!replyMap.has(comment.id.toString()) && !loadingReplies && comment.replyCount > 0"
                                    class="view-replies-trigger">
                                    <button @click="fetchReplyComments(comment.id)" class="btn-view-replies">
                                        查看 {{ comment.replyCount }} 条回复
                                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
                                            stroke="currentColor" stroke-width="2">
                                            <polyline points="6 9 12 15 18 9"></polyline>
                                        </svg>
                                    </button>
                                </div>

                                <div v-if="loadingReplies && !replyMap.has(comment.id.toString())"
                                    class="loading-container-small">
                                    <div class="spinner-small"></div>
                                </div>

                                <div v-if="replyMap.has(comment.id.toString())">
                                    <div v-for="reply in replyMap.get(comment.id.toString())" :key="reply.id"
                                        class="reply-item">
                                        <div class="reply-header">
                                            <img :src="reply.avatar" alt="avatar" class="reply-avatar"
                                                @click="handleCommentUserClick(comment)" style="cursor: pointer;" />
                                            <div class="reply-meta" @click="handleCommentUserClick(comment)"
                                                style="cursor: pointer;">
                                                <span class="reply-nickname">{{ reply.nickname }}</span>
                                                <span v-if="reply.userId === post.userId && !post.isAnonymous"
                                                    class="author-badge small">作者</span>
                                                <span v-if="reply.replyNickname" class="reply-to-text">
                                                    回复 <span class="reply-target-name">{{ reply.replyNickname
                                                    }}</span>
                                                </span>
                                            </div>
                                        </div>

                                        <div class="reply-content">{{ reply.content }}</div>

                                        <div class="reply-footer">
                                            <span class="time-text">{{ formatRelativeTime(reply.createTime) }}</span>

                                            <div class="action-group">
                                                <button @click="handleLikeComment(reply.id)"
                                                    class="action-btn like-action"
                                                    :class="{ 'active': likedCommentIds.has(reply.id) }">
                                                    <svg viewBox="0 0 24 24"
                                                        :fill="likedCommentIds.has(reply.id) ? 'currentColor' : 'none'"
                                                        stroke="currentColor" stroke-width="2" width="14" height="14">
                                                        <path
                                                            d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                                                    </svg>
                                                    <span>{{ reply.likecount || 0 }}</span>
                                                </button>
                                                <button class="action-btn reply-action"
                                                    :class="{ 'active': repliedCommentIds.has(reply.id) }"
                                                    @click="startReply(reply)">
                                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                                        stroke-width="2" width="14" height="14">
                                                        <path
                                                            d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z">
                                                        </path>
                                                    </svg>
                                                    <span>回复</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>

                                    <div v-if="hasMoreReplies[comment.id.toString()]" class="view-replies-trigger">
                                        <button @click="loadMoreReplies(comment.id)" class="btn-view-replies">
                                            查看更多回复
                                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
                                                stroke="currentColor" stroke-width="2">
                                                <polyline points="6 9 12 15 18 9"></polyline>
                                            </svg>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 底部加载状态/哨兵 -->
                        <div class="load-status-container">
                            <div v-if="loadingTop" class="status-text loading">
                                <div class="spinner-small"></div>
                                <span>加载中...</span>
                            </div>
                            <div v-else-if="!hasMoreTop" class="status-text end">
                                <span>—— 到底了 ——</span>
                            </div>
                            <!-- <div v-if="hasMoreTop && !loadingTop" ref="sentinelRef" class="sentinel"></div> -->
                            <div v-show="hasMoreTop && !loadingTop" ref="sentinelRef" class="sentinel"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 【位置 B】固定底部的输入框：当滚动超过位置 A 时显示 -->
        <transition name="slide-up">
            <div v-show="isInputFixed" class="fixed-comment-bar">
                <div class="fixed-comment-inner">
                    <div class="compact-input-wrapper fixed-wrapper">
                        <img :src="avator" alt="avatar" class="compact-avatar" />
                        <input ref="fixedInputRef" v-model="commentContent" type="text"
                            :placeholder="currentReplyTarget ? `回复 ${currentReplyTarget.nickname}...` : '写下你的评论...'"
                            @keyup.enter="handlePublishComment" class="compact-input" />
                        <button @click="handlePublishComment" :disabled="!commentContent.trim()"
                            class="btn-compact-publish">
                            发布
                        </button>
                    </div>
                    <!-- 固定位的回复提示 -->
                    <div v-if="currentReplyTarget" class="replying-banner fixed-banner">
                        <span>正在回复: {{ currentReplyTarget.nickname }}</span>
                        <button @click="cancelReply" class="btn-cancel-reply">×</button>
                    </div>
                </div>
            </div>
        </transition>

        <!-- 删除确认框 -->
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
import { ref, onMounted, computed, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useAppStore } from '../stores/app'
import { getPostByIdApi, likePostApi, collectPostApi, deletePostApi } from '../api/post.js'
import { ElMessage } from 'element-plus'
import { getTopCommentsApi, getReplyCommentsApi, likeCommentApi, insertCommentApi } from '../api/comment.js'
import NotificationDropdown from '../components/NotificationDropdown.vue'
import UserAvatarDropdown from '../components/UserAvatarDropdown.vue'
import TopNavbar from '../components/TopNavbar.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import { debounce } from '../utils/debounce.js'

// 评论相关状态
const topComments = ref([])
const replyMap = ref(new Map())
const loadingTop = ref(false)
const loadingReplies = ref(false)
const hasMoreTop = ref(true)
const hasMoreReplies = ref({})

// 帖子菜单相关状态
const showPostMenu = ref(false)
const deleteDialogVisible = ref(false)

// --- 滚动与固定输入框相关 ---
const sentinelRef = ref(null)
const commentsSectionRef = ref(null)
const inlineInputRef = ref(null) // 默认位置输入框引用
const fixedInputRef = ref(null)  // 固定位置输入框引用
const isInputFixed = ref(false)  // 控制输入框是否固定到底部
let observer = null
let scrollListener = null

// -----------
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const post = ref(null)
const loading = ref(false)
const error = ref(null)

// --- 用户信息逻辑 ---
const nickname = computed(() => userStore.userInfo?.nickname || '登录');
const avator = computed(() => userStore.userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png');
const currentUserId = computed(() => userStore.userInfo?.id)

// 评论输入相关状态
const commentContent = ref('')
const currentReplyTarget = ref(null)

// 追踪点赞和回复状态的集合
const likedCommentIds = ref(new Set())
const repliedCommentIds = ref(new Set())

// 轮播图相关状态
const currentImageIndex = ref(0)
const touchStartX = ref(0)
const touchEndX = ref(0)
const isDragging = ref(false)
const dragStartX = ref(0)
const dragOffset = ref(0)
const isHoveringCarousel = ref(false)

const imageCount = computed(() => {
    return post.value && post.value.images ? JSON.parse(post.value.images).length : 0
})

const goToImage = (index) => {
    currentImageIndex.value = index
}

const prevImage = () => {
    if (currentImageIndex.value > 0) {
        currentImageIndex.value--
    }
}

const nextImage = () => {
    if (currentImageIndex.value < imageCount.value - 1) {
        currentImageIndex.value++
    }
}

const handleTouchStart = (e) => {
    touchStartX.value = e.touches[0].clientX
    isDragging.value = true
    dragStartX.value = e.touches[0].clientX
    dragOffset.value = 0
}

const handleTouchMove = (e) => {
    if (!isDragging.value) return
    const diff = e.touches[0].clientX - dragStartX.value
    if (currentImageIndex.value === 0 && diff > 0) {
        dragOffset.value = diff * 0.3
    } else if (currentImageIndex.value === imageCount.value - 1 && diff < 0) {
        dragOffset.value = diff * 0.3
    } else {
        dragOffset.value = diff
    }
    touchEndX.value = e.touches[0].clientX
}

const handleTouchEnd = () => {
    if (!isDragging.value) return
    isDragging.value = false
    const diff = touchStartX.value - touchEndX.value
    const threshold = 50
    if (diff > threshold) {
        nextImage()
    } else if (diff < -threshold) {
        prevImage()
    }
    touchStartX.value = 0
    touchEndX.value = 0
    dragOffset.value = 0
}

const handleMouseDown = (e) => {
    isDragging.value = true
    dragStartX.value = e.clientX
    dragOffset.value = 0
}

const handleMouseMove = (e) => {
    if (!isDragging.value) return
    const diff = e.clientX - dragStartX.value
    if (currentImageIndex.value === 0 && diff > 0) {
        dragOffset.value = diff * 0.3
    } else if (currentImageIndex.value === imageCount.value - 1 && diff < 0) {
        dragOffset.value = diff * 0.3
    } else {
        dragOffset.value = diff
    }
}

const handleMouseUp = () => {
    if (!isDragging.value) return
    isDragging.value = false
    const threshold = 50
    if (dragOffset.value > threshold) {
        prevImage()
    } else if (dragOffset.value < -threshold) {
        nextImage()
    }
    dragOffset.value = 0
}

// 切换为回复模式
const startReply = (comment) => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
        ElMessage.warning('请先登录');
        return;
    }

    repliedCommentIds.value.add(comment.id)

    let targetParentId = comment.parentId && comment.parentId !== 0 ? comment.parentId : comment.id;

    currentReplyTarget.value = {
        id: comment.id,
        userId: comment.userId,
        nickname: comment.nickname,
        parentId: targetParentId
    }

    // 聚焦当前可见的输入框
    nextTick(() => {
        const inputRef = isInputFixed.value ? fixedInputRef.value : inlineInputRef.value;
        if (inputRef) {
            inputRef.focus()
        }
    })
}

// 取消回复
const cancelReply = () => {
    currentReplyTarget.value = null
}

// 【新增】处理点击其他地方取消回复
const handleClickOutside = (event) => {
    // 如果当前没有正在回复，直接返回
    if (!currentReplyTarget.value) return;

    // 获取输入框元素（包括默认和固定两个）
    const inlineInputEl = inlineInputRef.value?.$el || inlineInputRef.value;
    const fixedInputEl = fixedInputRef.value?.$el || fixedInputRef.value;

    // 检查点击目标是否在输入框区域、回复提示条区域、或者被点击的评论按钮本身
    // 这里简单判断：如果点击的不是输入框及其子元素，也不是回复按钮，则取消
    const isClickInsideInput = inlineInputEl?.contains(event.target) || fixedInputEl?.contains(event.target);

    // 注意：event.target 可能是 svg 或 path，需要向上查找是否包含 action-btn
    let targetBtn = event.target;
    while (targetBtn && targetBtn !== document.body) {
        if (targetBtn.classList && targetBtn.classList.contains('reply-action')) {
            // 如果点击的是回复按钮，不取消，因为 startReply 会处理
            return;
        }
        targetBtn = targetBtn.parentNode;
    }

    if (!isClickInsideInput) {
        cancelReply();
    }
};

// 发布评论
// 发布评论
const handlePublishComment = async () => {
    const content = commentContent.value.trim()
    if (!content) return

    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
        ElMessage.warning('请先登录');
        return;
    }

    // 记录当前回复的目标信息，因为后面会清空 currentReplyTarget
    const targetParentId = currentReplyTarget.value ? currentReplyTarget.value.parentId : 0
    const isReplyToComment = targetParentId !== 0 // 判断是否是回复某条评论（而不是直接评论帖子）

    const data = {
        postId: post.value.id,
        content,
        parentId: targetParentId,
        replyUserId: currentReplyTarget.value ? currentReplyTarget.value.userId : 0
    }

    try {
        await insertCommentApi(data)

        commentContent.value = ''
        currentReplyTarget.value = null

        ElMessage.success('评论发布成功')

        // ================= 核心逻辑修改开始 =================

        // 1. 如果是回复某条评论（子评论）
        if (isReplyToComment) {
            const key = targetParentId.toString()

            // 情况 A: 该父评论的回复列表已经展开（在 map 中存在）
            if (replyMap.value.has(key)) {
                // 重新获取该父评论下的最新回复列表
                // 注意：这里为了简单和保证顺序，通常重新拉取第一页或者最新几条
                // 如果需要更精细的控制，可以只拉取最新一条并 unshift 到数组头部
                await fetchReplyComments(targetParentId, false)

                // 更新对应父评论在 topComments 中的计数
                updateParentReplyCount(targetParentId, 1)
            }
            // 情况 B: 该父评论的回复列表尚未展开
            else {
                // 强制加载并展开该父评论的回复
                await fetchReplyComments(targetParentId, false)

                // 同样更新计数
                updateParentReplyCount(targetParentId, 1)
            }
        }
        // 2. 如果是直接评论帖子（一级评论）
        else {
            // 重置 hasMoreTop 并重新获取第一页一级评论
            hasMoreTop.value = true
            await fetchTopComments(false)
        }

        // ================= 核心逻辑修改结束 =================

        // 可选：发布后自动滚动到评论区顶部
        // scrollToComments() 
    } catch (err) {
        ElMessage.error('发布失败，请重试')
        console.error(err)
    }
}

// 辅助函数：更新一级评论列表中的回复计数
const updateParentReplyCount = (parentId, increment = 1) => {
    // 在 topComments 中找到对应的父评论
    const parentComment = topComments.value.find(c => c.id === parentId)
    if (parentComment) {
        parentComment.replyCount = (parentComment.replyCount || 0) + increment
    }
}

// 处理评论点赞逻辑
const handleLikeComment = async (commentId) => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
        ElMessage.warning('请先登录');
        return;
    }

    let targetComment = topComments.value.find(c => c.id === commentId);
    if (!targetComment) {
        for (const [, replies] of replyMap.value.entries()) {
            const found = replies.find(r => r.id === commentId);
            if (found) {
                targetComment = found;
                break;
            }
        }
    }
    if (!targetComment) return;

    const isCurrentlyLiked = likedCommentIds.value.has(commentId);

    if (isCurrentlyLiked) {
        likedCommentIds.value.delete(commentId);
        targetComment.likeCount = Math.max(0, (targetComment.likeCount || 0) - 1);
    } else {
        likedCommentIds.value.add(commentId);
        targetComment.likeCount = (targetComment.likeCount || 0) + 1;
    }

    try {
        await likeCommentApi(commentId);
    } catch (err) {
        if (isCurrentlyLiked) {
            likedCommentIds.value.add(commentId);
            targetComment.likeCount = (targetComment.likeCount || 0) + 1;
        } else {
            likedCommentIds.value.delete(commentId);
            targetComment.likeCount = Math.max(0, (targetComment.likeCount || 0) - 1);
        }
        console.error('评论点赞失败', err);
        ElMessage.error('操作失败，请稍后重试');
    }
};

// 获取一级评论
// 新增：专门存储游标
const nextCursorId = ref(0)

const fetchTopComments = async (isLoadMore = false) => {
    if (!post.value?.id) return
    if (loadingTop.value) return

    if (isLoadMore && !hasMoreTop.value) {
        console.log('没有更多评论了')
        return
    }

    loadingTop.value = true
    try {
        let cursorId = 0
        if (isLoadMore) {
            cursorId = nextCursorId.value
        }

        const res = await getTopCommentsApi({
            postId: post.value.id,
            lastId: cursorId,
            pageSize: 5
        })

        if (res.code === 0) {
            const newList = res.data.list || []

            newList.forEach(c => {
                if (c.isLiked) likedCommentIds.value.add(c.id)
            })

            if (isLoadMore) {
                topComments.value.push(...newList)
            } else {
                topComments.value = newList
                nextCursorId.value = 0
            }

            // 1. 先更新状态
            hasMoreTop.value = !res.data.isLast

            // 2. 更新游标
            if (res.data.nextCursorId) {
                nextCursorId.value = res.data.nextCursorId
            } else if (newList.length > 0) {
                nextCursorId.value = newList[newList.length - 1].id
            } else {
                nextCursorId.value = 0
            }

            // console.log('Fetch Top Comments - IsLoadMore:', isLoadMore, 'Count:', newList.length, 'HasMore:', hasMoreTop.value, 'NextCursorId:', nextCursorId.value)

            // 3. 等待 DOM 更新
            await nextTick()

            // 4. 【关键修改】无论是否有更多数据，先断开旧的 Observer
            if (observer) {
                observer.disconnect()
                observer = null
            }

            // 5. 【关键修改】如果还有更多数据，且哨兵元素存在，重新初始化
            // 注意：这里必须确保 sentinelRef.value 是当前 DOM 中那个最新的 div
            if (hasMoreTop.value) {
                if (sentinelRef.value) {
                    initObserver()
                } else {
                    // 极端情况下 sentinelRef 可能还没更新，尝试再次等待
                    setTimeout(() => {
                        if (sentinelRef.value) initObserver()
                    }, 100)
                }
            }
        }
    } catch (err) {
        ElMessage.error('获取评论失败')
        if (!isLoadMore) {
            hasMoreTop.value = false
        }
    } finally {
        loadingTop.value = false
    }
}

// 初始化 Intersection Observer (用于加载更多评论)
const initObserver = () => {
    if (observer) {
        observer.disconnect()
    }

    if (!sentinelRef.value) {
        console.warn('Sentinel element not found in DOM')
        return
    }

    // 打印哨兵元素的位置信息，方便调试
    // const rect = sentinelRef.value.getBoundingClientRect()
    // console.log('Initializing Observer for sentinel at top:', rect.top, 'bottom:', rect.bottom)

    observer = new IntersectionObserver((entries) => {
        const entry = entries[0]
        // console.log('Intersection Observer Triggered:', entry.isIntersecting, 'Ratio:', entry.intersectionRatio)

        if (entry.isIntersecting && hasMoreTop.value && !loadingTop.value) {
            // console.log('>>> Trigger Load More <<<')
            fetchTopComments(true)
        }
    }, {
        root: null,
        rootMargin: '200px', // 提前 200px 加载
        threshold: 0.1
    })

    observer.observe(sentinelRef.value)
    // console.log('Observer initialized successfully')
}




// 获取某个父评论的子评论
const fetchReplyComments = async (parentId, isLoadMore = false) => {
    if (!parentId) return
    if (loadingReplies.value) return

    loadingReplies.value = true
    try {
        let lastId = 0
        const key = parentId.toString()

        if (isLoadMore && replyMap.value.has(key)) {
            const currentList = replyMap.value.get(key)
            if (currentList && currentList.length > 0) {
                lastId = currentList[currentList.length - 1].id
            } else {
                isLoadMore = false
            }
        }

        const res = await getReplyCommentsApi({
            parentId,
            lastId,
            pageSize: 5
        })

        if (res.code === 0) {
            const list = res.data.list || []

            list.forEach(r => {
                if (r.isLiked) likedCommentIds.value.add(r.id)
            })

            if (isLoadMore) {
                if (replyMap.value.has(key)) {
                    const currentList = replyMap.value.get(key)
                    replyMap.value.set(key, [...currentList, ...list])
                }
            } else {
                replyMap.value.set(key, list)
            }

            hasMoreReplies.value[key] = list.length >= 5
        }
    } catch (err) {
        console.error('获取子评论失败', err)
        ElMessage.warning('获取子评论失败')
    } finally {
        loadingReplies.value = false
    }
}

const loadMoreReplies = async (parentId) => {
    await fetchReplyComments(parentId, true)
}

const handleProfileClick = () => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
    } else {
        router.push('/personalHome');
    }
};

const formatTime = (timestamp) => {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatRelativeTime = (timestamp) => {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    const now = new Date()
    const diff = now - date

    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
    if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
    return `${date.getMonth() + 1}-${date.getDate()}`
}

// 【修改】通用的跳转用户主页方法
const goToUserHome = (userId) => {
    if (!userId) return;

    // 获取当前登录用户ID
    const currentId = userStore.userInfo?.id;

    // 确保 userId 和 currentId 都是字符串或数字类型进行比较
    if (currentId && String(currentId) === String(userId)) {
        // 如果是自己，跳转到个人主页
        window.open('/personalHome', '_blank');
    } else {
        // 如果是他人，跳转到公共用户主页 (现在指向 PersonalHome 组件)
        window.open(`/user/${userId}`, '_blank');
    }
};

// 【修改】原有的 goToAuthorHome 现在可以直接调用通用方法
const goToAuthorHome = (postData) => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
        return;
    }
    goToUserHome(postData.userId);
};

// 【新增】评论区头像/昵称点击处理
const handleCommentUserClick = (comment) => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
        return;
    }
    goToUserHome(comment.userId);
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

const fetchPostDetail = async () => {
    const id = route.params.id
    if (!id) {
        error.value = '无效的帖子ID'
        return
    }
    loading.value = true
    error.value = null

    try {
        const res = await getPostByIdApi(id)
        if (res.code === 0) {
            post.value = res.data
            if (post.value.isLiked === undefined) post.value.isLiked = false
            if (post.value.isCollected === undefined) post.value.isCollected = false
        } else {
            throw new Error(res.message || '获取详情失败')
        }
    } catch (err) {
        console.error(err)
        error.value = err.message || '网络请求失败'
    } finally {
        loading.value = false
    }
}

const handleLike = async () => {
    if (!post.value) return
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
        ElMessage.warning('请先登录')
        return;
    }
    const originalIsLiked = post.value.isLiked
    const originalLikeCount = post.value.likeCount
    post.value.isLiked = !post.value.isLiked
    post.value.likeCount = post.value.isLiked ? (originalLikeCount + 1) : (originalLikeCount - 1)
    try {
        await likePostApi(post.value.id)
    } catch (err) {
        post.value.isLiked = originalIsLiked
        post.value.likeCount = originalLikeCount
        console.error('点赞失败', err)
    }
}

const debouncedHandleLike = debounce(handleLike, 500)

const handleCollect = async () => {
    if (!post.value) return
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal();
        ElMessage.warning('请先登录')
        return;
    }
    const originalIsCollected = post.value.isCollected
    const originalFavoriteCount = post.value.favoriteCount
    post.value.isCollected = !post.value.isCollected
    post.value.favoriteCount = post.value.isCollected ? (originalFavoriteCount + 1) : (originalFavoriteCount - 1)
    try {
        await collectPostApi(post.value.id)
    } catch (err) {
        post.value.isCollected = originalIsCollected
        post.value.favoriteCount = originalFavoriteCount
        console.error('收藏失败', err)
    }
}

const debouncedHandleCollect = debounce(handleCollect, 500)

const scrollToComments = () => {
    if (commentsSectionRef.value) {
        commentsSectionRef.value.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
}

// 切换帖子菜单显示状态
const togglePostMenu = () => {
    showPostMenu.value = !showPostMenu.value
}

// 处理编辑帖子
const handleEditPost = () => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal()
        ElMessage.warning('请先登录')
        return
    }
    showPostMenu.value = false
    window.open(`/submit?edit=${post.value.id}`, '_blank')
}

// 处理删除帖子
const handleDeletePost = () => {
    if (!userStore.isLoggedIn) {
        appStore.openLoginModal()
        ElMessage.warning('请先登录')
        return
    }
    showPostMenu.value = false
    deleteDialogVisible.value = true
}

// 确认删除帖子
const handleDeleteConfirm = async () => {
    if (!post.value) return
    
    try {
        const res = await deletePostApi(post.value.id)
        if (res.code === 0) {
            ElMessage.success('帖子删除成功')
            // 删除成功后跳转到首页或用户主页
            window.open('/personalHome', '_self')
        } else {
            ElMessage.error(res.message || '删除失败')
        }
    } catch (error) {
        console.error('删除帖子失败:', error)
        ElMessage.error('网络错误，删除失败')
    } finally {
        deleteDialogVisible.value = false
    }
}

// 取消删除
const handleDeleteCancel = () => {
    deleteDialogVisible.value = false
}

// 点击其他地方关闭菜单
const handleClickOutsideMenu = (event) => {
    if (showPostMenu.value) {
        const menuWrapper = document.querySelector('.post-menu-wrapper')
        if (menuWrapper && !menuWrapper.contains(event.target)) {
            showPostMenu.value = false
        }
    }
}

// 【新增】执行滚动的函数
const checkAndScrollToComments = async () => {
    // 检查路由查询参数
    if (route.query.scrollToComments === 'true') {
        // 等待 DOM 更新，确保评论区已经渲染出来
        await nextTick()

        // 稍微延迟一下，确保图片等资源加载初步完成，避免滚动位置偏差
        setTimeout(() => {
            scrollToComments()

            // 【重要】滚动完成后，清除 URL 中的参数，防止用户刷新页面时再次滚动
            // 使用 replace 模式，不增加历史记录
            router.replace({
                query: { ...route.query, scrollToComments: undefined }
            })
        }, 100) // 500ms 延迟通常足够让内容渲染稳定
    }
}

// --- 修改点 1: 增加一个变量记录输入框的初始偏移量 ---
const inlineInputOffsetTop = ref(0)
// 【新增】记录导航栏高度，用于动态计算阈值
const navbarHeight = ref(64)

// 【修改】监听滚动，判断输入框是否应该固定
const handleScroll = () => {
    // 使用 requestAnimationFrame 确保在浏览器重绘前执行，提高移动端性能
    window.requestAnimationFrame(() => {
        // 获取当前页面的滚动距离
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;

        // 动态获取导航栏高度 (应对移动端导航栏变矮的情况)
        const navbarEl = document.querySelector('.navbar');
        if (navbarEl) {
            navbarHeight.value = navbarEl.offsetHeight;
        }

        // 如果还没有计算过初始位置，先计算一次
        // 注意：这里需要确保 inlineInputRef 已经挂载且可见
        if (inlineInputOffsetTop.value === 0 && inlineInputRef.value) {
            // getBoundingClientRect().top + scrollTop 等于元素距离文档顶部的绝对距离
            // 在移动端，确保元素没有被 transform 或其他属性影响定位
            const rect = inlineInputRef.value.getBoundingClientRect();
            inlineInputOffsetTop.value = rect.top + scrollTop;
        }

        // 判定条件：
        // 1. 必须已经初始化了初始位置
        // 2. 当前滚动距离 > 输入框初始位置 - 导航栏高度 - 一点缓冲(10px)
        if (inlineInputOffsetTop.value > 0) {
            // 动态计算阈值：输入框初始位置 - 导航栏高度 - 缓冲
            const threshold = inlineInputOffsetTop.value - navbarHeight.value - 10;
            if (scrollTop > threshold) {
                if (!isInputFixed.value) {
                    isInputFixed.value = true;
                }
            } else {
                if (isInputFixed.value) {
                    isInputFixed.value = false;
                }
            }
            // console.log('Scroll Top:', scrollTop, 'Threshold:', threshold, 'IsFixed:', isInputFixed.value);
        }
    });
};

const handleVisibilityChange = async () => {
    if (!document.hidden) {
        await fetchTopComments(false)
        await nextTick()
        initObserver()
    }
}

// 【新增】监听路由变化
// 如果用户已经在详情页，通过某种方式改变了 query 参数（虽然少见），或者从其他页面回来
watch(() => route.query.scrollToComments, (newVal) => {
    if (newVal === 'true') {
        checkAndScrollToComments()
    }
})


// 【修改】生命周期钩子
onMounted(() => {
    const init = async () => {
        // 【关键】先强制滚动到页面顶部，防止从其他页面跳转时继承滚动位置
        window.scrollTo(0, 0)

        await fetchPostDetail()

        // 1. 先加载第一页评论
        await fetchTopComments(false)

        if (!observer && sentinelRef.value) {
            initObserver()
        }

        // 【重要】移动端兼容性处理
        // 1. 添加 passive: true 以提高滚动性能
        // 2. 确保监听的是 window，因为你的布局是标准流
        window.addEventListener('scroll', handleScroll, { passive: true });

        // 【新增】针对部分移动端浏览器，resize 时可能需要重新计算位置
        window.addEventListener('resize', () => {
            // 重置初始位置，迫使下次滚动时重新计算
            inlineInputOffsetTop.value = 0;
            handleScroll();
        });

        document.addEventListener('click', handleClickOutside);
        document.addEventListener('click', handleClickOutsideMenu);

        checkAndScrollToComments()
    }
    init()

    window.addEventListener('visibilitychange', handleVisibilityChange)
})

onUnmounted(() => {
    // 务必断开 Observer，防止内存泄漏
    if (observer) {
        observer.disconnect()
        observer = null
    }
    window.removeEventListener('visibilitychange', handleVisibilityChange)
    window.removeEventListener('scroll', handleScroll);
    window.removeEventListener('resize', handleScroll);
    document.removeEventListener('click', handleClickOutside);
    document.removeEventListener('click', handleClickOutsideMenu);
})
</script>

<style scoped>
/* ================= 页面基础布局 ================= */
.post-detail-page {
    min-height: 100vh;
    background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 50%, #f8f9ff 100%);
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    color: #374151;
    padding-bottom: 80px;
    padding-top: 80px;
    position: relative;
}

.post-detail-page::before {
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

/* ================= 顶部导航栏 ================= */
.navbar {
    position: sticky;
    top: 0;
    z-index: 100;
    background-color: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid #e5e7eb;
    box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.navbar-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.brand {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 700;
    font-size: 1.25rem;
    color: #111827;
    cursor: pointer;
}

.brand-icon {
    font-size: 1.5rem;
}

.actions {
    display: flex;
    align-items: center;
    gap: 20px;
}

.user-profile {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
}

.user-nickname {
    font-size: 0.9rem;
    color: #374151;
    font-weight: 500;
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: transform 0.2s;
}

.avatar:hover {
    transform: scale(1.05);
}

/* ================= 主要内容区域 ================= */
.main-content-wrapper {
    padding-top: 20px;
}

.detail-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
    position: relative;
    z-index: 1;
}

.post-article {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    padding: 36px;
    box-shadow: 
        0 8px 24px rgba(0, 0, 0, 0.06),
        0 2px 8px rgba(0, 0, 0, 0.04),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
    border: 1px solid rgba(255, 255, 255, 0.6);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-article:hover {
    box-shadow: 
        0 12px 32px rgba(0, 0, 0, 0.08),
        0 4px 12px rgba(0, 0, 0, 0.06),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.post-header {
    margin-bottom: 28px;
    border-bottom: 2px solid transparent;
    border-image: linear-gradient(to right, transparent, rgba(255, 183, 77, 0.3), transparent) 1;
    padding-bottom: 24px;
    position: relative;
}

.post-header::after {
    content: '🐾';
    position: absolute;
    bottom: -12px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 20px;
    opacity: 0.6;
    background: white;
    padding: 0 12px;
}

.post-title {
    font-size: 2rem;
    font-weight: 800;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0 0 18px 0;
    line-height: 1.35;
    letter-spacing: -0.02em;
}

.post-meta {
    display: flex;
    align-items: center;
    gap: 18px;
    font-size: 0.9rem;
    color: #6b7280;
    flex-wrap: wrap;
}

.author-info {
    display: flex;
    align-items: center;
    gap: 10px;
    font-weight: 500;
    padding: 6px 12px;
    border-radius: 24px;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
}

.author-info:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
    transform: translateY(-1px);
}

.me-tag {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    font-size: 0.7rem;
    padding: 3px 8px;
    border-radius: 12px;
    font-weight: 600;
    box-shadow: 0 2px 6px rgba(102, 126, 234, 0.3);
}

.post-author-avatar {
    width: 52px;
    height: 52px;
    border-radius: 50%;
    object-fit: cover;
    border: 3px solid white;
    box-shadow: 
        0 4px 12px rgba(0, 0, 0, 0.12),
        0 0 0 1px rgba(102, 126, 234, 0.2);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-author-avatar:hover {
    transform: scale(1.08) rotate(5deg);
    box-shadow: 
        0 6px 16px rgba(0, 0, 0, 0.15),
        0 0 0 2px rgba(102, 126, 234, 0.4);
}

.type-tag {
    padding: 4px 12px;
    border-radius: 16px;
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.02em;
    transition: all 0.2s;
}

.type-tag.official {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    box-shadow: 0 3px 8px rgba(102, 126, 234, 0.3);
}

.type-tag.community {
    background: linear-gradient(135deg, #f093fb, #f5576c);
    color: white;
    box-shadow: 0 3px 8px rgba(245, 87, 108, 0.3);
}

.post-menu-wrapper {
    position: absolute;
    top: 0;
    right: 0;
    z-index: 10;
}

.post-menu-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border: none;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    border-radius: 50%;
    cursor: pointer;
    color: #667eea;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-menu-btn:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.post-menu-btn:active {
    transform: scale(0.95);
}

.post-menu-dropdown {
    position: absolute;
    top: calc(100% + 8px);
    right: 0;
    background: white;
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    padding: 8px;
    min-width: 140px;
    z-index: 1000;
    animation: dropdownFadeIn 0.2s ease;
}

@keyframes dropdownFadeIn {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.menu-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    color: #374151;
    font-size: 0.9rem;
    font-weight: 500;
}

.menu-item:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    color: #667eea;
}

.menu-item svg {
    width: 18px;
    height: 18px;
    flex-shrink: 0;
}

.menu-item.delete-item {
    color: #ef4444;
}

.menu-item.delete-item:hover {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(220, 38, 38, 0.1));
    color: #dc2626;
}

.post-images-wrapper {
    margin-bottom: 28px;
    width: 100%;
    max-width: 100%;
    box-sizing: border-box;
}

.single-image-container {
    border-radius: 16px;
    overflow: hidden;
    background: linear-gradient(135deg, #f8f9ff, #f0f2ff);
    width: 100%;
    box-shadow: 
        0 8px 24px rgba(0, 0, 0, 0.08),
        0 2px 8px rgba(0, 0, 0, 0.04);
    border: 2px solid rgba(255, 255, 255, 0.8);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.single-image-container:hover {
    box-shadow: 
        0 12px 32px rgba(0, 0, 0, 0.12),
        0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
}

.single-image-container .detail-img {
    width: 100%;
    height: auto;
    max-height: 500px;
    object-fit: contain;
    display: block;
}

.post-images-carousel {
    position: relative;
    margin-bottom: 16px;
    border-radius: 16px;
    overflow: hidden;
    background: linear-gradient(135deg, #f8f9ff, #f0f2ff);
    width: 100%;
    max-width: 100%;
    box-sizing: border-box;
    box-shadow: 
        0 8px 24px rgba(0, 0, 0, 0.08),
        0 2px 8px rgba(0, 0, 0, 0.04);
    border: 2px solid rgba(255, 255, 255, 0.8);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-images-carousel:hover {
    box-shadow: 
        0 12px 32px rgba(0, 0, 0, 0.12),
        0 4px 12px rgba(0, 0, 0, 0.08);
}

.carousel-track {
    display: flex;
    transition: transform 0.3s ease-out;
    width: 100%;
    position: relative;
    touch-action: none;
    -webkit-touch-action: none;
    user-select: none;
    -webkit-user-select: none;
}

.carousel-slide {
    min-width: 100%;
    width: 100%;
    flex-shrink: 0;
    position: relative;
}

.detail-img {
    width: 100%;
    height: auto;
    max-height: 500px;
    object-fit: contain;
    display: block;
    user-select: none;
    -webkit-user-drag: none;
}

.carousel-btn {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.9));
    color: white;
    border: none;
    border-radius: 50%;
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    opacity: 0;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    backdrop-filter: blur(8px);
}

.carousel-btn.visible {
    opacity: 1;
}

.carousel-btn:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 1), rgba(118, 75, 162, 1));
    transform: translateY(-50%) scale(1.1);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.carousel-btn:active {
    transform: translateY(-50%) scale(0.95);
}

.carousel-btn svg {
    width: 20px;
    height: 20px;
}

.carousel-prev {
    left: 16px;
}

.carousel-next {
    right: 16px;
}

.carousel-indicators-outside {
    display: flex;
    justify-content: center;
    gap: 10px;
    padding: 12px 0 4px 0;
}

.indicator-dot-outside {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: linear-gradient(135deg, #d1d5db, #e5e7eb);
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    position: relative;
}

.indicator-dot-outside::before {
    content: '';
    position: absolute;
    inset: -4px;
    border-radius: 50%;
    background: transparent;
    transition: all 0.3s;
}

.indicator-dot-outside.active {
    background: linear-gradient(135deg, #667eea, #764ba2);
    transform: scale(1.4);
    box-shadow: 
        0 3px 8px rgba(102, 126, 234, 0.4),
        0 0 0 3px rgba(102, 126, 234, 0.2);
}

.indicator-dot-outside.active::before {
    background: rgba(102, 126, 234, 0.15);
}

.indicator-dot-outside:hover:not(.active) {
    background: linear-gradient(135deg, #9ca3af, #d1d5db);
    transform: scale(1.2);
}

.post-content {
    font-size: 1.1rem;
    line-height: 1.9;
    color: #374151;
    white-space: pre-wrap;
    padding: 8px 0;
}

.post-content::first-letter {
    font-size: 1.3em;
    font-weight: 700;
    color: #667eea;
    float: left;
    margin-right: 8px;
    line-height: 1;
}

.tags-section {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 24px;
    margin-bottom: 24px;
    padding: 16px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
    border-radius: 12px;
    border: 1px dashed rgba(102, 126, 234, 0.3);
}

.detail-tag-item {
    padding: 6px 14px;
    background: linear-gradient(135deg, #f0f2ff, #e8ebff);
    color: #667eea;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 600;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    border: 1px solid rgba(102, 126, 234, 0.2);
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.detail-tag-item::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    opacity: 0;
    transition: opacity 0.25s;
}

.detail-tag-item:hover {
    background: linear-gradient(135deg, #e8ebff, #dce0ff);
    color: #764ba2;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
    border-color: rgba(102, 126, 234, 0.4);
}

.detail-tag-item:hover::before {
    opacity: 1;
}

.post-footer {
    margin-top: 32px;
    padding-top: 24px;
    border-top: 2px solid transparent;
    border-image: linear-gradient(to right, transparent, rgba(255, 183, 77, 0.3), transparent) 1;
}

.stats-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    gap: 12px;
}

.stat-item {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    color: #6b7280;
    font-size: 0.9rem;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    flex: 1;
    min-width: 0;
    padding: 10px 16px;
    border-radius: 16px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.5), rgba(250, 248, 245, 0.5));
    border: 1px solid rgba(0, 0, 0, 0.04);
    cursor: default;
}

/* 手机端布局：图标在上，数字在下 */
@media (max-width: 768px) {
    .stats-row {
        flex-wrap: wrap;
    }
    
    .stat-item {
        flex-direction: column;
        gap: 4px;
        padding: 12px 8px;
        min-height: 80px;
    }
    
    .stat-item svg {
        width: 22px;
        height: 22px;
    }
    
    .stat-item span {
        font-size: 0.8rem;
        text-align: center;
        line-height: 1.2;
    }
}

.stat-item svg {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-item:hover {
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(250, 248, 245, 0.8));
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.like-item,
.collect-item,
.comment-stat-item {
    cursor: pointer;
}

.like-item:hover {
    color: #ff6b6b;
    background: linear-gradient(135deg, rgba(255, 107, 107, 0.08), rgba(255, 138, 101, 0.08));
}

.like-item:hover svg {
    transform: scale(1.15);
}

.collect-item:hover {
    color: #ffa726;
    background: linear-gradient(135deg, rgba(255, 167, 38, 0.08), rgba(255, 183, 77, 0.08));
}

.collect-item:hover svg {
    transform: scale(1.15) rotate(15deg);
}

.comment-stat-item:hover {
    color: #667eea;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
}

.comment-stat-item:hover svg {
    transform: scale(1.15);
}

.like-item.active {
    color: #ff6b6b;
    background: linear-gradient(135deg, rgba(255, 107, 107, 0.12), rgba(255, 138, 101, 0.12));
    border-color: rgba(255, 107, 107, 0.2);
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
    background: linear-gradient(135deg, rgba(255, 167, 38, 0.12), rgba(255, 183, 77, 0.12));
    border-color: rgba(255, 167, 38, 0.2);
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

.loading-container,
.error-container {
    text-align: center;
    padding: 60px 0;
    color: #6b7280;
}

.loading-container::before {
    content: '🐾';
    display: block;
    font-size: 48px;
    margin-bottom: 16px;
    animation: pawBounce 1.5s ease-in-out infinite;
}

@keyframes pawBounce {
    0%, 100% { transform: translateY(0) scale(1); }
    50% { transform: translateY(-10px) scale(1.1); }
}

.spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(102, 126, 234, 0.2);
    border-top-color: #667eea;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    margin: 0 auto 16px;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.btn-retry {
    margin-top: 16px;
    padding: 10px 24px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-retry:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.error-container::before {
    content: '😿';
    display: block;
    font-size: 48px;
    margin-bottom: 16px;
}

/* ================= 评论区样式 ================= */
.comments-section {
    margin-top: 32px;
    padding: 28px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    box-shadow: 
        0 8px 24px rgba(0, 0, 0, 0.06),
        0 2px 8px rgba(0, 0, 0, 0.04),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
    border: 1px solid rgba(255, 255, 255, 0.6);
    scroll-margin-top: 80px;
    overflow-y: visible;
    position: relative;
}

.comments-section::before {
    content: '💬';
    position: absolute;
    top: -16px;
    left: 28px;
    font-size: 28px;
    background: white;
    padding: 0 8px;
    border-radius: 50%;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.comments-section h3 {
    font-size: 1.3rem;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid transparent;
    border-image: linear-gradient(to right, transparent, rgba(102, 126, 234, 0.3), transparent) 1;
}

.empty-comments {
    text-align: center;
    padding: 48px 0;
    color: #9ca3af;
    font-size: 0.95rem;
}

.empty-comments::before {
    content: '🐾';
    display: block;
    font-size: 48px;
    margin-bottom: 12px;
    opacity: 0.5;
}

.input-placeholder {
    min-height: 80px;
    width: 100%;
    height: 84px;
}


/* ================= 紧凑输入框样式 (通用) ================= */
.inline-comment-input-container {
    margin-bottom: 28px;
    padding-bottom: 20px;
    border-bottom: 2px solid transparent;
    border-image: linear-gradient(to right, transparent, rgba(102, 126, 234, 0.2), transparent) 1;
}

.compact-input-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;
    background: linear-gradient(135deg, #f8f9ff, #f0f2ff);
    border: 2px solid rgba(102, 126, 234, 0.2);
    border-radius: 28px;
    padding: 8px 8px 8px 18px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.compact-input-wrapper:focus-within {
    background: white;
    border-color: #667eea;
    box-shadow: 
        0 4px 16px rgba(102, 126, 234, 0.15),
        0 0 0 3px rgba(102, 126, 234, 0.1);
    transform: translateY(-1px);
}

.compact-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
    border: 2px solid white;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.compact-input {
    flex: 1;
    border: none;
    background: transparent;
    font-size: 0.95rem;
    outline: none;
    color: #374151;
    min-width: 0;
}

.compact-input::placeholder {
    color: #9ca3af;
}

.btn-compact-publish {
    padding: 8px 20px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: 0.9rem;
    font-weight: 600;
    white-space: nowrap;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 3px 8px rgba(102, 126, 234, 0.3);
}

.btn-compact-publish:disabled {
    background: linear-gradient(135deg, #d1d5db, #e5e7eb);
    cursor: not-allowed;
    box-shadow: none;
}

.btn-compact-publish:hover:not(:disabled) {
    background: linear-gradient(135deg, #5a6fd6, #6a4192);
    transform: translateY(-2px);
    box-shadow: 0 5px 12px rgba(102, 126, 234, 0.4);
}

.btn-compact-publish:active:not(:disabled) {
    transform: translateY(0);
}

.replying-banner {
    margin-top: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 0.85rem;
    color: #667eea;
    background: linear-gradient(135deg, #f0f2ff, #e8ebff);
    padding: 6px 14px;
    border-radius: 8px;
    width: fit-content;
    border: 1px solid rgba(102, 126, 234, 0.2);
    box-shadow: 0 2px 6px rgba(102, 126, 234, 0.1);
}

.btn-cancel-reply {
    background: none;
    border: none;
    color: #6b7280;
    font-size: 1.3rem;
    line-height: 1;
    cursor: pointer;
    padding: 0 6px;
    margin-left: 8px;
    transition: all 0.2s;
    border-radius: 50%;
}

.btn-cancel-reply:hover {
    color: #ef4444;
    background: rgba(239, 68, 68, 0.1);
}

/* ================= 固定底部输入框样式 ================= */
.fixed-comment-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #ffffff;
    border-top: 1px solid #e5e7eb;
    /* 加深阴影，让浮动感更强 */
    box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.08);
    z-index: 9999;

    /* 【修改】增加底部内边距，适配全面屏手机，并让输入框离底边远一点 */
    padding: 12px 0;
    padding-bottom: calc(12px + env(safe-area-inset-bottom, 0px));

    will-change: transform;
}

.fixed-comment-inner {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
    width: 100%;
    box-sizing: border-box;
}

.fixed-wrapper {
    background-color: #f3f4f6;
    /* 固定位稍微深一点背景，区分层级 */
}

.fixed-banner {
    width: 100%;
    box-sizing: border-box;
    margin-top: 6px;
}

/* 动画效果 */
.slide-up-enter-active,
.slide-up-leave-active {
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-up-enter-from,
.slide-up-leave-to {
    transform: translateY(100%);
}

/* ================= 评论项样式 ================= */
.comment-item {
    margin-bottom: 28px;
    padding: 20px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.7), rgba(250, 248, 245, 0.7));
    border-radius: 16px;
    border: 1px solid rgba(255, 183, 77, 0.15);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.comment-item:hover {
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(250, 248, 245, 0.9));
    transform: translateY(-2px);
    box-shadow: 
        0 6px 16px rgba(0, 0, 0, 0.06),
        0 2px 8px rgba(0, 0, 0, 0.04);
    border-color: rgba(255, 183, 77, 0.3);
}

.comment-item:last-child {
    border-bottom: none;
}

.comment-header {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 12px;
}

.comment-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid white;
    box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.comment-avatar:hover {
    transform: scale(1.1) rotate(5deg);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.comment-user-info {
    display: flex;
    flex-direction: column;
}

.nickname-row {
    display: flex;
    align-items: center;
    gap: 8px;
}

.nickname {
    font-weight: 600;
    color: #374151;
    font-size: 0.95rem;
}

.author-badge {
    background: linear-gradient(135deg, #ff4d4f, #ff6b6b);
    color: #ffffff;
    font-size: 0.7rem;
    padding: 3px 8px;
    border-radius: 12px;
    font-weight: bold;
    border: 1px solid #ff4d4f;
    box-shadow: 0 2px 6px rgba(255, 77, 79, 0.3);
    white-space: nowrap;
}

.author-badge.small {
    font-size: 0.65rem;
    padding: 2px 6px;
}

.comment-content {
    font-size: 1rem;
    line-height: 1.7;
    color: #374151;
    margin-bottom: 12px;
    word-break: break-word;
    padding-left: 4px;
}

.comment-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 0.85rem;
    color: #9ca3af;
    padding-top: 8px;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
}

.time-text {
    font-size: 0.8rem;
    color: #9ca3af;
}

.action-group {
    display: flex;
    align-items: center;
    gap: 18px;
}

.action-btn {
    display: flex;
    align-items: center;
    gap: 5px;
    background: none;
    border: none;
    cursor: pointer;
    color: #9ca3af;
    font-size: 0.85rem;
    padding: 6px 10px;
    border-radius: 12px;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn svg {
    width: 16px;
    height: 16px;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn:hover {
    background: rgba(0, 0, 0, 0.04);
    color: #4b5563;
}

.action-btn:hover svg {
    transform: scale(1.15);
}

.like-action.active {
    color: #ff6b6b;
    background: rgba(255, 107, 107, 0.08);
}

.like-action.active:hover {
    background: rgba(255, 107, 107, 0.12);
}

.view-replies-trigger {
    margin-top: 12px;
    margin-bottom: 12px;
}

.btn-view-replies {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
    border: 1px solid rgba(102, 126, 234, 0.2);
    color: #667eea;
    font-size: 0.85rem;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 14px;
    border-radius: 12px;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-view-replies:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
    color: #764ba2;
    border-color: rgba(102, 126, 234, 0.4);
    transform: translateX(4px);
}

.btn-view-replies svg {
    transition: transform 0.25s;
}

.btn-view-replies:hover svg {
    transform: translateX(3px);
}

.replies-section {
    margin-top: 16px;
    padding-left: 52px;
}

.reply-item {
    margin-bottom: 18px;
    background: linear-gradient(135deg, rgba(240, 242, 255, 0.5), rgba(232, 235, 255, 0.5));
    padding: 14px;
    border-radius: 12px;
    border: 1px solid rgba(102, 126, 234, 0.15);
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.reply-item:hover {
    background: linear-gradient(135deg, rgba(240, 242, 255, 0.7), rgba(232, 235, 255, 0.7));
    transform: translateX(4px);
    box-shadow: 0 3px 10px rgba(102, 126, 234, 0.1);
}

.reply-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 8px;
}

.reply-avatar {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid white;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.reply-meta {
    display: flex;
    align-items: baseline;
    gap: 8px;
    font-size: 0.85rem;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.reply-nickname {
    font-weight: 600;
    color: #374151;
    max-width: 100px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.reply-to-text {
    color: #6b7280;
    font-size: 0.8rem;
    max-width: 100px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.reply-target-name {
    color: #667eea;
    font-weight: 500;
    max-width: 100px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.reply-content {
    font-size: 0.9rem;
    color: #374151;
    line-height: 1.6;
    margin-bottom: 8px;
    word-break: break-word;
}

.reply-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 0.8rem;
    color: #9ca3af;
    padding-top: 6px;
    border-top: 1px solid rgba(0, 0, 0, 0.03);
}

.reply-footer .action-btn svg {
    width: 14px;
    height: 14px;
}

.reply-footer .action-btn {
    padding: 4px 8px;
    font-size: 0.8rem;
}

.load-status-container {
    min-height: 60px;
    /* 确保容器有高度，防止哨兵紧贴上一个元素 */
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.status-text {
    font-size: 0.85rem;
    color: #9ca3af;
    display: flex;
    align-items: center;
    gap: 8px;
}

.status-text.end {
    color: #d1d5db;
    font-style: italic;
}

.sentinel {
    height: 1px;
    width: 100%;
    visibility: hidden;
    /* 必须是 hidden，不能是 display: none */
    pointer-events: none;
    position: relative;
    /* 确保它在文档流中占据位置 */
}

.loading-container-small {
    padding: 12px 0;
    display: flex;
    justify-content: center;
}

.spinner-small {
    width: 20px;
    height: 20px;
    border: 2px solid rgba(102, 126, 234, 0.2);
    border-top-color: #667eea;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

/* ================= 响应式适配 ================= */
@media screen and (max-width: 768px) {
    .navbar-content {
        height: 56px;
        padding: 0 16px;
    }

    .brand-name {
        display: none;
    }

    .user-nickname {
        display: none;
    }

    .user-profile {
        gap: 0;
    }

    .main-content-wrapper {
        padding-top: 10px;
    }

    .detail-container {
        padding: 0 12px;
    }

    .post-article {
        padding: 20px;
        border-radius: 16px;
    }

    .post-title {
        font-size: 1.6rem;
    }

    .post-header::after {
        font-size: 18px;
    }

    .post-author-avatar {
        width: 44px;
        height: 44px;
    }

    .post-menu-btn {
        width: 32px;
        height: 32px;
    }

    .post-menu-dropdown {
        min-width: 120px;
    }

    .menu-item {
        padding: 8px 10px;
        font-size: 0.85rem;
    }

    .menu-item svg {
        width: 16px;
        height: 16px;
    }

    .replies-section {
        padding-left: 28px;
    }

    .comments-section {
        padding: 20px;
        border-radius: 16px;
    }

    .comments-section::before {
        font-size: 24px;
        top: -14px;
        left: 20px;
    }

    .fixed-comment-inner {
        padding: 0 16px;
    }

    .post-detail-page {
        padding-bottom: 70px;
    }

    .compact-input-wrapper {
        padding: 6px 6px 6px 14px;
    }

    .btn-compact-publish {
        padding: 7px 14px;
        font-size: 0.85rem;
    }

    .detail-img {
        max-height: 300px;
        width: 100%;
        height: auto;
    }

    .post-images-carousel {
        border-radius: 12px;
    }

    .carousel-btn {
        width: 36px;
        height: 36px;
        opacity: 0;
    }

    .carousel-btn.visible {
        opacity: 0.85;
    }

    .carousel-btn svg {
        width: 18px;
        height: 18px;
    }

    .carousel-prev {
        left: 10px;
    }

    .carousel-next {
        right: 10px;
    }

    .carousel-indicators-outside {
        gap: 8px;
        padding: 8px 0 0 0;
    }

    .indicator-dot-outside {
        width: 8px;
        height: 8px;
    }

    .comment-item {
        padding: 16px;
        margin-bottom: 20px;
    }

    .comment-avatar {
        width: 36px;
        height: 36px;
    }

    .reply-item {
        padding: 12px;
    }
}

@media screen and (max-width: 480px) {
    .detail-img {
        max-height: 250px;
    }

    .post-title {
        font-size: 1.4rem;
    }

    .post-article {
        padding: 16px;
    }

    .comments-section {
        padding: 16px;
    }

    .comments-section h3 {
        font-size: 1.1rem;
    }

    .carousel-btn {
        width: 32px;
        height: 32px;
        opacity: 0;
    }

    .carousel-btn.visible {
        opacity: 0.9;
    }

    .carousel-btn svg {
        width: 16px;
        height: 16px;
    }

    .carousel-prev {
        left: 8px;
    }

    .carousel-next {
        right: 8px;
    }

    .carousel-indicators-outside {
        gap: 6px;
        padding: 6px 0 0 0;
    }

    .indicator-dot-outside {
        width: 7px;
        height: 7px;
    }

    .comment-item {
        padding: 14px;
    }

    .comment-content {
        font-size: 0.95rem;
    }

    .reply-item {
        padding: 10px;
    }

    .btn-compact-publish {
        padding: 6px 12px;
        font-size: 0.8rem;
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

/* actions 区域样式 */
.actions {
    display: flex;
    align-items: center;
    gap: 12px;
}
</style>