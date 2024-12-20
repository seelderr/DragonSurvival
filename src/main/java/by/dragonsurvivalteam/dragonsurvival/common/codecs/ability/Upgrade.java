package by.dragonsurvivalteam.dragonsurvival.common.codecs.ability;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

public record Upgrade(Type type, int maximumLevel, LevelBasedValue experienceOrLevelCost) {
    public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Type.CODEC.fieldOf("type").forGetter(Upgrade::type),
        ExtraCodecs.intRange(DragonAbilityInstance.MIN_LEVEL, DragonAbilityInstance.MAX_LEVEL).fieldOf("maximum_level").forGetter(Upgrade::maximumLevel),
        /* FIXME :: that name is not clear since it's not level costs but level requirements
            We should find a better name or use Codec.either and have dedicated methods in here so that the outer classes don't need to interact with that 'either' entry
            FIXME x2 :: This problem just got worse since we also have growth costs now
        */
        LevelBasedValue.CODEC.fieldOf("experience_or_level_cost").forGetter(Upgrade::experienceOrLevelCost)
    ).apply(instance, Upgrade::new));

    public enum Type implements StringRepresentable {
        MANUAL("manual"),
        PASSIVE_LEVEL("passive_level"),
        PASSIVE_GROWTH("passive_growth"),;

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
