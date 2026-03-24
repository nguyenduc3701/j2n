import { request } from "@repo/network";
import { HTTP_METHODS } from "@repo/ui/src/constants";
import { BaseResponse } from "@/types/services";

export const authApi = {
  async login(data: any) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<any>>(
      "/api/bff/login",
      options
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async register(data: any) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<any>>(
      "/api/bff/register",
      options
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async logout() {
    const options = {
      method: HTTP_METHODS.POST,
    };
    const response = await request<BaseResponse<void>>(
      "/api/bff/logout",
      options
    );
    return {
      ...response.data,
      status: response.status,
    };
  },
};
