/**
 * Constants for SummonerAPI Frontend
 * Centralizes magic numbers and configuration values
 */

// Debounce delays (ms)
export const DEBOUNCE_DELAYS = {
  SEARCH: 300,
  FILTER: 200,
  JUST_OPENED: 200
};

// Cache durations (ms)
export const CACHE_DURATION = {
  RECENT_CHAMPIONS: 5000, // 5 seconds
  SUGGESTIONS: 0 // No caching for suggestions
};

// Request timeouts and retries
export const REQUEST_CONFIG = {
  MAX_LOAD_ATTEMPTS: 3,
  LOAD_MORE_TIMEOUT: 0 // No timeout
};

// UI dimensions and limits
export const UI_CONFIG = {
  SUGGESTIONS_MAX_HEIGHT: 320,
  MATCH_PAGE_SIZE: 10,
  RECENT_CHAMPIONS_LIMIT: 5,
  CHART_TOP_CHAMPIONS: 8
};

// Z-index layers
export const Z_INDEX = {
  BASE: 0,
  CONTROLS: 10,
  DROPDOWN: 1060,
  NAVBAR: 1020
};

// Theme configurations
export const THEMES = {
  DARK: 'dark',
  LIGHT: 'light'
};

// Bootstrap theme mappings
export const BOOTSWATCH_THEMES = {
  dark: 'darkly',
  light: 'lux'
};

// Queue type mappings
export const QUEUE_TYPES = {
  RANKED_SOLO: 420,
  RANKED_FLEX: 440,
  NORMAL_DRAFT: 400,
  NORMAL_BLIND: 430,
  ARAM: 450,
  CLASH: 700,
  ARENA: 1700,
  URF_OLD: 900,
  URF: 1900,
  TFT_RANKED: 1100,
  TFT_NORMAL: 1090,
  TFT_DOUBLE_UP: 1130,
  TFT_HYPER_ROLL: 1160,
  CUSTOM: 0
};

// Queue name mappings
export const QUEUE_NAMES = {
  [QUEUE_TYPES.RANKED_SOLO]: 'Ranked Solo/Duo',
  [QUEUE_TYPES.RANKED_FLEX]: 'Ranked Flex',
  [QUEUE_TYPES.NORMAL_DRAFT]: 'Normal Draft',
  [QUEUE_TYPES.NORMAL_BLIND]: 'Normal Blind',
  [QUEUE_TYPES.ARAM]: 'ARAM',
  [QUEUE_TYPES.CLASH]: 'Clash',
  [QUEUE_TYPES.ARENA]: 'Arena',
  [QUEUE_TYPES.URF]: 'URF',
  [QUEUE_TYPES.URF_OLD]: 'URF (old)',
  [QUEUE_TYPES.TFT_RANKED]: 'TFT Ranked',
  [QUEUE_TYPES.TFT_NORMAL]: 'TFT Normal',
  [QUEUE_TYPES.TFT_DOUBLE_UP]: 'TFT Double Up',
  [QUEUE_TYPES.TFT_HYPER_ROLL]: 'TFT Hyper Roll',
  [QUEUE_TYPES.CUSTOM]: 'Custom'
};

// Game duration thresholds
export const GAME_DURATION = {
  REMAKE_THRESHOLD: 300, // 5 minutes in seconds
  MIN_DURATION: 0
};

// LP change thresholds
export const LP_CHANGE = {
  GAIN: 0,
  LOSS: 0
};

// KDA calculations
export const KDA_CONFIG = {
  PERFECT_THRESHOLD: 0,
  DECIMAL_PLACES: 2
};

// Image dimensions
export const IMAGE_SIZES = {
  PROFILE_AVATAR: { width: 112, height: 112 },
  CHAMP_ICON_RECENT: { width: 28, height: 28 },
  CHAMP_ICON_TABLE: { width: 24, height: 24 },
  CHAMP_ICON_PERFORMANCE: { width: 32, height: 32 },
  SUGGESTION_AVATAR: { width: 32, height: 32 },
  CHAMP_PORTRAIT: { width: 96, height: 96 },
  SPELL_ICON: { width: 48, height: 48 },
  PASSIVE_ICON: { width: 48, height: 48 }
};

// LocalStorage keys
export const STORAGE_KEYS = {
  SEARCH_HISTORY: 'searchHistory',
  THEME: 'theme',
  MAX_HISTORY_SIZE: 10
};

// URL patterns (relative to avoid hardcoding)
export const URLS = {
  API: {
    SUGGESTIONS: '/api/summoner-suggestions',
    MATCHES: '/api/matches'
  },
  STATIC: {
    CHAMP_SQUARE: '/img/champ-square/', // To be configured from backend
    SPLASH: '/img/splash/',
    PASSIVE: '/img/passive/',
    SPELL: '/img/spell/'
  }
};

// CSS class names
export const CSS_CLASSES = {
  ACTIVE: 'active',
  LOADING: 'match-row-loading',
  REMAKE: 'remake',
  WIN: 'win',
  LOSS: 'loss',
  SELF_ROW: 'table-info self-row',
  DROPDOWN_OPEN: 'search-dropdown-open'
};

// ARIA attributes
export const ARIA = {
  EXPANDED: 'aria-expanded',
  SELECTED: 'aria-selected',
  BUSY: 'aria-busy',
  HIDDEN: 'aria-hidden',
  LABEL: 'aria-label',
  PRESSED: 'aria-pressed',
  DESCRIBEDBY: 'aria-describedby',
  AUTOCOMPLETE: 'aria-autocomplete',
  HASPOPUP: 'aria-haspopup',
  CONTROLS: 'aria-controls',
  ACTIVEDESCENDANT: 'aria-activedescendant'
};

// Event types
export const EVENTS = {
  INPUT: 'input',
  CLICK: 'click',
  FOCUS: 'focus',
  BLUR: 'blur',
  KEYDOWN: 'keydown',
  SUBMIT: 'submit',
  POINTERDOWN: 'pointerdown',
  FOCUSIN: 'focusin',
  LOAD: 'load',
  COLLAPSE_SHOWN: 'shown.bs.collapse',
  BEFOREUNLOAD: 'beforeunload'
};

// Key codes
export const KEYS = {
  ENTER: 'Enter',
  ESCAPE: 'Escape',
  TAB: 'Tab',
  ARROW_UP: 'ArrowUp',
  ARROW_DOWN: 'ArrowDown',
  SPACE: ' '
};

// DOM selectors
export const SELECTORS = {
  RIOT_ID_INPUT: '#riotId',
  SUGGESTIONS_CONTAINER: '#suggestions-container',
  SEARCH_STATUS: '#searchStatus',
  MATCH_LIST: '#matchList',
  LOAD_MORE_BTN: '#loadMoreBtn',
  THEME_TOGGLE: '#themeToggle',
  CHAMP_SEARCH: '#champSearch',
  CHAMP_SEARCH_STATUS: '#champSearchStatus',
  CHAMP_LIST: '#champList',
  FILTER_ALL: '#filterAll',
  FILTER_RANKED: '#filterRanked',
  HISTORY_FILTERS: '#historyFilters',
  MATCH_SEARCH: '#matchSearch',
  MATCH_SEARCH_STATUS: '#matchSearchStatus',
  SUMMARY_WR: '#summaryWR',
  SUMMARY_KDA: '#summaryKDA',
  SUMMARY_COUNT: '#summaryCount',
  QUEUE_TOGGLE: '#queueToggle',
  QUEUE_DROPDOWN: '#queueDropdown',
  NO_MATCHES_MSG: '#noMatchesMsg',
  RECENT_CHAMPS_BODY: '#recentChampsBody',
  RECENT_COUNT: '#recentCount',
  LP_CHART: '#lpChart',
  CHAMPION_CHART: '#championChart'
};

// Error messages
export const MESSAGES = {
  CHART_FAILED: 'Chart rendering failed',
  LOAD_FAILED: 'Load more failed',
  INVALID_DATA: 'Invalid match data',
  NO_MORE_MATCHES: 'No more matches',
  ERROR_LOADING: 'Error loading matches',
  CHART_EMPTY_LP: 'No ranked LP data available for recent matches yet.',
  CHART_EMPTY_CHAMP: 'No champion distribution data available yet.',
  UNABLE_RECENT_CHAMPS: 'Unable to display recent champions.'
};
