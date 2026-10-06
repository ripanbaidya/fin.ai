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
  <div className="flex items-center justify-between gap-3 py-3.5 border-b border-gray-100 last:border-0">
    <span className="text-sm text-gray-500 shrink-0">{label}</span>
    <div className="flex items-center gap-2 sm:gap-3 min-w-0">
      <span className="text-sm text-gray-900 font-medium text-right truncate">
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
          <span className="truncate max-w-[160px] sm:max-w-xs block">
            {profile.email}
          </span>
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
              className={`w-1.5 h-1.5 rounded-full ${
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
              onClick={onOpenVerify}
              className="text-xs font-semibold text-gray-900 border border-gray-900 px-2.5 py-1 rounded-lg hover:bg-gray-900 hover:text-white transition-colors whitespace-nowrap"
            >
              Verify now
            </button>
          ) : (
            <span className="inline-flex items-center gap-1 text-xs text-blue-600 bg-blue-50 border border-blue-100 px-2 py-1 rounded-lg">
              <svg
                width="10"
                height="10"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
              Done
            </span>
          )
        }
      />
    </div>
  </div>
);

export default AccountInfoCard;
