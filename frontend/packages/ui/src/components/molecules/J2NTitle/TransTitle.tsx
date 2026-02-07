"use client";

import React from "react";
import { useTranslation } from "react-i18next";
import { IJ2NTitleProps } from "./J2NTitle.type";
import J2NTitle from "./index";

interface ITransTitleProps extends Omit<IJ2NTitleProps, "title"> {
  tKey: string;
}

const TransTitle: React.FC<ITransTitleProps> = ({
  tKey,
  subTitle,
  ...props
}) => {
  const { t } = useTranslation();
  return (
    <J2NTitle
      title={t(tKey)}
      subTitle={subTitle ? t(subTitle) : undefined}
      {...props}
    />
  );
};

export default TransTitle;
