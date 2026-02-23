import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Upload, X } from 'lucide-react';
import axios from 'axios';

const ReportFoundItem = () => {
  const [formData, setFormData] = useState({
    category: '',
    description: '',
    foundLocation: '',
    foundDate: '',
    foundTime: '',
    currentLocation: '',
    holdingStatus: 'self', // self or office
    availability: '',
    contactMethod: 'email'
  });
  const [photos, setPhotos] = useState([]);
  const [previews, setPreviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const categories = [
    'Electronics', 'Bags & Backpacks', 'Books & Stationery', 
    'Clothing', 'Wallets & Purses', 'Keys', 'Jewelry', 'Other'
  ];

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handlePhotoUpload = (e) => {
    const files = Array.from(e.target.files);
    
    if (photos.length + files.length > 3) {
      setError('Maximum 3 photos allowed');
      return;
    }

    for (let file of files) {
      if (file.size > 5 * 1024 * 1024) {
        setError('Each photo must be less than 5MB');
        return;
      }
    }

    setPhotos([...photos, ...files]);
    
    files.forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviews(prev => [...prev, reader.result]);
      };
      reader.readAsDataURL(file);
    });
  };

  const removePhoto = (index) => {
    setPhotos(photos.filter((_, i) => i !== index));
    setPreviews(previews.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.description. length < 20) {
      setError('Description must be at least 20 characters');
      return;
    }

    setLoading(true);

    const formDataToSend = new FormData();
    Object.keys(formData).forEach(key => {
      formDataToSend.append(key, formData[key]);
    });
    photos.forEach(photo => {
      formDataToSend. append('photos', photo);
    });

    try {
      const response = await axios.post(
        'http://localhost:8080/api/found-items',
        formDataToSend,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      );

      alert(`Found item reported successfully! ${response.data.matchCount || 0} potential matches found.`);
      navigate('/my-items');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit report');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container mx-auto px-4 max-w-2xl">
        <h1 className="text-3xl font-bold mb-6">Report Found Item</h1>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6 space-y-6">
          
          {/* Category */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">Category *</label>
            <select
              name="category"
              value={formData.category}
              onChange={handleChange}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            >
              <option value="">Select a category</option>
              {categories.map(cat => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
          </div>

          {/* Description */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Description * (minimum 20 characters)
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus: outline-none focus:ring-2 focus:ring-blue-500"
              rows="4"
              placeholder="Provide detailed description..."
              required
              minLength="20"
            />
            <p className="text-sm text-gray-500 mt-1">
              {formData.description.length} / 20 characters
            </p>
          </div>

          {/* Found Location & Date */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">Found Location *</label>
            <input
              type="text"
              name="foundLocation"
              value={formData.foundLocation}
              onChange={handleChange}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus: outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="e.g., Cafeteria, near entrance"
              required
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-gray-700 font-semibold mb-2">Date Found *</label>
              <input
                type="date"
                name="foundDate"
                value={formData.foundDate}
                onChange={handleChange}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            <div>
              <label className="block text-gray-700 font-semibold mb-2">Time Found</label>
              <input
                type="time"
                name="foundTime"
                value={formData.foundTime}
                onChange={handleChange}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Current Holding Location (FR9) */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Current Holding Location *
            </label>
            <div className="space-y-3">
              <div>
                <label className="flex items-center">
                  <input
                    type="radio"
                    name="holdingStatus"
                    value="self"
                    checked={formData. holdingStatus === 'self'}
                    onChange={handleChange}
                    className="mr-2"
                  />
                  I have the item
                </label>
              </div>
              <div>
                <label className="flex items-center">
                  <input
                    type="radio"
                    name="holdingStatus"
                    value="office"
                    checked={formData.holdingStatus === 'office'}
                    onChange={handleChange}
                    className="mr-2"
                  />
                  Submitted to Lost & Found Office
                </label>
              </div>
            </div>
          </div>

          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Specific Location Details *
            </label>
            <input
              type="text"
              name="currentLocation"
              value={formData.currentLocation}
              onChange={handleChange}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="e. g., Room 204, Admin Building / Lost & Found Office"
              required
            />
          </div>

          {/* Availability & Contact (FR9) */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">Availability Times</label>
            <input
              type="text"
              name="availability"
              value={formData.availability}
              onChange={handleChange}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="e.g., Weekdays 9AM-5PM"
            />
          </div>

          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Preferred Contact Method *
            </label>
            <select
              name="contactMethod"
              value={formData.contactMethod}
              onChange={handleChange}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            >
              <option value="email">Email</option>
              <option value="phone">Phone</option>
              <option value="both">Both</option>
            </select>
          </div>

          {/* Photo Upload */}
          <div>
            <label className="block text-gray-700 font-semibold mb-2">
              Photos (0-3, max 5MB each)
            </label>
            
            {previews.length < 3 && (
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                <input
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={handlePhotoUpload}
                  className="hidden"
                  id="photo-upload"
                />
                <label htmlFor="photo-upload" className="cursor-pointer">
                  <Upload className="mx-auto text-gray-400 w-12 h-12 mb-2" />
                  <p className="text-gray-600">Click to upload photos</p>
                </label>
              </div>
            )}

            {previews.length > 0 && (
              <div className="grid grid-cols-3 gap-4 mt-4">
                {previews.map((preview, index) => (
                  <div key={index} className="relative">
                    <img
                      src={preview}
                      alt={`Preview ${index + 1}`}
                      className="w-full h-32 object-cover rounded-lg"
                    />
                    <button
                      type="button"
                      onClick={() => removePhoto(index)}
                      className="absolute top-1 right-1 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary text-white py-3 rounded-lg font-semibold hover:bg-blue-800 transition disabled:opacity-50"
          >
            {loading ?  'Submitting...' :  'Submit Found Item Report'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ReportFoundItem;