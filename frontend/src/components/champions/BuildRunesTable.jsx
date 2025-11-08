/**
 * A component for displaying champion rune builds in a table format.
 *
 * <p>This module provides a table component that displays a list of popular
 * rune combinations for a champion, along with their usage statistics
 * (number of games and win rate).</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Displays keystone icon, primary and sub-style names, game count, and win rate</li>
 *   <li>Handles loading and empty states</li>
 *   <li>Customizable title and metadata</li>
 *   <li>Clean and responsive table layout</li>
 * </ul>
 *
 * @module components/champions/BuildRunesTable
 * @author zerox80
 * @version 2.0
 */
import PropTypes from 'prop-types';
import '../../styles/champions/champion-build.css';

/**
 * Calculates the win rate as a percentage from wins and total count.
 *
 * @param {number} wins - The number of wins.
 * @param {number} count - The total number of games played.
 * @returns {number} The win rate percentage, rounded to the nearest integer. Returns 0 if count is 0.
 *
 * @example
 * const winrate = calculateWinrate(10, 15); // returns 67
 */
function calculateWinrate(wins, count) {
  if (!count) return 0;
  return Math.round((wins / count) * 100);
}

/**
 * Renders a table of champion rune builds with their statistics.
 *
 * <p>This component displays a list of rune combinations in a table, showing the
 * primary and sub rune styles, keystone, game count, and win rate. It is used
 * to provide players with insights into the most effective rune setups for a champion.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {string} props.title - The title of the table (e.g., "Most Popular Runes").
 * @param {Array<object>} props.runes - The list of rune combination objects to display.
 * @param {boolean} [props.loading=false] - A flag to indicate if the data is currently loading.
 * @param {React.ReactNode} [props.meta] - Additional content, such as controls or filters, to display in the header.
 * @returns {React.ReactElement} The rendered rune build table component.
 *
 * @example
 * const runes = [
 *   { primaryStyleName: "Precision", subStyleName: "Domination", keystoneName: "Press the Attack", keystoneIconUrl: "...", count: 100, wins: 60 },
 *   { primaryStyleName: "Sorcery", subStyleName: "Inspiration", keystoneName: "Arcane Comet", keystoneIconUrl: "...", count: 50, wins: 25 },
 * ];
 * <BuildRunesTable title="Rune Builds" runes={runes} />
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

