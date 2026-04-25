import request from "../utils/request";

export function insertCommentApi(data) {
  return request.post("/comment/insert", data);
}

export function likeCommentApi(commentId) {
  return request.post("/comment/like", null, { params: { commentId } });
}

export function getTopCommentsApi(params) {
  return request.get("/comment/page/top", { params });
}

export function getReplyCommentsApi(params) {
  return request.get("/comment/page/reply", { params });
}
