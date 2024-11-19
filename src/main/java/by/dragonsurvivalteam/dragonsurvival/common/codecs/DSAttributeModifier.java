package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Optional;

public record DSAttributeModifier(Holder<Attribute> attribute, AttributeModifier modifier, Optional<String> dragonType) {
    public static final Codec<DSAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(DSAttributeModifier::attribute),
            AttributeModifier.CODEC.fieldOf("modifier").forGetter(DSAttributeModifier::modifier),
            Codec.STRING.optionalFieldOf("dragon_type").forGetter(DSAttributeModifier::dragonType)
    ).apply(instance, DSAttributeModifier::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DSAttributeModifier> STREAM_CODEC = StreamCodec.composite(
            Attribute.STREAM_CODEC, DSAttributeModifier::attribute,
            AttributeModifier.STREAM_CODEC, DSAttributeModifier::modifier,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), DSAttributeModifier::dragonType,
            DSAttributeModifier::new
    );

    public static DSAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, double amount, final AttributeModifier.Operation operation) {
        return createModifier(type, attribute, amount, operation, null);
    }

    public static DSAttributeModifier createModifier(final ModifierType type, final Holder<Attribute> attribute, double amount, final AttributeModifier.Operation operation, String dragonType) {
        return new DSAttributeModifier(attribute, new AttributeModifier(type.randomId(attribute), amount, operation), Optional.ofNullable(dragonType));
    }
}
