package by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import by.dragonsurvivalteam.dragonsurvival.client.gui.SkinsScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons.*;
import by.dragonsurvivalteam.dragonsurvival.client.gui.utils.TooltipRender;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.ColorSelectorButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.UndoRedoButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ArrowButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DropDownButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ExtendedCheckbox;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components.DragonEditorConfirmComponent;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components.DragonUIRenderComponent;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorRegistry;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonEditorObject.Texture;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.SkinPreset.SkinAgeGroup;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.client.util.TooltipUtils;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.SkinCap;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigRange;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.dragon_editor.SyncPlayerSkinPreset;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.network.player.SynchronizeDragonCap;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncAltarCooldown;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.common.collect.EvictingQueue;
import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class DragonEditorScreen extends Screen implements TooltipRender{
	private static final ResourceLocation backgroundTexture = new ResourceLocation("textures/block/black_concrete.png");
	private static final ResourceLocation RESET_POSITION = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/reset_position_button.png");
	private static final ResourceLocation SAVE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/save_icon.png");
	private static final ResourceLocation RANDOM = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/random_icon.png");
	private static final ResourceLocation RESET = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/reset_button.png");
	public final ConcurrentHashMap<Integer, EvictingQueue<CompoundTag>> UNDO_QUEUES = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<Integer, EvictingQueue<CompoundTag>> REDO_QUEUES = new ConcurrentHashMap<>();
	private final Screen source;
	private final String[] animations = {"sit_dentist",
	                                     "sit_head_locked",
	                                     "idle_head_locked",
	                                     "fly_head_locked",
	                                     "swim_fast",
	                                     "run_head_locked"};
	@ConfigRange( min = 1, max = 1000 )
	@ConfigOption( side = ConfigSide.CLIENT, category = "misc", key = "editorHistory", comment = "The amount of undos and redos that are saved in the dragon editor." )
	public static Integer editorHistory = 10;
	public int HISTORY_SIZE = editorHistory;
	public int guiLeft;
	public int guiTop;
	public boolean confirmation;
	public boolean showUi = true;

	public DragonUIRenderComponent dragonRender;

	public static final DragonStateHandler handler = new DragonStateHandler();

	public DragonLevel level;
	public AbstractDragonType dragonType;
	public SkinPreset preset;
	public int currentSelected;

	public int backgroundColor = -804253680;
	float tick;
	private int curAnimation;
	private int lastSelected;
	private boolean hasInit;
	private DragonEditorConfirmComponent conf;

	public DragonEditorScreen(Screen source){
		this(source, null);
	}

	public DragonEditorScreen(Screen source, AbstractDragonType dragonType){
		super(Component.translatable("ds.gui.dragon_editor"));
		this.source = source;
		this.dragonType = dragonType;
	}

	private static void reverseQueue(Queue<CompoundTag> queue){
		int n = queue.size();
		Stack<CompoundTag> stack = new Stack<>();
		for(int i = 0; i < n; i++){
			CompoundTag curr = queue.poll();
			stack.push(curr);
		}
		for(int i = 0; i < n; i++){
			CompoundTag curr = stack.pop();
			queue.add(curr);
		}
	}

	public void doAction(){
		UNDO_QUEUES.computeIfAbsent(currentSelected, s -> EvictingQueue.create(HISTORY_SIZE));
		REDO_QUEUES.computeIfAbsent(currentSelected, s -> EvictingQueue.create(HISTORY_SIZE));

		REDO_QUEUES.get(currentSelected).clear();
		reverseQueue(UNDO_QUEUES.get(currentSelected));
		UNDO_QUEUES.get(currentSelected).add(preset.writeNBT());
		reverseQueue(UNDO_QUEUES.get(currentSelected));
	}

	public void undoAction(){
		UNDO_QUEUES.computeIfAbsent(currentSelected, s -> EvictingQueue.create(HISTORY_SIZE));
		REDO_QUEUES.computeIfAbsent(currentSelected, s -> EvictingQueue.create(HISTORY_SIZE));

		if(UNDO_QUEUES.get(currentSelected).size() > 0){
			reverseQueue(REDO_QUEUES.get(currentSelected));
			REDO_QUEUES.get(currentSelected).add(preset.writeNBT());
			reverseQueue(REDO_QUEUES.get(currentSelected));

			preset.readNBT(UNDO_QUEUES.get(currentSelected).poll());
			handler.getSkinData().compileSkin();
			update();
		}
	}

	public void redoAction(){
		UNDO_QUEUES.computeIfAbsent(currentSelected, s -> EvictingQueue.create(HISTORY_SIZE));
		REDO_QUEUES.computeIfAbsent(currentSelected, s -> EvictingQueue.create(HISTORY_SIZE));

		if(REDO_QUEUES.get(currentSelected).size() > 0){
			reverseQueue(UNDO_QUEUES.get(currentSelected));
			UNDO_QUEUES.get(currentSelected).add(preset.writeNBT());
			reverseQueue(UNDO_QUEUES.get(currentSelected));

			preset.readNBT(REDO_QUEUES.get(currentSelected).poll());
			handler.getSkinData().compileSkin();
			update();
		}
	}

	@Override
	public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
		if (dragonRender == null) {
			// TODO :: Can happen with the dragon-editor command before using the altar first
			init();
		}

		tick += pPartialTicks;
		if(tick >= 60 * 20){
			save();
			tick = 0;
		}

		if(showUi){
			dragonRender.x = width / 2 - 70;
			dragonRender.y = guiTop;
			dragonRender.width = 140;
			dragonRender.height = 125;
		}else{
			dragonRender.x = 0;
			dragonRender.width = width;
		}

		FakeClientPlayerUtils.getFakePlayer(0, handler).animationSupplier = () -> animations[curAnimation];

		guiGraphics.pose().pushPose();
		// Avoid overlapping parts of the rendered entity (dragon)
		guiGraphics.pose().translate(0, 0, -300);
		renderBackground(guiGraphics);
		children().stream().filter(DragonUIRenderComponent.class::isInstance).toList().forEach(s -> ((DragonUIRenderComponent)s).render(guiGraphics, pMouseX, pMouseY, pPartialTicks));
		guiGraphics.pose().popPose();

		DragonAltarGUI.renderBorders(guiGraphics, backgroundTexture, 0, width, 32, height - 32, width, height);

		guiGraphics.pose().pushPose();

		TextRenderUtil.drawCenteredScaledText(guiGraphics, width / 2, 10, 2f, title.getString(), DyeColor.WHITE.getTextColor());

		if(showUi){
			int i = 0;
			for(EnumSkinLayer layers : EnumSkinLayer.values()){
				String name = layers.name;
				SkinsScreen.drawNonShadowLineBreak(guiGraphics, font, Component.translatable("ds.gui.dragon_editor.part." + name.toLowerCase()), (i < 5 ? width / 2 - 100 - 100 : width / 2 + 83) + 45, guiTop + 10 + (i >= 5 ? (i - 5) * 30 : i * 30) - 12, DyeColor.WHITE.getTextColor());
				i++;
			}
		}

		if(showUi){
			SkinsScreen.drawNonShadowLineBreak(guiGraphics, font, Component.empty().append(WordUtils.capitalize(animations[curAnimation].replace("_", " "))), width / 2, height / 2 + 72, DyeColor.GRAY.getTextColor());
		}

		guiGraphics.pose().popPose();

		for(Renderable widget : new CopyOnWriteArrayList<>(renderables)){
			widget.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
		}

        for (GuiEventListener child : children) {
            if (!(child instanceof DragonUIRenderComponent)) {
                ((Renderable) child).render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
            }
        }
	}

	public SkinPreset save(){
		SkinPreset newPreset = new SkinPreset();
		newPreset.readNBT(preset.writeNBT());
		String type = dragonType != null ? dragonType.getTypeName().toUpperCase() : null;

		DragonEditorRegistry.getSavedCustomizations().skinPresets.computeIfAbsent(type, key -> new HashMap<>());
		DragonEditorRegistry.getSavedCustomizations().skinPresets.get(type).put(currentSelected, newPreset);
		DragonEditorRegistry.getSavedCustomizations().current.get(type).put(level, currentSelected);

		try{
			Gson gson = GsonFactory.newBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(DragonEditorRegistry.savedFile);
			gson.toJson(DragonEditorRegistry.getSavedCustomizations(), writer);
			writer.close();
		}catch(IOException e){
			DragonSurvivalMod.LOGGER.error("An error occured while trying to save the dragon skin", e);
		}

		return newPreset;
	}

	@Override
	public void renderBackground(@NotNull final GuiGraphics guiGraphics) {
		guiGraphics.fill(0, 0, width, height, backgroundColor);
	}

	private void initialize(final DragonStateHandler localHandler) {
		if (dragonType == null && localHandler.isDragon()) {
			level = localHandler.getLevel();
			dragonType = localHandler.getType();
		}

		if (dragonType == null) {
			return;
		}

		if (level == null) {
			level = DragonLevel.NEWBORN;
		}

		String type = dragonType.getTypeName().toUpperCase();

		DragonEditorRegistry.getSavedCustomizations().current.computeIfAbsent(type, key -> new HashMap<>());
		DragonEditorRegistry.getSavedCustomizations().current.get(type).putIfAbsent(level, 0);

		currentSelected = DragonEditorRegistry.getSavedCustomizations().current.get(type).get(level);

		DragonEditorRegistry.getSavedCustomizations().skinPresets.computeIfAbsent(type, key -> new HashMap<>());
		DragonEditorRegistry.getSavedCustomizations().skinPresets.get(type).computeIfAbsent(currentSelected, key -> {
			SkinPreset newPreset = new SkinPreset();
			newPreset.initDefaults(dragonType);
			return newPreset;
		});

		SkinPreset curPreset = DragonEditorRegistry.getSavedCustomizations().skinPresets.get(type).get(currentSelected);
		preset = new SkinPreset();
		preset.readNBT(curPreset.writeNBT());

		handler.getSkinData().skinPreset = preset;
		handler.getSkinData().compileSkin();

		dragonRender.zoom = (float) (level.size * preset.sizeMul);

		handler.setHasWings(true);
		handler.setType(dragonType);
	}

	@Override
	public void init(){
		super.init();

		guiLeft = (width - 256) / 2;
		guiTop = (height - 120) / 2;

		conf = new DragonEditorConfirmComponent(this, width / 2 - 130 / 2, height / 2 - 141 / 2, 130, 154);
		initDragonRender();

		if (!hasInit) {
			initialize(DragonUtils.getHandler(getMinecraft().player));
			update();

			hasInit = true;
		}

		addRenderableWidget(new NewbornEditorButton(this));
		addRenderableWidget(new YoungEditorButton(this));
		addRenderableWidget(new AdultEditorButton(this));

		int maxWidth = -1;

		for(EnumSkinLayer layers : EnumSkinLayer.values()){
			String name = layers.name().substring(0, 1).toUpperCase(Locale.ROOT) + layers.name().toLowerCase().substring(1).replace("_", " ");
			maxWidth = (int)Math.max(maxWidth, font.width(name) * 1.45F);
		}

		int i = 0;
		for (EnumSkinLayer layers : EnumSkinLayer.values()) {
			ArrayList<String> valueList = DragonEditorHandler.getKeys(dragonType, layers);

			if (layers != EnumSkinLayer.BASE) {
				valueList.add(0, SkinCap.defaultSkinValue);
			}

			String[] values = valueList.toArray(new String[0]);
			String curValue = preset.skinAges.get(level).get().layerSettings.get(layers).get().selectedSkin;

			DropDownButton btn = new DragonEditorDropdownButton(this, i < 8 ? width / 2 - 210 : width / 2 + 80, guiTop - 5 + (i >= 8 ? (i - 8) * 20 : i * 20), 100, 15, curValue, values, layers) {
				@Override
				public void updateMessage(){
					if(current != null){
						message = Component.translatable(partToTranslation(current));
					}
				}
			};
			addRenderableWidget(btn);
			addRenderableWidget(new ArrowButton(btn.getX() - 15, btn.getY() + 1, 13, 13, false, s -> {
				int index = 0;

				for(int i1 = 0; i1 < btn.values.length; i1++){
					if(Objects.equals(btn.values[i1], btn.current)){
						index = i1;
						break;
					}
				}

				index = Functions.wrap(index - 1, 0, btn.values.length - 1);
				doAction();
				btn.current = btn.values[index];
				btn.setter.accept(btn.current);
				btn.updateMessage();
			}){
				@Override
				public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
					active = showUi;

					if(active){
						super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
					}
				}
			});

			addRenderableWidget(new ArrowButton(btn.getX() + btn.getWidth() + 2, btn.getY() + 1, 13, 13, true, s -> {
				int index = 0;

				for(int i1 = 0; i1 < btn.values.length; i1++){
					if(Objects.equals(btn.values[i1], btn.current)){
						index = i1;
						break;
					}
				}

				index = Functions.wrap(index + 1, 0, btn.values.length - 1);
				doAction();
				btn.current = btn.values[index];
				btn.setter.accept(btn.current);
				btn.updateMessage();
			}){
				@Override
				public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
					active = showUi || btn.values == null || btn.values.length <= 1;

					if(showUi){
						super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
					}
				}
			});

			addRenderableWidget(new ColorSelectorButton(this, layers, btn.getX() + 10 + btn.getWidth() + 2, btn.getY(), btn.getHeight(), btn.getHeight(), s -> {
				doAction();
				preset.skinAges.get(level).get().layerSettings.get(layers).get().hue = s.floatValue();
				handler.getSkinData().compileSkin();
				update();
			}));
			i++;
		}

		addRenderableWidget(new Button(width / 2 + 45, height / 2 + 75 - 7, 15, 15, Component.empty(), btn -> {
			curAnimation += 1;

			if(curAnimation >= animations.length){
				curAnimation = 0;
			}
		}, Supplier::get) {
			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
				active = visible = showUi;
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
			}

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				if(isHoveredOrFocused()){
					guiGraphics.blit(ClientMagicHUDHandler.widgetTextures, getX(), getY(), (float) 66 / 2, (float) 222 / 2, 11, 17, 128, 128);
				}else{
					guiGraphics.blit(ClientMagicHUDHandler.widgetTextures, getX(), getY(), (float) 44 / 2, (float) 222 / 2, 11, 17, 128, 128);
				}
			}
		});

		addRenderableWidget(new Button(width / 2 - 45 - 20, height / 2 + 75 - 7, 15, 15, Component.empty(), btn -> {
			curAnimation -= 1;

			if(curAnimation < 0){
				curAnimation = animations.length - 1;
			}
		}, Supplier::get) {
			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
				active = visible = showUi;
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
			}

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				RenderSystem.setShaderTexture(0, ClientMagicHUDHandler.widgetTextures);

				if(isHoveredOrFocused()){
					guiGraphics.blit(ClientMagicHUDHandler.widgetTextures, getX(), getY(), (float) 22 / 2, (float) 222 / 2, 11, 17, 128, 128);
				}else{
					guiGraphics.blit(ClientMagicHUDHandler.widgetTextures, getX(), getY(), 0, (float) 222 / 2, 11, 17, 128, 128);
				}
			}
		});

		for(int num = 1; num <= 9; num++){
			addRenderableWidget(new DragonEditorSlotButton(width / 2 + 200 + 15, guiTop + (num - 1) * 12 + 5 + 30, num, this));
		}

		// Slider to change the size
		addRenderableWidget(new ForgeSlider(width / 2 - 100 - 100, height - 25, 100, 20, Component.translatable("ds.gui.dragon_editor.size"), Component.empty().append("%"), ServerConfig.minSizeVari, ServerConfig.maxSizeVari, Math.round((preset.sizeMul - 1.0) * 100), true){
			@Override
			protected void applyValue(){
				super.applyValue();
				double val = 1.0 + getValueInt() / 100.0;
				if(preset.sizeMul != val){
					preset.sizeMul = val;
					dragonRender.zoom = (float)(level.size * preset.sizeMul);
				}

				double val1 = Math.round((preset.sizeMul - 1.0) * 100);

				if(val1 > 0){
					setMessage(Component.translatable("ds.gui.dragon_editor.size").append("+").append(val1 + "%"));
				}else{
					setMessage(Component.translatable("ds.gui.dragon_editor.size").append(val1 + "%"));
				}
			}

			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

				if (TooltipUtils.needsTooltip(this)) {
					setTooltip(Tooltip.create(Component.translatable("ds.gui.dragon_editor.size_info")));
				}
			}
		});

		addRenderableWidget(new ExtendedCheckbox(width, height, 0, 0, 0, Component.translatable("ds.gui.dragon_editor.wings"), preset.skinAges.get(level).get().wings, p -> preset.skinAges.get(level).get().wings = p.selected()){
			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
				selected = preset.skinAges.get(level).get().wings;
				super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTicks);
			}
		});
		addRenderableWidget(new ExtendedCheckbox(width / 2 + 100, height - 25, 120, 19, 19, Component.translatable("ds.gui.dragon_editor.default_skin"), preset.skinAges.get(level).get().defaultSkin, p -> preset.skinAges.get(level).get().defaultSkin = p.selected()){
			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
				selected = preset.skinAges.get(level).get().defaultSkin;
				super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTicks);
			}

			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

				if (isHovered()) {
					guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.default_skin.tooltip"), pMouseX, pMouseY);
				}
			}
		});

		addRenderableWidget(new ExtendedButton(width / 2 - 75 - 10, height - 25, 75, 20, Component.translatable("ds.gui.dragon_editor.save"), null){
			Renderable renderButton;
			boolean toggled;

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
				super.renderWidget(guiGraphics, mouseX, mouseY, partial);
				if(toggled && (!visible || !confirmation)){
					toggled = false;
					Screen screen = Minecraft.getInstance().screen;
					screen.children.removeIf(s -> s == conf);
					screen.renderables.removeIf(s -> s == renderButton);
				}
			}

			@Override
			public void onPress(){
				DragonStateProvider.getCap(minecraft.player).ifPresent(cap -> {
					minecraft.player.level().playSound(minecraft.player, minecraft.player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1, 0.7f);

					if(cap.getType() != dragonType && cap.getType() != null){
						if(!ServerConfig.saveAllAbilities || !ServerConfig.saveGrowthStage){
							confirmation = true;
							return;
						}
					}
					if(!confirmation){
						confirm();
					}
				});

				if(confirmation){
					if(!toggled){
						renderButton = new ExtendedButton(0, 0, 0, 0, Component.empty(), null){
							@Override
							public void render(@NotNull final GuiGraphics guiGraphics, int p_230430_2_, int p_230430_3_, float p_230430_4_){
								active = visible = false;

								if(conf != null && confirmation){
									guiGraphics.pose().pushPose();
									// Render the pop-up message above the dragon
									guiGraphics.pose().translate(0, 0, 150);
									conf.render(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
									guiGraphics.pose().popPose();
								}
							}
						};
						children.add(0, conf);
						children.add(conf);
						renderables.add(renderButton);
					}
					toggled = !toggled;
				}else{
					children.removeIf(s -> s == conf);
					renderables.removeIf(s -> s == renderButton);
				}
			}

			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

				if (isHoveredOrFocused()) {
					guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.tooltip.done"), pMouseX, pMouseY);
				}
			}
		});

		addRenderableWidget(new ExtendedButton(width / 2 + 10, height - 25, 75, 20, Component.translatable("ds.gui.dragon_editor.back"), null){
			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial){
				super.renderWidget(guiGraphics, mouseX, mouseY, partial);
			}

			@Override
			public void onPress(){
				Minecraft.getInstance().setScreen(source);
			}

			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

				if (isHoveredOrFocused()) {
					guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.tooltip.back"), pMouseX, pMouseY);
				}
			}
		});

		addRenderableWidget(new ExtendedButton(guiLeft + 290, 11, 18, 18, Component.empty(), btn -> {
			doAction();
			preset.skinAges.put(level, Lazy.of(()->new SkinAgeGroup(level, dragonType)));
			handler.getSkinData().compileSkin();
			update();
		}){
			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

				if (isHovered()) {
					guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.reset"), pMouseX, pMouseY);
				}
			}

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				guiGraphics.blit(RESET, getX(), getY(), 0, 0, width, height, width, height);
			}
		});


		addRenderableWidget(new ExtendedButton(guiLeft + 260, 11, 18, 18, Component.empty(), btn -> {
			doAction();

			ArrayList<String> extraKeys = DragonEditorHandler.getKeys(FakeClientPlayerUtils.getFakePlayer(0, handler), EnumSkinLayer.EXTRA);

			extraKeys.removeIf(s -> {
				Texture text = DragonEditorHandler.getSkin(FakeClientPlayerUtils.getFakePlayer(0, handler), EnumSkinLayer.EXTRA, s, dragonType);
				return !text.random;
			});

			for(EnumSkinLayer layer : EnumSkinLayer.values()){
				ArrayList<String> keys = DragonEditorHandler.getKeys(FakeClientPlayerUtils.getFakePlayer(0, handler), layer);

				if(Objects.equals(layer.name, "Extra")){
					keys = extraKeys;
				}

				if(layer != EnumSkinLayer.BASE){
					keys.add(SkinCap.defaultSkinValue);
				}

				if(!keys.isEmpty()){
					String key = keys.get(minecraft.player.getRandom().nextInt(keys.size()));
					if(Objects.equals(layer.name, "Extra")){
						extraKeys.remove(key);
					}

					LayerSettings settings = preset.skinAges.get(level).get().layerSettings.get(layer).get();
					settings.selectedSkin = key;
					Texture text = DragonEditorHandler.getSkin(FakeClientPlayerUtils.getFakePlayer(0, handler), layer, key, dragonType);

					if(text != null && text.randomHue){
						settings.hue = 0.25f + minecraft.player.getRandom().nextFloat() * 0.5f;
						settings.saturation = 0.25f + minecraft.player.getRandom().nextFloat() * 0.5f;
						settings.brightness = 0.3f + minecraft.player.getRandom().nextFloat() * 0.2f;
						settings.modifiedColor = true;
					}else{
						settings.hue = 0.5f;
						settings.saturation = 0.5f;
						settings.brightness = 0.5f;
						settings.modifiedColor = true;
					}
				}
				handler.getSkinData().compileSkin();
			}

			update();
		}){
			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

				if (isHovered()) {
					guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.random"), pMouseX, pMouseY);
				}
			}

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				guiGraphics.blit(RANDOM, getX(), getY(), 0, 0, width, height, width, height);
			}
		});

		addRenderableWidget(new UndoRedoButton(guiLeft + 318, 11, 18, 18, false, button -> undoAction()){
			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
				active = UNDO_QUEUES.containsKey(currentSelected) && !UNDO_QUEUES.get(currentSelected).isEmpty();
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

				if (isHoveredOrFocused()) {
					guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.undo"), pMouseX, pMouseY);
				}
			}
		});

		addRenderableWidget(new UndoRedoButton(guiLeft + 340, 11, 18, 18, true, button -> redoAction()){
			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
				active = REDO_QUEUES.containsKey(currentSelected) && !REDO_QUEUES.get(currentSelected).isEmpty();
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

				if (isHoveredOrFocused()) {
					guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.redo"), pMouseX, pMouseY);
				}
			}
		});

		addRenderableWidget(new ExtendedButton(width / 2 + 213, guiTop + 10, 18, 18, Component.empty(), button -> { /* Nothing to do */ }){
			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
				active = visible = showUi;
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

				if (visible) {
					guiGraphics.blit(SAVE, getX(), getY(), 0, 0, 16, 16, 16, 16);

					if (isHoveredOrFocused()) {
						guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.save_slot"), pMouseX, pMouseY);
					}
				}
			}

			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) { /* Nothing to do */ }
		});

		addRenderableWidget(new CopySettingsButton(this, guiLeft + 230, 11, 18, 18, Component.empty(), button -> { /* Nothing to do */ }));

		addRenderableWidget(new ExtendedButton(dragonRender.x + dragonRender.width - 17, dragonRender.y + dragonRender.height + 3, 15, 15, Component.empty(), btn -> {
			dragonRender.yRot = -3;
			dragonRender.xRot = -5;
			dragonRender.xOffset = 0;
			dragonRender.yOffset = 0;
			dragonRender.zoom = (float)(level.size * preset.sizeMul);
		}){
			@Override
			public void renderWidget(@NotNull final GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_){
				guiGraphics.blit(RESET_POSITION, getX(), getY(), 0, 0, width, height, width, height);
			}

			@Override
			public void render(@NotNull final GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks){
				active = visible = showUi;
				super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);

				if (isHovered()) {
					guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable("ds.gui.dragon_editor.reset"), pMouseX, pMouseY);
				}
			}
		});

		addRenderableWidget(new ExtendedCheckbox(guiLeft - 15, 11, 40, 18, 18, Component.translatable("ds.gui.dragon_editor.show_ui"), showUi, p -> showUi = p.selected()));
		addRenderableWidget(new BackgroundColorButton(guiLeft - 45, 11, 18, 18, Component.empty(), s -> {}, this));
		addRenderableWidget(new HelpButton(dragonType, guiLeft - 75, 11, 15, 15, "ds.help.customization", 1));
		//addRenderableWidget(new ScreenshotButton(guiLeft + 240, 10, 18, 18, Component.empty(), (s) -> {}, this));
	}

	public void update(){
		if (dragonType != null) {
			handler.setType(dragonType);
		}

		handler.getSkinData().skinPreset = preset;
		handler.setSize(level.size);
		handler.setHasWings(true);

		if (currentSelected != lastSelected) {
			preset = new SkinPreset();

			if (DragonEditorRegistry.getSavedCustomizations().skinPresets.containsKey(dragonType.getTypeName().toUpperCase())) {
				preset.readNBT(DragonEditorRegistry.getSavedCustomizations().skinPresets.get(dragonType.getTypeName().toUpperCase()).get(currentSelected).writeNBT());
			}
			handler.getSkinData().skinPreset = preset;
		}

		lastSelected = currentSelected;

		initDragonRender();
	}

	private void initDragonRender(){
		children.removeIf(DragonUIRenderComponent.class::isInstance);

		float yRot = -3, xRot = -5, zoom = 0, xOffset = 0, yOffset = 0;
		if(dragonRender != null){
			yRot = dragonRender.yRot;
			xRot = dragonRender.xRot;
			zoom = dragonRender.zoom;
			xOffset = dragonRender.xOffset;
			yOffset = dragonRender.yOffset;
		}

		dragonRender = new DragonUIRenderComponent(this, width / 2 - 70, guiTop, 140, 125, () -> FakeClientPlayerUtils.getFakeDragon(0, handler));
		dragonRender.xRot = xRot;
		dragonRender.yRot = yRot;
		dragonRender.zoom = zoom;
		dragonRender.xOffset = xOffset;
		dragonRender.yOffset = yOffset;

		children.add(0, dragonRender);
	}

	public void confirm(){
		DragonStateProvider.getCap(minecraft.player).ifPresent(cap -> {
			minecraft.player.level().playSound(minecraft.player, minecraft.player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1, 0.7f);

			if(cap.getType() != dragonType){
				minecraft.player.sendSystemMessage(Component.translatable("ds." + dragonType.getTypeName().toLowerCase() + "_dragon_choice"));

				if(dragonType == null && cap.getType() != null){
					DragonCommand.reInsertClawTools(Minecraft.getInstance().player, cap);
				}

				cap.setType(dragonType);

				double size = cap.getSavedDragonSize(cap.getTypeName());

				if(!ServerConfig.saveGrowthStage || size == 0){
					cap.setSize(DragonLevel.NEWBORN.size);
				} else {
					cap.setSize(size);
				}

				cap.setHasWings(ServerConfig.saveGrowthStage ? cap.hasWings() || ServerFlightHandler.startWithWings : ServerFlightHandler.startWithWings);
				cap.setIsHiding(false);
				cap.getMovementData().spinLearned = ServerConfig.saveGrowthStage && cap.getMovementData().spinLearned;

				NetworkHandler.CHANNEL.sendToServer(new SyncAltarCooldown(Minecraft.getInstance().player.getId(), Functions.secondsToTicks(ServerConfig.altarUsageCooldown)));
				NetworkHandler.CHANNEL.sendToServer(new SynchronizeDragonCap(Minecraft.getInstance().player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), 0));
				NetworkHandler.CHANNEL.sendToServer(new CompleteDataSync(Minecraft.getInstance().player.getId(), cap.writeNBT()));
				NetworkHandler.CHANNEL.sendToServer(new SyncSpinStatus(Minecraft.getInstance().player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
			}

			if (minecraft != null && minecraft.player != null) {
				NetworkHandler.CHANNEL.sendToServer(new SyncPlayerSkinPreset(minecraft.player.getId(), save()));
			}
		});

		minecraft.player.closeContainer();
	}

	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY){
		if(!super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)){
			if(dragonRender != null && dragonRender.isMouseOver(pMouseX, pMouseY)){
				return dragonRender.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
			}
		}

		return false;
	}

	public static String partToTranslation(final String part) {
		String text = "ds.skin_part." + DragonEditorScreen.handler.getTypeName().toLowerCase(Locale.ROOT) + "." + part.toLowerCase(Locale.ROOT);

		if (I18n.exists(text)) {
			return text;
		}

		return part;
	}

	public static String partToTechnical(final String part) {
		return part.replace("ds.skin_part.", "").replace(DragonEditorScreen.handler.getTypeName().toLowerCase(Locale.ROOT) + ".", "");
	}
}