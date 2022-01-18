package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.jackraidenph.dragonsurvival.client.util.TextRenderUtil;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class DragonAltarGUI extends Screen {
    public static final ResourceLocation CONFIRM_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/confirm_button.png");
    public static final ResourceLocation CANCEL_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/cancel_button.png");
    
    private int guiLeft;
    private int guiTop;
    private boolean hasInit = false;
    
    
    private String[] animations = {"sit", "idle", "fly", "swim_fast", "run"};
    private int animation1 = 1;
    private int animation2 = 0;
    
    public DragonStateHandler handler1 = new DragonStateHandler();
    public DragonStateHandler handler2 = new DragonStateHandler();
    
    public DragonAltarGUI() {
        super(new TranslationTextComponent("ds.gui.dragon_altar"));
    }
    
    
    public static void renderBorders(ResourceLocation texture, int x0, int x1, int y0, int y1, int width, int height){
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        Minecraft.getInstance().getTextureManager().bind(texture);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.vertex((double)x0, (double)y0, -100.0D).uv(0.0F, (float)y0 / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)(x0 + width), (double)y0, -100.0D).uv((float)width / 32.0F, (float)y0 / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)(x0 + width), 0.0D, -100.0D).uv((float)width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)x0, (double)height, -100.0D).uv(0.0F, (float)height / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)(x0 + width), (double)height, -100.0D).uv((float)width / 32.0F, (float)height / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)(x0 + width), (double)y1, -100.0D).uv((float)width / 32.0F, (float)y1 / 32.0F).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)x0, (double)y1, -100.0D).uv(0.0F, (float)y1 / 32.0F).color(64, 64, 64, 255).endVertex();
        tessellator.end();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.vertex((double)x0, (double)(y0 + 4), 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex((double)x1, (double)(y0 + 4), 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex((double)x1, (double)y0, 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex((double)x0, (double)y0, 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex((double)x0, (double)y1, 0.0D).uv(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex((double)x1, (double)y1, 0.0D).uv(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex((double)x1, (double)(y1 - 4), 0.0D).uv(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex((double)x0, (double)(y1 - 4), 0.0D).uv(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
        tessellator.end();
    
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }
    
    private static ResourceLocation backgroundTexture = new ResourceLocation("textures/block/dirt.png");
    
    @Override
    public void renderBackground(MatrixStack pMatrixStack)
    {
        super.renderBackground(pMatrixStack);
        renderBorders(backgroundTexture, 0, width, 32, height - 32, width, height);
    }
    private int tick;
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.minecraft == null)
            return;
        this.renderBackground(matrixStack);
        
        tick++;
        
        if((tick % 200 * 20) == 0){
            animation1++;
            animation2++;
            
            if(animation1 >= animations.length){
                animation1 = 0;
            }
    
            if(animation2 >= animations.length){
                animation2 = 0;
            }
        }
        
        for(Widget btn : buttons){
            if(btn instanceof AltarTypeButton){
                AltarTypeButton button = (AltarTypeButton)btn;
                
                if(button.isHovered()) {
                    handler1.setType(button.type);
                    handler1.setHasWings(true);
                    handler1.setSize(DragonLevel.ADULT.size);
                    
                    handler2.setType(button.type);
                    handler2.setSize(button.type == DragonType.NONE ? DragonLevel.ADULT.size : DragonLevel.BABY.size);
    
                    FakeClientPlayerUtils.getFakePlayer(0, handler1).animationSupplier = () -> animations[animation1];
                    FakeClientPlayerUtils.getFakePlayer(1, handler2).animationSupplier = () -> animations[animation2];
    
                    renderDragon(width / 2 + 170, button.y + (button.getHeight() / 2) + 32, 5, matrixStack, DragonLevel.ADULT.size, FakeClientPlayerUtils.getFakePlayer(0, handler1), FakeClientPlayerUtils.getFakeDragon(0, handler1));
                    renderDragon(width / 2 - 170, button.y + (button.getHeight() / 2) + 32, -5, matrixStack, button.type == DragonType.NONE ? DragonLevel.ADULT.size : DragonLevel.BABY.size, FakeClientPlayerUtils.getFakePlayer(1, handler2), FakeClientPlayerUtils.getFakeDragon(1, handler2));
                }
            }
        }
    
        TextRenderUtil.drawCenteredScaledText(matrixStack, width / 2, 10, 2f, title.getString(), DyeColor.WHITE.getTextColor());
    
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    private void renderDragon(int x, int y, int xrot, MatrixStack matrixStack, float size, PlayerEntity player, DragonEntity dragon)
    {
        matrixStack.pushPose();
        float scale = size * 1.5f;
        matrixStack.scale(scale, scale, scale);
        matrixStack.translate(0, 0, 400);
        ClientDragonRender.dragonModel.setCurrentTexture(null);
        ClientDragonRender.renderEntityInInventory(DragonStateProvider.isDragon(player) ? dragon : player, x, y, scale, xrot, -3);
        matrixStack.popPose();
    }
    
    
    @Override
    protected void init() {
        super.init();

        if(!hasInit){
            hasInit = true;
        }
        
        this.guiLeft = (this.width - 304) / 2;
        this.guiTop = (this.height - 190) / 2;
    
        this.addButton(new HelpButton(this.guiLeft, 10, 18, 18, "ds.help.altar"){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                p_230430_1_.pushPose();
                p_230430_1_.translate(0, 0, 200);
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
                p_230430_1_.popPose();
            }
        });
        addButton(new AltarTypeButton(this, DragonType.CAVE, width / 2 - 104, this.guiTop + 20));
        addButton(new AltarTypeButton(this, DragonType.FOREST, width / 2 - 51, this.guiTop + 20));
        addButton(new AltarTypeButton(this, DragonType.SEA, width / 2 + 2, this.guiTop + 20));
        addButton(new AltarTypeButton(this, DragonType.NONE, width / 2 + 55, guiTop + 20));
        //ds.gui.customization
        addButton(new ExtendedButton(width / 2 - 50, height - 25, 100, 20, new TranslationTextComponent("ds.gui.customization"), (btn) -> {
            Minecraft.getInstance().setScreen(new DragonCustomizationScreen(Minecraft.getInstance().screen));
        }){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.visible = DragonStateProvider.isDragon(minecraft.player);
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            }
        });
    }
}
