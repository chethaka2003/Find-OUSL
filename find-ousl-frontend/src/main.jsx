import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'

/**
 * Main Entry Point
 * Renders the React app into the DOM
 * Developer: PrabashVijayanga
 * Project: OUSL Lost & Found System
 */

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)