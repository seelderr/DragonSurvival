package by.dragonsurvivalteam.dragonsurvival.client.gui.screens;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.TabButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonGrowthHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawRender;
import by.dragonsurvivalteam.dragonsurvival.network.claw.SyncDragonClawsMenuToggle;
import by.dragonsurvivalteam.dragonsurvival.network.container.RequestOpenInventory;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvival.MODID;

public class DragonInventoryScreen extends EffectRenderingInventoryScreen<DragonContainer> {
    @Translation(type = Translation.Type.MISC, comments = "§6Dragon Tab§r")
    private static final String TOGGLE = Translation.Type.GUI.wrap("dragon_inventory.toggle");

    @Translation(type = Translation.Type.MISC, comments = "Toggle showing claws and teeth textures on your model.")
    private static final String TOGGLE_CLAWS = Translation.Type.GUI.wrap("dragon_inventory.toggle_claws");

    @Translation(type = Translation.Type.MISC, comments = "Open vanilla inventory screen")
    private static final String TOGGLE_VANILLA_INVENTORY = Translation.Type.GUI.wrap("dragon_inventory.toggle_vanilla_inventory");

    @Translation(type = Translation.Type.MISC, comments = {
            "■ A dragon is §6born§r with strong claws and teeth, but you can make them even better! Just put §6any tools§r§f here in your claw slots and your bare paw will borrow their aspect as long as they are intact.",
            "§7■ Does not stack with «Claws and Teeth» skill, which only applies if these slots are empty."
    })
    private static final String HELP_CLAWS = Translation.Type.GUI.wrap("help.claws");

    @Translation(type = Translation.Type.MISC, comments = "■ §6Stage§r§7: ")
    private static final String GROWTH_STAGE = Translation.Type.GUI.wrap("dragon_inventory.growth_stage");

    @Translation(type = Translation.Type.MISC, comments = "■ §6Age§r§7: %s")
    private static final String GROWTH_AGE = Translation.Type.GUI.wrap("dragon_inventory.growth_age");

    @Translation(type = Translation.Type.MISC, comments = {
            "\n■ All dragons will gradually grow as time passes, improving their attributes. At certain growth stages, your appearance will change, and your growth will slow. You can use these items to speed up growth:§r",
            "§6■ %s\n",
            "§7■ A Star Bone will revert your growth slightly, and a Star Heart will completely stop you from growing. The biggest dragons can take other players for a ride!"
    })
    private static final String GROWTH_INFO = Translation.Type.GUI.wrap("dragon_inventory.growth_info");

    public static final ResourceLocation INVENTORY_TOGGLE_BUTTON = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/inventory_button.png");

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/dragon_inventory.png");
    private static final ResourceLocation CLAWS_TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/dragon_claws.png");
    private static final ResourceLocation DRAGON_CLAW_BUTTON = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/dragon_claws_button.png");
    private static final ResourceLocation DRAGON_CLAW_CHECKMARK = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/dragon_claws_checked.png");

    public static double mouseX = -1;
    public static double mouseY = -1;

    private boolean clawsMenu;
    private final Player player;
    private boolean buttonClicked;
    private boolean isGrowthIconHovered;
    private final List<ExtendedButton> clawMenuButtons = new ArrayList<>();

    private static HashMap<String, ResourceLocation> textures;

    static {
        initResources();
    }

    private static void initResources() {
        textures = new HashMap<>();

        Set<String> keys = DragonTypes.staticTypes.keySet();

        for (String key : keys) {
            AbstractDragonType type = DragonTypes.staticTypes.get(key);

            String prefix = "textures/gui/growth/";
            String suffix = ".png";

            for (int i = 1; i <= DragonLevel.keys(null).size(); i++) {
                String growthResource = createTextureKey(type, "growth", "_" + i);
                textures.put(growthResource, ResourceLocation.fromNamespaceAndPath(MODID, prefix + growthResource + suffix));
            }

            String circleResource = createTextureKey(type, "circle", "");
            textures.put(circleResource, ResourceLocation.fromNamespaceAndPath(MODID, prefix + circleResource + suffix));
        }
    }

    private static String createTextureKey(final AbstractDragonType type, final String textureType, final String addition) {
        return textureType + "_" + type.getTypeNameLowerCase() + addition;
    }

    public DragonInventoryScreen(DragonContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        player = inv.player;

        DragonStateProvider.getOptional(player).ifPresent(cap -> clawsMenu = cap.getClawToolData().isMenuOpen());

        imageWidth = 203;
        imageHeight = 166;
    }

    @Override
    @SuppressWarnings("DataFlowIssue") // minecraft gets set from setScreen -> init
    protected void init() {
        super.init();

        if (mouseX != -1 && mouseY != -1) {
            InputConstants.grabOrReleaseMouse(minecraft.getWindow().getWindow(), 212993, mouseX, mouseY);
            mouseX = -1;
            mouseY = -1;
        }

        leftPos = (width - imageWidth) / 2;

        DragonStateHandler handler = DragonStateProvider.getData(player);

        addRenderableWidget(new TabButton(leftPos, topPos - 28, TabButton.Type.INVENTORY_TAB, this));
        addRenderableWidget(new TabButton(leftPos + 28, topPos - 26, TabButton.Type.ABILITY_TAB, this));
        addRenderableWidget(new TabButton(leftPos + 57, topPos - 26, TabButton.Type.GITHUB_REMINDER_TAB, this));
        addRenderableWidget(new TabButton(leftPos + 86, topPos - 26, TabButton.Type.SKINS_TAB, this));

        ExtendedButton clawToggle = new ExtendedButton(leftPos + 27, topPos + 10, 11, 11, Component.empty(), button -> {
            clawsMenu = !clawsMenu;
            clearWidgets();
            init();

            PacketDistributor.sendToServer(new SyncDragonClawsMenuToggle.Data(clawsMenu));
            DragonStateProvider.getOptional(player).ifPresent(cap -> cap.getClawToolData().setMenuOpen(clawsMenu));
        }) {
            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                guiGraphics.blit(DRAGON_CLAW_BUTTON, getX(), getY(), 0, 0, 11, 11, 11, 11);
            }
        };
        clawToggle.setTooltip(Tooltip.create(Component.translatable(TOGGLE)));
        addRenderableWidget(clawToggle);

        // Growth icon in the claw menu
        ExtendedButton growthIcon = new ExtendedButton(leftPos - 58, topPos - 35, 32, 32, Component.empty(), btn -> {
        }) {
            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                isGrowthIconHovered = isHovered();
            }
        };
        addRenderableWidget(growthIcon);
        clawMenuButtons.add(growthIcon);

        // Info button at the bottom of the claw menu
        HelpButton infoButton = new HelpButton(leftPos - 80 + 34, topPos + 112, 9, 9, HELP_CLAWS, 0, true);
        addRenderableWidget(infoButton);
        clawMenuButtons.add(infoButton);

        // Button to enable / disable the rendering of claws
        ExtendedButton clawRenderButton = new ExtendedButton(leftPos - 80 + 34, topPos + 140, 9, 9, Component.empty(), p_onPress_1_ -> {
            boolean claws = !handler.getClawToolData().shouldRenderClaws;

            handler.getClawToolData().shouldRenderClaws = claws;
            ConfigHandler.updateConfigValue("render_dragon_claws", handler.getClawToolData().shouldRenderClaws);
            PacketDistributor.sendToServer(new SyncDragonClawRender.Data(player.getId(), claws));
        }) {
            @Override
            public void renderWidget(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                DragonStateHandler handler = DragonStateProvider.getData(player);

                if (handler.getClawToolData().shouldRenderClaws) {
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0, 0, 100);
                    guiGraphics.blit(DRAGON_CLAW_CHECKMARK, getX(), getY(), 0, 0, 9, 9, 9, 9);
                    guiGraphics.pose().popPose();
                }
            }
        };
        clawRenderButton.setTooltip(Tooltip.create(Component.translatable(TOGGLE_CLAWS)));
        addRenderableWidget(clawRenderButton);
        clawMenuButtons.add(clawRenderButton);

        if (InventoryScreenHandler.inventoryToggle) {
            ExtendedButton inventoryToggle = new ExtendedButton(leftPos + imageWidth - 28, height / 2 - 30 + 47, 20, 18, Component.empty(), p_onPress_1_ -> {
                Minecraft.getInstance().setScreen(new InventoryScreen(player));
                PacketDistributor.sendToServer(new RequestOpenInventory.Data());
            }) {
                @Override
                public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                    float u = 0f;
                    float v = isHoveredOrFocused() ? 20f : 0f;
                    guiGraphics.blit(INVENTORY_TOGGLE_BUTTON, getX(), getY(), u, v, 20, 18, 256, 256);
                }
            };
            inventoryToggle.setTooltip(Tooltip.create(Component.translatable(TOGGLE_VANILLA_INVENTORY)));
            addRenderableWidget(inventoryToggle);
        }
    }

    @Override
    protected void renderLabels(@NotNull final GuiGraphics guiGraphics, int p_230451_2_, int p_230451_3_) { /* Nothing to do */ }

    @Override
    protected void renderBg(@NotNull final GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        RenderSystem.disableBlend();

        DragonStateHandler handler = DragonStateProvider.getData(player);

        int scissorY1 = topPos + 77;
        int scissorX1 = leftPos + 101;
        int scissorX0 = leftPos + 25;
        int scissorY0 = topPos + 8;

        // In order to scale up the smaller dragon sizes, since they are too small otherwise
        int scale = (int) (20 + ((ServerConfig.DEFAULT_MAX_GROWTH_SIZE - handler.getSize()) * 0.25));
        scale = Math.clamp(scale, 10, 40); // Very large dragon sizes (above the default max. size) will have a < 20 scale value

        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, scissorX0, scissorY0, scissorX1, scissorY1, scale, 0, mouseX, mouseY, player);

        if (!clawsMenu) {
            return;
        }

        guiGraphics.blit(CLAWS_TEXTURE, leftPos - 80, topPos, 0, 0, 77, 170);

        if (textures == null || textures.isEmpty()) {
            initResources();
        }

        double curSize = handler.getSize();
        float progress = 0;

        // FIXME level
//        if (handler.getLevel() == DragonLevel.NEWBORN) {
//            progress = (float) ((curSize - DragonLevel.NEWBORN.size) / (DragonLevel.YOUNG.size - DragonLevel.NEWBORN.size));
//        } else if (handler.getLevel() == DragonLevel.YOUNG) {
//            progress = (float) ((curSize - DragonLevel.YOUNG.size) / (DragonLevel.ADULT.size - DragonLevel.YOUNG.size));
//        } else if (handler.getLevel() == DragonLevel.ADULT && handler.getSize() < 40) {
//            progress = (float) ((curSize - DragonLevel.ADULT.size) / (40 - DragonLevel.ADULT.size));
//        } else if (handler.getLevel() == DragonLevel.ADULT) {
//            progress = (float) ((curSize - 40) / (ServerConfig.maxGrowthSize - 40));
//        }

        int size = 34;
        int thickness = 5;
        int circleX = leftPos - 58;
        int circleY = topPos - 35;
        int sides = 6;

        int radius = size / 2;

        Color c = new Color(99, 99, 99);

        RenderSystem.setShaderColor(c.brighter().getRed() / 255.0f, c.brighter().getBlue() / 255.0f, c.brighter().getGreen() / 255.0f, 1.0f);
        RenderingUtils.drawSmoothCircle(guiGraphics, circleX + radius, circleY + radius, radius, sides, 1, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);
        RenderSystem.setShaderTexture(0, textures.get(createTextureKey(handler.getType(), "circle", "")));
        RenderingUtils.drawTexturedCircle(guiGraphics, circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, sides, progress, -0.5);

        RenderSystem.setShaderColor(c.getRed() / 255.0f, c.getBlue() / 255.0f, c.getGreen() / 255.0f, 1.0f);
        RenderingUtils.drawSmoothCircle(guiGraphics, circleX + radius, circleY + radius, radius - thickness, sides, 1, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);
        // FIXME :: add icon to registry?
//        guiGraphics.blit(textures.get(createTextureKey(handler.getType(), "growth", "_" + (handler.getLevel().ordinal() + 1))), circleX + 6, circleY + 6, 150, 0, 0, 20, 20, 20, 20);
    }

    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        if (buttonClicked) {
            buttonClicked = false;
            return true;
        } else {
            return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
        }
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_231046_1_, p_231046_2_);

        if (Keybind.DRAGON_INVENTORY.get().isActiveAndMatches(mouseKey)) {
            onClose();
            return true;
        }

        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    @Override
    public void render(@NotNull final GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        clawMenuButtons.forEach(
                button -> button.visible = clawsMenu
        );

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 100);
        guiGraphics.pose().popPose();

        renderTooltip(guiGraphics, mouseX, mouseY);

        if (isGrowthIconHovered) {
            DragonStateHandler handler = DragonStateProvider.getData(player);
            String age = (int) handler.getSize() - DragonLevel.min(handler.getLevel()) + "/";
            double seconds = 0;

            // FIXME level
//            if (handler.getLevel() == DragonLevel.NEWBORN) {
//                age += DragonLevel.YOUNG.size - handler.getLevel().size;
//                double missing = DragonLevel.YOUNG.size - handler.getSize();
//                double increment = (DragonLevel.YOUNG.size - DragonLevel.NEWBORN.size) / (DragonGrowthHandler.NEWBORN_TO_YOUNG * 20.0) * ServerConfig.newbornGrowthModifier;
//                seconds = missing / increment / 20;
//            } else if (handler.getLevel() == DragonLevel.YOUNG) {
//                age += DragonLevel.ADULT.size - handler.getLevel().size;
//
//                double missing = DragonLevel.ADULT.size - handler.getSize();
//                double increment = (DragonLevel.ADULT.size - DragonLevel.YOUNG.size) / (DragonGrowthHandler.YOUNG_TO_ADULT * 20.0) * ServerConfig.youngGrowthModifier;
//                seconds = missing / increment / 20;
//            } else if (handler.getLevel() == DragonLevel.ADULT && handler.getSize() < 40) {
//                age += 40 - handler.getLevel().size;
//
//                double missing = 40 - handler.getSize();
//                double increment = (40 - DragonLevel.ADULT.size) / (DragonGrowthHandler.ADULT_TO_ANCIENT * 20.0) * ServerConfig.adultGrowthModifier;
//                seconds = missing / increment / 20;
//            } else if (handler.getLevel() == DragonLevel.ADULT) {
//                age += (int) (ServerConfig.maxGrowthSize - handler.getLevel().size);
//
//                double missing = ServerConfig.maxGrowthSize - handler.getSize();
//                double increment = (ServerConfig.maxGrowthSize - 40) / (DragonGrowthHandler.ANCIENT * 20.0) * ServerConfig.maxGrowthModifier;
//                seconds = missing / increment / 20;
//            }

            if (seconds != 0) {
                int minutes = (int) (seconds / 60);
                seconds -= minutes * 60;

                int hours = minutes / 60;
                minutes -= hours * 60;

                String hourString = hours > 0 ? hours >= 10 ? Integer.toString(hours) : "0" + hours : "00";
                String minuteString = minutes > 0 ? minutes >= 10 ? Integer.toString(minutes) : "0" + minutes : "00";

                if (handler.growing) {
                    age += " (" + hourString + ":" + minuteString + ")";
                } else {
                    age += " (§4--:--§r)";
                }
            }

            ArrayList<Item> allowedList = new ArrayList<>();

            // FIXME level :: add to registry
//            HashSet<Item> newbornList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growNewborn);
//            HashSet<Item> youngList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growYoung);
//            HashSet<Item> adultList = ConfigHandler.getResourceElements(Item.class, ServerConfig.growAdult);
//
//            if (handler.getSize() < DragonLevel.YOUNG.size) {
//                allowedList.addAll(newbornList);
//            } else if (handler.getSize() < DragonLevel.ADULT.size) {
//                allowedList.addAll(youngList);
//            } else {
//                allowedList.addAll(adultList);
//            }

            List<String> displayData = allowedList.stream().map(i -> new ItemStack(i).getDisplayName().getString()).toList();
            StringJoiner result = new StringJoiner(", ");
            displayData.forEach(result::add);

            List<Component> components = List.of(
//                    Component.translatable(GROWTH_STAGE).append(handler.getLevel().translatableName()),
                    Component.translatable(GROWTH_AGE, age),
                    Component.translatable(GROWTH_INFO, result.toString())
            );

            guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, components, mouseX, mouseY);
        }
    }
}