package com.annoyances.forJava.client;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.FifteenAnnoyances;
import com.annoyances.forJava.ModNetworking;
import com.annoyances.forJava.client.mixin.GameRendererAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;

public class FifteenAnnoyancesClient implements ClientModInitializer {
    private static com.annoyances.forJava.Annoyance activeAnnoyance = com.annoyances.forJava.Annoyance.UNKNOWN;

    private static boolean drunkVisualsActive = false;
    private static int previousRenderDistance = 0;

    private static final String[] GLITCH_SHADERS = {"invert", "bits", "spider", "box_blur", "color_convolve"};
    private static int glitchTicksLeft = 0;
    private static int glitchCooldown = 100;
    private static final Random RANDOM = new Random();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.TYPE, (payload, context) -> {
            int id = payload.annoyanceId();
            Minecraft.getInstance().execute(() -> {
                activeAnnoyance = com.annoyances.forJava.Annoyance.fromId(id);
                FifteenAnnoyances.LOGGER.info("Client received annoyance: {}", activeAnnoyance.getDisplayName());
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Annoyance annoyance = getActiveAnnoyance();

            boolean drunk = annoyance == Annoyance.DRUNK;
            if (drunk != drunkVisualsActive) {
                drunkVisualsActive = drunk;
                if (drunk) {
                    previousRenderDistance = client.options.renderDistance().get();
                    client.options.renderDistance().set(2);
                    applyPostEffect(client, ResourceLocation.fromNamespaceAndPath("fifteenannoyances", "depth_blur"));
                } else {
                    client.options.renderDistance().set(previousRenderDistance);
                    client.gameRenderer.clearPostEffect();
                }
            }

            if (annoyance == Annoyance.SOUNDSQM) {
                if (glitchTicksLeft > 0) {
                    glitchTicksLeft--;
                    if (glitchTicksLeft == 0) {
                        client.gameRenderer.clearPostEffect();
                    }
                } else {
                    glitchCooldown--;
                    if (glitchCooldown <= 0) {
                        glitchCooldown = 120 + RANDOM.nextInt(160);
                        glitchTicksLeft = 30 + RANDOM.nextInt(25);
                        applyPostEffect(client, GLITCH_SHADERS[RANDOM.nextInt(GLITCH_SHADERS.length)]);
                    }
                }
            } else if (glitchTicksLeft > 0) {
                glitchTicksLeft = 0;
                glitchCooldown = 100;
                client.gameRenderer.clearPostEffect();
            }
        });

        FifteenAnnoyances.LOGGER.info("15 Annoyances client loaded!");
    }

    private static void applyPostEffect(Minecraft client, String name) {
        applyPostEffect(client, ResourceLocation.withDefaultNamespace(name));
    }

    private static void applyPostEffect(Minecraft client, ResourceLocation id) {
        GameRenderer gameRenderer = client.gameRenderer;
        GameRendererAccessor accessor = (GameRendererAccessor) gameRenderer;
        accessor.fifteenannoyances$setPostEffectId(id);
        accessor.fifteenannoyances$setEffectActive(true);
    }

    public static com.annoyances.forJava.Annoyance getActiveAnnoyance() {
        return activeAnnoyance;
    }

    public static boolean isHoneymoonNight() {
        if (activeAnnoyance != com.annoyances.forJava.Annoyance.HONEYMOON) return false;
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return false;
        long time = client.level.getDayTime() % 24000;
        return time >= 13000 && time < 23000;
    }
}
