"use client";

import {
  IAddImagePayload,
  IAddSchedulePayload,
  productService,
} from "@/services/productServices";
import { IProductImage, IProductSchedule } from "@/types/product";
import {
  ActionIcon,
  Badge,
  Box,
  Group,
  Image,
  Modal,
  NumberInput,
  Select,
  SimpleGrid,
  Stack,
  Tabs,
  Text,
  Textarea,
  TextInput,
  Tooltip,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import {
  IconCalendar,
  IconCamera,
  IconInfoCircle,
  IconPlus,
  IconStar,
  IconStarFilled,
  IconTrash,
} from "@tabler/icons-react";
import { useEffect, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@repo/query";
import { TourModalMode, TourModalProps } from "./travel.types";

const TRAVEL_TYPE = "TOUR";

interface TourFormValues {
  title: string;
  description: string;
  price: number | "";
  thumbnail: string;
  duration: string;
  start_location: string;
  category_id: string;
}

const TourModal = ({
  opened,
  mode,
  tour,
  categories,
  loading,
  onClose,
  onSubmit,
  onModeChange,
}: TourModalProps) => {
  const { t } = useTranslation();
  const queryClient = useQueryClient();

  const isView = mode === TourModalMode.VIEW;
  const isCreate = mode === TourModalMode.CREATE;

  // New image URL input state
  const [newImageUrl, setNewImageUrl] = useState("");

  // ── React Query Fetching for Schedules and Images ─────────────────────────

  const { data: schedules = [], isLoading: scheduleLoading } = useQuery<IProductSchedule[]>({
    queryKey: ["schedules", tour?.id],
    queryFn: async () => {
      const res = await productService.getSchedulesByProductId(tour!.id);
      return (res.data as IProductSchedule[]) || [];
    },
    enabled: !!tour?.id && opened,
  });

  const { data: images = [], isLoading: imageLoading } = useQuery<IProductImage[]>({
    queryKey: ["images", tour?.id],
    queryFn: async () => {
      const res = await productService.getImagesByProductId(tour!.id);
      return (res.data as IProductImage[]) || [];
    },
    enabled: !!tour?.id && opened,
  });

  // ── Mutations for Schedules ────────────────────────────────────────────────

  const addScheduleMutation = useMutation({
    mutationFn: (payload: IAddSchedulePayload) => productService.addScheduleToProduct(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["schedules", tour?.id] });
    },
  });

  const updateScheduleMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: any }) =>
      productService.updateProductSchedule(id, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["schedules", tour?.id] });
    },
  });

  const deleteScheduleMutation = useMutation({
    mutationFn: (id: number) => productService.deleteProductSchedule(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["schedules", tour?.id] });
    },
  });

  // ── Mutations for Images ───────────────────────────────────────────────────

  const addImageMutation = useMutation({
    mutationFn: (payload: IAddImagePayload) => productService.addImageToProduct(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["images", tour?.id] });
    },
  });

  const deleteImageMutation = useMutation({
    mutationFn: (id: number) => productService.deleteProductImage(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["images", tour?.id] });
    },
  });

  const setPrimaryImageMutation = useMutation({
    mutationFn: (id: number) => productService.setPrimaryImage(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["images", tour?.id] });
    },
  });

  // ── Main form ─────────────────────────────────────────────────────────────
  const form = useForm<TourFormValues>({
    initialValues: {
      title: "",
      description: "",
      price: "",
      thumbnail: "",
      duration: "",
      start_location: "",
      category_id: "",
    },
    validate: {
      title: (v) =>
        !v ? t("travelManagement.validation.title_required") : null,
      price: (v) =>
        v === "" || v === undefined
          ? t("travelManagement.validation.price_required")
          : Number(v) <= 0
            ? t("travelManagement.validation.price_min")
            : null,
      category_id: (v) =>
        !v ? t("travelManagement.validation.category_required") : null,
    },
  });

  // ── Populate form on open ─────────────────────────────────────────────────
  useEffect(() => {
    if (opened) {
      if (tour) {
        form.setValues({
          title: tour.title ?? "",
          description: tour.description ?? "",
          price: tour.price ?? "",
          thumbnail: tour.thumbnail ?? "",
          duration: tour.duration ?? "",
          start_location: tour.start_location ?? "",
          category_id: tour.category_id
            ? String(tour.category_id)
            : tour.category?.id
              ? String(tour.category.id)
              : "",
        });
      } else {
        form.reset();
      }
    }
  }, [opened, tour]);

  // ── Form submit ───────────────────────────────────────────────────────────
  const handleSubmit = async (values: TourFormValues) => {
    await onSubmit({
      title: values.title,
      description: values.description,
      price: Number(values.price),
      thumbnail: values.thumbnail,
      duration: values.duration,
      start_location: values.start_location,
      category_id: Number(values.category_id),
      type: TRAVEL_TYPE,
    });
  };

  // ── Schedule handlers ─────────────────────────────────────────────────────
  const handleAddSchedule = async () => {
    if (!tour) return;
    const nextDay =
      schedules.length > 0
        ? Math.max(...schedules.map((s) => s.day_number)) + 1
        : 1;
    try {
      const payload: IAddSchedulePayload = {
        product_id: tour.id,
        day_number: nextDay,
        title: `Day ${nextDay}`,
      };
      await addScheduleMutation.mutateAsync(payload);
    } catch (e) {
      console.error(e);
    }
  };

  const handleDeleteSchedule = async (scheduleId: number) => {
    try {
      await deleteScheduleMutation.mutateAsync(scheduleId);
    } catch (e) {
      console.error(e);
    }
  };

  const handleUpdateScheduleField = async (
    schedule: IProductSchedule,
    field: keyof IProductSchedule,
    value: string,
  ) => {
    try {
      await updateScheduleMutation.mutateAsync({
        id: schedule.id,
        payload: {
          product_id: schedule.product_id,
          day_number: schedule.day_number,
          title: field === "title" ? value : schedule.title,
          content: field === "content" ? value : schedule.content,
          hotel: field === "hotel" ? value : schedule.hotel,
          breakfast: field === "breakfast" ? value : schedule.breakfast,
          lunch: field === "lunch" ? value : schedule.lunch,
          dinner: field === "dinner" ? value : schedule.dinner,
        },
      });
    } catch (e) {
      console.error(e);
    }
  };

  // ── Image handlers ────────────────────────────────────────────────────────
  const handleAddImage = async () => {
    if (!tour || !newImageUrl.trim()) return;
    try {
      const payload: IAddImagePayload = {
        product_id: tour.id,
        image_url: newImageUrl.trim(),
        is_primary: images.length === 0,
      };
      await addImageMutation.mutateAsync(payload);
      setNewImageUrl("");
    } catch (e) {
      console.error(e);
    }
  };

  const handleDeleteImage = async (imageId: number) => {
    try {
      await deleteImageMutation.mutateAsync(imageId);
    } catch (e) {
      console.error(e);
    }
  };

  const handleSetPrimary = async (imageId: number) => {
    if (!tour) return;
    try {
      await setPrimaryImageMutation.mutateAsync(imageId);
    } catch (e) {
      console.error(e);
    }
  };

  // ── Modal title ───────────────────────────────────────────────────────────
  const modalTitle =
    mode === TourModalMode.CREATE
      ? t("travelManagement.modal.create_title")
      : mode === TourModalMode.EDIT
        ? t("travelManagement.modal.edit_title")
        : t("travelManagement.modal.view_title");

  const categoryOptions = categories.map((cat) => ({
    value: String(cat.id),
    label: cat.name,
  }));

  const isSchedulePending =
    addScheduleMutation.isPending ||
    updateScheduleMutation.isPending ||
    deleteScheduleMutation.isPending;

  const isImagePending =
    addImageMutation.isPending ||
    deleteImageMutation.isPending ||
    setPrimaryImageMutation.isPending;

  return (
    <Modal
      opened={opened}
      onClose={onClose}
      title={<Text fw={700}>{modalTitle}</Text>}
      size="xl"
      centered
      styles={{ body: { padding: 0 } }}
    >
      <Tabs defaultValue="general" color="var(--color-j2n-grape-deep-400)">
        <Tabs.List>
          <Tabs.Tab value="general" leftSection={<IconInfoCircle size={16} />}>
            {t("travelManagement.modal.tabs.general")}
          </Tabs.Tab>
          {!isCreate && (
            <>
              <Tabs.Tab
                value="itinerary"
                leftSection={<IconCalendar size={16} />}
              >
                {t("travelManagement.modal.tabs.itinerary")}
              </Tabs.Tab>
              <Tabs.Tab value="gallery" leftSection={<IconCamera size={16} />}>
                {t("travelManagement.modal.tabs.gallery")}
              </Tabs.Tab>
            </>
          )}
        </Tabs.List>

        {/* ── General Info Tab ─────────────────────────────────────────── */}
        <Tabs.Panel value="general" p="md">
          <form onSubmit={form.onSubmit(handleSubmit)}>
            <Stack gap="md">
              <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
                <TextInput
                  label={t("travelManagement.modal.fields.title")}
                  placeholder={t("travelManagement.modal.fields.title")}
                  required={!isView}
                  readOnly={isView}
                  {...form.getInputProps("title")}
                />
                <Select
                  label={t("travelManagement.modal.fields.category")}
                  placeholder={t("travelManagement.modal.fields.category")}
                  data={categoryOptions}
                  required={!isView}
                  disabled={isView}
                  {...form.getInputProps("category_id")}
                />
              </SimpleGrid>

              <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
                <NumberInput
                  label={t("travelManagement.modal.fields.price")}
                  placeholder="0"
                  min={0}
                  required={!isView}
                  readOnly={isView}
                  suffix=" ₫"
                  thousandSeparator=","
                  {...form.getInputProps("price")}
                />
                <TextInput
                  label={t("travelManagement.modal.fields.duration")}
                  placeholder={t("travelManagement.modal.fields.duration")}
                  readOnly={isView}
                  {...form.getInputProps("duration")}
                />
              </SimpleGrid>

              <TextInput
                label={t("travelManagement.modal.fields.start_location")}
                placeholder={t("travelManagement.modal.fields.start_location")}
                readOnly={isView}
                {...form.getInputProps("start_location")}
              />

              <Textarea
                label={t("travelManagement.modal.fields.description")}
                placeholder={t("travelManagement.modal.fields.description")}
                rows={4}
                readOnly={isView}
                {...form.getInputProps("description")}
              />

              <TextInput
                label={t("travelManagement.modal.fields.thumbnail")}
                placeholder="https://..."
                readOnly={isView}
                {...form.getInputProps("thumbnail")}
              />

              {form.values.thumbnail && (
                <Image
                  src={form.values.thumbnail}
                  height={160}
                  radius="md"
                  fit="cover"
                  alt="thumbnail"
                />
              )}
            </Stack>

            {!isView && (
              <Group justify="flex-end" mt="xl" gap="sm">
                {mode === TourModalMode.EDIT && (
                  <J2NButton
                    type="button"
                    j2nType={J2NButtonTypes.SECONDARY}
                    onClick={() => onModeChange(TourModalMode.VIEW)}
                  >
                    {t("travelManagement.modal.cancel")}
                  </J2NButton>
                )}
                <J2NButton
                  type="submit"
                  j2nType={J2NButtonTypes.PRIMARY}
                  loading={loading}
                >
                  {t("travelManagement.modal.save")}
                </J2NButton>
              </Group>
            )}

            {isView && (
              <Group justify="flex-end" mt="xl">
                <J2NButton
                  type="button"
                  j2nType={J2NButtonTypes.PRIMARY}
                  onClick={() => onModeChange(TourModalMode.EDIT)}
                >
                  {t("users.actions.edit")}
                </J2NButton>
              </Group>
            )}
          </form>
        </Tabs.Panel>

        {/* ── Itinerary Tab ────────────────────────────────────────────── */}
        {!isCreate && (
          <Tabs.Panel value="itinerary" p="md">
            <Group justify="flex-end" mb="md">
              <J2NButton
                leftSection={<IconPlus size={16} />}
                j2nType={J2NButtonTypes.PRIMARY}
                onClick={handleAddSchedule}
                loading={scheduleLoading || isSchedulePending}
              >
                {t("travelManagement.modal.itinerary.add_day")}
              </J2NButton>
            </Group>

            {schedules.length === 0 ? (
              <Text c="dimmed" ta="center" py="xl">
                {t("travelManagement.modal.itinerary.no_schedules")}
              </Text>
            ) : (
              <Stack gap="lg">
                {[...schedules]
                  .sort((a, b) => a.day_number - b.day_number)
                  .map((schedule) => (
                    <Box
                      key={schedule.id}
                      p="md"
                      style={{
                        border: "1px solid #e9ecef",
                        borderRadius: 8,
                      }}
                    >
                      <Group justify="space-between" mb="sm">
                        <Badge color="grape" variant="light" size="lg">
                          {t(
                            "travelManagement.modal.itinerary.day_number",
                          ).replace("{number}", String(schedule.day_number))}
                        </Badge>
                        <Tooltip
                          label={t(
                            "travelManagement.modal.itinerary.delete_day",
                          )}
                        >
                          <ActionIcon
                            color="red"
                            variant="subtle"
                            onClick={() => handleDeleteSchedule(schedule.id)}
                            loading={isSchedulePending}
                          >
                            <IconTrash size={16} />
                          </ActionIcon>
                        </Tooltip>
                      </Group>

                      <Stack gap="sm">
                        <TextInput
                          label={t(
                            "travelManagement.modal.itinerary.day_title",
                          )}
                          value={schedule.title ?? ""}
                          onChange={(e) =>
                            handleUpdateScheduleField(
                              schedule,
                              "title",
                              e.target.value,
                            )
                          }
                        />
                        <Textarea
                          label={t(
                            "travelManagement.modal.itinerary.day_content",
                          )}
                          value={schedule.content ?? ""}
                          rows={3}
                          onChange={(e) =>
                            handleUpdateScheduleField(
                              schedule,
                              "content",
                              e.target.value,
                            )
                          }
                        />
                        <SimpleGrid cols={2} spacing="sm">
                          <TextInput
                            label={t("travelManagement.modal.itinerary.hotel")}
                            value={schedule.hotel ?? ""}
                            onChange={(e) =>
                              handleUpdateScheduleField(
                                schedule,
                                "hotel",
                                e.target.value,
                              )
                            }
                          />
                          <TextInput
                            label={t(
                              "travelManagement.modal.itinerary.breakfast",
                            )}
                            value={schedule.breakfast ?? ""}
                            onChange={(e) =>
                              handleUpdateScheduleField(
                                schedule,
                                "breakfast",
                                e.target.value,
                              )
                            }
                          />
                          <TextInput
                            label={t("travelManagement.modal.itinerary.lunch")}
                            value={schedule.lunch ?? ""}
                            onChange={(e) =>
                              handleUpdateScheduleField(
                                schedule,
                                "lunch",
                                e.target.value,
                              )
                            }
                          />
                          <TextInput
                            label={t("travelManagement.modal.itinerary.dinner")}
                            value={schedule.dinner ?? ""}
                            onChange={(e) =>
                              handleUpdateScheduleField(
                                schedule,
                                "dinner",
                                e.target.value,
                              )
                            }
                          />
                        </SimpleGrid>
                      </Stack>
                    </Box>
                  ))}
              </Stack>
            )}
          </Tabs.Panel>
        )}

        {/* ── Gallery Tab ──────────────────────────────────────────────── */}
        {!isCreate && (
          <Tabs.Panel value="gallery" p="md">
            <Group gap="sm" mb="lg">
              <TextInput
                placeholder={t("travelManagement.modal.gallery.image_url")}
                value={newImageUrl}
                onChange={(e) => setNewImageUrl(e.target.value)}
                style={{ flex: 1 }}
              />
              <J2NButton
                j2nType={J2NButtonTypes.PRIMARY}
                leftSection={<IconPlus size={16} />}
                onClick={handleAddImage}
                loading={imageLoading || isImagePending}
              >
                {t("travelManagement.modal.gallery.add_image")}
              </J2NButton>
            </Group>

            {images.length === 0 ? (
              <Text c="dimmed" ta="center" py="xl">
                {t("travelManagement.modal.gallery.no_images")}
              </Text>
            ) : (
              <SimpleGrid cols={{ base: 2, sm: 3 }} spacing="sm">
                {images.map((img) => (
                  <Box
                    key={img.id}
                    style={{
                      position: "relative",
                      borderRadius: 8,
                      overflow: "hidden",
                    }}
                  >
                    <Image
                      src={img.image_url}
                      height={140}
                      fit="cover"
                      alt="tour image"
                      radius="md"
                    />
                    {img.is_primary && (
                      <Badge
                        color="yellow"
                        variant="filled"
                        style={{
                          position: "absolute",
                          top: 6,
                          left: 6,
                          zIndex: 2,
                        }}
                        leftSection={<IconStarFilled size={10} />}
                      >
                        {t("travelManagement.modal.gallery.primary_badge")}
                      </Badge>
                    )}
                    <Group
                      gap="xs"
                      style={{
                        position: "absolute",
                        top: 6,
                        right: 6,
                        zIndex: 2,
                      }}
                    >
                      {!img.is_primary && (
                        <Tooltip
                          label={t(
                            "travelManagement.modal.gallery.set_primary",
                          )}
                        >
                          <ActionIcon
                            size="sm"
                            color="yellow"
                            variant="filled"
                            onClick={() => handleSetPrimary(img.id)}
                            loading={isImagePending}
                          >
                            <IconStar size={12} />
                          </ActionIcon>
                        </Tooltip>
                      )}
                      <Tooltip
                        label={t("travelManagement.modal.gallery.delete_image")}
                      >
                        <ActionIcon
                          size="sm"
                          color="red"
                          variant="filled"
                          onClick={() => handleDeleteImage(img.id)}
                          loading={isImagePending}
                        >
                          <IconTrash size={12} />
                        </ActionIcon>
                      </Tooltip>
                    </Group>
                  </Box>
                ))}
              </SimpleGrid>
            )}
          </Tabs.Panel>
        )}
      </Tabs>
    </Modal>
  );
};

export default TourModal;
