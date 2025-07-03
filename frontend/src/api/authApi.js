// src/api/authApi.js
import apiClient, { API_ENDPOINTS } from './apiConfig';

export const authApi = {
  // 로그인
  login: async (email, password) => {
    try {
      const response = await apiClient.post(`${API_ENDPOINTS.AUTH}/login`, {
        email,
        password
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { success: false, message: '로그인 중 오류가 발생했습니다.' };
    }
  },

  // 회원가입
  signup: async (userData) => {
    try {
      const response = await apiClient.post(`${API_ENDPOINTS.AUTH}/signup`, {
        email: userData.email,
        userName: userData.username,
        phoneNumber: userData.phone,
        password: userData.password,
        isKt: userData.carrier === 'KT'
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { success: false, message: '회원가입 중 오류가 발생했습니다.' };
    }
  },

  // 로그아웃
  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }
};