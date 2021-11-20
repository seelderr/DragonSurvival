package by.jackraidenph.dragonsurvival.magic.Abilities.Actives;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.ArrayList;

public class AoeBuffAbility extends ActiveDragonAbility
{
	private EffectInstance effect;
	private Color effectColor;
	private int range;
	
	public AoeBuffAbility(EffectInstance effect, int range, Color c, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.effect = effect;
		this.effectColor = c;
		this.range = range;
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
		return new AoeBuffAbility(effect, range, effectColor, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		super.onActivation(player);
		AreaEffectCloudEntity entity = new AreaEffectCloudEntity(EntityType.AREA_EFFECT_CLOUD, player.level);
		entity.setWaitTime(0);
		entity.setPos(player.position().x, player.position().y + 0.5, player.position().z);
		entity.setPotion(new Potion(new EffectInstance(effect.getEffect(), Functions.secondsToTicks(getDuration()) * 4, effect.getAmplifier(), false, false))); //Effect duration is divided by 4 normaly
		entity.setDuration(10);
		entity.setRadius(getRange());
		entity.setFixedColor(effectColor.getRGB());
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
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration());
	}
}
