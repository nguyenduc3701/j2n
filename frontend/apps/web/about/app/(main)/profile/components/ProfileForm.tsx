"use client";

import React from "react";
import {
  Grid,
  Stack,
  Text,
  TextInput,
  Textarea,
  Divider,
  Group,
  Paper,
} from "@mantine/core";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";

import ProfileInfoField from "./ProfileInfoField";

import { ProfileFormProps } from "./profile.types";

const ProfileForm = ({
  user,
  isEditing,
  setIsEditing,
  isSubmitting,
  form,
  handleUpdate,
}: ProfileFormProps) => {
  const { t } = useTranslation();

  return (
    <J2NSection name="profile-details" className="py-20">
      <form onSubmit={form.onSubmit(handleUpdate)}>
        <Paper
          radius="lg"
          p="xl"
          className="bg-white/50 backdrop-blur-sm border border-j2n-sand-300 shadow-sm"
        >
          <Grid gutter="xl">
            <ProfileInfoField
              label="users.modal.email"
              value={user.email}
              isEditing={false} // Email is usually not editable
            />

            <ProfileInfoField
              label="users.modal.fullname"
              value={user.full_name}
              isEditing={isEditing}
            >
              <TextInput
                placeholder={t("users.modal.fullname")}
                {...form.getInputProps("full_name")}
                required
              />
            </ProfileInfoField>

            <ProfileInfoField
              label="users.modal.phone"
              value={user.phone_number || t("not_updated")}
              isEditing={isEditing}
            >
              <TextInput
                placeholder={t("users.modal.phone")}
                {...form.getInputProps("phone_number")}
              />
            </ProfileInfoField>

            <ProfileInfoField
              label="users.modal.birth"
              value={
                user.birth
                  ? new Date(user.birth).toLocaleDateString()
                  : t("not_updated")
              }
              isEditing={isEditing}
            >
              <TextInput type="date" {...form.getInputProps("birth")} />
            </ProfileInfoField>

            <ProfileInfoField
              label="users.modal.address"
              value={user.address || t("not_updated")}
              isEditing={isEditing}
              span={12}
            >
              <Textarea
                placeholder={t("users.modal.address")}
                {...form.getInputProps("address")}
              />
            </ProfileInfoField>

            <ProfileInfoField
              label="users.modal.role"
              value={user.role_id}
              isEditing={false}
            />

            <ProfileInfoField
              label="joined_at"
              value={new Date(user.created_at).toLocaleDateString()}
              isEditing={false}
            />

            {user.role_id === "RECRUITER" && (
              <ProfileInfoField
                label="users.modal.company"
                value={user.company || t("not_updated")}
                isEditing={isEditing}
                span={12}
              >
                <TextInput
                  placeholder={t("users.modal.company")}
                  {...form.getInputProps("company")}
                />
              </ProfileInfoField>
            )}

            {user.role_id === "RENTER" && (
              <ProfileInfoField
                label="users.modal.room"
                value={user.room_id || t("not_updated")}
                isEditing={isEditing}
                span={12}
              >
                <TextInput
                  placeholder={t("users.modal.room")}
                  {...form.getInputProps("room_id")}
                />
              </ProfileInfoField>
            )}
          </Grid>

          {isEditing && (
            <Group justify="flex-end" mt="xl">
              <J2NButton
                j2nType={J2NButtonTypes.SECONDARY}
                onClick={() => setIsEditing(false)}
                disabled={isSubmitting}
              >
                {t("users.modal.cancel")}
              </J2NButton>
              <J2NButton
                j2nType={J2NButtonTypes.PRIMARY}
                type="submit"
                loading={isSubmitting}
              >
                {t("users.modal.save")}
              </J2NButton>
            </Group>
          )}
        </Paper>
      </form>
    </J2NSection>
  );
};

export default ProfileForm;
