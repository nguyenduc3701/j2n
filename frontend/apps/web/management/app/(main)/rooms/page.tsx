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
} from "@tabler/icons-react";
import { useEffect, useState } from "react";

import AssetTab from "./components/AssetTab";
import BillTab from "./components/BillTab";
import FeeTab from "./components/FeeTab";
import RoomModal from "./components/RoomModal";
import RoomSearchForm from "./components/RoomSearchForm";
import RoomTable from "./components/RoomTable";

import { roomService } from "@/services/roomServices";
import { IRoom } from "@/types/room";
import { ModalMode } from "./components/room.types";

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

  const handleEditRoom = (room: IRoom) => {
    setModalMode(ModalMode.EDIT);
    setSelectedRoom(room);
    setModalOpened(true);
  };

  const handleModalSubmit = async (values: Partial<IRoom>) => {
    if (!selectedRoom) return;
    setSubmitLoading(true);
    try {
      await roomService.updateRoom(selectedRoom.id, values);
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
      </div>
    </J2NMotionFade>
  );
};

export default RoomsPage;
