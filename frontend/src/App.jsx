import { Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import { ROLES } from './constants/roles';
import { ROUTES } from './constants/routes';
import AdminPage from './pages/AdminPage';
import ConfirmEmailPage from './pages/ConfirmEmailPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import GameObjectsPage from './pages/GameObjectsPage';
import LoginPage from './pages/LoginPage';
import NotFoundPage from './pages/NotFoundPage';
import RegisterPage from './pages/RegisterPage';
import ResetPasswordPage from './pages/ResetPasswordPage';
import SellerPage from './pages/SellerPage';
import SuggestSellerPage from './pages/SuggestSellerPage';
import TopSellersPage from './pages/TopSellersPage';

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<TopSellersPage />} />
        <Route path={ROUTES.seller(':id')} element={<SellerPage />} />
        <Route path={ROUTES.suggestSeller} element={<SuggestSellerPage />} />
        <Route path={ROUTES.objects} element={<GameObjectsPage />} />
        <Route path={ROUTES.login} element={<LoginPage />} />
        <Route path={ROUTES.register} element={<RegisterPage />} />
        <Route path={ROUTES.confirmEmail} element={<ConfirmEmailPage />} />
        <Route path={ROUTES.forgotPassword} element={<ForgotPasswordPage />} />
        <Route path={ROUTES.resetPassword} element={<ResetPasswordPage />} />
        <Route element={<ProtectedRoute roles={[ROLES.admin]} />}>
          <Route path={ROUTES.admin} element={<AdminPage />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
