package by.jackraidenph.dragonsurvival.client.gui;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.ArrowButton;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.HelpButton;
import by.jackraidenph.dragonsurvival.client.render.ClientDragonRender;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.jackraidenph.dragonsurvival.network.flight.SyncSpinStatus;
import by.jackraidenph.dragonsurvival.network.status.SyncAltarCooldown;
import by.jackraidenph.dragonsurvival.util.Functions;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.lwjgl.opengl.GL11;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DragonAltarGUI extends Screen {
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/dragon_altar_texture.png");
    public static final ResourceLocation CONFIRM_BUTTON = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/confirm_button.png");
    
    private int guiLeft;
    private int guiTop;
    private boolean hasInit = false;
    
    private float yRot = -3;
    private float xRot = -5;
    private float zoom = 0;
    
    public DragonType selected = DragonType.NONE;
    public DragonLevel level = DragonLevel.ADULT;
    private DragonEntity dragon;
    private static RemoteClientPlayerEntity clientPlayer;
    
    private String[] animations = {"sit", "idle", "fly", "swim_fast", "run"};
    private int curAnimation = 0;
    
    private boolean expandedSettings = false;
    
    public void update(){
        DragonStateHandler clientHandler = DragonStateProvider.getCap(clientPlayer).orElse(null);
        clientHandler.setType(selected);
        clientHandler.setSize(level.size);
    }
    
    public DragonAltarGUI(ITextComponent title) {
        super(title);
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

        if(!expandedSettings && selected != DragonType.NONE) {
            if (mouseX >= startX + 40 && mouseX <= startX + 40 + 13 && mouseY >= startY + 77 && mouseY <= startY + 77 + 6) {
                expandedSettings = true;
            }
        }else{
            if (mouseX < startX + 7 || mouseX > startX + 7 + 79 || mouseY < startY + 46 || mouseY > startY + 46 + 37) {
                expandedSettings = false;
            }
        }

        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
        RenderSystem.enableBlend();
        blit(matrixStack, startX, startY, 0, 0, 310, 159, 512, 512);
        RenderSystem.disableBlend();
        List<IReorderingProcessor> description = Minecraft.getInstance().font.split(new TranslationTextComponent("ds.altar_dragon_info." + (selected == DragonType.NONE ? "human" : selected.name().toLowerCase() + "_dragon")), (int)(78 * 2));
    
        matrixStack.pushPose();
        matrixStack.scale(0.5f, 0.5f, 0);
        matrixStack.translate((guiLeft + 7), (guiTop + 89), 0);
        for (int i = 0; i < description.size(); i++) {
            IReorderingProcessor text = description.get(i);
            Minecraft.getInstance().font.drawShadow(matrixStack, text, guiLeft + 9, guiTop + 91 + i * 12, DyeColor.WHITE.getTextColor());
        }
        matrixStack.popPose();
    
        GL11.glScissor((int)((guiLeft + 7) * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)((guiTop + 107.4) * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)(79 * Minecraft.getInstance().getWindow().getGuiScale()),
                       (int)(76 * Minecraft.getInstance().getWindow().getGuiScale()));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        
        float scale = zoom;
        matrixStack.scale(scale, scale, scale);
        matrixStack.translate(0, 0, 400);
        ClientDragonRender.dragonModel.setCurrentTexture(null);
        renderEntityInInventory(guiLeft + 7 + (76 / 2), (int)(guiTop + 7 + 46 + zoom), scale, xRot, yRot, DragonStateProvider.isDragon(clientPlayer) ? dragon : clientPlayer);
    
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        matrixStack.translate(0, 0, -400);
        matrixStack.popPose();
        
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 600);
        this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
        
        if(expandedSettings){
            blit(matrixStack, startX + 7, startY + 46, 0, 189, 79, 37, 512, 512);
            
            drawCenteredString(matrixStack, minecraft.font, level.getName(), (guiLeft + 5) + 41, guiTop + 53, DyeColor.WHITE.getTextColor());
            drawCenteredString(matrixStack, minecraft.font, animations[curAnimation], (guiLeft + 5) + 41, guiTop + 53 + 17, DyeColor.WHITE.getTextColor());
        }else{
            blit(matrixStack, startX + 40, startY + 77, 0, 178, 13, 6, 512, 512);
        }
        
        matrixStack.popPose();
    
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    
    @Override
    protected void init() {
        super.init();

        if(!hasInit){
            DragonStateHandler handler = DragonStateProvider.getCap(Minecraft.getInstance().player).orElse(null);
            
            if(handler != null){
                selected = handler.getType();
            }
    
            level = handler.getLevel();
            zoom = level.size;
            
            if(clientPlayer == null) {
                clientPlayer = new RemoteClientPlayerEntity(minecraft.level, new GameProfile(UUID.randomUUID(), "DRAGON_RENDER"));
            
                DragonStateProvider.getCap(clientPlayer).ifPresent((cap) -> {
                    cap.setHasWings(true);
                
                    if(handler != null){
                        cap.setType(handler.getType());
                    }
                });
            }
        
            dragon = new DragonEntity(DSEntities.DRAGON, minecraft.level){
                @Override
                public void registerControllers(AnimationData animationData) {
                    animationData.addAnimationController(new AnimationController<DragonEntity>(this, "controller", 2, (event) -> {
                        AnimationBuilder builder = new AnimationBuilder();
                        builder.addAnimation(animations[curAnimation], true);
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
            
            update();
            hasInit = true;
        }
        
        this.guiLeft = (this.width - 304) / 2;
        this.guiTop = (this.height - 190) / 2;
    
        this.addButton(new HelpButton(this.guiLeft + 8, this.guiTop + 8, 9, 9, "ds.help.altar"){
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
    
        addButton(new ExtendedButton(guiLeft + 6, guiTop + 131, 23, 23, new StringTextComponent(""), (b) -> {
            Minecraft.getInstance().setScreen(new DragonCustomizationScreen(this, selected));
        }){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.active = selected != DragonType.NONE;
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            }
    
            @Override
            public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
            {
                super.renderButton(mStack, mouseX, mouseY, partial);
//                Minecraft.getInstance().getTextureManager().bind(CONFIRM_BUTTON);
//                blit(mStack, x, y, 0, 0, width, height, width, height);
        
                if(isHovered){
                    ArrayList<ITextComponent> components = new ArrayList<>();
                    components.add(new TranslationTextComponent("ds.gui.dragon_altar.customize") );
                    GuiUtils.drawHoveringText(mStack, components, mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
                }
            }
        });
        
        addButton(new ExtendedButton(guiLeft + 6 + 29, guiTop + 131, 23, 23, new StringTextComponent(""), (b) -> {
        
        }){
            @Override
            public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
            {
                super.renderButton(mStack, mouseX, mouseY, partial);
//                Minecraft.getInstance().getTextureManager().bind(CONFIRM_BUTTON);
//                blit(mStack, x, y, 0, 0, width, height, width, height);
//
            }
        });
        
        addButton(new ExtendedButton(guiLeft + 6 + (29 * 2), guiTop + 131, 23, 23, new StringTextComponent(""), (b) -> {
                initiateDragonForm(selected);
                Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("ds." + (selected == DragonType.NONE ? "choice_human" : selected.name().toLowerCase() + "_dragon_choice")), Minecraft.getInstance().player.getUUID());
        }){
            @Override
            public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partial)
            {
                super.renderButton(mStack, mouseX, mouseY, partial);
                Minecraft.getInstance().getTextureManager().bind(CONFIRM_BUTTON);
                blit(mStack, x + 2, y + 1, 0, 0, 20, 20, 20, 20);
    
                if(isHovered){
                    ArrayList<ITextComponent> components = new ArrayList<>();
                    components.add(new TranslationTextComponent("ds.gui.dragon_altar.confirm") );
                    GuiUtils.drawHoveringText(mStack, components, mouseX, mouseY, Minecraft.getInstance().screen.width, Minecraft.getInstance().screen.height, 200, Minecraft.getInstance().font);
                }
            }
        });
    
        addButton(new ArrowButton(guiLeft + 10, guiTop + 49, 15, 17, false, (btn) -> {
            int curLevel = level.ordinal();
            if(curLevel == 0){
                curLevel = DragonLevel.values().length-1;
            }else{
                curLevel -= 1;
            }
            
            level = DragonLevel.values()[curLevel];
            update();
        }){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.visible = this.active = expandedSettings;
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            }
        });
    
        addButton(new ArrowButton(guiLeft + 82 - 10, guiTop + 49, 15, 17, true, (btn) -> {
            int curLevel = level.ordinal();
            if(curLevel == DragonLevel.values().length - 1){
                curLevel = 0;
            }else{
                curLevel += 1;
            }
    
            level = DragonLevel.values()[curLevel];
            update();
        }){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.visible = this.active = expandedSettings;
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            }
        });
    
        addButton(new ArrowButton(guiLeft + 10, guiTop + 49 + 17, 15, 17, false, (btn) -> {
            curAnimation -= 1;
            
            if(curAnimation < 0){
                curAnimation = animations.length - 1;
            }
            
            update();
        }){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.visible = this.active = expandedSettings;
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
            }
        });
    
        addButton(new ArrowButton(guiLeft + 82 - 10, guiTop + 49 + 17, 15, 17, true, (btn) -> {
            curAnimation += 1;
    
            if(curAnimation >= animations.length){
                curAnimation = 0;
            }
            
            update();
        }){
            @Override
            public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_)
            {
                this.visible = this.active = expandedSettings;
                super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
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
        matrixstack.translate(0, (Math.abs(yRot) / 17) * -(Math.abs(zoom)), 0);
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
    
    
    private void initiateDragonForm(DragonType type) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        
        if (player == null)
            return;
        
        player.closeContainer();
        DragonStateProvider.getCap(player).ifPresent(cap -> {
            player.level.playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 1, 0.7f);
            
            cap.setType(type);
            
            if(type == DragonType.NONE){
                cap.setSize(20F);
                cap.setHasWings(false);
            }else{
                if(!ConfigHandler.SERVER.saveGrowthStage.get() || cap.getSize() == 0){
                    cap.setSize(DragonLevel.BABY.size);
                }
                
                cap.setHasWings(ConfigHandler.SERVER.saveGrowthStage.get() ? cap.hasWings() || ConfigHandler.SERVER.startWithWings.get() : ConfigHandler.SERVER.startWithWings.get());
            }
            
            cap.setIsHiding(false);
            cap.getMovementData().spinLearned = false;
            
            NetworkHandler.CHANNEL.sendToServer(new SyncAltarCooldown(Minecraft.getInstance().player.getId(), Functions.secondsToTicks(ConfigHandler.SERVER.altarUsageCooldown.get())));
            NetworkHandler.CHANNEL.sendToServer(new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), ConfigHandler.SERVER.caveLavaSwimmingTicks.get(), 0));
            NetworkHandler.CHANNEL.sendToServer(new SyncSpinStatus(Minecraft.getInstance().player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
        });
    }
    
    
    @Override
    public boolean mouseDragged(double x1, double y1, int p_231045_5_, double x2, double y2)
    {
        xRot -= x2 / 6;
        yRot -= y2 / 6;
        
        xRot = MathHelper.clamp(xRot, -17, 17);
        yRot = MathHelper.clamp(yRot, -17, 17);
        
        return super.mouseDragged(x1, y1, p_231045_5_, x2, y2);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        zoom += amount;
        zoom = MathHelper.clamp(zoom, 10, 80);
        
        return true;
    }
}
