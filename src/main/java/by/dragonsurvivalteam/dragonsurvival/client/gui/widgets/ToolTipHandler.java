package by.dragonsurvivalteam.dragonsurvival.client.gui.widgets;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.AbilityScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.SkillProgressButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.joml.Vector2ic;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

@EventBusSubscriber(Dist.CLIENT)
public class ToolTipHandler {
    private static final ResourceLocation TOOLTIP_BLINKING = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/magic_tips_1.png");
    private static final ResourceLocation TOOLTIP = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/magic_tips_0.png");

    @ConfigOption(side = ConfigSide.CLIENT, category = "tooltips", key = "tooltipChanges", comment = "Should the mod be allowed ot change the color and appearance of tooltips?")
    public static Boolean tooltipChanges = true;

    @ConfigOption(side = ConfigSide.CLIENT, category = "tooltips", key = "hideUnsafeFood", comment = "Should the tooltip be hidden for unsafe (negative effects) food?")
    public static Boolean hideUnsafeFood = true;

    @ConfigOption(side = ConfigSide.CLIENT, category = "tooltips", key = "dragonFoodTooltips", comment = "Should dragon foods have their tooltip color changed to show which type of dragon can consume it?")
    public static Boolean dragonFoodTooltips = true;

    private final static ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(MODID, "food_tooltip_icon_font");

    @SubscribeEvent
    public static void checkIfDragonFood(ItemTooltipEvent tooltipEvent) {
        if (tooltipEvent.getEntity() != null) {
            Item item = tooltipEvent.getItemStack().getItem();
            List<Component> toolTip = tooltipEvent.getToolTip();

            if (DragonFoodHandler.getEdibleFoods(DragonTypes.CAVE).contains(item)) {
                toolTip.add(createFoodTooltip(item, DragonTypes.CAVE, ChatFormatting.RED, "\uEA02", "\uEA05"));
            }

            if (DragonFoodHandler.getEdibleFoods(DragonTypes.FOREST).contains(item)) {
                toolTip.add(createFoodTooltip(item, DragonTypes.FOREST, ChatFormatting.GREEN, "\uEA01", "\uEA04"));
            }

            if (DragonFoodHandler.getEdibleFoods(DragonTypes.SEA).contains(item)) {
                toolTip.add(createFoodTooltip(item, DragonTypes.SEA, ChatFormatting.DARK_AQUA, "\uEA03", "\uEA06"));
            }
        }
    }

    private static MutableComponent createFoodTooltip(final Item item, final AbstractDragonType type, final ChatFormatting color, final String nutritionIcon, final String saturationIcon) {
        MutableComponent component = Component.translatable("ds." + type.getTypeName() + ".dragon.food");
        FoodProperties properties = DragonFoodHandler.getDragonFoodProperties(item, type);

        String nutrition = "0";
        String saturation = "0";

        if (properties != null) {
            float nutritionValue = properties.nutrition();
            float saturationValue = properties.saturation();

            // 1 Icon = 2 points (e.g. 10 nutrition icons for a maximum food level of 20)
            nutrition = String.format("%.1f", nutritionValue / 2);
            saturation = String.format("%.1f", saturationValue / 2);
        }

        MutableComponent nutritionIconComponent = Component.literal(nutritionIcon).withStyle(Style.EMPTY.withFont(ICONS));
        MutableComponent nutritionComponent = Component.literal(": " + nutrition + " ").withStyle(color);

        MutableComponent saturationIconComponent = Component.literal(saturationIcon).withStyle(Style.EMPTY.withFont(ICONS));
        MutableComponent saturationComponent = Component.literal(" / " + saturation + " ").withStyle(color);

        return component.append(nutritionComponent).append(nutritionIconComponent).append(saturationComponent).append(saturationIconComponent);
    }

    @SubscribeEvent
    public static void itemDescriptions(ItemTooltipEvent event) {
        if (event.getEntity() != null) {
            Item item = event.getItemStack().getItem();
            List<Component> toolTip = event.getToolTip();

            if (item == DSBlocks.FIRE_DRAGON_BEACON.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.passiveFireBeacon"));
            }
            if (item == DSBlocks.MAGIC_DRAGON_BEACON.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.passiveMagicBeacon"));
            }
            if (item == DSBlocks.PEACE_DRAGON_BEACON.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.passivePeaceBeacon"));
            }
            if (item == DSBlocks.CAVE_DRAGON_DOOR.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.caveDoor"));
            }
            if (item == DSBlocks.FOREST_DRAGON_DOOR.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.forestDoor"));
            }
            if (item == DSBlocks.SEA_DRAGON_DOOR.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.seaDoor"));
            }
            if (item == DSBlocks.LEGACY_DRAGON_DOOR.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.legacyDoor"));
            }
            if (item == DSBlocks.HELMET_BLOCK_1.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.grayHelmet"));
            }
            if (item == DSBlocks.HELMET_BLOCK_2.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.goldHelmet"));
            }
            if (item == DSBlocks.HELMET_BLOCK_3.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.blackHelmet"));
            }
            if (item == DSBlocks.DRAGON_BEACON.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.dragonBeacon"));
            }
            if (item == DSBlocks.DRAGON_MEMORY_BLOCK.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.dragonMemoryBlock"));
            }
            if (item == DSBlocks.SEA_SOURCE_OF_MAGIC.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.sea_source_of_magic"));
            }
            if (item == DSBlocks.FOREST_SOURCE_OF_MAGIC.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.forest_source_of_magic"));
            }
            if (item == DSBlocks.CAVE_SOURCE_OF_MAGIC.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.cave_source_of_magic"));
            }
            if (item == DSBlocks.DRAGON_PRESSURE_PLATE.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.dragon_pressure_plate"));
            }
            if (item == DSBlocks.HUMAN_PRESSURE_PLATE.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.human_pressure_plate"));
            }
            if (item == DSBlocks.SEA_PRESSURE_PLATE.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.sea_dragon_pressure_plate"));
            }
            if (item == DSBlocks.FOREST_PRESSURE_PLATE.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.forest_dragon_pressure_plate"));
            }
            if (item == DSBlocks.CAVE_PRESSURE_PLATE.get().asItem()) {
                toolTip.add(Component.translatable("ds.description.cave_dragon_pressure_plate"));
            }
        }
    }

    private static boolean isBlinking;
    private static int tick;

    @SubscribeEvent
    public static void renderHelpTextCornerElements(RenderTooltipEvent.Pre event) {
        boolean render = isHelpText();

        if (!render) {
            return;
        }

        if (!isBlinking) {
            if (tick >= Functions.secondsToTicks(30)) {
                isBlinking = true;
                tick = 0;
            }
        } else {
            if (tick >= Functions.secondsToTicks(5)) {
                isBlinking = false;
                tick = 0;
            }
        }

        tick++;

        int width = event.getComponents().stream().map(component -> component.getWidth(Minecraft.getInstance().font)).max(Integer::compareTo).orElse(0);
        int height = event.getComponents().stream().map(ClientTooltipComponent::getHeight).reduce(Integer::sum).orElse(0);
        Vector2ic tooltipPosition = event.getTooltipPositioner().positionTooltip(event.getScreenWidth(), event.getScreenHeight(), event.getX(), event.getY(), width, height);

        int x = tooltipPosition.x();
        int y = tooltipPosition.y();

        int texWidth = 128;
        int texHeight = 128;

        event.getGraphics().blit(isBlinking ? TOOLTIP_BLINKING : TOOLTIP, x - 8 - 6, y - 8 - 6, 400, 1, 1 % texHeight, 16, 16, texWidth, texHeight);
        event.getGraphics().blit(isBlinking ? TOOLTIP_BLINKING : TOOLTIP, x + width - 8 + 6, y - 8 - 6, 400, texWidth - 16 - 1, 1 % texHeight, 16, 16, texWidth, texHeight);

        event.getGraphics().blit(isBlinking ? TOOLTIP_BLINKING : TOOLTIP, x - 8 - 6, y + height - 8 + 6, 400, 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);
        event.getGraphics().blit(isBlinking ? TOOLTIP_BLINKING : TOOLTIP, x + width - 8 + 6, y + height - 8 + 6, 400, texWidth - 16 - 1, 1 % texHeight + 16, 16, 16, texWidth, texHeight);

        event.getGraphics().blit(isBlinking ? TOOLTIP_BLINKING : TOOLTIP, x + width / 2 - 47, y - 16, 400, 16 + 2 * texWidth + 1, 1 % texHeight, 94, 16, texWidth, texHeight);
        event.getGraphics().blit(isBlinking ? TOOLTIP_BLINKING : TOOLTIP, x + width / 2 - 47, y + height, 400, 16 + 2 * texWidth + 1, 1 % texHeight + 16, 94, 16, texWidth, texHeight);
    }

    private static boolean isHelpText() {
        if (!tooltipChanges) {
            return false;
        }
        if (Minecraft.getInstance().level == null) {
            return false;
        }

        if (Minecraft.getInstance().screen == null) {
            return false;
        }

        for (GuiEventListener btn : Minecraft.getInstance().screen.children()) {
            if (btn instanceof HelpButton && ((HelpButton) btn).isHoveredOrFocused()) {
                return true;
            }
        }

        return false;
    }

    @SubscribeEvent
    public static void renderTooltipBorderInDragonColor(RenderTooltipEvent.Color event) {
        if (!tooltipChanges) {
            return;
        }

        boolean isAbilityScreen = Minecraft.getInstance().screen instanceof AbilityScreen;
        ItemStack stack = event.getItemStack();

        boolean isSeaFood = dragonFoodTooltips && !stack.isEmpty() && DragonFoodHandler.getEdibleFoods(DragonTypes.SEA).contains(stack.getItem());
        boolean isForestFood = dragonFoodTooltips && !stack.isEmpty() && DragonFoodHandler.getEdibleFoods(DragonTypes.FOREST).contains(stack.getItem());
        boolean isCaveFood = dragonFoodTooltips && !stack.isEmpty() && DragonFoodHandler.getEdibleFoods(DragonTypes.CAVE).contains(stack.getItem());

        boolean isDragonFood = isSeaFood || isForestFood || isCaveFood;
        boolean isSkillProgressButtonHovered = false;

        if (isAbilityScreen) {
            for (GuiEventListener widget : ((AbilityScreen) Minecraft.getInstance().screen).widgetList()) {
                if (widget instanceof SkillProgressButton && ((SkillProgressButton) widget).isHoveredOrFocused()) {
                    isSkillProgressButtonHovered = true;
                    break;
                }
            }
        }

        if (isHelpText()) {
            int top = new Color(154, 132, 154).getRGB();
            int bottom = new Color(89, 68, 89).getRGB();

            event.setBorderStart(top);
            event.setBorderEnd(bottom);
        } else if (isAbilityScreen || isDragonFood) {
            AbstractDragonType type = DragonUtils.getDragonType(Minecraft.getInstance().player);
            Color topColor = null;
            Color bottomColor = null;

            if (type != null) {
                if (Objects.equals(type, DragonTypes.SEA) && isSkillProgressButtonHovered || isSeaFood) {
                    topColor = new Color(93, 201, 255);
                    bottomColor = new Color(49, 109, 144);
                } else if (Objects.equals(type, DragonTypes.FOREST) && isSkillProgressButtonHovered || isForestFood) {
                    topColor = new Color(0, 255, 148);
                    bottomColor = new Color(4, 130, 82);
                } else if (Objects.equals(type, DragonTypes.CAVE) && isSkillProgressButtonHovered || isCaveFood) {
                    topColor = new Color(255, 118, 133);
                    bottomColor = new Color(139, 66, 74);
                }
            }

            if (topColor != null) {
                event.setBorderStart(topColor.getRGB());
            }

            if (bottomColor != null) {
                event.setBorderEnd(bottomColor.getRGB());
            }
        }
    }
}