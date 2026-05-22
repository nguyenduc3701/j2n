import { MantineSpacing, TableProps } from "@mantine/core";
import React from "react";

export interface IColumn<T> {
  key: string;
  title: React.ReactNode;
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
  activePage?: number;
  fontSize?: string | number;
  loading?: boolean;
  emptyState?: React.ReactNode;
}

export interface IRenderedRow<T> {
  id: string | number;
  cells: React.ReactNode[];
  record: T;
}

export interface IJ2NTableInteractiveProps<T>
  extends Omit<IJ2NTableProps<T>, "columns"> {
  headers: React.ReactNode[];
  renderedRows: IRenderedRow<T>[];
}
