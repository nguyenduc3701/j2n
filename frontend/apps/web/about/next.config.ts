import type { NextConfig } from "next";

/** @type {import('next').NextConfig} */
const nextConfig: NextConfig = {
  /* config options here */
  reactCompiler: true,
  reactStrictMode: false,
  images: {
    domains: ["picsum.photos"],
  },
  transpilePackages: ["@repo/ui", "@repo/assets"],
  experimental: {
    externalDir: true,
  },
};

export default nextConfig;
