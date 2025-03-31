import axios from "axios";

const API_URL = "http://localhost:8080/api/delivery-process";

export const getAllDeliveryProcesses = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

export const getDeliveryProcessById = async (id: number) => {
  const response = await axios.get(`${API_URL}/${id}`);
  return response.data;
};

export const createDeliveryProcess = async (data: any) => {
  const response = await axios.post(API_URL, data);
  return response.data;
};

export const updateDeliveryProcess = async (id: number, data: any) => {
  const response = await axios.put(`${API_URL}/${id}`, data);
  return response.data;
};

export const deleteDeliveryProcess = async (id: number) => {
  await axios.delete(`${API_URL}/${id}`);
};
