import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { pointApi } from '../api/pointApi';

function Navbar() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [userPoints, setUserPoints] = useState(0);
  const [showUserMenu, setShowUserMenu] = useState(false);

  // 사용자 정보 가져오기
  useEffect(() => {
    const userData = localStorage.getItem('user');
    if (userData) {
      const parsedUser = JSON.parse(userData);
      setUser(parsedUser);
      fetchUserPoints(parsedUser.id);
    }
  }, []);

  const fetchUserPoints = async (userId) => {
    try {
      const pointData = await pointApi.getPoints(userId);
      setUserPoints(pointData.point || 0);
    } catch (error) {
      console.error('포인트 조회 오류:', error);
      setUserPoints(0);
    }
  };

  const handleLogout = () => {
    authApi.logout();
    setUser(null);
    setUserPoints(0);
    setShowUserMenu(false);
    navigate('/');
  };

  const handleLogoClick = () => {
    if (user) {
      navigate('/main');
    } else {
      navigate('/');
    }
  };

  return (
    <div style={styles.navbar}>
      <div style={styles.leftSection}>
        {user && (
          <button onClick={() => navigate('/charge')} style={styles.navButton}>
            충전
          </button>
        )}
      </div>

      <div style={styles.logoContainer} onClick={handleLogoClick}>
        <div style={styles.logoText}>KT 걷다가서재</div>
      </div>

      <div style={styles.rightSection}>
        {user ? (
          <div style={styles.userSection}>
            <div style={styles.pointDisplay}>
              {userPoints.toLocaleString()}P
            </div>
            <div 
              style={styles.userProfile}
              onClick={() => setShowUserMenu(!showUserMenu)}
            >
              <div style={styles.userAvatar}>
                {user.userName.charAt(0)}
              </div>
              <span style={styles.userName}>{user.userName}</span>
              <span style={styles.dropdownArrow}>▼</span>
            </div>
            
            {showUserMenu && (
              <div style={styles.userMenu}>
                <div 
                  style={styles.menuItem}
                  onClick={() => {
                    setShowUserMenu(false);
                    navigate('/mypage');
                  }}
                >
                  마이페이지
                </div>
                <div 
                  style={styles.menuItem}
                  onClick={() => {
                    setShowUserMenu(false);
                    navigate('/register');
                  }}
                >
                  작가 등록
                </div>
                <div style={styles.menuDivider}></div>
                <div 
                  style={{...styles.menuItem, color: '#dc3545'}}
                  onClick={handleLogout}
                >
                  로그아웃
                </div>
              </div>
            )}
          </div>
        ) : (
          <button onClick={() => navigate('/')} style={styles.navButton}>
            로그인
          </button>
        )}
      </div>

      {/* 메뉴가 열려있을 때 배경 클릭으로 닫기 */}
      {showUserMenu && (
        <div 
          style={styles.menuOverlay}
          onClick={() => setShowUserMenu(false)}
        />
      )}
    </div>
  );
}

const styles = {
  navbar: {
    position: 'sticky',
    top: 0,
    zIndex: 1000,
    background: '#ffffff',
    borderBottom: '1px solid #e1bee7',
    padding: '0.5rem 2rem',
    height: '72px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.05)',
    fontFamily: 'sans-serif'
  },
  leftSection: {
    display: 'flex',
    alignItems: 'center',
    minWidth: '120px'
  },
  rightSection: {
    display: 'flex',
    alignItems: 'center',
    minWidth: '120px',
    justifyContent: 'flex-end',
    position: 'relative'
  },
  navButton: {
    padding: '0.8rem 1.2rem',
    background: '#f9f5fb',
    border: '1px solid #e1bee7',
    borderRadius: '8px',
    cursor: 'pointer',
    color: '#8E24AA',
    fontSize: '0.9rem',
    fontWeight: '600',
    transition: 'all 0.2s ease'
  },
  logoContainer: {
    flex: 1,
    height: '100%',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    cursor: 'pointer'
  },
  logoText: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: '#8E24AA'
  },
  userSection: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    position: 'relative'
  },
  pointDisplay: {
    background: '#f9f5fb',
    color: '#8E24AA',
    padding: '0.5rem 1rem',
    borderRadius: '20px',
    fontSize: '0.9rem',
    fontWeight: 'bold',
    border: '1px solid #e1bee7'
  },
  userProfile: {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    cursor: 'pointer',
    padding: '0.5rem',
    borderRadius: '8px',
    transition: 'background-color 0.2s ease'
  },
  userAvatar: {
    width: '36px',
    height: '36px',
    borderRadius: '50%',
    background: '#8E24AA',
    color: '#ffffff',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '1rem',
    fontWeight: 'bold'
  },
  userName: {
    fontSize: '0.9rem',
    fontWeight: '600',
    color: '#333333'
  },
  dropdownArrow: {
    fontSize: '0.7rem',
    color: '#8E24AA',
    transition: 'transform 0.2s ease'
  },
  userMenu: {
    position: 'absolute',
    top: '100%',
    right: 0,
    marginTop: '0.5rem',
    background: '#ffffff',
    border: '1px solid #e1bee7',
    borderRadius: '8px',
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
    minWidth: '180px',
    overflow: 'hidden',
    zIndex: 1001
  },
  menuItem: {
    padding: '0.8rem 1rem',
    cursor: 'pointer',
    fontSize: '0.9rem',
    color: '#333333',
    transition: 'background-color 0.2s ease',
    borderBottom: '1px solid #f0f0f0'
  },
  menuDivider: {
    height: '1px',
    background: '#e1bee7',
    margin: '0.5rem 0'
  },
  menuOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    zIndex: 999
  }
};

// 호버 효과를 위한 스타일 추가
const styleSheet = document.createElement('style');
styleSheet.textContent = `
  .navbar-user-profile:hover {
    background-color: #f9f5fb;
  }
  .navbar-menu-item:hover {
    background-color: #f9f5fb;
  }
  .navbar-nav-button:hover {
    background-color: #8E24AA;
    color: #ffffff;
  }
`;
document.head.appendChild(styleSheet);

export default Navbar;