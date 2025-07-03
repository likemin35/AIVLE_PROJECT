import React from 'react';

function TextAreaInput({ label, name, value, onChange }) {
  return (
    <div style={styles.wrapper}>
      <label style={styles.label}>{label}</label>
      <textarea
        name={name}
        value={value}
        onChange={onChange}
        rows={6}
        style={styles.textarea}
        placeholder="입력해 주세요"
      />
    </div>
  );
}

const styles = {
  wrapper: {
    marginBottom: '1.5rem',
    display: 'flex',
    flexDirection: 'column',
    gap: '0.5rem'
  },
  label: {
    fontWeight: 'bold',
    fontSize: '1rem',
    color: '#333'
  },
  textarea: {
    width: '100%',
    padding: '1rem',
    borderRadius: '8px',
    border: '1px solid #ccc',
    fontSize: '1rem',
    resize: 'vertical',
    minHeight: '120px',
    backgroundColor: '#fff',
    color: '#333',
    lineHeight: '1.5'
  }
};

export default TextAreaInput;
