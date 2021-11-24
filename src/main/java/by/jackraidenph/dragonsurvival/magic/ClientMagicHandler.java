package by.jackraidenph.dragonsurvival.magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.gui.Buttons.TabButton;
import by.jackraidenph.dragonsurvival.network.magic.ActivateAbilityInSlot;
import by.jackraidenph.dragonsurvival.network.magic.SyncCurrentAbilityCasting;
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
public class ClientMagicHandler
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
					ability.errorTicks = 0;
		            ability.errorMessage = null;
					
	                if (ability.getCurrentCastTimer() < ability.getCastingTime() && modeAbility == GLFW.GLFW_REPEAT) {
	                    ability.tickCasting();
						
						if(dragonStateHandler.getCurrentlyCasting() != ability) {
							DragonSurvivalMod.CHANNEL.sendToServer(new SyncCurrentAbilityCasting(slot, ability));
							dragonStateHandler.setCurrentlyCasting(ability);
						}
						
	                } else if (modeAbility == GLFW.GLFW_RELEASE) {
	                    ability.stopCasting();
						
						if(dragonStateHandler.getCurrentlyCasting() != null) {
							DragonSurvivalMod.CHANNEL.sendToServer(new SyncCurrentAbilityCasting(slot, null));
							dragonStateHandler.setCurrentlyCasting(null);
						}
		
	                } else if (ability.getCastingTime() <= 0 || ability.getCurrentCastTimer() >= ability.getCastingTime()){
	                    ability.onKeyPressed(playerEntity);
	                    DragonSurvivalMod.CHANNEL.sendToServer(new ActivateAbilityInSlot(slot, modeAbility));
	                }
	            }else{
		            if(dragonStateHandler.getCurrentlyCasting() != null) {
			            DragonSurvivalMod.CHANNEL.sendToServer(new SyncCurrentAbilityCasting(slot, null));
			            dragonStateHandler.setCurrentlyCasting(null);
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
	
				if(cap.renderAbilityHotbar()) {
					textureManager.bind(new ResourceLocation("textures/gui/widgets.png"));
					Screen.blit(event.getMatrixStack(), posX, posY - 2, 0, 0, 0, 41, 22, 256, 256);
					Screen.blit(event.getMatrixStack(), posX + 41, posY - 2, 0, 141, 0, 41, 22, 256, 256);
					
					for (int x = 0; x < count; x++) {
						ActiveDragonAbility ability = cap.getAbilityFromSlot(x);
						
						if (ability != null && ability.getIcon() != null) {
							textureManager.bind(ability.getIcon());
							Screen.blit(event.getMatrixStack(), posX + (x * sizeX) + 3, posY + 1, 0, 0, 16, 16, 16, 16);
							
							if (ability.getMaxCooldown() > 0 && ability.getCooldown() > 0 && ability.getMaxCooldown() != ability.getCooldown()) {
								float f = MathHelper.clamp((float)ability.getCooldown() / (float)ability.getMaxCooldown(), 0, 1);
								int boxX = posX + (x * sizeX) + 3;
								int boxY = posY + 1;
								int offset = 16 - (16 - (int)(f * 16));
								int color = new Color(0.15F, 0.15F, 0.15F, 0.75F).getRGB();
								int fColor = ability.errorTicks > 0 ? new Color(1F, 0F, 0F, 0.75F).getRGB() : color;
								AbstractGui.fill(event.getMatrixStack(), boxX, boxY, boxX + 16, boxY + (offset), fColor);
							}
						}
						
						if(ability.errorTicks > 0){
							ability.errorTicks--;
							
							if(ability.errorTicks <= 0){
								ability.errorMessage = null;
							}
						}
					}
					
					textureManager.bind(new ResourceLocation("textures/gui/widgets.png"));
					Screen.blit(event.getMatrixStack(), posX + (sizeX * cap.getSelectedAbilitySlot()) - 1, window.getGuiScaledHeight() - 23, 2, 0, 22, 24, 24, 256, 256);
					
					textureManager.bind(TabButton.buttonTexture);
					
					int maxMana = cap.getMaxMana(playerEntity);
					int curMana = cap.getCurrentMana();
					
					for(int i = 0; i < 1 + Math.ceil(maxMana / 10.0); i++){
						for(int x = 0; x < 10; x++){
							int manaSlot = (i * 10) + x;
							if(manaSlot < maxMana) {
								int xPos = curMana <= manaSlot ? 54 : cap.getType() == DragonType.SEA ? 0 : cap.getType() == DragonType.FOREST ? 18 : 36;
								float rescale = 2.15F;
								Screen.blit(event.getMatrixStack(), posX + (x * (int)(18 / rescale)), posY - 12 - (i * ((int)(18 / rescale) + 1)), xPos / rescale, 204 / rescale, (int)(18 / rescale), (int)(18 / rescale), (int)(256 / rescale), (int)(256 / rescale));
							}
						}
					}
				}
				
		
		        ActiveDragonAbility ability = cap.getAbilityFromSlot(cap.getSelectedAbilitySlot());
				
	            if(ability.getCurrentCastTimer() > 0){
		            textureManager.bind(ability.getIcon());
		
					int offset = 50;
					
		            Screen.blit(event.getMatrixStack(), (window.getGuiScaledWidth() / 2) - (8 / 2), window.getGuiScaledHeight() - offset - 8,
		                        0, 0, 8, 8, 8, 8);
					
		            float perc = Math.min((float)ability.getCurrentCastTimer() / (float)ability.getCastingTime(), 1);
	                textureManager.bind(TabButton.buttonTexture);
	                Screen.blit(event.getMatrixStack(), (window.getGuiScaledWidth() / 2) - (194 / 4), window.getGuiScaledHeight() - offset, 0, 180 / 2,  194 / 2, 6 / 2, 128, 128);
	                Screen.blit(event.getMatrixStack(), (window.getGuiScaledWidth() / 2) - (194 / 4), window.getGuiScaledHeight() - offset, 0, 174 / 2,  (int)((194 / 2) * perc), 6 / 2, 128, 128);
	            }
				
				if(ability.errorTicks > 0){
					Minecraft.getInstance().font.draw(event.getMatrixStack(), ability.errorMessage, (window.getGuiScaledWidth() / 2) - (Minecraft.getInstance().font.width(ability.errorMessage) / 2), window.getGuiScaledHeight() - 70, 0);
				}
				
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
		        double perc = Math.min(((float)ability.getCurrentCastTimer() / (float)ability.getCastingTime()), 1) / 4;
				double c4 = (2 * Math.PI) / 3;
				
				if(perc != 0 && perc != 1){
					perc = Math.pow(2, -10 * perc) * Math.sin((perc * 10 - 0.75) * c4) + 1;
				}
				
				float newFov = (float)MathHelper.clamp(perc, 0.75F, 1.2F);
	            event.setNewfov(newFov);
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
	                event.setDensity(event.getDensity() / 5);
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
