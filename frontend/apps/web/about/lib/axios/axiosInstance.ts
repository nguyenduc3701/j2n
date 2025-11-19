import axios from 'axios';


const baseURL =
  typeof window === "undefined"
    ? process.env.BFF_BASE_URL // server-side → container-to-container
    : process.env.NEXT_PUBLIC_BFF_BASE_URL; // client-side → browser

const axiosInstance = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: false,
});

export default axiosInstance;
