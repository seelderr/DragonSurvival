package by.jackraidenph.dragonsurvival.client.render;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.client.handlers.DragonSkins;
import by.jackraidenph.dragonsurvival.client.models.DragonArmorModel;
import by.jackraidenph.dragonsurvival.client.models.DragonModel;
import by.jackraidenph.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.mixins.AccessorEntityRenderer;
import by.jackraidenph.dragonsurvival.mixins.AccessorEntityRendererManager;
import by.jackraidenph.dragonsurvival.mixins.AccessorLivingRenderer;
import by.jackraidenph.dragonsurvival.server.handlers.ServerFlightHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
@Mod.EventBusSubscriber( Dist.CLIENT)
public class ClientDragonRender
{
	public static DragonModel dragonModel = new DragonModel();
	public static DragonArmorModel dragonArmorModel = new DragonArmorModel(dragonModel);
	/**
	 * First-person armor instance
	 */
	public static DragonEntity dragonArmor;
	/**
	 * Instance used for rendering first-person dragon model
	 */
	public static AtomicReference<DragonEntity> dragonEntity;
	/**
	 * Instances used for rendering third-person dragon models
	 */
	public static ConcurrentHashMap<Integer, AtomicReference<DragonEntity>> playerDragonHashMap = new ConcurrentHashMap<>(20);
	
	@SubscribeEvent
	public static void renderFirstPerson(RenderHandEvent renderHandEvent) {
		if (ConfigHandler.CLIENT.renderInFirstPerson.get()) {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
				if (playerStateHandler.isDragon()) {
					if(ConfigHandler.CLIENT.alternateHeldItem.get()){
						renderHandEvent.setCanceled(true);
					}
				}
			});
		}
	}
	
	/**
	 * Called for every player.
	 */
	@SubscribeEvent
	public static void thirdPersonPreRender(RenderPlayerEvent.Pre renderPlayerEvent) {
		if(!(renderPlayerEvent.getPlayer() instanceof AbstractClientPlayerEntity)){
			return;
		}
		
		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)renderPlayerEvent.getPlayer();
	    Minecraft mc = Minecraft.getInstance();
		
	    if (!playerDragonHashMap.containsKey(player.getId())) {
	        DragonEntity dummyDragon = DSEntities.DRAGON.create(player.level);
	        dummyDragon.player = player.getId();
	        playerDragonHashMap.put(player.getId(), new AtomicReference<>(dummyDragon));
	    }
		
		if (dragonArmor == null) {
			dragonArmor = DSEntities.DRAGON_ARMOR.create(player.level);
			assert dragonArmor != null;
			dragonArmor.player = player.getId();
		}
		
		DragonStateHandler cap = DragonStateProvider.getCap(player).orElse( null);
        if (cap != null && cap.isDragon()) {
            renderPlayerEvent.setCanceled(true);
            final float partialRenderTick = renderPlayerEvent.getPartialRenderTick();
            final float yaw = player.getViewYRot(partialRenderTick);
			
            DragonLevel dragonStage = cap.getLevel();
            ResourceLocation texture = DragonSkins.getPlayerSkin(player, cap.getType(), dragonStage);
            MatrixStack matrixStack = renderPlayerEvent.getMatrixStack();
			
            try {
                matrixStack.pushPose();
	
	            Vector3f lookVector = DragonStateProvider.getCameraOffset(player);
	            matrixStack.translate(-lookVector.x(), lookVector.y(), -lookVector.z());
				
	            double size = cap.getSize();
				// This is some arbitrary scaling that was created back when the maximum size was hard capped at 40. Touching it will cause the render to desync from the hitbox.
                float scale = (float)Math.max(size / 40.0D, 0.4D);
                String playerModelType = player.getModelName();
                LivingRenderer playerRenderer = ((AccessorEntityRendererManager) mc.getEntityRenderDispatcher()).getPlayerRenderers().get(playerModelType);
                int eventLight = renderPlayerEvent.getLight();
                final IRenderTypeBuffer renderTypeBuffer = renderPlayerEvent.getBuffers();
                if (ConfigHandler.CLIENT.dragonNameTags.get()) {
                    net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(player, player.getDisplayName(), playerRenderer, matrixStack, renderTypeBuffer, eventLight, partialRenderTick);
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
                    if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || ((AccessorLivingRenderer) playerRenderer).callShouldShowName(player))) {
                        ((AccessorEntityRenderer) playerRenderer).callRenderNameTag(player, renderNameplateEvent.getContent(), matrixStack, renderTypeBuffer, eventLight);
                    }
                }

                matrixStack.mulPose(Vector3f.YN.rotationDegrees((float) cap.getMovementData().bodyYaw));
                matrixStack.scale(scale, scale, scale);
                ((AccessorEntityRenderer) renderPlayerEvent.getRenderer()).setShadowRadius((float)((3.0F * size + 62.0F) / 260.0F));
                DragonEntity dummyDragon = playerDragonHashMap.get(player.getId()).get();

                EntityRenderer<? super DragonEntity> dragonRenderer = mc.getEntityRenderDispatcher().getRenderer(dummyDragon);
                dragonModel.setCurrentTexture(texture);
	
	            if (player.isCrouching() && cap.isWingsSpread() && !player.isOnGround()) {
		            matrixStack.translate(0, -0.15, 0);
		
	            }else if (player.isCrouching()) {
					matrixStack.translate(0, 0.325 - ((size / DragonLevel.ADULT.size) * 0.150), 0);
					
				} else if (player.isSwimming() || player.isAutoSpinAttack() || (cap.isWingsSpread() && !player.isOnGround() && !player.isInWater() && !player.isInLava())) {
					matrixStack.translate(0, -0.15 - ((size / DragonLevel.ADULT.size) * 0.2), 0);
				}
	            if (!player.isInvisible()) {
					if(ServerFlightHandler.isGliding(player)){
						if(ConfigHandler.CLIENT.renderOtherPlayerRotation.get() || mc.player == player) {
							float upRot = MathHelper.clamp((float)(player.getDeltaMovement().y * 20), -80, 80);
							
							dummyDragon.prevXRot = MathHelper.lerp(0.1F, dummyDragon.prevXRot, upRot);
							dummyDragon.prevXRot = MathHelper.clamp(dummyDragon.prevXRot, -80, 80);
							
							if(Float.isNaN(dummyDragon.prevXRot)){
								dummyDragon.prevXRot = upRot;
							}
							
							if(Float.isNaN(dummyDragon.prevXRot)){
								dummyDragon.prevXRot = 0;
							}
							
							matrixStack.mulPose(Vector3f.XN.rotationDegrees(dummyDragon.prevXRot));
							
							Vector3d vector3d1 = player.getDeltaMovement();
							Vector3d vector3d = player.getViewVector(1f);
							double d0 = Entity.getHorizontalDistanceSqr(vector3d1);
							double d1 = Entity.getHorizontalDistanceSqr(vector3d);
							double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
							double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
							
							float rot = MathHelper.clamp(((float)(Math.signum(d3) * Math.acos(d2))) * 2, -1, 1);
							
							dummyDragon.prevZRot = MathHelper.lerp(0.1F, dummyDragon.prevZRot, rot);
							dummyDragon.prevZRot = MathHelper.clamp(dummyDragon.prevZRot, -1, 1);
							
							if(Float.isNaN(dummyDragon.prevZRot)){
								dummyDragon.prevZRot = rot;
							}
							
							if(Float.isNaN(dummyDragon.prevZRot)){
								dummyDragon.prevZRot = 0;
							}
							
							matrixStack.mulPose(Vector3f.ZP.rotation(dummyDragon.prevZRot));
						}
					}
					if(player != mc.player || !Minecraft.getInstance().options.getCameraType().isFirstPerson() || !ServerFlightHandler.isGliding(player)) {
						dragonRenderer.render(dummyDragon, yaw, partialRenderTick, matrixStack, renderTypeBuffer, eventLight);
						
						ItemStack helmet = player.getItemBySlot(EquipmentSlotType.HEAD);
						ItemStack chestPlate = player.getItemBySlot(EquipmentSlotType.CHEST);
						ItemStack legs = player.getItemBySlot(EquipmentSlotType.LEGS);
						ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);
						
						ResourceLocation helmetTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.HEAD));
						ResourceLocation chestPlateTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.CHEST));
						ResourceLocation legsTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.LEGS));
						ResourceLocation bootsTexture = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.FEET));
						
						renderArmorPiece(helmet, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, helmetTexture);
						renderArmorPiece(chestPlate, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, chestPlateTexture);
						renderArmorPiece(legs, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, legsTexture);
						renderArmorPiece(boots, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, bootsTexture);
					}
                }

                if (!player.isSpectator()) {
                    for (LayerRenderer<Entity, EntityModel<Entity>> layer : ((AccessorLivingRenderer) playerRenderer).getRenderLayers()) {
                        if (layer instanceof ParrotVariantLayer) {
                            matrixStack.scale(1.0F / scale, 1.0F / scale, 1.0F / scale);
                            matrixStack.mulPose(Vector3f.XN.rotationDegrees(180.0F));
                            double height = 1.3 * scale;
                            double forward = 0.3 * scale;
                            float parrotHeadYaw = MathHelper.clamp(-1.0F * (((float) cap.getMovementData().bodyYaw) - (float) cap.getMovementData().headYaw), -75.0F, 75.0F);
                            matrixStack.translate(0, -height, -forward);
                            layer.render(matrixStack, renderTypeBuffer, eventLight, player, 0.0F, 0.0F, partialRenderTick, (float) player.tickCount + partialRenderTick, parrotHeadYaw, (float) cap.getMovementData().headPitch);
                            matrixStack.translate(0, height, forward);
                            matrixStack.mulPose(Vector3f.XN.rotationDegrees(-180.0F));
                            matrixStack.scale(scale, scale, scale);
                            break;
                        }
                    }

                    ItemRenderer itemRenderer = mc.getItemRenderer();
                    final int combinedOverlayIn = LivingRenderer.getOverlayCoords(player, 0);
                    if (player.hasEffect(DragonEffects.TRAPPED)) {
                        ClientEvents.renderBolas(eventLight, combinedOverlayIn, renderTypeBuffer, matrixStack);
                    }
					
					if(player != Minecraft.getInstance().player || ConfigHandler.CLIENT.alternateHeldItem.get() || !Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
						ItemStack right = player.getMainHandItem();
						matrixStack.pushPose();
						matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
						matrixStack.translate(0.5, 1, -0.8);
						itemRenderer.renderStatic(right, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, eventLight, combinedOverlayIn, matrixStack, renderTypeBuffer);
						matrixStack.popPose();
						matrixStack.pushPose();
						
						ItemStack left = player.getOffhandItem();
						matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
						matrixStack.translate(-0.5, 1, -0.8);
						mc.getItemInHandRenderer().renderItem(player, left, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, true, matrixStack, renderTypeBuffer, eventLight);
						matrixStack.popPose();
					}
                }
            } catch (Throwable throwable) {
                if (!(throwable instanceof NullPointerException) || ConfigHandler.CLIENT.clientDebugMessages.get())
                    throwable.printStackTrace();
                matrixStack.popPose();
            } finally {
                matrixStack.popPose();
            }
        }
        else
            ((AccessorEntityRenderer)renderPlayerEvent.getRenderer()).setShadowRadius(0.5F);
	}
	
	private static void renderArmorPiece(ItemStack stack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, float yaw, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation helmetTexture)
	{
		Color armorColor = new Color(1f, 1f, 1f);
		
		if(stack.getItem() instanceof IDyeableArmorItem){
			int colorCode = ((IDyeableArmorItem)stack.getItem()).getColor(stack);
			armorColor = new Color(colorCode);
		}
		
		if(!stack.isEmpty()) {
			EntityRenderer<? super DragonEntity> dragonArmorRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ClientDragonRender.dragonArmor);
			ClientDragonRender.dragonArmor.copyPosition(entitylivingbaseIn);
			ClientDragonRender.dragonArmorModel.setArmorTexture(helmetTexture);
			Color preColor = ((DragonRenderer)dragonArmorRenderer).renderColor;
			((DragonRenderer)dragonArmorRenderer).renderColor = armorColor;
			dragonArmorRenderer.render(ClientDragonRender.dragonArmor, yaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
			((DragonRenderer)dragonArmorRenderer).renderColor = preColor;
			
		}
	}
	
	private static String constructArmorTexture(PlayerEntity playerEntity, EquipmentSlotType equipmentSlot) {
		String texture = "textures/armor/";
		Item item = playerEntity.getItemBySlot(equipmentSlot).getItem();
		if (item instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem) item;
			IArmorMaterial armorMaterial = armorItem.getMaterial();
			if (armorMaterial.getClass() == ArmorMaterial.class) {
				if (armorMaterial == ArmorMaterial.NETHERITE) {
					texture += "netherite_";
				} else if (armorMaterial == ArmorMaterial.DIAMOND) {
					texture += "diamond_";
				} else if (armorMaterial == ArmorMaterial.IRON) {
					texture += "iron_";
				} else if (armorMaterial == ArmorMaterial.LEATHER) {
					texture += "leather_";
				} else if (armorMaterial == ArmorMaterial.GOLD) {
					texture += "gold_";
				} else if (armorMaterial == ArmorMaterial.CHAIN) {
					texture += "chainmail_";
				} else if (armorMaterial == ArmorMaterial.TURTLE)
					texture += "turtle_";
				else {
					return texture + "empty_armor.png";
				}
				
				texture += "dragon_";
				switch (equipmentSlot) {
					case HEAD:
						texture += "helmet";
						break;
					case CHEST:
						texture += "chestplate";
						break;
					case LEGS:
						texture += "leggings";
						break;
					case FEET:
						texture += "boots";
						break;
				}
				texture += ".png";
				return texture;
			} else {
				int defense = armorItem.getDefense();
				switch (equipmentSlot) {
					case FEET:
						texture += MathHelper.clamp(defense, 1, 4) + "_dragon_boots";
						break;
					case CHEST:
						texture += MathHelper.clamp(defense / 2, 1, 4) + "_dragon_chestplate";
						break;
					case HEAD:
						texture += MathHelper.clamp(defense, 1, 4) + "_dragon_helmet";
						break;
					case LEGS:
						texture += MathHelper.clamp((int) (defense / 1.5), 1, 4) + "_dragon_leggings";
						break;
				}
				return texture + ".png";
			}
		}
		return texture + "empty_armor.png";
	}
}
