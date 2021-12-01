package by.jackraidenph.dragonsurvival.magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.gui.magic.AbilityScreen;
import by.jackraidenph.dragonsurvival.handlers.DragonFoodHandler;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.network.magic.ActivateAbilityServerSide;
import by.jackraidenph.dragonsurvival.network.magic.SyncAbilityCastingToServer;
import by.jackraidenph.dragonsurvival.particles.CaveDragon.SmallFireParticleData;
import by.jackraidenph.dragonsurvival.particles.ForestDragon.SmallPoisonParticleData;
import by.jackraidenph.dragonsurvival.particles.SeaDragon.LargeLightningParticleData;
import by.jackraidenph.dragonsurvival.registration.ClientModEvents;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientMagicHandler
{
	public static final ResourceLocation widgetTextures = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/widgets.png");
	public static final ResourceLocation castBars = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/cast_bars.png");
	
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
							dragonStateHandler.setCurrentlyCasting(ability);
							DragonSurvivalMod.CHANNEL.sendToServer(new SyncAbilityCastingToServer(playerEntity.getId(), ability));
						}
						
	                } else if (modeAbility == GLFW.GLFW_RELEASE) {
	                    ability.stopCasting();
						
						if(dragonStateHandler.getCurrentlyCasting() != null) {
							DragonSurvivalMod.CHANNEL.sendToServer(new SyncAbilityCastingToServer(playerEntity.getId(), null));
							dragonStateHandler.setCurrentlyCasting(null);
						}
		
	                } else if (ability.getCastingTime() <= 0 || ability.getCurrentCastTimer() >= ability.getCastingTime()){
	                    ability.onKeyPressed(playerEntity);
	                    DragonSurvivalMod.CHANNEL.sendToServer(new ActivateAbilityServerSide(slot));
	                }
	            }else{
		            if(dragonStateHandler.getCurrentlyCasting() != null) {
			            DragonSurvivalMod.CHANNEL.sendToServer(new SyncAbilityCastingToServer(playerEntity.getId(), null));
			            dragonStateHandler.setCurrentlyCasting(null);
		            }
	            }
	        }
	    });
	}
	
	@SubscribeEvent
	public static void cancelExpBar(RenderGameOverlayEvent event) {
		PlayerEntity playerEntity = Minecraft.getInstance().player;
		
		if( event.getType() == ElementType.EXPERIENCE && !playerEntity.isCreative()){
			DragonStateProvider.getCap(playerEntity).ifPresent(cap -> {
				ActiveDragonAbility ability = cap.getAbilityFromSlot(cap.getSelectedAbilitySlot());
				if(ability == null) return;
				
				if(DragonStateProvider.getCurrentMana(playerEntity) < ability.getManaCost() && ((DragonStateProvider.getCurrentMana(playerEntity) + (playerEntity.totalExperience / 10) >= ability.getManaCost()) || playerEntity.experienceLevel > 0)){
					event.setCanceled(true);
					MainWindow window = Minecraft.getInstance().getWindow();
					
					int screenWidth = window.getGuiScaledWidth();
					int screenHeight =  window.getGuiScaledHeight();
					
					Minecraft.getInstance().getTextureManager().bind(widgetTextures);
					MatrixStack stack = event.getMatrixStack();
					int x = window.getGuiScaledWidth() / 2 - 91;
					int i = Minecraft.getInstance().player.getXpNeededForNextLevel();
					if (i > 0) {
						int j = 182;
						int k = (int)(Minecraft.getInstance().player.experienceProgress * 183.0F);
						int l = screenHeight - 32 + 3;
						blit(stack, x, l, 0, 164, 182, 5);
						if (k > 0) {
							blit(stack, x, l, 0, 169, k, 5);
						}
					}
					
					if (Minecraft.getInstance().player.experienceLevel > 0) {
						String s = "" + Minecraft.getInstance().player.experienceLevel;
						int i1 = (screenWidth - Minecraft.getInstance().font.width(s)) / 2;
						int j1 = screenHeight - 31 - 4;
						Minecraft.getInstance().font.draw(stack, s, (float)(i1 + 1), (float)j1, 0);
						Minecraft.getInstance().font.draw(stack, s, (float)(i1 - 1), (float)j1, 0);
						Minecraft.getInstance().font.draw(stack, s, (float)i1, (float)(j1 + 1), 0);
						Minecraft.getInstance().font.draw(stack, s, (float)i1, (float)(j1 - 1), 0);
						Minecraft.getInstance().font.draw(stack, s, (float)i1, (float)j1, new Color(243, 48, 59).getRGB());
					}
				}
			});
		}
	}
	
	public static void blit(MatrixStack p_238474_1_, int p_238474_2_, int p_238474_3_, int p_238474_4_, int p_238474_5_, int p_238474_6_, int p_238474_7_) {
		Screen.blit(p_238474_1_, p_238474_2_, p_238474_3_, 0, (float)p_238474_4_, (float)p_238474_5_, p_238474_6_, p_238474_7_, 256, 256);
	}
	
	@SubscribeEvent
	public static void renderAbilityHud(RenderGameOverlayEvent.Post event) {
	    PlayerEntity playerEntity = Minecraft.getInstance().player;
	    
	    if (playerEntity == null || !DragonStateProvider.isDragon(playerEntity) || playerEntity.isSpectator())
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
					
					textureManager.bind(widgetTextures);
					
					int maxMana = DragonStateProvider.getMaxMana(playerEntity);
					int curMana = DragonStateProvider.getCurrentMana(playerEntity);
					
					for(int i = 0; i < 1 + Math.ceil(maxMana / 10.0); i++){
						for(int x = 0; x < 10; x++){
							int manaSlot = (i * 10) + x;
							if(manaSlot < maxMana) {
								boolean goodCondi = MagicHandler.isPlayerInGoodConditions(playerEntity);
								int condiXPos = cap.getType() == DragonType.SEA ? 0 : cap.getType() == DragonType.FOREST ? 18 : 36;;
								int xPos = curMana <= manaSlot ? (goodCondi ? condiXPos + 72 : 54) : cap.getType() == DragonType.SEA ? 0 : cap.getType() == DragonType.FOREST ? 18 : 36;
								float rescale = 2.15F;
								Screen.blit(event.getMatrixStack(), posX + (x * (int)(18 / rescale)), posY - 12 - (i * ((int)(18 / rescale) + 1)), xPos / rescale, 204 / rescale, (int)(18 / rescale), (int)(18 / rescale), (int)(256 / rescale), (int)(256 / rescale));
							}
						}
					}
				}
				
		
		        ActiveDragonAbility ability = cap.getAbilityFromSlot(cap.getSelectedAbilitySlot());
				
	            if(ability.getCurrentCastTimer() > 0){
					GL11.glPushMatrix();
		            GL11.glScalef(0.5F, 0.5F, 0);
					int width = 196;
					int height = 47;
					
					int yPos1 = cap.getType() == DragonType.CAVE ? 0 : cap.getType() == DragonType.FOREST ? 47 : 94;
					int yPos2 = cap.getType() == DragonType.CAVE ? 142 : cap.getType() == DragonType.FOREST ? 147 : 152;
					
		            float perc = Math.min((float)ability.getCurrentCastTimer() / (float)ability.getCastingTime(), 1);
					
					int startX = (window.getGuiScaledWidth() / 2) - ConfigHandler.CLIENT.casterBarXPos.get();
					int startY = window.getGuiScaledHeight() - ConfigHandler.CLIENT.casterBarYPos.get();
		
		            GL11.glTranslatef(startX, startY, 0);
		
		
		            textureManager.bind(castBars);
		            Screen.blit(event.getMatrixStack(), startX, startY, 0, yPos1,  width, height, 256, 256);
					Screen.blit(event.getMatrixStack(), startX + 2, startY + 41, 0, yPos2,  (int)((191) * perc), 4, 256, 256);
		
		            textureManager.bind(ability.getIcon());
		            Screen.blit(event.getMatrixStack(), startX + 78, startY + 3,
		                        0, 0, 36, 36, 36, 36);
		
		            GL11.glPopMatrix();
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
	
	@OnlyIn( Dist.CLIENT)
	@SubscribeEvent
	public static void livingTick(LivingUpdateEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		
		
		if (!entity.level.isClientSide) {
			return;
		}
		
		if(entity == Minecraft.getInstance().player || DragonStateProvider.isDragon(entity)){
			return;
		}
		if (entity.hasEffect(DragonEffects.BURN)) {
			IParticleData data = new SmallFireParticleData(37F, false);
			for (int i = 0; i < 4; i++) {
				renderEffectParticle(entity, data);
			}
		}
		
		if (entity.hasEffect(DragonEffects.DRAIN)) {
			IParticleData data = new SmallPoisonParticleData(37F, false);
			for (int i = 0; i < 4; i++) {
				renderEffectParticle(entity, data);
			}
		}
		
		if (entity.hasEffect(DragonEffects.CHARGED)) {
			IParticleData data = new LargeLightningParticleData(37F, false);
			for (int i = 0; i < 4; i++) {
				renderEffectParticle(entity, data);
			}
		}
	}
	
	private static void renderEffectParticle(LivingEntity entity, IParticleData data)
	{
		double d0 = (double)(entity.level.random.nextFloat()) * entity.getBbWidth();
		double d1 = (double)(entity.level.random.nextFloat()) * entity.getBbHeight();
		double d2 = (double)(entity.level.random.nextFloat()) * entity.getBbWidth();
		double x = entity.getX() + d0 - (entity.getBbWidth() / 2);
		double y = entity.getY() + d1;
		double z = entity.getZ() + d2 - (entity.getBbWidth() / 2);
		Minecraft.getInstance().player.level.addParticle(data, x, y, z, 0, 0, 0);
	}
	
	//TODO This isnt removing the lava fog when on shaders
	@SubscribeEvent
	@OnlyIn( Dist.CLIENT)
	public static void removeLavaAndWaterFog(EntityViewRenderEvent.FogDensity event) {
	    ClientPlayerEntity player = Minecraft.getInstance().player;
	    DragonStateProvider.getCap(player).ifPresent(cap -> {
	        if(!cap.isDragon()) return;
	        
	        if (cap.getType() == DragonType.CAVE && event.getInfo().getFluidInCamera().is(FluidTags.LAVA)) {
	            if(player.hasEffect(DragonEffects.LAVA_VISION)) {
	                event.setDensity(0.02F);
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
	
	private static final ResourceLocation tooltip_1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/magic_tips_0.png");
	private static final ResourceLocation tooltip_2 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/magic_tips_1.png");
	private static boolean blink = false;
	private static int tick = 0;
	
	@SubscribeEvent
	public static void onPostTooltipEvent(RenderTooltipEvent.PostText event) {
		boolean renderAll = Objects.equals(Minecraft.getInstance().player.getGameProfile().getId(), UUID.fromString("05a6e38f-9cd9-3f4a-849c-68841b773e39")) || Objects.equals(Minecraft.getInstance().player.getGameProfile().getId(), UUID.fromString("6848748e-f3c1-4c30-91e4-4c7cc3fbeec5"));
		boolean text = false;
		boolean screen = Minecraft.getInstance().screen instanceof AbilityScreen;
		String translatedText1 = I18n.get("ds.skill.help");
		String translatedText2 = I18n.get("ds.skill.help.claws");
		
		String mergedString = "";
		
		for(ITextProperties comp : event.getLines()) {
			if (comp instanceof TranslationTextComponent) {
				TranslationTextComponent textComponent = (TranslationTextComponent)comp;
				if(textComponent.getKey().contains("ds.skill.help")){
					text = true;
					break;
				}
			}
			
			mergedString += comp.getString();
		}
		
		if(!text){
			if(mergedString.replace("\n", "").replace(" ", "").contains(translatedText1.replace("\n", "").replace(" ", ""))
			|| mergedString.replace("\n", "").replace(" ", "").contains(translatedText2.replace("\n", "").replace(" ", ""))){
				text = true;
			}
		}
		
		boolean render = text || renderAll;
		
		if(!render){
			return;
		}
		
		if(!blink){
			if(tick >= Functions.secondsToTicks(30)){
				blink = true;
				tick = 0;
			}
		}else{
			if(tick >= Functions.secondsToTicks(5)){
				blink = false;
				tick = 0;
			}
		}
		
		tick++;
		
		int x = event.getX();
		int y = event.getY();
		int width = event.getWidth();
		int height = event.getHeight();
		MatrixStack matrix = event.getMatrixStack();
		
		Minecraft.getInstance().getTextureManager().bind(blink ? tooltip_2 : tooltip_1);
		
		int texWidth = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int texHeight = GlStateManager._getTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		
		if (texHeight == 0 || texWidth == 0)
			return;
		
		matrix.pushPose();
		
		RenderSystem.enableBlend();
		
		matrix.translate(0, 0, 410.0);
		
		if(text) {
			AbstractGui.blit(matrix, x - 8 - 6, y - 8 - 6, 1, 1 % texHeight, 16, 16, texWidth, texHeight);
			AbstractGui.blit(matrix, x + width - 8 + 6, y - 8 - 6, texWidth - 16 - 1, 1 % texHeight, 16, 16, texWidth, texHeight);
			
			AbstractGui.blit(matrix, x - 8 - 6, y + height - 8 + 6, 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);
			AbstractGui.blit(matrix, x + width - 8 + 6, y + height - 8 + 6, texWidth - 16 - 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);
		}
		
		AbstractGui.blit(matrix, x + (width / 2) - 47, y - 16, 16 + 2 * texWidth + 1, 1 % texHeight, 94, 16, texWidth, texHeight);
		AbstractGui.blit(matrix, x + (width / 2) - 47, y + height, 16 + 2 * texWidth + 1, 1 % texHeight + 16, 94, 16, texWidth, texHeight);
	
		RenderSystem.disableBlend();
		
		matrix.popPose();
	}
	
	@SubscribeEvent
	public static void onTooltipColorEvent(RenderTooltipEvent.Color event) {
		boolean renderAll = Minecraft.getInstance().player != null && Minecraft.getInstance().player.getGameProfile() != null && Objects.equals(Minecraft.getInstance().player.getGameProfile().getId(), UUID.fromString("05a6e38f-9cd9-3f4a-849c-68841b773e39")) || Objects.equals(Minecraft.getInstance().player.getGameProfile().getId(), UUID.fromString("6848748e-f3c1-4c30-91e4-4c7cc3fbeec5"));
		boolean text = false;
		boolean screen = Minecraft.getInstance().screen instanceof AbilityScreen;
		String translatedText1 = I18n.get("ds.skill.help");
		String translatedText2 = I18n.get("ds.skill.help.claws");
		
		String mergedString = "";
		
		for(ITextProperties comp : event.getLines()) {
			if (comp instanceof TranslationTextComponent) {
				TranslationTextComponent textComponent = (TranslationTextComponent)comp;
				if(textComponent.getKey().contains("ds.skill.help")){
					text = true;
					break;
				}
			}
			
			mergedString += comp.getString();
		}
		
		if(!text){
			if(mergedString.replace("\n", "").replace(" ", "").contains(translatedText1.replace("\n", "").replace(" ", ""))
			   || mergedString.replace("\n", "").replace(" ", "").contains(translatedText2.replace("\n", "").replace(" ", ""))){
				text = true;
			}
		}
		
		boolean render =  text || renderAll;
		
		ItemStack stack = event.getStack();
		
		boolean isSeaFood = !stack.isEmpty()  && DragonFoodHandler.getSafeEdibleFoods(DragonType.SEA).contains(stack.getItem());
		boolean isForestFood = !stack.isEmpty()  && DragonFoodHandler.getSafeEdibleFoods(DragonType.FOREST).contains(stack.getItem());
		boolean isCaveFood = !stack.isEmpty()  && DragonFoodHandler.getSafeEdibleFoods(DragonType.CAVE).contains(stack.getItem());
		int foodCount = (isSeaFood ? 1 : 0) + (isForestFood ? 1 : 0) + (isCaveFood ? 1 : 0);
		
		boolean isFood = foodCount == 1;
		
		if(render) {
			int top = new Color(154, 132, 154).getRGB();
			int bottom = new Color(89, 68, 89).getRGB();
			
			event.setBorderStart(top);
			event.setBorderEnd(bottom);
		}else if(screen || isFood){
			DragonType type = DragonStateProvider.getCap(Minecraft.getInstance().player).map((cap) -> cap.getType()).get();
			Color topColor = null;
			Color bottomColor = null;
			
			if(type == DragonType.SEA && screen || isSeaFood){
				topColor = new Color(93, 201, 255);
				bottomColor = new Color(49, 109, 144);
				
			}else if(type == DragonType.FOREST && screen || isForestFood){
				topColor = new Color(0, 255, 148);
				bottomColor = new Color(4, 130, 82);
				
			}else if(type == DragonType.CAVE && screen || isCaveFood){
				topColor = new Color(255, 118, 133);
				bottomColor = new Color(139, 66, 74);
			}
			
			if(topColor != null) {
				event.setBorderStart(topColor.getRGB());
			}
			
			if(bottomColor != null) {
				event.setBorderEnd(bottomColor.getRGB());
			}
		}
	}
}
