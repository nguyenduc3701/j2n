import {
  Box,
  Center,
  Flex,
  Paper,
  SimpleGrid,
  Stack,
  Text,
} from "@mantine/core";
import J2NAvatar from "@repo/components/atoms/J2NAvatar";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import J2NDivider from "@repo/components/atoms/J2NDivider";
import J2NImage from "@repo/components/atoms/J2NImage";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NTimeline from "@repo/components/atoms/J2NTimeline";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import TransTitle from "@repo/components/molecules/J2NTitle/TransTitle";
import { TileSize } from "@repo/components/molecules/J2NTitle/J2NTitle.type";
import {
  IconBrandInstagram,
  IconDownload,
  IconMailOpened,
  IconPhone,
} from "@tabler/icons-react";

import J2NFooter from "@repo/components/molecules/J2NFooter";
import AutoCarousel from "./components/AutoCarousel";
import GithubDivider from "./components/GithubDivider";
import PersonalCard from "./components/PersonalCard";
import SkillCard from "./components/SkillCard";
import TimelineCard from "./components/TimelineCard";
import {
  mainSkills,
  otherSkills,
  personalData,
  personalPhotos,
  timelineData,
} from "./components/data";

const AboutMePage = () => {
  return (
    <J2NMotionFade>
      <div className="about-me-wrapper px-62">
        <J2NSection name="short-introduction" className="py-20">
          <Center>
            <Stack align="center">
              <J2NAvatar
                size={"250px"}
                src="https://picsum.photos/id/237/200/300"
                className="m-3"
              />
              <J2NTransText
                tKey="short_intro"
                size="lg"
                className="text-j2n-ink-500 text-center opacity-65 cursor-default"
              />
            </Stack>
          </Center>
        </J2NSection>
        <J2NDivider />
        <J2NSection name="main-introduction" className="py-20">
          <Flex
            gap="xl"
            justify="space-around"
            align="flex-start"
            direction={{ base: "column", md: "row" }}
          >
            <Box className="w-full md:w-1/2">
              <J2NImage
                src="https://picsum.photos/id/237/200/300"
                alt="Avatar"
                width="100%"
                height="auto"
                classNames="aspect-[600/650] w-full"
                sizes="(max-width: 768px) 100vw, 50vw"
              />
            </Box>
            <Box className="w-full md:w-1/2">
              <Stack className="pt-15">
                <TransTitle tKey="career_title" size={TileSize.xl} />
                <J2NTransText
                  tKey="career_desc"
                  size="xl"
                  className="text-j2n-ink-500 opacity-65 cursor-default max-w-[90%]"
                />
                <J2NButton
                  j2nType={J2NButtonTypes.PRIMARY}
                  className="max-w-[180px]"
                  size="lg"
                >
                  <IconDownload size={25} />{" "}
                  <J2NTransText span tKey="download_cv" />
                </J2NButton>
              </Stack>
            </Box>
          </Flex>
        </J2NSection>
        <J2NDivider />
        <J2NSection name="skills" className="py-20">
          <J2NTransText
            tKey="skills_tech"
            size="xl"
            className="font-secondary-700 text-j2n-plum-dark-500! pb-5!"
          />
          <SimpleGrid cols={5} spacing="md" className="pb-5">
            {mainSkills.map((skill, index) => (
              <SkillCard key={index} size="lg" {...skill} />
            ))}
          </SimpleGrid>
          <J2NTransText
            tKey="other_tech"
            size="xl"
            className="font-secondary-700 text-j2n-plum-dark-500! pb-5!"
          />
          <SimpleGrid cols={6} spacing={40} verticalSpacing="xl">
            {otherSkills.map((skill, index) => (
              <SkillCard key={index} size="md" index={index} {...skill} />
            ))}
          </SimpleGrid>
        </J2NSection>
        <J2NDivider />
        <J2NSection name="career-timeline" className="py-20">
          <J2NTransText
            tKey="career_timeline"
            size="xl"
            className="font-secondary-700 text-j2n-plum-dark-500! pb-5!"
          />
          <J2NTimeline animate={true}>
            {timelineData.map((item, index) => (
              <TimelineCard key={index} index={index} {...item} />
            ))}
          </J2NTimeline>
        </J2NSection>
        <J2NDivider />
        <J2NSection name="personal-information" className="py-20">
          <J2NTransText
            tKey="personal_info"
            size="lg"
            className="font-secondary-700 text-j2n-plum-dark-500! pb-5!"
          />
          <SimpleGrid cols={3} spacing="xl" className="pb-5">
            {personalData.map((item, index) => (
              <PersonalCard key={index} {...item} />
            ))}
          </SimpleGrid>
        </J2NSection>
        <GithubDivider url="https://github.com/nguyenduc3701" />
        <J2NSection name="personal-photos" className="py-10">
          <J2NMotionFade>
            <AutoCarousel photos={personalPhotos} />
          </J2NMotionFade>
        </J2NSection>
        <J2NDivider />
        <J2NSection name="contact-me" className="py-10">
          <J2NMotionFade>
            <Paper
              radius={"lg"}
              className="w-full h-full bg-j2n-plum-dark-500! px-10 py-15 text-j2n-sand-100!"
            >
              <Stack align="center" gap={"md"}>
                <J2NTransText
                  tKey="contact_info"
                  className="text-3xl! font-secondary-700"
                />
                <J2NTransText tKey="contact_desc" className="text-2xl!" />
                <Flex gap={"lg"}>
                  <Flex gap={4} className="cursor-default">
                    <IconMailOpened size={24} />
                    <Text size="sm">nguyenduc3701@gmail.com</Text>
                  </Flex>
                  <Flex gap={4} className="cursor-default">
                    <IconPhone size={24} />
                    <Text size="sm">+84 888272332</Text>
                  </Flex>
                  <Flex gap={4} className="cursor-default">
                    <IconBrandInstagram size={24} />
                    <Text size="sm">minh.duc.3701</Text>
                  </Flex>
                </Flex>
                <J2NButton
                  j2nType={J2NButtonTypes.SECONDARY}
                  className="max-w-[200px] px-10!"
                  size="lg"
                >
                  <IconDownload size={18} />{" "}
                  <J2NTransText span tKey="download_cv" />
                </J2NButton>
              </Stack>
            </Paper>
          </J2NMotionFade>
        </J2NSection>
        <J2NFooter className="pt-2 pb-5" />
      </div>
    </J2NMotionFade>
  );
};

export default AboutMePage;
