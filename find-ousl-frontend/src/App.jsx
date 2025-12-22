import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';

/**
 * Main App Component
 * Sets up routing for the entire application
 * Routes without navbar: Login, Signup
 * Routes with navbar: Home, and all other pages
 */
function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Authentication Routes - No Navbar */}
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          
          {/* Main Application Routes - With Navbar */}
          <Route
            path="/*"
            element={
              <>
                {/* Navbar appears on all routes except login/signup */}
                <Navbar />
                <Routes>
                  {/* Home Page */}
                  <Route path="/" element={<Home />} />
                  
                  {/* TODO: Add more routes */}
                  {/* <Route path="/report-lost" element={<ReportLostItem />} /> */}
                  
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