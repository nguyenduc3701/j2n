"use client";

import J2NHeader from "@repo/components/molecules/J2NHeader";
import { useEffect, useState } from "react";
import { userService } from "@/services/userServices";
import { IUser } from "@/types/user";
import { useTranslation } from "@repo/ui/src/providers";
import { Loader, Center } from "@mantine/core";
import J2NMotionScale from "@repo/components/atoms/J2NMotionTransition/J2NMotionScale";
import { useRouter } from "next/navigation";

export default function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [user, setUser] = useState<IUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();
  const { t } = useTranslation();

  const menuItems = [
    { name: t("Users"), href: "/users" },
    { name: t("Rooms"), href: "/rooms" },
    { name: t("Stores"), href: "/stores" },
    { name: t("Travel"), href: "/travel" },
  ];

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await userService.getMe();
        if (response && response.data) {
          setUser(response.data);
        }
      } catch (error) {
        window.location.href =
          "http://localhost:3100/login?redirectUrl=/dashboard";
      } finally {
        setIsLoading(false);
      }
    };

    fetchUser();
  }, [router]);

  if (isLoading) {
    return (
      <Center h="100vh" className="bg-j2n-sand-500">
        <J2NMotionScale initialScale={0.8} duration={0.5}>
          <Loader size="xl" color="var(--color-j2n-grape-deep-500)" />
        </J2NMotionScale>
      </Center>
    );
  }

  return (
    <>
      <J2NHeader
        title="Jadon Nguyen"
        className="bg-j2n-sand-100!"
        redirectUrl="/dashboard"
        menuItems={menuItems}
        accountProps={{
          isLogin: !!user,
          userName: user?.full_name || "User",
          roleName: user?.role_id,
          baseUrl: "http://localhost:3101",
        }}
      />
      <main className="bg-j2n-sand-100 min-h-screen">{children}</main>
    </>
  );
}
