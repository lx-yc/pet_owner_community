import request from "../utils/request";

// 【新增】获取指定用户信息 (通过用户ID)
export function getUserByIdApi(userId) {
    return request.get(`/user/detail/${userId}`)
}

// 用户登录
export function loginApi(data) {
    return request.post('/user/login', data)
}

// 用户注册
export function registerApi(data) {
    return request.post('/user/register', data)
}

// 获取用户信息
export function getUserInfoApi(params) {
    return request.get('/user/list', { params })
}

// 修改用户信息
export function updateUserInfoApi(data) {
    return request.post('/user/update', data)
}

// 关注用户
export function followUserApi(followUserId) {
    return request.post('/user/follow', { followUserId: followUserId })
}

// 获取关注列表
export function getFollowingListApi(userId, params) {
    return request.get(`/user/${userId}/following`, { params })
}

// 获取粉丝列表
export function getFollowerListApi(userId, params) {
    return request.get(`/user/${userId}/followers`, { params })
}