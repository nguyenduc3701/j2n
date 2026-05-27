"use client";

import { IRoom } from "@/types/room";
import { Badge, Group, SimpleGrid, Text } from "@mantine/core";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { ModalMode } from "./room.types";

interface GeneralTabProps {
  room: IRoom;
  onModeChange?: (mode: ModalMode) => void;
}

const GeneralTab = ({ room, onModeChange }: GeneralTabProps) => {
  const { t } = useTranslation();

  const formatCurrency = (amount: number) =>
    new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(amount);

  return (
    <>
      <SimpleGrid cols={2} spacing="md">
        <div>
          <Text size="sm" c="dimmed">
            {t("rooms.table.room_number")}
          </Text>
          <Text size="md" fw={500}>
            {room.room_number}
          </Text>
        </div>
        <div>
          <Text size="sm" c="dimmed">
            {t("rooms.table.floor")}
          </Text>
          <Text size="md" fw={500}>
            {room.floor}
          </Text>
        </div>
        <div>
          <Text size="sm" c="dimmed">
            {t("rooms.table.price")}
          </Text>
          <Text size="md" fw={500}>
            {room.base_price ? formatCurrency(room.base_price) : "-"}
          </Text>
        </div>
        <div>
          <Text size="sm" c="dimmed">
            {t("rooms.table.area")}
          </Text>
          <Text size="md" fw={500}>
            {room.area ? `${room.area} m²` : "-"}
          </Text>
        </div>
        <div>
          <Text size="sm" c="dimmed">
            {t("rooms.table.capacity")}
          </Text>
          <Text size="md" fw={500}>
            {room.max_people}
          </Text>
        </div>
        <div>
          <Text size="sm" c="dimmed">
            {t("rooms.table.status")}
          </Text>
          <Badge
            color={
              room.status === "AVAILABLE"
                ? "green"
                : room.status === "OCCUPIED"
                  ? "blue"
                  : "red"
            }
          >
            {room.status}
          </Badge>
        </div>
        <div style={{ gridColumn: "span 2" }}>
          <Text size="sm" c="dimmed">
            {t("description")}
          </Text>
          <Text size="md">{room.description || "-"}</Text>
        </div>
      </SimpleGrid>

      {onModeChange && (
        <Group justify="flex-end" mt="xl">
          <J2NButton
            onClick={() => onModeChange(ModalMode.EDIT)}
            j2nType={J2NButtonTypes.PRIMARY}
          >
            {t("users.actions.edit")}
          </J2NButton>
        </Group>
      )}
    </>
  );
};

export default GeneralTab;
