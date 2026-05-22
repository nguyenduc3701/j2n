"use client";

import { Box, Group, Paper, Tabs } from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import TransTitle from "@repo/components/molecules/J2NTitle/TransTitle";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { IconCategory, IconPlus, IconRoute } from "@tabler/icons-react";
import { openConfirmModal } from "@mantine/modals";
import { Text } from "@mantine/core";
import { useEffect, useMemo, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@repo/query";

import CategoryTab from "./components/CategoryTab";
import TourModal from "./components/TourModal";
import TourSearchForm from "./components/TourSearchForm";
import TourTable from "./components/TourTable";

import {
  productService,
  ICreateProductPayload,
} from "@/services/productServices";
import { IProduct, IProductCategory } from "@/types/product";
import { TourModalMode, TourSearchValues } from "./components/travel.types";

const TRAVEL_TYPE = "TOUR";

const TravelPage = () => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState<string | null>("tours");

  // ── Tour search values state ─────────────────────────────────────────────
  const [searchValues, setSearchValues] = useState<TourSearchValues | null>(null);

  // ── Modal state ──────────────────────────────────────────────────────────
  const [modalOpened, setModalOpened] = useState(false);
  const [modalMode, setModalMode] = useState<TourModalMode>(TourModalMode.VIEW);
  const [selectedTour, setSelectedTour] = useState<IProduct | null>(null);

  // ── React Query Fetching ──────────────────────────────────────────────────

  const { data: tours = [], isLoading: loading } = useQuery<IProduct[]>({
    queryKey: ["tours", TRAVEL_TYPE],
    queryFn: async () => {
      const res = await productService.getProductsByType(TRAVEL_TYPE);
      return (res.data as IProduct[]) || [];
    },
  });

  const { data: categories = [], isLoading: categoryLoading } = useQuery<IProductCategory[]>({
    queryKey: ["categories", TRAVEL_TYPE],
    queryFn: async () => {
      const res = await productService.getCategoriesByType(TRAVEL_TYPE);
      return (res.data as IProductCategory[]) || [];
    },
  });

  // Compute filtered tours based on query data and search values
  const filteredTours = useMemo(() => {
    let result = tours;
    if (!searchValues) return result;
    if (searchValues.title) {
      result = result.filter((t) =>
        t.title.toLowerCase().includes(searchValues.title!.toLowerCase()),
      );
    }
    if (searchValues.category_id) {
      result = result.filter(
        (t) =>
          t.category?.id === Number(searchValues.category_id) ||
          t.category_id === Number(searchValues.category_id),
      );
    }
    return result;
  }, [tours, searchValues]);

  // ── Mutations ─────────────────────────────────────────────────────────────

  const createProductMutation = useMutation({
    mutationFn: (payload: ICreateProductPayload) => productService.createProduct(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["tours", TRAVEL_TYPE] });
    },
  });

  const updateProductMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: ICreateProductPayload }) =>
      productService.updateProduct(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["tours", TRAVEL_TYPE] });
    },
  });

  const deleteProductMutation = useMutation({
    mutationFn: (id: number) => productService.deleteProduct(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["tours", TRAVEL_TYPE] });
    },
  });

  // ── Search / Filter ───────────────────────────────────────────────────────

  const handleSearch = (values: TourSearchValues) => {
    setSearchValues(values);
  };

  const handleClear = () => {
    setSearchValues(null);
  };

  // ── Modal handlers ────────────────────────────────────────────────────────

  const handleViewTour = async (tour: IProduct) => {
    setModalMode(TourModalMode.VIEW);
    setSelectedTour(tour);
    setModalOpened(true);
    try {
      const detailRes = await productService.getProductById(tour.id);
      if (detailRes.data) setSelectedTour(detailRes.data as IProduct);
    } catch (e) {
      console.error(e);
    }
  };

  const handleEditTour = (tour: IProduct) => {
    setModalMode(TourModalMode.EDIT);
    setSelectedTour(tour);
    setModalOpened(true);
  };

  const handleCreateTour = () => {
    setModalMode(TourModalMode.CREATE);
    setSelectedTour(null);
    setModalOpened(true);
  };

  const handleDeleteTour = (tour: IProduct) => {
    openConfirmModal({
      title: t("travelManagement.modal.view_title"),
      children: (
        <Text size="sm">{t("travelManagement.modal.confirm_delete")}</Text>
      ),
      labels: {
        confirm: t("common.confirm"),
        cancel: t("common.cancel"),
      },
      confirmProps: { color: "red" },
      onConfirm: async () => {
        try {
          await deleteProductMutation.mutateAsync(tour.id);
        } catch (e) {
          console.error(e);
        }
      },
    });
  };

  const handleModalSubmit = async (values: Partial<IProduct>) => {
    try {
      const payload: ICreateProductPayload = {
        category_id: values.category_id!,
        title: values.title!,
        description: values.description,
        price: values.price!,
        thumbnail: values.thumbnail,
        duration: values.duration,
        start_location: values.start_location,
        type: TRAVEL_TYPE,
      };
      if (selectedTour && modalMode === TourModalMode.EDIT) {
        await updateProductMutation.mutateAsync({ id: selectedTour.id, payload });
      } else if (modalMode === TourModalMode.CREATE) {
        await createProductMutation.mutateAsync(payload);
      }
      setModalOpened(false);
    } catch (e) {
      console.error(e);
    }
  };

  const submitLoading = createProductMutation.isPending || updateProductMutation.isPending;

  return (
    <J2NMotionFade>
      <div className="travel-wrapper px-25 pt-25">
        <Group justify="space-between" mb="lg">
          <TransTitle tKey="Travel" />
        </Group>

        <Tabs
          value={activeTab}
          onChange={setActiveTab}
          color={"var(--color-j2n-grape-deep-400)"}
        >
          <Tabs.List mb="md">
            <Tabs.Tab value="tours" leftSection={<IconRoute size={18} />}>
              {t("travelManagement.tabs.tours")}
            </Tabs.Tab>
            <Tabs.Tab
              value="categories"
              leftSection={<IconCategory size={18} />}
            >
              {t("travelManagement.tabs.categories")}
            </Tabs.Tab>
          </Tabs.List>

          {/* ── Tours Tab ───────────────────────────────────────────────── */}
          <Tabs.Panel value="tours">
            <Paper shadow="sm" radius="md" p="md" withBorder>
              <Group justify="flex-end" mb="sm">
                <J2NButton
                  leftSection={<IconPlus size={16} />}
                  j2nType={J2NButtonTypes.PRIMARY}
                  onClick={handleCreateTour}
                >
                  {t("travelManagement.create")}
                </J2NButton>
              </Group>
              <TourSearchForm
                categories={categories}
                onSearch={handleSearch}
                onClear={handleClear}
              />
              <Box mt="md">
                <TourTable
                  data={filteredTours}
                  loading={loading}
                  onView={handleViewTour}
                  onEdit={handleEditTour}
                  onDelete={handleDeleteTour}
                />
              </Box>
            </Paper>
          </Tabs.Panel>

          {/* ── Categories Tab ──────────────────────────────────────────── */}
          <Tabs.Panel value="categories">
            <Paper shadow="sm" radius="md" p="md" withBorder>
              <CategoryTab
                categories={categories}
                loading={categoryLoading}
              />
            </Paper>
          </Tabs.Panel>
        </Tabs>

        <TourModal
          opened={modalOpened}
          onClose={() => setModalOpened(false)}
          mode={modalMode}
          tour={selectedTour}
          categories={categories}
          loading={submitLoading}
          onSubmit={handleModalSubmit}
          onModeChange={setModalMode}
        />
      </div>
    </J2NMotionFade>
  );
};

export default TravelPage;
