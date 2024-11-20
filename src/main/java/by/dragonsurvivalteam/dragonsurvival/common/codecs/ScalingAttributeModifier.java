package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;

public record ScalingAttributeModifier(ResourceLocation id, Holder<Attribute> attribute, LevelBasedValue perSize, AttributeModifier.Operation operation, Optional<String> dragonType) {
    public static final Codec<ScalingAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ScalingAttributeModifier::id),
            Attribute.CODEC.fieldOf("attribute").forGetter(ScalingAttributeModifier::attribute),
            LevelBasedValue.CODEC.fieldOf("per_size").forGetter(ScalingAttributeModifier::perSize),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(ScalingAttributeModifier::operation),
            Codec.STRING.optionalFieldOf("dragon_type").forGetter(ScalingAttributeModifier::dragonType)
    ).apply(instance, ScalingAttributeModifier::new));

    public static ScalingAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return createModifier(type, attribute, amount, operation, null);
    }

    public static ScalingAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, LevelBasedValue perSize, final AttributeModifier.Operation operation) {
        return createModifier(type, attribute, perSize, operation, null);
    }

    public static ScalingAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation, String dragonType) {
        return createModifier(type, attribute, LevelBasedValue.perLevel(amount), operation, dragonType);
    }

    public static ScalingAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, final LevelBasedValue perSize, final AttributeModifier.Operation operation, String dragonType) {
        return new ScalingAttributeModifier(type.randomId(attribute, perSize.calculate(1), operation), attribute, perSize, operation, Optional.ofNullable(dragonType));
    }

    public AttributeModifier getModifier(double size) {
        return new AttributeModifier(id(), perSize().calculate((int) size), operation());
    }
}
