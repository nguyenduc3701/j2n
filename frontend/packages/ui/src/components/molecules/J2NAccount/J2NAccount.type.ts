export interface IJ2NAccountProps {
  isLogin: boolean;
  userName: string;
  roleName?: string;
  size?: number;
  menuItems?: IDropdownMenuItem[];
  logoutAction?: () => void;
  helpAction?: () => void;
}

export interface IDropdownMenuItem {
  label: string;
  icon: React.ReactNode;
  href: string;
  onClick?: () => void;
}
