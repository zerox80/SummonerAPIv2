/**
 * Page component for displaying detailed champion information.
 *
 * <p>This module provides a comprehensive champion detail page that displays extensive
 * information about a specific champion including their abilities, base stats,
 * recommended builds, lore, and performance insights. It features tabbed navigation,
 * filtering options, and interactive elements for exploring champion data.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Comprehensive champion overview with stats and insights</li>
 *   <li>Tabbed navigation for different content sections</li>
 *   <li>Build recommendations with filtering by role and queue</li>
 *   <li>Ability information with expandable descriptions</li>
 *   <li>Champion lore with expand/collapse functionality</li>
 *   <li>Interactive stat visualizations</li>
 *   <li>Responsive design with smooth scrolling</li>
 * </ul>
 *
 * @module pages/ChampionDetailPage
 * @author zerox80
 * @version 2.0
 */

import { useCallback, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import SegmentedControl from '../components/SegmentedControl.jsx';
import ChampionHero from '../components/champions/ChampionHero.jsx';
import AbilityList from '../components/champions/AbilityList.jsx';
import BuildItemsTable from '../components/champions/BuildItemsTable.jsx';
import BuildRunesTable from '../components/champions/BuildRunesTable.jsx';
import BuildSummonerSpellsTable from '../components/champions/BuildSummonerSpellsTable.jsx';
import CuratedItemGroups from '../components/champions/CuratedItemGroups.jsx';
import { useChampionDetail, useChampionBuild } from '../hooks/useChampionDetail.js';
import { formatQueueById, roleLabel } from '../utils/formatters.js';
import { mergeChampionAbilities } from '../utils/championSpells.js';
import { CURATED_BUILDS } from '../data/curatedBuilds.js';
import '../styles/champions/champion-detail-page.css';

const ROLE_OPTIONS = [
  { label: 'All Roles', value: 'ALL' },
  { label: 'Top', value: 'TOP' },
  { label: 'Jungle', value: 'JUNGLE' },
  { label: 'Mid', value: 'MIDDLE' },
  { label: 'ADC', value: 'BOTTOM' },
  { label: 'Support', value: 'UTILITY' }
];

const QUEUE_OPTIONS = [
  { label: 'Ranked Solo', value: '420' },
  { label: 'Ranked Flex', value: '440' },
  { label: 'ARAM', value: '450' }
];

const BUILD_VIEW_OPTIONS = [
  { label: 'Items', value: 'items' },
  { label: 'Runes', value: 'runes' },
  { label: 'Summoners', value: 'spells' }
];

const TAB_OPTIONS = [
  { id: 'overview', label: 'Overview', icon: '📊' },
  { id: 'builds', label: 'Builds', icon: '⚔️' },
  { id: 'abilities', label: 'Abilities', icon: '✨' },
  { id: 'lore', label: 'Lore', icon: '📖' }
];

const IMAGE_BASE = 'https://ddragon.leagueoflegends.com/cdn/14.19.1/img/champion/';

/**
 * Clamps a score to a range of 0-10.
 *
 * @param {number} score - The score to clamp.
 * @returns {number} The clamped score.
 */
function clampScore(score) {
  if (score == null) return 0;
  return Math.max(0, Math.min(score, 10));
}

/**
 * Gets a difficulty label based on a score.
 *
 * @param {number} score - The difficulty score.
 * @returns {string} The difficulty label.
 */
function getDifficultyLabel(score) {
  if (score == null) return 'Unknown';
  if (score <= 3) return 'Beginner Friendly';
  if (score <= 6) return 'Moderate';
  if (score <= 8) return 'Challenging';
  return 'Expert Only';
}

/**
 * Gets the damage profile based on attack and magic scores.
 *
 * @param {object} info - The champion info object.
 * @returns {string|null} The damage profile or null.
 */
function getDamageProfile(info) {
  if (!info) return null;
  const { attack = 0, magic = 0 } = info;
  if (attack === 0 && magic === 0) return null;
  if (attack >= magic + 2) return 'Physical';
  if (magic >= attack + 2) return 'Magical';
  return 'Hybrid';
}

/**
 * Formats a stat value to a string with a specified number of digits.
 *
 * @param {number} value - The value to format.
 * @param {number} [digits=0] - The number of digits to show after the decimal.
 * @returns {string} The formatted value.
 */
function formatStatValue(value, digits = 0) {
  if (value == null) return '—';
  if (typeof value !== 'number' || Number.isNaN(value)) return value;
  return value.toLocaleString(undefined, {
    minimumFractionDigits: digits,
    maximumFractionDigits: digits
  });
}

/**
 * Renders the champion detail page.
 *
 * @returns {React.ReactElement} The rendered component.
 */
export default function ChampionDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [role, setRole] = useState('ALL');
  const [queue, setQueue] = useState('420');
  const [buildView, setBuildView] = useState('items');
  const [activeTab, setActiveTab] = useState('overview');
  const [loreExpanded, setLoreExpanded] = useState(false);
  const queueId = Number(queue);
  const normalizedId = (id || '').toLowerCase();

  const detailQuery = useChampionDetail(id);
  const buildQuery = useChampionBuild(id, {
    queueId,
    role: role === 'ALL' ? undefined : role
  });

  const champion = detailQuery.data;
  const build = buildQuery.data;
  const curatedBuild = CURATED_BUILDS[normalizedId];

  const buildMeta = useMemo(() => {
    if (!build) return null;
    const tokens = [];
    if (build.patch) tokens.push(`Patch ${build.patch}`);
    if (build.queueId) tokens.push(formatQueueById(build.queueId));
    if (build.role) tokens.push(roleLabel(build.role));
    return tokens.length > 0 ? tokens.join(' • ') : null;
  }, [build]);

  const heroImage = useMemo(() => {
    if (!champion?.imageFull) return null;
    return `${IMAGE_BASE}${champion.imageFull}`;
  }, [champion]);

  const abilities = useMemo(() => mergeChampionAbilities(champion), [champion]);

  const quickInsights = useMemo(() => {
    if (!champion) return [];
    const tags = champion.tags?.length ? champion.tags.join(' • ') : 'Unassigned';
    const difficulty = getDifficultyLabel(champion.info?.difficulty);
    const damageProfile = getDamageProfile(champion.info);
    const range = champion.stats?.attackrange;
    const insights = [
      { label: 'Primary Roles', value: tags },
      { label: 'Resource', value: champion.partype || '—' },
      { label: 'Difficulty', value: difficulty }
    ];

    if (damageProfile) insights.push({ label: 'Damage Profile', value: damageProfile });
    if (insights.length < 4 && typeof range === 'number') {
      insights.push({ label: 'Attack Range', value: `${Math.round(range)} units` });
    }

    return insights.slice(0, 4);
  }, [champion]);

  const statScores = useMemo(() => {
    if (!champion?.info) return [];
    return [
      { label: 'Attack', value: clampScore(champion.info.attack) },
      { label: 'Magic', value: clampScore(champion.info.magic) },
      { label: 'Defense', value: clampScore(champion.info.defense) },
      { label: 'Difficulty', value: clampScore(champion.info.difficulty) }
    ].map((entry) => ({
      ...entry,
      percent: Math.min(100, Math.round((entry.value / 10) * 100))
    }));
  }, [champion]);

  const baseStats = useMemo(() => {
    if (!champion?.stats) return [];
    const stats = champion.stats;
    const usesResource = champion.partype && champion.partype !== 'None';

    return [
      { label: 'Base HP', value: formatStatValue(stats.hp) },
      {
        label: usesResource ? 'Base Mana' : 'Resource',
        value: usesResource ? formatStatValue(stats.mp) : (champion.partype || 'None')
      },
      { label: 'Attack Damage', value: formatStatValue(stats.attackdamage) },
      { label: 'Attack Speed', value: formatStatValue(stats.attackspeed, 2) },
      { label: 'Move Speed', value: formatStatValue(stats.movespeed) },
      { label: 'Attack Range', value: formatStatValue(stats.attackrange) }
    ];
  }, [champion]);

  const handleQuickNav = useCallback((targetId) => {
    const section = document.getElementById(targetId);
    if (section) {
      section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }, []);

  if (detailQuery.isLoading) {
    return <p className="champions-state">Loading champion ...</p>;
  }

  if (detailQuery.isError || !champion) {
    return (
      <div className="champions-state error">
        <p>{detailQuery.error?.message || 'Failed to load champion.'}</p>
        <button type="button" className="cta-button" onClick={() => navigate(-1)}>Back</button>
      </div>
    );
  }

  const tagline = champion.tags?.join(' • ');
  const shouldClampLore = champion.lore && champion.lore.length > 420;

  return (
    <div className="champion-detail-page">
      <ChampionHero
        name={champion.name}
        title={champion.title}
        tagline={tagline}
        imageSrc={heroImage}
        actions={(
          <div className="champion-detail__control-groups">
            <div className="champion-detail__control-group">
              <p className="champion-detail__control-label">Preferred Role</p>
              <SegmentedControl options={ROLE_OPTIONS} value={role} onChange={setRole} size="sm" />
            </div>
            <div className="champion-detail__control-group">
              <p className="champion-detail__control-label">Queue Type</p>
              <SegmentedControl options={QUEUE_OPTIONS} value={queue} onChange={setQueue} size="sm" />
            </div>
          </div>
        )}
      />

      <nav className="champion-detail__tab-nav glass-panel" aria-label="Champion sections">
        {TAB_OPTIONS.map((tab) => (
          <button 
            key={tab.id} 
            type="button" 
            className={`champion-detail__tab-button ${activeTab === tab.id ? 'is-active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            <span className="champion-detail__tab-icon">{tab.icon}</span>
            <span className="champion-detail__tab-label">{tab.label}</span>
          </button>
        ))}
      </nav>

      <div className="champion-detail__content-area">
        {activeTab === 'overview' && (
          <div className="champion-detail__tab-content">
            <div className="champion-detail__compact-layout">
              <section className="champion-detail__overview glass-panel">
                <div className="champion-detail__overview-top">
                  {champion.blurb && (
                    <p className="champion-detail__overview-blurb">{champion.blurb}</p>
                  )}
                  <div className="champion-detail__insights">
                    {quickInsights.map((insight) => (
                      <article key={insight.label} className="champion-detail__insight">
                        <p className="champion-detail__insight-label">{insight.label}</p>
                        <p className="champion-detail__insight-value">{insight.value}</p>
                      </article>
                    ))}
                  </div>
                </div>
                {statScores.length > 0 && (
                  <div className="champion-detail__stat-grid">
                    {statScores.map((stat) => (
                      <div key={stat.label} className="champion-detail__stat-card">
                        <div className="champion-detail__stat-header">
                          <span>{stat.label}</span>
                          <span>{stat.value}/10</span>
                        </div>
                        <div className="champion-detail__stat-bar">
                          <span className="champion-detail__stat-bar-fill" style={{ '--fill': `${stat.percent}%` }} />
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </section>

              {baseStats.length > 0 && (
                <section className="champion-detail__section">
                  <div className="champion-detail__section-header glass-panel">
                    <div>
                      <p className="champion-detail__section-eyebrow">Vitals</p>
                      <h3>Base Stats</h3>
                    </div>
                  </div>
                  <div className="champion-detail__stats glass-panel">
                    <dl className="champion-detail__stats-grid">
                      {baseStats.map((entry) => (
                        <div key={entry.label} className="champion-detail__stat-item">
                          <dt>{entry.label}</dt>
                          <dd>{entry.value}</dd>
                        </div>
                      ))}
                    </dl>
                  </div>
                </section>
              )}
            </div>
          </div>
        )}

        {activeTab === 'builds' && (
          <div className="champion-detail__tab-content">
            <section className="champion-detail__section">
              <div className="champion-detail__section-header glass-panel">
                <div>
                  <p className="champion-detail__section-eyebrow">Meta Insights</p>
                  <h3>Optimal Recommendations</h3>
                </div>
                <SegmentedControl options={BUILD_VIEW_OPTIONS} value={buildView} onChange={setBuildView} size="sm" />
              </div>
              <div className="champion-detail__build-content">
                {buildView === 'items' && (
                  <BuildItemsTable
                    title="Most Popular Items"
                    items={build?.items}
                    loading={buildQuery.isLoading}
                    meta={buildMeta}
                  />
                )}
                {buildView === 'runes' && (
                  <BuildRunesTable
                    title="Rune Preferences"
                    runes={build?.runes}
                    loading={buildQuery.isLoading}
                    meta={buildMeta}
                  />
                )}
                {buildView === 'spells' && (
                  <BuildSummonerSpellsTable
                    title="Summoner Spells"
                    spells={build?.spells}
                    loading={buildQuery.isLoading}
                    meta={buildMeta}
                  />
                )}
              </div>
            </section>

            {curatedBuild && (
              <section className="champion-detail__section">
                <div className="champion-detail__section-header glass-panel">
                  <div>
                    <p className="champion-detail__section-eyebrow">Expert Path</p>
                    <h3>{curatedBuild.title}</h3>
                  </div>
                </div>
                <CuratedItemGroups title={null} groups={curatedBuild.groups} />
              </section>
            )}
          </div>
        )}

        {activeTab === 'abilities' && (
          <div className="champion-detail__tab-content">
            <section className="champion-detail__section champion-detail__abilities">
              <div className="champion-detail__section-header glass-panel">
                <div>
                  <p className="champion-detail__section-eyebrow">Kit Primer</p>
                  <h3>Ability Overview</h3>
                </div>
              </div>
              <AbilityList passive={abilities.passive} spells={abilities.spells} showHeader={false} />
            </section>
          </div>
        )}

        {activeTab === 'lore' && (
          <div className="champion-detail__tab-content">
            <section className="champion-detail__section">
              <div className="champion-detail__section-header glass-panel">
                <div>
                  <p className="champion-detail__section-eyebrow">Story</p>
                  <h3>Lore</h3>
                </div>
              </div>
              <div className="champion-detail__lore glass-panel">
                <p className={`champion-detail__lore-text${shouldClampLore && !loreExpanded ? ' is-clamped' : ''}`}>{champion.lore}</p>
                {shouldClampLore && (
                  <button
                    type="button"
                    className="champion-detail__lore-toggle"
                    onClick={() => setLoreExpanded((prev) => !prev)}
                    aria-expanded={loreExpanded}
                  >
                    {loreExpanded ? 'Show less' : 'Show more'}
                  </button>
                )}
              </div>
            </section>
          </div>
        )}
      </div>
    </div>
  );
}
