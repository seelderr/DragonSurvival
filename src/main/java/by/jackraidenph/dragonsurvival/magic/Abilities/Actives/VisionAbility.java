package by.jackraidenph.dragonsurvival.magic.Abilities.Actives;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.magic.common.AbilityAnimation;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.registration.ClientModEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Locale;

public class VisionAbility extends ActiveDragonAbility
{
	private Effect effect;
	public VisionAbility(Effect effect, String name, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(name, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.effect = effect;
	}
	
	@Override
	public VisionAbility createInstance()
	{
		return new VisionAbility(effect, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	public int getDuration(){
		return 30 * getLevel();
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		super.onActivation(player);
		player.addEffect(new EffectInstance(effect, Functions.secondsToTicks(getDuration())));
		player.addEffect(new EffectInstance(Effects.NIGHT_VISION, Functions.secondsToTicks(getDuration()), 0, false, false));
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> components = super.getInfo();
		components.add(new TranslationTextComponent("ds.skill.duration.seconds", getDuration()));
		
		if(!ClientModEvents.ABILITY4.isUnbound()) {
			components.add(new TranslationTextComponent("ds.skill.keybind", ClientModEvents.ABILITY4.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT)));
		}
		
		return components;
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration());
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
}
