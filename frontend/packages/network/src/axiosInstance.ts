import axios from "axios";
import { ACCESS_TOKEN } from "./constants";

// Ensure this file is processed by Next.js or a bundler that supports env vars replacement
// or that these are available in the runtime environment.
const baseURL =
  typeof window === "undefined"
    ? process.env.BFF_BASE_URL
    : process.env.NEXT_PUBLIC_BFF_BASE_URL;

const axiosInstance = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

// Response interceptor to handle silent refresh and update local storage
axiosInstance.interceptors.response.use(
  (response) => {
    // Check if BFF returned a new token in headers
    const newAccessToken = response.headers["x-new-access-token"];
    if (newAccessToken && typeof window !== "undefined") {
      localStorage.setItem(ACCESS_TOKEN, newAccessToken);
      console.log("[Network] Access token synced from silent refresh");
    }
    return response;
  },
  (error) => {
    return Promise.reject(error);
  },
);

export default axiosInstance;
