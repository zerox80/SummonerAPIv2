import { Link } from 'react-router-dom';
import '../styles/brand-mark.css';

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
