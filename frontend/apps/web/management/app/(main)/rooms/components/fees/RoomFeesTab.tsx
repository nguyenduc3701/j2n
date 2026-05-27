"use client";

import { roomService } from "@/services/roomServices";
import { IRoom } from "@/types/room";
import { Group, MultiSelect, Table } from "@mantine/core";
import { useQuery } from "@repo/query";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { useState } from "react";

interface RoomFeesTabProps {
  room: IRoom;
  opened: boolean;
}

const RoomFeesTab = ({ room, opened }: RoomFeesTabProps) => {
  const { t } = useTranslation();
  const [tabLoading, setTabLoading] = useState(false);
  const [selectedFeeIds, setSelectedFeeIds] = useState<string[]>(
    room.fees ? room.fees.map((rf) => String(rf.fee_id)) : [],
  );

  const { data: availableFees = [] } = useQuery({
    queryKey: ["available-fees"],
    queryFn: async () => {
      const res = await roomService.getActiveFees();
      return res.data || [];
    },
    enabled: opened && !!room.id,
    staleTime: 5 * 60 * 1000,
  });

  const formatCurrency = (amount: number) =>
    new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);

  const handleUpdateRoomFees = async () => {
    try {
      setTabLoading(true);
      const feeIds = selectedFeeIds.map(Number);
      await roomService.updateRoomFees(room.id, feeIds);
      const detailedRoomRes = await roomService.getRoomById(room.id);
      if (detailedRoomRes.data) {
        room.fees = detailedRoomRes.data.fees;
        if (room.fees) {
          setSelectedFeeIds(room.fees.map((rf) => String(rf.fee_id)));
        }
      }
    } catch (e) {
      console.error("Failed to update room fees", e);
    } finally {
      setTabLoading(false);
    }
  };

  return (
    <>
      <Group gap="xs" mb="md" align="flex-end">
        <MultiSelect
          label={t("rooms.tabs.fees")}
          placeholder={t("rooms.fees.create")}
          data={availableFees.map((f) => ({
            value: String(f.id),
            label: `${f.name} (${formatCurrency(f.unit_price)} / ${f.unit_name})`,
          }))}
          value={selectedFeeIds}
          onChange={setSelectedFeeIds}
          searchable
          style={{ flexGrow: 1 }}
        />
        <J2NButton
          onClick={handleUpdateRoomFees}
          loading={tabLoading}
          j2nType={J2NButtonTypes.PRIMARY}
        >
          {t("rooms.modal.save")}
        </J2NButton>
      </Group>

      <Table>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>{t("rooms.fees.name")}</Table.Th>
            <Table.Th>{t("rooms.fees.price")}</Table.Th>
            <Table.Th>{t("rooms.fees.unit")}</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {room.fees?.map((rf) => (
            <Table.Tr key={rf.id}>
              <Table.Td>{rf.fee_name}</Table.Td>
              <Table.Td>{formatCurrency(rf.price)}</Table.Td>
              <Table.Td>
                {availableFees.find((f) => f.id === rf.fee_id)?.unit_name ||
                  "-"}
              </Table.Td>
            </Table.Tr>
          ))}
          {(!room.fees || room.fees.length === 0) && (
            <Table.Tr>
              <Table.Td colSpan={3} style={{ textAlign: "center" }}>
                No fees configured for this room
              </Table.Td>
            </Table.Tr>
          )}
        </Table.Tbody>
      </Table>
    </>
  );
};

export default RoomFeesTab;
