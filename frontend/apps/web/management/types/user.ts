export interface IUser {
  id: number;
  user_name: string;
  full_name: string;
  email: string;
  phone_number: string | null;
  birth: string | null;
  image_url: string | null;
  room_id: string | null;
  address: string | null;
  company: string | null;
  role_id: string;
  status: string;
  created_at: string;
  updated_at: string;
  permissions: string[];
}
