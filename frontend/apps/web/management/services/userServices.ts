import { request } from "@repo/network";
import { HTTP_METHODS } from "@repo/ui/src/constants";
import { IUser } from "@/types/user";
import { BaseResponse, IUserResponse } from "@/types/services";

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

  async getUsers(params: {
    page?: number;
    size?: number;
    user_name?: string;
    email?: string;
    full_name?: string;
    role_id?: string;
    status?: string;
    start_date?: string;
    end_date?: string;
    room_id?: string | number;
  }) {
    const {
      page = 1,
      size = 10,
      user_name,
      email,
      full_name,
      role_id,
      status,
      start_date,
      end_date,
      room_id,
    } = params;

    const options = {
      method: HTTP_METHODS.POST,
      data: {
        page,
        size,
        user_name,
        email,
        full_name,
        role_id,
        status,
        start_date,
        end_date,
        room_id,
      },
    };

    const response = await request<BaseResponse<IUserResponse>>(
      "/api/bff/users/list",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async getUserById(id: string) {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<IUser>>(
      `/api/bff/users/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async createUser(data: Partial<IUser>) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<IUser>>(
      "/api/bff/users",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async updateUser(id: string, data: Partial<IUser>) {
    const options = {
      method: HTTP_METHODS.PUT,
      data,
    };
    const response = await request<BaseResponse<IUser>>(
      `/api/bff/users/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async deleteUser(id: string) {
    const options = {
      method: HTTP_METHODS.DELETE,
    };
    const response = await request<BaseResponse<void>>(
      `/api/bff/users/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async toggleStatus(id: string, currentStatus?: string) {
    const newStatus = currentStatus === "ACTIVE" ? "INACTIVE" : "ACTIVE";
    return this.updateUser(id, { status: newStatus });
  },

  async getRoles() {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<any[]>>(
      "/api/bff/roles",
      options,
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
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async getPermissionsByRoleId(roleId: string) {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<any[]>>(
      `/api/bff/roles/${roleId}/permissions`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },
};
