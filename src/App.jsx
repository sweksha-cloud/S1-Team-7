import { useState } from "react";
import Home from "./pages/Home";
import Signup from "./pages/Signup";

function App() {
  const [activePage, setActivePage] = useState("home");

  if (activePage === "signup") {
    return <Signup onHomeClick={() => setActivePage("home")} />;
  }

  return <Home onSignupClick={() => setActivePage("signup")} />;
}

export default App;