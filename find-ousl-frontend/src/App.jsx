import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';

// Public Pages
import Home from './pages/public/Home';

// Auth Pages
import Login from './pages/auth/Login';
import Signup from './pages/auth/Signup';
import ForgotPassword from './pages/auth/ForgotPassword';
import ResetPassword from './pages/auth/ResetPassword';

// Item Pages
import ReportLostItem from './pages/items/ReportLostItem';
import ReportFoundItem from './pages/items/ReportFoundItem';

/**
 * Main App Component
 */
function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Authentication Routes - No Navbar */}
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          
          {/* Main Application Routes - With Navbar */}
          <Route
            path="/*"
            element={
              <>
                {/* Navbar appears on all routes except login/signup/password-recovery */}
                <Navbar />
                <Routes>
                  {/* Home Page */}
                  <Route path="/" element={<Home />} />
                  
                  {/* Item Management Routes */}
                  <Route path="/report-lost" element={<ReportLostItem />} />
                  <Route path="/report-found" element={<ReportFoundItem />} />

                  
                </Routes>
              </>
            }
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;