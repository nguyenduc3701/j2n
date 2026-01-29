"use client";

import React from "react";
import Link from "next/link";
import { Menu, Flex, Text } from "@mantine/core";
import { IJ2NAccountProps, IDropdownMenuItem } from "./J2NAccount.type";
import {
  IconHelp,
  IconUser,
  IconLogout,
  IconCaretDownFilled,
} from "@tabler/icons-react";

const J2NAccount: React.FC<IJ2NAccountProps> = ({
  isLogin,
  userName,
  roleName,
  size,
  menuItems,
  logoutAction,
  helpAction,
}) => {
  const handleLogout = () => {
    if (!logoutAction) return;
    logoutAction();
  };

  const hanleHelpAction = () => {
    if (!helpAction) return;
    helpAction();
  };

  const iconSize = size || 14;

  const MOCK_MENU_ITEMS: IDropdownMenuItem[] = [
    {
      label: "Profile",
      icon: <IconUser size={iconSize} />,
      href: "/profile",
      onClick: () => {},
    },
  ];

  const Items = menuItems || MOCK_MENU_ITEMS;

  return (
    <Flex align="center" gap="xs" className="j2n-account-wrapper w-fit">
      {isLogin && (
        <Menu width="target">
          <Menu.Target>
            <Flex className="cursor-pointer" gap="xs" align="center">
              <IconCaretDownFilled size={iconSize} />
              <Flex align="end" gap={3}>
                <Text size="xl" className="text-j2n-ink-500 font-secondary-500">
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
            {Items.map((item) => {
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
      <IconHelp
        className="cursor-pointer opacity-80 hover:opacity-100 text-j2n-ink-500"
        title="Help"
        id="help-ico"
        size={iconSize}
        onClick={hanleHelpAction}
      />
    </Flex>
  );
};

export default J2NAccount;
