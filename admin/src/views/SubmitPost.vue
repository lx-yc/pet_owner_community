<template>
    <div class="submit-post-page">
        <div class="page-bg-effects">
            <div class="gradient-orb orb-1"></div>
            <div class="gradient-orb orb-2"></div>
            <div class="gradient-orb orb-3"></div>
        </div>
        
        <TopNavbar :show-nav-links="false" :show-search="false">
            <template #actions>
                <button class="btn-submit" @click="handleSubmit" :disabled="submitting">
                    {{ submitting ? (isEditing ? '更新中...' : '发布中...') : (isEditing ? '更新' : '发布') }}
                </button>
            </template>
        </TopNavbar>

        <div class="form-container">
            <div class="form-card" v-if="isMounted">
                <div class="form-header">
                    <h1>{{ isEditing ? '编辑帖子' : '发布新帖子' }}</h1>
                </div>

                <div class="form-body">
                    <div class="form-item">
                        <label class="form-label">标题</label>
                        <input v-model="form.title" type="text" class="form-input" placeholder="请输入帖子标题" maxlength="50" />
                        <span class="char-count">{{ form.title.length }}/50</span>
                    </div>

                    <div class="form-item">
                        <label class="form-label">内容</label>
                        <textarea v-model="form.content" class="form-textarea" placeholder="分享你的想法..." rows="8" maxlength="2000"></textarea>
                        <span class="char-count">{{ form.content.length }}/2000</span>
                    </div>

                    <div class="form-item">
                        <label class="form-label">类型</label>
                        <div class="type-selector">
                            <div v-for="type in availableTypes" :key="type.value" class="type-option"
                                :class="{ active: form.type === type.value }" @click="form.type = type.value">
                                <span class="type-icon">{{ type.icon }}</span>
                                <span class="type-label">{{ type.label }}</span>
                            </div>
                        </div>
                    </div>

                    <div class="form-item">
                        <label class="form-label">标签（最多选择5个）</label>
                        <div class="tag-selector">
                            <div v-if="selectedTags.length > 0" class="selected-tags-section">
                                <div class="section-label">已选标签</div>
                                <TransitionGroup name="tag-move" tag="div" class="selected-tags-list">
                                    <div v-for="tag in selectedTags" :key="tag" class="selected-tag">
                                        <span class="tag-text">{{ tag }}</span>
                                        <span class="tag-remove" @click="removeTag(tag)">×</span>
                                    </div>
                                </TransitionGroup>
                            </div>
                            
                            <div class="available-tags-section">
                                <div v-if="selectedTags.length > 0" class="section-label">点击添加更多标签</div>
                                <TransitionGroup name="tag-fade" tag="div" class="available-tags-list">
                                    <div v-for="tag in filteredAvailableTags" :key="tag" 
                                         class="tag-option" 
                                         @click="addTag(tag)">
                                        {{ tag }}
                                    </div>
                                </TransitionGroup>
                                <div v-if="isEditingTag" class="tag-input-wrapper">
                                    <input v-model="customTagInput" 
                                           type="text" 
                                           class="tag-input"
                                           placeholder="输入标签" 
                                           maxlength="4" 
                                           @blur="finishEditTag" 
                                           @keyup.enter="finishEditTag"
                                           @keyup.escape="cancelEditTag"
                                           ref="tagInputRef" />
                                </div>
                                <div v-else class="tag-option tag-add" @click="startEditTag">
                                    + 自定义
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form-item">
                        <label class="form-label">匿名发布</label>
                        <div class="toggle-switch" :class="{ active: form.isAnonymous === 1 }"
                            @click="form.isAnonymous = form.isAnonymous === 1 ? 0 : 1">
                            <div class="toggle-knob"></div>
                        </div>
                        <span class="toggle-hint">{{ form.isAnonymous === 1 ? '已开启匿名' : '当前为实名' }}</span>
                    </div>

                    <div class="form-item">
                        <label class="form-label">上传图片（最多9张）</label>
                        <div class="image-uploader">
                            <div class="image-grid">
                                <div v-for="(img, index) in imageList" :key="index" class="image-item">
                                    <img :src="img" class="uploaded-image" />
                                    <div class="image-remove" @click="removeImage(index)">×</div>
                                </div>
                                <div v-if="imageList.length < 9" class="image-upload-btn" @click="triggerUpload">
                                    <svg viewBox="0 0 24 24" width="32" height="32" stroke="currentColor" stroke-width="2"
                                        fill="none" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="12" y1="5" x2="12" y2="19"></line>
                                        <line x1="5" y1="12" x2="19" y2="12"></line>
                                    </svg>
                                    <span class="upload-text">添加图片</span>
                                    <span class="upload-count">{{ imageList.length }}/9</span>
                                </div>
                            </div>
                        </div>
                        <input ref="fileInputRef" type="file" accept="image/*" multiple class="file-input"
                            @change="handleFileChange" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { insertPostApi, getPostByIdApi, updatePostApi } from '../api/post'
import { uploadImageApi } from '../api/upload'
import { ElMessage } from 'element-plus'
import TopNavbar from '../components/TopNavbar.vue'

const router = useRouter()
const fileInputRef = ref(null)
const tagInputRef = ref(null)
const submitting = ref(false)
const uploadingCount = ref(0)
const isEditing = ref(false)
const postId = ref('')

const form = reactive({
    title: '',
    content: '',
    type: 'normal',
    tags: '日常',
    isAnonymous: 0,
    season: ''
})

const imageList = ref([])
const imageFiles = ref([])
const selectedTags = ref([])
const isEditingTag = ref(false)
const customTagInput = ref('')
const isMounted = ref(false)

const availableTags = ['日常', '猫咪', '狗狗', '求助', '分享', '搞笑', '温馨', '健康', '训练', '美食']

const availableTypes = [
    { value: 'normal', label: '日常', icon: '📝' },
    { value: '宠物分享', label: '宠物分享', icon: '🐾' },
    { value: '心理倾诉', label: '心理倾诉', icon: '💭' }
]

const filteredAvailableTags = computed(() => {
    return availableTags.filter(tag => !selectedTags.value.includes(tag))
})

const addTag = (tag) => {
    if (selectedTags.value.length >= 5) {
        ElMessage.warning('最多只能选择5个标签')
        return
    }
    if (!selectedTags.value.includes(tag)) {
        selectedTags.value.push(tag)
        form.tags = selectedTags.value.join(',')
    }
}

const removeTag = (tag) => {
    selectedTags.value = selectedTags.value.filter(t => t !== tag)
    form.tags = selectedTags.value.join(',')
}

const startEditTag = () => {
    if (selectedTags.value.length >= 5) {
        ElMessage.warning('最多只能选择5个标签')
        return
    }
    isEditingTag.value = true
    customTagInput.value = ''
    nextTick(() => {
        tagInputRef.value?.focus()
    })
}

const finishEditTag = () => {
    const tag = customTagInput.value.trim()
    if (tag && tag.length <= 4) {
        if (!selectedTags.value.includes(tag)) {
            selectedTags.value.push(tag)
            form.tags = selectedTags.value.join(',')
        }
        if (!availableTags.includes(tag)) {
            availableTags.push(tag)
        }
    }
    isEditingTag.value = false
    customTagInput.value = ''
}

const cancelEditTag = () => {
    isEditingTag.value = false
    customTagInput.value = ''
}

const triggerUpload = () => {
    fileInputRef.value?.click()
}

const handleFileChange = (event) => {
    const files = Array.from(event.target.files)
    const remaining = 9 - imageList.value.length
    const toUpload = files.slice(0, remaining)

    toUpload.forEach(file => {
        const url = URL.createObjectURL(file)
        imageList.value.push(url)
        imageFiles.value.push(file)
    })

    event.target.value = ''
}

const removeImage = (index) => {
    URL.revokeObjectURL(imageList.value[index])
    imageList.value.splice(index, 1)
    imageFiles.value.splice(index, 1)
}

const handleSubmit = async () => {
    if (!form.title.trim()) {
        ElMessage.warning('请输入帖子标题')
        return
    }
    if (!form.content.trim()) {
        ElMessage.warning('请输入帖子内容')
        return
    }
    if (!form.tags) {
        ElMessage.warning('请选择标签')
        return
    }

    submitting.value = true

    try {
        let imageUrls = []
        if (imageFiles.value.length > 0) {
            ElMessage.info('正在上传图片...')
            const uploadPromises = imageFiles.value.map(file => uploadImageApi(file))
            const uploadResults = await Promise.all(uploadPromises)
            imageUrls = uploadResults.map(res => {
                if (res.code === 0) {
                    return res.data
                } else {
                    throw new Error(res.message || '图片上传失败')
                }
            })
        } else if (imageList.value.length > 0) {
            // 保留原有的图片
            imageUrls = imageList.value
        }

        const postData = {
            id: postId.value,
            title: form.title,
            content: form.content,
            type: form.type,
            tags: form.tags,
            isAnonymous: form.isAnonymous,
            season: '',
            images: imageUrls.length > 0 ? JSON.stringify(imageUrls) : ''
        }
        
        console.log('Post data:', postData)
        
        if (isEditing.value) {
            await updatePostApi(postData)
            ElMessage.success('更新成功')
        } else {
            await insertPostApi(postData)
            ElMessage.success('发布成功')
        }
        
        // 编辑成功后关闭窗口
        if (isEditing.value) {
            window.close()
        } else {
            // 发布成功后清空表单
            imageUrls = []
            imageList.value = []
            imageFiles.value = []
            selectedTags.value = []
            form.tags = ''
        }
    } catch (error) {
        console.error(isEditing.value ? '更新失败:' : '发布失败:', error)
        ElMessage.error(isEditing.value ? '更新失败，请重试' : '发布失败，请重试')
    } finally {
        submitting.value = false
    }
}

onMounted(async () => {
    document.title = '投稿 - PetCommunity'
    setTimeout(() => {
        isMounted.value = true
    }, 100)
    
    // 检测 URL 参数，判断是否为编辑模式
    const urlParams = new URLSearchParams(window.location.search)
    const editParam = urlParams.get('edit')
    if (editParam) {
        isEditing.value = true
        postId.value = editParam
        document.title = '编辑帖子 - PetCommunity'
        await loadPostDetail(editParam)
    }
})

// 加载帖子详情
const loadPostDetail = async (id) => {
    try {
        const response = await getPostByIdApi(id)
        if (response.code === 0) {
            const post = response.data
            form.title = post.title || ''
            form.content = post.content || ''
            form.type = post.type || 'normal'
            form.tags = post.tags || '日常'
            form.isAnonymous = post.isAnonymous || 0
            form.season = post.season || ''
            
            // 处理标签
            if (post.tags) {
                selectedTags.value = post.tags.split(',').filter(tag => tag.trim())
            }
            
            // 处理图片
            if (post.images) {
                try {
                    const images = JSON.parse(post.images)
                    if (Array.isArray(images)) {
                        imageList.value = images
                    }
                } catch (error) {
                    console.error('解析图片失败:', error)
                }
            }
        } else {
            ElMessage.error('获取帖子详情失败')
        }
    } catch (error) {
        console.error('加载帖子详情失败:', error)
        ElMessage.error('加载失败，请重试')
    }
}
</script>

<style scoped>
.submit-post-page {
    min-height: 100vh;
    background: #f8f9fa;
    position: relative;
}

.page-bg-effects {
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

.navbar {
    position: sticky;
    top: 0;
    z-index: 100;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(20px) saturate(180%);
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.navbar-content {
    max-width: 1100px;
    width: 100%;
    margin: 0 auto;
    padding: 0 24px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-sizing: border-box;
}

.brand {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
}

.brand-icon {
    font-size: 1.6rem;
}

.brand-name {
    font-size: 1.25rem;
    font-weight: 700;
    color: #111827;
}

.actions {
    display: flex;
    align-items: center;
    gap: 16px;
}

.btn-submit {
    padding: 10px 28px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 24px;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-submit:hover:not(:disabled) {
    background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.form-container {
    max-width: 850px;
    margin: 72px auto 48px;
    padding: 0 24px;
}

.form-card {
    background: rgba(255, 255, 255, 0.82);
    backdrop-filter: blur(24px) saturate(180%);
    -webkit-backdrop-filter: blur(24px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 20px;
    box-shadow: 
        0 1px 3px rgba(0, 0, 0, 0.04),
        0 8px 16px rgba(0, 0, 0, 0.02),
        0 4px 20px rgba(102, 126, 234, 0.05);
    overflow: hidden;
    position: relative;
    z-index: 1;
    animation: cardSlideIn 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes cardSlideIn {
    0% {
        opacity: 0;
        transform: translateY(30px) scale(0.95);
        box-shadow: 0 0 0 rgba(0, 0, 0, 0);
    }
    100% {
        opacity: 1;
        transform: translateY(0) scale(1);
        box-shadow: 
            0 1px 3px rgba(0, 0, 0, 0.04),
            0 8px 16px rgba(0, 0, 0, 0.02),
            0 4px 20px rgba(102, 126, 234, 0.05);
    }
}

.form-header {
    padding: 28px 32px;
    border-bottom: 1px solid rgba(243, 244, 246, 0.6);
    background: rgba(255, 255, 255, 0.6);
}

.form-header h1 {
    margin: 0;
    font-size: 1.6rem;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    letter-spacing: -0.5px;
}

.form-body {
    padding: 36px 32px;
    background: rgba(255, 255, 255, 0.4);
}

.form-item {
    margin-bottom: 32px;
}

.form-label {
    display: block;
    font-size: 0.95rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 10px;
}

.form-input {
    width: 100%;
    padding: 14px 18px;
    border: 1px solid rgba(229, 231, 235, 0.8);
    border-radius: 12px;
    font-size: 1rem;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-sizing: border-box;
    background: rgba(255, 255, 255, 0.8);
}

.form-input:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
    background: #ffffff;
    transform: translateY(-1px);
}

.form-textarea {
    width: 100%;
    padding: 14px 18px;
    border: 1px solid rgba(229, 231, 235, 0.8);
    border-radius: 12px;
    font-size: 1rem;
    resize: vertical;
    font-family: inherit;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-sizing: border-box;
    background: rgba(255, 255, 255, 0.8);
}

.form-textarea:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
    background: #ffffff;
    transform: translateY(-1px);
}

.char-count {
    display: block;
    text-align: right;
    font-size: 0.8rem;
    color: #9ca3af;
    margin-top: 6px;
}

.tag-selector {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.selected-tags-section {
    margin-bottom: 4px;
}

.available-tags-section {
    margin-top: 4px;
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 12px;
}

.section-label {
    font-size: 0.85rem;
    color: #9ca3af;
    margin-bottom: 10px;
    font-weight: 500;
}

.selected-tags-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.selected-tag {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 14px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20px;
    font-size: 0.9rem;
    color: white;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
    animation: tagSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tag-text {
    font-weight: 500;
}

.tag-remove {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    cursor: pointer;
    font-size: 14px;
    line-height: 1;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.tag-remove:hover {
    background: rgba(255, 255, 255, 0.5);
    transform: scale(1.1);
}

.available-tags-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 10px;
}

.tag-option {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 8px 16px;
    background: #f3f4f6;
    border-radius: 20px;
    font-size: 0.9rem;
    color: #6b7280;
    cursor: pointer;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    user-select: none;
    position: relative;
    overflow: hidden;
}

.tag-option::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    border-radius: 50%;
    background: rgba(79, 70, 229, 0.1);
    transform: translate(-50%, -50%);
    transition: width 0.3s, height 0.3s;
}

.tag-option:hover::before {
    width: 100px;
    height: 100px;
}

.tag-option:hover {
    background: #e5e7eb;
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tag-option:active {
    transform: translateY(0);
}

.tag-add {
    border: 1px solid #e5e7eb;
    background: #f9fafb;
    color: #6b7280;
    flex: 0 0 auto;
    width: auto;
}

.tag-add:hover {
    border-color: #4f46e5;
    color: #4f46e5;
    background: #eef2ff;
}

.tag-input-wrapper {
    display: inline-flex;
    flex-shrink: 0;
    animation: inputFadeIn 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.tag-input {
    padding: 8px 12px;
    border: 2px solid #4f46e5;
    border-radius: 20px;
    font-size: 0.9rem;
    outline: none;
    width: 100px;
    max-width: 100px;
    background: white;
    transition: all 0.2s;
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.tag-input:focus {
    border-color: #4338ca;
    box-shadow: 0 0 0 4px rgba(79, 70, 229, 0.15);
}

/* 动画效果 */
@keyframes tagSlideIn {
    0% {
        opacity: 0;
        transform: scale(0.8) translateY(-10px);
    }
    100% {
        opacity: 1;
        transform: scale(1) translateY(0);
    }
}

@keyframes inputFadeIn {
    0% {
        opacity: 0;
        transform: scale(0.9);
    }
    100% {
        opacity: 1;
        transform: scale(1);
    }
}

/* Vue TransitionGroup 动画 */
.tag-move-enter-active {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tag-move-leave-active {
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    opacity: 0;
    transform: scale(0.8);
}

.tag-move-enter-from {
    opacity: 0;
    transform: scale(0.8) translateY(-10px);
}

.tag-move-leave-to {
    opacity: 0;
    transform: scale(0.8);
}

.tag-move-move {
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tag-fade-enter-active {
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.tag-fade-leave-active {
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    opacity: 0;
    transform: scale(0.9);
}

.tag-fade-enter-from {
    opacity: 0;
    transform: scale(0.9);
}

.tag-fade-leave-to {
    opacity: 0;
    transform: scale(0.9);
}

.type-selector {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
}

.type-option {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 20px;
    background: rgba(249, 250, 251, 0.8);
    border: 2px solid rgba(229, 231, 235, 0.8);
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.type-option:hover {
    border-color: #667eea;
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.type-option.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-color: transparent;
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.type-icon {
    font-size: 1.2rem;
}

.type-label {
    font-size: 0.9rem;
    font-weight: 500;
    color: #374151;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.type-option.active .type-label {
    color: white;
}

.toggle-switch {
    display: inline-flex;
    align-items: center;
    width: 48px;
    height: 26px;
    background: #e5e7eb;
    border-radius: 13px;
    position: relative;
    cursor: pointer;
    transition: all 0.2s;
}

.toggle-switch.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.toggle-knob {
    width: 22px;
    height: 22px;
    background: white;
    border-radius: 50%;
    position: absolute;
    left: 2px;
    transition: all 0.2s;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.toggle-switch.active .toggle-knob {
    left: 24px;
}

.toggle-hint {
    margin-left: 12px;
    font-size: 0.85rem;
    color: #6b7280;
}

.image-uploader {
    margin-top: 8px;
}

.image-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
    gap: 16px;
}

.image-item {
    position: relative;
    aspect-ratio: 1;
    border-radius: 12px;
    overflow: hidden;
    animation: imageItemSlideIn 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes imageItemSlideIn {
    0% {
        opacity: 0;
        transform: scale(0.8) rotate(-5deg);
    }
    100% {
        opacity: 1;
        transform: scale(1) rotate(0deg);
    }
}

.uploaded-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.image-item:hover .uploaded-image {
    transform: scale(1.05);
}

.image-remove {
    position: absolute;
    top: 4px;
    right: 4px;
    width: 24px;
    height: 24px;
    background: rgba(0, 0, 0, 0.6);
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    transform: scale(0);
    opacity: 0;
}

.image-item:hover .image-remove {
    transform: scale(1);
    opacity: 1;
}

.image-remove:hover {
    background: rgba(239, 68, 68, 0.9);
    transform: scale(1.1);
}

.image-upload-btn {
    aspect-ratio: 1;
    border: 2px dashed #d1d5db;
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    cursor: pointer;
    transition: all 0.2s;
    color: #9ca3af;
}

.image-upload-btn:hover {
    border-color: #667eea;
    color: #667eea;
    background: rgba(249, 250, 251, 0.9);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.upload-text {
    font-size: 0.85rem;
}

.upload-count {
    font-size: 0.75rem;
    color: #d1d5db;
}

.file-input {
    display: none;
}

@media screen and (max-width: 768px) {
    .form-container {
        padding: 0 16px;
        margin: 24px auto;
    }

    .form-body {
        padding: 24px;
    }

    .image-grid {
        grid-template-columns: repeat(3, 1fr);
        gap: 12px;
    }

    .form-header {
        padding: 24px;
    }

    .form-header h1 {
        font-size: 1.4rem;
    }

    .form-input,
    .form-textarea {
        padding: 12px 16px;
    }

    .btn-submit {
        padding: 12px 24px;
        font-size: 0.95rem;
    }

    .type-option {
        padding: 10px 16px;
    }
}

@media screen and (max-width: 480px) {
    .form-container {
        padding: 0 12px;
        margin: 16px auto;
    }

    .form-body {
        padding: 20px;
    }

    .form-header {
        padding: 20px;
    }

    .form-header h1 {
        font-size: 1.25rem;
    }

    .image-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 10px;
    }

    .form-item {
        margin-bottom: 24px;
    }

    .form-input,
    .form-textarea {
        padding: 12px 14px;
    }

    .type-option {
        padding: 8px 14px;
    }

    .btn-submit {
        padding: 12px 20px;
        font-size: 0.9rem;
    }
}
</style>
