"use client";

import React, { useEffect, useState } from "react";
import {
  Box,
  Group,
  TextInput,
  Select,
  ActionIcon,
  Tooltip,
  Badge,
  SimpleGrid,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { IconEdit, IconPlus } from "@tabler/icons-react";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { useTranslation } from "@repo/ui/src/providers";
import { roomService } from "@/services/roomServices";
import { IFee } from "@/types/room";

const ICON_COLOR = "#75616A";

const FeeTab = () => {
  const { t } = useTranslation();
  const [data, setData] = useState<IFee[]>([]);
  const [loading, setLoading] = useState(false);

  // Modal edit state
  const [modalOpened, setModalOpened] = useState(false);
  const [editingFee, setEditingFee] = useState<IFee | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  const form = useForm({
    initialValues: {
      name: "",
      unit_price: 0,
      unit_name: "",
      is_active: true,
    },
    validate: {
      name: (value) => (value ? null : t("rooms.fees.validation.name_required")),
      unit_price: (value) => (value >= 0 ? null : t("rooms.fees.validation.price_min")),
      unit_name: (value) => (value ? null : t("rooms.fees.validation.unit_required")),
    },
  });

  const fetchFees = async () => {
    setLoading(true);
    try {
      const res = await roomService.getActiveFees();
      if (res.data) {
        setData(res.data);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFees();
  }, []);

  const handleOpenCreate = () => {
    setEditingFee(null);
    form.reset();
    form.setValues({
      name: "",
      unit_price: 0,
      unit_name: "",
      is_active: true,
    });
    setModalOpened(true);
  };

  const handleOpenEdit = (fee: IFee) => {
    setEditingFee(fee);
    form.setValues({
      name: fee.name || "",
      unit_price: fee.unit_price || 0,
      unit_name: fee.unit_name || "",
      is_active: fee.is_active !== undefined ? fee.is_active : true,
    });
    setModalOpened(true);
  };

  const handleSubmit = async (values: typeof form.values) => {
    setSubmitLoading(true);
    try {
      if (editingFee) {
        await roomService.updateFee(editingFee.id, values);
      } else {
        await roomService.createFee(values);
      }
      setModalOpened(false);
      fetchFees();
    } catch (e) {
      console.error(e);
    } finally {
      setSubmitLoading(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  const columns = [
    { key: "name", title: t("rooms.fees.name") },
    {
      key: "price",
      title: t("rooms.fees.price"),
      render: (r: IFee) => `${formatCurrency(r.unit_price)} / ${r.unit_name}`,
    },
    {
      key: "status",
      title: t("rooms.table.status"),
      render: (r: IFee) => (
        <Badge color={r.is_active ? "green" : "gray"}>
          {r.is_active ? t("users.status.active").toUpperCase() : t("users.status.inactive").toUpperCase()}
        </Badge>
      ),
    },
    {
      key: "actions",
      title: t("rooms.table.actions"),
      render: (r: IFee) => (
        <Group gap="xs">
          <Tooltip label={t("rooms.fees.edit_pricing")}>
            <ActionIcon variant="subtle" style={{ color: ICON_COLOR }} onClick={() => handleOpenEdit(r)}>
              <IconEdit size={18} />
            </ActionIcon>
          </Tooltip>
        </Group>
      ),
    },
  ];

  return (
    <Box>
      <Group justify="flex-end" mb="md">
        <J2NButton onClick={handleOpenCreate} leftSection={<IconPlus size={16} />} j2nType={J2NButtonTypes.PRIMARY}>
          {t("rooms.fees.create")}
        </J2NButton>
      </Group>

      <J2NTable<IFee>
        columns={columns}
        data={data}
        pageSize={50}
        activePage={1}
        total={data.length}
        onPageChange={() => {}}
        loading={loading}
      />

      <J2NModal
        opened={modalOpened}
        onClose={() => setModalOpened(false)}
        title={editingFee ? t("rooms.fees.edit_title") : t("rooms.fees.create_title")}
        centered
      >
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <SimpleGrid cols={1} spacing="md">
            <TextInput
              label={t("rooms.fees.name")}
              disabled={!!editingFee}
              required
              {...form.getInputProps("name")}
            />
            <TextInput
              label={t("rooms.fees.price")}
              type="number"
              required
              {...form.getInputProps("unit_price")}
            />
            <TextInput
              label={t("rooms.fees.unit")}
              required
              {...form.getInputProps("unit_name")}
            />
            {editingFee && (
              <Select
                label={t("rooms.table.status")}
                data={[
                  { value: "true", label: t("users.status.active") },
                  { value: "false", label: t("users.status.inactive") }
                ]}
                required
                {...form.getInputProps("is_active")}
                value={form.values.is_active ? "true" : "false"}
                onChange={(val) => form.setFieldValue("is_active", val === "true")}
              />
            )}
          </SimpleGrid>
          <Group justify="flex-end" mt="xl">
            <J2NButton type="button" onClick={() => setModalOpened(false)} j2nType={J2NButtonTypes.SECONDARY}>
              {t("rooms.modal.cancel")}
            </J2NButton>
            <J2NButton type="submit" loading={submitLoading} j2nType={J2NButtonTypes.PRIMARY}>
              {t("rooms.modal.save")}
            </J2NButton>
          </Group>
        </form>
      </J2NModal>
    </Box>
  );
};

export default FeeTab;
