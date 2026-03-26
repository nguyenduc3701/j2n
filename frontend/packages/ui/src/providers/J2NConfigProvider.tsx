"use client";

import { ReactNode, useEffect } from "react";
import { useAppStore } from "@repo/store";
import { request } from "@repo/network";

export function J2NConfigProvider({ children }: { children: ReactNode }) {
  const { configurations, setConfigurations } = useAppStore();

  useEffect(() => {
    const fetchConfigs = async () => {
      // If configurations is not yet loaded, fetch it
      if (configurations === null) {
        try {
          // fetch based on keys
          const response = await request<any>("/api/bff/configurations/keys", {
            method: "POST",
            data: {
              keys: ["management-portal.base-url", "about-portal.base-url"],
            },
          });

          // Process the response data
          if (response && response.data && response.data.data) {
            const configMap: Record<string, string> = {};
            response.data.data.forEach((item: any) => {
              configMap[item.config_key] = item.config_value;
            });
            setConfigurations(configMap);
          }
        } catch (error) {
          console.error("Failed to fetch configurations:", error);
        }
      }
    };

    fetchConfigs();
  }, [configurations, setConfigurations]);

  return <>{children}</>;
}
