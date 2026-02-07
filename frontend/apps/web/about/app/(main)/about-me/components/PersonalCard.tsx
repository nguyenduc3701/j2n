import React, { ReactNode } from "react";
import { Paper, Stack, Center } from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NTransText from "@repo/components/atoms/J2NTransText";

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
        shadow="xs"
        radius="lg"
        className="cursor-default bg-j2n-sand-400/70! transition-all duration-200 w-full"
      >
        <Center className="px-5 py-10 h-full">
          <Stack gap={2} align="center" justify="space-between" h="100%">
            {icon}
            <J2NTransText
              tKey={title}
              fw={500}
              className="text-j2n-plum-dark-500! text-3xl!"
            />
            <J2NTransText
              tKey={description}
              size="md"
              className="text-center text-j2n-plum-dark-500! opacity-65 cursor-default whitespace-break-spaces flex-1"
            />
          </Stack>
        </Center>
      </Paper>
    </J2NMotionFade>
  );
};

export default PersonalCard;
