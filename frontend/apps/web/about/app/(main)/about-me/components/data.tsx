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
  IconTrekking,
  IconBuilding,
  IconUserCheck,
} from "@tabler/icons-react";
import { SkillCardProps } from "./SkillCard";

export const mainSkills: (SkillCardProps & { icon: React.ReactNode })[] = [
  {
    name: "ReactJS",
    experience: 6,
    description:
      "Core expertise used to build diverse interfaces, from travel platforms to complex digital wallets.",
    size: "lg",
    icon: <ReactDark width={80} />,
  },
  {
    name: "Javascript",
    experience: 6,
    description:
      "The foundation of my career, applied in developing high-performance web portals and APIs.",
    size: "lg",
    icon: <JavaScript width={80} />,
  },
  {
    name: "Typescript",
    experience: 6,
    description:
      "Quickly mastered during my first week at Add-on to ensure type-safety for Microsoft-integrated systems.",
    size: "lg",
    icon: <TypeScript width={80} />,
  },
  {
    name: "Java Spring Boot",
    experience: 4,
    description:
      "Self-taught and officially applied at Ascend to build robust services for digital wallet systems.",
    size: "lg",
    icon: <Spring width={80} />,
  },
  {
    name: "NextJS",
    experience: 4,
    description:
      "Leveraged to build and optimize various enterprise portals, enhancing both SEO and performance.",
    size: "lg",
    icon: <Nextjs width={80} />,
  },
];

export const otherSkills: (SkillCardProps & { icon: React.ReactNode })[] = [
  {
    name: "HTML",
    experience: 6,
    description:
      "Built semantic & accessible foundations for diverse enterprise applications.",
    icon: <HTML5 width={50} />,
  },
  {
    name: "CSS",
    experience: 6,
    description:
      "Crafting pixel-perfect and responsive interfaces since the start of my career.",
    icon: <CSS width={50} />,
  },
  {
    name: "Ant Design",
    experience: 6,
    description:
      "Developed complex interfaces for Digital Wallet portals and Travel projects.",
    icon: <AntDesign width={50} />,
  },
  {
    name: "Redux",
    experience: 6,
    description:
      "Managed sophisticated global states for heavy-duty financial applications.",
    icon: <Redux width={50} />,
  },
  {
    name: "Sass",
    experience: 5,
    description:
      "Structured modular styles for large-scale projects at Add-on and Ascend.",
    icon: <Sass width={50} />,
  },
  {
    name: "Tailwind",
    experience: 4,
    description:
      "Main utility-first framework used to speed up UI development in modern apps.",
    icon: <TailwindCSS width={50} />,
  },
  {
    name: "Docker",
    experience: 4,
    description:
      "Standardizing development environments and microservices deployment.",
    icon: <Docker width={50} />,
  },
  {
    name: "Kubernetes",
    experience: 4,
    description:
      "Maintaining and scaling containerized services for production stability.",
    icon: <Kubernetes width={50} />,
  },
  {
    name: "MySQL",
    experience: 4,
    description:
      "Designing database schemas and writing optimized scripts for Digital Wallet services.",
    icon: <MySQLDark width={50} />,
  },
  {
    name: "RabbitMQ",
    experience: 4,
    description:
      "Decoupling microservices and managing background tasks for wallet services.",
    icon: <IconMessage size={50} />,
  },
  {
    name: "Redis",
    experience: 4,
    description:
      "Boosting system performance through efficient caching layers in production.",
    icon: <Redis width={50} />,
  },
  {
    name: "Fluent UI",
    experience: 2,
    description:
      "Customized UI libraries for Microsoft Add-on booking systems as a Team Lead.",
    icon: <Microsoft width={50} />,
  },
  {
    name: "Mantine UI",
    experience: 1,
    description:
      "Selected for modern portal developments requiring highly flexible components.",
    icon: <Mantine width={50} />,
  },
  {
    name: "Firebase",
    experience: 1,
    description:
      "Structured databases and real-time features for travel-related projects.",
    icon: <Firebase width={50} />,
  },
  {
    name: "PostgreSQL",
    experience: 1,
    description:
      "Implemented as a reliable relational database for high-performance backends.",
    icon: <PostgreSQL width={50} />,
  },
  {
    name: "NodeJS",
    experience: 1,
    description:
      "Built scalable APIs and backend utilities during my early career at Travel Connect.",
    icon: <Nodejs width={50} />,
  },
  {
    name: "Flowable",
    experience: 1,
    description:
      "Automating complex business processes for enterprise-level workflows.",
    icon: <IconGitMerge size={50} />,
  },
  {
    name: "Zustand",
    experience: "Personal Projects",
    description:
      "Optimizing state management in newer Next.js and React portals.",
    icon: <IconDatabase size={50} />,
  },
  {
    name: "React Native",
    experience: "Personal Projects",
    description:
      "Personal projects: Exploring mobile development and cross-platform UX/UI.",
    icon: <IconDeviceMobile size={50} />,
  },
  {
    name: "GraphQL",
    experience: "Personal Projects",
    description:
      "Personal projects: Investigating efficient data fetching and flexible API schemas.",
    icon: <GraphQL width={50} />,
  },
];

export const timelineData = [
  {
    title: "University Of Economics - Technology For Industries",
    time: "2014 - 2019",
    description: "Major in Information Technology",
  },
  {
    title: "Travel Connect",
    subTitle: "( Frontend Developer )",
    time: "12/2019 - 05/2020",
    description:
      "Designed, provided feedback on UX/UI, and structured the database. During my time working here, I participated in building APIs by NodeJS within a short period.",
    tooltip:
      "Travel Connect is a startup providing travel package solutions for both B2B and B2C clients, offering flexible, transparent, and cost-optimized tour options.",
    technologies:
      "Used JavaScript, ReactJS, Ant Design, and Firebase to build the interface for a travel project.",
  },
  {
    title: "Add-on Development",
    subTitle: "( Frontend Developer )",
    time: "06/2020 - 12/2021",
    description:
      "Worked as a frontend developer responsible for the UI of a new project. Custom a UI library based on Fluent UI to meet the required functionalities, designed UX/UI interfaces",
    tooltip:
      "A software development company specializing in Microsoft ecosystem extensions and custom enterprise booking solutions.",
    technologies:
      "Leveraged TypeScript, ReactJS, Figma, and Fluent UI to build complex booking interfaces integrated with Microsoft 365.",
  },
  {
    title: "Cafe Review",
    subTitle: "( Fullstack Developer )",
    time: "11/2020 - 06/2021",
    description:
      "Cafe Review is a short-term outsourced project that provides an online news platform capable of automatically crawling articles from selected sources while also supporting manual content creation.",
    tooltip:
      "A specialized media tool for content aggregators to automate news collection and manage editorial workflows.",
    technologies:
      "Implemented using ReactJS, NodeJS, and MySQL for the web crawling engine and backend services.",
  },
  {
    title: "Add-on Development",
    subTitle: "( Leader Frontend Team )",
    time: "01/2021 - 08/2022",
    description:
      "Worked as a developer, then became the team leader of a group of 5 fresher and junior members, designed UX/UI interfaces, reviewed code, presented, and received requirements directly from the PO.",
    tooltip:
      "A software development company specializing in Microsoft ecosystem extensions and custom enterprise booking solutions.",
    technologies:
      "Utilized Figma for UI/UX prototyping, alongside Advanced TypeScript, Git-flow, and customized enterprise UI frameworks.",
  },
  {
    title: "Ascend Technology",
    subTitle: "( Fullstack Developer )",
    time: "08/2022 - Present",
    description:
      "Maintained stability and developed new features based on requirements, enhancing the project's security and performance. Responsible for maintaining and developing 4 portals and 3 services.",
    tooltip:
      "A Fintech-focused technology firm providing secure and scalable digital payment and wallet infrastructure.",
    technologies:
      "Utilizing Java Spring Boot, MySQL for backend services, and ReactJS/NextJS with Ant Design for secure financial management portals.",
  },
];

export const personalData = [
  {
    title: "Hobbies",
    description:
      " Manchester United, Football, Games, Movies, Banh Mi, Travel,...",
    icon: <IconTrekking size={60} className="text-j2n-plum-dark-500!" />,
  },
  {
    title: "Hometown",
    description: "Ngo Cho Kham Thien, Van Mieu - Quoc Tu Giam, Hanoi, Vietnam",
    icon: <IconBuilding size={60} className="text-j2n-plum-dark-500!" />,
  },
  {
    title: "Personality",
    description:
      "Capable of working independently and collaboratively, with strong self-learning skills and an open-minded approach to new ideas.",
    icon: <IconUserCheck size={60} className="text-j2n-plum-dark-500!" />,
  },
];
