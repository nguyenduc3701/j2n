"use client";

import React from "react";
import { ActionIcon, Badge, Group, Tooltip } from "@mantine/core";
import {
  IconEdit,
  IconEye,
  IconTrash,
} from "@tabler/icons-react";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { IUser } from "@/types/user";
import { useTranslation } from "@repo/ui/src/providers";
import { UserTableProps } from "./user.types";

const ICON_COLOR = "#75616A";

const UserTable = ({
  data,
  loading,
  total,
  pageSize,
  activePage,
  onPageChange,
  onView,
  onEdit,
  onDelete,
}: UserTableProps) => {
  const { t } = useTranslation();

  const getRoleInfo = (roleId: string | number) => {
    switch (roleId.toString().toUpperCase()) {
      case "ADMIN":
        return { label: t("admin"), color: "violet" };
      case "RECRUITER":
        return { label: t("recruiter"), color: "orange" };
      case "RENTER":
        return { label: t("renter"), color: "cyan" };
      case "VISITOR":
        return { label: t("visitor"), color: "gray" };
      default:
        return { label: roleId.toString(), color: "gray" };
    }
  };

  const columns = [
    {
      key: "user_name",
      title: t("users.table.username"),
    },
    {
      key: "full_name",
      title: t("users.table.fullname"),
    },
    {
      key: "email",
      title: t("users.table.email"),
    },
    {
      key: "role_id",
      title: t("users.table.role"),
      render: (record: IUser) => {
        const roleInfo = getRoleInfo(record.role_id);
        return (
          <Badge variant="light" color={roleInfo.color}>
            {roleInfo.label}
          </Badge>
        );
      },
    },
    {
      key: "status",
      title: t("users.table.status"),
      render: (record: IUser) => (
        <Badge
          color={record.status === "ACTIVE" ? "green" : "red"}
          variant="outline"
        >
          {t(`users.status.${record.status.toLowerCase()}`)}
        </Badge>
      ),
    },
    {
      key: "actions",
      title: t("users.table.actions"),
      render: (record: IUser) => (
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
              style={{ color: ICON_COLOR }}
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
    <J2NTable<IUser>
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

export default UserTable;
