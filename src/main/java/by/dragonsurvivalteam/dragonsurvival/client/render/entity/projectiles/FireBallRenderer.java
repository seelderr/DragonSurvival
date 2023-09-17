package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBallEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class FireBallRenderer extends GeoEntityRenderer<FireBallEntity> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(DragonSurvivalMod.MODID, "textures/entity/fireball_texture.png");

	public FireBallRenderer(final EntityRendererProvider.Context context, final GeoModel<FireBallEntity> model) {
		super(context, model);
	}

	@Override
	protected int getBlockLightLevel(@NotNull final FireBallEntity entity, @NotNull final BlockPos position) {
		return 15;
	}

	@Override
	public void render(@NotNull final FireBallEntity entity, float p_225623_2_, float p_225623_3_, @NotNull final PoseStack poseStack, @NotNull final MultiBufferSource buffer, int p_225623_6_) {
		super.render(entity, p_225623_2_, p_225623_3_, poseStack, buffer, p_225623_6_);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull final FireBallEntity entity) {
		return TEXTURE_LOCATION;
	}
}