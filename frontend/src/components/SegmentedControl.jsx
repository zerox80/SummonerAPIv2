import PropTypes from 'prop-types';
import '../styles/components/segmented-control.css';

/**
 * Renders an accessible segmented control for switching between options.
 *
 * @param {Object} props - Component props.
 * @param {Array<{label: React.ReactNode, value: string}>} props.options - Ordered list of selectable options.
 * @param {string} props.value - Currently selected option value.
 * @param {Function} props.onChange - Callback invoked with the next option value when a button is pressed.
 * @param {('sm'|'md'|'lg')} [props.size='md'] - Visual density preset that controls padding and font size.
 * @returns {React.ReactElement} Group of toggle buttons that behave like a tab list.
 */
export default function SegmentedControl({ options, value, onChange, size = 'md' }) {
  return (
    <div className={`segmented-control segmented-control--${size}`} role="tablist">
      {options.map((option) => {
        const active = option.value === value;
        return (
          <button
            key={option.value}
            type="button"
            className={`segmented-control__option${active ? ' is-active' : ''}`}
            onClick={() => onChange(option.value)}
            role="tab"
            aria-selected={active}
          >
            {option.label}
          </button>
        );
      })}
    </div>
  );
}

SegmentedControl.propTypes = {
  options: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.node.isRequired,
    value: PropTypes.string.isRequired
  })).isRequired,
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  size: PropTypes.oneOf(['sm', 'md', 'lg'])
};
