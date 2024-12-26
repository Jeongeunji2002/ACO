// src/utils/axios.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL, // .env 파일에 설정된 URL 사용
  headers: {
    'Content-Type': 'application/json',
  },
});

export default apiClient;
