package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.AttributeModifierSupplier;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStage;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonStages;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.neoforge.common.CommonHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public record DragonAbility(
        List<Modifier> modifiers,
        LevelBasedValue upgradeCost,
        AbilitySlot slot) implements AttributeModifierSupplier {

    enum AbilityType {
        PASSIVE,
        ACTIVE,
        INNATE
    }

    public record AbilitySlot(int slot, AbilityType type) {
        public static final Codec<AbilitySlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(AbilitySlot::slot),
                Codec.STRING.xmap(AbilityType::valueOf, AbilityType::name).fieldOf("type").forGetter(AbilitySlot::type)
        ).apply(instance, AbilitySlot::new));
    }

    public static final ResourceKey<Registry<DragonAbility>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvival.res("dragon_abilities"));

    public static final Codec<DragonAbility> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Modifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(DragonAbility::modifiers),
            LevelBasedValue.CODEC.fieldOf("upgrade_cost").forGetter(DragonAbility::upgradeCost),
            AbilitySlot.CODEC.fieldOf("slot").forGetter(DragonAbility::slot)

    ).apply(instance, instance.stable(DragonAbility::new)));

    public static void update(@Nullable final HolderLookup.Provider provider) {
        validate(provider);
    }

    private static void validate(@Nullable final HolderLookup.Provider provider) {
        StringBuilder nextAbilityCheck = new StringBuilder("The following abilities are incorrectly defined:");
        AtomicBoolean areAbilitiesValid = new AtomicBoolean(true);

        ResourceHelper.keys(provider, REGISTRY).forEach(key -> {
            //noinspection OptionalGetWithoutIsPresent -> ignore
            Holder.Reference<DragonAbility> ability = ResourceHelper.get(provider, key, REGISTRY).get();

            // Nothing for now
        });

        if (!areAbilitiesValid.get()) {
            throw new IllegalStateException(nextAbilityCheck.toString());
        }
    }
}
