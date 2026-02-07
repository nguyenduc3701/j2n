import React from "react";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NSection from "@repo/components/atoms/J2NSection";
import TransTitle from "@repo/components/molecules/J2NTitle/TransTitle";
import { TileSize } from "@repo/components/molecules/J2NTitle/J2NTitle.type";
import { Stack, Text, List, ListItem } from "@mantine/core";
import J2NDivider from "@repo/components/atoms/J2NDivider";
import J2NTable from "@repo/components/atoms/J2NTable";
import J2NFooter from "@repo/components/molecules/J2NFooter";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import Image from "next/image";

import {
  mockSystemBackendColumns,
  mockSystemBackendData,
  mockSystemFrontendColumns,
  mockSystemFrontendData,
} from "./components/data";
import Link from "next/link";

const SystemPage = () => {
  return (
    <J2NMotionFade>
      <div className="about-me-wrapper px-62">
        <J2NSection name="overview" className="py-10">
          <Stack align="center" className="mb-10">
            <TransTitle
              className="text-center"
              tKey="system.overview.title"
              size={TileSize.xl}
              underlineStyle={{ width: "100%" }}
              underline={false}
            />
            <J2NTransText
              tKey="system.overview.desc"
              size="xl"
              className="text-j2n-ink-500! opacity-65 cursor-default w-3/4 text-center"
            />
          </Stack>
          <Image
            src="https://picsum.photos/id/237/200/300"
            alt="System Architecture"
            width={1000}
            height={1000}
            className="w-full h-auto max-h-[500px] object-cover"
          />
        </J2NSection>
        <J2NDivider />
        <J2NSection name="frontend" className="py-10">
          <TransTitle
            tKey="system.frontend.title"
            size={TileSize.xl}
            subTitle="system.frontend.subtitle"
            className="mb-2"
          />
          <J2NTransText
            tKey="system.frontend.desc"
            size="lg"
            className="text-j2n-ink-500! cursor-default mb-2"
          />
          <J2NTable
            columns={mockSystemFrontendColumns}
            data={mockSystemFrontendData}
            horizontalSpacing="xl"
            fontSize={18}
            className="mb-2"
          />
          <J2NTransText tKey="system.frontend.key_char" fw={900} size="lg" />
          <List
            className="mb-2!"
            type="unordered"
            size="lg"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.frontend.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.frontend.list.1" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.frontend.list.2" />
            </ListItem>
          </List>
          <Text size="lg" className="mb-2!">
            <J2NTransText
              tKey="system.frontend.ui_design"
              className="font-bold pr-2!"
              span
            />
            <Link
              href="https://www.figma.com/design/oVSfPR7ScJOFYduTKxa8OA/J2N-Design-UI?node-id=2111-2&p=f&t=fhxeHcrVHzC4kNzV-0"
              target="_blank"
              className="text-j2n-grape-deep-500! cursor-pointer italic font-semibold hover:opacity-60 transition-opacity duration-300"
            >
              Figma Design UI
            </Link>
          </Text>
        </J2NSection>
        <J2NDivider />
        <J2NSection name="backend" className="py-10">
          <TransTitle
            tKey="system.backend.title"
            size={TileSize.xl}
            subTitle="system.backend.subtitle"
            className="mb-2"
          />
          <J2NTransText
            tKey="system.backend.desc"
            size="lg"
            className="text-j2n-ink-500! cursor-default mb-2"
          />
          <J2NTransText
            tKey="system.backend.bff.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <List
            className="mb-2!"
            type="unordered"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.backend.bff.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.backend.bff.list.1" />
              <List type="unordered" withPadding listStyleType="disc">
                <ListItem>
                  <J2NTransText tKey="system.backend.bff.sublist.0" />
                </ListItem>
                <ListItem>
                  <J2NTransText tKey="system.backend.bff.sublist.1" />
                </ListItem>
                <ListItem>
                  <J2NTransText tKey="system.backend.bff.sublist.2" />
                </ListItem>
                <ListItem>
                  <J2NTransText tKey="system.backend.bff.sublist.3" />
                </ListItem>
              </List>
            </ListItem>
          </List>
          <J2NTransText
            tKey="system.backend.bff.desc"
            size="lg"
            className="mb-2!"
          />
          <J2NTransText
            tKey="system.backend.gateway.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <J2NTransText
            tKey="system.backend.gateway.desc"
            size="lg"
            className="mb-2!"
          />
          <J2NTransText
            tKey="system.backend.gateway.key_resp"
            fw={900}
            size="sm"
            className="font-secondary-700 text-j2n-ink-500! mb-2!"
          />
          <List
            className="mb-2!"
            type="unordered"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.backend.gateway.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.backend.gateway.list.1" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.backend.gateway.list.2" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.backend.gateway.list.3" />
            </ListItem>
          </List>
          <J2NTransText
            tKey="system.backend.microservices.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <J2NTransText
            tKey="system.backend.microservices.desc"
            size="lg"
            className="mb-2!"
          />
          <J2NTable
            columns={mockSystemBackendColumns}
            data={mockSystemBackendData}
            horizontalSpacing="xl"
            fontSize={18}
            className="mb-2"
          />
        </J2NSection>
        <J2NDivider />
        <J2NSection name="database" className="py-10">
          <TransTitle
            tKey="system.database.title"
            size={TileSize.xl}
            subTitle="system.database.subtitle"
            className="mb-2"
          />
          <J2NTransText
            tKey="system.database.desc"
            size="lg"
            className="text-j2n-ink-500! cursor-default mb-2"
          />
          <J2NTransText
            tKey="system.database.advantages"
            size="sm"
            fw={900}
            className="font-secondary-700 mb-2!"
          />
          <List
            className="mb-2!"
            type="unordered"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.database.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.database.list.1" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.database.list.2" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.database.list.3" />
            </ListItem>
          </List>
        </J2NSection>
        <J2NDivider />
        <J2NSection name="message-queue" className="py-10">
          <TransTitle
            tKey="system.message.title"
            size={TileSize.xl}
            subTitle="system.message.subtitle"
            className="mb-2"
          />
          <J2NTransText
            tKey="system.message.desc"
            size="lg"
            className="text-j2n-ink-500! cursor-default mb-2"
          />
          <J2NTransText
            tKey="system.message.use_cases"
            size="sm"
            fw={900}
            className="font-secondary-700 mb-2!"
          />
          <List
            className="mb-2!"
            type="unordered"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.message.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.message.list.1" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.message.list.2" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.message.list.3" />
            </ListItem>
          </List>
          <J2NTransText
            tKey="system.message.desc_bottom"
            size="lg"
            className="mb-2!"
          />
        </J2NSection>

        <J2NDivider />
        <J2NSection name="flow" className="py-10">
          <TransTitle
            tKey="system.flow.title"
            size={TileSize.xl}
            className="mb-2"
          />
          <J2NTransText
            tKey="system.flow.request.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <List
            className="mb-2!"
            type="ordered"
            withPadding
            listStyleType="decimal"
          >
            <ListItem>
              <J2NTransText tKey="system.flow.request.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.flow.request.list.1" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.flow.request.list.2" />
              <List type="unordered" withPadding listStyleType="disc">
                <ListItem>
                  <J2NTransText tKey="system.flow.request.list.sublist.0" />
                </ListItem>
                <ListItem>
                  <J2NTransText tKey="system.flow.request.list.sublist.1" />
                </ListItem>
              </List>
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.flow.request.list.3" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.flow.request.list.4" />
            </ListItem>
          </List>

          <J2NTransText
            tKey="system.flow.event.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <List
            className="mb-2!"
            type="ordered"
            withPadding
            listStyleType="decimal"
          >
            <ListItem>
              <J2NTransText tKey="system.flow.event.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.flow.event.list.1" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.flow.event.list.2" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.flow.event.list.3" />
            </ListItem>
          </List>
          <J2NTransText tKey="system.flow.desc" size="lg" className="mb-2!" />
        </J2NSection>

        <J2NDivider />
        <J2NSection name="security" className="py-10">
          <TransTitle
            tKey="system.security.title"
            size={TileSize.xl}
            className="mb-2"
          />
          <J2NTransText
            tKey="system.security.jwt.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <List
            className="mb-2!"
            type="unordered"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.security.jwt.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.security.jwt.list.1" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.security.jwt.list.2" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.security.jwt.list.3" />
            </ListItem>
          </List>

          <J2NTransText
            tKey="system.security.gateway.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <List
            className="mb-2!"
            type="unordered"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.security.gateway.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.security.gateway.list.1" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.security.gateway.list.2" />
            </ListItem>
          </List>

          <J2NTransText
            tKey="system.security.isolation.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <List
            className="mb-2!"
            type="unordered"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.security.isolation.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.security.isolation.list.1" />
            </ListItem>
          </List>

          <J2NTransText
            tKey="system.security.rabbitmq.title"
            fw={900}
            size="md"
            className="font-secondary-700 text-j2n-ink-500! my-2!"
          />
          <List
            className="mb-2!"
            type="unordered"
            withPadding
            listStyleType="disc"
          >
            <ListItem>
              <J2NTransText tKey="system.security.rabbitmq.list.0" />
            </ListItem>
            <ListItem>
              <J2NTransText tKey="system.security.rabbitmq.list.1" />
            </ListItem>
          </List>
        </J2NSection>

        <J2NFooter className="pt-2 pb-5" />
      </div>
    </J2NMotionFade>
  );
};

export default SystemPage;
