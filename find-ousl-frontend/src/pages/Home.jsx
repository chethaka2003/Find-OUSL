import { Search } from 'lucide-react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import heroImage from '../assets/images/ousl-building.jpg';

/**
 * Home Page Component
 * Landing page of the OUSL Lost & Found System */
const Home = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  // Mock data for recently found items
  // TODO: Replace with actual API call to backend
  const recentlyFoundItems = [
    { id: 1, name: 'Laptop', location: 'Found in the library', image: '💻' },
    { id: 2, name: 'Umbrella (Black)', location: 'Found in the main hall', image: '☂️' },
    { id: 3, name: 'Smartphone', location: 'Found in the ECL LAB', image: '📱' },
    { id: 4, name: 'Wallet', location: 'Found near the cafeteria', image: '👛' },
  ];

  /**
   * Handle search form submission
   * Redirects to search results page with query parameter
   */
  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery)}`);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      
      {/* Hero Section with Background Image */}
      <div 
        className="relative bg-cover bg-center h-[600px] flex items-center justify-center"
        style={{
          backgroundImage: `url(${heroImage})`,
          backgroundBlendMode: 'overlay',
          backgroundColor: 'rgba(0, 0, 0, 0.4)' // Dark overlay for text readability
        }}
      >
        {/* Hero Content */}
        <div className="text-center text-white z-10 px-4">
          <h1 className="text-5xl font-bold mb-4">Welcome to OUSL Lost & Found</h1>
          <p className="text-lg mb-6">
            Your trusted platform for reuniting lost items with their owners within the Open
            <br />
            University of Sri Lanka community.
          </p>
          
          {/* Search Bar */}
          <form onSubmit={handleSearch} className="max-w-xl mx-auto">
            <div className="relative">
              <input
                type="text"
                placeholder="Search for lost items (e.g., wallet, textbook...)"
                className="w-full px-4 py-3 rounded-lg text-gray-800 focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              {/* Search Button */}
              <button 
                type="submit"
                className="absolute right-2 top-2 bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition"
                aria-label="Search"
              >
                <Search className="w-5 h-5" />
              </button>
            </div>
          </form>
        </div>
      </div>

      {/* Recently Found Items Section */}
      <div className="container mx-auto px-4 py-12">
        <h2 className="text-3xl font-bold mb-8">Recently Found Items</h2>
        
        {/* Items Grid - Responsive: 1 column mobile, 2 tablet, 4 desktop */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {recentlyFoundItems.map((item) => (
            <div 
              key={item.id}
              className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition cursor-pointer transform hover:scale-105"
              onClick={() => navigate(`/item/${item.id}`)}
            >
              {/* Item Icon/Image */}
              <div className="text-6xl text-center mb-4">{item.image}</div>
              
              {/* Item Details */}
              <h3 className="text-xl font-bold mb-2">{item.name}</h3>
              <p className="text-gray-600 text-sm">{item.location}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Three Step Process Section */}
      <div className="bg-white py-12">
        <div className="container mx-auto px-4">
          {/* Section Description */}
          <p className="text-center text-gray-700 mb-8">
            A simple three-step process to help you find your lost items or report
            <br />
            something you've found.
          </p>

          {/* Process Steps Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            
            {/* Step 1: Search for Items */}
            <div className="bg-primary text-white rounded-lg p-8 text-center">
              <div className="bg-blue-500 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <Search className="w-8 h-8" />
              </div>
              <h3 className="text-2xl font-bold mb-4">Search for Items</h3>
              <p className="text-sm">
                Browse our listings or use the search bar to find your lost belongings quickly and easily.
              </p>
            </div>

            {/* Step 2: Report Lost Items */}
            <div className="bg-primary text-white rounded-lg p-8 text-center">
              <div className="bg-blue-500 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-3xl">📝</span>
              </div>
              <h3 className="text-2xl font-bold mb-4">Report Lost Items</h3>
              <p className="text-sm">
                Can't find your item? Submit a detailed report so others can help you look for it.
              </p>
            </div>

            {/* Step 3: Claim Your Item */}
            <div className="bg-primary text-white rounded-lg p-8 text-center">
              <div className="bg-blue-500 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-3xl">🛡️</span>
              </div>
              <h3 className="text-2xl font-bold mb-4">Claim Your Item</h3>
              <p className="text-sm">
                Once you find your item, follow the secure process to verify ownership and claim it back.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Call to Action Section */}
      <div className="bg-primary text-white py-16">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl font-bold mb-4">
            Ready to Find Your Lost Item or Report a Find?
          </h2>
          <p className="mb-8">
            Join the OUSL community in keeping our campus a place where lost items find their way home.
          </p>
          
          {/* CTA Buttons */}
          <div className="flex gap-4 justify-center flex-wrap">
            <button 
              onClick={() => navigate('/search')}
              className="bg-blue-500 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-600 transition"
            >
              Start Searching
            </button>
            <button 
              onClick={() => navigate('/submit-found')}
              className="bg-white text-primary px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition"
            >
              Report an Item
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;