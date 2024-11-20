package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.text.NumberFormat;
import java.util.Objects;

public enum ModifierType {
    DRAGON_TYPE("type"),
    DRAGON_BODY("body"),
    DRAGON_LEVEL("level");

    private static final RandomSource RANDOM = RandomSource.create();
    private final String path;

    ModifierType(final String path) {
        this.path = DragonSurvival.MODID + "/" + path + "/";
    }

    public String path() {
        return path;
    }

    public ResourceLocation randomId(final Holder<Attribute> attribute, double amount, final AttributeModifier.Operation operation) {
        String attributeId = attribute.getRegisteredName().replace(":", ".");
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(4);
        // Currently only relevant for data generation -> should be specific enough to avoid overlapping
        return DragonSurvival.res(path() + Objects.hash(format.format(amount), operation.getSerializedName()) + "/" + attributeId);
    }
}
