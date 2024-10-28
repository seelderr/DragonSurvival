package by.dragonsurvivalteam.dragonsurvival.mixins;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(EntityRenderer.class)
public interface AccessorEntityRenderer {
	@Accessor("shadowRadius")
	void setShadowRadius(float radius);

	@Invoker("renderNameTag")
	void callRenderNameTag(Entity pEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, float pPartialTick);
}