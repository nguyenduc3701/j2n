import React, { ReactNode } from "react";
import { Paper, Stack, Text, Center } from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";

export interface IPersonalCardProps {
  title: string;
  description: string;
  icon: ReactNode;
}

const PersonalCard = ({ title, description, icon }: IPersonalCardProps) => {
  return (
    <J2NMotionFade className="h-full">
      <Paper
        h="100%"
        shadow="md"
        radius="lg"
        className="cursor-default bg-j2n-sand-500/70! transition-all duration-200 w-full"
      >
        <Center className="px-3 py-5 h-full">
          <Stack gap={1} align="center" justify="space-between" h="100%">
            {icon}
            <Text fw={500} className="text-j2n-plum-dark-500! text-3xl!">
              {title}
            </Text>
            <Text
              size="md"
              className="text-center text-j2n-plum-dark-500! opacity-65 cursor-default whitespace-break-spaces flex-1"
            >
              {description}
            </Text>
          </Stack>
        </Center>
      </Paper>
    </J2NMotionFade>
  );
};

export default PersonalCard;
