import { h } from "preact";

export default function SvgIcon({ name = "", prefix = "icon", ...props }) {
  const symbolId = `#${prefix}-${name}`;

  return (
    <svg {...props} aria-hidden="true">
      <use href={symbolId} />
    </svg>
  );
}
