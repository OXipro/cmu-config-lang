package com.oxipro.cmu.configlang.bukkit.language;

import com.oxipro.cmu.configlang.api.language.detection.ILanguageDetector;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Client locale via Player#getLocale (reflection for old Spigot). */
public class ClientLocaleDetector implements ILanguageDetector<Player> {

    private static final Logger LOGGER = Logger.getLogger(ClientLocaleDetector.class.getName());

    private enum Strategy { MODERN, LEGACY_SPIGOT, NONE }

    private static volatile Strategy resolvedStrategy;
    private static volatile Method modernMethod;
    private static volatile Method spigotAccessor;
    private static volatile Method legacyLocaleMethod;

    @Override
    public Locale detect(Player player) {
        if (player == null) return null;
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
            LOGGER.log(Level.WARNING, "Failed to read locale for " + player.getName(), t);
            return null;
        }
    }

    private void ensureResolved(Player player) {
        if (resolvedStrategy != null) return;
        synchronized (ClientLocaleDetector.class) {
            if (resolvedStrategy != null) return;
            resolvedStrategy = resolveStrategy(player);
        }
    }

    private Strategy resolveStrategy(Player player) {
        try {
            modernMethod = Player.class.getMethod("getLocale");
            return Strategy.MODERN;
        } catch (NoSuchMethodException ignored) {
        }

        try {
            spigotAccessor = Player.class.getMethod("spigot");
            Object spigotInstance = spigotAccessor.invoke(player);
            legacyLocaleMethod = spigotInstance.getClass().getMethod("getLocale");
            return Strategy.LEGACY_SPIGOT;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No locale method on this server version", e);
            return Strategy.NONE;
        }
    }

    private Locale parseLocale(String raw) {
        if (raw == null || raw.isEmpty()) return null;
        String[] parts = raw.split("_");
        if (parts[0].isEmpty()) return null;
        if (parts.length >= 2 && !parts[1].isEmpty()) {
            return new Locale(parts[0], parts[1]);
        }
        return new Locale(parts[0]);
    }
}
