package by.jackraidenph.dragonsurvival.magic.Abilities.Actives;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class AoeBuffAbility extends ActiveDragonAbility
{
	protected EffectInstance effect;
	protected int range;
	protected ParticleType particle;
	
	public AoeBuffAbility(EffectInstance effect, int range, ParticleType particle, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.effect = effect;
		this.range = range;
		this.particle = particle;
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> components = super.getInfo();
		components.add(new TranslationTextComponent("ds.skill.aoe", getRange() + "x" + getRange()));
		return components;
	}
	
	@Override
	public AoeBuffAbility createInstance()
	{
		return new AoeBuffAbility(effect, range, particle, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	public EffectInstance getEffect(){
		return new EffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()) * 4, effect.getAmplifier(), false, false);
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		super.onActivation(player);
		AreaEffectCloudEntity entity = new AreaEffectCloudEntity(EntityType.AREA_EFFECT_CLOUD, player.level);
		entity.setWaitTime(0);
		entity.setPos(player.position().x, player.position().y + 0.5, player.position().z);
		entity.setPotion(new Potion(getEffect())); //Effect duration is divided by 4 normaly
		entity.setDuration(10);
		entity.setRadius(getRange());
		
		try {
			entity.setParticle(particle.getDeserializer().fromCommand(particle, new StringReader("")));
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}
		
		player.level.addFreshEntity(entity);
	}
	
	public int getDuration(){
		return getLevel() * (effect.getDuration() > 0 ? effect.getDuration() : 30);
	}
	
	public int getRange()
	{
		return range;
	}
	
	@Override
	public String loopAnimation()
	{
		return "cast_mass_buff";
	}
	
	@Override
	public String stopAnimation()
	{
		return "mass_buff";
	}
	
	@Override
	public int endAnimationTime()
	{
		return Functions.secondsToTicks(13);
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration());
	}
}
