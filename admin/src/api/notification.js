import request from "../utils/request";

export function getUnreadCountApi() {
  return request.get("/notification/unread/count");
}

export function clearUnreadApi() {
  return request.post("/notification/unread/clear");
}
