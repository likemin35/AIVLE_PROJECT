import React from 'react';

function FormInput({
  label,
  name,
  value,
  onChange,
  type = 'text',
  placeholder = '',
  required = false
}) {
  return (
    <div style={{ marginBottom: '1.5rem' }}>
      <label
        htmlFor={name}
        style={{
          display: 'block',
          fontWeight: '600',
          marginBottom: '0.5rem',
          fontSize: '1rem',
          color: '#333'
        }}
      >
        {label}
      </label>
      <input
        id={name}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        style={{
          width: '100%',
          padding: '1rem',
          fontSize: '1rem',
          borderRadius: '8px',
          border: '1px solid #ccc',
          outline: 'none',
          transition: 'border-color 0.2s ease',
        }}
        onFocus={(e) =>
          (e.target.style.borderColor = '#8E24AA')
        }
        onBlur={(e) =>
          (e.target.style.borderColor = '#ccc')
        }
      />
    </div>
  );
}

export default FormInput;
