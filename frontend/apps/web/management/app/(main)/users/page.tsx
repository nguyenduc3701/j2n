"use client";

import { userService } from "@/services/userServices";
import { IUser } from "@/types/user";
import { Box, Flex, Paper, Stack, Text } from "@mantine/core";
import { modals } from "@mantine/modals";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import TransTitle from "@repo/components/molecules/J2NTitle/TransTitle";
import J2NButton, {
  J2NButtonTypes,
} from "@repo/ui/src/components/atoms/J2NButton";
import { useTranslation } from "@repo/ui/src/providers";
import { cleanObject } from "@repo/ui/src/utils";
import { IconPlus } from "@tabler/icons-react";
import { useEffect, useState } from "react";
import UserModal from "./components/UserModal";
import UserSearchForm from "./components/UserSearchForm";
import UserTable from "./components/UserTable";
import { ModalMode } from "./components/user.types";

const UsersPage = () => {
  const { t } = useTranslation();
  const [users, setUsers] = useState<IUser[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [searchParams, setSearchParams] = useState<any>({});
  const pageSize = 10;

  // Modal states
  const [modalOpened, setModalOpened] = useState(false);
  const [modalMode, setModalMode] = useState<ModalMode>(ModalMode.VIEW);
  const [selectedUser, setSelectedUser] = useState<IUser | null>(null);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await userService.getUsers({
        page,
        size: pageSize,
        ...searchParams,
      });
      if (response && response.status === 200) {
        setUsers(response.data.users);
        setTotal(response.data.page.total);
      }
    } catch (error) {
      console.error("Failed to fetch users:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page, searchParams]);

  const handleSearch = (values: any) => {
    setSearchParams(cleanObject(values));
    setPage(1);
  };

  const handleClear = () => {
    setSearchParams({});
    setPage(1);
  };

  const handleCreate = () => {
    setSelectedUser(null);
    setModalMode(ModalMode.CREATE);
    setModalOpened(true);
  };

  const handleView = (user: IUser) => {
    setSelectedUser(user);
    setModalMode(ModalMode.VIEW);
    setModalOpened(true);
  };

  const handleEdit = (user: IUser) => {
    setSelectedUser(user);
    setModalMode(ModalMode.EDIT);
    setModalOpened(true);
  };

  const handleDelete = (user: IUser) => {
    modals.openConfirmModal({
      title: t("users.actions.delete"),
      centered: true,
      children: (
        <Text size="sm">
          {t("users.messages.confirm_delete", { name: user.full_name })}
        </Text>
      ),
      labels: { confirm: t("common.confirm"), cancel: t("common.cancel") },
      confirmProps: { color: "#75616a" },
      onConfirm: async () => {
        try {
          const response = await userService.deleteUser(user.id.toString());
          if (response && response.status === 200) {
            fetchUsers();
          }
        } catch (error) {
          console.error("Failed to delete user:", error);
        }
      },
    });
  };

  const handleModalSubmit = async (values: Partial<IUser>) => {
    try {
      let response;
      if (modalMode === ModalMode.CREATE) {
        response = await userService.createUser(values);
      } else if (modalMode === ModalMode.EDIT && selectedUser) {
        response = await userService.updateUser(
          selectedUser.id.toString(),
          values,
        );
      }

      if (response && (response.status === 200 || response.status === 201)) {
        setModalOpened(false);
        fetchUsers();
      }
    } catch (error) {
      console.error("Failed to submit user form:", error);
    }
  };

  return (
    <J2NMotionFade>
      <div className="user-management-wrapper px-25 pt-25 pb-25">
        <Flex justify="space-between" align="center" mb="xl">
          <TransTitle tKey="users.title" />
          <J2NButton
            onClick={handleCreate}
            leftSection={<IconPlus size={18} />}
            j2nType={J2NButtonTypes.PRIMARY}
            size="md"
            radius="md"
          >
            {t("users.create")}
          </J2NButton>
        </Flex>

        <Stack gap="lg">
          <Paper shadow="xs" radius="lg" withBorder p="md">
            <UserSearchForm onSearch={handleSearch} onClear={handleClear} />

            <Box
              style={{
                backgroundColor: "transparent",
              }}
            >
              <UserTable
                data={users}
                loading={loading}
                total={total}
                pageSize={pageSize}
                activePage={page}
                onPageChange={setPage}
                onView={handleView}
                onEdit={handleEdit}
                onDelete={handleDelete}
              />
            </Box>
          </Paper>
        </Stack>

        <UserModal
          opened={modalOpened}
          onClose={() => setModalOpened(false)}
          mode={modalMode}
          user={selectedUser}
          onSubmit={handleModalSubmit}
          loading={loading}
          onModeChange={setModalMode}
        />
      </div>
    </J2NMotionFade>
  );
};

export default UsersPage;
