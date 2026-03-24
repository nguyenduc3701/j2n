import { J2NI18nProvider } from "@repo/ui/src/providers";
import { MantineProvider } from "@mantine/core";
import { ModalsProvider } from "@mantine/modals";
import "@mantine/core/styles.css";
import "@mantine/carousel/styles.css";
import "@mantine/charts/styles.css";
import "@repo/ui/src/styles/globals.css";
import type { Metadata } from "next";
import {
  Merienda,
  Noto_Sans_JP,
  Noto_Sans_KR,
  Saira_Condensed,
} from "next/font/google";

const saira = Saira_Condensed({
  weight: ["300", "400", "500", "600", "700"],
  subsets: ["latin", "latin-ext"],
  variable: "--font-primary",
});

const notoKR = Noto_Sans_KR({
  weight: ["300", "400", "500", "700"],
  subsets: ["latin", "latin-ext", "vietnamese", "cyrillic"],
  variable: "--font-primary-kr",
});

const notoJP = Noto_Sans_JP({
  weight: ["300", "400", "500", "700"],
  subsets: ["latin", "latin-ext", "vietnamese", "cyrillic"],
  variable: "--font-primary-jp",
});

const merienda = Merienda({
  weight: ["300", "400", "500", "600", "700"],
  subsets: ["latin", "latin-ext"],
  variable: "--font-secondary",
});

export const metadata: Metadata = {
  title: "Management Dashboard",
  description: "Management application",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${saira.variable} ${notoKR.variable} ${notoJP.variable} ${merienda.variable} antialiased`}
      >
        <MantineProvider>
          <ModalsProvider>
            <J2NI18nProvider>{children}</J2NI18nProvider>
          </ModalsProvider>
        </MantineProvider>
      </body>
    </html>
  );
}
