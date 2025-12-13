import urllib.request
import json
import os

# 1. Manual Overrides (The 'A' champions we already verified)
MANUAL_OVERRIDES = {
    "Aatrox": {
        "role": "Fighter",
        "difficulty": "Moderate",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerTeleport", "name": "Teleport" }
        ],
        "items": {
            "core": [
                { "id": "3074", "name": "Ravenous Hydra" },
                { "id": "6630", "name": "Goredrinker" },
                { "id": "3053", "name": "Sterak's Gage" }
            ],
            "situational": [
                { "id": "3071", "name": "Black Cleaver" },
                { "id": "3065", "name": "Spirit Visage" },
                { "id": "3026", "name": "Guardian Angel" }
            ]
        },
        "runes": {
            "primary": "Conqueror",
            "secondary": "Resolve"
        }
    },
    "Ahri": {
        "role": "Mage",
        "difficulty": "Moderate",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerIgnite", "name": "Ignite" }
        ],
        "items": {
            "core": [
                { "id": "6655", "name": "Luden's Tempest" },
                { "id": "3089", "name": "Rabadon's Deathcap" },
                { "id": "4645", "name": "Shadowflame" }
            ],
            "situational": [
                { "id": "3157", "name": "Zhonya's Hourglass" },
                { "id": "3135", "name": "Void Staff" },
                { "id": "3102", "name": "Banshee's Veil" }
            ]
        },
        "runes": {
            "primary": "Electrocute",
            "secondary": "Sorcery"
        }
    },
    "Akali": {
        "role": "Assassin",
        "difficulty": "High",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerTeleport", "name": "Teleport" }
        ],
        "items": {
            "core": [
                { "id": "3152", "name": "Hextech Rocketbelt" },
                { "id": "3157", "name": "Zhonya's Hourglass" },
                { "id": "4645", "name": "Shadowflame" }
            ],
            "situational": [
                { "id": "3089", "name": "Rabadon's Deathcap" },
                { "id": "3135", "name": "Void Staff" },
                { "id": "3100", "name": "Lich Bane" }
            ]
        },
        "runes": {
            "primary": "Conqueror",
            "secondary": "Resolve"
        }
    },
    "Akshan": {
        "role": "Marksman",
        "difficulty": "High",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerIgnite", "name": "Ignite" }
        ],
        "items": {
            "core": [
                { "id": "6671", "name": "Galeforce" },
                { "id": "3031", "name": "Infinity Edge" },
                { "id": "3036", "name": "Lord Dominik's Regards" }
            ],
            "situational": [
                { "id": "3153", "name": "Blade of the Ruined King" },
                { "id": "3046", "name": "Phantom Dancer" },
                { "id": "3026", "name": "Guardian Angel" }
            ]
        },
        "runes": {
            "primary": "Press the Attack",
            "secondary": "Domination"
        }
    },
    "Alistar": {
        "role": "Support",
        "difficulty": "Low",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerIgnite", "name": "Ignite" }
        ],
        "items": {
            "core": [
                { "id": "3190", "name": "Locket of the Iron Solari" },
                { "id": "3110", "name": "Frozen Heart" },
                { "id": "3050", "name": "Zeke's Convergence" }
            ],
            "situational": [
                { "id": "3109", "name": "Knight's Vow" },
                { "id": "3075", "name": "Thornmail" },
                { "id": "3193", "name": "Gargoyle Stoneplate" }
            ]
        },
        "runes": {
            "primary": "Aftershock",
            "secondary": "Inspiration"
        }
    },
    "Amumu": {
        "role": "Tank",
        "difficulty": "Low",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerSmite", "name": "Smite" }
        ],
        "items": {
            "core": [
                { "id": "6662", "name": "Iceborn Gauntlet" },
                { "id": "3068", "name": "Sunfire Aegis" },
                { "id": "3075", "name": "Thornmail" }
            ],
            "situational": [
                { "id": "3111", "name": "Mercury's Treads" },
                { "id": "8020", "name": "Abyssal Mask" },
                { "id": "3193", "name": "Gargoyle Stoneplate" }
            ]
        },
        "runes": {
            "primary": "Conqueror",
            "secondary": "Domination"
        }
    },
    "Anivia": {
        "role": "Mage",
        "difficulty": "High",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerTeleport", "name": "Teleport" }
        ],
        "items": {
            "core": [
                { "id": "6657", "name": "Rod of Ages" },
                { "id": "3003", "name": "Archangel's Staff" },
                { "id": "3157", "name": "Zhonya's Hourglass" }
            ],
            "situational": [
                { "id": "3089", "name": "Rabadon's Deathcap" },
                { "id": "3135", "name": "Void Staff" },
                { "id": "6653", "name": "Liandry's Torment" }
            ]
        },
        "runes": {
            "primary": "Electrocute",
            "secondary": "Precision"
        }
    },
    "Annie": {
        "role": "Mage",
        "difficulty": "Low",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerIgnite", "name": "Ignite" }
        ],
        "items": {
            "core": [
                { "id": "6653", "name": "Liandry's Anguish" },
                { "id": "3157", "name": "Zhonya's Hourglass" },
                { "id": "3089", "name": "Rabadon's Deathcap" }
            ],
            "situational": [
                { "id": "3116", "name": "Rylai's Crystal Scepter" },
                { "id": "4645", "name": "Shadowflame" },
                { "id": "3135", "name": "Void Staff" }
            ]
        },
        "runes": {
            "primary": "Electrocute",
            "secondary": "Sorcery"
        }
    },
    "Aphelios": {
        "role": "Marksman",
        "difficulty": "High",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerHeal", "name": "Heal" }
        ],
        "items": {
            "core": [
                { "id": "6671", "name": "Galeforce" },
                { "id": "3031", "name": "Infinity Edge" },
                { "id": "3072", "name": "Bloodthirster" }
            ],
            "situational": [
                { "id": "3036", "name": "Lord Dominik's Regards" },
                { "id": "3026", "name": "Guardian Angel" },
                { "id": "3004", "name": "Manamune" }
            ]
        },
        "runes": {
            "primary": "Fleet Footwork",
            "secondary": "Sorcery"
        }
    },
    "Ashe": {
        "role": "Marksman",
        "difficulty": "Low",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerHeal", "name": "Heal" }
        ],
        "items": {
            "core": [
                { "id": "6672", "name": "Kraken Slayer" },
                { "id": "3085", "name": "Runaan's Hurricane" },
                { "id": "3031", "name": "Infinity Edge" }
            ],
            "situational": [
                { "id": "3153", "name": "Blade of the Ruined King" },
                { "id": "3026", "name": "Guardian Angel" },
                { "id": "3036", "name": "Lord Dominik's Regards" }
            ]
        },
        "runes": {
            "primary": "Lethal Tempo",
            "secondary": "Inspiration"
        }
    },
    "Aurelion Sol": {
        "role": "Mage",
        "difficulty": "Moderate",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerTeleport", "name": "Teleport" }
        ],
        "items": {
            "core": [
                { "id": "6653", "name": "Liandry's Anguish" },
                { "id": "3116", "name": "Rylai's Crystal Scepter" },
                { "id": "3089", "name": "Rabadon's Deathcap" }
            ],
            "situational": [
                { "id": "3157", "name": "Zhonya's Hourglass" },
                { "id": "3135", "name": "Void Staff" },
                { "id": "3102", "name": "Banshee's Veil" }
            ]
        },
        "runes": {
            "primary": "Arcane Comet",
            "secondary": "Inspiration"
        }
    },
    "Azir": {
        "role": "Mage",
        "difficulty": "High",
        "spells": [
            { "id": "SummonerFlash", "name": "Flash" },
            { "id": "SummonerTeleport", "name": "Teleport" }
        ],
        "items": {
            "core": [
                { "id": "6653", "name": "Liandry's Anguish" },
                { "id": "3115", "name": "Nashor's Tooth" },
                { "id": "3157", "name": "Zhonya's Hourglass" }
            ],
            "situational": [
                { "id": "3089", "name": "Rabadon's Deathcap" },
                { "id": "3135", "name": "Void Staff" },
                { "id": "3102", "name": "Banshee's Veil" }
            ]
        },
        "runes": {
            "primary": "Conqueror",
            "secondary": "Sorcery"
        }
    }
}

# 2. Class Templates
TEMPLATES = {
    "Mage": {
        "role": "Mage",
        "spells": [{"id": "SummonerFlash", "name": "Flash"}, {"id": "SummonerTeleport", "name": "Teleport"}],
        "items": {
            "core": [
                {"id": "6655", "name": "Luden's Companion"},
                {"id": "3020", "name": "Sorcerer's Shoes"},
                {"id": "4645", "name": "Shadowflame"}
            ],
            "situational": [
                {"id": "3157", "name": "Zhonya's Hourglass"},
                {"id": "3089", "name": "Rabadon's Deathcap"},
                {"id": "3135", "name": "Void Staff"}
            ]
        },
        "runes": {"primary": "Electrocute", "secondary": "Sorcery"}
    },
    "Marksman": {
        "role": "Marksman",
        "spells": [{"id": "SummonerFlash", "name": "Flash"}, {"id": "SummonerHeal", "name": "Heal"}],
        "items": {
            "core": [
                {"id": "6672", "name": "Kraken Slayer"},
                {"id": "3006", "name": "Berserker's Greaves"},
                {"id": "3031", "name": "Infinity Edge"}
            ],
            "situational": [
                {"id": "3036", "name": "Lord Dominik's Regards"},
                {"id": "3072", "name": "Bloodthirster"},
                {"id": "3026", "name": "Guardian Angel"}
            ]
        },
        "runes": {"primary": "Press the Attack", "secondary": "Precision"}
    },
    "Assassin": {
        "role": "Assassin",
        "spells": [{"id": "SummonerFlash", "name": "Flash"}, {"id": "SummonerIgnite", "name": "Ignite"}],
        "items": {
            "core": [
                {"id": "3142", "name": "Youmuu's Ghostblade"},
                {"id": "3158", "name": "Ionian Boots of Lucidity"},
                {"id": "6699", "name": "Voltaic Cyclosword"}
            ],
            "situational": [
                {"id": "3814", "name": "Edge of Night"},
                {"id": "6694", "name": "Serylda's Grudge"},
                {"id": "3026", "name": "Guardian Angel"}
            ]
        },
        "runes": {"primary": "Electrocute", "secondary": "Domination"}
    },
    "Tank": {
        "role": "Tank",
        "spells": [{"id": "SummonerFlash", "name": "Flash"}, {"id": "SummonerTeleport", "name": "Teleport"}],
        "items": {
            "core": [
                {"id": "3084", "name": "Heartsteel"},
                {"id": "3111", "name": "Mercury's Treads"},
                {"id": "3068", "name": "Sunfire Aegis"}
            ],
            "situational": [
                {"id": "3075", "name": "Thornmail"},
                {"id": "3065", "name": "Spirit Visage"},
                {"id": "6665", "name": "Jak'Sho, The Protean"}
            ]
        },
        "runes": {"primary": "Grasp of the Undying", "secondary": "Resolve"}
    },
    "Fighter": {
        "role": "Fighter",
        "spells": [{"id": "SummonerFlash", "name": "Flash"}, {"id": "SummonerTeleport", "name": "Teleport"}],
        "items": {
            "core": [
                {"id": "6610", "name": "Sundered Sky"},
                {"id": "3047", "name": "Plated Steelcaps"},
                {"id": "3071", "name": "Black Cleaver"}
            ],
            "situational": [
                {"id": "3053", "name": "Sterak's Gage"},
                {"id": "6333", "name": "Death's Dance"},
                {"id": "3026", "name": "Guardian Angel"}
            ]
        },
        "runes": {"primary": "Conqueror", "secondary": "Precision"}
    },
    "Support": {
        "role": "Support",
        "spells": [{"id": "SummonerFlash", "name": "Flash"}, {"id": "SummonerIgnite", "name": "Ignite"}],
        "items": {
            "core": [
                {"id": "6617", "name": "Moonstone Renewer"},
                {"id": "3158", "name": "Ionian Boots of Lucidity"},
                {"id": "3504", "name": "Ardent Censer"}
            ],
            "situational": [
                {"id": "3190", "name": "Locket of the Iron Solari"},
                {"id": "3107", "name": "Redemption"},
                {"id": "3222", "name": "Mikael's Blessing"}
            ]
        },
        "runes": {"primary": "Summon Aery", "secondary": "Inspiration"}
    },
    "TankSupport": {
        "role": "Support",
        "spells": [{"id": "SummonerFlash", "name": "Flash"}, {"id": "SummonerIgnite", "name": "Ignite"}],
        "items": {
            "core": [
                {"id": "3190", "name": "Locket of the Iron Solari"},
                {"id": "3111", "name": "Mercury's Treads"},
                {"id": "3109", "name": "Knight's Vow"}
            ],
            "situational": [
                {"id": "3050", "name": "Zeke's Convergence"},
                {"id": "3075", "name": "Thornmail"},
                {"id": "3193", "name": "Gargoyle Stoneplate"}
            ]
        },
        "runes": {"primary": "Aftershock", "secondary": "Inspiration"}
    }
}

def generate_overrides():
    print("Fetching champion data...")
    try:
        with urllib.request.urlopen("https://ddragon.leagueoflegends.com/api/versions.json") as url:
            versions = json.loads(url.read().decode())
            latest_version = versions[0] if versions else "14.19.1"

        with urllib.request.urlopen(f"https://ddragon.leagueoflegends.com/cdn/{latest_version}/data/en_US/champion.json") as url:
            data = json.loads(url.read().decode())
            champions = data['data']
    except Exception as e:
        print(f"Error fetching data: {e}")
        return

    overrides = {}

    for name, champ_data in champions.items():
        # Use manual override if exists
        if name in MANUAL_OVERRIDES:
            overrides[name] = MANUAL_OVERRIDES[name]
            continue

        # Determine template
        tags = champ_data.get('tags', [])
        primary_tag = tags[0] if tags else "Fighter"
        
        template = None
        
        if "Support" in tags:
            if "Tank" in tags:
                template = TEMPLATES["TankSupport"]
            else:
                template = TEMPLATES["Support"]
        elif primary_tag in TEMPLATES:
            template = TEMPLATES[primary_tag]
        else:
            # Fallback
            template = TEMPLATES["Fighter"]

        overrides[name] = {
            "role": template["role"],
            "difficulty": champ_data.get("info", {}).get("difficulty", 5) > 7 and "High" or (champ_data.get("info", {}).get("difficulty", 5) < 4 and "Low" or "Moderate"),
            "spells": template["spells"],
            "items": template["items"],
            "runes": template["runes"]
        }

    # Write to file
    output_path = os.path.join("frontend", "src", "data", "championOverrides.js")
    with open(output_path, "w", encoding="utf-8") as f:
        f.write("export const CHAMPION_OVERRIDES = {\n")
        
        sorted_names = sorted(overrides.keys())
        for i, name in enumerate(sorted_names):
            data = overrides[name]
            f.write(f'    "{name}": ')
            f.write(json.dumps(data, indent=8).replace('"', '"').replace('        }', '        }').replace('    }', '    }'))
            if i < len(sorted_names) - 1:
                f.write(",\n")
            else:
                f.write("\n")
        
        f.write("};\n")
    
    print(f"Successfully generated overrides for {len(overrides)} champions to {output_path}")

if __name__ == "__main__":
    generate_overrides()
