"use client";

import {
  Group,
  Select,
  SimpleGrid,
  Textarea,
  TextInput,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";
import { useTranslation } from "@repo/ui/src/providers";
import { useEffect } from "react";
import { ModalMode, ROOM_STATUS_OPTIONS, RoomModalProps } from "./room.types";
import GeneralTab from "./GeneralTab";

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

  const form = useForm({
    initialValues: {
      room_number: "",
      floor: 0,
      base_price: 0,
      area: "",
      max_people: 1,
      status: "AVAILABLE",
      description: "",
      current_electric_index: 0,
    },
    validate: {
      room_number: (value) => (value ? null : t("validation.room_required")),
    },
  });

  useEffect(() => {
    if (room && mode !== ModalMode.CREATE) {
      form.setValues({
        room_number: room.room_number || "",
        floor: room.floor || 0,
        base_price: room.base_price || 0,
        area: room.area || "",
        max_people: room.max_people || 1,
        status: room.status || "AVAILABLE",
        description: room.description || "",
        current_electric_index: 0,
      });
    } else {
      form.reset();
    }
  }, [room, mode]);

  const isView = mode === ModalMode.VIEW;

  const handleSubmit = (values: typeof form.values) => {
    const payload = {
      ...values,
      floor: (values.floor as any) !== "" && values.floor !== undefined && values.floor !== null ? Number(values.floor) : undefined,
      base_price: (values.base_price as any) !== "" && values.base_price !== undefined && values.base_price !== null ? Number(values.base_price) : undefined,
      max_people: (values.max_people as any) !== "" && values.max_people !== undefined && values.max_people !== null ? Number(values.max_people) : undefined,
      current_electric_index: (values.current_electric_index as any) !== "" && values.current_electric_index !== undefined && values.current_electric_index !== null ? Number(values.current_electric_index) : undefined,
    };
    onSubmit(payload);
  };

  return (
    <J2NModal
      opened={opened}
      onClose={onClose}
      title={
        isView
          ? t("rooms.modal.view_title").replace(
              "{number}",
              room?.room_number || "",
            )
          : mode === ModalMode.CREATE
          ? t("rooms.create")
          : t("rooms.modal.edit_title").replace(
              "{number}",
              room?.room_number || "",
            )
      }
      size="lg"
      centered
    >
      {isView ? (
        room && <GeneralTab room={room} onModeChange={onModeChange} />
      ) : (
        <form onSubmit={form.onSubmit(handleSubmit)}>
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
            {mode === ModalMode.CREATE && (
              <TextInput
                label={t("rooms.table.electric_index")}
                type="number"
                {...form.getInputProps("current_electric_index")}
              />
            )}
            <Textarea
              label={t("description")}
              style={{ gridColumn: mode === ModalMode.CREATE ? "span 1" : "span 2" }}
              {...form.getInputProps("description")}
            />
          </SimpleGrid>
          <Group justify="flex-end" mt="xl">
            <J2NButton
              type="button"
              onClick={onClose}
              j2nType={J2NButtonTypes.SECONDARY}
            >
              {t("rooms.modal.cancel")}
            </J2NButton>
            <J2NButton
              type="submit"
              loading={loading}
              j2nType={J2NButtonTypes.PRIMARY}
            >
              {t("rooms.modal.save")}
            </J2NButton>
          </Group>
        </form>
      )}
    </J2NModal>
  );
};

export default RoomModal;
