import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';

function ManuscriptCreatePage() {
  const navigate = useNavigate();
  const [manuscript, setManuscript] = useState({
    title: '',
    author: '',
    content: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setManuscript({ ...manuscript, [name]: value });
  };

  const handleSave = () => {
    if (!manuscript.title || !manuscript.author || !manuscript.content) {
      alert('모든 필드를 입력해주세요.');
      return;
    }
    alert('원고가 저장되었습니다.');
    navigate('/main');
  };

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9f9f9', color: '#000000', fontFamily: 'sans-serif' }}>
      <Navbar />
      <div style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
        <div style={{ background: '#ffffff', borderRadius: '16px', padding: '2rem', boxShadow: '0 4px 12px rgba(0, 0, 0, 0.05)' }}>
          <h2 style={{ color: '#8E24AA', fontSize: '1.8rem', fontWeight: 'bold', marginBottom: '2rem' }}>새 원고 작성</h2>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ fontWeight: '600', color: '#000' }}>제목</label>
            <input
              name="title"
              value={manuscript.title}
              onChange={handleChange}
              style={{ width: '100%', padding: '1rem', borderRadius: '8px', border: '1px solid #ccc', marginTop: '0.5rem' }}
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ fontWeight: '600', color: '#000' }}>작가명</label>
            <input
              name="author"
              value={manuscript.author}
              onChange={handleChange}
              style={{ width: '100%', padding: '1rem', borderRadius: '8px', border: '1px solid #ccc', marginTop: '0.5rem' }}
            />
          </div>

          <div style={{ marginBottom: '2rem' }}>
            <label style={{ fontWeight: '600', color: '#000' }}>내용</label>
            <textarea
              name="content"
              value={manuscript.content}
              onChange={handleChange}
              rows="10"
              style={{ width: '100%', padding: '1rem', borderRadius: '8px', border: '1px solid #ccc', marginTop: '0.5rem' }}
            />
          </div>

          <button
            onClick={handleSave}
            style={{
              width: '100%',
              padding: '1rem',
              backgroundColor: '#8E24AA',
              color: '#ffffff',
              border: 'none',
              borderRadius: '8px',
              fontSize: '1.1rem',
              fontWeight: 'bold',
              cursor: 'pointer'
            }}
          >
            저장하기
          </button>
        </div>
      </div>
    </div>
  );
}

export default ManuscriptCreatePage;
