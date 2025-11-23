export const CHAMPION_OVERRIDES = {
    "Aatrox": {
        role: "Fighter",
        difficulty: "Moderate",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" }
        ],
        items: {
            core: [
                { id: "3074", name: "Ravenous Hydra" },
                { id: "6630", name: "Goredrinker" }, // Note: Item meta changes often, using generally known items
                { id: "3053", name: "Sterak's Gage" }
            ],
            situational: [
                { id: "3071", name: "Black Cleaver" },
                { id: "3065", name: "Spirit Visage" },
                { id: "3026", name: "Guardian Angel" }
            ]
        },
        runes: {
            primary: "Conqueror",
            secondary: "Resolve"
        }
    },
    "Ahri": {
        role: "Mage",
        difficulty: "Moderate",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerIgnite", name: "Ignite" }
        ],
        items: {
            core: [
                { id: "6655", name: "Luden's Tempest" },
                { id: "3089", name: "Rabadon's Deathcap" },
                { id: "4645", name: "Shadowflame" }
            ],
            situational: [
                { id: "3157", name: "Zhonya's Hourglass" },
                { id: "3135", name: "Void Staff" },
                { id: "3102", name: "Banshee's Veil" }
            ]
        },
        runes: {
            primary: "Electrocute",
            secondary: "Sorcery"
        }
    },
    "Akali": {
        role: "Assassin",
        difficulty: "High",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" } // or Ignite
        ],
        items: {
            core: [
                { id: "3152", name: "Hextech Rocketbelt" },
                { id: "3157", name: "Zhonya's Hourglass" },
                { id: "4645", name: "Shadowflame" }
            ],
            situational: [
                { id: "3089", name: "Rabadon's Deathcap" },
                { id: "3135", name: "Void Staff" },
                { id: "3100", name: "Lich Bane" }
            ]
        },
        runes: {
            primary: "Conqueror", // or Electrocute
            secondary: "Resolve"
        }
    },
    "Akshan": {
        role: "Marksman",
        difficulty: "High",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerIgnite", name: "Ignite" }
        ],
        items: {
            core: [
                { id: "6671", name: "Galeforce" }, // Or Kraken
                { id: "3031", name: "Infinity Edge" },
                { id: "3036", name: "Lord Dominik's Regards" }
            ],
            situational: [
                { id: "3153", name: "Blade of the Ruined King" },
                { id: "3046", name: "Phantom Dancer" },
                { id: "3026", name: "Guardian Angel" }
            ]
        },
        runes: {
            primary: "Press the Attack",
            secondary: "Domination"
        }
    },
    "Alistar": {
        role: "Support",
        difficulty: "Low",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerIgnite", name: "Ignite" } // or Hexflash
        ],
        items: {
            core: [
                { id: "3190", name: "Locket of the Iron Solari" },
                { id: "3110", name: "Frozen Heart" },
                { id: "3050", name: "Zeke's Convergence" }
            ],
            situational: [
                { id: "3109", name: "Knight's Vow" },
                { id: "3075", name: "Thornmail" },
                { id: "3193", name: "Gargoyle Stoneplate" }
            ]
        },
        runes: {
            primary: "Aftershock",
            secondary: "Inspiration"
        }
    },
    "Amumu": {
        role: "Tank",
        difficulty: "Low",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" }
        ],
        items: {
            core: [
                { id: "6662", name: "Iceborn Gauntlet" }, // or Sunfire
                { id: "3068", name: "Sunfire Aegis" },
                { id: "3075", name: "Thornmail" }
            ],
            situational: [
                { id: "3111", name: "Mercury's Treads" },
                { id: "8020", name: "Abyssal Mask" },
                { id: "3193", name: "Gargoyle Stoneplate" }
            ]
        },
        runes: {
            primary: "Conqueror",
            secondary: "Domination"
        }
    },
    "Anivia": {
        role: "Mage",
        difficulty: "High",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" }
        ],
        items: {
            core: [
                { id: "6657", name: "Rod of Ages" },
                { id: "3003", name: "Archangel's Staff" },
                { id: "3157", name: "Zhonya's Hourglass" }
            ],
            situational: [
                { id: "3089", name: "Rabadon's Deathcap" },
                { id: "3135", name: "Void Staff" },
                { id: "6653", name: "Liandry's Torment" }
            ]
        },
        runes: {
            primary: "Electrocute",
            secondary: "Precision"
        }
    },
    "Annie": {
        role: "Mage",
        difficulty: "Low",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerIgnite", name: "Ignite" }
        ],
        items: {
            core: [
                { id: "6653", name: "Liandry's Anguish" }, // or Ludens
                { id: "3157", name: "Zhonya's Hourglass" },
                { id: "3089", name: "Rabadon's Deathcap" }
            ],
            situational: [
                { id: "3116", name: "Rylai's Crystal Scepter" },
                { id: "4645", name: "Shadowflame" },
                { id: "3135", name: "Void Staff" }
            ]
        },
        runes: {
            primary: "Electrocute",
            secondary: "Sorcery"
        }
    },
    "Aphelios": {
        role: "Marksman",
        difficulty: "High",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" }
        ],
        items: {
            core: [
                { id: "6671", name: "Galeforce" },
                { id: "3031", name: "Infinity Edge" },
                { id: "3072", name: "Bloodthirster" }
            ],
            situational: [
                { id: "3036", name: "Lord Dominik's Regards" },
                { id: "3026", name: "Guardian Angel" },
                { id: "3004", name: "Manamune" }
            ]
        },
        runes: {
            primary: "Fleet Footwork",
            secondary: "Sorcery"
        }
    },
    "Ashe": {
        role: "Marksman",
        difficulty: "Low",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" } // or Ghost
        ],
        items: {
            core: [
                { id: "6672", name: "Kraken Slayer" },
                { id: "3085", name: "Runaan's Hurricane" },
                { id: "3031", name: "Infinity Edge" }
            ],
            situational: [
                { id: "3153", name: "Blade of the Ruined King" },
                { id: "3026", name: "Guardian Angel" },
                { id: "3036", name: "Lord Dominik's Regards" }
            ]
        },
        runes: {
            primary: "Lethal Tempo",
            secondary: "Inspiration"
        }
    },
    "Aurelion Sol": {
        role: "Mage",
        difficulty: "Moderate",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" }
        ],
        items: {
            core: [
                { id: "6653", name: "Liandry's Anguish" },
                { id: "3116", name: "Rylai's Crystal Scepter" },
                { id: "3089", name: "Rabadon's Deathcap" }
            ],
            situational: [
                { id: "3157", name: "Zhonya's Hourglass" },
                { id: "3135", name: "Void Staff" },
                { id: "3102", name: "Banshee's Veil" }
            ]
        },
        runes: {
            primary: "Arcane Comet",
            secondary: "Inspiration"
        }
    },
    "Azir": {
        role: "Mage",
        difficulty: "High",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" }
        ],
        items: {
            core: [
                { id: "6653", name: "Liandry's Anguish" },
                { id: "3115", name: "Nashor's Tooth" },
                { id: "3157", name: "Zhonya's Hourglass" }
            ],
            situational: [
                { id: "3089", name: "Rabadon's Deathcap" },
                { id: "3135", name: "Void Staff" },
                { id: "3102", name: "Banshee's Veil" }
            ]
        },
        runes: {
            primary: "Conqueror",
            secondary: "Sorcery"
        }
    }
};
