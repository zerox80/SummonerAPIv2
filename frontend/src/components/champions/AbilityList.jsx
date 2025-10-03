import PropTypes from 'prop-types';
import Tooltip from '../Tooltip.jsx';
import '../../styles/champions/champion-abilities.css';

const KEY_MAP = ['Q', 'W', 'E', 'R'];

function AbilityDescription({ text }) {
  if (!text) return null;

  return (
    <p
      className="ability-card__description"
      dangerouslySetInnerHTML={{ __html: text.replace(/\n/g, '<br />') }}
    />
  );
}

AbilityDescription.propTypes = {
  text: PropTypes.string
};

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
              <AbilityDescription text={passive.description || passive.tooltip} />
              {passive.notes?.length > 0 && (
                <ul className="ability-card__notes">
                  {passive.notes.map((note) => (
                    <li key={note}>{note}</li>
                  ))}
                </ul>
              )}
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
              </div>
              <div className="ability-card__stats">
                {spell.cooldown && (
                  <Tooltip label="Abklingzeit">
                    <span><strong>CD</strong> {spell.cooldown}</span>
                  </Tooltip>
                )}
                {spell.cost && (
                  <Tooltip label="Ressourcenkosten">
                    <span><strong>Kosten</strong> {spell.cost}</span>
                  </Tooltip>
                )}
                {spell.range && (
                  <Tooltip label="Reichweite">
                    <span><strong>Reichweite</strong> {spell.range}</span>
                  </Tooltip>
                )}
                {spell.damage && (
                  <Tooltip label="Schaden">
                    <span><strong>Schaden</strong> {spell.damage}</span>
                  </Tooltip>
                )}
                {spell.scaling && (
                  <Tooltip label="Skalierung">
                    <span><strong>Skalierung</strong> {spell.scaling}</span>
                  </Tooltip>
                )}
              </div>
              <AbilityDescription text={spell.tooltip || spell.description} />
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
    range: PropTypes.string,
    damage: PropTypes.string,
    scaling: PropTypes.string,
    notes: PropTypes.arrayOf(PropTypes.string)
  }))
};
