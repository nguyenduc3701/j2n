import { request } from "@repo/network";
import { HTTP_METHODS } from "@repo/ui/src/constants";
import { IUser } from "@/types/user";

interface BaseResponse<T> {
  data: T;
  message: string;
  status: number;
}

export const userService = {
  async getMe() {
    // const options = {
    //   method: HTTP_METHODS.GET,
    // };
    // return await request<BaseResponse<IUser>>("/auth/users/me", options);

    return {
      data: {
        data: {
          id: 1,
          user_name: "admin",
          full_name: "Jadon Nguyen",
          email: "jadon.nguyen@example.com",
          phone_number: null,
          birth: null,
          image_url: null,
          room_id: null,
          address: null,
          company: null,
          role_id: "Admin",
          status: "active",
          created_at: "2023-01-01T00:00:00Z",
          updated_at: "2023-01-01T00:00:00Z",
          permissions: ["CAN_VIEW_MANAGEMENT_PAGE"],
        },
        message: "success",
        status: 200,
      },
    } as any;
  },
};
