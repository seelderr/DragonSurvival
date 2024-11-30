package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DamageReductions;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.function.Function;
import javax.annotation.Nullable;

public class DamageReduction implements ClientEffectProvider {
    public static final int INFINITE_DURATION = -1;
    public static final int IMMUNE = 1;
    public static int NO_LEVEL = -1;

    public static final Codec<DamageReduction> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(DamageReduction::id),
            RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).fieldOf("damage_types").forGetter(DamageReduction::damageTypes),
            LevelBasedValue.CODEC.optionalFieldOf("damage_reduction", LevelBasedValue.constant(IMMUNE)).forGetter(DamageReduction::reduction),
            LevelBasedValue.CODEC.optionalFieldOf("duration", LevelBasedValue.constant(INFINITE_DURATION)).forGetter(DamageReduction::duration)
    ).apply(instance, DamageReduction::new));

    public static final Codec<DamageReduction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DIRECT_CODEC.fieldOf("base_data").forGetter(Function.identity()),
            Codec.INT.fieldOf("current_duration").forGetter(DamageReduction::currentDuration),
            Codec.INT.fieldOf("applied_ability_level").forGetter(DamageReduction::appliedAbilityLevel),
            ClientData.CODEC.fieldOf("client_data").forGetter(DamageReduction::clientData)
    ).apply(instance, DamageReduction::new));

    private final ResourceLocation id;
    private final HolderSet<DamageType> damageTypes;
    private final LevelBasedValue reduction;
    private final LevelBasedValue duration;

    private int currentDuration;
    private int appliedAbilityLevel = NO_LEVEL;
    private ClientData clientData = ClientEffectProvider.NONE;

    public DamageReduction(final ResourceLocation id, final HolderSet<DamageType> damageTypes, final LevelBasedValue reduction, final LevelBasedValue duration) {
        this.id = id;
        this.damageTypes = damageTypes;
        this.reduction = reduction;
        this.duration = duration;
    }

    public DamageReduction(final DamageReduction baseData, int currentDuration, int appliedAbilityLevel, final ClientData clientData) {
        this.id = baseData.id();
        this.damageTypes = baseData.damageTypes();
        this.reduction = baseData.reduction();
        this.duration = baseData.duration();

        this.currentDuration = currentDuration;
        this.appliedAbilityLevel = appliedAbilityLevel;
        this.clientData = clientData;
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
            reduction = reduction().calculate(appliedAbilityLevel);
        }

        if (reduction == IMMUNE) {
            return 0;
        }

        return damageAmount * (1 - reduction);
    }

    public ResourceLocation id() {
        return id;
    }

    public HolderSet<DamageType> damageTypes() {
        return damageTypes;
    }

    public LevelBasedValue reduction() {
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

    @Override
    public ClientData clientData() {
        return clientData;
    }

    @Override
    public int getDuration() {
        return (int) duration().calculate(appliedAbilityLevel());
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof DamageReduction otherReduction && id().equals(otherReduction.id())) {
            return true;
        }

        return super.equals(other);
    }
}
