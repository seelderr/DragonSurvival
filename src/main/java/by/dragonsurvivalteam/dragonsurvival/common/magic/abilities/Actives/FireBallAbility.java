package by.dragonsurvivalteam.dragonsurvival.common.magic.abilities.Actives;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.KeyInputHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles.FireBallEntity;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Locale;

public class FireBallAbility extends ActiveDragonAbility{

	public FireBallAbility(DragonType type, String id, String icon, int minLevel, int maxLevel, int manaCost, int castTime, int cooldown, Integer[] requiredLevels){
		super(type, id, icon, minLevel, maxLevel, manaCost, castTime, cooldown, requiredLevels);
	}

	@Override
	public FireBallAbility createInstance(){
		return new FireBallAbility(type, id, icon, minLevel, maxLevel, manaCost, castTime, abilityCooldown, requiredLevels);
	}

	@Override

	public ArrayList<Component> getInfo(){
		ArrayList<Component> components = super.getInfo();
		components.add(new TranslatableComponent("ds.skill.damage", getDamage()));

		if(!KeyInputHandler.ABILITY2.isUnbound()){
			String key = KeyInputHandler.ABILITY2.getKey().getDisplayName().getContents().toUpperCase(Locale.ROOT);

			if(key.isEmpty()){
				key = KeyInputHandler.ABILITY2.getKey().getDisplayName().getString();
			}
			components.add(new TranslatableComponent("ds.skill.keybind", key));
		}

		return components;
	}

	public float getDamage(){
		return getDamage(getLevel());
	}

	public static float getDamage(int level){
		return ServerConfig.fireballDamage.floatValue() * level;
	}

	@Override
	public void onActivation(Player player){
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

		float f1 = -(float)handler.getMovementData().bodyYaw * ((float)Math.PI / 180F);


		float f4 = Mth.sin(f1);
		float f5 = Mth.cos(f1);


		Double size = DragonStateProvider.getCap(player).map((cap) -> cap.getSize()).get();

		double x = player.getX() + f4;
		double y = player.getY() + (size / 20F) - 0.2;
		double z = player.getZ() + f5;

		FireBallEntity entity = new FireBallEntity(player.level, player, d2, d3, d4);
		entity.setPos(x + vector3d.x * speed, y, z + vector3d.z * speed);
		entity.setLevel(getLevel());
		entity.shootFromRotation(player, player.xRot, player.yRot, 0.0F, (float)speed, 1.0F);
		player.level.addFreshEntity(entity);
	}

	@Override
	public Component getDescription(){
		return new TranslatableComponent("ds.skill.description." + getId(), getDamage());
	}

	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.damage", "+" + ServerConfig.fireballDamage.floatValue()));
		list.add(new TranslatableComponent("ds.skill.aoe", "+1"));
		return list;
	}


	@Override
	public boolean isDisabled(){
		return super.isDisabled() || !ServerConfig.fireball;
	}
}