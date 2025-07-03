import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/authApi';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
    if (!email || !password) {
      alert('이메일과 비밀번호를 입력해주세요.');
      return;
    }

    setIsLoading(true);
    try {
      const response = await authApi.login(email, password);
      
      if (response.success) {
        // 로그인 성공 시 사용자 정보 저장
        localStorage.setItem('user', JSON.stringify(response.user));
        alert('로그인에 성공했습니다!');
        navigate('/main');
      } else {
        alert(response.message || '로그인에 실패했습니다.');
      }
    } catch (error) {
      console.error('Login error:', error);
      alert(error.message || '로그인 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.loginCard}>
        <div style={styles.logoSection}>
          <h2 style={styles.brandName}>KT 걷다가서재</h2>
          <p style={styles.tagline}>당신의 디지털 서재</p>
        </div>

        <div style={styles.formSection}>
          <div style={styles.inputGroup}>
            <label style={styles.label}>이메일</label>
            <div style={styles.inputWrapper}>
              <input
                type="email"
                placeholder="이메일을 입력하세요"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                style={styles.input}
                disabled={isLoading}
              />
            </div>
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>비밀번호</label>
            <div style={styles.passwordWrapper}>
              <input
                type={showPassword ? "text" : "password"}
                placeholder="비밀번호를 입력하세요"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={styles.input}
                disabled={isLoading}
                onKeyPress={(e) => e.key === 'Enter' && handleLogin()}
              />
              <span
                style={styles.eyeIcon}
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? '숨기기' : '보기'}
              </span>
            </div>
          </div>

          <button 
            style={{
              ...styles.loginButton,
              opacity: isLoading ? 0.7 : 1,
              cursor: isLoading ? 'not-allowed' : 'pointer'
            }} 
            onClick={handleLogin}
            disabled={isLoading}
          >
            <span style={styles.buttonText}>
              {isLoading ? '로그인 중...' : '로그인'}
            </span>
          </button>

          <div style={styles.signupSection}>
            <span style={styles.signupText}>계정이 없으신가요?</span>
            <Link to="/signup" style={styles.signupLink}>
              회원가입
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
  loginCard: {
    background: '#ffffff',
    border: '1px solid #e0e0e0',
    borderRadius: '16px',
    padding: '2.5rem',
    boxShadow: '0 4px 20px rgba(0, 0, 0, 0.1)',
    color: '#333333',
    maxWidth: '450px',
    width: '100%'
  },
  logoSection: {
    textAlign: 'center',
    marginBottom: '2rem'
  },
  brandName: {
    fontSize: '1.8rem',
    fontWeight: 'bold',
    margin: '0 0 0.5rem 0',
    color: '#8E24AA'
  },
  tagline: {
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
  passwordWrapper: {
    position: 'relative'
  },
  eyeIcon: {
    position: 'absolute',
    right: '1rem',
    top: '50%',
    transform: 'translateY(-50%)',
    cursor: 'pointer',
    fontSize: '0.8rem',
    color: '#8E24AA',
    opacity: 0.7
  },
  loginButton: {
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
  buttonText: {
    display: 'block',
    transform: 'translateY(0)',
    transition: 'transform 0.2s ease'
  },
  signupSection: {
    textAlign: 'center',
    marginTop: '1rem'
  },
  signupText: {
    color: '#666666',
    fontSize: '0.9rem'
  },
  signupLink: {
    color: '#8E24AA',
    textDecoration: 'none',
    fontWeight: 'bold',
    marginLeft: '0.5rem',
    fontSize: '0.9rem'
  }
};

export default LoginPage;