package by.dragonsurvivalteam.dragonsurvival.client.gui;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.TabButton;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.DragonSkins;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SyncDragonSkinSettings;
import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;
import software.bernie.geckolib3.core.processor.IBone;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class SkinsScreen extends Screen{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/skin_interface.png");

	private static final ResourceLocation UNCHECKED = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/unchecked.png");
	private static final ResourceLocation CHECKED = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_claws_tetris.png");

	private static final ResourceLocation DISCORD = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/discord_button.png");
	private static final ResourceLocation WIKI = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/wiki_button.png");
	private static final ResourceLocation HELP = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_claws_button.png");

	private static final String DISCORD_URL = "http://discord.gg/8SsB8ar";
	private static final String WIKI_URL = "https://dragons-survival.fandom.com/wiki/Skins";
	public static ResourceLocation skinTexture = null;
	public static ResourceLocation glowTexture = null;
	private static String playerName = null;
	private static String lastPlayerName = null;
	private static DragonLevel level = DragonLevel.ADULT;
	private static final ArrayList<String> seenSkins = new ArrayList<>();
	private static boolean noSkin = false;
	private static boolean loading = false;
	public Screen sourceScreen;
	private int guiLeft;
	private int guiTop;
	private float yRot = -3;
	private float xRot = -5;
	private float zoom = 0;
	private final DragonStateHandler handler = new DragonStateHandler();
	private URI clickedLink;
	protected int imageWidth = 164;
	protected int imageHeight = 128;

	public SkinsScreen(Screen sourceScreen){
		super(new StringTextComponent(""));
		this.sourceScreen = sourceScreen;
	}

	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks){
		if(this.minecraft == null){
			return;
		}

		RenderSystem.pushMatrix();
		RenderSystem.translatef(0F, 0F, -500);
		this.renderBackground(stack);
		RenderSystem.popMatrix();

		int startX = this.guiLeft;
		int startY = this.guiTop;

		stack.pushPose();
		final IBone neckandHead = ClientDragonRender.dragonModel.getAnimationProcessor().getBone("Neck");

		if(neckandHead != null){
			neckandHead.setHidden(false);
		}

		DragonEntity dragon = FakeClientPlayerUtils.getFakeDragon(0, this.handler);
		EntityRenderer<? super DragonEntity> dragonRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dragon);

		if(noSkin && Objects.equals(playerName, minecraft.player.getGameProfile().getName())){
			ClientDragonRender.dragonModel.setCurrentTexture(null);
			((DragonRenderer)dragonRenderer).glowTexture = null;
		}else{
			ClientDragonRender.dragonModel.setCurrentTexture(skinTexture);
			((DragonRenderer)dragonRenderer).glowTexture = glowTexture;
		}

		float scale = zoom;
		stack.scale(scale, scale, scale);

		if(!loading){
			this.handler.setHasWings(true);
			this.handler.setSize(level.size);
			this.handler.setType(DragonUtils.getDragonType(minecraft.player));

			this.handler.getSkin().skinPreset.initDefaults(this.handler);

			if(noSkin && Objects.equals(playerName, minecraft.player.getGameProfile().getName())){
				this.handler.getSkin().skinPreset.readNBT(DragonUtils.getHandler(minecraft.player).getSkin().skinPreset.writeNBT());;
			}else{
				this.handler.getSkin().skinPreset.skinAges.get(level).defaultSkin = true;
			}

			FakeClientPlayerUtils.getFakePlayer(0, this.handler).animationSupplier = () -> "fly";
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0, 0, 100);
			ClientDragonRender.renderEntityInInventory(dragon, startX + 15, startY + 70, scale, xRot, yRot);
			RenderSystem.popMatrix();
		}

		((DragonRenderer)dragonRenderer).glowTexture = null;

		stack.popPose();

		RenderSystem.translatef(0F, 0F, 400);

		this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
		blit(stack, startX + 128, startY, 0, 0, 164, 256);

		drawNonShadowString(stack, minecraft.font, new TranslationTextComponent("ds.gui.skins").withStyle(TextFormatting.DARK_GRAY), startX + 128 + ((imageWidth) / 2), startY + 7, -1);
		drawCenteredString(stack, minecraft.font, new TranslationTextComponent("ds.gui.skins.toggle"), startX + 128 + ((imageWidth) / 2), startY + 30, -1);

		drawNonShadowString(stack, minecraft.font, new StringTextComponent(playerName + " - " + level.getName()).withStyle(TextFormatting.GRAY), startX + 15, startY - 15, -1);

		if(!loading){
			if(noSkin){
				if(playerName == minecraft.player.getGameProfile().getName()){
					drawNonShadowLineBreak(stack, minecraft.font, new TranslationTextComponent("ds.gui.skins.noskin.yours").withStyle(TextFormatting.DARK_GRAY), startX + 40, startY + this.imageHeight - 20, -1);
				}else{
					drawNonShadowLineBreak(stack, minecraft.font, new TranslationTextComponent("ds.gui.skins.noskin").withStyle(TextFormatting.DARK_GRAY), startX + 65, startY + this.imageHeight - 20, -1);
				}
			}
		}

		super.render(stack, mouseX, mouseY, partialTicks);

		for(Widget btn : buttons){
			if(btn.isHovered()){
				btn.renderToolTip(stack, mouseX, mouseY);
			}
		}

		RenderSystem.translatef(0F, 0F, -400f);
	}

	@Override
	public void init(Minecraft p_231158_1_, int width, int height){
		super.init(p_231158_1_, width, height);

		int startX = this.guiLeft;
		int startY = this.guiTop;

		addButton(new TabButton(startX + 128 + 5, startY - 26, 0, this));
		addButton(new TabButton(startX + 128 + 33, startY - 26, 1, this));
		addButton(new TabButton(startX + 128 + 62, startY - 26, 2, this));
		addButton(new TabButton(startX + 128 + 91, startY - 28, 3, this));

		addButton(new Button(startX + 128, startY + 45, imageWidth, 20, new TranslationTextComponent("ds.level.newborn"), (button) -> {
			DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);

			if(handler != null){
				handler.getSkin().renderNewborn = !handler.getSkin().renderNewborn;
				NetworkHandler.CHANNEL.sendToServer(new SyncDragonSkinSettings(getMinecraft().player.getId(), handler.getSkin().renderNewborn, handler.getSkin().renderYoung, handler.getSkin().renderAdult));
				//				executor.execute(() -> setTextures());
				setTextures();
			}
		}){
			@Override
			public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);

				DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);
				minecraft.getTextureManager().bind(handler == null || !handler.getSkin().renderNewborn ? UNCHECKED : CHECKED);
				blit(p_230431_1_, x + 3, y + 3, 0, 0, 13, 13, 13, 13);
			}
		});

		addButton(new Button(startX + 128, startY + 45 + 23, imageWidth, 20, new TranslationTextComponent("ds.level.young"), (button) -> {
			DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);

			if(handler != null){
				handler.getSkin().renderYoung = !handler.getSkin().renderYoung;
				NetworkHandler.CHANNEL.sendToServer(new SyncDragonSkinSettings(getMinecraft().player.getId(), handler.getSkin().renderNewborn, handler.getSkin().renderYoung, handler.getSkin().renderAdult));
				//				executor.execute(() -> setTextures());
				setTextures();
			}
		}){
			@Override
			public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);

				DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);
				minecraft.getTextureManager().bind(handler == null || !handler.getSkin().renderYoung ? UNCHECKED : CHECKED);
				blit(p_230431_1_, x + 3, y + 3, 0, 0, 13, 13, 13, 13);
			}
		});

		addButton(new Button(startX + 128, startY + 45 + 46, imageWidth, 20, new TranslationTextComponent("ds.level.adult"), (button) -> {
			DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);

			if(handler != null){
				handler.getSkin().renderAdult = !handler.getSkin().renderAdult;
				NetworkHandler.CHANNEL.sendToServer(new SyncDragonSkinSettings(getMinecraft().player.getId(), handler.getSkin().renderNewborn, handler.getSkin().renderYoung, handler.getSkin().renderAdult));
				//				executor.execute(() -> setTextures());
				setTextures();
			}
		}){
			@Override
			public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);

				DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);
				minecraft.getTextureManager().bind(handler == null || !handler.getSkin().renderAdult ? UNCHECKED : CHECKED);
				blit(p_230431_1_, x + 3, y + 3, 0, 0, 13, 13, 13, 13);
			}
		});

		addButton(new Button(startX + 128, startY + 128, imageWidth, 20, new TranslationTextComponent("ds.gui.skins.other_skins"), (button) -> {
			ConfigHandler.CLIENT.renderOtherPlayerSkins.set(!ConfigHandler.CLIENT.renderOtherPlayerSkins.get());
			//			executor.execute(() -> setTextures());
			setTextures();
		}){
			@Override
			public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);

				minecraft.getTextureManager().bind(ConfigHandler.CLIENT.renderOtherPlayerSkins.get() ? CHECKED : UNCHECKED);
				blit(p_230431_1_, x + 3, y + 3, 0, 0, 13, 13, 13, 13);
			}
		});

		addButton(new Button(startX + 128 + (imageWidth / 2) - 8, startY + 128 + 30, 16, 16, new StringTextComponent(""), (button) -> {
			try{
				URI uri = new URI(DISCORD_URL);
				this.clickedLink = uri;
				this.minecraft.setScreen(new ConfirmOpenLinkScreen(this::confirmLink, DISCORD_URL, false));
			}catch(URISyntaxException urisyntaxexception){
				urisyntaxexception.printStackTrace();
			}
		}){
			@Override
			public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				minecraft.getTextureManager().bind(DISCORD);
				blit(p_230431_1_, x, y, 0, 0, 16, 16, 16, 16);
			}

			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_){
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.skins.tooltip.discord")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
		});

		addButton(new Button(startX + 128 + (imageWidth / 2) - 8 + 25, startY + 128 + 30, 16, 16, new StringTextComponent(""), (button) -> {
			try{
				URI uri = new URI(WIKI_URL);
				this.clickedLink = uri;
				this.minecraft.setScreen(new ConfirmOpenLinkScreen(this::confirmLink, WIKI_URL, false));
			}catch(URISyntaxException urisyntaxexception){
				urisyntaxexception.printStackTrace();
			}
		}){
			@Override
			public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				minecraft.getTextureManager().bind(WIKI);
				blit(p_230431_1_, x, y, 0, 0, 16, 16, 16, 16);
			}

			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_){
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.skins.tooltip.wiki")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
		});

		addButton(new HelpButton(startX + 128 + (imageWidth / 2) - 8 - 25, startY + 128 + 30, 16, 16, "ds.gui.skins.tooltip.help", 1));

		addButton(new Button(startX - 60, startY + 128, 90, 20, new TranslationTextComponent("ds.gui.skins.yours"), (button) -> {
			playerName = minecraft.player.getGameProfile().getName();
			setTextures();
		}){
			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_){
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.skins.tooltip.yours")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
		});

		addButton(new Button(startX + 35, startY + 128, 60, 20, new TranslationTextComponent("ds.gui.skins.random"), (button) -> {
			ArrayList<Pair<DragonLevel, String>> skins = new ArrayList<>();
			ArrayList<String> users = new ArrayList<>();
			Random random = new Random();

			for(Map.Entry<DragonLevel, ArrayList<String>> ent : DragonSkins.SKIN_USERS.entrySet()){
				for(String user : ent.getValue()){
					skins.add(Pair.of(ent.getKey(), user));

					if(!users.contains(user)){
						users.add(user);
					}
				}
			}

			skins.removeIf((c) -> seenSkins.contains(c.second));
			if(skins.size() > 0){
				Pair<DragonLevel, String> skin = skins.get(random.nextInt(skins.size()));

				if(skin != null){
					level = skin.first;
					playerName = skin.second;

					seenSkins.add(skin.second);

					if(seenSkins.size() >= users.size() / 2){
						seenSkins.remove(0);
					}

					//					executor.execute(() -> setTextures());
					setTextures();
				}
			}
		}){
			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_){
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.skins.tooltip.random")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
		});

		addButton(new Button(startX + 90, startY + 10, 11, 17, new StringTextComponent(""), (button) -> {
			int pos = MathHelper.clamp(level.ordinal() + 1, 0, DragonLevel.values().length - 1);
			level = DragonLevel.values()[pos];

			//			executor.execute(() -> setTextures());
			setTextures();
		}){
			@Override
			public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_){
				Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);

				if(isHovered()){
					blit(stack, x, y, 66 / 2, 222 / 2, 11, 17, 128, 128);
				}else{
					blit(stack, x, y, 44 / 2, 222 / 2, 11, 17, 128, 128);
				}
			}
		});

		addButton(new Button(startX - 70, startY + 10, 11, 17, new StringTextComponent(""), (button) -> {
			int pos = MathHelper.clamp(level.ordinal() - 1, 0, DragonLevel.values().length - 1);
			level = DragonLevel.values()[pos];

			//			executor.execute(() -> setTextures());
			setTextures();
		}){
			@Override
			public void renderButton(MatrixStack stack, int mouseX, int mouseY, float p_230431_4_){
				Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);

				if(isHovered()){
					blit(stack, x, y, 22 / 2, 222 / 2, 11, 17, 128, 128);
				}else{
					blit(stack, x, y, 0, 222 / 2, 11, 17, 128, 128);
				}
			}
		});
	}

	@Override
	protected void init(){
		super.init();

		this.guiLeft = (this.width - 256) / 2;
		this.guiTop = (this.height - 128) / 2;

		if(playerName == null){
			playerName = minecraft.player.getGameProfile().getName();
		}

		setTextures();
	}

	public void setTextures(){
		loading = true;

		ResourceLocation skinTexture = DragonSkins.getPlayerSkin(playerName + "_" + level.name);
		ResourceLocation glowTexture = null;
		boolean defaultSkin = false;

		if((!DragonSkins.renderStage(minecraft.player, level) && playerName == minecraft.player.getGameProfile().getName()) || skinTexture == null){
			skinTexture = null;
			defaultSkin = true;
		}

		if(skinTexture != null){
			if(!defaultSkin){
				glowTexture = DragonSkins.getPlayerGlow(playerName + "_" + level.name);
			}
		}

		SkinsScreen.glowTexture = glowTexture;
		SkinsScreen.skinTexture = skinTexture;

		if(Objects.equals(lastPlayerName, playerName) || lastPlayerName == null){
			zoom = level.size;
		}

		noSkin = defaultSkin;
		loading = false;
		lastPlayerName = playerName;
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}

	private void confirmLink(boolean p_231162_1_){
		if(p_231162_1_){
			this.openLink(this.clickedLink);
		}

		this.clickedLink = null;
		this.minecraft.setScreen(this);
	}

	private void openLink(URI p_231156_1_){
		Util.getPlatform().openUri(p_231156_1_);
	}

	public static void drawNonShadowString(MatrixStack p_238472_0_, FontRenderer p_238472_1_, ITextComponent p_238472_2_, int p_238472_3_, int p_238472_4_, int p_238472_5_){
		p_238472_1_.draw(p_238472_0_, LanguageMap.getInstance().getVisualOrder(p_238472_2_), p_238472_3_ - p_238472_1_.width(p_238472_2_) / 2, p_238472_4_, p_238472_5_);
	}

	public static void drawNonShadowLineBreak(MatrixStack p_238472_0_, FontRenderer p_238472_1_, ITextComponent p_238472_2_, int p_238472_3_, int p_238472_4_, int p_238472_5_){
		IReorderingProcessor ireorderingprocessor = p_238472_2_.getVisualOrderText();

		List<ITextProperties> wrappedLine = p_238472_1_.getSplitter().splitLines(p_238472_2_, 150, Style.EMPTY);
		int i = 0;
		for(ITextProperties properties : wrappedLine){
			p_238472_1_.draw(p_238472_0_, LanguageMap.getInstance().getVisualOrder(properties), p_238472_3_ - p_238472_1_.width(ireorderingprocessor) / 2, p_238472_4_ + i * 9, p_238472_5_);
			i++;
		}
	}

	@Override
	public boolean mouseDragged(double x1, double y1, int p_231045_5_, double x2, double y2){
		xRot -= x2 / 5;
		yRot -= y2 / 5;

		//		xRot = MathHelper.clamp(xRot, -17, 17);
		//		yRot = MathHelper.clamp(yRot, -17, 17);

		return super.mouseDragged(x1, y1, p_231045_5_, x2, y2);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount){
		zoom += (float)amount;
		zoom = MathHelper.clamp(zoom, 10, 80);

		return true;
	}
}