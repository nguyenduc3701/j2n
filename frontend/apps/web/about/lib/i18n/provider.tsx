"use client";

import { ReactNode, useEffect, useState } from "react";
import { I18nextProvider } from "react-i18next";
import i18n from "./config";
import { Loader } from "@mantine/core";

export function I18nProvider({ children }: { children: ReactNode }) {
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    const savedLng = localStorage.getItem("language") || "en";
    const loadLanguage = async () => {
      if (savedLng !== i18n.language) {
        await i18n.changeLanguage(savedLng);
      }
      setIsReady(true);
    };
    loadLanguage();

    // Sync lang attribute
    const handleLanguageChanged = (lng: string) => {
      document.documentElement.lang = lng;
      localStorage.setItem("language", lng);
    };

    if (i18n.language) {
      document.documentElement.lang = i18n.language;
    }

    i18n.on("languageChanged", handleLanguageChanged);
    return () => {
      i18n.off("languageChanged", handleLanguageChanged);
    };
  }, []);

  if (!isReady)
    return (
      <div className="flex justify-center items-center h-screen bg-transparent">
        <Loader size={50} color="gray" />
      </div>
    );

  return <I18nextProvider i18n={i18n}>{children}</I18nextProvider>;
}
