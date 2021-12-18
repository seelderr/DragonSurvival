package by.jackraidenph.dragonsurvival.handlers.ClientSide;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.config.DragonBodyMovementType;
import by.jackraidenph.dragonsurvival.entity.BolasEntity;
import by.jackraidenph.dragonsurvival.gui.DragonScreen;
import by.jackraidenph.dragonsurvival.gui.buttons.TabButton;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.PacketSyncCapabilityMovement;
import by.jackraidenph.dragonsurvival.network.magic.OpenDragonInventory;
import by.jackraidenph.dragonsurvival.registration.ItemsInit;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    
    /**
     * Durations of jumps
     */
    public static ConcurrentHashMap<Integer, Integer> dragonsJumpingTicks = new ConcurrentHashMap<>(20);
    
    @SubscribeEvent
    public static void decreaseJumpDuration(TickEvent.PlayerTickEvent playerTickEvent) {
        if (playerTickEvent.phase == TickEvent.Phase.END) {
            PlayerEntity playerEntity = playerTickEvent.player;
            dragonsJumpingTicks.computeIfPresent(playerEntity.getId(), (playerEntity1, integer) -> integer > 0 ? integer - 1 : integer);
        }
    }
    
    @SubscribeEvent
    public static void onOpenScreen(GuiOpenEvent openEvent) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
    
        if(!ConfigHandler.CLIENT.dragonInventory.get()) return;
        if(Minecraft.getInstance().screen != null) return;
        if(player == null || player.isCreative() || !DragonStateProvider.isDragon(player)) return;
        
        if (openEvent.getGui() instanceof InventoryScreen) {
            openEvent.setCanceled(true);
            NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
        }
    }
    
    @SubscribeEvent
    public static void addCraftingButton(GuiScreenEvent.InitGuiEvent.Post initGuiEvent)
    {
        Screen sc = initGuiEvent.getGui();
        
        if(! DragonStateProvider.isDragon(Minecraft.getInstance().player)) return;
        
        if(sc instanceof InventoryScreen) {
            InventoryScreen screen = (InventoryScreen)sc;
    
            if(ConfigHandler.CLIENT.dragonTabs.get()) {
                initGuiEvent.addWidget(new TabButton(screen.getGuiLeft(), screen.getGuiTop() - 28, 0, screen)
                {
                    @Override
                    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_)
                    {
                        super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
                        this.x = screen.getGuiLeft();
                    }
                });
                
                initGuiEvent.addWidget(new TabButton(screen.getGuiLeft() + 28, screen.getGuiTop() - 26, 1, screen)
                {
                    @Override
                    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_)
                    {
                        super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
                        this.x = screen.getGuiLeft() + 28;
                    }
                });
                
                initGuiEvent.addWidget(new TabButton(screen.getGuiLeft() + 57, screen.getGuiTop() - 26, 2, screen)
                {
                    @Override
                    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_)
                    {
                        super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
                        this.x = screen.getGuiLeft() + 57;
                    }
                });
                
                initGuiEvent.addWidget(new TabButton(screen.getGuiLeft() + 86, screen.getGuiTop() - 26, 3, screen)
                {
                    @Override
                    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_)
                    {
                        super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
                        this.x = screen.getGuiLeft() + 86;
                    }
                });
            }
    
            if(ConfigHandler.CLIENT.inventoryToggle.get()) {
                initGuiEvent.addWidget(new ImageButton(screen.getGuiLeft() + 128, screen.height / 2 - 22, 20, 18, 20, 0, 19, DragonScreen.INVENTORY_TOGGLE_BUTTON, p_onPress_1_ -> {
                    NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
                })
                {
                    @Override
                    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_)
                    {
                        super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
                        this.x = screen.getGuiLeft() + 128;
                        
                        if(isHovered()){
                            ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.toggle_inventory.dragon")));
                            Minecraft.getInstance().screen.renderComponentTooltip(p_230431_1_, description, p_230431_2_, p_230431_3_);
                        }
                    }
                });
            }
        }
        
        if(sc instanceof CreativeScreen){
            CreativeScreen screen = (CreativeScreen)sc;
            
            if(ConfigHandler.CLIENT.inventoryToggle.get()) {
                initGuiEvent.addWidget(new ImageButton(screen.getGuiLeft() + 128 + 20, screen.height / 2 - 50, 20, 18, 20, 0, 19, DragonScreen.INVENTORY_TOGGLE_BUTTON, p_onPress_1_ -> {
                    NetworkHandler.CHANNEL.sendToServer(new OpenDragonInventory());
                })
                {
                    @Override
                    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
                    {
                        this.active = this.visible = screen.getSelectedTab() == ItemGroup.TAB_INVENTORY.getId();
                        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
                    }
    
                    @Override
                    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_)
                    {
                        super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
                        if(isHovered()){
                            ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.toggle_inventory.dragon")));
                            Minecraft.getInstance().screen.renderComponentTooltip(p_230431_1_, description, p_230431_2_, p_230431_3_);
                        }
                    }
                });
            }
        }
        
    }
    
    
    public static Vector3d getInputVector(Vector3d movement, float fricSpeed, float yRot) {
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
    public static void onClientTick(RenderTickEvent renderTickEvent) {
        if (renderTickEvent.phase == Phase.START) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity player = minecraft.player;
            if (player != null) {
                DragonStateProvider.getCap(player).ifPresent(playerStateHandler -> {
                    if (playerStateHandler.isDragon()) {
                        float headRot = player.yRot != 0.0 ? player.yRot : player.yHeadRot;
                        double bodyYaw = playerStateHandler.getMovementData().bodyYaw;
                        float bodyAndHeadYawDiff =(((float)bodyYaw) - headRot);
    
                        Vector3d moveVector = getInputVector(new Vector3d(player.input.leftImpulse, 0, player.input.forwardImpulse), 1F, player.yRot);
                        boolean isFlying = false;
                        if (playerStateHandler.isFlying() && !player.isOnGround() && !player.isInWater() && !player.isInLava()) { // TODO: Remove this when fixing flight system
                            moveVector = new Vector3d(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
                            isFlying = true;
                        }
                        float f = (float) MathHelper.atan2(moveVector.z, moveVector.x) * (180F / (float) Math.PI) - 90F;
                        float f1 = (float) (Math.pow(moveVector.x, 2) + Math.pow(moveVector.z, 2));
    
                        if (f1 > 0.000028) {
                            if (isFlying || (minecraft.options.getCameraType() != PointOfView.FIRST_PERSON && ConfigHandler.CLIENT.thirdPersonBodyMovement.get() == DragonBodyMovementType.DRAGON) ||
                                    minecraft.options.getCameraType() == PointOfView.FIRST_PERSON && ConfigHandler.CLIENT.firstPersonBodyMovement.get() == DragonBodyMovementType.DRAGON) {
                                float f2 = MathHelper.wrapDegrees(f - (float) bodyYaw);
                                bodyYaw += 0.5F * f2;
                            } else if ((minecraft.options.getCameraType() != PointOfView.FIRST_PERSON && ConfigHandler.CLIENT.thirdPersonBodyMovement.get() == DragonBodyMovementType.VANILLA) ||
                                    minecraft.options.getCameraType() == PointOfView.FIRST_PERSON && ConfigHandler.CLIENT.firstPersonBodyMovement.get() == DragonBodyMovementType.VANILLA) {

                                float f5 = MathHelper.abs(MathHelper.wrapDegrees(player.yRot) - f);
                                if (95.0F < f5 && f5 < 265.0F) {
                                    f -= 180.0F;
                                }

                                float _f = MathHelper.wrapDegrees(f - (float) bodyYaw);
                                bodyYaw += _f * 0.3F;
                                float _f1 = MathHelper.wrapDegrees(player.yRot - (float) bodyYaw);
                                boolean flag = _f1 < -90.0F || _f1 >= 90.0F;

                                if (_f1 < -75.0F) {
                                    _f1 = -75.0F;
                                }

                                if (_f1 >= 75.0F) {
                                    _f1 = 75.0F;
                                }
    
                                bodyYaw = player.yRot - _f1;
                                if (_f1 * _f1 > 2500.0F) {
                                    bodyYaw += _f1 * 0.2F;
                                }
                            }
                            
                            bodyYaw = MathHelper.lerp(0.25, playerStateHandler.getMovementData().bodyYaw, bodyYaw) ;
                            
                            if (bodyAndHeadYawDiff > 180) {
                                bodyYaw -= 360;
                            }

                            if (bodyAndHeadYawDiff <= -180) {
                                bodyYaw += 360;
                            }
                            
                            playerStateHandler.setMovementData(bodyYaw, headRot, player.xRot, player.swinging && player.getAttackStrengthScale(-3.0f) != 1);
                            NetworkHandler.CHANNEL.sendToServer(new PacketSyncCapabilityMovement(player.getId(), bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
                        } else if (Math.abs(bodyAndHeadYawDiff) > 180F) {
                            if (Math.abs(bodyAndHeadYawDiff) > 360F)
                                bodyYaw -= bodyAndHeadYawDiff;
                            else {
                                float turnSpeed = Math.min(1F + (float) Math.pow(Math.abs(bodyAndHeadYawDiff) - 180F, 1.5F) / 30F, 50F);
                                playerStateHandler.setMovementData((float) bodyYaw - Math.signum(bodyAndHeadYawDiff) * turnSpeed, headRot, player.xRot, player.swinging && player.getAttackStrengthScale(-3.0f) != 1);
                            }
                            NetworkHandler.CHANNEL.sendToServer(new PacketSyncCapabilityMovement(player.getId(), bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
                        } else if (playerStateHandler.getMovementData().bite != (player.swinging && player.getAttackStrengthScale(-3.0f) != 1) || headRot != playerStateHandler.getMovementData().headYaw) {
                            playerStateHandler.setMovementData(playerStateHandler.getMovementData().bodyYaw, headRot, player.xRot, player.swinging && player.getAttackStrengthScale(-3.0f) != 1);
                            NetworkHandler.CHANNEL.sendToServer(new PacketSyncCapabilityMovement(player.getId(), playerStateHandler.getMovementData().bodyYaw, playerStateHandler.getMovementData().headYaw, playerStateHandler.getMovementData().headPitch, playerStateHandler.getMovementData().bite));
                        }
                    }
                });
            }
        }
    }

    private static ItemStack BOLAS;
    
    
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
    
    public static void renderBolas(int light, int overlayCoords, IRenderTypeBuffer buffers, MatrixStack matrixStack) {
        matrixStack.pushPose();
        matrixStack.scale(3, 3, 3);
        matrixStack.translate(0, 0.5, 0);
        if (BOLAS == null)
            BOLAS = new ItemStack(ItemsInit.huntingNet);
        Minecraft.getInstance().getItemRenderer().renderStatic(BOLAS, ItemCameraTransforms.TransformType.NONE, light, overlayCoords, matrixStack, buffers);
        matrixStack.popPose();
    }
    
    @SubscribeEvent
    public static void unloadWorld(WorldEvent.Unload worldEvent) {
        ClientDragonRender.dragonEntity = null;
        ClientDragonRender.dragonArmor = null;
        ClientDragonRender.playerDragonHashMap.clear();
    }
    
    public static String getMaterial(String texture, ItemStack clawItem)
    {
        TieredItem item = (TieredItem)clawItem.getItem();
        IItemTier tier = item.getTier();
        if (tier == ItemTier.NETHERITE) {
            texture += "netherite_";
        } else if (tier == ItemTier.DIAMOND) {
            texture += "diamond_";
        } else if (tier == ItemTier.IRON) {
            texture += "iron_";
        } else if (tier == ItemTier.GOLD) {
            texture += "gold_";
        } else if (tier == ItemTier.STONE) {
            texture += "stone_";
        } else if (tier == ItemTier.WOOD) {
            texture += "wooden_";
        }	else {
            texture += "moded_";
        }
        return texture;
    }
}
