"use client";

import {
  ICreateCategoryPayload,
  productService,
} from "@/services/productServices";
import { IProductCategory } from "@/types/product";
import { ActionIcon, Badge, Box, Group, Text, Tooltip } from "@mantine/core";
import { openConfirmModal } from "@mantine/modals";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { useTranslation } from "@repo/ui/src/providers";
import { IconEdit, IconPlus, IconTrash } from "@tabler/icons-react";
import { useState } from "react";
import { useMutation, useQueryClient } from "@repo/query";
import CategoryModal from "./CategoryModal";
import { CategoryTabProps } from "./travel.types";

const TRAVEL_TYPE = "TOUR";
const ICON_COLOR = "#75616A";

const CategoryTab = ({ categories, loading }: CategoryTabProps) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [modalOpened, setModalOpened] = useState(false);
  const [selectedCategory, setSelectedCategory] =
    useState<IProductCategory | null>(null);

  // ── Mutations ─────────────────────────────────────────────────────────────

  const createCategoryMutation = useMutation({
    mutationFn: (payload: ICreateCategoryPayload) => productService.createCategory(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories", TRAVEL_TYPE] });
    },
  });

  const updateCategoryMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: ICreateCategoryPayload }) =>
      productService.updateCategory(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories", TRAVEL_TYPE] });
    },
  });

  const deleteCategoryMutation = useMutation({
    mutationFn: (id: number) => productService.deleteCategory(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories", TRAVEL_TYPE] });
    },
  });

  const handleOpenCreate = () => {
    setSelectedCategory(null);
    setModalOpened(true);
  };

  const handleOpenEdit = (category: IProductCategory) => {
    setSelectedCategory(category);
    setModalOpened(true);
  };

  const handleDelete = (category: IProductCategory) => {
    openConfirmModal({
      title: t("travelManagement.categories.edit_title"),
      children: (
        <Text size="sm">{t("travelManagement.categories.confirm_delete")}</Text>
      ),
      labels: {
        confirm: t("common.confirm"),
        cancel: t("common.cancel"),
      },
      confirmProps: { color: "red" },
      onConfirm: async () => {
        try {
          await deleteCategoryMutation.mutateAsync(category.id);
        } catch (e) {
          console.error(e);
        }
      },
    });
  };

  const handleSubmit = async (values: Partial<IProductCategory>) => {
    try {
      const payload: ICreateCategoryPayload = {
        name: values.name!,
        slug: values.slug!,
        type: TRAVEL_TYPE,
      };
      if (selectedCategory) {
        await updateCategoryMutation.mutateAsync({ id: selectedCategory.id, payload });
      } else {
        await createCategoryMutation.mutateAsync(payload);
      }
      setModalOpened(false);
    } catch (e) {
      console.error(e);
    }
  };

  const columns = [
    {
      key: "name",
      title: t("travelManagement.categories.table.name"),
    },
    {
      key: "slug",
      title: t("travelManagement.categories.table.slug"),
      render: (record: IProductCategory) => (
        <Badge variant="light" color="grape">
          {record.slug}
        </Badge>
      ),
    },
    {
      key: "type",
      title: t("travelManagement.categories.table.type"),
      render: (record: IProductCategory) => (
        <Badge variant="outline">{record.type}</Badge>
      ),
    },
    {
      key: "actions",
      title: t("travelManagement.categories.table.actions"),
      render: (record: IProductCategory) => (
        <Group gap="xs">
          <Tooltip label={t("users.actions.edit")}>
            <ActionIcon
              variant="subtle"
              style={{ color: ICON_COLOR }}
              onClick={() => handleOpenEdit(record)}
            >
              <IconEdit size={18} />
            </ActionIcon>
          </Tooltip>
          <Tooltip label={t("users.actions.delete")}>
            <ActionIcon
              variant="subtle"
              color="red"
              onClick={() => handleDelete(record)}
            >
              <IconTrash size={18} />
            </ActionIcon>
          </Tooltip>
        </Group>
      ),
    },
  ];

  const submitLoading = createCategoryMutation.isPending || updateCategoryMutation.isPending;

  return (
    <Box>
      <Group justify="flex-end" mb="md">
        <J2NButton
          leftSection={<IconPlus size={16} />}
          j2nType={J2NButtonTypes.PRIMARY}
          onClick={handleOpenCreate}
        >
          {t("travelManagement.categories.create")}
        </J2NButton>
      </Group>

      <J2NTable<IProductCategory>
        columns={columns}
        data={categories}
        loading={loading}
        highlightOnHover
        verticalSpacing="sm"
        horizontalSpacing="md"
        withTableBorder={false}
      />

      <CategoryModal
        opened={modalOpened}
        category={selectedCategory}
        loading={submitLoading}
        onClose={() => setModalOpened(false)}
        onSubmit={handleSubmit}
      />
    </Box>
  );
};

export default CategoryTab;
