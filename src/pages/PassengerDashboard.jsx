import { useState } from "react";

export default function UserDashboard() {
  const [searchQuery, setSearchQuery] = useState("");

  return (
    <div style={{ 
      display: "flex", 
      flexDirection: "column", 
      alignItems: "center", 
      width: "100%",
      padding: "20px",
      boxSizing: "border-box"
    }}>
      
      {}
      <div style={{ 
        display: "grid", 
        gridTemplateColumns: "1fr 1fr", 
        gap: "15px", 
        width: "100%", 
        maxWidth: "500px", 
        marginBottom: "30px" 
      }}>
        <button className="login-submit" style={{ backgroundColor: "#3498db", margin: 0 }} onClick={() => alert("Searching for rides...")}>
          🔍 Find a Ride
        </button>
        <button className="login-submit" style={{ backgroundColor: "#9b59b6", margin: 0 }} onClick={() => alert("Viewing my bookings...")}>
          📅 My Bookings
        </button>
      </div>

      {}
      <div className="login-shell" style={{ width: "100%", maxWidth: "500px", textAlign: "left" }}>
        <h3 style={{ textAlign: "center", marginBottom: "20px" }}>Available Rides</h3>
        
        <input 
          type="text" 
          placeholder="Search by destination (e.g. SJSU North Garage)" 
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          style={{ width: "100%", padding: "10px", marginBottom: "20px", borderRadius: "4px", border: "1px solid #ccc" }}
        />

        <div style={{ textAlign: "center", color: "#666", padding: "20px" }}>
          <p>No active rides found for your search.</p>
        </div>
      </div>
    </div>
  );
}