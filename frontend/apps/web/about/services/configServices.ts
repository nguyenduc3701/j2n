import { request } from "@repo/network";
import { HTTP_METHODS } from "@repo/ui/src/constants";

import { BaseResponse, IConfiguration } from "@/types/services";

export const configApi = {
  async getPortfolioConfigurations() {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<IConfiguration[]>>(
      "/api/bff/configuration/portfolio",
      options
    );
    return {
      ...response.data,
      status: response.status,
    };
  },

  async getGlobalConfigurations() {
    const options = {
      method: HTTP_METHODS.GET,
    };
    const response = await request<BaseResponse<IConfiguration[]>>(
      "/api/bff/configuration/global",
      options
    );
    return {
      ...response.data,
      status: response.status,
    };
  },
};
