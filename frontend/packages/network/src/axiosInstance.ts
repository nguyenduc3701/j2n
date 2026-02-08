import axios from "axios";

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
  withCredentials: false,
});

export default axiosInstance;
