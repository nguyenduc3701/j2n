"use client";

import { motion, useInView, Variants } from "framer-motion";
import { useRef } from "react";
import { MarginType, IMotionScrollProps } from "./J2NMotionTrasition.type";

/** Component dùng chung cho slide transition khi scroll */
export default function J2NMotionScroll({
  children,
  once = true,
  direction = "up",
  delay = 0,
  duration = 0.45,
  margin = "0px 0px -50px 0px" as MarginType,
  className,
}: IMotionScrollProps) {
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
      className={`motion-scroll-wrapper ${className}`}
      variants={variants}
      initial="hidden"
      animate={inView ? "visible" : "hidden"}
    >
      {children}
    </motion.div>
  );
}
