"use client";

import { useTranslation as useTranslationBase } from "react-i18next";

export function useTranslation(ns: string | string[] = "common") {
  const { t, i18n } = useTranslationBase(ns);

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng);
    localStorage.setItem("language", lng);
  };

  return { t, i18n, changeLanguage, lang: i18n.language };
}
