import { ACCESS_TOKEN } from "./constants";
import { AxiosRequestConfig, AxiosResponse } from "axios";
import axiosInstance from "./axiosInstance";

export async function request<T>(
  url: string,
  options?: AxiosRequestConfig,
): Promise<AxiosResponse<T>> {
  try {
    // Check if window is defined to safely access localStorage
    const token =
      typeof window !== "undefined" ? localStorage.getItem(ACCESS_TOKEN) : null;

    // Ensure existing headers are preserved and type cast for Record<string, string>
    const headers = {
      ...(options?.headers || {}),
    } as Record<string, string>;

    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await axiosInstance({
      url,
      ...options,
      headers,
    });
    return response;
  } catch (err) {
    console.error("Request error:", err);
    throw err; // Propatage error back to caller
  }
}
