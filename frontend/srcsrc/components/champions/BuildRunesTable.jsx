import PropTypes from 'prop-types';
import '../../styles/champions/champion-build.css';

/**
 * Calculates the winrate percentage.
 *
 * @param {number} wins - The number of wins.
 * @param {number} count - The total number of games.
 * @returns {number} The winrate percentage.
 */
function calculateWinrate(wins, count) {
  if (!count) return 0;
  return Math.round((wins / count) * 100);
}

/**
 * Renders a table of champion build runes.
 *
 * @param {object} props - The component props.
 * @param {string} props.title - The title of the table.
 * @param {Array<object>} props.runes - The list of runes to display.
 * @param {boolean} props.loading - Whether the data is currently loading.
 * @param {React.ReactNode} props.meta - Additional metadata to display in the header.
 * @returns {React.ReactElement} The rendered component.
 */
export default function BuildRunesTable({ title, runes, loading, meta }) {
  if (loading) {
    return (
      <section className="champion-build-section glass-panel">
        <header className="champion-build-section__header">
          <h3>{title}</h3>
          {meta && <span className="champion-build-section__meta">{meta}</span>}
        </header>
        <p className="champion-build-section__empty">Loading runes ...</p>
      </section>
    );
  }

  if (!runes || runes.length === 0) {
    return (
      <section className="champion-build-section glass-panel">
        <header className="champion-build-section__header">
          <h3>{title}</h3>
          {meta && <span className="champion-build-section__meta">{meta}</span>}
        </header>
        <p className="champion-build-section__empty">No rune data available.</p>
      </section>
    );
  }

  return (
    <section className="champion-build-section glass-panel">
      <header className="champion-build-section__header">
        <h3>{title}</h3>
        {meta && <span className="champion-build-section__meta">{meta}</span>}
      </header>
      <div className="champion-build-table-wrapper">
        <table className="table-clean champion-build-table">
          <thead>
            <tr>
              <th>Primary Style</th>
              <th>Sub Style</th>
              <th>Games</th>
              <th>Win Rate</th>
            </tr>
          </thead>
          <tbody>
            {runes.map((rune) => (
              <tr key={`${rune.primaryStyleName}-${rune.subStyleName}-${rune.keystone}`}>
                <td>
                  <div className="champion-build-table__rune">
                    {rune.keystoneIconUrl && (
                      <img src={rune.keystoneIconUrl} alt={rune.keystoneName || rune.primaryStyleName} loading="lazy" />
                    )}
                    <div className="champion-build-table__rune-styles">
                      <span>{rune.primaryStyleName}</span>
                      <span>{rune.keystoneName}</span>
                    </div>
                  </div>
                </td>
                <td>{rune.subStyleName}</td>
                <td>{rune.count.toLocaleString()}</td>
                <td>{calculateWinrate(rune.wins, rune.count)}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

BuildRunesTable.propTypes = {
  title: PropTypes.string.isRequired,
  runes: PropTypes.arrayOf(PropTypes.shape({
    primaryStyleName: PropTypes.string.isRequired,
    subStyleName: PropTypes.string.isRequired,
    keystoneName: PropTypes.string,
    keystoneIconUrl: PropTypes.string,
    keystone: PropTypes.number,
    count: PropTypes.number.isRequired,
    wins: PropTypes.number.isRequired
  })),
  loading: PropTypes.bool,
  meta: PropTypes.node
};
