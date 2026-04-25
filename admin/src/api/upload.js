import request from "../utils/request";

export function uploadImageApi(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request.post("/upload/image", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}
