export interface IProductCategory {
  id: number;
  name: string;
  slug: string;
  type: string;
}

export interface IProductImage {
  id: number;
  product_id?: number;
  image_url: string;
  is_primary: boolean;
  created_at?: string;
  updated_at?: string;
}

export interface IProductSchedule {
  id: number;
  product_id?: number;
  day_number: number;
  title: string;
  content?: string;
  hotel?: string;
  breakfast?: string;
  lunch?: string;
  dinner?: string;
  created_at?: string;
  updated_at?: string;
}

export interface IProduct {
  id: number;
  title: string;
  description?: string;
  price: number;
  thumbnail?: string;
  duration?: string;
  start_location?: string;
  size?: string;
  design?: string;
  type: string;
  category?: IProductCategory;
  category_id?: number;
  schedules?: IProductSchedule[];
  images?: IProductImage[];
  created_at?: string;
  updated_at?: string;
}
