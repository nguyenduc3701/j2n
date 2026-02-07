import { IconBrandGithub } from "@tabler/icons-react";
import Link from "next/link";

export interface GithubDividerProps {
  url: string;
  className?: string;
}

const GithubDivider = ({ url, className }: GithubDividerProps) => {
  return (
    <div className={`custom-divider-wrapper flex items-end ${className}`}>
      <div className="relative z-10 p-1 w-fit h-fit bg-transparent rounded-full border-[8px] md:border-[12px] lg:border-[15px] border-j2n-plum-dark-500!">
        <IconBrandGithub className="text-j2n-plum-dark-500! w-8 h-8 md:w-10 md:h-10 lg:w-12 lg:h-12" />
      </div>
      <div
        id="bottom-divider"
        className="relative flex-1 h-[8px] md:h-[12px] lg:h-[15px] bg-j2n-plum-dark-500! rounded-r-full -ml-5 md:-ml-8 lg:-ml-10 min-w-[50px]"
      >
        <Link
          href={url}
          target="_blank"
          className="text-j2n-plum-dark-500! italic absolute bottom-full right-8 mb-1 text-xl"
        >
          {url}
        </Link>
      </div>
    </div>
  );
};

export default GithubDivider;
