package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void onSetupFog(Camera camera, FogRenderer.FogMode fogShape,
                                    Vector4f fogColor, float viewDistance, boolean thickFog, float tickDelta,
                                    CallbackInfoReturnable<net.minecraft.client.renderer.FogParameters> cir) {
    }

    @Inject(method = "computeFogColor", at = @At("RETURN"), cancellable = true)
    private static void fifteenannoyances$yellowNightFog(Camera camera, float tickDelta,
                                    net.minecraft.client.multiplayer.ClientLevel level, int renderDistance,
                                    float darken, CallbackInfoReturnable<Vector4f> cir) {
        if (!FifteenAnnoyancesClient.isHoneymoonNight()) return;
        Vector4f c = cir.getReturnValue();
        float r = c.x + (0.85f - c.x) * 0.3f;
        float g = c.y + (0.72f - c.y) * 0.3f;
        float b = c.z + (0.35f - c.z) * 0.3f;
        cir.setReturnValue(new Vector4f(r, g, b, c.w));
    }
}
