import packageMetadata from "../../../package.json";

export type SocialLinkLabel = "GitHub" | "Instagram" | "LinkedIn";

export interface SocialLink {
  label: SocialLinkLabel;
  url: string;
}

export const about = {
  appName: "fin.ai",
  version: packageMetadata.version,
  license: "Apache License 2.0",
  socialLinks: [
    { label: "GitHub", url: "https://github.com/ripanbaidya" },
    { label: "Instagram", url: "https://www.instagram.com/ridominus/" },
    { label: "LinkedIn", url: "https://www.linkedin.com/in/ripanbaidya/" },
  ] satisfies SocialLink[],
};
