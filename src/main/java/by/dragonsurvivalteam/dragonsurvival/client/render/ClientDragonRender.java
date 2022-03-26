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
import by.dragonsurvivalteam.dragonsurvival.common.entity.Dragon;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.mixins.AccessorEntityRenderer;
import by.dragonsurvivalteam.dragonsurvival.mixins.AccessorEntityRendererManager;
import by.dragonsurvivalteam.dragonsurvival.mixins.AccessorLivingEntityRenderer;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.PacketSyncCapabilityMovement;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
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
	public static Dragon dragonArmor;
	public static Dragon dummyDragon;

	/**
	 * Instances used for rendering third-person dragon models
	 */
	public static ConcurrentHashMap<Integer, AtomicReference<Dragon>> playerDragonHashMap = new ConcurrentHashMap<>(20);
	private static boolean wasFreeLook = false;

	@SubscribeEvent
	public static void renderFirstPerson(RenderHandEvent renderHandEvent){
		if(ConfigHandler.CLIENT.renderInFirstPerson.get()){
			LocalPlayer player = Minecraft.getInstance().player;
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
		if(!(renderPlayerEvent.getPlayer() instanceof LocalPlayer)){
			return;
		}

		LocalPlayer player = (LocalPlayer)renderPlayerEvent.getPlayer();
		Minecraft mc = Minecraft.getInstance();

		if(!playerDragonHashMap.containsKey(player.getId())){
			Dragon dummyDragon = DSEntities.DRAGON.create(player.level);
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
			final float partialRenderTick = renderPlayerEvent.getPartialTick();
			final float yaw = player.getViewYRot(partialRenderTick);

			DragonLevel dragonStage = cap.getLevel();
			ResourceLocation texture = DragonSkins.getPlayerSkin(player, cap.getType(), dragonStage);
			PoseStack matrixStack = renderPlayerEvent.getPoseStack();

			try{
				matrixStack.pushPose();

				Vector3f lookVector = DragonUtils.getCameraOffset(player);
				matrixStack.translate(-lookVector.x(), lookVector.y(), -lookVector.z());

				double size = cap.getSize() * cap.getSkin().skinPreset.sizeMul;
				// This is some arbitrary scaling that was created back when the maximum size was hard capped at 40. Touching it will cause the render to desync from the hitbox.
				float scale = (float)Math.max(size / 40.0D, 0.4D);
				String playerModelType = player.getModelName();
				EntityRenderer playerRenderer = ((AccessorEntityRendererManager)mc.getEntityRenderDispatcher()).getPlayerRenderers().get(playerModelType);
				int eventLight = renderPlayerEvent.getPackedLight();
				final MultiBufferSource renderTypeBuffer = renderPlayerEvent.getMultiBufferSource();
				if(ConfigHandler.CLIENT.dragonNameTags.get()){
					net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(player, player.getDisplayName(), playerRenderer, matrixStack, renderTypeBuffer, eventLight, partialRenderTick);
					net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
					if(renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || ((AccessorLivingEntityRenderer)playerRenderer).callShouldShowName(player))){
						((AccessorEntityRenderer)playerRenderer).callRenderNameTag(player, renderNameplateEvent.getContent(), matrixStack, renderTypeBuffer, eventLight);
					}
				}

				matrixStack.mulPose(Vector3f.YN.rotationDegrees((float)cap.getMovementData().bodyYaw));
				matrixStack.scale(scale, scale, scale);
				((AccessorEntityRenderer)renderPlayerEvent.getRenderer()).setShadowRadius((float)((3.0F * size + 62.0F) / 260.0F));
				Dragon dummyDragon = playerDragonHashMap.get(player.getId()).get();

				EntityRenderer<? super Dragon> dragonRenderer = mc.getEntityRenderDispatcher().getRenderer(dummyDragon);
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
							float upRot = Mth.clamp((float)(player.getDeltaMovement().y * 20), -80, 80);

							dummyDragon.prevXRot = Mth.lerp(0.1F, dummyDragon.prevXRot, upRot);
							dummyDragon.prevXRot = Mth.clamp(dummyDragon.prevXRot, -80, 80);

							if(Float.isNaN(dummyDragon.prevXRot)){
								dummyDragon.prevXRot = upRot;
							}

							if(Float.isNaN(dummyDragon.prevXRot)){
								dummyDragon.prevXRot = 0;
							}

							matrixStack.mulPose(Vector3f.XN.rotationDegrees(dummyDragon.prevXRot));

							Vec3 vector3d1 = player.getDeltaMovement();
							Vec3 vector3d = player.getViewVector(1f);
							double d0 = vector3d1.horizontalDistanceSqr();
							double d1 = vector3d.horizontalDistanceSqr();
							double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
							double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;

							float rot = Mth.clamp(((float)(Math.signum(d3) * Math.acos(d2))) * 2, -1, 1);

							dummyDragon.prevZRot = Mth.lerp(0.1F, dummyDragon.prevZRot, rot);
							dummyDragon.prevZRot = Mth.clamp(dummyDragon.prevZRot, -1, 1);

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
							ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
							ItemStack chestPlate = player.getItemBySlot(EquipmentSlot.CHEST);
							ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
							ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

							ResourceLocation helmetTexture = new ResourceLocation(DragonSurvivalMod.MODID, DragonArmorRenderLayer.constructArmorTexture(player, EquipmentSlot.HEAD));
							ResourceLocation chestPlateTexture = new ResourceLocation(DragonSurvivalMod.MODID, DragonArmorRenderLayer.constructArmorTexture(player, EquipmentSlot.CHEST));
							ResourceLocation legsTexture = new ResourceLocation(DragonSurvivalMod.MODID, DragonArmorRenderLayer.constructArmorTexture(player, EquipmentSlot.LEGS));
							ResourceLocation bootsTexture = new ResourceLocation(DragonSurvivalMod.MODID, DragonArmorRenderLayer.constructArmorTexture(player, EquipmentSlot.FEET));

							renderArmorPiece(helmet, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, helmetTexture);
							renderArmorPiece(chestPlate, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, chestPlateTexture);
							renderArmorPiece(legs, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, legsTexture);
							renderArmorPiece(boots, matrixStack, renderTypeBuffer, yaw, eventLight, dummyDragon, partialRenderTick, bootsTexture);
						}
					}
				}

				if(!player.isSpectator()){
					for(LayerRenderer<Entity, EntityModel<Entity>> layer : ((AccessorLivingEntityRenderer)playerRenderer).getRenderLayers()){
						if(layer instanceof ParrotVariantLayer){
							matrixStack.scale(1.0F / scale, 1.0F / scale, 1.0F / scale);
							matrixStack.mulPose(Vector3f.XN.rotationDegrees(180.0F));
							double height = 1.3 * scale;
							double forward = 0.3 * scale;
							float parrotHeadYaw = Mth.clamp(-1.0F * (((float)cap.getMovementData().bodyYaw) - (float)cap.getMovementData().headYaw), -75.0F, 75.0F);
							matrixStack.translate(0, -height, -forward);
							layer.render(matrixStack, renderTypeBuffer, eventLight, player, 0.0F, 0.0F, partialRenderTick, (float)player.tickCount + partialRenderTick, parrotHeadYaw, (float)cap.getMovementData().headPitch);
							matrixStack.translate(0, height, forward);
							matrixStack.mulPose(Vector3f.XN.rotationDegrees(-180.0F));
							matrixStack.scale(scale, scale, scale);
							break;
						}
					}

					final int combinedOverlayIn = LivingEntityRenderer.getOverlayCoords(player, 0);
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

	private static void renderArmorPiece(ItemStack stack, PoseStack matrixStackIn, MultiBufferSource bufferIn, float yaw, int packedLightIn, Dragon entitylivingbaseIn, float partialTicks, ResourceLocation helmetTexture){
		Color armorColor = new Color(1f, 1f, 1f);

		if(stack.getItem() instanceof IDyeableArmorItem){
			int colorCode = ((IDyeableArmorItem)stack.getItem()).getColor(stack);
			armorColor = new Color(colorCode);
		}

		if(!stack.isEmpty()){
			EntityRenderer<? super Dragon> dragonArmorRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(ClientDragonRender.dragonArmor);
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
		LocalPlayer player = Minecraft.getInstance().player;
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
			LocalPlayer player = minecraft.player;
			if(player != null){
				DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
					if(playerStateHandler.isDragon()){
						playerStateHandler.getMovementData().headYawLastTick = Mth.lerp(0.05, playerStateHandler.getMovementData().headYawLastTick, playerStateHandler.getMovementData().headYaw);
						playerStateHandler.getMovementData().headPitchLastTick = Mth.lerp(0.05, playerStateHandler.getMovementData().headPitchLastTick, playerStateHandler.getMovementData().headPitch);
						playerStateHandler.getMovementData().bodyYawLastTick = Mth.lerp(0.05, playerStateHandler.getMovementData().bodyYawLastTick, playerStateHandler.getMovementData().bodyYaw);

						double bodyYaw = playerStateHandler.getMovementData().bodyYaw;
						float headRot = Functions.angleDifference((float)bodyYaw, Mth.wrapDegrees(player.yRot != 0.0 ? player.yRot : player.yHeadRot));

						if(ConfigHandler.CLIENT.rotateBodyWithCamera.get() && (!KeyInputHandler.FREE_LOOK.isDown() && !wasFreeLook)){
							if(headRot > 150){
								bodyYaw += 150 - headRot;
							}else if(headRot < -150){
								bodyYaw -= 150 + headRot;
							}
						}
						headRot = (float)Mth.lerp(0.05, playerStateHandler.getMovementData().headYaw, headRot);


						double headPitch = Mth.lerp(0.1, playerStateHandler.getMovementData().headPitch, player.xRot);
						Vec3 moveVector = getInputVector(new Vec3(player.input.leftImpulse, 0, player.input.forwardImpulse), 1F, player.yRot);

						if(ServerFlightHandler.isFlying(player)){
							moveVector = new Vec3(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
						}

						float f = (float)Mth.atan2(moveVector.z, moveVector.x) * (180F / (float)Math.PI) - 90F;
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
									float f5 = Mth.abs(Mth.wrapDegrees(player.yRot) - f);
									if(95.0F < f5 && f5 < 265.0F){
										f -= 180.0F;
									}

									float _f = Mth.wrapDegrees(f - (float)bodyYaw);
									bodyYaw += _f * 0.3F;
									float _f1 = Mth.wrapDegrees(player.yRot - (float)bodyYaw);

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
									bodyYaw = Mth.rotLerp(0.1f, (float)playerStateHandler.getMovementData().bodyYaw, (float)bodyYaw);
									bodyYaw = Mth.wrapDegrees(bodyYaw);

									playerStateHandler.setMovementData(bodyYaw, headRot, headPitch, playerStateHandler.getMovementData().bite);
									NetworkHandler.CHANNEL.sendToServer(new PacketSyncCapabilityMovement(player.getId(), playerStateHandler.getMovementData().bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
									return;
								}
							}
						}


						if(f1 > 0.000028){
							float f2 = Mth.wrapDegrees(f - (float)bodyYaw);
							bodyYaw += 0.5F * f2;

							if(minecraft.options.getCameraType() == PointOfView.FIRST_PERSON){
								float f5 = Mth.abs(Mth.wrapDegrees(player.yRot) - f);
								if(95.0F < f5 && f5 < 265.0F){
									f -= 180.0F;
								}

								float _f = Mth.wrapDegrees(f - (float)bodyYaw);
								bodyYaw += _f * 0.3F;
								float _f1 = Mth.wrapDegrees(player.yRot - (float)bodyYaw);

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
							bodyYaw = Mth.rotLerp(0.1f, (float)playerStateHandler.getMovementData().bodyYaw, (float)bodyYaw);
							bodyYaw = Mth.wrapDegrees(bodyYaw);

							playerStateHandler.setMovementData(bodyYaw, headRot, headPitch, playerStateHandler.getMovementData().bite);
							NetworkHandler.CHANNEL.sendToServer(new PacketSyncCapabilityMovement(player.getId(), playerStateHandler.getMovementData().bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
						}
					}
				});
			}
		}
	}

	public static Vec3 getInputVector(Vec3 movement, float fricSpeed, float yRot){
		double d0 = movement.lengthSqr();
		if(d0 < 1.0E-7D){
			return Vec3.ZERO;
		}else{
			Vec3 vector3d = (d0 > 1.0D ? movement.normalize() : movement).scale(fricSpeed);
			float f = Mth.sin(yRot * ((float)Math.PI / 180F));
			float f1 = Mth.cos(yRot * ((float)Math.PI / 180F));
			return new Vec3(vector3d.x * (double)f1 - vector3d.z * (double)f, vector3d.y, vector3d.z * (double)f1 + vector3d.x * (double)f);
		}
	}

	public static void renderEntityInInventory(LivingEntity entity, int x, int y, float scale, float xRot, float yRot){
		renderEntityInInventory(entity, x, y, scale, xRot, yRot, 0, 0);
	}

	public static void renderEntityInInventory(LivingEntity entity, int x, int y, float scale, float xRot, float yRot, float xOffset, float yOffset){
		if(entity == null){
			return;
		}

		if(entity instanceof Dragon){
			if(ClientDragonRender.dragonArmor == null){
				ClientDragonRender.dragonArmor = DSEntities.DRAGON_ARMOR.create(Minecraft.getInstance().player.level);
				assert ClientDragonRender.dragonArmor != null;
				ClientDragonRender.dragonArmor.player = Minecraft.getInstance().player.getId();
			}

			if(!ClientDragonRender.playerDragonHashMap.containsKey(Minecraft.getInstance().player.getId())){
				Dragon dummyDragon = DSEntities.DRAGON.create(Minecraft.getInstance().player.level);
				dummyDragon.player = Minecraft.getInstance().player.getId();
				ClientDragonRender.playerDragonHashMap.put(Minecraft.getInstance().player.getId(), new AtomicReference<>(dummyDragon));
			}
		}

		RenderSystem.pushMatrix();
		RenderSystem.translatef((float)x, (float)y, 1050.0F);
		RenderSystem.scalef(1.0F, 1.0F, -1.0F);
		PoseStack matrixstack = new PoseStack();
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
		MultiBufferSource.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
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