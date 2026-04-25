import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

export function requireLogin(actionFn) {
  const userStore = useUserStore()
  const appStore = useAppStore()

  if (userStore.isLoggedIn) {
    actionFn()
  } else {
    ElMessage.warning('请先登录后再进行此操作')
    appStore.openLoginModal() // 自动弹出全局登录框
  }
}