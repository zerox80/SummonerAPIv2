/**
 * A component for displaying a hero section for a champion.
 *
 * <p>This module provides a hero component that is typically used at the top of a
 * champion's detail page. It displays the champion's splash art, name, title,
 * and other information in a visually appealing layout.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Displays champion splash art with a fallback placeholder</li>
 *   <li>Shows champion name, title, and tagline</li>
 *   <li>Includes a slot for action buttons or links</li>
 *   <li>Accessible with ARIA attributes</li>
 * </ul>
 *
 * @module components/champions/ChampionHero
 * @author zerox80
 * @version 2.0
 */
import PropTypes from 'prop-types';
import '../../styles/champions/champion-hero.css';

/**
 * Renders a hero section for a champion with their name, title, and splash art.
 *
 * <p>This component creates a visually engaging header for a champion's detail page.
 * It features the champion's splash art as a background, with their name, title,
 * and an optional tagline overlaid on top. It also provides a slot for action
 * buttons, such as a link to the champion's official lore page.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {string} props.name - The name of the champion.
 * @param {string} [props.title] - The champion's title (e.g., "the Dark Child").
 * @param {string} [props.tagline] - A short tagline or description for the champion.
 * @param {string} props.imageSrc - The URL of the champion's splash art.
 * @param {React.ReactNode} [props.actions] - Action buttons or links related to the champion.
 * @returns {React.ReactElement} The rendered hero section component.
 *
 * @example
 * <ChampionHero
 *   name="Annie"
 *   title="the Dark Child"
 *   tagline="Annie is a tiny mage with immense pyromantic power."
 *   imageSrc="/path/to/annie.jpg"
 *   actions={<a href="#">View on Universe</a>}
 * />
 */
export default function ChampionHero({ name, title, tagline, imageSrc, actions }) {
  return (
    <section className="champion-hero glass-panel" aria-labelledby="champion-hero-heading">
      <div className="champion-hero__media" role="presentation">
        {imageSrc ? (
          <img src={imageSrc} alt="" loading="lazy" />
        ) : (
          <div className="champion-hero__placeholder" aria-hidden="true" />
        )}
        <div className="champion-hero__media-overlay" />
      </div>
      <div className="champion-hero__content">
        <p className="badge-soft">League Champion</p>
        <h1 id="champion-hero-heading">{name}</h1>
        <h2>{title}</h2>
        {tagline && <p className="champion-hero__tagline">{tagline}</p>}
        {actions && <div className="champion-hero__actions">{actions}</div>}
      </div>
    </section>
  );
}

ChampionHero.propTypes = {
  name: PropTypes.string.isRequired,
  title: PropTypes.string,
  tagline: PropTypes.string,
  imageSrc: PropTypes.string.isRequired,
  actions: PropTypes.node
};
