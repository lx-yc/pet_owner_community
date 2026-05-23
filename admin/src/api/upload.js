import request from "../utils/request";
import { compressImageForUpload } from "../utils/imageCompress";

export async function uploadImageApi(file, type = 'pet') {
  const compressedFile = await compressImageForUpload(file);
  const formData = new FormData();
  formData.append("file", compressedFile);
  formData.append("type", type);
  return request.post("/upload/image", formData);
}

export function deleteImageApi(imageUrl) {
  return request.post("/upload/deleteImage", null, {
    params: { imageUrl }
  });
}
