"use client";

import { Center, Paper, Stack } from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NLogo from "@repo/components/molecules/J2NHeader/J2N-logo.svg";
import Image from "next/image";
import Link from "next/link";
import RegisterForm from "./components/RegisterForm";

const RegisterPage = () => {
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
                className="object-contain max-w-[180px]! max-h-[180px]! scale-[2]"
              />
              <RegisterForm />
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
