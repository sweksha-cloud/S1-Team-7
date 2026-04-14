import { useState } from "react";
import "../css/Home.css";
import "../css/Login.css";

const STORAGE_KEY = "unirideUsers";

const initialForm = {
  email: "",
  password: "",
};

export default function Login({ onHomeClick }) {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});
  const [statusMessage, setStatusMessage] = useState("");

  function handleLogoKeyDown(event) {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      onHomeClick();
    }
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: "" }));
    setStatusMessage("");
  }

  function validate(currentForm) {
    const nextErrors = {};
    const normalizedEmail = currentForm.email.trim().toLowerCase();

    if (!normalizedEmail) {
      nextErrors.email = "Email is required.";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(normalizedEmail)) {
      nextErrors.email = "Enter a valid email address.";
    }

    if (!currentForm.password) {
      nextErrors.password = "Password is required.";
    }

    return nextErrors;
  }

  function handleSubmit(event) {
    event.preventDefault();

    const nextErrors = validate(form);
    if (Object.keys(nextErrors).length > 0) {
      setErrors(nextErrors);
      return;
    }

    const normalizedEmail = form.email.trim().toLowerCase();
    const users = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");

    const matchedUser = users.find(
      (user) =>
        user.email?.toLowerCase() === normalizedEmail &&
        user.password === form.password,
    );

    if (!matchedUser) {
      setErrors({
        email: "",
        password: "Incorrect email or password.",
      });
      return;
    }

    setForm(initialForm);
    setErrors({});
    setStatusMessage(`Welcome back, ${matchedUser.firstName || "student"}.`);
  }

  return (
    <div className="login-page">
      <nav className="navbar">
        <h1
          className="logo"
          onClick={onHomeClick}
          onKeyDown={handleLogoKeyDown}
          role="button"
          tabIndex={0}
        >
          UniRide
        </h1>
        <div className="nav-links" />
      </nav>

      <section className="login-hero">
        <div className="login-shell">
          <header className="login-header">
            <h2>Log In to UniRide</h2>
          </header>

          <form className="login-form" onSubmit={handleSubmit} noValidate>
            <label htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              placeholder="name@sjsu.edu"
            />
            {errors.email && <p className="field-error">{errors.email}</p>}

            <label htmlFor="password">Password</label>
            <input
              id="password"
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Enter your password"
            />
            {errors.password && <p className="field-error">{errors.password}</p>}

            <button className="login-submit" type="submit">
              Log in
            </button>

            {statusMessage && <p className="login-success">{statusMessage}</p>}
          </form>
        </div>
      </section>

      <footer className="footer">
        <p>© 2026 UniRide | SJSU Carpool System</p>
      </footer>
    </div>
  );
}
