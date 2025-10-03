import { useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import SegmentedControl from '../components/SegmentedControl.jsx';
import ChampionHero from '../components/champions/ChampionHero.jsx';
import AbilityList from '../components/champions/AbilityList.jsx';
import BuildItemsTable from '../components/champions/BuildItemsTable.jsx';
import BuildRunesTable from '../components/champions/BuildRunesTable.jsx';
import BuildSummonerSpellsTable from '../components/champions/BuildSummonerSpellsTable.jsx';
import { useChampionDetail, useChampionBuild } from '../hooks/useChampionDetail.js';
import '../styles/champions/champion-detail-page.css';

const QUEUE_OPTIONS = [
  { label: 'Solo/Duo', value: '420' },
  { label: 'Flex', value: '440' },
  { label: 'ARAM', value: '450' }
];

const ROLE_OPTIONS = [
  { label: 'Alle Rollen', value: 'ALL' },
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
  const [queue, setQueue] = useState('420');
  const [role, setRole] = useState('ALL');

  const detailQuery = useChampionDetail(id);
  const buildQuery = useChampionBuild(id, {
    queueId: queue === 'ALL' ? undefined : Number(queue),
    role: role === 'ALL' ? undefined : role
  });

  const champion = detailQuery.data;
  const build = buildQuery.data;

  const heroImage = useMemo(() => {
    if (!champion?.imageFull) return null;
    return `${IMAGE_BASE}${champion.imageFull}`;
  }, [champion]);

  if (detailQuery.isLoading) {
    return <p className="champions-state">Champion wird geladen ...</p>;
  }

  if (detailQuery.isError || !champion) {
    return (
      <div className="champions-state error">
        <p>{detailQuery.error?.message || 'Champion konnte nicht geladen werden.'}</p>
        <button type="button" className="cta-button" onClick={() => navigate(-1)}>Zurück</button>
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
            <SegmentedControl options={QUEUE_OPTIONS} value={queue} onChange={setQueue} />
            <SegmentedControl options={ROLE_OPTIONS} value={role} onChange={setRole} />
          </div>
        )}
      />

      <div className="champion-detail__layout">
        <section className="champion-detail__lore">
          <h3>Geschichte</h3>
          <p>{champion.lore}</p>
        </section>

        <AbilityList passive={champion.passive} spells={champion.spells} />
      </div>

      <BuildItemsTable title="Beliebteste Items" items={build?.items} />
      <BuildRunesTable title="Runenpräferenzen" runes={build?.runes} />
      <BuildSummonerSpellsTable title="Beschwörerzauber" spells={build?.spells} />
    </div>
  );
}
