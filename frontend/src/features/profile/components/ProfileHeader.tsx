import { useState } from "react";
import { AppError } from "../../../api/errorParser";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { FormError } from "../../../shared/components/ui/FormError";
import type { UserProfileResponse } from "../profile.types";
import { getInitials } from "../../../shared/utils/profileHelpers";

interface Props {
  profile: UserProfileResponse;
  isUpdating: boolean;
  onUpdateName: (name: string) => void;
  nameSuccess: string;
  updateError: AppError | null;
}

const ProfileHeader: React.FC<Props> = ({
  profile,
  isUpdating,
  onUpdateName,
  nameSuccess,
  updateError,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [fullName, setFullName] = useState(profile.fullName);
  const [fieldError, setFieldError] = useState("");

  const isActive = profile.status === "ACTIVE";
  const initials = getInitials(profile.fullName);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const name = fullName.trim();

    if (!name) {
      setFieldError("Name is required");
      return;
    }

    setFieldError("");
    onUpdateName(name);
    setIsEditing(false);
  };

  const handleCancel = () => {
    setFullName(profile.fullName);
    setFieldError("");
    setIsEditing(false);
  };

  return (
    <div className="overflow-hidden rounded-2xl border border-gray-200 bg-white">
      <div className="p-4 sm:p-6">
        {/* Profile */}
        <div className="flex items-center gap-4 sm:gap-5">
          <div className="shrink-0">
            <div
              role="img"
              aria-label={`${profile.fullName} initials`}
              className="flex h-16 w-16 items-center justify-center rounded-full border border-gray-200 bg-gray-900 text-lg font-bold text-white"
            >
              {initials}
            </div>
          </div>

          <div className="min-w-0 flex-1">
            <p className="truncate text-base font-bold leading-tight text-gray-900 sm:text-lg">
              {profile.fullName}
            </p>
          </div>
        </div>

        {/* Status */}
        <div className="mt-4 mb-5 flex flex-wrap items-center gap-2">
          <span
            className={`inline-flex items-center gap-1 rounded-full border px-2.5 py-1 text-xs font-medium ${
              isActive
                ? "border-green-200 bg-green-50 text-green-700"
                : "border-amber-200 bg-amber-50 text-amber-700"
            }`}
          >
            <span
              className={`h-1 w-1 rounded-full ${
                isActive ? "bg-green-500" : "bg-amber-500"
              }`}
            />

            {profile.status.replaceAll("_", " ")}
          </span>

          <span
            className={`inline-flex items-center gap-1 rounded-full border px-2.5 py-1 text-xs font-medium ${
              isActive
                ? "border-blue-200 bg-blue-50 text-blue-600"
                : "border-amber-200 bg-amber-50 text-amber-600"
            }`}
          >
            <span className="text-[10px] font-bold">
              {isActive ? "✓" : "×"}
            </span>

            {isActive ? "Email verified" : "Unverified email"}
          </span>
        </div>

        {/* Edit */}
        {!isEditing ? (
          <div className="flex flex-wrap items-center gap-2">
            {nameSuccess && (
              <span className="inline-flex items-center gap-1.5 rounded-lg border border-green-200 bg-green-50 px-3 py-1.5 text-xs font-medium text-green-700">
                <span className="text-[10px] font-bold">✓</span>
                {nameSuccess}
              </span>
            )}

            <button
              type="button"
              onClick={() => setIsEditing(true)}
              className="inline-flex items-center gap-1.5 rounded-full border border-gray-200 px-3 py-1.5 text-xs text-gray-600 transition-colors hover:border-gray-300 hover:bg-gray-50 hover:text-gray-900"
            >
              <span className="text-[11px]">✎</span>
              Edit name
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-2">
            {updateError && !updateError.isValidation && (
              <FormError error={updateError.message} />
            )}

            <div className="flex flex-col items-stretch gap-2 xs:flex-row xs:items-center">
              <input
                type="text"
                value={fullName}
                onChange={(e) => {
                  setFullName(e.target.value);

                  if (fieldError) {
                    setFieldError("");
                  }
                }}
                autoFocus
                maxLength={100}
                placeholder="Full name"
                className="min-w-0 flex-1 rounded-lg border border-gray-200 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-black"
              />

              <div className="flex shrink-0 gap-2">
                <button
                  type="submit"
                  disabled={isUpdating}
                  className="flex flex-1 items-center justify-center gap-1.5 rounded-lg bg-gray-900 px-4 py-2 text-xs font-medium text-white transition-colors hover:bg-black disabled:opacity-60 xs:flex-none"
                >
                  {isUpdating && (
                    <span className="h-3 w-3 animate-spin rounded-full border-2 border-white border-t-transparent" />
                  )}

                  {isUpdating ? "Saving..." : "Save"}
                </button>

                <button
                  type="button"
                  onClick={handleCancel}
                  disabled={isUpdating}
                  className="flex-1 rounded-lg border border-gray-200 px-4 py-2 text-xs text-gray-600 transition-colors hover:bg-gray-50 disabled:opacity-50 xs:flex-none"
                >
                  Cancel
                </button>
              </div>
            </div>

            <FieldErrorMessage message={fieldError} />
          </form>
        )}
      </div>
    </div>
  );
};

export default ProfileHeader;