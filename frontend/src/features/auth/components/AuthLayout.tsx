import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import { ROUTES } from "../../../routes/routePaths";

interface Props {
  children: ReactNode;
  imagePosition: "left" | "right";
  imageUrl: string;
  imageHeading: string;
  imageDescription: string;
}

const AuthLayout: React.FC<Props> = ({
  children,
  imagePosition,
  imageUrl,
  imageHeading,
  imageDescription,
}) => {
  const imagePanel = (
    <aside
      aria-label={imageHeading}
      className="relative isolate hidden min-h-screen flex-col justify-end overflow-hidden bg-cover bg-center p-10 text-white lg:flex lg:p-12 xl:p-16"
      style={{
        backgroundImage: `linear-gradient(180deg, rgba(20, 36, 54, 0.08) 0%, rgba(20, 36, 54, 0.18) 38%, rgba(20, 36, 54, 0.78) 100%), url("${imageUrl}")`,
      }}
    >
      <div className="max-w-md">
        <p className="mb-3 text-sm font-medium tracking-wide text-white/80">
          A clearer way to manage money
        </p>
        <h2 className="text-3xl font-semibold leading-tight tracking-[-0.035em] sm:text-4xl lg:text-[2.75rem]">
          {imageHeading}
        </h2>
        <p className="mt-4 max-w-sm text-sm leading-6 text-white/80 sm:text-base">
          {imageDescription}
        </p>
      </div>
    </aside>
  );

  const formPanel = (
    <section className="flex min-w-0 flex-col justify-center px-5 py-8 sm:px-10 sm:py-10 lg:min-h-screen lg:px-12 xl:px-20">
      <header className="mb-8 sm:mb-10">
        <Link
          to={ROUTES.home}
          aria-label="fin.ai home"
          className="inline-flex items-baseline text-[1.65rem] font-semibold tracking-[-0.06em] text-[#202124] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#1a73e8] focus-visible:ring-offset-4"
        >
          fin<span className="text-[#1a73e8]">.ai</span>
        </Link>
      </header>
      <div className="w-full max-w-md">{children}</div>
    </section>
  );

  return (
    <main className="min-h-screen bg-white font-sans">
      <div className="grid min-h-screen w-full grid-cols-1 bg-white lg:grid-cols-2">
        {imagePosition === "left" ? (
          <>
            {imagePanel}
            {formPanel}
          </>
        ) : (
          <>
            {formPanel}
            {imagePanel}
          </>
        )}
      </div>
    </main>
  );
};

export default AuthLayout;
