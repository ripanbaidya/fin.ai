import { useState } from "react";

interface Props {
  onDeleteAccount: (password: string) => void;
  isDeleting: boolean;
  errorMessage?: string | null;
}

const DangerZone: React.FC<Props> = ({ onDeleteAccount, isDeleting, errorMessage }) => {
  const [open, setOpen] = useState(false);
  const [password, setPassword] = useState("");

  return (
    <div className="bg-white border border-gray-200 rounded-2xl overflow-hidden">
      <div className="border-b border-gray-100 bg-gray-50 px-4 py-4 sm:px-5">
        <div>
          <p className="text-sm font-semibold text-gray-900">Account deletion</p>
          <p className="mt-0.5 text-xs leading-relaxed text-gray-500">
            Confirm with your password. Deletion starts with an account grace period.
          </p>
        </div>
      </div>

      {/* Actions */}
      <div className="px-4 sm:px-5 py-4 sm:py-5">
        <div className="flex flex-col xs:flex-row xs:items-center justify-between gap-3 xs:gap-4">
          <div className="min-w-0">
            <p className="text-sm font-semibold text-gray-900">Delete account</p>
            <p className="text-xs text-gray-400 mt-0.5 leading-relaxed">
              Requests account deletion. Your account enters a grace period before permanent removal.
            </p>
          </div>
          <button
            onClick={() => setOpen((v) => !v)}
            className="shrink-0 self-start xs:self-auto text-xs font-semibold text-red-700 border border-red-300 bg-red-50 px-4 py-2 rounded-lg hover:bg-red-100 transition"
          >
            Delete
          </button>
        </div>

        {open && (
          <div className="mt-4 rounded-xl border border-red-200 bg-red-50/60 p-4 space-y-3">
            <p className="text-xs text-red-700">
              Enter your current password to confirm account deletion.
            </p>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Current password"
              className="w-full rounded-lg border border-red-200 bg-white px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-red-500/30"
            />
            {errorMessage ? <p className="text-xs text-red-600">{errorMessage}</p> : null}
            <div className="flex items-center gap-2">
              <button
                onClick={() => {
                  setOpen(false);
                  setPassword("");
                }}
                disabled={isDeleting}
                className="text-xs font-medium px-3 py-2 rounded-lg border border-gray-200 bg-white hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={() => onDeleteAccount(password)}
                disabled={!password.trim() || isDeleting}
                className="text-xs font-semibold px-3 py-2 rounded-lg text-white bg-red-600 hover:bg-red-500 disabled:opacity-50 disabled:cursor-not-allowed"
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
