package by.jackraidenph.dragonsurvival.client.gui;


import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.client.gui.widgets.buttons.*;
import by.jackraidenph.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.DragonAbilities;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.DragonAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.InnateDragonAbility;
import by.jackraidenph.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;

public class AbilityScreen extends Screen
{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(DragonSurvivalMod.MODID, "textures/gui/magic_interface.png");
    
    private final int xSize = 256;
    private final int ySize = 256;
    private int guiLeft;
    private int guiTop;
    
    private DragonType type;
    public Screen sourceScreen;
    
    public ArrayList<ActiveDragonAbility> unlockAbleSkills = new ArrayList<>();
    
    public AbilityScreen(Screen sourceScreen) {
        super(new TextComponent("AbilityScreen"));
        this.sourceScreen = sourceScreen;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void init()
    {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize / 2) / 2;
        
        int startX = this.guiLeft;
        int startY = this.guiTop;
        
        //Inventory
        addRenderableWidget(new TabButton(startX + 5, startY - 26, 0, this));
        addRenderableWidget(new TabButton(startX + 33, startY - 28, 1, this));
        addRenderableWidget(new TabButton(startX + 62, startY - 26, 2, this));
        addRenderableWidget(new TabButton(startX + 91, startY - 26, 3, this));
    
        addRenderableWidget(new SkillProgressButton(guiLeft + (int)(219 / 2F), startY + 8, 4, this));
        
        for(int i = 0; i <= 4; i++){
            addRenderableWidget(new SkillProgressButton(guiLeft + (int)(219 / 2F) - (i * (23 + ((4 - i) / 4))), startY + 8, 4 - i, this));
            addRenderableWidget(new SkillProgressButton(guiLeft + (int)(219 / 2F) + (i * (23 + ((4 - i) / 4))), startY + 8, 4 + i, this));
        }
        
        DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
     
            int num = 0;
            for(ActiveDragonAbility ability : DragonAbilities.ACTIVE_ABILITIES.get(cap.getType())){
                if(ability != null) {
                    addRenderableWidget(new AbilityButton((int)(guiLeft + (90 / 2.0)), (guiTop + 40 + (num * 23)), ability, this));
                    num++;
                }
            }
            
            num = 0;
            for(PassiveDragonAbility ability : DragonAbilities.PASSIVE_ABILITIES.get(cap.getType())){
                if(ability != null) {
                    addRenderableWidget(new AbilityButton(guiLeft + (int)(217 / 2F), (guiTop + 40 + (num * 23)), ability, this));
                    addRenderableWidget(new IncreaseLevelButton(guiLeft + (int)(219 / 2F) + 30, (guiTop + 40 + (num * 23)), num, this));
                    addRenderableWidget(new DecreaseLevelButton(guiLeft + (int)(219 / 2F) - 25, (guiTop + 40 + (num * 23)), num, this));
                    num++;
                }
            }
    
            num = 0;
            for(InnateDragonAbility ability : DragonAbilities.INNATE_ABILITIES.get(cap.getType())){
                if(ability != null) {
                    addRenderableWidget(new AbilityButton(guiLeft + (int)(346 / 2F), (guiTop + 40 + (num * 23)), ability, this));
                    num++;
                }
            }
        });
        
        addRenderableWidget(new HelpButton(startX + (218 / 2) + 3, startY + (263 / 2) + 4, 9, 9, "ds.skill.help"));
    }
    
    @Override
    public void tick()
    {
        DragonStateProvider.getCap(Minecraft.getInstance().player).ifPresent(cap -> {
            type = cap.getType();
            unlockAbleSkills.clear();
    
            for(ActiveDragonAbility ab : DragonAbilities.ACTIVE_ABILITIES.get(cap.getType())){
                DragonAbility ability = cap.getMagic().getAbility(ab);
                ActiveDragonAbility db = ability != null ? ((ActiveDragonAbility)ability) : ab;
                
                if(db != null) {
                    for(int i = db.getLevel(); i < db.getMaxLevel(); i++){
                        ActiveDragonAbility newActivty = db.createInstance();
                        newActivty.setLevel(i + 1);
                        unlockAbleSkills.add(newActivty);
                    }
                }
            }
    
            unlockAbleSkills.sort(Comparator.comparingInt(c -> c.getCurrentRequiredLevel()));
        });
    }
    
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.minecraft == null)
            return;

        this.renderBackground(stack);

        
        int startX = this.guiLeft;
        int startY = this.guiTop;
        
        this.minecraft.getTextureManager().bindForSetup(BACKGROUND_TEXTURE);
        blit(stack, startX, startY, 0, 0, 256, 256);
        
        if(type != null) {
            int barYPos = type == DragonType.SEA ? 198 : type == DragonType.FOREST ? 186 : 192;
    
            minecraft.getTextureManager().bindForSetup(ClientMagicHUDHandler.widgetTextures);
    
            float progress = Mth.clamp((minecraft.player.experienceLevel / 50F), 0, 1);
            float progress1 = Math.min(1F, (Math.min(0.5F, progress) * 2F));
            float progress2 = Math.min(1F, (Math.min(0.5F, progress - 0.5F) * 2F));
    
            GL11.glPushMatrix();
            GL11.glTranslatef(0.5F, 0.75F, 0F);
            blit(stack, startX + (23 / 2), startY + 28, 0, 180 / 2, 105, 3, 128, 128);
            blit(stack, startX + (254 / 2), startY + 28,  0, 180 / 2,105, 3, 128, 128);

            blit(stack, startX + (23 / 2), startY + 28, 0, barYPos / 2, (int)(105 * progress1), 3, 128, 128);

            if (progress > 0.5) {
                blit(stack, startX + (254 / 2), startY + 28, 0, barYPos / 2, (int)(105 * progress2), 3, 128, 128);
            }
            
            int expChange = -1;
            
            for(GuiEventListener btn : children) {
                if (!(btn instanceof AbstractWidget) || !((AbstractWidget)btn).isHoveredOrFocused()) continue;
    
                if (btn instanceof IncreaseLevelButton) {
                    expChange = ((IncreaseLevelButton)btn).skillCost;
                    break;
                }
            }
            
            if(expChange != -1){
                float Changeprogress = Mth.clamp((expChange / 50F), 0, 1); //Total exp required to hit level 50
                float Changeprogress1 = Math.min(1F, (Math.min(0.5F, Changeprogress) * 2F));
                float Changeprogress2 = Math.min(1F, (Math.min(0.5F, Changeprogress - 0.5F) * 2F));
                
                blit(stack, startX + (23 / 2), startY + 28, 0, 174 / 2, (int)(105 * Changeprogress1), 3, 128, 128);
    
                if (Changeprogress2 > 0.5) {
                    blit(stack, startX + (254 / 2) - (int)(105 * progress1), startY + 28, 0, 174 / 2, (int)(105 * Changeprogress2), 3, 128, 128);
                }
            }
    
            GL11.glPopMatrix();
            
            GL11.glPushMatrix();
            MutableComponent textComponent = new TextComponent(Integer.toString(minecraft.player.experienceLevel)).withStyle(ChatFormatting.DARK_GRAY);
            int xPos = startX + 117 + 1;
            float finalXPos = (float)(xPos - minecraft.font.width(textComponent) / 2);
            minecraft.font.draw(stack, textComponent, finalXPos, startY + 26, 0);
            GL11.glPopMatrix();
        }
    
        super.render(stack, mouseX, mouseY, partialTicks);
        
        for(GuiEventListener btn : children){
            if(btn instanceof AbstractWidget && ((AbstractWidget)btn).isHoveredOrFocused()){
                ((AbstractWidget)btn).renderToolTip(stack, mouseX, mouseY);
            }
        }
    }
}
