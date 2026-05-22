"use client";

import React, { useEffect } from "react";
import { Modal, Stack, TextInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { IProductCategory } from "@/types/product";
import { CategoryModalProps } from "./travel.types";

const CategoryModal = ({
  opened,
  category,
  loading,
  onClose,
  onSubmit,
}: CategoryModalProps) => {
  const { t } = useTranslation();

  const form = useForm<Partial<IProductCategory>>({
    initialValues: {
      name: "",
      slug: "",
      type: "travel",
    },
    validate: {
      name: (v) =>
        !v ? t("travelManagement.categories.validation.name_required") : null,
      slug: (v) =>
        !v ? t("travelManagement.categories.validation.slug_required") : null,
    },
  });

  useEffect(() => {
    if (opened) {
      if (category) {
        form.setValues({
          name: category.name,
          slug: category.slug,
          type: category.type,
        });
      } else {
        form.reset();
      }
    }
  }, [opened, category]);

  const title = category
    ? t("travelManagement.categories.edit_title")
    : t("travelManagement.categories.create_title");

  return (
    <Modal
      opened={opened}
      onClose={onClose}
      title={title}
      size="md"
      centered
    >
      <form onSubmit={form.onSubmit(onSubmit)}>
        <Stack gap="md">
          <TextInput
            label={t("travelManagement.categories.name")}
            placeholder={t("travelManagement.categories.name")}
            required
            {...form.getInputProps("name")}
          />
          <TextInput
            label={t("travelManagement.categories.slug")}
            placeholder="e.g. beach-tours"
            required
            {...form.getInputProps("slug")}
          />
          <TextInput
            label={t("travelManagement.categories.type")}
            disabled
            {...form.getInputProps("type")}
          />
        </Stack>

        <Stack gap="sm" mt="xl">
          <J2NButton
            type="submit"
            j2nType={J2NButtonTypes.PRIMARY}
            loading={loading}
            fullWidth
          >
            {t("travelManagement.modal.save")}
          </J2NButton>
          <J2NButton
            type="button"
            j2nType={J2NButtonTypes.SECONDARY}
            onClick={onClose}
            fullWidth
          >
            {t("travelManagement.modal.cancel")}
          </J2NButton>
        </Stack>
      </form>
    </Modal>
  );
};

export default CategoryModal;
