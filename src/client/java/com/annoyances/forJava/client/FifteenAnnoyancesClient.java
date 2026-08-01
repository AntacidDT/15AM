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
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

public class FifteenAnnoyancesClient implements ClientModInitializer {
    private static com.annoyances.forJava.Annoyance activeAnnoyance = com.annoyances.forJava.Annoyance.UNKNOWN;

    private static boolean drunkVisualsActive = false;
    private static int previousRenderDistance = 0;

    private static boolean visionVisualsActive = false;

    private enum VisionMode {
        GLITCH("vision_glitch", true),
        INVERT("vision_invert", true),
        MIRROR("vision_mirror", false),
        UPSIDE("vision_upside", false),
        BEHIND("vision_behind", false);

        final String chainName;
        final boolean needsTime;

        VisionMode(String chainName, boolean needsTime) {
            this.chainName = chainName;
            this.needsTime = needsTime;
        }
    }

    private static VisionMode currentVisionMode = null;

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

            ClientVisuals.tickAmbient(client);

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

            boolean vision = annoyance == Annoyance.VISION_GLITCH;
            if (vision != visionVisualsActive) {
                visionVisualsActive = vision;
                if (vision) {
                    currentVisionMode = VisionMode.GLITCH;
                    applyPostEffect(client, visionChain(currentVisionMode));
                } else {
                    currentVisionMode = null;
                    client.gameRenderer.clearPostEffect();
                }
            }

            if (vision && client.level != null) {
                long ticks = client.level.getGameTime();
                VisionMode[] modes = VisionMode.values();
                VisionMode mode = modes[(int) ((ticks / 120) % modes.length)];
                if (mode != currentVisionMode) {
                    currentVisionMode = mode;
                    applyPostEffect(client, visionChain(mode));
                }
                if (mode.needsTime) {
                    PostChain chain = client.getShaderManager().getPostChain(visionChain(mode), LevelTargetBundle.MAIN_TARGETS);
                    if (chain != null) {
                        chain.setUniform("Time", (float) ticks);
                    }
                }
            }
        });

        FifteenAnnoyances.LOGGER.info("15 Annoyances client loaded!");
    }

    private static ResourceLocation visionChain(VisionMode mode) {
        return ResourceLocation.fromNamespaceAndPath("fifteenannoyances", mode.chainName);
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
}
