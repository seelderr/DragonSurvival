package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.ResourceTextFieldOption;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.lists.OptionsList;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigType;
import com.google.common.primitives.Primitives;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResourceTextField extends EditBox implements TooltipAccessor {
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	private static final int maxItems = 6;

	private final List<ResourceEntry> suggestions = new ArrayList<>();
	private final ResourceTextFieldOption textField;

	protected DropdownList list;

	private ResourceEntry stack;

    private final String optionKey;
	// TODO :: Enum?
	private final boolean isItem;
	private final boolean isBlock;
	private final boolean isEntity;
	private final boolean isEffect;
	private final boolean isBiome;

	public ResourceTextField(final String optionKey, final ResourceTextFieldOption textField, int x, int y, int width, int height, final Component component) {
		super(Minecraft.getInstance().font, x, y, width, height, component);
		setBordered(false);
		this.textField = textField;
        this.optionKey = optionKey;

		Field field = ConfigHandler.configFields.get(optionKey);
		Class<?> checkType = Primitives.unwrap(field.getType());

		if (field.isAnnotationPresent(ConfigType.class)) {
			ConfigType type = field.getAnnotation(ConfigType.class);
			checkType = Primitives.unwrap(type.value());
		}

		isItem = Item.class.isAssignableFrom(checkType);
		isBlock = Block.class.isAssignableFrom(checkType);
		isEntity = EntityType.class.isAssignableFrom(checkType);
		isEffect = MobEffect.class.isAssignableFrom(checkType);
		isBiome = Biome.class.isAssignableFrom(checkType);

		list = new DropdownList(this.x, this.y + this.height, this.width, 0, 23);
		// So that when you click on the suggestion entries it fills the text field
		Minecraft.getInstance().screen.children.add(list);
		update();
	}

	@Override
	public void render(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (shouldBeHidden()) {
			return;
		}

//		RenderSystem.enableDepthTest();
		super.render(poseStack, mouseX, mouseY, partialTicks);

		// Re-check if still focused
		if ((isFocused() || list != null) && (!visible || !isMouseOver(mouseX, mouseY) && !list.isMouseOver(mouseX, mouseY))) {
			setFocus(false);
		}

		if (isFocused() && list != null) {
			list.reposition(x, y + height, width, Math.min(suggestions.size() + 1, maxItems) * height);
		}

		if (list != null && list.visible) {
//			poseStack.translate(0, 0, 200);
			list.render(poseStack, mouseX, mouseY, partialTicks);
//			poseStack.translate(0, 0, -200);
		}

//		RenderSystem.disableDepthTest();
	}

	@Override
	public boolean isActive() {
		if (shouldBeHidden()) {
			return false;
		}

		return super.isActive();
	}

	public void update() {
		stack = null;
		suggestions.clear();

		if (list != null) {
			list.children().clear();
		}

		String resource = getValue().isEmpty() && textField != null ? textField.getter.apply(Minecraft.getInstance().options) : getValue();

		// Only keep the actual resource location (namespace:path)
		while (StringUtils.countMatches(resource, ":") > 1) {
			resource = resource.substring(0, resource.lastIndexOf(":"));
		}

		List<ResourceEntry> resourceEntries = parseCombinedList(Collections.singletonList(resource), true);

		for (ResourceEntry resourceEntry : resourceEntries) {
			if (!resourceEntry.displayItems.isEmpty()) {
				stack = resourceEntry;
				break;
			} else if (resourceEntries.indexOf(resourceEntry) == resourceEntries.size() - 1) {
				stack = resourceEntry;
			}
		}

		if (!isFocused()) {
			return;
		}

		fillSuggestions(resource);

		if (suggestions.isEmpty() && !resource.isEmpty()) {
			fillSuggestions("");
		}

		suggestions.removeIf(entry -> entry.id.isEmpty());
		suggestions.removeIf(ResourceEntry::isEmpty);
		suggestions.sort((entryOne, entryTwo) -> entryTwo.mod.compareTo(entryOne.mod));
		suggestions.sort(Comparator.comparing(c -> c.id));

		for (int i = 0; i < suggestions.size(); i++) {
			ResourceEntry entry = suggestions.get(i);

			if (list != null) {
				list.addEntry(new ResourceDropdownEntry(this, i, entry, val -> {
					setValue(val.id);
					setFocus(false);
					update();
				}));
			}
		}
	}

	private void fillSuggestions(final String resource) {
		SuggestionsBuilder builder = new SuggestionsBuilder(resource, 0);

		if (isItem) {
			SharedSuggestionProvider.suggestResource(ForgeRegistries.ITEMS.getKeys(), builder);
		}

		if (isBlock) {
			SharedSuggestionProvider.suggestResource(ForgeRegistries.BLOCKS.getKeys(), builder);
		}

		if (isEntity) {
			SharedSuggestionProvider.suggestResource(ForgeRegistries.ENTITY_TYPES.getKeys(), builder);
		}

		if (isEffect) {
			SharedSuggestionProvider.suggestResource(ForgeRegistries.MOB_EFFECTS.getKeys(), builder);
		}

		if (isBiome) {
			SharedSuggestionProvider.suggestResource(ForgeRegistries.BIOMES.getKeys(), builder);
		}

		Suggestions sgs = builder.build();
		List<String> suggestions = new ArrayList<>(sgs.getList().stream().map(Suggestion::getText).toList());

		suggestions.removeIf(string -> string == null || string.isEmpty());
		// TODO :: Currently does not suggest tags?
		suggestions.forEach(string -> this.suggestions.addAll(parseCombinedList(Collections.singletonList(string), true)));
	}

	/**
	 * @param values The resource locations
	 * @param isTag If this is set also search the registries for tags
	 * @return List of entries which match the given resource location
	 */
	public List<ResourceEntry> parseCombinedList(final List<String> values, boolean isTag) {
		List<ResourceEntry> results = new ArrayList<>();

		for (String value : values) {
			if (value.isEmpty() || StringUtils.countMatches(value, ":") == 0) {
				continue;
			}

			ResourceLocation location = ResourceLocation.tryParse(value);

			if (location == null) {
				continue;
			}

			// Go through the registries to create a ResourceEntry (which will contain relevant information, e.g. a fitting ItemStack to render in the text field)
			// TODO :: Currently tags are not suggested
			if (isTag) {
				if (isItem) {
					try {
						// Exception can be IllegalAccessException : java.lang.LinkageError: bad method type alias: (ItemLike)void not visible from class net.minecraft.world.item.ItemStack
						// DragonSurvivalMod.LOGGER.debug("Error while trying to retrieve value from registry for the config, value: [" + value + "] - [" + element.getDescriptionId() + "]", e);
						results.add(new ResourceEntry(value, Objects.requireNonNull(ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registry.ITEM_REGISTRY, location)).stream().map(ItemStack::new).toList()), true));
					} catch (Exception e) {
						DragonSurvivalMod.LOGGER.debug("Error while trying to retrieve a value from the 'ITEMS' registry for the config, value: [" + value + "]", e);
					}
				}

				if (isBlock) {
					try {
						results.add(new ResourceEntry(value, Objects.requireNonNull(ForgeRegistries.BLOCKS.tags().getTag(TagKey.create(Registry.BLOCK_REGISTRY, location))).stream().map(ItemStack::new).toList(), true));
					} catch (Exception e) {
						DragonSurvivalMod.LOGGER.debug("Error while trying to retrieve a value from the 'BLOCKS' registry for the config, value: [" + value + "]", e);
					}
				}

				if (isEntity) {
                    try {
                        results.add(new ResourceEntry(value, Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags().getTag(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, location))).stream().map(type -> new ItemStack(ForgeSpawnEggItem.fromEntityType(type))).toList(), true));
                    } catch (Exception e) {
						DragonSurvivalMod.LOGGER.debug("Error while trying to retrieve a value from the 'ENTITY_TYPES' registry for the config, value: [" + value + "]", e);
					}
                }
			}

			if (isItem) {
                try {
                    results.add(new ResourceEntry(value, Collections.singletonList(new ItemStack(ItemParser.parseForItem(HolderLookup.forRegistry(Registry.ITEM), new StringReader(value)).item()))));
                } catch (CommandSyntaxException ignored) { /* Nothing to do */ }
            }

			if (isBlock) {
                try {
                    results.add(new ResourceEntry(value, Collections.singletonList(new ItemStack(BlockStateParser.parseForBlock(HolderLookup.forRegistry(Registry.BLOCK), new StringReader(value), false).blockState().getBlock()))));
                } catch (CommandSyntaxException ignored) { /* Nothing to do */ }
            }

			if (isEntity) {
                try {
                    EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(location);

                    if (entityType != null) {
                        SpawnEggItem item = ForgeSpawnEggItem.fromEntityType(entityType);

                        if (item != null) {
                            results.add(new ResourceEntry(value, Collections.singletonList(new ItemStack(item))));
                        } else {
                            results.add(new ResourceEntry(value, Collections.singletonList(new ItemStack(ItemParser.parseForItem(HolderLookup.forRegistry(Registry.ITEM), new StringReader(value)).item()))));
                        }
                    }
                } catch (CommandSyntaxException ignored) { /* Nothing to do */ }
            }

			if (isEffect) {
                try {
                    MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(location);
                    MobEffectInstance instance = new MobEffectInstance(effect, 20);
                    ItemStack stack = new ItemStack(Items.POTION);
                    PotionUtils.setPotion(stack, Potions.WATER);
                    PotionUtils.setCustomEffects(stack, Collections.singletonList(instance));
                    results.add(new ResourceEntry(value, Collections.singletonList(stack)));
                } catch (Exception e) {
					DragonSurvivalMod.LOGGER.debug("Error while trying to retrieve a value from the 'MOB_EFFECTS' registry for the config, value: [" + value + "]", e);
				}
            }
		}

		results.forEach(entry -> {
			if (entry.displayItems != null && !entry.displayItems.isEmpty()) {
				entry.displayItems = entry.displayItems.stream().filter(c -> {
					boolean blockItem = c.getItem() instanceof BlockItem;
					boolean itemNameBlockItem = c.getItem() instanceof ItemNameBlockItem;

					return !isItem ? !itemNameBlockItem : isBlock || itemNameBlockItem || !blockItem;
				}).toList();

				entry.displayItems = entry.displayItems.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
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
	public void setFocus(boolean focus) {
		if (shouldBeHidden()) {
			return;
		}

		super.setFocus(focus);
        list.visible = focus && Minecraft.getInstance().screen != null && !Minecraft.getInstance().screen.children.isEmpty();

        if (Minecraft.getInstance().screen == null) {
            DragonSurvivalMod.LOGGER.warn("Screen was not available while trying to focus 'ResourceTextField' [" + optionKey + "]");
            return;
        }

		// Hide suggestion windows which are no longer relevant
		Minecraft.getInstance().screen.children.forEach(widget -> {
			if (widget instanceof DropdownList dropdownList && dropdownList != list) {
				if (list.visible && dropdownList.visible) {
					dropdownList.visible = false;
				}
			}
		});
	}

	/** Avoid overlapping suggestion entries */
	private boolean shouldBeHidden() {
		AtomicBoolean shouldBeHidden = new AtomicBoolean(false);

		Minecraft.getInstance().screen.children.forEach(widget -> {
			if (widget instanceof OptionsList optionsList) {
				optionsList.children().forEach(listEntry -> {
					GuiEventListener entry = listEntry.children().get(0);

					if (entry instanceof ResourceTextField resourceTextField && resourceTextField != this) {
						if (resourceTextField.list.visible && !resourceTextField.list.children().isEmpty()) {
							// Offset by item height since without it the text field below the focused one is not hidden
							if (y > resourceTextField.list.getTop() - 23 && y < resourceTextField.list.getBottom() + 3) {
								shouldBeHidden.set(true);
							}
						}
					}
				});
			}
		});

		return shouldBeHidden.get();
	}

	@Override
	public void renderButton(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (shouldBeHidden()) {
			return;
		}

		int v = isHovered ? 32 : 0;

		// Background box for the list entries
		ScreenUtils.blitWithBorder(poseStack, BACKGROUND_TEXTURE, x, y + 1, 0, v, width, height, 32, 32, 10, 10, 10, 10, (float) 0);

		if (stack != null && !stack.isEmpty()) {
			stack.tick();
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			itemRenderer.renderAndDecorateItem(stack.getDisplayItem(), x + 3, y + 3);
			itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, stack.getDisplayItem(), x + 3, y + 3);
		}

		x += 25;
		y += 6;

		super.renderButton(poseStack, mouseX, mouseY, partialTicks);
		setTextColor(14737632);

		if (getValue().isEmpty()) { // FIXME :: What is this for?
			boolean isFocus = isFocused();
			setFocus(false);
			int cursor = getCursorPosition();
			setCursorPosition(0);
			setTextColor(7368816);
			setValue(getMessage().getString());
			super.renderButton(poseStack, mouseX, mouseY, partialTicks);
			setValue("");
			setTextColor(14737632);
			setCursorPosition(cursor);
			setFocus(isFocus);
		}

		x -= 25;
		y -= 6;
	}

	@Override
	public @NotNull List<FormattedCharSequence> getTooltip() {
		return List.of();
	}
}