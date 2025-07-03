import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import MainPage from './pages/MainPage';
import PointChargePage from './pages/PointChargePage';
import BookRentalPage from './pages/BookRentalPage';
import BookReadPage from './pages/BookReadPage';
import AuthorRegisterPage from './pages/AuthorRegisterPage';
import ManuscriptCreatePage from './pages/ManuscriptCreatePage';
import ManuscriptEditPage from './pages/ManuscriptEditPage';
import MyPage from './pages/MyPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/register" element={<AuthorRegisterPage />} />
        <Route path="/charge" element={<PointChargePage />} />
        <Route path="/rent/:bookId" element={<BookRentalPage />} />
        <Route path="/read/:bookId" element={<BookReadPage />} />
        <Route path="/manuscripts/create" element={<ManuscriptCreatePage />} />
        <Route path="/manuscripts/edit/:bookId" element={<ManuscriptEditPage />} />
      </Routes>
    </Router>
  );
}

export default App;