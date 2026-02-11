import { AlertProps } from "@mantine/core";
export type J2NAlertType = "warn" | "error" | "success" | "info";

export interface J2NAlertProps extends Omit<AlertProps, "title" | "color"> {
  type?: J2NAlertType;
  title?: React.ReactNode;
}
