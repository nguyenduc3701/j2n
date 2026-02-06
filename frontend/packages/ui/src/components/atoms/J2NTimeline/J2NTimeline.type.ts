import { ReactNode } from "react";

export interface J2NTimelineProps {
  children?: ReactNode;
  className?: string;
  animate?: boolean;
  lineColor?: string;
}

export interface J2NTimelineItemProps {
  children?: ReactNode;
  className?: string;
  date?: string;
  dateClassName?: string;
  icon?: ReactNode;
  iconStyle?: React.CSSProperties;
  contentStyle?: React.CSSProperties;
  contentArrowStyle?: React.CSSProperties;
  position?: "left" | "right";
}
