"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Flex } from "@mantine/core";
import { motion, AnimatePresence } from "framer-motion";
import { IHeaderProps, IMenuItem, Target } from "./J2NHeader.type";
import J2NMotionFade from "../../atoms/J2NMotionTransition/J2NMotionFade";
import { IconHelp, IconMenu2, IconX } from "@tabler/icons-react";
import J2NLogo from "./J2N-logo.svg";

const mockMenuItems: IMenuItem[] = [
  { name: "Room", href: "/room", target: Target.NEW_TAB },
  { name: "Store", href: "/store", target: Target.NEW_TAB },
  { name: "Travel", href: "/travel", target: Target.NEW_TAB },
  { name: "Management", href: "/management", target: Target.CURRENT_TAB },
  { name: "System", href: "/system", target: Target.CURRENT_TAB },
  { name: "About me", href: "/about-me", target: Target.CURRENT_TAB },
];

const J2NHeader = (props: IHeaderProps) => {
  const { logoSrc, title, className } = props;
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  let finalLogoSrc = logoSrc;
  if (!finalLogoSrc) {
    if (typeof J2NLogo === "object" && J2NLogo !== null && "src" in J2NLogo) {
      finalLogoSrc = (J2NLogo as any).src;
    } else {
      finalLogoSrc = J2NLogo as string;
    }
  }

  const handleHelpClick = () => {
    console.log("Help clicked");
  };

  return (
    <header
      id="j2n-header"
      className={`header-wrapper bg-transparent w-full h-auto relative ${className}`}
    >
      <J2NMotionFade>
        <Flex
          justify="space-between"
          align="center"
          className="header-content w-full"
        >
          <Link
            href="/"
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
          <div className="header-menu mx-5 hidden lg:flex items-center gap-2 flex-row">
            {mockMenuItems?.map((item, index) => (
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
            <IconHelp
              className="cursor-pointer opacity-80 hover:opacity-100 text-j2n-ink-500"
              title="Help"
              id="help-ico"
              onClick={handleHelpClick}
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
                {mockMenuItems?.map((item, index) => (
                  <Link
                    key={index}
                    href={item.href}
                    target={item.target}
                    className="py-3 text-xl font-secondary-500 text-j2n-grape-deep-500 border-b border-gray-100 last:border-0"
                    onClick={() => setIsMobileMenuOpen(false)}
                  >
                    {item.name}
                  </Link>
                ))}
                <div
                  className="py-3 flex items-center gap-2 cursor-pointer text-j2n-ink-500 hover:text-j2n-grape-deep-500"
                  onClick={handleHelpClick}
                >
                  <IconHelp title="Help" id="help-ico" size={24} />
                  <span className="text-xl font-secondary-500">Help</span>
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
