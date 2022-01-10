package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class DragonAltarGUI extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_altar_texture.png");
    public static final ResourceLocation CONFIRM_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/confirm_button.png");
    
    private int guiLeft;
    private int guiTop;
    private boolean hasInit = false;
    
    private DragonEntity dragon;
    private static RemoteClientPlayerEntity clientPlayer;
    
    
    public DragonAltarGUI() {
        super(new StringTextComponent("Dragon altar"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.minecraft == null)
            return;
        this.renderBackground(matrixStack);
        int startX = this.guiLeft;
        int startY = this.guiTop;
        
        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
        blit(matrixStack, startX, startY, 0, 0, 310, 159, 512, 512);

        for(Widget btn : buttons){
            if(btn instanceof AltarTypeButton){
                AltarTypeButton button = (AltarTypeButton)btn;
                
                if(button.isHovered()) {
                    DragonStateProvider.getCap(clientPlayer).ifPresent((cap) -> {
                        cap.setType(button.type);
                    });
                    matrixStack.pushPose();
                    float scale = 20 * 2;
                    matrixStack.scale(scale, scale, scale);
                    matrixStack.translate(0, 0, 400);
                    ClientDragonRender.dragonModel.setCurrentTexture(null);
                    if(ClientDragonRender.dragonArmor != null && ClientDragonRender.dragonModel != null) {
                        renderEntityInInventory(guiLeft, (int)(guiTop + 7 + 100), scale, -5, -3, DragonStateProvider.isDragon(clientPlayer) ? dragon : clientPlayer);
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
            }
        }
        
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    
    @Override
    protected void init() {
        super.init();

        if(!hasInit){
            if(clientPlayer == null) {
                clientPlayer = new RemoteClientPlayerEntity(minecraft.level, new GameProfile(UUID.randomUUID(), "DRAGON_RENDER")){
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
                };
            
                DragonStateProvider.getCap(clientPlayer).ifPresent((cap) -> {
                    cap.setHasWings(true);
                    cap.setSize(DragonLevel.ADULT.size);
                });
            }
        
            dragon = new DragonEntity(DSEntities.DRAGON, minecraft.level){
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
                    return clientPlayer;
                }
            };
            hasInit = true;
        }
        
        this.guiLeft = (this.width - 304) / 2;
        this.guiTop = (this.height - 190) / 2;
    
        this.addButton(new HelpButton(this.guiLeft + 69, this.guiTop + 8, 11, 11, "ds.help.altar"){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                p_230430_1_.pushPose();
                p_230430_1_.translate(0, 0, 200);
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
                p_230430_1_.popPose();
            }
        });
        addButton(new AltarTypeButton(this, DragonType.CAVE, this.guiLeft + 93, this.guiTop + 6));
        addButton(new AltarTypeButton(this, DragonType.FOREST, this.guiLeft + 145, this.guiTop + 6));
        addButton(new AltarTypeButton(this, DragonType.SEA, this.guiLeft + 197, this.guiTop + 6));
        addButton(new AltarTypeButton(this, DragonType.NONE, guiLeft + 249, guiTop + 6));
        //ds.gui.customization
        addButton(new ExtendedButton(guiLeft + 87, guiTop + 165, 218, 30, new TranslationTextComponent("ds.gui.customization"), (btn) -> {
            Minecraft.getInstance().setScreen(new DragonCustomizationScreen(Minecraft.getInstance().screen));
        }){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.visible = DragonStateProvider.isDragon(minecraft.player);
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            }
    
            @Override
            public int getFGColor()
            {
                return Color.darkGray.getRGB();
            }
    
            @Override
            public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
            {
                if (this.visible)
                {
                    Minecraft mc = Minecraft.getInstance();
                    this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    
                    mc.getTextureManager().bind(BACKGROUND_TEXTURE);
                    blit(mStack, x, y, 87, 165, 218, 30, 512, 512);
    
                    ITextComponent buttonText = this.getMessage();
                    int strWidth = mc.font.width(buttonText);
                    int ellipsisWidth = mc.font.width("...");
        
                    if (strWidth > width - 6 && strWidth > ellipsisWidth) {
                        buttonText = new StringTextComponent(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");
                    }
                    
                    mc.font.draw(mStack, buttonText, this.x + this.width / 2 - (mc.font.width(buttonText) / 2), this.y + (this.height - 8) / 2, getFGColor());
                }
            }
        });
    }
    
    public void renderEntityInInventory(int p_228187_0_, int p_228187_1_, float p_228187_2_, float p_228187_3_, float p_228187_4_, LivingEntity p_228187_5_) {
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
