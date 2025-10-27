import { useState } from 'react';
import PropTypes from 'prop-types';
import Tooltip from '../Tooltip.jsx';
import '../../styles/champions/champion-abilities.css';

/**
 * Mapping array for spell hotkeys in order.
 * 
 * @constant
 * @type {Array<string>}
 * @default ['Q', 'W', 'E', 'R']
 */
const KEY_MAP = ['Q', 'W', 'E', 'R'];

/**
 * Component for rendering ability descriptions with HTML support.
 * 
 * <p>This component handles the rendering of ability descriptions, converting
 * newline characters to HTML breaks and safely rendering HTML content.
 * It returns null if no text is provided.</p>
 * 
 * @param {Object} props - Component props
 * @param {string} props.text - The ability description text to render
 * @returns {React.Element|null} Rendered description paragraph or null
 */
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

/**
 * Component for displaying champion abilities in an interactive grid layout.
 * 
 * <p>This component renders both passive and active abilities for a champion,
 * with expandable descriptions, ability statistics, and interactive tooltips.
 * It supports collapsible content for long descriptions and includes comprehensive
 * ability information like cooldown, cost, range, damage, and scaling.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Interactive ability cards with expand/collapse functionality</li>
 *   <li>Automatic content length detection for optimal display</li>
 *   <li>Tooltip support for ability statistics</li>
 *   <li>Responsive grid layout for all screen sizes</li>
 *   <li>Accessibility support with proper ARIA attributes</li>
 *   <li>Lazy loading for ability images</li>
 * </ul>
 * 
 * @param {Object} props - Component props
 * @param {Object} props.passive - Passive ability data
 * @param {string} props.passive.name - Passive ability name
 * @param {string} [props.passive.description] - Passive ability description
 * @param {string} [props.passive.tooltip] - Passive ability tooltip (fallback for description)
 * @param {string} props.passive.imageFull - Passive ability image filename
 * @param {Array<string>} [props.passive.notes] - Additional notes about the passive ability
 * @param {Array<Object>} props.spells - Array of active spell data
 * @param {string} props.spells[].id - Unique spell identifier
 * @param {string} props.spells[].name - Spell name
 * @param {string} [props.spells[].description] - Spell description
 * @param {string} [props.spells[].tooltip] - Spell tooltip (fallback for description)
 * @param {string} props.spells[].imageFull - Spell image filename
 * @param {string} [props.spells[].cooldown] - Spell cooldown information
 * @param {string} [props.spells[].cost] - Spell resource cost
 * @param {string} [props.spells[].range] - Spell range information
 * @param {string} [props.spells[].damage] - Spell damage information
 * @param {string} [props.spells[].scaling] - Spell scaling information
 * @param {Array<string>} [props.spells[].notes] - Additional notes about the spell
 * @param {boolean} [props.showHeader=true] - Whether to show the section header
 * @returns {React.Element} Rendered abilities section
 * 
 * @example
 * // Basic usage with passive and spells
 * <AbilityList 
 *   passive={passiveData}
 *   spells={spellData}
 *   showHeader={true}
 * />
 * 
 * @example
 * // Minimal usage without header
 * <AbilityList 
 *   passive={passiveData}
 *   spells={spellData}
 *   showHeader={false}
 * />
 */
export default function AbilityList({ passive, spells, showHeader = true }) {
  const [expandedAbilities, setExpandedAbilities] = useState(() => new Set());

  /**
   * Toggles the expanded state of an ability.
   * 
   * @param {string} key - The ability key to toggle ('passive' or spell ID)
   */
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

  /**
   * Checks if an ability is currently expanded.
   * 
   * @param {string} key - The ability key to check
   * @returns {boolean} Whether the ability is expanded
   */
  const isExpanded = (key) => expandedAbilities.has(key);

  /**
   * Renders detailed ability information with expand/collapse functionality.
   * 
   * @param {string} key - The ability identifier
   * @param {string} description - The ability description text
   * @param {Array<string>} notes - Additional ability notes
   * @returns {React.Element} Rendered ability details
   */
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
      {showHeader && (
        <header className="champion-abilities__header">
          <h3>Ability Overview</h3>
          <p>Understand the champion kit and how it plays.</p>
        </header>
      )}
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
  })),
  showHeader: PropTypes.bool
};
