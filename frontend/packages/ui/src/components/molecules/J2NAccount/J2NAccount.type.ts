export interface IJ2NAccountProps {
  isLogin: boolean;
  userName: string;
  roleName?: string;
  size?: number;
  baseUrl?: string;
  logoutAction?: () => void;
}

export interface IDropdownMenuItem {
  label: string;
  icon: React.ReactNode;
  href?: string;
  onClick?: () => void;
}
