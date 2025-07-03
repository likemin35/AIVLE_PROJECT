import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/authApi';

function SignupPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    carrier: 'KT',
    isKT: true,
  });

  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === 'carrier') {
      setForm({
        ...form,
        carrier: value,
        isKT: value === 'KT',
      });
    } else {
      setForm({
        ...form,
        [name]: value,
      });
    }
  };

  const validateForm = () => {
    if (!form.username || !form.email || !form.phone || !form.password) {
      alert('모든 필드를 입력해주세요.');
      return false;
    }

    if (form.password !== form.confirmPassword) {
      alert('비밀번호가 일치하지 않습니다.');
      return false;
    }

    if (form.password.length < 6) {
      alert('비밀번호는 6자리 이상이어야 합니다.');
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(form.email)) {
      alert('올바른 이메일 형식을 입력해주세요.');
      return false;
    }

    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) return;

    setIsLoading(true);
    
    try {
      const response = await authApi.signup(form);
      
      if (response.success) {
        alert('회원가입이 완료되었습니다! 환영합니다!');
        navigate('/');
      } else {
        alert(response.message || '회원가입에 실패했습니다.');
      }
    } catch (error) {
      console.error('Signup error:', error);
      alert(error.message || '회원가입 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.signupCard}>
        <div style={styles.header}>
          <div style={styles.logoSection}>
            <h2 style={styles.brandName}>KT 걷다가서재</h2>
          </div>
          <h1 style={styles.title}>회원가입</h1>
          <p style={styles.subtitle}>새로운 독서 여행을 시작하세요</p>
        </div>

        <div style={styles.formSection}>
          <div style={styles.inputGroup}>
            <label style={styles.label}>이름</label>
            <div style={styles.inputWrapper}>
              <input
                type="text"
                name="username"
                value={form.username}
                onChange={handleChange}
                placeholder="이름을 입력하세요"
                style={styles.input}
                disabled={isLoading}
              />
            </div>
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>이메일</label>
            <div style={styles.inputWrapper}>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="example@email.com"
                style={styles.input}
                disabled={isLoading}
              />
            </div>
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>전화번호 및 통신사</label>
            <div style={styles.phoneSection}>
              <div style={styles.phoneWrapper}>
                <input
                  type="text"
                  name="phone"
                  value={form.phone}
                  onChange={handleChange}
                  placeholder="010-1234-5678"
                  style={{ ...styles.input, flex: 1 }}
                  disabled={isLoading}
                />
              </div>
              <select
                name="carrier"
                value={form.carrier}
                onChange={handleChange}
                style={styles.carrierSelect}
                disabled={isLoading}
              >
                <option value="KT">KT</option>
                <option value="SKT">SKT</option>
                <option value="LG U+">LG U+</option>
                <option value="알뜰폰">알뜰폰</option>
              </select>
            </div>
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>비밀번호</label>
            <div style={styles.inputWrapper}>
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="비밀번호를 입력하세요 (6자리 이상)"
                style={styles.input}
                disabled={isLoading}
              />
            </div>
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>비밀번호 확인</label>
            <div style={styles.inputWrapper}>
              <input
                type="password"
                name="confirmPassword"
                value={form.confirmPassword}
                onChange={handleChange}
                placeholder="비밀번호를 다시 입력하세요"
                style={styles.input}
                disabled={isLoading}
              />
            </div>
          </div>

          {form.isKT && (
            <div style={styles.ktBenefitCard}>
              <div style={styles.benefitHeader}>
                <h4 style={styles.benefitTitle}>KT 고객 특별 혜택</h4>
              </div>
              <div style={styles.benefitList}>
                <div style={styles.benefitItem}>
                  <span style={styles.checkIcon}>✓</span>
                  신규 가입 포인트 6,000P 지급 (일반 1,000P + KT 보너스 5,000P)
                </div>
                <div style={styles.benefitItem}>
                  <span style={styles.checkIcon}>✓</span>
                  KT 전용 도서 콘텐츠 이용
                </div>
              </div>
            </div>
          )}

          <button 
            onClick={handleSubmit} 
            disabled={isLoading}
            style={{
              ...styles.submitButton,
              ...(isLoading ? styles.loadingButton : {})
            }}
          >
            {isLoading ? "가입 중..." : "회원가입"}
          </button>

          <div style={styles.loginSection}>
            <span style={styles.loginText}>이미 계정이 있으신가요?</span>
            <Link to="/" style={styles.loginLink}>
              로그인
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    background: '#ffffff',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontFamily: 'sans-serif',
    padding: '1rem'
  },
  signupCard: {
    background: '#ffffff',
    border: '1px solid #e0e0e0',
    borderRadius: '16px',
    padding: '2.5rem',
    boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)',
    color: '#333333',
    maxWidth: '500px',
    width: '100%'
  },
  header: {
    textAlign: 'center',
    marginBottom: '2rem'
  },
  logoSection: {
    marginBottom: '1rem'
  },
  brandName: {
    fontSize: '1.8rem',
    fontWeight: 'bold',
    margin: '0',
    color: '#8E24AA'
  },
  title: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    margin: '0 0 0.5rem 0',
    color: '#333333'
  },
  subtitle: {
    fontSize: '1rem',
    color: '#666666',
    margin: 0
  },
  formSection: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1.5rem'
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem'
  },
  label: {
    fontSize: '0.9rem',
    fontWeight: '600',
    color: '#333333'
  },
  inputWrapper: {
    position: 'relative'
  },
  input: {
    width: '100%',
    padding: '1rem 1.2rem',
    borderRadius: '8px',
    border: '1px solid #e0e0e0',
    background: '#ffffff',
    color: '#333333',
    fontSize: '1rem',
    outline: 'none',
    transition: 'all 0.3s ease',
    boxSizing: 'border-box'
  },
  phoneSection: {
    display: 'flex',
    gap: '0.5rem'
  },
  phoneWrapper: {
    flex: 2,
    display: 'flex'
  },
  carrierSelect: {
    flex: 1,
    padding: '1rem 1.2rem',
    borderRadius: '8px',
    border: '1px solid #e0e0e0',
    background: '#ffffff',
    color: '#333333',
    fontSize: '1rem',
    outline: 'none'
  },
  ktBenefitCard: {
    background: '#f0f8ff',
    border: '1px solid #8E24AA',
    borderRadius: '12px',
    padding: '1.5rem',
    marginTop: '1rem'
  },
  benefitHeader: {
    marginBottom: '1rem'
  },
  benefitTitle: {
    fontSize: '1.1rem',
    fontWeight: 'bold',
    color: '#8E24AA',
    margin: 0
  },
  benefitList: {
    display: 'flex',
    flexDirection: 'column',
    gap: '0.8rem'
  },
  benefitItem: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    fontSize: '0.9rem',
    color: '#333333'
  },
  checkIcon: {
    color: '#8E24AA',
    fontWeight: 'bold',
    fontSize: '1rem'
  },
  submitButton: {
    width: '100%',
    padding: '1.2rem',
    borderRadius: '8px',
    border: 'none',
    background: '#8E24AA',
    color: '#ffffff',
    fontSize: '1.1rem',
    fontWeight: 'bold',
    cursor: 'pointer',
    transition: 'all 0.3s ease',
    boxShadow: '0 4px 8px rgba(142, 36, 170, 0.3)'
  },
  loadingButton: {
    opacity: 0.7,
    cursor: 'not-allowed'
  },
  loginSection: {
    textAlign: 'center',
    marginTop: '1rem'
  },
  loginText: {
    color: '#666666',
    fontSize: '0.9rem'
  },
  loginLink: {
    color: '#8E24AA',
    textDecoration: 'none',
    fontWeight: 'bold',
    marginLeft: '0.5rem',
    fontSize: '0.9rem'
  }
};

export default SignupPage;