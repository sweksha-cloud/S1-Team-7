import React from "react";
import "../css/Home.css";

export default function Home({ onLoginClick, onSignupClick }) {
  return (
    <div className="home-container">
      <nav className="navbar">
        <h1 className="logo">Uni<span className="highlight">Ride</span></h1>
        <div className="nav-links">
          <button className="nav-btn-secondary" onClick={onLoginClick}>Log In</button>
          <button className="nav-btn-primary" onClick={onSignupClick}>Sign Up</button>
        </div>
      </nav>

      <main className="hero-section">
        <div className="hero-wrapper">

          <div className="hero-content">
            <div className="campus-tag">SJSU Campus Network</div>
            <h1>Commute smarter to <span className="highlight">SJSU</span></h1>
            <p>
              The safe, reliable way for Spartans to share rides, save money, and 
              reduce campus traffic — all in one place.
            </p>
            <div className="hero-btns">
              <button className="main-btn-primary" onClick={onSignupClick}>Get Started</button>
              <button className="main-btn-outline" onClick={onLoginClick}>View Rides →</button>
            </div>
          </div>

          <div className="search-card">
            <h4 className="card-label">Find a Ride Now</h4>
            <div className="input-group">
              <label>FROM</label>
              <input type="text" placeholder="Your pickup location" />
            </div>
            <div className="input-group">
              <label>TO</label>
              <input type="text" placeholder="San Jose State University" />
            </div>
            <div className="input-row">
              <div className="input-group">
                <label>DATE</label>
                <input type="date" />
              </div>
              <div className="input-group" style={{ width: '80px' }}>
                <label>SEATS</label>
                <select>
                  <option>1</option>
                  <option>2</option>
                  <option>3</option>
                </select>
              </div>
            </div>
            <button className="search-submit">Search rides →</button>
          </div>
        </div>
      </main>

      {}
      <section className="stats-bar">
        <div className="stat-item"><h3>2k+</h3><p>SPARTAN RIDERS</p></div>
        <div className="stat-item"><h3>320</h3><p>RIDES THIS WEEK</p></div>
        <div className="stat-item"><h3>4.9</h3><p>AVG RATING</p></div>
        <div className="stat-item"><h3 className="highlight">$18</h3><p>AVG TRIP SAVED</p></div>
      </section>

      {}
      <section className="features-section">
        <p className="section-subtitle">Why UniRide</p>
        <h2 className="section-title">Built for Spartans,<br/>by Spartans</h2>
        
        <div className="features-grid">
          <div className="feature-card">
            <div className="icon-box">🛡️</div>
            <h3>Verified students only</h3>
            <p>Only users with @sjsu.edu emails can join — so every rider and driver is a trusted Spartan.</p>
          </div>
          <div className="feature-card">
            <div className="icon-box">💰</div>
            <h3>Split the costs</h3>
            <p>Share gas and parking costs with students heading the same way. Save up to $20 per trip.</p>
          </div>
          <div className="feature-card">
            <div className="icon-box">🌎</div>
            <h3>Greener campus</h3>
            <p>Fewer solo cars means less congestion at North and South garages.</p>
          </div>
        </div>
      </section>
      
      <footer className="home-footer">
        <p>© 2026 UniRide · Built for Spartans</p>
        <div className="footer-links">
          <span>Privacy</span>
          <span>Terms</span>
          <span>Contact</span>
        </div>
      </footer>
    </div>
  );
}