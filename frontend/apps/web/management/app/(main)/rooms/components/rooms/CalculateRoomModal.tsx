"use client";

import { roomService } from "@/services/roomServices";
import { IRoom } from "@/types/room";
import { Divider, Group, NumberInput, Stack, Text } from "@mantine/core";
import { useForm } from "@mantine/form";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";
import { useQueryClient, useMutation } from "@repo/query";
import { useTranslation } from "@repo/ui/src/providers";
import { IconBolt } from "@tabler/icons-react";

interface CalculateRoomModalProps {
  opened: boolean;
  onClose: () => void;
  room: IRoom | null;
}

const CalculateRoomModal = ({
  opened,
  onClose,
  room,
}: CalculateRoomModalProps) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();

  const form = useForm({
    initialValues: {
      electricity_new_index: 0,
    },
  });

  const calculateMutation = useMutation({
    mutationFn: (electricityNewIndex: number) => {
      if (!room) throw new Error("No room selected");
      const today = new Date();
      return roomService.calculateBill({
        room_id: room.id,
        month: today.getMonth() + 1,
        year: today.getFullYear(),
        current_electric_index: electricityNewIndex,
        current_water_index: 0,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["bills"] });
      handleClose();
    },
  });

  const handleClose = () => {
    form.reset();
    onClose();
  };

  const handleSubmit = (values: typeof form.values) => {
    calculateMutation.mutate(values.electricity_new_index);
  };

  return (
    <J2NModal
      opened={opened}
      onClose={handleClose}
      title={t("rooms.actions.calculate_title").replace(
        "{number}",
        room?.room_number || "",
      )}
      centered
      size="sm"
    >
      <form onSubmit={form.onSubmit(handleSubmit)}>
        <Stack gap="md">
          <Text size="sm" c="dimmed">
            {t("rooms.bills.enter_electric_index_single")}
          </Text>
          <Divider />
          <NumberInput
            id="calculate-room-electric-index"
            label={t("rooms.table.electric_index")}
            placeholder="0"
            min={0}
            leftSection={<IconBolt size={16} />}
            {...form.getInputProps("electricity_new_index")}
          />
          {calculateMutation.isError && (
            <Text size="sm" c="red">
              {t("rooms.bills.calculate_error")}
            </Text>
          )}
          <Divider />
          <Group justify="flex-end">
            <J2NButton
              type="button"
              onClick={handleClose}
              j2nType={J2NButtonTypes.SECONDARY}
            >
              {t("common.cancel")}
            </J2NButton>
            <J2NButton
              type="submit"
              loading={calculateMutation.isPending}
              j2nType={J2NButtonTypes.PRIMARY}
            >
              {t("common.confirm")}
            </J2NButton>
          </Group>
        </Stack>
      </form>
    </J2NModal>
  );
};

export default CalculateRoomModal;
