export type MarginValue = `${number}${"px" | "%"}`;
export type MarginType =
  | MarginValue
  | `${MarginValue} ${MarginValue}`
  | `${MarginValue} ${MarginValue} ${MarginValue}`
  | `${MarginValue} ${MarginValue} ${MarginValue} ${MarginValue}`;

export type MotionDirection = "up" | "down" | "left" | "right" | "none";

export interface IMotionScrollProps {
  children: React.ReactNode;
  /** animation xuất hiện một lần khi scroll */
  once?: boolean;
  /** hướng chuyển động */
  direction?: MotionDirection;
  /** độ trễ */
  delay?: number;
  /** thời gian animation */
  duration?: number;
  /** khoảng offset để kích hoạt in view */
  margin?: MarginType | undefined;
  /** className để truyền vào wrapper */
  className?: string;
}

export interface IMotionFadeProps {
  children: React.ReactNode;
  once?: boolean;
  delay?: number;
  duration?: number;
  margin?: MarginType | undefined;
  className?: string;
}

export interface IMotionScaleProps {
  children: React.ReactNode;
  once?: boolean;
  delay?: number;
  duration?: number;
  initialScale?: number;
  margin?: MarginType | undefined;
  className?: string;
}
