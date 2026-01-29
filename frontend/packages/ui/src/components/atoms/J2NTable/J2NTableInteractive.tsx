"use client";

import {
  Checkbox,
  Flex,
  Pagination,
  Table,
  TableScrollContainer,
  TableTbody,
  TableTd,
  TableTh,
  TableThead,
  TableTr,
} from "@mantine/core";
import { useEffect, useState } from "react";
import { IJ2NTableInteractiveProps } from "./J2NTable.type";

const J2NTableInteractive = <T extends Record<string, any>>({
  headers,
  renderedRows,
  useCheckbox,
  onCheckboxChange,
  highlightOnHover,
  verticalSpacing,
  horizontalSpacing,
  withTableBorder,
  withColumnBorders,
  minWidth,
  className,
  data, // Lấy ra để không truyền vào Mantine Table
  pageSize,
  total,
  onPageChange,
  ...props
}: IJ2NTableInteractiveProps<T>) => {
  const [selectedIds, setSelectedIds] = useState<(string | number)[]>([]);
  const [activePage, setPage] = useState(1);

  const toggleAll = () => {
    if (selectedIds.length === renderedRows.length) {
      setSelectedIds([]);
    } else {
      setSelectedIds(renderedRows.map((row) => row.id));
    }
  };

  const toggleRow = (id: string | number) => {
    setSelectedIds((current) =>
      current.includes(id) ? current.filter((i) => i !== id) : [...current, id],
    );
  };

  // Thông báo sự thay đổi ra bên ngoài
  useEffect(() => {
    if (onCheckboxChange) {
      const selectedRecords = renderedRows
        .filter((row) => selectedIds.includes(row.id))
        .map((row) => row.record);
      onCheckboxChange(selectedRecords);
    }
  }, [selectedIds]);

  // Handle page change
  const handlePageChange = (page: number) => {
    setPage(page);
    if (onPageChange) {
      onPageChange(page);
    }
  };

  // Paginate rows if pageSize is provided
  const paginatedRows = pageSize
    ? renderedRows.slice((activePage - 1) * pageSize, activePage * pageSize)
    : renderedRows;

  const totalPages = pageSize
    ? Math.ceil((total || renderedRows.length) / pageSize)
    : 0;

  return (
    <Flex direction="column" gap="md">
      <TableScrollContainer minWidth={minWidth || "100%"}>
        <Table
          highlightOnHover={highlightOnHover}
          verticalSpacing={verticalSpacing}
          horizontalSpacing={horizontalSpacing}
          withTableBorder={withTableBorder}
          withColumnBorders={withColumnBorders}
          {...props}
          className={`j2n-table border-j2n-sand-medium-400 ${className || ""}`}
          styles={{
            table: { backgroundColor: "transparent" },
            thead: { backgroundColor: "transparent" },
            th: {
              color: "#0f080f",
              fontWeight: 600,
              fontFamily: "var(--font-secondary)",
            },
            ...props.styles,
          }}
        >
          <TableThead>
            <TableTr>
              {useCheckbox && (
                <TableTh style={{ width: 40 }}>
                  <Checkbox
                    checked={
                      selectedIds.length === renderedRows.length &&
                      renderedRows.length > 0
                    }
                    indeterminate={
                      selectedIds.length > 0 &&
                      selectedIds.length < renderedRows.length
                    }
                    onChange={toggleAll}
                  />
                </TableTh>
              )}
              {headers.map((title, idx) => (
                <TableTh key={idx}>{title}</TableTh>
              ))}
            </TableTr>
          </TableThead>
          <TableTbody>
            {paginatedRows.map((row) => (
              <TableTr key={row.id}>
                {useCheckbox && (
                  <TableTd>
                    <Checkbox
                      checked={selectedIds.includes(row.id)}
                      onChange={() => toggleRow(row.id)}
                    />
                  </TableTd>
                )}
                {row.cells.map((cell, cellIdx) => (
                  <TableTd key={cellIdx}>{cell}</TableTd>
                ))}
              </TableTr>
            ))}
          </TableTbody>
        </Table>
      </TableScrollContainer>

      {pageSize && totalPages > 1 && (
        <Flex justify="center" mt="md">
          <Pagination
            total={totalPages}
            value={activePage}
            onChange={handlePageChange}
            color="j2n-ink.5"
            size="sm"
            radius="md"
          />
        </Flex>
      )}
    </Flex>
  );
};

export default J2NTableInteractive;
