"use client";

import React from "react";
import { Grid, Stack, Text } from "@mantine/core";
import J2NTransText from "@repo/components/atoms/J2NTransText";

import { ProfileInfoFieldProps } from "./profile.types";

const ProfileInfoField = ({
  label,
  value,
  isEditing,
  children,
  span = { base: 12, md: 6 },
}: ProfileInfoFieldProps) => {
  return (
    <Grid.Col span={span}>
      <Stack gap="xs">
        <J2NTransText
          tKey={label}
          size="sm"
          className="font-secondary-700 text-j2n-plum-dark-500 uppercase tracking-wider"
        />
        {isEditing ? (
          children
        ) : (
          <Text size="lg" className="text-j2n-ink-500">
            {value}
          </Text>
        )}
      </Stack>
    </Grid.Col>
  );
};

export default ProfileInfoField;
