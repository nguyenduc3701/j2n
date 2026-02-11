import React from "react";
import { Grid, GridCol, Stack, Badge, Text, Flex } from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NAlert from "@repo/components/atoms/J2NAlert";
import { DashboardCard, DashboardTitle } from "./components";
import {
  IconUserCircle,
  IconBed,
  IconBasket,
  IconBeach,
} from "@tabler/icons-react";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NFooter from "@repo/components/molecules/J2NFooter";

const RenderActiveBadge = (activeNum: number) => {
  return (
    <Badge className="bg-j2n-sand-500!">
      <Flex className="p-1" align="center" gap={4}>
        <Text fw={500} className="text-j2n-plum-dark-500!">
          {activeNum}
        </Text>
        <J2NTransText
          fw={500}
          className="text-j2n-plum-dark-500!"
          tKey="active"
        />
      </Flex>
    </Badge>
  );
};

const DashboardPage = ({ isWarning = true }: { isWarning: boolean }) => {
  return (
    <J2NMotionFade>
      <div className="dashboard-wrapper px-25 pt-25">
        {isWarning && (
          <J2NAlert title="Warning" radius="md" type="error">
            <J2NTransText tKey="warning-content" />
          </J2NAlert>
        )}
        <J2NSection name="account-summary" className="py-5">
          <DashboardTitle
            icon={<IconUserCircle size={24} />}
            title="account_and_users"
          />
          <Grid mt="md">
            <GridCol span={3}>
              <DashboardCard title="user_distribution" />
            </GridCol>
            <GridCol span={3}>
              <DashboardCard title="account_type" />
            </GridCol>
            <GridCol span={6}>
              <DashboardCard title="recently_registered" />
            </GridCol>
          </Grid>
        </J2NSection>
        <J2NSection name="rooms" className="py-5">
          <DashboardTitle icon={<IconBed size={24} />} title="rooms" />
          <Grid mt="md">
            <GridCol span={3}>
              <DashboardCard title="total_income" hideIcon />
            </GridCol>
            <GridCol span={3}>
              <DashboardCard title="remaining" hideIcon />
            </GridCol>
            <GridCol span={3}>
              <DashboardCard hideIcon />
            </GridCol>
            <GridCol span={3}>
              <DashboardCard title="bills_trend" hideIcon />
            </GridCol>
          </Grid>
        </J2NSection>
        <J2NSection name="store" className="py-5">
          <DashboardTitle icon={<IconBasket size={24} />} title="store" />
          <Grid mt="md">
            <GridCol className="store-left-wrapper" span={6}>
              <Stack>
                <DashboardCard title="total_income" hideIcon />
                <Grid mt="md">
                  <GridCol span={6}>
                    <DashboardCard title="total_orders" hideIcon />
                  </GridCol>
                  <GridCol span={6}>
                    <DashboardCard title="total_products" hideIcon />
                  </GridCol>
                </Grid>
                <DashboardCard title="income_analysis" hideIcon />
              </Stack>
            </GridCol>
            <GridCol className="store-right-wrapper" span={6}>
              <Stack h="100%">
                <DashboardCard
                  title="most_popular_product"
                  hideIcon
                  style={{ flex: 1 }}
                />
                <DashboardCard
                  title="discount_programs"
                  icon={RenderActiveBadge(2)}
                  style={{ flex: 1 }}
                />
              </Stack>
            </GridCol>
          </Grid>
        </J2NSection>
        <J2NSection name="travel" className="py-5">
          <DashboardTitle icon={<IconBeach size={24} />} title="travel" />
          <Grid mt="md">
            <GridCol className="travel-left-wrapper" span={6}>
              <Stack>
                <DashboardCard title="total_income" hideIcon />
                <Grid mt="md">
                  <GridCol span={6}>
                    <DashboardCard title="total_orders" hideIcon />
                  </GridCol>
                  <GridCol span={6}>
                    <DashboardCard title="total_products" hideIcon />
                  </GridCol>
                </Grid>
                <DashboardCard title="income_analysis" hideIcon />
              </Stack>
            </GridCol>
            <GridCol className="travel-right-wrapper" span={6}>
              <Stack h="100%">
                <DashboardCard
                  title="most_popular_product"
                  hideIcon
                  style={{ flex: 1 }}
                />
                <DashboardCard
                  title="discount_programs"
                  icon={RenderActiveBadge(2)}
                  style={{ flex: 1 }}
                />
              </Stack>
            </GridCol>
          </Grid>
        </J2NSection>
        <J2NFooter className="pt-2 pb-5" />
      </div>
    </J2NMotionFade>
  );
};

export default DashboardPage;
