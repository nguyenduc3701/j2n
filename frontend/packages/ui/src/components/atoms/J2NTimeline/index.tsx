"use client";

import React from "react";
import { J2NTimelineProps, J2NTimelineItemProps } from "./J2NTimeline.type";
import {
  VerticalTimeline,
  VerticalTimelineElement,
} from "react-vertical-timeline-component";
import "react-vertical-timeline-component/style.min.css";
import "./J2NTimeline.css";

const J2NTimeline = ({
  children,
  className,
  animate = true,
  lineColor = "#75616A",
}: J2NTimelineProps) => {
  return (
    <VerticalTimeline
      className={`j2n-timeline ${className || ""}`}
      animate={animate}
      lineColor={lineColor}
    >
      {children}
    </VerticalTimeline>
  );
};

const J2NTimelineItem = ({
  children,
  className,
  date,
  dateClassName,
  icon,
  iconStyle,
  contentStyle,
  contentArrowStyle,
  position,
}: J2NTimelineItemProps) => {
  return (
    <VerticalTimelineElement
      className={className}
      date={date}
      dateClassName={dateClassName}
      icon={icon}
      iconStyle={{
        background: "#EFEAE7",
        border: "7px solid #75616A",
        boxShadow: "none",
        ...iconStyle,
      }}
      contentStyle={{
        background: "transparent",
        boxShadow: "none",
        padding: 0,
        ...contentStyle,
      }}
      contentArrowStyle={{
        display: "none",
        ...contentArrowStyle,
      }}
      position={position}
    >
      {children}
    </VerticalTimelineElement>
  );
};

J2NTimeline.Item = J2NTimelineItem;

export default J2NTimeline;
