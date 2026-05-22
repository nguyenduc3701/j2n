import { request } from "@repo/network";
import { HTTP_METHODS } from "@repo/ui/src/constants";
import { BaseResponse } from "@/types/services";
import {
  IProduct,
  IProductCategory,
  IProductImage,
  IProductSchedule,
} from "@/types/product";

// ─── Request Payload Types ──────────────────────────────────────────────────

export interface ICreateProductPayload {
  category_id: number;
  title: string;
  description?: string;
  price: number;
  thumbnail?: string;
  duration?: string;
  start_location?: string;
  size?: string;
  design?: string;
  type: string;
}

export interface ICreateCategoryPayload {
  name: string;
  slug: string;
  type: string;
}

export interface IAddImagePayload {
  product_id: number;
  image_url: string;
  is_primary?: boolean;
}

export interface IAddSchedulePayload {
  product_id: number;
  day_number: number;
  title: string;
  content?: string;
  hotel?: string;
  breakfast?: string;
  lunch?: string;
  dinner?: string;
}

// ─── Product Service ─────────────────────────────────────────────────────────

export const productService = {
  // --- Products ---

  async getAllProducts() {
    const response = await request<BaseResponse<IProduct[]>>(
      "/api/bff/product",
      { method: HTTP_METHODS.GET }
    );
    return { ...response.data, status: response.status };
  },

  async getProductById(id: number | string) {
    const response = await request<BaseResponse<IProduct>>(
      `/api/bff/product/${id}`,
      { method: HTTP_METHODS.GET }
    );
    return { ...response.data, status: response.status };
  },

  async getProductsByType(type: string) {
    const response = await request<BaseResponse<IProduct[]>>(
      `/api/bff/product/type/${type}`,
      { method: HTTP_METHODS.GET }
    );
    return { ...response.data, status: response.status };
  },

  async getProductsByCategory(categoryId: number | string) {
    const response = await request<BaseResponse<IProduct[]>>(
      `/api/bff/product/category/${categoryId}`,
      { method: HTTP_METHODS.GET }
    );
    return { ...response.data, status: response.status };
  },

  async createProduct(data: ICreateProductPayload) {
    const response = await request<BaseResponse<IProduct>>(
      "/api/bff/product",
      {
        method: HTTP_METHODS.POST,
        data: {
          categoryId: data.category_id,
          title: data.title,
          description: data.description,
          price: data.price,
          thumbnail: data.thumbnail,
          duration: data.duration,
          startLocation: data.start_location,
          size: data.size,
          design: data.design,
          type: data.type,
        },
      }
    );
    return { ...response.data, status: response.status };
  },

  async updateProduct(id: number | string, data: Partial<ICreateProductPayload>) {
    const response = await request<BaseResponse<IProduct>>(
      `/api/bff/product/${id}`,
      {
        method: HTTP_METHODS.PUT,
        data: {
          categoryId: data.category_id,
          title: data.title,
          description: data.description,
          price: data.price,
          thumbnail: data.thumbnail,
          duration: data.duration,
          startLocation: data.start_location,
          size: data.size,
          design: data.design,
          type: data.type ?? "travel",
        },
      }
    );
    return { ...response.data, status: response.status };
  },

  async deleteProduct(id: number | string) {
    const response = await request<BaseResponse<void>>(
      `/api/bff/product/${id}`,
      { method: HTTP_METHODS.DELETE }
    );
    return { ...response.data, status: response.status };
  },

  // --- Categories ---

  async getAllCategories() {
    const response = await request<BaseResponse<IProductCategory[]>>(
      "/api/bff/product/categories",
      { method: HTTP_METHODS.GET }
    );
    return { ...response.data, status: response.status };
  },

  async getCategoriesByType(type: string) {
    const response = await request<BaseResponse<IProductCategory[]>>(
      `/api/bff/product/categories/type/${type}`,
      { method: HTTP_METHODS.GET }
    );
    return { ...response.data, status: response.status };
  },

  async createCategory(data: ICreateCategoryPayload) {
    const response = await request<BaseResponse<IProductCategory>>(
      "/api/bff/product/categories",
      { method: HTTP_METHODS.POST, data }
    );
    return { ...response.data, status: response.status };
  },

  async updateCategory(id: number | string, data: ICreateCategoryPayload) {
    const response = await request<BaseResponse<IProductCategory>>(
      `/api/bff/product/categories/${id}`,
      { method: HTTP_METHODS.PUT, data }
    );
    return { ...response.data, status: response.status };
  },

  async deleteCategory(id: number | string) {
    const response = await request<BaseResponse<void>>(
      `/api/bff/product/categories/${id}`,
      { method: HTTP_METHODS.DELETE }
    );
    return { ...response.data, status: response.status };
  },

  // --- Product Images ---

  async getImagesByProductId(productId: number | string) {
    const response = await request<BaseResponse<IProductImage[]>>(
      `/api/bff/product/images/product/${productId}`,
      { method: HTTP_METHODS.GET }
    );
    return { ...response.data, status: response.status };
  },

  async addImageToProduct(data: IAddImagePayload) {
    const response = await request<BaseResponse<IProductImage>>(
      "/api/bff/product/images",
      {
        method: HTTP_METHODS.POST,
        data: {
          productId: data.product_id,
          imageUrl: data.image_url,
          isPrimary: data.is_primary,
        },
      }
    );
    return { ...response.data, status: response.status };
  },

  async deleteProductImage(imageId: number | string) {
    const response = await request<BaseResponse<void>>(
      `/api/bff/product/images/${imageId}`,
      { method: HTTP_METHODS.DELETE }
    );
    return { ...response.data, status: response.status };
  },

  async setPrimaryImage(imageId: number | string) {
    const response = await request<BaseResponse<IProductImage>>(
      `/api/bff/product/images/${imageId}/primary`,
      { method: HTTP_METHODS.PATCH }
    );
    return { ...response.data, status: response.status };
  },

  // --- Product Schedules ---

  async getSchedulesByProductId(productId: number | string) {
    const response = await request<BaseResponse<IProductSchedule[]>>(
      `/api/bff/product/schedules/product/${productId}`,
      { method: HTTP_METHODS.GET }
    );
    return { ...response.data, status: response.status };
  },

  async addScheduleToProduct(data: IAddSchedulePayload) {
    const response = await request<BaseResponse<IProductSchedule>>(
      "/api/bff/product/schedules",
      {
        method: HTTP_METHODS.POST,
        data: {
          productId: data.product_id,
          dayNumber: data.day_number,
          title: data.title,
          content: data.content,
          hotel: data.hotel,
          breakfast: data.breakfast,
          lunch: data.lunch,
          dinner: data.dinner,
        },
      }
    );
    return { ...response.data, status: response.status };
  },

  async updateProductSchedule(
    scheduleId: number | string,
    data: Partial<IAddSchedulePayload>
  ) {
    const response = await request<BaseResponse<IProductSchedule>>(
      `/api/bff/product/schedules/${scheduleId}`,
      {
        method: HTTP_METHODS.PUT,
        data: {
          productId: data.product_id,
          dayNumber: data.day_number,
          title: data.title,
          content: data.content,
          hotel: data.hotel,
          breakfast: data.breakfast,
          lunch: data.lunch,
          dinner: data.dinner,
        },
      }
    );
    return { ...response.data, status: response.status };
  },

  async deleteProductSchedule(scheduleId: number | string) {
    const response = await request<BaseResponse<void>>(
      `/api/bff/product/schedules/${scheduleId}`,
      { method: HTTP_METHODS.DELETE }
    );
    return { ...response.data, status: response.status };
  },
};
