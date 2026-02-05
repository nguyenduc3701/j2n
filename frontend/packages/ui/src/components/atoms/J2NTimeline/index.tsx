import React from "react";
import { Timeline, rem } from "@mantine/core";
import { J2NTimelineProps } from "./J2NTimeline.type";

const J2NTimeline = ({
  children,
  alternating,
  className,
  bulletSize = 20,
  ...props
}: J2NTimelineProps & { bulletSize?: number }) => {
  const bulletOffset = rem(bulletSize / 2);

  return (
    <Timeline
      {...props}
      bulletSize={bulletSize}
      className={`j2n-timeline ${className || ""}`}
      styles={(theme) =>
        alternating
          ? {
              root: {
                display: "flex",
                flexDirection: "column",
                position: "relative",
                // Căn giữa đường line chính
                "&::before": {
                  left: "50% !important",
                  transform: "translateX(-50%)",
                },
              },
              item: {
                width: "50%",
                position: "relative",
                display: "flex",
                flexDirection: "column",
                border: 0,

                // ITEM LẺ (1, 3, 5...) -> BÊN TRÁI
                "&:nth-of-type(odd)": {
                  alignSelf: "flex-start",
                  textAlign: "right",
                  paddingRight: `calc(${theme.spacing.xl} + ${bulletOffset})`,
                  paddingLeft: 0,

                  "& .mantine-Timeline-itemBullet": {
                    right: `calc(${bulletOffset} * -1)`,
                    left: "auto",
                  },

                  "& .mantine-Timeline-itemBody": {
                    marginRight: 0,
                  },
                },

                // ITEM CHẴN (2, 4, 6...) -> BÊN PHẢI
                "&:nth-of-type(even)": {
                  alignSelf: "flex-end",
                  textAlign: "left",
                  paddingLeft: `calc(${theme.spacing.xl} + ${bulletOffset})`,
                  paddingRight: 0,

                  "& .mantine-Timeline-itemBullet": {
                    left: `calc(${bulletOffset} * -1)`,
                    right: "auto",
                  },
                },

                // Fix đường line nối giữa các item
                "&::before": {
                  display: "none", // Ẩn line con của từng item để dùng line tổng của root
                },
              },
            }
          : {}
      }
    >
      {children}
    </Timeline>
  );
};

J2NTimeline.Item = Timeline.Item;

export default J2NTimeline;
