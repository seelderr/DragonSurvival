package by.dragonsurvivalteam.dragonsurvival.client.render;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonSkins;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonArmorModel;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonArmorRenderLayer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.DragonEffects;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.mixins.AccessorEntityRenderer;
import by.dragonsurvivalteam.dragonsurvival.mixins.AccessorEntityRendererManager;
import by.dragonsurvivalteam.dragonsurvival.mixins.AccessorLivingRenderer;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.PacketSyncCapabilityMovement;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.Color;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber( Dist.CLIENT )
public class ClientDragonRender{
	public static DragonModel dragonModel = new DragonModel();
	public static DragonArmorModel dragonArmorModel = new DragonArmorModel(dragonModel);
	/**
	 * First-person armor instance
	 */
	public static DragonEntity dragonArmor;
	public static DragonEntity dummyDragon;

	/**
	 * Instances used for rendering third-person dragon models
	 */
	public static ConcurrentHashMap<Integer, AtomicReference<DragonEntity>> playerDragonHashMap = new ConcurrentHashMap<>(20);
	private static boolean wasFreeLook = false;

	@SubscribeEvent
	public static void renderFirstPerson(RenderHandEvent renderHandEvent){
		if(ConfigHandler.CLIENT.renderInFirstPerson.get()){
			ClientPlayerEntity player = Minecraft.getInstance().player;
			DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
				if(playerStateHandler.isDragon()){
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
	public static void thirdPersonPreRender(RenderPlayerEvent.Pre renderPlayerEvent){
		if(!(renderPlayerEvent.getPlayer() instanceof AbstractClientPlayerEntity)){
			return;
		}

		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)renderPlayerEvent.getPlayer();
		Minecraft mc = Minecraft.getInstance();

		if(!playerDragonHashMap.containsKey(player.getId())){
			DragonEntity dummyDragon = DSEntities.DRAGON.create(player.level);
			dummyDragon.player = player.getId();
			playerDragonHashMap.put(player.getId(), new AtomicReference<>(dummyDragon));
		}

		if(dragonArmor == null){
			dragonArmor = DSEntities.DRAGON_ARMOR.create(player.level);
			assert dragonArmor != null;
			dragonArmor.player = player.getId();
		}

		if(dummyDragon == null){
			dummyDragon = DSEntities.DRAGON.create(player.level);
			assert dummyDragon != null;
			dummyDragon.player = player.getId();
		}

		DragonStateHandler cap = DragonUtils.getHandler(player);
		if(cap != null && cap.isDragon()){
			renderPlayerEvent.setCanceled(true);
			final float partialRenderTick = renderPlayerEvent.getPartialRenderTick();
			final float yaw = player.getViewYRot(partialRenderTick);

			DragonLevel dragonStage = cap.getLevel();
			ResourceLocation texture = DragonSkins.getPlayerSkin(player, cap.getType(), dragonStage);
			MatrixStack matrixStack = renderPlayerEvent.getMatrixStack();

			try{
				matrixStack.pushPose();

				Vector3f lookVector = DragonUtils.getCameraOffset(player);
				matrixStack.translate(-lookVector.x(), lookVector.y(), -lookVector.z());

				double size = cap.getSize() * cap.getSkin().skinPreset.sizeMul;
				// This is some arbitrary scaling that was created back when the maximum size was hard capped at 40. Touching it will cause the render to desync from the hitbox.
				float scale = (float)Math.max(size / 40.0D, 0.4D);
				String playerModelType = player.getModelName();
				LivingRenderer playerRenderer = ((AccessorEntityRendererManager)mc.getEntityRenderDispatcher()).getPlayerRenderers().get(playerModelType);
				int eventLight = renderPlayerEvent.getLight();
				final IRenderTypeBuffer renderTypeBuffer = renderPlayerEvent.getBuffers();
				if(ConfigHandler.CLIENT.dragonNameTags.get()){
					net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(player, player.getDisplayName(), playerRenderer, matrixStack, renderTypeBuffer, eventLight, partialRenderTick);
					net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
					if(renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || ((AccessorLivingRenderer)playerRenderer).callShouldShowName(player))){
						((AccessorEntityRenderer)playerRenderer).callRenderNameTag(player, renderNameplateEvent.getContent(), matrixStack, renderTypeBuffer, eventLight);
					}
				}

				matrixStack.mulPose(Vector3f.YN.rotationDegrees((float)cap.getMovementData().bodyYaw));
				matrixStack.scale(scale, scale, scale);
				((AccessorEntityRenderer)renderPlayerEvent.getRenderer()).setShadowRadius((float)((3.0F * size + 62.0F) / 260.0F));
				DragonEntity dummyDragon = playerDragonHashMap.get(player.getId()).get();

				EntityRenderer<? super DragonEntity> dragonRenderer = mc.getEntityRenderDispatcher().getRenderer(dummyDragon);
				dragonModel.setCurrentTexture(texture);

				if(player.isCrouching() && cap.isWingsSpread() && !player.isOnGround()){
					matrixStack.translate(0, -0.15, 0);
				}else if(player.isCrouching()){
					matrixStack.translate(0, 0.325 - ((size / DragonLevel.ADULT.size) * 0.140), 0);
				}else if(player.isSwimming() || player.isAutoSpinAttack() || (cap.isWingsSpread() && !player.isOnGround() && !player.isInWater() && !player.isInLava())){
					matrixStack.translate(0, -0.15 - ((size / DragonLevel.ADULT.size) * 0.2), 0);
				}
				if(!player.isInvisible()){
					if(ServerFlightHandler.isGliding(player)){
						if(ConfigHandler.CLIENT.renderOtherPlayerRotation.get() || mc.player == player){
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
					if(player != mc.player || !Minecraft.getInstance().options.getCameraType().isFirstPerson() || !ServerFlightHandler.isGliding(player) || ConfigHandler.CLIENT.renderFirstPersonFlight.get()){
						dragonRenderer.render(dummyDragon, yaw, partialRenderTick, matrixStack, renderTypeBuffer, eventLight);

						if(!ConfigHandler.CLIENT.armorRenderLayer.get()){
							ItemStack helmet = player.getItemBySlot(EquipmentSlotType.HEAD);
							ItemStack chestPlate = player.getItemBySlot(EquipmentSlotType.CHEST);
							ItemStack legs = player.getItemBySlot(EquipmentSlotType.LEGS);
							ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);

							ResourceLocation helmetTexture = new ResourceLocation(DragonSurvivalMod.MODID, DragonArmorRenderLayer.constructArmorTexture(player, EquipmentSlotType.HEAD));
							ResourceLocation chestPlateTexture = new ResourceLocation(DragonSurvivalMod.MODID, DragonArmorRenderLayer.constructArmorTexture(player, EquipmentSlotType.CHEST));
							ResourceLocation legsTexture = new ResourceLocation(DragonSurvivalMod.MODID, DragonArmorRenderLayer.constructArmorTexture(player, EquipmentSlotType.LEGS));
							ResourceLocation bootsTexture = new ResourceLocation(DragonSurvivalMod.MODID, DragonArmorRenderLayer.constructArmorTexture(player, EquipmentSlotType.FEET));

							renderArmorPiece(helmet, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, helmetTexture);
							renderArmorPiece(chestPlate, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, chestPlateTexture);
							renderArmorPiece(legs, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, legsTexture);
							renderArmorPiece(boots, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, bootsTexture);
						}
					}
				}

				if(!player.isSpectator()){
					for(LayerRenderer<Entity, EntityModel<Entity>> layer : ((AccessorLivingRenderer)playerRenderer).getRenderLayers()){
						if(layer instanceof ParrotVariantLayer){
							matrixStack.scale(1.0F / scale, 1.0F / scale, 1.0F / scale);
							matrixStack.mulPose(Vector3f.XN.rotationDegrees(180.0F));
							double height = 1.3 * scale;
							double forward = 0.3 * scale;
							float parrotHeadYaw = MathHelper.clamp(-1.0F * (((float)cap.getMovementData().bodyYaw) - (float)cap.getMovementData().headYaw), -75.0F, 75.0F);
							matrixStack.translate(0, -height, -forward);
							layer.render(matrixStack, renderTypeBuffer, eventLight, player, 0.0F, 0.0F, partialRenderTick, (float)player.tickCount + partialRenderTick, parrotHeadYaw, (float)cap.getMovementData().headPitch);
							matrixStack.translate(0, height, forward);
							matrixStack.mulPose(Vector3f.XN.rotationDegrees(-180.0F));
							matrixStack.scale(scale, scale, scale);
							break;
						}
					}

					final int combinedOverlayIn = LivingRenderer.getOverlayCoords(player, 0);
					if(player.hasEffect(DragonEffects.TRAPPED)){
						ClientEvents.renderBolas(eventLight, combinedOverlayIn, renderTypeBuffer, matrixStack);
					}
				}
			}catch(Throwable throwable){
				if(!(throwable instanceof NullPointerException) || ConfigHandler.CLIENT.clientDebugMessages.get()){
					throwable.printStackTrace();
				}
				matrixStack.popPose();
			}finally{
				matrixStack.popPose();
			}
		}else{
			((AccessorEntityRenderer)renderPlayerEvent.getRenderer()).setShadowRadius(0.5F);
		}
	}

	private static void renderArmorPiece(ItemStack stack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, float yaw, int packedLightIn, DragonEntity entitylivingbaseIn, float partialTicks, ResourceLocation helmetTexture){
		Color armorColor = new Color(1f, 1f, 1f);

		if(stack.getItem() instanceof IDyeableArmorItem){
			int colorCode = ((IDyeableArmorItem)stack.getItem()).getColor(stack);
			armorColor = new Color(colorCode);
		}

		if(!stack.isEmpty()){
			EntityRenderer<? super DragonEntity> dragonArmorRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ClientDragonRender.dragonArmor);
			ClientDragonRender.dragonArmor.copyPosition(entitylivingbaseIn);
			ClientDragonRender.dragonArmorModel.setArmorTexture(helmetTexture);
			Color preColor = ((DragonRenderer)dragonArmorRenderer).renderColor;
			((DragonRenderer)dragonArmorRenderer).renderLayers = false;
			((DragonRenderer)dragonArmorRenderer).renderColor = armorColor;
			dragonArmorRenderer.render(entitylivingbaseIn, yaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
			((DragonRenderer)dragonArmorRenderer).renderColor = preColor;
			((DragonRenderer)dragonArmorRenderer).renderLayers = true;
		}
	}

	@SubscribeEvent
	public static void spin(InputEvent.ClickInputEvent keyInputEvent){
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if(player == null){
			return;
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);
		if(handler == null || !handler.isDragon()){
			return;
		}

		if(keyInputEvent.isAttack() && keyInputEvent.shouldSwingHand() && !handler.getMovementData().dig){
			handler.getMovementData().bite = true;
		}
	}

	@SubscribeEvent
	public static void onClientTick(RenderTickEvent renderTickEvent){
		if(renderTickEvent.phase == Phase.START){
			Minecraft minecraft = Minecraft.getInstance();
			ClientPlayerEntity player = minecraft.player;
			if(player != null){
				DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
					if(playerStateHandler.isDragon()){
						playerStateHandler.getMovementData().headYawLastTick = MathHelper.lerp(0.05, playerStateHandler.getMovementData().headYawLastTick, playerStateHandler.getMovementData().headYaw);
						playerStateHandler.getMovementData().headPitchLastTick = MathHelper.lerp(0.05, playerStateHandler.getMovementData().headPitchLastTick, playerStateHandler.getMovementData().headPitch);
						playerStateHandler.getMovementData().bodyYawLastTick = MathHelper.lerp(0.05, playerStateHandler.getMovementData().bodyYawLastTick, playerStateHandler.getMovementData().bodyYaw);

						double bodyYaw = playerStateHandler.getMovementData().bodyYaw;
						float headRot = Functions.angleDifference((float)bodyYaw, MathHelper.wrapDegrees(player.yRot != 0.0 ? player.yRot : player.yHeadRot));

						if(ConfigHandler.CLIENT.rotateBodyWithCamera.get() && (!KeyInputHandler.FREE_LOOK.isDown() && !wasFreeLook)){
							if(headRot > 150){
								bodyYaw += 150 - headRot;
							}else if(headRot < -150){
								bodyYaw -= 150 + headRot;
							}
						}
						headRot = (float)MathHelper.lerp(0.05, playerStateHandler.getMovementData().headYaw, headRot);


						double headPitch = MathHelper.lerp(0.1, playerStateHandler.getMovementData().headPitch, player.xRot);
						Vector3d moveVector = getInputVector(new Vector3d(player.input.leftImpulse, 0, player.input.forwardImpulse), 1F, player.yRot);

						if(ServerFlightHandler.isFlying(player)){
							moveVector = new Vector3d(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
						}

						float f = (float)MathHelper.atan2(moveVector.z, moveVector.x) * (180F / (float)Math.PI) - 90F;
						float f1 = (float)(Math.pow(moveVector.x, 2) + Math.pow(moveVector.z, 2));

						if(KeyInputHandler.FREE_LOOK.isDown()){
							wasFreeLook = true;
						}

						if(wasFreeLook && !Minecraft.getInstance().options.getCameraType().isFirstPerson()){
							wasFreeLook = false;
						}

						if(!ConfigHandler.CLIENT.firstPersonRotation.get() && !KeyInputHandler.FREE_LOOK.isDown()){
							if((!wasFreeLook || moveVector.length() > 0) && Minecraft.getInstance().options.getCameraType().isFirstPerson()){
								bodyYaw = player.yRot;
								wasFreeLook = false;
								if(moveVector.length() > 0){
									float f5 = MathHelper.abs(MathHelper.wrapDegrees(player.yRot) - f);
									if(95.0F < f5 && f5 < 265.0F){
										f -= 180.0F;
									}

									float _f = MathHelper.wrapDegrees(f - (float)bodyYaw);
									bodyYaw += _f * 0.3F;
									float _f1 = MathHelper.wrapDegrees(player.yRot - (float)bodyYaw);

									if(_f1 < -75.0F){
										_f1 = -75.0F;
									}

									if(_f1 >= 75.0F){
										_f1 = 75.0F;

										bodyYaw = player.yRot - _f1;
										if(_f1 * _f1 > 2500.0F){
											bodyYaw += _f1 * 0.2F;
										}
									}
								}

								if(playerStateHandler.getMovementData().bodyYaw != bodyYaw || headRot != playerStateHandler.getMovementData().headYaw){
									bodyYaw = MathHelper.rotLerp(0.1f, (float)playerStateHandler.getMovementData().bodyYaw, (float)bodyYaw);
									bodyYaw = MathHelper.wrapDegrees(bodyYaw);

									playerStateHandler.setMovementData(bodyYaw, headRot, headPitch, playerStateHandler.getMovementData().bite);
									NetworkHandler.CHANNEL.sendToServer(new PacketSyncCapabilityMovement(player.getId(), playerStateHandler.getMovementData().bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
									return;
								}
							}
						}


						if(f1 > 0.000028){
							float f2 = MathHelper.wrapDegrees(f - (float)bodyYaw);
							bodyYaw += 0.5F * f2;

							if(minecraft.options.getCameraType() == PointOfView.FIRST_PERSON){
								float f5 = MathHelper.abs(MathHelper.wrapDegrees(player.yRot) - f);
								if(95.0F < f5 && f5 < 265.0F){
									f -= 180.0F;
								}

								float _f = MathHelper.wrapDegrees(f - (float)bodyYaw);
								bodyYaw += _f * 0.3F;
								float _f1 = MathHelper.wrapDegrees(player.yRot - (float)bodyYaw);

								if(_f1 < -75.0F){
									_f1 = -75.0F;
								}

								if(_f1 >= 75.0F){
									_f1 = 75.0F;

									bodyYaw = player.yRot - _f1;
									if(_f1 * _f1 > 2500.0F){
										bodyYaw += _f1 * 0.2F;
									}
								}
							}
						}

						if(playerStateHandler.getMovementData().bodyYaw != bodyYaw || headRot != playerStateHandler.getMovementData().headYaw){
							bodyYaw = MathHelper.rotLerp(0.1f, (float)playerStateHandler.getMovementData().bodyYaw, (float)bodyYaw);
							bodyYaw = MathHelper.wrapDegrees(bodyYaw);

							playerStateHandler.setMovementData(bodyYaw, headRot, headPitch, playerStateHandler.getMovementData().bite);
							NetworkHandler.CHANNEL.sendToServer(new PacketSyncCapabilityMovement(player.getId(), playerStateHandler.getMovementData().bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
						}
					}
				});
			}
		}
	}

	public static Vector3d getInputVector(Vector3d movement, float fricSpeed, float yRot){
		double d0 = movement.lengthSqr();
		if(d0 < 1.0E-7D){
			return Vector3d.ZERO;
		}else{
			Vector3d vector3d = (d0 > 1.0D ? movement.normalize() : movement).scale(fricSpeed);
			float f = MathHelper.sin(yRot * ((float)Math.PI / 180F));
			float f1 = MathHelper.cos(yRot * ((float)Math.PI / 180F));
			return new Vector3d(vector3d.x * (double)f1 - vector3d.z * (double)f, vector3d.y, vector3d.z * (double)f1 + vector3d.x * (double)f);
		}
	}

	public static void renderEntityInInventory(LivingEntity entity, int x, int y, float scale, float xRot, float yRot){
		renderEntityInInventory(entity, x, y, scale, xRot, yRot, 0, 0);
	}

	public static void renderEntityInInventory(LivingEntity entity, int x, int y, float scale, float xRot, float yRot, float xOffset, float yOffset){
		if(entity == null){
			return;
		}

		if(entity instanceof DragonEntity){
			if(ClientDragonRender.dragonArmor == null){
				ClientDragonRender.dragonArmor = DSEntities.DRAGON_ARMOR.create(Minecraft.getInstance().player.level);
				assert ClientDragonRender.dragonArmor != null;
				ClientDragonRender.dragonArmor.player = Minecraft.getInstance().player.getId();
			}

			if(!ClientDragonRender.playerDragonHashMap.containsKey(Minecraft.getInstance().player.getId())){
				DragonEntity dummyDragon = DSEntities.DRAGON.create(Minecraft.getInstance().player.level);
				dummyDragon.player = Minecraft.getInstance().player.getId();
				ClientDragonRender.playerDragonHashMap.put(Minecraft.getInstance().player.getId(), new AtomicReference<>(dummyDragon));
			}
		}

		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)x, (float)y, 1050.0F);
		RenderSystem.scalef(1.0F, 1.0F, -1.0F);
		MatrixStack matrixstack = new MatrixStack();
		matrixstack.translate(0.0D, 0.0D, 1000.0D);
		matrixstack.scale(scale, scale, scale);
		Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion quaternion1 = Vector3f.XP.rotationDegrees(yRot * 10.0F);
		quaternion.mul(quaternion1);
		matrixstack.mulPose(quaternion);
		matrixstack.translate(xOffset, -1 + yOffset, 0);
		float f2 = entity.yBodyRot;
		float f3 = entity.yRot;
		float f4 = entity.xRot;
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 180.0F + xRot * 10.0F;
		entity.yRot = 180.0F + xRot * 10.0F;
		entity.xRot = -yRot * 10.0F;
		entity.yHeadRot = entity.yRot;
		entity.yHeadRotO = entity.yRot;
		EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		boolean renderHitbox = entityrenderermanager.shouldRenderHitBoxes();
		quaternion1.conj();
		entityrenderermanager.overrideCameraOrientation(quaternion1);
		entityrenderermanager.setRenderShadow(false);
		IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> {
			entityrenderermanager.setRenderHitBoxes(false);
			entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1F, matrixstack, irendertypebuffer$impl, 15728880);
			entityrenderermanager.setRenderHitBoxes(renderHitbox);
		});

		irendertypebuffer$impl.endBatch();
		entityrenderermanager.setRenderShadow(true);

		entity.yBodyRot = f2;
		entity.yRot = f3;
		entity.xRot = f4;
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
		RenderSystem.popMatrix();
	}
}