/**
 * BrandMark component that displays the application logo and navigation.
 * 
 * <p>This component renders the SummonerAPI branding with a Greek letter sigma glyph
 * and the application name. It serves as a clickable link to the homepage and
 * includes proper accessibility attributes for screen readers.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Clickable navigation to homepage</li>
 *   <li>Accessibility support with ARIA labels</li>
 *   <li>Responsive design with CSS styling</li>
 *   <li>Bilingual support (German label for homepage)</li>
 * </ul>
 * 
 * @component BrandMark
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */

import { Link } from 'react-router-dom';
import '../styles/brand-mark.css';

/**
 * Renders the BrandMark component with logo and navigation link.
 * 
 * <p>This function returns a JSX element containing the application branding.
 * The component includes a sigma glyph (Σ) and the application name with subtitle,
 * all wrapped in a Link component for navigation to the homepage.</p>
 * 
 * @returns {JSX.Element} The rendered BrandMark component
 * 
 * @example
 * // Basic usage in a header
 * <header>
 *   <BrandMark />
 * </header>
 */
export default function BrandMark() {
  return (
    <Link to="/" className="brand-mark" aria-label="SummonerAPI v2 Startseite">
      <span className="brand-mark__glyph" aria-hidden>Σ</span>
      <span className="brand-mark__text">
        <span className="brand-mark__title">SummonerAPI</span>
        <span className="brand-mark__subtitle">League Companion</span>
      </span>
    </Link>
  );
}
