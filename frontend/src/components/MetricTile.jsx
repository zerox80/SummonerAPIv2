/**
 * Component for displaying metric data in a styled tile format.
 * 
 * <p>This module provides a reusable MetricTile component for displaying key performance
 * indicators, statistics, and other important metrics in a clean, consistent format.
 * The component supports flexible content display with optional secondary information,
 * icons, and trend indicators.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Flexible content display with optional secondary information</li>
 *   <li>Icon support for visual context and branding</li>
 *   <li>Trend indicators for showing changes over time</li>
 *   <li>Emphasis mode for highlighting important metrics</li>
 *   <li>Responsive design that works across all screen sizes</li>
 *   <li>Semantic HTML structure for accessibility</li>
 * </ul>
 * 
 * @module components/MetricTile
 * @author zerox80
 * @version 2.0
 */

import PropTypes from 'prop-types';
import '../styles/components/metric-tile.css';

/**
 * Component for displaying metric data in a styled tile format.
 * 
 * <p>This component renders a metric tile with a label, primary value, and optional
 * secondary information, icon, and trend indicator. It's designed to display
 * key performance indicators, statistics, and other important metrics in a
 * clean, consistent format throughout the application.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Flexible content display with optional secondary information</li>
 *   <li>Icon support for visual context and branding</li>
 *   <li>Trend indicators for showing changes over time</li>
 *   <li>Emphasis mode for highlighting important metrics</li>
 *   <li>Responsive design that works across all screen sizes</li>
 *   <li>Semantic HTML structure for accessibility</li>
 * </ul>
 * 
 * @component MetricTile
 * @param {Object} props - Component props
 * @param {string} props.label - The metric label displayed in the header
 * @param {string|number} props.value - The primary metric value to display
 * @param {string|React.ReactNode} [props.secondary] - Optional secondary information below the main value
 * @param {React.ReactNode} [props.icon] - Optional icon to display next to the label
 * @param {React.ReactNode} [props.trend] - Optional trend indicator (e.g., arrow, percentage change)
 * @param {boolean} [props.emphasize=false] - Whether to apply emphasis styling to make the tile stand out
 * @returns {React.Element} Rendered metric tile component
 * 
 * @example
 * // Basic metric tile
 * <MetricTile 
 *   label="Win Rate"
 *   value="67%"
 *   secondary="15W / 5L"
 * />
 * 
 * @example
 * // Metric tile with icon and trend
 * <MetricTile 
 *   label="Rank"
 *   value="Gold II"
 *   icon="🏆"
 *   trend="↑ 25 LP"
 *   emphasize={true}
 * />
 * 
 * @example
 * // Metric with complex secondary content
 * <MetricTile 
 *   label="KDA"
 *   value="3.5"
 *   secondary={<span>10/2/8</span>}
 *   icon="⚔️"
 * />
 */
export default function MetricTile({
  label,
  value,
  secondary,
  icon,
  trend,
  emphasize = false
}) {
  return (
    <article className={`metric-tile${emphasize ? ' metric-tile--emphasize' : ''}`}>
      <header className="metric-tile__header">
        <span className="metric-tile__label">{label}</span>
        {icon && <span className="metric-tile__icon" aria-hidden>{icon}</span>}
      </header>
      <div className="metric-tile__value">{value}</div>
      {secondary && <div className="metric-tile__secondary">{secondary}</div>}
      {trend && <div className="metric-tile__trend">{trend}</div>}
    </article>
  );
}

MetricTile.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
  secondary: PropTypes.oneOfType([PropTypes.string, PropTypes.node]),
  icon: PropTypes.node,
  trend: PropTypes.node,
  emphasize: PropTypes.bool
};
