package by.jackraidenph.dragonsurvival.gui.magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.containers.DragonContainer;
import by.jackraidenph.dragonsurvival.gui.magic.buttons.TabButton;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.KeyInputHandler;
import by.jackraidenph.dragonsurvival.handlers.Magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.OpenInventory;
import by.jackraidenph.dragonsurvival.network.claw.DragonClawsMenuToggle;
import by.jackraidenph.dragonsurvival.network.claw.SyncDragonClawRender;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;

public class DragonScreen extends DisplayEffectsScreen<DragonContainer> {
    static final ResourceLocation BACKGROUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_inventory.png");
    public static final ResourceLocation INVENTORY_TOGGLE_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/inventory_button.png");
    
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
        
        InventoryScreen.renderEntityInInventory(i + 60, j + 70, 30, (float)(i + 51) - mouseX * 20, (float)(j + 75 - 50) - mouseY * 20, this.minecraft.player);
    
        GL11.glTranslatef(0F, 0F, -100F);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        
        
        if(clawsMenu){
            minecraft.getTextureManager().bind(CLAWS_TEXTURE);
            this.blit(stack,leftPos - 80, topPos, 0, 0, 77, 170);
        }
        
        GlStateManager._popMatrix();
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
        
        addButton(new TabButton(leftPos, topPos - 28, 0, this));
        addButton(new TabButton(leftPos + 28, topPos - 26, 1, this));
        addButton(new TabButton(leftPos + 57, topPos - 26, 2, this));
        addButton(new TabButton(leftPos + 86, topPos - 26, 3, this));
    
        addButton(new Button(leftPos + 27, topPos + 10, 11, 11, new StringTextComponent(""), p_onPress_1_ -> {
            clawsMenu = !clawsMenu;
            this.leftPos += (clawsMenu ? 80 : -80);
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
        });
    

        
        addButton(new Button(leftPos - 80 + 34, topPos + 140, 9, 9, null, p_onPress_1_ -> {
            DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
    
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
          }
        });
        
        if(ConfigHandler.CLIENT.inventoryToggle.get()) {
            addButton(new ImageButton(this.leftPos + (imageWidth - 28), (this.height / 2 - 30) + 50, 20, 18, 0, 0, 19, INVENTORY_TOGGLE_BUTTON, p_onPress_1_ -> {
                Minecraft.getInstance().setScreen(new InventoryScreen(this.player));
                NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
            }));
        }
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
