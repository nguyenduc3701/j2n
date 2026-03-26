"use client";

import React, { useEffect } from "react";
import {
  Grid,
  Group,
  Select,
  TextInput,
  Textarea,
  PasswordInput,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { useTranslation } from "@repo/ui/src/providers";
import { IUser } from "@/types/user";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import {
  ModalMode,
  ROLE_MAPPING,
  USER_ROLES,
  USER_STATUS_OPTIONS,
  UserModalProps,
} from "./user.types";
import { IconEdit } from "@tabler/icons-react";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";

const UserModal = ({
  opened,
  onClose,
  mode,
  user,
  onSubmit,
  loading,
  onModeChange,
}: UserModalProps) => {
  const { t } = useTranslation();
  const isView = mode === ModalMode.VIEW;

  const form = useForm<Partial<IUser>>({
    initialValues: {
      user_name: undefined,
      full_name: undefined,
      email: undefined,
      phone_number: null,
      birth: null,
      address: null,
      company: null,
      role_id: undefined,
      status: "INACTIVE",
      room_id: null,
      password: "",
    },
    validate: {
      user_name: (value) =>
        mode === ModalMode.CREATE && !value
          ? t("validation.username_required")
          : null,
      full_name: (value) => (!value ? t("validation.fullname_required") : null),
      email: (value) =>
        mode === ModalMode.CREATE && (!value || !/^\S+@\S+$/.test(value))
          ? t("validation.email_invalid")
          : null,
      password: (value) =>
        mode === ModalMode.CREATE && (!value || (value?.length || 0) < 6)
          ? t("validation.password_length")
          : null,
      room_id: (value, { role_id }) =>
        role_id === ROLE_MAPPING.RENTER && !value
          ? t("validation.room_required")
          : null,
      company: (value, { role_id }) =>
        role_id === ROLE_MAPPING.RECRUITER && !value
          ? t("validation.company_required")
          : null,
    },
  });

  useEffect(() => {
    if (opened && user) {
      // Map role name string (from API) back to role value for Select component
      form.setValues({
        ...user,
        phone_number: user.phone_number || null,
        birth: user.birth || null,
        address: user.address || null,
        company: user.company || null,
        room_id: user.room_id || null,
        role_id:
          ROLE_MAPPING[user.role_id?.toUpperCase() || ""] ||
          user.role_id?.toString(),
      });
    } else if (opened && mode === ModalMode.CREATE) {
      form.reset();
    }
  }, [opened, user, mode]);

  const handleSubmit = (values: Partial<IUser>) => {
    onSubmit(values);
  };

  const getTitle = () => {
    switch (mode) {
      case ModalMode.CREATE:
        return t("users.modal.create");
      case ModalMode.EDIT:
        return t("users.modal.edit");
      case ModalMode.VIEW:
        return t("users.modal.view");
    }
  };

  return (
    <J2NModal opened={opened} onClose={onClose} title={getTitle()}>
      <form onSubmit={form.onSubmit(handleSubmit)}>
        <Grid>
          <Grid.Col span={{ base: 12, md: 6 }}>
            <TextInput
              label={t("users.modal.username")}
              placeholder={t("users.modal.username")}
              disabled={mode !== ModalMode.CREATE}
              {...form.getInputProps("user_name")}
              required
            />
          </Grid.Col>
          <Grid.Col span={{ base: 12, md: 6 }}>
            <TextInput
              label={t("users.modal.email")}
              placeholder={t("users.modal.email")}
              disabled={mode !== ModalMode.CREATE}
              {...form.getInputProps("email")}
              required
            />
          </Grid.Col>
          {mode === ModalMode.CREATE && (
            <Grid.Col span={{ base: 12, md: 6 }}>
              <PasswordInput
                label={t("password")}
                placeholder={t("password")}
                {...form.getInputProps("password")}
                required
              />
            </Grid.Col>
          )}
          <Grid.Col span={{ base: 12, md: 6 }}>
            <TextInput
              label={t("users.modal.fullname")}
              placeholder={t("users.modal.fullname")}
              readOnly={isView}
              {...form.getInputProps("full_name")}
              required
            />
          </Grid.Col>
          <Grid.Col span={{ base: 12, md: 6 }}>
            <TextInput
              label={t("users.modal.phone")}
              placeholder={t("users.modal.phone")}
              readOnly={isView}
              {...form.getInputProps("phone_number")}
            />
          </Grid.Col>
          <Grid.Col span={{ base: 12, md: 6 }}>
            <TextInput
              label={t("users.modal.birth")}
              placeholder="YYYY-MM-DD"
              readOnly={isView}
              {...form.getInputProps("birth")}
            />
          </Grid.Col>
          <Grid.Col span={{ base: 12, md: 6 }}>
            <Select
              label={t("users.modal.role")}
              placeholder={t("users.modal.role")}
              disabled={isView}
              data={USER_ROLES.map((role) => ({
                value: role.value,
                label: t(role.labelKey),
              }))}
              {...form.getInputProps("role_id")}
            />
          </Grid.Col>
          <Grid.Col span={{ base: 12, md: 6 }}>
            <Select
              label={t("users.modal.status")}
              placeholder={t("users.modal.status")}
              disabled={isView || mode === ModalMode.CREATE}
              data={USER_STATUS_OPTIONS.map((opt) => ({
                value: opt.value,
                label: t(opt.labelKey),
              }))}
              {...form.getInputProps("status")}
            />
          </Grid.Col>
          <Grid.Col span={{ base: 12, md: 6 }}>
            <TextInput
              label={t("users.modal.address")}
              placeholder={t("users.modal.address")}
              readOnly={isView}
              {...form.getInputProps("address")}
            />
          </Grid.Col>
          {form.values.role_id === ROLE_MAPPING.RENTER && (
            <Grid.Col span={{ base: 12, md: 6 }}>
              <TextInput
                label={t("users.modal.room")}
                placeholder={t("users.modal.room")}
                readOnly={isView}
                {...form.getInputProps("room_id")}
                required
              />
            </Grid.Col>
          )}
          {form.values.role_id === ROLE_MAPPING.RECRUITER && (
            <Grid.Col span={{ base: 12, md: 6 }}>
              <TextInput
                label={t("users.modal.company")}
                placeholder={t("users.modal.company")}
                readOnly={isView}
                {...form.getInputProps("company")}
                required
              />
            </Grid.Col>
          )}
        </Grid>

        <Group justify="flex-end" mt="xl">
          <J2NButton j2nType={J2NButtonTypes.SECONDARY} onClick={onClose}>
            {t("users.modal.cancel")}
          </J2NButton>
          {isView ? (
            <J2NButton
              j2nType={J2NButtonTypes.PRIMARY}
              onClick={() => onModeChange?.(ModalMode.EDIT)}
            >
              {t("users.actions.edit")}
            </J2NButton>
          ) : (
            <J2NButton
              type="submit"
              j2nType={J2NButtonTypes.PRIMARY}
              loading={loading}
            >
              {t("users.modal.save")}
            </J2NButton>
          )}
        </Group>
      </form>
    </J2NModal>
  );
};

export default UserModal;
