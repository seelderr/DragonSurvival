package by.jackraidenph.dragonsurvival.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.config.ConfigUtils;
import by.jackraidenph.dragonsurvival.containers.DragonContainer;
import by.jackraidenph.dragonsurvival.gui.buttons.TabButton;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.KeyInputHandler;
import by.jackraidenph.dragonsurvival.handlers.DragonGrowthHandler;
import by.jackraidenph.dragonsurvival.handlers.Magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.SortInventoryPacket;
import by.jackraidenph.dragonsurvival.network.claw.DragonClawsMenuToggle;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawRender;
import by.jackraidenph.dragonsurvival.network.container.OpenInventory;
import by.jackraidenph.dragonsurvival.util.DragonLevel;
import by.jackraidenph.dragonsurvival.util.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class DragonScreen extends DisplayEffectsScreen<DragonContainer> {
    static final ResourceLocation BACKGROUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_inventory.png");
    public static final ResourceLocation INVENTORY_TOGGLE_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/inventory_button.png");
    public static final ResourceLocation SORTING_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/sorting_button.png");
    
    private static final ResourceLocation CLAWS_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_claws.png");
    private static final ResourceLocation DRAGON_CLAW_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_claws_button.png");
    private static final ResourceLocation DRAGON_CLAW_CHECKMARK = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_claws_tetris.png");
    
    private boolean buttonClicked;
    
    public boolean clawsMenu = false;
    
    private PlayerEntity player;
    public DragonScreen(DragonContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        passEvents = true;
        player = inv.player;
    
        DragonStateProvider.getCap(player).ifPresent((cap) -> {
            clawsMenu = cap.getClawInventory().isClawsMenuOpen();
        });
    }
    
    
    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(BACKGROUND);
        int i = leftPos;
        int j = topPos;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        this.blit(stack,leftPos, topPos, 0, 0, imageWidth, imageHeight);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    
        RenderSystem.pushMatrix();
    
        GL11.glScissor((int)((leftPos + 26) * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)((height * Minecraft.getInstance().getWindow().getGuiScale()) - (topPos + 79) * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)(76 * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)(70 * Minecraft.getInstance().getWindow().getGuiScale()));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    
        GL11.glTranslatef(0F, 0F, 100F);
    
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
        int sizeOffset = (int)(handler.getSize() - handler.getLevel().size) / 2;
        InventoryScreen.renderEntityInInventory(i + 60, j + 70, (int)(30 - sizeOffset), (float)(i + 51) - mouseX * 20, (float)(j + 75 - 50) - mouseY * 20, this.minecraft.player);
    
        GL11.glTranslatef(0F, 0F, -100F);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        
        
        if(clawsMenu){
            minecraft.getTextureManager().bind(CLAWS_TEXTURE);
            this.blit(stack,leftPos - 80, topPos, 0, 0, 77, 170);
        }
    

        if(clawsMenu) {
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
            
            GL11.glPushMatrix();
            
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            Color c = new Color(99, 99, 99);
            GL11.glColor4d(c.getRed()  / 255.0, c.getBlue() / 255.0, c.getGreen() / 255.0, 1.0);
            drawTexturedRing(circleX + radius, circleY + radius, radius - thickness, radius, 0, 0, 0, 128, sides, 1, 0);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            
            GL11.glColor4d(1F, 1F, 1F, 1.0);
            minecraft.getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/circle_" + handler.getType().name().toLowerCase() + ".png"));
            drawTexturedCircle(circleX + radius, circleY + radius, radius, 0.5, 0.5, 0.5, sides, progress, -0.5);
    
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glLineWidth(4.0F);
            if(handler.growing){
                GL11.glColor4f(0F, 0F, 0F, 1F);
            }else{
                GL11.glColor4f(76 / 255F, 0F, 0F, 1F);
            }
            drawSmoothCircle(circleX + radius, circleY + radius, radius, sides, 1, 0);
    
            GL11.glColor4d(c.getRed()  / 255.0, c.getBlue() / 255.0, c.getGreen() / 255.0, 1.0);
            drawSmoothCircle(circleX + radius, circleY + radius, radius - thickness, sides, 1, 0);
            GL11.glLineWidth(1.0F);
    
            c = c.brighter();
            GL11.glColor4d(c.getRed()  / 255.0, c.getBlue() / 255.0, c.getGreen() / 255.0, 1.0);
            drawTexturedRing(circleX + radius, circleY + radius, 0, radius - thickness, 0, 0,0,0, sides, 1, 0);
    
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4d(1F, 1F, 1F, 1.0);
    
            minecraft.getTextureManager().bind(new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/growth/growth_" + handler.getType().name().toLowerCase() + "_" + (handler.getLevel().ordinal() + 1) + ".png"));
            this.blit(stack, circleX + 6, circleY + 6, 0, 0, 20, 20, 20, 20);
    
            GL11.glPopMatrix();
        }
        
        GlStateManager._popMatrix();
    }
    
    public static void drawTexturedCircle(double x, double y, double radius, double u, double v, double texRadius, int sides, double percent, double startAngle) {
        double rad;
        double sin;
        double cos;
        
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glTexCoord2d(u, v);
        GL11.glVertex2d(x, y);
        
        for (int i = 0; i <= percent * sides; i++) {
            rad = PI_TWO * ((double) i / (double) sides + startAngle);
            sin = Math.sin(rad);
            cos = Math.cos(rad);
            
            GL11.glTexCoord2d(u + sin * texRadius, v + cos * texRadius);
            GL11.glVertex2d(x + sin * radius, y + cos * radius);
        }
        
        if(percent == 1.0){
            rad = PI_TWO * (percent + startAngle);
            sin = Math.sin(rad);
            cos = Math.cos(rad);
    
            GL11.glTexCoord2d(u + sin * texRadius, v + cos * texRadius);
            GL11.glVertex2d(x + sin * radius, y + cos * radius);
        }
        
        GL11.glEnd();
    }
    
    static final double PI_TWO = (Math.PI * 2.0);
    
    public static void drawSmoothCircle(double x, double y, double radius, int sides, double percent, double startAngle) {
        boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
        boolean lineSmooth = GL11.glGetBoolean(GL11.GL_LINE_SMOOTH);
        
        double rad;
        double sin;
        double cos;
        
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        
        for (int i = 0; i <= percent * sides; i++) {
            rad = PI_TWO * ((double) i / (double) sides + startAngle);
            sin = Math.sin(rad);
            cos = -Math.cos(rad);
            
            GL11.glVertex2d(x + sin * radius, y + cos * radius);
        }
        
        rad = PI_TWO * (percent + startAngle);
        sin = Math.sin(rad);
        cos = -Math.cos(rad);
        
        GL11.glVertex2d(x + sin * radius, y + cos * radius);
        
        GL11.glEnd();
        if (!lineSmooth) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }
        if (!blend) {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();
    }
    
    
    public static void drawTexturedRing(double x, double y, double innerRadius, double outerRadius, double u, double v, double texInnerRadius, double texOuterRadius, int sides, double percent, double startAngle) {
        double rad;
        double sin;
        double cos;
    
        boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
        boolean lineSmooth = GL11.glGetBoolean(GL11.GL_LINE_SMOOTH);
        
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glBegin(GL11.GL_QUAD_STRIP);
        
        for (int i = 0; i <= percent * sides; i++) {
            rad = PI_TWO * ((double) i / (double) sides + startAngle);
            sin = Math.sin(rad);
            cos = -Math.cos(rad);
            
            GL11.glTexCoord2d(u + sin * texOuterRadius, v + cos * texOuterRadius);
            GL11.glVertex2d(x + sin * outerRadius, y + cos * outerRadius);
            
            GL11.glTexCoord2d(u + sin * texInnerRadius, v + cos * texInnerRadius);
            GL11.glVertex2d(x + sin * innerRadius, y + cos * innerRadius);
        }
        
        rad = PI_TWO * (percent + startAngle);
        sin = Math.sin(rad);
        cos = -Math.cos(rad);
        
        GL11.glTexCoord2d(u + sin * texOuterRadius, v + cos * texOuterRadius);
        GL11.glVertex2d(x + sin * outerRadius, y + cos * outerRadius);
        
        GL11.glTexCoord2d(u + sin * texInnerRadius, v + cos * texInnerRadius);
        GL11.glVertex2d(x + sin * innerRadius, y + cos * innerRadius);
        
        GL11.glEnd();
        
        if (!lineSmooth) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }
        if (!blend) {
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();
    }
    
    
    @Override
    protected void renderLabels(MatrixStack stack, int p_230451_2_, int p_230451_3_)
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
    
        this.leftPos = (this.width - this.imageWidth) / 2;
        
        DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
    
        addButton(new TabButton(leftPos, topPos - 28, 0, this));
        addButton(new TabButton(leftPos + 28, topPos - 26, 1, this));
        addButton(new TabButton(leftPos + 57, topPos - 26, 2, this));
        addButton(new TabButton(leftPos + 86, topPos - 26, 3, this));
        
        addButton(new Button(leftPos + 27, topPos + 10, 11, 11, new StringTextComponent(""), p_onPress_1_ -> {
            clawsMenu = !clawsMenu;
            buttons.clear();
            init();
            
            NetworkHandler.CHANNEL.sendToServer(new DragonClawsMenuToggle(clawsMenu));
            DragonStateProvider.getCap(player).ifPresent((cap) -> cap.getClawInventory().setClawsMenuOpen(clawsMenu));
            
        }){
            @Override
            public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
            {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_DEPTH_TEST);
    
                minecraft.getTextureManager().bind(DRAGON_CLAW_BUTTON);
                this.blit(stack,x, y, 0, 0, 11, 11, 11, 11);
                
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
            }
    
            @Override
            public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
            {
                ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.claws")));
                Minecraft.getInstance().screen.renderComponentTooltip(p_230443_1_, description, p_230443_2_, p_230443_3_);
            }
        });
    
        addButton(new Button(leftPos - 58, topPos - 40, 32, 32, null, (button) -> {}){
            @Override
            public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
            {
                this.visible = clawsMenu;
                this.active = clawsMenu;
            }
        
            @Override
            public void renderToolTip(MatrixStack stack, int mouseX, int mouseY)
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
                
                ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.growth_stage", handler.getLevel().getName()),
                                                                                      new TranslationTextComponent("ds.gui.growth_age", age),
                                                                                      new TranslationTextComponent("ds.gui.growth_help", result)));
                Minecraft.getInstance().screen.renderComponentTooltip(stack, description, mouseX, mouseY);
            }
    
            @Override
            public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_)
            {
                return false;
            }
        });
        
        addButton(new Button(leftPos - 80 + 33, topPos + 111, 11, 11, null, (button) -> {}){
            @Override
            public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_)
            {
                this.visible = clawsMenu;
                this.active = clawsMenu;
                
                if(isHovered()){
                    minecraft.getTextureManager().bind(ClientMagicHUDHandler.widgetTextures);
                    DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
                    if(handler != null) {
                        int xP = handler.getType() == DragonType.SEA ? 0 : handler.getType() == DragonType.FOREST ? 18 : 36;
                        GL11.glPushMatrix();
                        blit(stack, x + 1, y + 1, xP / 2, 204 / 2, 9, 9, 128, 128);
                        GL11.glPopMatrix();
                    }
                }
            }
        
            @Override
            public void renderToolTip(MatrixStack stack, int mouseX, int mouseY)
            {
                ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.skill.help.claws")));
                Minecraft.getInstance().screen.renderComponentTooltip(stack, description, mouseX, mouseY);
            }
    
            @Override
            public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_)
            {
                return false;
            }
        });
    

        
        addButton(new Button(leftPos - 80 + 34, topPos + 140, 9, 9, null, p_onPress_1_ -> {
            if(handler != null){
               boolean claws = !handler.getClawInventory().renderClaws;
               
               handler.getClawInventory().renderClaws = claws;
               NetworkHandler.CHANNEL.sendToServer(new SyncDragonClawRender(player.getId(), claws));
            }
        }){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.active = clawsMenu;
                DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
                
                if(handler != null && handler.getClawInventory().renderClaws && clawsMenu){
                    minecraft.getTextureManager().bind(DRAGON_CLAW_CHECKMARK);
                    this.blit(p_230430_1_,x, y, 0, 0, 9, 9, 9, 9);
                }
                this.isHovered = p_230430_2_ >= this.x && p_230430_3_ >= this.y && p_230430_2_ < this.x + this.width && p_230430_3_ < this.y + this.height;
    
                if(isHovered){
                    ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.claws.rendering")));
                    Minecraft.getInstance().screen.renderComponentTooltip(p_230430_1_, description, p_230430_2_, p_230430_3_);
                }
          }
        });
        
        if(ConfigHandler.CLIENT.inventoryToggle.get()) {
            addButton(new ImageButton(this.leftPos + (imageWidth - 28), (this.height / 2 - 30) + 50, 20, 18, 0, 0, 19, INVENTORY_TOGGLE_BUTTON, p_onPress_1_ -> {
                Minecraft.getInstance().setScreen(new InventoryScreen(this.player));
                NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
            }){
                @Override
                public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
                {
                    ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.toggle_inventory.vanilla")));
                    Minecraft.getInstance().screen.renderComponentTooltip(p_230443_1_, description, p_230443_2_, p_230443_3_);
                }
            });
        }
    
        addButton(new ImageButton(this.leftPos + (imageWidth - 28), (this.height / 2), 20, 18, 0, 0, 18, SORTING_BUTTON, p_onPress_1_ -> {
            NetworkHandler.CHANNEL.sendToServer(new SortInventoryPacket());
        }){
            @Override
            public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_)
            {
                ArrayList<ITextComponent> description = new ArrayList<>(Arrays.asList(new TranslationTextComponent("ds.gui.sort")));
                Minecraft.getInstance().screen.renderComponentTooltip(p_230443_1_, description, p_230443_2_, p_230443_3_);
            }
        });
    }
    
    public void render(MatrixStack p_230450_1_,int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(p_230450_1_);
        this.doRenderEffects = !clawsMenu;
        super.render(p_230450_1_, p_render_1_, p_render_2_, p_render_3_);
    
        this.renderTooltip(p_230450_1_, p_render_1_, p_render_2_);
        
        for(Widget w : buttons){
            if(w.isHovered()){
                w.renderToolTip(p_230450_1_, p_render_1_, p_render_2_);
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
        InputMappings.Input mouseKey = InputMappings.getKey(p_231046_1_, p_231046_2_);
    
        if (KeyInputHandler.DRAGON_INVENTORY.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }
        
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }
}
