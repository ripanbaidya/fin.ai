import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "./store/authStore";
import AppRoutes from "./routes/AppRoutes";

const App: React.FC = () => {
  const navigate = useNavigate();
  const { clearAuth } = useAuthStore();

  useEffect(() => {
    const handler = () => {
      clearAuth();
      navigate("/login", { replace: true });
    };
    window.addEventListener("auth:unauthorized", handler);
    return () => window.removeEventListener("auth:unauthorized", handler);
  }, [clearAuth, navigate]);

  return <AppRoutes />;
};

export default App;
