"use client";

import { roomService } from "@/services/roomServices";
import { IAsset, IRoom } from "@/types/room";
import {
  ActionIcon,
  Group,
  NumberInput,
  Select,
  Table,
  Tooltip,
} from "@mantine/core";
import { useQuery } from "@repo/query";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { IconPlus, IconTrash } from "@tabler/icons-react";
import { useState } from "react";

interface RoomAssetsTabProps {
  room: IRoom;
  opened: boolean;
}

const RoomAssetsTab = ({ room, opened }: RoomAssetsTabProps) => {
  const { t } = useTranslation();
  const [selectedAssetId, setSelectedAssetId] = useState<string | null>(null);
  const [mapQuantity, setMapQuantity] = useState<number>(1);
  const [tabLoading, setTabLoading] = useState(false);
  const [localAssets, setLocalAssets] = useState<IAsset[]>(
    room.assets || [],
  );

  const { data: availableAssets = [] } = useQuery({
    queryKey: ["available-assets"],
    queryFn: async () => {
      const res = await roomService.searchAssets({ size: 100 });
      return res.data?.assets || [];
    },
    enabled: opened && !!room.id,
    staleTime: 5 * 60 * 1000,
  });

  const handleMapAsset = () => {
    if (!selectedAssetId) return;
    const assetToAdd = availableAssets.find(
      (a) => Number(a.id) === Number(selectedAssetId),
    );
    if (!assetToAdd) return;

    setLocalAssets((prev) => {
      const existing = prev.find(
        (la) => Number(la.id) === Number(assetToAdd.id),
      );
      if (existing) {
        return prev.map((la) =>
          Number(la.id) === Number(assetToAdd.id)
            ? { ...la, quantity: la.quantity + mapQuantity }
            : la,
        );
      }
      return [...prev, { ...assetToAdd, quantity: mapQuantity }];
    });
    setSelectedAssetId(null);
    setMapQuantity(1);
  };

  const handleUnmapAsset = (index: number) => {
    setLocalAssets((prev) => prev.filter((_, i) => i !== index));
  };

  const handleUpdateLocalQuantity = (index: number, quantity: number) => {
    setLocalAssets((prev) =>
      prev.map((a, i) => (i === index ? { ...a, quantity } : a)),
    );
  };

  const handleSaveAssets = async () => {
    try {
      setTabLoading(true);
      const payload = localAssets.map((asset) => ({
        asset_id: asset.id,
        quantity: asset.quantity,
      }));

      await roomService.updateRoomAssets(room.id, payload);

      const detailedRoomRes = await roomService.getRoomById(room.id);
      if (detailedRoomRes.data) {
        room.assets = detailedRoomRes.data.assets;
        setLocalAssets(detailedRoomRes.data.assets || []);
      }
    } catch (e) {
      console.error("Failed to save room assets", e);
    } finally {
      setTabLoading(false);
    }
  };

  const hasAssetChanges = () => {
    const original = room.assets || [];
    if (original.length !== localAssets.length) return true;
    for (const local of localAssets) {
      const orig = original.find((o) => Number(o.id) === Number(local.id));
      if (!orig || orig.quantity !== local.quantity) return true;
    }
    return false;
  };

  return (
    <>
      <Group gap="xs" mb="md">
        <Select
          placeholder={t("rooms.assets.name")}
          data={availableAssets
            .filter((a) => !localAssets.some((la) => la.id === a.id))
            .map((a) => ({
              value: String(a.id),
              label: a.name,
            }))}
          value={selectedAssetId}
          onChange={setSelectedAssetId}
          searchable
        />
        <NumberInput
          placeholder={t("rooms.assets.quantity")}
          value={mapQuantity}
          onChange={(val) => setMapQuantity(Number(val) || 1)}
          min={1}
          style={{ width: 100 }}
        />
        <J2NButton
          leftSection={<IconPlus size={16} />}
          onClick={handleMapAsset}
          disabled={!selectedAssetId}
          j2nType={J2NButtonTypes.PRIMARY}
        >
          {t("rooms.assets.map_to_room")}
        </J2NButton>
      </Group>

      <Table>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>{t("rooms.assets.name")}</Table.Th>
            <Table.Th>{t("rooms.assets.quantity")}</Table.Th>
            <Table.Th>{t("description")}</Table.Th>
            <Table.Th style={{ width: 100 }}>
              {t("rooms.table.actions")}
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {localAssets.map((asset, index) => (
            <Table.Tr key={`asset-${asset.id || "new"}-${index}`}>
              <Table.Td>{asset.name}</Table.Td>
              <Table.Td>
                <NumberInput
                  value={asset.quantity}
                  onChange={(val) =>
                    handleUpdateLocalQuantity(index, Number(val) || 1)
                  }
                  min={1}
                  size="xs"
                  style={{ width: 80 }}
                />
              </Table.Td>
              <Table.Td>{asset.description || "-"}</Table.Td>
              <Table.Td>
                <Tooltip label={t("users.actions.delete")}>
                  <ActionIcon
                    type="button"
                    color="red"
                    variant="subtle"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleUnmapAsset(index);
                    }}
                  >
                    <IconTrash size={16} />
                  </ActionIcon>
                </Tooltip>
              </Table.Td>
            </Table.Tr>
          ))}
          {localAssets.length === 0 && (
            <Table.Tr>
              <Table.Td colSpan={4} style={{ textAlign: "center" }}>
                No assets assigned to this room
              </Table.Td>
            </Table.Tr>
          )}
        </Table.Tbody>
      </Table>

      <Group justify="flex-end" mt="md">
        <J2NButton
          onClick={handleSaveAssets}
          loading={tabLoading}
          disabled={!hasAssetChanges()}
          j2nType={J2NButtonTypes.PRIMARY}
        >
          {t("rooms.modal.save")}
        </J2NButton>
      </Group>
    </>
  );
};

export default RoomAssetsTab;
