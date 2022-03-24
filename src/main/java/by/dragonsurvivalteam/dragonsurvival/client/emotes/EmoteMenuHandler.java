package by.dragonsurvivalteam.dragonsurvival.client.emotes;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.EmoteCap;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.emotes.SyncEmote;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber( Dist.CLIENT )
public class EmoteMenuHandler{
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
	private static int emotePage = 0;
	private static boolean keybinding = false;
	private static String currentlyKeybinding = null;

	@SubscribeEvent
	public static void addEmoteButton(GuiScreenEvent.InitGuiEvent.Post initGuiEvent){
		Screen sc = initGuiEvent.getGui();
		currentlyKeybinding = null;
		if(sc instanceof ChatScreen && DragonUtils.isDragon(Minecraft.getInstance().player)){
			ChatScreen screen = (ChatScreen)sc;

			emotePage = MathHelper.clamp(emotePage, 0, maxPages() - 1);
			List<Emote> emotes = getEmotes();

			if(emotes == null || emotes.size() <= 0){
				return;
			}

			int width = 160;
			int height = 10;

			int startX = screen.width - width;
			int startY = screen.height - 55;

			startX += ConfigHandler.CLIENT.emoteXOffset.get();
			startY += ConfigHandler.CLIENT.emoteYOffset.get();

			initGuiEvent.addWidget(new Button(startX, startY - ((PER_PAGE + 2) * height) - 5, width, height, new StringTextComponent(">"), (btn) -> {
			}){
				@Override
				public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks){
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
					this.active = this.visible = (handler != null && handler.getEmotes().emoteMenuOpen);
					if(handler == null || !handler.getEmotes().emoteMenuOpen){
						return;
					}
					int color = new Color(0.15F, 0.15F, 0.15F, 0.75F).getRGB();
					AbstractGui.fill(stack, x, y, x + this.width, y + this.height, color);

					int j = getFGColor();
					drawCenteredString(stack, Minecraft.getInstance().font, (emotePage + 1) + "/" + maxPages(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
				}

				@Override
				public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_){return false;}
			});

			initGuiEvent.addWidget(new Button(startX + ((width / 4) - 10), startY - ((PER_PAGE + 2) * height) - 5, 15, height, null, (btn) -> {
				if(emotePage > 0){
					emotePage = MathHelper.clamp(emotePage - 1, 0, maxPages() - 1);
					emotes.clear();
					emotes.addAll(getEmotes());
				}
				currentlyKeybinding = null;

			}){
				@Override
				public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks){
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
					this.active = this.visible = (handler != null && handler.getEmotes().emoteMenuOpen);
					if(handler == null || !handler.getEmotes().emoteMenuOpen){
						return;
					}

					if(isHovered){
						AbstractGui.fill(stack, x, y, x + this.width, y + this.height, new Color(0.35F, 0.35F, 0.35F, 0.75F).getRGB());
					}

					Minecraft.getInstance().getTextureManager().bind(BUTTON_LEFT);

					RenderSystem.pushMatrix();
					RenderSystem.scalef(0.25F, 0.25F, 0F);
					RenderSystem.translatef(x * 3, y * 3, 0);
					RenderSystem.translatef(15, (height / 2) - 2, 0);

					blit(stack, x, y, 0, 0, 32, 32, 32, 32);

					RenderSystem.popMatrix();
				}
			});

			initGuiEvent.addWidget(new Button(startX + width - ((width / 4) + 5), startY - ((PER_PAGE + 2) * height) - 5, 15, height, null, (btn) -> {
				if(emotePage < (maxPages() - 1)){
					emotePage = MathHelper.clamp(emotePage + 1, 0, maxPages() - 1);
					emotes.clear();
					emotes.addAll(getEmotes());
				}
				currentlyKeybinding = null;

			}){
				@Override
				public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks){
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
					this.active = this.visible = (handler != null && handler.getEmotes().emoteMenuOpen);
					if(handler == null || !handler.getEmotes().emoteMenuOpen){
						return;
					}

					if(isHovered){
						AbstractGui.fill(stack, x, y, x + this.width, y + this.height, new Color(0.35F, 0.35F, 0.35F, 0.75F).getRGB());
					}

					Minecraft.getInstance().getTextureManager().bind(BUTTON_RIGHT);

					RenderSystem.pushMatrix();
					RenderSystem.scalef(0.25F, 0.25F, 0F);
					RenderSystem.translatef(x * 3, y * 3, 0);
					RenderSystem.translatef(20, (height / 2) - 2, 0);

					blit(stack, x, y, 0, 0, 32, 32, 32, 32);

					RenderSystem.popMatrix();
				}
			});

			initGuiEvent.addWidget(new Button(startX, startY, width, height, new StringTextComponent(">"), (btn) -> {
				DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
				handler.getEmotes().emoteMenuOpen = !handler.getEmotes().emoteMenuOpen;
				NetworkHandler.CHANNEL.sendToServer(new SyncEmote(Minecraft.getInstance().player.getId(), handler.getEmotes()));
				currentlyKeybinding = null;

			}){
				@Override
				public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks){
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
					DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
					int color = isHovered ? new Color(0.35F, 0.35F, 0.35F, 0.75F).getRGB() : new Color(0.15F, 0.15F, 0.15F, 0.75F).getRGB();
					AbstractGui.fill(stack, x, y, x + this.width, y + this.height, color);

					int j = getFGColor();
					drawCenteredString(stack, Minecraft.getInstance().font, new TranslationTextComponent("ds.emote.toggle"), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);


					RenderSystem.pushMatrix();
					RenderSystem.scalef(0.25F, 0.25F, 0F);
					RenderSystem.translatef(x * 3, y * 3, 0);
					RenderSystem.translatef(15, (height / 2), 0);

					if(handler.getEmotes().emoteMenuOpen){
						Minecraft.getInstance().getTextureManager().bind(BUTTON_UP);
						blit(stack, x, y, 0, 0, 32, 32, 32, 32);
					}else{
						Minecraft.getInstance().getTextureManager().bind(BUTTON_DOWN);
						blit(stack, x, y, 0, 0, 32, 32, 32, 32);
					}

					RenderSystem.popMatrix();
				}
			});

			for(int i = 0; i < PER_PAGE; i++){
				int finalI = i;
				initGuiEvent.addWidget(new Button(startX, startY - 20 - (height * ((PER_PAGE - 1) - finalI)), width, height, null, (btn) -> {
					DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
					Emote emote = emotes.size() > finalI ? emotes.get(finalI) : null;

					if(emote == null || handler.getEmotes().currentEmotes.stream().anyMatch((s) -> Objects.equals(s.animation, emote.animation))){
						return;
					}

					if(emote.blend && handler.getEmotes().currentEmotes.stream().anyMatch((s) -> s.blend)
					|| !emote.blend && handler.getEmotes().currentEmotes.stream().anyMatch((s) -> !s.blend)){
						clearEmotes();
					}

					addEmote(emote);
				}){
					@Override
					public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick){
						DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
						this.active = this.visible = handler.getEmotes().emoteMenuOpen;
						this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
						if(!handler.getEmotes().emoteMenuOpen){
							return;
						}
						int color = isHovered && emotes.size() > finalI ? new Color(0.1F, 0.1F, 0.1F, 0.8F).getRGB() : new Color(0.1F, 0.1F, 0.1F, 0.5F).getRGB();
						AbstractGui.fill(stack, x, y, x + this.width, y + this.height, color);

						Emote emote = emotes.size() > finalI ? emotes.get(finalI) : null;

						if(emote != null){
							RenderSystem.pushMatrix();
							drawString(stack, Minecraft.getInstance().font, new TranslationTextComponent(emote.name), this.x + 22, this.y + (this.height - 8) / 2, Color.lightGray.getRGB());
							RenderSystem.popMatrix();

							Minecraft.getInstance().getTextureManager().bind(emote.loops ? PLAY_LOOPED : PLAY_ONCE);
							blit(stack, x, y, 0, 0, 10, 10, 10, 10);

							Minecraft.getInstance().getTextureManager().bind(emote.sound != null ? SOUND : NO_SOUND);
							blit(stack, x + 10, y, 0, 0, 10, 10, 10, 10);
						}
					}
				});

				initGuiEvent.addWidget(new ExtendedButton(startX - 70, startY - 20 - (height * ((PER_PAGE - 1) - finalI)), 60, height, StringTextComponent.EMPTY, (btn) -> {
					Emote emote = emotes.size() > finalI ? emotes.get(finalI) : null;

					if(emote != null){
						currentlyKeybinding = emote.id;
					}
				}){
					@Override
					public boolean mouseClicked(double pMouseX, double pMouseY, int pButton){
						if(pButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT){
							DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
							Emote emote = emotes.size() > finalI ? emotes.get(finalI) : null;

							if(emote != null){
								handler.getEmotes().emoteKeybinds.remove(emote.id);
								NetworkHandler.CHANNEL.sendToServer(new SyncEmote(Minecraft.getInstance().player.getId(), handler.getEmotes()));
								return true;
							}
						}
						return super.mouseClicked(pMouseX, pMouseY, pButton);
					}

					@Override
					public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick){
						DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
						this.active = this.visible = handler.getEmotes().emoteMenuOpen && keybinding;
						this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

						if(!handler.getEmotes().emoteMenuOpen || !keybinding){
							return;
						}

						int color = isHovered && emotes.size() > finalI ? new Color(0.1F, 0.1F, 0.1F, 0.8F).getRGB() : new Color(0.1F, 0.1F, 0.1F, 0.5F).getRGB();
						AbstractGui.fill(stack, x, y, x + this.width, y + this.height, color);


						Emote emote = emotes.size() > finalI ? emotes.get(finalI) : null;

						if(emote != null){
							if(Objects.equals(currentlyKeybinding, emote.id)){
								RenderingUtils.drawRect(stack, x, y, width-1, height, new Color(0.1F, 0.1F, 0.1F, 0.8F).getRGB());
								TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 1, 1f, "...", -1);
							}else if(handler.getEmotes().emoteKeybinds.containsKey(emote.id)){
								int id = handler.getEmotes().emoteKeybinds.get(emote.id);
								if(id != 0){
									Input input = InputMappings.Type.KEYSYM.getOrCreate(id);
									TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 1, 1f, input.getDisplayName(), -1);
								}
							}
						}
					}
				});
			}

			initGuiEvent.addWidget(new ExtendedButton(startX + (width / 2) - (width / 4), startY - height, width / 2, height, StringTextComponent.EMPTY, (btn) -> {
				keybinding = !keybinding;
				currentlyKeybinding = null;
			}){
				@Override
				public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick){
					DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
					this.active = this.visible = handler.getEmotes().emoteMenuOpen;
					this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

					if(!handler.getEmotes().emoteMenuOpen){
						return;
					}

					int color = isHovered  ? new Color(0.1F, 0.1F, 0.1F, 0.8F).getRGB() : new Color(0.1F, 0.1F, 0.1F, 0.5F).getRGB();
					AbstractGui.fill(stack, x, y, x + this.width, y + this.height, color);

					int j = getFGColor();
					drawCenteredString(stack, Minecraft.getInstance().font, new TranslationTextComponent("ds.emote.keybinds"), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
				}
			});
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onKey(InputEvent.KeyInputEvent keyInputEvent){
		Screen sc = Minecraft.getInstance().screen;
		int pKeyCode = keyInputEvent.getKey();

		if(DragonUtils.isDragon(Minecraft.getInstance().player)){
			if(sc instanceof ChatScreen){
				DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
				if(currentlyKeybinding != null){
					if(pKeyCode == 256){
						handler.getEmotes().emoteKeybinds.remove(currentlyKeybinding);
					}else{
						handler.getEmotes().emoteKeybinds.put(currentlyKeybinding, keyInputEvent.getKey());
					}
					NetworkHandler.CHANNEL.sendToServer(new SyncEmote(Minecraft.getInstance().player.getId(), handler.getEmotes()));
					currentlyKeybinding = null;
				}
			}else{
				DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
				if(handler.getEmotes().emoteKeybinds.contains(pKeyCode)){
					Map.Entry<String, Integer> entry = handler.getEmotes().emoteKeybinds.entrySet().stream().filter((s) -> s.getValue() == pKeyCode).findFirst().orElse(null);
					if(entry != null){
						Emote emote = EmoteRegistry.EMOTES.stream().filter((s) -> Objects.equals(s.id, entry.getKey())).findFirst().orElse(null);

						if(emote == null || handler.getEmotes().currentEmotes.stream().anyMatch((s) -> Objects.equals(s.animation, emote.animation))){
							return;
						}

						if(emote.blend && handler.getEmotes().currentEmotes.stream().anyMatch((s) -> s.blend) || !emote.blend && handler.getEmotes().currentEmotes.stream().anyMatch((s) -> !s.blend)){
							clearEmotes();
						}

						addEmote(emote);
					}
				}
			}
		}
	}


	public static void clearEmotes(){
		if(Minecraft.getInstance().player == null) return;

		DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);
		handler.getEmotes().currentEmotes.clear();
		handler.getEmotes().emoteTicks.clear();
		NetworkHandler.CHANNEL.sendToServer(new SyncEmote(Minecraft.getInstance().player.getId(), handler.getEmotes()));
	}

	public static void addEmote(Emote emote){
		DragonStateHandler cap = DragonUtils.getHandler(Minecraft.getInstance().player);
		cap.getEmotes().currentEmotes.add(0, emote);
		List<Emote> ls1 = cap.getEmotes().currentEmotes.stream().limit(EmoteCap.MAX_EMOTES).collect(Collectors.toList());
		List<Integer> ls2 = cap.getEmotes().emoteTicks.stream().limit(EmoteCap.MAX_EMOTES).collect(Collectors.toList());

		cap.getEmotes().currentEmotes.clear();
		cap.getEmotes().currentEmotes.addAll(ls1);

		cap.getEmotes().emoteTicks.clear();
		cap.getEmotes().emoteTicks.addAll(ls2);

		NetworkHandler.CHANNEL.sendToServer(new SyncEmote(Minecraft.getInstance().player.getId(), cap.getEmotes()));
	}

	public static List<Emote> getEmotes(){
		DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);

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

					if(!hasType){
						return true;
					}
				}

				if(em.requirements.age != null){
					boolean hasAge = false;
					for(String t : em.requirements.age){
						if(t.equalsIgnoreCase(handler.getLevel().name)){
							hasAge = true;
							break;
						}
					}

					return !hasAge;
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

	private static int createMap(int num, HashMap<Integer, ArrayList<Emote>> list, Emote emote){
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