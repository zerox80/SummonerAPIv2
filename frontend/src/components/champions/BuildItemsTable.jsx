import PropTypes from 'prop-types';
import '../../styles/champions/champion-build.css';

function calculateWinrate(wins, count) {
  if (!count) return 0;
  return Math.round((wins / count) * 100);
}

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
