
import PropTypes from 'prop-types';
import '../styles/load-state.css';


export function LoadingState({ message = 'Loading ...' }) {
  return (
    <div className="load-state">
      <div className="load-state__spinner" aria-hidden />
      <p>{message}</p>
    </div>
  );
}


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
