"use client";

import { useEffect, useState } from "react";
import { useTranslation as useTranslationBase } from "react-i18next";

export function useTranslation(ns: string | string[] = "common") {
  const { t, i18n } = useTranslationBase(ns);
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
  }, []);

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng);
    localStorage.setItem("language", lng);
  };

  return { t, i18n, changeLanguage, isReady };
}
