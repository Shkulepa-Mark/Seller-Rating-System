import { useState } from 'react';
import { Link } from 'react-router-dom';
import { authApi } from '../api/auth';
import Alert from '../components/Alert';
import FormField from '../components/FormField';
import { ROUTES } from '../constants/routes';
import { useSubmit } from '../hooks/useSubmit';

const INITIAL_VALUES = { firstName: '', lastName: '', email: '', password: '' };

export default function RegisterPage() {
  const [values, setValues] = useState(INITIAL_VALUES);
  const [registered, setRegistered] = useState(false);
  const { submit, pending, error } = useSubmit(authApi.register);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setValues((previous) => ({ ...previous, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const response = await submit(values);
    if (response !== undefined) {
      setRegistered(true);
    }
  };

  if (registered) {
    return (
      <section className="auth-page">
        <div className="card form">
          <h1 className="card__title">Check your inbox</h1>
          <p className="muted">
            We sent a confirmation link to <strong>{values.email}</strong>. It stays valid for 24 hours.
            Once the email is confirmed and an administrator approves your profile, you can log in.
          </p>
          <Link to={ROUTES.login} className="btn">
            Go to login
          </Link>
        </div>
      </section>
    );
  }

  return (
    <section className="auth-page">
      <form className="card form" onSubmit={handleSubmit}>
        <h1 className="card__title">Create seller profile</h1>
        <div className="form__row">
          <FormField
            label="First name"
            name="firstName"
            autoComplete="given-name"
            required
            value={values.firstName}
            onChange={handleChange}
          />
          <FormField
            label="Last name"
            name="lastName"
            autoComplete="family-name"
            required
            value={values.lastName}
            onChange={handleChange}
          />
        </div>
        <FormField
          label="Email"
          name="email"
          type="email"
          autoComplete="email"
          required
          value={values.email}
          onChange={handleChange}
        />
        <FormField
          label="Password"
          name="password"
          type="password"
          autoComplete="new-password"
          minLength={8}
          maxLength={50}
          required
          value={values.password}
          onChange={handleChange}
        />
        <Alert>{error?.message}</Alert>
        <button type="submit" className="btn" disabled={pending}>
          {pending ? 'Creating…' : 'Sign up'}
        </button>
        <div className="form__links">
          <Link to={ROUTES.login}>Already have an account?</Link>
        </div>
      </form>
    </section>
  );
}
