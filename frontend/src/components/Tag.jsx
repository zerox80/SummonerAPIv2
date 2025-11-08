/**
 * A simple and reusable tag component for displaying categorical information.
 *
 * <p>This module provides a tag component that can be used to display small
 * pieces of information, such as categories, statuses, or keywords. The tag's
 * appearance can be customized using different "tones" to convey semantic
 * meaning.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Multiple color tones (default, success, warning, danger, info)</li>
 *   <li>Simple and clean design</li>
 *   <li>Lightweight and easy to use</li>
 * </ul>
 *
 * @module components/Tag
 * @author zerox80
 * @version 2.0
 */
import PropTypes from 'prop-types';
import '../styles/components/tag.css';

/**
 * Renders a tag component with a specific tone and content.
 *
 * <p>This component is used to highlight information with a small, colored label.
 * The `tone` prop determines the color of the tag, allowing it to convey
 * semantic meaning (e.g., success, warning, danger).</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {('default'|'success'|'warning'|'danger'|'info')} [props.tone='default'] - The tone of the tag, which determines its color scheme.
 * @param {React.ReactNode} props.children - The content to be displayed inside the tag.
 * @returns {React.ReactElement} The rendered tag component.
 *
 * @example
 * // Default tag
 * <Tag>Default</Tag>
 *
 * @example
 * // Tag with a success tone
 * <Tag tone="success">Complete</Tag>
 *
 * @example
 * // Tag with a warning tone
 * <Tag tone="warning">In Progress</Tag>
 *
 * @example
 * // Tag with a danger tone
 * <Tag tone="danger">Failed</Tag>
 */
export default function Tag({ tone = 'default', children }) {
  return <span className={`tag tag--${tone}`}>{children}</span>;
}

Tag.propTypes = {
  tone: PropTypes.oneOf(['default', 'success', 'warning', 'danger', 'info']),
  children: PropTypes.node.isRequired
};
