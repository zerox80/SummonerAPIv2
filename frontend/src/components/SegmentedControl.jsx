import PropTypes from 'prop-types';
import '../styles/components/segmented-control.css';

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
