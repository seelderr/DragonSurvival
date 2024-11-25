package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;

public record Modifier(ModifierType type, Holder<Attribute> attribute, LevelBasedValue amount, AttributeModifier.Operation operation, Optional<String> dragonType, Optional<PerValueType> perValueType) {
    public enum PerValueType {
        CONSTANT,
        PER_SIZE,
        PER_LEVEL
    }

    public static final Codec<PerValueType> PER_VALUE_TYPE_CODEC = Codec.STRING.xmap(PerValueType::valueOf, PerValueType::name);

    public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModifierType.CODEC.fieldOf("type").forGetter(Modifier::type),
            Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(Modifier::amount),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation),
            Codec.STRING.optionalFieldOf("dragon_type").forGetter(Modifier::dragonType),
            PER_VALUE_TYPE_CODEC.optionalFieldOf("per_value_of").forGetter(Modifier::perValueType)
    ).apply(instance, Modifier::new));

    public static Modifier constant(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return new Modifier(type, attribute, LevelBasedValue.constant(amount), operation, Optional.empty(), Optional.empty());
    }

    public static Modifier constant(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation, final String dragonType) {
        return new Modifier(type, attribute, LevelBasedValue.constant(amount), operation, Optional.of(dragonType), Optional.empty());
    }

    public static Modifier perSize(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return new Modifier(type, attribute, LevelBasedValue.perLevel(amount), operation, Optional.empty(), Optional.of(PerValueType.PER_SIZE));
    }

    public static Modifier perSize(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation, final String dragonType) {
        return new Modifier(type, attribute, LevelBasedValue.perLevel(amount), operation, Optional.of(dragonType), Optional.of(PerValueType.PER_SIZE));
    }

    public static Modifier perLevel(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return new Modifier(type, attribute, LevelBasedValue.perLevel(amount), operation, Optional.empty(), Optional.of(PerValueType.PER_LEVEL));
    }

    public AttributeModifier getModifier(double size) {
        perValueType.ifPresent(perValueType -> {
            if (perValueType == PerValueType.PER_LEVEL) {
                throw new IllegalStateException("Attempted to calculate a per level modifier with a per size method");
            }
        });
        return new AttributeModifier(type().randomId(attribute(), operation()), amount().calculate((int) size), operation());
    }

    public AttributeModifier getModifier(int level) {
        perValueType.ifPresent(perValueType -> {
            if (perValueType == PerValueType.PER_SIZE) {
                throw new IllegalStateException("Attempted to calculate a per size modifier with a per level method");
            }
        });
        return new AttributeModifier(type().randomId(attribute(), operation()), amount().calculate(level), operation());
    }
}
