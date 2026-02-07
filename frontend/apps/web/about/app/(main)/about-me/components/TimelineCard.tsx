"use client";

import React from "react";
import J2NTimeline from "@repo/components/atoms/J2NTimeline";
import TransTitle from "@repo/components/molecules/J2NTitle/TransTitle";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import { TileSize } from "@repo/components/molecules/J2NTitle/J2NTitle.type";
import { Text, Tooltip, Flex } from "@mantine/core";
import { IconInfoCircle } from "@tabler/icons-react";

export interface TimelineCardProps {
  index: number;
  title: string;
  description: string;
  time: string;
  tooltip?: string;
  subTitle?: string;
  icon?: React.ReactNode;
  technologies?: string;
}

const TimelineCard = ({
  index,
  title,
  subTitle,
  description,
  tooltip,
  time,
  icon,
  technologies,
}: TimelineCardProps) => {
  return (
    <J2NTimeline.Item
      key={index}
      className={index % 2 === 0 ? "text-right" : "text-left"}
      icon={icon}
    >
      <Flex
        align="center"
        justify={index % 2 === 0 ? "flex-end" : "flex-start"}
        gap="xs"
      >
        {tooltip && index % 2 !== 0 && (
          <Tooltip
            styles={{
              tooltip: {
                boxShadow: "0px 4px 20px 0px rgba(0, 0, 0, 0.10)",
                border: "1px solid #E0E0E0",
              },
            }}
            autoContrast
            multiline
            openDelay={300}
            label={<J2NTransText tKey={tooltip} />}
            color="#EFEAE7"
            position={index % 2 === 0 ? "top-end" : "top-start"}
          >
            <IconInfoCircle
              className="cursor-default opacity-80 hover:opacity-100 text-j2n-ink-500"
              size={20}
            />
          </Tooltip>
        )}
        <TransTitle
          tKey={title}
          subTitle={subTitle}
          size={TileSize.sm}
          underline={false}
          underlineStyle={{ width: "100%" }}
          reverseSubTitle={index % 2 === 0}
        />
        {tooltip && index % 2 === 0 && (
          <Tooltip
            styles={{
              tooltip: {
                boxShadow: "0px 4px 20px 0px rgba(0, 0, 0, 0.10)",
                border: "1px solid #E0E0E0",
              },
            }}
            autoContrast
            multiline
            openDelay={300}
            label={<J2NTransText tKey={tooltip} />}
            color="#EFEAE7"
            position={index % 2 === 0 ? "top-end" : "top-start"}
          >
            <IconInfoCircle
              className="cursor-default opacity-80 hover:opacity-100 text-j2n-ink-500"
              size={20}
            />
          </Tooltip>
        )}
      </Flex>
      {description && (
        <J2NTransText
          tKey={description}
          size="md"
          className="text-j2n-ink-400! mb-1! mt-0!"
        />
      )}
      {technologies && (
        <Text size="md" className="text-j2n-ink-400! m-0!">
          <b className="mr-1">
            <J2NTransText fw={700} span tKey="Technologies" />
          </b>
          <J2NTransText span tKey={technologies} />
        </Text>
      )}
      <Text size="xs" className="text-j2n-ink-500! m-0!">
        {time}
      </Text>
    </J2NTimeline.Item>
  );
};

export default TimelineCard;
