/**
 * A component for displaying champion summoner spell combinations in a table format.
 *
 * <p>This module provides a table component that displays a list of popular
 * summoner spell combinations for a champion, along with their usage statistics
 * (number of games and win rate).</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Displays spell icons, names, game count, and win rate</li>
 *   <li>Handles loading and empty states</li>
 *   <li>Customizable title and metadata</li>
 *   <li>Clean and responsive table layout</li>
 * </ul>
 *
 * @module components/champions/BuildSummonerSpellsTable
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
 * Renders a table of champion summoner spell combinations with their statistics.
 *
 * <p>This component displays a list of summoner spell combinations in a table,
 * showing the spell icons and names, the number of games played with that
 * combination, and its win rate. It helps players choose the most effective
 * summoner spells for a champion.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {string} props.title - The title of the table (e.g., "Most Popular Summoner Spells").
 * @param {Array<object>} props.spells - The list of summoner spell combination objects to display.
 * @param {boolean} [props.loading=false] - A flag to indicate if the data is currently loading.
 * @param {React.ReactNode} [props.meta] - Additional content, such as controls or filters, to display in the header.
 * @returns {React.ReactElement} The rendered summoner spells table component.
 *
 * @example
 * const spells = [
 *   { spell1Name: "Flash", spell2Name: "Ignite", spell1IconUrl: "...", spell2IconUrl: "...", count: 100, wins: 60 },
 *   { spell1Name: "Flash", spell2Name: "Teleport", spell1IconUrl: "...", spell2IconUrl: "...", count: 50, wins: 25 },
 * ];
 * <BuildSummonerSpellsTable title="Summoner Spells" spells={spells} />
 */
export default function BuildSummonerSpellsTable({ title, spells, loading, meta }) {
  if (loading) {
    return (
      <section className="champion-build-section glass-panel">
        <header className="champion-build-section__header">
          <h3>{title}</h3>
          {meta && <span className="champion-build-section__meta">{meta}</span>}
        </header>
        <p className="champion-build-section__empty">Loading summoner spells ...</p>
      </section>
    );
  }

  if (!spells || spells.length === 0) {
    return (
      <section className="champion-build-section glass-panel">
        <header className="champion-build-section__header">
          <h3>{title}</h3>
          {meta && <span className="champion-build-section__meta">{meta}</span>}
        </header>
        <p className="champion-build-section__empty">No summoner-spell data available.</p>
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
              <th>Summoner Spells</th>
              <th>Games</th>
              <th>Win Rate</th>
            </tr>
          </thead>
          <tbody>
            {spells.map((entry) => (
              <tr key={`${entry.spell1Name}-${entry.spell2Name}`}>
                <td>
                  <div className="champion-build-table__spell">
                    {entry.spell1IconUrl && <img src={entry.spell1IconUrl} alt={entry.spell1Name} loading="lazy" />}
                    {entry.spell2IconUrl && <img src={entry.spell2IconUrl} alt={entry.spell2Name} loading="lazy" />}
                    <span>{entry.spell1Name} + {entry.spell2Name}</span>
                  </div>
                </td>
                <td>{entry.count.toLocaleString()}</td>
                <td>{calculateWinrate(entry.wins, entry.count)}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

BuildSummonerSpellsTable.propTypes = {
  title: PropTypes.string.isRequired,
  spells: PropTypes.arrayOf(PropTypes.shape({
    spell1Name: PropTypes.string.isRequired,
    spell2Name: PropTypes.string.isRequired,
    spell1IconUrl: PropTypes.string,
    spell2IconUrl: PropTypes.string,
    count: PropTypes.number.isRequired,
    wins: PropTypes.number.isRequired
  })),
  loading: PropTypes.bool,
  meta: PropTypes.node
};
