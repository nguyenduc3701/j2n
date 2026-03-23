import { IJ2NAccountProps } from "../J2NAccount/J2NAccount.type";

export interface IHeaderProps {
  logoSrc?: string;
  className?: string;
  title?: string;
  redirectUrl?: string;
  hideMenu?: boolean;
  hideLogo?: boolean;
  accountProps?: IJ2NAccountProps;
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
