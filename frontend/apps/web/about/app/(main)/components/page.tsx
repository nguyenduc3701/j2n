"use client";

import J2NTitle from "@repo/components/molecules/J2NTitle";
import { TileSize } from "@repo/components/molecules/J2NTitle/J2NTitle.type";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NAvatar from "@repo/components/atoms/J2NAvatar";
import J2NImage from "@repo/components/atoms/J2NImage";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import J2NAccount from "@repo/components/molecules/J2NAccount";
import { IconDownload } from "@tabler/icons-react";
import J2NFooter from "@repo/components/molecules/J2NFooter";
import ProjectsTable from "./ProjectsTable";
import { Box, Flex } from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NTimeline from "@repo/components/atoms/J2NTimeline";

const mockProjects = [
  {
    id: 1,
    name: "J2N Core Architecture",
    tech: "Next.js, TurboRepo",
    year: "2024",
  },
  {
    id: 2,
    name: "Vortex Operation Portal",
    tech: "React, AntDesign",
    year: "2023",
  },
  {
    id: 3,
    name: "Mobile Banking App",
    tech: "React Native, Redux",
    year: "2022",
  },
];

const ComponentsPage = () => {
  return (
    // <J2NMotionFade>
    <J2NSection name="components" className="p-6">
      <h1>Components</h1>
      <J2NAccount isLogin userName="Jadon Nguyen" roleName="Recruiter" />
      <J2NTitle
        title="Frontend Layer"
        size={TileSize.xl}
        subTitle="(Frontend Developer)"
        divider
      />
      <Flex gap="xl" wrap="wrap" align="start" className="mb-10">
        <J2NAvatar size={"175px"} src="https://picsum.photos/id/237/200/300" />
        <J2NImage
          src="https://picsum.photos/id/237/200/300"
          alt="Avatar"
          width="250px"
          height="300px"
          classNames="m-2 p-2 shadow-lg rounded-md"
        />
      </Flex>
      <Box className="my-10">
        <J2NTitle title="Recent Projects" size={TileSize.md} className="mb-4" />
        <ProjectsTable data={mockProjects} />
      </Box>
      <Flex gap="md" className="mb-10">
        <J2NButton>Primary Action</J2NButton>
        <J2NButton j2nType={J2NButtonTypes.SECONDARY}>
          <IconDownload size={18} /> Download CV
        </J2NButton>
      </Flex>
      <J2NFooter />
      <J2NTimeline alternating bulletSize={20}>
        <J2NTimeline.Item title="2020">Started coding</J2NTimeline.Item>
        <J2NTimeline.Item title="2021">First Job</J2NTimeline.Item>
        <J2NTimeline.Item title="2023">Senior Dev</J2NTimeline.Item>
      </J2NTimeline>
    </J2NSection>

    // </J2NMotionFade>
  );
};

export default ComponentsPage;
