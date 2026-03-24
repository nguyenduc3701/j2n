export interface BaseResponse<T> {
  data: T;
  message: string;
  code: string;
}

export interface IPagingResponse {
  total: number;
  current: number;
  size: number;
}

export interface IConfiguration {
  id: number;
  configKey: string;
  configValue: string;
  configDescription: string;
  isActive: boolean;
  isPublic: boolean;
  category: string;
}
