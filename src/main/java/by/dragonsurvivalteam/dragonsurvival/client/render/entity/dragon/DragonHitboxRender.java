package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class DragonHitboxRender extends EntityRenderer<DragonHitBox>{
	public DragonHitboxRender(EntityRendererProvider.Context p_i46179_1_){
		super(p_i46179_1_);
	}

	@Override
	public boolean shouldRender(DragonHitBox p_225626_1_, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_){
		return true;
	}

	@Override
	public void render(DragonHitBox p_225623_1_, float p_225623_2_, float p_225623_3_, PoseStack p_225623_4_, MultiBufferSource p_225623_5_, int p_225623_6_){
	}

	@Override
	public ResourceLocation getTextureLocation(DragonHitBox p_110775_1_){
		return null;
	}
}