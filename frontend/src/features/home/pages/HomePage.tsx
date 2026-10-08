/**
 * Imports all home-specific sections and renders them in order.
 *
 *   HomeNavbar        — primary navigation
 *   HeroSection       — introduction and illustrative dashboard preview
 *   StatsSection      — concise product highlights
 *   FeaturesSection   — core feature overview
 *   HowItWorksSection — getting-started steps and AI chat example
 *   CTASection        — account creation prompt
 *   HomeFooter        — social links, copyright, and developer attribution
 */

import HomeNavbar from "../components/HomeNavbar";
import HeroSection from "../components/HeroSection";
import StatsSection from "../components/StatsSection";
import FeaturesSection from "../components/FeaturesSection";
import HowItWorksSection from "../components/HowItWorksSection";
import CTASection from "../components/CTASection";
import HomeFooter from "../components/HomeFooter";

export default function HomePage() {
  return (
    <div className="bg-white text-gray-900 font-sans antialiased">
      <HomeNavbar />
      <HeroSection />
      <StatsSection />
      <FeaturesSection />
      <HowItWorksSection />
      <CTASection />
      <HomeFooter />
    </div>
  );
}
