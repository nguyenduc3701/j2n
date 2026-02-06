import React from "react";
import { IJ2NTitleProps, TileSize } from "./J2NTitle.type";

const J2NTitle: React.FC<IJ2NTitleProps> = ({
  size = TileSize.md,
  title,
  subTitle,
  divider = false,
  className,
  underline = true,
  underlineStyle,
  reverseSubTitle = false,
}) => {
  const titleSizeClasses = {
    [TileSize.xl]: "text-3xl",
    [TileSize.lg]: "text-2xl",
    [TileSize.md]: "text-xl",
    [TileSize.sm]: "text-lg",
  };

  return (
    <div className={`title-wrapper flex flex-col ${className}`}>
      {divider && (
        <div className="w-full h-0.5 bg-j2n-sand-medium-300 mb-3.5"></div>
      )}
      <div className="flex items-baseline">
        {subTitle && reverseSubTitle && (
          <p className="text-j2n-ink-500/65 text-xs mr-3! italic">{`${subTitle}`}</p>
        )}

        <h1
          className={`font-secondary-700 ${titleSizeClasses[size]} text-j2n-plum-dark-500`}
        >
          <span
            className={`relative inline-block w-full ${
              underline
                ? "pb-1 after:content-[''] after:absolute after:left-0 after:bottom-0 after:h-[3px] after:w-[70%] after:bg-j2n-mauve-500"
                : ""
            } whitespace-break-spaces text-j2n-plum-dark-600 pb-3`}
            style={underlineStyle}
          >
            {title}
          </span>
        </h1>

        {subTitle && !reverseSubTitle && (
          <p className="text-j2n-ink-500/65 text-xs ml-3! italic">{`${subTitle}`}</p>
        )}
      </div>
    </div>
  );
};

export default J2NTitle;
