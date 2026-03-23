"use client";

import { Paper, PaperProps, Group } from "@mantine/core";
import { IconDots } from "@tabler/icons-react";
import React from "react";

import J2NTransText from "@repo/components/atoms/J2NTransText";

interface DashboardCardProps extends PaperProps {
  title?: string;
  icon?: React.ReactNode;
  hideIcon?: boolean;
  children?: React.ReactNode;
}

const DashboardCard: React.FC<DashboardCardProps> = ({
  title,
  icon,
  hideIcon = false,
  children,
  className,
  ...props
}) => {
  const renderIcon = () => {
    if (hideIcon) return null;
    if (icon) return icon;
    return <IconDots size={20} className="text-gray-500 cursor-pointer" />;
  };

    const iconNode = renderIcon();

  return (
    <Paper radius="md" bg="#F8F4F4" p="lg" className={className} {...props}>
      {(title || iconNode) && (
        <Group justify="space-between" mb="md" align="center" wrap="nowrap">
          {title && (
            <J2NTransText
              fw={600}
              size="lg"
              className="truncate text-j2n-plum-dark-500 opacity-70 font-secondary"
              tKey={title}
            />
          )}
          {iconNode}
        </Group>
      )}
      {children}
    </Paper>
  );
};

export default DashboardCard;
