"use client";

import { roomService } from "@/services/roomServices";
import { userService } from "@/services/userServices";
import { IRoom } from "@/types/room";
import { ActionIcon, Badge, Group, Select, Table, Text, Tooltip } from "@mantine/core";
import { modals } from "@mantine/modals";
import { useQuery, useMutation, useQueryClient } from "@repo/query";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { IconRefresh, IconTrash, IconUserPlus } from "@tabler/icons-react";
import { useState } from "react";

interface MembersTabProps {
  room: IRoom;
  opened: boolean;
  onRoomUpdate?: (updatedRoom: IRoom) => void;
}

const MembersTab = ({ room, opened, onRoomUpdate }: MembersTabProps) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [selectedRenterId, setSelectedRenterId] = useState<string | null>(null);

  const { data: occupants = [], refetch: refetchOccupants } = useQuery({
    queryKey: ["room-occupants", room.id],
    queryFn: async () => {
      const res = await userService.getUsers({
        room_id: room.id,
        role_id: "3", // 3 is the RENTER role ID
        size: 50,
      });
      return res.data?.users || [];
    },
    enabled: opened && !!room.id,
  });

  const { data: availableRenters = [] } = useQuery({
    queryKey: ["available-renters"],
    queryFn: async () => {
      const res = await userService.getUsers({ size: 100, status: "ACTIVE" });
      if (res.data?.users) {
        return res.data.users.filter(
          (u) => u.role_id?.toUpperCase() === "RENTER",
        );
      }
      return [];
    },
    enabled: opened && !!room.id,
    staleTime: 5 * 60 * 1000,
  });

  const mapMemberMutation = useMutation({
    mutationFn: (userIds: number[]) =>
      roomService.mapMemberToRoom({
        room_id: room.id,
        user_ids: userIds,
        is_primary: false,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["room-occupants", room.id] });
      queryClient.invalidateQueries({ queryKey: ["available-renters"] });
      setSelectedRenterId(null);
    },
    onError: (e) => {
      console.error("Failed to map renter", e);
    },
  });

  const removeMemberMutation = useMutation({
    mutationFn: (userId: number) => roomService.deleteRoomMemberByUserId(userId),
    onSuccess: async () => {
      queryClient.invalidateQueries({ queryKey: ["room-occupants", room.id] });
      queryClient.invalidateQueries({ queryKey: ["available-renters"] });
      try {
        const res = await roomService.getRoomById(room.id);
        if (res.data && onRoomUpdate) {
          onRoomUpdate(res.data);
        }
      } catch (e) {
        console.error("Failed to fetch updated room details", e);
      }
    },
    onError: (e) => {
      console.error("Failed to remove renter", e);
    },
  });

  const handleMapRenter = () => {
    if (!selectedRenterId) return;
    mapMemberMutation.mutate([Number(selectedRenterId)]);
  };

  const handleRemoveRenter = (userId: number, fullName: string) => {
    modals.openConfirmModal({
      title: t("rooms.actions.delete"),
      centered: true,
      children: (
        <Text size="sm">
          {t("rooms.actions.confirm_remove_member").replace("{name}", fullName)}
        </Text>
      ),
      labels: { confirm: t("common.confirm"), cancel: t("common.cancel") },
      confirmProps: { color: "red" },
      onConfirm: () => {
        removeMemberMutation.mutate(userId);
      },
    });
  };

  return (
    <>
      <Group justify="space-between" mb="md">
        <Group gap="xs">
          <Select
            id="rooms.membersTab.selectRenter"
            placeholder={t("users.search_placeholder")}
            data={availableRenters.map((u) => ({
              value: String(u.id),
              label: `${u.full_name} (${u.email || u.user_name})`,
            }))}
            value={selectedRenterId}
            onChange={setSelectedRenterId}
            searchable
          />
          <J2NButton
            id="rooms.membersTab.btnMapRenter"
            leftSection={<IconUserPlus size={16} />}
            onClick={handleMapRenter}
            disabled={!selectedRenterId || mapMemberMutation.isPending}
            j2nType={J2NButtonTypes.PRIMARY}
          >
            {t("rooms.assets.map_to_room")}
          </J2NButton>
        </Group>
        <ActionIcon
          id="rooms.membersTab.btnRefreshOccupants"
          variant="subtle"
          onClick={() => refetchOccupants()}
        >
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
            <Table.Th style={{ width: 100 }}>
              {t("rooms.table.actions")}
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {occupants.map((u) => (
            <Table.Tr key={u.id}>
              <Table.Td>{u.full_name}</Table.Td>
              <Table.Td>{u.email || "-"}</Table.Td>
              <Table.Td>{u.phone_number || "-"}</Table.Td>
              <Table.Td>
                <Badge color="blue" variant="light">
                  Renter
                </Badge>
              </Table.Td>
              <Table.Td>
                <Group gap="xs">
                  <Tooltip label="Remove mapping">
                    <ActionIcon
                      id={`rooms.membersTab.btnRemoveRenter.${u.id}`}
                      color="red"
                      variant="subtle"
                      loading={removeMemberMutation.isPending && removeMemberMutation.variables === u.id}
                      onClick={() => handleRemoveRenter(u.id, u.full_name)}
                    >
                      <IconTrash size={16} />
                    </ActionIcon>
                  </Tooltip>
                </Group>
              </Table.Td>
            </Table.Tr>
          ))}
          {occupants.length === 0 && (
            <Table.Tr>
              <Table.Td colSpan={5} style={{ textAlign: "center" }}>
                No occupants assigned to this room
              </Table.Td>
            </Table.Tr>
          )}
        </Table.Tbody>
      </Table>
    </>
  );
};

export default MembersTab;
