import { create } from "zustand";
import { devtools } from "zustand/middleware";

interface AppState {
  theme: "light" | "dark";
  setTheme: (theme: "light" | "dark") => void;
}

export const useAppStore = create<AppState>()(
  devtools(
    (set) => ({
      theme: "light",
      setTheme: (theme) => set({ theme }),
    }),
    { name: "AboutAppStore" },
  ),
);
