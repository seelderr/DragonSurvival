package by.dragonsurvivalteam.dragonsurvival.common.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class DragonModifiers{
	public static final UUID REACH_MODIFIER_UUID = UUID.fromString("7455d5c7-4e1f-4cca-ab46-d79353764020");
	public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("03574e62-f9e4-4f1b-85ad-fde00915e446");
	public static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("5bd3cebc-132e-4f9d-88ef-b686c7ad1e2c");
	public static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("2a9341f3-d19e-446c-924b-7cf2e5259e10");

	public static AttributeModifier buildHealthMod(double size){
		double healthMod = ((float)ConfigHandler.SERVER.minHealth.get() + (((size - 14) / 26F) * ((float)ConfigHandler.SERVER.maxHealth.get() - (float)ConfigHandler.SERVER.minHealth.get()))) - 20;
		healthMod = Math.min(healthMod, ConfigHandler.SERVER.maxHealth.get() - 20);


		return new AttributeModifier(HEALTH_MODIFIER_UUID, "Dragon Health Adjustment", healthMod, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier buildReachMod(double size){
		double reachMod = (((size - DragonLevel.BABY.size) / (60.0 - DragonLevel.BABY.size)) * (ConfigHandler.SERVER.reachBonus.get()));

		return new AttributeModifier(REACH_MODIFIER_UUID, "Dragon Reach Adjustment", reachMod, Operation.MULTIPLY_BASE);
	}

	public static AttributeModifier buildDamageMod(DragonStateHandler handler, boolean isDragon){
		double ageBonus = isDragon ? (handler.getLevel() == DragonLevel.ADULT ? ConfigHandler.SERVER.adultBonusDamage.get() : handler.getLevel() == DragonLevel.YOUNG ? ConfigHandler.SERVER.youngBonusDamage.get() : ConfigHandler.SERVER.babyBonusDamage.get()) : 0;

		return new AttributeModifier(DAMAGE_MODIFIER_UUID, "Dragon Damage Adjustment", ageBonus, Operation.ADDITION);
	}

	public static AttributeModifier buildSwimSpeedMod(DragonType dragonType){
		return new AttributeModifier(SWIM_SPEED_MODIFIER_UUID, "Dragon Swim Speed Adjustment", dragonType == DragonType.SEA && ConfigHandler.SERVER.seaSwimmingBonuses.get() ? 1 : 0, Operation.ADDITION);
	}

	public static void updateModifiers(PlayerEntity oldPlayer, PlayerEntity newPlayer){
		if(!DragonUtils.isDragon(newPlayer)){
			return;
		}

		AttributeModifier oldMod = getHealthModifier(oldPlayer);
		if(oldMod != null){
			updateHealthModifier(newPlayer, oldMod);
		}
		oldMod = getDamageModifier(oldPlayer);
		if(oldMod != null){
			updateDamageModifier(newPlayer, oldMod);
		}
		oldMod = getSwimSpeedModifier(oldPlayer);
		if(oldMod != null){
			updateSwimSpeedModifier(newPlayer, oldMod);
		}
		oldMod = getReachModifier(oldPlayer);
		if(oldMod != null){
			updateReachModifier(newPlayer, oldMod);
		}
	}

	@Nullable
	public static AttributeModifier getReachModifier(PlayerEntity player){
		return Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get())).getModifier(REACH_MODIFIER_UUID);
	}

	@Nullable
	public static AttributeModifier getHealthModifier(PlayerEntity player){
		return Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).getModifier(HEALTH_MODIFIER_UUID);
	}

	@Nullable
	public static AttributeModifier getDamageModifier(PlayerEntity player){
		return Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).getModifier(DAMAGE_MODIFIER_UUID);
	}

	@Nullable
	public static AttributeModifier getSwimSpeedModifier(PlayerEntity player){
		return Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get())).getModifier(SWIM_SPEED_MODIFIER_UUID);
	}

	public static void updateReachModifier(PlayerEntity player, AttributeModifier mod){
		if(!ConfigHandler.SERVER.bonuses.get()){
			return;
		}
		ModifiableAttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get()));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateHealthModifier(PlayerEntity player, AttributeModifier mod){
		if(!ConfigHandler.SERVER.healthAdjustments.get()){
			return;
		}
		float oldMax = player.getMaxHealth();
		ModifiableAttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
		float newHealth = player.getHealth() * player.getMaxHealth() / oldMax;
		player.setHealth(newHealth);
	}

	public static void updateDamageModifier(PlayerEntity player, AttributeModifier mod){
		if(!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.attackDamage.get()){
			return;
		}
		ModifiableAttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateSwimSpeedModifier(PlayerEntity player, AttributeModifier mod){
		if(!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.seaSwimmingBonuses.get()){
			return;
		}
		ModifiableAttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get()));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}
}