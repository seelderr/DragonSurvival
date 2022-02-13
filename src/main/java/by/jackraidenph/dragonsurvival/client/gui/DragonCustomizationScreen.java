package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationLayer;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.CustomizationRegistry;
import by.jackraidenph.dragonsurvival.client.SkinCustomization.DragonCustomizationHandler;
import by.jackraidenph.dragonsurvival.client.gui.widgets.CustomizationConfirmation;
import by.jackraidenph.dragonsurvival.client.gui.widgets.DragonUIRenderComponent;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.CustomizationSlotButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.DropDownButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.ColoredDropdownValueEntry;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownEntry;
import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.jackraidenph.dragonsurvival.client.util.TextRenderUtil;
import by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities.SkinCap;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.RequestClientData;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.SyncPlayerAllCustomization;
import by.jackraidenph.dragonsurvival.network.SynchronizationController;
import by.jackraidenph.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.jackraidenph.dragonsurvival.network.flight.SyncSpinStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncAltarCooldown;
import by.jackraidenph.dragonsurvival.util.Functions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class DragonCustomizationScreen extends Screen
{
	public DragonCustomizationScreen(Screen source)
	{
		this(source, DragonType.NONE);
	}
	
	public DragonCustomizationScreen(Screen source, DragonType type)
	{
		super(new TranslationTextComponent("ds.gui.customization"));
		this.source = source;
		this.type = type;
	}
	
	private int guiLeft;
	private int guiTop;
	
	private Screen source;
	
	public DragonLevel level = DragonLevel.ADULT;
	public DragonType type;
	
	public HashMap<DragonLevel, HashMap<CustomizationLayer, String>> map = new HashMap<>();
	
	public boolean confirmation = false;
	
	private String[] animations = {"sit", "idle", "fly", "swim_fast", "run"};
	private int curAnimation = 0;
	
	public int currentSelected;
	private int lastSelected;
	
	private boolean hasInit = false;
	
	private DragonUIRenderComponent dragonRender;
	public DragonStateHandler handler = new DragonStateHandler();
	
	public void update()
	{
		handler.getSkin().playerSkinLayers = map;
		handler.setSize(level.size);
		
		if (type != DragonType.NONE) {
			handler.setType(type);
		}
		
		if (currentSelected != lastSelected) {
			CustomizationRegistry.savedCustomizations.saved.computeIfAbsent(handler.getType(), (b) -> new HashMap<>());
			CustomizationRegistry.savedCustomizations.saved.get(handler.getType()).computeIfAbsent(currentSelected, (b) -> new HashMap<>());
			
			map = new HashMap<>();
			CustomizationRegistry.savedCustomizations.saved.get(handler.getType()).get(currentSelected).forEach((level, mp) -> {
				mp.forEach((layer, key) -> {
					map.computeIfAbsent(level, (b) -> new HashMap<>());
					map.get(level).put(layer, key);
				});
			});
			
			handler.getSkin().playerSkinLayers = map;
		}
		
		lastSelected = currentSelected;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		this.guiLeft = (this.width - 256) / 2;
		this.guiTop = (this.height - 120) / 2;
		
		dragonRender = new DragonUIRenderComponent(this, width / 2 - 100, guiTop, 200, 125, () -> FakeClientPlayerUtils.getFakeDragon(0, handler));
		children.add(dragonRender);
		
		DragonStateHandler handler = DragonStateProvider.getCap(getMinecraft().player).orElse(null);
		if(handler != null){
			level = handler.getLevel();
			dragonRender.zoom = level.size;
		}
		
		if (!hasInit) {
			if (handler != null) {
				level = handler.getLevel();
				dragonRender.zoom = level.size;
				
				if (type == DragonType.NONE) {
					type = handler.getType();
				}
				
				currentSelected = CustomizationRegistry.savedCustomizations.current.getOrDefault(type, new HashMap<>()).getOrDefault(level, 0);
				HashMap<DragonLevel, HashMap<CustomizationLayer, String>> mp1 = CustomizationRegistry.savedCustomizations.saved.getOrDefault(type, new HashMap<>()).getOrDefault(currentSelected, new HashMap<>());
				
				mp1.forEach((level, mp) -> {
					mp.forEach((layer, key) -> {
						map.computeIfAbsent(level, (b) -> new HashMap<>());
						map.get(level).put(layer, key);
					});
				});
			}
			
			this.handler.setHasWings(true);
			this.handler.setType(type);
			hasInit = true;
			update();
		}
		
		addButton(new HelpButton(type, guiLeft - 10, 10, 16, 16, "ds.help.customization"));
		
		addButton(new Button(width / 2 - 180, guiTop - 30, 120, 20, new TranslationTextComponent("ds.level.newborn"), (btn) -> {
			level = DragonLevel.BABY;
			dragonRender.zoom = level.size;
			update();
		})
		{
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.BABY ? 16777215 : 10526880;
				TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		addButton(new Button(width / 2 - 60, guiTop - 30, 120, 20, new TranslationTextComponent("ds.level.young"), (btn) -> {
			level = DragonLevel.YOUNG;
			dragonRender.zoom = level.size;
			update();
		})
		{
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.YOUNG ? 16777215 : 10526880;
				TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		addButton(new Button(width / 2 + 60, guiTop - 30, 120, 20, new TranslationTextComponent("ds.level.adult"), (btn) -> {
			level = DragonLevel.ADULT;
			dragonRender.zoom = level.size;
			update();
		})
		{
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				int j = isHovered || level == DragonLevel.ADULT ? 16777215 : 10526880;
				TextRenderUtil.drawCenteredScaledText(stack, x + (width / 2), y + 4, 1.5f, this.getMessage().getString(), j | MathHelper.ceil(this.alpha * 255.0F) << 24);
			}
		});
		
		int maxWidth = -1;
		
		for (CustomizationLayer layers : CustomizationLayer.values()) {
			String name = layers.name().substring(0, 1).toUpperCase(Locale.ROOT) + layers.name().toLowerCase().substring(1).replace("_", " ");
			maxWidth = (int)Math.max(maxWidth, font.width(name) * 1.45F);
		}
		
		int i = 0;
		for (CustomizationLayer layers : CustomizationLayer.values()) {
			ArrayList<String> valueList = DragonCustomizationHandler.getKeys(Minecraft.getInstance().player, layers);
			
			if(layers != CustomizationLayer.BASE){
				valueList.add(0, SkinCap.defaultSkinValue);
			}
			
			String[] values = valueList.toArray(new String[0]);
			String curValue = map.getOrDefault(level, new HashMap<>()).getOrDefault(layers, SkinCap.defaultSkinValue);
			addButton(new DropDownButton(i < 4 ? width / 2 - 100 - 100 : width / 2 + 70, guiTop + 10 + ((i >= 4 ? i - 4 : i) * 40), 100, 15, curValue, values, (s) -> {
				map.get(level).put(layers, s);
				update();
			}){
				@Override
				public DropdownEntry createEntry(int pos, String val)
				{
					return new ColoredDropdownValueEntry(this, pos, val, setter);
				}
			});
			i++;
		}
		
		addButton(new Button(width / 2 + 30, height / 2 + 75 - 7, 15, 15, new TranslationTextComponent(""), (btn) -> {
			curAnimation += 1;
			
			if (curAnimation >= animations.length) {
				curAnimation = 0;
			}
		})
		{
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);
				
				if (isHovered()) {
					blit(stack, x, y, 66 / 2, 222 / 2, 11, 17, 128, 128);
				} else {
					blit(stack, x, y, 44 / 2, 222 / 2, 11, 17, 128, 128);
				}
			}
		});
		
		addButton(new Button(width / 2 - 30 - 15, height / 2 + 75 - 7, 15, 15, new TranslationTextComponent(""), (btn) -> {
			curAnimation -= 1;
			
			if (curAnimation < 0) {
				curAnimation = animations.length - 1;
			}
		})
		{
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);
				
				if (isHovered()) {
					blit(stack, x, y, 22 / 2, 222 / 2, 11, 17, 128, 128);
				} else {
					blit(stack, x, y, 0, 222 / 2, 11, 17, 128, 128);
				}
			}
		});
		
		for (int num = 1; num <= 9; num++) {
			addButton(new CustomizationSlotButton(width / 2 + 190, guiTop + ((num - 1) * 12) + 5 + 20, num, this));
		}
		
		addButton(new ExtendedButton(width / 2 - 75 - 10, height - 25, 75, 20, new StringTextComponent("Save"), null)
		{
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);
				if (isHovered) {
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.customization.tooltip.done")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			@Override
			public void onPress()
			{
				DragonStateProvider.getCap(minecraft.player).ifPresent(cap -> {
					minecraft.player.level.playSound(minecraft.player, minecraft.player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 1, 0.7f);
					
					if (cap.getType() != type && cap.getType() != DragonType.NONE) {
						if (!ConfigHandler.SERVER.saveAllAbilities.get() || !ConfigHandler.SERVER.saveGrowthStage.get()) {
							confirmation = true;
							return;
						}
					}
					confirm();
				});
			}
		});
		
		addButton(new ExtendedButton(width / 2 + 10, height - 25, 75, 20, new TranslationTextComponent("ds.gui.customization.back"), null)
		{
			@Override
			public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
			{
				super.renderButton(mStack, mouseX, mouseY, partial);
				
				if (isHovered) {
					GuiUtils.drawHoveringText(mStack, Arrays.asList(new TranslationTextComponent("ds.gui.customization.tooltip.back")), mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
				}
			}
			
			@Override
			public void onPress()
			{
				Minecraft.getInstance().setScreen(source);
			}
		});
		
		addButton(new Button(guiLeft + 256, 9, 19, 19, new TranslationTextComponent(""), (btn) -> {
			map.computeIfAbsent(level, (b) -> new HashMap<>());
			map.get(level).put(CustomizationLayer.HORNS, type.name().toLowerCase() + "_horns_" + level.ordinal());
			map.get(level).put(CustomizationLayer.SPIKES, type.name().toLowerCase() + "_spikes_" + level.ordinal());
			map.get(level).put(CustomizationLayer.BOTTOM, type.name().toLowerCase() + "_bottom_" + level.ordinal());
			map.get(level).put(CustomizationLayer.BASE, type.name().toLowerCase() + "_base_" + level.ordinal());
			update();
		})
		{
			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
			{
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.customization.reset")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
			
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/reset_button.png"));
				blit(stack, x, y, 0, 0, width, height, width, height);
				
				if (this.isHovered()) {
					this.renderToolTip(stack, p_230431_2_, p_230431_3_);
				}
			}
		});
		
		addButton(new Button(guiLeft + 256 + 30, 9, 19, 19, new TranslationTextComponent(""), (btn) -> {
			map.computeIfAbsent(level, (b) -> new HashMap<>());
			
			for (CustomizationLayer layer : CustomizationLayer.values()) {
				ArrayList<String> keys = DragonCustomizationHandler.getKeys(minecraft.player, layer);
				
				if (layer != CustomizationLayer.BASE) {
					keys.add(SkinCap.defaultSkinValue);
				}
				
				if (keys.size() > 0) {
					map.get(level).put(layer, keys.get(minecraft.player.level.random.nextInt(keys.size())));
				}
			}
			
			update();
		})
		{
			@Override
			public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
			{
				GuiUtils.drawHoveringText(p_230443_1_, Arrays.asList(new TranslationTextComponent("ds.gui.customization.random")), p_230443_2_, p_230443_3_, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
			}
			
			@Override
			public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
			{
				Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/random_icon.png"));
				blit(stack, x, y, 0, 0, width, height, width, height);
				
				if (this.isHovered()) {
					this.renderToolTip(stack, p_230431_2_, p_230431_3_);
				}
			}
		});
		
		children.add(new CustomizationConfirmation(this, width / 2 - 100, height / 2 - (150 / 2), 200, 150));
	}
	
	
	private static ResourceLocation backgroundTexture = new ResourceLocation("textures/block/dirt.png");
	
	@Override
	public void renderBackground(MatrixStack pMatrixStack)
	{
		super.renderBackground(pMatrixStack);
		DragonAltarGUI.renderBorders(backgroundTexture, 0, width, 32, height - 32, width, height);
	}
	
	@Override
	public void render(MatrixStack stack, int p_230430_2_, int p_230430_3_, float p_230430_4_)
	{
		FakeClientPlayerUtils.getFakePlayer(0, handler).animationSupplier = () -> animations[curAnimation];
		
		this.renderBackground(stack);
		TextRenderUtil.drawCenteredScaledText(stack, width / 2, 10, 2f, title.getString(), DyeColor.WHITE.getTextColor());
		
		int i = 0;
		for (CustomizationLayer layers : CustomizationLayer.values()) {
			String name = layers.name;
			SkinsScreen.drawNonShadowLineBreak(stack, font, new StringTextComponent(name), (i < 4 ? width / 2 - 100 - 100 : width / 2 + 70) + 50, guiTop + 10 + ((i >= 4 ? i - 4 : i) * 40) - 12, DyeColor.WHITE.getTextColor());
			i++;
		}
		
		SkinsScreen.drawNonShadowLineBreak(stack, font, new StringTextComponent(animations[curAnimation]), width / 2, height / 2 + 72, DyeColor.GRAY.getTextColor());
		
		Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/save_icon.png"));
		blit(stack,width / 2 + 188, guiTop + 5, 0, 0, 16, 16,16, 16);
		
		super.render(stack, p_230430_2_, p_230430_3_, p_230430_4_);
		
		children.forEach((ch) -> {
			if(ch instanceof IRenderable){
				((IRenderable)ch).render(stack, p_230430_2_, p_230430_3_, p_230430_4_);
			}
		});
	}
	
	

	public void confirm()
	{
		DragonStateProvider.getCap(minecraft.player).ifPresent(cap -> {
			minecraft.player.level.playSound(minecraft.player, minecraft.player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 1, 0.7f);
			
			if(cap.getType() != type) {
				Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("ds." + type.name().toLowerCase() + "_dragon_choice"), Minecraft.getInstance().player.getUUID());
				cap.setType(type);
				
				if (!ConfigHandler.SERVER.saveGrowthStage.get() || cap.getSize() == 0) {
					cap.setSize(DragonLevel.BABY.size);
				}
				
				cap.setHasWings(ConfigHandler.SERVER.saveGrowthStage.get() ? cap.hasWings() || ConfigHandler.SERVER.startWithWings.get() : ConfigHandler.SERVER.startWithWings.get());
				cap.setIsHiding(false);
				cap.getMovementData().spinLearned = false;
				
				NetworkHandler.CHANNEL.sendToServer(new SyncAltarCooldown(Minecraft.getInstance().player.getId(), Functions.secondsToTicks(ConfigHandler.SERVER.altarUsageCooldown.get())));
				NetworkHandler.CHANNEL.sendToServer(new SynchronizeDragonCap(minecraft.player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), ConfigHandler.SERVER.caveLavaSwimmingTicks.get(), 0));
				NetworkHandler.CHANNEL.sendToServer(new SyncSpinStatus(Minecraft.getInstance().player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
				SynchronizationController.sendClientData(new RequestClientData(cap.getType(), cap.getLevel()));
			}
		});
		
		DragonType type = DragonStateProvider.getDragonType(minecraft.player);
		NetworkHandler.CHANNEL.sendToServer(new SyncPlayerAllCustomization(minecraft.player.getId(), map));
		CustomizationRegistry.savedCustomizations.saved.computeIfAbsent(type, (t) -> new HashMap<>());
		CustomizationRegistry.savedCustomizations.saved.get(type).put(currentSelected, map);
		
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(CustomizationRegistry.savedFile);
			gson.toJson(CustomizationRegistry.savedCustomizations, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Minecraft.getInstance().player.closeContainer();
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY)
	{
		if(dragonRender != null && dragonRender.isMouseOver(pMouseX, pMouseY)){
			return dragonRender.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
		}
		
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
}
