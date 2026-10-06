/**
 * Returns the first and last initials from a full name.
 * e.g. "Ripan Kumar Baidya" → "RB", "Ripan" → "R"
 */
export const getInitials = (name?: string): string => {
  const parts = (name ?? "")
    .trim()
    .split(/\s+/)
    .filter(Boolean);

  if (parts.length === 0) return "U";
  if (parts.length === 1) return parts[0][0].toUpperCase();
  return `${parts[0][0]}${parts[parts.length - 1][0]}`.toUpperCase();
};
