import React from "react";

export interface ProfileFormValues {
  full_name: string | null;
  phone_number: string | null;
  birth: string | null;
  address: string | null;
  company: string | null;
  room_id: string | null;
}

export interface ProfileFormProps {
  user: any;
  isEditing: boolean;
  setIsEditing: (value: boolean) => void;
  isSubmitting: boolean;
  form: any;
  handleUpdate: (values: ProfileFormValues) => void;
}

export interface ProfileHeaderProps {
  user: any;
  isEditing: boolean;
  setIsEditing: (value: boolean) => void;
  onAvatarClick: () => void;
  fileInputRef: React.RefObject<HTMLInputElement | null>;
  onFileUpload: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

export interface ProfileInfoFieldProps {
  label: string;
  value?: React.ReactNode;
  isEditing: boolean;
  children?: React.ReactNode;
  span?: any;
}

export interface ProfilePermissionsProps {
  columns: any[];
  data: any[];
}
