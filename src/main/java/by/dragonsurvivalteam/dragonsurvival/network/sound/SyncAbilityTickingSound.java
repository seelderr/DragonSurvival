package by.dragonsurvivalteam.dragonsurvival.network.sound;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.Activation;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncAbilityTickingSound(int playerId, int abilitySlot, SoundType soundType, boolean stop) implements CustomPacketPayload {
    public static final Type<SyncAbilityTickingSound> TYPE = new CustomPacketPayload.Type<>(DragonSurvival.res("sync_ability_ticking_sound"));

    public static final StreamCodec<FriendlyByteBuf, SyncAbilityTickingSound> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncAbilityTickingSound::playerId,
            ByteBufCodecs.VAR_INT, SyncAbilityTickingSound::abilitySlot,
            NeoForgeStreamCodecs.enumCodec(SoundType.class), SyncAbilityTickingSound::soundType,
            ByteBufCodecs.BOOL, SyncAbilityTickingSound::stop,
            SyncAbilityTickingSound::new
    );

    public enum SoundType {CHARGING, LOOPING}

    public static void handleClient(final SyncAbilityTickingSound packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(packet.playerId());

            if (!(entity instanceof Player player)) {
                return;
            }

            MagicData magic = MagicData.getData(player);
            DragonAbilityInstance instance = magic.getAbilityFromSlot(packet.abilitySlot());

            if (instance == null) {
                return;
            }

            if (packet.stop()) {
                instance.stopSound();
            } else {
                SoundEvent sound = getSoundEventFromAbilityInstance(packet, instance);

                if (sound != null) {
                    instance.queueTickingSound(sound, SoundSource.PLAYERS, player);
                }
            }
        });
    }

    private static SoundEvent getSoundEventFromAbilityInstance(final SyncAbilityTickingSound packet, final DragonAbilityInstance instance) {
        return switch (packet.soundType()) {
            case CHARGING: yield instance.value().activation().sound().flatMap(Activation.Sound::charging).orElse(null);
            case LOOPING: yield instance.value().activation().sound().flatMap(Activation.Sound::looping).orElse(null);
        };
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
