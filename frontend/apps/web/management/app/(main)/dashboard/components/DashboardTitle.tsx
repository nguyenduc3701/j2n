"use client";

import React from "react";
import { Group, GroupProps } from "@mantine/core";
import J2NTransText from "@repo/components/atoms/J2NTransText";

interface DashboardTitleProps extends GroupProps {
  icon?: React.ReactNode;
  title: string;
}

const DashboardTitle: React.FC<DashboardTitleProps> = ({
  icon,
  title,
  className,
  ...props
}) => {
  return (
    <Group
      align="center"
      gap="xs"
      className={`text-j2n-plum-dark-500 ${className || ""}`}
      {...props}
    >
      {icon}
      <J2NTransText
        tKey={title}
        className="font-secondary text-j2n-plum-dark-500"
        fw={600}
        size="lg"
      />
    </Group>
  );
};

export default DashboardTitle;
