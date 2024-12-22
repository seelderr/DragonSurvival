package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncAbilityLevel(ResourceKey<DragonAbility> abilityToChangeLevel, int newLevel) implements CustomPacketPayload {
    public static final Type<SyncAbilityLevel> TYPE = new CustomPacketPayload.Type<>(DragonSurvival.res("sync_ability_level"));

    public static final StreamCodec<FriendlyByteBuf, SyncAbilityLevel> STREAM_CODEC = StreamCodec.composite(
        ResourceKey.streamCodec(DragonAbility.REGISTRY), SyncAbilityLevel::abilityToChangeLevel,
        ByteBufCodecs.VAR_INT, SyncAbilityLevel::newLevel,
        SyncAbilityLevel::new
    );

    public static void handleServer(final SyncAbilityLevel packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            MagicData data = MagicData.getData(context.player());
            data.handleManualUpgrade(context.player(), packet.abilityToChangeLevel(), packet.newLevel());
        });
    }

    public static void handleClient(final SyncAbilityLevel packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            MagicData data = MagicData.getData(context.player());
            data.handleManualUpgrade(context.player(), packet.abilityToChangeLevel(), packet.newLevel());
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
