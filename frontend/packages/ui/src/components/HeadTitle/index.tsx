import React from "react";

interface HeadTitleProps {
  size?: "small" | "medium" | "large" | "xl";
  title: string;
  subTitle?: string;
  divider?: boolean;
}

const HeadTitle: React.FC<HeadTitleProps> = ({
  size = "medium",
  title,
  subTitle,
  divider = false,
}) => {
  const titleSizeClasses = {
    small: "text-lg",
    medium: "text-xl",
    large: "text-2xl",
    xl: "text-3xl",
  };

  return (
    <div className="pb-4 flex items-baseline">
      <h1 className={`font-bold ${titleSizeClasses[size]} text-red-600`}>
        {title}
      </h1>
      {subTitle && (
        <p className="text-gray-400 text-sm ml-3 italic">{subTitle}</p>
      )}
    </div>
  );
};

export default HeadTitle;
