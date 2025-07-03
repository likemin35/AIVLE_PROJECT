// src/api/authorApi.js
import apiClient, { API_ENDPOINTS } from './apiConfig';

export const authorApi = {
  // 작가 등록
  registerAuthor: async (authorData) => {
    try {
      const response = await apiClient.post(`${API_ENDPOINTS.AUTHORS}`, {
        email: authorData.email,
        authorName: authorData.name,
        introduction: authorData.bio,
        feturedWorks: authorData.mainWork,
        portfolios: authorData.portfolios.map(p => ({
          category: p.category,
          works: p.title
        })),
        isApprove: null // 초기에는 null로 설정
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '작가 등록 중 오류가 발생했습니다.' };
    }
  },

  // 작가 목록 조회
  getAuthors: async () => {
    try {
      const response = await apiClient.get(`${API_ENDPOINTS.AUTHORS}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '작가 목록 조회 중 오류가 발생했습니다.' };
    }
  },

  // 특정 작가 조회
  getAuthor: async (authorId) => {
    try {
      const response = await apiClient.get(`${API_ENDPOINTS.AUTHORS}/${authorId}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '작가 정보 조회 중 오류가 발생했습니다.' };
    }
  }
};