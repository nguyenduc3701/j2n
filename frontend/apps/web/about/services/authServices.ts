import { request } from "@repo/network";
import { HTTP_METHODS } from "@repo/ui/src/constants";

export const authApi = {
  async hello() {
    const options = {
      method: HTTP_METHODS.GET,
    };
    return await request("/api/bff", options);
  },
};
