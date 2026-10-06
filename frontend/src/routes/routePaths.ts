/**
 * Centralized route path constants
 */

/* Public routes */
export const PUBLIC_ROUTES = {
  home: '/home',
  login: '/login',
  signup: '/signup',
} as const;

/* Protected routes */
export const PROTECTED_ROUTES = {
  dashboard: '/dashboard',
  transactions: '/transactions',
  recurring: '/recurring',
  categories: '/categories',
  paymentModes: '/payment-modes',
  budgets: '/budgets',
  savings: '/savings',
  chat: '/chat',
  profile: '/profile',
  about: '/about',
  admin: '/admin',
} as const;

/* Combined (convenience re-export) */
export const ROUTES = {
  ...PUBLIC_ROUTES,
  ...PROTECTED_ROUTES,
} as const;

/* Type helpers */
export type PublicRoute = typeof PUBLIC_ROUTES[keyof typeof PUBLIC_ROUTES];
export type ProtectedRoute = typeof PROTECTED_ROUTES[keyof typeof PROTECTED_ROUTES];
export type AppRoute = typeof ROUTES[keyof typeof ROUTES];