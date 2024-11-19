package by.dragonsurvivalteam.dragonsurvival.common.codecs;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;

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

    public ResourceLocation randomId(final Holder<Attribute> attribute) {
        String attributeId = attribute.getRegisteredName().replace(":", ".");
        return DragonSurvival.res(path() + RANDOM.nextInt(100_000) + "/" + attributeId);
    }
}
