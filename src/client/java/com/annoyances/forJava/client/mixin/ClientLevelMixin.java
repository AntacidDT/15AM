package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void fifteenannoyances$yellowNightSky(Vec3 cameraPos, float tickDelta, CallbackInfoReturnable<Integer> cir) {
        if (!FifteenAnnoyancesClient.isHoneymoonNight()) return;
        int color = cir.getReturnValue();
        cir.setReturnValue(ARGB.lerp(0.3f, color, ARGB.color(0xE6, 0xC8, 0x78)));
    }
}
