package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemBlockRenderTypes.class)
public class MixinItemBlockRenderTypes {

    @Unique
    private static RenderType dragonSurvival$onRenderFluidLayer(FluidState fluidState)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null){
            return null;
        }

        if ((fluidState.is(Fluids.LAVA) || fluidState.is(Fluids.FLOWING_LAVA)) && player.hasEffect(DSEffects.LAVA_VISION))
            return RenderType.translucent();
        return null;
    }

    @ModifyReturnValue(method = "getRenderLayer", at = @At(value = "RETURN"))
    private static RenderType getRenderLayerReturnValue(RenderType renderType, @Local(argsOnly = true) FluidState fluidState) {
        RenderType modifiedRenderType = dragonSurvival$onRenderFluidLayer(fluidState);
        return modifiedRenderType != null ? modifiedRenderType : renderType;
    }
}
