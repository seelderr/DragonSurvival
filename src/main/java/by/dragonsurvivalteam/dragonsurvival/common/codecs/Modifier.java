package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;

public record Modifier(ModifierType type, Holder<Attribute> attribute, LevelBasedValue perSize, AttributeModifier.Operation operation, Optional<String> dragonType) {
    public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModifierType.CODEC.fieldOf("type").forGetter(Modifier::type),
            Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute),
            LevelBasedValue.CODEC.fieldOf("per_size").forGetter(Modifier::perSize),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation),
            Codec.STRING.optionalFieldOf("dragon_type").forGetter(Modifier::dragonType)
    ).apply(instance, Modifier::new));

    public static Modifier constant(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return new Modifier(type, attribute, LevelBasedValue.constant(amount), operation, Optional.empty());
    }

    public static Modifier constant(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation, final String dragonType) {
        return new Modifier(type, attribute, LevelBasedValue.constant(amount), operation, Optional.of(dragonType));
    }

    public static Modifier perSize(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return new Modifier(type, attribute, LevelBasedValue.perLevel(amount), operation, Optional.empty());
    }

    public static Modifier perSize(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation, final String dragonType) {
        return new Modifier(type, attribute, LevelBasedValue.perLevel(amount), operation, Optional.of(dragonType));
    }

    public static Modifier create(final ModifierType type, final Holder<Attribute> attribute, final LevelBasedValue perSize, final AttributeModifier.Operation operation) {
        return new Modifier(type, attribute, perSize, operation, Optional.empty());
    }

    public static Modifier create(final ModifierType type, final Holder<Attribute> attribute, LevelBasedValue perSize, final AttributeModifier.Operation operation, final String dragonType) {
        return new Modifier(type, attribute, perSize, operation, Optional.of(dragonType));
    }

    public AttributeModifier getModifier(double size) {
        return new AttributeModifier(type().randomId(attribute(), operation()), perSize().calculate((int) size), operation());
    }
}
