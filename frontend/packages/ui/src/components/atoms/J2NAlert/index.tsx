"use client";

import { Alert } from "@mantine/core";
import {
  IconAlertTriangle,
  IconCircleCheck,
  IconInfoCircle,
  IconXboxX,
} from "@tabler/icons-react";
import React from "react";
import { MotionFade } from "../J2NMotionTransition";
import { J2NAlertProps, J2NAlertType } from "./J2NAlert.type";

const ALERT_CONFIG: Record<
  J2NAlertType,
  { color: string; icon: React.ReactNode }
> = {
  warn: { color: "yellow", icon: <IconAlertTriangle size={20} /> },
  error: { color: "red", icon: <IconXboxX size={20} /> },
  success: { color: "green", icon: <IconCircleCheck size={20} /> },
  info: { color: "blue", icon: <IconInfoCircle size={20} /> },
};

const J2NAlert: React.FC<J2NAlertProps> = ({
  children,
  type = "info",
  className,
  ...props
}) => {
  const { color, icon } = ALERT_CONFIG[type];

  return (
    <MotionFade className="w-full">
      <Alert
        className={className}
        variant="light"
        color={color}
        icon={icon}
        w="100%"
        {...props}
      >
        {children}
      </Alert>
    </MotionFade>
  );
};

export default J2NAlert;
