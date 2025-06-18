// src/SignupForm.jsx
import React, { useContext } from 'react';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import axios from 'axios';
import './Form.css';
import { AuthContext } from './Context/AuthContext';

export const SignupForm = ({ onSignupSuccess }) => {
  const { login } = useContext(AuthContext);

  const formik = useFormik({
    initialValues: {
      username: '',
      password: '',
      gmail: '',
    },
    validationSchema: Yup.object({
      username: Yup.string()
        .required('Required: Please enter a username.'),
      password: Yup.string()
        .required('Required: Please enter a password.'),
      gmail: Yup.string()
        .email('Invalid email address.')
        .required('Required: Please enter your email.'),
    }),
    onSubmit: async (values) => {
      try {
        // Önce kayıt ol
        await axios.post('http://localhost:8082/auth/register', values);
        alert('Registration successful! Now logging in...');

        // Kayıt başarılıysa, otomatik olarak giriş yap
        const loginResult = await login(values.gmail, values.password);

        if (loginResult.success) {
          alert('Login successful!');
          if (onSignupSuccess) {
            onSignupSuccess(); // Üst komponenti başarılı kayıt ve giriş hakkında bilgilendir
          }
          formik.resetForm(); // Formu temizle
        } else {
          alert('Registration successful, but automatic login failed: ' + loginResult.message);
        }

      } catch (error) {
        alert('Registration failed. Please try again.');
        console.error('Registration error:', error);
      }
    },
  });

  return (
    <div className="form-container">
      <form onSubmit={formik.handleSubmit} className="signup-form">
        <h2>Register Your Account</h2>

        <div className="form-group">
          <label htmlFor="username">Username</label>
          <input
            id="username"
            type="text"
            placeholder="Enter your username"
            className={formik.touched.username && formik.errors.username ? 'input-error' : ''}
            {...formik.getFieldProps('username')}
          />
          {formik.touched.username && formik.errors.username ? (
            <div className="error-message">{formik.errors.username}</div>
          ) : null}
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            placeholder="Enter your password"
            className={formik.touched.password && formik.errors.password ? 'input-error' : ''}
            {...formik.getFieldProps('password')}
          />
          {formik.touched.password && formik.errors.password ? (
            <div className="error-message">{formik.errors.password}</div>
          ) : null}
        </div>

        <div className="form-group">
          <label htmlFor="gmail">Email</label>
          <input
            id="gmail"
            type="email"
            placeholder="Enter your email address"
            className={formik.touched.gmail && formik.errors.gmail ? 'input-error' : ''}
            {...formik.getFieldProps('gmail')}
          />
          {formik.touched.gmail && formik.errors.gmail ? (
            <div className="error-message">{formik.errors.gmail}</div>
          ) : null}
        </div>

        <button type="submit" className="submit-button" disabled={formik.isSubmitting}>
          {formik.isSubmitting ? 'Registering...' : 'Register'}
        </button>
      </form>
    </div>
  );
};