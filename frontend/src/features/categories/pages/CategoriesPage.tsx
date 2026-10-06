import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { categoryService } from "../categoryService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";

import CategoriesView from "../components/CategoriesView";

import type {
  CategoryResponse,
  CategoryType,
  UpdateCategoryRequest,
} from "../category.types";

export default function CategoriesPage() {
  const queryClient = useQueryClient();

  const [activeType, setActiveType] = useState<CategoryType>("EXPENSE");
  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState("");
  const [formType, setFormType] = useState<CategoryType>("EXPENSE");

  const [editingCategory, setEditingCategory] =
    useState<CategoryResponse | null>(null);

  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);

  const { data, isLoading, error, refetch } = useAppQuery<{
    data: CategoryResponse[];
  }>({
    queryKey: ["categories", activeType],
    queryFn: () => categoryService.getAll(activeType),
  });

  const categories = data?.data ?? [];

  const resetForm = () => {
    setName("");
    setFieldErrors({});
    setFormError(null);
    setShowForm(false);
    setEditingCategory(null);
  };

  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: () =>
      categoryService.create({
        name: name.trim(),
        categoryType: formType,
      }),
    onSuccess: () => {
      resetForm();
      queryClient.invalidateQueries({ queryKey: ["categories"] });
      toast.success("Category created");
    },
    onError: (err: AppError) => {
      if (err.isValidation) {
        setFieldErrors(err.toFieldErrorMap());
      } else {
        setFormError(err.message);
      }
    },
  });

  const { mutate: updateCategory, isPending: isUpdating } = useAppMutation({
    mutationFn: (payload: { id: string; data: UpdateCategoryRequest }) =>
      categoryService.update(payload.id, payload.data),
    onSuccess: () => {
      resetForm();
      queryClient.invalidateQueries({ queryKey: ["categories"] });
      toast.success("Category updated");
    },
    onError: (err: AppError) => {
      if (err.isValidation) {
        setFieldErrors(err.toFieldErrorMap());
      } else {
        setFormError(err.message);
      }
    },
  });

  const { mutate: remove, isPending: isDeleting } = useAppMutation({
    mutationFn: (id: string) => categoryService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] });
      toast.success("Category deleted");
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

    if (editingCategory) {
      updateCategory({
        id: editingCategory.id,
        data: {
          name: name.trim(),
          categoryType: formType,
        },
      });
    } else {
      create(undefined);
    }
  };

  const handleEdit = (category: CategoryResponse) => {
    setEditingCategory(category);
    setName(category.name);
    setFormType(category.categoryType);
    setShowForm(true);
  };

  const openForm = () => {
    setFormType(activeType);
    setName("");
    setFieldErrors({});
    setFormError(null);
    setShowForm(true);
    setEditingCategory(null);
  };

  const switchTab = (type: CategoryType) => {
    setActiveType(type);
    setShowForm(false);
  };

  return (
    <CategoriesView
      activeType={activeType}
      setActiveType={switchTab}
      showForm={showForm}
      name={name}
      setName={setName}
      formType={formType}
      setFormType={setFormType}
      editingCategory={editingCategory}
      fieldErrors={fieldErrors}
      formError={formError}
      categories={categories}
      isLoading={isLoading}
      error={error}
      refetch={refetch}
      isCreating={isCreating}
      isUpdating={isUpdating}
      isDeleting={isDeleting}
      handleSubmit={handleSubmit}
      handleEdit={handleEdit}
      handleDelete={remove}
      openForm={openForm}
    />
  );
}
