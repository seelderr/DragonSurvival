package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.upgrade;

import by.dragonsurvivalteam.dragonsurvival.registry.datagen.lang.LangKey;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.util.ExperienceUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

public record ValueBasedUpgrade(Type type, int maximumLevel, LevelBasedValue requirementOrCost) {
    public static final Codec<ValueBasedUpgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(ValueBasedUpgrade::type),
            ExtraCodecs.intRange(DragonAbilityInstance.MIN_LEVEL, DragonAbilityInstance.MAX_LEVEL).fieldOf("maximum_level").forGetter(ValueBasedUpgrade::maximumLevel),
            LevelBasedValue.CODEC.fieldOf("requirement_or_cost").forGetter(ValueBasedUpgrade::requirementOrCost)
    ).apply(instance, ValueBasedUpgrade::new));

    public MutableComponent getDescription(int level) {
        int requirement = (int) requirementOrCost.calculate(level + 1);
        return switch (type) {
            case MANUAL -> Component.translatable(LangKey.ABILITY_LEVEL_MANUAL_UPGRADE, requirement, ExperienceUtils.getLevel(requirement));
            case PASSIVE_LEVEL -> Component.translatable(LangKey.ABILITY_LEVEL_AUTO_UPGRADE, requirement);
            case PASSIVE_GROWTH -> Component.translatable(LangKey.ABILITY_GROWTH_AUTO_UPGRADE, requirement);
        };
    }

    public enum Type implements StringRepresentable {
        MANUAL("manual"),
        PASSIVE_LEVEL("passive_level"),
        PASSIVE_GROWTH("passive_growth");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
