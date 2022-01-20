package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.jackraidenph.dragonsurvival.server.containers.SourceOfMagicContainer;
import by.jackraidenph.dragonsurvival.server.tileentity.SourceOfMagicTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL11;

public class SourceOfMagicScreen extends AbstractContainerScreen<SourceOfMagicContainer>
{
    static final ResourceLocation BACKGROUND = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/source_of_magic_ui.png");
    static final ResourceLocation CAVE_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/cave_source_of_magic_0.png");
    static final ResourceLocation CAVE_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/cave_source_of_magic_1.png");
    static final ResourceLocation FOREST_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/forest_source_of_magic_0.png");
    static final ResourceLocation FOREST_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/forest_source_of_magic_1.png");
    static final ResourceLocation SEA_NEST0 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/sea_source_of_magic_0.png");
    static final ResourceLocation SEA_NEST1 = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/source_of_magic/sea_source_of_magic_1.png");
    private final SourceOfMagicTileEntity nestEntity;

    private final Player playerEntity;

    public SourceOfMagicScreen(SourceOfMagicContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        nestEntity = screenContainer.nestEntity;
        playerEntity = inv.player;
    }

    public void render(PoseStack PoseStack , int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(PoseStack );
        super.render(PoseStack , p_render_1_, p_render_2_, p_render_3_);
        this.renderTooltip(PoseStack , p_render_1_, p_render_2_);
    }
    
    @Override
    protected void init()
    {
        super.init();
        addRenderableWidget(new HelpButton(leftPos + 12, topPos + 12, 12, 12, "ds.help.source_of_magic"));
    }
    
    protected void renderLabels(PoseStack  PoseStack , int mouseX, int mouseY) {};
    
    @Override
    protected void renderBg(PoseStack  PoseStack , float partialTicks, int mouseX, int mouseY) { // WIP
        renderBackground(PoseStack );
        TextureManager textureManager = minecraft.getTextureManager();
        textureManager.bindForSetup(BACKGROUND);
        blit(PoseStack , leftPos, topPos, 0, 0, imageWidth, imageHeight);
        
        boolean hasItem = !nestEntity.getItem(0).isEmpty();
        
        switch (nestEntity.type) {
            case CAVE:
                textureManager.bindForSetup(hasItem ? CAVE_NEST1 : CAVE_NEST0);
                break;
            case FOREST:
                textureManager.bindForSetup(hasItem ? FOREST_NEST1 : FOREST_NEST0);
                break;
            case SEA:
                textureManager.bindForSetup(hasItem ? SEA_NEST1 : SEA_NEST0);
                break;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        blit(PoseStack , leftPos + 8, topPos + 8, 0, 0, 160, 49, 160, 49);
    }
}
