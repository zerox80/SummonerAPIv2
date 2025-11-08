import PropTypes from 'prop-types';
import '../styles/load-state.css';

/**
 * Renders a loading state indicator.
 *
 * @param {object} props - The component props.
 * @param {string} props.message - The message to display while loading.
 * @returns {React.ReactElement} The rendered component.
 */
export function LoadingState({ message = 'Loading ...' }) {
  return (
    <div className="load-state">
      <div className="load-state__spinner" aria-hidden />
      <p>{message}</p>
    </div>
  );
}

/**
 * Renders an error state message.
 *
 * @param {object} props - The component props.
 * @param {string} props.message - The error message to display.
 * @param {Function} props.onRetry - A callback function to retry the action.
 * @returns {React.ReactElement} The rendered component.
 */
export function ErrorState({ message, onRetry }) {
  return (
    <div className="load-state load-state--error" role="alert">
      <p>{message}</p>
      {onRetry && (
        <button type="button" className="cta-button" onClick={onRetry}>
          Try again
        </button>
      )}
    </div>
  );
}

/**
 * Renders an empty state message.
 *
 * @param {object} props - The component props.
 * @param {string} props.title - The title of the empty state message.
 * @param {string} props.description - The description of the empty state message.
 * @returns {React.ReactElement} The rendered component.
 */
export function EmptyState({ title, description }) {
  return (
    <div className="load-state load-state--empty">
      <div className="load-state__icon" aria-hidden>🕊️</div>
      <h3>{title}</h3>
      {description && <p>{description}</p>}
    </div>
  );
}

LoadingState.propTypes = {
  message: PropTypes.string
};

ErrorState.propTypes = {
  message: PropTypes.string.isRequired,
  onRetry: PropTypes.func
};

EmptyState.propTypes = {
  title: PropTypes.string.isRequired,
  description: PropTypes.string
};
