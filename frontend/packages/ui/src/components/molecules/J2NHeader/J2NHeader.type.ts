export interface IHeaderProps {
  logoSrc?: string;
  className?: string;
  title?: string;
}

export interface IMenuItem {
  name: string;
  href: string;
  target?: Target;
}

export enum Target {
  NEW_TAB = "_blank",
  CURRENT_TAB = "_self",
}
