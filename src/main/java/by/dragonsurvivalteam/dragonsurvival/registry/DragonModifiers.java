package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public class DragonModifiers{
	public static final UUID REACH_MODIFIER_UUID = UUID.fromString("7455d5c7-4e1f-4cca-ab46-d79353764020");
	public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("03574e62-f9e4-4f1b-85ad-fde00915e446");
	public static final UUID DAMAGE_MODIFIER_UUID = UUID.fromString("5bd3cebc-132e-4f9d-88ef-b686c7ad1e2c");
	public static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("2a9341f3-d19e-446c-924b-7cf2e5259e10");
	public static final UUID ATTACK_RANGE_MODIFIER_UUID = UUID.fromString("a2e9a028-4bef-48d4-a25b-9cfdcac99480");

	public static AttributeModifier buildHealthMod(double size){
		double healthMod = (float)ServerConfig.minHealth + (size - 14) / 26F * ((float)ServerConfig.maxHealth - (float)ServerConfig.minHealth) - 20;
		healthMod = Math.min(healthMod, ServerConfig.maxHealth - 20);


		return new AttributeModifier(HEALTH_MODIFIER_UUID, "Dragon Health Adjustment", healthMod, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier buildReachMod(double size){
		double reachMod = (size - DragonLevel.NEWBORN.size) / (60.0 - DragonLevel.NEWBORN.size) * ServerConfig.reachBonus;

		return new AttributeModifier(REACH_MODIFIER_UUID, "Dragon Reach Adjustment", reachMod, Operation.MULTIPLY_BASE);
	}
	
	public static AttributeModifier buildAttackRangeMod(double size) {
		double rangeMod = (size - DragonLevel.NEWBORN.size) / (60.0 - DragonLevel.NEWBORN.size) * ServerConfig.attackRangeBonus;
		
		return new AttributeModifier(ATTACK_RANGE_MODIFIER_UUID, "Dragon Attack Range Adjustment", rangeMod, Operation.MULTIPLY_BASE);
	}

	public static AttributeModifier buildDamageMod(DragonStateHandler handler, boolean isDragon){
		double ageBonus = isDragon ? handler.getLevel() == DragonLevel.ADULT ? ServerConfig.adultBonusDamage : handler.getLevel() == DragonLevel.YOUNG ? ServerConfig.youngBonusDamage : ServerConfig.babyBonusDamage : 0;

		return new AttributeModifier(DAMAGE_MODIFIER_UUID, "Dragon Damage Adjustment", ageBonus, Operation.ADDITION);
	}

	public static AttributeModifier buildSwimSpeedMod(AbstractDragonType dragonType){
		return new AttributeModifier(SWIM_SPEED_MODIFIER_UUID, "Dragon Swim Speed Adjustment", Objects.equals(dragonType, DragonTypes.SEA) && ServerConfig.seaSwimmingBonuses ? 1 : 0, Operation.ADDITION);
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
		oldMod = getBlockReachModifier(oldPlayer);
		if(oldMod != null){
			updateBlockReachModifier(newPlayer, oldMod);
		}
		oldMod = getEntityReachModifier(oldPlayer);
		if(oldMod != null){
			updateEntityReachModifier(newPlayer, oldMod);
		}
	}

	public static AttributeModifier getBlockReachModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(ForgeMod.BLOCK_REACH.get())).getModifier(REACH_MODIFIER_UUID);
	}

	public static AttributeModifier getEntityReachModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_REACH.get())).getModifier(REACH_MODIFIER_UUID);
	}

	public static AttributeModifier getHealthModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).getModifier(HEALTH_MODIFIER_UUID);
	}

	public static AttributeModifier getDamageModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).getModifier(DAMAGE_MODIFIER_UUID);
	}

	public static AttributeModifier getSwimSpeedModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(ForgeMod.SWIM_SPEED.get())).getModifier(SWIM_SPEED_MODIFIER_UUID);
	}

	public static void updateBlockReachModifier(Player player, AttributeModifier mod){
		if(!ServerConfig.bonuses){
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(ForgeMod.BLOCK_REACH.get()));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateEntityReachModifier(final Player player, final AttributeModifier modifier) {
		if (!ServerConfig.bonuses) {
			return;
		}

		AttributeInstance attribute = Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_REACH.get()));
		attribute.removeModifier(modifier);
		attribute.addPermanentModifier(modifier);
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
}