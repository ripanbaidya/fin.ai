import { Routes, Route, Navigate } from "react-router-dom";
import { ROUTES } from "./routePaths";
import PrivateRoute from "../shared/components/guards/PrivateRoute";
import Layout from "../shared/components/layout/Layout";

import LoginPage from "../features/auth/pages/LoginPage";
import SignupPage from "../features/auth/pages/SignupPage";
import HomePage from "../features/home/pages/HomePage";
import DashboardPage from "../features/dashboard/pages/DashboardPage";
import TransactionsPage from "../features/transactions/pages/TransactionsPage";
import CategoriesPage from "../features/categories/pages/CategoriesPage";
import PaymentModesPage from "../features/payment-modes/pages/PaymentModesPage";
import BudgetsPage from "../features/budgets/pages/BudgetsPage";
import SavingsPage from "../features/savings/pages/SavingsPage";
import ChatPage from "../features/chat/pages/ChatPage";
import ProfilePage from "../features/profile/pages/ProfilePage";
import AboutPage from "../features/about/pages/AboutPage";
import RecurringPage from "../features/recurring/pages/RecurringPage";
import AdminPage from "../features/admin/pages/AdminPage";

const AppRoutes: React.FC = () => (
  <Routes>
    {/* Public routes */}
    <Route path={ROUTES.home} element={<HomePage />} />
    <Route path={ROUTES.login} element={<LoginPage />} />
    <Route path={ROUTES.signup} element={<SignupPage />} />

    {/* Protected routes */}
    <Route
      element={
        <PrivateRoute>
          <Layout />
        </PrivateRoute>
      }
    >
      <Route path={ROUTES.dashboard} element={<DashboardPage />} />
      <Route path={ROUTES.transactions} element={<TransactionsPage />} />
      <Route path={ROUTES.recurring} element={<RecurringPage />} />
      <Route path={ROUTES.categories} element={<CategoriesPage />} />
      <Route path={ROUTES.paymentModes} element={<PaymentModesPage />} />
      <Route path={ROUTES.budgets} element={<BudgetsPage />} />
      <Route path={ROUTES.savings} element={<SavingsPage />} />
      <Route path={ROUTES.chat} element={<ChatPage />} />
      <Route path={ROUTES.profile} element={<ProfilePage />} />
      <Route path={ROUTES.about} element={<AboutPage />} />
      <Route path={ROUTES.admin} element={<AdminPage />} />
    </Route>

    {/* Catch-all */}
    <Route path="*" element={<Navigate to={ROUTES.home} replace />} />
  </Routes>
);

export default AppRoutes;
