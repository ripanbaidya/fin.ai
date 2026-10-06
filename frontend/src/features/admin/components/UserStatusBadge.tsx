interface Props {
  status: string;
}

const UserStatusBadge: React.FC<Props> = ({ status }) => (
  <span
    className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${
      status === "ACTIVE"
        ? "bg-green-50 text-green-700 border border-green-200"
        : status === "PENDING_VERIFICATION"
          ? "bg-amber-50 text-amber-700 border border-amber-200"
          : "bg-gray-100 text-gray-600 border border-gray-200"
    }`}
  >
    <span
      className={`w-1.5 h-1.5 rounded-full ${
        status === "ACTIVE" ? "bg-green-500" : status === "PENDING_VERIFICATION" ? "bg-amber-500" : "bg-gray-400"
      }`}
    />
    {status.replaceAll("_", " ").toLowerCase().replace(/^\w/, (letter) => letter.toUpperCase())}
  </span>
);

export default UserStatusBadge;
