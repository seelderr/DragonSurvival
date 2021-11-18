package by.jackraidenph.dragonsurvival.abilities.common;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

public class ActiveDragonAbility extends DragonAbility
{
    protected int manaCost;
    protected Integer[] requiredLevels;
    
    protected int castTime;
    protected int currentCastingTime;
    
    protected int abilityCooldown;
    protected int currentCooldown;
    
    public ActiveDragonAbility(String id,  String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
    {
        super(id, icon, minLevel, maxLevel);
        this.manaCost = manaCost;
        this.requiredLevels = requiredLevels;
        this.abilityCooldown = cooldown;
        this.castTime = castTime;
    }
    
    @Override
    public ActiveDragonAbility createInstance(){
        return new ActiveDragonAbility(id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
    }
    
    public Integer[] getRequiredLevels()
    {
        return requiredLevels;
    }
    
    public int getNextRequiredLevel(){
        if(level < maxLevel){
            if(getRequiredLevels().length > level && level > 0){
                return getRequiredLevels()[level];
            }
        }
        
        return 0;
    }
    
    public int getCurrentRequiredLevel(){
        if(getRequiredLevels().length > level && level > 0){
            return getRequiredLevels()[level - 1];
        }
        
        return 0;
    }
    
    public int getLevelCost(){
        return 1 + (int)(0.75 * getLevel());
    }
    
    public int getManaCost() {
        return manaCost;
    }
    
    public boolean canConsumeMana(PlayerEntity player) {
        return DragonStateProvider.getCurrentMana(player) >= this.getManaCost();
    }
    
    public void consumeMana(PlayerEntity player) {
        DragonStateProvider.consumeMana(player, this.getManaCost());
    }
    
    public void onActivation(PlayerEntity player) {
       resetSkill();
       consumeMana(player);
    }

    @Override
    public void onKeyPressed(PlayerEntity player, int keyMode) {
        if (!this.canConsumeMana(player)){
            if(keyMode == GLFW.GLFW_PRESS){
                if(!player.level.isClientSide) {
                    player.sendMessage(new TranslationTextComponent("ds.skill_mana_check_failure").withStyle(TextFormatting.DARK_AQUA), player.getUUID());
                }
            }
            stopCasting();
            return;
        }
    
        if (this.getCooldown() != 0) {
            if(keyMode == GLFW.GLFW_PRESS){
                if(!player.level.isClientSide) {
                    player.sendMessage(new TranslationTextComponent("ds.skill_cooldown_check_failure", nf.format(Functions.ticksToSeconds(this.getCooldown())) + "s").withStyle(TextFormatting.RED), player.getUUID());
                }
            }
            stopCasting();
            return;
        }
        
        //TODO Charging isnt working
        if (this.getCooldown() == 0 && this.canConsumeMana(player)){
            if(keyMode == GLFW.GLFW_RELEASE){
                stopCasting();
                
            }else if(getCurrentCastTimer() < getCastingTime() && keyMode == GLFW.GLFW_REPEAT) {
                tickCasting();
            }else if(getCurrentCastTimer() >= getCastingTime()){
                this.onActivation(player);
            }else if(keyMode == GLFW.GLFW_PRESS && getCastingTime() <= 0){
                this.onActivation(player);
            }
        }
    }
    
    public void resetSkill(){
        stopCasting();
        startCooldown();
    }
    
    public int getCooldown() {
        return this.currentCooldown;
    }
    
    public void setCooldown(int newCooldown) {
        this.currentCooldown = newCooldown;
    }
    
    public void startCooldown() {
        this.currentCooldown = this.getMaxCooldown();
        DragonSurvivalMod.getTickHandler().addToCoolDownList(this);
    }
    
    public int getMaxCooldown() {
        return abilityCooldown;
    }
    
    public void decreaseCooldownTimer() {
        if (this.currentCooldown > 0){
            this.currentCooldown--;
        }
    }
    
    public void tickCasting() {
        if (this.currentCastingTime <= this.getCastingTime()){
            this.currentCastingTime++;
        }
    }
    
    
    public void stopCasting() {
        this.currentCastingTime = 0;
    }
    
    public int getCurrentCastTimer(){
        return currentCastingTime;
    }
    
    public void setCastTimer(int chargeTimer) {
        this.currentCastingTime = chargeTimer;
    }
    
    public int getCastingTime() {
        return castTime;
    }
    
    public CompoundNBT saveNBT(){
        CompoundNBT nbt = super.saveNBT();
        nbt.putInt("cooldown", currentCooldown);
        return nbt;
    }
    
    public void loadNBT(CompoundNBT nbt){
        super.loadNBT(nbt);
        currentCooldown = nbt.getInt("cooldown");
    
        if(currentCooldown > 0) {
            DragonSurvivalMod.getTickHandler().addToCoolDownList(this);
        }
    }
}
