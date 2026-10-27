import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { authApi } from '../api/auth';
import Alert from '../components/Alert';
import Spinner from '../components/Spinner';
import { ROUTES } from '../constants/routes';

export default function ConfirmEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [status, setStatus] = useState(token ? 'loading' : 'missing');
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (!token) {
      return undefined;
    }

    let cancelled = false;
    authApi
      .confirmEmail(token)
      .then((response) => {
        if (!cancelled) {
          setMessage(response);
          setStatus('success');
        }
      })
      .catch((error) => {
        if (!cancelled) {
          setMessage(error.message);
          setStatus('error');
        }
      });

    return () => {
      cancelled = true;
    };
  }, [token]);

  return (
    <section className="auth-page">
      <div className="card form">
        <h1 className="card__title">Email confirmation</h1>
        {status === 'loading' && <Spinner label="Confirming your email" />}
        {status === 'success' && (
          <>
            <Alert variant="success">{message}</Alert>
            <Link to={ROUTES.login} className="btn">
              Go to login
            </Link>
          </>
        )}
        {status === 'error' && <Alert>{message}</Alert>}
        {status === 'missing' && <Alert>The confirmation link is incomplete. Open the link from your email.</Alert>}
      </div>
    </section>
  );
}
