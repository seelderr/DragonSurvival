package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.DamageModification;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

// TODO :: make it generic as "Damage Modifier" so penalties can re-use it to increase the damage taken?
public class DamageModifications implements INBTSerializable<CompoundTag> {
    public static final String DAMAGE_MODIFICATIONS = "damage_modifications";

    @Nullable public Map<ResourceLocation, DamageModification.Instance> damageModifications;

    public void tick(final Entity entity) {
        if (damageModifications != null) {
            damageModifications.values().forEach(modification -> modification.tick(entity));
        }
    }

    public void add(final DamageModification.Instance modification) {
        if (damageModifications == null) {
            damageModifications = new HashMap<>();
        }

        damageModifications.put(modification.baseData().id(), modification);
    }

    public void remove(final Entity entity, final DamageModification modification) {
        if (damageModifications == null) {
            return;
        }

        damageModifications.remove(modification.id());

        if (damageModifications.isEmpty()) {
            entity.removeData(DSDataAttachments.DAMAGE_MODIFICATIONS);
        }
    }

    public @Nullable DamageModification.Instance get(final DamageModification modification) {
        if (damageModifications == null) {
            return null;
        }

        return damageModifications.get(modification.id());
    }

    public float calculate(final Holder<DamageType> damageType, float damageAmount) {
        if (damageModifications == null) {
            return damageAmount;
        }

        float newDamageAmount = damageAmount;

        for (final DamageModification.Instance modification : damageModifications.values()) {
            newDamageAmount = modification.calculate(damageType, newDamageAmount);

            if (newDamageAmount == 0) {
                break;
            }
        }

        return newDamageAmount;
    }

    public int size() {
        if (damageModifications == null) {
            return 0;
        }

        return damageModifications.size();
    }

    public Collection<DamageModification.Instance> all() {
        if (damageModifications == null) {
            return Collections.emptyList();
        }

        return damageModifications.values();
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag entries = new ListTag();

        if (damageModifications != null) {
            damageModifications.values().forEach(DamageModification.Instance::save);
            tag.put(DAMAGE_MODIFICATIONS, entries);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        Map<ResourceLocation, DamageModification.Instance> modifications = new HashMap<>();
        ListTag entries = tag.getList(DAMAGE_MODIFICATIONS, ListTag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            DamageModification.Instance modifier = DamageModification.Instance.load(entries.getCompound(i));

            if (modifier != null) {
                modifications.put(modifier.baseData().id(), modifier);
            }
        }

        if (!modifications.isEmpty()) {
            damageModifications = modifications;
        } else {
            damageModifications = null;
        }
    }

    @SubscribeEvent
    public static void tickModifications(final EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        event.getEntity().getExistingData(DSDataAttachments.DAMAGE_MODIFICATIONS).ifPresent(modifications -> modifications.tick(event.getEntity()));
    }

    @SubscribeEvent
    public static void checkImmunity(final EntityInvulnerabilityCheckEvent event) {
        if (event.isInvulnerable()) {
            return;
        }

        event.getEntity().getExistingData(DSDataAttachments.DAMAGE_MODIFICATIONS).ifPresent(modifications -> {
            // Supply a dummy damage amount (since it doesn't matter for this check but allows re-using the same method)
            if (modifications.calculate(event.getSource().typeHolder(), 1) == 0) {
                event.setInvulnerable(true);
            }
        });
    }

    @SubscribeEvent
    public static void reduceDamage(final LivingIncomingDamageEvent event) {
        event.getEntity().getExistingData(DSDataAttachments.DAMAGE_MODIFICATIONS).ifPresent(modifications -> {
            event.setAmount(modifications.calculate(event.getSource().typeHolder(), event.getAmount()));
        });
    }
}
