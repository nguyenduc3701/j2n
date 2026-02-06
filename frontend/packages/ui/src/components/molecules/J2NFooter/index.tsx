import React from "react";
import { Text } from "@mantine/core";
import { IJ2NFooterProps } from "./J2NFooter.type";

const DEFAULT_TEXT =
  "I appreciate your attention, and i would welcome the opportunity to collaborate when the time comes.";

const J2NFooter: React.FC<IJ2NFooterProps> = ({ text, className }) => {
  const footerText = text || DEFAULT_TEXT;
  return (
    <footer
      className={`footer-wrapper w-full h-fit border-t border-j2n-sand-medium-400 ${className}`}
    >
      <Text className="text-j2n-ink-500 text-center py-5 opacity-65 cursor-default">
        {footerText}
      </Text>
    </footer>
  );
};

export default J2NFooter;
