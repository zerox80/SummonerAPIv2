/**
 * Components for displaying curated champion item builds.
 *
 * <p>This module provides components for rendering recommended item builds for a
 * champion. It includes components for displaying individual item icons and for
 * organizing items into groups, such as starting items, core builds, and
 * situational items.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Displays item icons with optional counts</li>
 *   <li>Organizes items into logical groups with titles and subtitles</li>
 *   <li>Supports sequential and non-sequential item displays</li>
 *   <li>Clean and responsive layout</li>
 * </ul>
 *
 * @module components/champions/CuratedItemGroups
 * @author zerox80
 * @version 2.0
 */
import PropTypes from 'prop-types';
import '../../styles/champions/champion-curated-build.css';

const ITEM_IMAGE_BASE = 'https://ddragon.leagueoflegends.com/cdn/14.19.1/img/item/';

/**
 * Renders an icon for a curated item, including its image and an optional count.
 *
 * <p>This component displays an item's image from the Data Dragon CDN and can
 * show a count badge if the item is included multiple times in a build.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {number} props.id - The ID of the item, used to construct the image URL.
 * @param {string} props.name - The name of the item, used for the alt text and title.
 * @param {number} [props.count] - The number of times the item appears in the build. A count is displayed if greater than 1.
 * @returns {React.ReactElement} The rendered item icon component.
 *
 * @example
 * <CuratedItemIcon id={1055} name="Doran's Blade" count={1} />
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
 * Renders a list of curated item groups for a champion build.
 *
 * <p>This component displays recommended item sets, such as starting items, core
 * builds, and situational items. It organizes items into groups, each with its
 * own title and list of items. It can also display items in a sequence to
 * indicate build order.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {string} props.title - The title of the section (e.g., "Recommended Item Build").
 * @param {Array<object>} props.groups - A list of item group objects to display.
 * @returns {React.ReactElement} The rendered curated item groups component.
 *
 * @example
 * const itemGroups = [
 *   {
 *     key: 'starter',
 *     title: 'Starting Items',
 *     items: [{ id: 1055, name: "Doran's Blade" }, { id: 2003, name: 'Health Potion' }],
 *   },
 *   {
 *     key: 'core',
 *     title: 'Core Build',
 *     sequence: true,
 *     items: [{ id: 3071, name: 'Black Cleaver' }, { id: 6630, name: 'Goredrinker' }],
 *   },
 * ];
 * <CuratedItemGroups title="Item Build" groups={itemGroups} />
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
