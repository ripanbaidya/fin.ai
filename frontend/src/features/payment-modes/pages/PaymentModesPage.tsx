import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { paymentModeService } from "../paymentModeService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";

import PaymentModesView from "../components/PaymentModesView";

import type {
  PaymentModeResponse,
  UpdatePaymentModeRequest,
} from "../paymentMode.types";

const PaymentModesPage: React.FC = () => {
  const queryClient = useQueryClient();

  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState("");
  const [editingMode, setEditingMode] = useState<PaymentModeResponse | null>(
    null,
  );
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);

  // fetch
  const { data, isLoading, error, refetch } = useAppQuery<{
    data: PaymentModeResponse[];
  }>({
    queryKey: ["payment-modes"],
    queryFn: paymentModeService.getAll,
  });

  const modes = data?.data ?? [];

  const resetForm = () => {
    setName("");
    setFieldErrors({});
    setFormError(null);
    setShowForm(false);
    setEditingMode(null);
  };

  // create
  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: () => paymentModeService.create({ name: name.trim() }),
    onSuccess: () => {
      resetForm();
      queryClient.invalidateQueries({ queryKey: ["payment-modes"] });
      toast.success("Payment mode created");
    },
    onError: (err: AppError) => {
      if (err.isValidation) {
        setFieldErrors(err.toFieldErrorMap());
      } else {
        setFormError(err.message);
      }
    },
  });

  // update
  const { mutate: updateMode, isPending: isUpdating } = useAppMutation({
    mutationFn: (payload: { id: string; data: UpdatePaymentModeRequest }) =>
      paymentModeService.update(payload.id, payload.data),
    onSuccess: () => {
      resetForm();
      queryClient.invalidateQueries({ queryKey: ["payment-modes"] });
      toast.success("Payment mode updated");
    },
    onError: (err: AppError) => {
      if (err.isValidation) {
        setFieldErrors(err.toFieldErrorMap());
      } else {
        setFormError(err.message);
      }
    },
  });

  // delete
  const { mutate: remove, isPending: isDeleting } = useAppMutation({
    mutationFn: (id: string) => paymentModeService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["payment-modes"] });
      toast.success("Payment mode deleted");
    },
    onError: (err: AppError) => toast.error(err.message),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!name.trim()) {
      setFieldErrors({ name: "Name is required" });
      return;
    }

    setFieldErrors({});
    setFormError(null);

    if (editingMode) {
      updateMode({ id: editingMode.id, data: { name: name.trim() } });
    } else {
      create(undefined);
    }
  };

  const handleEdit = (mode: PaymentModeResponse) => {
    setEditingMode(mode);
    setName(mode.name);
    setFieldErrors({});
    setFormError(null);
    setShowForm(true);
  };

  const openForm = () => {
    setName("");
    setFieldErrors({});
    setFormError(null);
    setEditingMode(null);
    setShowForm(true);
  };

  return (
    <PaymentModesView
      showForm={showForm}
      name={name}
      setName={setName}
      editingMode={editingMode}
      fieldErrors={fieldErrors}
      formError={formError}
      modes={modes}
      isLoading={isLoading}
      error={error}
      refetch={refetch}
      isCreating={isCreating}
      isUpdating={isUpdating}
      isDeleting={isDeleting}
      handleSubmit={handleSubmit}
      handleEdit={handleEdit}
      handleDelete={remove}
      resetForm={resetForm}
      openForm={openForm}
    />
  );
};

export default PaymentModesPage;
