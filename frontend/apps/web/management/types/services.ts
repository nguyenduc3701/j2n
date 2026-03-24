import { IUser } from "./user";

export interface IPagingResponse {
  total: number;
  current: number;
  size: number;
}

export interface IUserResponse {
  users: IUser[];
  page: IPagingResponse;
}

export interface BaseResponse<T> {
  data: T;
  message: string;
  code: string;
}
