
import PropTypes from 'prop-types';
import '../styles/components/tooltip.css';


export default function Tooltip({ label, children, placement = 'top' }) {
  return (
    <span className={`tooltip tooltip--${placement}`}>
      {children}
      <span className="tooltip__bubble" role="tooltip">{label}</span>
    </span>
  );
}

Tooltip.propTypes = {
  label: PropTypes.node.isRequired,
  children: PropTypes.node.isRequired,
  placement: PropTypes.oneOf(['top', 'bottom', 'left', 'right'])
};
