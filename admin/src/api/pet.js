import request from "../utils/request";

export function getPetListApi(params) {
  return request.get("/pet/list", { params });
}

export function insertPetApi(data) {
  return request.post("/pet/insert", data);
}

export function updatePetApi(data) {
  return request.post("/pet/update", data);
}

export function deletePetApi(id) {
  return request.delete("/pet/delete", { params: { id } });
}
