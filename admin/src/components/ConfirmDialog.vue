<template>
    <div v-if="visible" class="confirm-overlay" @click.self="handleCancel">
        <div class="confirm-modal">
            <div class="confirm-icon" :class="type">
                <svg v-if="type === 'danger'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
            </div>
            <h3 class="confirm-title">{{ title }}</h3>
            <p class="confirm-message">{{ message }}</p>
            <div class="confirm-actions">
                <button class="btn-cancel" @click="handleCancel">
                    {{ cancelText }}
                </button>
                <button class="btn-confirm" :class="type" @click="handleConfirm">
                    {{ confirmText }}
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    title: {
        type: String,
        default: '确认操作'
    },
    message: {
        type: String,
        default: '确定要执行此操作吗？'
    },
    type: {
        type: String,
        default: 'danger',
        validator: (value) => ['danger', 'info'].includes(value)
    },
    cancelText: {
        type: String,
        default: '取消'
    },
    confirmText: {
        type: String,
        default: '确定'
    }
})

const emit = defineEmits(['confirm', 'cancel'])

const handleConfirm = () => {
    emit('confirm')
}

const handleCancel = () => {
    emit('cancel')
}
</script>

<style scoped>
.confirm-overlay {
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
    z-index: 2000;
    padding: 20px;
    animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

.confirm-modal {
    background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
    border-radius: 20px;
    padding: 32px;
    width: 100%;
    max-width: 420px;
    box-shadow: 
        0 20px 60px rgba(0, 0, 0, 0.15),
        0 0 0 1px rgba(102, 126, 234, 0.1);
    animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    text-align: center;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(20px) scale(0.95);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

.confirm-icon {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    margin: 0 auto 20px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.confirm-icon.danger {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(239, 68, 68, 0.05));
    color: #ef4444;
}

.confirm-icon.info {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(102, 126, 234, 0.05));
    color: #667eea;
}

.confirm-icon svg {
    width: 32px;
    height: 32px;
}

.confirm-title {
    font-size: 1.3rem;
    font-weight: 700;
    color: #374151;
    margin: 0 0 12px 0;
}

.confirm-message {
    font-size: 0.95rem;
    color: #6b7280;
    margin: 0 0 28px 0;
    line-height: 1.5;
}

.confirm-actions {
    display: flex;
    gap: 12px;
}

.btn-cancel,
.btn-confirm {
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

.btn-confirm {
    color: white;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-confirm.danger {
    background: linear-gradient(135deg, #ef4444, #dc2626);
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.btn-confirm.danger:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(239, 68, 68, 0.4);
}

.btn-confirm.info {
    background: linear-gradient(135deg, #667eea, #764ba2);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-confirm.info:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

@media screen and (max-width: 480px) {
    .confirm-overlay {
        padding: 16px;
        align-items: flex-end;
    }

    .confirm-modal {
        padding: 24px;
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

    .confirm-icon {
        width: 56px;
        height: 56px;
        margin-bottom: 16px;
    }

    .confirm-icon svg {
        width: 28px;
        height: 28px;
    }

    .confirm-title {
        font-size: 1.2rem;
    }

    .confirm-message {
        font-size: 0.9rem;
        margin-bottom: 24px;
    }

    .confirm-actions {
        gap: 10px;
    }

    .btn-cancel,
    .btn-confirm {
        padding: 11px 20px;
        font-size: 0.95rem;
    }
}
</style>
