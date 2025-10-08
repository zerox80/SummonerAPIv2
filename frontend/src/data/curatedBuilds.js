const ITEM_IDS = {
  doransRing: 1056,
  healthPotion: 2003,
  rodOfAges: 6665,
  sorcerersShoes: 3020,
  seraphsEmbrace: 3040,
  liandrysTorment: 6653
};

export const CURATED_BUILDS = {
  anivia: {
    title: 'Suggested Item Path',
    groups: [
      {
        key: 'starting',
        title: 'Starting Items',
        subtitle: 'Doran\'s Ring + 2x Health Potion',
        meta: 'Best for most matchups',
        items: [
          { id: ITEM_IDS.doransRing, name: "Doran's Ring" },
          { id: ITEM_IDS.healthPotion, name: 'Health Potion', count: 2 }
        ]
      },
      {
        key: 'core',
        title: 'Core Build Order',
        subtitle: 'Rod of Ages ➜ Sorcerer\'s Shoes ➜ Seraph\'s Embrace ➜ Liandry\'s Torment',
        meta: 'Power spikes after the third item',
        sequence: true,
        items: [
          { id: ITEM_IDS.rodOfAges, name: 'Rod of Ages' },
          { id: ITEM_IDS.sorcerersShoes, name: "Sorcerer\'s Shoes" },
          { id: ITEM_IDS.seraphsEmbrace, name: "Seraph\'s Embrace" },
          { id: ITEM_IDS.liandrysTorment, name: "Liandry\'s Torment" }
        ]
      }
    ]
  }
};
