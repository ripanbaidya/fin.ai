import { useState } from "react";

interface Props {
  onDeleteAccount: (password: string) => void;
  isDeleting: boolean;
  errorMessage?: string | null;
}

const DangerZone: React.FC<Props> = ({
  onDeleteAccount,
  isDeleting,
  errorMessage,
}) => {
  const [open, setOpen] = useState(false);
  const [password, setPassword] = useState("");

  const close = () => {
    setOpen(false);
    setPassword("");
  };

  const toggle = () => {
    if (open) close();
    else setOpen(true);
  };

  return (
    <div className="overflow-hidden rounded-2xl border border-gray-200 bg-white">
      <div className="px-4 py-4 sm:px-5 sm:py-5">
        <div className="flex flex-col justify-between gap-3 xs:flex-row xs:items-center xs:gap-4">
          <div className="min-w-0">
            <p className="text-sm font-semibold text-gray-900">
              Delete account
            </p>
            <p className="mt-0.5 text-xs leading-relaxed text-gray-400">
              Your account enters a grace period before permanent removal.
            </p>
          </div>

          <button
            type="button"
            onClick={toggle}
            className="shrink-0 self-start rounded-lg border border-red-300 bg-red-50 px-4 py-2 text-xs font-semibold text-red-700 transition hover:bg-red-100 xs:self-auto"
          >
            Delete
          </button>
        </div>

        {open && (
          <div className="mt-4 space-y-3 rounded-xl border border-red-200 bg-red-50/60 p-4">
            <p className="text-xs text-red-700">
              Enter your current password to confirm deletion.
            </p>

            <input
              type="password"
              aria-label="Current password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Current password"
              className="w-full rounded-lg border border-red-200 bg-white px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-red-500/30"
            />

            {errorMessage && (
              <p className="text-xs text-red-600">{errorMessage}</p>
            )}

            <div className="flex gap-2">
              <button
                type="button"
                onClick={close}
                disabled={isDeleting}
                className="rounded-lg border border-gray-200 bg-white px-3 py-2 text-xs font-medium hover:bg-gray-50 disabled:opacity-50"
              >
                Cancel
              </button>

              <button
                type="button"
                onClick={() => onDeleteAccount(password)}
                disabled={!password.trim() || isDeleting}
                className="rounded-lg bg-red-600 px-3 py-2 text-xs font-semibold text-white hover:bg-red-500 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {isDeleting ? "Deleting..." : "Confirm Delete"}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DangerZone;