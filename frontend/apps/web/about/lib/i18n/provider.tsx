"use client";

import { ReactNode } from "react";
import { I18nextProvider } from "react-i18next";
import i18n from "./config";
import { useTranslation } from "./hooks";
import { Loader } from "@mantine/core";

export function I18nProvider({ children }: { children: ReactNode }) {
  const { isReady } = useTranslation();
  if (!isReady)
    return (
      <div className="flex justify-center items-center h-screen bg-transparent">
        <Loader size={50} color="gray" />
      </div>
    );
  return <I18nextProvider i18n={i18n}>{children}</I18nextProvider>;
}
