"use client";

import React from "react";
import { useTranslation } from "react-i18next";
import { Text, TextProps } from "@mantine/core";

export interface ITransTextProps extends TextProps {
  tKey: string;
}

const J2NTransText: React.FC<ITransTextProps> = ({ tKey, ...props }) => {
  const { t } = useTranslation();
  return (
    <Text span {...props}>
      {tKey.trim() !== "" ? t(tKey) : tKey}
    </Text>
  );
};

export default J2NTransText;
