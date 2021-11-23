package by.jackraidenph.dragonsurvival.magic.Abilities.Actives;

import by.jackraidenph.dragonsurvival.Functions;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class StrongLeatherAbility extends AoeBuffAbility
{
	public StrongLeatherAbility(EffectInstance effect, int range, ParticleType particle, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(effect, range, particle, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}
	
	public static int getDefence(int level){
		return level * 2;
	}
	
	@Override
	public EffectInstance getEffect()
	{
		return new EffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()) * 4, getLevel() - 1, false, false);
	}
	
	@Override
	public StrongLeatherAbility createInstance()
	{
		return new StrongLeatherAbility(effect, range, particle, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration(), getDefence(getLevel()));
	}
}
