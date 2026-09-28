import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Allow Android Emulator and local network access
  allowedDevOrigins: [
    "http://10.0.2.2:3000",
    "http://10.201.36.123:3000",
    "http://localhost:3000",
    "10.0.2.2:3000",
    "10.201.36.123:3000"
  ]
};

export default nextConfig;
