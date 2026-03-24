"use client";

import React from "react";
import { Group, Select, TextInput, SimpleGrid, Box } from "@mantine/core";
import { useForm } from "@mantine/form";
import { IconSearch, IconX } from "@tabler/icons-react";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import {
  initialSearchValues,
  USER_ROLES,
  USER_STATUS_OPTIONS,
  UserSearchFormProps,
} from "./user.types";

const UserSearchForm = ({ onSearch, onClear }: UserSearchFormProps) => {
  const { t } = useTranslation();
  const form = useForm({
    initialValues: initialSearchValues,
  });

  const handleClear = () => {
    form.setValues(initialSearchValues);
    onClear();
  };

  return (
    <Box p="md">
      <form onSubmit={form.onSubmit(onSearch)}>
        <SimpleGrid cols={{ base: 1, sm: 2, md: 3 }} spacing="md">
          <TextInput
            label={t("users.table.username")}
            placeholder={t("users.table.username")}
            {...form.getInputProps("user_name")}
          />
          <TextInput
            label={t("users.table.fullname")}
            placeholder={t("users.table.fullname")}
            {...form.getInputProps("full_name")}
          />
          <TextInput
            label={t("users.table.email")}
            placeholder={t("users.table.email")}
            {...form.getInputProps("email")}
          />
          <Select
            label={t("users.table.role")}
            placeholder={t("users.table.role")}
            data={USER_ROLES.map((role) => ({
              value: role.value,
              label: t(role.labelKey) || t("visitor"), // Fallback for visitor spelling
            }))}
            clearable
            {...form.getInputProps("role_id")}
          />
          <Select
            label={t("users.table.status")}
            placeholder={t("users.table.status")}
            data={USER_STATUS_OPTIONS.map((opt) => ({
              value: opt.value,
              label: t(opt.labelKey),
            }))}
            clearable
            {...form.getInputProps("status")}
          />
        </SimpleGrid>
        <Group justify="flex-end" mt="md">
          <J2NButton
            type="submit"
            leftSection={<IconSearch size={16} />}
            j2nType={J2NButtonTypes.PRIMARY}
          >
            {t("users.buttons.search")}
          </J2NButton>
          <J2NButton
            leftSection={<IconX size={16} />}
            onClick={handleClear}
            j2nType={J2NButtonTypes.SECONDARY}
          >
            {t("users.buttons.clear")}
          </J2NButton>
        </Group>
      </form>
    </Box>
  );
};

export default UserSearchForm;
