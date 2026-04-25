import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router";
import { useUserStore } from "../stores/user";

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000,
})

request.interceptors.request.use(
    config => {
        // 从本地获取token（和后端header("token")对应）
        const token = localStorage.getItem('token')

        if (token) {
            // 把token放到请求头：token
            config.headers['token'] = token
        }

        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// =================== 响应拦截器 ===================
// 对应后端返回：未登录 / token过期 / token无效 / 身份异常
request.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        const res = error.response || {}
        const msg = res.data?.msg || '请求失败'

        // 后端返回这些消息 → 清除token + 跳转到登录页
        if (
            msg === '未登录' ||
            msg === 'token无效' ||
            msg === '登录已过期，请重新登录！' ||
            msg === '身份异常，请重新登录'
        ) {
            ElMessage.error(msg)

            // 👇 在这里加 2 行，让 Pinia 同步清空！
            const userStore = useUserStore();
            userStore.logout(); // 🔥 核心：同时清空 Pinia + localStorage

            // 清除本地登录信息
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')

            // 跳转到登录页
            router.push('/login')
        } else {
            ElMessage.error(msg || '系统异常')
        }

        return Promise.reject(error)
    }
)

export default request