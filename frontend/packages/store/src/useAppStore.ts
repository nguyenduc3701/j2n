import { create } from "zustand";
import { persist, createJSONStorage, devtools } from "zustand/middleware";
import { request, HTTP_METHODS } from "@repo/network";

interface AppState {
  language: string;
  configurations: Record<string, string> | null;
  accessToken: string | null;
  user: any | null;
  setLanguage: (lang: string) => void;
  setAccessToken: (token: string | null) => void;
  setConfigurations: (configurations: Record<string, string>) => void;
  setUser: (user: any | null) => void;
  clearMe: () => void;
  fetchMe: () => Promise<any | null>;
}

const cookieStorage = {
  getItem: (name: string): string | null => {
    if (typeof document === "undefined") return null;
    const value = document.cookie
      .split("; ")
      .find((row) => row.startsWith(`${name}=`))
      ?.split("=")[1];
    return value ? decodeURIComponent(value) : null;
  },
  setItem: (name: string, value: string): void => {
    if (typeof document === "undefined") return;
    document.cookie = `${name}=${encodeURIComponent(value)}; path=/; max-age=31536000`;
  },
  removeItem: (name: string): void => {
    if (typeof document === "undefined") return;
    document.cookie = `${name}=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT`;
  },
};

export const useAppStore = create<AppState>()(
  devtools(
    persist(
      (set, get) => ({
        language: "en",
        accessToken: null,
        configurations: null,
        user: null,
        setLanguage: (language: string) => set({ language }),
        setAccessToken: (accessToken: string | null) => set({ accessToken }),
        setConfigurations: (configurations: Record<string, string>) =>
          set({ configurations }),
        setUser: (user: any | null) => set({ user }),
        clearMe: () => set({ user: null }),
        fetchMe: async () => {
          const { user, configurations } = get();
          if (user) return user;

          try {
            const response = await request<any>("/api/bff/users/me", {
              method: HTTP_METHODS.GET,
            });
            if (response.status === 200 && response.data?.data) {
              const userData = response.data.data;
              set({ user: userData });
              return userData;
            } else {
              throw new Error("User unauthorized");
            }
          } catch (error) {
            console.error("Fetch me failed:", error);
            const aboutUrl =
              configurations?.["about-portal.base-url"] ||
              "http://localhost:3100";
            if (typeof window !== "undefined") {
              window.location.href = `${aboutUrl}/login`;
            }
          }
          return null;
        },
      }),
      {
        name: "j2n-app-store",
        storage: createJSONStorage(() => cookieStorage),
      },
    ),
    { name: "AppStore" },
  ),
);
