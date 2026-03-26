"use client";

import React from "react";
import J2NSection from "@repo/components/atoms/J2NSection";
import J2NTransText from "@repo/components/atoms/J2NTransText";
import J2NTable from "@repo/components/atoms/J2NTable";

import { ProfilePermissionsProps } from "./profile.types";

const ProfilePermissions = ({ columns, data }: ProfilePermissionsProps) => {
  return (
    <J2NSection name="permissions" className="py-5">
      <J2NTransText
        tKey="permissions"
        size="lg"
        className="font-secondary-700 text-j2n-plum-dark-500 uppercase tracking-wider mb-10! block"
      />
      <J2NTable columns={columns} data={data} />
    </J2NSection>
  );
};

export default ProfilePermissions;
