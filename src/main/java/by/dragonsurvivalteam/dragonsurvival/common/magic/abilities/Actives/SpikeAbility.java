package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/SpikeAbility.java
import by.jackraidenph.dragonsurvival.client.handlers.KeyInputHandler;
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.projectiles.DragonSpikeEntity;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
=======
import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.DragonSpikeEntity;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/SpikeAbility.java
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Locale;

public class SpikeAbility extends ActiveDragonAbility{
	public SpikeAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels){
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}

	@Override
	public IFormattableTextComponent getDescription(){
		return new TranslationTextComponent("ds.skill.description." + getId(), getDamage());
	}

	public float getDamage(){
		return (float)(ConfigHandler.SERVER.spikeDamage.get() * getLevel());
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<ITextComponent> getLevelUpInfo(){
		ArrayList<ITextComponent> list = super.getLevelUpInfo();
		list.add(new TranslationTextComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.spikeDamage.get()));
		return list;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/SpikeAbility.java
	public Component getDescription()
	{
		return new TranslatableComponent("ds.skill.description." + getId(), getDamage());
=======
	public boolean isDisabled(){
		return super.isDisabled() || !ConfigHandler.SERVER.spike.get();
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/SpikeAbility.java
	}

	@Override
	public SpikeAbility createInstance(){
		return new SpikeAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/SpikeAbility.java
	public ArrayList<Component> getInfo()
	{
		ArrayList<Component> components = super.getInfo();
		components.add(new TranslatableComponent("ds.skill.damage", getDamage()));
		
		if(!KeyInputHandler.ABILITY2.isUnbound()) {
=======
	public ArrayList<ITextComponent> getInfo(){
		ArrayList<ITextComponent> components = super.getInfo();
		components.add(new TranslationTextComponent("ds.skill.damage", getDamage()));

		if(!KeyInputHandler.ABILITY2.isUnbound()){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/SpikeAbility.java
			String key = KeyInputHandler.ABILITY2.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);

			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY2.getKey().getDisplayName().getString();
			}
			components.add(new TranslatableComponent("ds.skill.keybind", key));
		}

		return components;
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/common/magic/abilities/Actives/SpikeAbility.java
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.damage", "+" + ConfigHandler.SERVER.spikeDamage.get() ));
		return list;
	}
	
	@Override
	public void onActivation(Player player)
	{
=======

	@Override
	public void onActivation(PlayerEntity player){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/common/magic/abilities/Actives/SpikeAbility.java
		super.onActivation(player);
		Vec3 vector3d = player.getViewVector(1.0F);
		double speed = 1d;
		double d2 = vector3d.x * speed;
		double d3 = vector3d.y * speed;
		double d4 = vector3d.z * speed;

		DragonStateHandler handler = DragonUtils.getHandler(player);
		if(handler == null){
			return;
		}
		handler.getMovementData().bite = true;

		DragonSpikeEntity entity = new DragonSpikeEntity(DSEntities.DRAGON_SPIKE, player.level, player);
		entity.setPos(entity.getX() + d2, entity.getY() + d3, entity.getZ() + d4);
		entity.setArrow_level(getLevel());
		entity.setBaseDamage(getDamage());
		entity.pickup = AbstractArrow.Pickup.DISALLOWED;
		entity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 4F, 1.0F);
		player.level.addFreshEntity(entity);
	}
}