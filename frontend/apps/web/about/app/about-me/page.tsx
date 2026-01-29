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

const AboutMePage = () => {
  return (
    <J2NSection name="about-me" className="bg-j2n-sand-500 p-6">
      <h1>about me</h1>
      <J2NAccount isLogin userName="Jadon Nguyen" roleName="Recruiter" />
      <J2NTitle
        title="Frontend Layer"
        size={TileSize.xl}
        subTitle="(Frontend Developer)"
        divider
      />

      <Flex gap="xl" wrap="wrap" align="start" className="mb-10">
        <J2NAvatar
          size={"175px"}
          src="https://scontent.fhan17-1.fna.fbcdn.net/v/t39.30808-6/597199448_122360319680004195_2578910024324556104_n.jpg?_nc_cat=102&ccb=1-7&_nc_sid=833d8c&_nc_eui2=AeEuYDeAh87at7_HxHk89rwLIyWAuvqobBEjJYC6-qhsEbSTlevtwQ6eZEsrqZSAizMqXtxEiLELE1LREdveXPN8&_nc_ohc=z2SA9lopvE0Q7kNvwEaOk6g&_nc_oc=AdkEeGRtB4p_J3SsDP7Jkd_inQt1xKEqHkFGQijqfDSANi0BsuMDD3Lw_UKPpliJt08&_nc_zt=23&_nc_ht=scontent.fhan17-1.fna&_nc_gid=L8iGssMXOixVVTN9g1w7-w&oh=00_Afkq3seLdsf8qoAZbcvNN5HCbI3_kD9wMjTCfQOzBMEYuQ&oe=6941AADC"
        />
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
    </J2NSection>
  );
};

export default AboutMePage;
