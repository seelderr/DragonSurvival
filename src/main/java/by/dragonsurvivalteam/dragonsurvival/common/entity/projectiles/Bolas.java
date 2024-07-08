package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncFlyingStatus;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import static by.dragonsurvivalteam.dragonsurvival.registry.DSModifiers.SLOW_MOVEMENT;

public class Bolas extends ThrowableItemProjectile{

	public Bolas(Level world){
		super(DSEntities.BOLAS_ENTITY.get(), world);
	}

	public Bolas(double p_i50156_2_, double p_i50156_4_, double p_i50156_6_, Level world){
		super(DSEntities.BOLAS_ENTITY.get(), p_i50156_2_, p_i50156_4_, p_i50156_6_, world);
	}

	public Bolas(LivingEntity shooter, Level world){
		super(DSEntities.BOLAS_ENTITY.get(), shooter, world);
	}


	@Override
	protected Item getDefaultItem(){
		return DSItems.HUNTING_NET.value();
	}

	@Override
	protected void onHit(HitResult p_70227_1_){
		super.onHit(p_70227_1_);
		if(!level().isClientSide()){
			remove(RemovalReason.DISCARDED);
		}
	}

	protected void onHitEntity(EntityHitResult entityHitResult){
		Entity entity = entityHitResult.getEntity();
		if(!entity.level().isClientSide()){
			if(entity instanceof LivingEntity living){
				AttributeInstance movementSpeed = living.getAttribute(Attributes.MOVEMENT_SPEED);
				AttributeModifier bolasTrap = new AttributeModifier(SLOW_MOVEMENT, -movementSpeed.getValue() / 2.f, AttributeModifier.Operation.ADD_VALUE);
				boolean addEffect = false;
				if(!movementSpeed.hasModifier(SLOW_MOVEMENT)){
					movementSpeed.addTransientModifier(bolasTrap);
					addEffect = true;
				}

				if(entity instanceof Player player) {
					DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(player);
					if(handler.isDragon()){
						handler.setWingsSpread(false);
						PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncFlyingStatus.Data(player.getId(), false));
					}
				}

				// There are some missing effects here, since they are handled elsewhere:
				// -The player can't jump (EventHandler.java)
				// -The player can't activate their wings (ClientFlightHandler.java)

				if(addEffect){
					living.addEffect(new MobEffectInstance(DSEffects.TRAPPED, Functions.secondsToTicks(ServerConfig.hunterTrappedDebuffDuration)));
				}
			}
		}
	}
}