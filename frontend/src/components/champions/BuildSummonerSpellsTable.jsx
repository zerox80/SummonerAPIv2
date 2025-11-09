
import PropTypes from 'prop-types';
import '../../styles/champions/champion-build.css';


function calculateWinrate(wins, count) {
  if (!count) return 0;
  return Math.round((wins / count) * 100);
}


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
