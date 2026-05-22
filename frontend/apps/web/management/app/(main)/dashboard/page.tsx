import React from "react";
import {
  Grid,
  GridCol,
  Stack,
  Badge,
  Text,
  Flex,
  Box,
  Avatar,
  Center,
} from "@mantine/core";
import J2NMotionFade from "@repo/components/atoms/J2NMotionTransition/J2NMotionFade";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NAlert from "@repo/components/atoms/J2NAlert";
import { DashboardCard, DashboardTitle } from "./components";
import J2NCurrency from "@repo/components/atoms/J2NCurrency";
import {
  IconUserCircle,
  IconBed,
  IconBasket,
  IconBeach,
  IconBackpack,
  IconHomeDollar,
  IconShield,
  IconZoomCode,
  IconShoppingCartCheck,
  IconPackage,
} from "@tabler/icons-react";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NFooter from "@repo/components/molecules/J2NFooter";
import { DonutChart, BarChart, PieChart } from "@mantine/charts";

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

const RenderDiscountPrograms = (discounts: any[]) => {
  if (discounts.length === 0) {
    return (
      <Center h={100}>
        <Text size="sm" c="dimmed">
          <J2NTransText tKey="no_discount_programs" />
        </Text>
      </Center>
    );
  }

  return (
    <Stack gap={0}>
      {discounts.map((discount, index) => (
        <Flex
          key={index}
          justify="space-between"
          align="center"
          py="sm"
          className={
            index < discounts.length - 1
              ? "border-b border-j2n-sand-medium-400"
              : ""
          }
        >
          <Stack gap={2}>
            <Text size="sm" fw={600} className="text-j2n-plum-dark-500">
              {discount.name}
            </Text>
            <Text size="xs" className="text-j2n-ink-300">
              {discount.startDate} - {discount.endDate}
            </Text>
          </Stack>
          <Text size="xs" fw={700} className="text-j2n-grape-deep-500">
            {discount.shortDetail}
          </Text>
        </Flex>
      ))}
    </Stack>
  );
};

const totalUsers = [
  { name: "Active", value: 252, color: "var(--color-j2n-mauve-500)" },
  { name: "Inactive", value: 168, color: "var(--color-j2n-sand-500)" },
];

const userTypeData = [
  {
    label: "admin",
    icon: <IconShield size={20} />,
    value: 10,
  },
  {
    label: "recruiter",
    icon: <IconZoomCode size={20} />,
    value: 50,
  },
  {
    label: "renter",
    icon: <IconHomeDollar size={20} />,
    value: 200,
  },
  {
    label: "visitor",
    icon: <IconBackpack size={20} />,
    value: 160,
  },
];

const recentlyRegisteredData = [
  {
    id: 0,
    name: "Luke Shaw",
    email: "lukeS@example.com",
    date: "2026-03-23 15:30",
  },
  {
    id: 1,
    name: "John Doe",
    email: "john@example.com",
    date: "2026-03-23 10:00",
  },
  {
    id: 2,
    name: "Jane Smith",
    email: "jane@example.com",
    date: "2024-03-22 15:30",
  },
  {
    id: 3,
    name: "Michael Ross",
    email: "michael@example.com",
    date: "2024-03-21 09:15",
  },
];

const mockBillTrend = [
  { month: "Jan", Bills: 1200 },
  { month: "Feb", Bills: 1900 },
  { month: "Mar", Bills: 800 },
  { month: "Apr", Bills: 1600 },
  { month: "May", Bills: 2100 },
  { month: "Jun", Bills: 1300 },
];

const mockProduts = [
  { name: "Product A", value: 400, color: "var(--color-j2n-mauve-500)" },
  { name: "Product B", value: 300, color: "var(--color-j2n-sand-500)" },
  { name: "Product C", value: 200, color: "var(--color-j2n-grape-deep-500)" },
  { name: "Product D", value: 100, color: "var(--color-j2n-ink-500)" },
];

const mockDiscounts = [
  {
    name: "Summer Sale 2024",
    startDate: "2024-06-01",
    endDate: "2024-06-30",
    shortDetail: "Buy 1 get 1 free",
  },
  {
    name: "Member Day",
    startDate: "2024-07-15",
    endDate: "2024-07-15",
    shortDetail: "-20%",
  },
];

const DashboardPage = ({ isWarning = true }: { isWarning: boolean }) => {
  return (
    <J2NMotionFade>
      <div className="dashboard-wrapper px-25 pt-25">
        {isWarning && (
          <J2NAlert title="warning" radius="md" type="error">
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
              <DashboardCard h="100%" title="user_distribution">
                <Stack align="center" gap="xs">
                  <Box pos="relative">
                    <DonutChart
                      data={totalUsers}
                      withTooltip={false}
                      size={160}
                      thickness={20}
                    />
                    <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 pointer-events-none flex flex-col items-center">
                      <Text
                        fw={700}
                        size="xl"
                        className="text-j2n-grape-deep-500"
                      >
                        420
                      </Text>
                      <J2NTransText
                        fw={500}
                        className="text-j2n-plum-dark-500! text-center"
                        tKey="total_users"
                      />
                    </div>
                  </Box>
                  <Flex justify="space-around" w="100%" mt="sm">
                    <Flex align="center" gap={6}>
                      <div className="w-3 h-3 bg-j2n-mauve-500 rounded-xs" />
                      <J2NTransText
                        fw={500}
                        className="text-j2n-plum-dark-500!"
                        tKey="active"
                      />
                    </Flex>
                    <Flex align="center" gap={6}>
                      <div className="w-3 h-3 bg-j2n-sand-500 rounded-xs" />
                      <J2NTransText
                        fw={500}
                        className="text-j2n-plum-dark-500!"
                        tKey="inactive"
                      />
                    </Flex>
                  </Flex>
                </Stack>
              </DashboardCard>
            </GridCol>
            <GridCol span={3}>
              <DashboardCard
                h="100%"
                title="account_type"
                style={{ display: "flex", flexDirection: "column" }}
              >
                <Stack gap="md" mt="sm" justify="flex-end" style={{ flex: 1 }}>
                  {userTypeData.map((item) => (
                    <Flex
                      key={item.label}
                      justify="space-between"
                      align="center"
                    >
                      <Flex align="center" gap="xs">
                        <Box className="text-j2n-grape-deep-500 bg-j2n-sand-500 p-1">
                          {item.icon}
                        </Box>
                        <J2NTransText
                          tKey={item.label}
                          className="text-j2n-plum-dark-500 opacity-80"
                        />
                      </Flex>
                      <Text className="text-j2n-grape-deep-500">
                        {item.value}
                      </Text>
                    </Flex>
                  ))}
                </Stack>
              </DashboardCard>
            </GridCol>
            <GridCol span={6}>
              <DashboardCard
                h="100%"
                title="recently_registered"
                style={{ display: "flex", flexDirection: "column" }}
              >
                <Stack gap="md" mt="sm" justify="flex-end" style={{ flex: 1 }}>
                  {recentlyRegisteredData.map((user) => (
                    <Flex key={user.id} justify="space-between" align="center">
                      <Flex align="center" gap="sm">
                        <Avatar
                          radius="xl"
                          size="md"
                          color="var(--color-j2n-grape-deep-500)"
                        >
                          {user.name.charAt(0)}
                        </Avatar>
                        <Stack gap={0}>
                          <Text
                            fw={600}
                            size="sm"
                            className="text-j2n-plum-dark-500"
                          >
                            {user.name}
                          </Text>
                          <Text size="xs" className="text-j2n-ink-300">
                            {user.email}
                          </Text>
                        </Stack>
                      </Flex>
                      <Text size="xs" className="text-j2n-ink-300">
                        {user.date.startsWith(
                          new Date().toISOString().split("T")[0],
                        ) ? (
                          <J2NTransText tKey="today" />
                        ) : (
                          user.date
                        )}
                      </Text>
                    </Flex>
                  ))}
                </Stack>
              </DashboardCard>
            </GridCol>
          </Grid>
        </J2NSection>
        <J2NSection name="rooms" className="py-5">
          <DashboardTitle icon={<IconBed size={24} />} title="Rooms" />
          <Grid mt="md">
            <GridCol span={3}>
              <DashboardCard
                h="100%"
                title="total_income"
                hideIcon
                style={{ display: "flex", flexDirection: "column" }}
              >
                <Stack
                  align="flex-end"
                  justify="flex-end"
                  style={{ flex: 1 }}
                  gap={0}
                >
                  <J2NCurrency
                    value={10420000}
                    size="xl"
                    className="text-j2n-grape-deep-500"
                  />
                  <Text size="xs" className="text-j2n-ink-300">
                    4 <J2NTransText tKey="Rooms" className="inline" />
                  </Text>
                </Stack>
              </DashboardCard>
            </GridCol>
            <GridCol span={3}>
              <DashboardCard
                h="100%"
                title="remaining"
                hideIcon
                style={{ display: "flex", flexDirection: "column" }}
              >
                <Stack
                  align="flex-end"
                  justify="flex-end"
                  style={{ flex: 1 }}
                  gap={0}
                >
                  <J2NCurrency
                    value={3420000}
                    size="xl"
                    className="text-j2n-grape-deep-500"
                  />
                  <Text size="xs" className="text-j2n-ink-300">
                    4/5 <J2NTransText tKey="rooms_paid" className="inline" />
                  </Text>
                </Stack>
              </DashboardCard>
            </GridCol>
            <GridCol span={3}>
              <DashboardCard
                h="100%"
                hideIcon
                style={{ display: "flex", flexDirection: "column" }}
              >
                <Flex w="100%" style={{ flex: 1 }}>
                  <Stack
                    flex={1}
                    justify="space-between"
                    className="border-r border-j2n-sand-medium-400 m-0"
                    pr="md"
                  >
                    <J2NTransText
                      tKey="electricity"
                      fw={600}
                      size="lg"
                      className="truncate text-j2n-plum-dark-500 opacity-70 font-secondary"
                    />
                    <J2NCurrency
                      value={1420000}
                      size="lg"
                      className="text-j2n-grape-deep-500 text-end"
                    />
                  </Stack>
                  <Stack flex={1} justify="space-between" pl="md">
                    <J2NTransText
                      tKey="water"
                      fw={600}
                      size="lg"
                      className="truncate text-j2n-plum-dark-500 opacity-70 font-secondary"
                    />
                    <J2NCurrency
                      value={650000}
                      size="lg"
                      className="text-j2n-grape-deep-500 text-end"
                    />
                  </Stack>
                </Flex>
                <Text size="xs" className="text-j2n-ink-300 m-0" ta="right">
                  01 <J2NTransText tKey="rooms_empty" className="inline" />
                </Text>
              </DashboardCard>
            </GridCol>
            <GridCol span={3}>
              <DashboardCard h="100%" title="bills_trend" hideIcon>
                <BarChart
                  h={120}
                  data={mockBillTrend}
                  dataKey="month"
                  series={[
                    { name: "Bills", color: "var(--color-j2n-mauve-500)" },
                  ]}
                  withYAxis={false}
                />
              </DashboardCard>
            </GridCol>
          </Grid>
        </J2NSection>
        <J2NSection name="store" className="py-5">
          <DashboardTitle icon={<IconBasket size={24} />} title="store" />
          <Grid mt="md">
            <GridCol className="store-left-wrapper" span={8}>
              <Stack h="100%">
                <DashboardCard title="total_income" hideIcon>
                  <J2NCurrency
                    value={50420000}
                    size="xl"
                    className="text-j2n-grape-deep-500"
                    showSymbolBox
                  />
                </DashboardCard>
                <Grid mt="md">
                  <GridCol span={6}>
                    <DashboardCard title="total_orders" hideIcon>
                      <J2NCurrency
                        value={"03"}
                        size="xl"
                        className="text-j2n-grape-deep-500"
                        showSymbolBox
                        icon={<IconShoppingCartCheck size={16} />}
                      />
                    </DashboardCard>
                  </GridCol>
                  <GridCol span={6}>
                    <DashboardCard title="total_products" hideIcon>
                      <J2NCurrency
                        value={"07"}
                        size="xl"
                        className="text-j2n-grape-deep-500"
                        showSymbolBox
                        icon={<IconPackage size={16} />}
                      />
                    </DashboardCard>
                  </GridCol>
                </Grid>
                <DashboardCard
                  h="100%"
                  title="income_analysis"
                  hideIcon
                  style={{ flex: 1, display: "flex", flexDirection: "column" }}
                >
                  <BarChart
                    h="100%"
                    data={mockBillTrend}
                    dataKey="month"
                    series={[
                      { name: "Bills", color: "var(--color-j2n-mauve-500)" },
                    ]}
                    withYAxis={false}
                  />
                </DashboardCard>
              </Stack>
            </GridCol>
            <GridCol className="store-right-wrapper" span={4}>
              <Stack h="100%">
                <DashboardCard
                  h="100%"
                  title="most_popular_product"
                  hideIcon
                  style={{ flex: 1 }}
                >
                  <Stack gap="md">
                    <Center>
                      <PieChart
                        data={mockProduts}
                        withTooltip
                        tooltipDataSource="segment"
                        size={140}
                      />
                    </Center>
                    <Stack gap="xs">
                      {mockProduts.map((item) => (
                        <Flex
                          key={item.name}
                          justify="space-between"
                          align="center"
                        >
                          <Flex align="center" gap="xs" style={{ flex: 1 }}>
                            <Box
                              w={10}
                              h={10}
                              bg={item.color}
                              className="rounded-full"
                            />
                            <Text
                              size="xs"
                              className="text-j2n-plum-dark-500 opacity-70 truncate"
                            >
                              {item.name}
                            </Text>
                          </Flex>
                          <Text size="xs" className="text-j2n-grape-deep-500">
                            {((item.value / 1000) * 100).toFixed(0)}%
                          </Text>
                        </Flex>
                      ))}
                    </Stack>
                  </Stack>
                </DashboardCard>
                <DashboardCard
                  h="100%"
                  title="discount_programs"
                  icon={RenderActiveBadge(mockDiscounts.length)}
                  style={{ flex: 1 }}
                >
                  {RenderDiscountPrograms(mockDiscounts)}
                </DashboardCard>
              </Stack>
            </GridCol>
          </Grid>
        </J2NSection>
        <J2NSection name="travel" className="py-5">
          <DashboardTitle icon={<IconBeach size={24} />} title="travel" />
          <Grid mt="md">
            <GridCol className="travel-left-wrapper" span={8}>
              <Stack h="100%">
                <DashboardCard title="total_income" hideIcon>
                  <J2NCurrency
                    value={25420000}
                    size="xl"
                    className="text-j2n-grape-deep-500"
                    showSymbolBox
                  />
                </DashboardCard>
                <Grid mt="md">
                  <GridCol span={6}>
                    <DashboardCard title="total_orders" hideIcon>
                      <J2NCurrency
                        value={"12"}
                        size="xl"
                        className="text-j2n-grape-deep-500"
                        showSymbolBox
                        icon={<IconShoppingCartCheck size={16} />}
                      />
                    </DashboardCard>
                  </GridCol>
                  <GridCol span={6}>
                    <DashboardCard title="total_products" hideIcon>
                      <J2NCurrency
                        value={"05"}
                        size="xl"
                        className="text-j2n-grape-deep-500"
                        showSymbolBox
                        icon={<IconPackage size={16} />}
                      />
                    </DashboardCard>
                  </GridCol>
                </Grid>
                <DashboardCard
                  h="100%"
                  title="income_analysis"
                  hideIcon
                  style={{ flex: 1, display: "flex", flexDirection: "column" }}
                >
                  <BarChart
                    h="100%"
                    data={mockBillTrend}
                    dataKey="month"
                    series={[
                      { name: "Bills", color: "var(--color-j2n-mauve-500)" },
                    ]}
                    withYAxis={false}
                  />
                </DashboardCard>
              </Stack>
            </GridCol>
            <GridCol className="travel-right-wrapper" span={4}>
              <Stack h="100%">
                <DashboardCard
                  h="100%"
                  title="most_popular_product"
                  hideIcon
                  style={{ flex: 1 }}
                >
                  <Stack gap="md">
                    <Center>
                      <PieChart
                        data={mockProduts}
                        withTooltip
                        tooltipDataSource="segment"
                        size={140}
                      />
                    </Center>
                    <Stack gap="xs">
                      {mockProduts.map((item) => (
                        <Flex
                          key={item.name}
                          justify="space-between"
                          align="center"
                        >
                          <Flex align="center" gap="xs" style={{ flex: 1 }}>
                            <Box
                              w={10}
                              h={10}
                              bg={item.color}
                              className="rounded-full"
                            />
                            <Text
                              size="xs"
                              className="text-j2n-plum-dark-500 opacity-70 truncate"
                            >
                              {item.name}
                            </Text>
                          </Flex>
                          <Text size="xs" className="text-j2n-grape-deep-500">
                            {((item.value / 1000) * 100).toFixed(0)}%
                          </Text>
                        </Flex>
                      ))}
                    </Stack>
                  </Stack>
                </DashboardCard>
                <DashboardCard
                  h="100%"
                  title="discount_programs"
                  icon={RenderActiveBadge(mockDiscounts.length)}
                  style={{ flex: 1 }}
                >
                  {RenderDiscountPrograms(mockDiscounts)}
                </DashboardCard>
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
