package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Camera.class)
public abstract class CameraMixin {

    private static final Random RANDOM = new Random();

    @Inject(method = "setup", at = @At("RETURN"))
    private void fifteenannoyances$earthquakeShake(BlockGetter level, Entity entity,
                                                   boolean detached, boolean thirdPersonReverse,
                                                   float partialTick, CallbackInfo ci) {
        if (FifteenAnnoyancesClient.getActiveAnnoyance() != Annoyance.EARTHQUAKE) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        Vec3 vel = client.player.getDeltaMovement();
        double speedSq = vel.horizontalDistanceSqr() + vel.y * vel.y;
        if (speedSq < 0.6) return;

        float intensity = (float) Math.min(0.12, (speedSq - 0.6) * 0.03);
        float rx = (RANDOM.nextFloat() * 2 - 1) * intensity;
        float ry = (RANDOM.nextFloat() * 2 - 1) * intensity;

        Camera self = (Camera) (Object) this;
        self.rotation().mul(new Quaternionf().rotateX(rx).rotateY(ry));
    }
}
