import React from 'react';
import '../../styles/champions/champion-build-override.css';

const SPELL_ICONS = {
    "Flash": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerFlash.png",
    "Teleport": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerTeleport.png",
    "Ignite": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerDot.png",
    "Heal": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerHeal.png",
    "Barrier": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerBarrier.png",
    "Exhaust": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerExhaust.png",
    "Ghost": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerHaste.png",
    "Cleanse": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerBoost.png",
    "Smite": "http://ddragon.leagueoflegends.com/cdn/14.19.1/img/spell/SummonerSmite.png"
};

// Helper to get item icon URL (using placeholder logic or ddragon if id is known)
const getItemIcon = (id) => `https://ddragon.leagueoflegends.com/cdn/14.19.1/img/item/${id}.png`;

export default function ChampionBuildOverride({ data }) {
    if (!data) return null;

    return (
        <div className="champion-build-override glass-panel">
            <div className="build-section">
                <h4 className="build-section-title">Summoner Spells</h4>
                <div className="spells-row">
                    {data.spells.map((spell) => (
                        <div key={spell.name} className="spell-item" title={spell.name}>
                            <img
                                src={SPELL_ICONS[spell.name] || SPELL_ICONS["Flash"]}
                                alt={spell.name}
                                className="spell-icon"
                            />
                            <span className="spell-name">{spell.name}</span>
                        </div>
                    ))}
                </div>
            </div>

            <div className="build-section">
                <h4 className="build-section-title">Core Items</h4>
                <div className="items-row">
                    {data.items.core.map((item) => (
                        <div key={item.name} className="item-card">
                            <div className="item-icon-wrapper">
                                <img src={getItemIcon(item.id)} alt={item.name} className="item-icon" />
                            </div>
                            <span className="item-name">{item.name}</span>
                        </div>
                    ))}
                </div>
            </div>

            <div className="build-section">
                <h4 className="build-section-title">Situational</h4>
                <div className="items-row">
                    {data.items.situational.map((item) => (
                        <div key={item.name} className="item-card">
                            <div className="item-icon-wrapper">
                                <img src={getItemIcon(item.id)} alt={item.name} className="item-icon" />
                            </div>
                            <span className="item-name">{item.name}</span>
                        </div>
                    ))}
                </div>
            </div>

            <div className="build-section">
                <h4 className="build-section-title">Runes</h4>
                <div className="runes-info">
                    <span className="rune-primary">{data.runes.primary}</span>
                    <span className="rune-separator">+</span>
                    <span className="rune-secondary">{data.runes.secondary}</span>
                </div>
            </div>
        </div>
    );
}
