package by.dragonsurvivalteam.dragonsurvival.mixins;


import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.optifine.shaders.ShadersRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinShaderRender.java
@Mixin( ShadersRender.class)
public class MixinShaderRender
{
	@Overwrite(remap = false)
	public static void updateCamera(Camera activeRenderInfo, Minecraft mc, float partialTicks)
	{
=======
@Mixin( ShadersRender.class )
public class MixinShaderRender{
	@Overwrite( remap = false )
	public static void updateActiveRenderInfo(ActiveRenderInfo activeRenderInfo, Minecraft mc, float partialTicks){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinShaderRender.java
		activeRenderInfo.setup(mc.level, mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity(), !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), partialTicks);
		EntityViewRenderEvent.CameraSetup cameraSetup = ForgeHooksClient.onCameraSetup(mc.gameRenderer, activeRenderInfo, partialTicks);
		activeRenderInfo.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
	}
}