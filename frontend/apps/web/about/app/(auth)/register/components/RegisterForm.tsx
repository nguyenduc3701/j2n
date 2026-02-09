"use client";

import React from "react";
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

const RegisterForm = () => {
  const { t } = useTranslation();
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
      role: USER_ROLES.VISITER,
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
      role: (value) => (value ? null : t("validation.role_required")),
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

  const handleSubmit = (values: typeof form.values) => {
    console.log(values);
    // Add authentication logic here
  };

  const inputClassName = "min-w-[300px] w-full font-primary";

  return (
    <form className="mt-1" onSubmit={form.onSubmit(handleSubmit)}>
      <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="xl" verticalSpacing="md">
        <TextInput
          className={inputClassName}
          placeholder={t("username")}
          required
          {...form.getInputProps("username")}
        />
        <TextInput
          className={inputClassName}
          placeholder={t("fullname")}
          {...form.getInputProps("fullname")}
        />

        <PasswordInput
          className={inputClassName}
          placeholder={t("password")}
          required
          {...form.getInputProps("password")}
        />
        <TextInput
          className={inputClassName}
          placeholder={t("email")}
          required
          {...form.getInputProps("email")}
        />

        <PasswordInput
          className={inputClassName}
          placeholder={t("confirm_password")}
          required
          {...form.getInputProps("confirm_password")}
        />
        <TextInput
          className={inputClassName}
          placeholder={t("phone_number")}
          {...form.getInputProps("phoneNumber")}
        />

        <TextInput
          className={inputClassName}
          placeholder={t("address")}
          {...form.getInputProps("address")}
        />
        <TextInput
          className={inputClassName}
          placeholder={t("dob")}
          type="date"
          {...form.getInputProps("birth")}
        />

        <Select
          className={inputClassName}
          data={[
            { value: USER_ROLES.RECRUITER, label: t("recruiter") },
            { value: USER_ROLES.RENTER, label: t("renter") },
            { value: USER_ROLES.VISITER, label: t("visiter") },
          ]}
          placeholder={t("role")}
          required
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
            {...form.getInputProps("room")}
          />
        )}

        {form.values.role === USER_ROLES.RECRUITER && (
          <TextInput
            className={inputClassName}
            placeholder={t("company")}
            required
            {...form.getInputProps("company")}
          />
        )}
      </SimpleGrid>
      <Center>
        <J2NButton
          j2nType={J2NButtonTypes.PRIMARY}
          className="max-w-[250px] max-h-9 px-15! mb-2 mx-auto mt-6 block"
          size="md"
          type="submit"
        >
          <J2NTransText span tKey="register" />
        </J2NButton>
      </Center>
    </form>
  );
};

export default RegisterForm;
