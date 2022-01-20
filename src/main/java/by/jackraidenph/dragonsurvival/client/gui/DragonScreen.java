package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.settings.SettingsSideScreen;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.TabButton;
import by.jackraidenph.dragonsurvival.client.handlers.ClientEvents;
import by.jackraidenph.dragonsurvival.client.handlers.KeyInputHandler;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.handlers.DragonGrowthHandler;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.config.ConfigUtils;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.claw.DragonClawsMenuToggle;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawRender;
import by.jackraidenph.dragonsurvival.network.container.OpenInventory;
import by.jackraidenph.dragonsurvival.network.entity.player.SortInventoryPacket;
import by.jackraidenph.dragonsurvival.server.containers.DragonContainer;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL20;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class DragonScreen extends EffectRenderingInventoryScreen<DragonContainer> implements MenuAccess<DragonContainer>
{
    static final ResourceLocation BACKGROUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_inventory.png");
    public static final ResourceLocation INVENTORY_TOGGLE_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/inventory_button.png");
    public static final ResourceLocation SETTINGS_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/settings_button.png");
    
    private static final ResourceLocation CLAWS_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_claws.png");
    private static final ResourceLocation DRAGON_CLAW_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_claws_button.png");
    private static final ResourceLocation DRAGON_CLAW_CHECKMARK = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_claws_tetris.png");
    
    private boolean buttonClicked;
    
    public boolean clawsMenu = false;
    
    private Player player;
    public DragonScreen(DragonContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        passEvents = true;
        player = inv.player;
    
        DragonStateProvider.getCap(player).ifPresent((cap) -> {
            clawsMenu = cap.getClawInventory().isClawsMenuOpen();
        });
    }
    
    
    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int i = leftPos;
        int j = topPos;

        this.blit(stack,leftPos, topPos, 0, 0, imageWidth, imageHeight);
        RenderSystem.enableScissor((int)((leftPos + 26) * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)((height * Minecraft.getInstance().getWindow().getGuiScale()) - (topPos + 79) * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)(76 * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)(70 * Minecraft.getInstance().getWindow().getGuiScale()));
        stack.translate(0F, 0F, 100F);
    
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        int sizeOffset = (int)(handler.getSize() - handler.getLevel().size) / 2;
        InventoryScreen.renderEntityInInventory(i + 60, j + 70, (int)(30 - sizeOffset), (float)(i + 51) - mouseX * 20, (float)(j + 75 - 50) - mouseY * 20, this.minecraft.player);
    
        stack.translate(0F, 0F, -100F);
        RenderSystem.disableScissor();
        
        
        if(clawsMenu){
            RenderSystem.setShaderTexture(0,CLAWS_TEXTURE);
            this.blit(stack,leftPos - 80, topPos, 0, 0, 77, 170);
        }
    

        if(clawsMenu && false) { //TODO Fix this
            double curSize = handler.getSize();
            float progress = 0;
    
            if (handler.getLevel() == DragonLevel.BABY) {
                progress = (float)((curSize - DragonLevel.BABY.size) / (DragonLevel.YOUNG.size - DragonLevel.BABY.size));
            } else if (handler.getLevel() == DragonLevel.YOUNG) {
                progress = (float)((curSize - DragonLevel.YOUNG.size) / (DragonLevel.ADULT.size - DragonLevel.YOUNG.size));
    
            } else if (handler.getLevel() == DragonLevel.ADULT && handler.getSize() < 40) {
                progress = (float)((curSize - DragonLevel.ADULT.size) / (40 - DragonLevel.ADULT.size));
                
            } else if (handler.getLevel() == DragonLevel.ADULT && handler.getSize() >= 40) {
                progress = (float)((curSize - 40) / (ConfigHandler.SERVER.maxGrowthSize.get() - 40));
            }
    
            int size = 34;
            int thickness = 5;
            int circleX = leftPos - 58;
            int circleY = topPos - 40;
            int sides = 6;
            
            int radius = size / 2;
            
            
            RenderSystem.disableTexture();
            Color c = new Color(99, 99, 99);
            RenderSystem.setShaderColor(c.getRed()  / 255.0f, c.getBlue() / 255.0f, c.getGreen() / 255.0f, 1.0f);
            drawTexturedRing(circleX + radius, circleY + radius, radius - thickness, radius, 0, 0, 0, 128, sides, 1, 0);
            RenderSystem.enableTexture();
            
            
            RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);
            RenderSystem.setShaderTexture(0,new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_" + handler.getType().name().toLowerCase() + ".png"));
            drawTexturedCircle(circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, sides, progress, -0.5);
    
            RenderSystem.disableTexture();
            
            RenderSystem.lineWidth(4.0F);
            if(handler.growing){
                RenderSystem.setShaderColor(0F, 0F, 0F, 1F);
            }else{
                RenderSystem.setShaderColor(76 / 255F, 0F, 0F, 1F);
            }
            drawSmoothCircle(circleX + radius, circleY + radius, radius, sides, 1, 0);
    
            RenderSystem.setShaderColor(c.getRed()  / 255.0f, c.getBlue() / 255.0f, c.getGreen() / 255.0f, 1.0f);
            drawSmoothCircle(circleX + radius, circleY + radius, radius - thickness, sides, 1, 0);
            RenderSystem.lineWidth(1.0F);
    
            c = c.brighter();
            RenderSystem.setShaderColor(c.getRed()  / 255.0f, c.getBlue() / 255.0f, c.getGreen() / 255.0f, 1.0f);
            drawTexturedRing(circleX + radius, circleY + radius, 0, radius - thickness, 0, 0,0,0, sides, 1, 0);
    
            RenderSystem.enableTexture();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1.0f);
    
            RenderSystem.setShaderTexture(0,new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/growth_" + handler.getType().name().toLowerCase() + "_" + (handler.getLevel().ordinal() + 1) + ".png"));
            this.blit(stack, circleX + 6, circleY + 6, 0, 0, 20, 20, 20, 20);
    
        }
    }
    
    public static void drawTexturedCircle(double x, double y, double radius, double u, double v, double texRadius, int sides, double percent, double startAngle) {
        double rad;
        double sin;
        double cos;
        
        GL20.glBegin(GL20.GL_TRIANGLE_FAN);
        GL20.glTexCoord2d(u, v);
        GL20.glVertex2d(x, y);
        
        for (int i = 0; i <= percent * sides; i++) {
            rad = PI_TWO * ((double) i / (double) sides + startAngle);
            sin = Math.sin(rad);
            cos = Math.cos(rad);
            
            GL20.glTexCoord2d(u + sin * texRadius, v + cos * texRadius);
            GL20.glVertex2d(x + sin * radius, y + cos * radius);
        }
        
        if(percent == 1.0){
            rad = PI_TWO * (percent + startAngle);
            sin = Math.sin(rad);
            cos = Math.cos(rad);
    
            GL20.glTexCoord2d(u + sin * texRadius, v + cos * texRadius);
            GL20.glVertex2d(x + sin * radius, y + cos * radius);
        }
        
        GL20.glEnd();
    }
    
    static final double PI_TWO = (Math.PI * 2.0);
    
    public static void drawSmoothCircle(double x, double y, double radius, int sides, double percent, double startAngle) {
        boolean blend = GL20.glGetBoolean(GL20.GL_BLEND);
        boolean lineSmooth = GL20.glGetBoolean(GL20.GL_LINE_SMOOTH);
        
        double rad;
        double sin;
        double cos;
        
        
        RenderSystem.enableBlend();
        GL20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        GL20.glEnable(GL20.GL_LINE_SMOOTH);
        GL20.glHint(GL20.GL_LINE_SMOOTH_HINT, GL20.GL_NICEST);
        GL20.glBegin(GL20.GL_LINE_STRIP);
        
        for (int i = 0; i <= percent * sides; i++) {
            rad = PI_TWO * ((double) i / (double) sides + startAngle);
            sin = Math.sin(rad);
            cos = -Math.cos(rad);
            
            GL20.glVertex2d(x + sin * radius, y + cos * radius);
        }
        
        rad = PI_TWO * (percent + startAngle);
        sin = Math.sin(rad);
        cos = -Math.cos(rad);
        
        GL20.glVertex2d(x + sin * radius, y + cos * radius);
        
        GL20.glEnd();
        if (!lineSmooth) {
            GL20.glDisable(GL20.GL_LINE_SMOOTH);
        }
        if (!blend) {
            RenderSystem.disableBlend();
        }
        
    }
    
    
    public static void drawTexturedRing(double x, double y, double innerRadius, double outerRadius, double u, double v, double texInnerRadius, double texOuterRadius, int sides, double percent, double startAngle) {
        double rad;
        double sin;
        double cos;
        
        boolean blend = GL20.glGetBoolean(GL20.GL_BLEND);
        boolean lineSmooth = GL20.glGetBoolean(GL20.GL_LINE_SMOOTH);
        
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        GL20.glEnable(GL20.GL_LINE_SMOOTH);
        GL20.glHint(GL20.GL_LINE_SMOOTH_HINT, GL20.GL_NICEST);
        GL20.glBegin(GL20.GL_QUAD_STRIP);
        
        for (int i = 0; i <= percent * sides; i++) {
            rad = PI_TWO * ((double) i / (double) sides + startAngle);
            sin = Math.sin(rad);
            cos = -Math.cos(rad);
    
            GL20.glTexCoord2d(u + sin * texOuterRadius, v + cos * texOuterRadius);
            GL20.glVertex2d(x + sin * outerRadius, y + cos * outerRadius);
    
            GL20.glTexCoord2d(u + sin * texInnerRadius, v + cos * texInnerRadius);
            GL20.glVertex2d(x + sin * innerRadius, y + cos * innerRadius);
        }
        
        rad = PI_TWO * (percent + startAngle);
        sin = Math.sin(rad);
        cos = -Math.cos(rad);
    
        GL20.glTexCoord2d(u + sin * texOuterRadius, v + cos * texOuterRadius);
        GL20.glVertex2d(x + sin * outerRadius, y + cos * outerRadius);
    
        GL20.glTexCoord2d(u + sin * texInnerRadius, v + cos * texInnerRadius);
        GL20.glVertex2d(x + sin * innerRadius, y + cos * innerRadius);
    
        GL20.glEnd();
        
        if (!lineSmooth) {
            GL20.glDisable(GL20.GL_LINE_SMOOTH);
        }
        if (!blend) {
            RenderSystem.disableBlend();
        }
        
    }
    
    
    @Override
    protected void renderLabels(PoseStack  stack, int p_230451_2_, int p_230451_3_)
    {
    }
    
    public int getLeftPos()
    {
        return leftPos;
    }
    
    @Override
    protected void init() {
        this.imageWidth = 203;
        this.imageHeight = 166;
        super.init();
    
        if(ClientEvents.mouseX != -1 && ClientEvents.mouseY != -1) {
            if(this.minecraft.getWindow() != null){
                InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, ClientEvents.mouseX, ClientEvents.mouseY);
                ClientEvents.mouseX = -1;
                ClientEvents.mouseY = -1;
            }
        }
    
        this.leftPos = (this.width - this.imageWidth) / 2;
        
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
    
        addRenderableWidget(new TabButton(leftPos, topPos - 28, 0, this));
        addRenderableWidget(new TabButton(leftPos + 28, topPos - 26, 1, this));
        addRenderableWidget(new TabButton(leftPos + 57, topPos - 26, 2, this));
        addRenderableWidget(new TabButton(leftPos + 86, topPos - 26, 3, this));
    
        addRenderableWidget(new Button(leftPos + 27, topPos + 10, 11, 11, new TextComponent(""), p_onPress_1_ -> {
            clawsMenu = !clawsMenu;
            children.clear();
            init();
            
            NetworkHandler.CHANNEL.sendToServer(new DragonClawsMenuToggle(clawsMenu));
            DragonStateProvider.getCap(player).ifPresent((cap) -> cap.getClawInventory().setClawsMenuOpen(clawsMenu));
            
        }){
            @Override
            public void renderButton(PoseStack  stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
            {
                RenderSystem.disableDepthTest();
                RenderSystem.setShaderTexture(0,DRAGON_CLAW_BUTTON);
                this.blit(stack,x, y, 0, 0, 11, 11, 11, 11);
                RenderSystem.enableDepthTest();
            }
    
            @Override
            public void renderToolTip(PoseStack  p_230443_1_, int p_230443_2_, int p_230443_3_)
            {
                ArrayList<Component> description = new ArrayList<>(Arrays.asList(new TranslatableComponent("ds.gui.claws")));
                Minecraft.getInstance().screen.renderComponentTooltip(p_230443_1_, description, p_230443_2_, p_230443_3_);
            }
        });
    
        addRenderableWidget(new HelpButton(leftPos - 58, topPos - 40, 32, 32, null){
            @Override
            public void renderButton(PoseStack  stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
            {
                this.visible = clawsMenu;
                this.active = clawsMenu;
            }
    
            @Override
            public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;
            }
    
            @Override
            public void renderToolTip(PoseStack  stack, int mouseX, int mouseY)
            {
                String age = (int)handler.getSize() - DragonLevel.BABY.size + "/";
                double seconds = 0;
                
                if(handler.getLevel() == DragonLevel.BABY){
                    age += DragonLevel.YOUNG.size - DragonLevel.BABY.size;
                    double missing = DragonLevel.YOUNG.size - handler.getSize();
                    double increment = ((DragonLevel.YOUNG.size - DragonLevel.BABY.size) / ((DragonGrowthHandler.newbornToYoung * 20.0))) * ConfigHandler.SERVER.newbornGrowthModifier.get();
                    seconds = (missing / increment) / 20;
                    
                }else if(handler.getLevel() == DragonLevel.YOUNG){
                    age += DragonLevel.ADULT.size - DragonLevel.BABY.size;
    
                    double missing = DragonLevel.ADULT.size - handler.getSize();
                    double increment = ((DragonLevel.ADULT.size - DragonLevel.YOUNG.size) / ((DragonGrowthHandler.youngToAdult * 20.0)))  * ConfigHandler.SERVER.youngGrowthModifier.get();
                    seconds = (missing / increment) / 20;
    
                }else if(handler.getLevel() == DragonLevel.ADULT && handler.getSize() < 40){
                    age += 40 - DragonLevel.BABY.size;
    
                    double missing = 40 - handler.getSize();
                    double increment = ((40 - DragonLevel.ADULT.size) / ((DragonGrowthHandler.adultToMax * 20.0)))  * ConfigHandler.SERVER.adultGrowthModifier.get();
                    seconds = (missing / increment) / 20;
    
                }else  if(handler.getLevel() == DragonLevel.ADULT && handler.getSize() >= 40){
                    age += (int)(ConfigHandler.SERVER.maxGrowthSize.get() - DragonLevel.BABY.size);
    
                    double missing = ConfigHandler.SERVER.maxGrowthSize.get() - handler.getSize();
                    double increment = ((ConfigHandler.SERVER.maxGrowthSize.get() - 40) / ((DragonGrowthHandler.beyond * 20.0)))  * ConfigHandler.SERVER.maxGrowthModifier.get();
                    seconds = (missing / increment) / 20;
               }
                
                if(seconds != 0){
                    int minutes = (int)(seconds / 60);
                    seconds -= (int)(minutes * 60);
    
                    int hours = (int)(minutes / 60);
                    minutes -= (hours * 60);
                    
                    String hourString = hours > 0 ? hours >= 10 ? Integer.toString(hours) : "0" + hours : "00";
                    String minuteString = minutes > 0 ? minutes >= 10 ? Integer.toString(minutes) : "0" + minutes : "00";
    
                    if(handler.growing) {
                        age += " (" + hourString + ":" + minuteString + ")";
                    }else{
                        age += " (§4--:--§r)";
                    }
                }
    
                ArrayList<Item> allowedList = new ArrayList<>();
    
                List<Item> newbornList = ConfigUtils.parseConfigItemList(ConfigHandler.SERVER.growNewborn.get());
                List<Item> youngList = ConfigUtils.parseConfigItemList(ConfigHandler.SERVER.growYoung.get());
                List<Item> adultList = ConfigUtils.parseConfigItemList(ConfigHandler.SERVER.growAdult.get());
                
                if(handler.getSize() < DragonLevel.YOUNG.size){
                    allowedList.addAll(newbornList);
                }else if(handler.getSize() < DragonLevel.ADULT.size){
                    allowedList.addAll(youngList);
                }else {
                    allowedList.addAll(adultList);
                }
    
                List<String> displayData = allowedList.stream()
                        .map(i -> new ItemStack(i).getDisplayName().getString())
                        .collect(Collectors.toList());
                StringJoiner result = new StringJoiner(", ");
                displayData.forEach(result::add);
                
                ArrayList<Component> description = new ArrayList<>(Arrays.asList(new TranslatableComponent("ds.gui.growth_stage", handler.getLevel().getName()),
                                                                                      new TranslatableComponent("ds.gui.growth_age", age),
                                                                                      new TranslatableComponent("ds.gui.growth_help", result)));
                Minecraft.getInstance().screen.renderComponentTooltip(stack, description, mouseX, mouseY);
            }
        });
    
        addRenderableWidget(new HelpButton(leftPos - 80 + 34, topPos + 112, 9, 9, "ds.skill.help.claws"){
            @Override
            public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.visible = clawsMenu;
                this.active = clawsMenu;
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            }
        });
    
    
    
        addRenderableWidget(new Button(leftPos - 80 + 34, topPos + 140, 9, 9, null, p_onPress_1_ -> {
            if(handler != null){
               boolean claws = !handler.getClawInventory().renderClaws;
               
               handler.getClawInventory().renderClaws = claws;
               NetworkHandler.CHANNEL.sendToServer(new SyncDragonClawRender(player.getId(), claws));
            }
        }){
            @Override
            public void render(PoseStack  p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.active = clawsMenu;
                DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
                
                if(handler != null && handler.getClawInventory().renderClaws && clawsMenu){
                    RenderSystem.setShaderTexture(0,DRAGON_CLAW_CHECKMARK);
                    this.blit(p_230430_1_,x, y, 0, 0, 9, 9, 9, 9);
                }
                this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;
    
                if(isHovered){
                    ArrayList<Component> description = new ArrayList<>(Arrays.asList(new TranslatableComponent("ds.gui.claws.rendering")));
                    Minecraft.getInstance().screen.renderComponentTooltip(p_230430_1_, description, p_230430_2_, p_230430_3_);
                }
          }
        });
        
        if(ConfigHandler.CLIENT.inventoryToggle.get()) {
            addRenderableWidget(new ImageButton(this.leftPos + (imageWidth - 28), (this.height / 2 - 30) + 50, 20, 18, 0, 0, 19, INVENTORY_TOGGLE_BUTTON, p_onPress_1_ -> {
                Minecraft.getInstance().setScreen(new InventoryScreen(this.player));
                NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
            }){
                @Override
                public void renderToolTip(PoseStack  p_230443_1_, int p_230443_2_, int p_230443_3_)
                {
                    ArrayList<Component> description = new ArrayList<>(Arrays.asList(new TranslatableComponent("ds.gui.toggle_inventory.vanilla")));
                    Minecraft.getInstance().screen.renderComponentTooltip(p_230443_1_, description, p_230443_2_, p_230443_3_);
                }
            });
        }
    
        addRenderableWidget(new ImageButton(this.leftPos + (imageWidth - 28), (this.height / 2), 20, 18, 40, 0, 20, INVENTORY_TOGGLE_BUTTON, p_onPress_1_ -> {
            NetworkHandler.CHANNEL.sendToServer(new SortInventoryPacket());
        }){
            @Override
            public void renderToolTip(PoseStack  p_230443_1_, int p_230443_2_, int p_230443_3_)
            {
                ArrayList<Component> description = new ArrayList<>(Arrays.asList(new TranslatableComponent("ds.gui.sort")));
                Minecraft.getInstance().screen.renderComponentTooltip(p_230443_1_, description, p_230443_2_, p_230443_3_);
            }
        });
    
        addRenderableWidget(new ImageButton(this.leftPos + (imageWidth - 27), (this.height / 2) + 40, 18, 18, 0, 0, 18, SETTINGS_BUTTON, p_onPress_1_ -> {
            Minecraft.getInstance().setScreen(new SettingsSideScreen(this, Minecraft.getInstance().options, new TranslatableComponent("ds.gui.tab_button.4")));
        }){
            @Override
            public void renderToolTip(PoseStack  p_230443_1_, int p_230443_2_, int p_230443_3_)
            {
                ArrayList<Component> description = new ArrayList<>(Arrays.asList(new TranslatableComponent("ds.gui.tab_button.4")));
                Minecraft.getInstance().screen.renderComponentTooltip(p_230443_1_, description, p_230443_2_, p_230443_3_);
            }
        });
        
    
    }
    
    public void render(PoseStack  p_230450_1_,int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(p_230450_1_);
        super.render(p_230450_1_, p_render_1_, p_render_2_, p_render_3_);
    
        this.renderTooltip(p_230450_1_, p_render_1_, p_render_2_);
        
        for(GuiEventListener w : children){
            if(w instanceof AbstractButton && ((AbstractButton)w).isHoveredOrFocused()){
                ((AbstractButton)w).renderToolTip(p_230450_1_, p_render_1_, p_render_2_);
            }
        }
    }
    
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        if (this.buttonClicked) {
            this.buttonClicked = false;
            return true;
        } else {
            return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
        }
    }
    
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_231046_1_, p_231046_2_);
    
        if (KeyInputHandler.DRAGON_INVENTORY.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }
        
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }
}
