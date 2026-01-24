import React from "react";

export enum TileSize {
  xl = "xl",
  lg = "lg",
  md = "md",
  sm = "sm",
}

interface TitleProps {
  size?: TileSize;
  title: string;
  subTitle?: string;
  divider?: boolean;
}

const J2NTitle: React.FC<TitleProps> = ({
  size = TileSize.md,
  title,
  subTitle,
  divider = false,
}) => {
  const titleSizeClasses = {
    [TileSize.xl]: "text-3xl",
    [TileSize.lg]: "text-2xl",
    [TileSize.md]: "text-xl",
    [TileSize.sm]: "text-lg",
  };

  return (
    <div className="title-wrapper flex flex-col">
      {divider && (
        <div className="w-full h-0.5 bg-j2n-sand-medium-300 mb-3.5"></div>
      )}
      <div className="flex items-baseline pl-2">
        <h1
          className={`font-secondary-700 ${titleSizeClasses[size]} text-j2n-plum-dark-500`}
        >
          <span className="inline-block w-[70%] border-b-3 border-j2n-mauve-500 whitespace-nowrap text-j2n-plum-dark-600">
            {title}
          </span>
        </h1>

        {subTitle && (
          <p className="text-j2n-ink-500/65 text-xs ml-3 italic">{`${subTitle}`}</p>
        )}
      </div>
    </div>
  );
};

export default J2NTitle;
