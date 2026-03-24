import React from "react";
import { IJ2NTableProps, IRenderedRow } from "./J2NTable.type";
import J2NTableInteractive from "./J2NTableInteractive";

/**
 * J2NTable - Hybrid Component
 * - Luôn hỗ trợ SSR: Có thể chạy hàm `render` từ Server Components.
 * - Hỗ trợ Interactive: Tự động dùng Client Component bên trong khi cần (selection, v.v.).
 */
const J2NTable = <T extends Record<string, any>>({
  columns,
  data,
  activePage,
  ...props
}: IJ2NTableProps<T>) => {
  // Logic render cột và dòng (có thể chạy ở Server)
  const headers = columns.map((col) => col.title);
 
  const renderedRows: IRenderedRow<T>[] = data.map((record, index) => ({
    id: record.id || index,
    record,
    cells: columns.map((col) =>
      col.render ? col.render(record, index) : record[col.key],
    ),
  }));
 
  return (
    <J2NTableInteractive
      headers={headers}
      renderedRows={renderedRows}
      data={data}
      activePage={activePage}
      {...props}
    />
  );
};

export default J2NTable;
