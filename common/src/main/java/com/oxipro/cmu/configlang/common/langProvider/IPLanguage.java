package com.oxipro.cmu.configlang.common.langProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;

public class IPLanguage {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    private Map<String, Locale> defaultCountryToLocale = Map.ofEntries(
            Map.entry("FR", Locale.FRENCH),
            Map.entry("BE", Locale.FRENCH),
            Map.entry("CA", Locale.FRENCH),
            Map.entry("NL", new Locale("nl")),
            Map.entry("DE", Locale.GERMAN),
            Map.entry("ES", new Locale("es")),
            Map.entry("IT", new Locale("it")),
            Map.entry("PT", new Locale("pt")),
            Map.entry("BR", new Locale("pt", "BR")),
            Map.entry("US", Locale.ENGLISH),
            Map.entry("GB", Locale.ENGLISH),
            Map.entry("AU", Locale.ENGLISH)
    );

    public void setCustomCountryToLocaleMap(Map<String, Locale> ctl) {
        this.defaultCountryToLocale = ctl;
    }

    public String getCountryCode(String ip) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json/" + ip + "?fields=status,countryCode"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = gson.fromJson(response.body(), JsonObject.class);

            if (!json.has("status") || !"success".equals(json.get("status").getAsString())) {
                return null;
            }

            return json.get("countryCode").getAsString();

        } catch (Exception e) {
            return null;
        }
    }

    public String getLanguageCode(String countryCode) {
        if (countryCode == null) return "en";

        switch (countryCode) {
            case "FR":
            case "BE":
            case "LU":
            case "CH":
            case "CA":
                return "fr";

            case "DE":
            case "AT":
                return "de";

            case "ES":
            case "MX":
            case "AR":
                return "es";

            case "IT":
                return "it";

            case "PT":
            case "BR":
                return "pt";

            case "NL":
                return "nl";

            default:
                return "en";
        }
    }

    public Locale getLocaleFromIp(String ip) {
        try {
            String country = getCountryCode(ip);
            String lang = getLanguageCode(country);
            if (lang == null || country == null) {
                return new Locale("en", "EN");
            }
            return new Locale(lang, country);
        } catch (Exception e) {
            return new Locale("en", "EN");
        }
    }

}
