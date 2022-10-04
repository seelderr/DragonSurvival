package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.BreathAbility;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity>{
	public ResourceLocation glowTexture = null;

	public boolean shouldRenderLayers = true;
	public boolean isRenderLayers = false;

	public Color renderColor = Color.ofRGB(255, 255, 255);

	public DragonRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<DragonEntity> modelProvider){
		super(renderManager, modelProvider);
		this.addLayer(new DragonGlowLayerRenderer(this));
		this.addLayer(new ClawsAndTeethRenderLayer(this));
		this.addLayer(new DragonArmorRenderLayer(this));
	}


	private int pOverlay;

	@Override
	public void renderEarly(DragonEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks){
		Minecraft.getInstance().getProfiler().push("player_dragon");
		super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		pOverlay = packedOverlayIn;
	}

	@Override
	public void renderLate(DragonEntity animatable, PoseStack stackIn, float partialTicks, MultiBufferSource renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
		super.renderLate(animatable, stackIn, partialTicks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		Minecraft.getInstance().getProfiler().pop();
	}

	@ConfigOption( side = ConfigSide.CLIENT, key = "renderHeldItem", comment = "Should items be rendered in third person for dragon players?", category = "rendering" )
	public static boolean renderHeldItem = true;

	@Override
	public void render(DragonEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn){
		if(entity.getPlayer().hasEffect(MobEffects.INVISIBILITY)){
			return;
		}

		Player player = entity.getPlayer();
		DragonStateHandler handler = DragonUtils.getHandler(player);

		boolean hasWings = handler.hasWings() && handler.getSkin().skinPreset.skinAges.get(handler.getLevel()).wings;

		IBone leftWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingLeft");
		IBone rightWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingRight");

		if(leftWing != null)
			leftWing.setHidden(!hasWings);

		if(rightWing != null)
			rightWing.setHidden(!hasWings);

		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);

		if(!isRenderLayers){
			GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelLocation(entity));

			if(model != null){
				if(renderHeldItem){
				if(player != Minecraft.getInstance().player || ClientDragonRender.alternateHeldItem || !Minecraft.getInstance().options.getCameraType().isFirstPerson()){
					if(!entity.getPlayer().getInventory().offhand.get(0).isEmpty()){
						model.getBone(ClientDragonRender.renderItemsInMouth ? "LeftItem_jaw" : "LeftItem").ifPresent(bone -> {
							PoseStack newMatrixStack = new PoseStack();
							newMatrixStack.last().normal().mul(bone.getWorldSpaceNormal());
							newMatrixStack.last().pose().multiply(bone.getWorldSpaceXform());
							newMatrixStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());

							Minecraft.getInstance().getItemRenderer().renderStatic(entity.getPlayer().getInventory().offhand.get(0), TransformType.THIRD_PERSON_LEFT_HAND, packedLightIn, pOverlay, newMatrixStack, bufferIn, 0);
						});
					}

					if(!entity.getPlayer().getInventory().getSelected().isEmpty()){
						model.getBone(ClientDragonRender.renderItemsInMouth ? "RightItem_jaw" : "RightItem").ifPresent(bone -> {
							PoseStack newMatrixStack = new PoseStack();
							newMatrixStack.last().normal().mul(bone.getWorldSpaceNormal());
							newMatrixStack.last().pose().multiply(bone.getWorldSpaceXform());
							newMatrixStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());

							Minecraft.getInstance().getItemRenderer().renderStatic(entity.getPlayer().getInventory().getSelected(), TransformType.FIRST_PERSON_RIGHT_HAND, packedLightIn, pOverlay, newMatrixStack, bufferIn, 0);
						});
					}
				}

					model.getBone("BreathSource").ifPresent(bone -> {
						PoseStack newMatrixStack = new PoseStack();
						newMatrixStack.last().normal().mul(bone.getWorldSpaceNormal());
						newMatrixStack.last().pose().multiply(bone.getWorldSpaceXform());


						if(handler.getMagic().getCurrentlyCasting() instanceof BreathAbility ability){
							if(ability.getChargeTime() >= ability.getSkillChargeTime()){
								if(ability.getEffectEntity() != null){
									newMatrixStack.mulPose(Vector3f.YN.rotationDegrees(-90));
									//stack.mulPose(Vector3f.ZN.rotationDegrees(player.xRot));//For head pitch
									EntityRenderer<? super Entity> effectRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ability.getEffectEntity());
									effectRender.render(ability.getEffectEntity(), player.getViewYRot(partialTicks), partialTicks, newMatrixStack, bufferIn, 200);
								}
							}
						}
					});
				}
			}
		}
	}

	@Override
	public Color getRenderColor(DragonEntity animatable, float partialTicks, PoseStack stack,
		@Nullable MultiBufferSource renderTypeBuffer,
		@Nullable VertexConsumer vertexBuilder, int packedLightIn){
		return renderColor;
	}
}