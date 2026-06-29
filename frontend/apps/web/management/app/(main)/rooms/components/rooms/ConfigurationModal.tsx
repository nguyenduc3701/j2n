"use client";

import { IRoom } from "@/types/room";
import { Tabs } from "@mantine/core";
import J2NModal from "@repo/ui/src/components/atoms/J2NModal";
import { useTranslation } from "@repo/ui/src/providers";
import { useState } from "react";
import MembersTab from "./MembersTab";
import RoomAssetsTab from "../assets-catalog/RoomAssetsTab";
import RoomFeesTab from "../fees/RoomFeesTab";
import RoomBillingTab from "../bills/RoomBillingTab";

interface ConfigurationModalProps {
  opened: boolean;
  onClose: () => void;
  room: IRoom | null;
  onRoomUpdate?: (updatedRoom: IRoom) => void;
}

const ConfigurationModal = ({
  opened,
  onClose,
  room,
  onRoomUpdate,
}: ConfigurationModalProps) => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState<string | null>("members");

  const handleClose = () => {
    setActiveTab("members");
    onClose();
  };

  return (
    <J2NModal
      opened={opened}
      onClose={handleClose}
      title={t("rooms.actions.configuration_title").replace(
        "{number}",
        room?.room_number || "",
      )}
      size="xl"
      centered
    >
      {room && (
        <Tabs value={activeTab} onChange={setActiveTab} variant="outline">
          <Tabs.List>
            <Tabs.Tab value="members">{t("rooms.modal.tabs.members")}</Tabs.Tab>
            <Tabs.Tab value="assets">{t("rooms.modal.tabs.assets")}</Tabs.Tab>
            <Tabs.Tab value="fees">{t("rooms.modal.tabs.fees")}</Tabs.Tab>
            <Tabs.Tab value="billing">{t("rooms.modal.tabs.billing")}</Tabs.Tab>
          </Tabs.List>

          <Tabs.Panel value="members" pt="md">
            <MembersTab room={room} opened={opened} onRoomUpdate={onRoomUpdate} />
          </Tabs.Panel>

          <Tabs.Panel value="assets" pt="md">
            <RoomAssetsTab room={room} opened={opened} onRoomUpdate={onRoomUpdate} />
          </Tabs.Panel>

          <Tabs.Panel value="fees" pt="md">
            <RoomFeesTab room={room} opened={opened} />
          </Tabs.Panel>

          <Tabs.Panel value="billing" pt="md">
            <RoomBillingTab room={room} opened={opened} />
          </Tabs.Panel>
        </Tabs>
      )}
    </J2NModal>
  );
};

export default ConfigurationModal;
