import { useEffect, useMemo, useRef, useState } from 'react';
import PropTypes from 'prop-types';
import { useQuery } from '@tanstack/react-query';
import { api } from '../../api/client.js';
import useDebouncedValue from '../../hooks/useDebouncedValue.js';
import '../../styles/summoner/search-panel.css';

const PLACEHOLDER_IDS = [
  'HideonBush#KR1',
  'G2 Caps#1323'
];

export default function SearchPanel({ onSubmit, initialValue = '', isLoading }) {
  const [input, setInput] = useState(initialValue);
  const [focused, setFocused] = useState(false);
  const [placeholderIndex, setPlaceholderIndex] = useState(0);
  const debounced = useDebouncedValue(input, 220);
  const formRef = useRef(null);

  const suggestionsQuery = useQuery({
    queryKey: ['suggestions', debounced],
    queryFn: () => api.suggestions({ query: debounced }),
    enabled: debounced.length >= 2
  });

  useEffect(() => {
    const id = setInterval(() => {
      setPlaceholderIndex((prev) => (prev + 1) % PLACEHOLDER_IDS.length);
    }, 3800);
    return () => clearInterval(id);
  }, []);

  useEffect(() => {
    setInput(initialValue);
  }, [initialValue]);

  const suggestions = useMemo(() => {
    if (!Array.isArray(suggestionsQuery.data)) return [];
    return suggestionsQuery.data.slice(0, 5);
  }, [suggestionsQuery.data]);

  const suggestionsVisible = focused && suggestions.length > 0;

  const handleSubmit = (event) => {
    event.preventDefault();
    const value = input.trim();
    if (value.length === 0) return;
    onSubmit(value);
  };

  const handleSelectSuggestion = (value) => {
    setInput(value);
    onSubmit(value);
    setFocused(false);
  };

  return (
    <section className={`summoner-search glass-panel ${suggestionsVisible ? 'is-showing-suggestions' : ''}`}>
      <div className="summoner-search__headline">
        <p className="badge-soft">Ultra-fast Summoner Lookup</p>
        <h1>
          Find your <span className="gradient-text">Summoner Profile</span>
        </h1>
        <p className="summoner-search__subtitle">
          Analyze matches, ranks, and champion performance.
        </p>
      </div>
      <form ref={formRef} className="summoner-search__form" onSubmit={handleSubmit} autoComplete="off">
        <div className={`summoner-search__input ${focused ? 'is-focused' : ''}`}>
          <span className="summoner-search__prefix">Riot ID</span>
          <input
            value={input}
            onChange={(event) => setInput(event.target.value)}
            onFocus={() => setFocused(true)}
            onBlur={() => setTimeout(() => setFocused(false), 180)}
            type="text"
            placeholder={PLACEHOLDER_IDS[placeholderIndex]}
            spellCheck="false"
            aria-label="Enter Riot ID"
            aria-haspopup="listbox"
            aria-expanded={suggestionsVisible}
          />
          <button type="submit" className="cta-button" disabled={isLoading}>
            {isLoading ? 'Searching ...' : 'Search'}
          </button>
        </div>
        {suggestionsVisible && (
          <div className="summoner-search__suggestions" role="listbox">
            {suggestions.map((item) => (
              <button
                key={item.riotId}
                type="button"
                className="summoner-search__suggestion"
                onMouseDown={(event) => event.preventDefault()}
                onClick={() => handleSelectSuggestion(item.riotId)}
              >
                <span className="summoner-search__suggestion-icon" aria-hidden>
                  <img
                    src={item.profileIconUrl || `https://ddragon.leagueoflegends.com/cdn/14.19.1/img/profileicon/${item.profileIconId}.png`}
                    alt="Profile icon"
                    loading="lazy"
                  />
                </span>
                <span>
                  <strong>{item.riotId}</strong>
                  <small>Level {item.summonerLevel}</small>
                </span>
                <span className="summoner-search__suggestion-arrow" aria-hidden>
                  ↗
                </span>
              </button>
            ))}
          </div>
        )}
      </form>
    </section>
  );
}

SearchPanel.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  initialValue: PropTypes.string,
  isLoading: PropTypes.bool
};
