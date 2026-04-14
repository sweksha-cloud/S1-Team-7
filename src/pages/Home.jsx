import React from "react";
import "../css/Home.css";

export default function Home({ onLoginClick, onSignupClick }) {
  return (
    <div className="home-container">
      <nav className="navbar">
        <h1 className="logo">UniRide</h1>
        <div className="nav-links">
          <button className="nav-btn-secondary" onClick={onLoginClick}>Log In</button>
          <button className="nav-btn-primary" onClick={onSignupClick}>Sign Up</button>
        </div>
      </nav>

      <main className="hero-section">
        <div className="hero-content">
          <h1>Commute Smarter to <span className="highlight">SJSU</span></h1>
          <p>
            The safe, reliable way for Spartans to share rides, save money, and 
            reduce campus traffic.
          </p>
          <div className="hero-btns">
            <button className="main-btn-primary" onClick={onSignupClick}>Get Started</button>
            <button className="main-btn-outline" onClick={onLoginClick}>View Rides</button>
          </div>
        </div>
      </main>

      <section className="features-grid">
        <div className="feature-card">
          <div className="icon">🛡️</div>
          <h3>Verified Students</h3>
          <p>Only users with @sjsu.edu emails can join our network.</p>
        </div>
        <div className="feature-card">
          <div className="icon">💰</div>
          <h3>Save Money</h3>
          <p>Split gas and parking costs with fellow students heading your way.</p>
        </div>
        <div className="feature-card">
          <div className="icon">🌎</div>
          <h3>Eco-Friendly</h3>
          <p>Reduce the number of cars in the North and South garages.</p>
        </div>
      </section>
      
      <footer className="home-footer">
        <p>© 2026 UniRide | Built for Spartans</p>
      </footer>
    </div>
  );
}