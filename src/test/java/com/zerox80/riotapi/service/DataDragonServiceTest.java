package com.zerox80.riotapi.service;

import com.zerox80.riotapi.model.ChampionDetail;
import com.zerox80.riotapi.model.SpellSummary;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class DataDragonServiceTest {

    @Test
    void getImageBasesResolvesLatestPlaceholder() throws Exception {
        Map<String, String> responses = new HashMap<>();
        responses.put("https://ddragon.leagueoflegends.com/api/versions.json", "[\"15.18.1\",\"15.17.1\"]");

        DataDragonService service = new DataDragonService(
                new StubHttpClient(responses),
                "de_DE",
                "SummonerAPI-Test/1.0",
                null
        );

        Map<String, String> bases = service.getImageBases("latest");

        assertEquals("15.18.1", bases.get("version"));
        assertEquals("https://ddragon.leagueoflegends.com/cdn/15.18.1/img/champion/", bases.get("champSquare"));
    }

    @Test
    void getChampionDetailProvidesLocaleAwareSpells() throws Exception {
        Map<String, String> responses = new HashMap<>();
        responses.put("https://ddragon.leagueoflegends.com/api/versions.json", "[\"15.18.1\"]");
        responses.put("https://ddragon.leagueoflegends.com/cdn/15.18.1/data/en_US/champion.json", "{\n  \"data\": {\n    \"Anivia\": {\n      \"id\": \"Anivia\",\n      \"name\": \"Anivia\",\n      \"title\": \"The Cryophoenix\",\n      \"tags\": [\"Mage\"],\n      \"image\": { \"full\": \"Anivia.png\" }\n    }\n  }\n}");
        responses.put("https://ddragon.leagueoflegends.com/cdn/15.18.1/data/en_US/champion/Anivia.json", sampleChampionDetailJson());
        responses.put("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/en_us/v1/champions/34.json", sampleCDragonJson());
        responses.put("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champions/34.json", sampleCDragonJson());

        DataDragonService service = new DataDragonService(
                new StubHttpClient(responses),
                "de_DE",
                "SummonerAPI-Test/1.0",
                null
        );

        ChampionDetail detail = service.getChampionDetail("Anivia", Locale.ENGLISH);

        assertNotNull(detail.getPassive(), "Passive should be populated");
        List<SpellSummary> spells = detail.getSpells();
        assertNotNull(spells);
        assertEquals(4, spells.size(), "All four spells should be present");
        assertTrue(spells.stream().allMatch(spell -> spell.getTooltip() != null && spell.getTooltip().matches(".*\\d+.*")),
                "Rendered tooltips should contain numeric values");
        assertEquals("12/11/10", spells.get(0).getCooldown());
        assertEquals("1100", spells.get(0).getRange());
    }

    @Test
    void sanitizeHtmlFallsBackToPlainTextWhenPolicyThrows() throws Exception {
        DataDragonService service = new ExplodingTooltipService(new StubHttpClient(Map.of()));

        Method sanitize = DataDragonService.class.getDeclaredMethod("sanitizeHtml", String.class);
        sanitize.setAccessible(true);

        String sanitized = (String) sanitize.invoke(service, "<b>Hi</b><script>alert('x')</script>");

        assertEquals("Hi alert('x')", sanitized);
        assertFalse(sanitized.contains("<"));
        assertFalse(sanitized.contains(">"));
    }

    private static String sampleChampionDetailJson() {
        return "{\n  \"data\": {\n    \"Anivia\": {\n      \"id\": \"Anivia\",\n      \"key\": \"34\",\n      \"name\": \"Anivia\",\n      \"title\": \"The Cryophoenix\",\n      \"lore\": \"Sample lore\",\n      \"tags\": [\"Mage\"],\n      \"image\": { \"full\": \"Anivia.png\" },\n      \"passive\": {\n        \"name\": \"Rebirth\",\n        \"description\": \"Upon taking fatal damage...\",\n        \"image\": { \"full\": \"Anivia_P.png\" }\n      },\n      \"spells\": [\n        {\n          \"id\": \"FlashFrost\",\n          \"name\": \"Flash Frost\",\n          \"tooltip\": \"Anivia hurls a sphere dealing {{ e1 }} magic damage.\",\n          \"cooldownBurn\": \"12/11/10\",\n          \"costBurn\": \"80\",\n          \"rangeBurn\": \"1100\",\n          \"effectBurn\": [null, \"60/80/100\"],\n          \"image\": { \"full\": \"FlashFrost.png\" },\n          \"datavalues\": {},\n          \"vars\": [],\n          \"leveltip\": {\n            \"label\": [\"Damage\"],\n            \"effect\": [\"+20\"]\n          }\n        },\n        {\n          \"id\": \"Crystallize\",\n          \"name\": \"Crystallize\",\n          \"tooltip\": \"Creates a wall for {{ e1 }} seconds.\",\n          \"cooldownBurn\": \"17/16/15\",\n          \"costBurn\": \"70\",\n          \"rangeBurn\": \"1000\",\n          \"effectBurn\": [null, \"5\"],\n          \"image\": { \"full\": \"Crystallize.png\" },\n          \"datavalues\": {},\n          \"vars\": []\n        },\n        {\n          \"id\": \"Frostbite\",\n          \"name\": \"Frostbite\",\n          \"tooltip\": \"Deals {{ e1 }} damage (double vs chilled targets).\",\n          \"cooldownBurn\": \"4\",\n          \"costBurn\": \"50\",\n          \"rangeBurn\": \"650\",\n          \"effectBurn\": [null, \"55/85/115/145/175\"],\n          \"image\": { \"full\": \"Frostbite.png\" },\n          \"datavalues\": {},\n          \"vars\": []\n        },\n        {\n          \"id\": \"GlacialStorm\",\n          \"name\": \"Glacial Storm\",\n          \"tooltip\": \"Deals {{ e1 }} damage per second.\",\n          \"cooldownBurn\": \"0.5\",\n          \"costBurn\": \"60\",\n          \"rangeBurn\": \"750\",\n          \"effectBurn\": [null, \"40/55/70\"],\n          \"image\": { \"full\": \"GlacialStorm.png\" },\n          \"datavalues\": {},\n          \"vars\": []\n        }\n      ]\n    }\n  }\n}\n";
    }

    private static String sampleCDragonJson() {
        return "{\n  \"spells\": [\n    { \"dynamicDescription\": \"Deals 60 damage.\" },\n    { \"dynamicDescription\": \"Creates a wall for 5 seconds.\" },\n    { \"dynamicDescription\": \"Deals 100 damage.\" },\n    { \"dynamicDescription\": \"Storm deals 40 damage per second.\" }\n  ]\n}\n";
    }

    @Test
    void getChampionDetail_sanitizesTooltips() throws Exception {
        String maliciousTooltip = "<script>alert(1)</script><b>Bold</b><a href=\"http://x\" onclick=\"evil()\">link</a><magicDamage>123</magicDamage>";
        Map<String, String> responses = new HashMap<>();
        responses.put("https://ddragon.leagueoflegends.com/api/versions.json", "[\"15.18.1\"]");
        responses.put("https://ddragon.leagueoflegends.com/cdn/15.18.1/data/en_US/champion.json", "{\n  \"data\": {\n    \"Anivia\": {\n      \"id\": \"Anivia\",\n      \"name\": \"Anivia\",\n      \"title\": \"The Cryophoenix\",\n      \"tags\": [\"Mage\"],\n      \"image\": { \"full\": \"Anivia.png\" }\n    }\n  }\n}");
        String detail = "{\n  \"data\": {\n    \"Anivia\": {\n      \"id\": \"Anivia\",\n      \"key\": \"34\",\n      \"name\": \"Anivia\",\n      \"title\": \"The Cryophoenix\",\n      \"lore\": \"Sample lore\",\n      \"tags\": [\"Mage\"],\n      \"image\": { \"full\": \"Anivia.png\" },\n      \"passive\": {\n        \"name\": \"Rebirth\",\n        \"description\": \"" + maliciousTooltip + "\",\n        \"image\": { \"full\": \"Anivia_P.png\" }\n      },\n      \"spells\": [\n        {\n          \"id\": \"FlashFrost\",\n          \"name\": \"Flash Frost\",\n          \"tooltip\": \"" + maliciousTooltip + "\",\n          \"cooldownBurn\": \"12/11/10\",\n          \"costBurn\": \"80\",\n          \"rangeBurn\": \"1100\",\n          \"effectBurn\": [null, \"60/80/100\"],\n          \"image\": { \"full\": \"FlashFrost.png\" },\n          \"datavalues\": {},\n          \"vars\": []\n        },\n        {\n          \"id\": \"Crystallize\",\n          \"name\": \"Crystallize\",\n          \"tooltip\": \"Creates a wall.\",\n          \"cooldownBurn\": \"17/16/15\",\n          \"costBurn\": \"70\",\n          \"rangeBurn\": \"1000\",\n          \"effectBurn\": [null, \"5\"],\n          \"image\": { \"full\": \"Crystallize.png\" },\n          \"datavalues\": {},\n          \"vars\": []\n        },\n        {\n          \"id\": \"Frostbite\",\n          \"name\": \"Frostbite\",\n          \"tooltip\": \"Deals damage.\",\n          \"cooldownBurn\": \"4\",\n          \"costBurn\": \"50\",\n          \"rangeBurn\": \"650\",\n          \"effectBurn\": [null, \"55/85/115/145/175\"],\n          \"image\": { \"full\": \"Frostbite.png\" },\n          \"datavalues\": {},\n          \"vars\": []\n        },\n        {\n          \"id\": \"GlacialStorm\",\n          \"name\": \"Glacial Storm\",\n          \"tooltip\": \"Storm\",\n          \"cooldownBurn\": \"4\",\n          \"costBurn\": \"50\",\n          \"rangeBurn\": \"650\",\n          \"effectBurn\": [null, \"10\"],\n          \"image\": { \"full\": \"GlacialStorm.png\" },\n          \"datavalues\": {},\n          \"vars\": []\n        }\n      ]\n    }\n  }\n}";
        responses.put("https://ddragon.leagueoflegends.com/cdn/15.18.1/data/en_US/champion/Anivia.json", detail);
        responses.put("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/en_us/v1/champions/34.json", sampleCDragonJson());
        responses.put("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champions/34.json", sampleCDragonJson());

        DataDragonService service = new DataDragonService(
                new StubHttpClient(responses),
                "de_DE",
                "SummonerAPI-Test/1.0",
                null
        );

        ChampionDetail detailObj = service.getChampionDetail("Anivia", java.util.Locale.ENGLISH);
        assertNotNull(detailObj);
        assertNotNull(detailObj.getPassive());
        String pdesc = detailObj.getPassive().getDescription();
        // scripts and anchor attributes removed; sanitized output should not contain the malicious payload
        assertFalse(pdesc.toLowerCase().contains("<script"));
        assertFalse(pdesc.toLowerCase().contains("onclick="));
        assertFalse(pdesc.toLowerCase().contains("href="));
        assertFalse(pdesc.contains("alert(1)"));
        assertNotNull(detailObj.getSpells());
        String tooltip0 = detailObj.getSpells().get(0).getTooltip();
        assertFalse(tooltip0.toLowerCase().contains("<script"));
        assertFalse(tooltip0.toLowerCase().contains("onclick="));
        assertFalse(tooltip0.toLowerCase().contains("href="));
        assertFalse(tooltip0.contains("alert(1)"));
    }

    private static class ExplodingTooltipService extends DataDragonService {
        ExplodingTooltipService(HttpClient client) {
            super(client, "de_DE", "SummonerAPI-Test/1.0", null);
        }

        @Override
        protected String applyTooltipPolicy(String html) {
            throw new RuntimeException("boom");
        }
    }

    private static class StubHttpClient extends HttpClient {
        private final Map<String, String> responses;

        StubHttpClient(Map<String, String> responses) {
            this.responses = responses;
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return Optional.of(Duration.ofSeconds(5));
        }

        @Override
        public Redirect followRedirects() {
            return Redirect.NEVER;
        }

        @Override
        public Optional<ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public Optional<Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public SSLContext sslContext() {
            return null;
        }

        @Override
        public SSLParameters sslParameters() {
            return new SSLParameters();
        }

        @Override
        public Optional<Executor> executor() {
            return Optional.empty();
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_2;
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException {
            String body = responses.get(request.uri().toString());
            if (body == null) {
                throw new IOException("No stub response for " + request.uri());
            }
            return new StubHttpResponse<>(request.uri(), (T) body, 200);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            try {
                return CompletableFuture.completedFuture(send(request, responseBodyHandler));
            } catch (IOException e) {
                CompletableFuture<HttpResponse<T>> failed = new CompletableFuture<>();
                failed.completeExceptionally(e);
                return failed;
            }
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            return sendAsync(request, responseBodyHandler);
        }
    }

    private static class StubHttpResponse<T> implements HttpResponse<T> {
        private final URI uri;
        private final T body;
        private final int status;

        StubHttpResponse(URI uri, T body, int status) {
            this.uri = uri;
            this.body = body;
            this.status = status;
        }

        @Override
        public int statusCode() {
            return status;
        }

        @Override
        public HttpRequest request() {
            return HttpRequest.newBuilder(uri).build();
        }

        @Override
        public Optional<HttpResponse<T>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(Map.of(), (k, v) -> true);
        }

        @Override
        public T body() {
            return body;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return uri;
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_2;
        }
    }
}
