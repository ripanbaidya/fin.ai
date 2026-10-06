import { Link } from "react-router-dom";
import { useAuth } from "../../../shared/hooks/useAuth";
import { getInitials } from "../../../shared/utils/profileHelpers";

const HomeNavbar: React.FC = () => {
  const { isAuthenticated, user } = useAuth();
  const initials = getInitials(user?.fullName);

  return (
    <header className="sticky top-0 z-50 border-b border-gray-200 bg-white">
      <nav
        aria-label="Main navigation"
        className="mx-auto flex h-16 max-w-6xl items-center justify-between gap-4 px-5"
      >
        <Link
          to="/home"
          className="shrink-0 text-xl font-semibold tracking-tight text-[#202124]"
        >
          fin
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
                className="rounded-full border border-[#dadce0]"
                aria-label="Go to profile"
                title="Profile"
              >
                <span
                  aria-hidden="true"
                  className="flex h-9 w-9 items-center justify-center rounded-full bg-[#e8f0fe] text-xs font-medium text-[#1967d2]"
                >
                  {initials}
                </span>
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
