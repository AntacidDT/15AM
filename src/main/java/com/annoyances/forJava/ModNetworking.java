package com.annoyances.forJava;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ModNetworking {
    public static final ResourceLocation SYNC_ANNOUNCEMENT_ID =
            ResourceLocation.fromNamespaceAndPath(FifteenAnnoyances.MOD_ID, "sync_annoyance");

    public static final CustomPacketPayload.Type<SyncAnnoyancePayload> TYPE =
            new CustomPacketPayload.Type<>(SYNC_ANNOUNCEMENT_ID);

    public static final StreamCodec<FriendlyByteBuf, SyncAnnoyancePayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    SyncAnnoyancePayload::annoyanceId,
                    SyncAnnoyancePayload::new
            );

    public static void register() {
        PayloadTypeRegistry.playS2C().register(TYPE, CODEC);
        PayloadTypeRegistry.playC2S().register(TYPE, CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
        });
    }

    public static void sendToPlayer(ServerPlayer player, int annoyanceId) {
        ServerPlayNetworking.send(player, new SyncAnnoyancePayload(annoyanceId));
    }

    public record SyncAnnoyancePayload(int annoyanceId) implements CustomPacketPayload {
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
