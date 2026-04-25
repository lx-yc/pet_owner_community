import request from "../utils/request";

// 根据帖子ID获取帖子详情
export function getPostByIdApi(id) {
  return request.get('/post/detail', { params: { id } })
}

// 获取帖子列表
export function getPostListApi(params) {
  return request.get('/post/list', { params })
}

// 新增帖子
export function insertPostApi(data) {
  return request.post('/post/insert', data)
}

// 删除帖子
export function deletePostApi(id) {
  return request.delete('/post/delete', { params: { id } })
}

// 修改帖子
export function updatePostApi(data) {
  return request.post('/post/update', data)
}

// 获取我的帖子列表
export function getMyPostListApi(params) {
  return request.get('/post/my/list', { params })
}

// 获取我的点赞列表
export function getMyLikeListApi(params) {
  return request.get('/post/my/likes', { params })
}

// 获取我的收藏列表
export function getMyFavoriteListApi(params) {
  return request.get('/post/my/favorites', { params })
}

// 点赞
export function likePostApi(postId) {
  return request.post('/post/like', { postId: postId })
}

// 收藏
export function collectPostApi(postId) {
  return request.post('/post/favorite', { postId: postId })
}

// 浏览
export function viewPostApi(postId) {
  return request.post('/post/view', { postId: postId })
}

// 获取指定用户的帖子列表
export function getUserPostListApi(userId, params) {
  return request.get(`/post/user/${userId}/posts`, { params })
}

// 获取指定用户的点赞列表
export function getUserLikeListApi(userId, params) {
  return request.get(`/post/user/${userId}/likes`, { params })
}

// 获取指定用户的收藏列表
export function getUserFavoriteListApi(userId, params) {
  return request.get(`/post/user/${userId}/favorites`, { params })
}

// 获取我的帖子统计数据
export function getMyPostStatsApi() {
  return request.get('/post/my/stats')
}

// 获取指定用户的帖子统计数据
export function getUserPostStatsApi(userId) {
  return request.get(`/post/user/${userId}/stats`)
}