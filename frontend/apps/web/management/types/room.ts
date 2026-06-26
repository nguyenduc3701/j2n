import { IPagingResponse } from "./services";

export interface IRoom {
  id: number;
  room_number: string;
  floor?: number;
  base_price?: number;
  area?: string;
  max_people?: number;
  status: string;
  description?: string;
  is_immutable?: boolean;
  created_at?: string;
  updated_at?: string;
  assets?: IAsset[];
  members?: IRoomMember[];
  fees?: IRoomFee[];
}

export interface IRoomResponse {
  rooms: IRoom[];
  page: IPagingResponse;
}

export interface IAsset {
  id: number;
  name: string;
  quantity: number;
  description?: string;
  created_at?: string;
  updated_at?: string;
}

export interface IAssetResponse {
  assets: IAsset[];
  page: IPagingResponse;
}

export interface IBill {
  id: string;
  room_id: number;
  room_number: string;
  month: number;
  year: number;
  previous_electric_index: number;
  current_electric_index: number;
  electric_usage: number;
  electric_amount: number;
  previous_water_index: number;
  current_water_index: number;
  water_usage: number;
  water_amount: number;
  room_amount: number;
  other_fees_amount: number;
  total_amount: number;
  status: "PAID" | "UNPAID";
  payment_link?: string;
  created_at?: string;
  updated_at?: string;
}

export interface IBillResponse {
  bills: IBill[];
  page: IPagingResponse;
}

export interface IFee {
  id: number;
  name: string;
  unit_price: number;
  unit_name: string;
  is_active: boolean;
  created_at?: string;
  updated_at?: string;
}

export interface IRoomFee {
  id: number;
  fee_id: number;
  name: string;
  unit_price: number;
  unit_name: string;
}

export interface IRoomMember {
  id: number;
  room_id: number;
  user_id: number;
  full_name: string;
  phone_number?: string;
  email?: string;
  is_primary: boolean;
  joined_at?: string;
}
