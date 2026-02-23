import axios from 'axios';

/**
 * API Service Configuration
 * Centralized axios instance for all API calls to backend
 * Handles authentication tokens and error responses
 */

// Create axios instance with default configuration
const api = axios.create({
  baseURL: 'http://localhost:8080/api', // TODO: Replace with actual backend URL
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // Request timeout: 10 seconds
});

/**
 * Request Interceptor
 * Automatically adds authentication token to all requests
 */
api.interceptors.request.use(
  (config) => {
    // Get token from localStorage
    const token = localStorage.getItem('token');
    
    // Add token to request headers if available
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Log request for debugging (remove in production)
    console.log('API Request:', config.method.toUpperCase(), config.url);
    
    return config;
  },
  (error) => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

/*
    * Response Interceptor - Handles common error responses (401 Unauthorized, etc.)
 */
api.interceptors.response.use(
  (response) => {
    // Log successful response (remove in production)
    console.log('API Response:', response.status, response.config.url);
    return response;
  },
  (error) => {
    // Handle 401 Unauthorized - Token expired or invalid
    if (error.response?.status === 401) {
      console.log('Unauthorized! Redirecting to login...');
      
      // Clear stored authentication data
      localStorage.removeItem('token');
      localStorage.removeItem('userRole');
      localStorage.removeItem('username');
      
      // Redirect to login page
      window.location.href = '/login';
    }
    
    // Handle 403 Forbidden - Insufficient permissions
    if (error.response?.status === 403) {
      console.error('Forbidden! You do not have permission to access this resource.');
    }
    
    // Handle 500 Internal Server Error
    if (error.response?.status === 500) {
      console.error('Server Error! Please try again later.');
    }
    
    // Log error details
    console.error('API Error:', error.response?.status, error.message);
    
    return Promise.reject(error);
  }
);

// Export configured axios instance
export default api;
