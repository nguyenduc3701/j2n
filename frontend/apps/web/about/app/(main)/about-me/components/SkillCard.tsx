import { Flex, Paper, Text, Tooltip } from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import React from "react";

export interface SkillCardProps {
  name: string;
  experience: string | number;
  description: string;
  icon: React.JSX.Element;
  size?: "md" | "lg";
  index?: number;
}

const SkillCard: React.FC<SkillCardProps> = ({
  name,
  experience,
  description,
  icon,
  size = "md",
  index = 0,
}) => {
  const isLarge = size === "lg";

  return (
    <J2NMotionFade delay={index * 0.2} duration={0.4}>
      <Tooltip
        label={description}
        withArrow
        multiline
        w={200}
        transitionProps={{ duration: 200 }}
      >
        <Paper
          shadow="sm"
          className={`
          cursor-default
          bg-j2n-sand-light-400!
          hover:bg-j2n-sand-light-500!
          transition-all duration-200
          ${isLarge ? "w-full aspect-square" : "w-full h-[150px]"}
        `}
        >
          <Flex
            className="w-full h-full p-3"
            justify="center"
            align="center"
            direction="column"
            gap={2}
          >
            <div className="shrink-0 pb-2">{icon}</div>
            <Text
              size={name.length > 12 && isLarge ? "sm" : isLarge ? "lg" : "md"}
              className="text-j2n-ink-300 text-center"
              lh={1.1}
            >
              {name}
            </Text>
            <Text size="xs" className="text-j2n-ink-200 opacity-60">
              {typeof experience === "number"
                ? `~ ${experience} years`
                : experience}
            </Text>
          </Flex>
        </Paper>
      </Tooltip>
    </J2NMotionFade>
  );
};

export default SkillCard;
