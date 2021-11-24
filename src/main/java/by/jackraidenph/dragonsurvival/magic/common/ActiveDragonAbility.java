package by.jackraidenph.dragonsurvival.magic.common;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

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
    public ArrayList<ITextComponent> getInfo()
    {
        ArrayList<ITextComponent> components = super.getInfo();
    
        components.add(new TranslationTextComponent("ds.skill.mana_cost", getManaCost()));
    
        if(getCastingTime() > 0){
            components.add(new TranslationTextComponent("ds.skill.cast_time", Functions.ticksToSeconds(getCastingTime())));
        }
    
        if(getMaxCooldown() > 0){
            components.add(new TranslationTextComponent("ds.skill.cooldown", Functions.ticksToSeconds(getMaxCooldown())));
        }
        
        return components;
    }
    
    @Override
    public int getLevel()
    {
        if(requiredLevels != null && getPlayer() != null){
            int level = 0;
            
            for(int req : requiredLevels){
                if(getPlayer().experienceLevel >= req){
                    level++;
                }
            }
            
            return level;
        }
        return super.getLevel();
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
        if(getLevel() <= maxLevel){
            if(getRequiredLevels().length > getLevel() && getLevel() > 0){
                return getRequiredLevels()[getLevel()];
            }
        }
        
        return 0;
    }
    
    public int getCurrentRequiredLevel(){
        if(getRequiredLevels().length >= getLevel() && getLevel() > 0){
            return getRequiredLevels()[getLevel() - 1];
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
        return player.isCreative() || DragonStateProvider.getCurrentMana(player) >= this.getManaCost();
    }
    
    public void consumeMana(PlayerEntity player) {
        DragonStateProvider.consumeMana(player, this.getManaCost());
    }
    
    public void onActivation(PlayerEntity player) {
       resetSkill();
       consumeMana(player);
    }

    public int errorTicks;
    public ITextComponent errorMessage;
    
    public boolean canRun(PlayerEntity player, int keyMode){
        if (!this.canConsumeMana(player)){
            if(keyMode == GLFW.GLFW_PRESS){
                errorMessage = new TranslationTextComponent("ds.skill_mana_check_failure").withStyle(TextFormatting.DARK_AQUA);
                errorTicks = Functions.secondsToTicks(5);
                player.playSound(SoundEvents.GENERIC_SPLASH, 0.15f, 100f);
            }
            stopCasting();
            return false;
        }
    
        if (this.getCooldown() != 0) {
            if(keyMode == GLFW.GLFW_PRESS){
                errorMessage = new TranslationTextComponent("ds.skill_cooldown_check_failure", nf.format(this.getCooldown() / 20F) + "s").withStyle(TextFormatting.RED);
                errorTicks = Functions.secondsToTicks(5);
                player.playSound(SoundEvents.WITHER_SHOOT, 0.05f, 100f);
            }
            DragonSurvivalMod.HANDLER.addToCoolDownList(this);
            stopCasting();
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onKeyPressed(PlayerEntity player) {
        this.onActivation(player);
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
        DragonSurvivalMod.HANDLER.addToCoolDownList(this);
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
    
    public int getCastingTime() {
        return castTime;
    }
    
    public CompoundNBT saveNBT(){
        CompoundNBT nbt = super.saveNBT();
        nbt.putInt("cooldown", currentCooldown);
        nbt.putInt("castTime", currentCastingTime);
    
        return nbt;
    }
    
    public void loadNBT(CompoundNBT nbt){
        super.loadNBT(nbt);
        currentCooldown = nbt.getInt("cooldown");
        currentCastingTime = nbt.getInt("castTime");
    
        if(currentCooldown > 0) {
            DragonSurvivalMod.HANDLER.addToCoolDownList(this);
        }
    }
}
