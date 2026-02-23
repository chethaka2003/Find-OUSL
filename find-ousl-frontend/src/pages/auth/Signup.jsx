import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, Mail, Lock, Users, Home as HomeIcon } from 'lucide-react';
import axios from 'axios';
import campusImage from '../../assets/images/ousl-campus.jpg';
import ouslLogo from '../../assets/images/ousl-logo.png';

/**
 * Signup Page Component
 * Allows new users to create an account */
const Signup = () => {
  // Form state management
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    userRole: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();

  /**
   * Handle input field changes
   * Updates formData state when user types
   */
  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target. name]: e.target.value
    });
  };

  /**
   * Handle signup form submission
   * Validates passwords match and sends data to backend
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validate that passwords match
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    // Validate password strength (minimum 6 characters)
    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    setLoading(true);

    try {
      // TODO:  Replace with actual backend API endpoint
      const response = await axios.post('http://localhost:8080/api/auth/signup', {
        fullName: formData.fullName,
        email: formData.email,
        password: formData. password,
        role: formData.userRole
      });

      // Store authentication token and user info
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('userRole', formData.userRole);
      localStorage.setItem('username', formData.fullName);
      
      // Redirect to home page after successful registration
      navigate('/');
    } catch (err) {
      // Display error message
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Registration failed. Please try again.');
      }
      console.error('Signup error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      
      {/* Left Side - Signup Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center bg-gray-50 p-8 relative">
        
        {/* Home Button - Top Right */}
        <button
          onClick={() => navigate('/')}
          className="absolute top-6 right-6 flex items-center gap-2 text-gray-600 hover:text-primary transition font-semibold"
        >
          <HomeIcon className="w-5 h-5" />
          <span>Home</span>
        </button>

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
          <h2 className="text-3xl font-bold text-primary mb-2">Create Account</h2>
          <p className="text-gray-600 mb-8">Please fill in the details to create your account.</p>

          {/* Error Message Display */}
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}

          {/* Signup Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            
            {/* Full Name Input */}
            <div>
              <div className="relative">
                <User className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  name="fullName"
                  placeholder="Full Name"
                  value={formData.fullName}
                  onChange={handleChange}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus: ring-blue-500"
                  required
                />
              </div>
            </div>

            {/* Email Input */}
            <div>
              <div className="relative">
                <Mail className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type="email"
                  name="email"
                  placeholder="Email Address"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus: ring-2 focus:ring-blue-500"
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
                  name="password"
                  placeholder="Password"
                  value={formData.password}
                  onChange={handleChange}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus: ring-2 focus:ring-blue-500"
                  required
                  minLength="6"
                />
              </div>
            </div>

            {/* Confirm Password Input */}
            <div>
              <div className="relative">
                <Lock className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <input
                  type="password"
                  name="confirmPassword"
                  placeholder="Confirm Password"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                  minLength="6"
                />
              </div>
            </div>

            {/* User Role Dropdown */}
            <div>
              <div className="relative">
                <Users className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
                <select
                  name="userRole"
                  value={formData.userRole}
                  onChange={handleChange}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">Select User Role</option>
                  <option value="student">Student</option>
                  <option value="staff">Staff</option>
                </select>
              </div>
            </div>

            {/* Sign Up Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary text-white py-3 rounded-lg font-semibold hover:bg-blue-800 transition disabled: opacity-50"
            >
              {loading ? 'Creating Account...' : 'Sign Up'}
            </button>
          </form>

          {/* Login Link */}
          <p className="text-center mt-6 text-gray-600">
            Already have an account? {' '}
            <Link to="/login" className="text-primary font-semibold hover: underline">
              Login
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
          <h2 className="text-4xl font-bold mb-4">Join Our Community</h2>
          <p className="text-lg">
            Create your account today and start connecting lost items with their rightful owners at OUSL.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Signup;