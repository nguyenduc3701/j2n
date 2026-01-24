"use client";

import React, { useRef } from "react";
import { motion, useInView, Variants } from "framer-motion";

type MarginValue = `${number}${"px" | "%"}`;
export type MarginType =
  | MarginValue
  | `${MarginValue} ${MarginValue}`
  | `${MarginValue} ${MarginValue} ${MarginValue}`
  | `${MarginValue} ${MarginValue} ${MarginValue} ${MarginValue}`;

export type MotionDirection = "up" | "down" | "left" | "right" | "none";

export interface MotionTransitionProps {
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

/** Component dùng chung cho fade + slide transition */
export default function MotionTransition({
  children,
  once = true,
  direction = "none",
  delay = 0,
  duration = 0.45,
  margin = "0px 0px -100px 0px" as MarginType,
  className,
}: MotionTransitionProps) {
  const ref = useRef<HTMLDivElement | null>(null);
  const inView = useInView(ref, { once, margin });

  const getDirectionOffset = (): { x: number; y: number } => {
    switch (direction) {
      case "left":
        return { x: -20, y: 0 };
      case "right":
        return { x: 20, y: 0 };
      case "up":
        return { x: 0, y: -20 };
      case "down":
        return { x: 0, y: 20 };
      default:
        return { x: 0, y: 0 };
    }
  };

  const startOffset = getDirectionOffset();

  const variants: Variants = {
    hidden: {
      opacity: 0,
      x: startOffset.x,
      y: startOffset.y,
    },
    visible: {
      opacity: 1,
      x: 0,
      y: 0,
      transition: {
        duration,
        delay,
        ease: "easeOut",
      },
    },
  };

  return (
    <motion.div
      ref={ref}
      className={`motion-transition-wrapper ${className}`}
      variants={variants}
      initial="hidden"
      animate={inView ? "visible" : "hidden"}
    >
      {children}
    </motion.div>
  );
}
