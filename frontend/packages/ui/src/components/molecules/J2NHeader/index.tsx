"use client";

import React from "react";
import Link from "next/link";
import { IHeaderProps } from "./J2NHeader.type";
import { IconHelp } from "@tabler/icons-react";

const J2NHeader = (props: IHeaderProps) => {
  const { logoSrc, logoWidth, logoHeight, title, menuItems } = props;

  const width = logoWidth ? logoWidth : "50";
  const height = logoHeight ? logoHeight : "50";

  const handleHelpClick = () => {
    console.log("Help clicked");
  };

  return (
    <header id="j2n-header" className="header-wrapper w-full h-auto">
      <div className="header-content w-full flex justify-between items-center">
        <div className="header-branch">
          <div className="branch-logo">
            <img
              className="j2n-logo"
              src={logoSrc}
              alt={title}
              width={width}
              height={height}
            />
          </div>
          <div className="branch-title">{title}</div>
        </div>
        <div className="header-menu">
          {menuItems?.map((item, index) => (
            <Link
              className="menu-item"
              key={index}
              href={item.href}
              target={item.target}
            >
              {item.name}
            </Link>
          ))}
          <IconHelp title="Help" onClick={handleHelpClick} />
        </div>
      </div>
    </header>
  );
};

export default J2NHeader;
