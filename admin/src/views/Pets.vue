<template>
    <div class="pets-page">
        <!-- 页面标题 -->
        <div class="page-header">
            <div class="header-content">
                <div class="header-text">
                    <h1 class="page-title">我的宠物</h1>
                    <p class="page-subtitle">看看你家毛孩子的可爱瞬间</p>
                </div>
                <button v-if="isLoggedIn" class="btn-add-pet" @click="openAddForm">
                    <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="5" x2="12" y2="19"></line>
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                    </svg>
                    <span class="btn-text">添加宠物</span>
                </button>
            </div>
        </div>

        <!-- 宠物列表容器 -->
        <div class="pets-container">

            <!-- 1. 未登录状态 -->
            <div v-if="!isLoggedIn" class="unauthorized-state">
                <svg class="lock-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
                <h3>请先登录</h3>
                <p>登录后即可查看和管理您的宠物信息</p>
                <button @click="handleGoLogin" class="btn-login-trigger">
                    去登录
                </button>
            </div>

            <!-- 2. 加载状态 (仅登录后显示) -->
            <div v-else-if="loading && pets.length === 0" class="loading-state">
                <div class="spinner"></div>
                <p>正在加载你的小宝贝...</p>
            </div>

            <!-- 3. 错误状态 (仅登录后显示) -->
            <div v-else-if="error" class="error-state">
                <p>{{ error }}</p>
                <button @click="fetchPets()" class="btn-retry">重试</button>
            </div>

            <!-- 4. 空状态 (仅登录后显示) -->
            <div v-else-if="!loading && pets.length === 0" class="empty-state">
                <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path
                        d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z">
                    </path>
                </svg>
                <p>还没有添加宠物呢，快去添加吧！</p>
            </div>

            <!-- 5. 宠物卡片列表 (仅登录后且有数据时显示) -->
            <div v-else class="pets-grid">
                <article v-for="pet in pets" :key="pet.id" class="pet-card" @click="openPetDetail(pet)">
                    <!-- 封面图 (头像) -->
                    <div class="card-image-wrapper">
                        <img :src="pet.avatar" alt="宠物头像" class="card-img" loading="lazy" />
                        <!-- 宠物类型标签 -->
                        <span class="type-tag" :class="getTypeClass(pet.type)">
                            {{ pet.type || '未知' }}
                        </span>
                    </div>

                    <!-- 卡片内容区 -->
                    <div class="card-body">
                        <div class="card-header">
                            <h3 class="card-title">{{ pet.name || '未命名' }}</h3>
                            <span class="age-text">{{ pet.age }}岁</span>
                        </div>

                        <!-- 生日信息 -->
                        <div class="birthday-info">
                            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path
                                    d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z">
                                </path>
                            </svg>
                            <span class="info-text">生日: {{ pet.birthday }}</span>
                        </div>

                        <!-- 描述 -->
                        <p class="card-excerpt">{{ pet.info || '一只可爱的小伙伴' }}</p>

                        <!-- 底部操作栏 -->
                        <div class="card-footer">
                            <span class="time-text">{{ formatTime(pet.createTime) }}</span>
                            <div class="card-actions">
                                <button class="action-btn btn-edit" @click.stop="openEditForm(pet)">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                                    </svg>
                                </button>
                                <button class="action-btn btn-delete" @click.stop="handleDelete(pet)">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </div>
                </article>
            </div>

            <!-- 加载更多 (仅登录后显示) -->
            <div v-if="isLoggedIn && hasMore && !loading" class="load-more-container">
                <button @click="loadMorePets()" class="btn-load-more" :disabled="loading">
                    {{ loading ? '加载中...' : '加载更多' }}
                </button>
            </div>

            <div v-if="isLoggedIn && !hasMore && pets.length > 0" class="no-more">
                - 已经到底啦 -
            </div>
        </div>

        <!-- 宠物表单弹窗 -->
        <PetForm
            :visible="showForm"
            :pet-data="editingPet"
            @close="closeForm"
            @success="handleFormSuccess"
        />

        <!-- 删除确认框 -->
        <ConfirmDialog
            :visible="showDeleteConfirm"
            :title="deleteConfirmTitle"
            :message="deleteConfirmMessage"
            type="danger"
            @confirm="confirmDelete"
            @cancel="cancelDelete"
        />

        <!-- 宠物详情弹窗 -->
        <Teleport to="body">
            <Transition name="detail-modal">
                <div v-if="showDetail" class="pet-detail-overlay" @click.self="closeDetail" @keydown.escape="closeDetail">
                    <div class="pet-detail-modal" ref="detailModalRef">
                        <!-- 关闭按钮 -->
                        <button class="detail-close-btn" @click="closeDetail">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="18" y1="6" x2="6" y2="18"></line>
                                <line x1="6" y1="6" x2="18" y2="18"></line>
                            </svg>
                        </button>

                        <!-- 详情内容 -->
                        <div class="detail-content">
                            <!-- 大图 -->
                            <div class="detail-image-wrapper">
                                <img :src="selectedPet.avatar" alt="宠物头像" class="detail-img" />
                                <span class="detail-type-tag" :class="getTypeClass(selectedPet.type)">
                                    {{ selectedPet.type || '未知' }}
                                </span>
                            </div>

                            <!-- 信息区 -->
                            <div class="detail-body">
                                <div class="detail-header">
                                    <h2 class="detail-title">{{ selectedPet.name || '未命名' }}</h2>
                                    <span class="detail-age">{{ selectedPet.age }}岁</span>
                                </div>

                                <div class="detail-info-grid">
                                    <div class="info-item">
                                        <svg class="info-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                                        </svg>
                                        <div class="info-content">
                                            <span class="info-label">生日</span>
                                            <span class="info-value">{{ selectedPet.birthday }}</span>
                                        </div>
                                    </div>

                                    <div class="info-item">
                                        <svg class="info-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"></path>
                                        </svg>
                                        <div class="info-content">
                                            <span class="info-label">类型</span>
                                            <span class="info-value">{{ selectedPet.type || '未知' }}</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="detail-description">
                                    <h4 class="desc-title">简介</h4>
                                    <p class="desc-text">{{ selectedPet.info || '一只可爱的小伙伴' }}</p>
                                </div>

                                <div class="detail-footer">
                                    <span class="detail-time">添加于 {{ formatTime(selectedPet.createTime) }}</span>
                                    <div class="detail-actions">
                                        <button class="detail-btn btn-edit-detail" @click.stop="openEditForm(selectedPet); closeDetail();">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                <path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                                            </svg>
                                            编辑
                                        </button>
                                        <button class="detail-btn btn-delete-detail" @click.stop="handleDelete(selectedPet); closeDetail();">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                            </svg>
                                            删除
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </Transition>
        </Teleport>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useUserStore } from '../stores/user'
import { useAppStore } from '../stores/app.js'
import { getPetListApi, deletePetApi } from '../api/pet.js'
import PetForm from '../components/PetForm.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'

const userStore = useUserStore()
const appStore = useAppStore()

const isLoggedIn = computed(() => {
    return !!userStore.token || !!userStore.userInfo
})

const loading = ref(false)
const error = ref(null)
const pets = ref([])
const hasMore = ref(true)
const cursorId = ref(null)
const pageSize = ref(10)

const showForm = ref(false)
const editingPet = ref(null)

const showDetail = ref(false)
const selectedPet = ref({})
const detailModalRef = ref(null)

const showDeleteConfirm = ref(false)
const deleteTarget = ref(null)
const deleteConfirmTitle = ref('删除宠物')
const deleteConfirmMessage = ref('')

watch(isLoggedIn, (newVal, oldVal) => {
    if (newVal && !oldVal) {
        console.log('检测到登录成功，正在加载宠物数据...')
        fetchPets(false)
    }
})

const handleGoLogin = () => {
    appStore.openLoginModal() 
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

const fetchPets = async (isLoadMore = false) => {
    if (!isLoggedIn.value) {
        return
    }

    try {
        loading.value = true
        error.value = null

        const params = {
            pageSize: pageSize.value,
        }

        if (isLoadMore && cursorId.value !== null) {
            params.cursorId = cursorId.value
        }

        const data = await getPetListApi(params)
        console.log('Pet list:', data)

        if (data.code === 0) {
            const newList = data.data.list || []

            if (isLoadMore) {
                pets.value.push(...newList)
            } else {
                pets.value = newList
            }

            if (data.data.nextCursorId !== null) {
                cursorId.value = data.data.nextCursorId
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

const loadMorePets = () => {
    fetchPets(true)
}

const openAddForm = () => {
    editingPet.value = null
    showForm.value = true
}

const openEditForm = (pet) => {
    editingPet.value = { ...pet }
    showForm.value = true
}

const openPetDetail = (pet) => {
    selectedPet.value = { ...pet }
    showDetail.value = true
    document.addEventListener('keydown', handleEscKey)
    setTimeout(() => {
        document.addEventListener('click', handleOutsideClick)
    }, 100)
}

const closeDetail = () => {
    showDetail.value = false
    selectedPet.value = {}
    document.removeEventListener('keydown', handleEscKey)
    document.removeEventListener('click', handleOutsideClick)
}

const handleEscKey = (e) => {
    if (e.key === 'Escape') {
        closeDetail()
    }
}

const handleOutsideClick = (e) => {
    if (detailModalRef.value && !detailModalRef.value.contains(e.target)) {
        closeDetail()
    }
}

const closeForm = () => {
    showForm.value = false
    editingPet.value = null
}

const handleFormSuccess = () => {
    fetchPets(false)
}

const handleDelete = (pet) => {
    deleteTarget.value = pet
    deleteConfirmTitle.value = '删除宠物'
    deleteConfirmMessage.value = `确定要删除宠物"${pet.name}"吗？此操作不可撤销。`
    showDeleteConfirm.value = true
}

const confirmDelete = async () => {
    if (!deleteTarget.value) return

    try {
        const result = await deletePetApi(deleteTarget.value.id)
        if (result.code === 0) {
            fetchPets(false)
        } else {
            alert(result.message || '删除失败')
        }
    } catch (error) {
        alert('删除失败：' + error.message)
    } finally {
        showDeleteConfirm.value = false
        deleteTarget.value = null
    }
}

const cancelDelete = () => {
    showDeleteConfirm.value = false
    deleteTarget.value = null
}

const getTypeClass = (type) => {
    const typeLower = (type || '').toLowerCase()
    if (typeLower.includes('狗') || typeLower.includes('犬')) {
        return 'dog'
    } else if (typeLower.includes('猫') || typeLower.includes('咪')) {
        return 'cat'
    } else if (typeLower.includes('兔') || typeLower.includes('兔子')) {
        return 'rabbit'
    } else if (typeLower.includes('仓鼠') || typeLower.includes('鼠')) {
        return 'hamster'
    } else if (typeLower.includes('鸟') || typeLower.includes('鹦鹉') || typeLower.includes('鸽')) {
        return 'bird'
    } else if (typeLower.includes('鱼') || typeLower.includes('金鱼') || typeLower.includes('热带鱼')) {
        return 'fish'
    } else if (typeLower.includes('龟') || typeLower.includes('鳖') || typeLower.includes('乌龟')) {
        return 'turtle'
    } else if (typeLower.includes('蛇') || typeLower.includes('蟒')) {
        return 'snake'
    }
    return 'other'
}

onMounted(() => {
    if (isLoggedIn.value) {
        fetchPets(false)
    }
})
</script>

<style scoped>
.pets-page {
    min-height: 100vh;
    background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 50%, #f8f9ff 100%);
    padding-bottom: 40px;
    /* padding-top: 80px; */
    position: relative;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
    .pets-page {
        padding-top: 70px;
    }
}

@media screen and (max-width: 480px) {
    .pets-page {
        padding-top: 60px;
    }
}

.pets-page::before {
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

/* ================= 页面标题 ================= */
.page-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 40px 20px;
    margin-bottom: 30px;
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.25);
    border-radius: 16px;
    overflow: hidden;
    position: relative;
}

.page-header::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -20%;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    pointer-events: none;
}

.page-header::after {
    content: '';
    position: absolute;
    bottom: -30%;
    left: -10%;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.08);
    border-radius: 50%;
    pointer-events: none;
}

.header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    position: relative;
    z-index: 1;
}

.header-text {
    flex: 1;
}

.page-title {
    font-size: 2rem;
    font-weight: 800;
    margin: 0 0 10px 0;
    display: flex;
    align-items: center;
    gap: 12px;
}

.page-subtitle {
    margin: 0;
    opacity: 0.9;
    font-size: 1rem;
    font-weight: 300;
}

.btn-add-pet {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 24px;
    color: white;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    white-space: nowrap;
}

.btn-add-pet:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-icon {
    width: 20px;
    height: 20px;
}

/* ================= 宠物列表容器 ================= */
.pets-container {
    max-width: 1200px;
    margin: 0 auto;
    padding-left: 20px;
    padding-right: 20px;
    position: relative;
    z-index: 1;
}

/* ================= 未登录状态样式 ================= */
.unauthorized-state {
    text-align: center;
    padding: 80px 20px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    box-shadow: 
        0 8px 24px rgba(0, 0, 0, 0.06),
        0 2px 8px rgba(0, 0, 0, 0.04);
    margin-top: 20px;
    border: 1px solid rgba(255, 255, 255, 0.6);
}

.lock-icon {
    width: 64px;
    height: 64px;
    color: #cbd5e1;
    margin-bottom: 20px;
}

.unauthorized-state h3 {
    font-size: 1.5rem;
    color: #334155;
    margin-bottom: 10px;
}

.unauthorized-state p {
    color: #64748b;
    margin-bottom: 30px;
}

.btn-login-trigger {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border: none;
    padding: 12px 32px;
    border-radius: 24px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-login-trigger:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

/* ================= 宠物卡片网格 ================= */
.pets-grid {
    display: grid;
    gap: 24px;
    grid-template-columns: repeat(3, 1fr);
    transition: all 0.3s ease;
}

/* ================= 宠物卡片样式 ================= */
.pet-card {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 
        0 4px 12px rgba(0, 0, 0, 0.04),
        0 1px 3px rgba(0, 0, 0, 0.03);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    display: flex;
    flex-direction: column;
    border: 1px solid rgba(255, 255, 255, 0.6);
    height: 100%;
}

.pet-card:hover {
    transform: translateY(-6px);
    box-shadow: 
        0 12px 28px rgba(102, 126, 234, 0.12),
        0 4px 12px rgba(0, 0, 0, 0.06);
    border-color: rgba(102, 126, 234, 0.2);
}

.card-image-wrapper {
    position: relative;
    width: 100%;
    padding-top: 65%;
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
    transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.pet-card:hover .card-img {
    transform: scale(1.08);
}

.type-tag {
    position: absolute;
    top: 12px;
    left: 12px;
    padding: 5px 12px;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 600;
    backdrop-filter: blur(8px);
    box-shadow: 0 3px 8px rgba(0, 0, 0, 0.15);
    letter-spacing: 0.02em;
}

.type-tag.dog {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    color: white;
}

.type-tag.cat {
    background: linear-gradient(135deg, #ec4899, #be185d);
    color: white;
}

.type-tag.other {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
}

.type-tag.rabbit {
    background: linear-gradient(135deg, #f472b6, #db2777);
    color: white;
}

.type-tag.hamster {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    color: white;
}

.type-tag.bird {
    background: linear-gradient(135deg, #10b981, #059669);
    color: white;
}

.type-tag.fish {
    background: linear-gradient(135deg, #3b82f6, #1d4ed8);
    color: white;
}

.type-tag.turtle {
    background: linear-gradient(135deg, #84cc16, #4d7c0f);
    color: white;
}

.type-tag.snake {
    background: linear-gradient(135deg, #6b7280, #374151);
    color: white;
}

.card-body {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.card-title {
    font-size: 1.25rem;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0;
    line-height: 1.4;
}

.age-text {
    font-size: 0.85rem;
    color: #667eea;
    font-weight: 600;
    padding: 4px 10px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
    border-radius: 12px;
    border: 1px solid rgba(102, 126, 234, 0.15);
}

.birthday-info {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    padding: 8px 12px;
    background: linear-gradient(135deg, rgba(240, 242, 255, 0.6), rgba(232, 235, 255, 0.6));
    border-radius: 10px;
    border: 1px solid rgba(102, 126, 234, 0.08);
}

.icon {
    width: 16px;
    height: 16px;
    color: #667eea;
}

.info-text {
    font-size: 0.85rem;
    color: #64748b;
    font-weight: 500;
}

.card-excerpt {
    font-size: 0.95rem;
    color: #64748b;
    line-height: 1.6;
    margin: 0 0 16px 0;

    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;

    flex-grow: 1;
}

.card-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: auto;
    padding-top: 10px;
    border-top: 1px solid rgba(102, 126, 234, 0.08);
}

.card-actions {
    display: flex;
    gap: 6px;
}

.action-btn {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
}

.action-btn svg {
    width: 14px;
    height: 14px;
}

.btn-edit {
    background: rgba(102, 126, 234, 0.1);
    color: #667eea;
}

.btn-edit:hover {
    background: rgba(102, 126, 234, 0.2);
    transform: scale(1.1);
}

.btn-delete {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
}

.btn-delete:hover {
    background: rgba(239, 68, 68, 0.2);
    transform: scale(1.1);
}

.time-text {
    font-size: 0.8rem;
    color: #94a3b8;
}

/* ================= 状态样式 ================= */
.loading-state,
.error-state,
.empty-state,
.no-more {
    text-align: center;
    padding: 60px 0;
    color: #64748b;
}

.spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(102, 126, 234, 0.15);
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

.empty-icon {
    width: 64px;
    height: 64px;
    color: #cbd5e1;
    margin-bottom: 16px;
}

.btn-retry,
.btn-load-more {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border: none;
    padding: 10px 28px;
    border-radius: 24px;
    font-size: 0.95rem;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    font-weight: 600;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-retry:hover,
.btn-load-more:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.load-more-container {
    text-align: center;
    margin-top: 40px;
}

.no-more {
    font-size: 0.9rem;
    color: #94a3b8;
    margin-top: 40px;
}

/* ================= 宠物详情弹窗 ================= */
.pet-detail-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.6);
    backdrop-filter: blur(8px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    padding: 20px;
}

.pet-detail-modal {
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    box-shadow: 
        0 20px 60px rgba(0, 0, 0, 0.15),
        0 8px 24px rgba(102, 126, 234, 0.1);
    max-width: 700px;
    width: 100%;
    max-height: 90vh;
    overflow-y: auto;
    position: relative;
    border: 1px solid rgba(255, 255, 255, 0.8);
}

.detail-close-btn {
    position: absolute;
    top: 16px;
    right: 16px;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(102, 126, 234, 0.15);
    color: #667eea;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    z-index: 10;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.detail-close-btn:hover {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
    transform: rotate(90deg) scale(1.1);
    border-color: rgba(239, 68, 68, 0.2);
}

.detail-close-btn svg {
    width: 20px;
    height: 20px;
}

.detail-content {
    display: flex;
    flex-direction: column;
}

.detail-image-wrapper {
    position: relative;
    width: 100%;
    height: 350px;
    background: linear-gradient(135deg, #f8f9ff, #f0f2ff);
    overflow: hidden;
    border-radius: 20px 20px 0 0;
}

.detail-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.detail-type-tag {
    position: absolute;
    top: 20px;
    left: 20px;
    padding: 8px 16px;
    border-radius: 24px;
    font-size: 0.9rem;
    font-weight: 600;
    backdrop-filter: blur(8px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    letter-spacing: 0.02em;
}

.detail-type-tag.dog {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    color: white;
}

.detail-type-tag.cat {
    background: linear-gradient(135deg, #ec4899, #be185d);
    color: white;
}

.detail-type-tag.other {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
}

.detail-type-tag.rabbit {
    background: linear-gradient(135deg, #f472b6, #db2777);
    color: white;
}

.detail-type-tag.hamster {
    background: linear-gradient(135deg, #f59e0b, #d97706);
    color: white;
}

.detail-type-tag.bird {
    background: linear-gradient(135deg, #10b981, #059669);
    color: white;
}

.detail-type-tag.fish {
    background: linear-gradient(135deg, #3b82f6, #1d4ed8);
    color: white;
}

.detail-type-tag.turtle {
    background: linear-gradient(135deg, #84cc16, #4d7c0f);
    color: white;
}

.detail-type-tag.snake {
    background: linear-gradient(135deg, #6b7280, #374151);
    color: white;
}

.detail-body {
    padding: 32px 28px 28px;
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.detail-title {
    font-size: 2rem;
    font-weight: 800;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0;
    line-height: 1.4;
}

.detail-age {
    font-size: 1rem;
    color: #667eea;
    font-weight: 600;
    padding: 6px 14px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
    border-radius: 16px;
    border: 1px solid rgba(102, 126, 234, 0.15);
}

.detail-info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
}

.info-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: linear-gradient(135deg, rgba(240, 242, 255, 0.6), rgba(232, 235, 255, 0.6));
    border-radius: 12px;
    border: 1px solid rgba(102, 126, 234, 0.08);
}

.info-icon {
    width: 24px;
    height: 24px;
    color: #667eea;
    flex-shrink: 0;
}

.info-content {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.info-label {
    font-size: 0.8rem;
    color: #94a3b8;
    font-weight: 500;
}

.info-value {
    font-size: 1rem;
    color: #334155;
    font-weight: 600;
}

.detail-description {
    padding: 20px;
    background: linear-gradient(135deg, rgba(248, 249, 255, 0.8), rgba(240, 242, 255, 0.8));
    border-radius: 12px;
    border: 1px solid rgba(102, 126, 234, 0.08);
}

.desc-title {
    font-size: 1rem;
    font-weight: 700;
    color: #334155;
    margin: 0 0 12px 0;
}

.desc-text {
    font-size: 0.95rem;
    color: #64748b;
    line-height: 1.8;
    margin: 0;
}

.detail-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 20px;
    border-top: 1px solid rgba(102, 126, 234, 0.08);
}

.detail-time {
    font-size: 0.85rem;
    color: #94a3b8;
}

.detail-actions {
    display: flex;
    gap: 12px;
}

.detail-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    border-radius: 20px;
    border: none;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.detail-btn svg {
    width: 18px;
    height: 18px;
}

.btn-edit-detail {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    color: #667eea;
    border: 1px solid rgba(102, 126, 234, 0.2);
}

.btn-edit-detail:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.btn-delete-detail {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(220, 38, 38, 0.1));
    color: #ef4444;
    border: 1px solid rgba(239, 68, 68, 0.2);
}

.btn-delete-detail:hover {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.2), rgba(220, 38, 38, 0.2));
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
}

/* 详情弹窗动画 */
.detail-modal-enter-active,
.detail-modal-leave-active {
    transition: opacity 0.3s ease;
}

.detail-modal-enter-active .pet-detail-modal,
.detail-modal-leave-active .pet-detail-modal {
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.3s ease;
}

.detail-modal-enter-from {
    opacity: 0;
}

.detail-modal-enter-from .pet-detail-modal {
    transform: scale(0.9) translateY(20px);
    opacity: 0;
}

.detail-modal-leave-to {
    opacity: 0;
}

.detail-modal-leave-to .pet-detail-modal {
    transform: scale(0.9) translateY(20px);
    opacity: 0;
}

/* ================= 移动端适配 ================= */
@media screen and (max-width: 768px) {
    .page-header {
        padding: 28px 16px;
        margin-bottom: 20px;
        border-radius: 12px;
    }

    .page-header::before {
        width: 200px;
        height: 200px;
        right: -15%;
    }

    .page-header::after {
        width: 150px;
        height: 150px;
    }

    .page-title {
        font-size: 1.5rem;
    }

    .page-subtitle {
        font-size: 0.9rem;
    }

    .btn-add-pet {
        padding: 10px;
        border-radius: 50%;
    }

    .btn-text {
        display: none;
    }

    .btn-icon {
        width: 22px;
        height: 22px;
    }

    .pets-container {
        padding-left: 12px;
        padding-right: 12px;
    }

    .pets-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;
    }

    .pet-card {
        border-radius: 12px;
    }

    .card-image-wrapper {
        padding-top: 60%;
    }

    .card-body {
        padding: 14px;
    }

    .card-header {
        margin-bottom: 10px;
    }

    .card-title {
        font-size: 1.05rem;
    }

    .age-text {
        font-size: 0.75rem;
        padding: 3px 8px;
    }

    .birthday-info {
        gap: 6px;
        margin-bottom: 10px;
        padding: 6px 10px;
    }

    .icon {
        width: 14px;
        height: 14px;
    }

    .info-text {
        font-size: 0.75rem;
    }

    .card-excerpt {
        font-size: 0.85rem;
        margin-bottom: 12px;
    }

    .card-footer {
        padding-top: 12px;
    }

    .time-text {
        font-size: 0.7rem;
    }

    .unauthorized-state {
        padding: 40px 16px;
        border-radius: 12px;
    }

    .lock-icon {
        width: 48px;
        height: 48px;
    }

    .unauthorized-state h3 {
        font-size: 1.25rem;
    }

    .unauthorized-state p {
        font-size: 0.9rem;
    }

    .btn-login-trigger {
        padding: 10px 24px;
        font-size: 0.9rem;
    }

    .loading-state,
    .error-state,
    .empty-state,
    .no-more {
        padding: 40px 0;
    }

    .spinner {
        width: 36px;
        height: 36px;
    }

    .btn-retry,
    .btn-load-more {
        padding: 9px 24px;
        font-size: 0.9rem;
    }
}

@media screen and (max-width: 480px) {
    .page-header {
        padding: 20px 14px;
        margin-bottom: 16px;
        border-radius: 10px;
    }

    .header-content {
        gap: 12px;
    }

    .page-title {
        font-size: 1.3rem;
    }

    .page-subtitle {
        font-size: 0.8rem;
    }

    .btn-add-pet {
        padding: 9px;
        border-radius: 50%;
    }

    .btn-text {
        display: none;
    }

    .btn-icon {
        width: 20px;
        height: 20px;
    }

    .pets-container {
        padding-left: 10px;
        padding-right: 10px;
    }

    .pets-grid {
        grid-template-columns: 1fr;
        gap: 12px;
    }

    .pet-card {
        border-radius: 10px;
        flex-direction: row;
        height: 160px;
        align-items: stretch;
    }

    .card-image-wrapper {
        width: 140px;
        flex-shrink: 0;
        padding-top: 0;
        height: 100%;
    }

    .card-img {
        position: relative;
        width: 100%;
        height: 100%;
    }

    .type-tag {
        top: 8px;
        left: 8px;
        padding: 4px 8px;
        font-size: 0.7rem;
    }

    .card-body {
        padding: 12px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        overflow: hidden;
    }

    .card-header {
        margin-bottom: 8px;
    }

    .card-title {
        font-size: 1rem;
        -webkit-line-clamp: 1;
        line-clamp: 1;
    }

    .age-text {
        font-size: 0.7rem;
        padding: 2px 6px;
    }

    .birthday-info {
        gap: 5px;
        margin-bottom: 8px;
        padding: 5px 8px;
    }

    .icon {
        width: 12px;
        height: 12px;
    }

    .info-text {
        font-size: 0.7rem;
    }

    .card-excerpt {
        font-size: 0.8rem;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        margin-bottom: 8px;
        line-height: 1.4;
    }

    .card-footer {
        padding-top: 10px;
    }

    .time-text {
        font-size: 0.65rem;
    }

    .card-actions {
        gap: 4px;
    }

    .action-btn {
        width: 24px;
        height: 24px;
    }

    .action-btn svg {
        width: 12px;
        height: 12px;
    }

    .unauthorized-state {
        padding: 30px 14px;
    }

    .lock-icon {
        width: 40px;
        height: 40px;
    }

    .unauthorized-state h3 {
        font-size: 1.1rem;
    }

    .unauthorized-state p {
        font-size: 0.85rem;
    }

    .btn-login-trigger {
        padding: 10px 20px;
        font-size: 0.9rem;
    }

    .loading-state,
    .error-state,
    .empty-state,
    .no-more {
        padding: 32px 0;
    }

    .spinner {
        width: 32px;
        height: 32px;
    }

    .btn-retry,
    .btn-load-more {
        padding: 8px 20px;
        font-size: 0.85rem;
    }
}

@media screen and (max-width: 360px) {
    .page-header {
        padding: 16px 12px;
    }

    .page-title {
        font-size: 1.2rem;
    }

    .page-subtitle {
        font-size: 0.75rem;
    }

    .pets-container {
        padding-left: 8px;
        padding-right: 8px;
    }

    .pet-card {
        height: 145px;
    }

    .card-image-wrapper {
        width: 120px;
    }

    .card-body {
        padding: 10px;
    }

    .card-title {
        font-size: 0.95rem;
    }

    .age-text {
        font-size: 0.65rem;
    }

    .birthday-info {
        padding: 4px 6px;
    }

    .info-text {
        font-size: 0.65rem;
    }

    .card-excerpt {
        font-size: 0.75rem;
    }

    .time-text {
        font-size: 0.6rem;
    }

    .detail-modal {
        padding: 16px;
    }

    .pet-detail-modal {
        width: 95%;
        max-width: 500px;
        max-height: 90vh;
    }

    .detail-image-wrapper {
        height: 250px;
    }

    .detail-body {
        padding: 24px 20px 20px;
    }

    .detail-title {
        font-size: 1.5rem;
    }

    .detail-age {
        font-size: 0.9rem;
    }

    .detail-info-grid {
        flex-direction: column;
        gap: 12px;
    }

    .detail-actions {
        flex-direction: column;
        gap: 10px;
    }

    .detail-btn {
        width: 100%;
        justify-content: center;
    }
}
</style>