"use client";

import { Box, Flex } from "@mantine/core";
import J2NAvatar from "@repo/components/atoms/J2NAvatar";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import J2NImage from "@repo/components/atoms/J2NImage";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NTimeline from "@repo/components/atoms/J2NTimeline";
import J2NAccount from "@repo/components/molecules/J2NAccount";
import J2NFooter from "@repo/components/molecules/J2NFooter";
import J2NTitle from "@repo/components/molecules/J2NTitle";
import { TileSize } from "@repo/components/molecules/J2NTitle/J2NTitle.type";
import { IconDownload } from "@tabler/icons-react";
import ProjectsTable from "./ProjectsTable";
import CustomDivider from "../about-me/components/GithubDivider";

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
      <J2NTimeline animate={true}>
        <J2NTimeline.Item>
          <h3 className="vertical-timeline-element-title">Creative Director</h3>
          <h4 className="vertical-timeline-element-subtitle">Miami, FL</h4>
          <p>
            Creative Direction, User Experience, Visual Design, Project
            Management, Team Leading
          </p>
        </J2NTimeline.Item>
        <J2NTimeline.Item>
          <h3 className="vertical-timeline-element-title">Art Director</h3>
          <h4 className="vertical-timeline-element-subtitle">
            San Francisco, CA
          </h4>
          <p>
            Creative Direction, User Experience, Visual Design, SEO, Online
            Marketing
          </p>
        </J2NTimeline.Item>
      </J2NTimeline>
      <CustomDivider />
    </J2NSection>
  );
};

export default ComponentsPage;
