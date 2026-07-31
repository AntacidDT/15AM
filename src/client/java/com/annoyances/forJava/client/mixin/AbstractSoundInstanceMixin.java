package com.annoyances.forJava.client.mixin;

import com.annoyances.forJava.client.RandomPitchAccessor;
import com.annoyances.forJava.client.SoundLocationAccessor;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin implements RandomPitchAccessor, SoundLocationAccessor {

    @Shadow
    protected float pitch;

    @Shadow
    protected ResourceLocation location;

    @Override
    public void fifteenannoyances$setRandomPitch() {
        this.pitch = 0.5f + (float) (Math.random() * 2.0f);
    }

    @Override
    public void fifteenannoyances$setLocation(ResourceLocation location) {
        this.location = location;
    }
}
