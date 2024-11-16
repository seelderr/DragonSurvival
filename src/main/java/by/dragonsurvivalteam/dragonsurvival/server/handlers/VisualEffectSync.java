package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncVisualEffectAdded;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncVisualEffectRemoved;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/** To update effects which affect clients (through visuals) on non-player entities (they get synchronized correctly) (vanilla handles glowing and invisibility effects with data accessors) */
@EventBusSubscriber // FIXME :: could technically be done with data attachments (as our own data accessors)?
public class VisualEffectSync {
    private static final List<Holder<MobEffect>> VISUAL_EFFECTS = List.of(
            DSEffects.DRAIN,
            DSEffects.CHARGED,
            DSEffects.BURN,
            DSEffects.BLOOD_SIPHON,
            DSEffects.REGENERATION_DELAY,
            DSEffects.TRAPPED
    );

    @SubscribeEvent
    public static void handleEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        LivingEntity entity = event.getEntity();

        // Client-side entry is likely our own packet
        if (entity.level().isClientSide() || instance == null || entity instanceof ServerPlayer) {
            return;
        }

        if (!VISUAL_EFFECTS.contains(instance.getEffect())) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntity(entity, new SyncVisualEffectAdded.Data(entity.getId(), BuiltInRegistries.MOB_EFFECT.getId(instance.getEffect().value()), instance.getDuration(), instance.getAmplifier()));
    }

    @SubscribeEvent
    public static void handleEffectRemoved(MobEffectEvent.Remove event) {
        handleEffectRemoval(event.getEffectInstance(), event.getEntity());
    }

    @SubscribeEvent
    public static void handleEffectExpired(MobEffectEvent.Expired event) {
        handleEffectRemoval(event.getEffectInstance(), event.getEntity());
    }

    private static void handleEffectRemoval(MobEffectInstance instance, LivingEntity entity) {
        // Client-side entry is likely our own packet
        if (entity.level().isClientSide() || instance == null || entity instanceof ServerPlayer) {
            return;
        }

        if (!VISUAL_EFFECTS.contains(instance.getEffect())) {
            return;
        }

        PacketDistributor.sendToPlayersTrackingEntity(entity, new SyncVisualEffectRemoved.Data(entity.getId(), BuiltInRegistries.MOB_EFFECT.getId(instance.getEffect().value())));
    }
}