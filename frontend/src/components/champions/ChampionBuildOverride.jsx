import '../../styles/champions/champion-build-override.css';

const DDRAGON_VERSION = import.meta.env.VITE_DDRAGON_VERSION || '14.19.1';
const DDRAGON_CDN = 'https://ddragon.leagueoflegends.com/cdn';
const SPELL_IMAGE_BASE = `${DDRAGON_CDN}/${DDRAGON_VERSION}/img/spell/`;
const ITEM_IMAGE_BASE = `${DDRAGON_CDN}/${DDRAGON_VERSION}/img/item/`;
const CDN_IMG_BASE = `${DDRAGON_CDN}/img/`;
const TRANSPARENT_PIXEL =
    'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';

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

function getSpellIcon(spell) {
    const rawId = spell?.id;
    const key =
        (typeof rawId === 'string' && rawId.trim() ? rawId.trim() : null) ||
        (typeof rawId === 'number' ? SPELL_KEYS_BY_ID[rawId] : null) ||
        SPELL_KEYS_BY_NAME[spell?.name];

    if (!key) return null;
    return `${SPELL_IMAGE_BASE}${key}.png`;
}

function getItemIcon(id) {
    if (!id) return null;
    return `${ITEM_IMAGE_BASE}${id}.png`;
}

function getCdnImgUrl(path) {
    if (!path || typeof path !== 'string') return null;
    if (path.startsWith('http://')) return path.replace('http://', 'https://');
    if (path.startsWith('https://')) return path;
    return `${CDN_IMG_BASE}${path.replace(/^\/+/, '')}`;
}

function withFallback(fallbackSrc) {
    return (event) => {
        if (!fallbackSrc) return;
        // Prevent infinite onError loops.
        event.currentTarget.onerror = null;
        event.currentTarget.src = fallbackSrc;
    };
}

export default function ChampionBuildOverride({ data }) {
    if (!data) return null;

    const spells = Array.isArray(data.spells) ? data.spells : [];
    const coreItems = Array.isArray(data?.items?.core) ? data.items.core : [];
    const situationalItems = Array.isArray(data?.items?.situational) ? data.items.situational : [];
    const primaryRunes = data?.runes?.primary || null;
    const secondaryRunes = data?.runes?.secondary || null;
    const shards = Array.isArray(data?.runes?.shards) ? data.runes.shards : [];

    return (
        <div className="champion-build-override glass-panel">
            <div className="build-section">
                <h4 className="build-section-title">Summoner Spells</h4>
                <div className="spells-row">
                    {spells.map((spell, index) => (
                        <div
                            key={`${spell?.id || spell?.name || 'spell'}-${index}`}
                            className="spell-item"
                            title={spell?.name || ''}
                        >
                            <img
                                src={getSpellIcon(spell) || `${SPELL_IMAGE_BASE}SummonerFlash.png`}
                                onError={withFallback(`${SPELL_IMAGE_BASE}SummonerFlash.png`)}
                                alt={spell?.name || 'Summoner spell'}
                                className="spell-icon"
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
                        <div key={`${item?.id || item?.name || 'item'}-${index}`} className="item-card">
                            <div className="item-icon-wrapper">
                                <img
                                    src={getItemIcon(item?.id) || TRANSPARENT_PIXEL}
                                    onError={withFallback(TRANSPARENT_PIXEL)}
                                    alt={item?.name || 'Item'}
                                    className="item-icon"
                                    loading="lazy"
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
                        <div key={`${item?.id || item?.name || 'item'}-${index}`} className="item-card">
                            <div className="item-icon-wrapper">
                                <img
                                    src={getItemIcon(item?.id) || TRANSPARENT_PIXEL}
                                    onError={withFallback(TRANSPARENT_PIXEL)}
                                    alt={item?.name || 'Item'}
                                    className="item-icon"
                                    loading="lazy"
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
                                onError={withFallback(TRANSPARENT_PIXEL)}
                                alt={primaryRunes?.name || 'Primary'}
                                className="tree-icon"
                            />
                            <span className="tree-name">{primaryRunes?.name || '—'}</span>
                        </div>
                        <div className="runes-list">
                            {(Array.isArray(primaryRunes?.perks) ? primaryRunes.perks : []).map((perk, index) => (
                                <div
                                    key={`${perk?.id || 'perk'}-${index}`}
                                    className={`rune-item ${index === 0 ? 'keystone' : ''}`}
                                    title={perk?.name || ''}
                                >
                                    <img
                                        src={getCdnImgUrl(perk?.icon) || TRANSPARENT_PIXEL}
                                        onError={withFallback(TRANSPARENT_PIXEL)}
                                        alt={perk?.name || 'Rune'}
                                        className="rune-icon"
                                    />
                                </div>
                            ))}
                        </div>
                    </div>

                    <div className="rune-tree secondary-tree">
                        <div className="tree-header">
                            <img
                                src={getCdnImgUrl(secondaryRunes?.icon) || TRANSPARENT_PIXEL}
                                onError={withFallback(TRANSPARENT_PIXEL)}
                                alt={secondaryRunes?.name || 'Secondary'}
                                className="tree-icon"
                            />
                            <span className="tree-name">{secondaryRunes?.name || '—'}</span>
                        </div>
                        <div className="runes-list">
                            {(Array.isArray(secondaryRunes?.perks) ? secondaryRunes.perks : []).map((perk, index) => (
                                <div key={`${perk?.id || 'perk'}-${index}`} className="rune-item" title={perk?.name || ''}>
                                    <img
                                        src={getCdnImgUrl(perk?.icon) || TRANSPARENT_PIXEL}
                                        onError={withFallback(TRANSPARENT_PIXEL)}
                                        alt={perk?.name || 'Rune'}
                                        className="rune-icon"
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
                                    key={`${shard?.id || 'shard'}-${index}`}
                                    className="rune-item shard"
                                    title={shard?.name || ''}
                                >
                                    <img
                                        src={getCdnImgUrl(shard?.icon) || TRANSPARENT_PIXEL}
                                        onError={withFallback(TRANSPARENT_PIXEL)}
                                        alt={shard?.name || 'Shard'}
                                        className="rune-icon"
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
