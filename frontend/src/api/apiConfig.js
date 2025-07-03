// src/api/apiConfig.js
import axios from 'axios';

// API Gateway 기본 URL
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8088';

// 각 서비스별 base URL
export const API_ENDPOINTS = {
  AUTH: `${API_BASE_URL}/auth`,
  POINTS: `${API_BASE_URL}/points`,
  AUTHORS: `${API_BASE_URL}/authors`,
  MANUSCRIPTS: `${API_BASE_URL}/manuscripts`,
  USERS: `${API_BASE_URL}/users`,
  SUBSCRIPTIONS: `${API_BASE_URL}/subscriptions`,
  BOOKS: `${API_BASE_URL}/books`,
  AI: `${API_BASE_URL}/ai`,
  EVENTS: `${API_BASE_URL}/events`,
};

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000, // 10초 타임아웃
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 (토큰이 있으면 헤더에 추가)
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // 요청 로깅 (개발 환경에서만)
    if (process.env.NODE_ENV === 'development') {
      console.log('🚀 API Request:', {
        url: config.url,
        method: config.method,
        data: config.data
      });
    }
    
    return config;
  },
  (error) => {
    console.error('❌ Request Error:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터 (에러 처리 및 로깅)
apiClient.interceptors.response.use(
  (response) => {
    // 응답 로깅 (개발 환경에서만)
    if (process.env.NODE_ENV === 'development') {
      console.log('✅ API Response:', {
        url: response.config.url,
        status: response.status,
        data: response.data
      });
    }
    return response;
  },
  (error) => {
    console.error('❌ Response Error:', error);
    
    if (error.response) {
      const { status, data } = error.response;
      
      switch (status) {
        case 401:
          // 인증 오류
          localStorage.removeItem('authToken');
          localStorage.removeItem('user');
          window.location.href = '/';
          break;
        case 403:
          // 권한 오류
          alert('접근 권한이 없습니다.');
          break;
        case 404:
          // 리소스 없음
          console.warn('요청한 리소스를 찾을 수 없습니다.');
          break;
        case 500:
          // 서버 오류
          alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
          break;
        default:
          // 기타 오류
          const errorMessage = data?.message || '알 수 없는 오류가 발생했습니다.';
          console.error(`Error ${status}:`, errorMessage);
      }
    } else if (error.request) {
      // 네트워크 오류
      alert('네트워크 연결을 확인해주세요.');
    }
    
    return Promise.reject(error);
  }
);

// 재시도 로직이 포함된 API 호출 함수
export const apiWithRetry = async (apiCall, maxRetries = 3) => {
  for (let i = 0; i < maxRetries; i++) {
    try {
      return await apiCall();
    } catch (error) {
      if (i === maxRetries - 1) throw error;
      
      // 지수 백오프로 재시도
      const delay = Math.pow(2, i) * 1000;
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }
};

export default apiClient;