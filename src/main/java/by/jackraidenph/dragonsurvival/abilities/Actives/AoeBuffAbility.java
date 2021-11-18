package by.jackraidenph.dragonsurvival.abilities.Actives;

import by.jackraidenph.dragonsurvival.abilities.common.ActiveDragonAbility;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;

public class AoeBuffAbility extends ActiveDragonAbility
{
	private Effect effect;
	private Color effectColor;
	
	public AoeBuffAbility(Effect effect, Color c, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.effect = effect;
		this.effectColor = c;
	}
	
	@Override
	public AoeBuffAbility createInstance()
	{
		return new AoeBuffAbility(effect, effectColor, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		super.onActivation(player);
		SheepEntity entity = new SheepEntity(EntityType.SHEEP, player.level);
		entity.setPos(player.position().x, player.position().y, player.position().z);
		player.level.addFreshEntity(entity);
	}
	
	public int getDuration(){
		return getLevel() * 30;
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDuration());
	}
}
