import { IProduct, IProductCategory } from "@/types/product";

// ─── Modal Mode ───────────────────────────────────────────────────────────────

export enum TourModalMode {
  VIEW = "view",
  EDIT = "edit",
  CREATE = "create",
}

// ─── Prop Types ───────────────────────────────────────────────────────────────

export interface TourTableProps {
  data: IProduct[];
  loading: boolean;
  onView: (tour: IProduct) => void;
  onEdit: (tour: IProduct) => void;
  onDelete: (tour: IProduct) => void;
}

export interface TourSearchFormProps {
  categories: IProductCategory[];
  onSearch: (values: TourSearchValues) => void;
  onClear: () => void;
}

export interface TourModalProps {
  opened: boolean;
  mode: TourModalMode;
  tour: IProduct | null;
  categories: IProductCategory[];
  loading: boolean;
  onClose: () => void;
  onSubmit: (values: Partial<IProduct>) => Promise<void>;
  onModeChange: (mode: TourModalMode) => void;
}

export interface CategoryTabProps {
  categories: IProductCategory[];
  loading: boolean;
  onRefresh?: () => void;
}

export interface CategoryModalProps {
  opened: boolean;
  category: IProductCategory | null;
  loading: boolean;
  onClose: () => void;
  onSubmit: (values: Partial<IProductCategory>) => Promise<void>;
}

// ─── Search / Form Values ─────────────────────────────────────────────────────

export interface TourSearchValues {
  title?: string;
  category_id?: string;
}

export const initialTourSearchValues: TourSearchValues = {
  title: "",
  category_id: "",
};
