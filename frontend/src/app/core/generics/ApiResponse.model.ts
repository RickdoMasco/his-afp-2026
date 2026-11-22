export type ApiResponseStatus = 'SUCCESS' | 'FAIL' | 'ERROR';

export interface ApiResponse<T> {
  status: ApiResponseStatus;
  statusCode: number;
  timestamp: string;
  data?: T;
  message?: string;
  errorCode?: string;
}

export interface Page<T> {
  content: T[];
  empty: boolean;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
