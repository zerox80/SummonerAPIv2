import PropTypes from 'prop-types';
import Tooltip from '../Tooltip.jsx';
import '../../styles/champions/champion-abilities.css';

const KEY_MAP = ['Q', 'W', 'E', 'R'];

export default function AbilityList({ passive, spells }) {
  return (
    <section className="champion-abilities glass-panel">
      <header className="champion-abilities__header">
        <h3>Fähigkeitenübersicht</h3>
        <p>Verstehe das Kit und die Spielweise des Champions.</p>
      </header>
      <div className="champion-abilities__grid">
        {passive && (
          <article className="ability-card ability-card--passive">
            <div className="ability-card__icon">
              <img src={`https://ddragon.leagueoflegends.com/cdn/14.19.1/img/passive/${passive.imageFull}`} alt={passive.name} loading="lazy" />
            </div>
            <div className="ability-card__content">
              <div className="ability-card__meta">
                <span className="ability-card__hotkey">Passive</span>
                <h4>{passive.name}</h4>
              </div>
              <p>{passive.description}</p>
            </div>
          </article>
        )}
        {spells?.map((spell, index) => (
          <article key={spell.id} className="ability-card">
            <div className="ability-card__icon">
              <img src={`https://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/${spell.imageFull}`} alt={spell.name} loading="lazy" />
            </div>
            <div className="ability-card__content">
              <div className="ability-card__meta">
                <span className="ability-card__hotkey">{KEY_MAP[index] || ''}</span>
                <h4>{spell.name}</h4>
                <div className="ability-card__meta-tags">
                  {spell.cooldown && (
                    <Tooltip label="Abklingzeit">
                      <span className="ability-card__cooldown">CD: {spell.cooldown}</span>
                    </Tooltip>
                  )}
                  {spell.cost && (
                    <Tooltip label="Kosten">
                      <span className="ability-card__cost">Kosten: {spell.cost}</span>
                    </Tooltip>
                  )}
                </div>
              </div>
              <p>{spell.tooltip || spell.description}</p>
              {spell.notes?.length > 0 && (
                <ul className="ability-card__notes">
                  {spell.notes.map((note) => (
                    <li key={note}>{note}</li>
                  ))}
                </ul>
              )}
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

AbilityList.propTypes = {
  passive: PropTypes.shape({
    name: PropTypes.string,
    description: PropTypes.string,
    imageFull: PropTypes.string
  }),
  spells: PropTypes.arrayOf(PropTypes.shape({
    id: PropTypes.string,
    name: PropTypes.string,
    tooltip: PropTypes.string,
    description: PropTypes.string,
    cooldown: PropTypes.string,
    cost: PropTypes.string,
    imageFull: PropTypes.string,
    notes: PropTypes.arrayOf(PropTypes.string)
  }))
};
