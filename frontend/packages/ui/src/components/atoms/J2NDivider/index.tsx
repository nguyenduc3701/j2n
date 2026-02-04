import { Divider } from "@mantine/core";
import { J2NDividerProps } from "./J2NDivider.type";

export default function J2NDivider({
  color = "#DAD8D8",
  ...props
}: J2NDividerProps) {
  return <Divider color={color} {...props} />;
}
