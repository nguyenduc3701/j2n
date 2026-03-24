"use client";

import { Modal, ModalProps } from "@mantine/core";
import React from "react";

export interface J2NModalProps extends ModalProps {
  children?: React.ReactNode;
}

const J2NModal: React.FC<J2NModalProps> = ({
  children,
  radius = "md",
  size = "lg",
  ...props
}) => {
  return (
    <Modal
      radius={radius}
      size={size}
      {...props}
      styles={{
        body: {
          maxHeight: "calc(100vh - 250px)",
          overflowY: "auto",
          scrollbarWidth: "thin",
          "&::-webkit-scrollbar": {
            width: "6px",
          },
          "&::-webkit-scrollbar-track": {
            backgroundColor: "transparent",
          },
          "&::-webkit-scrollbar-thumb": {
            backgroundColor: "rgba(0, 0, 0, 0.1)",
            borderRadius: "10px",
          },
          "&::-webkit-scrollbar-thumb:hover": {
            backgroundColor: "rgba(0, 0, 0, 0.2)",
          },
          ...((props.styles as any)?.body || {}),
        },
        ...(props.styles as any),
      }}
    >
      {children}
    </Modal>
  );
};

export default J2NModal;
