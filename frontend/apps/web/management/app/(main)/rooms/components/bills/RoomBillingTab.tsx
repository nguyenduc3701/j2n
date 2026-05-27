"use client";

import { roomService } from "@/services/roomServices";
import { IRoom } from "@/types/room";
import { ActionIcon, Badge, Group, Table } from "@mantine/core";
import { useQuery } from "@repo/query";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { IconRefresh } from "@tabler/icons-react";
import { useState } from "react";

interface RoomBillingTabProps {
  room: IRoom;
  opened: boolean;
}

const RoomBillingTab = ({ room, opened }: RoomBillingTabProps) => {
  const { t } = useTranslation();
  const [tabLoading, setTabLoading] = useState(false);

  const { data: roomBills = [], refetch: refetchBills } = useQuery({
    queryKey: ["room-bills", room.id],
    queryFn: async () => {
      const res = await roomService.getBillsByRoomId(room.id);
      return res.data || [];
    },
    enabled: opened && !!room.id,
  });

  const formatCurrency = (amount: number) =>
    new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);

  const handleCalculateBill = async () => {
    try {
      setTabLoading(true);
      const today = new Date();
      await roomService.calculateBill({
        room_id: room.id,
        month: today.getMonth() + 1,
        year: today.getFullYear(),
        current_electric_index: 0,
        current_water_index: 0,
      });
      refetchBills();
    } catch (e) {
      console.error("Failed to calculate bill", e);
    } finally {
      setTabLoading(false);
    }
  };

  return (
    <>
      <Group justify="space-between" mb="md">
        <J2NButton
          onClick={handleCalculateBill}
          loading={tabLoading}
          j2nType={J2NButtonTypes.PRIMARY}
        >
          {t("rooms.bills.calculate")}
        </J2NButton>
        <ActionIcon variant="subtle" onClick={() => refetchBills()}>
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
    </>
  );
};

export default RoomBillingTab;
