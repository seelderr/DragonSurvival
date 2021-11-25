package by.jackraidenph.dragonsurvival.magic.Abilities.Actives;

import by.jackraidenph.dragonsurvival.Functions;
import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.magic.entity.FireBreathEntity;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class BreathAbility extends ActiveDragonAbility
{
	private DragonType type;
	
	public BreathAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.type = type;
	}
	
	@Override
	public BreathAbility createInstance()
	{
		return new BreathAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	public int channelCost = 1;
	private boolean firstUse = true;
	private int spawnCooldown = 0;
	
	public boolean canConsumeMana(PlayerEntity player) {
		return player.isCreative() || DragonStateProvider.getCurrentMana(player) >= (firstUse ? this.getManaCost() : channelCost)
		       || (player.totalExperience / 10) >= (firstUse ? this.getManaCost() : channelCost) || player.experienceLevel > 0;
	}
	
	public void stopCasting() {
		super.stopCasting();
		
		if(getCooldown() == 0 && !firstUse){
			startCooldown();
			firstUse = true;
		}
		spawnCooldown = 0;
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		if(firstUse) {
			DragonStateProvider.consumeMana(player, this.getManaCost());
			firstUse = false;
			spawnCooldown = 0;
		}else{
			if(player.tickCount % Functions.secondsToTicks(2) == 0){
				DragonStateProvider.consumeMana(player, channelCost);
			}
		}
		
		if(spawnCooldown <= 0) {
			Vector3d vector3d = player.getViewVector(1.0F);
			double speed = 1d;
			double d2 = vector3d.x * speed;
			double d3 = vector3d.y * speed;
			double d4 = vector3d.z * speed;
			
			FireBreathEntity entity = new FireBreathEntity(player.level, player, 0, 0, 0);
			entity.setPos(player.getX() + d2, player.getY(0.5D) + d3, player.getZ() + d4);
			entity.setLevel(getLevel());
			entity.setOwner(player);
			player.level.addFreshEntity(entity);
			
			spawnCooldown = 65;
		}else{
			spawnCooldown--;
		}
	}
	
	public static int getDamage(int level){
		return 3 * level;
	}
	
	public int getDamage(){
		return getDamage(getLevel());
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDamage());
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> list = super.getInfo();
		list.add(new TranslationTextComponent("ds.skill.channel_cost", channelCost, "2s"));
		return list;
	}
}
