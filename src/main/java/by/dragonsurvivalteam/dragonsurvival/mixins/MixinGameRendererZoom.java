package by.dragonsurvivalteam.dragonsurvival.mixins;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( GameRenderer.class )
public interface MixinGameRendererZoom{
	@Accessor( "zoom" )
	float getZoom();

	@Accessor( "zoom" )
	void setZoom(float zoom);
}