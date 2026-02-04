"use client";

import React from "react";
import J2NTable from "@repo/components/atoms/J2NTable";

interface ProjectsTableProps {
  data: any[];
}

const ProjectsTable: React.FC<ProjectsTableProps> = ({ data }) => {
  const columns = [
    { key: "id", title: "No." },
    {
      key: "name",
      title: "Project Name",
      render: (record: any) => (
        <span className="font-secondary-600">{record.name}</span>
      ),
    },
    { key: "tech", title: "Technologies" },
    { key: "year", title: "Year" },
  ];

  const handleCheckboxChange = (selected: any[]) => {
    console.log("Selected Projects in Client:", selected);
  };

  return (
    <J2NTable
      columns={columns}
      data={data}
      //   useCheckbox={true}
      pageSize={2}
      //   onCheckboxChange={handleCheckboxChange}
    />
  );
};

export default ProjectsTable;
