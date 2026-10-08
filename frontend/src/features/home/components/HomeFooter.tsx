import { FaGithub, FaLinkedin } from "react-icons/fa";
import type { IconType } from "react-icons";

const socialLinks: {
  label: string;
  href: string;
  icon: IconType;
}[] = [
  {
    label: "GitHub",
    href: "https://github.com/ripanbaidya",
    icon: FaGithub,
  },
  {
    label: "LinkedIn",
    href: "https://www.linkedin.com/in/ripanbaidya/",
    icon: FaLinkedin,
  },
];

const socialLinkClass =
  "flex h-9 w-9 items-center justify-center rounded-full text-gray-500 " +
  "transition-all duration-200 ease-out " +
  "hover:-translate-y-0.5 hover:bg-gray-100 hover:text-gray-900 " +
  "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-gray-300";

const HomeFooter: React.FC = () => {
  return (
    <footer className="border-t border-gray-200 bg-white">
      <div className="mx-auto flex w-full max-w-6xl flex-col items-center justify-between gap-4 px-5 py-6 sm:flex-row sm:px-6">
        {/* Brand & Copyright */}
        <p className="text-center text-xs text-gray-500 sm:text-left">
          <span className="font-bold tracking-tight text-gray-900">fin</span>{" "}
          &copy; 2026 All rights reserved.
        </p>

        {/* Social Links */}
        <nav aria-label="Social links" className="flex items-center gap-1">
          {socialLinks.map(({ label, href, icon: Icon }) => (
            <a
              key={label}
              href={href}
              target="_blank"
              rel="noopener noreferrer"
              aria-label={label}
              className={socialLinkClass}
            >
              <Icon aria-hidden="true" size={14} />
            </a>
          ))}
        </nav>
      </div>
    </footer>
  );
};

export default HomeFooter;
