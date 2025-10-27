/**
 * Curated champion builds and item recommendations.
 * 
 * <p>This module contains pre-configured item builds for specific champions,
 * providing recommended starting items, core build paths, and situational items.
 * These builds are curated by the development team and serve as default
 * recommendations when statistical data is not available.</p>
 * 
 * <p>Each build includes:</p>
 * <ul>
 *   <li>Starting items for early game</li>
 *   <li>Core build order with power spike information</li>
 *   <li>Item metadata and descriptions</li>
 *   <li>Recommended item counts for consumables</li>
 * </ul>
 * 
 * @module data/curatedBuilds
 * @author zerox80
 * @version 2.0
 */

/**
 * Item ID constants for referenced items in curated builds.
 * 
 * <p>These constants map to Riot Games' item IDs and are used to ensure
 * consistency across all curated builds. Item IDs are sourced from the
 * official Riot Games Data Dragon API.</p>
 * 
 * @constant
 * @type {Object}
 * @property {number} doransRing - Item ID for Doran's Ring (1056)
 * @property {number} healthPotion - Item ID for Health Potion (2003)
 * @property {number} rodOfAges - Item ID for Rod of Ages (6665)
 * @property {number} sorcerersShoes - Item ID for Sorcerer's Shoes (3020)
 * @property {number} seraphsEmbrace - Item ID for Seraph's Embrace (3040)
 * @property {number} liandrysTorment - Item ID for Liandry's Torment (6653)
 */
const ITEM_IDS = {
  doransRing: 1056,
  healthPotion: 2003,
  rodOfAges: 6665,
  sorcerersShoes: 3020,
  seraphsEmbrace: 3040,
  liandrysTorment: 6653
};

/**
 * Curated champion builds organized by champion key.
 * 
 * <p>This object contains pre-configured builds for specific champions,
 * with each build including item groups, metadata, and recommended paths.
 * Builds are designed to provide solid default recommendations for players
 * who are new to a champion or looking for standard build patterns.</p>
 * 
 * <p>Build structure:</p>
 * <ul>
 *   <li>Each champion has a title and array of item groups</li>
 *   <li>Groups are categorized by type (starting, core, situational)</li>
 *   <li>Items include ID, name, count, and sequence information</li>
 *   <li>Metadata provides context and timing information</li>
 * </ul>
 * 
 * @constant
 * @type {Object}
 * @example
 * // Access Anivia's curated build
 * const aniviaBuild = CURATED_BUILDS.anivia;
 * console.log(aniviaBuild.title); // "Suggested Item Path"
 */
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
