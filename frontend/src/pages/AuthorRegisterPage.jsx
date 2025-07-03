import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import FormInput from '../components/FormInput';
import TextAreaInput from '../components/TextAreaInput';
import PortfolioList from '../components/PortfolioList';
import { authorApi } from '../api/authorApi';

function AuthorRegisterPage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [form, setForm] = useState({
    email: '',
    name: '',
    bio: '',
    mainWork: '',
    portfolios: [],
  });

  // 사용자 정보 가져오기
  useEffect(() => {
    const userData = localStorage.getItem('user');
    if (userData) {
      const parsedUser = JSON.parse(userData);
      setUser(parsedUser);
      // 사용자 이메일 자동 입력
      setForm(prev => ({
        ...prev,
        email: parsedUser.email
      }));
    } else {
      alert('로그인이 필요합니다.');
      navigate('/');
    }
  }, [navigate]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const validateForm = () => {
    if (!form.email || !form.name || !form.bio || !form.mainWork) {
      alert('모든 필수 항목을 입력해주세요.');
      return false;
    }

    if (form.name.length < 2) {
      alert('작가 이름은 2자 이상이어야 합니다.');
      return false;
    }

    if (form.bio.length < 10) {
      alert('작가 소개는 10자 이상 작성해주세요.');
      return false;
    }

    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    setIsLoading(true);
    try {
      await authorApi.registerAuthor(form);
      alert('작가 등록 신청이 완료되었습니다! 승인까지 1-2일 소요될 예정입니다.');
      navigate('/main');
    } catch (error) {
      console.error('작가 등록 오류:', error);
      alert(error.message || '작가 등록 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9f9f9', color: '#000000', fontFamily: 'sans-serif' }}>
      <Navbar />
      <div style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
        <div style={{ background: '#ffffff', borderRadius: '16px', padding: '2rem', boxShadow: '0 4px 12px rgba(0, 0, 0, 0.05)' }}>
          <h2 style={{ color: '#8E24AA', fontSize: '1.8rem', fontWeight: 'bold', marginBottom: '2rem' }}>작가 등록</h2>

          <div style={{ marginBottom: '1.5rem' }}>
            <FormInput
              label="이메일 *"
              name="email"
              value={form.email}
              onChange={handleChange}
              type="email"
              placeholder="이메일 주소"
              required={true}
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <FormInput
              label="작가 이름 *"
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="필명 또는 실명"
              required={true}
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <TextAreaInput
              label="작가 소개 *"
              name="bio"
              value={form.bio}
              onChange={handleChange}
            />
            <div style={{ fontSize: '0.8rem', color: '#666', marginTop: '0.5rem' }}>
              자신의 글쓰기 스타일, 경험, 관심 분야 등을 소개해주세요. (최소 10자)
            </div>
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <FormInput
              label="대표작 *"
              name="mainWork"
              value={form.mainWork}
              onChange={handleChange}
              placeholder="가장 자신 있는 작품이나 대표작"
              required={true}
            />
          </div>

          <div style={{ marginBottom: '2rem' }}>
            <PortfolioList
              portfolios={form.portfolios}
              setPortfolios={(portfolios) => setForm({ ...form, portfolios })}
            />
            <div style={{ fontSize: '0.8rem', color: '#666', marginTop: '0.5rem' }}>
              포트폴리오는 선택사항입니다. 기존 작품이 있다면 추가해주세요.
            </div>
          </div>

          <div style={{ 
            backgroundColor: '#fff3cd', 
            border: '1px solid #ffeaa7', 
            borderRadius: '8px', 
            padding: '1rem', 
            marginBottom: '2rem' 
          }}>
            <h4 style={{ color: '#856404', margin: '0 0 0.5rem 0' }}>📋 작가 승인 안내</h4>
            <ul style={{ color: '#856404', margin: 0, paddingLeft: '1.2rem' }}>
              <li>작가 등록 신청 후 1-2일 내 승인 결과를 이메일로 안내드립니다.</li>
              <li>승인 완료 후 원고 작성 및 출판 신청이 가능합니다.</li>
              <li>허위 정보 작성 시 승인이 거부될 수 있습니다.</li>
            </ul>
          </div>

          <button
            style={{
              width: '100%',
              padding: '1rem',
              backgroundColor: isLoading ? '#cccccc' : '#8E24AA',
              color: '#ffffff',
              border: 'none',
              borderRadius: '8px',
              fontSize: '1.1rem',
              fontWeight: 'bold',
              cursor: isLoading ? 'not-allowed' : 'pointer',
              transition: 'all 0.3s ease',
              boxShadow: isLoading ? 'none' : '0 4px 12px rgba(142, 36, 170, 0.2)'
            }}
            onClick={handleSubmit}
            disabled={isLoading}
          >
            {isLoading ? '등록 신청 중...' : '등록 신청하기'}
          </button>

          <div style={{ textAlign: 'center', marginTop: '1rem' }}>
            <button
              onClick={() => navigate('/main')}
              style={{
                background: 'none',
                border: 'none',
                color: '#8E24AA',
                textDecoration: 'underline',
                cursor: 'pointer',
                fontSize: '0.9rem'
              }}
            >
              나중에 등록하기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AuthorRegisterPage;