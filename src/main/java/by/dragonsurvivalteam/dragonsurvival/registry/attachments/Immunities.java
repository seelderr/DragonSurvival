package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.Immunity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class Immunities extends Storage<Immunity.Instance> {

    public static Immunities getData(Entity entity) {
        return entity.getData(DSDataAttachments.IMMUNITIES);
    }

    public boolean isFireImmune() {
        if(storage == null) {
            return false;
        }

        return storage.values().stream().anyMatch(Immunity.Instance::isFireImmune);
    }

    public boolean hasImmunity(DamageSource source) {
        if(storage == null) {
            return false;
        }

        return storage.values().stream().anyMatch(instance -> instance.isImmune(source));
    }

    @SubscribeEvent
    public static void checkImmunities(final EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getExistingData(DSDataAttachments.IMMUNITIES).ifPresent(storage -> {
                if (storage.hasImmunity(event.getSource())) {
                    event.setInvulnerable(true);
                }
            });
        }
    }

    @SubscribeEvent
    public static void tickImmunities(final EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.getExistingData(DSDataAttachments.IMMUNITIES).ifPresent(storage -> {
                storage.tick();

                if (storage.isEmpty()) {
                    livingEntity.removeData(DSDataAttachments.IMMUNITIES);
                }
            });
        }
    }

    @Override
    protected Tag save(HolderLookup.@NotNull Provider provider, Immunity.Instance entry) {
        return entry.save(provider);
    }

    @Override
    protected Immunity.Instance load(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        return Immunity.Instance.load(provider, tag);
    }
}
