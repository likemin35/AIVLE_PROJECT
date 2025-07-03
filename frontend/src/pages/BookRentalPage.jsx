import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { bookApi } from '../api/bookApi';
import { pointApi } from '../api/pointApi';

function BookRentalPage() {
  const { bookId } = useParams();
  const navigate = useNavigate();
  
  const [book, setBook] = useState(null);
  const [user, setUser] = useState(null);
  const [userPoint, setUserPoint] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [showPointModal, setShowPointModal] = useState(false);
  const [showSubscriptionModal, setShowSubscriptionModal] = useState(false);

  // 사용자 정보 및 포인트 가져오기
  useEffect(() => {
    const userData = localStorage.getItem('user');
    if (userData) {
      const parsedUser = JSON.parse(userData);
      setUser(parsedUser);
      fetchUserPoints(parsedUser.id);
    } else {
      alert('로그인이 필요합니다.');
      navigate('/');
    }
  }, [navigate]);

  // 도서 정보 가져오기
  useEffect(() => {
    if (bookId) {
      fetchBookInfo();
    }
  }, [bookId]);

  const fetchBookInfo = async () => {
    try {
      const data = await bookApi.getBook(bookId);
      setBook(data);
    } catch (error) {
      console.error('도서 정보 조회 오류:', error);
      alert('도서 정보를 불러오는데 실패했습니다.');
      navigate('/main');
    } finally {
      setIsLoading(false);
    }
  };

  const fetchUserPoints = async (userId) => {
    try {
      const pointData = await pointApi.getPoints(userId);
      setUserPoint(pointData.point || 0);
    } catch (error) {
      console.error('포인트 조회 오류:', error);
      setUserPoint(0);
    }
  };

  const handlePointRead = () => {
    if (!book) return;
    
    if (userPoint < book.price) {
      alert('보유 포인트가 부족합니다. 포인트를 충전해주세요.');
      return;
    }
    setShowPointModal(true);
  };

  const confirmPointRead = async () => {
    if (!user || !book) return;

    try {
      // 도서 대여 신청 (ApplySubscription 이벤트 발행)
      await bookApi.rentBook(user.id, book.id);
      
      setShowPointModal(false);
      alert('도서 대여 신청이 완료되었습니다! 포인트 차감 후 읽기 페이지로 이동합니다.');
      
      // 잠시 후 읽기 페이지로 이동
      setTimeout(() => {
        navigate(`/read/${bookId}`);
      }, 1000);
    } catch (error) {
      console.error('도서 대여 오류:', error);
      alert('도서 대여 신청 중 오류가 발생했습니다.');
      setShowPointModal(false);
    }
  };

  const handleSubscriptionRead = () => {
    if (!user) return;

    if (!user.isPurchase) {
      setShowSubscriptionModal(true);
      return;
    }
    
    // 구독권이 있는 경우 바로 읽기 페이지로 이동
    alert('보라패스로 도서를 시작합니다! 무제한으로 이용하세요.');
    navigate(`/read/${bookId}`);
  };

  const confirmSubscription = () => {
    setShowSubscriptionModal(false);
    navigate('/charge');
  };

  if (isLoading) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: '#ffffff', color: '#000000', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
        <Navbar />
        <div style={{ fontSize: '1.2rem', color: '#8E24AA' }}>도서 정보를 불러오는 중...</div>
      </div>
    );
  }

  if (!book) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: '#ffffff', color: '#000000', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
        <Navbar />
        <h2>도서를 찾을 수 없습니다</h2>
        <p>요청하신 도서 정보를 찾을 수 없습니다.</p>
        <button onClick={() => navigate('/main')} style={{ backgroundColor: '#8E24AA', color: '#ffffff', padding: '1rem 2rem', border: 'none', borderRadius: '12px', cursor: 'pointer' }}>
          메인으로 돌아가기
        </button>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#ffffff', color: '#000000', fontFamily: 'sans-serif' }}>
      <Navbar />
      <div style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
        <div style={{ display: 'flex', gap: '2rem', flexWrap: 'wrap' }}>
          <img src={book.image} alt={book.title} style={{ width: '250px', height: '350px', objectFit: 'cover', borderRadius: '16px', border: '1px solid #8E24AA' }} />
          <div style={{ flex: 1 }}>
            <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: '#8E24AA' }}>{book.title}</h1>
            <p style={{ margin: '0.5rem 0' }}>저자: {book.author}</p>
            <p style={{ margin: '0.5rem 0' }}>조회수: {book.views.toLocaleString()}회</p>
            <p style={{ margin: '0.5rem 0' }}>대여가: {book.price.toLocaleString()}P</p>
            {user && (
              <div style={{ 
                marginTop: '1.5rem', 
                padding: '1rem', 
                backgroundColor: '#f9f5fb', 
                borderRadius: '8px', 
                border: '1px solid #e1bee7' 
              }}>
                <p style={{ margin: '0.5rem 0', fontWeight: '600', color: '#000' }}>
                  현재 보유 포인트: {userPoint.toLocaleString()}P
                </p>
                <p style={{ margin: '0.5rem 0', fontWeight: '600', color: '#000' }}>
                  보라패스 상태: {user.isPurchase ? '구독중' : '미구독'}
                </p>
              </div>
            )}
          </div>
        </div>

        <div style={{ marginTop: '2rem' }}>
          <h2 style={{ fontSize: '1.5rem', color: '#8E24AA' }}>줄거리</h2>
          <p style={{ lineHeight: '1.6', marginTop: '1rem' }}>{book.summary}</p>
        </div>

        {user && (
          <div style={{ marginTop: '2rem' }}>
            <h2 style={{ fontSize: '1.5rem', color: '#8E24AA', marginBottom: '1.5rem' }}>읽기 옵션</h2>
            <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap' }}>
              <button 
                onClick={handleSubscriptionRead} 
                style={{ 
                  backgroundColor: '#8E24AA', 
                  color: '#ffffff', 
                  padding: '1.2rem 2rem', 
                  borderRadius: '12px', 
                  border: 'none', 
                  cursor: 'pointer', 
                  flex: 1,
                  fontSize: '1.1rem',
                  fontWeight: '600',
                  boxShadow: '0 4px 12px rgba(142, 36, 170, 0.3)',
                  transition: 'all 0.3s ease',
                  minWidth: '200px'
                }}
              >
                {user.isPurchase ? '보라패스로 읽기 (무제한)' : '보라패스로 읽기'}
                <br />
                <span style={{ fontSize: '0.9rem', opacity: 0.9 }}>
                  {user.isPurchase ? '즉시 이용 가능' : '구독 필요'}
                </span>
              </button>
              <button 
                onClick={handlePointRead} 
                style={{ 
                  backgroundColor: userPoint < book.price ? '#cccccc' : '#8E24AA', 
                  color: '#ffffff', 
                  padding: '1.2rem 2rem', 
                  borderRadius: '12px', 
                  border: 'none', 
                  cursor: userPoint < book.price ? 'not-allowed' : 'pointer', 
                  flex: 1,
                  fontSize: '1.1rem',
                  fontWeight: '600',
                  boxShadow: userPoint < book.price ? 'none' : '0 4px 12px rgba(142, 36, 170, 0.3)',
                  transition: 'all 0.3s ease',
                  minWidth: '200px'
                }}
              >
                포인트로 읽기
                <br />
                <span style={{ fontSize: '0.9rem', opacity: 0.9 }}>
                  {book.price.toLocaleString()}P (3일간 이용)
                </span>
              </button>
            </div>
          </div>
        )}

        {showPointModal && user && (
          <div style={{ 
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 1000
          }}>
            <div style={{ 
              backgroundColor: '#ffffff',
              borderRadius: '16px', 
              padding: '2rem',
              maxWidth: '400px',
              width: '90%',
              textAlign: 'center'
            }}>
              <h3 style={{ color: '#8E24AA', marginBottom: '1.5rem' }}>포인트 결제 확인</h3>
              <div style={{ textAlign: 'left', marginBottom: '2rem' }}>
                <p style={{ marginBottom: '0.5rem' }}><strong>도서:</strong> {book.title}</p>
                <p style={{ marginBottom: '0.5rem' }}><strong>저자:</strong> {book.author}</p>
                <p style={{ marginBottom: '0.5rem' }}><strong>대여 기간:</strong> 3일</p>
                <p style={{ marginBottom: '0.5rem' }}><strong>사용 포인트:</strong> {book.price.toLocaleString()}P</p>
                <p style={{ marginBottom: '0.5rem' }}><strong>잔여 포인트:</strong> {(userPoint - book.price).toLocaleString()}P</p>
              </div>
              <div style={{ display: 'flex', gap: '1rem' }}>
                <button 
                  onClick={confirmPointRead} 
                  style={{ 
                    flex: 1,
                    backgroundColor: '#8E24AA', 
                    color: '#ffffff', 
                    padding: '1rem', 
                    borderRadius: '8px', 
                    border: 'none',
                    cursor: 'pointer',
                    fontWeight: '600'
                  }}
                >
                  읽기 시작
                </button>
                <button 
                  onClick={() => setShowPointModal(false)} 
                  style={{ 
                    flex: 1,
                    padding: '1rem', 
                    borderRadius: '8px', 
                    border: '1px solid #8E24AA', 
                    backgroundColor: '#ffffff', 
                    color: '#8E24AA',
                    cursor: 'pointer',
                    fontWeight: '600'
                  }}
                >
                  취소
                </button>
              </div>
            </div>
          </div>
        )}

        {showSubscriptionModal && (
          <div style={{ 
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 1000
          }}>
            <div style={{ 
              backgroundColor: '#ffffff',
              borderRadius: '16px', 
              padding: '2rem',
              maxWidth: '400px',
              width: '90%',
              textAlign: 'center'
            }}>
              <h3 style={{ color: '#8E24AA', marginBottom: '1.5rem' }}>보라패스 구독 안내</h3>
              <p style={{ marginBottom: '1.5rem', lineHeight: '1.5' }}>
                보라패스를 구독하면 모든 도서를 무제한으로 읽을 수 있습니다.
              </p>
              <div style={{ 
                backgroundColor: '#f9f5fb',
                padding: '1rem',
                borderRadius: '8px',
                marginBottom: '2rem',
                textAlign: 'left'
              }}>
                <p style={{ margin: '0.3rem 0' }}>✓ 무제한 도서 대여</p>
                <p style={{ margin: '0.3rem 0' }}>✓ 광고 제거</p>
                <p style={{ margin: '0.3rem 0' }}>✓ 우선 예약 서비스</p>
                <p style={{ margin: '0.3rem 0', fontWeight: 'bold', color: '#8E24AA' }}>월 9,900원</p>
              </div>
              <div style={{ display: 'flex', gap: '1rem' }}>
                <button 
                  onClick={confirmSubscription} 
                  style={{ 
                    flex: 1,
                    backgroundColor: '#8E24AA', 
                    color: '#ffffff', 
                    padding: '1rem', 
                    borderRadius: '8px', 
                    border: 'none',
                    cursor: 'pointer',
                    fontWeight: '600'
                  }}
                >
                  구독하러 가기
                </button>
                <button 
                  onClick={() => setShowSubscriptionModal(false)} 
                  style={{ 
                    flex: 1,
                    padding: '1rem', 
                    borderRadius: '8px', 
                    border: '1px solid #8E24AA', 
                    backgroundColor: '#ffffff', 
                    color: '#8E24AA',
                    cursor: 'pointer',
                    fontWeight: '600'
                  }}
                >
                  나중에
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default BookRentalPage;