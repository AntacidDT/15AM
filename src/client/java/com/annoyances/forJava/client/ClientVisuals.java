package com.annoyances.forJava.client;

import com.annoyances.forJava.Annoyance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ClientVisuals {
    private static final Random RANDOM = new Random();

    private static final int[] CHAOS_COLORS = {
            0xFF4444, 0x44FF44, 0x4444FF, 0xFFFF44,
            0xFF44FF, 0x44FFFF, 0xFF8844, 0xFF44AA
    };

    public static void tickAmbient(Minecraft client) {
        ClientLevel level = client.level;
        LocalPlayer player = client.player;
        if (level == null || player == null) return;

        Annoyance annoyance = FifteenAnnoyancesClient.getActiveAnnoyance();
        if (annoyance == Annoyance.UNKNOWN) return;

        Vec3 pos = player.position();
        boolean night = (level.getDayTime() % 24000) >= 13000 && (level.getDayTime() % 24000) < 23000;

        switch (annoyance) {
            case OUTROVERT -> {}
            case WIND_SURGE -> spawn(level, pos, ParticleTypes.CLOUD, 0.06);
            case CHAOS -> spawnDust(level, pos);
            case EARTHQUAKE -> spawn(level, pos, ParticleTypes.SMOKE, 0.04);
            case GRAVITY_FLIP -> spawn(level, pos, ParticleTypes.DRAGON_BREATH, 0.05);
            case VISION_GLITCH -> spawn(level, pos, ParticleTypes.CRIT, 0.05);
            case TELEPORT_FRENZY -> spawn(level, pos, ParticleTypes.PORTAL, 0.05);
            case FLOOR_IS_LAVA -> spawn(level, pos, ParticleTypes.FLAME, 0.04);
            case BLOCK_TEMPER -> {}
            case CAFFEINATED -> spawn(level, pos, ParticleTypes.ENCHANT, 0.04);
            case MOB_RAIN -> spawn(level, pos, ParticleTypes.RAIN, 0.05);
            case INTROVERTS -> spawn(level, pos, ParticleTypes.SPIT, 0.04);
            case IDENTITY_CRISIS -> spawn(level, pos, ParticleTypes.SOUL, 0.04);
            case AGGRESSIVE_MOBS -> spawn(level, pos, ParticleTypes.LAVA, 0.03);
            default -> {}
        }
    }

    private static void spawn(ClientLevel level, Vec3 pos, ParticleOptions particle, double speed) {
        if (RANDOM.nextDouble() < 0.6) return;
        double x = pos.x + (RANDOM.nextDouble() * 2 - 1) * 1.6;
        double y = pos.y + RANDOM.nextDouble() * 2.2;
        double z = pos.z + (RANDOM.nextDouble() * 2 - 1) * 1.6;
        level.addParticle(particle, x, y, z,
                (RANDOM.nextDouble() - 0.5) * speed * 2,
                RANDOM.nextDouble() * speed,
                (RANDOM.nextDouble() - 0.5) * speed * 2);
    }

    private static void spawnDust(ClientLevel level, Vec3 pos) {
        if (RANDOM.nextDouble() < 0.5) return;
        int color = CHAOS_COLORS[RANDOM.nextInt(CHAOS_COLORS.length)];
        double x = pos.x + (RANDOM.nextDouble() * 2 - 1) * 2.4;
        double y = pos.y + RANDOM.nextDouble() * 2.6;
        double z = pos.z + (RANDOM.nextDouble() * 2 - 1) * 2.4;
        level.addParticle(new DustParticleOptions(color, 0.8f), x, y, z, 0, 0.02, 0);
    }
}
