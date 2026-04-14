import { useMemo, useState } from "react";
import "../css/Home.css";
import "../css/Signup.css";

const STORAGE_KEY = "unirideUsers";

const initialForm = {
	firstName: "",
	lastName: "",
	email: "",
	sjsuId: "",
	gender: "",
	password: "",
	roles: [],
};

export default function Signup({ onHomeClick }) {
	const [form, setForm] = useState(initialForm);
	const [errors, setErrors] = useState({});
	const [successMessage, setSuccessMessage] = useState("");

	function handleLogoKeyDown(event) {
		if (event.key === "Enter" || event.key === " ") {
			event.preventDefault();
			onHomeClick();
		}
	}

	const passwordStrength = useMemo(() => {
		const length = form.password.length;

		if (length === 0) {
			return "";
		}

		if (length < 8) {
			return "Weak";
		}

		if (length < 12) {
			return "Medium";
		}

		return "Strong";
	}, [form.password]);

	function handleChange(event) {
		const { name, value, type, checked } = event.target;

		if (type === "checkbox" && name === "roles") {
			setForm((prev) => {
				const exists = prev.roles.includes(value);
				const roles = checked
					? [...prev.roles, value]
					: prev.roles.filter((role) => role !== value);

				return exists === checked ? prev : { ...prev, roles };
			});
			setErrors((prev) => ({ ...prev, roles: "" }));
			setSuccessMessage("");
			return;
		}

		setForm((prev) => ({ ...prev, [name]: value }));
		setErrors((prev) => ({ ...prev, [name]: "" }));
		setSuccessMessage("");
	}

	function validate(currentForm) {
		const nextErrors = {};
		const normalizedEmail = currentForm.email.trim().toLowerCase();
		const normalizedFirstName = currentForm.firstName.trim();
		const normalizedLastName = currentForm.lastName.trim();

		if (!normalizedFirstName) {
			nextErrors.firstName = "First name is required.";
		}

		if (!normalizedLastName) {
			nextErrors.lastName = "Last name is required.";
		}

		if (!normalizedEmail) {
			nextErrors.email = "Email is required.";
		} else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(normalizedEmail)) {
			nextErrors.email = "Enter a valid email address.";
		}

		if (!currentForm.sjsuId.trim()) {
			nextErrors.sjsuId = "SJSU ID is required.";
		} else if (!/^\d{9}$/.test(currentForm.sjsuId.trim())) {
			nextErrors.sjsuId = "SJSU ID must be 9 digits.";
		}

		if (!currentForm.gender) {
			nextErrors.gender = "Please select a gender.";
		}

		if (!currentForm.password) {
			nextErrors.password = "Password is required.";
		} else if (currentForm.password.length < 8) {
			nextErrors.password = "Password must be at least 8 characters.";
		}

		if (!currentForm.roles.length) {
			nextErrors.roles = "Please choose at least one registration type.";
		}

		const savedUsers = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");
		const hasDuplicate = savedUsers.some(
			(user) => user.email?.toLowerCase() === normalizedEmail,
		);

		if (hasDuplicate) {
			nextErrors.email = "This email is already registered.";
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
		const newUser = {
			firstName: form.firstName.trim(),
			lastName: form.lastName.trim(),
			email: normalizedEmail,
			sjsuId: form.sjsuId.trim(),
			gender: form.gender,
			password: form.password,
			roles: form.roles,
			createdAt: new Date().toISOString(),
		};

		const savedUsers = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");
		localStorage.setItem(STORAGE_KEY, JSON.stringify([...savedUsers, newUser]));

		setForm(initialForm);
		setErrors({});
		setSuccessMessage("Registration complete. Your account has been created.");
	}

	return (
		<div className="signup-page">
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

			<section className="signup-hero">
				<div className="signup-shell">
					<header className="signup-header">
						<h2>Create Your UniRide Account</h2>
					</header>

					<form className="signup-form" onSubmit={handleSubmit} noValidate>
					<div className="name-row">
						<div className="name-field">
							<label htmlFor="firstName">First name</label>
							<input
								id="firstName"
								name="firstName"
								type="text"
								value={form.firstName}
								onChange={handleChange}
								placeholder="First name"
							/>
							{errors.firstName && <p className="field-error">{errors.firstName}</p>}
						</div>

						<div className="name-field">
							<label htmlFor="lastName">Last name</label>
							<input
								id="lastName"
								name="lastName"
								type="text"
								value={form.lastName}
								onChange={handleChange}
								placeholder="Last name"
							/>
							{errors.lastName && <p className="field-error">{errors.lastName}</p>}
						</div>
					</div>

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

					<label htmlFor="sjsuId">SJSU ID</label>
					<input
						id="sjsuId"
						name="sjsuId"
						type="text"
						inputMode="numeric"
						maxLength={9}
						value={form.sjsuId}
						onChange={handleChange}
						placeholder="9-digit SJSU ID"
					/>
					{errors.sjsuId && <p className="field-error">{errors.sjsuId}</p>}

					<label htmlFor="gender">Gender</label>
					<select
						id="gender"
						name="gender"
						value={form.gender}
						onChange={handleChange}
					>
						<option value="">Select gender</option>
						<option value="male">Male</option>
                        <option value="female">Female</option>
						<option value="non-binary">Non-binary</option>
						<option value="prefer-not-to-say">Prefer not to say</option>
					</select>
					{errors.gender && <p className="field-error">{errors.gender}</p>}

					<label htmlFor="password">Password</label>
					<input
						id="password"
						name="password"
						type="password"
						value={form.password}
						onChange={handleChange}
						placeholder="At least 8 characters"
					/>
					{passwordStrength && (
						<p className="password-strength">Strength: {passwordStrength}</p>
					)}
					{errors.password && <p className="field-error">{errors.password}</p>}

					<fieldset className="role-selector" aria-label="Register as roles">
						<legend>Register as (select one or more)</legend>
						<label>
							<input
								type="checkbox"
								name="roles"
								value="passenger"
								checked={form.roles.includes("passenger")}
								onChange={handleChange}
							/>
							Passenger
						</label>
						<label>
							<input
								type="checkbox"
								name="roles"
								value="driver"
								checked={form.roles.includes("driver")}
								onChange={handleChange}
							/>
							Driver
						</label>
					</fieldset>
					{errors.roles && <p className="field-error">{errors.roles}</p>}

					<button className="signup-submit" type="submit">
						Create account
					</button>

					{successMessage && <p className="signup-success">{successMessage}</p>}
					</form>
				</div>
			</section>

			<footer className="footer">
				<p>© 2026 UniRide | SJSU Carpool System</p>
			</footer>
		</div>
	);
}
