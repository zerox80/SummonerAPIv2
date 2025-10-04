let curatedById = {};

try {
  // eslint-disable-next-line global-require, import/no-unresolved
  const curatedData = require('../data/championSpells.de.json');
  curatedById = Object.entries(curatedData).reduce((accumulator, [key, value]) => {
    accumulator[key.toLowerCase()] = value;
    return accumulator;
  }, {});
} catch (error) {
  curatedById = {};
}

function mergeNotes(baseNotes = [], curatedNotes = []) {
  const normalized = [...baseNotes, ...curatedNotes]
    .map((entry) => (typeof entry === 'string' ? entry.trim() : ''))
    .filter(Boolean);
  return [...new Set(normalized)];
}

function prefer(curated, fallback) {
  return curated !== undefined && curated !== null && curated !== '' ? curated : fallback;
}

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
