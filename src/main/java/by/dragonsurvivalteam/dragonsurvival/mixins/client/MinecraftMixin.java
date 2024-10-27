package by.dragonsurvivalteam.dragonsurvival.mixins.client;

import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active.HunterAbility;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/** Enable the features of the Fabulous graphics mode */
@Mixin(Minecraft.class) // Maybe it's enough to enable 'RenderStateShard#ITEM_ENTITY_TARGET'?
public abstract class MinecraftMixin {
    @Shadow static Minecraft instance;

    @ModifyReturnValue(method = "useShaderTransparency", at = @At("RETURN"))
    private static boolean dragonSurvival$enableTranslucencyFix(boolean isFabulous) {
        // TODO :: not entirely sure if this is safe, should maybe be done in a different way
        return isFabulous || (/* Unsure why this check exists in vanilla */ !instance.gameRenderer.isPanoramicMode() && HunterAbility.fixTranslucency);
    }
}
