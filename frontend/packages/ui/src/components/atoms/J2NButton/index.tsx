import React from "react";
import { Button, ButtonProps } from "@mantine/core";
import clsx from "clsx";

export enum J2NButtonTypes {
  PRIMARY = "primary",
  SECONDARY = "secondary",
}

const J2N_BUTTON_CLASSES: Record<J2NButtonTypes, string> = {
  primary: "!bg-j2n-mauve-500 !text-j2n-sand-light-300 hover:opacity-80",
  secondary: "!bg-j2n-sand-light-300 !text-j2n-plum-dark-500 hover:opacity-80",
};

export interface J2NButtonProps extends ButtonProps {
  j2nType?: J2NButtonTypes;
}

const J2NButton: React.FC<J2NButtonProps> = ({
  children,
  j2nType = J2NButtonTypes.PRIMARY,
  className,
  ...props
}) => {
  return (
    <Button
      className={clsx(
        "transition-all duration-200",
        J2N_BUTTON_CLASSES[j2nType],
        className,
      )}
      {...props}
    >
      {children}
    </Button>
  );
};

export default J2NButton;
