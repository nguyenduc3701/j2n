export enum TileSize {
  xl = "xl",
  lg = "lg",
  md = "md",
  sm = "sm",
}

export interface IJ2NTitleProps {
  size?: TileSize;
  title: string;
  subTitle?: string;
  divider?: boolean;
  className?: string;
}
