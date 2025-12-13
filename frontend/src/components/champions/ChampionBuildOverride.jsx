import { memo } from 'react';

import '../../styles/champions/champion-build-override.css';

const DDRAGON_CDN = 'https://ddragon.leagueoflegends.com/cdn';
const DEFAULT_DDRAGON_VERSION = import.meta.env.VITE_DDRAGON_VERSION;
const CDN_IMG_BASE = `${DDRAGON_CDN}/img/`;
const TRANSPARENT_PIXEL =
    'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';
const EMPTY_ARRAY = Object.freeze([]);

const SPELL_KEYS_BY_NAME = {
    Flash: 'SummonerFlash',
    Teleport: 'SummonerTeleport',
    Ignite: 'SummonerDot',
    Heal: 'SummonerHeal',
    Barrier: 'SummonerBarrier',
    Exhaust: 'SummonerExhaust',
    Ghost: 'SummonerHaste',
    Cleanse: 'SummonerBoost',
    Smite: 'SummonerSmite'
};

const SPELL_KEYS_BY_ID = {
    1: 'SummonerBoost',
    3: 'SummonerExhaust',
    4: 'SummonerFlash',
    6: 'SummonerHaste',
    7: 'SummonerHeal',
    11: 'SummonerSmite',
    12: 'SummonerTeleport',
    14: 'SummonerDot',
    21: 'SummonerBarrier'
};

function getSpellIcon(spell, version = DEFAULT_DDRAGON_VERSION) {
    // Riot's DDragon summoner spell images are versioned; require an explicit version.
    // Prefer passing a version from the champion detail API; fall back to VITE_DDRAGON_VERSION.
    if (!version) return null;

    const rawId = spell?.id;
    const key =
        (typeof rawId === 'string' && rawId.trim() ? rawId.trim() : null) ||
        (typeof rawId === 'number' ? SPELL_KEYS_BY_ID[rawId] : null) ||
        SPELL_KEYS_BY_NAME[spell?.name];

    if (!key) return null;
    return `${DDRAGON_CDN}/${version}/img/spell/${key}.png`;
}

function getItemIcon(id, version = DEFAULT_DDRAGON_VERSION) {
    if (!id || !version) return null;
    return `${DDRAGON_CDN}/${version}/img/item/${id}.png`;
}

function getCdnImgUrl(path) {
    if (!path || typeof path !== 'string') return null;
    if (path.startsWith('http://')) return path.replace('http://', 'https://');
    if (path.startsWith('https://')) return path;
    return `${CDN_IMG_BASE}${path.replace(/^\/+/, '')}`;
}

function handleImgError(event) {
    const img = event.currentTarget;
    const fallbackSrc = img?.dataset?.fallback;

    if (!fallbackSrc) return;
    // Prevent infinite onError loops.
    img.onerror = null;
    img.src = fallbackSrc;
}

function ChampionBuildOverride({ data, ddragonVersion }) {
    if (!data) return null;

    const version = ddragonVersion || DEFAULT_DDRAGON_VERSION || null;
    const defaultSpellFallback = version ? `${DDRAGON_CDN}/${version}/img/spell/SummonerFlash.png` : TRANSPARENT_PIXEL;

    const spells = Array.isArray(data.spells) ? data.spells : EMPTY_ARRAY;
    const coreItems = Array.isArray(data?.items?.core) ? data.items.core : EMPTY_ARRAY;
    const situationalItems = Array.isArray(data?.items?.situational) ? data.items.situational : EMPTY_ARRAY;
    const primaryRunes = data?.runes?.primary || null;
    const secondaryRunes = data?.runes?.secondary || null;
    const shards = Array.isArray(data?.runes?.shards) ? data.runes.shards : EMPTY_ARRAY;
    const primaryPerks = Array.isArray(primaryRunes?.perks) ? primaryRunes.perks : EMPTY_ARRAY;
    const secondaryPerks = Array.isArray(secondaryRunes?.perks) ? secondaryRunes.perks : EMPTY_ARRAY;

    return (
        <div className="champion-build-override glass-panel">
            <div className="build-section">
                <h4 className="build-section-title">Summoner Spells</h4>
                <div className="spells-row">
                    {spells.map((spell, index) => (
                        <div
                            key={`spell-${spell?.id ?? spell?.name ?? index}`}
                            className="spell-item"
                            title={spell?.name || ''}
                        >
                            <img
                                src={getSpellIcon(spell, version) || defaultSpellFallback}
                                data-fallback={defaultSpellFallback}
                                onError={handleImgError}
                                alt={spell?.name || 'Summoner spell'}
                                className="spell-icon"
                                decoding="async"
                            />
                            <span className="spell-name">{spell?.name || '—'}</span>
                        </div>
                    ))}
                </div>
            </div>

            <div className="build-section">
                <h4 className="build-section-title">Core Items</h4>
                <div className="items-row">
                    {coreItems.map((item, index) => (
                        <div key={`item-${item?.id ?? item?.name ?? index}`} className="item-card">
                            <div className="item-icon-wrapper">
                                <img
                                    src={getItemIcon(item?.id, version) || TRANSPARENT_PIXEL}
                                    data-fallback={TRANSPARENT_PIXEL}
                                    onError={handleImgError}
                                    alt={item?.name || 'Item'}
                                    className="item-icon"
                                    loading="lazy"
                                    decoding="async"
                                    fetchPriority="low"
                                />
                            </div>
                            <span className="item-name">{item?.name || item?.id || '—'}</span>
                        </div>
                    ))}
                </div>
            </div>

            <div className="build-section">
                <h4 className="build-section-title">Situational</h4>
                <div className="items-row">
                    {situationalItems.map((item, index) => (
                        <div key={`item-${item?.id ?? item?.name ?? index}`} className="item-card">
                            <div className="item-icon-wrapper">
                                <img
                                    src={getItemIcon(item?.id, version) || TRANSPARENT_PIXEL}
                                    data-fallback={TRANSPARENT_PIXEL}
                                    onError={handleImgError}
                                    alt={item?.name || 'Item'}
                                    className="item-icon"
                                    loading="lazy"
                                    decoding="async"
                                    fetchPriority="low"
                                />
                            </div>
                            <span className="item-name">{item?.name || item?.id || '—'}</span>
                        </div>
                    ))}
                </div>
            </div>

            <div className="build-section">
                <h4 className="build-section-title">Runes</h4>
                <div className="runes-container">
                    <div className="rune-tree primary-tree">
                        <div className="tree-header">
                            <img
                                src={getCdnImgUrl(primaryRunes?.icon) || TRANSPARENT_PIXEL}
                                data-fallback={TRANSPARENT_PIXEL}
                                onError={handleImgError}
                                alt={primaryRunes?.name || 'Primary'}
                                className="tree-icon"
                                loading="lazy"
                                decoding="async"
                                fetchPriority="low"
                            />
                            <span className="tree-name">{primaryRunes?.name || '—'}</span>
                        </div>
                        <div className="runes-list">
                            {primaryPerks.map((perk, index) => (
                                <div
                                    key={`perk-${perk?.id ?? perk?.name ?? index}`}
                                    className={`rune-item ${index === 0 ? 'keystone' : ''}`}
                                    title={perk?.name || ''}
                                >
                                    <img
                                        src={getCdnImgUrl(perk?.icon) || TRANSPARENT_PIXEL}
                                        data-fallback={TRANSPARENT_PIXEL}
                                        onError={handleImgError}
                                        alt={perk?.name || 'Rune'}
                                        className="rune-icon"
                                        loading="lazy"
                                        decoding="async"
                                        fetchPriority="low"
                                    />
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="rune-tree secondary-tree">
                        <div className="tree-header">
                            <img
                                src={getCdnImgUrl(secondaryRunes?.icon) || TRANSPARENT_PIXEL}
                                data-fallback={TRANSPARENT_PIXEL}
                                onError={handleImgError}
                                alt={secondaryRunes?.name || 'Secondary'}
                                className="tree-icon"
                                loading="lazy"
                                decoding="async"
                                fetchPriority="low"
                            />
                            <span className="tree-name">{secondaryRunes?.name || '—'}</span>
                        </div>
                        <div className="runes-list">
                            {secondaryPerks.map((perk, index) => (
                                <div
                                    key={`perk-${perk?.id ?? perk?.name ?? index}`}
                                    className="rune-item"
                                    title={perk?.name || ''}
                                >
                                    <img
                                        src={getCdnImgUrl(perk?.icon) || TRANSPARENT_PIXEL}
                                        data-fallback={TRANSPARENT_PIXEL}
                                        onError={handleImgError}
                                        alt={perk?.name || 'Rune'}
                                        className="rune-icon"
                                        loading="lazy"
                                        decoding="async"
                                        fetchPriority="low"
                                    />
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="rune-tree shards-tree">
                        <div className="tree-header">
                            <span className="tree-name">Shards</span>
                        </div>
                        <div className="runes-list">
                            {shards.map((shard, index) => (
                                <div
                                    key={`shard-${shard?.id ?? shard?.name ?? index}`}
                                    className="rune-item shard"
                                    title={shard?.name || ''}
                                >
                                    <img
                                        src={getCdnImgUrl(shard?.icon) || TRANSPARENT_PIXEL}
                                        data-fallback={TRANSPARENT_PIXEL}
                                        onError={handleImgError}
                                        alt={shard?.name || 'Shard'}
                                        className="rune-icon"
                                        loading="lazy"
                                        decoding="async"
                                        fetchPriority="low"
                                    />
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default memo(ChampionBuildOverride);
