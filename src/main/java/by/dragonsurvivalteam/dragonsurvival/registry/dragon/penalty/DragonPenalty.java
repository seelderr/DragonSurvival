package by.dragonsurvivalteam.dragonsurvival.registry.dragon.penalty;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record DragonPenalty(
        Component name,
        EntityPredicate condition,
        PenaltyEffect effect,
        PenaltyTrigger trigger
) {
    public static final ResourceKey<Registry<DragonPenalty>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_penalties"));

    public static final Codec<DragonPenalty> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(DragonPenalty::name),
            EntityPredicate.CODEC.fieldOf("conditions").forGetter(DragonPenalty::condition),
            PenaltyEffect.CODEC.fieldOf("effect").forGetter(DragonPenalty::effect),
            PenaltyTrigger.CODEC.fieldOf("trigger").forGetter(DragonPenalty::trigger)
    ).apply(instance, instance.stable(DragonPenalty::new)));

    public static final Codec<Holder<DragonPenalty>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DragonPenalty>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }

    public void apply(final ServerPlayer dragon, final PenaltyInstance instance) {
        if (trigger.matches(dragon, instance, condition.matches(dragon.serverLevel(), dragon.position(), dragon))) {
            effect.apply(dragon);
        }
    }
}