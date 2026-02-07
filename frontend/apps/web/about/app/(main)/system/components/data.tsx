import React from "react";
import J2NTransText from "@repo/components/atoms/J2NTransText";

export const mockSystemFrontendColumns = [
  {
    key: "application",
    title: <J2NTransText tKey="system.data.columns.application" />,
    render: (record: any) => <J2NTransText tKey={record.application} />,
  },
  {
    key: "renderMode",
    title: <J2NTransText tKey="system.data.columns.render" />,
    render: (record: any) => <J2NTransText tKey={record.renderMode} />,
  },
  {
    key: "purpose",
    title: <J2NTransText tKey="system.data.columns.purpose" />,
    render: (record: any) => <J2NTransText tKey={record.purpose} />,
  },
];

export const mockSystemFrontendData = [
  {
    application: "system.data.apps.about",
    renderMode: "system.data.render.ssr",
    purpose: "system.data.purpose.system",
  },
  {
    application: "system.data.apps.management",
    renderMode: "system.data.render.csr",
    purpose: "system.data.purpose.management",
  },
  {
    application: "system.data.apps.room",
    renderMode: "system.data.render.csr",
    purpose: "system.data.purpose.room",
  },
  {
    application: "system.data.apps.store",
    renderMode: "system.data.render.csr",
    purpose: "system.data.purpose.store",
  },
  {
    application: "system.data.apps.travel",
    renderMode: "system.data.render.ssr",
    purpose: "system.data.purpose.travel",
  },
];

export const mockSystemBackendColumns = [
  {
    key: "service",
    title: <J2NTransText tKey="system.data.columns.service" />,
    render: (record: any) => <J2NTransText tKey={record.service} />,
  },
  {
    key: "responsibility",
    title: <J2NTransText tKey="system.data.columns.responsibility" />,
    render: (record: any) => <J2NTransText tKey={record.responsibility} />,
  },
];

export const mockSystemBackendData = [
  {
    service: "system.data.services.auth",
    responsibility: "system.data.responsibility.auth",
  },
  {
    service: "system.data.services.image",
    responsibility: "system.data.responsibility.image",
  },
  {
    service: "system.data.services.room",
    responsibility: "system.data.responsibility.room",
  },
  {
    service: "system.data.services.store",
    responsibility: "system.data.responsibility.store",
  },
  {
    service: "system.data.services.travel",
    responsibility: "system.data.responsibility.travel",
  },
];
