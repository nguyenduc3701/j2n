import J2NSection from "@repo/components/atoms/J2NSection";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NAvatar from "@repo/components/atoms/J2NAvatar";
import J2NDivider from "@repo/components/atoms/J2NDivider";
import { Center, Text, Flex, Box, Stack } from "@mantine/core";
import { TileSize } from "@repo/components/molecules/J2NTitle/J2NTitle.type";
import J2NTitle from "@repo/components/molecules/J2NTitle";
import J2NButton, { J2NButtonTypes } from "@repo/components/atoms/J2NButton";
import J2NImage from "@repo/components/atoms/J2NImage";
import { IconDownload } from "@tabler/icons-react";

const AboutMePage = () => {
  return (
    <J2NMotionFade>
      <div className="about-me-wrapper px-36">
        <J2NSection name="short-introduction" className="py-20">
          <Center>
            <Flex
              direction={{ base: "column" }}
              justify="center"
              align="center"
            >
              <J2NAvatar
                size={"200px"}
                src="https://picsum.photos/id/237/200/300"
                className="m-3"
              />
              <Text
                size="lg"
                className="text-j2n-ink-500 text-center opacity-65 cursor-default"
              >
                “I'm a Fullstack developer with about 6 years of experience,
                specializing in JavaScript, TypeScript, ReactJS and Java... “
              </Text>
            </Flex>
          </Center>
        </J2NSection>
        <J2NDivider />
        <J2NSection name="main-introduction" className="py-20">
          <Flex gap="md" justify="space-around" align="flex-start">
            <Box className="max-w-[50%]">
              <J2NImage
                src="https://picsum.photos/id/237/200/300"
                alt="Avatar"
                width="450px"
                height="550px"
                classNames="m-2 p-2 shadow-lg rounded-md"
              />
            </Box>
            <Box className="max-w-[50%]">
              <Stack className="pt-15">
                <J2NTitle title="About my career journey" size={TileSize.xl} />
                <Text
                  size="lg"
                  className="text-j2n-ink-500 opacity-65 cursor-default max-w-[80%]"
                >
                  Hi, I'm a front-end developer with about 6 years of
                  experience, specializing in JavaScript, TypeScript, and
                  ReactJS. My journey in the tech industry has allowed me to
                  work on several impactful company projects, where I've not
                  only honed my front-end skills but also gained valuable
                  insights into backend development with Java. While front-end
                  development is my primary focus, I bring a well-rounded
                  approach to my work, ensuring that my projects are robust and
                  integrated seamlessly with backend systems.
                </Text>
                <J2NButton
                  j2nType={J2NButtonTypes.PRIMARY}
                  className="max-w-[150px]"
                >
                  <IconDownload size={18} /> Download CV
                </J2NButton>
              </Stack>
            </Box>
          </Flex>
        </J2NSection>
      </div>
    </J2NMotionFade>
  );
};

export default AboutMePage;
