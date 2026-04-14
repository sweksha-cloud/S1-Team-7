import { useState } from "react";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";

function App() {
  const [activePage, setActivePage] = useState("home");

  if (activePage === "login") {
    return <Login onHomeClick={() => setActivePage("home")} />;
  }

  if (activePage === "signup") {
    return <Signup onHomeClick={() => setActivePage("home")} />;
  }

  return (
    <Home
      onLoginClick={() => setActivePage("login")}
      onSignupClick={() => setActivePage("signup")}
    />
  );
}

export default App;