package com.annoyances.forJava;

import net.minecraft.network.chat.Component;

import java.util.Random;

public enum Annoyance {
    OUTROVERT("Outrovert", "they like you i guess"),
    WIND_SURGE("Wind Surge", "That was not the wind."),
    CHAOS("Chaos", "BEHOLD, THE CHAOS!!"),
    INTROVERTS("Introverts", "social anxiety simulator"),
    EARTHQUAKE("Earthquake", "the ground has a stutter"),
    GRAVITY_FLIP("Gravity Flip", "toasts land on butter side now."),
    VISION_GLITCH("Vision Glitch", "you might have schizophrenia."),
    TELEPORT_FRENZY("Teleport Frenzy", "you teleport, i guess."),
    FLOOR_IS_LAVA("Floor Is Lava", "do not afk."),
    DRUNK("Drunk", "are drunk you lot a"),
    BLOCK_TEMPER("Block Temper", "your blocks have a temper."),
    CAFFEINATED("Caffeinated", "you're too addicted to coffee."),
    MOB_RAIN("Mob Rain", "someone seems to rain from above"),
    IDENTITY_CRISIS("Identity Crisis", "js choose an identity bro"),
    AGGRESSIVE_MOBS("Those Dang Mobs", "they got a BAD temper."),
    UNKNOWN("???", "Unknown annoyance awaits.");

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
