import React from "react";
import {
  ReactDark,
  JavaScript,
  TypeScript,
  Spring,
  Nextjs,
  HTML5,
  CSS,
  TailwindCSS,
  Sass,
  AntDesign,
  Mantine,
  Redux,
  Docker,
  Kubernetes,
  MySQLDark,
  PostgreSQL,
  Nodejs,
  Redis,
  GraphQL,
  Microsoft,
  Firebase,
  Keycloak,
} from "@ridemountainpig/svgl-react";
import {
  IconDatabase,
  IconMessage,
  IconGitMerge,
  IconDeviceMobile,
  IconTrekking,
  IconBuilding,
  IconUserCheck,
} from "@tabler/icons-react";
import { SkillCardProps } from "./SkillCard";

export const mainSkills: (SkillCardProps & { icon: React.ReactNode })[] = [
  {
    name: "ReactJS",
    experience: 6,
    description: "skills.reactjs",
    size: "lg",
    icon: <ReactDark width={100} />,
  },
  {
    name: "Javascript",
    experience: 6,
    description: "skills.javascript",
    size: "lg",
    icon: <JavaScript width={100} />,
  },
  {
    name: "Typescript",
    experience: 6,
    description: "skills.typescript",
    size: "lg",
    icon: <TypeScript width={100} />,
  },
  {
    name: "Java Spring Boot",
    experience: 4,
    description: "skills.java_spring",
    size: "lg",
    icon: <Spring width={100} />,
  },
  {
    name: "NextJS",
    experience: 4,
    description: "skills.nextjs",
    size: "lg",
    icon: <Nextjs width={100} />,
  },
];

export const otherSkills: (SkillCardProps & { icon: React.ReactNode })[] = [
  {
    name: "HTML",
    experience: 6,
    description: "skills.html",
    icon: <HTML5 width={50} />,
  },
  {
    name: "CSS",
    experience: 6,
    description: "skills.css",
    icon: <CSS width={50} />,
  },
  {
    name: "Ant Design",
    experience: 6,
    description: "skills.ant_design",
    icon: <AntDesign width={50} />,
  },
  {
    name: "Redux",
    experience: 6,
    description: "skills.redux",
    icon: <Redux width={50} />,
  },
  {
    name: "Sass",
    experience: 5,
    description: "skills.sass",
    icon: <Sass width={50} />,
  },
  {
    name: "Tailwind",
    experience: 4,
    description: "skills.tailwind",
    icon: <TailwindCSS width={50} />,
  },
  {
    name: "Docker",
    experience: 4,
    description: "skills.docker",
    icon: <Docker width={50} />,
  },
  {
    name: "Kubernetes",
    experience: 4,
    description: "skills.kubernetes",
    icon: <Kubernetes width={50} />,
  },
  {
    name: "MySQL",
    experience: 4,
    description: "skills.mysql",
    icon: <MySQLDark width={50} />,
  },
  {
    name: "RabbitMQ",
    experience: 4,
    description: "skills.rabbitmq",
    icon: <IconMessage size={50} />,
  },
  {
    name: "Redis",
    experience: 4,
    description: "skills.redis",
    icon: <Redis width={50} />,
  },
  {
    name: "Fluent UI",
    experience: 2,
    description: "skills.fluent_ui",
    icon: <Microsoft width={50} />,
  },
  {
    name: "Keycloak",
    experience: 2,
    description: "skills.keycloak",
    icon: <Keycloak width={50} />,
  },
  {
    name: "Mantine UI",
    experience: 1,
    description: "skills.mantine_ui",
    icon: <Mantine width={50} />,
  },
  {
    name: "Firebase",
    experience: 1,
    description: "skills.firebase",
    icon: <Firebase width={50} />,
  },
  {
    name: "PostgreSQL",
    experience: 1,
    description: "skills.postgresql",
    icon: <PostgreSQL width={50} />,
  },
  {
    name: "NodeJS",
    experience: 1,
    description: "skills.nodejs",
    icon: <Nodejs width={50} />,
  },
  {
    name: "Flowable",
    experience: 1,
    description: "skills.flowable",
    icon: <IconGitMerge size={50} />,
  },
  {
    name: "Zustand",
    experience: "personal_projects",
    description: "skills.zustand",
    icon: <IconDatabase size={50} />,
  },
  {
    name: "React Native",
    experience: "personal_projects",
    description: "skills.react_native",
    icon: <IconDeviceMobile size={50} />,
  },
  {
    name: "GraphQL",
    experience: "personal_projects",
    description: "skills.graphql",
    icon: <GraphQL width={50} />,
  },
];

export const timelineData = [
  {
    title: "timeline.uni.title",
    time: "2014 - 2019",
    description: "timeline.uni.desc",
  },
  {
    title: "timeline.travel_connect.title",
    subTitle: "timeline.travel_connect.subtitle",
    time: "12/2019 - 05/2020",
    description: "timeline.travel_connect.desc",
    tooltip: "timeline.travel_connect.tooltip",
    technologies: "timeline.travel_connect.tech",
  },
  {
    title: "timeline.add_on_fe.title",
    subTitle: "timeline.add_on_fe.subtitle",
    time: "06/2020 - 12/2021",
    description: "timeline.add_on_fe.desc",
    tooltip: "timeline.add_on_fe.tooltip",
    technologies: "timeline.add_on_fe.tech",
  },
  {
    title: "timeline.cafe_review.title",
    subTitle: "timeline.cafe_review.subtitle",
    time: "11/2020 - 06/2021",
    description: "timeline.cafe_review.desc",
    tooltip: "timeline.cafe_review.tooltip",
    technologies: "timeline.cafe_review.tech",
  },
  {
    title: "timeline.add_on_lead.title",
    subTitle: "timeline.add_on_lead.subtitle",
    time: "01/2021 - 08/2022",
    description: "timeline.add_on_lead.desc",
    tooltip: "timeline.add_on_lead.tooltip",
    technologies: "timeline.add_on_lead.tech",
  },
  {
    title: "timeline.ascend.title",
    subTitle: "timeline.ascend.subtitle",
    time: "08/2022 - Present",
    description: "timeline.ascend.desc",
    tooltip: "timeline.ascend.tooltip",
    technologies: "timeline.ascend.tech",
  },
];

export const personalData = [
  {
    title: "personal.hobbies.title",
    description: "personal.hobbies.desc",
    icon: <IconTrekking size={60} className="text-j2n-plum-dark-500!" />,
  },
  {
    title: "personal.hometown.title",
    description: "personal.hometown.desc",
    icon: <IconBuilding size={60} className="text-j2n-plum-dark-500!" />,
  },
  {
    title: "personal.personality.title",
    description: "personal.personality.desc",
    icon: <IconUserCheck size={60} className="text-j2n-plum-dark-500!" />,
  },
];

export const personalPhotos = [
  {
    src: "https://picsum.photos/id/237/200/300",
    alt: "Avatar",
    width: 1000,
    height: 1000,
  },
  {
    src: "https://picsum.photos/id/237/200/300",
    alt: "Avatar",
    width: 1000,
    height: 1000,
  },
  {
    src: "https://picsum.photos/id/237/200/300",
    alt: "Avatar",
    width: 1000,
    height: 1000,
  },
];
