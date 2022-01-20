package by.jackraidenph.dragonsurvival.client.emotes;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteServer;
import by.jackraidenph.dragonsurvival.network.emotes.SyncEmoteStatsServer;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent.InitScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber( Dist.CLIENT)
public class EmoteMenuHandler
{
	private static int emotePage = 0;
	private static final int PER_PAGE = 10;
	
	private static final ResourceLocation EMPTY_SLOT = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/empty_slot.png");
	
	private static final ResourceLocation PLAY_ONCE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/play_once.png");
	private static final ResourceLocation PLAY_LOOPED = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/play_looped.png");
	
	private static final ResourceLocation SOUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/sound.png");
	private static final ResourceLocation NO_SOUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/no_sound.png");
	
	private static final ResourceLocation BUTTON_UP = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/button_up.png");
	private static final ResourceLocation BUTTON_DOWN = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/button_down.png");
	private static final ResourceLocation BUTTON_LEFT = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/button_left.png");
	private static final ResourceLocation BUTTON_RIGHT = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/emote/button_right.png");
	
	
	public static void setEmote(Emote emote){
		DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent((cap) -> cap.getEmotes().setCurrentEmote(emote));
		NetworkHandler.CHANNEL.sendToServer(new SyncEmoteServer(emote != null ? emote.id : "nil"));
	}
	
	@SubscribeEvent
	public static void addEmoteButton(InitScreenEvent.Post initGuiEvent)
	{
		Screen sc = initGuiEvent.getScreen();
		
		if(sc instanceof ChatScreen && DragonUtils.isDragon(Minecraft.getInstance().player)) {
			ChatScreen screen = (ChatScreen)sc;
			
			emotePage = Mth.clamp(emotePage, 0, maxPages() - 1);
			List<Emote> emotes = getEmotes();
			
			if(emotes == null || emotes.size() <= 0) return;
			
			int width = 160;
			int height = 10;
			
			int startX = screen.width - width;
			int startY = screen.height - 55;
			
			startX += ConfigHandler.CLIENT.emoteXOffset.get();
			startY += ConfigHandler.CLIENT.emoteYOffset.get();
			
			initGuiEvent.addListener(new Button(startX, startY - ((PER_PAGE + 2) * height) - 5, width, height, new TextComponent(">"), (btn) -> {
			}){
				@Override
				public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
				{
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
					this.active = this.visible = (handler != null && handler.getEmotes().emoteMenuOpen);
					if(handler == null || !handler.getEmotes().emoteMenuOpen) return;
					int color = new Color(0.15F, 0.15F, 0.15F, 0.75F).getRGB();
					Gui.fill(stack, x, y, x + this.width, y + this.height, color);
					
					int j = getFGColor();
					drawCenteredString(stack, Minecraft.getInstance().font, (emotePage + 1) +"/" + maxPages(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
				}
				
				@Override
				public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {return false;}
			});
			
			initGuiEvent.addListener(new Button(startX + ((width / 4) - 10), startY - ((PER_PAGE + 2) * height) - 5, 15, height, null, (btn) -> {
				if(emotePage > 0) {
					emotePage = Mth.clamp(emotePage - 1, 0, maxPages() - 1);
					emotes.clear();
					emotes.addAll(getEmotes());
				}
			}){
				@Override
				public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
				{
					stack.pushPose();
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
					this.active = this.visible = (handler != null && handler.getEmotes().emoteMenuOpen);
					if(handler == null || !handler.getEmotes().emoteMenuOpen) return;
					
					if(isHovered){
						Gui.fill(stack, x, y, x + this.width, y + this.height, new Color(0.35F, 0.35F, 0.35F, 0.75F).getRGB());
					}
					
					RenderSystem.setShaderTexture(0, BUTTON_LEFT);
					
					
					stack.scale(0.25F, 0.25F, 0F);
					stack.translate(x * 3, y * 3, 0);
					stack.translate(15, (height / 2) - 2, 0);
					
					blit(stack, x, y, 0, 0, 32, 32, 32, 32);
					stack.popPose();
				}
			});
			
			initGuiEvent.addListener(new Button(startX + width - ((width / 4) + 5), startY - ((PER_PAGE + 2) * height) - 5, 15, height, null, (btn) -> {
				if(emotePage < (maxPages() - 1)) {
					emotePage = Mth.clamp(emotePage + 1, 0, maxPages() - 1);
					emotes.clear();
					emotes.addAll(getEmotes());
				}
			}){
				@Override
				public void render(PoseStack  stack, int mouseX, int mouseY, float partialTicks)
				{
					stack.pushPose();
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
					this.active = this.visible = (handler != null && handler.getEmotes().emoteMenuOpen);
					if(handler == null || !handler.getEmotes().emoteMenuOpen) return;
					
					if(isHovered){
						Gui.fill(stack, x, y, x + this.width, y + this.height, new Color(0.35F, 0.35F, 0.35F, 0.75F).getRGB());
					}
					
					RenderSystem.setShaderTexture(0, BUTTON_RIGHT);
					
					
					stack.scale(0.25F, 0.25F, 0F);
					stack.translate(x * 3, y * 3, 0);
					stack.translate(20, (height / 2) - 2, 0);
					
					blit(stack, x, y, 0, 0, 32, 32, 32, 32);
					stack.popPose();
				}
			});
			
			initGuiEvent.addListener(new Button(startX, startY, width, height, new TextComponent(">"), (btn) -> {
				DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
				
				if(handler != null){
					handler.getEmotes().emoteMenuOpen = !handler.getEmotes().emoteMenuOpen;
					NetworkHandler.CHANNEL.sendToServer(new SyncEmoteStatsServer(handler.getEmotes().emoteMenuOpen));
				}
			}){
				@Override
				public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks)
				{
					stack.pushPose();
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
					if(handler == null) return;
					int color = isHovered ? new Color(0.35F, 0.35F, 0.35F, 0.75F).getRGB() : new Color(0.15F, 0.15F, 0.15F, 0.75F).getRGB();
					Gui.fill(stack, x, y, x + this.width, y + this.height, color);
					
					int j = getFGColor();
					drawCenteredString(stack, Minecraft.getInstance().font, new TranslatableComponent("ds.emote.toggle"), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
					
					
					stack.scale(0.25F, 0.25F, 0F);
					stack.translate(x * 3, y * 3, 0);
					stack.translate(15, (height / 2), 0);
					
					if(handler.getEmotes().emoteMenuOpen) {
						RenderSystem.setShaderTexture(0, BUTTON_UP);
						blit(stack, x, y, 0, 0, 32, 32, 32, 32);
					}else{
						RenderSystem.setShaderTexture(0, BUTTON_DOWN);
						blit(stack, x, y, 0, 0, 32, 32, 32, 32);
					}
					
					stack.popPose();
				}
			});
			
			for(int i = 0; i < PER_PAGE; i++){
				int finalI = i;
				initGuiEvent.addListener(new Button(startX, startY - 20 - (height * ((PER_PAGE-1) - finalI)), width, height, null, (btn) -> {
					DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
					Emote emote = emotes.size() > finalI ? emotes.get(finalI) : null;
					
					if(handler.getEmotes().getCurrentEmote() != null && emote != null && Objects.equals(handler.getEmotes().getCurrentEmote().id, emote.id)) return;
					
					if(handler != null && emote != null){
						setEmote(emote);
					}
				}){
					@Override
					public void render(PoseStack  stack, int mouseX, int mouseY, float partialTick)
					{
						DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
						this.active = this.visible = (handler != null && handler.getEmotes().emoteMenuOpen);
						this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
						if(handler == null || !handler.getEmotes().emoteMenuOpen) return;
						int color = isHovered && emotes.size() > finalI ? new Color(0.1F, 0.1F, 0.1F, 0.8F).getRGB() : new Color(0.1F, 0.1F, 0.1F, 0.5F).getRGB();
						Gui.fill(stack, x, y, x + this.width, y + this.height, color);
						
						Emote emote = emotes.size() > finalI ? emotes.get(finalI) : null;
						
						if(emote != null) {
							drawString(stack, Minecraft.getInstance().font, new TranslatableComponent(emote.name), this.x + 22, this.y + (this.height - 8) / 2, Color.lightGray.getRGB());
							
							RenderSystem.setShaderTexture(0, emote.loops ? PLAY_LOOPED : PLAY_ONCE);
							blit(stack, x, y, 0, 0, 10, 10, 10, 10);
							
							RenderSystem.setShaderTexture(0, emote.sound != null ? SOUND : NO_SOUND);
							blit(stack, x+10, y, 0, 0, 10, 10, 10, 10);
						}
					}
				});
			}
		}
	}
	
	public static List<Emote> getEmotes(){
		DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
		
		if(handler == null) return new ArrayList<>();
		
		ArrayList<Emote> emotes = new ArrayList<>();
		HashMap<Integer, ArrayList<Emote>> list = new HashMap<>();
		
		emotes.addAll(EmoteRegistry.EMOTES);
		
		emotes.removeIf((em) -> {
			if(em.requirements != null){
				if(em.requirements.type != null){
					boolean hasType = false;
					for(String t : em.requirements.type){
						if(t.equalsIgnoreCase(handler.getType().name())){
							hasType = true;
							break;
						}
					}
					
					if(!hasType) return true;
				}
				
				if(em.requirements.age != null){
					boolean hasAge = false;
					for(String t : em.requirements.age){
						if(t.equalsIgnoreCase(handler.getLevel().name)){
							hasAge = true;
							break;
						}
					}
					
					if(!hasAge) return true;
				}
				
				//TODO Add this when alternate models are added
//				if(em.requirements.model != null){
//					boolean hasModel = false;
//					for(String t : em.requirements.model){
//						if(t.toLowerCase().equals(handler.getModel().name.toLowerCase())){
//							hasModel = true;
//							break;
//						}
//					}
//
//					if(!hasModel) return true;
//				}
			}
			
			return false;
		});
		
		int num = 0;
		for(Emote emote : emotes){
			num = createMap(num, list, emote);
		}
		
		return list.size() > emotePage ? list.get(emotePage) : new ArrayList<>();
	}
	
	public static int maxPages(){
		int num = 0;
		HashMap<Integer, ArrayList<Emote>> list = new HashMap<>();
		
		for(Emote emote : EmoteRegistry.EMOTES){
			num = createMap(num, list, emote);
		}
		
		return list.keySet().size();
	}
	
	private static int createMap(int num, HashMap<Integer, ArrayList<Emote>> list, Emote emote)
	{
		if(!list.containsKey(num)){
			list.put(num, new ArrayList<>());
		}
		
		if(list.get(num).size() >= PER_PAGE){
			num++;
			list.put(num, new ArrayList<>());
		}
		
		list.get(num).add(emote);
		return num;
	}
}
