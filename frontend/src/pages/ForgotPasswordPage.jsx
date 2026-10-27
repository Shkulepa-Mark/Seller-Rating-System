import { useState } from 'react';
import { Link } from 'react-router-dom';
import { authApi } from '../api/auth';
import Alert from '../components/Alert';
import FormField from '../components/FormField';
import { ROUTES } from '../constants/routes';
import { useSubmit } from '../hooks/useSubmit';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const { submit, pending, error } = useSubmit(authApi.forgotPassword);

  const handleSubmit = async (event) => {
    event.preventDefault();
    const response = await submit(email);
    if (response !== undefined) {
      setSent(true);
    }
  };

  return (
    <section className="auth-page">
      <form className="card form" onSubmit={handleSubmit}>
        <h1 className="card__title">Reset password</h1>
        <p className="muted">Enter your email and we will send you a reset code.</p>
        <FormField
          label="Email"
          type="email"
          autoComplete="email"
          required
          value={email}
          onChange={(event) => setEmail(event.target.value)}
        />
        <Alert>{error?.message}</Alert>
        <Alert variant="success">{sent ? 'If the email is registered, a reset code is on its way.' : ''}</Alert>
        <button type="submit" className="btn" disabled={pending}>
          {pending ? 'Sending…' : 'Send reset code'}
        </button>
        <div className="form__links">
          <Link to={ROUTES.resetPassword}>I already have a code</Link>
          <Link to={ROUTES.login}>Back to login</Link>
        </div>
      </form>
    </section>
  );
}
