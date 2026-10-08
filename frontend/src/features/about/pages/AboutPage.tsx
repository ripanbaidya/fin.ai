import { FaGithub, FaInstagram, FaLinkedin } from "react-icons/fa";
import { about } from "../about";

const SOCIAL_ICONS = {
  GitHub: FaGithub,
  Instagram: FaInstagram,
  LinkedIn: FaLinkedin,
} as const;

export default function AboutPage() {
  return (
    <div className="mx-auto w-full min-w-0 max-w-2xl space-y-5 py-4">
      <header>
        <h1 className="text-2xl font-semibold tracking-tight text-gray-900">
          About {about.appName}
        </h1>
      </header>

      <section className="space-y-5 rounded-2xl border border-gray-200 bg-white p-5 sm:p-7">
        <dl className="border-t border-gray-100 pt-5">
          <div className="flex flex-wrap items-center justify-between gap-x-3 gap-y-1 py-2">
            <dt className="text-sm text-gray-500">Application Name</dt>
            <dd className="text-sm font-medium text-gray-900">
              {about.appName}
            </dd>
          </div>

          <div className="flex flex-wrap items-center justify-between gap-x-3 gap-y-1 py-2">
            <dt className="text-sm text-gray-500">Version</dt>
            <dd className="text-sm font-medium text-gray-900">
              {about.version}
            </dd>
          </div>

          <div className="flex flex-wrap items-center justify-between gap-x-3 gap-y-1 py-2">
            <dt className="text-sm text-gray-500">License</dt>
            <dd className="text-sm font-medium text-gray-900">
              {about.license}
            </dd>
          </div>
        </dl>

        <div className="border-t border-gray-100 pt-5">
          <h2 className="text-xs font-medium normal-case tracking-wide text-gray-500">
            Social Links
          </h2>
          <ul className="mt-3 flex flex-wrap gap-3">
            {about.socialLinks.map(({ label, url }) => {
              const Icon = SOCIAL_ICONS[label];
              return (
                <li key={label}>
                  <a
                    href={url}
                    target="_blank"
                    rel="noreferrer"
                    className="inline-flex items-center gap-2 rounded-full border border-gray-200 px-3 py-2 text-sm text-gray-700 hover:bg-gray-50"
                  >
                    <Icon aria-hidden="true" />
                    {label}
                  </a>
                </li>
              );
            })}
          </ul>
        </div>
      </section>
    </div>
  );
}
