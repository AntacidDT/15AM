package com.annoyances.forJava;

import net.minecraft.network.chat.Component;

import java.util.Random;

public enum Annoyance {
    HELIUM_AIR("Helium Air", "Air is now helium. Hope you like floating."),
    WIND_SURGE("Wind Surge", "Tornado season, every day."),
    SWAPPED_TEXTURES("Swapped Textures", "Nothing looks right anymore."),
    MULTIFALL("Multifall", "Every fall bounces you right back up."),
    UPSIDE_DOWN("Upside Down", "Everything is flipped. Or is it?"),
    EARTHQUAKE("Earthquake", "The ground has a mind of its own."),
    GRAVITY_FLIP("Gravity Flip", "Down is up. Up is down. Good luck."),
    SOUNDSQM("Sounds??", "What even is audio."),
    TELEPORT_FRENZY("Teleport Frenzy", "You never quite stay where you are."),
    FLOOR_IS_LAVA("Floor Is Lava", "Standing still is a fire hazard."),
    DRUNK("Drunk", "Constant nausea and screen wobble."),
    HONEYMOON("Honeymoon", "Nights are sticky."),
    CAFFEINATED("Caffeinated", "Sleep is impossible. Coffee is life."),
    MOB_RAIN("Mob Rain", "The sky is falling. Literally."),
    AGGRESSIVE_MOBS("Those Dang Mobs", "All mobs are completely aggressive."),
    UNKNOWN("???", "You don't know what was chosen...");

    private final String displayName;
    private final String description;

    private static final Random RANDOM = new Random();

    Annoyance(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Component getDisplayComponent() {
        return Component.literal(displayName);
    }

    public static Annoyance random() {
        return values()[RANDOM.nextInt(values().length)];
    }

    public static Annoyance randomExcludingUnknown() {
        Annoyance a;
        do {
            a = values()[RANDOM.nextInt(values().length)];
        } while (a == UNKNOWN);
        return a;
    }

    public static Annoyance fromId(int id) {
        if (id >= 0 && id < values().length) {
            return values()[id];
        }
        return UNKNOWN;
    }

    public int getId() {
        return ordinal();
    }
}
