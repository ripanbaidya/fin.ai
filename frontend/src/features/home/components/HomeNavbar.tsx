import { FiUser } from "react-icons/fi";
import { Link } from "react-router-dom";
import { useAuth } from "../../../shared/hooks/useAuth";
import { getInitials } from "../../../shared/utils/profileHelpers";

const HomeNavbar: React.FC = () => {
  const { isAuthenticated, user } = useAuth();
  const profileName = user?.fullName?.trim();
  const initials = profileName ? getInitials(profileName) : null;

  return (
    <header className="sticky top-0 z-50 border-b border-gray-200 bg-white">
      <nav
        aria-label="Main navigation"
        className="mx-auto flex h-16 max-w-6xl items-center justify-between gap-4 px-5"
      >
        <Link
          to="/home"
            aria-label="fin home"
            className="inline-flex min-w-0 shrink-0 items-center gap-2 rounded-full text-lg font-semibold tracking-tight text-[#202124] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#1a73e8] focus-visible:ring-offset-2 sm:gap-2.5 sm:text-xl"
          >
            <img
              src="/fin-icon.svg"
              alt=""
              className="h-8 w-8 shrink-0 sm:h-9 sm:w-9"
            />
            {/* <span>fin</span> */}
          </Link>

        <div className="hidden items-center gap-7 sm:flex">
          <a
            href="#features"
            className="text-sm text-[#5f6368] transition-colors hover:text-[#202124]"
          >
            Features
          </a>
          <a
            href="#how-it-works"
            className="text-sm text-[#5f6368] transition-colors hover:text-[#202124]"
          >
            How it works
          </a>
        </div>

        <div className="flex shrink-0 items-center gap-2 sm:gap-3">
          {isAuthenticated ? (
            <>
              <Link
                to="/dashboard"
                className="rounded-lg px-3 py-2 text-sm font-medium text-[#3c4043] transition-colors hover:bg-[#f1f3f4]"
              >
                Dashboard
              </Link>
              <Link
                to="/profile"
                className="inline-flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-[#e8f0fe] text-xs font-semibold text-[#1967d2] transition-colors hover:bg-[#d2e3fc] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#1a73e8] focus-visible:ring-offset-2"
                aria-label={
                  profileName ? `Go to profile for ${profileName}` : "Go to profile"
                }
                title={profileName || "Profile"}
              >
                {initials ? (
                  initials
                ) : (
                  <FiUser aria-hidden="true" className="h-5 w-5" />
                )}
              </Link>
            </>
          ) : (
            <>
              <Link
                to="/login"
                className="rounded-lg px-3 py-2 text-sm font-medium text-[#3c4043] transition-colors hover:bg-[#f1f3f4]"
              >
                Sign in
              </Link>
              <Link
                to="/signup"
                className="rounded-lg bg-[#1a73e8] px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-[#1765cc]"
              >
                Get started
              </Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
};

export default HomeNavbar;
