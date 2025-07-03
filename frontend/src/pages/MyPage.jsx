import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';

function MyPage() {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState({
    id: 'user123',
    email: 'user@example.com',
    name: '조하민',
    isAuthor: false,
    hasSubscription: true,
    subscriptionEndDate: '2025-02-15',
    authorInfo: null
  });

  const [showWritingMenu, setShowWritingMenu] = useState(false);

  const getSubscriptionStatus = () => {
    if (userInfo.hasSubscription) {
      const endDate = new Date(userInfo.subscriptionEndDate);
      const today = new Date();
      const daysLeft = Math.ceil((endDate - today) / (1000 * 60 * 60 * 24));

      return {
        status: '구독중',
        detail: `${userInfo.subscriptionEndDate}까지 (${daysLeft}일 남음)`
      };
    } else {
      return {
        status: '미구독',
        detail: '보라패스를 구독하고 무제한으로 읽어보세요'
      };
    }
  };

  const subscriptionInfo = getSubscriptionStatus();

  const handleAuthorRegistration = () => {
    setUserInfo(prev => ({
      ...prev,
      isAuthor: true,
      authorInfo: {
        registrationDate: new Date().toLocaleDateString(),
        status: 'active',
        publishedBooks: 0
      }
    }));
    alert('작가 등록이 완료되었습니다! 이제 원고를 작성하고 출판할 수 있습니다.');
  };

  const cardStyle = {
    background: '#ffffff',
    border: '1px solid #e0e0e0',
    borderRadius: '16px',
    padding: '1.5rem',
    marginBottom: '2rem',
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.05)'
  };

  const titleStyle = {
    fontSize: '1.2rem',
    fontWeight: 'bold',
    marginBottom: '1rem',
    color: '#8E24AA'
  };

  const labelStyle = {
    fontWeight: '600',
    marginRight: '0.5rem',
    color: '#000000'
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9f9f9', color: '#000000', fontFamily: 'sans-serif' }}>
      <Navbar />
      <div style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
        <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: '#8E24AA', marginBottom: '2rem' }}>마이페이지</h1>

        {/* 기본 정보 */}
        <div style={cardStyle}>
          <div style={titleStyle}>기본 정보</div>
          <p><span style={labelStyle}>아이디:</span>{userInfo.id}</p>
          <p><span style={labelStyle}>이메일:</span>{userInfo.email}</p>
          <p><span style={labelStyle}>이름:</span>{userInfo.name}</p>
          <p><span style={labelStyle}>작가 등록:</span>{userInfo.isAuthor ? '등록완료' : '미등록'}</p>
          {userInfo.isAuthor && userInfo.authorInfo && (
            <>
              <p><span style={labelStyle}>등록일:</span>{userInfo.authorInfo.registrationDate}</p>
              <p><span style={labelStyle}>출간 도서:</span>{userInfo.authorInfo.publishedBooks}권</p>
            </>
          )}
        </div>

        {/* 구독 정보 */}
        <div style={cardStyle}>
          <div style={titleStyle}>보라패스</div>
          <p><span style={labelStyle}>상태:</span>{subscriptionInfo.status}</p>
          <p><span style={labelStyle}>정보:</span>{subscriptionInfo.detail}</p>
          {!userInfo.hasSubscription && (
            <button onClick={() => navigate('/charge')} style={{ marginTop: '1rem', backgroundColor: '#8E24AA', color: '#ffffff', padding: '0.6rem 1.2rem', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>구독하기</button>
          )}
        </div>

        {/* 포인트 정보 */}
        <div style={cardStyle}>
          <div style={titleStyle}>포인트</div>
          <p><span style={labelStyle}>보유 포인트:</span>12,500P</p>
          <button onClick={() => navigate('/charge')} style={{ marginTop: '1rem', backgroundColor: '#8E24AA', color: '#ffffff', padding: '0.6rem 1.2rem', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>충전하기</button>
        </div>

        {/* 작가 활동 */}
        <div style={cardStyle}>
          <div style={titleStyle}>작가 활동</div>
          {userInfo.isAuthor ? (
            <>
              <button onClick={() => navigate('/manuscripts/create')} style={{ backgroundColor: '#8E24AA', color: '#ffffff', padding: '0.6rem 1.2rem', border: 'none', borderRadius: '8px', cursor: 'pointer', marginRight: '0.5rem' }}>새 원고 작성</button>
              <button onClick={() => setShowWritingMenu(!showWritingMenu)} style={{ backgroundColor: '#8E24AA', color: '#ffffff', padding: '0.6rem 1.2rem', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>내 원고 관리</button>
              {showWritingMenu && (
                <div style={{ marginTop: '1rem' }}>
                  <button onClick={() => navigate('/manuscripts/edit/1')} style={{ marginRight: '1rem' }}>원고 수정</button>
                  <button onClick={() => alert('원고 목록 페이지로 이동합니다.')} style={{ marginRight: '1rem' }}>원고 목록</button>
                  <button onClick={() => alert('출간 현황 페이지로 이동합니다.')}>출간 현황</button>
                </div>
              )}
            </>
          ) : (
            <>
              <p style={{ marginBottom: '1rem' }}>작가 등록 후 창작 활동을 시작해보세요.</p>
              <button onClick={handleAuthorRegistration} style={{ backgroundColor: '#8E24AA', color: '#ffffff', padding: '0.6rem 1.2rem', border: 'none', borderRadius: '8px', cursor: 'pointer' }}>작가 등록하기</button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default MyPage;
