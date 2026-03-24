"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Loader, Center } from "@mantine/core";
import { ACCESS_TOKEN } from "@repo/constants";
import { useTranslation } from "@repo/ui/src/providers";
import J2NMotionScale from "@repo/components/atoms/J2NMotionTransition/J2NMotionScale";

export default function Home() {
  const router = useRouter();
  const { i18n } = useTranslation();
  const handleUrlAuth = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const urlToken = urlParams.get("token");
    const urlLang = urlParams.get("lang");

    if (urlToken || urlLang) {
      if (urlToken) {
        localStorage.setItem(ACCESS_TOKEN, urlToken);
      }
      if (urlLang) {
        i18n.changeLanguage(urlLang);
        localStorage.setItem("language", urlLang);
      }
      // Clean the URL (remove token and lang from address bar)
      const newUrl = window.location.pathname;
      window.history.replaceState({}, "", newUrl);
      router.push("/dashboard");
      return true;
    }
    return false;
  };

  const checkLocalAuth = () => {
    const token = localStorage.getItem(ACCESS_TOKEN);
    if (token) {
      router.push("/dashboard");
    } else {
      // No token found, redirect to login page
      window.location.href =
        "http://localhost:3100/login?redirectUrl=/dashboard";
    }
  };

  useEffect(() => {
    const handled = handleUrlAuth();
    if (!handled) {
      checkLocalAuth();
    }
  }, [router, i18n]);

  return (
    <Center h="100vh" className="bg-j2n-sand-500">
      <J2NMotionScale initialScale={0.8} duration={0.5}>
        <Loader size="xl" color="var(--color-j2n-grape-deep-500)" />
      </J2NMotionScale>
    </Center>
  );
}
