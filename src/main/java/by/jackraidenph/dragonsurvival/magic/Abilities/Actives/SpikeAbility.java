package by.jackraidenph.dragonsurvival.magic.Abilities.Actives;

import by.jackraidenph.dragonsurvival.magic.entity.DragonSpikeEntity;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.registration.EntityTypesInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SpikeAbility extends ActiveDragonAbility
{
	public SpikeAbility(String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels)
	{
		super(id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}
	
	public int getDamage(){
		return 5 * getLevel();
	}
	
	@Override
	public IFormattableTextComponent getDescription()
	{
		return new TranslationTextComponent("ds.skill.description." + getId(), getDamage());
	}
	
	@Override
	public SpikeAbility createInstance()
	{
		return new SpikeAbility(id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}
	
	@Override
	public void onActivation(PlayerEntity player)
	{
		super.onActivation(player);
		
		DragonSpikeEntity entity = new DragonSpikeEntity(EntityTypesInit.DRAGON_SPIKE, player.level, player);
		entity.setLevel(getLevel());
		entity.setBaseDamage(getDamage());
		entity.pickup = AbstractArrowEntity.PickupStatus.DISALLOWED;
		entity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 4F, 1.0F);
		player.level.addFreshEntity(entity);
	}
}
