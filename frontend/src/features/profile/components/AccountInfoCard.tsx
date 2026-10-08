import type { UserProfileResponse } from "../profile.types";

interface Props {
  profile: UserProfileResponse;
  onOpenVerify: () => void;
}

const InfoRow: React.FC<{
  label: string;
  value: React.ReactNode;
  action?: React.ReactNode;
}> = ({ label, value, action }) => (
  <div className="flex items-start justify-between gap-3 py-3.5 border-b border-gray-100 last:border-0">
    <span className="text-sm text-gray-500 shrink-0">{label}</span>
    <div className="flex min-w-0 items-center gap-2 sm:gap-3">
      <span className="min-w-0 text-right text-sm font-medium text-gray-900">
        {value}
      </span>
      {action && <div className="shrink-0">{action}</div>}
    </div>
  </div>
);

const AccountInfoCard: React.FC<Props> = ({ profile, onOpenVerify }) => (
  <div className="bg-white border border-gray-200 rounded-2xl overflow-hidden">
    {/* Section header */}
    <div className="px-4 sm:px-5 pt-4 pb-2 border-b border-gray-100">
      <p className="text-xs font-semibold uppercase tracking-widest text-gray-400">
        Account details
      </p>
    </div>

    <div className="px-4 sm:px-5">
      <InfoRow
        label="Email"
        value={
          <span className="block max-w-full break-all">{profile.email}</span>
        }
      />

      <InfoRow
        label="Account status"
        value={
          <span
            className={`inline-flex items-center gap-1.5 font-medium ${
              profile.status === "ACTIVE" ? "text-green-600" : "text-amber-600"
            }`}
          >
            <span
              className={`w-1 h-1 rounded-full ${
                profile.status === "ACTIVE" ? "bg-green-500" : "bg-amber-500"
              }`}
            />
            {profile.status.replaceAll("_", " ")}
          </span>
        }
      />

      <InfoRow
        label="Email verified"
        value={
          <span
            className={`font-medium ${
              profile.status === "ACTIVE" ? "text-blue-600" : "text-amber-600"
            }`}
          >
            {profile.status === "ACTIVE" ? "Verified" : "Not verified"}
          </span>
        }
        action={
          profile.status !== "ACTIVE" ? (
            <button
              type="button"
              onClick={onOpenVerify}
              className="whitespace-nowrap rounded-lg border border-gray-900 px-2.5 py-1 text-xs font-semibold text-gray-900 transition-colors hover:bg-gray-900 hover:text-white"
            >
              Verify now
            </button>
          ) : (
            <span
              aria-label="Email verified"
              className="inline-flex h-4 w-4 items-center justify-center rounded-full bg-blue-50 text-blue-600"
            >
              <svg
                width="9"
                height="9"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
            </span>
          )
        }
      />
    </div>
  </div>
);

export default AccountInfoCard;
