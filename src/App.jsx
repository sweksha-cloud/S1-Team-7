import { useState } from "react";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import DriverDashboard from "./pages/DriverDashboard";

function App() {
  const [activePage, setActivePage] = useState("home");

  const handleSignOut = () => {
    localStorage.removeItem("uniride_session"); 
    setActivePage("home"); 
  };

  if (activePage === "login") {
    return (
      <Login 
        onHomeClick={() => setActivePage("home")} 
        onLoginSuccess={() => setActivePage("driver")} 
      />
    );
  }

  if (activePage === "signup") {
    return <Signup onHomeClick={() => setActivePage("home")} />;
  }

  if (activePage === "driver") {
    return (
    <div className="login-page" style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
      <nav className="navbar">
        <h1 className="logo" onClick={() => setActivePage("home")} style={{cursor: 'pointer'}}>
          UniRide
        </h1>
        <div className="nav-links">
           <button onClick={handleSignOut} className="sign-out-btn">Sign Out</button>
        </div>
      </nav>
      
      {}
      <div style={{ 
        flex: 1, 
        display: "flex", 
        justifyContent: "center", 
        alignItems: "flex-start", 
        paddingTop: "50px" 
      }}>
        <DriverDashboard />
      </div>
    </div>
  );
}

  return (
    <Home
      onLoginClick={() => setActivePage("login")}
      onSignupClick={() => setActivePage("signup")}
    />
  );
}

export default App;