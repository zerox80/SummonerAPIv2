import PropTypes from 'prop-types';
import '../../styles/champions/champion-hero.css';

/**
 * Renders a hero section for a champion.
 *
 * @param {object} props - The component props.
 * @param {string} props.name - The name of the champion.
 * @param {string} props.title - The title of the champion.
 * @param {string} props.tagline - A tagline for the champion.
 * @param {string} props.imageSrc - The URL of the champion's image.
 * @param {React.ReactNode} props.actions - Action buttons or links for the hero section.
 * @returns {React.ReactElement} The rendered component.
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
