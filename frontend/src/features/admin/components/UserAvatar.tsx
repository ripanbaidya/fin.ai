import { getInitials } from "../../../shared/utils/profileHelpers";

interface Props {
  fullName: string;
  size?: "sm" | "md";
}

const sizeMap = {
  sm: "w-8 h-8 text-xs",
  md: "w-9 h-9 text-xs",
};

const UserAvatar: React.FC<Props> = ({ fullName, size = "md" }) => (
  <div
    className={`${sizeMap[size]} rounded-full bg-gray-900 flex items-center justify-center shrink-0`}
  >
    <span className="text-[#e8ff4f] font-black select-none">
      {getInitials(fullName)}
    </span>
  </div>
);

export default UserAvatar;
