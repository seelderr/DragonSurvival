package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.fml.ModList;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;
import software.bernie.geckolib.util.RenderUtil;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {
	@ConfigOption( side = ConfigSide.CLIENT, key = "renderHeldItem", comment = "Should items be rendered in third person for dragon players?", category = "rendering" )
	public static boolean renderHeldItem = true;

	public ResourceLocation glowTexture = null;

	public boolean shouldRenderLayers = true;
	public boolean isRenderLayers = false;

	/** Used when rendering dyeable armor pieces in {@link ClientDragonRender#renderArmorPiece} */
	public Color renderColor = Color.ofRGB(255, 255, 255);

	public DragonRenderer(final EntityRendererProvider.Context context, final GeoModel<DragonEntity> model) {
		super(context, model);

		getRenderLayers().add(new DragonGlowLayerRenderer(this));
		getRenderLayers().add(new ClawsAndTeethRenderLayer(this));
		getRenderLayers().add(new DragonArmorRenderLayer(this));
		getRenderLayers().add(new DragonItemRenderLayer(this, (bone, animatable) -> {
			if(bone.getName().equals(ClientDragonRender.renderItemsInMouth ? "RightItem_jaw" : "RightItem")) {
				return animatable.getMainHandItem();
			} else if(bone.getName().equals(ClientDragonRender.renderItemsInMouth ? "LeftItem_jaw" : "LeftItem")) {
				return animatable.getOffhandItem();
			}
			return null;
		}, (bone, animatable) -> null));

		/*if (ModList.get().isLoaded("curios")) {
			getRenderLayers().add(new DragonCuriosRenderLayer(this));
		}*/
	}

	@Override
	public void preRender(final PoseStack poseStack, final DragonEntity animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		Minecraft.getInstance().getProfiler().push("player_dragon");
		super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	public void postRender(final PoseStack poseStack, final DragonEntity animatable, final BakedGeoModel model, final MultiBufferSource bufferSource, final VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
		Minecraft.getInstance().getProfiler().pop();
	}

	@Override
	public void actuallyRender(final PoseStack poseStack, final DragonEntity animatable, final BakedGeoModel model, final RenderType renderType, final MultiBufferSource bufferSource, final VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
		Player player = animatable.getPlayer();

		if (player == null || player.hasEffect(MobEffects.INVISIBILITY)) {
			return;
		}

		DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);

		boolean hasWings = handler.getSkinData().skinPreset.skinAges.get(handler.getLevel()).get().wings;
		if (handler.getBody() != null)
			hasWings = hasWings || !handler.getBody().canHideWings();

		GeoBone leftWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingLeft");
		GeoBone rightWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingRight");
		GeoBone smallLeftWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("SmallWingLeft");
		GeoBone smallRightWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("SmallWingRight");

		if (leftWing != null)
			leftWing.setHidden(!hasWings);

		if(rightWing != null)
			rightWing.setHidden(!hasWings);
		
		if (smallLeftWing != null)
			smallLeftWing.setHidden(!hasWings);
		
		if (smallRightWing != null)
			smallRightWing.setHidden(!hasWings);

		super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
	}

	@Override
	public Color getRenderColor(final DragonEntity animatable, float partialTick, int packedLight) {
		return renderColor;
	}
}