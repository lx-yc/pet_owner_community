<template>
    <Teleport to="body">
        <Transition name="fade">
            <div v-if="visible" class="profile-edit-overlay" @click.self="handleClose">
                <Transition name="slide-up">
                    <div v-if="visible" class="profile-edit-modal">
                        <div class="modal-header">
                            <h3 class="modal-title">
                                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
                                编辑个人资料
                            </h3>
                            <button class="close-btn" @click="handleClose">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <line x1="18" y1="6" x2="6" y2="18"></line>
                                    <line x1="6" y1="6" x2="18" y2="18"></line>
                                </svg>
                            </button>
                        </div>

                        <div class="modal-body">
                            <div class="avatar-section">
                                <div class="avatar-preview">
                                    <img :src="formData.avatar || defaultAvatar" alt="Avatar" />
                                    <div class="avatar-overlay" @click="triggerAvatarUpload">
                                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
                                            <circle cx="12" cy="13" r="4"></circle>
                                        </svg>
                                        <span>更换头像</span>
                                    </div>
                                </div>
                                <input 
                                    ref="avatarInput" 
                                    type="file" 
                                    accept="image/*" 
                                    style="display: none" 
                                    @change="handleAvatarChange"
                                />
                            </div>

                            <div class="form-grid">
                                <div class="form-item">
                                    <label class="form-label">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                            <circle cx="12" cy="7" r="4"></circle>
                                        </svg>
                                        昵称
                                    </label>
                                    <input 
                                        v-model="formData.nickname" 
                                        type="text" 
                                        class="form-input" 
                                        placeholder="输入你的昵称"
                                        maxlength="20"
                                    />
                                </div>

                                <div class="form-item">
                                    <label class="form-label">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                                        </svg>
                                        个性签名
                                    </label>
                                    <textarea 
                                        v-model="formData.signature" 
                                        class="form-textarea" 
                                        placeholder="写一句你的个性签名..."
                                        maxlength="100"
                                        rows="3"
                                    ></textarea>
                                </div>

                                <div class="form-item">
                                    <label class="form-label">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                                        </svg>
                                        手机号
                                    </label>
                                    <input 
                                        v-model="formData.phone" 
                                        type="tel" 
                                        class="form-input" 
                                        placeholder="输入手机号"
                                    />
                                </div>

                                <div class="form-item">
                                    <label class="form-label">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
                                            <polyline points="22,6 12,13 2,6"></polyline>
                                        </svg>
                                        邮箱
                                    </label>
                                    <input 
                                        v-model="formData.email" 
                                        type="email" 
                                        class="form-input" 
                                        placeholder="输入邮箱地址"
                                    />
                                </div>

                                <div class="form-item">
                                    <label class="form-label">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <circle cx="12" cy="12" r="10"></circle>
                                            <path d="M8 14s1.5 2 4 2 4-2 4-2"></path>
                                            <line x1="9" y1="9" x2="9.01" y2="9"></line>
                                            <line x1="15" y1="9" x2="15.01" y2="9"></line>
                                        </svg>
                                        心情状态
                                    </label>
                                    <div class="mood-selector">
                                        <div 
                                            v-for="mood in moodOptions" 
                                            :key="mood.value"
                                            class="mood-item"
                                            :class="{ active: formData.moodStatus === mood.value }"
                                            @click="formData.moodStatus = mood.value"
                                        >
                                            <span class="mood-emoji">{{ mood.emoji }}</span>
                                            <span class="mood-label">{{ mood.label }}</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-item">
                                    <label class="form-label">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                                        </svg>
                                        心理标签
                                    </label>
                                    <div class="tag-selector">
                                        <div 
                                            v-for="tag in tagOptions" 
                                            :key="tag"
                                            class="tag-item"
                                            :class="{ active: formData.mentalTag === tag }"
                                            @click="formData.mentalTag = tag"
                                        >
                                            {{ tag }}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="modal-footer">
                            <button class="btn-cancel" @click="handleClose">取消</button>
                            <button class="btn-save" @click="handleSave" :disabled="saving">
                                <svg v-if="saving" class="loading-spinner" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M21 12a9 9 0 11-6.219-8.56"></path>
                                </svg>
                                <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                                    <polyline points="17 21 17 13 7 13 7 21"></polyline>
                                    <polyline points="7 3 7 8 15 8"></polyline>
                                </svg>
                                {{ saving ? '保存中...' : '保存修改' }}
                            </button>
                        </div>
                    </div>
                </Transition>
            </div>
        </Transition>
    </Teleport>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue';
import { useUserStore } from '../stores/user';
import { updateUserInfoApi } from '../api/user';
import { uploadImageApi, deleteImageApi } from '../api/upload';
import { ElMessage } from 'element-plus';

const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    }
});

const emit = defineEmits(['update:visible', 'saved']);

const userStore = useUserStore();
const avatarInput = ref(null);
const saving = ref(false);
const defaultAvatar = '';
const originalAvatar = ref('');

const moodOptions = [
    { value: '开心', emoji: '😊', label: '开心' },
    { value: '平静', emoji: '😌', label: '平静' },
    { value: '焦虑', emoji: '😰', label: '焦虑' },
    { value: '低落', emoji: '😔', label: '低落' },
    { value: '兴奋', emoji: '🤩', label: '兴奋' }
];

const tagOptions = [
    '宠物达人', '铲屎官', '萌宠控', '治愈系',
    '阳光开朗', '佛系青年', '社交达人', '安静美男/女'
];

const formData = reactive({
    nickname: '',
    avatar: '',
    signature: '',
    phone: '',
    email: '',
    moodStatus: '',
    mentalTag: ''
});

watch(() => props.visible, (val) => {
    if (val && userStore.userInfo) {
        formData.nickname = userStore.userInfo.nickname || '';
        formData.avatar = userStore.userInfo.avatar || '';
        originalAvatar.value = userStore.userInfo.avatar || '';
        formData.signature = userStore.userInfo.signature || '';
        formData.phone = userStore.userInfo.phone || '';
        formData.email = userStore.userInfo.email || '';
        formData.moodStatus = userStore.userInfo.moodStatus || '';
        formData.mentalTag = userStore.userInfo.mentalTag || '';
    }
    
    if (val) {
        document.body.style.overflow = 'hidden';
    } else {
        document.body.style.overflow = '';
    }
});

const handleClose = () => {
    emit('update:visible', false);
};

const triggerAvatarUpload = () => {
    avatarInput.value?.click();
};

const handleAvatarChange = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
        ElMessage.warning('请选择图片文件');
        return;
    }

    if (file.size > 5 * 1024 * 1024) {
        ElMessage.warning('图片大小不能超过5MB');
        return;
    }

    try {
        ElMessage.info('上传中...');
        const res = await uploadImageApi(file, 'avatar');
        if (res.code === 0) {
            formData.avatar = res.data;
            ElMessage.success('头像上传成功');
        } else {
            throw new Error(res.message);
        }
    } catch (err) {
        console.error(err);
        ElMessage.error('头像上传失败');
    }

    e.target.value = '';
};

const handleSave = async () => {
    if (!formData.nickname?.trim()) {
        ElMessage.warning('昵称不能为空');
        return;
    }

    saving.value = true;
    try {
        const res = await updateUserInfoApi({
            nickname: formData.nickname.trim(),
            avatar: formData.avatar,
            signature: formData.signature.trim(),
            phone: formData.phone.trim(),
            email: formData.email.trim(),
            moodStatus: formData.moodStatus,
            mentalTag: formData.mentalTag
        });

        if (res.code === 0) {
            userStore.updateUserInfo({
                nickname: formData.nickname.trim(),
                avatar: formData.avatar,
                signature: formData.signature.trim(),
                phone: formData.phone.trim(),
                email: formData.email.trim(),
                moodStatus: formData.moodStatus,
                mentalTag: formData.mentalTag
            });

            // 如果头像有变化，删除旧头像
            if (formData.avatar !== originalAvatar.value && originalAvatar.value) {
                try {
                    await deleteImageApi(originalAvatar.value);
                    console.log('旧头像删除成功');
                } catch (err) {
                    console.error('删除旧头像失败:', err);
                    // 即使删除失败也不影响保存结果
                }
            }

            ElMessage.success('资料修改成功');
            emit('saved');
            handleClose();
        } else {
            throw new Error(res.message);
        }
    } catch (err) {
        console.error(err);
        ElMessage.error(err.message || '保存失败');
    } finally {
        saving.value = false;
    }
};
</script>

<style scoped>
.profile-edit-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(8px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2000;
    padding: 20px;
}

.profile-edit-modal {
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border-radius: 24px;
    width: 100%;
    max-width: 680px;
    max-height: 90vh;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.1);
    display: flex;
    flex-direction: column;
}

.modal-header {
    padding: 24px 28px;
    border-bottom: 1px solid #e5e7eb;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

.modal-title {
    font-size: 1.25rem;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 10px;
    margin: 0;
}

.close-btn {
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: white;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.close-btn:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: rotate(90deg);
}

.modal-body {
    padding: 28px;
    overflow-y: auto;
    flex: 1;
}

.avatar-section {
    display: flex;
    justify-content: center;
    margin-bottom: 32px;
}

.avatar-preview {
    position: relative;
    width: 120px;
    height: 120px;
    border-radius: 50%;
    overflow: hidden;
    border: 4px solid #667eea;
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.avatar-preview:hover {
    transform: scale(1.05);
    box-shadow: 0 12px 32px rgba(102, 126, 234, 0.4);
}

.avatar-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.avatar-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    opacity: 0;
    transition: opacity 0.3s;
    color: white;
}

.avatar-preview:hover .avatar-overlay {
    opacity: 1;
}

.avatar-overlay span {
    font-size: 0.75rem;
    font-weight: 500;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
}

.form-item {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.form-item:has(.mood-selector),
.form-item:has(.tag-selector) {
    grid-column: 1 / -1;
}

.form-label {
    font-size: 0.875rem;
    font-weight: 600;
    color: #374151;
    display: flex;
    align-items: center;
    gap: 6px;
}

.form-input,
.form-textarea {
    padding: 12px 16px;
    border: 2px solid #e5e7eb;
    border-radius: 12px;
    font-size: 0.95rem;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background: white;
    font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-textarea {
    resize: vertical;
    min-height: 80px;
}

.mood-selector {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

.mood-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    padding: 12px 16px;
    border: 2px solid #e5e7eb;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background: white;
    min-width: 80px;
}

.mood-item:hover {
    border-color: #667eea;
    transform: translateY(-2px);
}

.mood-item.active {
    border-color: #667eea;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.mood-emoji {
    font-size: 1.75rem;
}

.mood-label {
    font-size: 0.75rem;
    color: #6b7280;
    font-weight: 500;
}

.mood-item.active .mood-label {
    color: #667eea;
}

.tag-selector {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
}

.tag-item {
    padding: 8px 16px;
    border: 2px solid #e5e7eb;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background: white;
    font-size: 0.875rem;
    font-weight: 500;
    color: #6b7280;
}

.tag-item:hover {
    border-color: #667eea;
    color: #667eea;
    transform: translateY(-2px);
}

.tag-item.active {
    border-color: #667eea;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.modal-footer {
    padding: 20px 28px;
    border-top: 1px solid #e5e7eb;
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    background: #f9fafb;
}

.btn-cancel,
.btn-save {
    padding: 12px 28px;
    border-radius: 12px;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: none;
    display: flex;
    align-items: center;
    gap: 8px;
}

.btn-cancel {
    background: white;
    color: #6b7280;
    border: 2px solid #e5e7eb;
}

.btn-cancel:hover {
    border-color: #d1d5db;
    background: #f9fafb;
}

.btn-save {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-save:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-save:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.loading-spinner {
    animation: spin 1s linear infinite;
}

@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-up-enter-from {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
}

.slide-up-leave-to {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
}

@media (max-width: 768px) {
    .form-grid {
        grid-template-columns: 1fr;
    }

    .profile-edit-modal {
        max-width: 100%;
        border-radius: 20px 20px 0 0;
        max-height: 85vh;
    }

    .profile-edit-overlay {
        align-items: flex-end;
        padding: 0;
    }
}
</style>
