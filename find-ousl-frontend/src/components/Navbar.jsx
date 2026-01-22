import { Link, useNavigate } from 'react-router-dom';
import { Bell, User } from 'lucide-react';
import ouslLogo from '../assets/images/ousl-logo.png';

/**
 * Navbar Component
 * Main navigation bar displayed at the top of all pages (except login/signup)
 */
const Navbar = () => {
  const navigate = useNavigate();
  // Check if user is logged in by checking for token in localStorage
  const isLoggedIn = localStorage.getItem('token');

  return (
    <nav className="bg-white shadow-md">
      <div className="container mx-auto px-4 py-3 flex items-center justify-between">
        
        {/* Logo and Title Section */}
        <Link to="/" className="flex items-center gap-3">
          <img 
            src={ouslLogo} 
            alt="OUSL Logo" 
            className="h-10 w-10 object-contain" // Logo size: 40x40px
          />
          <span className="text-xl font-bold">OUSL Lost & Found System</span>
        </Link>

        {/* Navigation Links and User Actions */}
        <div className="flex items-center gap-6">
          
          {/* Main Navigation Links */}
          <Link to="/" className="text-gray-700 hover:text-primary transition">
            Home
          </Link>
          <Link to="/report-lost" className="text-gray-700 hover:text-primary transition">
            Report Lost Item
          </Link>
          <Link to="/report-found" className="text-gray-700 hover:text-primary transition">
            Report Found Item
          </Link>

          {/* Notification Bell - Only visible when logged in */}
          {isLoggedIn && (
            <button 
              onClick={() => navigate('/notifications')}
              className="relative hover:opacity-80 transition"
              aria-label="Notifications"
            >
              <Bell className="w-6 h-6 text-gray-700" />
              {/* Notification badge */}
              <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">
                3
              </span>
            </button>
          )}

          {/* User Profile or Login/Signup Buttons */}
          {isLoggedIn ? (
            // Show profile button when logged in
            <button 
              onClick={() => navigate('/profile')}
              className="bg-gray-800 text-white rounded-full p-2 hover:bg-gray-700 transition"
              aria-label="User Profile"
            >
              <User className="w-6 h-6" />
            </button>
          ) : (
            // Show login and signup buttons when not logged in
            <div className="flex items-center gap-3">
              <button
                onClick={() => navigate('/login')}
                className="text-primary border border-primary px-4 py-2 rounded-lg hover:bg-blue-50 transition"
              >
                Login
              </button>
              <button
                onClick={() => navigate('/signup')}
                className="bg-primary text-white px-4 py-2 rounded-lg hover:bg-blue-800 transition"
              >
                Sign Up
              </button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;