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
} from "@ridemountainpig/svgl-react";
import {
  IconDatabase,
  IconMessage,
  IconGitMerge,
  IconDeviceMobile,
} from "@tabler/icons-react";
import { SkillCardProps } from "./SkillCard";

export const mainSkills: (SkillCardProps & { icon: React.ReactNode })[] = [
  {
    name: "ReactJS",
    experience: 6,
    description:
      "Core expertise developed since the start of my professional career.",
    icon: <ReactDark width={80} />,
  },
  {
    name: "Javascript",
    experience: 6,
    description: "Deep foundation in modern ES6+ and asynchronous programming.",
    icon: <JavaScript width={80} />,
  },
  {
    name: "Typescript",
    experience: 6,
    description:
      "Adopted early at Add-on to build robust and type-safe applications.",
    icon: <TypeScript width={80} />,
  },
  {
    name: "Java Spring Boot",
    experience: 4,
    description:
      "Transitioned to Fullstack Developer at Ascend using Spring ecosystem.",
    icon: <Spring width={80} />,
  },
  {
    name: "NextJS",
    experience: 4,
    description:
      "Building high-performance enterprise portals and SSR applications.",
    icon: <Nextjs width={80} />,
  },
];

export const otherSkills: (SkillCardProps & { icon: React.ReactNode })[] = [
  {
    name: "HTML",
    experience: 6,
    description:
      "Built semantic & accessible foundations for diverse enterprise applications.",
    icon: <HTML5 width={40} />,
  },
  {
    name: "CSS",
    experience: 6,
    description:
      "Crafting pixel-perfect and responsive interfaces since the start of my career.",
    icon: <CSS width={40} />,
  },
  {
    name: "Ant Design",
    experience: 6,
    description:
      "Developed complex interfaces for Digital Wallet portals and Travel projects.",
    icon: <AntDesign width={40} />,
  },
  {
    name: "Redux",
    experience: 6,
    description:
      "Managed sophisticated global states for heavy-duty financial applications.",
    icon: <Redux width={40} />,
  },
  {
    name: "Sass",
    experience: 5,
    description:
      "Structured modular styles for large-scale projects at Add-on and Ascend.",
    icon: <Sass width={40} />,
  },
  {
    name: "Tailwind",
    experience: 4,
    description:
      "Main utility-first framework used to speed up UI development in modern apps.",
    icon: <TailwindCSS width={40} />,
  },
  {
    name: "Docker",
    experience: 4,
    description:
      "Standardizing development environments and microservices deployment.",
    icon: <Docker width={40} />,
  },
  {
    name: "Kubernetes",
    experience: 4,
    description:
      "Maintaining and scaling containerized services for production stability.",
    icon: <Kubernetes width={40} />,
  },
  {
    name: "MySQL",
    experience: 4,
    description:
      "Designing database schemas and writing optimized scripts for Digital Wallet services.",
    icon: <MySQLDark width={40} />,
  },
  {
    name: "RabbitMQ",
    experience: 4,
    description:
      "Decoupling microservices and managing background tasks for wallet services.",
    icon: <IconMessage size={40} />,
  },
  {
    name: "Redis",
    experience: 4,
    description:
      "Boosting system performance through efficient caching layers in production.",
    icon: <Redis width={40} />,
  },
  {
    name: "Fluent UI",
    experience: 2,
    description:
      "Customized UI libraries for Microsoft Add-on booking systems as a Team Lead.",
    icon: <Microsoft width={40} />,
  },
  {
    name: "Mantine UI",
    experience: 1,
    description:
      "Selected for modern portal developments requiring highly flexible components.",
    icon: <Mantine width={40} />,
  },
  {
    name: "Firebase",
    experience: 1,
    description:
      "Structured databases and real-time features for travel-related projects.",
    icon: <Firebase width={40} />,
  },
  {
    name: "PostgreSQL",
    experience: 1,
    description:
      "Implemented as a reliable relational database for high-performance backends.",
    icon: <PostgreSQL width={40} />,
  },
  {
    name: "NodeJS",
    experience: 1,
    description:
      "Built scalable APIs and backend utilities during my early career at Travel Connect.",
    icon: <Nodejs width={40} />,
  },
  {
    name: "Flowable",
    experience: 1,
    description:
      "Automating complex business processes for enterprise-level workflows.",
    icon: <IconGitMerge size={40} />,
  },
  {
    name: "Zustand",
    experience: "Personal Projects",
    description:
      "Optimizing state management in newer Next.js and React portals.",
    icon: <IconDatabase size={40} />,
  },
  {
    name: "React Native",
    experience: "Personal Projects",
    description:
      "Personal projects: Exploring mobile development and cross-platform UX/UI.",
    icon: <IconDeviceMobile size={40} />,
  },
  {
    name: "GraphQL",
    experience: "Personal Projects",
    description:
      "Personal projects: Investigating efficient data fetching and flexible API schemas.",
    icon: <GraphQL width={40} />,
  },
];
