package by.dragonsurvivalteam.dragonsurvival.client.render.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBallEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class FireBallRenderer extends GeoEntityRenderer<FireBallEntity> {
	private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/fireball_texture.png");

	public FireBallRenderer(final EntityRendererProvider.Context context, final GeoModel<FireBallEntity> model) {
		super(context, model);
	}

	@Override
	protected int getBlockLightLevel(@NotNull final FireBallEntity entity, @NotNull final BlockPos position) {
		return 15;
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull final FireBallEntity entity) {
		return TEXTURE_LOCATION;
	}
}