import { request } from '@/lib/axios/client';
import { HTTP_METHODS } from '@/constants';

export const authApi = {
  async hello() {
    const options = {
      method: HTTP_METHODS.GET
    }
    return await request('/api/bff',options);
  },
};