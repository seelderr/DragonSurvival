package by.jackraidenph.dragonsurvival.handlers.ClientSide;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.ServerFlightHandler;
import by.jackraidenph.dragonsurvival.network.status.SyncFlightSpeed;
import by.jackraidenph.dragonsurvival.network.status.SyncFlyingStatus;
import by.jackraidenph.dragonsurvival.sounds.FastGlideSound;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.TimeUnit;

/**
 * Used in pair with {@link ServerFlightHandler}
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
@SuppressWarnings("unused")
public class ClientFlightHandler {

    /**
     * Acceleration
     */
    static double ax, ay, az;
    
    @SubscribeEvent
    public static void flightCamera(CameraSetup setup){
        ClientPlayerEntity currentPlayer = Minecraft.getInstance().player;
        ActiveRenderInfo info = setup.getInfo();
    
        if(currentPlayer != null) {
            DragonStateHandler dragonStateHandler = DragonStateProvider.getCap(currentPlayer).orElse(null);
    
            if (dragonStateHandler != null) {
                if ( ServerFlightHandler.isGliding(currentPlayer)) {
                    if (setup.getInfo().isDetached()) {
                
                        Vector3d lookVec = currentPlayer.getLookAngle();
                        double increase = MathHelper.clamp(lookVec.y * 10, 0, lookVec.y * 5);
                
                        info.move(0, increase, 0);
                    }
                }
            }
        }
    }
    public static final ResourceLocation SPIN_COOLDOWN = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/spin_cooldown.png");
    @SubscribeEvent
    public static void renderFlightCooldown(RenderGameOverlayEvent.Post event) {
        PlayerEntity playerEntity = Minecraft.getInstance().player;
        
        if (playerEntity == null || !DragonStateProvider.isDragon(playerEntity) || playerEntity.isSpectator())
            return;
        
        DragonStateProvider.getCap(playerEntity).ifPresent(cap -> {
            if(!ServerFlightHandler.isFlying(playerEntity) && !ServerFlightHandler.isWaterSpin(playerEntity)){
                return;
            }
            
            if(cap.getMovementData().spinCooldown > 0) {
                if (event.getType() == ElementType.HOTBAR) {
                    GL11.glPushMatrix();
    
                    TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                    MainWindow window = Minecraft.getInstance().getWindow();
                    Minecraft.getInstance().getTextureManager().bind(SPIN_COOLDOWN);
    
                    int cooldown = ConfigHandler.SERVER.flightSpinCooldown.get() * 20;
                    float f = ((float)cooldown - (float)cap.getMovementData().spinCooldown) / (float)cooldown;
    
                    int k = (window.getGuiScaledWidth() / 2) - (66 / 2);
                    int j = window.getGuiScaledHeight() - 96;
    
                    k += ConfigHandler.CLIENT.spinCooldownXOffset.get();
                    j += ConfigHandler.CLIENT.spinCooldownYOffset.get();
    
                    int l = (int)(f * 62);
                    Screen.blit(event.getMatrixStack(), k, j, 0, 0, 66, 21, 256, 256);
                    Screen.blit(event.getMatrixStack(), k + 4, j + 1, 4, 21, l, 21, 256, 256);
    
                    GL11.glPopMatrix();
                }
            }
        });
    }
    
    public static boolean wasGliding = false;
    
    /**
     * Controls acceleration
     */
    @SubscribeEvent
    public static void flightControl(ClientTickEvent tickEvent) {
        ClientPlayerEntity playerEntity = Minecraft.getInstance().player;
        if (playerEntity != null && !playerEntity.isPassenger()) {
            DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
                if (dragonStateHandler.isDragon()) {
                    if(ServerFlightHandler.isWaterSpin(playerEntity) && ServerFlightHandler.isSpin(playerEntity)){
                        MovementInput movement = playerEntity.input;
                        
                        Vector3d motion = playerEntity.getDeltaMovement();
                        Vector3d lookVec = playerEntity.getLookAngle();
                        
                        double yaw = Math.toRadians(playerEntity.yHeadRot + 90);
                        double lookY = lookVec.y;
    
                        double speedLimit = ConfigHandler.SERVER.maxFlightSpeed.get();
                        ax = MathHelper.clamp(ax, -0.2 * speedLimit, 0.2 * speedLimit);
                        az = MathHelper.clamp(az, -0.2 * speedLimit, 0.2 * speedLimit);
    
                        ax += (Math.cos(yaw) / 500) * 50;
                        az += (Math.sin(yaw) / 500) * 50;
                        ay = lookVec.y / 8;
    
                        if (lookY < 0) {
                            motion = motion.add(ax, 0, az);
                        } else {
                            motion = motion.add(ax, ay, az);
                        }
                        motion = motion.multiply(0.99F, 0.98F, 0.99F);
    
                        if (motion.length() != playerEntity.getDeltaMovement().length()) {
                            NetworkHandler.CHANNEL.sendToServer(new SyncFlightSpeed(playerEntity.getId(), motion));
                        }
    
                        playerEntity.setDeltaMovement(motion);
                        ay = playerEntity.getDeltaMovement().y;
                    }
                    
                    if (dragonStateHandler.isWingsSpread()) {
                        MovementInput movement = playerEntity.input;
    
                        boolean hasFood = playerEntity.getFoodData().getFoodLevel() > ConfigHandler.SERVER.flightHungerThreshold.get() || playerEntity.isCreative() || ConfigHandler.SERVER.allowFlyingWithoutHunger.get();
                        
                        //start
                        if (ServerFlightHandler.isFlying(playerEntity)) {
                            Vector3d motion = playerEntity.getDeltaMovement();
                            
                            Vector3d lookVec = playerEntity.getLookAngle();
                            float f6 = playerEntity.xRot * ((float) Math.PI / 180F);
                            double d9 = Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z);
                            double d11 = Math.sqrt(LivingEntity.getHorizontalDistanceSqr(motion));
                            double d12 = lookVec.length();
                            float f3 = MathHelper.cos(f6);
                            f3 = (float) ((double) f3 * (double) f3 * Math.min(1.0D, d12 / 0.4D));
                            ModifiableAttributeInstance gravity = playerEntity.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
                            double g = gravity.getValue();
                            
                            if(ServerFlightHandler.isGliding(playerEntity)){
                                if(!wasGliding){
                                    Minecraft.getInstance().getSoundManager().play(new FastGlideSound(playerEntity));
                                    wasGliding = true;
                                }
                            }
                            
                            if(ServerFlightHandler.isGliding(playerEntity) || (ax != 0 || az != 0)) {
                                motion = playerEntity.getDeltaMovement().add(0.0D, g * (-1.0D + (double)f3 * 0.75D), 0.0D);
    
                                if (motion.y < 0.0D && d9 > 0.0D) {
                                    double d3 = motion.y * -0.1D * (double)f3;
                                    motion = motion.add(lookVec.x * d3 / d9, d3, lookVec.z * d3 / d9);
                                }
    
                                if (f6 < 0.0F && d9 > 0.0D) {
                                    double d13 = d11 * (double)(-MathHelper.sin(f6)) * 0.04D;
                                    motion = motion.add(-lookVec.x * d13 / d9, d13 * 3.2D, -lookVec.z * d13 / d9);
                                }
    
                                if (d9 > 0.0D) {
                                    motion = motion.add((lookVec.x / d9 * d11 - motion.x) * 0.1D, 0.0D, (lookVec.z / d9 * d11 - motion.z) * 0.1D);
                                }
                                double lookY = lookVec.y;
                                double yaw = Math.toRadians(playerEntity.yHeadRot + 90);
                                if (lookY < 0) {
                                    ax += Math.cos(yaw) / 500;
                                    az += Math.sin(yaw) / 500;
                                } else {
                                    ax *= 0.99;
                                    az *= 0.99;
                                    ay = lookVec.y / 8;
                                }
                                double speedLimit = ConfigHandler.SERVER.maxFlightSpeed.get();
                                ax = MathHelper.clamp(ax, -0.2 * speedLimit, 0.2 * speedLimit);
                                az = MathHelper.clamp(az, -0.2 * speedLimit, 0.2 * speedLimit);
    
                                if(ServerFlightHandler.isSpin(playerEntity)){
                                    ax += (Math.cos(yaw) / 500) * 100;
                                    az += (Math.sin(yaw) / 500) * 100;
                                    ay = lookVec.y / 8;
                                }
                                
                                if(ServerFlightHandler.isGliding(playerEntity)) {
                                    if (lookY < 0) {
                                        motion = motion.add(ax, 0, az);
                                    } else {
                                        motion = motion.add(ax, ay, az);
                                    }
                                    motion = motion.multiply(0.99F, 0.98F, 0.99F);
                                    
                                    if (motion.length() != playerEntity.getDeltaMovement().length()) {
                                        NetworkHandler.CHANNEL.sendToServer(new SyncFlightSpeed(playerEntity.getId(), motion));
                                    }
    
                                    playerEntity.setDeltaMovement(motion);
                                    ay = playerEntity.getDeltaMovement().y;
                                }
                                //end
                            }
                            
                            if(!ServerFlightHandler.isGliding(playerEntity)){
                                wasGliding = false;
                                double maxForward = 0.5;
    
                                Vector3d moveVector = ClientEvents.getInputVector(new Vector3d(movement.leftImpulse, 0, movement.forwardImpulse), 1F, playerEntity.yRot);
                                moveVector.multiply(1.3, 0, 1.3);
    
                                double lookY = lookVec.y;
    
                                boolean moving = movement.up || movement.down || movement.left || movement.right;
                                double yaw = Math.toRadians(playerEntity.yHeadRot + 90);
    
                                if(ServerFlightHandler.isSpin(playerEntity)){
                                    ax += (Math.cos(yaw) / 500) * 200;
                                    az += (Math.sin(yaw) / 500) * 200;
                                    ay = lookVec.y / 8;
                                }
                                
                                if(moving && !movement.jumping && !movement.shiftKeyDown){
                                    maxForward = 0.8;
                                    moveVector.multiply(1.4, 0, 1.4);
                                    motion = new Vector3d(MathHelper.lerp(0.1, motion.x, moveVector.x), 0, MathHelper.lerp(0.1, motion.z, moveVector.z));
                                    motion = new Vector3d(MathHelper.clamp(motion.x, -maxForward, maxForward), 0, MathHelper.clamp(motion.z, -maxForward, maxForward));
    
                                    motion = motion.add(ax, ay, az);
    
                                    ax *= 0.9F;
                                    ay *= 0.9F;
                                    az *= 0.9F;
                                    
                                    motion = new Vector3d(motion.x, -(g * 2) + motion.y, motion.z);
                                    
                                    if(motion.length() != playerEntity.getDeltaMovement().length()){
                                        NetworkHandler.CHANNEL.sendToServer(new SyncFlightSpeed(playerEntity.getId(), motion));
                                    }
                                   
                                    playerEntity.setDeltaMovement(motion);
                                    return;
                                }
    
                                motion = motion.multiply(0.99F, 0.98F, 0.99F);
                                motion = new Vector3d(MathHelper.lerp(0.1, motion.x, moveVector.x), 0, MathHelper.lerp(0.1, motion.z, moveVector.z));
                                motion = new Vector3d(MathHelper.clamp(motion.x, -maxForward, maxForward), 0, MathHelper.clamp(motion.z, -maxForward, maxForward));
    
                                motion = motion.add(ax, ay, az);
    
                                if(dragonStateHandler.getMovementData().bite){
                                    motion.multiply(10, 10, 10);
                                }
    
                                ax *= 0.9F;
                                ay *= 0.9F;
                                az *= 0.9F;
    
                                if(movement.jumping){
                                    motion = new Vector3d(motion.x, 0.4 + motion.y, motion.z);
        
                                    if(motion.length() != playerEntity.getDeltaMovement().length()){
                                        NetworkHandler.CHANNEL.sendToServer(new SyncFlightSpeed(playerEntity.getId(), motion));
                                    }
        
                                    playerEntity.setDeltaMovement(motion);
                                    return;
                                }else if(movement.shiftKeyDown){
                                    motion = new Vector3d(motion.x, -0.5 + motion.y, motion.z);
                                    if(motion.length() != playerEntity.getDeltaMovement().length()){
                                        NetworkHandler.CHANNEL.sendToServer(new SyncFlightSpeed(playerEntity.getId(), motion));
                                    }
        
                                    playerEntity.setDeltaMovement(motion);
                                    return;
                                }
                                
                                if(playerEntity.fallDistance >= 2.5) { //Dont activate on a regular jump
                                    double yMotion = hasFood ? -g + ay : -(g * 4) + ay;
                                    motion = new Vector3d(motion.x, yMotion, motion.z);
                                    
                                    if(motion.length() != playerEntity.getDeltaMovement().length()){
                                        NetworkHandler.CHANNEL.sendToServer(new SyncFlightSpeed(playerEntity.getId(), motion));
                                    }
                                    
                                    playerEntity.setDeltaMovement(motion);
                                }
                            }
                        } else {
                            wasGliding = false;
                            ax = 0;
                            az = 0;
                            ay = 0;
                        }
                    }else{
                        ax = 0;
                        az = 0;
                        ay = 0;
                    }
                }
            });
        }
    }
    
    private static long lastHungerMessage;
    
    @SubscribeEvent
    public static void toggleWings(InputEvent.KeyInputEvent keyInputEvent) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(player == null) return;
    
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        if(handler == null) return;
    
        boolean currentState = handler.isWingsSpread();
        Vector3d lookVec = player.getLookAngle();
    
        if(ConfigHandler.CLIENT.jumpToFly.get() && !player.isCreative() && !player.isSpectator()) {
            if (Minecraft.getInstance().options.keyJump.consumeClick()) {
                if(keyInputEvent.getAction() == GLFW.GLFW_PRESS) {
                    if (handler.hasWings() && !currentState && (lookVec.y > 0.8 || !ConfigHandler.CLIENT.lookAtSkyForFlight.get())) {
                        if (!player.isOnGround() && !player.isInLava() && !player.isInWater()) {
                            if (player.getFoodData().getFoodLevel() > ConfigHandler.SERVER.flightHungerThreshold.get() || player.isCreative() || ConfigHandler.SERVER.allowFlyingWithoutHunger.get()) {
                                NetworkHandler.CHANNEL.sendToServer(new SyncFlyingStatus(player.getId(), true));
                            } else {
                                if (lastHungerMessage == 0 || lastHungerMessage + TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS) < System.currentTimeMillis()) {
                                    lastHungerMessage = System.currentTimeMillis();
                                    player.sendMessage(new TranslationTextComponent("ds.wings.nohunger"), player.getUUID());
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (KeyInputHandler.TOGGLE_WINGS.consumeClick()) {
            if (handler.hasWings()) {
                //Allows toggling the wings if food level is above 0, player is creative, wings are already enabled (allows disabling even when hungry) or if config options is turned on
                if((player.getFoodData().getFoodLevel() > ConfigHandler.SERVER.flightHungerThreshold.get() || player.isCreative()) || currentState || ConfigHandler.SERVER.allowFlyingWithoutHunger.get()) {
                    NetworkHandler.CHANNEL.sendToServer(new SyncFlyingStatus(player.getId(), !currentState));
                    if (ConfigHandler.CLIENT.notifyWingStatus.get()) {
                        if (!currentState) player.sendMessage(new TranslationTextComponent("ds.wings.enabled"), player.getUUID());
                        else player.sendMessage(new TranslationTextComponent("ds.wings.disabled"), player.getUUID());
                    }
                }else{
                    player.sendMessage(new TranslationTextComponent("ds.wings.nohunger"), player.getUUID());
                }
            } else {
                player.sendMessage(new TranslationTextComponent("ds.you.have.no.wings"), player.getUUID());
            }
        }
    }
}
