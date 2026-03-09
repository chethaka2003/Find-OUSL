import { useState } from 'react';
import { Mail } from 'lucide-react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import ouslLogo from '../../assets/images/ousl-logo.png'; 

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await axios.post('http://localhost:8080/api/auth/forgot-password', { email });
      setSuccess(true);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to send reset email');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-8">
      <div className="max-w-md w-full bg-white rounded-lg shadow-lg p-8">
        
        {/* Logo */}
        <div className="flex items-center gap-3 mb-8 justify-center">
          <img src={ouslLogo} alt="OUSL Logo" className="h-12 w-12" />
          <span className="text-xl font-bold">OUSL Lost & Found</span>
        </div>

        <h2 className="text-2xl font-bold text-primary mb-2">Forgot Password?</h2>
        <p className="text-gray-600 mb-6">
          Enter your email and we'll send you a reset link within 5 minutes.
        </p>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {success ?  (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
            <p className="font-semibold">Reset link sent! </p>
            <p className="text-sm">Please check your email for password reset instructions.</p>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="relative">
              <Mail className="absolute left-3 top-3 text-gray-400 w-5 h-5" />
              <input
                type="email"
                placeholder="Enter your registered email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-primary text-white py-3 rounded-lg font-semibold hover:bg-blue-800 transition disabled:opacity-50"
            >
              {loading ? 'Sending.. .' : 'Send Reset Link'}
            </button>
          </form>
        )}

        <p className="text-center mt-6 text-gray-600">
          <Link to="/login" className="text-primary font-semibold hover:underline">
            Back to Login
          </Link>
        </p>
      </div>
    </div>
  );
};

export default ForgotPassword;