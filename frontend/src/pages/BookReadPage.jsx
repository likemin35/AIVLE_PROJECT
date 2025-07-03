// src/pages/BookReadPage.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';

const dummyBooks = [
  {
    id: 1,
    title: '해리포터',
    author: '조하민',
    content: `제1장 - 마법의 시작

평범한 소년 해리 포터는 자신이 마법사라는 사실을 11살 생일에 알게 됩니다. 더즐리 가족과 함께 살던 해리는 호그와트 마법학교 입학 통지서를 받게 되고, 완전히 새로운 세계로 떠나게 됩니다.

"해리야, 넌 마법사다." 하그리드가 말했습니다.

해리는 믿을 수 없었습니다. 하지만 곧 자신의 운명을 받아들이게 되고, 론과 헤르미온느라는 소중한 친구들을 만나게 됩니다.

호그와트에서의 첫 해는 모험으로 가득했습니다. 마법 수업, 퀴디치, 그리고 철학자의 돌을 둘러싼 위험한 모험이 기다리고 있었습니다.

제2장 - 친구들과의 만남

기차 안에서 론 위즐리를 만난 해리는 처음으로 진정한 친구를 사귀게 됩니다. 똑똑하고 용감한 헤르미온느 그레인저도 곧 이들의 소중한 친구가 됩니다.

"우리는 함께라면 무엇이든 할 수 있어." 헤르미온느가 말했습니다.

세 친구는 학교에서 일어나는 신비한 사건들을 해결하며 더욱 단단한 우정을 쌓아갑니다. 스네이프 교수의 의심스러운 행동, 플러피라는 거대한 개, 그리고 철학자의 돌의 비밀...

제3장 - 용기의 시험

마침내 해리와 친구들은 철학자의 돌을 지키기 위해 위험한 도전에 나섭니다. 체스 게임, 마법의 덫, 그리고 마지막에 기다리고 있는 진짜 적과의 대결...

"진정한 용기란 두려워도 옳은 일을 하는 것이야." 덤블도어 교장이 말씀하셨습니다.

해리는 이 모험을 통해 진정한 용기가 무엇인지, 그리고 사랑의 힘이 얼마나 강한지 깨닫게 됩니다. 어머니의 사랑이 자신을 지켜주고 있다는 것을 알게 된 해리는 앞으로 닥칠 더 큰 모험들을 준비하게 됩니다.

그리고 이것은 해리 포터의 마법 같은 이야기의 시작일 뿐이었습니다...`
  }
];

function BookReadPage() {
  const { bookId } = useParams();
  const navigate = useNavigate();
  const book = dummyBooks.find((b) => b.id === parseInt(bookId));

  const [fontSize, setFontSize] = useState(16);
  const [backgroundColor, setBackgroundColor] = useState('#ffffff');
  const [textColor, setTextColor] = useState('#000000');
  const [showSettings, setShowSettings] = useState(false);
  const [readingProgress, setReadingProgress] = useState(0);
  const [showContents, setShowContents] = useState(false);

  // 읽기 진행률 계산
  useEffect(() => {
    const handleScroll = () => {
      const scrollTop = window.pageYOffset;
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      const scrollPercent = (scrollTop / docHeight) * 100;
      setReadingProgress(Math.min(100, Math.max(0, scrollPercent)));
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  if (!book) {
    return (
      <div style={{ minHeight: '100vh', backgroundColor: '#ffffff', color: '#000000', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
        <Navbar />
        <h2>도서를 찾을 수 없습니다</h2>
        <button onClick={() => navigate('/main')} style={{ backgroundColor: '#8E24AA', color: '#ffffff', padding: '1rem 2rem', border: 'none', borderRadius: '12px', cursor: 'pointer' }}>
          메인으로 돌아가기
        </button>
      </div>
    );
  }

  const backgroundOptions = [
    { name: '밝게', bg: '#ffffff', text: '#000000' },
    { name: '어둡게', bg: '#1a1a1a', text: '#ffffff' },
    { name: '세피아', bg: '#f4f1e8', text: '#5d4e37' },
    { name: '연보라', bg: '#f9f5fb', text: '#333333' }
  ];

  const contents = [
    { title: '제1장 - 마법의 시작', position: 0 },
    { title: '제2장 - 친구들과의 만남', position: 40 },
    { title: '제3장 - 용기의 시험', position: 70 }
  ];

  const scrollToPosition = (position) => {
    const docHeight = document.documentElement.scrollHeight - window.innerHeight;
    const scrollTo = (docHeight * position) / 100;
    window.scrollTo({ top: scrollTo, behavior: 'smooth' });
    setShowContents(false);
  };

  return (
    <div style={{ 
      minHeight: '100vh', 
      backgroundColor: backgroundColor, 
      color: textColor, 
      fontFamily: 'sans-serif',
      position: 'relative'
    }}>
      <Navbar />
      
      {/* 진행률 표시 */}
      <div style={{
        position: 'fixed',
        top: '72px',
        left: 0,
        width: `${readingProgress}%`,
        height: '4px',
        backgroundColor: '#8E24AA',
        zIndex: 1000,
        transition: 'width 0.3s ease'
      }} />

      {/* 읽기 도구 모음 */}
      <div style={{
        position: 'fixed',
        top: '80px',
        right: '20px',
        display: 'flex',
        flexDirection: 'column',
        gap: '10px',
        zIndex: 1000
      }}>
        <button
          onClick={() => setShowContents(!showContents)}
          style={styles.toolButton}
          title="목차"
        >
          📋
        </button>
        <button
          onClick={() => setShowSettings(!showSettings)}
          style={styles.toolButton}
          title="설정"
        >
          ⚙️
        </button>
        <button
          onClick={() => navigate(-1)}
          style={styles.toolButton}
          title="뒤로가기"
        >
          ←
        </button>
      </div>

      {/* 목차 모달 */}
      {showContents && (
        <div style={styles.modalOverlay} onClick={() => setShowContents(false)}>
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h3 style={styles.modalTitle}>목차</h3>
            {contents.map((item, index) => (
              <button
                key={index}
                onClick={() => scrollToPosition(item.position)}
                style={styles.contentsItem}
              >
                {item.title}
              </button>
            ))}
            <button
              onClick={() => setShowContents(false)}
              style={styles.closeButton}
            >
              닫기
            </button>
          </div>
        </div>
      )}

      {/* 설정 모달 */}
      {showSettings && (
        <div style={styles.modalOverlay} onClick={() => setShowSettings(false)}>
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h3 style={styles.modalTitle}>읽기 설정</h3>
            
            <div style={styles.settingGroup}>
              <label style={styles.settingLabel}>글꼴 크기</label>
              <div style={styles.fontSizeControls}>
                <button 
                  onClick={() => setFontSize(Math.max(12, fontSize - 2))}
                  style={styles.fontButton}
                >
                  A-
                </button>
                <span style={styles.fontSizeDisplay}>{fontSize}px</span>
                <button 
                  onClick={() => setFontSize(Math.min(24, fontSize + 2))}
                  style={styles.fontButton}
                >
                  A+
                </button>
              </div>
            </div>

            <div style={styles.settingGroup}>
              <label style={styles.settingLabel}>배경 테마</label>
              <div style={styles.backgroundOptions}>
                {backgroundOptions.map((option, index) => (
                  <button
                    key={index}
                    onClick={() => {
                      setBackgroundColor(option.bg);
                      setTextColor(option.text);
                    }}
                    style={{
                      ...styles.backgroundOption,
                      backgroundColor: option.bg,
                      color: option.text,
                      border: backgroundColor === option.bg ? '2px solid #8E24AA' : '1px solid #ccc'
                    }}
                  >
                    {option.name}
                  </button>
                ))}
              </div>
            </div>

            <button
              onClick={() => setShowSettings(false)}
              style={styles.closeButton}
            >
              닫기
            </button>
          </div>
        </div>
      )}

      {/* 메인 콘텐츠 */}
      <div style={{
        maxWidth: '800px',
        margin: '0 auto',
        padding: '2rem',
        paddingTop: '4rem'
      }}>
        {/* 도서 헤더 */}
        <div style={styles.bookHeader}>
          <h1 style={{
            ...styles.bookTitle,
            color: textColor
          }}>{book.title}</h1>
          <p style={{
            ...styles.bookAuthor,
            color: textColor
          }}>저자: {book.author}</p>
          <div style={styles.progressInfo}>
            <span style={{ color: textColor }}>읽기 진행률: {Math.round(readingProgress)}%</span>
          </div>
        </div>

        {/* 도서 내용 */}
        <div style={{
          ...styles.bookContent,
          fontSize: `${fontSize}px`,
          color: textColor,
          lineHeight: '1.8'
        }}>
          {book.content.split('\n\n').map((paragraph, index) => (
            <p key={index} style={{ 
              marginBottom: '1.5rem',
              textIndent: paragraph.startsWith('제') ? '0' : '1rem'
            }}>
              {paragraph.startsWith('제') ? (
                <strong style={{ 
                  fontSize: `${fontSize + 4}px`, 
                  color: '#8E24AA',
                  display: 'block',
                  marginBottom: '1rem',
                  textIndent: '0'
                }}>
                  {paragraph}
                </strong>
              ) : (
                paragraph
              )}
            </p>
          ))}
        </div>

        {/* 책 완료 버튼 */}
        <div style={styles.completionSection}>
          <div style={{
            ...styles.completionCard,
            backgroundColor: backgroundColor === '#ffffff' ? '#f9f5fb' : 'rgba(142, 36, 170, 0.1)'
          }}>
            <h3 style={{ color: '#8E24AA', marginBottom: '1rem' }}>읽기 완료!</h3>
            <p style={{ color: textColor, marginBottom: '1rem' }}>
              "{book.title}"을(를) 모두 읽으셨습니다. 즐거운 독서였나요?
            </p>
            <div style={styles.completionButtons}>
              <button
                onClick={() => navigate('/main')}
                style={styles.mainButton}
              >
                메인으로
              </button>
              <button
                onClick={() => alert('리뷰 작성 기능은 준비 중입니다.')}
                style={styles.reviewButton}
              >
                리뷰 작성
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

const styles = {
  toolButton: {
    width: '50px',
    height: '50px',
    borderRadius: '50%',
    backgroundColor: '#8E24AA',
    color: '#ffffff',
    border: 'none',
    fontSize: '20px',
    cursor: 'pointer',
    boxShadow: '0 4px 12px rgba(142, 36, 170, 0.3)',
    transition: 'all 0.3s ease'
  },
  modalOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 2000
  },
  modalContent: {
    background: '#ffffff',
    color: '#000000',
    padding: '2rem',
    borderRadius: '16px',
    minWidth: '300px',
    maxWidth: '500px',
    maxHeight: '80vh',
    overflow: 'auto'
  },
  modalTitle: {
    fontSize: '1.5rem',
    fontWeight: 'bold',
    color: '#8E24AA',
    marginBottom: '1.5rem',
    textAlign: 'center'
  },
  contentsItem: {
    display: 'block',
    width: '100%',
    padding: '1rem',
    marginBottom: '0.5rem',
    backgroundColor: '#f9f5fb',
    border: '1px solid #e1bee7',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '1rem',
    textAlign: 'left',
    transition: 'all 0.3s ease'
  },
  settingGroup: {
    marginBottom: '2rem'
  },
  settingLabel: {
    display: 'block',
    fontWeight: '600',
    marginBottom: '1rem',
    color: '#8E24AA'
  },
  fontSizeControls: {
    display: 'flex',
    alignItems: 'center',
    gap: '1rem',
    justifyContent: 'center'
  },
  fontButton: {
    width: '40px',
    height: '40px',
    borderRadius: '8px',
    backgroundColor: '#8E24AA',
    color: '#ffffff',
    border: 'none',
    cursor: 'pointer',
    fontSize: '16px',
    fontWeight: 'bold'
  },
  fontSizeDisplay: {
    fontSize: '1.1rem',
    fontWeight: 'bold',
    minWidth: '50px',
    textAlign: 'center'
  },
  backgroundOptions: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '0.5rem'
  },
  backgroundOption: {
    padding: '1rem',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '0.9rem',
    fontWeight: '600',
    transition: 'all 0.3s ease'
  },
  closeButton: {
    width: '100%',
    padding: '1rem',
    backgroundColor: '#8E24AA',
    color: '#ffffff',
    border: 'none',
    borderRadius: '8px',
    fontSize: '1rem',
    fontWeight: 'bold',
    cursor: 'pointer',
    marginTop: '1rem'
  },
  bookHeader: {
    textAlign: 'center',
    marginBottom: '3rem',
    paddingBottom: '2rem',
    borderBottom: '2px solid #8E24AA'
  },
  bookTitle: {
    fontSize: '2.2rem',
    fontWeight: 'bold',
    marginBottom: '1rem'
  },
  bookAuthor: {
    fontSize: '1.2rem',
    marginBottom: '1rem'
  },
  progressInfo: {
    fontSize: '1rem',
    opacity: 0.8
  },
  bookContent: {
    marginBottom: '4rem'
  },
  completionSection: {
    marginTop: '4rem',
    textAlign: 'center'
  },
  completionCard: {
    padding: '2rem',
    borderRadius: '16px',
    border: '1px solid #e1bee7'
  },
  completionButtons: {
    display: 'flex',
    gap: '1rem',
    justifyContent: 'center',
    flexWrap: 'wrap'
  },
  mainButton: {
    padding: '1rem 2rem',
    backgroundColor: '#8E24AA',
    color: '#ffffff',
    border: 'none',
    borderRadius: '8px',
    fontSize: '1rem',
    fontWeight: 'bold',
    cursor: 'pointer',
    transition: 'all 0.3s ease'
  },
  reviewButton: {
    padding: '1rem 2rem',
    backgroundColor: '#ffffff',
    color: '#8E24AA',
    border: '2px solid #8E24AA',
    borderRadius: '8px',
    fontSize: '1rem',
    fontWeight: 'bold',
    cursor: 'pointer',
    transition: 'all 0.3s ease'
  }
};

export default BookReadPage;