package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DamageReductions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import javax.annotation.Nullable;

public class DamageReduction {
    public static final int INFINITE_DURATION = -1;
    public static final int IMMUNE = 1;
    public static int NO_LEVEL = -1;

    public static final Codec<DamageReduction> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).fieldOf("damage_types").forGetter(DamageReduction::damageTypes),
            LevelBasedValue.CODEC.optionalFieldOf("damage_reduction", LevelBasedValue.constant(IMMUNE)).forGetter(DamageReduction::damageReduction),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(DamageReduction::duration)
    ).apply(instance, DamageReduction::new));

    public static final Codec<DamageReduction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).fieldOf("damage_types").forGetter(DamageReduction::damageTypes),
            LevelBasedValue.CODEC.optionalFieldOf("damage_reduction", LevelBasedValue.constant(IMMUNE)).forGetter(DamageReduction::damageReduction),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(DamageReduction::duration),
            Codec.INT.fieldOf("current_duration").forGetter(DamageReduction::currentDuration),
            Codec.INT.fieldOf("applied_ability_level").forGetter(DamageReduction::appliedAbilityLevel)
    ).apply(instance, DamageReduction::new));

    private final HolderSet<DamageType> damageTypes;
    private final LevelBasedValue reduction;
    private final LevelBasedValue duration;

    private int currentDuration;
    private int appliedAbilityLevel = NO_LEVEL;

    public DamageReduction(final HolderSet<DamageType> damageTypes, final LevelBasedValue reduction, final LevelBasedValue duration) {
        this.damageTypes = damageTypes;
        this.reduction = reduction;
        this.duration = duration;
    }

    public DamageReduction(final HolderSet<DamageType> damageTypes, final LevelBasedValue reduction, final LevelBasedValue duration, int currentDuration, int appliedAbilityLevel) {
        this.damageTypes = damageTypes;
        this.reduction = reduction;
        this.duration = duration;
        this.currentDuration = currentDuration;
        this.appliedAbilityLevel = appliedAbilityLevel;
    }

    public Tag save() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    public static @Nullable DamageReduction load(final CompoundTag nbt) {
        return CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(DragonSurvival.LOGGER::error).orElse(null);
    }

    public void apply(final Entity entity, int abilityLevel) {
        DamageReductions data = entity.getData(DSDataAttachments.DAMAGE_REDUCTIONS);
        int newDuration = (int) duration().calculate(abilityLevel);

        if (currentDuration == newDuration && appliedAbilityLevel == abilityLevel && data.contains(this)) {
            return;
        }

        data.remove(entity, this);
        currentDuration = newDuration;
        appliedAbilityLevel = abilityLevel;
        data.add(this);
    }

    public void tick(final Entity entity) {
        if (currentDuration == INFINITE_DURATION) {
            return;
        }

        currentDuration--;

        if (currentDuration == 0) {
            entity.getData(DSDataAttachments.DAMAGE_REDUCTIONS).remove(entity, this);
        }
    }

    public float calculate(final Holder<DamageType> damageType, float damageAmount) {
        float reduction = 0;

        if (damageTypes().contains(damageType)) {
            // TODO :: this way the reduction amount does not change if the ability is leveled or de-leveld
            //  this seems like it would be okay?
            reduction = damageReduction().calculate(appliedAbilityLevel);
        }

        if (reduction == IMMUNE) {
            return 0;
        }

        return damageAmount * (1 - reduction);
    }

    public HolderSet<DamageType> damageTypes() {
        return damageTypes;
    }

    public LevelBasedValue damageReduction() {
        return reduction;
    }

    public LevelBasedValue duration() {
        return duration;
    }

    public int currentDuration() {
        return currentDuration;
    }

    public int appliedAbilityLevel() {
        return appliedAbilityLevel;
    }
}
