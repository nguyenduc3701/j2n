import { IRoom } from "@/types/room";

export enum ModalMode {
  CREATE = "CREATE",
  EDIT = "EDIT",
  VIEW = "VIEW",
}

export interface RoomSearchFormProps {
  onSearch: (values: any) => void;
  onClear: () => void;
}

export interface RoomTableProps {
  data: IRoom[];
  loading?: boolean;
  total: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  activePage?: number;
  onView: (room: IRoom) => void;
  onEdit: (room: IRoom) => void;
  onCalculate: (room: IRoom) => void;
  onConfiguration: (room: IRoom) => void;
  onDelete: (room: IRoom) => void;
}

export interface RoomModalProps {
  opened: boolean;
  onClose: () => void;
  mode: ModalMode;
  room?: IRoom | null;
  onSubmit: (values: Partial<IRoom>) => void;
  loading?: boolean;
  onModeChange?: (mode: ModalMode) => void;
}

export const initialSearchValues = {
  room_number: "",
  floor: "",
  status: null,
};

export const ROOM_STATUS_OPTIONS = [
  { value: "AVAILABLE", labelKey: "rooms.status.available" },
  { value: "OCCUPIED", labelKey: "rooms.status.occupied" },
  { value: "MAINTENANCE", labelKey: "rooms.status.maintenance" },
];
