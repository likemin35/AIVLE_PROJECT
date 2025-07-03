import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function PaymentForm({ method, amount }) {
  const navigate = useNavigate();

  const [cardCompany, setCardCompany] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [expiryMonth, setExpiryMonth] = useState('');
  const [expiryYear, setExpiryYear] = useState('');
  const [cvc, setCvc] = useState('');

  const [bankName, setBankName] = useState('');
  const [accountNumber, setAccountNumber] = useState('');

  const [isProcessing, setIsProcessing] = useState(false);

  const handleSubmit = async () => {
    if (method === 'card') {
      if (!cardCompany || !cardNumber || !expiryMonth || !expiryYear || !cvc) {
        alert('모든 카드 정보를 입력해주세요.');
        return;
      }
    } else if (method === 'bank') {
      if (!bankName || !accountNumber) {
        alert('모든 계좌 정보를 입력해주세요.');
        return;
      }
    }

    setIsProcessing(true);

    setTimeout(() => {
      setIsProcessing(false);

      if (amount.type === 'point') {
        alert(`${amount.point}P 충전이 완료되었습니다!`);
      } else if (amount.type === 'subscription') {
        alert(`${amount.name} 구독이 완료되었습니다!`);
      }

      navigate('/main');
    }, 2500);
  };

  const isSubscription = amount.type === 'subscription';

  return (
    <div style={styles.container}>
      <div style={styles.summaryBox}>
        <div style={styles.summaryHeader}>
          <h4 style={styles.summaryTitle}>결제 정보</h4>
        </div>
        <div style={styles.summaryContent}>
          {isSubscription ? (
            <>
              <div style={styles.summaryItem}>
                <span style={styles.summaryLabel}>상품명</span>
                <span style={styles.summaryValue}>{amount.name}</span>
              </div>
              <div style={styles.summaryItem}>
                <span style={styles.summaryLabel}>이용기간</span>
                <span style={styles.summaryValue}>1개월</span>
              </div>
              <div style={styles.benefitHighlight}>프리미엄 무제한 이용</div>
            </>
          ) : (
            <>
              <div style={styles.summaryItem}>
                <span style={styles.summaryLabel}>충전 포인트</span>
                <span style={styles.summaryValue}>{amount.point?.toLocaleString()}P</span>
              </div>
              <div style={styles.benefitHighlight}>즉시 사용 가능</div>
            </>
          )}
          <div style={styles.summaryTotal}>
            <span style={styles.totalLabel}>총 결제금액</span>
            <span style={styles.totalAmount}>{amount.price?.toLocaleString()}원</span>
          </div>
        </div>
      </div>

      <div style={styles.formContainer}>
        <div style={styles.formHeader}>
          <h4 style={styles.formTitle}>
            {method === 'card' ? '카드 정보 입력' : '계좌 정보 입력'}
          </h4>
        </div>

        <div style={styles.formContent}>
          {method === 'card' ? (
            <>
              <div style={styles.inputGroup}>
                <label style={styles.label}>카드사</label>
                <select
                  value={cardCompany}
                  onChange={(e) => setCardCompany(e.target.value)}
                  style={styles.select}
                >
                  <option value="">카드사를 선택하세요</option>
                  <option value="삼성카드">삼성카드</option>
                  <option value="국민카드">국민카드</option>
                  <option value="롯데카드">롯데카드</option>
                  <option value="현대카드">현대카드</option>
                  <option value="신한카드">신한카드</option>
                  <option value="우리카드">우리카드</option>
                </select>
              </div>

              <div style={styles.inputGroup}>
                <label style={styles.label}>카드번호</label>
                <input
                  type="text"
                  placeholder="1234-5678-1234-5678"
                  value={cardNumber}
                  onChange={(e) => setCardNumber(e.target.value)}
                  style={styles.input}
                  maxLength="19"
                />
              </div>

              <div style={styles.row}>
                <div style={styles.inputGroup}>
                  <label style={styles.label}>유효기간</label>
                  <div style={styles.expiryRow}>
                    <select
                      value={expiryMonth}
                      onChange={(e) => setExpiryMonth(e.target.value)}
                      style={{ ...styles.select, flex: 1 }}
                    >
                      <option value="">월</option>
                      {[...Array(12)].map((_, i) => (
                        <option key={i} value={String(i + 1).padStart(2, '0')}>
                          {String(i + 1).padStart(2, '0')}월
                        </option>
                      ))}
                    </select>
                    <select
                      value={expiryYear}
                      onChange={(e) => setExpiryYear(e.target.value)}
                      style={{ ...styles.select, flex: 1 }}
                    >
                      <option value="">년</option>
                      {[...Array(10)].map((_, i) => {
                        const year = new Date().getFullYear() + i;
                        return (
                          <option key={year} value={year}>
                            {year}년
                          </option>
                        );
                      })}
                    </select>
                  </div>
                </div>

                <div style={styles.inputGroup}>
                  <label style={styles.label}>CVC</label>
                  <input
                    type="text"
                    placeholder="123"
                    value={cvc}
                    onChange={(e) => setCvc(e.target.value)}
                    style={styles.input}
                    maxLength="4"
                  />
                </div>
              </div>
            </>
          ) : (
            <>
              <div style={styles.inputGroup}>
                <label style={styles.label}>은행명</label>
                <select
                  value={bankName}
                  onChange={(e) => setBankName(e.target.value)}
                  style={styles.select}
                >
                  <option value="">은행을 선택하세요</option>
                  <option value="국민은행">국민은행</option>
                  <option value="농협은행">농협은행</option>
                  <option value="기업은행">기업은행</option>
                  <option value="하나은행">하나은행</option>
                  <option value="신한은행">신한은행</option>
                  <option value="우리은행">우리은행</option>
                  <option value="SC제일은행">SC제일은행</option>
                  <option value="카카오뱅크">카카오뱅크</option>
                  <option value="토스뱅크">토스뱅크</option>
                </select>
              </div>

              <div style={styles.inputGroup}>
                <label style={styles.label}>계좌번호</label>
                <input
                  type="text"
                  placeholder="계좌번호를 입력하세요"
                  value={accountNumber}
                  onChange={(e) => setAccountNumber(e.target.value)}
                  style={styles.input}
                />
              </div>
            </>
          )}

          <div style={styles.securityNotice}>
            모든 결제 정보는 암호화되어 안전하게 처리됩니다
          </div>

          <button
            onClick={handleSubmit}
            disabled={isProcessing}
            style={{
              ...styles.submitButton,
              ...(isProcessing ? styles.processingButton : {})
            }}
          >
            {isProcessing ? '결제 처리 중...' : `${amount.price?.toLocaleString()}원 결제하기`}
          </button>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: '600px',
    margin: '0 auto',
    padding: '2rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '2rem'
  },
  summaryBox: {
    background: '#f8f8f8',
    borderRadius: '12px',
    boxShadow: '0 4px 10px rgba(0,0,0,0.05)'
  },
  summaryHeader: {
    padding: '1rem 1.5rem',
    borderBottom: '1px solid #ddd'
  },
  summaryTitle: {
    margin: 0,
    fontSize: '1.3rem',
    fontWeight: 'bold',
    color: '#000'
  },
  summaryContent: {
    padding: '1.5rem'
  },
  summaryItem: {
    display: 'flex',
    justifyContent: 'space-between',
    marginBottom: '1rem'
  },
  summaryLabel: {
    color: '#333'
  },
  summaryValue: {
    fontWeight: 'bold',
    color: '#000'
  },
  benefitHighlight: {
    backgroundColor: '#fffbe6',
    color: '#b58d00',
    padding: '1rem',
    borderRadius: '8px',
    fontWeight: '600',
    marginBottom: '1rem'
  },
  summaryTotal: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '1rem',
    borderTop: '1px solid #ddd',
    paddingTop: '1rem',
    fontWeight: 'bold'
  },
  totalLabel: {
    color: '#000'
  },
  totalAmount: {
    color: '#8E24AA',
    fontSize: '1.3rem'
  },
  formContainer: {
    background: '#f8f8f8',
    borderRadius: '12px',
    boxShadow: '0 4px 10px rgba(0,0,0,0.05)'
  },
  formHeader: {
    padding: '1rem 1.5rem',
    borderBottom: '1px solid #ddd'
  },
  formTitle: {
    margin: 0,
    fontSize: '1.3rem',
    fontWeight: 'bold',
    color: '#000'
  },
  formContent: {
    padding: '1.5rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem'
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem'
  },
  label: {
    fontWeight: 'bold',
    color: '#000'
  },
  input: {
    padding: '0.8rem',
    border: '1px solid #ccc',
    borderRadius: '6px',
    fontSize: '1rem'
  },
  select: {
    padding: '0.8rem',
    border: '1px solid #ccc',
    borderRadius: '6px',
    fontSize: '1rem'
  },
  row: {
    display: 'flex',
    gap: '1rem'
  },
  expiryRow: {
    display: 'flex',
    gap: '0.5rem'
  },
  securityNotice: {
    fontSize: '0.9rem',
    color: '#2e7d32',
    backgroundColor: '#e8f5e9',
    padding: '0.8rem',
    borderRadius: '8px'
  },
  submitButton: {
    backgroundColor: '#8E24AA',
    color: '#fff',
    padding: '1rem',
    fontSize: '1.1rem',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontWeight: 'bold'
  },
  processingButton: {
    backgroundColor: '#ccc',
    cursor: 'not-allowed'
  }
};

export default PaymentForm;
