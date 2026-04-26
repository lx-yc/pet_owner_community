import request from "../utils/request";

export function uploadImageApi(file, type = 'pet') {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("type", type);
  return request.post("/upload/image", formData);
}

export function deleteImageApi(imageUrl) {
  return request.post("/upload/deleteImage", null, {
    params: { imageUrl }
  });
}
