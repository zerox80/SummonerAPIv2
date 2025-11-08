/**
 * Components for displaying various load states such as loading, error, and empty.
 *
 * <p>This module provides a set of reusable components to handle common UI states
 * during data fetching and rendering. These components ensure a consistent user
 * experience by providing clear feedback to the user about the current state of
 * the application.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Consistent styling for loading, error, and empty states</li>
 *   <li>Customizable messages and titles</li>
 *   <li>Optional retry functionality for error states</li>
 *   <li>Accessibility support with ARIA roles</li>
 * </ul>
 *
 * @module components/LoadState
 * @author zerox80
 * @version 2.0
 */
import PropTypes from 'prop-types';
import '../styles/load-state.css';

/**
 * Renders a loading state indicator with a customizable message.
 *
 * <p>This component displays a spinner and a message to indicate that data is being
 * fetched or processed. It provides immediate feedback to the user that an
 * action is in progress.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {string} [props.message='Loading ...'] - The message to display next to the spinner.
 * @returns {React.ReactElement} The rendered loading state component.
 *
 * @example
 * // Default loading state
 * <LoadingState />
 *
 * @example
 * // Loading state with a custom message
 * <LoadingState message="Fetching summoner data..." />
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
 * Renders an error state message with an optional retry button.
 *
 * <p>This component informs the user that an error has occurred. It can include
 * a "Try again" button that triggers a callback function, allowing the user to
 * re-attempt the failed action.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {string} props.message - The error message to display.
 * @param {Function} [props.onRetry] - A callback function to be executed when the "Try again" button is clicked.
 * @returns {React.ReactElement} The rendered error state component.
 *
 * @example
 * // Error state with a simple message
 * <ErrorState message="Failed to load data." />
 *
 * @example
 * // Error state with a retry button
 * <ErrorState message="Failed to load data." onRetry={() => console.log('Retrying...')} />
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
 * Renders an empty state message with a title and optional description.
 *
 * <p>This component is used to indicate that there is no data to display. It can
 * be used in lists, tables, or other content areas that are conditionally
 * rendered based on the availability of data.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {string} props.title - The main title of the empty state message.
 * @param {string} [props.description] - An optional description to provide more context.
 * @returns {React.ReactElement} The rendered empty state component.
 *
 * @example
 * // Empty state with a title only
 * <EmptyState title="No matches found" />
 *
 * @example
 * // Empty state with a title and description
 * <EmptyState title="No matches found" description="There are no recent matches for this summoner." />
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
