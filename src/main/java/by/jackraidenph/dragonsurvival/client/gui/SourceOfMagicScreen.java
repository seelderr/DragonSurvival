package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.server.containers.SourceOfMagicContainer;
import by.jackraidenph.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public class SourceOfMagicScreen extends ContainerScreen<SourceOfMagicContainer> {
    static final ResourceLocation BACKGROUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/source_of_magic_ui.png");
    static final ResourceLocation CAVE_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/cave_source_of_magic_0.png");
    static final ResourceLocation CAVE_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/cave_source_of_magic_1.png");
    static final ResourceLocation FOREST_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/forest_source_of_magic_0.png");
    static final ResourceLocation FOREST_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/forest_source_of_magic_1.png");
    static final ResourceLocation SEA_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/sea_source_of_magic_0.png");
    static final ResourceLocation SEA_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/sea_source_of_magic_1.png");
    private final SourceOfMagicTileEntity nestEntity;

    private final PlayerEntity playerEntity;

    public SourceOfMagicScreen(SourceOfMagicContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        nestEntity = screenContainer.nestEntity;
        playerEntity = inv.player;
    }

    public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
        this.renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }

    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {};
    
    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) { // WIP
        renderBackground(matrixStack);
        TextureManager textureManager = minecraft.getTextureManager();
        textureManager.bind(BACKGROUND);
        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        boolean hasItem = !nestEntity.regenItem.getStackInSlot(0).isEmpty();
        
        switch (nestEntity.type) {
            case CAVE:
                textureManager.bind(hasItem ? CAVE_NEST1 : CAVE_NEST0);
                break;
            case FOREST:
                textureManager.bind(hasItem ? FOREST_NEST1 : FOREST_NEST0);
                break;
            case SEA:
                textureManager.bind(hasItem ? SEA_NEST1 : SEA_NEST0);
                break;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F,1f);
        blit(matrixStack, leftPos + 8, topPos + 8, 0, 0, 160, 49, 160, 49);
    }
}
