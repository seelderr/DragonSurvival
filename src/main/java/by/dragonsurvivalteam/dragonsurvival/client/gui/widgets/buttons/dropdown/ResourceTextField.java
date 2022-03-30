package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.settings.ResourceTextFieldOption;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.common.ForgeConfigSpec.ValueSpec;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ResourceTextField extends EditBox implements TooltipAccessor{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	private static final int maxItems = 6;
	private final List<by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry> suggestions = new ArrayList<>();
	boolean isItem;
	boolean isBlock;
	boolean isEntity;
	boolean isEffect;
	boolean isBiome;
	boolean isTag;
	private ResourceTextFieldOption option;
	private ValueSpec spec;
	private by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry stack;
	private by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList list;
	private AbstractWidget renderButton;

	public ResourceTextField(ValueSpec spec, ResourceTextFieldOption option, int pX, int pY, int pWidth, int pHeight, Component pMessage){
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		setBordered(false);
		this.option = option;
		this.spec = spec;

		isItem = ConfigHandler.isItemPredicate(spec::test);
		isBlock = ConfigHandler.isBlockPredicate(spec::test);
		isEntity = ConfigHandler.isEntityPredicate(spec::test);
		isEffect = ConfigHandler.isEffectPredicate(spec::test);
		isBiome = ConfigHandler.isBiomePredicate(spec::test);
		isTag = ConfigHandler.isTagPredicate(spec::test);
		update();
	}

	public ResourceTextField(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean isItem, boolean isBlock, boolean isEntity, boolean isEffect, boolean isBiome, boolean isTag){
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		this.isItem = isItem;
		this.isBlock = isBlock;
		this.isEntity = isEntity;
		this.isEffect = isEffect;
		this.isBiome = isBiome;
		this.isTag = isTag;
		setBordered(false);
		update();
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks){
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);

		if((isFocused() || list != null) && (!visible || (!isMouseOver(pMouseX, pMouseY) && !list.isMouseOver(pMouseX, pMouseY)))){
			setFocus(false);
		}

		if(isFocused() && list != null){
			list.reposition(x, y + height, width, Math.min(suggestions.size() + 1, maxItems) * height);
		}
	}

	public void update(){
		stack = null;
		suggestions.clear();
		if(list != null){
			list.children().clear();
		}

		String value = getValue().isEmpty() && option != null ? option.getter.apply(Minecraft.getInstance().options) : getValue();
		String type = (isItem ? "item" : isBlock ? "block" : isEntity ? "entity" : isEffect ? "effect" : isBiome ? "biome" : "") + ":";
		String tagType = "tag:";

		String resource = value.toLowerCase(Locale.ROOT).startsWith(type) ? value.substring(type.length()) : value;
		resource = resource.toLowerCase(Locale.ROOT).startsWith(tagType) ? resource.substring(tagType.length()) : resource;
		String start = value.substring(0, value.length() - resource.length());

		while(StringUtils.countMatches(resource, ":") > 1){
			resource = resource.substring(0, resource.lastIndexOf(":"));
		}

		stack = parseCombinedList(Collections.singletonList(resource), isTag).stream().findFirst().orElse(null);
		if(!isFocused()){
			return;
		}

		run(resource);

		if(suggestions.size() == 0 && !resource.isEmpty()){
			run("");
		}

		suggestions.sort((c1, c2) -> c2.mod.compareTo(c1.mod));
		suggestions.sort(Comparator.comparing(c -> c.id));
		suggestions.removeIf((s) -> !start.isEmpty() && !s.id.startsWith(start));
		suggestions.removeIf(by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry::isEmpty);

		for(int i = 0; i < suggestions.size(); i++){
			by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry entry = suggestions.get(i);
			if(list != null){
				list.addEntry(new by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceDropdownEntry(this, i, entry, (val) -> {
					setValue(val.id);
					setFocus(false);
					update();
				}));
			}
		}
	}

	private void run(String resource){
		SuggestionsBuilder builder = new SuggestionsBuilder(resource, 0);

		if(isItem){
			SharedSuggestionProvider.suggestResource(ForgeRegistries.ITEMS.getKeys(), builder);
		}
		if(isBlock){
			SharedSuggestionProvider.suggestResource(ForgeRegistries.BLOCKS.getKeys(), builder);
		}
		if(isEntity){
			SharedSuggestionProvider.suggestResource(ForgeRegistries.ENTITIES.getKeys(), builder);
		}
		if(isEffect){
			SharedSuggestionProvider.suggestResource(ForgeRegistries.MOB_EFFECTS.getKeys(), builder);
		}
		if(isBiome){
			SharedSuggestionProvider.suggestResource(ForgeRegistries.BIOMES.getKeys(), builder);
		}

		if(isTag){
			if(isBlock){
				SharedSuggestionProvider.suggestResource(ForgeRegistries.BLOCKS.getKeys(), builder);
			}
			if(isItem){
				SharedSuggestionProvider.suggestResource(ForgeRegistries.ITEMS.getKeys(), builder);
			}
			if(isEntity){
				SharedSuggestionProvider.suggestResource(ForgeRegistries.ENTITIES.getKeys(), builder);
			}
		}

		Suggestions sgs = builder.build();
		List<String> suggestions = sgs.getList().stream().map(Suggestion::getText).toList();
		suggestions.removeIf((s) -> s == null || s.isEmpty());
		suggestions.forEach((s) -> this.suggestions.addAll(parseCombinedList(Collections.singletonList(s), isTag)));
	}

	public List<by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry> parseCombinedList(List<String> values, boolean isTag){
		List<by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry> results = new ArrayList<>();

		for(String value : values){
			if(value.isEmpty() || StringUtils.countMatches(value, ":") == 0){
				continue;
			}

			ResourceLocation location = ResourceLocation.tryParse(value);
			if(location == null){
				continue;
			}

			if(isTag){
				if(isItem){
					try{
						results.add(new ResourceEntry("tag:" + value, Objects.requireNonNull((ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registry.ITEM_REGISTRY, location))).stream().map(ItemStack::new).toList())));
					}catch(Exception ignored){
					}
				}

				if(isBlock){
					try{
						results.add(new ResourceEntry("tag:" + value, Objects.requireNonNull(ForgeRegistries.BLOCKS.tags().getTag(TagKey.create(Registry.BLOCK_REGISTRY, location))).stream().map((s) -> new ItemStack(s)).toList()));
					}catch(Exception ignored){
					}
				}

				if(isEntity){
					try{
						results.add(new ResourceEntry("tag:" + value, Objects.requireNonNull(ForgeRegistries.ENTITIES.tags().getTag(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, location))).stream().map((s) -> new ItemStack(ForgeSpawnEggItem.fromEntityType(s))).toList()));
					}catch(Exception ignored){
					}
				}


//				if (isBiome) {
//					try {
//						Set<ResourceKey<Biome>> biomes = BiomeDictionary.getBiomes(BiomeDictionaryHelper.getType(value));
//						results.addAll(biomes.stream().map((bi) -> {
//							try{
//								Biome biome = ForgeRegistries.BIOMES.getValue(bi.location());
//								biome.getGenerationSettings().sur
//								return new ResourceEntry("tag:" + value, Collections.singletonList(new ItemStack(biome.getGenerationSettings().get().getTopMaterial().getBlock())));
//							}catch(Exception ignored){
//							}
//							return null;
//						}).toList());
//					} catch (Exception ignored) {}
//				}
			}

			if(isItem){
				try{
					results.add(new by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry("item:" + value, Collections.singletonList(new ItemStack(new ItemParser(new StringReader(value), false).parse().getItem()))));
				}catch(Exception ignored){
				}
			}

			if(isBlock){
				try{
					results.add(new by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry("block:" + value, Collections.singletonList(new ItemStack(Objects.requireNonNull(new BlockStateParser(new StringReader(value), false).parse(false).getState()).getBlock()))));
				}catch(Exception ignored){
				}
			}

			if(isEntity){
				try{
					EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(location);
					if(entityType != null){
						SpawnEggItem item = ForgeSpawnEggItem.fromEntityType(entityType);

						if(item != null){
							results.add(new by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry("entity:" + value, Collections.singletonList(new ItemStack(item))));
						}else{
							results.add(new by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry("entity:" + value, Collections.singletonList(new ItemStack(new ItemParser(new StringReader(value), false).parse().getItem()))));
						}
					}
				}catch(Exception ignored){
				}
			}

			if(isEffect){
				try{
					MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(location);
					MobEffectInstance instance = new MobEffectInstance(effect, 20);
					ItemStack stack = new ItemStack(Items.POTION);
					PotionUtils.setPotion(stack, Potions.WATER);
					PotionUtils.setCustomEffects(stack, Collections.singletonList(instance));
					results.add(new by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.ResourceEntry("effect:" + value, Collections.singletonList(stack)));
				}catch(Exception ignored){
				}
			}
			//TODO
			//			if (isBiome) {
			//				try {
			//					Biome biome = ForgeRegistries.BIOMES.getValue(location);
			//					results.add(new ResourceEntry("biome:"+value, Collections.singletonList(new ItemStack(biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial().getBlock()))));
			//				} catch (Exception ignored) {}
			//			}
		}
		results.forEach((s) -> {
			if(s.displayItems != null && !s.displayItems.isEmpty()){
				s.displayItems = s.displayItems.stream().filter((c) -> {
					boolean blItem = c.getItem() instanceof BlockItem;
					boolean nameBlItem = c.getItem() instanceof ItemNameBlockItem;

					return !isItem ? !nameBlItem : isBlock || (nameBlItem || !blItem);
				}).toList();
				s.displayItems = s.displayItems.stream().filter((c) -> !c.isEmpty()).toList();
			}
		});
		return results;
	}

	@Override
	public void deleteChars(int pNum){
		super.deleteChars(pNum);
		update();
	}

	@Override
	public boolean charTyped(char pCodePoint, int pModifiers){
		boolean val = super.charTyped(pCodePoint, pModifiers);
		update();
		return val;
	}

	@Override
	public void setFocus(boolean focus){
		super.setFocus(focus);

		Screen screen = Minecraft.getInstance().screen;

		if(focus){
			list = new by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown.DropdownList(x, y + height, width, Math.min(suggestions.size(), maxItems) * height, 23);
			update();

			boolean hasBorder = false;
			if(screen.children.size() > 0){
				screen.children.add(0, list);
				screen.children.add(list);

				screen.renderables.add(0, list);
				screen.renderables.add(list);

				for(GuiEventListener child : screen.children){
					if(child instanceof AbstractSelectionList){
						if(((AbstractSelectionList)child).renderTopAndBottom){
							hasBorder = true;
							break;
						}
					}
				}
			}else{
				screen.children.add(list);
				screen.renderables.add(list);
			}

			boolean finalHasBorder = hasBorder;
			renderButton = new ExtendedButton(0, 0, 0, 0, TextComponent.EMPTY, null){
				@Override
				public void render(PoseStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_){
					this.active = this.visible = false;
					list.visible = ResourceTextField.this.visible;

					if(finalHasBorder){
						RenderSystem.enableScissor(0, (int)(32 * Minecraft.getInstance().getWindow().getGuiScale()), Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight() - (int)((32) * Minecraft.getInstance().getWindow().getGuiScale()) * 2);
					}

					if(list.visible){
						list.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
					}

					if(finalHasBorder){
						RenderSystem.disableScissor();
					}
				}
			};
			screen.children.add(renderButton);
			screen.renderables.add(renderButton);
		}else{
			screen.children.removeIf((s) -> s == list);
			screen.children.removeIf((s) -> s == renderButton);

			screen.renderables.removeIf((s) -> s == list);
			screen.renderables.removeIf((s) -> s == renderButton);
			list = null;
		}
	}

	@Override
	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks){
		GuiUtils.drawContinuousTexturedBox(pPoseStack, BACKGROUND_TEXTURE, x, y + 1, 0, isHovered ? 32 : 0, width, height, 32, 32, 10, 0);

		if(stack != null && !stack.isEmpty()){
			stack.tick();
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

		super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTicks);
		setTextColor(14737632);

		if(getValue().isEmpty()){
			boolean isFocus = isFocused();
			setFocus(false);
			int curser = getCursorPosition();
			setCursorPosition(0);
			setTextColor(7368816);
			setValue(this.getMessage().getString());
			super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTicks);
			setValue("");
			setTextColor(14737632);
			setCursorPosition(curser);
			setFocus(isFocus);
		}

		this.x -= 25;
		this.y -= 6;
	}

	@Override
	public List<FormattedCharSequence> getTooltip(){
		return List.of();
	}
}