import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { ROUTES } from '../constants/routes';
import { useAuth } from '../context/AuthContext';

const navLinkClass = ({ isActive }) => (isActive ? 'nav__link nav__link--active' : 'nav__link');

export default function Layout() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate(ROUTES.home);
  };

  return (
    <div className="app-shell">
      <header className="header">
        <div className="container header__inner">
          <Link to={ROUTES.home} className="brand">
            <span className="brand__mark">★</span>
            SellerRank
          </Link>

          <nav className="nav" aria-label="Main navigation">
            <NavLink to={ROUTES.home} end className={navLinkClass}>
              Top sellers
            </NavLink>
            <NavLink to={ROUTES.suggestSeller} className={navLinkClass}>
              Add seller
            </NavLink>
            <NavLink to={ROUTES.objects} className={navLinkClass}>
              Game objects
            </NavLink>
            {isAdmin && (
              <NavLink to={ROUTES.admin} className={navLinkClass}>
                Moderation
              </NavLink>
            )}
          </nav>

          <div className="header__actions">
            {isAuthenticated ? (
              <>
                <span className="header__user">{user.email}</span>
                <button type="button" className="btn btn--ghost btn--small" onClick={handleLogout}>
                  Log out
                </button>
              </>
            ) : (
              <>
                <Link to={ROUTES.login} className="btn btn--ghost btn--small">
                  Log in
                </Link>
                <Link to={ROUTES.register} className="btn btn--small">
                  Sign up
                </Link>
              </>
            )}
          </div>
        </div>
      </header>

      <main className="container main">
        <Outlet />
      </main>

      <footer className="footer">
        <div className="container">Independent rating system for in-game item sellers</div>
      </footer>
    </div>
  );
}
