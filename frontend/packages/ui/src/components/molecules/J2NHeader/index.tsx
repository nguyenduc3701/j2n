"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useTranslation } from "react-i18next";
import { Flex } from "@mantine/core";
import { motion, AnimatePresence } from "framer-motion";
import { IHeaderProps, IMenuItem, Target } from "./J2NHeader.type";
import J2NMotionFade from "../../atoms/J2NMotionTransition/J2NMotionFade";
import { IconMenu2, IconX } from "@tabler/icons-react";
import J2NLogo from "./J2N-logo.svg";
import J2NLanguages from "../J2NLanguages";
import J2NAccount from "../J2NAccount";
import { useAppStore } from "@repo/store";

const J2NHeader = (props: IHeaderProps) => {
  const {
    logoSrc,
    title,
    className,
    redirectUrl,
    hideMenu,
    hideLogo = false,
    accountProps,
    menuItems: propMenuItems,
  } = props;
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const { i18n, t } = useTranslation();
  const { configurations } = useAppStore();

  const handleChangeLanguage = (lang: string) => {
    i18n.changeLanguage(lang);
  };

  const menuItems: IMenuItem[] = hideMenu
    ? []
    : propMenuItems || [
        { name: t("About me"), href: "/about-me", target: Target.CURRENT_TAB },
        { name: t("System"), href: "/system", target: Target.CURRENT_TAB },
        { name: t("room"), href: "/room", target: Target.NEW_TAB },
        // { name: t("store"), href: "/store", target: Target.NEW_TAB },
        // { name: t("Travel"), href: "/travel", target: Target.NEW_TAB },
        {
          name: t("Management"),
          href: configurations?.["management-portal.base-url"] || "http://localhost:3101/",
          target: Target.NEW_TAB,
        },
      ];

  let finalLogoSrc = logoSrc;
  if (!finalLogoSrc) {
    if (typeof J2NLogo === "object" && J2NLogo !== null && "src" in J2NLogo) {
      finalLogoSrc = (J2NLogo as any).src;
    } else {
      finalLogoSrc = J2NLogo as string;
    }
  }

  return (
    <header
      id="j2n-header"
      className={`header-wrapper bg-transparent w-full h-auto absolute z-50 pl-3 ${className}`}
    >
      <J2NMotionFade>
        <Flex
          justify={hideLogo ? "flex-end" : "space-between"}
          align="center"
          className="header-content w-full"
        >
          {!hideLogo && (
            <Link
              href={redirectUrl || "/"}
              className="header-branch flex items-center cursor-pointer"
            >
              <div className="branch-logo max-w-[80px] max-h-[80px]">
                <img
                  className="j2n-logo object-contain scale-[2]"
                  src={finalLogoSrc}
                  alt={title}
                  width={"100%"}
                  height={"100%"}
                />
              </div>
              <h1 className="branch-title text-xl font-secondary-700 text-j2n-grape-deep-500">
                {title}
              </h1>
            </Link>
          )}
          <div className="header-menu mx-5 hidden lg:flex items-center gap-2 flex-row">
            {menuItems?.map((item, index) => (
              <Link
                id={`menu-item-${index}`}
                className="menu-item text-md text-j2n-ink-500 opacity-70 font-secondary-500 mx-3 hover:opacity-100"
                key={index}
                href={item.href}
                target={item.target}
              >
                {item.name}
              </Link>
            ))}
            {accountProps && <J2NAccount {...accountProps} />}
            <J2NLanguages
              defaultLanguage={i18n.language}
              onChange={handleChangeLanguage}
            />
          </div>

          {/* Mobile Menu Toggle */}
          <div className="lg:hidden mx-5 cursor-pointer text-j2n-ink-500">
            {isMobileMenuOpen ? (
              <IconX size={32} onClick={() => setIsMobileMenuOpen(false)} />
            ) : (
              <IconMenu2 size={32} onClick={() => setIsMobileMenuOpen(true)} />
            )}
          </div>
        </Flex>

        {/* Mobile Menu Content */}
        <AnimatePresence>
          {isMobileMenuOpen && (
            <motion.div
              initial={{ y: -50, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              exit={{ y: -50, opacity: 0 }}
              transition={{ duration: 0.3, ease: "easeInOut" }}
              className="lg:hidden w-full absolute left-0 z-50 shadow-lg border-gray-200"
            >
              <Flex
                direction="column"
                className="bg-j2n-sand-light-300 backdrop-blur-sm p-4 w-full"
              >
                {menuItems?.map((item, index) => (
                  <Link
                    key={index}
                    href={item.href}
                    target={item.target || Target.CURRENT_TAB}
                    className="py-3 text-xl font-secondary-500 text-j2n-grape-deep-500 border-b border-gray-100 last:border-0"
                    onClick={() => setIsMobileMenuOpen(false)}
                  >
                    {item.name}
                  </Link>
                ))}
                {accountProps && (
                  <div className="py-2 border-b border-gray-100">
                    <J2NAccount {...accountProps} />
                  </div>
                )}
                <div className="py-3 flex items-center gap-2">
                  <span className="text-xl font-secondary-500">Language: </span>
                  <J2NLanguages
                    defaultLanguage={i18n.language}
                    onChange={handleChangeLanguage}
                  />
                </div>
              </Flex>
            </motion.div>
          )}
        </AnimatePresence>
      </J2NMotionFade>
    </header>
  );
};

export default J2NHeader;
