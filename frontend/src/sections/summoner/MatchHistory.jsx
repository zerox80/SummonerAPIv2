import { useMemo, useState, useCallback } from 'react';
import PropTypes from 'prop-types';
import SegmentedControl from '../../components/SegmentedControl.jsx';
import Tag from '../../components/Tag.jsx';
import { formatDuration, relativeGameTime, formatQueueById, formatKDA, roleLabel } from '../../utils/formatters.js';
import '../../styles/summoner/match-history.css';

const DEFAULT_DDRAGON_VERSION = '15.20.1';
const DEFAULT_ITEM_BASE = `https://ddragon.leagueoflegends.com/cdn/${DEFAULT_DDRAGON_VERSION}/img/item/`;
const DEFAULT_SPELL_BASE = `https://ddragon.leagueoflegends.com/cdn/${DEFAULT_DDRAGON_VERSION}/img/spell/`;
const DEFAULT_CHAMPION_BASE = `https://ddragon.leagueoflegends.com/cdn/${DEFAULT_DDRAGON_VERSION}/img/champion/`;

const QUEUE_FILTER_OPTIONS = [
  { label: 'All', value: 'ALL' },
  { label: 'Solo/Duo', value: '420' },
  { label: 'Flex', value: '440' },
  { label: 'ARAM', value: '450' }
];

const RESULT_FILTER_OPTIONS = [
  { label: 'All', value: 'ALL' },
  { label: 'Wins', value: 'WIN' },
  { label: 'Losses', value: 'LOSS' }
];

const ROLE_FILTER_OPTIONS = [
  { label: 'All Roles', value: 'ALL' },
  { label: 'Top', value: 'TOP' },
  { label: 'Jungle', value: 'JUNGLE' },
  { label: 'Mid', value: 'MIDDLE' },
  { label: 'ADC', value: 'BOTTOM' },
  { label: 'Support', value: 'UTILITY' }
];

const POSITION_ORDER = {
  TOP: 0,
  JUNGLE: 1,
  MIDDLE: 2,
  BOTTOM: 3,
  UTILITY: 4
};

const SUMMONER_SPELL_DATA = {
  1: { key: 'SummonerBoost', name: 'Cleanse' },
  3: { key: 'SummonerExhaust', name: 'Exhaust' },
  4: { key: 'SummonerFlash', name: 'Flash' },
  6: { key: 'SummonerHaste', name: 'Ghost' },
  7: { key: 'SummonerHeal', name: 'Heal' },
  11: { key: 'SummonerSmite', name: 'Smite' },
  12: { key: 'SummonerTeleport', name: 'Teleport' },
  13: { key: 'SummonerMana', name: 'Clarity' },
  14: { key: 'SummonerDot', name: 'Ignite' },
  21: { key: 'SummonerBarrier', name: 'Barrier' },
  32: { key: 'SummonerSnowball', name: 'Mark' },
  39: { key: 'SummonerUnleashedSmite', name: 'Unleashed Smite' }
};

const KEYSTONE_BASE_URL = 'https://ddragon.leagueoflegends.com/cdn/img/perk-images/Styles';

const KEYSTONE_DATA = {
  8005: { icon: `${KEYSTONE_BASE_URL}/Precision/PressTheAttack/PressTheAttack.png`, name: 'Press the Attack' },
  8008: { icon: `${KEYSTONE_BASE_URL}/Precision/LethalTempo/LethalTempoTemp.png`, name: 'Lethal Tempo' },
  8010: { icon: `${KEYSTONE_BASE_URL}/Precision/Conqueror/Conqueror.png`, name: 'Conqueror' },
  8021: { icon: `${KEYSTONE_BASE_URL}/Precision/FleetFootwork/FleetFootwork.png`, name: 'Fleet Footwork' },
  8112: { icon: `${KEYSTONE_BASE_URL}/Domination/Electrocute/Electrocute.png`, name: 'Electrocute' },
  8124: { icon: `${KEYSTONE_BASE_URL}/Domination/Predator/Predator.png`, name: 'Predator' },
  8128: { icon: `${KEYSTONE_BASE_URL}/Domination/DarkHarvest/DarkHarvest.png`, name: 'Dark Harvest' },
  8214: { icon: `${KEYSTONE_BASE_URL}/Sorcery/SummonAery/SummonAery.png`, name: 'Summon Aery' },
  8229: { icon: `${KEYSTONE_BASE_URL}/Sorcery/ArcaneComet/ArcaneComet.png`, name: 'Arcane Comet' },
  8230: { icon: `${KEYSTONE_BASE_URL}/Sorcery/PhaseRush/PhaseRush.png`, name: 'Phase Rush' },
  8437: { icon: `${KEYSTONE_BASE_URL}/Resolve/GraspOfTheUndying/GraspOfTheUndying.png`, name: 'Grasp of the Undying' },
  8439: { icon: `${KEYSTONE_BASE_URL}/Resolve/VeteranAftershock/VeteranAftershock.png`, name: 'Aftershock' },
  8465: { icon: `${KEYSTONE_BASE_URL}/Resolve/Guardian/Guardian.png`, name: 'Guardian' },
  8351: { icon: `${KEYSTONE_BASE_URL}/Inspiration/GlacialAugment/GlacialAugment.png`, name: 'Glacial Augment' },
  8360: { icon: `${KEYSTONE_BASE_URL}/Inspiration/UnsealedSpellbook/UnsealedSpellbook.png`, name: 'Unsealed Spellbook' },
  8369: { icon: `${KEYSTONE_BASE_URL}/Inspiration/FirstStrike/FirstStrike.png`, name: 'First Strike' }
};

function resolveChampionImage(championSquares, championBase, championName, championId) {
  if (!championName) return null;
  const championByName = championSquares?.[championName];
  if (championByName) return championByName;
  const idKey = championId != null ? String(championId) : null;
  if (idKey && championSquares?.[idKey]) return championSquares[idKey];
  return `${championBase}${championName}.png`;
}

function applyFilters(matches, summoner, filters) {
  if (!Array.isArray(matches)) return [];
  
  return matches.filter((match) => {
    if (!match?.info?.participants) return false;
    const participant = match.info.participants.find((p) => p?.puuid === summoner?.puuid);
    if (!participant) return false;

    if (filters.queue !== 'ALL' && `${match.info.queueId}` !== filters.queue) return false;
    if (filters.result === 'WIN' && !participant.win) return false;
    if (filters.result === 'LOSS' && participant.win) return false;
    if (filters.role !== 'ALL' && participant.teamPosition !== filters.role) return false;

    return true;
  });
}

function sortTeamParticipants(participants = []) {
  return [...participants].sort((a, b) => {
    const roleDiff = (POSITION_ORDER[a.teamPosition] ?? 99) - (POSITION_ORDER[b.teamPosition] ?? 99);
    if (roleDiff !== 0) return roleDiff;
    const aScore = (a.kills || 0) + (a.assists || 0);
    const bScore = (b.kills || 0) + (b.assists || 0);
    return bScore - aScore;
  });
}

function getSpellAsset(spellId, spellBase) {
  if (!spellId || !SUMMONER_SPELL_DATA[spellId]) return null;
  const { key, name } = SUMMONER_SPELL_DATA[spellId];
  return {
    src: `${spellBase}${key}.png`,
    name
  };
}

function getKeystoneAsset(perks, runeBase) {
  const keystoneId = perks?.styles?.[0]?.selections?.[0]?.perk;
  const keystone = KEYSTONE_DATA[keystoneId];
  if (!keystone) return null;
  const icon = typeof keystone.icon === 'string' ? keystone.icon : '';
  const isAbsolute = icon.startsWith('http://') || icon.startsWith('https://');
  return {
    src: isAbsolute ? icon : `${runeBase}${icon}`,
    name: keystone.name
  };
}

function sumTeamStat(participants = [], extractor) {
  return participants.reduce((total, current) => total + (extractor(current) || 0), 0);
}

export default function MatchHistory({ matches, summoner, filters, onFiltersChange, fetchMore, hasMore, isFetchingMore, championSquares, bases }) {
  const filteredMatches = useMemo(() => applyFilters(matches, summoner, filters), [matches, summoner, filters]);
  const [expandedMatchId, setExpandedMatchId] = useState(null);

  const itemBase = bases?.item || DEFAULT_ITEM_BASE;
  const spellBase = bases?.spell || DEFAULT_SPELL_BASE;
  const runeBase = bases?.img || 'https://ddragon.leagueoflegends.com/cdn/img/';
  const championBase = bases?.champSquare
    || (bases?.img ? `${bases.img}champion/` : DEFAULT_CHAMPION_BASE);

  const handleToggle = useCallback((matchId) => {
    setExpandedMatchId((prev) => (prev === matchId ? null : matchId));
  }, []);

  const handleSummaryKey = useCallback((event, matchId) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      handleToggle(matchId);
    }
  }, [handleToggle]);

  return (
    <section className="match-history glass-panel">
      <header className="match-history__header">
        <div>
          <p className="badge-soft">Match Timeline</p>
          <h3>Match History</h3>
        </div>
        <div className="match-history__filters">
          <SegmentedControl 
            options={QUEUE_FILTER_OPTIONS} 
            value={filters.queue} 
            onChange={(value) => onFiltersChange({ ...filters, queue: value })} 
            size="sm"
          />
          <SegmentedControl 
            options={RESULT_FILTER_OPTIONS} 
            value={filters.result} 
            onChange={(value) => onFiltersChange({ ...filters, result: value })} 
            size="sm"
          />
          <SegmentedControl 
            options={ROLE_FILTER_OPTIONS} 
            value={filters.role} 
            onChange={(value) => onFiltersChange({ ...filters, role: value })} 
            size="sm"
          />
        </div>
      </header>

      {filteredMatches.length === 0 && (
        <div className="match-history__empty">
          <p>No matches found</p>
          <span>Try changing filters or play more games</span>
        </div>
      )}

      <div className="match-history__list">
        {filteredMatches.map((match) => {
          const participant = match.info.participants.find((p) => p?.puuid === summoner?.puuid);
          if (!participant) return null;

          const championImg = resolveChampionImage(
            championSquares,
            championBase,
            participant.championName,
            participant.championId
          );
          const gameTime = relativeGameTime(match.info.gameEndTimestamp || match.info.gameCreation);
          const duration = formatDuration(Math.round((match.info.gameDuration || 0) / 1000));
          const kda = formatKDA(participant.kills, participant.deaths, participant.assists);
          const cs = (participant.totalMinionsKilled || 0) + (participant.neutralMinionsKilled || 0);
          const matchId = match.metadata?.matchId || `${match.info?.gameId || match.info?.gameCreation}-${participant.puuid}`;
          const isExpanded = expandedMatchId === matchId;

          let expandedContent = null;
          if (isExpanded) {
            const byTeam = (match.info.participants || []).reduce((acc, player) => {
              if (!acc[player.teamId]) acc[player.teamId] = [];
              acc[player.teamId].push(player);
              return acc;
            }, {});

            const teamOrder = participant.teamId === 100 ? [100, 200] : [participant.teamId, participant.teamId === 100 ? 200 : 100];
            const teams = teamOrder.map((teamId) => {
              const players = sortTeamParticipants(byTeam[teamId]);
              const totalKills = sumTeamStat(players, (p) => p.kills);
              const totalDamage = sumTeamStat(players, (p) => p.totalDamageDealtToChampions);
              const won = players.length > 0 ? players[0].win : false;
              return {
                teamId,
                totalKills,
                totalDamage,
                won,
                players
              };
            });

            const allyTeam = teams[0];
            const allyKills = allyTeam?.totalKills || 0;
            const allyDamage = allyTeam?.totalDamage || 0;
            const killParticipation = allyKills > 0 ? Math.round(((participant.kills + participant.assists) / allyKills) * 100) : 0;
            const damageShare = allyDamage > 0 ? Math.round(((participant.totalDamageDealtToChampions || 0) / allyDamage) * 100) : 0;
            const visionScore = participant.visionScore || 0;
            const gold = participant.goldEarned || 0;
            const durationMinutes = Math.max(1, Math.round((match.info.gameDuration || 0) / 60));
            const gpm = Math.round(gold / durationMinutes);
            const spellOne = getSpellAsset(participant.summoner1Id, spellBase);
            const spellTwo = getSpellAsset(participant.summoner2Id, spellBase);
            const keystone = getKeystoneAsset(participant.perks, runeBase);
            const items = [participant.item0, participant.item1, participant.item2, participant.item3, participant.item4, participant.item5];
            const trinket = participant.item6;

            expandedContent = (
              <div className="match-card__details">
                <div className="match-card__detail-grid">
                  <div className="match-card__build">
                    <p className="match-card__section-label">Loadout</p>
                    <div className="match-card__build-row">
                      <div className="match-card__build-group">
                        <span className="match-card__meta-label">Summoner Spells</span>
                        <div className="match-card__spell-pair">
                          {[spellOne, spellTwo].map((spell, index) => (
                            <div key={index} className="match-card__spell">
                              {spell ? (
                                <img src={spell.src} alt={spell.name} title={spell.name} loading="lazy" />
                              ) : (
                                <span className="match-card__spell-placeholder" />
                              )}
                            </div>
                          ))}
                        </div>
                      </div>
                      <div className="match-card__build-group">
                        <span className="match-card__meta-label">Keystone</span>
                        <div className="match-card__keystone">
                          {keystone ? (
                            <img src={keystone.src} alt={keystone.name} title={keystone.name} loading="lazy" />
                          ) : (
                            <span className="match-card__spell-placeholder" />
                          )}
                        </div>
                      </div>
                    </div>
                    <div className="match-card__items">
                      {items.map((itemId, index) => (
                        <div key={index} className={`match-card__item-slot ${itemId ? '' : 'is-empty'}`}>
                          {itemId ? (
                            <img
                              src={`${itemBase}${itemId}.png`}
                              alt={`Item ${itemId}`}
                              title={`Item ${itemId}`}
                              loading="lazy"
                            />
                          ) : null}
                        </div>
                      ))}
                      <div className={`match-card__item-slot match-card__item-slot--trinket ${trinket ? '' : 'is-empty'}`}>
                        {trinket ? (
                          <img
                            src={`${itemBase}${trinket}.png`}
                            alt={`Trinket ${trinket}`}
                            title={`Trinket ${trinket}`}
                            loading="lazy"
                          />
                        ) : null}
                      </div>
                    </div>
                  </div>
                  <div className="match-card__stat-grid">
                    <div className="match-card__detail-stat">
                      <span className="label">KP</span>
                      <span className="value">{killParticipation}%</span>
                    </div>
                    <div className="match-card__detail-stat">
                      <span className="label">Damage Share</span>
                      <span className="value">{damageShare}%</span>
                    </div>
                    <div className="match-card__detail-stat">
                      <span className="label">Vision Score</span>
                      <span className="value">{visionScore}</span>
                    </div>
                    <div className="match-card__detail-stat">
                      <span className="label">Gold / min</span>
                      <span className="value">{gpm}</span>
                    </div>
                    <div className="match-card__detail-stat">
                      <span className="label">Total Damage</span>
                      <span className="value">{(participant.totalDamageDealtToChampions || 0).toLocaleString()}</span>
                    </div>
                    <div className="match-card__detail-stat">
                      <span className="label">Wards Placed</span>
                      <span className="value">{participant.wardsPlaced ?? 0}</span>
                    </div>
                  </div>
                </div>
                <div className="match-card__scoreboard">
                  {teams.map((team) => (
                    <div
                      key={team.teamId}
                      className={`match-card__team ${team.teamId === participant.teamId ? 'is-ally' : 'is-enemy'}`}
                    >
                      <div className="match-card__team-header">
                        <div>
                          <p className="match-card__team-label">{team.teamId === 100 ? 'Blue Team' : 'Red Team'}</p>
                          <span className="match-card__team-result">{team.won ? 'Victory' : 'Defeat'} · {team.totalKills} Kills</span>
                        </div>
                      </div>
                      <div className="match-card__team-body">
                        {team.players.map((player) => {
                          const playerChampionImg = resolveChampionImage(
                            championSquares,
                            championBase,
                            player.championName,
                            player.championId
                          );
                          const playerSpells = [
                            getSpellAsset(player.summoner1Id, spellBase),
                            getSpellAsset(player.summoner2Id, spellBase)
                          ];
                          const playerItems = [player.item0, player.item1, player.item2, player.item3, player.item4, player.item5];
                          const playerTrinket = player.item6;
                          const playerCs = (player.totalMinionsKilled || 0) + (player.neutralMinionsKilled || 0);
                          const isSelf = player.puuid === participant.puuid;

                          return (
                            <div key={`${player.puuid}-${player.championName}`} className={`match-player-row ${isSelf ? 'is-self' : ''}`}>
                              <div className="match-player-row__champion">
                                <img src={playerChampionImg} alt={player.championName} loading="lazy" />
                                <div className="match-player-row__identity">
                                  <span className="name">{player.riotIdGameName || player.summonerName || 'Unknown'}</span>
                                  <span className="role">{roleLabel(player.teamPosition)}</span>
                                </div>
                              </div>
                              <div className="match-player-row__spells">
                                {playerSpells.map((spell, index) => (
                                  <div key={index} className="match-player-row__spell">
                                    {spell ? <img src={spell.src} alt={spell.name} title={spell.name} loading="lazy" /> : null}
                                  </div>
                                ))}
                              </div>
                              <div className="match-player-row__kda">
                                <span className="score">{player.kills}/{player.deaths}/{player.assists}</span>
                                <span className="ratio">{formatKDA(player.kills, player.deaths, player.assists)}</span>
                              </div>
                              <div className="match-player-row__cs">
                                <span className="score">{playerCs}</span>
                                <span className="label">CS</span>
                              </div>
                              <div className="match-player-row__items">
                                {playerItems.map((itemId, index) => (
                                  <div key={index} className={`match-player-row__item ${itemId ? '' : 'is-empty'}`}>
                                    {itemId ? (
                                      <img
                                        src={`${itemBase}${itemId}.png`}
                                        alt={`Item ${itemId}`}
                                        title={`Item ${itemId}`}
                                        loading="lazy"
                                      />
                                    ) : null}
                                  </div>
                                ))}
                                <div className={`match-player-row__item match-player-row__item--trinket ${playerTrinket ? '' : 'is-empty'}`}>
                                  {playerTrinket ? (
                                    <img
                                      src={`${itemBase}${playerTrinket}.png`}
                                      alt={`Trinket ${playerTrinket}`}
                                      title={`Trinket ${playerTrinket}`}
                                      loading="lazy"
                                    />
                                  ) : null}
                                </div>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            );
          }

          return (
            <article
              key={matchId}
              className={`match-card ${participant.win ? 'is-win' : 'is-loss'} ${isExpanded ? 'is-expanded' : ''}`}
            >
              <div
                className="match-card__summary"
                role="button"
                tabIndex={0}
                aria-expanded={isExpanded}
                onClick={() => handleToggle(matchId)}
                onKeyDown={(event) => handleSummaryKey(event, matchId)}
              >
                <div className="match-card__champion">
                  <img src={championImg} alt={participant.championName} loading="lazy" />
                  <span className="match-card__champion-name">{participant.championName}</span>
                </div>
                <div className="match-card__info">
                  <div className="match-card__queue">
                    <Tag tone={participant.win ? 'success' : 'danger'}>
                      {participant.win ? 'Win' : 'Loss'}
                    </Tag>
                    <span>{formatQueueById(match.info.queueId)}</span>
                  </div>
                  <div className="match-card__time">
                    <span>{gameTime}</span>
                    <span>·</span>
                    <span>{duration}</span>
                  </div>
                </div>
                <div className="match-card__stats">
                  <div className="match-card__stat">
                    <span className="label">KDA</span>
                    <span className="value">{participant.kills}/{participant.deaths}/{participant.assists}</span>
                  </div>
                  <div className="match-card__stat">
                    <span className="label">CS</span>
                    <span className="value">{cs}</span>
                  </div>
                  <div className="match-card__stat">
                    <span className="label">Gold</span>
                    <span className="value">{(participant.goldEarned || 0).toLocaleString()}</span>
                  </div>
                  <div className="match-card__stat">
                    <span className="label">Damage</span>
                    <span className="value">{(participant.totalDamageDealtToChampions || 0).toLocaleString()}</span>
                  </div>
                </div>
                <div className="match-card__role">
                  <Tag tone="info">{roleLabel(participant.teamPosition)}</Tag>
                  {typeof match.info.lpChange === 'number' && (
                    <Tag tone={match.info.lpChange >= 0 ? 'success' : 'danger'}>
                      {match.info.lpChange >= 0 ? '+' : ''}{match.info.lpChange} LP
                    </Tag>
                  )}
                </div>
                <div className="match-card__toggle" aria-hidden="true">
                  <svg className={`match-card__chevron ${isExpanded ? 'is-open' : ''}`} width="18" height="18" viewBox="0 0 24 24" fill="none">
                    <path d="M6 9l6 6 6-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </div>
              </div>
              {expandedContent}
            </article>
          );
        })}
      </div>

      {hasMore && (
        <div className="match-history__load-more">
          <button 
            type="button" 
            className="cta-button" 
            onClick={fetchMore} 
            disabled={isFetchingMore}
          >
            {isFetchingMore ? 'Loading more matches ...' : 'Load more matches'}
          </button>
        </div>
      )}
    </section>
  );
}

MatchHistory.propTypes = {
  matches: PropTypes.arrayOf(PropTypes.object),
  summoner: PropTypes.shape({
    puuid: PropTypes.string
  }),
  filters: PropTypes.shape({
    queue: PropTypes.string,
    result: PropTypes.string,
    role: PropTypes.string
  }).isRequired,
  onFiltersChange: PropTypes.func.isRequired,
  fetchMore: PropTypes.func.isRequired,
  hasMore: PropTypes.bool,
  isFetchingMore: PropTypes.bool,
  championSquares: PropTypes.object,
  bases: PropTypes.object
};
