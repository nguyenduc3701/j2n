"use client";

import React, { useEffect, useState } from "react";
import {
  Tabs,
  TextInput,
  Select,
  Textarea,
  SimpleGrid,
  Group,
  Table,
  Badge,
  ActionIcon,
  Tooltip,
  Text,
  Divider,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { IconTrash, IconUserPlus, IconPlus, IconRefresh, IconUserStar } from "@tabler/icons-react";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";
import { useTranslation } from "@repo/ui/src/providers";
import { ModalMode, RoomModalProps, ROOM_STATUS_OPTIONS } from "./room.types";
import { roomService } from "@/services/roomServices";
import { userService } from "@/services/userServices";
import { IAsset, IBill, IRoomFee, IRoomMember } from "@/types/room";
import { IUser } from "@/types/user";

const RoomModal = ({
  opened,
  onClose,
  mode,
  room,
  onSubmit,
  loading,
  onModeChange,
}: RoomModalProps) => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState<string | null>("general");

  // Local state for tabs data
  const [occupants, setOccupants] = useState<IUser[]>([]);
  const [roomBills, setRoomBills] = useState<IBill[]>([]);
  const [availableRenters, setAvailableRenters] = useState<IUser[]>([]);
  const [selectedRenterId, setSelectedRenterId] = useState<string | null>(null);
  const [availableAssets, setAvailableAssets] = useState<IAsset[]>([]);
  const [selectedAssetId, setSelectedAssetId] = useState<string | null>(null);
  const [tabLoading, setTabLoading] = useState(false);

  const form = useForm({
    initialValues: {
      room_number: "",
      floor: 0,
      base_price: 0,
      area: "",
      max_people: 1,
      status: "AVAILABLE",
      current_electric_index: 0,
      description: "",
    },
    validate: {
      room_number: (value) => (value ? null : t("validation.room_required")),
    },
  });

  // Load room details into form when room changes
  useEffect(() => {
    if (room) {
      form.setValues({
        room_number: room.room_number || "",
        floor: room.floor || 0,
        base_price: room.base_price || 0,
        area: room.area || "",
        max_people: room.max_people || 1,
        status: room.status || "AVAILABLE",
        current_electric_index: room.current_electric_index || 0,
        description: room.description || "",
      });
      // Load tabs data if in VIEW mode
      if (mode === ModalMode.VIEW) {
        fetchOccupants();
        fetchBills();
        fetchAvailableRenters();
        fetchAvailableAssets();
      }
    } else {
      form.reset();
    }
    setActiveTab("general");
  }, [room, mode]);

  const fetchOccupants = async () => {
    if (!room) return;
    try {
      setTabLoading(true);
      const res = await userService.getUsers({ room_id: room.id, size: 50 });
      if (res.data?.users) {
        setOccupants(res.data.users);
      }
    } catch (e) {
      console.error("Error fetching occupants", e);
    } finally {
      setTabLoading(false);
    }
  };

  const fetchBills = async () => {
    if (!room) return;
    try {
      const res = await roomService.getBillsByRoomId(room.id);
      if (res.data) {
        setRoomBills(res.data);
      }
    } catch (e) {
      console.error("Error fetching bills", e);
    }
  };

  const fetchAvailableRenters = async () => {
    try {
      // Fetch users with RENTER role who are ACTIVE and not already assigned if possible
      const res = await userService.getUsers({ size: 100, status: "ACTIVE" });
      if (res.data?.users) {
        // Filter users who are renters
        const renters = res.data.users.filter(u => u.role_id?.toUpperCase() === "RENTER");
        setAvailableRenters(renters);
      }
    } catch (e) {
      console.error("Error fetching available renters", e);
    }
  };

  const fetchAvailableAssets = async () => {
    try {
      const res = await roomService.searchAssets({ size: 100, status: "AVAILABLE" });
      if (res.data?.assets) {
        setAvailableAssets(res.data.assets);
      }
    } catch (e) {
      console.error("Error fetching assets", e);
    }
  };

  const handleMapRenter = async () => {
    if (!room || !selectedRenterId) return;
    try {
      setTabLoading(true);
      await roomService.mapMemberToRoom({
        room_id: room.id,
        user_ids: [Number(selectedRenterId)],
        is_primary: false,
      });
      setSelectedRenterId(null);
      fetchOccupants();
    } catch (e) {
      console.error("Failed to map renter", e);
    } finally {
      setTabLoading(false);
    }
  };

  const handleRemoveRenter = async (userId: number) => {
    if (!room) return;
    try {
      setTabLoading(true);
      // RoomMemberEntity ID is needed. Let's find it.
      // Wait! In room.members, we have mapping details. Let's get the room details or fetch mapping if available.
      // If we don't have the room member entity id directly, we can check if it's stored.
      // Wait, let's fetch the room from backend again to get the members
      const detailedRoomRes = await roomService.getRoomById(room.id);
      const detailedRoom = detailedRoomRes.data;
      if (detailedRoom && detailedRoom.members) {
        const memberMapping = detailedRoom.members.find((m: IRoomMember) => m.user_id === userId);
        if (memberMapping) {
          await roomService.deleteRoomMember(memberMapping.id);
          fetchOccupants();
        }
      }
    } catch (e) {
      console.error("Failed to remove renter", e);
    } finally {
      setTabLoading(false);
    }
  };

  const handleTogglePrimary = async (userId: number, currentPrimary: boolean) => {
    if (!room) return;
    try {
      setTabLoading(true);
      const detailedRoomRes = await roomService.getRoomById(room.id);
      const detailedRoom = detailedRoomRes.data;
      if (detailedRoom && detailedRoom.members) {
        const memberMapping = detailedRoom.members.find((m: IRoomMember) => m.user_id === userId);
        if (memberMapping) {
          await roomService.updateRoomMember(memberMapping.id, { is_primary: !currentPrimary });
          fetchOccupants();
        }
      }
    } catch (e) {
      console.error("Failed to toggle primary status", e);
    } finally {
      setTabLoading(false);
    }
  };

  const handleMapAsset = async () => {
    if (!room || !selectedAssetId) return;
    try {
      setTabLoading(true);
      await roomService.mapAssetToRoom({
        room_id: room.id,
        asset_ids: [Number(selectedAssetId)],
      });
      setSelectedAssetId(null);
      // Refresh room assets
      if (room.id) {
        const detailedRoomRes = await roomService.getRoomById(room.id);
        if (detailedRoomRes.data && room) {
          room.assets = detailedRoomRes.data.assets;
        }
      }
      fetchAvailableAssets();
    } catch (e) {
      console.error("Failed to map asset", e);
    } finally {
      setTabLoading(false);
    }
  };

  const handleCalculateBill = async () => {
    if (!room) return;
    try {
      setTabLoading(true);
      const today = new Date();
      await roomService.calculateBill({
        room_id: room.id,
        month: today.getMonth() + 1,
        year: today.getFullYear(),
        current_electric_index: form.values.current_electric_index || 0,
        current_water_index: 0,
      });
      fetchBills();
    } catch (e) {
      console.error("Failed to calculate bill", e);
    } finally {
      setTabLoading(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);
  };

  const isView = mode === ModalMode.VIEW;

  return (
    <J2NModal
      opened={opened}
      onClose={onClose}
      title={
        isView
          ? t("rooms.modal.view_title").replace("{number}", room?.room_number || "")
          : t("rooms.modal.edit_title").replace("{number}", room?.room_number || "")
      }
      size="lg"
      centered
    >
      {isView ? (
        <Tabs value={activeTab} onChange={setActiveTab} variant="outline">
          <Tabs.List>
            <Tabs.Tab value="general">{t("rooms.modal.tabs.general")}</Tabs.Tab>
            <Tabs.Tab value="members">{t("rooms.modal.tabs.members")}</Tabs.Tab>
            <Tabs.Tab value="assets">{t("rooms.modal.tabs.assets")}</Tabs.Tab>
            <Tabs.Tab value="billing">{t("rooms.modal.tabs.billing")}</Tabs.Tab>
          </Tabs.List>

          <Tabs.Panel value="general" pt="md">
            <SimpleGrid cols={2} spacing="md">
              <div>
                <Text size="sm" c="dimmed">
                  {t("rooms.table.room_number")}
                </Text>
                <Text size="md" fw={500}>
                  {room?.room_number}
                </Text>
              </div>
              <div>
                <Text size="sm" c="dimmed">
                  {t("rooms.table.floor")}
                </Text>
                <Text size="md" fw={500}>
                  {room?.floor}
                </Text>
              </div>
              <div>
                <Text size="sm" c="dimmed">
                  {t("rooms.table.price")}
                </Text>
                <Text size="md" fw={500}>
                  {room?.base_price ? formatCurrency(room.base_price) : "-"}
                </Text>
              </div>
              <div>
                <Text size="sm" c="dimmed">
                  {t("rooms.table.area")}
                </Text>
                <Text size="md" fw={500}>
                  {room?.area ? `${room.area} m²` : "-"}
                </Text>
              </div>
              <div>
                <Text size="sm" c="dimmed">
                  {t("rooms.table.capacity")}
                </Text>
                <Text size="md" fw={500}>
                  {room?.max_people}
                </Text>
              </div>
              <div>
                <Text size="sm" c="dimmed">
                  {t("rooms.table.status")}
                </Text>
                <Badge
                  color={
                    room?.status === "AVAILABLE"
                      ? "green"
                      : room?.status === "OCCUPIED"
                      ? "blue"
                      : "red"
                  }
                >
                  {room?.status}
                </Badge>
              </div>
              <div style={{ gridColumn: "span 2" }}>
                <Text size="sm" c="dimmed">
                  {t("description")}
                </Text>
                <Text size="md">{room?.description || "-"}</Text>
              </div>
            </SimpleGrid>
            {onModeChange && (
              <Group justify="flex-end" mt="xl">
                <J2NButton onClick={() => onModeChange(ModalMode.EDIT)} j2nType={J2NButtonTypes.PRIMARY}>
                  {t("users.actions.edit")}
                </J2NButton>
              </Group>
            )}
          </Tabs.Panel>

          <Tabs.Panel value="members" pt="md">
            <Group justify="space-between" mb="md">
              <Group gap="xs">
                <Select
                  placeholder={t("users.search_placeholder")}
                  data={availableRenters.map(u => ({
                    value: String(u.id),
                    label: `${u.full_name} (${u.email || u.user_name})`,
                  }))}
                  value={selectedRenterId}
                  onChange={setSelectedRenterId}
                  searchable
                />
                <J2NButton
                  leftSection={<IconUserPlus size={16} />}
                  onClick={handleMapRenter}
                  disabled={!selectedRenterId}
                  j2nType={J2NButtonTypes.PRIMARY}
                >
                  {t("rooms.assets.map_to_room")}
                </J2NButton>
              </Group>
              <ActionIcon variant="subtle" onClick={fetchOccupants}>
                <IconRefresh size={18} />
              </ActionIcon>
            </Group>

            <Table>
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>{t("fullname")}</Table.Th>
                  <Table.Th>{t("email")}</Table.Th>
                  <Table.Th>{t("phone_number")}</Table.Th>
                  <Table.Th>Primary</Table.Th>
                  <Table.Th style={{ width: 100 }}>{t("rooms.table.actions")}</Table.Th>
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {occupants.map((u) => {
                  // Find member mapping inside room
                  // Since occupants come from search, we check mapping status
                  return (
                    <Table.Tr key={u.id}>
                      <Table.Td>{u.full_name}</Table.Td>
                      <Table.Td>{u.email || "-"}</Table.Td>
                      <Table.Td>{u.phone_number || "-"}</Table.Td>
                      <Table.Td>
                        {/* We don't have isPrimary inside IUser, but we can query room.members */}
                        <Badge color="blue" variant="light">Renter</Badge>
                      </Table.Td>
                      <Table.Td>
                        <Group gap="xs">
                          <Tooltip label="Remove mapping">
                            <ActionIcon
                              color="red"
                              variant="subtle"
                              onClick={() => handleRemoveRenter(u.id)}
                            >
                              <IconTrash size={16} />
                            </ActionIcon>
                          </Tooltip>
                        </Group>
                      </Table.Td>
                    </Table.Tr>
                  );
                })}
                {occupants.length === 0 && (
                  <Table.Tr>
                    <Table.Td colSpan={5} style={{ textAlign: "center" }}>
                      No occupants assigned to this room
                    </Table.Td>
                  </Table.Tr>
                )}
              </Table.Tbody>
            </Table>
          </Tabs.Panel>

          <Tabs.Panel value="assets" pt="md">
            <Group gap="xs" mb="md">
              <Select
                placeholder={t("rooms.assets.name")}
                data={availableAssets.map(a => ({
                  value: String(a.id),
                  label: `${a.name} (${a.serial_number || "No serial"})`,
                }))}
                value={selectedAssetId}
                onChange={setSelectedAssetId}
                searchable
              />
              <J2NButton
                leftSection={<IconPlus size={16} />}
                onClick={handleMapAsset}
                disabled={!selectedAssetId}
                j2nType={J2NButtonTypes.PRIMARY}
              >
                {t("rooms.assets.map_to_room")}
              </J2NButton>
            </Group>

            <Table>
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>{t("rooms.assets.name")}</Table.Th>
                  <Table.Th>{t("rooms.assets.serial")}</Table.Th>
                  <Table.Th>{t("rooms.table.status")}</Table.Th>
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {room?.assets?.map((asset) => (
                  <Table.Tr key={asset.id}>
                    <Table.Td>{asset.name}</Table.Td>
                    <Table.Td>{asset.serial_number || "-"}</Table.Td>
                    <Table.Td>
                      <Badge color="green">{asset.status}</Badge>
                    </Table.Td>
                  </Table.Tr>
                ))}
                {(!room?.assets || room.assets.length === 0) && (
                  <Table.Tr>
                    <Table.Td colSpan={3} style={{ textAlign: "center" }}>
                      No assets assigned to this room
                    </Table.Td>
                  </Table.Tr>
                )}
              </Table.Tbody>
            </Table>
          </Tabs.Panel>

          <Tabs.Panel value="billing" pt="md">
            <Group justify="space-between" mb="md">
              <J2NButton onClick={handleCalculateBill} j2nType={J2NButtonTypes.PRIMARY}>
                {t("rooms.bills.calculate")}
              </J2NButton>
              <ActionIcon variant="subtle" onClick={fetchBills}>
                <IconRefresh size={18} />
              </ActionIcon>
            </Group>

            <Table>
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>{t("rooms.bills.month")}</Table.Th>
                  <Table.Th>{t("rooms.table.electric_index")}</Table.Th>
                  <Table.Th>{t("rooms.bills.amount")}</Table.Th>
                  <Table.Th>{t("rooms.table.status")}</Table.Th>
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {roomBills.map((bill) => (
                  <Table.Tr key={bill.id}>
                    <Table.Td>{`${bill.month}/${bill.year}`}</Table.Td>
                    <Table.Td>{bill.current_electric_index}</Table.Td>
                    <Table.Td>{formatCurrency(bill.total_amount)}</Table.Td>
                    <Table.Td>
                      <Badge color={bill.status === "PAID" ? "green" : "red"}>
                        {bill.status}
                      </Badge>
                    </Table.Td>
                  </Table.Tr>
                ))}
                {roomBills.length === 0 && (
                  <Table.Tr>
                    <Table.Td colSpan={4} style={{ textAlign: "center" }}>
                      No bill history found
                    </Table.Td>
                  </Table.Tr>
                )}
              </Table.Tbody>
            </Table>
          </Tabs.Panel>
        </Tabs>
      ) : (
        <form onSubmit={form.onSubmit(onSubmit)}>
          <SimpleGrid cols={2} spacing="md">
            <TextInput
              label={t("rooms.table.room_number")}
              required
              {...form.getInputProps("room_number")}
            />
            <TextInput
              label={t("rooms.table.floor")}
              type="number"
              required
              {...form.getInputProps("floor")}
            />
            <TextInput
              label={t("rooms.table.price")}
              type="number"
              {...form.getInputProps("base_price")}
            />
            <TextInput
              label={t("rooms.table.area")}
              {...form.getInputProps("area")}
            />
            <TextInput
              label={t("rooms.table.capacity")}
              type="number"
              {...form.getInputProps("max_people")}
            />
            <Select
              label={t("rooms.table.status")}
              data={ROOM_STATUS_OPTIONS.map((opt) => ({
                value: opt.value,
                label: t(opt.labelKey),
              }))}
              {...form.getInputProps("status")}
            />
            <TextInput
              label={t("rooms.table.electric_index")}
              type="number"
              {...form.getInputProps("current_electric_index")}
            />
            <Textarea
              label={t("description")}
              style={{ gridColumn: "span 2" }}
              {...form.getInputProps("description")}
            />
          </SimpleGrid>
          <Group justify="flex-end" mt="xl">
            <J2NButton type="button" onClick={onClose} j2nType={J2NButtonTypes.SECONDARY}>
              {t("rooms.modal.cancel")}
            </J2NButton>
            <J2NButton type="submit" loading={loading} j2nType={J2NButtonTypes.PRIMARY}>
              {t("rooms.modal.save")}
            </J2NButton>
          </Group>
        </form>
      )}
    </J2NModal>
  );
};

export default RoomModal;
