package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.magic.DragonAbilities;
import by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives.BreathAbilities.BreathAbility;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity>{
	public ResourceLocation glowTexture = null;
	public boolean renderLayers = true;
	public boolean isLayer = false;
	public Color renderColor = Color.ofRGB(255, 255, 255);
	private float partialTicks;
	private DragonEntity currentEntity;

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

		final IBone leftwing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingLeft");
		final IBone rightWing = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("WingRight");

		if(leftwing != null){
			leftwing.setHidden(!hasWings);
		}

		if(rightWing != null){
			rightWing.setHidden(!hasWings);
		}

		if(getGeoModelProvider().getTextureLocation(entity) == null){
			return;
		}

		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
		Player player = currentEntity != null ? currentEntity.getPlayer() : null;

		if(!isLayer && player != null){
			if(bone.getName().equals(ConfigHandler.CLIENT.renderItemsInMouth.get() ? "RightItem_jaw" : "RightItem") && !mainHand.isEmpty()){
				if(player != Minecraft.getInstance().player || ConfigHandler.CLIENT.alternateHeldItem.get() || !Minecraft.getInstance().options.getCameraType().isFirstPerson()){
					stack.pushPose();
					GeoCube ch = bone.childCubes != null && bone.childCubes.size() > 0 ? bone.childCubes.get(0) : null;
					RenderUtils.translate(bone, stack);
					RenderUtils.moveToPivot(bone, stack);
					RenderUtils.rotate(bone, stack);
					RenderUtils.scale(bone, stack);
					stack.mulPose(Vector3f.ZP.rotationDegrees(0));
					stack.translate(0.0, 0, 0.0);
					if(ch != null){
						stack.scale(ch.size.x(), ch.size.y(), ch.size.z());
					}
					Minecraft.getInstance().getItemRenderer().renderStatic(currentEntity.getPlayer().getInventory().getSelected(), ConfigHandler.CLIENT.thirdPersonItemRender.get() ? TransformType.THIRD_PERSON_RIGHT_HAND : TransformType.GROUND, packedLightIn, packedOverlayIn, stack, rtb, 0);
					stack.popPose();
					bufferIn = rtb.getBuffer(RenderType.entityCutout(whTexture));
				}
			}else if(bone.getName().equals(ConfigHandler.CLIENT.renderItemsInMouth.get() ? "LeftItem_jaw" : "LeftItem") && !offHand.isEmpty()){
				if(player != Minecraft.getInstance().player || ConfigHandler.CLIENT.alternateHeldItem.get() || !Minecraft.getInstance().options.getCameraType().isFirstPerson()){
					stack.pushPose();
					RenderUtils.translate(bone, stack);
					RenderUtils.moveToPivot(bone, stack);
					RenderUtils.rotate(bone, stack);
					RenderUtils.scale(bone, stack);
					GeoCube ch = bone.childCubes != null && bone.childCubes.size() > 0 ? bone.childCubes.get(0) : null;

					stack.mulPose(Vector3f.ZP.rotationDegrees(0));
					stack.translate(0.0, 0, 0.0);
					if(ch != null){
						stack.scale(ch.size.x(), ch.size.y(), ch.size.z());
					}
					Minecraft.getInstance().getItemRenderer().renderStatic(currentEntity.getPlayer().getInventory().offhand.get(0), ConfigHandler.CLIENT.thirdPersonItemRender.get() ? TransformType.THIRD_PERSON_RIGHT_HAND : TransformType.GROUND, packedLightIn, packedOverlayIn, stack, rtb, 0);
					stack.popPose();
					bufferIn = rtb.getBuffer(RenderType.entityCutout(whTexture));
				}
			}else if(bone.getName().equals("BreathSource")){
				DragonStateHandler handler = DragonUtils.getHandler(player);

				if(handler != null){
					if(handler.getMagic().getCurrentlyCasting() instanceof BreathAbility){
						BreathAbility ability = (BreathAbility)handler.getMagic().getCurrentlyCasting();
						int slot = DragonAbilities.getAbilitySlot(ability);
						if(ability.getCurrentCastTimer() >= ability.getCastingTime() || handler.getMagic().getAbilityFromSlot(slot).getCurrentCastTimer() >= ability.getCastingTime()){
							if(ability.getEffectEntity() != null){
								stack.pushPose();
								RenderUtils.translate(bone, stack);
								RenderUtils.moveToPivot(bone, stack);
								RenderUtils.rotate(bone, stack);
								RenderUtils.scale(bone, stack);
								stack.mulPose(Vector3f.YN.rotationDegrees(-90));
								//stack.mulPose(Vector3f.ZN.rotationDegrees(player.xRot));//For head pitch
								EntityRenderer<? super Entity> effectRender = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ability.getEffectEntity());
								effectRender.render(ability.getEffectEntity(), player.getViewYRot(partialTicks), partialTicks, stack, rtb, 200);
								bufferIn = rtb.getBuffer(RenderType.entityCutout(whTexture));

								stack.popPose();
							}
						}
					}
				}
			}
		}

		stack.pushPose();
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.rotate(bone, stack);
		RenderUtils.scale(bone, stack);
		RenderUtils.moveBackFromPivot(bone, stack);

		GeoBone currentCheckBone = bone;
		boolean isHidden = currentCheckBone.isHidden;

		//Check if any of the parents is hidden
		if(!isHidden){
			while(currentCheckBone.parent != null && !isHidden){
				isHidden = currentCheckBone.isHidden;
				currentCheckBone = currentCheckBone.parent;
			}
		}

		//Disable rendering if current bone or any bones above is hidden.
		if(!isHidden){
			for(GeoCube cube : bone.childCubes){
				stack.pushPose();
				renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				stack.popPose();
			}
		}

		//Still list through all bones even when hidden to allow rendering effects based on head in first person
		for(GeoBone childBone : bone.childBones){
			renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}

		stack.popPose();
	}

	@Override
	public void renderLate(DragonEntity animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks){
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		currentEntity = animatable;
		this.partialTicks = partialTicks;
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