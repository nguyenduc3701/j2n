"use client";

import React, { useRef } from "react";
import { motion, useInView, Variants } from "framer-motion";
import { MarginType, IMotionScaleProps } from "./J2NMotionTrasition.type";

export default function J2NMotionScale({
  children,
  once = true,
  delay = 0,
  duration = 0.4,
  initialScale = 0.9,
  margin = "0px 0px -50px 0px" as MarginType,
  className,
}: IMotionScaleProps) {
  const ref = useRef<HTMLDivElement | null>(null);
  const inView = useInView(ref, { once, margin });

  const variants: Variants = {
    hidden: { opacity: 0, scale: initialScale },
    visible: {
      opacity: 1,
      scale: 1,
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
      className={`motion-scale-wrapper ${className || ""}`}
      variants={variants}
      initial="hidden"
      animate={inView ? "visible" : "hidden"}
    >
      {children}
    </motion.div>
  );
}
