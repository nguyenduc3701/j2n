"use client";

import React from "react";
import { useTranslation } from "react-i18next";
import { Text, TextProps, Flex, Box } from "@mantine/core";
import { IJ2NCurrencyProps } from "./J2NCurrency.type";

const CURRENCY_MAP: Record<
  string,
  { code: string; locale: string; symbol: string }
> = {
  vi: { code: "VND", locale: "vi-VN", symbol: "đ" },
  en: { code: "USD", locale: "en-US", symbol: "$" },
  kr: { code: "KRW", locale: "ko-KR", symbol: "₩" },
  jp: { code: "JPY", locale: "ja-JP", symbol: "¥" },
  cn: { code: "CNY", locale: "zh-CN", symbol: "¥" },
  // user presets
  vietnam: { code: "VND", locale: "vi-VN", symbol: "đ" },
  us: { code: "USD", locale: "en-US", symbol: "$" },
  korea: { code: "KRW", locale: "ko-KR", symbol: "₩" },
  japan: { code: "JPY", locale: "ja-JP", symbol: "¥" },
  china: { code: "CNY", locale: "zh-CN", symbol: "¥" },
};

const DEFAULT_PRESET = { code: "USD", locale: "en-US", symbol: "$" };

const J2NCurrency: React.FC<IJ2NCurrencyProps & TextProps> = ({
  value,
  currency,
  locale,
  className,
  showSymbolBox,
  icon,
  ...textProps
}) => {
  const { i18n } = useTranslation();
  const [currentLang, setCurrentLang] = React.useState<string>("en");

  React.useEffect(() => {
    // Priority: 1. Props currency, 2. localStorage, 3. i18n.language, 4. default 'en'
    const storedLang =
      typeof window !== "undefined" ? localStorage.getItem("language") : null;
    const initialLang = currency || storedLang || i18n.language || "en";
    setCurrentLang(initialLang);
  }, [currency, i18n.language]);

  const preset = CURRENCY_MAP[currentLang] || DEFAULT_PRESET;

  const formatValue = (val: number, isDecimal = false) => {
    try {
      return new Intl.NumberFormat(locale || preset.locale, {
        style: isDecimal ? "decimal" : "currency",
        currency: preset.code,
        minimumFractionDigits:
          preset.code === "VND" ||
          preset.code === "JPY" ||
          preset.code === "KRW"
            ? 0
            : 2,
      }).format(val);
    } catch (error) {
      return val.toString();
    }
  };

  if (showSymbolBox) {
    return (
      <Flex justify="space-between" align="center" className={className}>
        <Text {...textProps}>
          {typeof value === "number" ? formatValue(value, true) : value}
        </Text>
        <Box className="text-j2n-grape-deep-500 bg-j2n-sand-500 rounded-xs text-lg flex items-center justify-center w-6 h-6 leading-none">
          {icon || preset.symbol}
        </Box>
      </Flex>
    );
  }

  return (
    <Text className={className} {...textProps}>
      {typeof value === "number" ? formatValue(value) : value}
    </Text>
  );
};

export default J2NCurrency;
