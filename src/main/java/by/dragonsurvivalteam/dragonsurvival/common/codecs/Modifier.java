package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;

public record Modifier(Holder<Attribute> attribute, LevelBasedValue amount, AttributeModifier.Operation operation, Optional<String> dragonType) {
    public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(Modifier::amount),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation),
            Codec.STRING.optionalFieldOf("dragon_type").forGetter(Modifier::dragonType)
    ).apply(instance, Modifier::new));

    public static Modifier constant(final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return new Modifier(attribute, LevelBasedValue.constant(amount), operation, Optional.empty());
    }

    public static Modifier constant(final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation, final String dragonType) {
        return new Modifier(attribute, LevelBasedValue.constant(amount), operation, Optional.of(dragonType));
    }

    public static Modifier per(final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return new Modifier(attribute, LevelBasedValue.perLevel(amount), operation, Optional.empty());
    }

    public static Modifier per(final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation, final String dragonType) {
        return new Modifier(attribute, LevelBasedValue.perLevel(amount), operation, Optional.of(dragonType));
    }

    public AttributeModifier getModifier(final ModifierType type, double level) {
        return getModifier(type, (int) level);
    }

    public AttributeModifier getModifier(final ModifierType type, int level) {
        return new AttributeModifier(type.randomId(attribute(), operation()), amount().calculate(level), operation());
    }
}
