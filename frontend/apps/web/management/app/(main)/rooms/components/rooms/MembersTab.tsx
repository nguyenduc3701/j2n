"use client";

import { roomService } from "@/services/roomServices";
import { userService } from "@/services/userServices";
import { IRoom, IRoomMember } from "@/types/room";
import { ActionIcon, Badge, Group, Select, Table, Tooltip } from "@mantine/core";
import { useQuery } from "@repo/query";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { IconRefresh, IconTrash, IconUserPlus } from "@tabler/icons-react";
import { useState } from "react";

interface MembersTabProps {
  room: IRoom;
  opened: boolean;
}

const MembersTab = ({ room, opened }: MembersTabProps) => {
  const { t } = useTranslation();
  const [selectedRenterId, setSelectedRenterId] = useState<string | null>(null);
  const [tabLoading, setTabLoading] = useState(false);

  const { data: occupants = [], refetch: refetchOccupants } = useQuery({
    queryKey: ["room-occupants", room.id],
    queryFn: async () => {
      const res = await userService.getUsers({ room_id: room.id, size: 50 });
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

  const handleMapRenter = async () => {
    if (!selectedRenterId) return;
    try {
      setTabLoading(true);
      await roomService.mapMemberToRoom({
        room_id: room.id,
        user_ids: [Number(selectedRenterId)],
        is_primary: false,
      });
      setSelectedRenterId(null);
      refetchOccupants();
    } catch (e) {
      console.error("Failed to map renter", e);
    } finally {
      setTabLoading(false);
    }
  };

  const handleRemoveRenter = async (userId: number) => {
    try {
      setTabLoading(true);
      const detailedRoomRes = await roomService.getRoomById(room.id);
      const detailedRoom = detailedRoomRes.data;
      if (detailedRoom?.members) {
        const memberMapping = detailedRoom.members.find(
          (m: IRoomMember) => m.user_id === userId,
        );
        if (memberMapping) {
          await roomService.deleteRoomMember(memberMapping.id);
          refetchOccupants();
        }
      }
    } catch (e) {
      console.error("Failed to remove renter", e);
    } finally {
      setTabLoading(false);
    }
  };

  return (
    <>
      <Group justify="space-between" mb="md">
        <Group gap="xs">
          <Select
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
            leftSection={<IconUserPlus size={16} />}
            onClick={handleMapRenter}
            disabled={!selectedRenterId || tabLoading}
            j2nType={J2NButtonTypes.PRIMARY}
          >
            {t("rooms.assets.map_to_room")}
          </J2NButton>
        </Group>
        <ActionIcon variant="subtle" onClick={() => refetchOccupants()}>
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
                      color="red"
                      variant="subtle"
                      loading={tabLoading}
                      onClick={() => handleRemoveRenter(u.id)}
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
