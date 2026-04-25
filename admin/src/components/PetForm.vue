<template>
    <div v-if="visible" class="pet-form-overlay" @click.self="handleClose">
        <div class="pet-form-modal">
            <div class="form-header">
                <h2 class="form-title">
                    <span class="title-icon">{{ isEdit ? '✏️' : '🐾' }}</span>
                    {{ isEdit ? '编辑宠物' : '添加宠物' }}
                </h2>
                <button class="btn-close" @click="handleClose">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 6L6 18M6 6l12 12"></path>
                    </svg>
                </button>
            </div>

            <form class="form-body" @submit.prevent="handleSubmit">
                <div class="form-group">
                    <label class="form-label">
                        <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                        宠物名称
                    </label>
                    <input
                        v-model="formData.name"
                        type="text"
                        class="form-input"
                        placeholder="请输入宠物名称"
                        required
                    />
                </div>

                <div class="form-group">
                    <label class="form-label">
                        <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"></circle>
                            <path d="M8 14s1.5 2 4 2 4-2 4-2"></path>
                            <line x1="9" y1="9" x2="9.01" y2="9"></line>
                            <line x1="15" y1="9" x2="15.01" y2="9"></line>
                        </svg>
                        宠物类型
                    </label>
                    <input
                        v-model="formData.type"
                        type="text"
                        class="form-input"
                        placeholder="例如：狗、猫、兔子等"
                        required
                    />
                </div>

                <div class="form-group">
                    <label class="form-label">
                        <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                        </svg>
                        年龄
                    </label>
                    <input
                        v-model.number="formData.age"
                        type="number"
                        class="form-input"
                        placeholder="请输入宠物年龄"
                        min="0"
                        max="30"
                        required
                    />
                </div>

                <div class="form-group">
                    <label class="form-label">
                        <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                        生日
                    </label>
                    <input
                        v-model="formData.birthday"
                        type="date"
                        class="form-input"
                        required
                    />
                </div>

                <div class="form-group">
                    <label class="form-label">
                        <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                        </svg>
                        宠物头像
                    </label>
                    <div class="avatar-upload">
                        <div class="avatar-preview" :class="{ 'has-image': avatarPreview }">
                            <img v-if="avatarPreview" :src="avatarPreview" alt="头像预览" />
                            <div v-else class="avatar-placeholder">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"></path>
                                </svg>
                                <span>点击上传</span>
                            </div>
                        </div>
                        <input
                            ref="fileInput"
                            type="file"
                            accept="image/*"
                            class="file-input"
                            @change="handleFileChange"
                        />
                        <button type="button" class="btn-upload" @click="triggerFileInput" :disabled="uploading">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"></path>
                                <polyline points="17 8 12 3 7 8"></polyline>
                                <line x1="12" y1="3" x2="12" y2="15"></line>
                            </svg>
                            {{ uploading ? '上传中...' : (avatarPreview ? '更换头像' : '选择图片') }}
                        </button>
                        <button v-if="avatarPreview" type="button" class="btn-remove" @click="removeAvatar">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                            </svg>
                            删除
                        </button>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">
                        <svg class="label-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                        </svg>
                        宠物介绍
                    </label>
                    <textarea
                        v-model="formData.info"
                        class="form-textarea"
                        placeholder="介绍一下你家的小可爱吧..."
                        rows="4"
                    ></textarea>
                </div>

                <div class="form-actions">
                    <button type="button" class="btn-cancel" @click="handleClose">
                        取消
                    </button>
                    <button type="submit" class="btn-submit" :disabled="submitting">
                        {{ submitting ? '提交中...' : (isEdit ? '保存修改' : '添加宠物') }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { insertPetApi, updatePetApi } from '../api/pet.js'
import { uploadImageApi } from '../api/upload.js'

const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    petData: {
        type: Object,
        default: null
    }
})

const emit = defineEmits(['close', 'success'])

const isEdit = computed(() => !!props.petData)

const formData = ref({
    name: '',
    type: '',
    age: 0,
    birthday: '',
    avatar: '',
    info: ''
})

const avatarPreview = ref('')
const fileInput = ref(null)
const uploading = ref(false)
const submitting = ref(false)

watch(() => props.visible, (val) => {
    if (val && props.petData) {
        formData.value = {
            id: props.petData.id,
            name: props.petData.name || '',
            type: props.petData.type || '',
            age: props.petData.age || 0,
            birthday: props.petData.birthday || '',
            avatar: props.petData.avatar || '',
            info: props.petData.info || ''
        }
        avatarPreview.value = props.petData.avatar || ''
    } else if (val) {
        formData.value = {
            name: '',
            type: '',
            age: 0,
            birthday: '',
            avatar: '',
            info: ''
        }
        avatarPreview.value = ''
    }
})

const handleClose = () => {
    emit('close')
}

const triggerFileInput = () => {
    fileInput.value?.click()
}

const handleFileChange = async (event) => {
    const file = event.target.files?.[0]
    if (!file) return

    if (!file.type.startsWith('image/')) {
        alert('请选择图片文件')
        return
    }

    if (file.size > 5 * 1024 * 1024) {
        alert('图片大小不能超过5MB')
        return
    }

    try {
        uploading.value = true

        const result = await uploadImageApi(file)

        if (result.code === 0) {
            avatarPreview.value = result.data
            formData.value.avatar = result.data
        } else {
            alert(result.message || '上传失败')
        }
    } catch (error) {
        alert('上传失败：' + error.message)
    } finally {
        uploading.value = false
        if (fileInput.value) {
            fileInput.value.value = ''
        }
    }
}

const removeAvatar = () => {
    avatarPreview.value = ''
    formData.value.avatar = ''
}

const handleSubmit = async () => {
    try {
        submitting.value = true

        const apiCall = isEdit.value ? updatePetApi : insertPetApi
        const result = await apiCall(formData.value)

        if (result.code === 0) {
            emit('success')
            emit('close')
        } else {
            alert(result.message || '操作失败')
        }
    } catch (error) {
        alert('操作失败：' + error.message)
    } finally {
        submitting.value = false
    }
}
</script>

<style scoped>
.pet-form-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    padding: 20px;
    animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

.pet-form-modal {
    background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
    border-radius: 20px;
    width: 100%;
    max-width: 520px;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 
        0 20px 60px rgba(0, 0, 0, 0.15),
        0 0 0 1px rgba(102, 126, 234, 0.1);
    animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(20px) scale(0.98);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.form-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24px 28px;
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.03), rgba(118, 75, 162, 0.03));
    border-radius: 20px 20px 0 0;
}

.form-title {
    font-size: 1.4rem;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    display: flex;
    align-items: center;
    gap: 10px;
    margin: 0;
}

.title-icon {
    font-size: 1.5rem;
}

.btn-close {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    border: none;
    background: rgba(102, 126, 234, 0.08);
    color: #667eea;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
}

.btn-close:hover {
    background: rgba(102, 126, 234, 0.15);
    transform: rotate(90deg);
}

.btn-close svg {
    width: 18px;
    height: 18px;
}

.form-body {
    padding: 28px;
}

.form-group {
    margin-bottom: 22px;
}

.form-label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 0.95rem;
    font-weight: 600;
    color: #374151;
    margin-bottom: 10px;
}

.label-icon {
    width: 18px;
    height: 18px;
    color: #667eea;
}

.form-input,
.form-textarea {
    width: 100%;
    padding: 12px 16px;
    border: 2px solid rgba(102, 126, 234, 0.15);
    border-radius: 12px;
    font-size: 0.95rem;
    color: #374151;
    background: white;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    outline: none;
    box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
    border-color: #667eea;
    box-shadow: 
        0 0 0 3px rgba(102, 126, 234, 0.1),
        0 2px 8px rgba(102, 126, 234, 0.08);
}

.form-input::placeholder,
.form-textarea::placeholder {
    color: #9ca3af;
}

.form-textarea {
    resize: vertical;
    min-height: 100px;
    font-family: inherit;
}

.avatar-upload {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
}

.avatar-preview {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    border: 3px dashed rgba(102, 126, 234, 0.3);
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;
    position: relative;
}

.avatar-preview.has-image {
    border-style: solid;
    border-color: rgba(102, 126, 234, 0.5);
}

.avatar-preview:hover {
    border-color: #667eea;
    transform: scale(1.02);
}

.avatar-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.avatar-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
    color: #667eea;
}

.avatar-placeholder svg {
    width: 32px;
    height: 32px;
}

.avatar-placeholder span {
    font-size: 0.8rem;
    font-weight: 500;
}

.file-input {
    display: none;
}

.btn-upload,
.btn-remove {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    border-radius: 10px;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: none;
}

.btn-upload {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    color: #667eea;
    border: 1px solid rgba(102, 126, 234, 0.2);
}

.btn-upload:hover:not(:disabled) {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
    transform: translateY(-1px);
}

.btn-upload:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-upload svg {
    width: 18px;
    height: 18px;
}

.btn-remove {
    background: rgba(239, 68, 68, 0.08);
    color: #ef4444;
    border: 1px solid rgba(239, 68, 68, 0.15);
}

.btn-remove:hover {
    background: rgba(239, 68, 68, 0.15);
    transform: translateY(-1px);
}

.btn-remove svg {
    width: 16px;
    height: 16px;
}

.form-actions {
    display: flex;
    gap: 12px;
    margin-top: 28px;
    padding-top: 20px;
    border-top: 1px solid rgba(102, 126, 234, 0.08);
}

.btn-cancel,
.btn-submit {
    flex: 1;
    padding: 12px 24px;
    border-radius: 12px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: none;
}

.btn-cancel {
    background: rgba(102, 126, 234, 0.08);
    color: #667eea;
    border: 1px solid rgba(102, 126, 234, 0.15);
}

.btn-cancel:hover {
    background: rgba(102, 126, 234, 0.15);
    transform: translateY(-1px);
}

.btn-submit {
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-submit:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

@media screen and (max-width: 768px) {
    .pet-form-overlay {
        padding: 12px;
        align-items: flex-end;
    }

    .pet-form-modal {
        max-height: 95vh;
        border-radius: 16px 16px 0 0;
        animation: slideUpMobile 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }

    @keyframes slideUpMobile {
        from {
            opacity: 0;
            transform: translateY(100%);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .form-header {
        padding: 20px 20px;
        border-radius: 16px 16px 0 0;
    }

    .form-title {
        font-size: 1.2rem;
    }

    .form-body {
        padding: 20px;
    }

    .form-label {
        font-size: 0.9rem;
    }

    .form-input,
    .form-textarea {
        padding: 10px 14px;
        font-size: 0.9rem;
    }

    .form-actions {
        margin-top: 24px;
        padding-top: 16px;
    }

    .btn-cancel,
    .btn-submit {
        padding: 11px 20px;
        font-size: 0.95rem;
    }

    .avatar-preview {
        width: 100px;
        height: 100px;
    }

    .avatar-placeholder svg {
        width: 28px;
        height: 28px;
    }

    .btn-upload,
    .btn-remove {
        padding: 9px 18px;
        font-size: 0.85rem;
    }
}

@media screen and (max-width: 480px) {
    .pet-form-overlay {
        padding: 0;
        align-items: stretch;
    }

    .pet-form-modal {
        max-width: 100%;
        max-height: 100vh;
        border-radius: 0;
        animation: none;
    }

    .form-header {
        padding: 16px;
        border-radius: 0;
    }

    .form-title {
        font-size: 1.1rem;
    }

    .btn-close {
        width: 32px;
        height: 32px;
    }

    .form-body {
        padding: 16px;
    }

    .form-group {
        margin-bottom: 18px;
    }

    .form-label {
        font-size: 0.85rem;
        margin-bottom: 8px;
    }

    .label-icon {
        width: 16px;
        height: 16px;
    }

    .form-input,
    .form-textarea {
        padding: 10px 12px;
        font-size: 0.9rem;
        border-radius: 10px;
    }

    .form-textarea {
        min-height: 80px;
    }

    .form-actions {
        margin-top: 20px;
        padding-top: 14px;
        gap: 10px;
    }

    .btn-cancel,
    .btn-submit {
        padding: 10px 16px;
        font-size: 0.9rem;
        border-radius: 10px;
    }

    .avatar-preview {
        width: 90px;
        height: 90px;
    }

    .avatar-placeholder svg {
        width: 24px;
        height: 24px;
    }

    .avatar-placeholder span {
        font-size: 0.75rem;
    }

    .btn-upload,
    .btn-remove {
        padding: 8px 16px;
        font-size: 0.8rem;
        border-radius: 8px;
    }

    .btn-upload svg,
    .btn-remove svg {
        width: 14px;
        height: 14px;
    }
}
</style>
