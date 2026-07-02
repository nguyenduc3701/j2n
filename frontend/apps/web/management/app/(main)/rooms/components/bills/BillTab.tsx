"use client";

import React, { useState } from "react";
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
  Text,
  NumberInput,
  Stack,
  Divider,
  Checkbox,
} from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { modals } from "@mantine/modals";
import { useForm } from "@mantine/form";
import { IconSearch, IconEye, IconSend, IconPlayerPlay, IconX, IconBolt } from "@tabler/icons-react";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { useTranslation } from "@repo/ui/src/providers";
import { roomService } from "@/services/roomServices";
import { IBill, IRoom, IRoomMember } from "@/types/room";
import { useQuery, useMutation, useQueryClient } from "@repo/query";

const ICON_COLOR = "#75616A";

// --- Electric Index Input Modal Content ---
interface ElectricIndexModalProps {
  occupiedRooms: IRoom[];
  onConfirm: (indices: Record<number, number>) => void;
  onCancel: () => void;
  loading: boolean;
  t: (key: string) => string;
}

const ElectricIndexModalContent = ({ occupiedRooms, onConfirm, onCancel, loading, t }: ElectricIndexModalProps) => {
  const [indices, setIndices] = useState<Record<number, number>>(() => {
    const initial: Record<number, number> = {};
    occupiedRooms.forEach((r) => { initial[r.id] = 0; });
    return initial;
  });

  const handleChange = (roomId: number, value: number | string) => {
    setIndices((prev) => ({ ...prev, [roomId]: Number(value) || 0 }));
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        {t("rooms.bills.enter_electric_indices")}
      </Text>
      <Divider />
      {occupiedRooms.map((room) => (
        <Group key={room.id} justify="space-between" align="center">
          <Text fw={500} size="sm">
            {t("rooms.table.room_number")} {room.room_number}
          </Text>
          <NumberInput
            id={`electric-index-room-${room.id}`}
            placeholder="0"
            min={0}
            value={indices[room.id] ?? 0}
            onChange={(val) => handleChange(room.id, val)}
            leftSection={<IconBolt size={14} />}
            style={{ width: 150 }}
          />
        </Group>
      ))}
      <Divider />
      <Group justify="flex-end" mt="xs">
        <J2NButton j2nType={J2NButtonTypes.SECONDARY} onClick={onCancel}>
          {t("common.cancel")}
        </J2NButton>
        <J2NButton j2nType={J2NButtonTypes.PRIMARY} loading={loading} onClick={() => onConfirm(indices)}>
          {t("common.confirm")}
        </J2NButton>
      </Group>
    </Stack>
  );
};

// --- Send Bill Modal Content ---
interface SendBillModalProps {
  members: IRoomMember[];
  onConfirm: (selectedUserIds: number[]) => void;
  onCancel: () => void;
  t: (key: string) => string;
}

const SendBillModalContent = ({ members, onConfirm, onCancel, t }: SendBillModalProps) => {
  const [selected, setSelected] = useState<number[]>(() => {
    return members.filter((m) => m.is_primary).map((m) => m.user_id);
  });

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Select members to receive this bill.
      </Text>
      <Divider />
      {members.length === 0 ? (
        <Text size="sm" c="dimmed" ta="center">No members in this room</Text>
      ) : (
        members.map((m) => (
          <Checkbox
            key={m.id}
            label={`${m.full_name} ${m.is_primary ? '(Primary)' : ''}`}
            checked={selected.includes(m.user_id)}
            onChange={(e) => {
              if (e.currentTarget.checked) {
                setSelected((prev) => [...prev, m.user_id]);
              } else {
                setSelected((prev) => prev.filter((id) => id !== m.user_id));
              }
            }}
          />
        ))
      )}
      <Divider />
      <Group justify="flex-end" mt="xs">
        <J2NButton j2nType={J2NButtonTypes.SECONDARY} onClick={onCancel}>
          {t("common.cancel")}
        </J2NButton>
        <J2NButton
          j2nType={J2NButtonTypes.PRIMARY}
          onClick={() => onConfirm(selected)}
          disabled={selected.length === 0}
        >
          Send
        </J2NButton>
      </Group>
    </Stack>
  );
};

const BillTab = () => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [activePage, setActivePage] = useState(1);
  const pageSize = 10;

  // Search filter states
  const [searchParams, setSearchParams] = useState({
    room_id: "",
    month: null as string | null,
    status: null as string | null,
  });
  const [searchTrigger, setSearchTrigger] = useState(0);

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

  // Electric index modal state
  const [electricModalOpen, setElectricModalOpen] = useState(false);

  // Send Bill Modal state
  const [sendBillModalOpen, setSendBillModalOpen] = useState(false);
  const [selectedSendBill, setSelectedSendBill] = useState<IBill | null>(null);

  // --- React Query Fetching ---
  const { data: billsData, isFetching: loading } = useQuery({
    queryKey: ["bills", activePage, searchParams, searchTrigger],
    queryFn: async () => {
      const res = await roomService.searchBillsAdmin({
        page: activePage,
        size: pageSize,
        room_id: searchParams.room_id ? Number(searchParams.room_id) : undefined,
        month: searchParams.month ? Number(searchParams.month) : undefined,
        status: searchParams.status || undefined,
      });
      return res.data;
    },
  });

  // Fetch occupied rooms (only when modal is opened)
  const { data: occupiedRoomsData, isLoading: roomsLoading } = useQuery({
    queryKey: ["rooms", "occupied"],
    queryFn: async () => {
      const res = await roomService.searchRooms({ page: 1, size: 100, status: "OCCUPIED" });
      return res.data;
    },
    enabled: electricModalOpen,
  });

  const occupiedRooms: IRoom[] = occupiedRoomsData?.rooms ?? [];

  // Fetch room members for send bill modal
  const { data: sendBillMembersData, isLoading: sendBillMembersLoading } = useQuery({
    queryKey: ["roomMembers", selectedSendBill?.room_id],
    queryFn: async () => {
      if (!selectedSendBill) return [];
      const res = await roomService.getRoomMembersByRoomId(selectedSendBill.room_id);
      return res.data;
    },
    enabled: sendBillModalOpen && !!selectedSendBill,
  });

  const sendBillMembers: IRoomMember[] = sendBillMembersData || [];

  const data = billsData?.bills || [];
  const total = billsData?.page?.total || 0;

  // --- Mutations ---
  const calculateAllMutation = useMutation({
    mutationFn: (payload: { month?: number; electric_indices: Record<number, number> }) =>
      roomService.calculateAllBills(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["bills"] });
    },
  });


  const handleSearch = (values: typeof searchForm.values) => {
    setSearchParams(values);
    setActivePage(1);
    setSearchTrigger((prev) => prev + 1);
  };

  const handleClear = () => {
    const initialValues = { room_id: "", month: null, status: null };
    searchForm.setValues(initialValues);
    setSearchParams(initialValues);
    setActivePage(1);
    setSearchTrigger((prev) => prev + 1);
  };

  const handleCalculateAll = () => {
    setElectricModalOpen(true);
  };

  const handleElectricConfirm = async (indices: Record<number, number>) => {
    try {
      const today = new Date();
      await calculateAllMutation.mutateAsync({
        month: today.getMonth() + 1,
        electric_indices: indices,
      });
      setElectricModalOpen(false);
    } catch (e) {
      console.error(e);
    }
  };

  const handleSendBillClick = (bill: IBill) => {
    setSelectedSendBill(bill);
    setSendBillModalOpen(true);
  };

  const handleSendBillConfirm = (selectedUserIds: number[]) => {
    notifications.show({
      title: "Success",
      message: "Sent bill successfully!",
      color: "green",
    });
    setSendBillModalOpen(false);
    setSelectedSendBill(null);
  };

  const bulkLoading = calculateAllMutation.isPending;

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  const columns = [
    { key: "room_number", title: t("rooms.table.room_number") },
    {
      key: "room_price",
      title: t("rooms.table.price"),
      render: (r: IBill) => formatCurrency(r.room_amount),
    },
    {
      key: "period",
      title: t("rooms.bills.month"),
      render: (r: IBill) => {
        const year = r.created_at ? new Date(r.created_at).getFullYear() : new Date().getFullYear();
        return `${r.billing_month}/${year}`;
      },
    },
    {
      key: "electric_amount",
      title: t("electricity"),
      render: (r: IBill) => `${formatCurrency(r.electric_amount)} (${r.electricity_usage} kWh)`,
    },
    {
      key: "water_amount",
      title: t("water"),
      render: (r: IBill) => formatCurrency(r.water_amount),
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
            <Tooltip label="Send bill">
              <ActionIcon variant="subtle" color="blue" onClick={() => handleSendBillClick(r)}>
                <IconSend size={18} />
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
                <Table.Td>
                  {`${selectedBill.billing_month}/${
                    selectedBill.created_at
                      ? new Date(selectedBill.created_at).getFullYear()
                      : new Date().getFullYear()
                  }`}
                </Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Electricity Index (Prev → Current)</Table.Td>
                <Table.Td>{`${selectedBill.electricity_old_index} → ${selectedBill.electricity_new_index} (${selectedBill.electricity_usage} kWh)`}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Electricity Charges</Table.Td>
                <Table.Td>{formatCurrency(selectedBill.electric_amount)}</Table.Td>
              </Table.Tr>

              <Table.Tr>
                <Table.Td fw={500}>Water Charges</Table.Td>
                <Table.Td>{formatCurrency(selectedBill.water_amount)}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Room Base Rent</Table.Td>
                <Table.Td>{formatCurrency(selectedBill.room_amount)}</Table.Td>
              </Table.Tr>
              {selectedBill.service_amount > 0 && (
                <Table.Tr>
                  <Table.Td fw={500}>Service Fees</Table.Td>
                  <Table.Td>{formatCurrency(selectedBill.service_amount)}</Table.Td>
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

      {/* Electric Index Input Modal */}
      <J2NModal
        opened={electricModalOpen}
        onClose={() => setElectricModalOpen(false)}
        title={t("rooms.bills.calculate_all")}
        centered
        size="md"
      >
        {roomsLoading ? (
          <Text size="sm" c="dimmed" ta="center">{t("rooms.bills.loading_rooms")}</Text>
        ) : occupiedRooms.length === 0 ? (
          <Text size="sm" c="dimmed" ta="center">{t("rooms.bills.no_occupied_rooms")}</Text>
        ) : (
          <ElectricIndexModalContent
            occupiedRooms={occupiedRooms}
            onConfirm={handleElectricConfirm}
            onCancel={() => setElectricModalOpen(false)}
            loading={calculateAllMutation.isPending}
            t={t}
          />
        )}
      </J2NModal>

      {/* Send Bill Modal */}
      <J2NModal
        opened={sendBillModalOpen}
        onClose={() => {
          setSendBillModalOpen(false);
          setSelectedSendBill(null);
        }}
        title={`Send Bill - Room ${selectedSendBill?.room_number || ""}`}
        centered
        size="md"
      >
        {sendBillMembersLoading ? (
          <Text size="sm" c="dimmed" ta="center">Loading members...</Text>
        ) : (
          <SendBillModalContent
            members={sendBillMembers}
            onConfirm={handleSendBillConfirm}
            onCancel={() => {
              setSendBillModalOpen(false);
              setSelectedSendBill(null);
            }}
            t={t}
          />
        )}
      </J2NModal>
    </Box>
  );
};

export default BillTab;
