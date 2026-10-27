import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { authApi } from '../api/auth';
import Alert from '../components/Alert';
import FormField from '../components/FormField';
import { ROUTES } from '../constants/routes';
import { useSubmit } from '../hooks/useSubmit';

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [values, setValues] = useState({ code: searchParams.get('code') ?? '', newPassword: '' });
  const { submit, pending, error } = useSubmit(authApi.resetPassword);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setValues((previous) => ({ ...previous, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const response = await submit(values);
    if (response !== undefined) {
      navigate(ROUTES.login, { replace: true });
    }
  };

  return (
    <section className="auth-page">
      <form className="card form" onSubmit={handleSubmit}>
        <h1 className="card__title">Choose a new password</h1>
        <FormField label="Reset code" name="code" required value={values.code} onChange={handleChange} />
        <FormField
          label="New password"
          name="newPassword"
          type="password"
          autoComplete="new-password"
          minLength={8}
          required
          value={values.newPassword}
          onChange={handleChange}
        />
        <Alert>{error?.message}</Alert>
        <button type="submit" className="btn" disabled={pending}>
          {pending ? 'Saving…' : 'Update password'}
        </button>
        <div className="form__links">
          <Link to={ROUTES.login}>Back to login</Link>
        </div>
      </form>
    </section>
  );
}
