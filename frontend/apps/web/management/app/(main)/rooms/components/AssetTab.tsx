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
  Textarea,
  Pagination,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { IconPlus, IconSearch, IconX, IconEdit, IconTrash } from "@tabler/icons-react";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { useTranslation } from "@repo/ui/src/providers";
import { roomService } from "@/services/roomServices";
import { IAsset } from "@/types/room";

const ICON_COLOR = "#75616A";

const AssetTab = () => {
  const { t } = useTranslation();
  const [data, setData] = useState<IAsset[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [activePage, setActivePage] = useState(1);
  const pageSize = 10;

  // Search filter state
  const [searchParams, setSearchParams] = useState({
    name: "",
    status: null as string | null,
  });

  // Search form state
  const searchForm = useForm({
    initialValues: {
      name: "",
      status: null as string | null,
    },
  });

  // Modal state
  const [modalOpened, setModalOpened] = useState(false);
  const [editingAsset, setEditingAsset] = useState<IAsset | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  const form = useForm({
    initialValues: {
      name: "",
      serial_number: "",
      status: "AVAILABLE",
      description: "",
    },
    validate: {
      name: (value) => (value ? null : "Asset name is required"),
    },
  });

  const fetchAssets = async (page = activePage, params = searchParams) => {
    setLoading(true);
    try {
      const res = await roomService.searchAssets({
        page,
        size: pageSize,
        name: params.name || undefined,
        status: params.status || undefined,
      });
      if (res.data?.assets) {
        setData(res.data.assets);
        setTotal(res.data.page?.total || 0);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAssets(activePage, searchParams);
  }, [activePage]);

  const handleSearch = (values: typeof searchForm.values) => {
    setSearchParams(values);
    setActivePage(1);
    fetchAssets(1, values);
  };

  const handleClear = () => {
    const initialValues = { name: "", status: null };
    searchForm.setValues(initialValues);
    setSearchParams(initialValues);
    setActivePage(1);
    fetchAssets(1, initialValues);
  };

  const handleOpenCreate = () => {
    setEditingAsset(null);
    form.reset();
    setModalOpened(true);
  };

  const handleOpenEdit = (asset: IAsset) => {
    setEditingAsset(asset);
    form.setValues({
      name: asset.name || "",
      serial_number: asset.serial_number || "",
      status: asset.status || "AVAILABLE",
      description: asset.description || "",
    });
    setModalOpened(true);
  };

  const handleDelete = async (id: number) => {
    if (!confirm(t("users.messages.confirm_delete") || "Are you sure?")) return;
    try {
      await roomService.deleteAsset(id);
      fetchAssets();
    } catch (e) {
      console.error(e);
    }
  };

  const handleSubmit = async (values: typeof form.values) => {
    setSubmitLoading(true);
    try {
      if (editingAsset) {
        await roomService.updateAsset(editingAsset.id, values);
      } else {
        await roomService.createAsset(values);
      }
      setModalOpened(false);
      form.reset();
      fetchAssets();
    } catch (e) {
      console.error(e);
    } finally {
      setSubmitLoading(false);
    }
  };

  const columns = [
    { key: "name", title: t("rooms.assets.name") },
    { key: "serial_number", title: t("rooms.assets.serial"), render: (r: IAsset) => r.serial_number || "-" },
    {
      key: "status",
      title: t("rooms.table.status"),
      render: (r: IAsset) => (
        <Badge color={r.status === "AVAILABLE" ? "green" : r.status === "IN_USE" ? "blue" : "orange"}>
          {r.status}
        </Badge>
      ),
    },
    { key: "description", title: t("description"), render: (r: IAsset) => r.description || "-" },
    {
      key: "actions",
      title: t("rooms.table.actions"),
      render: (r: IAsset) => (
        <Group gap="xs">
          <Tooltip label={t("users.actions.edit")}>
            <ActionIcon variant="subtle" style={{ color: ICON_COLOR }} onClick={() => handleOpenEdit(r)}>
              <IconEdit size={18} />
            </ActionIcon>
          </Tooltip>
          <Tooltip label={t("users.actions.delete")}>
            <ActionIcon variant="subtle" color="red" onClick={() => handleDelete(r.id)}>
              <IconTrash size={18} />
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
          {t("rooms.assets.create")}
        </J2NButton>
      </Group>

      <Box mb="md">
        <form onSubmit={searchForm.onSubmit(handleSearch)}>
          <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
            <TextInput
              label={t("rooms.assets.name")}
              placeholder={t("rooms.assets.name")}
              {...searchForm.getInputProps("name")}
            />
            <Select
              label={t("rooms.table.status")}
              placeholder={t("rooms.table.status")}
              data={["AVAILABLE", "IN_USE", "MAINTENANCE"]}
              clearable
              {...searchForm.getInputProps("status")}
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

      <J2NTable<IAsset>
        columns={columns}
        data={data}
        pageSize={pageSize}
        activePage={activePage}
        total={total}
        onPageChange={setActivePage}
        loading={loading}
      />

      <J2NModal
        opened={modalOpened}
        onClose={() => setModalOpened(false)}
        title={editingAsset ? t("users.actions.edit") : t("rooms.assets.create")}
        centered
      >
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <SimpleGrid cols={1} spacing="md">
            <TextInput
              label={t("rooms.assets.name")}
              required
              {...form.getInputProps("name")}
            />
            <TextInput
              label={t("rooms.assets.serial")}
              {...form.getInputProps("serial_number")}
            />
            <Select
              label={t("rooms.table.status")}
              data={["AVAILABLE", "IN_USE", "MAINTENANCE"]}
              required
              {...form.getInputProps("status")}
            />
            <Textarea
              label={t("description")}
              {...form.getInputProps("description")}
            />
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

export default AssetTab;
