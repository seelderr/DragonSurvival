package by.jackraidenph.dragonsurvival.handlers.ClientSide;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.gecko.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.gecko.model.DragonArmorModel;
import by.jackraidenph.dragonsurvival.gecko.model.DragonModel;
import by.jackraidenph.dragonsurvival.mixins.AccessorEntityRenderer;
import by.jackraidenph.dragonsurvival.mixins.AccessorEntityRendererManager;
import by.jackraidenph.dragonsurvival.mixins.AccessorLivingRenderer;
import by.jackraidenph.dragonsurvival.mixins.MixinGameRendererZoom;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
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
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
	@SuppressWarnings("unchecked,rawtypes")
	@SubscribeEvent
	public static void thirdPersonPreRender(RenderPlayerEvent.Pre renderPlayerEvent) {
	
	    PlayerEntity player = renderPlayerEvent.getPlayer();
	    Minecraft mc = Minecraft.getInstance();
	
	    // TODO come up with actual solution instead of just not rendering your passenger in first person.
	    if (mc.options.getCameraType() == PointOfView.FIRST_PERSON && mc.player.hasPassenger(player)) {
	        renderPlayerEvent.setCanceled(true);
	        return;
	    }
	
	    if (!playerDragonHashMap.containsKey(player.getId())) {
	        DragonEntity dummyDragon = EntityTypesInit.DRAGON.create(player.level);
	        dummyDragon.player = player.getId();
	        playerDragonHashMap.put(player.getId(), new AtomicReference<>(dummyDragon));
	    }
		
		if (dragonArmor == null) {
			dragonArmor = EntityTypesInit.DRAGON_ARMOR.create(player.level);
			assert dragonArmor != null;
			dragonArmor.player = player.getId();
		}
		
	    DragonStateProvider.getCap(player).ifPresent(cap -> {
	        if (cap.isDragon()) {
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
	                float scale = (float)Math.max(size / 40, DragonLevel.BABY.maxWidth);
	                String playerModelType = ((AbstractClientPlayerEntity) player).getModelName();
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
		
		            if (player.isCrouching() && cap.isFlying() && !player.isOnGround()) {
			            matrixStack.translate(0, -0.15, 0);
			
		            }else if (player.isCrouching()) {
						matrixStack.translate(0, 0.325 - ((size / DragonLevel.ADULT.size) * 0.150), 0);
						
					} else if (player.isSwimming() || player.isAutoSpinAttack() || (cap.isFlying() && !player.isOnGround() && !player.isInWater() && !player.isInLava())) {
						matrixStack.translate(0, -0.15 - ((size / DragonLevel.ADULT.size) * 0.2), 0);
					}
		            MixinGameRendererZoom gameRenderer = (MixinGameRendererZoom)Minecraft.getInstance().gameRenderer;
		            gameRenderer.setZoom(1.0F);
					
		            if (!player.isInvisible()) {
						if(ClientFlightHandler.canGlide(player) && cap.isFlying() && !player.isOnGround() && !player.isInLava() && !player.isInWater()){
							matrixStack.mulPose(Vector3f.XN.rotationDegrees((float)(player.getDeltaMovement().y * 20)));
							
							if(player == mc.player){
								int height = player.level.getHeight(Type.MOTION_BLOCKING, player.blockPosition().getX(), player.blockPosition().getZ());
								double aboveGround = Math.max(0, 4.0 - (player.position().y - height));
								
								if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
									Vector3d lookVec = player.getLookAngle();
									float f = Math.min(Math.max(0.5F, 1F - (float)(((lookVec.y * 5) / 2.5) * 0.5)), 3F);
									gameRenderer.setZoom(f);
									if (lookVec.y > 0.05) {
										matrixStack.translate(0, -(lookVec.y * 5) + MathHelper.clamp(((lookVec.y * 5) * (aboveGround / 4F)), 0, (lookVec.y * 5)), 0);
									}
								}
							}
						}
						
	                    dragonRenderer.render(dummyDragon, yaw, partialRenderTick, matrixStack, renderTypeBuffer, eventLight);
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
	    });
	}
}
