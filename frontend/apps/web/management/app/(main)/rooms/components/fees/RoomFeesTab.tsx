"use client";

import { roomService } from "@/services/roomServices";
import { IRoom, IRoomFee } from "@/types/room";
import { Group, MultiSelect } from "@mantine/core";
import { useQuery } from "@repo/query";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NTable from "@repo/ui/src/components/atoms/J2NTable";
import { useTranslation } from "@repo/ui/src/providers";
import { useEffect, useState } from "react";

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

  useEffect(() => {
    if (opened) {
      setSelectedFeeIds(room.fees ? room.fees.map((rf) => String(rf.fee_id)) : []);
    }
  }, [opened, room.fees, room.id]);

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

  const columns = [
    {
      key: "name",
      title: t("rooms.fees.name"),
    },
    {
      key: "unit_price",
      title: t("rooms.fees.price"),
      render: (rf: IRoomFee) => formatCurrency(rf.unit_price),
    },
    {
      key: "unit_name",
      title: t("rooms.fees.unit"),
    },
  ];

  return (
    <>
      <Group gap="xs" mb="md" align="flex-end">
        <MultiSelect
          id="roomFeesTab.multiSelectFees"
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
          id="roomFeesTab.btnSave"
          onClick={handleUpdateRoomFees}
          loading={tabLoading}
          j2nType={J2NButtonTypes.PRIMARY}
        >
          {t("rooms.modal.save")}
        </J2NButton>
      </Group>

      <J2NTable<IRoomFee>
        id="roomFeesTab.tableFees"
        columns={columns}
        data={room.fees || []}
        highlightOnHover
        verticalSpacing="sm"
        horizontalSpacing="md"
        withTableBorder={false}
      />
    </>
  );
};

export default RoomFeesTab;
