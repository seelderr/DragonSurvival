package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active.SpikeAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;


public class DragonSpikeEntity extends AbstractArrow{
	public static final EntityDataAccessor<Integer> ARROW_LEVEL = SynchedEntityData.defineId(DragonSpikeEntity.class, EntityDataSerializers.INT);

	public DragonSpikeEntity(Level p_i50172_2_){
		super(DSEntities.DRAGON_SPIKE.get(), p_i50172_2_);
	}

	public DragonSpikeEntity(EntityType<? extends AbstractArrow> type, Level worldIn){
		super(type, worldIn);
	}

	public DragonSpikeEntity(EntityType<? extends AbstractArrow> type, Level world, LivingEntity entity){
		super(type, world, ItemStack.EMPTY);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder pBuilder){
		super.defineSynchedData(pBuilder);
		pBuilder.define(ARROW_LEVEL, 1);
	}


	@Override
	protected void onHitEntity(EntityHitResult p_213868_1_){
		Entity entity = p_213868_1_.getEntity();
		Entity entity1 = getOwner();
		DamageSource damagesource;
		if(entity1 == null){
			damagesource = damageSources().arrow(this, this);
		}else{
			damagesource = damageSources().arrow(this, entity1);
			if(entity1 instanceof LivingEntity livingEntity){
				livingEntity.setLastHurtMob(entity);
			}
		}
		float damage = (float)getBaseDamage();

		if(TargetingFunctions.attackTargets(getOwner(), ent -> ent.hurt(damagesource, damage), entity)){
			if(entity instanceof LivingEntity livingentity){
				if(!level().isClientSide()){
					livingentity.setArrowCount(livingentity.getArrowCount() + 1);
				}

				if(!level().isClientSide() && entity1 instanceof LivingEntity){
					EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
					EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity);
				}

				doPostHurtEffects(livingentity);

				if(entity1 instanceof ServerPlayer serverPlayer && !isSilent()){
					serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
				}
			}

			if(getPierceLevel() <= 0){
				remove(RemovalReason.DISCARDED);
			}
		}else{
			setDeltaMovement(getDeltaMovement().scale(-0.1D));
			setYRot(getYRot() + 180.0F);
			yRotO += 180.0F;

			if(!level().isClientSide() && getDeltaMovement().lengthSqr() < 1.0E-7D){
				remove(RemovalReason.DISCARDED);
			}
		}
	}

	@Override
	public double getBaseDamage(){
		return getArrow_level() * SpikeAbility.spikeDamage;
	}

	public int getArrow_level(){
		return entityData.get(ARROW_LEVEL);
	}

	public void setArrow_level(int arrow_level){
		entityData.set(ARROW_LEVEL, arrow_level);
	}

	@Override
	public boolean isCritArrow(){
		return false;
	}

	@Override
	protected void onHitBlock(BlockHitResult p_230299_1_){
		super.onHitBlock(p_230299_1_);
	}

	@Override

	protected SoundEvent getDefaultHitGroundSoundEvent(){
		return SoundEvents.AXE_STRIP;
	}

	@Override
	protected ItemStack getPickupItem(){
		return ItemStack.EMPTY;
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemStack.EMPTY;
	}
}