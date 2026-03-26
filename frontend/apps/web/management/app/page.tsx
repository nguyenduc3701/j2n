"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Loader, Center } from "@mantine/core";
import { ACCESS_TOKEN } from "@repo/constants";
import { useTranslation } from "@repo/ui/src/providers";
import { useAppStore } from "@repo/store";
import J2NMotionScale from "@repo/components/atoms/J2NMotionTransition/J2NMotionScale";

export default function Home() {
  const router = useRouter();
  const { i18n } = useTranslation();
  const { accessToken, setAccessToken, configurations } = useAppStore();

  const checkLocalAuth = () => {
    const localToken = localStorage.getItem(ACCESS_TOKEN);
    const currentToken = accessToken || localToken;

    if (currentToken) {
      if (!accessToken && localToken) {
        setAccessToken(localToken);
      }
      router.push("/dashboard");
    } else {
      const aboutUrl = configurations?.["about-portal.base-url"] || "http://localhost:3100";
      window.location.href = `${aboutUrl}/login?redirectUrl=/dashboard`;
    }
  };

  useEffect(() => {
    checkLocalAuth();
  }, [router, i18n, accessToken]);

  return (
    <Center h="100vh" className="bg-j2n-sand-500">
      <J2NMotionScale initialScale={0.8} duration={0.5}>
        <Loader size="xl" color="var(--color-j2n-grape-deep-500)" />
      </J2NMotionScale>
    </Center>
  );
}
