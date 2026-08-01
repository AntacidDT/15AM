package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Annoyance annoyance = FifteenAnnoyancesClient.getActiveAnnoyance();
        Integer color = overlayColor(annoyance);
        if (color == null) return;

        Minecraft client = Minecraft.getInstance();
        int width = client.getWindow().getGuiScaledWidth();
        int height = client.getWindow().getGuiScaledHeight();
        guiGraphics.fill(0, 0, width, height, color);
    }

    private static Integer overlayColor(Annoyance annoyance) {
        return switch (annoyance) {
            case OUTROVERT -> 0x0AFF69B4;
            case WIND_SURGE -> 0x0A55FFFF;
            case CHAOS -> 0x0FFF4444;
            case INTROVERTS -> 0x0A9AD1B7;
            case EARTHQUAKE -> 0x0FFF8800;
            case GRAVITY_FLIP -> 0x0AAA00FF;
            case VISION_GLITCH -> 0x0A66CCFF;
            case TELEPORT_FRENZY -> 0x0ACC66FF;
            case FLOOR_IS_LAVA -> 0x0FFF6600;
            case BLOCK_TEMPER -> 0x0AFFD700;
            case CAFFEINATED -> 0x0AFF2222;
            case MOB_RAIN -> 0x0A880000;
            case IDENTITY_CRISIS -> 0x0ADF00FF;
            case AGGRESSIVE_MOBS -> 0x0AFF0000;
            default -> null;
        };
    }
}
