package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class DragonModifiers{
	public static final UUID REACH_MODIFIER_UUID = UUID.fromString("7455d5c7-4e1f-4cca-ab46-d79353764020");
	public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("03574e62-f9e4-4f1b-85ad-fde00915e446");
	public static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("5bd3cebc-132e-4f9d-88ef-b686c7ad1e2c");
	public static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("2a9341f3-d19e-446c-924b-7cf2e5259e10");
	public static final UUID ATTACK_RANGE_MODIFIER_UUID = UUID.fromString("a2e9a028-4bef-48d4-a25b-9cfdcac99480");
	public static final UUID STEP_HEIGHT_MODIFIER_UUID = UUID.fromString("f3b0b3e3-3b7d-4b1b-8f3d-3b7d4b1b8f3d");

	public static AttributeModifier buildHealthMod(double size){
		double healthModifier;
		if(ServerConfig.allowLargeScaling && size > ServerConfig.maxHealthSize) {
			double healthModifierPercentage = Math.max(1.0, (size - ServerConfig.maxHealthSize) / (ServerConfig.maxGrowthSize - DragonLevel.ADULT.size));
			healthModifier = Mth.lerp(healthModifierPercentage, ServerConfig.maxHealth, ServerConfig.largeMaxHealth) - 20;
		}
		else {
			double healthModifierPercentage = Math.max(1.0, (size - DragonLevel.NEWBORN.size) / (ServerConfig.maxHealthSize - DragonLevel.NEWBORN.size));
			healthModifier = Mth.lerp(healthModifierPercentage, ServerConfig.minHealth, ServerConfig.maxHealth) - 20;
		}
		return new AttributeModifier(HEALTH_MODIFIER_UUID, "Dragon Health Adjustment", healthModifier, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier buildReachMod(double size){
		double reachModifier;
		if(ServerConfig.allowLargeScaling && size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
			reachModifier = ServerConfig.reachBonus + ServerConfig.largeReachScalar * (size / ServerConfig.DEFAULT_MAX_GROWTH_SIZE);
		}
		else {
			reachModifier = Math.max(ServerConfig.reachBonus, (size - DragonLevel.NEWBORN.size) / (ServerConfig.DEFAULT_MAX_GROWTH_SIZE - DragonLevel.NEWBORN.size) * ServerConfig.reachBonus);
		}
		return new AttributeModifier(REACH_MODIFIER_UUID, "Dragon Reach Adjustment", reachModifier, Operation.MULTIPLY_BASE);
	}
	
	public static AttributeModifier buildAttackRangeMod(double size) {
		double rangeMod;
		if(ServerConfig.allowLargeScaling && size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
			rangeMod = ServerConfig.attackRangeBonus + ServerConfig.largeReachScalar * (size / ServerConfig.DEFAULT_MAX_GROWTH_SIZE);
		}
		else {
			rangeMod = Math.max(ServerConfig.attackRangeBonus, (size - DragonLevel.NEWBORN.size) / (ServerConfig.DEFAULT_MAX_GROWTH_SIZE - DragonLevel.NEWBORN.size) * ServerConfig.attackRangeBonus);
		}
		return new AttributeModifier(ATTACK_RANGE_MODIFIER_UUID, "Dragon Attack Range Adjustment", rangeMod, Operation.MULTIPLY_BASE);
	}

	public static AttributeModifier buildDamageMod(DragonStateHandler handler, boolean isDragon){
		double ageBonus = isDragon ? handler.getLevel() == DragonLevel.ADULT ? ServerConfig.adultBonusDamage : handler.getLevel() == DragonLevel.YOUNG ? ServerConfig.youngBonusDamage : ServerConfig.babyBonusDamage : 0;
		if(ServerConfig.allowLargeScaling && handler.getSize() > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
			double damageModPercentage = Math.max(1.0, (handler.getSize() - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / (ServerConfig.maxGrowthSize - ServerConfig.DEFAULT_MAX_GROWTH_SIZE));
			ageBonus = Mth.lerp(damageModPercentage, ageBonus, ServerConfig.largeDamageBonus);
		}
		return new AttributeModifier(DAMAGE_MODIFIER_UUID, "Dragon Damage Adjustment", ageBonus, Operation.ADDITION);
	}

	public static AttributeModifier buildSwimSpeedMod(AbstractDragonType dragonType){
		return new AttributeModifier(SWIM_SPEED_MODIFIER_UUID, "Dragon Swim Speed Adjustment", Objects.equals(dragonType, DragonTypes.SEA) && ServerConfig.seaSwimmingBonuses ? 1 : 0, Operation.ADDITION);
	}

	public static AttributeModifier buildStepHeightMod(double size) {
		double stepHeightBonus = 0;
		if(size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE && ServerConfig.allowLargeScaling)  {
			stepHeightBonus = ServerConfig.largeStepHeightScalar * (size - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE;
		}
		return new AttributeModifier(STEP_HEIGHT_MODIFIER_UUID, "Dragon Step Height Adjustment", stepHeightBonus, Operation.ADDITION);
	}

	public static void updateModifiers(Player oldPlayer, Player newPlayer){
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
		oldMod = getAttackRangeModifier(oldPlayer);
		if (oldMod != null){
			updateAttackRangeModifier(newPlayer, oldMod);
		}
		oldMod = getStepHeightModifier(oldPlayer);
		if (oldMod != null){
			updateStepHeightModifier(newPlayer, oldMod);
		}
	}

	@Nullable
	public static AttributeModifier getReachModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get())).getModifier(REACH_MODIFIER_UUID);
	}

	@Nullable
	public static AttributeModifier getHealthModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).getModifier(HEALTH_MODIFIER_UUID);
	}

	@Nullable
	public static AttributeModifier getDamageModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).getModifier(DAMAGE_MODIFIER_UUID);
	}

	@Nullable
	public static AttributeModifier getSwimSpeedModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get())).getModifier(SWIM_SPEED_MODIFIER_UUID);
	}
	
	@Nullable
	public static AttributeModifier getAttackRangeModifier(Player player) {
		return Objects.requireNonNull(player.getAttribute(ForgeMod.ATTACK_RANGE.get())).getModifier(ATTACK_RANGE_MODIFIER_UUID);
	}

	@Nullable
	public static AttributeModifier getStepHeightModifier(Player player) {
		return Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).getModifier(STEP_HEIGHT_MODIFIER_UUID);
	}

	public static void updateReachModifier(Player player, AttributeModifier mod){
		if(!ServerConfig.bonuses){
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get()));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}
	
	public static void updateAttackRangeModifier(Player player, AttributeModifier mod) {
		if(!ServerConfig.bonuses) {
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.ATTACK_RANGE.get()));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateHealthModifier(Player player, AttributeModifier mod){
		if(!ServerConfig.healthAdjustments){
			return;
		}
		float oldMax = player.getMaxHealth();
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
		float newHealth = player.getHealth() * player.getMaxHealth() / oldMax;
		player.setHealth(newHealth);
	}

	public static void updateDamageModifier(Player player, AttributeModifier mod){
		if(!ServerConfig.bonuses || !ServerConfig.attackDamage){
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateSwimSpeedModifier(Player player, AttributeModifier mod){
		if(!ServerConfig.bonuses || !ServerConfig.seaSwimmingBonuses){
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get()));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateStepHeightModifier(Player player, AttributeModifier mod) {
		if(!ServerConfig.bonuses) {
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get()));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}
}