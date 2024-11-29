package by.dragonsurvivalteam.dragonsurvival.registry.attachments;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.DamageReduction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

// TODO :: make it generic as "Damage Modifier" so penalties can re-use it to increase the damage taken?
public class DamageReductions implements INBTSerializable<CompoundTag> {
    public static final String DAMAGE_REDUCTIONS = "damage_reductions";

    @Nullable public List<DamageReduction> damageReductions;

    public void tick(final Entity entity) {
        if (damageReductions != null) {
            damageReductions.forEach(reduction -> reduction.tick(entity));
        }
    }

    public void add(final DamageReduction reduction) {
        if (damageReductions == null) {
            damageReductions = new ArrayList<>();
        }

        damageReductions.add(reduction);
    }

    public void remove(final Entity entity, final DamageReduction reduction) {
        if (damageReductions == null) {
            return;
        }

        damageReductions.remove(reduction);

        if (damageReductions.isEmpty()) {
            entity.removeData(DSDataAttachments.DAMAGE_REDUCTIONS);
        }
    }

    public boolean contains(final DamageReduction reduction) {
        if (damageReductions == null) {
            return false;
        }

        return damageReductions.contains(reduction);
    }

    public float calculate(final Holder<DamageType> damageType, float damageAmount) {
        if (damageReductions == null) {
            return damageAmount;
        }

        float newDamageAmount = damageAmount;

        for (final DamageReduction damageReduction : damageReductions) {
            newDamageAmount = damageReduction.calculate(damageType, newDamageAmount);

            if (newDamageAmount == 0) {
                break;
            }
        }

        return newDamageAmount;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag entries = new ListTag();

        if (damageReductions != null) {
            damageReductions.forEach(DamageReduction::save);
            tag.put(DAMAGE_REDUCTIONS, entries);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, @NotNull final CompoundTag tag) {
        ArrayList<DamageReduction> damageReductions = new ArrayList<>();
        ListTag entries = tag.getList(DAMAGE_REDUCTIONS, ListTag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            DamageReduction modifier = DamageReduction.load(entries.getCompound(i));

            if (modifier != null) {
                damageReductions.add(modifier);
            }
        }

        if (!damageReductions.isEmpty()) {
            this.damageReductions = damageReductions;
        }
    }

    @SubscribeEvent
    public static void tickReductions(final EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        event.getEntity().getExistingData(DSDataAttachments.DAMAGE_REDUCTIONS).ifPresent(reductions -> reductions.tick(event.getEntity()));
    }

    @SubscribeEvent
    public static void checkImmunity(final EntityInvulnerabilityCheckEvent event) {
        if (event.isInvulnerable()) {
            return;
        }

        event.getEntity().getExistingData(DSDataAttachments.DAMAGE_REDUCTIONS).ifPresent(reductions -> {
            // Supply a dummy damage amount (since it doesn't matter for this check but allows re-using the same method)
            if (reductions.calculate(event.getSource().typeHolder(), 1) == 0) {
                event.setInvulnerable(true);
            }
        });
    }

    @SubscribeEvent
    public static void reduceDamage(final LivingIncomingDamageEvent event) {
        event.getEntity().getExistingData(DSDataAttachments.DAMAGE_REDUCTIONS).ifPresent(reductions -> {
            event.setAmount(reductions.calculate(event.getSource().typeHolder(), event.getAmount()));
        });
    }
}
