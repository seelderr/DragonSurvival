package by.jackraidenph.dragonsurvival.client.gui.widgets;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.settings.ListConfigSettingsScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.settings.ResourceTextFieldOption;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.util.BiomeDictionaryHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.registries.ForgeRegistries;
import org.codehaus.plexus.util.StringUtils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceTextField extends TextFieldWidget implements IBidiTooltip
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	private ResourceTextFieldOption option;
	private ValueSpec spec;
	
	private ResourceEntry stack;
	private List<ResourceEntry> suggestions = new ArrayList<>();
	private static final int maxItems = 5;
	
	public double scroll;
	
	
	public ResourceTextField(int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage)
	{
		this(null, null, pX, pY, pWidth, pHeight, pMessage);
	}
	
	public ResourceTextField(ValueSpec spec, ResourceTextFieldOption option, int pX, int pY, int pWidth, int pHeight, ITextComponent pMessage)
	{
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		setBordered(false);
		this.option = option;
		this.spec = spec;
		update();
	}
	
	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		if(stack != null) {
			stack.tick();
		}
	}
	
	@Override
	public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks)
	{
		Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
		GuiUtils.drawContinuousTexturedBox(pMatrixStack, x, y + 1, 0, isHovered ? 32 : 0, width, height, 32, 32, 10, 0);
		
		if (stack != null && !stack.isEmpty()) {
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			itemRenderer.renderAndDecorateItem(stack.getDisplayItem(), x + 3, y + 3);
			itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, stack.getDisplayItem(), x + 3, y + 3);
		}
		
		if(!isFocused()){
			suggestions.clear();
		}
		
		this.x += 25;
		this.y += 6;
		
		if(spec != null && !spec.test(Arrays.asList(getValue()))){
			setTextColor(DyeColor.RED.getTextColor());
		}else{
			setTextColor(14737632);
		}
		
		super.renderButton(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		setTextColor(14737632);
		
		if(getValue().isEmpty()){
			boolean isFocus = isFocused();
			setFocus(false);
			int curser = getCursorPosition();
			setCursorPosition(0);
			setTextColor(7368816);
			setValue(this.getMessage().getString());
			super.renderButton(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
			setValue("");
			setTextColor(14737632);
			setCursorPosition(curser);
			setFocus(isFocus);
		}
		
		this.x -= 25;
		this.y -= 6;
		
		if(Minecraft.getInstance().screen instanceof ListConfigSettingsScreen){
			ListConfigSettingsScreen settingsScreen = (ListConfigSettingsScreen)Minecraft.getInstance().screen;
			
			if(isFocused() && suggestions != null && !suggestions.isEmpty()){
				if(settingsScreen.selectedField != this){
					settingsScreen.selectedField = this;
				}
			}else{
				if(settingsScreen.selectedField == this){
					settingsScreen.selectedField = null;
				}
			}
		}
	}
	
	public void renderPost(MatrixStack mStack, int mouseX, int mouseY, float partial){
		if(isFocused() && suggestions != null && !suggestions.isEmpty()) {
			int amount = Math.min(maxItems, suggestions.size());
			
			Minecraft.getInstance().textureManager.bind(BACKGROUND_TEXTURE);
			GuiUtils.drawContinuousTexturedBox(mStack, x, y + height, 0, 0, width, amount * height, 32, 32, 10, 0);
			
			int maxElement = (int)(scroll / 20);
			int renderNum = 0;
			
			suggestions.forEach(ResourceEntry::tick);
			
			for (int i1 = Math.max(0, maxElement-maxItems); i1 < Math.min(Math.max(maxElement, maxItems), suggestions.size()); i1++) {
				ResourceEntry suggestion = suggestions.get(i1);
				
				if(suggestion != null) {
					boolean isAbove = false;
					int startY = y + (height);
					int level = renderNum * (height);
					
					int color = new Color(0.1F, 0.1F, 0.1F, 1F).getRGB();
					
					if (renderNum % 2 == 0) {
						color = new Color(0.2F, 0.2F, 0.2F, 1F).getRGB();
					}
					
					if(mouseX >= x + 2 && mouseY >= startY + level + 2 - (renderNum > 0 ? 4 : 0) && mouseX < x + width - 2 && mouseY < startY + height + level - 2){
						color = new Color(color).brighter().getRGB();
						isAbove = true;
					}
					
					AbstractGui.fill(mStack, x + 2, startY + level + 2 - (renderNum > 0 ? 4 : 0), x + width - 2, startY + height + level - 2, color);
					
					String text = suggestion.id;
					Minecraft.getInstance().font.drawShadow(mStack, new StringTextComponent(Minecraft.getInstance().font.substrByWidth(new StringTextComponent(text), width - 20).getString()), x + 25, y + 5 + height + (renderNum * height), DyeColor.WHITE.getTextColor());
					
					if (!suggestion.isEmpty()) {
						ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
						itemRenderer.renderAndDecorateItem(suggestion.getDisplayItem(), x + 3, startY + level + 1);
						
						if(isAbove) {
							List<ITextComponent> lines = suggestion.getDisplayItem().getTooltipLines(Minecraft.getInstance().player, TooltipFlags.NORMAL);
							GuiUtils.drawHoveringText(mStack, lines, mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
						}
					}
					
					renderNum++;
				}
			}
			{
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuilder();
				
				float maxPosition = (suggestions.size()) * height;
				float maxScroll = Math.max(0, maxPosition);
				
				scroll = MathHelper.clamp(scroll, 0, maxScroll);
				
				int y0 = y + height + 2;
				int y1 = (y + height + (amount * height)) - 2;
				int x0 = x;
				int x1 = x + width;
				
				int i = x1 - 8;
				int j = i + 6;
				int k1 = (int)maxScroll;
				if (k1 > maxItems * height) {
					RenderSystem.disableTexture();
					int l1 = (int)((float)((y1 - y0) * (y1 - y0)) / maxPosition);
					l1 = MathHelper.clamp(l1, 32, y1 - y0 - 8);
					int i2 = (int)scroll * (y1 - y0 - l1) / k1 + y0;
					if (i2 < y0) {
						i2 = y0;
					}
					
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
					bufferbuilder.vertex((double)i, (double)y1, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
					bufferbuilder.vertex((double)j, (double)y1, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
					bufferbuilder.vertex((double)j, (double)y0, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)y0, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)(i2 + l1), 0.0D).uv(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
					bufferbuilder.vertex((double)j, (double)(i2 + l1), 0.0D).uv(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
					bufferbuilder.vertex((double)j, (double)i2, 0.0D).uv(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)i2, 0.0D).uv(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)(i2 + l1 - 1), 0.0D).uv(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
					bufferbuilder.vertex((double)(j - 1), (double)(i2 + l1 - 1), 0.0D).uv(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
					bufferbuilder.vertex((double)(j - 1), (double)i2, 0.0D).uv(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
					bufferbuilder.vertex((double)i, (double)i2, 0.0D).uv(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
					tessellator.end();
					
					RenderSystem.enableTexture();
				}
			}
		}
	}
	
	public void update(){
		scroll = 0;
		stack = null;
		suggestions.clear();
		
		boolean isItem = ConfigHandler.isItemPredicate((obj) -> spec.test(obj));
		boolean isBlock = ConfigHandler.isBlockPredicate((obj) -> spec.test(obj));
		boolean isEntity = ConfigHandler.isEntityPredicate((obj) -> spec.test(obj));
		boolean isEffect = ConfigHandler.isEffectPredicate((obj) -> spec.test(obj));
		boolean isBiome = ConfigHandler.isBiomePredicate((obj) -> spec.test(obj));
		boolean isTag = ConfigHandler.isTagPredicate((obj) -> spec.test(obj));
		
		String value = getValue().isEmpty() ? option.getter.apply(Minecraft.getInstance().options) : getValue();
		String type = (isItem ? "item" : isBlock ? "block" : isEntity ? "entity" : isEffect ? "effect" : isBiome ? "biome" : "") + ":";
		String tagType = "tag:";
		
		String resource = value.toLowerCase(Locale.ROOT).startsWith(type) ? value.substring(type.length()) : value;
		String resource1 = resource.toLowerCase(Locale.ROOT).startsWith(tagType) ? resource.substring(tagType.length()) : resource;
		
		List<ItemStack> sk = parseCombinedList(Collections.singletonList(type + resource1), isTag, isBlock, isItem, isEntity, isBiome);
		stack = new ResourceEntry(value, sk);
		if(!isFocused()) return;
		
		run(isItem, isBlock, isEntity, isEffect, isBiome, isTag, type, resource, resource1);
		
		if(suggestions.size() == 0 && !resource.isEmpty()){
			run(isItem, isBlock, isEntity, isEffect, isBiome, isTag, type, "", resource1);
		}
		
		suggestions.sort((c1, c2) -> c2.mod.compareTo(c1.mod));
		suggestions.sort(Comparator.comparing(c -> c.id));
	}
	
	private void run(boolean isItem, boolean isBlock, boolean isEntity, boolean isEffect, boolean isBiome, boolean isTag, String type, String resource, String resource1)
	{
		SuggestionsBuilder builder = new SuggestionsBuilder(resource, 0);
		
		if(isItem) ISuggestionProvider.suggestResource(ForgeRegistries.ITEMS.getKeys(), builder);
		if(isBlock) ISuggestionProvider.suggestResource(ForgeRegistries.BLOCKS.getKeys(), builder);
		if(isEntity) ISuggestionProvider.suggestResource(ForgeRegistries.ENTITIES.getKeys(), builder);
		if(isEffect) ISuggestionProvider.suggestResource(ForgeRegistries.POTIONS.getKeys(), builder);
		if(isBiome) ISuggestionProvider.suggestResource(ForgeRegistries.BIOMES.getKeys(), builder);
		
		runSuggestions(type, builder, false, isBlock, isItem, isEntity, isBiome);
		
		if(isTag) {
			builder = new SuggestionsBuilder(resource1, 0);
			
			if(isBlock) ISuggestionProvider.suggestResource(BlockTags.getAllTags().getAvailableTags(), builder);
			if(isItem) ISuggestionProvider.suggestResource(ItemTags.getAllTags().getAvailableTags(), builder);
			if(isEntity) ISuggestionProvider.suggestResource(EntityTypeTags.getAllTags().getAvailableTags(), builder);
			
			runSuggestions(type, builder, true, isBlock, isItem, isEntity, isBiome);
		}
	}
	
	private void runSuggestions(String type, SuggestionsBuilder builder, boolean isTag, boolean isBlock, boolean isItem, boolean isEntity, boolean isBiome)
	{
		Suggestions sgs = builder.build();
		
		List<String> suggestions = sgs.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
		suggestions.removeIf((s) -> s == null || s.isEmpty());
		
		for(String bl : suggestions){
			List<ItemStack> items = parseCombinedList(Collections.singletonList(type + bl), isTag, isBlock, isItem, isEntity, isBiome);
			
			if(!items.isEmpty()) {
				ResourceEntry entry = new ResourceEntry((isTag ? "tag:" : type) + bl, items);
				this.suggestions.add(entry);
			}
		}
	}
	
	public static List<ItemStack> parseCombinedList(List<String> values, boolean isTag, boolean isBlock, boolean isItem, boolean isEntity, boolean isBiome){
		List<IItemProvider> result = new ArrayList<>();
		List<ItemStack> itemStacks = new ArrayList<>();
		
		for(String value : values) {
			if(value.isEmpty() || StringUtils.countMatches(value, ":") == 0) continue;
			
			String type = value.substring(0, value.indexOf(":"));
			String resource = value.substring(value.indexOf(":")+1);
			
			while (StringUtils.countMatches(resource, ":") > 1) {
				resource = resource.substring(0, resource.lastIndexOf(":"));
			}
			
			ResourceLocation location = ResourceLocation.tryParse(resource);
			if(location == null) continue;
			
			if (isTag) {
				if(isItem) {
					try {
						result.addAll(Objects.requireNonNull(ItemTags.getAllTags().getTag(location)).getValues());
					} catch (Exception ignored) {}
				}
				
				if (isBlock) {
					try {
						result.addAll(Objects.requireNonNull(BlockTags.getAllTags().getTag(location)).getValues());
					} catch (Exception ignored) {}
				}
				
				if(isEntity) {
					try {
						Objects.requireNonNull(EntityTypeTags.getAllTags().getTag(location)).getValues().forEach(ForgeSpawnEggItem::fromEntityType);
					} catch (Exception ignored) {}
				}
				
				if(isBiome) {
					try {
						Set<RegistryKey<Biome>> biomes = BiomeDictionary.getBiomes(BiomeDictionaryHelper.getType(value));
						result.addAll(biomes.stream().map((bi) -> {
							try {
								Biome biome = ForgeRegistries.BIOMES.getValue(bi.location());
								return biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial().getBlock();
							} catch (Exception ignored) {}
							return null;
						}).collect(Collectors.toList()));
					} catch (Exception ignored) {}
				}
			}
			
			
			if (type.equalsIgnoreCase("item")) {
				try {
					result.add(new ItemParser(new StringReader(resource), false).parse().getItem());
				} catch (Exception ignored) {}
			}
			
			if (type.equalsIgnoreCase("block")) {
				
				try {
					result.add(Objects.requireNonNull(new BlockStateParser(new StringReader(resource), false).parse(false).getState()).getBlock());
				} catch (Exception ignored) {}
			}
			
			if (type.equalsIgnoreCase("entity")) {
				
				try {
					EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(location);
					if(entityType != null){
						SpawnEggItem item = ForgeSpawnEggItem.fromEntityType(entityType);
						
						if(item != null){
							result.add(item);
						}else{
							result.add(new ItemParser(new StringReader(resource), false).parse().getItem());
						}
					}
				} catch (Exception ignored) {}
			}
			
			if (type.equalsIgnoreCase("effect")) {
				try {
					Effect effect = ForgeRegistries.POTIONS.getValue(location);
					EffectInstance instance = new EffectInstance(effect, 20);
					ItemStack stack = new ItemStack(Items.POTION);
					PotionUtils.setPotion(stack, Potions.WATER);
					PotionUtils.setCustomEffects(stack, Collections.singletonList(instance));
					itemStacks.add(stack);
				} catch (Exception ignored) {}
			}
			
			if (type.equalsIgnoreCase("biome")) {
				try {
					Biome biome = ForgeRegistries.BIOMES.getValue(location);
					result.add(biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial().getBlock());
				} catch (Exception ignored) {}
			}
		}
		
		result.removeIf(Objects::isNull);
		itemStacks.removeIf(Objects::isNull);
		
		result = result.stream().distinct().collect(Collectors.toList());
		itemStacks.addAll(result.stream().map(ItemStack::new).collect(Collectors.toList()));
		
		if(isItem && !isBlock){
			itemStacks.removeIf((it) -> it.getItem() instanceof BlockItem);
		}
		
		return itemStacks;
	}
	
	@Override
	public boolean charTyped(char pCodePoint, int pModifiers)
	{
		boolean val = super.charTyped(pCodePoint, pModifiers);
		update();
		return val;
	}
	
	@Override
	public void deleteChars(int pNum)
	{
		super.deleteChars(pNum);
		update();
	}
	
	@Override
	public Optional<List<IReorderingProcessor>> getTooltip()
	{
		return option != null ? option.getTooltip() : Optional.empty();
	}
	
	@Override
	public boolean isMouseOver(double pMouseX, double pMouseY)
	{
		int maxHeight = y + height;
		
		if(isFocused() && !suggestions.isEmpty()){
			maxHeight += Math.min(maxItems, suggestions.size()) * height;
		}
		
		return this.active && this.visible && pMouseX >= (double)this.x && pMouseY >= (double)this.y && pMouseX < (double)(this.x + this.width) && pMouseY < maxHeight;
	}
	
	protected boolean clicked(double pMouseX, double pMouseY) {
		return isMouseOver(pMouseX, pMouseY);
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
	{
		if(isFocused() && !suggestions.isEmpty()){
			int maxElement = (int)(scroll / 20);
			int renderNum = 0;
			
			for (int i1 = Math.max(0, maxElement-maxItems); i1 < Math.min(Math.max(maxElement, maxItems), suggestions.size()); i1++) {
				ResourceEntry suggestion = suggestions.get(i1);
				
				int startY = y + (height);
				int level = renderNum * (height);
				String text = suggestion.id;
				
				if(pMouseX >= x + 2 && pMouseY >= startY + level + 2 - (renderNum > 0 ? 4 : 0) && pMouseX < x + width - 2 && pMouseY < startY + height + level - 2){
					setValue(text);
					setFocus(false);
					update();
					return true;
				}
				
				renderNum++;
			}
		}
		
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}
	
	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta)
	{
		scroll -= pDelta * 10;
		return super.mouseScrolled(pMouseX, pMouseY, pDelta);
	}
	
	public class ResourceEntry{
		public Entity ent;
		public String id;
		public String mod;
		
		public List<ItemStack> displayItems;
		private ItemStack cachedDisplay = ItemStack.EMPTY;
		
		private int index;
		private int tick;
		
		public ResourceEntry(String id, List<ItemStack> displayItems)
		{
			this.id = id;
			this.displayItems = displayItems;
			
			String[] split = id.split(":");
			mod = split[0];
		}
		
		public boolean isEmpty(){
			return displayItems == null || displayItems.isEmpty();
		}
		
		public void tick(){
			tick++;
			
			if(cachedDisplay == null || cachedDisplay.isEmpty()){
				if(displayItems != null && !displayItems.isEmpty()){
					cachedDisplay = displayItems.get(0);
				}
			}
			
			if(displayItems != null && displayItems.size() > 1){
				if(tick % 120 == 0){
					index++;
					
					if(index >= displayItems.size()){
						index = 0;
					}
					
					cachedDisplay = displayItems.get(index);
				}
			}else{
				if(index > 0){
					cachedDisplay = displayItems.get(0);
					index = 0;
				}
			}
		}
		
		public ItemStack getDisplayItem(){
			return cachedDisplay;
		}
	}
}