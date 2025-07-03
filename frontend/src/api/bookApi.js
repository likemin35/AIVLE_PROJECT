// src/api/bookApi.js
import apiClient, { API_ENDPOINTS } from './apiConfig';

export const bookApi = {
  // 도서 목록 조회 (카테고리별)
  getBooks: async (category = null) => {
    try {
      console.log('📚 도서 목록 조회 시작:', category);
      const response = await apiClient.get(`${API_ENDPOINTS.BOOKS}`, {
        params: category ? { category } : {}
      });
      console.log('✅ 도서 목록 조회 성공:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ 도서 목록 조회 실패:', error);
      throw error.response?.data || { message: '도서 목록 조회 중 오류가 발생했습니다.' };
    }
  },

  // 특정 도서 조회
  getBook: async (bookId) => {
    try {
      console.log('📖 도서 상세 조회 시작:', bookId);
      const response = await apiClient.get(`${API_ENDPOINTS.BOOKS}/${bookId}`);
      console.log('✅ 도서 상세 조회 성공:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ 도서 상세 조회 실패:', error);
      throw error.response?.data || { message: '도서 조회 중 오류가 발생했습니다.' };
    }
  },

  // 도서 대여 신청
  rentBook: async (userId, bookId) => {
    try {
      console.log('🏗️ 도서 대여 신청 시작:', { userId, bookId });
      const response = await apiClient.post(`${API_ENDPOINTS.EVENTS}`, {
        eventType: 'ApplySubscription',
        userId,
        bookId
      });
      console.log('✅ 도서 대여 신청 성공:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ 도서 대여 신청 실패:', error);
      throw error.response?.data || { message: '도서 대여 신청 중 오류가 발생했습니다.' };
    }
  }
};