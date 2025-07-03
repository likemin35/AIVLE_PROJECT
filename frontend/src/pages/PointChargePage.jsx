import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { pointApi } from '../api/pointApi';

function PointChargePage() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('point');
  const [selectedAmount, setSelectedAmount] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState(null);
  const [currentPoints, setCurrentPoints] = useState(0);
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);

  const pointOptions = [
    { point: 5000, price: 5000, bonus: 0 },
    { point: 10000, price: 10000, bonus: 0 },
    { point: 22000, price: 20000, bonus: 2000 },
    { point: 55000, price: 50000, bonus: 5000 }
  ];

  const subscriptionOption = {
    name: '보라패스',
    duration: '1개월',
    price: 9900,
    benefits: ['무제한 도서 대여', '광고 제거', '우선 예약 서비스']
  };

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

  const fetchUserPoints = async (userId) => {
    try {
      const pointData = await pointApi.getPoints(userId);
      setCurrentPoints(pointData.point || 0);
    } catch (error) {
      console.error('포인트 조회 오류:', error);
      setCurrentPoints(0);
    } finally {
      setIsLoading(false);
    }
  };

  const handleItemSelect = (item) => {
    setSelectedAmount(item);
    setPaymentMethod(null);
  };

  const resetSelection = () => {
    setSelectedAmount(null);
    setPaymentMethod(null);
  };

  const handlePayment = async () => {
    if (!selectedAmount || !paymentMethod || !user) return;

    setIsProcessing(true);

    try {
      if (selectedAmount.type === 'point') {
        // 포인트 충전
        await pointApi.chargePoints(user.id, selectedAmount.point);
        alert(`${selectedAmount.point.toLocaleString()}P 충전이 완료되었습니다!`);
        
        // 포인트 정보 새로고침
        await fetchUserPoints(user.id);
      } else if (selectedAmount.type === 'subscription') {
        // 구독권 구매 (임시로 alert만 표시)
        alert(`${selectedAmount.name} 구독이 완료되었습니다!`);
        
        // 사용자 구독 상태 업데이트 (로컬 스토리지)
        const updatedUser = { ...user, isPurchase: true };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        setUser(updatedUser);
      }

      // 결제 완료 후 상태 초기화
      resetSelection();
      
      setTimeout(() => {
        navigate('/main');
      }, 1000);
    } catch (error) {
      console.error('결제 오류:', error);
      alert('결제 중 오류가 발생했습니다: ' + (error.message || '알 수 없는 오류'));
    } finally {
      setIsProcessing(false);
    }
  };

  if (isLoading) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: '#f9f9f9', color: '#000000', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
        <Navbar />
        <div style={{ fontSize: '1.2rem', color: '#8E24AA' }}>포인트 정보를 불러오는 중...</div>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9f9f9', color: '#000000', fontFamily: 'sans-serif' }}>
      <Navbar />
      <div style={{ maxWidth: '900px', margin: '0 auto', padding: '2rem' }}>
        <h1 style={{ color: '#8E24AA', fontSize: '2rem', fontWeight: 'bold', marginBottom: '2rem' }}>포인트 충전 & 구독</h1>

        <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
          <button onClick={() => { setActiveTab('point'); resetSelection(); }}
            style={{ flex: 1, padding: '1rem', borderRadius: '8px', backgroundColor: activeTab === 'point' ? '#8E24AA' : '#eeeeee', color: activeTab === 'point' ? '#ffffff' : '#333', border: 'none', fontWeight: 'bold' }}>포인트 충전</button>
          <button onClick={() => { setActiveTab('subscription'); resetSelection(); }}
            style={{ flex: 1, padding: '1rem', borderRadius: '8px', backgroundColor: activeTab === 'subscription' ? '#8E24AA' : '#eeeeee', color: activeTab === 'subscription' ? '#ffffff' : '#333', border: 'none', fontWeight: 'bold' }}>구독권 구매</button>
        </div>

        <div style={{ backgroundColor: '#ffffff', borderRadius: '16px', padding: '2rem', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', marginBottom: '2rem' }}>
          <h2 style={{ fontSize: '1.5rem', color: '#8E24AA', marginBottom: '1rem' }}>보유 포인트</h2>
          <p style={{ fontSize: '2rem', fontWeight: 'bold' }}>{currentPoints.toLocaleString()} P</p>
        </div>

        {activeTab === 'point' && (
          <div style={{ marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>포인트 상품 선택</h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
              {pointOptions.map(opt => (
                <div key={opt.point} onClick={() => handleItemSelect({ point: opt.point, price: opt.price, type: 'point' })}
                  style={{ border: selectedAmount?.point === opt.point ? '2px solid #8E24AA' : '1px solid #ccc', borderRadius: '12px', padding: '1rem', cursor: 'pointer', backgroundColor: selectedAmount?.point === opt.point ? '#f3e5f5' : '#ffffff' }}>
                  <p style={{ fontWeight: 'bold', fontSize: '1.2rem', color: '#8E24AA' }}>{opt.point.toLocaleString()}P</p>
                  <p>{opt.price.toLocaleString()}원</p>
                  {opt.bonus > 0 && <p style={{ color: '#8E24AA', fontSize: '0.9rem' }}>+{opt.bonus.toLocaleString()}P 보너스</p>}
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'subscription' && (
          <div style={{ marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>구독권 선택</h3>
            <div onClick={() => handleItemSelect({ name: subscriptionOption.name, price: subscriptionOption.price, type: 'subscription' })}
              style={{ border: selectedAmount?.type === 'subscription' ? '2px solid #8E24AA' : '1px solid #ccc', borderRadius: '12px', padding: '1rem', backgroundColor: selectedAmount?.type === 'subscription' ? '#f3e5f5' : '#ffffff', cursor: 'pointer' }}>
              <h4 style={{ color: '#8E24AA', fontSize: '1.2rem', marginBottom: '0.5rem' }}>{subscriptionOption.name}</h4>
              <p>{subscriptionOption.duration} / {subscriptionOption.price.toLocaleString()}원</p>
              <ul>
                {subscriptionOption.benefits.map((b, i) => <li key={i}>{b}</li>)}
              </ul>
              {user?.isPurchase && (
                <p style={{ color: '#28a745', fontWeight: 'bold', marginTop: '0.5rem' }}>✓ 현재 구독중</p>
              )}
            </div>
          </div>
        )}

        {selectedAmount && (
          <div style={{ backgroundColor: '#ffffff', borderRadius: '16px', padding: '2rem', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>결제 수단</h3>
            <div style={{ display: 'flex', gap: '1rem' }}>
              <button onClick={() => setPaymentMethod('card')} style={{ flex: 1, padding: '1rem', borderRadius: '8px', backgroundColor: paymentMethod === 'card' ? '#8E24AA' : '#eeeeee', color: paymentMethod === 'card' ? '#ffffff' : '#333', border: 'none', fontWeight: 'bold' }}>카드 결제</button>
              <button onClick={() => setPaymentMethod('bank')} style={{ flex: 1, padding: '1rem', borderRadius: '8px', backgroundColor: paymentMethod === 'bank' ? '#8E24AA' : '#eeeeee', color: paymentMethod === 'bank' ? '#ffffff' : '#333', border: 'none', fontWeight: 'bold' }}>계좌 이체</button>
            </div>
          </div>
        )}

        {selectedAmount && paymentMethod && (
          <div style={{ backgroundColor: '#ffffff', borderRadius: '16px', padding: '2rem', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', marginBottom: '2rem' }}>
            <h3 style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>결제 정보</h3>
            
            <div style={{ marginBottom: '2rem', padding: '1rem', backgroundColor: '#f9f5fb', borderRadius: '8px' }}>
              {selectedAmount.type === 'point' ? (
                <>
                  <p><strong>충전 포인트:</strong> {selectedAmount.point.toLocaleString()}P</p>
                  <p><strong>결제 금액:</strong> {selectedAmount.price.toLocaleString()}원</p>
                </>
              ) : (
                <>
                  <p><strong>상품명:</strong> {selectedAmount.name}</p>
                  <p><strong>이용기간:</strong> 1개월</p>
                  <p><strong>결제 금액:</strong> {selectedAmount.price.toLocaleString()}원</p>
                </>
              )}
            </div>

            <button
              onClick={handlePayment}
              disabled={isProcessing}
              style={{
                width: '100%',
                padding: '1.2rem',
                backgroundColor: isProcessing ? '#cccccc' : '#8E24AA',
                color: '#ffffff',
                border: 'none',
                borderRadius: '8px',
                fontSize: '1.1rem',
                fontWeight: 'bold',
                cursor: isProcessing ? 'not-allowed' : 'pointer',
                transition: 'all 0.3s ease'
              }}
            >
              {isProcessing ? '결제 처리 중...' : `${selectedAmount.price.toLocaleString()}원 결제하기`}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default PointChargePage;