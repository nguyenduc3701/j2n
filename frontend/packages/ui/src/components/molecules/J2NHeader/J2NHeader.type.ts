export interface IHeaderProps {
  logoSrc: string;
  logoWidth?: string | number;
  logoHeight?: string | number;
  className?: string;
  title?: string;
  menuItems?: IMenuItem[];
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
