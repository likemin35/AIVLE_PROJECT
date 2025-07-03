import React from 'react';

function PointOptionButton({ options, selected, onSelect }) {
  return (
    <div style={styles.container}>
      {options.map(({ point, price }) => {
        const isSelected = selected?.point === point;
        return (
          <button
            key={point}
            onClick={() => onSelect({ point, price })}
            style={isSelected ? styles.selectedBtn : styles.btn}
          >
            <div style={styles.pointText}>{point.toLocaleString()}P</div>
            <div style={styles.priceText}>{price.toLocaleString()}원</div>
          </button>
        );
      })}
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '1rem',
    justifyContent: 'center',
    marginTop: '1rem',
    marginBottom: '2rem'
  },
  btn: {
    backgroundColor: '#f5f5f5',
    border: '1px solid #ccc',
    borderRadius: '12px',
    padding: '1rem 1.5rem',
    cursor: 'pointer',
    minWidth: '140px',
    textAlign: 'center',
    fontSize: '1rem',
    fontWeight: '500',
    color: '#333',
    transition: 'all 0.3s ease'
  },
  selectedBtn: {
    backgroundColor: '#8E24AA',
    color: '#fff',
    border: '2px solid #8E24AA',
    borderRadius: '12px',
    padding: '1rem 1.5rem',
    cursor: 'pointer',
    minWidth: '140px',
    textAlign: 'center',
    fontSize: '1rem',
    fontWeight: '600',
    boxShadow: '0 4px 12px rgba(142, 36, 170, 0.3)',
    transition: 'all 0.3s ease'
  },
  pointText: {
    fontSize: '1.2rem',
    fontWeight: 'bold'
  },
  priceText: {
    fontSize: '0.95rem',
    marginTop: '0.3rem'
  }
};

export default PointOptionButton;
