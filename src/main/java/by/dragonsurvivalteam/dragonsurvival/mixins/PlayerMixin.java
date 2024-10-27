package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Objects;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers.SLOW_FALLING;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
	/** Add -0.07 to 0.08, so we get the vanilla default of 0.01 */
	@Unique private final static AttributeModifier dragonSurvival$SLOW_FALL_MOD = new AttributeModifier(SLOW_FALLING, -0.07, AttributeModifier.Operation.ADD_VALUE);

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
	public void isInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> callback) {
		if (ServerConfig.disableSuffocation && source == damageSources().inWall() && DragonStateProvider.isDragon(this)) {
			callback.setReturnValue(true);
		}
	}

	@Inject(method = "isImmobile", at = @At("HEAD"), cancellable = true)
	private void castMovement(CallbackInfoReturnable<Boolean> callback) {
		DragonStateHandler handler = DragonStateProvider.getData((Player) (Object) this);

		if (!isDeadOrDying() && !isSleeping()) {
			if (!ServerConfig.canMoveWhileCasting) {
				ActiveDragonAbility casting = handler.getMagicData().getCurrentlyCasting();

				if (casting != null && casting.requiresStationaryCasting()) {
					callback.setReturnValue(true);
				}
			}

			if (!ServerConfig.canMoveInEmote && Arrays.stream(handler.getEmoteData().currentEmotes).noneMatch(Objects::nonNull)) {
				callback.setReturnValue(true);
			}
		}
	}

	/** Allow treasure blocks to trigger sleep logic */
	@Inject(method = "isSleepingLongEnough", at = @At("HEAD"), cancellable = true)
	public void isSleepingLongEnough(CallbackInfoReturnable<Boolean> callback) {
		DragonStateProvider.getOptional(this).ifPresent(handler -> {
			if (handler.isDragon() && handler.treasureResting && handler.treasureSleepTimer >= 100) {
				callback.setReturnValue(true);
			}
		});
	}
}