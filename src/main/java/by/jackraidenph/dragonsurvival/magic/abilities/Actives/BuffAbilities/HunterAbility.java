package by.jackraidenph.dragonsurvival.magic.abilities.Actives.BuffAbilities;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.KeyInputHandler;
import by.jackraidenph.dragonsurvival.magic.common.AbilityAnimation;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.registration.DragonEffects;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Locale;

public class HunterAbility extends ActiveDragonAbility
{
	public HunterAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}
	
	@Override
	public HunterAbility createInstance()
	{
		return new HunterAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	public int getDuration(){
		return ConfigHandler.SERVER.hunterDuration.get() * getLevel();
	}
	
	public double getDamage(){
		return ConfigHandler.SERVER.hunterDamageBonus.get() * getLevel();
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), (1.5 * getLevel() + "x"), getDuration());
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		super.onActivation(player);
		player.addEffect(new EffectInstance(DragonEffects.HUNTER, Functions.secondsToTicks(getDuration()), getLevel() - 1));
		player.level.playLocalSound(player.position().x, player.position().y + 0.5, player.position().z, SoundEvents.UI_TOAST_IN, SoundCategory.PLAYERS, 5F, 0.1F, false);
	}
	
	@Override
	public AbilityAnimation getLoopingAnimation()
	{
		return new AbilityAnimation("cast_self_buff", true);
	}
	
	@Override
	public AbilityAnimation getStoppingAnimation()
	{
		return new AbilityAnimation("self_buff", 160, true);
	}
	
	public int getCastingSlowness() { return 10; }
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> components = super.getInfo();
		
		if(!KeyInputHandler.ABILITY4.isUnbound()) {
			String key = KeyInputHandler.ABILITY4.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);
			
			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY4.getKey().getDisplayName().getString();
			}
			components.add(new TranslationTextComponent("ds.skill.keybind", key));
		}
		
		return components;
	}
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){
		ArrayList<ITextComponent> list = super.getLevelUpInfo();
		list.add(new TranslationTextComponent("ds.skill.duration.seconds", "+" + ConfigHandler.SERVER.hunterDuration.get()));
		list.add(new TranslationTextComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.hunterDamageBonus.get() + "X"));
		return list;
	}
	
	@Override
	public boolean isDisabled()
	{
		return super.isDisabled() || !ConfigHandler.SERVER.hunter.get();
	}
}
