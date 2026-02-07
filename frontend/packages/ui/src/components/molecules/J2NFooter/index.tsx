import React from "react";
import J2NTransText from "../../atoms/J2NTransText";
import { IJ2NFooterProps } from "./J2NFooter.type";

const J2NFooter: React.FC<IJ2NFooterProps> = ({ text, className }) => {
  return (
    <footer
      className={`footer-wrapper w-full h-fit border-t border-j2n-sand-medium-400 ${className}`}
    >
      <J2NTransText
        tKey={text || "footer_text"}
        className="text-j2n-ink-500 text-center py-5 opacity-65 cursor-default"
      />
    </footer>
  );
};

export default J2NFooter;
