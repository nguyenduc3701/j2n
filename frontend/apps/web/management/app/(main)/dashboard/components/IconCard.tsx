"use client";

import { Box, BoxProps } from "@mantine/core";
import React from "react";

interface IconCardProps extends BoxProps {
  icon: React.ReactNode;
}

const IconCard: React.FC<IconCardProps> = ({ icon, style, ...props }) => {
  return (
    <Box
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#CEC3C1",
        color: "#4B3D4C",
        minWidth: "35px",
        height: "35px",
        borderRadius: "4px",
        ...style,
      }}
      {...props}
    >
      {icon}
    </Box>
  );
};

export default IconCard;
