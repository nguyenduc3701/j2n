import { IUser } from "@/types/user";

export enum ModalMode {
  CREATE = "CREATE",
  EDIT = "EDIT",
  VIEW = "VIEW",
}

export interface UserSearchFormProps {
  onSearch: (values: any) => void;
  onClear: () => void;
}

export interface UserTableProps {
  data: IUser[];
  loading?: boolean;
  total: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  activePage?: number;
  onView: (user: IUser) => void;
  onEdit: (user: IUser) => void;
  onDelete: (user: IUser) => void;
}

export interface UserModalProps {
  opened: boolean;
  onClose: () => void;
  mode: ModalMode;
  user?: IUser | null;
  onSubmit: (values: Partial<IUser>) => void;
  loading?: boolean;
  onModeChange?: (mode: ModalMode) => void;
}

export const initialSearchValues = {
  user_name: "",
  email: "",
  full_name: "",
  role_id: null,
  status: null,
};

export const ROLE_MAPPING: Record<string, string> = {
  ADMIN: "1",
  RECRUITER: "2",
  RENTER: "3",
  VISITOR: "4",
};

export const USER_ROLES = [
  { value: ROLE_MAPPING.ADMIN, labelKey: "admin" },
  { value: ROLE_MAPPING.RECRUITER, labelKey: "recruiter" },
  { value: ROLE_MAPPING.RENTER, labelKey: "renter" },
  { value: ROLE_MAPPING.VISITOR, labelKey: "visitor" },
];

export const USER_STATUS_OPTIONS = [
  { value: "ACTIVE", labelKey: "users.status.active" },
  { value: "INACTIVE", labelKey: "users.status.inactive" },
];
