package by.jackraidenph.dragonsurvival.handlers;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.abilities.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.abilities.common.DragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.config.DragonBodyMovementType;
import by.jackraidenph.dragonsurvival.entity.BolasEntity;
import by.jackraidenph.dragonsurvival.gecko.DragonArmorModel;
import by.jackraidenph.dragonsurvival.gecko.DragonEntity;
import by.jackraidenph.dragonsurvival.gecko.DragonModel;
import by.jackraidenph.dragonsurvival.gecko.DragonRenderer;
import by.jackraidenph.dragonsurvival.mixins.AccessorEntityRenderer;
import by.jackraidenph.dragonsurvival.mixins.AccessorEntityRendererManager;
import by.jackraidenph.dragonsurvival.mixins.AccessorLivingRenderer;
import by.jackraidenph.dragonsurvival.network.*;
import by.jackraidenph.dragonsurvival.registration.ClientModEvents;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import by.jackraidenph.dragonsurvival.registration.ItemsInit;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.core.processor.IBone;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

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
    /**
     * States of digging/breaking blocks
     */
    public static ConcurrentHashMap<Integer, Boolean> dragonsDigging = new ConcurrentHashMap<>(20);
    /**
     * Durations of jumps
     */
    public static ConcurrentHashMap<Integer, Integer> dragonsJumpingTicks = new ConcurrentHashMap<>(20);
    
    private static byte timer = 0;
    private static byte abilityHoldTimer = 0;
    
    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent keyInputEvent) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = Minecraft.getInstance().player;
        
        if (player != null && ClientModEvents.DRAGON_INVENTORY.consumeClick()) {
            if(minecraft.screen == null && DragonStateProvider.isDragon(minecraft.player)){
                DragonSurvivalMod.CHANNEL.sendToServer(new OpenDragonInventory());
            }
        }else  if (player != null && ClientModEvents.USE_ABILITY.consumeClick()) {
            if(minecraft.screen == null && DragonStateProvider.isDragon(minecraft.player)){
                DragonSurvivalMod.CHANNEL.sendToServer(new ActivateAbilityInSlot());
            }
        }else  if (player != null && ClientModEvents.TOGGLE_ABILITIES.consumeClick()) {
            if(DragonStateProvider.isDragon(minecraft.player)){
                DragonStateProvider.getCap(minecraft.player).ifPresent(dragonStateHandler -> {
                    dragonStateHandler.setRenderAbilities(!dragonStateHandler.renderAbilityHotbar());
                    DragonSurvivalMod.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.renderAbilityHotbar()));
                });
            }
        }else  if (player != null && ClientModEvents.NEXT_ABILITY.consumeClick()) {
            if(DragonStateProvider.isDragon(minecraft.player)){
                DragonStateProvider.getCap(minecraft.player).ifPresent(dragonStateHandler -> {
                    int nextSlot = dragonStateHandler.getSelectedAbilitySlot() == 3 ? 0 : dragonStateHandler.getSelectedAbilitySlot() + 1;
                    dragonStateHandler.setSelectedAbilitySlot(nextSlot);
                    DragonSurvivalMod.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.renderAbilityHotbar()));
                });
            }
        }else  if (player != null && ClientModEvents.PREV_ABILITY.consumeClick()) {
            if(DragonStateProvider.isDragon(minecraft.player)){
                DragonStateProvider.getCap(minecraft.player).ifPresent(dragonStateHandler -> {
                    int nextSlot = dragonStateHandler.getSelectedAbilitySlot() == 0 ? 3 : dragonStateHandler.getSelectedAbilitySlot() - 1;
                    dragonStateHandler.setSelectedAbilitySlot(nextSlot);
                    DragonSurvivalMod.CHANNEL.sendToServer(new SyncDragonAbilitySlot(player.getId(), dragonStateHandler.getSelectedAbilitySlot(), dragonStateHandler.renderAbilityHotbar()));
                });
            }
        }
    }
    
    
    @SubscribeEvent
    public static void abilityKeyBindingChecks(TickEvent.ClientTickEvent clientTickEvent) {
        
        if ((Minecraft.getInstance().player == null) ||
            (Minecraft.getInstance().level == null) ||
            (clientTickEvent.phase != TickEvent.Phase.END) ||
            (!DragonStateProvider.isDragon(Minecraft.getInstance().player)))
            return;
        
        PlayerEntity playerEntity = Minecraft.getInstance().player;
        
        abilityHoldTimer = (byte) (ClientModEvents.USE_ABILITY.isDown() ? abilityHoldTimer < 3 ? abilityHoldTimer + 1 : abilityHoldTimer : 0);
        byte modeAbility;
        if (ClientModEvents.USE_ABILITY.isDown() && abilityHoldTimer > 1)
            modeAbility = GLFW.GLFW_REPEAT;
        else if (ClientModEvents.USE_ABILITY.isDown() && abilityHoldTimer == 1)
            modeAbility = GLFW.GLFW_PRESS;
        else
            modeAbility = GLFW.GLFW_RELEASE;
        
        int slot = DragonStateProvider.getCap(playerEntity).map((i) -> i.getSelectedAbilitySlot()).orElse(0);
        timer = (byte) ((modeAbility == GLFW.GLFW_RELEASE) ? timer < 3 ? timer + 1 : timer : 0);
        
        if (timer > 1)
            return;
        
        IMessage message = new ActivateAbilityInSlot(slot, modeAbility);
        DragonSurvivalMod.CHANNEL.sendToServer(message);
        
        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            DragonAbility ability = dragonStateHandler.getAbilityFromSlot(slot);
            if(ability.getLevel() > 0) {
                ActivateAbilityInSlot.runAbility(playerEntity, ability, modeAbility);
            }
        });
    }
    
    @SubscribeEvent
    public static void renderAbilityHud(RenderGameOverlayEvent.Post event) {
        PlayerEntity playerEntity = Minecraft.getInstance().player;
        
        if ((playerEntity == null) || !DragonStateProvider.isDragon(playerEntity))
            return;
        
        DragonStateProvider.getCap(playerEntity).ifPresent(cap -> {
            if(!cap.renderAbilityHotbar()) return;
            
            if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                MainWindow window = Minecraft.getInstance().getWindow();
                
                GL11.glTranslated(0.0d, 0.125d, 0.0d);
                
                int count = 4;
                
                int sizeX = 20;
                int sizeY = 20;
                
                boolean rightSide = true;
                
                int posX = rightSide ? window.getGuiScaledWidth() - (sizeX * count) - 20 : (sizeX * count) + 20;
                int posY = window.getGuiScaledHeight() - (sizeY);
    
                textureManager.bind(new ResourceLocation("textures/gui/widgets.png"));
                Screen.blit(event.getMatrixStack(), posX, posY - 2, 0, 0, 0, 41, 22, 256, 256);
                Screen.blit(event.getMatrixStack(), posX + 41, posY - 2, 0, 141, 0, 41, 22, 256, 256);
                
                for (int x = 0; x < count; x++) {
                    ActiveDragonAbility ability = cap.getAbilityFromSlot(x);
                    
                    if(ability != null && ability.getIcon() != null) {
                        textureManager.bind(ability.getIcon());
                        Screen.blit(event.getMatrixStack(), posX + (x * sizeX) + 3, posY + 1
                                , 1, 0, 0, 16, 16, 16, 16);
                    }
                    
                    //TODO This isnt rendering
                    if(ability.getMaxCooldown() > 0 && ability.getCooldown() > 0 && ability.getMaxCooldown() != ability.getCooldown()){
                        float f = MathHelper.clamp((float)ability.getCooldown() / (float)ability.getMaxCooldown(), 0, 1);
                        int boxX = posX + (x * sizeX) + 3;
                        int boxY = posY + 1;
                        int offset = 16 - (16 - (int)(f * 16));
                        AbstractGui.fill(event.getMatrixStack(), boxX, boxY, boxX + 16, boxY + (offset), new Color(0.15F, 0.15F, 0.15F, 0.75F).getRGB());
                    }
                }
                
                textureManager.bind(new ResourceLocation("textures/gui/widgets.png"));
                Screen.blit(event.getMatrixStack(), posX + (sizeX * cap.getSelectedAbilitySlot()) - 1, window.getGuiScaledHeight() - 23, 2, 0, 22, 24, 24, 256, 256);
    
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
        });
    }
    
    
    @SubscribeEvent
    public static void decreaseJumpDuration(TickEvent.PlayerTickEvent playerTickEvent) {
        if (playerTickEvent.phase == TickEvent.Phase.END) {
            PlayerEntity playerEntity = playerTickEvent.player;
            dragonsJumpingTicks.computeIfPresent(playerEntity.getId(), (playerEntity1, integer) -> integer > 0 ? integer - 1 : integer);
        }
    }

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
                            int colorCode = ((IDyeableArmorItem)bootsItem.getItem()).getColor(bootsItem);
                            ((DragonRenderer)dragonArmorRenderer).renderColor = new Color(colorCode);
                        }

                        ResourceLocation boots = new ResourceLocation(DragonSurvivalMod.MODID, constructArmorTexture(player, EquipmentSlotType.FEET));
                        dragonArmorModel.setArmorTexture(boots);
                        dragonArmorRenderer.render(dragonArmor, playerYaw, partialTicks, eventMatrixStack, buffers, light);
                        ((DragonRenderer)dragonArmorRenderer).renderColor = renderColor;

                        eventMatrixStack.translate(0, 0, 0.15);
                    } catch (Throwable ignored) {
                        if (!(ignored instanceof NullPointerException))
                            ignored.printStackTrace();
                    } finally {
                        eventMatrixStack.popPose();
                    }

                }
            });
        }
    }

    @SubscribeEvent
    public static void addCraftingButton(GuiScreenEvent.InitGuiEvent.Post initGuiEvent)
    {
        Screen screen=initGuiEvent.getGui();
        if(screen instanceof InventoryScreen && DragonStateProvider.isDragon(Minecraft.getInstance().player)) {
            Button openCrafting = new Button(screen.width/2, screen.height-30, 60, 20, new StringTextComponent("Crafting"), p_onPress_1_ -> {
                DragonSurvivalMod.CHANNEL.sendToServer(new OpenCrafting());
            });
            initGuiEvent.addWidget(openCrafting);
        }
    }

    private static Vector3d getInputVector(Vector3d movement, float fricSpeed, float yRot) {
        double d0 = movement.lengthSqr();
        if (d0 < 1.0E-7D) {
            return Vector3d.ZERO;
        } else {
            Vector3d vector3d = (d0 > 1.0D ? movement.normalize() : movement).scale((double) fricSpeed);
            float f = MathHelper.sin(yRot * ((float) Math.PI / 180F));
            float f1 = MathHelper.cos(yRot * ((float) Math.PI / 180F));
            return new Vector3d(vector3d.x * (double) f1 - vector3d.z * (double) f, vector3d.y, vector3d.z * (double) f1 + vector3d.x * (double) f);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent clientTickEvent) {
        if (clientTickEvent.phase == TickEvent.Phase.START) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity player = minecraft.player;
            if (player != null) {
                DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
                    if (playerStateHandler.isDragon()) {
                        float bodyAndHeadYawDiff = (((float) playerStateHandler.getMovementData().bodyYaw) - player.yHeadRot);

                        Vector3d moveVector = getInputVector(new Vector3d(player.input.leftImpulse, 0, player.input.forwardImpulse), 1F, player.yRot);
                        boolean isFlying = false;
                        if (ClientFlightHandler.wingsEnabled && !player.isOnGround() && !player.isInWater() && !player.isInLava()) { // TODO: Remove this when fixing flight system
                            moveVector = new Vector3d(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
                            isFlying = true;
                        }
                        float f = (float) MathHelper.atan2(moveVector.z, moveVector.x) * (180F / (float) Math.PI) - 90F;
                        float f1 = (float) (Math.pow(moveVector.x, 2) + Math.pow(moveVector.z, 2));

                        if (f1 > 0.000028) {
                            if (isFlying || (minecraft.options.getCameraType() != PointOfView.FIRST_PERSON && ConfigHandler.CLIENT.thirdPersonBodyMovement.get() == DragonBodyMovementType.DRAGON) ||
                                    minecraft.options.getCameraType() == PointOfView.FIRST_PERSON && ConfigHandler.CLIENT.firstPersonBodyMovement.get() == DragonBodyMovementType.DRAGON) {
                                float f2 = MathHelper.wrapDegrees(f - (float) playerStateHandler.getMovementData().bodyYaw);
                                playerStateHandler.getMovementData().bodyYaw += 0.5F * f2;
                            } else if ((minecraft.options.getCameraType() != PointOfView.FIRST_PERSON && ConfigHandler.CLIENT.thirdPersonBodyMovement.get() == DragonBodyMovementType.VANILLA) ||
                                    minecraft.options.getCameraType() == PointOfView.FIRST_PERSON && ConfigHandler.CLIENT.firstPersonBodyMovement.get() == DragonBodyMovementType.VANILLA) {

                                float f5 = MathHelper.abs(MathHelper.wrapDegrees(player.yRot) - f);
                                if (95.0F < f5 && f5 < 265.0F) {
                                    f -= 180.0F;
                                }

                                float _f = MathHelper.wrapDegrees(f - (float) playerStateHandler.getMovementData().bodyYaw);
                                playerStateHandler.getMovementData().bodyYaw += _f * 0.3F;
                                float _f1 = MathHelper.wrapDegrees(player.yRot - (float) playerStateHandler.getMovementData().bodyYaw);
                                boolean flag = _f1 < -90.0F || _f1 >= 90.0F;

                                if (_f1 < -75.0F) {
                                    _f1 = -75.0F;
                                }

                                if (_f1 >= 75.0F) {
                                    _f1 = 75.0F;
                                }

                                playerStateHandler.getMovementData().bodyYaw = player.yRot - _f1;
                                if (_f1 * _f1 > 2500.0F) {
                                    playerStateHandler.getMovementData().bodyYaw += _f1 * 0.2F;
                                }
                            }
                            if (bodyAndHeadYawDiff > 180)
                                playerStateHandler.getMovementData().bodyYaw -= 360;
                            if (bodyAndHeadYawDiff <= -180)
                                playerStateHandler.getMovementData().bodyYaw += 360;
                            playerStateHandler.setMovementData(playerStateHandler.getMovementData().bodyYaw, player.yHeadRot, player.xRot, player.swinging && player.getAttackStrengthScale(-3.0f) != 1);
                            DragonSurvivalMod.CHANNEL.send(PacketDistributor.SERVER.noArg(), new PacketSyncCapabilityMovement(player.getId(), playerStateHandler.getMovementData().bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
                        } else if (Math.abs(bodyAndHeadYawDiff) > 180F) {
                            if (Math.abs(bodyAndHeadYawDiff) > 360F)
                                playerStateHandler.getMovementData().bodyYaw -= bodyAndHeadYawDiff;
                            else {
                                float turnSpeed = Math.min(1F + (float) Math.pow(Math.abs(bodyAndHeadYawDiff) - 180F, 1.5F) / 30F, 50F);
                                playerStateHandler.setMovementData((float) playerStateHandler.getMovementData().bodyYaw - Math.signum(bodyAndHeadYawDiff) * turnSpeed, player.yHeadRot, player.xRot, player.swinging && player.getAttackStrengthScale(-3.0f) != 1);
                            }
                            DragonSurvivalMod.CHANNEL.send(PacketDistributor.SERVER.noArg(), new PacketSyncCapabilityMovement(player.getId(), playerStateHandler.getMovementData().bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
                        } else if (playerStateHandler.getMovementData().bite != (player.swinging && player.getAttackStrengthScale(-3.0f) != 1) || player.yHeadRot != playerStateHandler.getMovementData().headYaw) {
                            playerStateHandler.setMovementData(playerStateHandler.getMovementData().bodyYaw, player.yHeadRot, player.xRot, player.swinging && player.getAttackStrengthScale(-3.0f) != 1);
                            DragonSurvivalMod.CHANNEL.send(PacketDistributor.SERVER.noArg(), new PacketSyncCapabilityMovement(player.getId(), playerStateHandler.getMovementData().bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
                        }
                    }
                });
            }
        }
    }

    private static ItemStack BOLAS;
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
                            renderBolas(eventLight, combinedOverlayIn, renderTypeBuffer, matrixStack);
                        }
                        ItemStack right = player.getMainHandItem();
                        matrixStack.pushPose();
                        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
                        matrixStack.translate(0.5f, 1, -0.8);
                        itemRenderer.renderStatic(right, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, eventLight, combinedOverlayIn, matrixStack, renderTypeBuffer);
                        matrixStack.popPose();
                        matrixStack.pushPose();
                        ItemStack left = player.getOffhandItem();
                        matrixStack.translate(0.25, 1, 0.4);
                        itemRenderer.renderStatic(left, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, eventLight, combinedOverlayIn, matrixStack, renderTypeBuffer);
                        matrixStack.popPose();
                    }
                } catch (Throwable throwable) {
                    if (!(throwable instanceof NullPointerException))
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

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void removeLavaAndWaterFog(EntityViewRenderEvent.FogDensity event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        DragonStateProvider.getCap(player).ifPresent(cap -> {
            if(!cap.isDragon()) return;
            
            if (cap.getType() == DragonType.CAVE && event.getInfo().getFluidInCamera().is(FluidTags.LAVA)) {
                if(player.hasEffect(DragonEffects.LAVA_VISION)) {
                    event.setDensity(0.01F);
                    event.setCanceled(true);
                }
            }else if (cap.getType() == DragonType.SEA && event.getInfo().getFluidInCamera().is(FluidTags.WATER)) {
                if(player.hasEffect(DragonEffects.WATER_VISION)) {
                    event.setDensity(event.getDensity() / 10);
                    event.setCanceled(true);
                }
            }
        });
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void removeFireOverlay(RenderBlockOverlayEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        DragonStateProvider.getCap(player).ifPresent(cap -> {
            if (cap.isDragon() && cap.getType() == DragonType.CAVE && event.getOverlayType() == OverlayType.FIRE)
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void renderTrap(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> postEvent) {
        LivingEntity entity = postEvent.getEntity();
        if (!(entity instanceof PlayerEntity) && entity.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
            AttributeModifier bolasTrap = new AttributeModifier(BolasEntity.DISABLE_MOVEMENT, "Bolas trap", -entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue(), AttributeModifier.Operation.ADDITION);
            if (entity.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(bolasTrap)) {
                int light = postEvent.getLight();
                int overlayCoords = LivingRenderer.getOverlayCoords(entity, 0);
                IRenderTypeBuffer buffers = postEvent.getBuffers();
                MatrixStack matrixStack = postEvent.getMatrixStack();
                renderBolas(light, overlayCoords, buffers, matrixStack);
            }
        }
    }

    private static void renderBolas(int light, int overlayCoords, IRenderTypeBuffer buffers, MatrixStack matrixStack) {
        matrixStack.pushPose();
        matrixStack.scale(3, 3, 3);
        matrixStack.translate(0, 0.5, 0);
        if (BOLAS == null)
            BOLAS = new ItemStack(ItemsInit.huntingNet);
        Minecraft.getInstance().getItemRenderer().renderStatic(BOLAS, ItemCameraTransforms.TransformType.NONE, light, overlayCoords, matrixStack, buffers);
        matrixStack.popPose();
    }

    @SubscribeEvent
    public static void checkIfDragonFood(ItemTooltipEvent tooltipEvent) {
        if (tooltipEvent.getPlayer() != null) {
            Item item = tooltipEvent.getItemStack().getItem();
            List<ITextComponent> toolTip = tooltipEvent.getToolTip();
            if (DragonFoodHandler.getSafeEdibleFoods(DragonType.CAVE).contains(item)) {
                toolTip.add(new TranslationTextComponent("ds.cave.dragon.food"));
            }
            if (DragonFoodHandler.getSafeEdibleFoods(DragonType.FOREST).contains(item)) {
                toolTip.add(new TranslationTextComponent("ds.forest.dragon.food"));
            }
            if (DragonFoodHandler.getSafeEdibleFoods(DragonType.SEA).contains(item)) {
                toolTip.add(new TranslationTextComponent("ds.sea.dragon.food"));
            }
        }
    }
}
