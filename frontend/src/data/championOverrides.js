export const CHAMPION_OVERRIDES = {
    "Aatrox": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3161", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "6699", name: "Item" },
            ],
            situational: [
                { id: "6694", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "6333", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ahri": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3118", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4628", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8139, name: "Taste of Blood", icon: "perk-images/Styles/Domination/TasteOfBlood/GreenTerror_TasteOfBlood.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Akali": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "4646", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Akshan": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3087", name: "Item" },
                { id: "3010", name: "Item" },
                { id: "6676", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3094", name: "Item" },
                { id: "6673", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Alistar": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3869", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3107", name: "Item" },
            ],
            situational: [
                { id: "3190", name: "Item" },
                { id: "3050", name: "Item" },
                { id: "3109", name: "Item" },
                { id: "3075", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8360, name: "Unsealed Spellbook", icon: "perk-images/Styles/Inspiration/UnsealedSpellbook/UnsealedSpellbook.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                    { id: 8306, name: "Hextech Flashtraption", icon: "perk-images/Styles/Inspiration/HextechFlashtraption/HextechFlashtraption.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ambessa": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6692", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3161", name: "Item" },
            ],
            situational: [
                { id: "6333", name: "Item" },
                { id: "3156", name: "Item" },
                { id: "3053", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Amumu": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6653", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "3068", name: "Item" },
            ],
            situational: [
                { id: "8020", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3110", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Anivia": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6657", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "3003", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "6653", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3137", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Annie": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3118", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4646", name: "Item" },
            ],
            situational: [
                { id: "4645", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8224, name: "Axiom Arcanist", icon: "perk-images/Styles/Sorcery/NullifyingOrb/Axiom_Arcanist.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5013, name: "Tenacity", icon: "perk-images/StatMods/StatModsTenacityIcon.png" },
            ]
        }
    },
    "Aphelios": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "6676", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "6673", name: "Item" },
                { id: "3085", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ashe": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3006", name: "Item" },
                { id: "6672", name: "Item" },
                { id: "3046", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3139", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "AurelionSol": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3116", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "6653", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Aurora": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6655", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
                { id: "3157", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8137, name: "Sixth Sense", icon: "perk-images/Styles/Domination/SixthSense/SixthSense.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Azir": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3115", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3135", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Bard": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3877", name: "Item" },
                { id: "3742", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "3107", name: "Item" },
                { id: "6653", name: "Item" },
                { id: "3190", name: "Item" },
                { id: "3091", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8141, name: "Deep Ward", icon: "perk-images/Styles/Domination/DeepWard/DeepWard.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Belveth": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6672", name: "Item" },
                { id: "6631", name: "Item" },
                { id: "3111", name: "Item" },
            ],
            situational: [
                { id: "3091", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3302", name: "Item" },
                { id: "6665", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Blitzcrank": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3869", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3107", name: "Item" },
            ],
            situational: [
                { id: "3190", name: "Item" },
                { id: "3110", name: "Item" },
                { id: "3050", name: "Item" },
                { id: "2065", name: "Item" },
                { id: "3075", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8351, name: "Glacial Augment", icon: "perk-images/Styles/Inspiration/GlacialAugment/GlacialAugment.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                    { id: 8306, name: "Hextech Flashtraption", icon: "perk-images/Styles/Inspiration/HextechFlashtraption/HextechFlashtraption.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Brand": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3871", name: "Item" },
                { id: "3116", name: "Item" },
                { id: "3020", name: "Item" },
            ],
            situational: [
                { id: "6653", name: "Item" },
                { id: "2503", name: "Item" },
                { id: "3118", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Braum": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3876", name: "Item" },
                { id: "3190", name: "Item" },
                { id: "3047", name: "Item" },
            ],
            situational: [
                { id: "3107", name: "Item" },
                { id: "3109", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "3110", name: "Item" },
                { id: "4643", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8465, name: "Guardian", icon: "perk-images/Styles/Resolve/Guardian/Guardian.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Briar": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3153", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3071", name: "Item" },
            ],
            situational: [
                { id: "6333", name: "Item" },
                { id: "6610", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "2501", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Caitlyn": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "6676", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "3094", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Camille": {
        role: "top",
        spells: [
            { id: "SummonerTeleport", name: "Teleport" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3074", name: "Item" },
            ],
            situational: [
                { id: "3161", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "6609", name: "Item" },
                { id: "2504", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Cassiopeia": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6657", name: "Item" },
                { id: "3003", name: "Item" },
                { id: "3116", name: "Item" },
            ],
            situational: [
                { id: "6653", name: "Item" },
                { id: "4629", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Chogath": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3152", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "4633", name: "Item" },
            ],
            situational: [
                { id: "2502", name: "Item" },
                { id: "3742", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "4401", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                    { id: 8429, name: "Conditioning", icon: "perk-images/Styles/Resolve/Conditioning/Conditioning.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Corki": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3004", name: "Item" },
            ],
            situational: [
                { id: "6676", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Darius": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHaste", name: "Ghost" },
        ],
        items: {
            core: [
                { id: "3047", name: "Item" },
                { id: "6631", name: "Item" },
                { id: "3053", name: "Item" },
            ],
            situational: [
                { id: "3742", name: "Item" },
                { id: "4401", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3075", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Diana": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6653", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "4633", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "DrMundo": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3084", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3742", name: "Item" },
            ],
            situational: [
                { id: "3065", name: "Item" },
                { id: "3748", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "2501", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8021, name: "Fleet Footwork", icon: "perk-images/Styles/Precision/FleetFootwork/FleetFootwork.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Draven": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3072", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "3036", name: "Item" },
                { id: "6676", name: "Item" },
                { id: "3094", name: "Item" },
                { id: "6673", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ekko": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3115", name: "Item" },
                { id: "3100", name: "Item" },
                { id: "3089", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3152", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8128, name: "Dark Harvest", icon: "perk-images/Styles/Domination/DarkHarvest/DarkHarvest.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8137, name: "Sixth Sense", icon: "perk-images/Styles/Domination/SixthSense/SixthSense.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Elise": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3020", name: "Item" },
                { id: "4646", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8128, name: "Dark Harvest", icon: "perk-images/Styles/Domination/DarkHarvest/DarkHarvest.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8137, name: "Sixth Sense", icon: "perk-images/Styles/Domination/SixthSense/SixthSense.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Evelynn": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3020", name: "Item" },
                { id: "3100", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "4646", name: "Item" },
                { id: "3135", name: "Item" },
                { id: "3102", name: "Item" },
                { id: "4645", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8137, name: "Sixth Sense", icon: "perk-images/Styles/Domination/SixthSense/SixthSense.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ezreal": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3004", name: "Item" },
            ],
            situational: [
                { id: "3161", name: "Item" },
                { id: "6694", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3156", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Fiddlesticks": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3152", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3102", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Fiora": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3074", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3078", name: "Item" },
            ],
            situational: [
                { id: "6333", name: "Item" },
                { id: "3181", name: "Item" },
                { id: "3161", name: "Item" },
                { id: "3156", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Fizz": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3100", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "4646", name: "Item" },
                { id: "3135", name: "Item" },
                { id: "4645", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Galio": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6664", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "4633", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3075", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Gangplank": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "6676", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "6673", name: "Item" },
                { id: "6701", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Garen": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3006", name: "Item" },
                { id: "6631", name: "Item" },
                { id: "3046", name: "Item" },
            ],
            situational: [
                { id: "3161", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3742", name: "Item" },
                { id: "6333", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8224, name: "Axiom Arcanist", icon: "perk-images/Styles/Sorcery/NullifyingOrb/Axiom_Arcanist.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Gnar": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3047", name: "Item" },
                { id: "3078", name: "Item" },
                { id: "3071", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "3091", name: "Item" },
                { id: "3143", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "6665", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8021, name: "Fleet Footwork", icon: "perk-images/Styles/Precision/FleetFootwork/FleetFootwork.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Gragas": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6657", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "4629", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Graves": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3142", name: "Item" },
                { id: "6676", name: "Item" },
                { id: "3036", name: "Item" },
            ],
            situational: [
                { id: "6673", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "3156", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8021, name: "Fleet Footwork", icon: "perk-images/Styles/Precision/FleetFootwork/FleetFootwork.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Gwen": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3115", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4633", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Hecarim": {
        role: "jungle",
        spells: [
            { id: "SummonerHaste", name: "Ghost" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3158", name: "Item" },
                { id: "3161", name: "Item" },
                { id: "3071", name: "Item" },
            ],
            situational: [
                { id: "6333", name: "Item" },
                { id: "6694", name: "Item" },
                { id: "3156", name: "Item" },
                { id: "3053", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8232, name: "Waterwalking", icon: "perk-images/Styles/Sorcery/Waterwalking/Waterwalking.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Heimerdinger": {
        role: "top",
        spells: [
            { id: "SummonerExhaust", name: "Exhaust" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "6653", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9101, name: "Absorb Life", icon: "perk-images/Styles/Precision/AbsorbLife/AbsorbLife.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Hwei": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "6653", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3157", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Illaoi": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3071", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "6610", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "6662", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3075", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Irelia": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3153", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3181", name: "Item" },
            ],
            situational: [
                { id: "3091", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3110", name: "Item" },
                { id: "6610", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ivern": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3158", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "6617", name: "Item" },
                { id: "3504", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "2065", name: "Item" },
                { id: "3089", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8232, name: "Waterwalking", icon: "perk-images/Styles/Sorcery/Waterwalking/Waterwalking.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Janna": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "6617", name: "Item" },
            ],
            situational: [
                { id: "3107", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "3222", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8351, name: "Glacial Augment", icon: "perk-images/Styles/Inspiration/GlacialAugment/GlacialAugment.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "JarvanIV": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6698", name: "Item" },
                { id: "6696", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "3110", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "6694", name: "Item" },
                { id: "3075", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Jax": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "6610", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3091", name: "Item" },
                { id: "3110", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Jayce": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3142", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3004", name: "Item" },
            ],
            situational: [
                { id: "6694", name: "Item" },
                { id: "3814", name: "Item" },
                { id: "3161", name: "Item" },
                { id: "6701", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Jhin": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3009", name: "Item" },
                { id: "3142", name: "Item" },
                { id: "3094", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8021, name: "Fleet Footwork", icon: "perk-images/Styles/Precision/FleetFootwork/FleetFootwork.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Jinx": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3032", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "3085", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "6673", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "KSante": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6662", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "2502", name: "Item" },
            ],
            situational: [
                { id: "6665", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "2504", name: "Item" },
                { id: "3143", name: "Item" },
                { id: "3110", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8439, name: "Aftershock", icon: "perk-images/Styles/Resolve/VeteranAftershock/VeteranAftershock.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Kaisa": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "6672", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3124", name: "Item" },
            ],
            situational: [
                { id: "3115", name: "Item" },
                { id: "3046", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3302", name: "Item" },
                { id: "3089", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Kalista": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3006", name: "Item" },
                { id: "3153", name: "Item" },
                { id: "3124", name: "Item" },
            ],
            situational: [
                { id: "3302", name: "Item" },
                { id: "3091", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3085", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Karma": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "6617", name: "Item" },
            ],
            situational: [
                { id: "3107", name: "Item" },
                { id: "3222", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "2065", name: "Item" },
                { id: "3504", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Karthus": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8128, name: "Dark Harvest", icon: "perk-images/Styles/Domination/DarkHarvest/DarkHarvest.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8137, name: "Sixth Sense", icon: "perk-images/Styles/Domination/SixthSense/SixthSense.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Kassadin": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6657", name: "Item" },
                { id: "3118", name: "Item" },
                { id: "3003", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Katarina": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3100", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "4645", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Kayle": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3009", name: "Item" },
                { id: "3115", name: "Item" },
                { id: "3089", name: "Item" },
            ],
            situational: [
                { id: "3100", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3135", name: "Item" },
                { id: "3116", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9101, name: "Absorb Life", icon: "perk-images/Styles/Precision/AbsorbLife/AbsorbLife.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Kennen": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3152", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3135", name: "Item" },
                { id: "3102", name: "Item" },
                { id: "3137", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8139, name: "Taste of Blood", icon: "perk-images/Styles/Domination/TasteOfBlood/GreenTerror_TasteOfBlood.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Khazix": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3142", name: "Item" },
                { id: "6701", name: "Item" },
                { id: "3814", name: "Item" },
            ],
            situational: [
                { id: "6694", name: "Item" },
                { id: "6698", name: "Item" },
                { id: "6695", name: "Item" },
                { id: "3156", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Kindred": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "6676", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3094", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Kled": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3748", name: "Item" },
                { id: "3181", name: "Item" },
                { id: "2501", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "3071", name: "Item" },
                { id: "6610", name: "Item" },
                { id: "3143", name: "Item" },
                { id: "6676", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "KogMaw": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3006", name: "Item" },
                { id: "3124", name: "Item" },
                { id: "3085", name: "Item" },
            ],
            situational: [
                { id: "3091", name: "Item" },
                { id: "3302", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "6672", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Leblanc": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6655", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3102", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "LeeSin": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6692", name: "Item" },
                { id: "6610", name: "Item" },
                { id: "3111", name: "Item" },
            ],
            situational: [
                { id: "3071", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3053", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Leona": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3877", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3190", name: "Item" },
            ],
            situational: [
                { id: "3107", name: "Item" },
                { id: "3109", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "3110", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8439, name: "Aftershock", icon: "perk-images/Styles/Resolve/VeteranAftershock/VeteranAftershock.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Lillia": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6653", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "4633", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3089", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Lissandra": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3118", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Lucian": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3508", name: "Item" },
                { id: "6675", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "3071", name: "Item" },
                { id: "3094", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Lulu": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "3504", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "6617", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "2065", name: "Item" },
                { id: "3190", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Lux": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3871", name: "Item" },
                { id: "6655", name: "Item" },
                { id: "3020", name: "Item" },
            ],
            situational: [
                { id: "4646", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8128, name: "Dark Harvest", icon: "perk-images/Styles/Domination/DarkHarvest/DarkHarvest.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Malphite": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3047", name: "Item" },
                { id: "3068", name: "Item" },
                { id: "3075", name: "Item" },
            ],
            situational: [
                { id: "3110", name: "Item" },
                { id: "2504", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "2502", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8429, name: "Conditioning", icon: "perk-images/Styles/Resolve/Conditioning/Conditioning.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Malzahar": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "6653", name: "Item" },
            ],
            situational: [
                { id: "3116", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Maokai": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3876", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3002", name: "Item" },
            ],
            situational: [
                { id: "3190", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "2502", name: "Item" },
                { id: "4643", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8439, name: "Aftershock", icon: "perk-images/Styles/Resolve/VeteranAftershock/VeteranAftershock.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "MasterYi": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6672", name: "Item" },
                { id: "3124", name: "Item" },
                { id: "3073", name: "Item" },
            ],
            situational: [
                { id: "3153", name: "Item" },
                { id: "3091", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3748", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Mel": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "4628", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3157", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Milio": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "6620", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "6617", name: "Item" },
                { id: "3504", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "2065", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "MissFortune": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3072", name: "Item" },
                { id: "6676", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "3036", name: "Item" },
                { id: "6694", name: "Item" },
                { id: "3814", name: "Item" },
                { id: "3094", name: "Item" },
                { id: "6673", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "MonkeyKing": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "6610", name: "Item" },
            ],
            situational: [
                { id: "3071", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "6698", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Mordekaiser": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3116", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "4633", name: "Item" },
            ],
            situational: [
                { id: "6653", name: "Item" },
                { id: "3073", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "6665", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Morgana": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3871", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "6653", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "4628", name: "Item" },
                { id: "3165", name: "Item" },
                { id: "3137", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8351, name: "Glacial Augment", icon: "perk-images/Styles/Inspiration/GlacialAugment/GlacialAugment.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Naafiri": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6698", name: "Item" },
                { id: "6696", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "6694", name: "Item" },
                { id: "3161", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3814", name: "Item" },
                { id: "3156", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Nami": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "4005", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "6617", name: "Item" },
                { id: "3504", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "2065", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Nasus": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHaste", name: "Ghost" },
        ],
        items: {
            core: [
                { id: "3158", name: "Item" },
                { id: "3078", name: "Item" },
                { id: "3110", name: "Item" },
            ],
            situational: [
                { id: "3065", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "3742", name: "Item" },
                { id: "6610", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8021, name: "Fleet Footwork", icon: "perk-images/Styles/Precision/FleetFootwork/FleetFootwork.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Nautilus": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3869", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3107", name: "Item" },
            ],
            situational: [
                { id: "3190", name: "Item" },
                { id: "3109", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "2504", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8439, name: "Aftershock", icon: "perk-images/Styles/Resolve/VeteranAftershock/VeteranAftershock.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Neeko": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3869", name: "Item" },
                { id: "3152", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3110", name: "Item" },
                { id: "4646", name: "Item" },
                { id: "3102", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Nidalee": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3100", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8232, name: "Waterwalking", icon: "perk-images/Styles/Sorcery/Waterwalking/Waterwalking.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Nilah": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "6676", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "3036", name: "Item" },
            ],
            situational: [
                { id: "6673", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Nocturne": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6631", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3073", name: "Item" },
            ],
            situational: [
                { id: "3071", name: "Item" },
                { id: "6696", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3814", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Nunu": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6653", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "2504", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "3110", name: "Item" },
                { id: "3157", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8232, name: "Waterwalking", icon: "perk-images/Styles/Sorcery/Waterwalking/Waterwalking.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Olaf": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHaste", name: "Ghost" },
        ],
        items: {
            core: [
                { id: "6631", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "6333", name: "Item" },
            ],
            situational: [
                { id: "6610", name: "Item" },
                { id: "3156", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3073", name: "Item" },
                { id: "2501", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Orianna": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4628", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ornn": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3068", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3075", name: "Item" },
            ],
            situational: [
                { id: "6665", name: "Item" },
                { id: "2502", name: "Item" },
                { id: "3143", name: "Item" },
                { id: "3110", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Pantheon": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3877", name: "Item" },
                { id: "3010", name: "Item" },
                { id: "6610", name: "Item" },
            ],
            situational: [
                { id: "3071", name: "Item" },
                { id: "6692", name: "Item" },
                { id: "3153", name: "Item" },
                { id: "3142", name: "Item" },
                { id: "6333", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Poppy": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3877", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3742", name: "Item" },
            ],
            situational: [
                { id: "3190", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "4401", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "2502", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9923, name: "Hail of Blades", icon: "perk-images/Styles/Domination/HailOfBlades/HailOfBlades.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8141, name: "Deep Ward", icon: "perk-images/Styles/Domination/DeepWard/DeepWard.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8306, name: "Hextech Flashtraption", icon: "perk-images/Styles/Inspiration/HextechFlashtraption/HextechFlashtraption.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Pyke": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3869", name: "Item" },
                { id: "3010", name: "Item" },
                { id: "3179", name: "Item" },
            ],
            situational: [
                { id: "3814", name: "Item" },
                { id: "3142", name: "Item" },
                { id: "6701", name: "Item" },
                { id: "4643", name: "Item" },
                { id: "6696", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9923, name: "Hail of Blades", icon: "perk-images/Styles/Domination/HailOfBlades/HailOfBlades.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8141, name: "Deep Ward", icon: "perk-images/Styles/Domination/DeepWard/DeepWard.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Qiyana": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6698", name: "Item" },
                { id: "6696", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "3814", name: "Item" },
                { id: "6694", name: "Item" },
                { id: "6695", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Quinn": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3010", name: "Item" },
                { id: "6698", name: "Item" },
                { id: "3814", name: "Item" },
            ],
            situational: [
                { id: "6676", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "6695", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Rakan": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3869", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3107", name: "Item" },
            ],
            situational: [
                { id: "3190", name: "Item" },
                { id: "3050", name: "Item" },
                { id: "3109", name: "Item" },
                { id: "2065", name: "Item" },
                { id: "3075", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8465, name: "Guardian", icon: "perk-images/Styles/Resolve/Guardian/Guardian.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8141, name: "Deep Ward", icon: "perk-images/Styles/Domination/DeepWard/DeepWard.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Rammus": {
        role: "jungle",
        spells: [
            { id: "SummonerHaste", name: "Ghost" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3047", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "3742", name: "Item" },
            ],
            situational: [
                { id: "6665", name: "Item" },
                { id: "2502", name: "Item" },
                { id: "3143", name: "Item" },
                { id: "8020", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8439, name: "Aftershock", icon: "perk-images/Styles/Resolve/VeteranAftershock/VeteranAftershock.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8429, name: "Conditioning", icon: "perk-images/Styles/Resolve/Conditioning/Conditioning.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "RekSai": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6631", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3161", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3071", name: "Item" },
                { id: "6610", name: "Item" },
                { id: "3143", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Rell": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3869", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3107", name: "Item" },
            ],
            situational: [
                { id: "3050", name: "Item" },
                { id: "3190", name: "Item" },
                { id: "3109", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "4643", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8439, name: "Aftershock", icon: "perk-images/Styles/Resolve/VeteranAftershock/VeteranAftershock.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8306, name: "Hextech Flashtraption", icon: "perk-images/Styles/Inspiration/HextechFlashtraption/HextechFlashtraption.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Renata": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3876", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3107", name: "Item" },
            ],
            situational: [
                { id: "3190", name: "Item" },
                { id: "4005", name: "Item" },
                { id: "2065", name: "Item" },
                { id: "3109", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8465, name: "Guardian", icon: "perk-images/Styles/Resolve/Guardian/Guardian.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Renekton": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6692", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3071", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3161", name: "Item" },
                { id: "3143", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Rengar": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3142", name: "Item" },
                { id: "6698", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "3814", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "6701", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Riven": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6696", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "6610", name: "Item" },
            ],
            situational: [
                { id: "6333", name: "Item" },
                { id: "6694", name: "Item" },
                { id: "3156", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Rumble": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6653", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "8010", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ryze": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6657", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "3003", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Samira": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "6676", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "3036", name: "Item" },
            ],
            situational: [
                { id: "6673", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3139", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8135, name: "Treasure Hunter", icon: "perk-images/Styles/Domination/TreasureHunter/TreasureHunter.png" },
                    { id: 8143, name: "Sudden Impact", icon: "perk-images/Styles/Domination/SuddenImpact/SuddenImpact.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Sejuani": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3084", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "2502", name: "Item" },
            ],
            situational: [
                { id: "3075", name: "Item" },
                { id: "2504", name: "Item" },
                { id: "3110", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "3068", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8439, name: "Aftershock", icon: "perk-images/Styles/Resolve/VeteranAftershock/VeteranAftershock.png" },
                    { id: 8429, name: "Conditioning", icon: "perk-images/Styles/Resolve/Conditioning/Conditioning.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Senna": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3877", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3071", name: "Item" },
            ],
            situational: [
                { id: "3094", name: "Item" },
                { id: "6617", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "3107", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Seraphine": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "6617", name: "Item" },
                { id: "3222", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "3504", name: "Item" },
                { id: "3165", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8465, name: "Guardian", icon: "perk-images/Styles/Resolve/Guardian/Guardian.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Sett": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6631", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "2501", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "3083", name: "Item" },
                { id: "3161", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Shaco": {
        role: "jungle",
        spells: [
            { id: "SummonerSmite", name: "Smite" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "6653", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "6673", name: "Item" },
                { id: "6699", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8224, name: "Axiom Arcanist", icon: "perk-images/Styles/Sorcery/NullifyingOrb/Axiom_Arcanist.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Shen": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3748", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3084", name: "Item" },
            ],
            situational: [
                { id: "2502", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "2504", name: "Item" },
                { id: "6664", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Shyvana": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3161", name: "Item" },
                { id: "6653", name: "Item" },
                { id: "3020", name: "Item" },
            ],
            situational: [
                { id: "4633", name: "Item" },
                { id: "3050", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8021, name: "Fleet Footwork", icon: "perk-images/Styles/Precision/FleetFootwork/FleetFootwork.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Singed": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3009", name: "Item" },
                { id: "3116", name: "Item" },
                { id: "6653", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "4633", name: "Item" },
                { id: "3742", name: "Item" },
                { id: "6665", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5013, name: "Tenacity", icon: "perk-images/StatMods/StatModsTenacityIcon.png" },
            ]
        }
    },
    "Sion": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6664", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "2502", name: "Item" },
            ],
            situational: [
                { id: "3748", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "2501", name: "Item" },
                { id: "6665", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                    { id: 8429, name: "Conditioning", icon: "perk-images/Styles/Resolve/Conditioning/Conditioning.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Sivir": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3032", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "6675", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Skarner": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3084", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "2502", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "2501", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8429, name: "Conditioning", icon: "perk-images/Styles/Resolve/Conditioning/Conditioning.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Smolder": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3508", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3161", name: "Item" },
            ],
            situational: [
                { id: "3094", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "6673", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8021, name: "Fleet Footwork", icon: "perk-images/Styles/Precision/FleetFootwork/FleetFootwork.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Sona": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3877", name: "Item" },
                { id: "6620", name: "Item" },
                { id: "3009", name: "Item" },
            ],
            situational: [
                { id: "6617", name: "Item" },
                { id: "3003", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "6616", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Soraka": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "6617", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "3107", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "3504", name: "Item" },
                { id: "4643", name: "Item" },
                { id: "3190", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8224, name: "Axiom Arcanist", icon: "perk-images/Styles/Sorcery/NullifyingOrb/Axiom_Arcanist.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Swain": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3871", name: "Item" },
                { id: "3116", name: "Item" },
                { id: "3020", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "6653", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "3165", name: "Item" },
                { id: "2502", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8128, name: "Dark Harvest", icon: "perk-images/Styles/Domination/DarkHarvest/DarkHarvest.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8141, name: "Deep Ward", icon: "perk-images/Styles/Domination/DeepWard/DeepWard.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Sylas": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3152", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Syndra": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "4629", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "TahmKench": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3876", name: "Item" },
                { id: "3084", name: "Item" },
                { id: "3111", name: "Item" },
            ],
            situational: [
                { id: "2502", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "4633", name: "Item" },
                { id: "3083", name: "Item" },
                { id: "3110", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Taliyah": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3158", name: "Item" },
                { id: "3003", name: "Item" },
                { id: "3116", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Talon": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3142", name: "Item" },
                { id: "6696", name: "Item" },
                { id: "3158", name: "Item" },
            ],
            situational: [
                { id: "3814", name: "Item" },
                { id: "6701", name: "Item" },
                { id: "6694", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8021, name: "Fleet Footwork", icon: "perk-images/Styles/Precision/FleetFootwork/FleetFootwork.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Taric": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3190", name: "Item" },
            ],
            situational: [
                { id: "3119", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "3109", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "4643", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8351, name: "Glacial Augment", icon: "perk-images/Styles/Inspiration/GlacialAugment/GlacialAugment.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Teemo": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3020", name: "Item" },
                { id: "6653", name: "Item" },
                { id: "3118", name: "Item" },
            ],
            situational: [
                { id: "4645", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
                { id: "3165", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9101, name: "Absorb Life", icon: "perk-images/Styles/Precision/AbsorbLife/AbsorbLife.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Thresh": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3876", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3190", name: "Item" },
            ],
            situational: [
                { id: "3107", name: "Item" },
                { id: "3109", name: "Item" },
                { id: "3050", name: "Item" },
                { id: "4643", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8465, name: "Guardian", icon: "perk-images/Styles/Resolve/Guardian/Guardian.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Tristana": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3032", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "6675", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3139", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Trundle": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3742", name: "Item" },
            ],
            situational: [
                { id: "3065", name: "Item" },
                { id: "3748", name: "Item" },
                { id: "3075", name: "Item" },
                { id: "3143", name: "Item" },
                { id: "3153", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Tryndamere": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6698", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3036", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3153", name: "Item" },
                { id: "6694", name: "Item" },
                { id: "3139", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5013, name: "Tenacity", icon: "perk-images/StatMods/StatModsTenacityIcon.png" },
            ]
        }
    },
    "TwistedFate": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6657", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3100", name: "Item" },
            ],
            situational: [
                { id: "3094", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8360, name: "Unsealed Spellbook", icon: "perk-images/Styles/Inspiration/UnsealedSpellbook/UnsealedSpellbook.png" },
                    { id: 8242, name: "Unflinching", icon: "perk-images/Styles/Sorcery/Unflinching/Unflinching.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Twitch": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3032", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3031", name: "Item" },
            ],
            situational: [
                { id: "3085", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8233, name: "Absolute Focus", icon: "perk-images/Styles/Sorcery/AbsoluteFocus/AbsoluteFocus.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Udyr": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6653", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3742", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "2502", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Urgot": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3071", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3053", name: "Item" },
            ],
            situational: [
                { id: "6665", name: "Item" },
                { id: "2501", name: "Item" },
                { id: "3742", name: "Item" },
                { id: "3748", name: "Item" },
                { id: "2504", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8005, name: "Press the Attack", icon: "perk-images/Styles/Precision/PressTheAttack/PressTheAttack.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8410, name: "Approach Velocity", icon: "perk-images/Styles/Resolve/ApproachVelocity/ApproachVelocity.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Varus": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3142", name: "Item" },
                { id: "3009", name: "Item" },
                { id: "3004", name: "Item" },
            ],
            situational: [
                { id: "3814", name: "Item" },
                { id: "3302", name: "Item" },
                { id: "6694", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "6701", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Vayne": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3153", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3124", name: "Item" },
            ],
            situational: [
                { id: "3302", name: "Item" },
                { id: "3073", name: "Item" },
                { id: "6665", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3091", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8429, name: "Conditioning", icon: "perk-images/Styles/Resolve/Conditioning/Conditioning.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Veigar": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6657", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "3003", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "3137", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3102", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5010, name: "Unknown Shard", icon: "" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Velkoz": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3871", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4628", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8128, name: "Dark Harvest", icon: "perk-images/Styles/Domination/DarkHarvest/DarkHarvest.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8141, name: "Deep Ward", icon: "perk-images/Styles/Domination/DeepWard/DeepWard.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Vex": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6655", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3137", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8139, name: "Taste of Blood", icon: "perk-images/Styles/Domination/TasteOfBlood/GreenTerror_TasteOfBlood.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Vi": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6610", name: "Item" },
                { id: "3111", name: "Item" },
                { id: "3071", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3156", name: "Item" },
                { id: "6692", name: "Item" },
                { id: "6695", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Viego": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6672", name: "Item" },
                { id: "6676", name: "Item" },
                { id: "6673", name: "Item" },
            ],
            situational: [
                { id: "3036", name: "Item" },
                { id: "3031", name: "Item" },
                { id: "6333", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Viktor": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "2503", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4645", name: "Item" },
            ],
            situational: [
                { id: "3157", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8401, name: "Shield Bash", icon: "perk-images/Styles/Resolve/MirrorShell/MirrorShell.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Vladimir": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHaste", name: "Ghost" },
        ],
        items: {
            core: [
                { id: "3158", name: "Item" },
                { id: "4628", name: "Item" },
                { id: "3089", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "4633", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8230, name: "Phase Rush", icon: "perk-images/Styles/Sorcery/PhaseRush/PhaseRush.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8236, name: "Gathering Storm", icon: "perk-images/Styles/Sorcery/GatheringStorm/GatheringStorm.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Volibear": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "6657", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "6675", name: "Item" },
            ],
            situational: [
                { id: "3065", name: "Item" },
                { id: "4629", name: "Item" },
                { id: "2502", name: "Item" },
                { id: "4633", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5013, name: "Tenacity", icon: "perk-images/StatMods/StatModsTenacityIcon.png" },
            ]
        }
    },
    "Warwick": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6631", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3153", name: "Item" },
            ],
            situational: [
                { id: "3075", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "6665", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8232, name: "Waterwalking", icon: "perk-images/Styles/Sorcery/Waterwalking/Waterwalking.png" },
                    { id: 8234, name: "Celerity", icon: "perk-images/Styles/Sorcery/Celerity/CelerityTemp.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Xayah": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3508", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "6675", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3139", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8009, name: "Presence of Mind", icon: "perk-images/Styles/Precision/PresenceOfMind/PresenceOfMind.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Xerath": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6655", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "3041", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "4628", name: "Item" },
                { id: "3135", name: "Item" },
                { id: "4646", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8128, name: "Dark Harvest", icon: "perk-images/Styles/Domination/DarkHarvest/DarkHarvest.png" },
                    { id: 8106, name: "Ultimate Hunter", icon: "perk-images/Styles/Domination/UltimateHunter/UltimateHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8224, name: "Axiom Arcanist", icon: "perk-images/Styles/Sorcery/NullifyingOrb/Axiom_Arcanist.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "XinZhao": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "6610", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "3071", name: "Item" },
            ],
            situational: [
                { id: "3053", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3156", name: "Item" },
                { id: "3065", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8014, name: "Coup de Grace", icon: "perk-images/Styles/Precision/CoupDeGrace/CoupDeGrace.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Yasuo": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3006", name: "Item" },
                { id: "3153", name: "Item" },
                { id: "6673", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3139", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Yone": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3006", name: "Item" },
                { id: "3153", name: "Item" },
                { id: "6673", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "6333", name: "Item" },
                { id: "3091", name: "Item" },
                { id: "6665", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                    { id: 8473, name: "Bone Plating", icon: "perk-images/Styles/Resolve/BonePlating/BonePlating.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9101, name: "Absorb Life", icon: "perk-images/Styles/Precision/AbsorbLife/AbsorbLife.png" },
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Yorick": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3047", name: "Item" },
                { id: "3078", name: "Item" },
                { id: "3161", name: "Item" },
            ],
            situational: [
                { id: "6694", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "3065", name: "Item" },
                { id: "4401", name: "Item" },
                { id: "6609", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8437, name: "Grasp of the Undying", icon: "perk-images/Styles/Resolve/GraspOfTheUndying/GraspOfTheUndying.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8446, name: "Demolish", icon: "perk-images/Styles/Resolve/Demolish/Demolish.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Yunara": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "6672", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3046", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "6673", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3033", name: "Item" },
                { id: "3072", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8345, name: "Biscuit Delivery", icon: "perk-images/Styles/Inspiration/BiscuitDelivery/BiscuitDelivery.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9103, name: "Legend: Bloodline", icon: "perk-images/Styles/Precision/LegendBloodline/LegendBloodline.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Yuumi": {
        role: "support",
        spells: [
            { id: "SummonerExhaust", name: "Exhaust" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3870", name: "Item" },
                { id: "3504", name: "Item" },
                { id: "6617", name: "Item" },
            ],
            situational: [
                { id: "3222", name: "Item" },
                { id: "6621", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "3041", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                    { id: 8463, name: "Font of Life", icon: "perk-images/Styles/Resolve/FontOfLife/FontOfLife.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Zaahen": {
        role: "top",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "3078", name: "Item" },
                { id: "3047", name: "Item" },
                { id: "6610", name: "Item" },
            ],
            situational: [
                { id: "6333", name: "Item" },
                { id: "3053", name: "Item" },
                { id: "2501", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8444, name: "Second Wind", icon: "perk-images/Styles/Resolve/SecondWind/SecondWind.png" },
                    { id: 8453, name: "Revitalize", icon: "perk-images/Styles/Resolve/Revitalize/Revitalize.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Zac": {
        role: "jungle",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerSmite", name: "Smite" },
        ],
        items: {
            core: [
                { id: "3068", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "3065", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "2502", name: "Item" },
                { id: "3075", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8010, name: "Conqueror", icon: "perk-images/Styles/Precision/Conqueror/Conqueror.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8347, name: "Cosmic Insight", icon: "perk-images/Styles/Inspiration/CosmicInsight/CosmicInsight.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Zed": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6692", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "6699", name: "Item" },
            ],
            situational: [
                { id: "6694", name: "Item" },
                { id: "3814", name: "Item" },
                { id: "6696", name: "Item" },
                { id: "3161", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8369, name: "First Strike", icon: "perk-images/Styles/Inspiration/FirstStrike/FirstStrike.png" },
                    { id: 8017, name: "Cut Down", icon: "perk-images/Styles/Precision/CutDown/CutDown.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                    { id: 8316, name: "Jack Of All Trades", icon: "perk-images/Styles/Inspiration/JackOfAllTrades/JackofAllTrades2.png" },
                ]
            },
            secondary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8321, name: "Cash Back", icon: "perk-images/Styles/Inspiration/CashBack/CashBack2.png" },
                    { id: 9105, name: "Legend: Haste", icon: "perk-images/Styles/Precision/LegendHaste/LegendHaste.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Zeri": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerBarrier", name: "Barrier" },
        ],
        items: {
            core: [
                { id: "3032", name: "Item" },
                { id: "3006", name: "Item" },
                { id: "3085", name: "Item" },
            ],
            situational: [
                { id: "3031", name: "Item" },
                { id: "6673", name: "Item" },
                { id: "3036", name: "Item" },
                { id: "3072", name: "Item" },
                { id: "3073", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8000,
                name: "Precision",
                icon: "perk-images/Styles/7201_Precision.png",
                perks: [
                    { id: 8008, name: "Lethal Tempo", icon: "perk-images/Styles/Precision/LethalTempo/LethalTempoTemp.png" },
                    { id: 8299, name: "Last Stand", icon: "perk-images/Styles/Sorcery/LastStand/LastStand.png" },
                    { id: 8429, name: "Conditioning", icon: "perk-images/Styles/Resolve/Conditioning/Conditioning.png" },
                    { id: 8451, name: "Overgrowth", icon: "perk-images/Styles/Resolve/Overgrowth/Overgrowth.png" },
                ]
            },
            secondary: {
                id: 8400,
                name: "Resolve",
                icon: "perk-images/Styles/7204_Resolve.png",
                perks: [
                    { id: 9104, name: "Legend: Alacrity", icon: "perk-images/Styles/Precision/LegendAlacrity/LegendAlacrity.png" },
                    { id: 9111, name: "Triumph", icon: "perk-images/Styles/Precision/Triumph.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Ziggs": {
        role: "adc",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerTeleport", name: "Teleport" },
        ],
        items: {
            core: [
                { id: "6655", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "4628", name: "Item" },
            ],
            situational: [
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3041", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            secondary: {
                id: 8300,
                name: "Inspiration",
                icon: "perk-images/Styles/7203_Whimsy.png",
                perks: [
                    { id: 8304, name: "Magical Footwear", icon: "perk-images/Styles/Inspiration/MagicalFootwear/MagicalFootwear.png" },
                    { id: 8313, name: "Triple Tonic", icon: "perk-images/Styles/Inspiration/PerfectTiming/AlchemistCabinet.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Zilean": {
        role: "support",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerHeal", name: "Heal" },
        ],
        items: {
            core: [
                { id: "3876", name: "Item" },
                { id: "3158", name: "Item" },
                { id: "2065", name: "Item" },
            ],
            situational: [
                { id: "4005", name: "Item" },
                { id: "3190", name: "Item" },
                { id: "3107", name: "Item" },
                { id: "4629", name: "Item" },
                { id: "4643", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8214, name: "Summon Aery", icon: "perk-images/Styles/Sorcery/SummonAery/SummonAery.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8126, name: "Cheap Shot", icon: "perk-images/Styles/Domination/CheapShot/CheapShot.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8232, name: "Waterwalking", icon: "perk-images/Styles/Sorcery/Waterwalking/Waterwalking.png" },
                ]
            },
            shards: [
                { id: 5007, name: "Ability Haste", icon: "perk-images/StatMods/StatModsCDRScalingIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5001, name: "Health Scaling", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Zoe": {
        role: "mid",
        spells: [
            { id: "SummonerFlash", name: "Flash" },
            { id: "SummonerDot", name: "Ignite" },
        ],
        items: {
            core: [
                { id: "3020", name: "Item" },
                { id: "6655", name: "Item" },
                { id: "3100", name: "Item" },
            ],
            situational: [
                { id: "3041", name: "Item" },
                { id: "3089", name: "Item" },
                { id: "4645", name: "Item" },
                { id: "3135", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8112, name: "Electrocute", icon: "perk-images/Styles/Domination/Electrocute/Electrocute.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8139, name: "Taste of Blood", icon: "perk-images/Styles/Domination/TasteOfBlood/GreenTerror_TasteOfBlood.png" },
                    { id: 8140, name: "Grisly Mementos", icon: "perk-images/Styles/Domination/GrislyMementos/GrislyMementos.png" },
                ]
            },
            secondary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                    { id: 8275, name: "Nimbus Cloak", icon: "perk-images/Styles/Sorcery/NimbusCloak/6361.png" },
                ]
            },
            shards: [
                { id: 5005, name: "Attack Speed", icon: "perk-images/StatMods/StatModsAttackSpeedIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
    "Zyra": {
        role: "support",
        spells: [
            { id: "SummonerExhaust", name: "Exhaust" },
            { id: "SummonerFlash", name: "Flash" },
        ],
        items: {
            core: [
                { id: "3871", name: "Item" },
                { id: "3020", name: "Item" },
                { id: "6653", name: "Item" },
            ],
            situational: [
                { id: "3116", name: "Item" },
                { id: "3157", name: "Item" },
                { id: "3165", name: "Item" },
                { id: "4645", name: "Item" },
            ],
        },
        runes: {
            primary: {
                id: 8200,
                name: "Sorcery",
                icon: "perk-images/Styles/7202_Sorcery.png",
                perks: [
                    { id: 8229, name: "Arcane Comet", icon: "perk-images/Styles/Sorcery/ArcaneComet/ArcaneComet.png" },
                    { id: 8105, name: "Relentless Hunter", icon: "perk-images/Styles/Domination/RelentlessHunter/RelentlessHunter.png" },
                    { id: 8139, name: "Taste of Blood", icon: "perk-images/Styles/Domination/TasteOfBlood/GreenTerror_TasteOfBlood.png" },
                    { id: 8210, name: "Transcendence", icon: "perk-images/Styles/Sorcery/Transcendence/Transcendence.png" },
                ]
            },
            secondary: {
                id: 8100,
                name: "Domination",
                icon: "perk-images/Styles/7200_Domination.png",
                perks: [
                    { id: 8226, name: "Manaflow Band", icon: "perk-images/Styles/Sorcery/ManaflowBand/ManaflowBand.png" },
                    { id: 8237, name: "Scorch", icon: "perk-images/Styles/Sorcery/Scorch/Scorch.png" },
                ]
            },
            shards: [
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5008, name: "Adaptive Force", icon: "perk-images/StatMods/StatModsAdaptiveForceIcon.png" },
                { id: 5011, name: "Health", icon: "perk-images/StatMods/StatModsHealthScalingIcon.png" },
            ]
        }
    },
};
