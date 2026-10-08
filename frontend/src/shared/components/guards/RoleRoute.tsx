import { Navigate, Outlet } from "react-router-dom";
import { ROUTES } from "../../../routes/routePaths";
import { useAuth } from "../../hooks/useAuth";

type RoleRouteProps = {
  role: "admin" | "user";
};

const RoleRoute: React.FC<RoleRouteProps> = ({ role }) => {
  const { isAdmin } = useAuth();
  const hasRequiredRole = role === "admin" ? isAdmin : !isAdmin;

  if (hasRequiredRole) return <Outlet />;

  return (
    <Navigate
      to={isAdmin ? ROUTES.admin : ROUTES.dashboard}
      replace
    />
  );
};

export default RoleRoute;
