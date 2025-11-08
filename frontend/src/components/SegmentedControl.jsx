/**
 * A reusable segmented control component for switching between different options or views.
 *
 * <p>This module provides an accessible and customizable segmented control that
 * functions as a tab list, allowing users to select one option from a set. It is
 * commonly used for filtering content, switching between views, or selecting a
 * specific mode.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Accessible by default with ARIA roles (tablist, tab)</li>
 *   <li>Customizable options with labels and values</li>
 *   <li>Support for different sizes (sm, md, lg)</li>
 *   <li>State management handled by parent component</li>
 * </ul>
 *
 * @module components/SegmentedControl
 * @author zerox80
 * @version 2.0
 */
import PropTypes from 'prop-types';
import '../styles/components/segmented-control.css';

/**
 * Renders an accessible segmented control for switching between a set of options.
 *
 * <p>This component displays a group of buttons where only one can be active at a time.
 * It is designed to be used for filtering, sorting, or switching between different
 * views. The component is fully controlled, with the parent component managing the
 * active state.</p>
 *
 * @component
 * @param {object} props - Component props.
 * @param {Array<{label: React.ReactNode, value: string}>} props.options - An array of option objects, each with a `label` to display and a unique `value`.
 * @param {string} props.value - The value of the currently selected option.
 * @param {Function} props.onChange - A callback function that is invoked with the value of the selected option when a button is clicked.
 * @param {('sm'|'md'|'lg')} [props.size='md'] - The size of the component, controlling padding and font size.
 * @returns {React.ReactElement} A group of toggle buttons that function as a tab list.
 *
 * @example
 * // Example of a simple segmented control
 * const [selectedValue, setSelectedValue] = useState('option1');
 * const options = [
 *   { label: 'Option 1', value: 'option1' },
 *   { label: 'Option 2', value: 'option2' },
 * ];
 *
 * <SegmentedControl
 *   options={options}
 *   value={selectedValue}
 *   onChange={setSelectedValue}
 *   size="md"
 * />
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
