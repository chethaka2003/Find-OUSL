import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, Lock, Users } from 'lucide-react';
import axios from 'axios';
import campusImage from '../assets/images/ousl-campus.jpg';
import ouslLogo from '../assets/images/ousl-logo.png';

/**
 * Login Page Component
 * Allows users to authenticate and access the system */

const Login = () => {
  // Form state management
  const [userRole, setUserRole] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();

  /* Handle login form submission */
  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // TODO: Replace actual backend API endpoint
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        username,
        password,
        role: userRole
      });

      // Store authentication token and user role in localStorage
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('userRole', userRole);
      localStorage.setItem('username', username);
      
      // Redirect to home page after successful login
      navigate('/');
    } catch (err) {
      // Display error message
      setError('Invalid credentials. Please try again.');
      console.error('Login error:', err);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Handle Google OAuth login
   * TODO: Implement Google OAuth integration
   */
  const handleGoogleLogin = () => {
    console.log('Google login clicked');
    // TODO: Implement Google OAuth flow
    alert('Google login will be implemented with backend integration');
  };

  return (
    <div className="min-h-screen flex">
      
      {/* Left Side - Login Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center bg-gray-50 p-8">
        <div className="max-w-md w-full">
          
          {/* Logo and Title */}
          <div className="flex items-center gap-3 mb-8">
            <img 
              src={ouslLogo} 
              alt="OUSL Logo" 
              className="h-12 w-12 object-contain"
            />
            <span className="text-2xl font-bold">OUSL Lost & Found System</span>
          </div>

          {/* Page Heading */}
          <h2 className="text-3xl font-bold text-primary mb-2">Welcome Back</h2>
          <p className="text-gray-600 mb-8">Please login to access your account.</p>

          {/* Error Message Display */}
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}

          {/* Login Form */}
          <form onSubmit={handleLogin} className="space-y-4">
            
            {/* User Role Dropdown */}
            <div>
              <div className="relative">
                <Users className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <select
                  value={userRole}
                  onChange={(e) => setUserRole(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">Select User Role</option>
                  <option value="student">Student</option>
                  <option value="staff">Staff</option>
                  <option value="admin">Admin</option>
                </select>
              </div>
            </div>

            {/* Username/Email Input */}
            <div>
              <div className="relative">
                <User className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  placeholder="Username or Email"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
            </div>

            {/* Password Input */}
            <div>
              <div className="relative">
                <Lock className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type="password"
                  placeholder="Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
            </div>

            {/* Forgot Password Link */}
            <div className="text-right">
              <Link to="/forgot-password" className="text-sm text-primary hover:underline">
                Forgot your password?
              </Link>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary text-white py-3 rounded-lg font-semibold hover:bg-blue-800 transition disabled:opacity-50"
            >
              {loading ? 'Logging in...' : 'Login'}
            </button>
          </form>

          {/* Divider */}
          <div className="flex items-center my-6">
            <div className="flex-1 border-t border-gray-300"></div>
            <span className="px-4 text-gray-500 text-sm">OR</span>
            <div className="flex-1 border-t border-gray-300"></div>
          </div>

          {/* Google Login Button */}
          <button
            onClick={handleGoogleLogin}
            className="w-full border border-gray-300 py-3 rounded-lg flex items-center justify-center gap-2 hover:bg-gray-50 transition"
          >
            <img src="https://www.google.com/favicon.ico" alt="Google" className="w-5 h-5" />
            <span>Login with Google</span>
          </button>

          {/* Signup Link */}
          <p className="text-center mt-6 text-gray-600">
            Don't have an account?{' '}
            <Link to="/signup" className="text-primary font-semibold hover:underline">
              Sign Up
            </Link>
          </p>
        </div>
      </div>

      {/* Right Side - Background Image with Overlay */}
      <div 
        className="hidden lg:flex lg:w-1/2 bg-cover bg-center items-center justify-center text-white p-12"
        style={{
          backgroundImage: `url(${campusImage})`,
          backgroundBlendMode: 'overlay',
          backgroundColor: 'rgba(30, 58, 95, 0.8)' // Dark blue overlay
        }}
      >
        <div className="text-center">
          <h2 className="text-4xl font-bold mb-4">Your Trusted Companion</h2>
          <p className="text-lg">
            The official platform for reuniting lost items with their owners at the Open
            University of Sri Lanka.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;