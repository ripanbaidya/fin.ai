import {
  RiBankCardLine,
  RiChat3Line,
  RiDownloadLine,
  RiLineChartLine,
  RiNotification3Line,
  RiTargetLine,
} from "react-icons/ri";
import { FEATURES } from "../../../shared/constants/homeData";

const FEATURE_ICONS = [
  RiChat3Line,
  RiLineChartLine,
  RiNotification3Line,
  RiTargetLine,
  RiBankCardLine,
  RiDownloadLine,
];

const FeaturesSection: React.FC = () => (
  <section id="features" className="bg-[#f8fafd] py-16 sm:py-20">
    <div className="mx-auto max-w-6xl px-5">
      <div className="max-w-2xl">
        <p className="mb-3 text-sm font-medium text-[#1a73e8]">
          Everything in one place
        </p>
        <h2 className="text-3xl font-semibold tracking-tight text-[#202124] sm:text-4xl">
          A simpler picture of your money.
        </h2>
        <p className="mt-4 text-base leading-7 text-[#5f6368]">
          The everyday tools you need to understand where your money goes and
          decide what to do next.
        </p>
      </div>

      <div className="mt-9 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {FEATURES.map((feature, index) => {
          const Icon = FEATURE_ICONS[index];
          return (
            <article
              key={feature.title}
              className="rounded-xl border border-[#e8eaed] bg-white p-5 transition-colors hover:border-[#c6dafc] sm:p-6"
            >
              <div className="mb-5 flex h-10 w-10 items-center justify-center rounded-lg bg-[#e8f0fe] text-[#1a73e8]">
                <Icon aria-hidden="true" size={20} />
              </div>
              <h3 className="text-base font-medium text-[#202124]">
                {feature.title}
              </h3>
              <p className="mt-2 text-sm leading-6 text-[#5f6368]">
                {feature.description}
              </p>
            </article>
          );
        })}
      </div>
      <p className="mt-5 text-sm text-[#80868b]">
        Tools to help make your day-to-day finances easier to manage.
      </p>
    </div>
  </section>
);

export default FeaturesSection;
