import { useEffect, useState, useRef } from "react";

/**
 * Animates a number from 0 to `target` over `duration` ms.
 * Only starts when `enabled` is true (e.g. when the element enters the viewport).
 */
export function useCounter(
  target: number,
  duration = 1600,
  enabled = false,
): number {
  const [count, setCount] = useState(0);

  useEffect(() => {
    if (!enabled) return;
    let startTime: number | null = null;

    const step = (ts: number) => {
      if (!startTime) startTime = ts;
      const progress = Math.min((ts - startTime) / duration, 1);
      // Ease-out cubic
      const eased = 1 - Math.pow(1 - progress, 3);
      setCount(Math.floor(eased * target));
      if (progress < 1) requestAnimationFrame(step);
    };

    requestAnimationFrame(step);
  }, [enabled, target, duration]);

  return count;
}

/**
 * Returns a ref and a boolean indicating whether the element is in the viewport.
 * Disconnects the observer once visible (fire-once).
 */
export function useInView<T extends Element>(
  threshold = 0.2,
) {
  const ref = useRef<T>(null);
  const [inView, setInView] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const obs = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setInView(true);
          obs.disconnect();
        }
      },
      { threshold },
    );
    obs.observe(el);
    return () => obs.disconnect();
  }, [threshold]);

  return { ref, inView };
}