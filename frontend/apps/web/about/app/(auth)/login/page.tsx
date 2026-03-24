"use client";

import { useEffect, useState } from "react";
import { Center, PasswordInput, Stack, TextInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NLogo from "@repo/components/molecules/J2NHeader/J2N-logo.svg";
import { useTranslation } from "@repo/ui/src/providers";
import { ACCESS_TOKEN } from "@repo/ui/src/constants";
import { authApi } from "@/services/authServices";
import { useRouter } from "next/navigation";
import Image from "next/image";
import Link from "next/link";

const LoginPage = () => {
  const { t, i18n } = useTranslation();
  const [loading, setLoading] = useState(false);
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

  const router = useRouter();
  const handleSubmit = async (values: typeof form.values) => {
    setLoading(true);
    try {
      const response = await authApi.login({
        user_name: values.username,
        password: values.password,
      });

      if (response && response.status === 200) {
        const { access_token } = response.data;
        if (access_token) {
          localStorage.setItem(ACCESS_TOKEN, access_token);
          // Redirect to management app (3101) with the token and lang in the URL
          const currentLang = i18n.language || "en";
          window.location.href = `http://localhost:3101/?token=${access_token}&lang=${currentLang}`;
        }
      } else {
        console.error("Login failed:", response.message);
      }
    } catch (error) {
      console.error("Login error:", error);
    } finally {
      setLoading(false);
    }
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
                disabled={loading}
                {...form.getInputProps("username")}
              />
              <PasswordInput
                className="min-w-[350px] font-primary"
                placeholder={t("password")}
                required
                mt="md"
                disabled={loading}
                {...form.getInputProps("password")}
              />
              <Stack mt="lg" align="center">
                <J2NButton
                  type="submit"
                  j2nType={J2NButtonTypes.PRIMARY}
                  className="max-w-[250px] max-h-9 px-15!"
                  size="md"
                  loading={loading}
                >
                  <J2NTransText span tKey="login" />
                </J2NButton>
                <Link href="/register" className="-mt-2">
                  <J2NTransText
                    span
                    tKey="register"
                    className="text-j2n-mauve-500! cursor-pointer"
                  />
                </Link>
              </Stack>
            </form>
          </Stack>
        </Center>
      </J2NSection>
    </J2NMotionFade>
  );
};

export default LoginPage;
