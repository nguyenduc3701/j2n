import { request } from "@repo/network";
import { HTTP_METHODS } from "@repo/ui/src/constants";
import { BaseResponse } from "@/types/services";
import {
  IRoom,
  IRoomResponse,
  IAsset,
  IAssetResponse,
  IBill,
  IBillResponse,
  IFee,
  IRoomFee,
  IRoomMember,
} from "@/types/room";

export const roomService = {
  // --- Rooms APIs ---
  async searchRooms(params: {
    page?: number;
    size?: number;
    room_number?: string;
    floor?: number;
    status?: string;
    min_price?: number;
    max_price?: number;
    area?: string;
    max_people?: number;
    [key: string]: any;
  }) {
    const { page = 1, size = 10, ...rest } = params;
    const options = {
      method: HTTP_METHODS.POST,
      data: {
        page,
        size,
        ...rest,
      },
    };
    const response = await request<BaseResponse<IRoomResponse>>(
      "/api/bff/rooms/search",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async getRoomById(id: string | number) {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<IRoom>>(
      `/api/bff/rooms/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async updateRoom(id: string | number, data: Partial<IRoom>) {
    const options = {
      method: HTTP_METHODS.PUT,
      data,
    };
    const response = await request<BaseResponse<IRoom>>(
      `/api/bff/rooms/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async createRoom(data: Partial<IRoom>) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<IRoom>>(
      "/api/bff/rooms/",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async deleteRoom(id: string | number) {
    const options = {
      method: HTTP_METHODS.DELETE,
    };
    const response = await request<BaseResponse<void>>(
      `/api/bff/rooms/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async updateRoomFees(roomId: string | number, feeIds: number[]) {
    const options = {
      method: HTTP_METHODS.PUT,
      data: {
        fee_ids: feeIds,
      },
    };
    const response = await request<BaseResponse<IRoomFee[]>>(
      `/api/bff/rooms/${roomId}/fees`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async updateRoomAssets(roomId: string | number, assets: { asset_id: number; quantity: number }[]) {
    const options = {
      method: HTTP_METHODS.PUT,
      data: {
        assets,
      },
    };
    const response = await request<BaseResponse<void>>(
      `/api/bff/rooms/${roomId}/assets`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  // --- Assets APIs ---
  async searchAssets(params: {
    page?: number;
    size?: number;
    name?: string;
    description?: string;
  }) {
    const { page = 1, size = 10, name, description } = params;
    const options = {
      method: HTTP_METHODS.POST,
      data: { page, size, name, description },
    };
    const response = await request<BaseResponse<IAssetResponse>>(
      "/api/bff/rooms/assets/search",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async createAsset(data: Partial<IAsset>) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<IAsset>>(
      "/api/bff/rooms/assets",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async updateAsset(id: string | number, data: Partial<IAsset>) {
    const options = {
      method: HTTP_METHODS.PUT,
      data,
    };
    const response = await request<BaseResponse<IAsset>>(
      `/api/bff/rooms/assets/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async deleteAsset(id: string | number) {
    const options = {
      method: HTTP_METHODS.DELETE,
    };
    const response = await request<BaseResponse<void>>(
      `/api/bff/rooms/assets/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  // --- Bills APIs ---
  async searchBills(params: {
    page?: number;
    size?: number;
    room_id?: number;
    month?: number;
    year?: number;
    status?: string;
  }) {
    const { page = 1, size = 10, ...rest } = params;
    const options = {
      method: HTTP_METHODS.POST,
      data: { page, size, ...rest },
    };
    const response = await request<BaseResponse<IBillResponse>>(
      "/api/bff/rooms/bills/search",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async searchBillsAdmin(params: {
    page?: number;
    size?: number;
    room_id?: number;
    month?: number;
    year?: number;
    status?: string;
  }) {
    const { page = 1, size = 10, ...rest } = params;
    const options = {
      method: HTTP_METHODS.POST,
      data: { page, size, ...rest },
    };
    const response = await request<BaseResponse<IBillResponse>>(
      "/api/bff/rooms/bills/admin/search",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async calculateBill(data: {
    room_id: number;
    month?: number;
    electricity_new_index: number;
  }) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<IBill>>(
      "/api/bff/rooms/bills/calculate",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async calculateAllBills(data: { month?: number; electric_indices: Record<number, number> }) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<IBill[]>>(
      `/api/bff/rooms/bills/calculate-all`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async getBillsByRoomId(roomId: string | number) {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<IBill[]>>(
      `/api/bff/rooms/bills/room/${roomId}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async payBill(billId: string) {
    const options = {
      method: HTTP_METHODS.POST,
    };
    const response = await request<BaseResponse<any>>(
      `/api/bff/rooms/bills/${billId}/pay`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  // --- Fees APIs ---
  async createFee(data: Partial<IFee>) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<IFee>>(
      "/api/bff/rooms/fees",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async getActiveFees() {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<IFee[]>>(
      "/api/bff/rooms/fees",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async updateFee(id: string | number, data: Partial<IFee>) {
    const options = {
      method: HTTP_METHODS.PUT,
      data,
    };
    const response = await request<BaseResponse<IFee>>(
      `/api/bff/rooms/fees/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  // --- Member Mapping APIs ---
  async mapMemberToRoom(data: {
    room_id: number;
    user_ids: number[];
    is_primary?: boolean;
  }) {
    const options = {
      method: HTTP_METHODS.POST,
      data,
    };
    const response = await request<BaseResponse<IRoomMember[]>>(
      "/api/bff/rooms/members/mapping",
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async updateRoomMember(id: string | number, data: { is_primary: boolean }) {
    const options = {
      method: HTTP_METHODS.PUT,
      data,
    };
    const response = await request<BaseResponse<IRoomMember>>(
      `/api/bff/rooms/members/${id}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async deleteRoomMemberByUserId(userId: string | number) {
    const options = {
      method: HTTP_METHODS.DELETE,
    };
    const response = await request<BaseResponse<void>>(
      `/api/bff/rooms/members/user/${userId}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async getRoomMembersByRoomId(roomId: string | number) {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<IRoomMember[]>>(
      `/api/bff/rooms/members/room/${roomId}`,
      options,
    );
    return {
      ...response.data,
      status: response.status,
    };
  },
};
