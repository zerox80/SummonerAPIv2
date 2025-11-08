/**
 * A compact sparkline chart component for visualizing data trends.
 *
 * <p>This module provides a simple and lightweight sparkline chart component
 * that can be used to display data trends in a compact and visually appealing
 * way. It is rendered as an SVG, making it scalable and easy to style.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Renders a simple line chart without axes or coordinates</li>
 *   <li>Customizable color and height</li>
 *   <li>Handles empty or invalid data gracefully</li>
 *   <li>Lightweight and performant</li>
 * </ul>
 *
 * @module components/Sparkline
 * @author zerox80
 * @version 2.0
 */
import PropTypes from 'prop-types';

/**
 * Renders a sparkline chart as an SVG element.
 *
 * <p>This component takes an array of numbers and visualizes them as a simple
 * line chart. It is ideal for displaying trends in a compact space, such as in
 * dashboards or data tables.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {Array<number>} props.data - An array of numbers representing the data points for the sparkline.
 * @param {string} [props.color='var(--accent-solid)'] - The color of the sparkline path.
 * @param {number} [props.height=48] - The height of the sparkline SVG element in pixels.
 * @returns {React.ReactElement|null} The rendered sparkline SVG component, or null if no data is provided.
 *
 * @example
 * // Example of a sparkline with default props
 * const data = [10, 20, 15, 25, 30, 20];
 * <Sparkline data={data} />
 *
 * @example
 * // Example of a sparkline with custom color and height
 * const data = [5, 10, 5, 15, 10, 20];
 * <Sparkline data={data} color="blue" height={50} />
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
