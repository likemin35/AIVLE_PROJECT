// src/api/pointApi.js
import apiClient, { API_ENDPOINTS } from './apiConfig';

export const pointApi = {
  // 포인트 조회
  getPoints: async (userId) => {
    try {
      console.log('💰 포인트 조회 시작:', userId);
      const response = await apiClient.get(`${API_ENDPOINTS.POINTS}/search`, {
        params: { userId, isSubscription: false }
      });
      console.log('✅ 포인트 조회 성공:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ 포인트 조회 실패:', error);
      // 포인트가 없는 경우 0으로 반환
      if (error.response?.status === 404) {
        return { point: 0 };
      }
      throw error.response?.data || { message: '포인트 조회 중 오류가 발생했습니다.' };
    }
  },

  // 포인트 충전
  chargePoints: async (userId, amount) => {
    try {
      console.log('💳 포인트 충전 시작:', { userId, amount });
      const response = await apiClient.post(`${API_ENDPOINTS.POINTS}/charge`, {
        userId,
        amount
      });
      console.log('✅ 포인트 충전 성공:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ 포인트 충전 실패:', error);
      throw error.response?.data || { message: '포인트 충전 중 오류가 발생했습니다.' };
    }
  },

  // 포인트 사용
  usePoints: async (userId, amount, isSubscription = false) => {
    try {
      console.log('💸 포인트 사용 시작:', { userId, amount, isSubscription });
      const response = await apiClient.post(`${API_ENDPOINTS.POINTS}/use`, {
        userId,
        amount,
        isSubscription
      });
      console.log('✅ 포인트 사용 성공:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ 포인트 사용 실패:', error);
      throw error.response?.data || { message: '포인트 사용 중 오류가 발생했습니다.' };
    }
  }
};