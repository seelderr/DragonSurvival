package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EffectRenderingInventoryScreen.class)
public interface EffectRenderingInventoryScreenAccessor {
    @Accessor("EFFECT_BACKGROUND_LARGE_SPRITE")
    ResourceLocation dragonSurvival$getEffectBackgroundLargeSprite();

    @Accessor("EFFECT_BACKGROUND_SMALL_SPRITE")
    ResourceLocation dragonSurvival$getEffectBackgroundSmallSprite();
}
