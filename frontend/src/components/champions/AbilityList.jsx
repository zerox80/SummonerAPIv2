import { useState } from 'react';
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
  const [expandedAbilities, setExpandedAbilities] = useState(() => new Set());

  const toggleAbility = (key) => {
    setExpandedAbilities((prev) => {
      const next = new Set(prev);
      if (next.has(key)) {
        next.delete(key);
      } else {
        next.add(key);
      }
      return next;
    });
  };

  const isExpanded = (key) => expandedAbilities.has(key);

  const renderAbilityDetails = (key, description, notes) => {
    const hasNotes = Array.isArray(notes) && notes.length > 0;
    const descriptionLength = description ? description.replace(/<[^>]*>/g, '').length : 0;
    const hasExtendedContent = descriptionLength > 260 || hasNotes;
    const expanded = isExpanded(key);

    return (
      <>
        <div className={`ability-card__details${expanded ? ' is-expanded' : ' is-collapsed'}`}>
          <AbilityDescription text={description} />
          {hasNotes && (
            <ul className="ability-card__notes">
              {notes.map((note) => (
                <li key={note}>{note}</li>
              ))}
            </ul>
          )}
        </div>
        {hasExtendedContent && (
          <button
            type="button"
            className="ability-card__toggle"
            onClick={() => toggleAbility(key)}
            aria-expanded={expanded}
          >
            {expanded ? 'Show less' : 'Show more'}
          </button>
        )}
      </>
    );
  };

  return (
    <section className="champion-abilities glass-panel">
      <header className="champion-abilities__header">
        <h3>Ability Overview</h3>
        <p>Understand the champion kit and how it plays.</p>
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
              {renderAbilityDetails('passive', passive.description || passive.tooltip, passive.notes)}
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
                  <Tooltip label="Cooldown">
                    <span><strong>CD</strong> {spell.cooldown}</span>
                  </Tooltip>
                )}
                {spell.cost && (
                  <Tooltip label="Resource Cost">
                    <span><strong>Cost</strong> {spell.cost}</span>
                  </Tooltip>
                )}
                {spell.range && (
                  <Tooltip label="Range">
                    <span><strong>Range</strong> {spell.range}</span>
                  </Tooltip>
                )}
                {spell.damage && (
                  <Tooltip label="Damage">
                    <span><strong>Damage</strong> {spell.damage}</span>
                  </Tooltip>
                )}
                {spell.scaling && (
                  <Tooltip label="Scaling">
                    <span><strong>Scaling</strong> {spell.scaling}</span>
                  </Tooltip>
                )}
              </div>
              {renderAbilityDetails(spell.id, spell.tooltip || spell.description, spell.notes)}
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
