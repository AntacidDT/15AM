package com.annoyances.forJava;

import com.annoyances.forJava.data.AnnoyanceSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Random;

public class AnnoyanceManager {
    private static final Random RANDOM = new Random();
    private static Annoyance pendingReveal = null;

    public enum Mode {
        SHARED,
        RANDOM
    }

    public static void init(ServerLevel level) {
        AnnoyanceSavedData data = AnnoyanceSavedData.get(level);

        if (data.getSharedAnnoyance() == Annoyance.UNKNOWN) {
            Annoyance chosen = Annoyance.randomExcludingUnknown();
            data.setSharedAnnoyance(chosen);
            data.setDirty();
            pendingReveal = chosen;

            FifteenAnnoyances.LOGGER.info("World annoyance selected: {}", chosen.getDisplayName());
        }
    }

    public static Annoyance get(ServerLevel level) {
        AnnoyanceSavedData data = AnnoyanceSavedData.get(level);
        if (data.getMode() == Mode.SHARED) {
            return data.getSharedAnnoyance();
        }
        return Annoyance.UNKNOWN;
    }

    public static Annoyance getForPlayer(ServerLevel level, ServerPlayer player) {
        AnnoyanceSavedData data = AnnoyanceSavedData.get(level);
        if (data.getMode() == Mode.SHARED) {
            return data.getSharedAnnoyance();
        }
        return data.getPlayerAnnoyance(player.getUUID());
    }

    public static Mode getMode(ServerLevel level) {
        return AnnoyanceSavedData.get(level).getMode();
    }

    public static void setMode(ServerLevel level, Mode mode) {
        AnnoyanceSavedData data = AnnoyanceSavedData.get(level);
        data.setMode(mode);
        if (mode == Mode.SHARED) {
            data.setSharedAnnoyance(Annoyance.randomExcludingUnknown());
            data.clearPlayerAnnoyances();
        }
        data.setDirty();
    }

    public static void setForced(ServerLevel level, Annoyance annoyance) {
        AnnoyanceSavedData data = AnnoyanceSavedData.get(level);
        data.setSharedAnnoyance(annoyance);
        data.setDirty();
        pendingReveal = null;

        MinecraftServer server = level.getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ModNetworking.sendToPlayer(player, annoyance.getId());
        }
    }

    public static void sendSyncPacket(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        AnnoyanceSavedData data = AnnoyanceSavedData.get(level);
        int annoyanceId;
        if (data.getMode() == Mode.SHARED) {
            annoyanceId = data.getSharedAnnoyance().getId();
        } else {
            annoyanceId = data.getPlayerAnnoyance(player.getUUID()).getId();
        }
        ModNetworking.sendToPlayer(player, annoyanceId);

        if (pendingReveal != null) {
            AnnoyanceReveal.playReveal(level, player, pendingReveal);
            pendingReveal = null;
        }
    }

    public static void tick(ServerLevel level) {
        AnnoyanceSavedData data = AnnoyanceSavedData.get(level);
        Annoyance active = data.getMode() == Mode.SHARED
                ? data.getSharedAnnoyance()
                : null;

        if (active != null) {
            switch (active) {
                case HELIUM_AIR -> HeliumAirAnnoyance.tick(level);
                case WIND_SURGE -> WindSurgeAnnoyance.tick(level);
                case DRUNK -> DrunkAnnoyance.tick(level);
                case HONEYMOON -> HoneymoonAnnoyance.tick(level);
                case CAFFEINATED -> CaffeinatedAnnoyance.tick(level);
                case EARTHQUAKE -> EarthquakeAnnoyance.tick(level);
                case GRAVITY_FLIP -> GravityFlipAnnoyance.tick(level);
                case TELEPORT_FRENZY -> TeleportFrenzyAnnoyance.tick(level);
                case FLOOR_IS_LAVA -> FloorIsLavaAnnoyance.tick(level);
                case MOB_RAIN -> MobRainAnnoyance.tick(level);
                default -> {}
            }
            return;
        }

        boolean quake = false;
        boolean gravityFlip = false;
        boolean teleport = false;
        boolean lava = false;
        boolean mobRain = false;
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            Annoyance p = data.getPlayerAnnoyance(player.getUUID());
            quake |= p == Annoyance.EARTHQUAKE;
            gravityFlip |= p == Annoyance.GRAVITY_FLIP;
            teleport |= p == Annoyance.TELEPORT_FRENZY;
            lava |= p == Annoyance.FLOOR_IS_LAVA;
            mobRain |= p == Annoyance.MOB_RAIN;
        }
        if (quake) EarthquakeAnnoyance.tick(level);
        if (gravityFlip) GravityFlipAnnoyance.tick(level);
        if (teleport) TeleportFrenzyAnnoyance.tick(level);
        if (lava) FloorIsLavaAnnoyance.tick(level);
        if (mobRain) MobRainAnnoyance.tick(level);
    }
}
