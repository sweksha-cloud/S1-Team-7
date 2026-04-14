import { useState } from "react";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import DriverDashboard from "./pages/DriverDashboard";
import PassengerDashboard from "./pages/PassengerDashboard";

function App() {
  const [activePage, setActivePage] = useState("home");

  const handleSignOut = () => {
    localStorage.removeItem("uniride_session");
    setActivePage("home");
  };

  const handleLoginSuccess = (role) => {
    if (role === "driver") {
      setActivePage("driver");
    } else {
      setActivePage("passenger");
    }
  };

  const handleDeleteAccount = () => {
    const session = JSON.parse(localStorage.getItem("uniride_session"));
    if (!session) return;

    if (window.confirm("Are you sure you want to delete your account?")) {
      const allUsers = JSON.parse(localStorage.getItem("unirideUsers") || "[]");
      const updatedUsers = allUsers.filter(user => user.email !== session.email);
      
      localStorage.setItem("unirideUsers", JSON.stringify(updatedUsers));
      localStorage.removeItem("uniride_session");
      setActivePage("home");
    }
  };

  if (activePage === "login") {
    return (
      <Login 
        onHomeClick={() => setActivePage("home")} 
        onLoginSuccess={handleLoginSuccess} 
      />
    );
  }

  if (activePage === "signup") {
    return <Signup onHomeClick={() => setActivePage("home")} />;
  }

  if (activePage === "passenger") {
    return (
      <div className="login-page" style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
        <nav className="navbar">
          <h1 className="logo" onClick={() => setActivePage("home")} style={{ cursor: "pointer" }}>UniRide</h1>
          <div className="nav-links">
            <button onClick={handleSignOut} className="sign-out-btn">Sign Out</button>
          </div>
        </nav>
        <div style={{ flex: 1, display: "flex", justifyContent: "center", alignItems: "flex-start", paddingTop: "50px" }}>
          <PassengerDashboard onDeleteAccount={handleDeleteAccount} />
        </div>
      </div>
    );
  }

  if (activePage === "driver") {
    return (
      <div className="login-page" style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
        <nav className="navbar">
          <h1 className="logo" onClick={() => setActivePage("home")} style={{ cursor: "pointer" }}>
            UniRide
          </h1>
          <div className="nav-links">
            <button onClick={handleSignOut} className="sign-out-btn">Sign Out</button>
          </div>
        </nav>
        <div style={{ 
          flex: 1, 
          display: "flex", 
          justifyContent: "center", 
          alignItems: "flex-start", 
          paddingTop: "50px" 
        }}>
          <DriverDashboard onDeleteAccount={handleDeleteAccount} />
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