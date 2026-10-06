import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { authService } from "../authService";
import { AppError } from "../../../api/errorParser";
import { ROUTES } from "../../../routes/routePaths";
import { useAuthStore } from "../../../store/authStore";

interface SignupForm {
  fullName: string;
  email: string;
  password: string;
}

export function useSignup() {
  const navigate = useNavigate();
  const { setAuth, setIsAdmin } = useAuthStore();

  const [form, setForm] = useState<SignupForm>({
    fullName: "",
    email: "",
    password: "",
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [agreeToTerms, setAgreeToTerms] = useState(false);

  const { mutate: signup, isPending } = useAppMutation({
    mutationFn: () => authService.signup(form),

    onSuccess: (res) => {
      setAuth(
        { ...res.data.user, fullName: form.fullName, email: form.email },
        res.data.token.accessToken,
        res.data.token.refreshToken,
      );
      setIsAdmin(res.data.user.role === "ROLE_ADMIN" || res.data.user.role === "ADMIN");
      navigate(ROUTES.profile);
    },

    onError: (error: AppError) => {
      setFieldErrors({});
      setFormError(null);
      if (error.isValidation) {
        setFieldErrors(error.toFieldErrorMap());
      } else if (error.isConflict) {
        setFieldErrors({ email: error.message });
      } else {
        setFormError(error.message);
      }
    },
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    if (fieldErrors[e.target.name]) {
      setFieldErrors((prev) => ({ ...prev, [e.target.name]: "" }));
    }
  };

  const handleTermsChange = (checked: boolean) => {
    setAgreeToTerms(checked);
    if (fieldErrors.terms) {
      setFieldErrors((prev) => ({ ...prev, terms: "" }));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!agreeToTerms) {
      setFieldErrors({ terms: "You must accept the terms to continue" });
      return;
    }
    setFieldErrors({});
    setFormError(null);
    signup(undefined);
  };

  return {
    form,
    fieldErrors,
    formError,
    agreeToTerms,
    isPending,
    handleChange,
    handleTermsChange,
    handleSubmit,
  };
}
