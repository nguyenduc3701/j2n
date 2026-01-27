"use client";

import React, { useRef } from "react";
import { motion, useInView, Variants } from "framer-motion";
import { MarginType, IMotionFadeProps } from "./J2NMotionTrasition.type";

export default function J2NMotionFade({
  children,
  once = true,
  delay = 0,
  duration = 0.5,
  margin = "0px 0px -50px 0px" as MarginType,
  className,
}: IMotionFadeProps) {
  const ref = useRef<HTMLDivElement | null>(null);
  const inView = useInView(ref, { once, margin });

  const variants: Variants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        duration,
        delay,
        ease: "easeInOut",
      },
    },
  };

  return (
    <motion.div
      ref={ref}
      className={`motion-fade-wrapper ${className || ""}`}
      variants={variants}
      initial="hidden"
      animate={inView ? "visible" : "hidden"}
    >
      {children}
    </motion.div>
  );
}
