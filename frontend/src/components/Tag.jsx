import PropTypes from 'prop-types';
import '../styles/components/tag.css';

export default function Tag({ tone = 'default', children }) {
  return <span className={`tag tag--${tone}`}>{children}</span>;
}

Tag.propTypes = {
  tone: PropTypes.oneOf(['default', 'success', 'warning', 'danger', 'info']),
  children: PropTypes.node.isRequired
};
