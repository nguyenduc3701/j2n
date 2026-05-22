"use client";

import React from "react";
import { Box, Group, SimpleGrid } from "@mantine/core";
import { useForm } from "@mantine/form";
import { IconSearch, IconX } from "@tabler/icons-react";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import {
  initialTourSearchValues,
  TourSearchFormProps,
  TourSearchValues,
} from "./travel.types";

const TourSearchForm = ({ categories, onSearch, onClear }: TourSearchFormProps) => {
  const { t } = useTranslation();
  const form = useForm<TourSearchValues>({
    initialValues: initialTourSearchValues,
  });

  const handleClear = () => {
    form.setValues(initialTourSearchValues);
    onClear();
  };

  return (
    <Box p="md">
      <form onSubmit={form.onSubmit(onSearch)}>
        <SimpleGrid cols={{ base: 1, sm: 2, md: 3 }} spacing="md">
          <div>
            <label
              style={{
                display: "block",
                fontSize: 14,
                fontWeight: 500,
                marginBottom: 4,
              }}
            >
              {t("travelManagement.search.title")}
            </label>
            <input
              type="text"
              placeholder={t("travelManagement.search.title")}
              style={{
                width: "100%",
                padding: "8px 12px",
                border: "1px solid #dee2e6",
                borderRadius: 6,
                fontSize: 14,
                boxSizing: "border-box",
              }}
              {...form.getInputProps("title")}
            />
          </div>
          <div>
            <label
              style={{
                display: "block",
                fontSize: 14,
                fontWeight: 500,
                marginBottom: 4,
              }}
            >
              {t("travelManagement.search.category")}
            </label>
            <select
              style={{
                width: "100%",
                padding: "8px 12px",
                border: "1px solid #dee2e6",
                borderRadius: 6,
                fontSize: 14,
                background: "white",
              }}
              {...form.getInputProps("category_id")}
            >
              <option value="">
                {t("travelManagement.search.category")}
              </option>
              {categories.map((cat) => (
                <option key={cat.id} value={String(cat.id)}>
                  {cat.name}
                </option>
              ))}
            </select>
          </div>
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

export default TourSearchForm;
