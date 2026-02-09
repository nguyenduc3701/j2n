"use client";

import i18next from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import enCommon from "../public/locales/en/common.json";
import viCommon from "../public/locales/vi/common.json";
import krCommon from "../public/locales/kr/common.json";
import jpCommon from "../public/locales/jp/common.json";
import cnCommon from "../public/locales/cn/common.json";
import { ReactNode, useEffect, useState } from "react";
import { I18nextProvider } from "react-i18next";

// Initialize i18next
// Re-initializing to load new locales
const i18n = i18next.createInstance();

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources: {
      en: { common: enCommon },
      vi: { common: viCommon },
      kr: { common: krCommon },
      jp: { common: jpCommon },
      cn: { common: cnCommon },
    },
    fallbackLng: "en",
    supportedLngs: ["en", "vi", "kr", "jp", "cn"],
    ns: ["common"],
    defaultNS: "common",
    interpolation: { escapeValue: false },
    react: { useSuspense: false },
    detection: {
      order: ["localStorage", "navigator"],
      caches: ["localStorage"],
      lookupLocalStorage: "language",
    },
  });

export interface I18nProviderProps {
  children: ReactNode;
}

export function J2NI18nProvider({ children }: I18nProviderProps) {
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    if (i18n.isInitialized) {
      setIsReady(true);
    } else {
      i18n.on("initialized", () => setIsReady(true));
    }
  }, []);

  if (!isReady) {
    return null; // or a loader
  }

  return <I18nextProvider i18n={i18n}>{children}</I18nextProvider>;
}

export default i18n;
