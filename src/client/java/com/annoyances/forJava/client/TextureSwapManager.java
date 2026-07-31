package com.annoyances.forJava.client;

import com.annoyances.forJava.FifteenAnnoyances;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class TextureSwapManager {
    private static Map<ResourceLocation, ResourceLocation> swapMap = new HashMap<>();
    private static long lastSwapTime = 0;
    private static final long SWAP_INTERVAL = 300_000;

    private static List<ResourceLocation> allSpriteLocations = null;

    public static void init(Set<ResourceLocation> spriteLocations) {
        allSpriteLocations = new ArrayList<>(spriteLocations);
        Collections.shuffle(allSpriteLocations);
        buildSwapMap();
        lastSwapTime = System.currentTimeMillis();
        FifteenAnnoyances.LOGGER.info("Texture swap map initialized with {} entries", swapMap.size());
    }

    public static void tick() {
        if (System.currentTimeMillis() - lastSwapTime > SWAP_INTERVAL) {
            if (allSpriteLocations != null && !allSpriteLocations.isEmpty()) {
                Collections.shuffle(allSpriteLocations);
                buildSwapMap();
                lastSwapTime = System.currentTimeMillis();
                FifteenAnnoyances.LOGGER.info("Texture swap map refreshed");
            }
        }
    }

    private static void buildSwapMap() {
        swapMap.clear();
        if (allSpriteLocations == null || allSpriteLocations.size() < 2) return;

        List<ResourceLocation> shuffled = new ArrayList<>(allSpriteLocations);
        Collections.shuffle(shuffled);

        for (int i = 0; i < allSpriteLocations.size(); i++) {
            swapMap.put(allSpriteLocations.get(i), shuffled.get(i));
        }
    }

    public static ResourceLocation getSwapped(ResourceLocation original) {
        return swapMap.getOrDefault(original, original);
    }

    public static boolean isActive() {
        return !swapMap.isEmpty();
    }
}
