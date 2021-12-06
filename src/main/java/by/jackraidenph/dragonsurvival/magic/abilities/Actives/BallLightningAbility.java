package by.jackraidenph.dragonsurvival.magic.abilities.Actives;

import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.entity.magic.BallLightningEntity;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.KeyInputHandler;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.util.DragonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Locale;

public class BallLightningAbility extends ActiveDragonAbility
{
	private int range;
	
	public BallLightningAbility(DragonType type, int range, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
		this.range = range;
	}
	
	@Override
	public BallLightningAbility createInstance()
	{
		return new BallLightningAbility(type, range, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		super.onActivation(player);
		
		Vector3d vector3d = player.getViewVector(1.0F);
		double speed = 1d;
		
		double d2 = vector3d.x * speed;
		double d3 = vector3d.y * speed;
		double d4 = vector3d.z * speed;
		
		BallLightningEntity entity = new BallLightningEntity(player.level, player, d2, d3, d4);
		entity.setPos(player.getX() + vector3d.x * speed, player.getY(0.5D) + 0.5D, player.getZ() + vector3d.z * speed);
		entity.setLevel(getLevel());
		entity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, (float)speed, 1.0F);
		player.level.addFreshEntity(entity);
	}
	
	public int getRange()
	{
		return range;
	}
	
	public static float getDamage(int level){
		return (float)(ConfigHandler.SERVER.ballLightningDamage.get() * level);
	}
	
	public float getDamage(){
		return getDamage(getLevel());
	}
	
	@Override
	public ArrayList<ITextComponent> getInfo()
	{
		ArrayList<ITextComponent> components = super.getInfo();
		components.add(new TranslationTextComponent("ds.skill.aoe", getRange() + "x" + getRange() + "x" + getRange()));
		components.add(new TranslationTextComponent("ds.skill.damage", getDamage()));
		
		if(!KeyInputHandler.ABILITY2.isUnbound()) {
			String key = KeyInputHandler.ABILITY2.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);
			
			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY2.getKey().getDisplayName().getString();
			}
			components.add(new TranslationTextComponent("ds.skill.keybind", key));
		}
		
		return components;
	}
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){
		ArrayList<ITextComponent> list = super.getLevelUpInfo();
		list.add(new TranslationTextComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.ballLightningDamage.get()));
		return list;
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDamage());
	}
	
	@Override
	public boolean isDisabled()
	{
		return super.isDisabled() || !ConfigHandler.SERVER.ballLightning.get();
	}
}
