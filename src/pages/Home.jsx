import React from "react";
import "../css/Home.css";

export default function Home({ onSignupClick }) {
  return (
    <div className="home">

      {/* Navbar */}
        <nav className="navbar">
        <h1 className="logo">UniRide</h1>
        <div className="nav-links">
          <button className="btn">Login</button>
          <button className="btn primary" onClick={onSignupClick}>Sign Up</button>
        </div>
      </nav>


      {/* Hero Section */}
      <section className="hero">
        <h2>Find Your Ride. Save Money. Ride Safe.</h2>
        <p>
          Connect with verified SJSU students for safe and affordable rides.
        </p>

        <div className="search-box">
          <input type="text" placeholder="From (Pickup location)" />
          <input type="text" placeholder="To (Destination)" />
          <input type="datetime-local" />
          <button className="btn primary">Search Rides</button>
        </div>
      </section>

      {/* Features Section */}
      <section className="features">
        <div className="feature-card">
          <h3>🔒 Student Verified</h3>
          <p>Only SJSU students can join for a safer community.</p>
        </div>

        <div className="feature-card">
          <h3>💸 Save Money</h3>
          <p>Split costs and reduce expensive commuting.</p>
        </div>

        <div className="feature-card">
          <h3>🚗 Easy Booking</h3>
          <p>Search, filter, and book rides in seconds.</p>
        </div>
      </section>

      {/* Example Rides Section */}
      <section className="rides">
        <h2>Available Rides</h2>

        <div className="ride-card">
          <p><strong>Driver:</strong> Sweksha</p>
          <p><strong>From:</strong> SJSU</p>
          <p><strong>To:</strong> San Francisco</p>
          <p><strong>Seats:</strong> 2 available</p>
          <button className="btn">View</button>
        </div>

        <div className="ride-card">
          <p><strong>Driver:</strong> Brian</p>
          <p><strong>From:</strong> Downtown SJ</p>
          <p><strong>To:</strong> Cupertino</p>
          <p><strong>Seats:</strong> 3 available</p>
          <button className="btn">View</button>
        </div>
      </section>

      {/* Footer */}
      <footer className="footer">
        <p>© 2026 UniRide | SJSU Carpool System</p>
      </footer>

    </div>
  );
}