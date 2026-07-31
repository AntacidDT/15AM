package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.Annoyance;
import com.annoyances.forJava.client.FifteenAnnoyancesClient;
import com.annoyances.forJava.client.SoundLocationAccessor;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    private static List<SoundEvent> fifteenannoyances$allSounds = null;

    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At("HEAD"))
    private void onPlay(SoundInstance soundInstance, CallbackInfo ci) {
        Annoyance annoyance = FifteenAnnoyancesClient.getActiveAnnoyance();
        if (annoyance != Annoyance.SOUNDSQM) return;
        if (!(soundInstance instanceof SoundLocationAccessor sla)) return;

        if (fifteenannoyances$allSounds == null) {
            fifteenannoyances$allSounds = BuiltInRegistries.SOUND_EVENT.stream().toList();
        }
        if (fifteenannoyances$allSounds.isEmpty()) return;

        SoundEvent randomSound = fifteenannoyances$allSounds.get(
                ThreadLocalRandom.current().nextInt(fifteenannoyances$allSounds.size()));
        sla.fifteenannoyances$setLocation(randomSound.location());
    }
}
