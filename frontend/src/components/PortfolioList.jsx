import React from 'react';

function PortfolioList({ portfolios, setPortfolios }) {
  const handleChange = (index, field, value) => {
    const updated = [...portfolios];
    updated[index][field] = value;
    setPortfolios(updated);
  };

  const handleAdd = () => {
    setPortfolios([...portfolios, { category: '', title: '' }]);
  };

  const handleRemove = (index) => {
    const updated = [...portfolios];
    updated.splice(index, 1);
    setPortfolios(updated);
  };

  return (
    <div style={styles.wrapper}>
      <h4 style={styles.heading}>포트폴리오 리스트</h4>

      {portfolios.map((item, index) => (
        <div key={index} style={styles.card}>
          <div style={styles.inputGroup}>
            <label style={styles.label}>카테고리</label>
            <input
              type="text"
              value={item.category}
              onChange={(e) => handleChange(index, 'category', e.target.value)}
              style={styles.input}
              placeholder="예: 에세이, 동화 등"
            />
          </div>
          <div style={styles.inputGroup}>
            <label style={styles.label}>작품 제목</label>
            <input
              type="text"
              value={item.title}
              onChange={(e) => handleChange(index, 'title', e.target.value)}
              style={styles.input}
              placeholder="예: 너의 이름은"
            />
          </div>
          <button onClick={() => handleRemove(index)} style={styles.removeButton}>
            삭제
          </button>
        </div>
      ))}

      <button onClick={handleAdd} style={styles.addButton}>
        + 포트폴리오 추가
      </button>
    </div>
  );
}

const styles = {
  wrapper: {
    marginTop: '2rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '1.5rem'
  },
  heading: {
    fontSize: '1.2rem',
    fontWeight: '600',
    marginBottom: '0.5rem'
  },
  card: {
    display: 'flex',
    flexDirection: 'column',
    gap: '1rem',
    padding: '1.2rem',
    borderRadius: '12px',
    backgroundColor: '#f9f9f9',
    border: '1px solid #ddd',
    boxShadow: '0 2px 6px rgba(0, 0, 0, 0.05)',
    position: 'relative'
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column'
  },
  label: {
    fontSize: '0.9rem',
    fontWeight: '500',
    marginBottom: '0.4rem'
  },
  input: {
    padding: '0.8rem 1rem',
    border: '1px solid #ccc',
    borderRadius: '8px',
    fontSize: '1rem',
    backgroundColor: '#fff'
  },
  removeButton: {
    position: 'absolute',
    top: '1rem',
    right: '1rem',
    backgroundColor: '#ff7043',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    padding: '0.4rem 0.8rem',
    cursor: 'pointer',
    fontSize: '0.9rem'
  },
  addButton: {
    alignSelf: 'flex-start',
    backgroundColor: '#8E24AA',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    padding: '0.8rem 1.2rem',
    fontSize: '1rem',
    fontWeight: '600',
    cursor: 'pointer',
    marginTop: '0.5rem'
  }
};

export default PortfolioList;
