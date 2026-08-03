package com.oxipro.cmu.configlang.common.langProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class IpLanguageDetector implements ILanguageDetector<String> {

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    private final HttpClient client;
    private final Executor executor;
    private final Map<String, Locale> cache = new ConcurrentHashMap<>();
    private Map<String, String> countryToLanguage = defaultCountryToLanguage();

    public IpLanguageDetector() {
        this(Executors.newCachedThreadPool());
    }

    public IpLanguageDetector(Executor executor) {
        this.executor = executor;
        this.client = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    public void setCountryToLanguageMap(Map<String, String> countryToLanguage) {
        this.countryToLanguage = countryToLanguage;
    }

    public void invalidate(String ip) {
        cache.remove(ip);
    }

    @Override
    public Locale detect(String ip) {
        Locale cached = cache.get(ip);
        if (cached != null) return cached;
        Locale resolved = resolve(ip);
        cache.put(ip, resolved);
        return resolved;
    }

    public CompletableFuture<Locale> detectAsync(String ip) {
        Locale cached = cache.get(ip);
        if (cached != null) return CompletableFuture.completedFuture(cached);
        return CompletableFuture.supplyAsync(() -> detect(ip), executor);
    }

    private Locale resolve(String ip) {
        if (ip == null || ip.isEmpty() || isPrivate(ip)) return DEFAULT_LOCALE;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json/" + ip + "?fields=status,countryCode"))
                    .timeout(TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            if (!json.has("status") || !"success".equals(json.get("status").getAsString())) {
                return DEFAULT_LOCALE;
            }

            String countryCode = json.get("countryCode").getAsString();
            String language = countryToLanguage.getOrDefault(countryCode, "en");
            return new Locale(language, countryCode);
        } catch (Exception e) {
            return DEFAULT_LOCALE;
        }
    }

    private boolean isPrivate(String ip) {
        return ip.equals("127.0.0.1") || ip.equals("::1") || ip.equals("0:0:0:0:0:0:0:1")
                || ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("172.16.")
                || ip.startsWith("172.17.") || ip.startsWith("172.18.") || ip.startsWith("172.19.")
                || ip.startsWith("172.2") || ip.startsWith("172.30.") || ip.startsWith("172.31.");
    }

    private static Map<String, String> defaultCountryToLanguage() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("FR", "fr");
        map.put("BE", "fr");
        map.put("LU", "fr");
        map.put("CH", "fr");
        map.put("CA", "fr");
        map.put("DE", "de");
        map.put("AT", "de");
        map.put("ES", "es");
        map.put("MX", "es");
        map.put("AR", "es");
        map.put("IT", "it");
        map.put("PT", "pt");
        map.put("BR", "pt");
        map.put("NL", "nl");
        return map;
    }
}
