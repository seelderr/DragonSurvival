package by.jackraidenph.dragonsurvival.handlers.Client;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.gecko.model.DragonArmorModel;
import by.jackraidenph.dragonsurvival.gecko.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.gecko.model.DragonModel;
import by.jackraidenph.dragonsurvival.gecko.renderer.DragonRenderer;
import by.jackraidenph.dragonsurvival.mixins.AccessorEntityRenderer;
import by.jackraidenph.dragonsurvival.mixins.AccessorEntityRendererManager;
import by.jackraidenph.dragonsurvival.mixins.AccessorLivingRenderer;
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
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib3.core.processor.IBone;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
@Mod.EventBusSubscriber( Dist.CLIENT)
public class ClientDragonRender
{
	public static DragonModel dragonModel;
	public static DragonArmorModel dragonArmorModel;
	/**
	 * First-person armor instance
	 */
	public static DragonEntity dragonArmor;
	/**
	 * Third-person armor instances
	 */
	public static ConcurrentHashMap<Integer, DragonEntity> playerArmorMap = new ConcurrentHashMap<>(20);
	/**
	 * Instance used for rendering first-person dragon model
	 */
	public static AtomicReference<DragonEntity> dragonEntity;
	/**
	 * Instances used for rendering third-person dragon models
	 */
	public static ConcurrentHashMap<Integer, AtomicReference<DragonEntity>> playerDragonHashMap = new ConcurrentHashMap<>(20);
	public static ConcurrentHashMap<Integer, Boolean> dragonsFlying = new ConcurrentHashMap<>(20);
	
	@SubscribeEvent
	public static void renderFirstPerson(RenderHandEvent renderHandEvent) {
	    if (ConfigHandler.CLIENT.renderInFirstPerson.get()) {
	        ClientPlayerEntity player = Minecraft.getInstance().player;
	        if (dragonEntity == null) {
	            dragonEntity = new AtomicReference<>(EntityTypesInit.DRAGON.create(player.level));
	            dragonEntity.get().player = player.getId();
	        }
	        if (dragonArmor == null) {
	            dragonArmor = EntityTypesInit.DRAGON_ARMOR.create(player.level);
	            assert dragonArmor != null;
	            dragonArmor.player = player.getId();
	        }
	        DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
	            if (playerStateHandler.isDragon()) {
	                MatrixStack eventMatrixStack = renderHandEvent.getMatrixStack();
	                try {
	                    eventMatrixStack.pushPose();
	                    float partialTicks = renderHandEvent.getPartialTicks();
	                    float playerYaw = player.getViewYRot(partialTicks);
	                    ResourceLocation texture = DragonSkins.getPlayerSkin(player, playerStateHandler.getType(), playerStateHandler.getLevel());
	                    eventMatrixStack.mulPose(Vector3f.XP.rotationDegrees(player.xRot));
	                    eventMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
	                    eventMatrixStack.mulPose(Vector3f.YP.rotationDegrees(player.yRot));
	                    eventMatrixStack.mulPose(Vector3f.YN.rotationDegrees((float) playerStateHandler.getMovementData().bodyYaw));
	                    eventMatrixStack.translate(0, -2, -1);
	                    IRenderTypeBuffer buffers = renderHandEvent.getBuffers();
	                    int light = renderHandEvent.getLight();
	
	                    EntityRenderer<? super DragonEntity> dragonRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dragonEntity.get());
	                    dragonEntity.get().copyPosition(player);
	                    dragonModel.setCurrentTexture(texture);
	
	                    ((DragonRenderer)dragonRenderer).glowTexture = DragonSkins.getGlowTexture(player, playerStateHandler.getType(), playerStateHandler.getLevel());
	
	                    final IBone neckandHead = dragonModel.getAnimationProcessor().getBone("Neck");
	                    if (neckandHead != null)
	                        neckandHead.setHidden(true);
	                    final IBone leftwing = dragonModel.getAnimationProcessor().getBone("WingLeft");
	                    final IBone rightWing = dragonModel.getAnimationProcessor().getBone("WingRight");
	                    if (leftwing != null)
	                        leftwing.setHidden(!playerStateHandler.hasWings());
	                    if (rightWing != null)
	                        rightWing.setHidden(!playerStateHandler.hasWings());
	                    if (!player.isInvisible())
	                        dragonRenderer.render(dragonEntity.get(), playerYaw, partialTicks, eventMatrixStack, buffers, light);
	
	                    EntityRenderer<? super DragonEntity> dragonArmorRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dragonArmor);
	                    dragonArmor.copyPosition(player);
	                    final IBone neck = dragonArmorModel.getAnimationProcessor().getBone("Neck");
	                    if (neck != null)
	                        neck.setHidden(true);
	
	                    ItemStack chestPlateItem = player.getItemBySlot(EquipmentSlotType.CHEST);
	                    ItemStack legsItem = player.getItemBySlot(EquipmentSlotType.LEGS);
	                    ItemStack bootsItem = player.getItemBySlot(EquipmentSlotType.FEET);
	
	                    Color renderColor = ((DragonRenderer)dragonArmorRenderer).renderColor;
	
	                    if(chestPlateItem.getItem() instanceof IDyeableArmorItem){
	                        int colorCode = ((IDyeableArmorItem)chestPlateItem.getItem()).getColor(chestPlateItem);
	                        ((DragonRenderer)dragonArmorRenderer).renderColor = new Color(colorCode);
	                    }
	
	                    ResourceLocation chestplate = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.CHEST));
	
	                    dragonArmorModel.setArmorTexture(chestplate);
	                    dragonArmorRenderer.render(dragonArmor, playerYaw, partialTicks, eventMatrixStack, buffers, light);
	                    ((DragonRenderer)dragonArmorRenderer).renderColor = renderColor;
	
	                    if(legsItem.getItem() instanceof IDyeableArmorItem){
	                        int colorCode = ((IDyeableArmorItem)legsItem.getItem()).getColor(legsItem);
	                        ((DragonRenderer)dragonArmorRenderer).renderColor = new Color(colorCode);
	                    }
	
	                    ResourceLocation legs = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.LEGS));
	
	                    dragonArmorModel.setArmorTexture(legs);
	                    dragonArmorRenderer.render(dragonArmor, playerYaw, partialTicks, eventMatrixStack, buffers, light);
	                    ((DragonRenderer)dragonArmorRenderer).renderColor = renderColor;
	
	                    if(bootsItem.getItem() instanceof IDyeableArmorItem){
	                        int colorCode = ((IDyeableArmorItem) bootsItem.getItem()).getColor(bootsItem);
	                        ((DragonRenderer) dragonArmorRenderer).renderColor = new Color(colorCode);
	                    }
	
	                    ResourceLocation boots = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.FEET));
	                    dragonArmorModel.setArmorTexture(boots);
	                    dragonArmorRenderer.render(dragonArmor, playerYaw, partialTicks, eventMatrixStack, buffers, light);
	                    ((DragonRenderer) dragonArmorRenderer).renderColor = renderColor;
	
	                    eventMatrixStack.translate(0, 0, 0.15);
	                } catch (Throwable throwable) {
	                    if (!(throwable instanceof NullPointerException) || ConfigHandler.CLIENT.clientDebugMessages.get())
	                        throwable.printStackTrace();
	                    eventMatrixStack.popPose();
	                } finally {
	                    eventMatrixStack.popPose();
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
	    if (!playerArmorMap.containsKey(player.getId())) {
	        DragonEntity dragonEntity = EntityTypesInit.DRAGON_ARMOR.create(player.level);
	        dragonEntity.player = player.getId();
	        playerArmorMap.put(player.getId(), dragonEntity);
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
	                float size = cap.getSize();
	                float scale = Math.max(size / 40, DragonLevel.BABY.maxWidth);
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
	                ((AccessorEntityRenderer) renderPlayerEvent.getRenderer()).setShadowRadius((3.0F * size + 62.0F) / 260.0F);
	                DragonEntity dummyDragon = playerDragonHashMap.get(player.getId()).get();
	
	                EntityRenderer<? super DragonEntity> dragonRenderer = mc.getEntityRenderDispatcher().getRenderer(dummyDragon);
	                dummyDragon.copyPosition(player);
	                dragonModel.setCurrentTexture(texture);
	
	                ((DragonRenderer)dragonRenderer).glowTexture = DragonSkins.getGlowTexture(player, cap.getType(), dragonStage);
	
	                final IBone leftwing = dragonModel.getAnimationProcessor().getBone("WingLeft");
	                final IBone rightWing = dragonModel.getAnimationProcessor().getBone("WingRight");
	                if (leftwing != null)
	                    leftwing.setHidden(!cap.hasWings());
	                if (rightWing != null)
	                    rightWing.setHidden(!cap.hasWings());
	                IBone neckHead = dragonModel.getAnimationProcessor().getBone("Neck");
	                if (neckHead != null)
	                    neckHead.setHidden(false);
	                if (player.isCrouching()) {
	                    switch (dragonStage) {
	                        case ADULT:
	                            matrixStack.translate(0, 0.125, 0);
	                            break;
	                        case YOUNG:
	                            matrixStack.translate(0, 0.25, 0);
	                            break;
	                        case BABY:
	                            matrixStack.translate(0, 0.325, 0);
	                    }
	                } else if (player.isSwimming() || player.isAutoSpinAttack() || (dragonsFlying.getOrDefault(player.getId(), false) && !player.isOnGround() && !player.isInWater() && !player.isInLava())) { // FIXME yea this too, I just copied what was up there to shift the model for swimming but I swear this should be done differently...
	                    switch (dragonStage) {
	                        case ADULT:
	                            matrixStack.translate(0, -0.35, 0);
	                            break;
	                        case YOUNG:
	                            matrixStack.translate(0, -0.25, 0);
	                            break;
	                        case BABY:
	                            matrixStack.translate(0, -0.15, 0);
	                    }
	                }
	                if (!player.isInvisible()) {
	                    dragonRenderer.render(dummyDragon, yaw, partialRenderTick, matrixStack, renderTypeBuffer, eventLight);
	                }
	
	                if (!player.isSpectator()) {
	                    String helmetTexture = constructArmorTexture(player, EquipmentSlotType.HEAD);
	                    String chestPlateTexture = constructArmorTexture(player, EquipmentSlotType.CHEST);
	                    String legsTexture = constructArmorTexture(player, EquipmentSlotType.LEGS);
	                    String bootsTexture = constructArmorTexture(player, EquipmentSlotType.FEET);
	
	                    ItemStack helmet = player.getItemBySlot(EquipmentSlotType.HEAD);
	                    ItemStack chestPlate = player.getItemBySlot(EquipmentSlotType.CHEST);
	                    ItemStack legs = player.getItemBySlot(EquipmentSlotType.LEGS);
	                    ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);
	
	
	                    DragonEntity dragonArmor = playerArmorMap.get(player.getId());
	                    dragonArmor.copyPosition(player);
	                    final IBone neck = dragonArmorModel.getAnimationProcessor().getBone("Neck");
	                    if (neck != null)
	                        neck.setHidden(false);
	                    EntityRenderer<? super DragonEntity> dragonArmorRenderer = mc.getEntityRenderDispatcher().getRenderer(dragonArmor);
	
	                    Color renderColor = ((DragonRenderer)dragonArmorRenderer).renderColor;
	
	                    if(helmet.getItem() instanceof IDyeableArmorItem){
	                        int colorCode = ((IDyeableArmorItem)helmet.getItem()).getColor(helmet);
	                        ((DragonRenderer)dragonArmorRenderer).renderColor = new Color(colorCode);
	                    }
	
	                    dragonArmorModel.setArmorTexture(new ResourceLocation(DragonSurvivalMod.MODID, helmetTexture));
	                    dragonArmorRenderer.render(dummyDragon, yaw, partialRenderTick, matrixStack, renderTypeBuffer, eventLight);
	                    ((DragonRenderer)dragonArmorRenderer).renderColor = renderColor;
	
	                    if(chestPlate.getItem() instanceof IDyeableArmorItem){
	                        int colorCode = ((IDyeableArmorItem)chestPlate.getItem()).getColor(chestPlate);
	                        ((DragonRenderer)dragonArmorRenderer).renderColor = new Color(colorCode);
	                    }
	
	                    dragonArmorModel.setArmorTexture(new ResourceLocation(DragonSurvivalMod.MODID, chestPlateTexture));
	                    dragonArmorRenderer.render(dummyDragon, yaw, partialRenderTick, matrixStack, renderTypeBuffer, eventLight);
	                    ((DragonRenderer)dragonArmorRenderer).renderColor = renderColor;
	
	                    if(legs.getItem() instanceof IDyeableArmorItem){
	                        int colorCode = ((IDyeableArmorItem)legs.getItem()).getColor(legs);
	                        ((DragonRenderer)dragonArmorRenderer).renderColor = new Color(colorCode);
	                    }
	                    dragonArmorModel.setArmorTexture(new ResourceLocation(DragonSurvivalMod.MODID, legsTexture));
	                    dragonArmorRenderer.render(dummyDragon, yaw, partialRenderTick, matrixStack, renderTypeBuffer, eventLight);
	                    ((DragonRenderer)dragonArmorRenderer).renderColor = renderColor;
	
	                    if(boots.getItem() instanceof IDyeableArmorItem){
	                        int colorCode = ((IDyeableArmorItem)boots.getItem()).getColor(boots);
	                        ((DragonRenderer)dragonArmorRenderer).renderColor = new Color(colorCode);
	                    }
	                    dragonArmorModel.setArmorTexture(new ResourceLocation(DragonSurvivalMod.MODID, bootsTexture));
	                    dragonArmorRenderer.render(dummyDragon, yaw, partialRenderTick, matrixStack, renderTypeBuffer, eventLight);
	                    ((DragonRenderer)dragonArmorRenderer).renderColor = renderColor;
						
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
