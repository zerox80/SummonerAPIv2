import { useMemo, useState } from 'react';
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

const IMAGE_BASE = 'https://ddragon.leagueoflegends.com/cdn/14.19.1/img/champion/';

export default function ChampionDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [role, setRole] = useState('ALL');
  const queueId = 420;
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

  return (
    <div className="champion-detail-page">
      <ChampionHero
        name={champion.name}
        title={champion.title}
        tagline={tagline}
        imageSrc={heroImage}
        actions={(
          <div className="champion-detail__controls">
            <SegmentedControl options={ROLE_OPTIONS} value={role} onChange={setRole} />
          </div>
        )}
      />

      <div className="champion-detail__layout">
        <div className="champion-detail__main-column">
          <section className="champion-detail__lore glass-panel">
            <h3>Lore</h3>
            <p>{champion.lore}</p>
          </section>

          {curatedBuild && (
            <CuratedItemGroups title={curatedBuild.title} groups={curatedBuild.groups} />
          )}

          <div className="champion-detail__build-grid">
            <BuildItemsTable
              title="Most Popular Items"
              items={build?.items}
              loading={buildQuery.isLoading}
              meta={buildMeta}
            />

            <BuildRunesTable
              title="Rune Preferences"
              runes={build?.runes}
              loading={buildQuery.isLoading}
              meta={buildMeta}
            />

            <BuildSummonerSpellsTable
              title="Summoner Spells"
              spells={build?.spells}
              loading={buildQuery.isLoading}
              meta={buildMeta}
            />
          </div>
        </div>

        <div className="champion-detail__sidebar">
          <AbilityList passive={abilities.passive} spells={abilities.spells} />
        </div>
      </div>
    </div>
  );
}
