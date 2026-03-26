"use client";

import React, { useEffect, useState, useRef, useMemo } from "react";
import { Center, Text, Loader, Stack } from "@mantine/core";
import { useForm } from "@mantine/form";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NDivider from "@repo/components/atoms/J2NDivider";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NFooter from "@repo/components/molecules/J2NFooter";
import { useAppStore } from "@repo/store";
import { userService } from "@/services/userServices";
import { useTranslation } from "@repo/ui/src/providers";

import ProfileHeader from "./components/ProfileHeader";
import ProfileForm from "./components/ProfileForm";
import ProfilePermissions from "./components/ProfilePermissions";

import { ProfileFormValues } from "./components/profile.types";

const ProfilePage = () => {
  const { user, fetchMe, setUser } = useAppStore();
  const [loading, setLoading] = useState(!user);
  const [isEditing, setIsEditing] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [allPermissions, setAllPermissions] = useState<any[]>([]);
  const { t } = useTranslation();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const form = useForm<ProfileFormValues>({
    initialValues: {
      full_name: null,
      phone_number: null,
      birth: null,
      address: null,
      company: null,
      room_id: null,
    },
    validate: {
      full_name: (value: string | null) =>
        !value ? t("validation.fullname_required") : null,
    },
  });

  useEffect(() => {
    if (user) {
      form.setValues({
        full_name: user.full_name || null,
        phone_number: user.phone_number || null,
        birth: user.birth
          ? new Date(user.birth).toISOString().split("T")[0]
          : null,
        address: user.address || null,
        company: user.company || null,
        room_id: user.room_id || null,
      });
    }
  }, [user]);

  useEffect(() => {
    const initData = async () => {
      try {
        const [userData, permissionsData] = await Promise.all([
          !user ? fetchMe() : Promise.resolve(user),
          userService.getPermissions(),
        ]);

        if (permissionsData.status === 200) {
          setAllPermissions(permissionsData.data);
        }
      } catch (error) {
        console.error("Failed to fetch initial data:", error);
      } finally {
        setLoading(false);
      }
    };

    initData();
  }, [user, fetchMe]);

  const userPermissions = allPermissions.filter((p) =>
    user?.permissions?.includes(p.name),
  );

  const permissionColumns = useMemo(
    () => [
      {
        key: "id",
        title: t("no"),
        render: (_: any, index: number) => index + 1,
      },
      {
        key: "name",
        title: t("permission_name"),
        render: (record: any) => (
          <Text className="font-semibold text-j2n-plum-dark-500">
            {record.name}
          </Text>
        ),
      },
      {
        key: "description",
        title: t("description"),
        render: (record: any) => (
          <Text className="text-j2n-ink-500/80 italic">
            {record.description}
          </Text>
        ),
      },
    ],
    [t],
  );

  const handleUpdate = async (values: typeof form.values) => {
    if (!user) return;
    setIsSubmitting(true);
    try {
      const response = await userService.updateUser(user.id.toString(), values);
      if (response.status === 200) {
        setUser(response.data);
        setIsEditing(false);
      }
    } catch (error) {
      console.error("Update failed:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleAvatarClick = () => {
    fileInputRef.current?.click();
  };

  const onFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !user) return;

    const formData = new FormData();
    formData.append("files", file);
    formData.append("owner_id", user.id.toString());
    formData.append("owner_type", "USER");

    setIsSubmitting(true);
    try {
      const uploadRes = await userService.uploadImage(formData);
      if (uploadRes.status === 200) {
        const imageId = uploadRes.data?.[0]?.id;
        if (imageId) {
          const newUrl = `/api/bff/image/USER/${imageId}`;
          const updateRes = await userService.updateUser(user.id.toString(), {
            image_url: newUrl,
          });
          if (updateRes.status === 200) {
            setUser(updateRes.data);
          }
        }
      }
    } catch (error) {
      console.error("Avatar upload failed:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return (
      <Center className="min-h-screen">
        <Stack align="center" gap="md">
          <Loader size="xl" color="var(--color-j2n-grape-deep-500)" />
          <J2NTransText
            tKey="loading_profile"
            size="xl"
            className="text-j2n-plum-dark-500 animate-pulse font-secondary-700"
          />
        </Stack>
      </Center>
    );
  }

  if (!user) {
    return (
      <Center className="min-h-screen">
        <J2NTransText
          tKey="failed_to_load_profile"
          size="xl"
          className="text-red-500 font-secondary-700"
        />
      </Center>
    );
  }

  return (
    <J2NMotionFade>
      <div className="profile-wrapper px-62 min-h-screen">
        <ProfileHeader
          user={user}
          isEditing={isEditing}
          setIsEditing={setIsEditing}
          onAvatarClick={handleAvatarClick}
          fileInputRef={fileInputRef}
          onFileUpload={onFileUpload}
        />

        <J2NDivider />

        <ProfileForm
          user={user}
          isEditing={isEditing}
          setIsEditing={setIsEditing}
          isSubmitting={isSubmitting}
          form={form}
          handleUpdate={handleUpdate}
        />

        <J2NDivider />

        <ProfilePermissions
          columns={permissionColumns}
          data={userPermissions}
        />
        <J2NFooter className="pt-2 pb-10" />
      </div>
    </J2NMotionFade>
  );
};

export default ProfilePage;
