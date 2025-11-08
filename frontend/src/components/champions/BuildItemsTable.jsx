/**
 * A component for displaying champion build items in a table format.
 *
 * <p>This module provides a table component that displays a list of items,
 * such as starting items, core items, or boots, along with their usage
 * statistics (number of games and win rate).</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Displays item icon, name, game count, and win rate</li>
 *   <li>Handles loading and empty states</li>
 *   <li>Customizable title and metadata</li>
 *   <li>Clean and responsive table layout</li>
 * </ul>
 *
 * @module components/champions/BuildItemsTable
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
 * Renders a table of champion build items with their statistics.
 *
 * <p>This component displays a list of items in a table, showing the item's icon
 * and name, the number of games it was used in, and its win rate. It is used
 * for displaying various item sets, such as starting items, core builds, and boots.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {string} props.title - The title of the table (e.g., "Starting Items").
 * @param {Array<object>} props.items - The list of item objects to display in the table.
 * @param {boolean} [props.loading=false] - A flag to indicate if the data is currently loading.
 * @param {React.ReactNode} [props.meta] - Additional content, such as controls or filters, to display in the header.
 * @returns {React.ReactElement} The rendered item build table component.
 *
 * @example
 * const items = [
 *   { itemId: 1055, name: "Doran's Blade", imageUrl: "...", count: 100, wins: 60 },
 *   { itemId: 1056, name: "Doran's Ring", imageUrl: "...", count: 50, wins: 25 },
 * ];
 * <BuildItemsTable title="Starting Items" items={items} />
 */
export default function BuildItemsTable({ title, items, loading, meta }) {
  if (loading) {
    return (
      <section className="champion-build-section glass-panel">
        <header className="champion-build-section__header">
          <h3>{title}</h3>
          {meta && <span className="champion-build-section__meta">{meta}</span>}
        </header>
        <p className="champion-build-section__empty">Loading build data ...</p>
      </section>
    );
  }

  if (!items || items.length === 0) {
    return (
      <section className="champion-build-section glass-panel">
        <header className="champion-build-section__header">
          <h3>{title}</h3>
          {meta && <span className="champion-build-section__meta">{meta}</span>}
        </header>
        <p className="champion-build-section__empty">No build data available.</p>
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
              <th>Item</th>
              <th>Games</th>
              <th>Win Rate</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.itemId || item.name}>
                <td>
                  <div className="champion-build-table__item">
                    {item.imageUrl && (
                      <img src={item.imageUrl} alt={item.name} loading="lazy" />
                    )}
                    <span>{item.name}</span>
                  </div>
                </td>
                <td>{item.count.toLocaleString()}</td>
                <td>{calculateWinrate(item.wins, item.count)}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

BuildItemsTable.propTypes = {
  title: PropTypes.string.isRequired,
  items: PropTypes.arrayOf(PropTypes.shape({
    itemId: PropTypes.number,
    name: PropTypes.string.isRequired,
    imageUrl: PropTypes.string,
    count: PropTypes.number.isRequired,
    wins: PropTypes.number.isRequired
  })),
  loading: PropTypes.bool,
  meta: PropTypes.node
};
