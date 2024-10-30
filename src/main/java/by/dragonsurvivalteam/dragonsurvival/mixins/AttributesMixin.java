package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Attributes.class)
public abstract class AttributesMixin {
    static {
        // smol
        ((RangedAttribute) Attributes.SCALE.value()).minValue = 0.018;
        // beeg
        ((RangedAttribute) Attributes.SCALE.value()).maxValue = 160;
    }
}
