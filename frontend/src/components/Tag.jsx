import PropTypes from 'prop-types';
import '../styles/components/tag.css';

/**
 * Renders a tag component.
 *
 * @param {object} props - The component props.
 * @param {('default'|'success'|'warning'|'danger'|'info')} [props.tone='default'] - The tone of the tag.
 * @param {React.ReactNode} props.children - The content of the tag.
 * @returns {React.ReactElement} The rendered component.
 */
export default function Tag({ tone = 'default', children }) {
  return <span className={`tag tag--${tone}`}>{children}</span>;
}

Tag.propTypes = {
  tone: PropTypes.oneOf(['default', 'success', 'warning', 'danger', 'info']),
  children: PropTypes.node.isRequired
};
