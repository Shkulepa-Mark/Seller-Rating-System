import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import Alert from '../components/Alert';
import FormField from '../components/FormField';
import { ROUTES } from '../constants/routes';
import { useAuth } from '../context/AuthContext';
import { useSubmit } from '../hooks/useSubmit';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [values, setValues] = useState({ email: '', password: '' });

  const redirectTo = location.state?.from?.pathname ?? ROUTES.home;
  const { submit, pending, error } = useSubmit(login);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setValues((previous) => ({ ...previous, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const user = await submit(values);
    if (user) {
      navigate(redirectTo, { replace: true });
    }
  };

  return (
    <section className="auth-page">
      <form className="card form" onSubmit={handleSubmit}>
        <h1 className="card__title">Welcome back</h1>
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
          autoComplete="current-password"
          required
          value={values.password}
          onChange={handleChange}
        />
        <Alert>{error?.message}</Alert>
        <button type="submit" className="btn" disabled={pending}>
          {pending ? 'Signing in…' : 'Log in'}
        </button>
        <div className="form__links">
          <Link to={ROUTES.forgotPassword}>Forgot password?</Link>
          <Link to={ROUTES.register}>Create an account</Link>
        </div>
      </form>
    </section>
  );
}
