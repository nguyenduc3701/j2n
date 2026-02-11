"use client";

import React from "react";
import { Group, Stack, Box, Flex } from "@mantine/core";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NDivider from "@repo/components/atoms/J2NDivider";

interface CardItemProps {
  avatar?: React.ReactNode;
  title?: string;
  subtitle?: string;
  value?: string;
  divider?: boolean;
  className?: string;
  onClick?: () => void;
}

const CardItem: React.FC<CardItemProps> = ({
  avatar,
  title,
  subtitle,
  value,
  divider = false,
  className,
  onClick,
}) => {
  return (
    <Box
      className={`w-full ${onClick ? "cursor-pointer" : ""} ${className || ""}`}
      onClick={onClick}
    >
      <Flex className="w-full py-2" justify="space-between" align="center">
        <Group gap="sm" wrap="nowrap" align="center">
          {avatar}
          <Stack gap={0} align="flex-start">
            {title && (
              <J2NTransText
                tKey={title}
                className="font-semibold text-md leading-tight font-secondary"
              />
            )}
            {subtitle && (
              <J2NTransText
                tKey={subtitle}
                className="text-xs text-gray-500 leading-tight"
              />
            )}
          </Stack>
        </Group>

        {value && <J2NTransText tKey={value} className="shrink-0" />}
      </Flex>
      {divider && <J2NDivider />}
    </Box>
  );
};

export default CardItem;
