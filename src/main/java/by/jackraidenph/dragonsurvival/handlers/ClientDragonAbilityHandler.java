package by.jackraidenph.dragonsurvival.handlers;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.abilities.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.gui.Buttons.TabButton;
import by.jackraidenph.dragonsurvival.network.Abilities.ActivateAbilityInSlot;
import by.jackraidenph.dragonsurvival.network.Abilities.SyncCurrentAbilityCasting;
import by.jackraidenph.dragonsurvival.registration.ClientModEvents;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientDragonAbilityHandler
{
	private static byte timer = 0;
	private static byte abilityHoldTimer = 0;
	
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
	    
	    DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
	        ActiveDragonAbility ability = dragonStateHandler.getAbilityFromSlot(slot);
	        if(ability.getLevel() > 0) {
	            if(ability.canRun(playerEntity, modeAbility)) {
	                if (ability.getCurrentCastTimer() < ability.getCastingTime() && modeAbility == GLFW.GLFW_REPEAT) {
	                    ability.tickCasting();
		                DragonSurvivalMod.CHANNEL.sendToServer(new SyncCurrentAbilityCasting(slot, ability));
						dragonStateHandler.setCurrentlyCasting(ability);
	                } else if (modeAbility == GLFW.GLFW_RELEASE) {
	                    ability.stopCasting();
		                DragonSurvivalMod.CHANNEL.sendToServer(new SyncCurrentAbilityCasting(slot, null));
		                dragonStateHandler.setCurrentlyCasting(null);
		
	                } else if (ability.getCastingTime() <= 0 || ability.getCurrentCastTimer() >= ability.getCastingTime()){
	                    ability.onKeyPressed(playerEntity);
	                    DragonSurvivalMod.CHANNEL.sendToServer(new ActivateAbilityInSlot(slot, modeAbility));
						if(ability.getCurrentCastTimer() >= ability.getCastingTime()){
							DragonSurvivalMod.CHANNEL.sendToServer(new SyncCurrentAbilityCasting(slot, null));
							dragonStateHandler.setCurrentlyCasting(null);
						}
	                }
	            }
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

	            TextureManager textureManager = Minecraft.getInstance().getTextureManager();
	            MainWindow window = Minecraft.getInstance().getWindow();
				
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
	                            , 0, 0, 16, 16, 16, 16);
		
		                if(ability.getMaxCooldown() > 0 && ability.getCooldown() > 0 && ability.getMaxCooldown() != ability.getCooldown() ){
			                float f = MathHelper.clamp((float)ability.getCooldown() / (float)ability.getMaxCooldown(), 0, 1);
			                int boxX = posX + (x * sizeX) + 3;
			                int boxY = posY + 1;
			                int offset = 16 - (16 - (int)(f * 16));
			                AbstractGui.fill(event.getMatrixStack(), boxX, boxY, boxX + 16, boxY + (offset), new Color(0.15F, 0.15F, 0.15F, 0.75F).getRGB());
		                }
	                }
	            }
				
		
		        ActiveDragonAbility ability = cap.getAbilityFromSlot(cap.getSelectedAbilitySlot());
	
	            //TODO Improve cast bar, for example add a background box to it and render the icon of the skill that is currently casting
	            //TODO And maybe change color based on dragon type?
	            //TODO Show current casting in seconds at the end of the bar
	            if(ability.getCurrentCastTimer() > 0){
	                float perc = Math.min((float)ability.getCurrentCastTimer() / (float)ability.getCastingTime(), 1);
	                textureManager.bind(TabButton.buttonTexture);
	                Screen.blit(event.getMatrixStack(), (window.getGuiScaledWidth() / 2) - (194 / 4), window.getGuiScaledHeight() - 60, 0, 180 / 2,  194 / 2, 6 / 2, 128, 128);
	                Screen.blit(event.getMatrixStack(), (window.getGuiScaledWidth() / 2) - (194 / 4), window.getGuiScaledHeight() - 60, 0, 174 / 2,  (int)((194 / 2) * perc), 6 / 2, 128, 128);
	            }
		
		        textureManager.bind(new ResourceLocation("textures/gui/widgets.png"));
	            Screen.blit(event.getMatrixStack(), posX + (sizeX * cap.getSelectedAbilitySlot()) - 1, window.getGuiScaledHeight() - 23, 2, 0, 22, 24, 24, 256, 256);

	            GL11.glPopMatrix();
	        }
	    });
	}
	
	@SubscribeEvent
	public static void onFovEvent(FOVUpdateEvent event) {
	    PlayerEntity player = event.getEntity();
	    
	    DragonStateProvider.getCap(player).ifPresent(cap -> {
	        ActiveDragonAbility ability = cap.getCurrentlyCasting();
	        
	        if(ability != null && ability.getCurrentCastTimer() > 0){
	            float perc = Math.min(0.5F + ((float)ability.getCurrentCastTimer() / (float)ability.getCastingTime() / 2), 1);
	            event.setNewfov(perc);
	        }
	    });
	}
	
	@SubscribeEvent
	@OnlyIn( Dist.CLIENT)
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
}
