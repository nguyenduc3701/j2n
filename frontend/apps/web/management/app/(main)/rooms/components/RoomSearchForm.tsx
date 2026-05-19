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
  ROOM_STATUS_OPTIONS,
  RoomSearchFormProps,
} from "./room.types";

const RoomSearchForm = ({ onSearch, onClear }: RoomSearchFormProps) => {
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
            label={t("rooms.table.room_number")}
            placeholder={t("rooms.table.room_number")}
            {...form.getInputProps("room_number")}
          />
          <TextInput
            label={t("rooms.table.floor")}
            placeholder={t("rooms.table.floor")}
            type="number"
            {...form.getInputProps("floor")}
          />
          <Select
            label={t("rooms.table.status")}
            placeholder={t("rooms.table.status")}
            data={ROOM_STATUS_OPTIONS.map((opt) => ({
              value: opt.value,
              label: t(opt.labelKey),
            }))}
            clearable
            {...form.getInputProps("status")}
          />
          <TextInput
            label={t("rooms.table.price") + " (Min)"}
            placeholder="Min Price"
            type="number"
            {...form.getInputProps("min_price")}
          />
          <TextInput
            label={t("rooms.table.price") + " (Max)"}
            placeholder="Max Price"
            type="number"
            {...form.getInputProps("max_price")}
          />
          <TextInput
            label={t("rooms.table.area")}
            placeholder={t("rooms.table.area")}
            {...form.getInputProps("area")}
          />
          <TextInput
            label={t("rooms.table.capacity")}
            placeholder={t("rooms.table.capacity")}
            type="number"
            {...form.getInputProps("max_people")}
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
            type="button"
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

export default RoomSearchForm;
