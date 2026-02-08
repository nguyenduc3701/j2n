"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Loader, Center } from "@mantine/core";
import { ACCESS_TOKEN } from "@repo/constants";
import J2NMotionScale from "@repo/components/atoms/J2NMotionTransition/J2NMotionScale";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem(ACCESS_TOKEN);
    if (token) {
      router.push("/dashboard");
    } else {
      router.push("http://localhost:3100/login?redirectUrl=/dashboard");
    }
  }, [router]);

  return (
    <Center h="100vh" className="bg-j2n-sand-500">
      <J2NMotionScale initialScale={0.8} duration={0.5}>
        <Loader size="xl" color="var(--color-j2n-grape-deep-500)" />
      </J2NMotionScale>
    </Center>
  );
}
