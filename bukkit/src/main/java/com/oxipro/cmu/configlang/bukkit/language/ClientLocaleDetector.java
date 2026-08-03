package com.oxipro.cmu.configlang.bukkit.language;

import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Détecte la locale client d'un joueur Bukkit/Spigot/Paper.
 * <p>
 * Aucun appel direct à une méthode dépréciée/supprimée : tout passe par réflexion,
 * résolue une seule fois au premier appel puis mise en cache. Priorité à
 * {@code Player#getLocale()} (stable depuis 1.12+), fallback vers
 * {@code Player$Spigot#getLocale()} pour du pur 1.8-1.11 le cas échéant.
 */
public class ClientLocaleDetector implements ILanguageDetector<Player> {

    private static final Logger LOGGER = Logger.getLogger(ClientLocaleDetector.class.getName());
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private enum Strategy { MODERN, LEGACY_SPIGOT, NONE }

    private static volatile Strategy resolvedStrategy;
    private static volatile Method modernMethod;
    private static volatile Method spigotAccessor;
    private static volatile Method legacyLocaleMethod;

    @Override
    public Locale detect(Player player) {
        if (player == null) {
            return DEFAULT_LOCALE;
        }
        return parseLocale(safeGetLocale(player));
    }

    private String safeGetLocale(Player player) {
        ensureResolved(player);

        try {
            switch (resolvedStrategy) {
                case MODERN:
                    Object result = modernMethod.invoke(player);
                    return result != null ? result.toString() : null;
                case LEGACY_SPIGOT:
                    Object spigot = spigotAccessor.invoke(player);
                    Object legacyResult = legacyLocaleMethod.invoke(spigot);
                    return legacyResult != null ? legacyResult.toString() : null;
                default:
                    return null;
            }
        } catch (Throwable t) {
            LOGGER.log(Level.WARNING, "Echec de récupération de la locale pour " + player.getName(), t);
            return null;
        }
    }

    private void ensureResolved(Player player) {
        if (resolvedStrategy != null) {
            return;
        }
        synchronized (ClientLocaleDetector.class) {
            if (resolvedStrategy != null) {
                return;
            }
            resolvedStrategy = resolveStrategy(player);
        }
    }

    private Strategy resolveStrategy(Player player) {
        // 1) API moderne, présente depuis 1.12 et toujours d'actualité en 1.21+
        try {
            modernMethod = Player.class.getMethod("getLocale");
            LOGGER.log(Level.FINE, "ClientLocaleDetector: stratégie MODERN sélectionnée");
            return Strategy.MODERN;
        } catch (NoSuchMethodException ignored) {

        }

        try {
            spigotAccessor = Player.class.getMethod("spigot");
            Object spigotInstance = spigotAccessor.invoke(player);
            legacyLocaleMethod = spigotInstance.getClass().getMethod("getLocale");
            LOGGER.log(Level.FINE, "ClientLocaleDetector: stratégie LEGACY_SPIGOT sélectionnée");
            return Strategy.LEGACY_SPIGOT;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Aucune méthode de récupération de locale trouvée sur cette version", e);
            return Strategy.NONE;
        }
    }

    private Locale parseLocale(String raw) {
        if (raw == null || raw.isEmpty()) {
            return DEFAULT_LOCALE;
        }

        String[] parts = raw.split("_");
        String language = parts[0];
        if (language.isEmpty()) {
            return DEFAULT_LOCALE;
        }

        if (parts.length >= 2 && !parts[1].isEmpty()) {
            return new Locale(language, parts[1]);
        }
        return new Locale(language);
    }
}