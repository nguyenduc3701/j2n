import { request } from "@repo/network";
import { HTTP_METHODS } from "@repo/ui/src/constants";
import { IUser } from "@/types/user";
import { BaseResponse } from "@/types/services";

export const userService = {
  async getMe() {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<IUser>>(
      "/api/bff/users/me",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async updateUser(id: string, data: any) {
    const options = {
      method: HTTP_METHODS.PUT,
      data,
    };
    const response = await request<BaseResponse<IUser>>(
      `/api/bff/users/${id}`,
      options
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async uploadImage(data: FormData) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    };
    const response = await request<BaseResponse<any>>(
      "/api/bff/image/upload",
      options
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async getPermissions() {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<any[]>>(
      "/api/bff/permissions",
      options
    );
    return {
      ...response.data,
      status: response.status,
    };
  },
};
