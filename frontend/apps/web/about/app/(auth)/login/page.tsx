"use client";

import { Center, PasswordInput, Stack, TextInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NLogo from "@repo/components/molecules/J2NHeader/J2N-logo.svg";
import { useTranslation } from "@repo/ui/src/providers";
import Image from "next/image";
import Link from "next/link";

const LoginPage = () => {
  const { t } = useTranslation();
  const form = useForm({
    initialValues: {
      username: "",
      password: "",
    },

    validate: {
      username: (value) =>
        value.length < 3 ? t("validation.username_length") : null,
      password: (value) =>
        value.length < 6 ? t("validation.password_length") : null,
    },
  });

  const handleSubmit = (values: typeof form.values) => {
    console.log(values);
    // Add authentication logic here
  };

  return (
    <J2NMotionFade>
      <J2NSection
        name="login"
        className="login-page-wrapper h-screen bg-j2n-sand-100"
      >
        <Center className="h-full">
          <Stack align="center" gap={1} className="mb-6">
            <Image
              src={J2NLogo}
              alt="J2N Logo"
              className="object-contain max-w-[200px]! max-h-[200px]! scale-[2]"
            />
            <form className="mt-3 mb-6" onSubmit={form.onSubmit(handleSubmit)}>
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
            </form>
            <J2NButton
              j2nType={J2NButtonTypes.PRIMARY}
              className="max-w-[250px] max-h-9 px-15! mb-2"
              size="md"
            >
              <J2NTransText span tKey="login" />
            </J2NButton>
            <Link href="/register">
              <J2NTransText
                span
                tKey="register"
                className="text-j2n-mauve-500! cursor-pointer"
              />
            </Link>
          </Stack>
        </Center>
      </J2NSection>
    </J2NMotionFade>
  );
};

export default LoginPage;
