

import PropTypes from 'prop-types';
import '../styles/components/metric-tile.css';


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
