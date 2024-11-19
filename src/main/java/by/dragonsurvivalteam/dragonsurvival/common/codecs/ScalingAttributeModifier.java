package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.Optional;
import java.util.UUID;

public record ScalingAttributeModifier(Holder<Attribute> attribute, LevelBasedValue perSize, AttributeModifier modifier, Optional<String> dragonType) {
    public static final Codec<ScalingAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(ScalingAttributeModifier::attribute),
            LevelBasedValue.CODEC.fieldOf("per_size").forGetter(ScalingAttributeModifier::perSize),
            AttributeModifier.CODEC.fieldOf("modifier").forGetter(ScalingAttributeModifier::modifier),
            Codec.STRING.optionalFieldOf("dragon_type").forGetter(ScalingAttributeModifier::dragonType)
    ).apply(instance, ScalingAttributeModifier::new));

    public static ScalingAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation) {
        return createModifier(type, attribute, amount, operation, null);
    }

    public static ScalingAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, LevelBasedValue perSize, float amount, final AttributeModifier.Operation operation) {
        return createModifier(type, attribute, perSize, amount, operation, null);
    }

    public static ScalingAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, float amount, final AttributeModifier.Operation operation, String dragonType) {
        return createModifier(type, attribute, LevelBasedValue.perLevel(amount), amount, operation, dragonType);
    }

    public static ScalingAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, final LevelBasedValue perSize, float amount, final AttributeModifier.Operation operation, String dragonType) {
        return new ScalingAttributeModifier(attribute, perSize, new AttributeModifier(type.randomId(attribute), amount, operation), Optional.ofNullable(dragonType));
    }
}
