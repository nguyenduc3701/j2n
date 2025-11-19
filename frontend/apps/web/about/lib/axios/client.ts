import { AxiosRequestConfig, AxiosResponse } from "axios";
import axiosInstance from "./axiosInstance";

export async function request<T>(
  url: string,
  options?: AxiosRequestConfig
): Promise<AxiosResponse<T>> {
  try {
    console.log(`process.env.NEXT_PUBLIC_BFF_BASE_URL`,process.env.NEXT_PUBLIC_BFF_BASE_URL)
    const response = await axiosInstance({
      url,
      ...options,
    });
    return response;
  } catch (err) {
    console.error("Request error:", err);
    throw err;
  }
}