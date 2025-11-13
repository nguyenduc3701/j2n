import { AxiosRequestConfig, AxiosResponse } from 'axios';
import axiosInstance from './axiosInstance'

export async function request<T>(
  url: string,
  options?: AxiosRequestConfig
): Promise<AxiosResponse<T>> {
  try {
    const response = await axiosInstance({
      url,
      ...options,
    });
    return response;
  } catch (err) {
    console.error('Request error:', err);
    throw err; // ném lỗi ra để caller handle
  }
}