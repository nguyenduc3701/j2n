"use client";

import React from "react";
import { Center, Stack, Avatar, Text, Paper } from "@mantine/core";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";

import { ProfileHeaderProps } from "./profile.types";

const ProfileHeader = ({
  user,
  isEditing,
  setIsEditing,
  onAvatarClick,
  fileInputRef,
  onFileUpload,
}: ProfileHeaderProps) => {
  const { t } = useTranslation();

  return (
    <J2NSection name="profile-header" className="py-20">
      <Paper
        radius="lg"
        p="xl"
        className="bg-white/50 backdrop-blur-sm border border-j2n-sand-300 shadow-lg"
      >
        <Center>
          <Stack align="center" gap="xl">
            <input
              type="file"
              accept="image/*"
              style={{ display: "none" }}
              ref={fileInputRef}
              onChange={onFileUpload}
            />
            <Avatar
              size={150}
              src={user.image_url}
              name={user.full_name}
              color="initials"
              className="border-4 border-j2n-plum-dark-500 shadow-xl cursor-pointer hover:opacity-80 transition-opacity"
              onClick={onAvatarClick}
            />
            <Stack align="center" gap={5}>
              <Text
                size="32px"
                className="font-secondary-700 text-j2n-plum-dark-500"
              >
                {user.full_name}
              </Text>
              <Text size="lg" className="text-j2n-ink-500 opacity-60">
                @{user.user_name}
              </Text>
              {!isEditing && (
                <J2NButton
                  j2nType={J2NButtonTypes.PRIMARY}
                  onClick={() => setIsEditing(true)}
                  className="mt-4"
                >
                  {t("users.actions.edit")}
                </J2NButton>
              )}
            </Stack>
          </Stack>
        </Center>
      </Paper>
    </J2NSection>
  );
};

export default ProfileHeader;
