"use client";

import { Box, Group, Paper, Tabs } from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import TransTitle from "@repo/components/molecules/J2NTitle/TransTitle";
import { useTranslation } from "@repo/ui/src/providers";
import {
  IconComponents,
  IconReceipt,
  IconSettings,
  IconSmartHome,
  IconPlus,
} from "@tabler/icons-react";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { modals } from "@mantine/modals";
import { useEffect, useState } from "react";

import AssetTab from "./components/assets-catalog/AssetTab";
import BillTab from "./components/bills/BillTab";
import CalculateRoomModal from "./components/rooms/CalculateRoomModal";
import ConfigurationModal from "./components/rooms/ConfigurationModal";
import FeeTab from "./components/fees/FeeTab";
import RoomModal from "./components/rooms/RoomModal";
import RoomSearchForm from "./components/rooms/RoomSearchForm";
import RoomTable from "./components/rooms/RoomTable";

import { roomService } from "@/services/roomServices";
import { IRoom } from "@/types/room";
import { ModalMode } from "./components/rooms/room.types";

const RoomsPage = () => {
  const { t } = useTranslation();
  const [activeTab, setActiveTab] = useState<string | null>("rooms");

  // Rooms list state
  const [rooms, setRooms] = useState<IRoom[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [activePage, setActivePage] = useState(1);
  const [searchParams, setSearchParams] = useState<any>({});
  const pageSize = 10;

  // Room modal state
  const [modalOpened, setModalOpened] = useState(false);
  const [modalMode, setModalMode] = useState<ModalMode>(ModalMode.VIEW);
  const [selectedRoom, setSelectedRoom] = useState<IRoom | null>(null);
  const [submitLoading, setSubmitLoading] = useState(false);

  // Calculate modal state
  const [calculateModalOpened, setCalculateModalOpened] = useState(false);
  const [calculateRoom, setCalculateRoom] = useState<IRoom | null>(null);

  // Configuration modal state
  const [configModalOpened, setConfigModalOpened] = useState(false);
  const [configRoom, setConfigRoom] = useState<IRoom | null>(null);

  const fetchRooms = async (page = activePage, params = searchParams) => {
    setLoading(true);
    try {
      const res = await roomService.searchRooms({
        page,
        size: pageSize,
        ...params,
      });
      if (res.data?.rooms) {
        setRooms(res.data.rooms);
        setTotal(res.data.page?.total || 0);
      }
    } catch (e) {
      console.error("Failed to fetch rooms", e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activeTab === "rooms") {
      fetchRooms(activePage, searchParams);
    }
  }, [activePage, activeTab]);

  const handleSearch = (values: any) => {
    setSearchParams(values);
    setActivePage(1);
    fetchRooms(1, values);
  };

  const handleClear = () => {
    setSearchParams({});
    setActivePage(1);
    fetchRooms(1, {});
  };

  const handleViewRoom = async (room: IRoom) => {
    setModalMode(ModalMode.VIEW);
    setSelectedRoom(room);
    setModalOpened(true);
    // Fetch full detail of the room to get assets/fees/members
    try {
      const detailRes = await roomService.getRoomById(room.id);
      if (detailRes.data) {
        setSelectedRoom(detailRes.data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleCreateRoom = () => {
    setSelectedRoom(null);
    setModalMode(ModalMode.CREATE);
    setModalOpened(true);
  };

  const handleEditRoom = (room: IRoom) => {
    setModalMode(ModalMode.EDIT);
    setSelectedRoom(room);
    setModalOpened(true);
  };

  const handleCalculateRoom = (room: IRoom) => {
    setCalculateRoom(room);
    setCalculateModalOpened(true);
  };

  const handleConfigureRoom = async (room: IRoom) => {
    setConfigRoom(room);
    setConfigModalOpened(true);
    try {
      const detailRes = await roomService.getRoomById(room.id);
      if (detailRes.data) {
        setConfigRoom(detailRes.data);
      }
    } catch (e) {
      console.error(e);
    }
  };

  const handleDeleteRoom = (room: IRoom) => {
    modals.openConfirmModal({
      title: t("rooms.actions.delete"),
      centered: true,
      children: (
        <span style={{ fontSize: "14px" }}>
          {t("rooms.actions.confirm_delete").replace("{number}", room.room_number)}
        </span>
      ),
      labels: { confirm: t("common.confirm"), cancel: t("common.cancel") },
      confirmProps: { color: "#75616a" },
      onConfirm: async () => {
        try {
          const res = await roomService.deleteRoom(room.id);
          if (res && res.status === 200) {
            fetchRooms(activePage, searchParams);
          }
        } catch (e) {
          console.error("Failed to delete room", e);
        }
      },
    });
  };

  const handleModalSubmit = async (values: Partial<IRoom>) => {
    setSubmitLoading(true);
    try {
      if (modalMode === ModalMode.CREATE) {
        await roomService.createRoom(values);
      } else {
        if (!selectedRoom) return;
        await roomService.updateRoom(selectedRoom.id, values);
      }
      setModalOpened(false);
      fetchRooms(activePage, searchParams);
    } catch (e) {
      console.error(e);
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <J2NMotionFade>
      <div className="room-wrapper px-25 pt-25">
        <Group justify="space-between" mb="lg">
          <TransTitle tKey="Rooms" />
        </Group>

        <Tabs
          value={activeTab}
          onChange={setActiveTab}
          color={"var(--color-j2n-grape-deep-400)"}
        >
          <Tabs.List mb="md">
            <Tabs.Tab value="rooms" leftSection={<IconSmartHome size={18} />}>
              {t("rooms.tabs.rooms")}
            </Tabs.Tab>
            <Tabs.Tab value="assets" leftSection={<IconComponents size={18} />}>
              {t("rooms.tabs.assets")}
            </Tabs.Tab>
            <Tabs.Tab value="bills" leftSection={<IconReceipt size={18} />}>
              {t("rooms.tabs.bills")}
            </Tabs.Tab>
            <Tabs.Tab value="fees" leftSection={<IconSettings size={18} />}>
              {t("rooms.tabs.fees")}
            </Tabs.Tab>
          </Tabs.List>

          <Tabs.Panel value="rooms">
            <Paper shadow="sm" radius="md" p="md" withBorder>
              <Group justify="flex-end" mb="md">
                <J2NButton
                  onClick={handleCreateRoom}
                  leftSection={<IconPlus size={16} />}
                  j2nType={J2NButtonTypes.PRIMARY}
                >
                  {t("rooms.create")}
                </J2NButton>
              </Group>
              <RoomSearchForm onSearch={handleSearch} onClear={handleClear} />
              <Box mt="md">
                <RoomTable
                  data={rooms}
                  loading={loading}
                  total={total}
                  pageSize={pageSize}
                  activePage={activePage}
                  onPageChange={setActivePage}
                  onView={handleViewRoom}
                  onEdit={handleEditRoom}
                  onCalculate={handleCalculateRoom}
                  onConfiguration={handleConfigureRoom}
                  onDelete={handleDeleteRoom}
                />
              </Box>
            </Paper>
          </Tabs.Panel>

          <Tabs.Panel value="assets">
            <Paper shadow="sm" radius="md" p="md" withBorder>
              <AssetTab />
            </Paper>
          </Tabs.Panel>

          <Tabs.Panel value="bills">
            <Paper shadow="sm" radius="md" p="md" withBorder>
              <BillTab />
            </Paper>
          </Tabs.Panel>

          <Tabs.Panel value="fees">
            <Paper shadow="sm" radius="md" p="md" withBorder>
              <FeeTab />
            </Paper>
          </Tabs.Panel>
        </Tabs>

        <RoomModal
          opened={modalOpened}
          onClose={() => setModalOpened(false)}
          mode={modalMode}
          room={selectedRoom}
          onSubmit={handleModalSubmit}
          loading={submitLoading}
          onModeChange={setModalMode}
        />

        <CalculateRoomModal
          opened={calculateModalOpened}
          onClose={() => setCalculateModalOpened(false)}
          room={calculateRoom}
        />

        <ConfigurationModal
          opened={configModalOpened}
          onClose={() => setConfigModalOpened(false)}
          room={configRoom}
        />
      </div>
    </J2NMotionFade>
  );
};

export default RoomsPage;
