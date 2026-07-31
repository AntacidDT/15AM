package com.annoyances.forJava.client;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.*;

public class SoundSwapManager {
    private static final Random RANDOM = new Random();
    private static Map<SoundEvent, SoundEvent> swapMap = new HashMap<>();
    private static List<SoundEvent> allSounds = null;
    private static boolean initialized = false;

    private static void ensureInit() {
        if (initialized) return;
        allSounds = List.of(
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundEvents.TNT_PRIMED,
                SoundEvents.ZOMBIE_AMBIENT,
                SoundEvents.ZOMBIE_HURT,
                SoundEvents.ZOMBIE_DEATH,
                SoundEvents.ZOMBIFIED_PIGLIN_ANGRY,
                SoundEvents.ZOMBIFIED_PIGLIN_HURT,
                SoundEvents.ZOMBIFIED_PIGLIN_DEATH,
                SoundEvents.SKELETON_AMBIENT,
                SoundEvents.SKELETON_HURT,
                SoundEvents.SKELETON_DEATH,
                SoundEvents.SPIDER_AMBIENT,
                SoundEvents.SPIDER_HURT,
                SoundEvents.SPIDER_DEATH,
                SoundEvents.CREEPER_PRIMED,
                SoundEvents.CREEPER_DEATH,
                SoundEvents.CREEPER_HURT,
                SoundEvents.CHICKEN_AMBIENT,
                SoundEvents.CHICKEN_HURT,
                SoundEvents.CHICKEN_EGG,
                SoundEvents.CHICKEN_DEATH,
                SoundEvents.COW_AMBIENT,
                SoundEvents.COW_HURT,
                SoundEvents.COW_DEATH,
                SoundEvents.PIG_AMBIENT,
                SoundEvents.PIG_HURT,
                SoundEvents.PIG_DEATH,
                SoundEvents.SHEEP_AMBIENT,
                SoundEvents.SHEEP_HURT,
                SoundEvents.WOLF_AMBIENT,
                SoundEvents.WOLF_HURT,
                SoundEvents.WOLF_DEATH,
                SoundEvents.WOLF_GROWL,
                SoundEvents.WOLF_SHAKE,
                SoundEvents.BAT_AMBIENT,
                SoundEvents.BAT_HURT,
                SoundEvents.BAT_DEATH,
                SoundEvents.BAT_LOOP,
                SoundEvents.BAT_TAKEOFF,
                SoundEvents.CAT_AMBIENT,
                SoundEvents.CAT_HURT,
                SoundEvents.CAT_DEATH,
                SoundEvents.CAT_PURR,
                SoundEvents.CAT_PURREOW,
                SoundEvents.HORSE_AMBIENT,
                SoundEvents.HORSE_DEATH,
                SoundEvents.HORSE_GALLOP,
                SoundEvents.HORSE_HURT,
                SoundEvents.HORSE_JUMP,
                SoundEvents.HORSE_STEP,
                SoundEvents.HORSE_STEP_WOOD,
                SoundEvents.VILLAGER_AMBIENT,
                SoundEvents.VILLAGER_DEATH,
                SoundEvents.VILLAGER_HURT,
                SoundEvents.VILLAGER_NO,
                SoundEvents.VILLAGER_YES,
                SoundEvents.PLAYER_BREATH,
                SoundEvents.PLAYER_BURP,
                SoundEvents.PLAYER_DEATH,
                SoundEvents.PLAYER_HURT,
                SoundEvents.PLAYER_LEVELUP,
                SoundEvents.PLAYER_SWIM,
                SoundEvents.SLIME_SQUISH,
                SoundEvents.MAGMA_CUBE_SQUISH
        );

        swapMap.clear();
        List<SoundEvent> shuffled = new ArrayList<>(allSounds);
        Collections.shuffle(shuffled, RANDOM);

        for (int i = 0; i < allSounds.size(); i++) {
            swapMap.put(allSounds.get(i), shuffled.get(i));
        }
        initialized = true;
    }

    public static SoundEvent getSwapped(SoundEvent original) {
        ensureInit();
        return swapMap.getOrDefault(original, original);
    }

    public static void reset() {
        swapMap.clear();
        initialized = false;
        allSounds = null;
    }
}
