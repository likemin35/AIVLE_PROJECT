import React from 'react';

function PaymentMethodSelector({ selected, onSelect }) {
  return (
    <div style={styles.container}>
      <button
        onClick={() => onSelect('card')}
        style={selected === 'card' ? styles.selectedBtn : styles.btn}
      >
        카드 결제
      </button>
      <button
        onClick={() => onSelect('bank')}
        style={selected === 'bank' ? styles.selectedBtn : styles.btn}
      >
        계좌 이체
      </button>
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    gap: '1rem',
    justifyContent: 'center',
    marginBottom: '1.5rem'
  },
  btn: {
    padding: '1rem 2rem',
    backgroundColor: '#f0f0f0',
    border: '1px solid #ccc',
    borderRadius: '12px',
    cursor: 'pointer',
    fontSize: '1rem',
    fontWeight: '500',
    color: '#333',
    transition: 'all 0.3s ease',
  },
  selectedBtn: {
    padding: '1rem 2rem',
    backgroundColor: '#8E24AA',
    color: '#fff',
    fontWeight: '700',
    borderRadius: '12px',
    border: '1px solid #8E24AA',
    cursor: 'pointer',
    fontSize: '1rem',
    boxShadow: '0 4px 12px rgba(142, 36, 170, 0.3)',
    transition: 'all 0.3s ease',
  }
};

export default PaymentMethodSelector;
