// src/api/manuscriptApi.js
import apiClient, { API_ENDPOINTS } from './apiConfig';

export const manuscriptApi = {
  // 원고 등록
  registerManuscript: async (manuscriptData) => {
    try {
      const response = await apiClient.post(`${API_ENDPOINTS.MANUSCRIPTS}`, {
        title: manuscriptData.title,
        content: manuscriptData.content,
        authorId: manuscriptData.authorId,
        isApprove: manuscriptData.isApprove || true
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '원고 등록 중 오류가 발생했습니다.' };
    }
  },

  // 원고 목록 조회
  getAllManuscripts: async () => {
    try {
      const response = await apiClient.get(`${API_ENDPOINTS.MANUSCRIPTS}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '원고 목록 조회 중 오류가 발생했습니다.' };
    }
  },

  // 원고 수정
  updateManuscript: async (bookId, manuscriptData) => {
    try {
      const response = await apiClient.put(`${API_ENDPOINTS.MANUSCRIPTS}/${bookId}`, {
        title: manuscriptData.title,
        content: manuscriptData.content,
        authorId: manuscriptData.authorId
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '원고 수정 중 오류가 발생했습니다.' };
    }
  },

  // 특정 원고 조회
  getManuscriptById: async (bookId) => {
    try {
      const response = await apiClient.get(`${API_ENDPOINTS.MANUSCRIPTS}/${bookId}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '원고 조회 중 오류가 발생했습니다.' };
    }
  },

  // 출판 신청
  requestPublish: async (bookId, requestData) => {
    try {
      const response = await apiClient.post(`${API_ENDPOINTS.MANUSCRIPTS}/${bookId}/request-publish`, {
        status: requestData.status || 'PUBLISHED',
        isApprove: requestData.isApprove || true
      });
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '출판 신청 중 오류가 발생했습니다.' };
    }
  },

  // 원고 삭제
  deleteManuscript: async (bookId) => {
    try {
      const response = await apiClient.delete(`${API_ENDPOINTS.MANUSCRIPTS}/${bookId}`);
      return response.data;
    } catch (error) {
      throw error.response?.data || { message: '원고 삭제 중 오류가 발생했습니다.' };
    }
  }
};