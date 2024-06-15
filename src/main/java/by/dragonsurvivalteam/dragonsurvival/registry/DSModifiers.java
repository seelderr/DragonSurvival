package by.dragonsurvivalteam.dragonsurvival.registry;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

import static by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod.MODID;

public class DSModifiers {
	public static final ResourceLocation DRAGON_REACH_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_reach_modifier");
	public static final ResourceLocation DRAGON_HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_health_modifier");
	public static final ResourceLocation DRAGON_DAMAGE_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_damage_modifier");
	public static final ResourceLocation DRAGON_SWIM_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_swim_speed_modifier");
	public static final ResourceLocation DRAGON_STEP_HEIGHT_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_step_height_modifier");
	public static final ResourceLocation DRAGON_MOVEMENT_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_movement_speed_modifier");
	public static final ResourceLocation DRAGON_JUMP_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_jump_bonus");
	public static final ResourceLocation DRAGON_SAFE_FALL_DISTANCE = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_safe_fall_distance");

	public static final ResourceLocation DRAGON_BODY_MOVEMENT_SPEED = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_movement_speed");
	public static final ResourceLocation DRAGON_BODY_ARMOR = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_armor");
	public static final ResourceLocation DRAGON_BODY_STRENGTH = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_strength");
	public static final ResourceLocation DRAGON_BODY_STRENGTH_MULT = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_strength_mult");
	public static final ResourceLocation DRAGON_BODY_KNOCKBACK_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_knockback_bonus");
	public static final ResourceLocation DRAGON_BODY_SWIM_SPEED_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_swim_speed_bonus");
	public static final ResourceLocation DRAGON_BODY_STEP_HEIGHT_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_step_height_bonus");
	public static final ResourceLocation DRAGON_BODY_GRAVITY_MULT = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_gravity_mult");
	public static final ResourceLocation DRAGON_BODY_HEALTH_MULT = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_health_mult");
	public static final ResourceLocation DRAGON_BODY_JUMP_BONUS = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_jump_bonus");
	public static final ResourceLocation DRAGON_BODY_SAFE_FALL_DISTANCE = ResourceLocation.fromNamespaceAndPath(MODID, "dragon_body_safe_fall_distance");

	// Used in MixinPlayerEntity to add the slow falling effect to dragons
	public static final ResourceLocation SLOW_FALLING = ResourceLocation.fromNamespaceAndPath(MODID, "slow_falling");

	// Used in EmoteHandler to keep track of the no move state
	public static final ResourceLocation EMOTE_NO_MOVE = ResourceLocation.fromNamespaceAndPath(MODID, "emote_no_move");

	public static AttributeModifier buildHealthMod(double size){
		double healthModifier;
		if(ServerConfig.allowLargeScaling && size > ServerConfig.maxHealthSize) {
			double healthModifierPercentage = Math.min(1.0, (size - ServerConfig.maxHealthSize) / (ServerConfig.maxGrowthSize - DragonLevel.ADULT.size));
			healthModifier = Mth.lerp(healthModifierPercentage, ServerConfig.maxHealth, ServerConfig.largeMaxHealth) - 20;
		}
		else {
			double healthModifierPercentage = Math.min(1.0, (size - DragonLevel.NEWBORN.size) / (ServerConfig.maxHealthSize - DragonLevel.NEWBORN.size));
			healthModifier = Mth.lerp(healthModifierPercentage, ServerConfig.minHealth, ServerConfig.maxHealth) - 20;
		}
		return new AttributeModifier(DRAGON_HEALTH_MODIFIER, healthModifier, Operation.ADD_VALUE);
	}

	public static AttributeModifier buildReachMod(double size){
		double reachModifier;
		if(ServerConfig.allowLargeScaling && size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
			reachModifier = ServerConfig.reachBonus + ServerConfig.largeReachScalar * (size / ServerConfig.DEFAULT_MAX_GROWTH_SIZE);
		}
		else {
			reachModifier = Math.max(ServerConfig.reachBonus, (size - DragonLevel.NEWBORN.size) / (ServerConfig.DEFAULT_MAX_GROWTH_SIZE - DragonLevel.NEWBORN.size) * ServerConfig.reachBonus);
		}
		return new AttributeModifier(DRAGON_REACH_MODIFIER, reachModifier, Operation.ADD_MULTIPLIED_BASE);
	}

	public static AttributeModifier buildDamageMod(DragonStateHandler handler, boolean isDragon){
		double ageBonus = isDragon ? handler.getLevel() == DragonLevel.ADULT ? ServerConfig.adultBonusDamage : handler.getLevel() == DragonLevel.YOUNG ? ServerConfig.youngBonusDamage : ServerConfig.babyBonusDamage : 0;
		if(ServerConfig.allowLargeScaling && handler.getSize() > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
			double damageModPercentage = Math.min(1.0, (handler.getSize() - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / (ServerConfig.maxGrowthSize - ServerConfig.DEFAULT_MAX_GROWTH_SIZE));
			ageBonus = Mth.lerp(damageModPercentage, ageBonus, ServerConfig.largeDamageBonus);
		}
		return new AttributeModifier(DRAGON_DAMAGE_MODIFIER, ageBonus, Operation.ADD_VALUE);
	}

	public static AttributeModifier buildSwimSpeedMod(AbstractDragonType dragonType){
		return new AttributeModifier(DRAGON_SWIM_SPEED_MODIFIER, Objects.equals(dragonType, DragonTypes.SEA) && ServerConfig.seaSwimmingBonuses ? 1 : 0, Operation.ADD_VALUE);
	}

	public static AttributeModifier buildStepHeightMod(DragonStateHandler handler, double size) {
		double stepHeightBonus = handler.getLevel() == DragonLevel.ADULT ? ServerConfig.adultStepHeight : handler.getLevel() == DragonLevel.YOUNG ? ServerConfig.youngStepHeight : ServerConfig.newbornStepHeight;
		if(size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE && ServerConfig.allowLargeScaling)  {
			stepHeightBonus += ServerConfig.largeStepHeightScalar * (size - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE;
		}
		return new AttributeModifier(DRAGON_STEP_HEIGHT_MODIFIER, stepHeightBonus, Operation.ADD_VALUE);
	}

	public static AttributeModifier buildMovementSpeedMod(DragonStateHandler handler, double size) {
		double moveSpeedMultiplier = 1;
		if(handler.getLevel() == DragonLevel.NEWBORN) {
			double youngPercent = Math.min(1.0, (size - DragonLevel.NEWBORN.size) / (DragonLevel.YOUNG.size - DragonLevel.NEWBORN.size));
			moveSpeedMultiplier = Mth.lerp(youngPercent, ServerConfig.moveSpeedNewborn, ServerConfig.moveSpeedYoung);
		} else if(handler.getLevel() == DragonLevel.YOUNG) {
			double adultPercent = Math.min(1.0, (size - DragonLevel.YOUNG.size) / (DragonLevel.ADULT.size - DragonLevel.YOUNG.size));
			moveSpeedMultiplier = Mth.lerp(adultPercent, ServerConfig.moveSpeedYoung, ServerConfig.moveSpeedAdult);
		} else if(handler.getLevel() == DragonLevel.ADULT) {
			if(ServerConfig.allowLargeScaling && size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
				moveSpeedMultiplier = ServerConfig.moveSpeedAdult + ServerConfig.largeMovementSpeedScalar * (size - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE;
			} else {
				moveSpeedMultiplier = ServerConfig.moveSpeedAdult;
			}
		}
		return new AttributeModifier(DRAGON_MOVEMENT_SPEED_MODIFIER, moveSpeedMultiplier - 1, Operation.ADD_MULTIPLIED_TOTAL);
	}

	private static double calculateJumpMod(DragonStateHandler handler) {
		double jumpBonus = 0;
		if (handler.getBody() != null) {
			jumpBonus = handler.getBody().getJumpBonus();
			if (ServerConfig.allowLargeScaling && handler.getSize() > ServerConfig.DEFAULT_MAX_GROWTH_SIZE) {
				jumpBonus += ServerConfig.largeJumpHeightScalar * (handler.getSize() - ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / ServerConfig.DEFAULT_MAX_GROWTH_SIZE;
			}
		}
		switch(handler.getLevel()){
			case NEWBORN -> jumpBonus += ServerConfig.newbornJump; //1+ block
			case YOUNG -> jumpBonus += ServerConfig.youngJump; //1.5+ block
			case ADULT -> jumpBonus += ServerConfig.adultJump; //2+ blocks
		}
		return jumpBonus;
	}

	public static AttributeModifier buildJumpMod(DragonStateHandler handler) {
		return new AttributeModifier(DRAGON_JUMP_BONUS, calculateJumpMod(handler), Operation.ADD_VALUE);
	}

	public static AttributeModifier buildSafeFallDistanceMod(DragonStateHandler handler) {
		// TODO: Not really sure about why this magic number is needed?
		return new AttributeModifier(DRAGON_SAFE_FALL_DISTANCE, calculateJumpMod(handler) * 4, Operation.ADD_VALUE);
	}

	public static void updateModifiers(Player player) {
		updateTypeModifiers(player);
		updateSizeModifiers(player);
		updateBodyModifiers(player);
	}

	public static void updateTypeModifiers(final Player player) {
		if (DragonStateProvider.getCap(player).isPresent()) {
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
			if (handler.isDragon()) {
				// Grant the dragon attribute modifiers
				AttributeModifier swimSpeed = buildSwimSpeedMod(handler.getType());
				updateSwimSpeedModifier(player, swimSpeed);
			} else {
				// Remove the dragon attribute modifiers
				AttributeModifier oldMod = getSwimSpeedModifier(player);
				if (oldMod != null) {
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(NeoForgeMod.SWIM_SPEED));
					max.removeModifier(oldMod);
				}
			}
		}
	}

	public static void updateSizeModifiers(final Player player) {
		if (DragonStateProvider.getCap(player).isPresent()) {
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
			if (handler.isDragon()) {
				// Grant the dragon attribute modifiers
				double size = handler.getSize();
				AttributeModifier health = buildHealthMod(size);
				updateHealthModifier(player, health);

				AttributeModifier damage = buildDamageMod(handler, handler.isDragon());
				updateDamageModifier(player, damage);

				AttributeModifier reach = buildReachMod(size);
				updateReachModifier(player, reach);

				AttributeModifier stepHeight = buildStepHeightMod(handler, size);
				updateStepHeightModifier(player, stepHeight);

				AttributeModifier moveSpeed = buildMovementSpeedMod(handler, size);
				updateMovementSpeedModifier(player, moveSpeed);

				AttributeModifier jumpMod = buildJumpMod(handler);
				updateJumpModifier(player, jumpMod);

				AttributeModifier safeFallDistance = buildSafeFallDistanceMod(handler);
				updateSafeFallDistanceModifier(player, safeFallDistance);
			} else {
				// Remove the dragon attribute modifiers
				AttributeModifier oldMod = getHealthModifier(player);
				if (oldMod != null) {
					float oldMax = player.getMaxHealth();
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH));
					max.removeModifier(oldMod);
					float newHealth = player.getHealth() * player.getMaxHealth() / oldMax;
					player.setHealth(newHealth);
				}

				oldMod = getDamageModifier(player);
				if (oldMod != null) {
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE));
					max.removeModifier(oldMod);
				}

				oldMod = getBlockReachModifier(player);
				if (oldMod != null) {
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE));
					max.removeModifier(oldMod);
				}

				oldMod = getEntityReachModifier(player);
				if (oldMod != null) {
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE));
					max.removeModifier(oldMod);
				}

				oldMod = getStepHeightModifier(player);
				if (oldMod != null) {
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.STEP_HEIGHT));
					max.removeModifier(oldMod);
				}

				oldMod = getMovementSpeedModifier(player);
				if (oldMod != null) {
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED));
					max.removeModifier(oldMod);
				}

				oldMod = getJumpModifier(player);
				if (oldMod != null) {
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.JUMP_STRENGTH));
					max.removeModifier(oldMod);
				}

				oldMod = getSafeFallDistanceModifier(player);
				if (oldMod != null) {
					AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.SAFE_FALL_DISTANCE));
					max.removeModifier(oldMod);
				}
			}
		}
	}

	public static void updateBodyModifiers(Player player) {
		AbstractDragonBody body = DragonUtils.getDragonBody(player);
		boolean isDragon = DragonStateProvider.isDragon(player);

		AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
		AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
		AttributeInstance strengthAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
		AttributeInstance attackKnockbackAttr = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
		AttributeInstance swimSpeedAttr = player.getAttribute(NeoForgeMod.SWIM_SPEED);
		AttributeInstance stepAttr = player.getAttribute(Attributes.STEP_HEIGHT);
		AttributeInstance gravityAttr = player.getAttribute(Attributes.GRAVITY);
		AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
		AttributeInstance jumpAttr = player.getAttribute(Attributes.JUMP_STRENGTH);
		AttributeInstance safeFallAttr = player.getAttribute(Attributes.SAFE_FALL_DISTANCE);

		if (body != null && isDragon) {
			if (speedAttr.getModifier(DRAGON_BODY_MOVEMENT_SPEED) == null || speedAttr.getModifier(DRAGON_BODY_MOVEMENT_SPEED).amount() != body.getRunMult()) {
				if (speedAttr.getModifier(DRAGON_BODY_MOVEMENT_SPEED) != null) { speedAttr.removeModifier(DRAGON_BODY_MOVEMENT_SPEED); }
				speedAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_MOVEMENT_SPEED, body.getRunMult() - 1, Operation.ADD_MULTIPLIED_TOTAL));
			}

			if (armorAttr.getModifier(DRAGON_BODY_ARMOR) == null || armorAttr.getModifier(DRAGON_BODY_ARMOR).amount() != body.getArmorBonus()) {
				if (armorAttr.getModifier(DRAGON_BODY_ARMOR) != null) { armorAttr.removeModifier(DRAGON_BODY_ARMOR); }
				armorAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_ARMOR, body.getArmorBonus(), Operation.ADD_VALUE));
			}

			if (strengthAttr.getModifier(DRAGON_BODY_STRENGTH) == null || strengthAttr.getModifier(DRAGON_BODY_STRENGTH).amount() != body.getDamageBonus()) {
				if (strengthAttr.getModifier(DRAGON_BODY_STRENGTH) != null) { strengthAttr.removeModifier(DRAGON_BODY_STRENGTH); }
				strengthAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_STRENGTH, body.getDamageBonus(), Operation.ADD_VALUE));
			}

			if (strengthAttr.getModifier(DRAGON_BODY_STRENGTH_MULT) == null || strengthAttr.getModifier(DRAGON_BODY_STRENGTH_MULT).amount() != (body.getDamageMult()  - 1)) {
				if (strengthAttr.getModifier(DRAGON_BODY_STRENGTH_MULT) != null) { strengthAttr.removeModifier(DRAGON_BODY_STRENGTH_MULT); }
				strengthAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_STRENGTH_MULT,body.getDamageMult() - 1, Operation.ADD_MULTIPLIED_TOTAL));
			}

			if (attackKnockbackAttr.getModifier(DRAGON_BODY_KNOCKBACK_BONUS) == null || attackKnockbackAttr.getModifier(DRAGON_BODY_KNOCKBACK_BONUS).amount() != body.getKnockbackBonus()) {
				if (attackKnockbackAttr.getModifier(DRAGON_BODY_KNOCKBACK_BONUS) != null) { attackKnockbackAttr.removeModifier(DRAGON_BODY_KNOCKBACK_BONUS); }
				attackKnockbackAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_KNOCKBACK_BONUS, body.getKnockbackBonus(), Operation.ADD_VALUE));
			}

			if (swimSpeedAttr.getModifier(DRAGON_BODY_SWIM_SPEED_BONUS) == null || swimSpeedAttr.getModifier(DRAGON_BODY_SWIM_SPEED_BONUS).amount() != body.getSwimSpeedBonus()) {
				if (swimSpeedAttr.getModifier(DRAGON_BODY_SWIM_SPEED_BONUS) != null) { swimSpeedAttr.removeModifier(DRAGON_BODY_SWIM_SPEED_BONUS); }
				swimSpeedAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_SWIM_SPEED_BONUS, body.getSwimSpeedBonus(), Operation.ADD_VALUE));
			}

			if (stepAttr.getModifier(DRAGON_BODY_STEP_HEIGHT_BONUS) == null || stepAttr.getModifier(DRAGON_BODY_STEP_HEIGHT_BONUS).amount() != body.getStepBonus()) {
				if (stepAttr.getModifier(DRAGON_BODY_STEP_HEIGHT_BONUS) != null) { stepAttr.removeModifier(DRAGON_BODY_STEP_HEIGHT_BONUS); }
				stepAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_STEP_HEIGHT_BONUS, body.getStepBonus(), Operation.ADD_VALUE));
			}

			if (gravityAttr.getModifier(DRAGON_BODY_GRAVITY_MULT) == null || gravityAttr.getModifier(DRAGON_BODY_GRAVITY_MULT).amount() != (body.getGravityMult() - 1)) {
				if (gravityAttr.getModifier(DRAGON_BODY_GRAVITY_MULT) != null) { gravityAttr.removeModifier(DRAGON_BODY_GRAVITY_MULT); }
				gravityAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_GRAVITY_MULT, body.getGravityMult() - 1, Operation.ADD_MULTIPLIED_TOTAL));
			}

			if (healthAttr.getModifier(DRAGON_BODY_HEALTH_MULT) == null || healthAttr.getModifier(DRAGON_BODY_HEALTH_MULT).amount() != (body.getHealthMult() - 1)) {
				if (healthAttr.getModifier(DRAGON_BODY_HEALTH_MULT) != null) { healthAttr.removeModifier(DRAGON_BODY_HEALTH_MULT); }
				healthAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_HEALTH_MULT, body.getHealthMult() - 1, Operation.ADD_MULTIPLIED_TOTAL));
			}

			if (jumpAttr.getModifier(DRAGON_BODY_JUMP_BONUS) == null || jumpAttr.getModifier(DRAGON_BODY_JUMP_BONUS).amount() != body.getJumpBonus()) {
				if (jumpAttr.getModifier(DRAGON_BODY_JUMP_BONUS) != null) { jumpAttr.removeModifier(DRAGON_BODY_JUMP_BONUS); }
				jumpAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_JUMP_BONUS, body.getJumpBonus(), Operation.ADD_VALUE));
			}

			if (safeFallAttr.getModifier(DRAGON_BODY_SAFE_FALL_DISTANCE) == null || safeFallAttr.getModifier(DRAGON_BODY_SAFE_FALL_DISTANCE).amount() != body.getJumpBonus()) {
				if (safeFallAttr.getModifier(DRAGON_BODY_SAFE_FALL_DISTANCE) != null) { safeFallAttr.removeModifier(DRAGON_BODY_SAFE_FALL_DISTANCE); }
				safeFallAttr.addTransientModifier(new AttributeModifier(DRAGON_BODY_SAFE_FALL_DISTANCE, body.getJumpBonus(), Operation.ADD_VALUE));
			}
		} else {
			speedAttr.removeModifier(DRAGON_BODY_MOVEMENT_SPEED);
			armorAttr.removeModifier(DRAGON_BODY_ARMOR);
			strengthAttr.removeModifier(DRAGON_BODY_STRENGTH);
			strengthAttr.removeModifier(DRAGON_BODY_STRENGTH_MULT);
			attackKnockbackAttr.removeModifier(DRAGON_BODY_KNOCKBACK_BONUS);
			swimSpeedAttr.removeModifier(DRAGON_BODY_SWIM_SPEED_BONUS);
			stepAttr.removeModifier(DRAGON_BODY_STEP_HEIGHT_BONUS);
			gravityAttr.removeModifier(DRAGON_BODY_GRAVITY_MULT);
			healthAttr.removeModifier(DRAGON_BODY_HEALTH_MULT);
			jumpAttr.removeModifier(DRAGON_BODY_JUMP_BONUS);
		}
	}

	@Nullable public static AttributeModifier getBlockReachModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE)).getModifier(DRAGON_REACH_MODIFIER);
	}

	@Nullable public static AttributeModifier getEntityReachModifier(Player player) {
		return Objects.requireNonNull(player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)).getModifier(DRAGON_REACH_MODIFIER);
	}

	@Nullable public static AttributeModifier getHealthModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(Attributes.MAX_HEALTH)).getModifier(DRAGON_HEALTH_MODIFIER);
	}

	@Nullable public static AttributeModifier getDamageModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).getModifier(DRAGON_DAMAGE_MODIFIER);
	}

	@Nullable public static AttributeModifier getSwimSpeedModifier(Player player){
		return Objects.requireNonNull(player.getAttribute(NeoForgeMod.SWIM_SPEED)).getModifier(DRAGON_SWIM_SPEED_MODIFIER);
	}

	@Nullable public static AttributeModifier getStepHeightModifier(Player player) {
		return Objects.requireNonNull(player.getAttribute(Attributes.STEP_HEIGHT)).getModifier(DRAGON_STEP_HEIGHT_MODIFIER);
	}

	@Nullable public static AttributeModifier getMovementSpeedModifier(Player player) {
		return Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).getModifier(DRAGON_MOVEMENT_SPEED_MODIFIER);
	}

	@Nullable public static AttributeModifier getJumpModifier(Player player) {
		return Objects.requireNonNull(player.getAttribute(Attributes.JUMP_STRENGTH)).getModifier(DRAGON_JUMP_BONUS);
	}

	@Nullable public static AttributeModifier getSafeFallDistanceModifier(Player player) {
		return Objects.requireNonNull(player.getAttribute(Attributes.SAFE_FALL_DISTANCE)).getModifier(DRAGON_SAFE_FALL_DISTANCE);
	}

	public static void updateReachModifier(Player player, AttributeModifier mod){
		if(!ServerConfig.bonuses){
			return;
		}
		AttributeInstance blockReach = Objects.requireNonNull(player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE));
		blockReach.removeModifier(mod);
		blockReach.addPermanentModifier(mod);

	AttributeInstance entityReach = Objects.requireNonNull(player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE));
		entityReach.removeModifier(mod);
		entityReach.addPermanentModifier(mod);
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
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(NeoForgeMod.SWIM_SPEED));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateStepHeightModifier(Player player, AttributeModifier mod) {
		if(!ServerConfig.bonuses) {
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.STEP_HEIGHT));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateMovementSpeedModifier(Player player, AttributeModifier mod) {
		if(!ServerConfig.bonuses) {
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateJumpModifier(Player player, AttributeModifier mod) {
		if(!ServerConfig.bonuses) {
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.JUMP_STRENGTH));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}

	public static void updateSafeFallDistanceModifier(Player player, AttributeModifier mod) {
		if(!ServerConfig.bonuses) {
			return;
		}
		AttributeInstance max = Objects.requireNonNull(player.getAttribute(Attributes.SAFE_FALL_DISTANCE));
		max.removeModifier(mod);
		max.addPermanentModifier(mod);
	}
}