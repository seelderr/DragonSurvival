package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.dropdown;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.widgets.ResourceTextFieldOption;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigType;
import com.google.common.primitives.Primitives;
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
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

public class ResourceTextField extends EditBox implements TooltipAccessor {
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/textbox.png");
	private static final int maxItems = 6;
	private final List<ResourceEntry> suggestions = new ArrayList<>();
	boolean isItem;
	boolean isBlock;
	boolean isEntity;
	boolean isEffect;
	boolean isBiome;
	private ResourceTextFieldOption option;
	private String optionKey;
	private ResourceEntry stack;
	protected DropdownList list;
	private AbstractWidget renderButton;

	private static ResourceTextField ACTIVE;

	public ResourceTextField(String optionKey, ResourceTextFieldOption option, int pX, int pY, int pWidth, int pHeight, Component pMessage){
		super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, pMessage);
		setBordered(false);
		this.option = option;
		this.optionKey = optionKey;

		Field fe = ConfigHandler.configFields.get(optionKey);

		Class<?> checkType = Primitives.unwrap(fe.getType());

		if(fe.isAnnotationPresent(ConfigType.class)){
			ConfigType type = fe.getAnnotation(ConfigType.class);
			checkType = Primitives.unwrap(type.value());
		}

		isItem = Item.class.isAssignableFrom(checkType);
		isBlock = Block.class.isAssignableFrom(checkType);
		isEntity = EntityType.class.isAssignableFrom(checkType);
		isEffect = MobEffect.class.isAssignableFrom(checkType);
		isBiome = Biome.class.isAssignableFrom(checkType);

		list = new DropdownList(x, y + height, width, 0, 23);
		Minecraft.getInstance().screen.children.add(list); // TODO :: Remove when closing screen?

		update();
	}

	@Override
	public void render(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);

		// Re-check if still focused
		if ((isFocused() || list != null) && (!visible || !isMouseOver(mouseX, mouseY) && !list.isMouseOver(mouseX, mouseY))) {
			setFocus(false);
		}

		if (isFocused() && list != null) {
			list.reposition(x, y + height, width, Math.min(suggestions.size() + 1, maxItems) * height);
		}

		if (list != null && list.visible) {
			list.render(poseStack, mouseX, mouseY, partialTicks);
		}
	}

	public void update() {
		stack = null;
		suggestions.clear();

		if (list != null) {
			list.children().clear();
		}

		String resource = getValue().isEmpty() && option != null ? option.getter.apply(Minecraft.getInstance().options) : getValue();

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

		run(resource);

		if (suggestions.isEmpty() && !resource.isEmpty()) {
			run("");
		}

		suggestions.sort((entryOne, entryTwo) -> entryTwo.mod.compareTo(entryOne.mod));
		suggestions.sort(Comparator.comparing(c -> c.id));
		suggestions.removeIf(entry -> entry.id.isEmpty());
		suggestions.removeIf(ResourceEntry::isEmpty);

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

	private void run(final String resource) {
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
		suggestions.forEach(string -> {
			this.suggestions.addAll(parseCombinedList(Collections.singletonList(string), true));
//			this.suggestions.addAll(parseCombinedList(Collections.singletonList(string), false));
		});
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

			if (isTag) {
				if (isItem) {
					try {
						results.add(new ResourceEntry(value, Objects.requireNonNull(ForgeRegistries.ITEMS.tags().getTag(TagKey.create(Registry.ITEM_REGISTRY, location)).stream().map(ItemStack::new).toList()), true));
					} catch (Exception ignored) { /* Nothing to do */ }
				}

				if (isBlock) {
					try {
						results.add(new ResourceEntry(value, Objects.requireNonNull(ForgeRegistries.BLOCKS.tags().getTag(TagKey.create(Registry.BLOCK_REGISTRY, location))).stream().map(ItemStack::new).toList(), true));
					} catch (Exception ignored) { /* Nothing to do */ }
				}

				if (isEntity) {
                    try {
                        results.add(new ResourceEntry(value, Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags().getTag(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, location))).stream().map(s -> new ItemStack(ForgeSpawnEggItem.fromEntityType(s))).toList(), true));
                    } catch (Exception ignored) { /* Nothing to do */ }
                }
			}

			if (isItem) {
                try {
                    results.add(new ResourceEntry(value, Collections.singletonList(new ItemStack(ItemParser.parseForItem(HolderLookup.forRegistry(Registry.ITEM), new StringReader(value)).item()))));
                } catch (Exception ignored) { /* Nothing to do */ }
            }

			if (isBlock) {
                try {
                    results.add(new ResourceEntry(value, Collections.singletonList(new ItemStack(BlockStateParser.parseForBlock(HolderLookup.forRegistry(Registry.BLOCK), new StringReader(value), false).blockState().getBlock()))));
                } catch (Exception ignored) { /* Nothing to do */ }
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
                } catch (Exception ignored) { /* Nothing to do */ }
            }

			if (isEffect) {
                try {
                    MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(location);
                    MobEffectInstance instance = new MobEffectInstance(effect, 20);
                    ItemStack stack = new ItemStack(Items.POTION);
                    PotionUtils.setPotion(stack, Potions.WATER);
                    PotionUtils.setCustomEffects(stack, Collections.singletonList(instance));
                    results.add(new ResourceEntry(value, Collections.singletonList(stack)));
                } catch (Exception ignored) { /* Nothing to do */ }
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
	protected boolean clicked(double mouseX, double mouseY) {
		ACTIVE = this;
		return super.clicked(mouseX, mouseY);
	}

	@Override
	public void setFocus(boolean focus) {
		super.setFocus(focus);

		Screen screen = Minecraft.getInstance().screen;

		// FIXME :: Instead of adding and removing children, do it all at once and then set them to visible / hide them?

		if (focus) {
//			list = new DropdownList(x, y + height, width, Math.min(suggestions.size(), maxItems) * height, 23);
//			update();

			boolean hasBorder = false;

			if (!screen.children.isEmpty()) {
//				screen.children.add(0, list);
//				screen.children.add(list);

				list.visible = true;

//				screen.renderables.add(0, list);
//				screen.renderables.add(list);

				for (GuiEventListener child : screen.children) {
					if (child instanceof AbstractSelectionList<?> abstractSelectionList) {
						if (abstractSelectionList.renderTopAndBottom) {
							hasBorder = true;
							break;
						}
					}
				}
			} else {
				list.visible = false;
//				screen.children.add(list);
//				screen.renderables.add(list);
			}

			boolean finalHasBorder = hasBorder;

			renderButton = new ExtendedButton(0, 0, 0, 0, Component.empty(), null) {
				@Override
				public void render(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
					active = visible = false;

					if (list != null) {
						list.visible = ResourceTextField.this.visible;

						if (finalHasBorder) {
							RenderSystem.enableScissor(0, (int) (32 * Minecraft.getInstance().getWindow().getGuiScale()), Minecraft.getInstance().getWindow().getScreenWidth(), Minecraft.getInstance().getWindow().getScreenHeight() - (int) (32 * Minecraft.getInstance().getWindow().getGuiScale()) * 2);
						}

						if (list.visible) {
							list.render(poseStack, mouseX, mouseY, partialTicks);
						}

						if (finalHasBorder) {
							RenderSystem.disableScissor();
						}
					}
				}
			};

//			screen.children.add(renderButton);
//			screen.renderables.add(renderButton);
		} else {
//			screen.children.removeIf(s -> s == list);
//			screen.children.removeIf(s -> s == renderButton);

//			screen.renderables.removeIf(s -> s == list);
//			screen.renderables.removeIf(s -> s == renderButton);

			list.visible = false;

//			list = null;
		}
	}

	@Override
	public void renderButton(@NotNull final PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		int v = isHovered ? 32 : 0;

		// Background box for the list entries
		ScreenUtils.blitWithBorder(poseStack, BACKGROUND_TEXTURE, x, y + 1, 0, v, width, height, 32, 32, 10, 10, 10, 10, (float) 0);

		if (stack != null && !stack.isEmpty()) {
			stack.tick();
			ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
			itemRenderer.renderAndDecorateItem(stack.getDisplayItem(), x + 3, y + 3);
			itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, stack.getDisplayItem(), x + 3, y + 3);
		}

		if (!isFocused()) {
//			suggestions.clear();
		}

		x += 25;
		y += 6;

		super.renderButton(poseStack, mouseX, mouseY, partialTicks);
		setTextColor(14737632);

		if (getValue().isEmpty()) {
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