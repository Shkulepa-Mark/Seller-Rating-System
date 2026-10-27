import { Link } from 'react-router-dom';
import { ROUTES } from '../constants/routes';

export default function NotFoundPage() {
  return (
    <section className="not-found">
      <p className="not-found__code">404</p>
      <h1 className="page-header__title">Page not found</h1>
      <p className="muted">The page you are looking for does not exist.</p>
      <Link to={ROUTES.home} className="btn">
        Back to top sellers
      </Link>
    </section>
  );
}
