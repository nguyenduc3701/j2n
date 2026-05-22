"use client";

import React from "react";
import { ActionIcon, Group, Tooltip } from "@mantine/core";
import { IconEdit, IconEye, IconTrash } from "@tabler/icons-react";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { useTranslation } from "@repo/ui/src/providers";
import { IProduct } from "@/types/product";
import { TourTableProps } from "./travel.types";

const ICON_COLOR = "#75616A";

const TourTable = ({ data, loading, onView, onEdit, onDelete }: TourTableProps) => {
  const { t } = useTranslation();

  const columns = [
    {
      key: "title",
      title: t("travelManagement.table.title"),
    },
    {
      key: "category",
      title: t("travelManagement.table.category"),
      render: (record: IProduct) => (
        <span>{record.category?.name ?? "-"}</span>
      ),
    },
    {
      key: "price",
      title: t("travelManagement.table.price"),
      render: (record: IProduct) => (
        <span>
          {new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND",
          }).format(record.price)}
        </span>
      ),
    },
    {
      key: "duration",
      title: t("travelManagement.table.duration"),
      render: (record: IProduct) => <span>{record.duration ?? "-"}</span>,
    },
    {
      key: "start_location",
      title: t("travelManagement.table.start_location"),
      render: (record: IProduct) => (
        <span>{record.start_location ?? "-"}</span>
      ),
    },
    {
      key: "actions",
      title: t("travelManagement.table.actions"),
      render: (record: IProduct) => (
        <Group gap="xs">
          <Tooltip label={t("users.actions.view")}>
            <ActionIcon
              variant="subtle"
              style={{ color: ICON_COLOR }}
              onClick={() => onView(record)}
            >
              <IconEye size={18} />
            </ActionIcon>
          </Tooltip>
          <Tooltip label={t("users.actions.edit")}>
            <ActionIcon
              variant="subtle"
              style={{ color: ICON_COLOR }}
              onClick={() => onEdit(record)}
            >
              <IconEdit size={18} />
            </ActionIcon>
          </Tooltip>
          <Tooltip label={t("users.actions.delete")}>
            <ActionIcon
              variant="subtle"
              color="red"
              onClick={() => onDelete(record)}
            >
              <IconTrash size={18} />
            </ActionIcon>
          </Tooltip>
        </Group>
      ),
    },
  ];

  return (
    <J2NTable<IProduct>
      columns={columns}
      data={data}
      highlightOnHover
      verticalSpacing="sm"
      horizontalSpacing="md"
      withTableBorder={false}
      loading={loading}
    />
  );
};

export default TourTable;
