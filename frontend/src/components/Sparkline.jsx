import PropTypes from 'prop-types';

/**
 * Renders a sparkline chart.
 *
 * @param {object} props - The component props.
 * @param {Array<number>} props.data - The data points for the sparkline.
 * @param {string} [props.color='var(--accent-solid)'] - The color of the sparkline.
 * @param {number} [props.height=48] - The height of the sparkline.
 * @returns {React.ReactElement|null} The rendered component or null if no data is provided.
 */
export default function Sparkline({ data, color = 'var(--accent-solid)', height = 48 }) {
  if (!Array.isArray(data) || data.length === 0) {
    return null;
  }

  const min = Math.min(...data);
  const max = Math.max(...data);
  const diff = max - min || 1;
  const points = data
    .map((value, index) => {
      const x = (index / Math.max(data.length - 1, 1)) * 100;
      const y = ((max - value) / diff) * 100;
      return `${x},${y}`;
    })
    .join(' ');

  return (
    <svg
      className="sparkline"
      viewBox="0 0 100 100"
      preserveAspectRatio="none"
      style={{ height }}
    >
      <polyline
        fill="none"
        stroke={color}
        strokeWidth="6"
        strokeLinecap="round"
        strokeLinejoin="round"
        points={points}
      />
    </svg>
  );
}

Sparkline.propTypes = {
  data: PropTypes.arrayOf(PropTypes.number).isRequired,
  color: PropTypes.string,
  height: PropTypes.number
};
