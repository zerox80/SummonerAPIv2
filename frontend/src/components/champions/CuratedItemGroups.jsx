import PropTypes from 'prop-types';
import '../../styles/champions/champion-curated-build.css';

const ITEM_IMAGE_BASE = 'https://ddragon.leagueoflegends.com/cdn/14.19.1/img/item/';

/**
 * Renders an icon for a curated item.
 *
 * @param {object} props - The component props.
 * @param {number} props.id - The ID of the item.
 * @param {string} props.name - The name of the item.
 * @param {number} props.count - The count of the item.
 * @returns {React.ReactElement} The rendered component.
 */
function CuratedItemIcon({ id, name, count }) {
  const src = `${ITEM_IMAGE_BASE}${id}.png`;
  return (
    <div className="curated-build__item-icon" title={name}>
      <img src={src} alt={name} loading="lazy" />
      {count && count > 1 && <span className="curated-build__item-count">×{count}</span>}
    </div>
  );
}

CuratedItemIcon.propTypes = {
  id: PropTypes.number.isRequired,
  name: PropTypes.string.isRequired,
  count: PropTypes.number
};

/**
 * Renders a list of curated item groups.
 *
 * @param {object} props - The component props.
 * @param {string} props.title - The title of the section.
 * @param {Array<object>} props.groups - The list of item groups to display.
 * @returns {React.ReactElement} The rendered component.
 */
export default function CuratedItemGroups({ title, groups }) {
  return (
    <section className="curated-build glass-panel">
      <header className="curated-build__header">
        <h3>{title}</h3>
      </header>
      <div className="curated-build__groups">
        {groups.map((group) => (
          <article key={group.key} className={`curated-build__group${group.sequence ? ' curated-build__group--sequence' : ''}`}>
            <div className="curated-build__group-headings">
              <p className="curated-build__group-title">{group.title}</p>
              {group.subtitle && <p className="curated-build__group-subtitle">{group.subtitle}</p>}
              {group.meta && <p className="curated-build__group-meta">{group.meta}</p>}
            </div>
            <div className="curated-build__items">
              {group.items.map((item, index) => (
                <div key={`${item.id}-${index}`} className="curated-build__item-wrapper">
                  <CuratedItemIcon id={item.id} name={item.name} count={item.count} />
                  <span className="curated-build__item-label">{item.name}</span>
                  {group.sequence && index < group.items.length - 1 && (
                    <span className="curated-build__item-arrow" aria-hidden="true">➜</span>
                  )}
                </div>
              ))}
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

CuratedItemGroups.propTypes = {
  title: PropTypes.string,
  groups: PropTypes.arrayOf(PropTypes.shape({
    key: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    subtitle: PropTypes.string,
    meta: PropTypes.string,
    sequence: PropTypes.bool,
    items: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.number.isRequired,
      name: PropTypes.string.isRequired,
      count: PropTypes.number
    })).isRequired
  })).isRequired
};
