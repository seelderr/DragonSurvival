package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.client.util.TextRenderUtil;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class DragonAltarGUI extends Screen {
    public static final ResourceLocation CONFIRM_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/confirm_button.png");
    public static final ResourceLocation CANCEL_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/cancel_button.png");
    
    private int guiLeft;
    private int guiTop;
    private boolean hasInit = false;
    
    private DragonEntity dragon1;
    private DragonEntity dragon2;
    
    private static RemoteClientPlayerEntity clientPlayer1;
    private static RemoteClientPlayerEntity clientPlayer2;
    
    
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
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.minecraft == null)
            return;
        this.renderBackground(matrixStack);
        
        for(Widget btn : buttons){
            if(btn instanceof AltarTypeButton){
                AltarTypeButton button = (AltarTypeButton)btn;
                
                if(button.isHovered()) {
                    DragonStateProvider.getCap(clientPlayer1).ifPresent((cap) -> {
                        cap.setType(button.type);
                    });
    
                    DragonStateProvider.getCap(clientPlayer2).ifPresent((cap) -> {
                        cap.setType(button.type);
                        cap.setSize(button.type == DragonType.NONE ? DragonLevel.ADULT.size : DragonLevel.BABY.size);
                    });
                    
                    renderDragon(width / 2 + 170, button.y + (button.getHeight() / 2) + 32, 5, matrixStack, DragonLevel.ADULT.size, clientPlayer1, dragon1);
                    renderDragon(width / 2 - 170, button.y + (button.getHeight() / 2) + 32, -5, matrixStack, button.type == DragonType.NONE ? DragonLevel.ADULT.size : DragonLevel.BABY.size, clientPlayer2, dragon2);
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
        if(ClientDragonRender.dragonArmor != null && ClientDragonRender.dragonModel != null) {
            renderEntityInInventory(x, y, scale, xrot, -3, DragonStateProvider.isDragon(player) ? dragon : player);
        }else{
            if (ClientDragonRender.dragonArmor == null) {
                ClientDragonRender.dragonArmor = DSEntities.DRAGON_ARMOR.create(Minecraft.getInstance().player.level);
                assert ClientDragonRender.dragonArmor != null;
                ClientDragonRender. dragonArmor.player = Minecraft.getInstance().player.getId();
            }

            if (!ClientDragonRender.playerDragonHashMap.containsKey(Minecraft.getInstance().player.getId())) {
                DragonEntity dummyDragon = DSEntities.DRAGON.create(Minecraft.getInstance().player.level);
                dummyDragon.player = Minecraft.getInstance().player.getId();
                ClientDragonRender.playerDragonHashMap.put(Minecraft.getInstance().player.getId(), new AtomicReference<>(dummyDragon));
            }
        }
        matrixStack.popPose();
    }
    
    
    @Override
    protected void init() {
        super.init();

        if(!hasInit){
            if(clientPlayer1 == null) {
                clientPlayer1 = new RemoteClientPlayerEntity(minecraft.level, new GameProfile(UUID.randomUUID(), "DRAGON_RENDER")){
                    @Override
                    public boolean shouldShowName()
                    {
                        return false;
                    }
    
                    @Override
                    public ITextComponent getDisplayName()
                    {
                        return StringTextComponent.EMPTY;
                    }
                    ResourceLocation skin = new ResourceLocation("textures/entity/steve.png");
                    @Override
                    public ResourceLocation getSkinTextureLocation()
                    {
                        return skin;
                    }
                };
            
                DragonStateProvider.getCap(clientPlayer1).ifPresent((cap) -> {
                    cap.setHasWings(true);
                    cap.setSize(DragonLevel.ADULT.size);
                });
            }
    
            if(clientPlayer2 == null) {
                clientPlayer2 = new RemoteClientPlayerEntity(minecraft.level, new GameProfile(UUID.randomUUID(), "DRAGON_RENDER")){
                    @Override
                    public boolean shouldShowName()
                    {
                        return false;
                    }
            
                    @Override
                    public ITextComponent getDisplayName()
                    {
                        return StringTextComponent.EMPTY;
                    }
    
                    ResourceLocation skin = new ResourceLocation("textures/entity/alex.png");
                    @Override
                    public ResourceLocation getSkinTextureLocation()
                    {
                        return skin;
                    }
                };
        
                DragonStateProvider.getCap(clientPlayer2).ifPresent((cap) -> {
                    cap.setHasWings(false);
                    cap.setSize(DragonLevel.BABY.size);
                });
            }
        
            dragon1 = new DragonEntity(DSEntities.DRAGON, minecraft.level){
                @Override
                public void registerControllers(AnimationData animationData) {
                    animationData.shouldPlayWhilePaused = true;
                    animationData.addAnimationController(new AnimationController<DragonEntity>(this, "controller", 2, (event) -> {
                        AnimationBuilder builder = new AnimationBuilder();
                        builder.addAnimation("sit", true);
                        event.getController().setAnimation(builder);
                        return PlayState.CONTINUE;
                    }));
                }
            
                @Override
                public PlayerEntity getPlayer()
                {
                    return clientPlayer1;
                }
            };
    
            dragon2 = new DragonEntity(DSEntities.DRAGON, minecraft.level){
                @Override
                public void registerControllers(AnimationData animationData) {
                    animationData.shouldPlayWhilePaused = true;
                    animationData.addAnimationController(new AnimationController<DragonEntity>(this, "controller", 2, (event) -> {
                        AnimationBuilder builder = new AnimationBuilder();
                        builder.addAnimation("idle", true);
                        event.getController().setAnimation(builder);
                        return PlayState.CONTINUE;
                    }));
                }
        
                @Override
                public PlayerEntity getPlayer()
                {
                    return clientPlayer2;
                }
            };
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
    
    public static void renderEntityInInventory(int p_228187_0_, int p_228187_1_, float p_228187_2_, float p_228187_3_, float p_228187_4_, LivingEntity p_228187_5_) {
       if(p_228187_5_ == null) return;
       
        float f = p_228187_3_;
        float f1 = p_228187_4_;
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)p_228187_0_, (float)p_228187_1_, 0);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.scale((float)p_228187_2_, (float)p_228187_2_, (float)p_228187_2_);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 10.0F);
        quaternion.mul(quaternion1);
        matrixstack.mulPose(quaternion);
        float f2 = p_228187_5_.yBodyRot;
        float f3 = p_228187_5_.yRot;
        float f4 = p_228187_5_.xRot;
        float f5 = p_228187_5_.yHeadRotO;
        float f6 = p_228187_5_.yHeadRot;
        p_228187_5_.yBodyRot = 180.0F + f * 10.0F;
        p_228187_5_.yRot = 180.0F + f * 10.0F;
        p_228187_5_.xRot = -f1 * 10.0F;
        p_228187_5_.yHeadRot = p_228187_5_.yRot;
        p_228187_5_.yHeadRotO = p_228187_5_.yRot;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
        boolean renderHitbox = entityrenderermanager.shouldRenderHitBoxes();
        quaternion1.conj();
        entityrenderermanager.overrideCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.setRenderHitBoxes(false);
            entityrenderermanager.render(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
            entityrenderermanager.setRenderHitBoxes(renderHitbox);
        });
        
        irendertypebuffer$impl.endBatch();
        entityrenderermanager.setRenderShadow(true);
        
        p_228187_5_.yBodyRot = f2;
        p_228187_5_.yRot = f3;
        p_228187_5_.xRot = f4;
        p_228187_5_.yHeadRotO = f5;
        p_228187_5_.yHeadRot = f6;
        RenderSystem.popMatrix();
    }
}
