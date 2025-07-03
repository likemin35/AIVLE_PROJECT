import React from 'react';

const ManuscriptForm = ({ manuscript, setManuscript, onSave, onSubmit }) => {
  const handleChange = (e) => {
    const { name, value } = e.target;
    setManuscript({ ...manuscript, [name]: value });
  };

  return (
    <div style={styles.container}>
      {/* 상단 바 */}
      <div style={styles.header}>
        <div style={styles.brand}>KT 걷다가서재</div>
        <div style={styles.mypage}>마이페이지</div>
      </div>

      {/* 본문 */}
      <div style={styles.formWrapper}>
        {/* 제목/작가 입력 */}
        <div style={styles.inputRow}>
          <div style={styles.inputGroup}>
            <label htmlFor="title" style={styles.label}>제목</label>
            <input
              id="title"
              name="title"
              value={manuscript.title}
              onChange={handleChange}
              style={styles.input}
              type="text"
              placeholder="원고 제목을 입력하세요"
            />
          </div>
          <div style={styles.inputGroup}>
            <label htmlFor="authorId" style={styles.label}>작가</label>
            <input
              id="authorId"
              name="authorId"
              value={manuscript.authorId}
              onChange={handleChange}
              style={styles.input}
              type="text"
              placeholder="작가명을 입력하세요"
            />
          </div>
        </div>

        {/* 내용 입력 */}
        <div style={styles.textareaGroup}>
          <label htmlFor="content" style={styles.label}>내용</label>
          <textarea
            id="content"
            name="content"
            value={manuscript.content}
            onChange={handleChange}
            placeholder="원고를 입력하세요"
            style={styles.textarea}
          />
        </div>

        {/* 버튼 */}
        <div style={styles.buttonGroup}>
          <button onClick={onSave} style={styles.saveButton}>
            임시 저장
          </button>
          <button onClick={onSubmit} style={styles.submitButton}>
            출판 신청
          </button>
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: {
    fontFamily: 'sans-serif',
    backgroundColor: '#fff',
    minHeight: '100vh'
  },
  header: {
    backgroundColor: '#f9f5fb',
    padding: '20px 40px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderBottom: '1px solid #e1bee7'
  },
  brand: {
    color: '#8E24AA',
    fontWeight: 'bold',
    fontSize: '20px'
  },
  mypage: {
    color: '#8E24AA',
    fontSize: '14px'
  },
  formWrapper: {
    padding: '40px',
    maxWidth: '900px',
    margin: '0 auto'
  },
  inputRow: {
    display: 'flex',
    gap: '20px',
    marginBottom: '20px'
  },
  inputGroup: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column'
  },
  label: {
    marginBottom: '6px',
    fontWeight: '600',
    fontSize: '1rem',
    color: '#333'
  },
  input: {
    padding: '12px',
    backgroundColor: '#f8f8f8',
    border: '1px solid #ddd',
    borderRadius: '6px',
    fontSize: '1rem',
    outline: 'none',
    transition: 'border-color 0.2s ease'
  },
  textareaGroup: {
    marginBottom: '30px',
    display: 'flex',
    flexDirection: 'column'
  },
  textarea: {
    width: '100%',
    height: '400px',
    padding: '12px',
    backgroundColor: '#ffffff',
    border: '1px solid #ddd',
    borderRadius: '6px',
    fontSize: '1rem',
    resize: 'vertical',
    outline: 'none',
    lineHeight: '1.6'
  },
  buttonGroup: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '12px'
  },
  saveButton: {
    backgroundColor: '#f9f5fb',
    color: '#8E24AA',
    border: '1px solid #e1bee7',
    padding: '12px 24px',
    borderRadius: '6px',
    fontWeight: 'bold',
    fontSize: '1rem',
    cursor: 'pointer',
    transition: 'background 0.2s ease'
  },
  submitButton: {
    backgroundColor: '#8E24AA',
    color: '#ffffff',
    border: 'none',
    padding: '12px 24px',
    borderRadius: '6px',
    fontWeight: 'bold',
    fontSize: '1rem',
    cursor: 'pointer',
    transition: 'background 0.2s ease'
  }
};

export default ManuscriptForm;
