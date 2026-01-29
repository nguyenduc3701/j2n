import { MantineSpacing, TableProps } from "@mantine/core";
import React from "react";

export interface IColumn<T> {
  key: string;
  title: string;
  render?: (record: T, index: number) => React.ReactNode;
}

export interface IJ2NTableProps<T = any> extends Omit<TableProps, "data"> {
  columns: IColumn<T>[];
  data: T[];
  highlightOnHover?: boolean;
  verticalSpacing?: MantineSpacing;
  horizontalSpacing?: MantineSpacing;
  withTableBorder?: boolean;
  withColumnBorders?: boolean;
  minWidth?: string | number;
  useCheckbox?: boolean;
  onCheckboxChange?: (selectedRows: T[]) => void;
  pageSize?: number;
  total?: number;
  onPageChange?: (page: number) => void;
}

export interface IRenderedRow<T> {
  id: string | number;
  cells: React.ReactNode[];
  record: T;
}
