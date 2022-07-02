package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BreathAbilities.BreathAbility;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

import javax.annotation.Nullable;

public class DragonRenderer extends ExtendedGeoEntityRenderer<DragonEntity>{
	public ResourceLocation glowTexture = null;

	public boolean shouldRenderLayers = true;
	public boolean isRenderLayers = false;

	public Color renderColor = Color.ofRGB(255, 255, 255);

	public DragonRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<DragonEntity> modelProvider){
		super(renderManager, modelProvider);
		this.addLayer(new DragonGlowLayerRenderer(this));
		this.addLayer(new DragonSkinLayerRenderer(this));
		this.addLayer(new ClawsAndTeethRenderLayer(this));
		this.addLayer(new DragonArmorRenderLayer(this));
	}


	@Override
	public void render(DragonEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn){
		Player player = entity.getPlayer();
		DragonStateHandler handler = DragonUtils.getHandler(player);

		boolean hasWings = handler.hasWings() && handler.getSkin().skinPreset.skinAges.get(handler.getLevel()).wings;

		IBone leftWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingLeft");
		IBone rightWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingRight");

		if(leftWing != null){
			leftWing.setHidden(!hasWings);
		}

		if(rightWing != null){
			rightWing.setHidden(!hasWings);
		}

		if(getGeoModelProvider().getTextureLocation(entity) == null){
			return;
		}

		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);

		if(!isRenderLayers){
			GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelLocation(entity));

			if(model != null){
				model.getBone("BreathSource").ifPresent(bone -> {
					PoseStack newMatrixStack = new PoseStack();
					newMatrixStack.last().normal().mul(bone.getWorldSpaceNormal());
					newMatrixStack.last().pose().multiply(bone.getWorldSpaceXform());

					if(handler.getMagic().getCurrentlyCasting() instanceof BreathAbility ability){
						int slot = DragonAbilities.getAbilitySlot(ability);
						if(ability.getCurrentCastTimer() >= ability.getCastingTime() || handler.getMagic().getAbilityFromSlot(slot).getCurrentCastTimer() >= ability.getCastingTime()){
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

	@Override
	protected boolean isArmorBone(GeoBone bone){
		return false;
	}

	@org.jetbrains.annotations.Nullable
	@Override
	protected ResourceLocation getTextureForBone(String boneName, DragonEntity currentEntity){
		return null;
	}

	@org.jetbrains.annotations.Nullable
	@Override
	protected ItemStack getHeldItemForBone(String boneName, DragonEntity currentEntity){
		if(currentEntity.level.isClientSide){
			if(currentEntity.getPlayer().getId() == Minecraft.getInstance().player.getId()){
				Minecraft.getInstance().levelRenderer.needsUpdate();
				CameraType pointofview = Minecraft.getInstance().options.getCameraType();

				if(pointofview.isFirstPerson()){
					return null;
				}
			}
		}

		if(boneName.equalsIgnoreCase(ClientDragonRender.renderItemsInMouth ? "RightItem_jaw" : "RightItem")){
			return currentEntity.getPlayer().getInventory().getSelected();
		}else if(boneName.equalsIgnoreCase(ClientDragonRender.renderItemsInMouth ? "LeftItem_jaw" : "LeftItem")){
			return currentEntity.getPlayer().getInventory().offhand.get(0);
		}

		return null;
	}

	@Override
	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName){
		if(boneName.equalsIgnoreCase(ClientDragonRender.renderItemsInMouth ? "RightItem_jaw" : "RightItem")){
			return TransformType.THIRD_PERSON_LEFT_HAND;
		}else if(boneName.equalsIgnoreCase(ClientDragonRender.renderItemsInMouth ? "LeftItem_jaw" : "LeftItem")){
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		}
		return null;
	}

	@org.jetbrains.annotations.Nullable
	@Override
	protected BlockState getHeldBlockForBone(String boneName, DragonEntity currentEntity){
		return null;
	}

	@Override
	protected void preRenderItem(PoseStack matrixStack, ItemStack item, String boneName, DragonEntity currentEntity, IBone bone){
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
		matrixStack.translate(0.0, -0.3, -0.5);
	}

	@Override
	protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, DragonEntity currentEntity){

	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, DragonEntity currentEntity, IBone bone){

	}

	@Override
	protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, DragonEntity currentEntity){

	}

	@Override
	public Color getRenderColor(DragonEntity animatable, float partialTicks, PoseStack stack,
		@Nullable
		MultiBufferSource renderTypeBuffer,
		@Nullable
		VertexConsumer vertexBuilder, int packedLightIn){
		return renderColor;
	}
}