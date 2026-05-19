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
  Table,
  SimpleGrid,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { IconSearch, IconEye, IconReceipt, IconPlayerPlay, IconX } from "@tabler/icons-react";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { useTranslation } from "@repo/ui/src/providers";
import { roomService } from "@/services/roomServices";
import { IBill } from "@/types/room";

const ICON_COLOR = "#75616A";

const BillTab = () => {
  const { t } = useTranslation();
  const [data, setData] = useState<IBill[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [activePage, setActivePage] = useState(1);
  const pageSize = 10;

  // Search filter states
  const [searchParams, setSearchParams] = useState({
    room_id: "",
    month: null as string | null,
    status: null as string | null,
  });

  // Search form state
  const searchForm = useForm({
    initialValues: {
      room_id: "",
      month: null as string | null,
      status: null as string | null,
    },
  });

  // Bill detail Modal state
  const [selectedBill, setSelectedBill] = useState<IBill | null>(null);
  const [bulkLoading, setBulkLoading] = useState(false);

  const fetchBills = async (page = activePage, params = searchParams) => {
    setLoading(true);
    try {
      const res = await roomService.searchBills({
        page,
        size: pageSize,
        room_id: params.room_id ? Number(params.room_id) : undefined,
        month: params.month ? Number(params.month) : undefined,
        status: params.status || undefined,
      });
      if (res.data?.bills) {
        setData(res.data.bills);
        setTotal(res.data.page?.total || 0);
      }
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBills(activePage, searchParams);
  }, [activePage]);

  const handleSearch = (values: typeof searchForm.values) => {
    setSearchParams(values);
    setActivePage(1);
    fetchBills(1, values);
  };

  const handleClear = () => {
    const initialValues = { room_id: "", month: null, status: null };
    searchForm.setValues(initialValues);
    setSearchParams(initialValues);
    setActivePage(1);
    fetchBills(1, initialValues);
  };

  const handleCalculateAll = async () => {
    if (!confirm("Are you sure you want to calculate bills for all occupied rooms?")) return;
    setBulkLoading(true);
    try {
      const today = new Date();
      await roomService.calculateAllBills(today.getMonth() + 1);
      fetchBills();
    } catch (e) {
      console.error(e);
    } finally {
      setBulkLoading(false);
    }
  };

  const handlePay = async (billId: string) => {
    if (!confirm("Proceed with marking this invoice as paid?")) return;
    try {
      await roomService.payBill(billId);
      fetchBills();
    } catch (e) {
      console.error(e);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  const columns = [
    { key: "room_number", title: t("rooms.table.room_number") },
    {
      key: "period",
      title: t("rooms.bills.month"),
      render: (r: IBill) => `${r.month}/${r.year}`,
    },
    {
      key: "electric_amount",
      title: t("electricity"),
      render: (r: IBill) => `${r.electric_usage} kWh (${formatCurrency(r.electric_amount)})`,
    },
    {
      key: "water_amount",
      title: t("water"),
      render: (r: IBill) => `${r.water_usage} m³ (${formatCurrency(r.water_amount)})`,
    },
    {
      key: "total_amount",
      title: t("rooms.bills.amount"),
      render: (r: IBill) => <strong>{formatCurrency(r.total_amount)}</strong>,
    },
    {
      key: "status",
      title: t("rooms.table.status"),
      render: (r: IBill) => (
        <Badge color={r.status === "PAID" ? "green" : "red"}>
          {r.status}
        </Badge>
      ),
    },
    {
      key: "actions",
      title: t("rooms.table.actions"),
      render: (r: IBill) => (
        <Group gap="xs">
          <Tooltip label="View details">
            <ActionIcon variant="subtle" style={{ color: ICON_COLOR }} onClick={() => setSelectedBill(r)}>
              <IconEye size={18} />
            </ActionIcon>
          </Tooltip>
          {r.status === "UNPAID" && (
            <Tooltip label="Pay bill">
              <ActionIcon variant="subtle" color="green" onClick={() => handlePay(r.id)}>
                <IconReceipt size={18} />
              </ActionIcon>
            </Tooltip>
          )}
        </Group>
      ),
    },
  ];

  return (
    <Box>
      <Group justify="flex-end" mb="md">
        <J2NButton
          onClick={handleCalculateAll}
          leftSection={<IconPlayerPlay size={16} />}
          loading={bulkLoading}
          j2nType={J2NButtonTypes.PRIMARY}
        >
          {t("rooms.bills.calculate_all")}
        </J2NButton>
      </Group>

      <Box mb="md">
        <form onSubmit={searchForm.onSubmit(handleSearch)}>
          <SimpleGrid cols={{ base: 1, sm: 2, md: 3 }} spacing="md">
            <TextInput
              label={t("rooms.table.room_number")}
              placeholder={t("rooms.table.room_number")}
              {...searchForm.getInputProps("room_id")}
            />
            <Select
              label={t("rooms.bills.month")}
              placeholder={t("rooms.bills.month")}
              data={Array.from({ length: 12 }, (_, i) => String(i + 1))}
              clearable
              {...searchForm.getInputProps("month")}
            />
            <Select
              label={t("rooms.table.status")}
              placeholder={t("rooms.table.status")}
              data={["PAID", "UNPAID"]}
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

      <J2NTable<IBill>
        columns={columns}
        data={data}
        pageSize={pageSize}
        activePage={activePage}
        total={total}
        onPageChange={setActivePage}
        loading={loading}
      />

      <J2NModal
        opened={!!selectedBill}
        onClose={() => setSelectedBill(null)}
        title={t("rooms.modal.view_title").replace("{number}", selectedBill?.room_number || "")}
        centered
        size="md"
      >
        {selectedBill && (
          <Table verticalSpacing="sm" withRowBorders>
            <Table.Tbody>
              <Table.Tr>
                <Table.Td fw={500}>Billing Month</Table.Td>
                <Table.Td>{`${selectedBill.month}/${selectedBill.year}`}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Electricity Index (Prev → Current)</Table.Td>
                <Table.Td>{`${selectedBill.previous_electric_index} → ${selectedBill.current_electric_index} (${selectedBill.electric_usage} kWh)`}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Electricity Charges</Table.Td>
                <Table.Td>{formatCurrency(selectedBill.electric_amount)}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Water Index (Prev → Current)</Table.Td>
                <Table.Td>{`${selectedBill.previous_water_index} → ${selectedBill.current_water_index} (${selectedBill.water_usage} m³)`}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Water Charges</Table.Td>
                <Table.Td>{formatCurrency(selectedBill.water_amount)}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Room Base Rent</Table.Td>
                <Table.Td>{formatCurrency(selectedBill.room_amount)}</Table.Td>
              </Table.Tr>
              {selectedBill.other_fees_amount > 0 && (
                <Table.Tr>
                  <Table.Td fw={500}>Additional Fees</Table.Td>
                  <Table.Td>{formatCurrency(selectedBill.other_fees_amount)}</Table.Td>
                </Table.Tr>
              )}
              <Table.Tr>
                <Table.Td fw={600}>Total Due</Table.Td>
                <Table.Td fw={600} style={{ color: "var(--mantine-color-blue-filled)" }}>
                  {formatCurrency(selectedBill.total_amount)}
                </Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Status</Table.Td>
                <Table.Td>
                  <Badge color={selectedBill.status === "PAID" ? "green" : "red"}>
                    {selectedBill.status}
                  </Badge>
                </Table.Td>
              </Table.Tr>
            </Table.Tbody>
          </Table>
        )}
        <Group justify="flex-end" mt="xl">
          <J2NButton onClick={() => setSelectedBill(null)} j2nType={J2NButtonTypes.SECONDARY}>{t("common.cancel")}</J2NButton>
        </Group>
      </J2NModal>
    </Box>
  );
};

export default BillTab;
