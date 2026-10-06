import StatCard from "./StatCard";

const AdminStatsGrid: React.FC = () => (
  <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 sm:gap-3">
    <StatCard
      label="Active Users"
      queryKey={["admin", "count", "USER", "ACTIVE"]}
      role="USER"
      accountStatus="ACTIVE"
    />
    <StatCard
      label="Inactive Users"
      queryKey={["admin", "count", "USER", "INACTIVE"]}
      role="USER"
      accountStatus="INACTIVE"
    />
    <StatCard
      label="Active Admins"
      queryKey={["admin", "count", "ADMIN", "ACTIVE"]}
      role="ADMIN"
      accountStatus="ACTIVE"
    />
    <StatCard
      label="Pending verification"
      queryKey={["admin", "count", "USER", "PENDING_VERIFICATION"]}
      role="USER"
      accountStatus="PENDING_VERIFICATION"
    />
  </div>
);

export default AdminStatsGrid;
