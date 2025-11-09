/**
 * Component for displaying tooltip information on hover.
 * 
 * <p>This module provides a simple tooltip implementation that displays
 * additional information when users hover over or focus on wrapped content.
 * It supports multiple placement positions and includes proper accessibility
 * attributes for screen readers.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Multiple placement options (top, bottom, left, right)</li>
 *   <li>Accessibility support with proper ARIA attributes</li>
 *   <li>CSS-based positioning and animations</li>
 *   <li>Support for any content type as tooltip label</li>
 *   <li>Keyboard and screen reader friendly</li>
 * </ul>
 * 
 * @module components/Tooltip
 * @author zerox80
 * @version 2.0
 */
import PropTypes from 'prop-types';
import '../styles/components/tooltip.css';

/**
 * Component for displaying tooltip information on hover.
 * 
 * <p>This component provides a simple tooltip implementation that displays
 * additional information when users hover over or focus on wrapped content.
 * It supports multiple placement positions and includes proper accessibility
 * attributes for screen readers.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Multiple placement options (top, bottom, left, right)</li>
 *   <li>Accessibility support with proper ARIA attributes</li>
 *   <li>CSS-based positioning and animations</li>
 *   <li>Support for any content type as tooltip label</li>
 *   <li>Keyboard and screen reader friendly</li>
 * </ul>
 * 
 * @component Tooltip
 * @param {Object} props - Component props
 * @param {React.ReactNode} props.label - The tooltip content to display on hover
 * @param {React.ReactNode} props.children - The content that triggers the tooltip
 * @param {string} [props.placement='top'] - Tooltip placement relative to the trigger content
 * @returns {React.Element} Rendered tooltip component
 * 
 * @example
 * // Basic tooltip with top placement
 * <Tooltip label="This is helpful information">
 *   <button>Hover me</button>
 * </Tooltip>
 * 
 * @example
 * // Tooltip with custom placement
 * <Tooltip label="Additional context" placement="bottom">
 *   <span>Info</span>
 * </Tooltip>
 * 
 * @example
 * // Tooltip with complex content
 * <Tooltip label={<div><strong>Tip:</strong> Use this wisely</div>}>
 *   <Icon name="info" />
 * </Tooltip>
 */
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
