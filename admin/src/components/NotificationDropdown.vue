<template>
    <!-- 添加 @mouseenter 和 @mouseleave 事件 -->
    <div class="notification-wrapper">

        <!-- 触发按钮 -->
        <div class="trigger-btn" @click="togglePanel" @mouseenter="!isMobile() && handleTriggerEnter" @mouseleave="!isMobile() && handleTriggerLeave"
            :class="{ 'active': visible }">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="bell-icon">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
            </svg>
            <span v-if="totalUnread > 0" class="badge-count">{{ totalUnread > 99 ? '99+' : totalUnread }}</span>
        </div>

        <!-- 下拉面板 -->
        <transition name="fade-scale">
            <div v-show="visible" class="dropdown-panel" :class="{ 'expanded': selectedTab }" @mouseenter="!isMobile() && handlePanelEnter" @mouseleave="!isMobile() && handlePanelLeave"
                @click.stop>

                <!-- 左侧导航栏 -->
                <div class="sidebar">
                    <div class="sidebar-header">
                        <span>消息中心</span>
                        <svg v-if="selectedTab" class="close-btn" @click.stop="closePanel" viewBox="0 0 24 24" fill="none"
                            stroke="currentColor" stroke-width="2">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                    </div>
                    <div class="nav-list">
                        <!-- 互动消息 Tab -->
                        <div class="nav-item" :class="{ active: activeTab === 'interaction' }"
                            @click="switchTab('interaction')">
                            <span class="nav-icon">💬</span>
                            <div class="nav-content">
                                <div class="nav-top">
                                    <span class="nav-label">互动消息</span>
                                    <span v-if="unreadCounts.interaction > 0" class="nav-badge">{{ unreadCounts.interaction }}</span>
                                </div>
                                <div class="nav-preview">{{ latestInteractionPreview }}</div>
                            </div>
                        </div>

                        <!-- 好友聊天 Tab (分组标题) -->
                        <div class="nav-group-title">好友聊天</div>

                        <!-- 好友列表 -->
                        <div v-for="friend in friendList" :key="friend.id" class="nav-item friend-item"
                            :class="{ active: activeTab === `friend_${friend.id}` }"
                            @click="switchTab(`friend_${friend.id}`)">
                            <img :src="friend.avatar" class="friend-avatar" />
                            <div class="nav-content">
                                <div class="nav-top">
                                    <span class="nav-label">{{ friend.name }}</span>
                                    <span v-if="friend.unread > 0" class="nav-badge small">{{ friend.unread }}</span>
                                </div>
                                <div class="nav-preview">{{ getFriendLatestPreview(friend.id) }}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 右侧内容区（点击后才显示） -->
                <div v-if="selectedTab" class="main-content">
                    <!-- 顶部标题栏 -->
                    <div class="content-header">
                        <div class="header-left">
                            <svg class="back-btn" @click="backToList" viewBox="0 0 24 24" fill="none"
                                stroke="currentColor" stroke-width="2">
                                <polyline points="15 18 9 12 15 6"></polyline>
                            </svg>
                            <h3>{{ currentTitle }}</h3>
                        </div>
                        <div class="header-actions">
                            <span v-if="currentUnread > 0" class="mark-read-btn"
                                @click="markCurrentAsRead">全部已读</span>
                            <svg class="close-btn-header" @click="closePanel" viewBox="0 0 24 24" fill="none"
                                stroke="currentColor" stroke-width="2">
                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                <line x1="6" y1="6" x2="18" y2="18"></line>
                            </svg>
                        </div>
                    </div>

                    <!-- 内容主体 -->
                    <div class="content-body" ref="scrollContainer">

                        <!-- 互动消息列表 -->
                        <div v-if="activeTab === 'interaction'" class="list-view">
                            <div v-if="interactionList.length === 0" class="empty-state">暂无互动消息</div>
                            <div v-for="item in interactionList" :key="item.id" class="message-item"
                                :class="{ unread: !item.isRead }" @click="handleInteractionClick(item)">
                                <img :src="getAvatarUrl(item.avatar)" class="msg-avatar" 
                                     @error="handleImageError" />
                                <div class="msg-info">
                                    <div class="msg-header">
                                        <span class="msg-name">{{ item.userName || '用户' }}</span>
                                        <span class="msg-time">{{ formatTime(item.createTime) }}</span>
                                    </div>
                                    <div class="msg-text">{{ item.content }}</div>
                                </div>
                                <div v-if="!item.isRead" class="unread-indicator"></div>
                            </div>
                        </div>

                        <!-- 好友聊天界面 -->
                        <div v-else-if="activeTab.startsWith('friend_')" class="chat-view">
                            <div class="chat-messages">
                                <div v-for="msg in currentChatHistory" :key="msg.id" class="chat-bubble"
                                    :class="{ 'self': msg.isSelf }">
                                    <img v-if="!msg.isSelf" :src="currentFriend.avatar" class="bubble-avatar" />
                                    <div class="bubble-content">
                                        <div class="bubble-text">{{ msg.text }}</div>
                                        <div class="bubble-time">{{ formatTime(msg.time) }}</div>
                                    </div>
                                </div>
                            </div>

                            <!-- 聊天输入框 -->
                            <div class="chat-input-area">
                                <input v-model="inputMessage" @keyup.enter="sendMessage" placeholder="输入消息..."
                                    class="chat-input" />
                                <button @click="sendMessage" class="send-btn">发送</button>
                            </div>
                        </div>

                        <!-- 默认空状态 -->
                        <div v-else class="empty-state">请选择一个会话</div>
                    </div>
                </div>
            </div>
        </transition>

        <!-- 全局消息弹窗组件 -->
        <MessageToast ref="toastRef" />
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
// 假设你有这个API，如果没有请根据实际后端接口调整
import { getUnreadCountApi, clearUnreadApi } from '../api/notification'
import { useUserStore } from '../stores/user'
import MessageToast from './MessageToast.vue'

const isMobile = () => {
    return window.innerWidth <= 768
}

const userStore = useUserStore()
const visible = ref(false)
const activeTab = ref('interaction')
const selectedTab = ref(false)
const justClickedTrigger = ref(false)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const getAvatarUrl = (avatar) => {
    if (!avatar) return defaultAvatar
    if (avatar.startsWith('http://') || avatar.startsWith('https://')) return avatar
    return `http://localhost:8080${avatar.startsWith('/') ? '' : '/'}${avatar}`
}

const handleImageError = (e) => {
    e.target.src = defaultAvatar
}

const toastRef = ref(null)
const scrollContainer = ref(null)
const inputMessage = ref('')
const ws = ref(null)
let hideTimer = null
const isInitialConnection = ref(true)

// --- 模拟数据 ---
const friendList = ref([
    // { id: 101, name: '铲屎官小王', avatar: 'https://i.pravatar.cc/150?u=101', unread: 2 },
    // { id: 102, name: '宠物医生李', avatar: 'https://i.pravatar.cc/150?u=102', unread: 0 },
    // { id: 103, name: '隔壁老张', avatar: 'https://i.pravatar.cc/150?u=103', unread: 1 },
])

const interactionList = ref([
    // { id: 1, userName: '爱猫人士', content: '点赞了你的帖子《布偶猫日常》', createTime: Date.now() - 1000 * 60 * 5, isRead: false, avatar: 'https://i.pravatar.cc/150?u=1' },
    // { id: 2, userName: '铲屎官小王', content: '评论了你的帖子：太可爱了！', createTime: Date.now() - 1000 * 60 * 30, isRead: false, avatar: 'https://i.pravatar.cc/150?u=101' },
    // { id: 3, userName: '系统通知', content: '你的认证申请已通过', createTime: Date.now() - 1000 * 60 * 60 * 2, isRead: true, avatar: '' },
])

const chatHistories = ref({
    101: [
        { id: 1, text: '你好呀，你家猫咪真可爱', time: Date.now() - 1000 * 60 * 60, isSelf: false },
        { id: 2, text: '谢谢！它叫咪咪', time: Date.now() - 1000 * 60 * 55, isSelf: true },
        { id: 3, text: '最近有空出来遛弯吗？', time: Date.now() - 1000 * 60 * 5, isSelf: false },
        { id: 4, text: '在吗？', time: Date.now() - 1000 * 60 * 2, isSelf: false },
    ],
    102: [
        { id: 1, text: '您好，请问疫苗什么时候打？', time: Date.now() - 1000 * 60 * 60 * 24, isSelf: true },
    ],
    103: []
})

// --- 计算属性 ---

const currentFriendId = computed(() => {
    if (activeTab.value.startsWith('friend_')) {
        return parseInt(activeTab.value.split('_')[1])
    }
    return null
})

const currentFriend = computed(() => {
    return friendList.value.find(f => f.id === currentFriendId.value)
})

const currentChatHistory = computed(() => {
    if (currentFriendId.value) {
        return chatHistories.value[currentFriendId.value] || []
    }
    return []
})

const currentTitle = computed(() => {
    if (activeTab.value === 'interaction') return '互动通知'
    if (currentFriend.value) return `与 ${currentFriend.value.name} 的聊天`
    return '消息'
})

// 获取互动消息最新预览
const latestInteractionPreview = computed(() => {
    if (interactionList.value.length > 0) {
        const latest = interactionList.value[0]
        return latest.content || '暂无新消息'
    }
    return '暂无新消息'
})

// 获取好友最新一条消息预览
const getFriendLatestPreview = (friendId) => {
    const history = chatHistories.value[friendId]
    if (history && history.length > 0) {
        const latest = history[history.length - 1]
        return `${latest.isSelf ? '我' : '对方'}: ${latest.text}`
    }
    return '暂无聊天记录'
}

const unreadCounts = computed(() => {
    let interaction = interactionList.value.filter(i => !i.isRead).length
    let friends = {}
    friendList.value.forEach(f => {
        friends[f.id] = f.unread
    })
    return {
        interaction,
        friends
    }
})

const totalUnread = computed(() => {
    let total = unreadCounts.value.interaction
    friendList.value.forEach(f => total += f.unread)
    return total
})

const currentUnread = computed(() => {
    if (activeTab.value === 'interaction') return unreadCounts.value.interaction
    if (currentFriend.value) return currentFriend.value.unread
    return 0
})

// --- 方法 ---

const switchTab = async (tab) => {
    const previousTab = activeTab.value
    activeTab.value = tab
    selectedTab.value = true

    await markAsReadByTab(tab)

    if (tab.startsWith('friend_')) {
        const fid = parseInt(tab.split('_')[1])
        const friend = friendList.value.find(f => f.id === fid)
        if (friend) friend.unread = 0
        nextTick(() => scrollToBottom())
    }
}

// 模拟标记已读接口调用
const markAsReadByTab = async (tab) => {
    try {
        // 这里需要根据你的后端API实际情况编写
        await clearUnreadApi()
        // console.log(`标记已读: ${tab}`)

        // 本地乐观更新
        if (tab === 'interaction') {
            interactionList.value.forEach(i => i.isRead = true)
        } else if (tab.startsWith('friend_')) {
            const fid = parseInt(tab.split('_')[1])
            const friend = friendList.value.find(f => f.id === fid)
            if (friend) friend.unread = 0
        }
    } catch (error) {
        console.error("标记已读失败", error)
    }
}

const handleInteractionClick = (item) => {
    item.isRead = true
    // 这里可以添加跳转逻辑，比如跳转到帖子详情
    console.log('点击互动消息', item)
}

const markCurrentAsRead = () => {
    markAsReadByTab(activeTab.value)
}

const sendMessage = () => {
    if (!inputMessage.value.trim() || !currentFriendId.value) return

    const newMsg = {
        id: Date.now(),
        text: inputMessage.value,
        time: Date.now(),
        isSelf: true
    }

    if (!chatHistories.value[currentFriendId.value]) {
        chatHistories.value[currentFriendId.value] = []
    }
    chatHistories.value[currentFriendId.value].push(newMsg)
    inputMessage.value = ''

    nextTick(() => scrollToBottom())
}

const scrollToBottom = () => {
    if (scrollContainer.value) {
        scrollContainer.value.scrollTop = scrollContainer.value.scrollHeight
    }
}

const formatTime = (timestamp) => {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    const now = new Date()
    const diff = now - date
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
    if (diff < 86400000) return `${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`
    return `${date.getMonth() + 1}-${date.getDate()}`
}

// --- WebSocket & 初始化 ---
// (WebSocket 部分保持不变，省略以节省空间，请保留原代码中的 initWebSocket 等逻辑)
const initWebSocket = () => {
    // ... 原有 WebSocket 逻辑 ...
    const userId = userStore.userInfo?.id
    if (!userId) return
    if (ws.value?.readyState === WebSocket.OPEN) return

    // const url = `ws://localhost:8080/ws/${userId}`
    const url = `ws://8.163.60.181:8080/ws/${userId}`
    ws.value = new WebSocket(url)

    ws.value.onopen = () => {
        console.log('WebSocket connected')
        isInitialConnection.value = true
        setTimeout(() => {
            isInitialConnection.value = false
        }, 1000)
    }

    ws.value.onmessage = (event) => {
        try {
            const msg = JSON.parse(event.data)
            if (msg.type === 'notification') {
                const isHistoryMessage = isInitialConnection.value || msg.isHistory
                if (!isHistoryMessage) {
                    if (toastRef.value) {
                        toastRef.value.addToast({
                            id: msg.id,
                            userName: msg.userName || '系统',
                            content: msg.content,
                            createTime: Date.now(),
                            avatar: getAvatarUrl(msg.avatar)
                        })
                    }
                }

                if (msg.category === 'chat') {
                    const fid = msg.senderId
                    const friend = friendList.value.find(f => f.id === fid)
                    if (friend) friend.unread++

                    if (!chatHistories.value[fid]) chatHistories.value[fid] = []
                    chatHistories.value[fid].push({
                        id: msg.id,
                        text: msg.content,
                        time: Date.now(),
                        isSelf: false
                    })

                    if (activeTab.value === `friend_${fid}` && visible.value && selectedTab.value) {
                        nextTick(() => scrollToBottom())
                    }
                } else {
                    interactionList.value.unshift({
                        id: msg.id,
                        userName: msg.userName,
                        content: msg.content,
                        createTime: Date.now(),
                        isRead: false,
                        avatar: getAvatarUrl(msg.avatar)
                    })
                }
            }
        } catch (e) {
            console.error(e)
        }
    }

    ws.value.onclose = () => {
        isInitialConnection.value = true
        setTimeout(initWebSocket, 3000)
    }
}

const handleTriggerEnter = () => {
    if (hideTimer) clearTimeout(hideTimer)
    if (!visible.value) {
        visible.value = true
        selectedTab.value = false
    }
}

const togglePanel = () => {
    if (visible.value) {
        visible.value = false
        selectedTab.value = false
    } else {
        justClickedTrigger.value = true
        visible.value = true
        selectedTab.value = false
    }
}

const handleTriggerLeave = () => {
    if (!selectedTab.value && !justClickedTrigger.value) {
        hideTimer = setTimeout(() => {
            visible.value = false
        }, 150)
    }
    justClickedTrigger.value = false
}

const handlePanelEnter = () => {
    if (hideTimer) clearTimeout(hideTimer)
}

const handlePanelLeave = () => {
    if (!selectedTab.value) {
        hideTimer = setTimeout(() => {
            visible.value = false
        }, 150)
    }
}

const backToList = () => {
    selectedTab.value = false
}

const closePanel = () => {
    visible.value = false
    selectedTab.value = false
}

const handleClickOutside = (event) => {
    const wrapper = document.querySelector('.notification-wrapper')
    if (wrapper && !wrapper.contains(event.target)) {
        if (!selectedTab.value) {
            hideTimer = setTimeout(() => {
                visible.value = false
            }, 150)
        }
    }
}

onMounted(() => {
    initWebSocket()
    document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
    if (ws.value) ws.value.close()
    if (hideTimer) clearTimeout(hideTimer)
    document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.notification-wrapper {
    position: relative;
}

.trigger-btn {
    position: relative;
    padding: 10px;
    cursor: pointer;
    border-radius: 14px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    color: #64748b;
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(102, 126, 234, 0.08);
}

.trigger-btn::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 14px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
    opacity: 0;
    transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.trigger-btn:hover::before,
.trigger-btn.active::before {
    opacity: 1;
}

.trigger-btn:hover,
.trigger-btn.active {
    color: #667eea;
    transform: translateY(-2px);
    box-shadow: 
        0 8px 24px rgba(102, 126, 234, 0.15),
        0 4px 12px rgba(102, 126, 234, 0.1);
}

.bell-icon {
    width: 22px;
    height: 22px;
    position: relative;
    z-index: 1;
    animation: bell-swing 2s ease-in-out infinite;
}

@keyframes bell-swing {
    0%, 100% { transform: rotate(0deg); }
    10% { transform: rotate(10deg); }
    20% { transform: rotate(-8deg); }
    30% { transform: rotate(6deg); }
    40% { transform: rotate(-4deg); }
    50% { transform: rotate(2deg); }
    60% { transform: rotate(0deg); }
}

.trigger-btn:hover .bell-icon {
    animation: none;
    transform: scale(1.1);
}

.badge-count {
    position: absolute;
    top: -4px;
    right: -4px;
    min-width: 18px;
    height: 18px;
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    color: white;
    font-size: 10px;
    line-height: 18px;
    text-align: center;
    border-radius: 9px;
    padding: 0 5px;
    font-weight: 700;
    box-shadow: 
        0 2px 8px rgba(239, 68, 68, 0.4),
        0 0 0 2px rgba(255, 255, 255, 0.8);
    z-index: 2;
    animation: badge-pulse 2s ease-in-out infinite;
}

@keyframes badge-pulse {
    0%, 100% { transform: scale(1); }
    50% { transform: scale(1.05); }
}

.dropdown-panel {
    position: absolute;
    top: calc(100% + 12px);
    right: -200px;
    height: 520px;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border-radius: 20px;
    box-shadow: 
        0 24px 64px rgba(0, 0, 0, 0.12),
        0 0 0 1px rgba(102, 126, 234, 0.08),
        0 8px 24px rgba(102, 126, 234, 0.08);
    z-index: 1100;
    display: flex;
    overflow: hidden;
    transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
    width: 260px;
}

.dropdown-panel.expanded {
    width: 720px;
}

.sidebar {
    width: 260px;
    background: linear-gradient(180deg, #f9fafb 0%, #ffffff 100%);
    border-right: 1px solid rgba(102, 126, 234, 0.08);
    display: flex;
    flex-direction: column;
}

.sidebar-header {
    padding: 20px 24px;
    font-size: 18px;
    font-weight: 700;
    color: #1f2937;
    border-bottom: 1px solid rgba(102, 126, 234, 0.08);
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.04) 0%, rgba(118, 75, 162, 0.04) 100%);
}

.close-btn {
    width: 20px;
    height: 20px;
    cursor: pointer;
    color: #9ca3af;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    border-radius: 6px;
    padding: 2px;
}

.close-btn:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.08);
    transform: rotate(90deg);
}

.nav-list {
    flex: 1;
    overflow-y: auto;
    padding: 12px 0;
}

.nav-list::-webkit-scrollbar {
    width: 6px;
}

.nav-list::-webkit-scrollbar-track {
    background: transparent;
}

.nav-list::-webkit-scrollbar-thumb {
    background: rgba(102, 126, 234, 0.2);
    border-radius: 3px;
    transition: background 0.2s;
}

.nav-list::-webkit-scrollbar-thumb:hover {
    background: rgba(102, 126, 234, 0.4);
}

.nav-group-title {
    padding: 12px 24px 8px;
    font-size: 11px;
    font-weight: 700;
    color: #9ca3af;
    text-transform: uppercase;
    letter-spacing: 0.08em;
}

.nav-item {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    margin: 4px 12px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    color: #4b5563;
    gap: 12px;
    border-radius: 12px;
    position: relative;
    overflow: hidden;
}

.nav-item::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
    opacity: 0;
    transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-item:hover::before {
    opacity: 1;
}

.nav-item:hover {
    transform: translateX(4px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.nav-item.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    font-weight: 600;
    box-shadow: 
        0 4px 16px rgba(102, 126, 234, 0.3),
        0 2px 8px rgba(102, 126, 234, 0.2);
}

.nav-item.active::before {
    display: none;
}

.nav-icon {
    font-size: 20px;
    position: relative;
    z-index: 1;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.12) 0%, rgba(118, 75, 162, 0.12) 100%);
    border-radius: 10px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-item.active .nav-icon {
    background: rgba(255, 255, 255, 0.2);
    transform: scale(1.08);
}

.friend-item .friend-avatar {
    width: 36px;
    height: 36px;
    border-radius: 10px;
    object-fit: cover;
    position: relative;
    z-index: 1;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.nav-item.active .friend-avatar {
    transform: scale(1.08);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.nav-content {
    flex: 1;
    min-width: 0;
    position: relative;
    z-index: 1;
}

.nav-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4px;
}

.nav-label {
    font-size: 14px;
    font-weight: 600;
    transition: color 0.3s;
}

.nav-item.active .nav-label {
    color: #ffffff;
}

.nav-badge {
    padding: 2px 8px;
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    color: white;
    font-size: 11px;
    font-weight: 700;
    border-radius: 10px;
    min-width: 20px;
    text-align: center;
    box-shadow: 0 2px 6px rgba(239, 68, 68, 0.3);
}

.nav-badge.small {
    padding: 2px 6px;
    font-size: 10px;
    min-width: 18px;
}

.nav-item.active .nav-badge {
    background: rgba(255, 255, 255, 0.25);
    box-shadow: none;
}

.nav-preview {
    font-size: 12px;
    color: #9ca3af;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    transition: color 0.3s;
}

.nav-item.active .nav-preview {
    color: rgba(255, 255, 255, 0.8);
}

.main-content {
    flex: 1;
    display: none;
    flex-direction: column;
    background: #ffffff;
    min-width: 0;
}

.dropdown-panel.expanded .main-content {
    display: flex;
}

.content-header {
    padding: 20px 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid rgba(102, 126, 234, 0.08);
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(118, 75, 162, 0.02) 100%);
}

.header-left {
    display: flex;
    align-items: center;
    gap: 12px;
}

.back-btn {
    width: 20px;
    height: 20px;
    cursor: pointer;
    color: #6b7280;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    border-radius: 6px;
    padding: 2px;
}

.back-btn:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.08);
    transform: translateX(-2px);
}

.header-actions {
    display: flex;
    align-items: center;
    gap: 12px;
}

.close-btn-header {
    width: 20px;
    height: 20px;
    cursor: pointer;
    color: #9ca3af;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    border-radius: 6px;
    padding: 2px;
}

.close-btn-header:hover {
    color: #667eea;
    background: rgba(102, 126, 234, 0.08);
    transform: rotate(90deg);
}

.content-header h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 700;
    color: #1f2937;
}

.mark-read-btn {
    font-size: 13px;
    color: #667eea;
    cursor: pointer;
    font-weight: 600;
    padding: 6px 12px;
    border-radius: 8px;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.mark-read-btn:hover {
    background: rgba(102, 126, 234, 0.08);
    color: #764ba2;
}

.content-body {
    flex: 1;
    overflow-y: auto;
    padding: 0;
}

.content-body::-webkit-scrollbar {
    width: 6px;
}

.content-body::-webkit-scrollbar-track {
    background: transparent;
}

.content-body::-webkit-scrollbar-thumb {
    background: rgba(102, 126, 234, 0.2);
    border-radius: 3px;
    transition: background 0.2s;
}

.content-body::-webkit-scrollbar-thumb:hover {
    background: rgba(102, 126, 234, 0.4);
}

.list-view {
    padding: 12px 0;
}

.message-item {
    display: flex;
    padding: 14px 20px;
    margin: 4px 12px;
    gap: 14px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border-radius: 12px;
    position: relative;
}

.message-item::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.04) 0%, rgba(118, 75, 162, 0.04) 100%);
    opacity: 0;
    transition: opacity 0.3s;
    border-radius: 12px;
}

.message-item:hover::before {
    opacity: 1;
}

.message-item:hover {
    transform: translateX(4px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.08);
}

.message-item.unread {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.06) 0%, rgba(118, 75, 162, 0.06) 100%);
    border-left: 3px solid #667eea;
}

.message-item.unread::before {
    display: none;
}

.msg-avatar {
    width: 42px;
    height: 42px;
    border-radius: 12px;
    object-fit: cover;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-item:hover .msg-avatar {
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.msg-info {
    flex: 1;
    overflow: hidden;
}

.msg-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 6px;
}

.msg-name {
    font-weight: 600;
    font-size: 14px;
    color: #1f2937;
}

.msg-time {
    font-size: 12px;
    color: #9ca3af;
    font-weight: 500;
}

.msg-text {
    font-size: 13px;
    color: #6b7280;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.4;
}

.unread-indicator {
    width: 10px;
    height: 10px;
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    border-radius: 50%;
    align-self: center;
    box-shadow: 0 2px 6px rgba(239, 68, 68, 0.4);
    animation: unread-pulse 2s ease-in-out infinite;
}

@keyframes unread-pulse {
    0%, 100% { transform: scale(1); opacity: 1; }
    50% { transform: scale(1.1); opacity: 0.8; }
}

.chat-view {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.chat-messages {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    background: linear-gradient(180deg, #ffffff 0%, #f9fafb 100%);
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.chat-messages::-webkit-scrollbar {
    width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
    background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
    background: rgba(102, 126, 234, 0.2);
    border-radius: 3px;
}

.chat-bubble {
    display: flex;
    align-items: flex-end;
    gap: 10px;
    animation: bubble-fade-in 0.3s ease-out;
}

@keyframes bubble-fade-in {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.chat-bubble.self {
    flex-direction: row-reverse;
}

.bubble-avatar {
    width: 34px;
    height: 34px;
    border-radius: 10px;
    object-fit: cover;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.bubble-content {
    max-width: 70%;
    display: flex;
    flex-direction: column;
}

.bubble-text {
    padding: 12px 16px;
    border-radius: 16px;
    font-size: 14px;
    line-height: 1.5;
    word-break: break-word;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.chat-bubble:not(.self) .bubble-text {
    background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
    color: #1f2937;
    border-bottom-left-radius: 4px;
}

.chat-bubble.self .bubble-text {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-bottom-right-radius: 4px;
    box-shadow: 
        0 4px 12px rgba(102, 126, 234, 0.3),
        0 2px 6px rgba(102, 126, 234, 0.2);
}

.bubble-time {
    font-size: 11px;
    color: #9ca3af;
    margin-top: 6px;
    padding: 0 4px;
    font-weight: 500;
}

.chat-bubble.self .bubble-time {
    text-align: right;
}

.chat-input-area {
    padding: 16px 20px;
    border-top: 1px solid rgba(102, 126, 234, 0.08);
    display: flex;
    gap: 12px;
    background: linear-gradient(180deg, #ffffff 0%, #f9fafb 100%);
}

.chat-input {
    flex: 1;
    padding: 12px 18px;
    border: 2px solid #e5e7eb;
    border-radius: 24px;
    outline: none;
    font-size: 14px;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    background: #ffffff;
    font-weight: 500;
}

.chat-input:focus {
    border-color: #667eea;
    background: #ffffff;
    box-shadow: 
        0 0 0 4px rgba(102, 126, 234, 0.1),
        0 2px 8px rgba(102, 126, 234, 0.08);
}

.chat-input::placeholder {
    color: #9ca3af;
    font-weight: 400;
}

.send-btn {
    padding: 0 24px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 24px;
    cursor: pointer;
    font-weight: 600;
    font-size: 14px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.send-btn:hover {
    transform: translateY(-2px);
    box-shadow: 
        0 6px 16px rgba(102, 126, 234, 0.4),
        0 3px 8px rgba(102, 126, 234, 0.3);
}

.send-btn:active {
    transform: translateY(0);
}

.empty-state {
    padding: 60px 20px;
    text-align: center;
    color: #9ca3af;
    font-size: 14px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
}

.empty-state::before {
    content: '🔔';
    font-size: 48px;
    opacity: 0.5;
}

.fade-scale-enter-active,
.fade-scale-leave-active {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-scale-enter-from {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
}

.fade-scale-leave-to {
    opacity: 0;
    transform: scale(0.95) translateY(-10px);
}

@media screen and (max-width: 768px) {
    .dropdown-panel {
        right: -12px;
        height: 480px;
    }

    .dropdown-panel.expanded {
        width: calc(100vw - 24px);
    }

    .sidebar {
        width: 100%;
    }

    .main-content {
        position: absolute;
        inset: 0;
        z-index: 10;
        background: white;
    }
}
</style>
