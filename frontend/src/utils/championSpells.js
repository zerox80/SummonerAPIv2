/**
 * Utilities for processing and merging champion spell data.
 * 
 * <p>This module provides functions to merge base champion ability data from the Riot API
 * with curated spell data from local JSON files. The curated data can override or enhance
 * the base data with improved descriptions, additional notes, and corrected information.
 * This allows for better spell descriptions and gameplay guidance while maintaining
 * the official data as a fallback.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Merges base API data with curated local data</li>
 *   <li>Prioritizes curated descriptions over tooltips</li>
 *   <li>Combines notes from both sources without duplication</li>
 *   <li>Supports multiple locales for curated data</li>
 *   <li>Handles missing or malformed data gracefully</li>
 * </ul>
 * 
 * @module utils/championSpells
 * @author zerox80
 * @version 2.0
 */

/** Cached curated spell data loaded from JSON files */
let curatedById = {};

/**
 * Dynamically imports curated spell data from JSON files.
 * 
 * <p>This uses Vite's import.meta.glob to load all champion spell JSON files
 * from the data directory. Files are loaded eagerly and the English version
 * is used as the primary source for curated data.</p>
 */
const curatedModules = import.meta.glob('../data/championSpells.*.json', {
  eager: true,
  import: 'default'
});

/**
 * Extracts the English curated spell data from all loaded modules.
 * 
 * <p>The English version (.en.json) is used as the default curated data
 * source. Other locales can be added in the future for internationalization.</p>
 */
const curatedEnglish = Object.entries(curatedModules)
  .find(([path]) => path.endsWith('.en.json'))?.[1];

/**
 * Processes and normalizes the curated spell data for efficient lookup.
 * 
 * <p>The curated data is converted to a lookup object with lowercase keys
 * for case-insensitive champion matching. This allows for flexible matching
 * against champion IDs or names.</p>
 */
if (curatedEnglish && typeof curatedEnglish === 'object' && curatedEnglish !== null) {
  curatedById = Object.entries(curatedEnglish).reduce((accumulator, [key, value]) => {
    accumulator[key.toLowerCase()] = value;
    return accumulator;
  }, {});
}

/**
 * Merges and deduplicates notes arrays from base and curated sources.
 * 
 * <p>This function combines notes from both sources, removes duplicates,
 * trims whitespace, and filters out empty strings. The result is a clean
 * array of unique notes.</p>
 * 
 * @param {Array} [baseNotes=[]] - Notes from the base API data
 * @param {Array} [curatedNotes=[]] - Notes from the curated data
 * @returns {Array} Merged and deduplicated array of notes
 */
function mergeNotes(baseNotes = [], curatedNotes = []) {
  const normalized = [...baseNotes, ...curatedNotes]
    .map((entry) => (typeof entry === 'string' ? entry.trim() : ''))
    .filter(Boolean);
  return [...new Set(normalized)];
}

/**
 * Returns the curated value if it's valid, otherwise returns the fallback value.
 * 
 * <p>This helper function implements the preference logic for merging data,
 * where curated data takes priority over base API data as long as it's not
 * null, undefined, or an empty string. This ensures that only meaningful
 * curated data overrides the base data.</p>
 * 
 * @param {*} curated - The curated value to prefer (can be any type)
 * @param {*} fallback - The fallback value to use if curated is invalid
 * @returns {*} The curated value if valid, otherwise the fallback value
 */
function prefer(curated, fallback) {
  return curated !== undefined && curated !== null && curated !== '' ? curated : fallback;
}

/**
 * Merges base passive ability data with curated passive data.
 * 
 * <p>This function combines passive ability information from the Riot API with
 * curated data, preferring curated values for name, description, and image.
 * Notes from both sources are merged and deduplicated. The function handles
 * missing data gracefully and returns null if both sources are empty.</p>
 * 
 * @param {Object} basePassive - Passive data from the base API
 * @param {Object} curatedPassive - Passive data from curated sources
 * @returns {Object|null} Merged passive data or null if no data exists
 */
function mergePassive(basePassive, curatedPassive) {
  if (!basePassive && !curatedPassive) return null;
  const merged = {
    ...basePassive
  };

  if (curatedPassive) {
    if (curatedPassive.name) merged.name = curatedPassive.name;
    if (curatedPassive.description) {
      merged.description = curatedPassive.description;
      merged.tooltip = curatedPassive.description;
    }
    if (curatedPassive.imageFull && !merged.imageFull) {
      merged.imageFull = curatedPassive.imageFull;
    }
    const mergedNotes = mergeNotes(basePassive?.notes, curatedPassive.notes);
    if (mergedNotes.length > 0) merged.notes = mergedNotes;
  }

  return merged;
}

/**
 * Merges base spell data with curated spell data.
 * 
 * <p>This function combines spell information from the Riot API with curated data,
 * preferring curated values for name, description, and gameplay statistics.
 * The function handles all spell properties including cooldown, cost, range,
 * damage, and scaling. Notes from both sources are merged and deduplicated.</p>
 * 
 * @param {Object} baseSpell - Spell data from the base API
 * @param {Object} curatedSpell - Spell data from curated sources
 * @returns {Object} Merged spell data with curated enhancements
 */
function mergeSpell(baseSpell, curatedSpell) {
  const merged = {
    ...baseSpell
  };

  merged.name = prefer(curatedSpell?.name, merged.name);

  const description = prefer(curatedSpell?.description, merged.tooltip || merged.description);
  if (description) {
    merged.description = description;
    merged.tooltip = description;
  }

  merged.cooldown = prefer(curatedSpell?.cooldown, merged.cooldown);
  merged.cost = prefer(curatedSpell?.cost, merged.cost);
  merged.range = prefer(curatedSpell?.range, merged.range);
  merged.damage = prefer(curatedSpell?.damage, merged.damage);
  merged.scaling = prefer(curatedSpell?.scaling, merged.scaling);

  const mergedNotes = mergeNotes(baseSpell?.notes, curatedSpell?.notes);
  if (mergedNotes.length > 0) {
    merged.notes = mergedNotes;
  }

  return merged;
}

/**
 * Merges champion ability data from the API with curated local data.
 * 
 * <p>This is the main export function that combines base champion data from the Riot API
 * with curated spell data from local JSON files. It enhances the base data with improved
 * descriptions, additional notes, and corrected information while maintaining the
 * official data as a fallback. The function handles both passive abilities and active spells.</p>
 * 
 * <p>The function uses a case-insensitive lookup to match champion data, allowing
 * flexibility in champion identification. If no curated data is found for a champion,
 * the original API data is returned unchanged.</p>
 * 
 * @param {Object} championDetail - Champion detail object from the Riot API
 * @param {string} championDetail.id - Champion ID for lookup
 * @param {string} championDetail.name - Champion name (alternative lookup key)
 * @param {Object} championDetail.passive - Passive ability data
 * @param {Array} championDetail.spells - Array of active spell data
 * @returns {Object} Merged champion abilities with curated enhancements
 * @returns {Object|null} returns.passive - Merged passive ability data or null
 * @returns {Array} returns.spells - Array of merged spell data
 * 
 * @example
 * // Basic usage
 * const champion = await api.champion({ id: 'Anivia' });
 * const mergedAbilities = mergeChampionAbilities(champion);
 * console.log(mergedAbilities.passive.description); // Enhanced description
 * 
 * @example
 * // Handling missing data
 * const emptyResult = mergeChampionAbilities(null);
 * console.log(emptyResult); // { passive: null, spells: [] }
 */
export function mergeChampionAbilities(championDetail) {
  if (!championDetail) {
    return {
      passive: null,
      spells: []
    };
  }

  const lookupKey = (championDetail.id || championDetail.name || '').toLowerCase();
  const curated = curatedById[lookupKey];

  const passive = mergePassive(championDetail.passive, curated?.passive);

  const baseSpells = Array.isArray(championDetail.spells) ? championDetail.spells : [];
  const mergedSpells = baseSpells.map((spell) => {
    const curatedSpell = curated?.spells?.find((entry) => entry.id === spell.id);
    return mergeSpell(spell, curatedSpell);
  });

  return {
    passive,
    spells: mergedSpells
  };
}
