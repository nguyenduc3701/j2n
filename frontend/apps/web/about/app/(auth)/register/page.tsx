"use client";

import {
  Center,
  Paper,
  PasswordInput,
  Select,
  Stack,
  TextInput,
} from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import { useForm } from "@mantine/form";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NLogo from "@repo/components/molecules/J2NHeader/J2N-logo.svg";
import { useTranslation } from "@repo/ui/src/providers";
import Image from "next/image";
import Link from "next/link";

const RegisterPage = () => {
  const { t } = useTranslation();
  const form = useForm({
    initialValues: {
      username: "",
      password: "",
      confirm_password: "",
      role: "",
    },

    validate: {
      username: (value) =>
        value.length < 3 ? "Username must include at least 3 characters" : null,
      password: (value) =>
        value.length < 6 ? "Password must include at least 6 characters" : null,
      confirm_password: (value, values) =>
        value !== values.password ? "Passwords do not match" : null,
      role: (value) => (value ? null : "Role is required"),
    },
  });

  const handleSubmit = (values: typeof form.values) => {
    console.log(values);
    // Add authentication logic here
  };

  return (
    <J2NMotionFade>
      <J2NSection
        name="register"
        className="register-page-wrapper h-screen bg-j2n-sand-100"
      >
        <Center className="h-full">
          <Paper shadow="xs" radius="lg" withBorder className="px-10">
            <Stack align="center" gap={1} className="mb-6">
              <Image
                src={J2NLogo}
                alt="J2N Logo"
                className="object-contain max-w-[200px]! max-h-[200px]! scale-[2]"
              />
              <form
                className="mt-3 mb-6"
                onSubmit={form.onSubmit(handleSubmit)}
              >
                <TextInput
                  className="min-w-[350px] font-primary"
                  placeholder={t("username")}
                  required
                  {...form.getInputProps("username")}
                />
                <PasswordInput
                  className="min-w-[350px] font-primary"
                  placeholder={t("password")}
                  required
                  mt="md"
                  {...form.getInputProps("password")}
                />
                <PasswordInput
                  className="min-w-[350px] font-primary"
                  placeholder={t("confirm_password")}
                  required
                  mt="md"
                  {...form.getInputProps("confirm_password")}
                />
                <Select
                  data={[
                    { value: "recruiter", label: t("recruiter") },
                    { value: "renter", label: t("renter") },
                    { value: "visiter", label: t("visiter") },
                  ]}
                  placeholder={t("role")}
                  required
                  mt="md"
                  {...form.getInputProps("role")}
                />
              </form>
              <J2NButton
                j2nType={J2NButtonTypes.PRIMARY}
                className="max-w-[250px] max-h-9 px-15! mb-2"
                size="md"
              >
                <J2NTransText span tKey="register" />
              </J2NButton>
              <Link href="/login">
                <J2NTransText
                  span
                  tKey="back_to_login"
                  className="text-j2n-mauve-500! cursor-pointer"
                />
              </Link>
            </Stack>
          </Paper>
        </Center>
      </J2NSection>
    </J2NMotionFade>
  );
};

export default RegisterPage;
