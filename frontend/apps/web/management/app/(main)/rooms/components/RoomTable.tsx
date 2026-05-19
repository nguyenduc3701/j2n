"use client";

import React from "react";
import { ActionIcon, Badge, Group, Tooltip } from "@mantine/core";
import { IconEdit, IconEye } from "@tabler/icons-react";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { IRoom } from "@/types/room";
import { useTranslation } from "@repo/ui/src/providers";
import { RoomTableProps } from "./room.types";

const ICON_COLOR = "#75616A";

const RoomTable = ({
  data,
  loading,
  total,
  pageSize,
  activePage,
  onPageChange,
  onView,
  onEdit,
}: RoomTableProps) => {
  const { t } = useTranslation();

  const getStatusBadge = (status: string) => {
    switch (status.toUpperCase()) {
      case "AVAILABLE":
        return (
          <Badge color="green" variant="light">
            {t("rooms.status.available")}
          </Badge>
        );
      case "OCCUPIED":
        return (
          <Badge color="blue" variant="light">
            {t("rooms.status.occupied")}
          </Badge>
        );
      case "MAINTENANCE":
        return (
          <Badge color="red" variant="light">
            {t("rooms.status.maintenance")}
          </Badge>
        );
      default:
        return (
          <Badge color="gray" variant="light">
            {status}
          </Badge>
        );
    }
  };

  const columns = [
    {
      key: "room_number",
      title: t("rooms.table.room_number"),
    },
    {
      key: "floor",
      title: t("rooms.table.floor"),
    },
    {
      key: "base_price",
      title: t("rooms.table.price"),
      render: (record: IRoom) => {
        return (
          <span>
            {record.base_price
              ? new Intl.NumberFormat("vi-VN", {
                  style: "currency",
                  currency: "VND",
                }).format(record.base_price)
              : t("not_updated")}
          </span>
        );
      },
    },
    {
      key: "area",
      title: t("rooms.table.area"),
      render: (record: IRoom) => <span>{record.area || "-"}</span>,
    },
    {
      key: "max_people",
      title: t("rooms.table.capacity"),
      render: (record: IRoom) => (
        <span>{record.max_people ? `${record.max_people} ${t("users.modal.role").toLowerCase() === "role" ? "people" : "người"}` : "-"}</span>
      ),
    },
    {
      key: "status",
      title: t("rooms.table.status"),
      render: (record: IRoom) => getStatusBadge(record.status),
    },
    {
      key: "actions",
      title: t("rooms.table.actions"),
      render: (record: IRoom) => (
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
        </Group>
      ),
    },
  ];

  return (
    <J2NTable<IRoom>
      columns={columns}
      data={data}
      pageSize={pageSize}
      activePage={activePage}
      total={total}
      onPageChange={onPageChange}
      highlightOnHover
      verticalSpacing="sm"
      horizontalSpacing="md"
      withTableBorder={false}
      loading={loading}
    />
  );
};

export default RoomTable;
