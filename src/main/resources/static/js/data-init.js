/**
 * Data initialization for chart components
 * Populates window globals with server-rendered data
 */

// Chart data
window.leagueEntriesData = [
/*# th:each="e, iterStat : ${leagueEntries}" */
/*# th:if="${e != null}" */
    { queueType: /*[[${e?.queueType}]]*/'', lp: /*[[${e?.leaguePoints}]]*/0 }/*[[${iterStat.last ? '' : ','}]]*/
/*# /th:if */
/*# /th:each */
];

window.matchesData = [
/*# th:each="m, iterStat : ${matchHistory}" */
/*# th:if="${m != null}" */
    { t: /*[[${m?.info?.gameEndTimestamp}]]*/0, q: /*[[${m?.info?.queueId}]]*/0, d: /*[[${m?.info?.lpChange}]]*/null }/*[[${iterStat.last ? '' : ','}]]*/
/*# /th:if */
/*# /th:each */
];

window.champLabelsData = [
/*# th:each="e, iterStat : ${championPlayCounts.entrySet()} " */
/*# th:if="${e != null}" */
    /*[[${e?.key}]]*/''/*[[${iterStat.last ? '' : ','}]]*/
/*# /th:if */
/*# /th:each */
];

window.champValuesData = [
/*# th:each="e, iterStat : ${championPlayCounts.entrySet()} " */
/*# th:if="${e != null}" */
    /*[[${e?.value}]]*/0/*[[${iterStat.last ? '' : ','}]]*/
/*# /th:if */
/*# /th:each */
];

window.matchesPageSizeData = /*[[${matchesPageSize}]]*/ 10;
