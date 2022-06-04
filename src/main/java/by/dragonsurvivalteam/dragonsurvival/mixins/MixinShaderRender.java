package by.dragonsurvivalteam.dragonsurvival.mixins;


import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import srg.net.optifine.shaders.ShadersRender;


@Mixin( ShadersRender.class )
public class MixinShaderRender{
	/**
	 * @author Horeak
	 */
	@Overwrite( remap = false )
	public static void updateActiveRenderInfo(Camera activeRenderInfo, Minecraft mc, float partialTicks){
		activeRenderInfo.setup(mc.level, mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity(), !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTicks);
		EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, activeRenderInfo, partialTicks);
		activeRenderInfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
	}
}