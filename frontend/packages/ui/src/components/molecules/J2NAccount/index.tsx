"use client";

import React from "react";
import Link from "next/link";
import { Menu, Flex, Text } from "@mantine/core";
import { IJ2NAccountProps, IDropdownMenuItem } from "./J2NAccount.type";
import {
  IconUser,
  IconLogout,
  IconCaretDownFilled,
  IconSettings,
} from "@tabler/icons-react";

const J2NAccount: React.FC<IJ2NAccountProps> = ({
  isLogin,
  userName,
  roleName,
  size,
  logoutAction,
}) => {
  const handleLogout = () => {
    if (!logoutAction) return;
    logoutAction();
  };

  const iconSize = size || 14;

  const MENU_ITEMS: IDropdownMenuItem[] = [
    {
      label: "Profile",
      icon: <IconUser size={16} />,
      href: "/profile",
    },
    {
      label: "Settings",
      icon: <IconSettings size={16} />,
      href: "/settings",
    },
  ];

  return (
    <Flex align="center" gap="xs" className="j2n-account-wrapper w-fit">
      {isLogin && (
        <Menu width="target">
          <Menu.Target>
            <Flex className="cursor-pointer" gap="xs" align="center">
              <IconCaretDownFilled size={iconSize} />
              <Flex align="end" gap={4}>
                <Text size="md" className="text-j2n-ink-500 font-secondary-500">
                  {userName}
                </Text>
                {roleName ? (
                  <Text size="xs" className="text-j2n-plum-dark-500">
                    {`(${roleName.toLocaleUpperCase()})`}
                  </Text>
                ) : null}
              </Flex>
            </Flex>
          </Menu.Target>
          <Menu.Dropdown className="bg-j2n-sand-light-300!">
            {MENU_ITEMS.map((item) => {
              if (item.href) {
                return (
                  <Link href={item.href} key={item.label}>
                    <Menu.Item leftSection={item.icon}>{item.label}</Menu.Item>
                  </Link>
                );
              }
              return (
                <Menu.Item
                  leftSection={item.icon}
                  onClick={item.onClick}
                  key={item.label}
                >
                  {item.label}
                </Menu.Item>
              );
            })}
            <Menu.Item
              onClick={handleLogout}
              leftSection={<IconLogout size={iconSize} />}
            >
              Logout
            </Menu.Item>
          </Menu.Dropdown>
        </Menu>
      )}
    </Flex>
  );
};

export default J2NAccount;
