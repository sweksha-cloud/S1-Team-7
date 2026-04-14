import { useState } from "react";

export default function DriverDashboard() {
  const [currentUser] = useState(() => {
    const session = localStorage.getItem("uniride_session");
    return session ? JSON.parse(session) : null;
  });

  const [vehicles, setVehicles] = useState(() => {
    const all = JSON.parse(localStorage.getItem("unirideVehicles") || "[]");
    return all.filter(v => v.ownerId === currentUser?.email);
  });

  const deleteVehicle = (id) => {
    const all = JSON.parse(localStorage.getItem("unirideVehicles") || "[]");
    const filtered = all.filter(v => v.id !== id);
    localStorage.setItem("unirideVehicles", JSON.stringify(filtered));
    setVehicles(filtered.filter(v => v.ownerId === currentUser.email));
  };

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
        <button className="login-submit" style={{ backgroundColor: "#2ecc71", margin: 0 }} onClick={() => alert("Coming Soon: Create a Ride")}>
          ➕ Create a Ride
        </button>
        <button className="login-submit" style={{ backgroundColor: "#3498db", margin: 0 }} onClick={() => alert("Coming Soon: View Requests")}>
          📩 Passenger Requests
        </button>
        <button className="login-submit" style={{ backgroundColor: "#9b59b6", margin: 0 }} onClick={() => alert("Coming Soon: Earnings")}>
          💰 My Earnings
        </button>
        <button className="login-submit" style={{ backgroundColor: "#95a5a6", margin: 0 }} onClick={() => alert("Coming Soon: Settings")}>
          ⚙️ Settings
        </button>
      </div>

      {}
      <div className="login-shell" style={{ 
        width: "100%", 
        maxWidth: "500px", 
        textAlign: "left",
        margin: "0 auto" 
      }}>
        <h3 style={{ textAlign: "center", marginBottom: "20px" }}>My Registered Vehicles</h3>
        
        {vehicles.length === 0 ? (
          <p style={{ textAlign: "center", color: "#666" }}>No vehicles added yet.</p>
        ) : (
          vehicles.map(v => (
            <div key={v.id} style={{ 
              display: "flex", 
              justifyContent: "space-between", 
              padding: "15px 0", 
              borderBottom: "1px solid #eee",
              alignItems: "center" 
            }}>
              <div>
                <strong>{v.color} {v.make}</strong><br />
                <small style={{ color: "#888" }}>Plate: {v.plate}</small>
              </div>
              <div style={{ display: "flex", gap: "10px" }}>
                <button onClick={() => alert("Edit Coming Soon")} style={{ background: "none", border: "none", color: "#3498db", cursor: "pointer" }}>Edit</button>
                <button onClick={() => deleteVehicle(v.id)} style={{ background: "none", border: "none", color: "#e74c3c", cursor: "pointer" }}>Delete</button>
              </div>
            </div>
          ))
        )}

        <button 
          className="login-submit" 
          style={{ marginTop: "20px", background: "#34495e", width: "100%" }}
          onClick={() => alert("Add Vehicle Form Coming Soon")}
        >
          Add New Vehicle
        </button>
      </div>
    </div>
  );
}