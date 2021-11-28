package by.jackraidenph.dragonsurvival.gui.magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.containers.DragonContainer;
import by.jackraidenph.dragonsurvival.gui.magic.buttons.TabButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public class DragonScreen extends DisplayEffectsScreen<DragonContainer> implements IRecipeShownListener {
    private final RecipeBookGui recipeBookGui = new RecipeBookGui();
    static final ResourceLocation BACKGROUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_inventory.png");
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    
    private boolean widthTooNarrow;
    private boolean buttonClicked;
    private PlayerEntity player;
    public DragonScreen(DragonContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        passEvents = true;
        player = inv.player;
    }

    @Override
    public void tick() {
        recipeBookGui.tick();
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
    
        GL11.glTranslatef(0F, 0F, 10F);
        
        InventoryScreen.renderEntityInInventory(i + 60, j + 70, 30, (float)(i + 51) - mouseX * 20, (float)(j + 75 - 50) - mouseY * 20, this.minecraft.player);
       
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager._popMatrix();
    }
    
    
    @Override
    public void recipesUpdated() {
        recipeBookGui.recipesUpdated();
    }
    
    @Override
    public RecipeBookGui getRecipeBookComponent()
    {
        return recipeBookGui;
    }
    
    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!widthTooNarrow || !recipeBookGui.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_)
    {
    }
    
    @Override
    protected void init() {
        this.imageWidth = 203;
        this.imageHeight = 166;
        super.init();
        widthTooNarrow = width < 379;
        this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width + 30, this.imageWidth);

        this.addButton(new ImageButton(this.leftPos + (imageWidth - 30), (this.height / 2 - 49) + 30, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (p_214076_1_) -> {
            this.recipeBookGui.initVisuals(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            this.leftPos = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width + 30, this.imageWidth);
            buttons.clear();
            init();
            ((ImageButton)p_214076_1_).setPosition(this.leftPos + (imageWidth - 30), (this.height / 2 - 49) + 30);
        }));
        this.children.add(this.recipeBookGui);
        
        addButton(new TabButton(leftPos, topPos - 28, 0, this));
        addButton(new TabButton(leftPos + 28, topPos - 26, 1, this));
        addButton(new TabButton(leftPos + 57, topPos - 26, 2, this));
        addButton(new TabButton(leftPos + 86, topPos - 26, 3, this));
    }
    
    public void render(MatrixStack p_230450_1_,int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(p_230450_1_);
        this.doRenderEffects = !this.recipeBookGui.isVisible();
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.renderBg(p_230450_1_, p_render_3_, p_render_1_, p_render_2_);
            this.recipeBookGui.render(p_230450_1_, p_render_1_, p_render_2_, p_render_3_);
        } else {
            this.recipeBookGui.render(p_230450_1_, p_render_1_, p_render_2_, p_render_3_);
            super.render(p_230450_1_, p_render_1_, p_render_2_, p_render_3_);
            this.recipeBookGui.renderGhostRecipe(p_230450_1_, this.getGuiLeft(), this.getGuiTop(), false, p_render_3_);
        }

        this.renderTooltip(p_230450_1_, p_render_1_, p_render_2_);
        this.recipeBookGui.renderTooltip(p_230450_1_, this.getGuiLeft(), this.getGuiTop(), p_render_1_, p_render_2_);
    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
        } else {
            return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
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

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        boolean flag = mouseX < (double) guiLeftIn || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.getXSize()) || mouseY >= (double) (guiTopIn + this.getYSize());
        return this.recipeBookGui.hasClickedOutside(mouseX, mouseY, this.getGuiLeft(), this.getGuiTop(), this.getXSize(), this.getYSize(), mouseButton) && flag;
    }
    
    
    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    protected void slotClicked(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }
}
