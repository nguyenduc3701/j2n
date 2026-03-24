"use client";

import React, { useState } from "react";
import {
  PasswordInput,
  Select,
  SimpleGrid,
  Center,
  TextInput,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import { useTranslation } from "@repo/ui/src/providers";
import { USER_ROLES } from "@repo/ui/src/constants/common";
import { authApi } from "@/services/authServices";
import { useRouter } from "next/navigation";

const RegisterForm = () => {
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);
  const form = useForm({
    initialValues: {
      username: "",
      password: "",
      confirm_password: "",
      email: "",
      fullname: "",
      phoneNumber: "",
      address: "",
      birth: "",
      role: USER_ROLES.VISITOR,
      room: "", // only for renter
      company: "", // only for recruiter
    },

    validate: {
      username: (value) =>
        value.length < 3 ? t("validation.username_length") : null,
      password: (value) =>
        value.length < 6 ? t("validation.password_length") : null,
      confirm_password: (value, values) =>
        value !== values.password ? t("validation.password_match") : null,
      email: (value) =>
        /^\S+@\S+$/.test(value) ? null : t("validation.email_invalid"),
      role: (value: any) => (value ? null : t("validation.role_required")),
      room: (value, values) =>
        values.role === USER_ROLES.RENTER && !value
          ? t("validation.room_required")
          : null,
      company: (value, values) =>
        values.role === USER_ROLES.RECRUITER && !value
          ? t("validation.company_required")
          : null,
    },
  });

  const router = useRouter();
  const handleSubmit = async (values: typeof form.values) => {
    setLoading(true);
    try {
      // Simple mapping from name to roleId (ensure these match your DB)
      const roleIdMapping: Record<string, number> = {
        [USER_ROLES.RECRUITER]: 1,
        [USER_ROLES.RENTER]: 2,
        [USER_ROLES.VISITOR]: 3,
        [USER_ROLES.ADMIN]: 4,
      };

      const payload = {
        user_name: values.username,
        password: values.password,
        email: values.email,
        full_name: values.fullname,
        phone_number: values.phoneNumber,
        address: values.address,
        birth: values.birth,
        role_id: roleIdMapping[values.role],
        company: values.company,
        // room is currently not in the BFF RegisterRequest, but we can send it just in case
        room_id: values.room,
      };

      const response = await authApi.register(payload);

      if (response && response.status === 200) {
        console.log("Registration successful");
        router.push("/login");
      } else {
        console.error("Registration failed:", response.message);
      }
    } catch (error) {
      console.error("Registration error:", error);
    } finally {
      setLoading(false);
    }
  };

  const inputClassName = "min-w-[300px] w-full font-primary";

  return (
    <form className="mt-1" onSubmit={form.onSubmit(handleSubmit)}>
      <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="xl" verticalSpacing="md">
        <TextInput
          className={inputClassName}
          placeholder={t("username")}
          required
          disabled={loading}
          {...form.getInputProps("username")}
        />
        <TextInput
          className={inputClassName}
          placeholder={t("fullname")}
          disabled={loading}
          {...form.getInputProps("fullname")}
        />

        <PasswordInput
          className={inputClassName}
          placeholder={t("password")}
          required
          disabled={loading}
          {...form.getInputProps("password")}
        />
        <TextInput
          className={inputClassName}
          placeholder={t("email")}
          required
          disabled={loading}
          {...form.getInputProps("email")}
        />

        <PasswordInput
          className={inputClassName}
          placeholder={t("confirm_password")}
          required
          disabled={loading}
          {...form.getInputProps("confirm_password")}
        />
        <TextInput
          className={inputClassName}
          placeholder={t("phone_number")}
          disabled={loading}
          {...form.getInputProps("phoneNumber")}
        />

        <TextInput
          className={inputClassName}
          placeholder={t("address")}
          disabled={loading}
          {...form.getInputProps("address")}
        />
        <TextInput
          className={inputClassName}
          placeholder={t("dob")}
          type="date"
          disabled={loading}
          {...form.getInputProps("birth")}
        />

        <Select
          className={inputClassName}
          data={[
            { value: USER_ROLES.RECRUITER, label: t("recruiter") },
            { value: USER_ROLES.RENTER, label: t("renter") },
            { value: USER_ROLES.VISITOR, label: t("visitor") },
          ]}
          placeholder={t("role")}
          required
          disabled={loading}
          {...form.getInputProps("role")}
        />

        {form.values.role === USER_ROLES.RENTER && (
          <Select
            className={inputClassName}
            data={[
              { value: "room1", label: t("room_1") },
              { value: "room2", label: t("room_2") },
              { value: "room3", label: t("room_3") },
            ]}
            placeholder={t("select_room")}
            required
            disabled={loading}
            {...form.getInputProps("room")}
          />
        )}

        {form.values.role === USER_ROLES.RECRUITER && (
          <TextInput
            className={inputClassName}
            placeholder={t("company")}
            required
            disabled={loading}
            {...form.getInputProps("company")}
          />
        )}
      </SimpleGrid>
      <Center>
        <J2NButton
          j2nType={J2NButtonTypes.PRIMARY}
          className="max-w-[250px] max-h-9 px-15! mb-2 mx-auto mt-6 block"
          size="md"
          loading={loading}
          type="submit"
        >
          <J2NTransText span tKey="register" />
        </J2NButton>
      </Center>
    </form>
  );
};

export default RegisterForm;
