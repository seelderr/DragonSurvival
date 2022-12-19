package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;


import by.dragonsurvivalteam.dragonsurvival.magic.abilities.ForestDragon.active.SpikeAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.util.TargetingFunctions;
import net.minecraft.network.protocol.Packet;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;


public class DragonSpikeEntity extends AbstractArrow{
	public static final EntityDataAccessor<Integer> ARROW_LEVEL = SynchedEntityData.defineId(DragonSpikeEntity.class, EntityDataSerializers.INT);

	public DragonSpikeEntity(Level p_i50172_2_){
		super(DSEntities.DRAGON_SPIKE, p_i50172_2_);
	}

	public DragonSpikeEntity(EntityType<? extends AbstractArrow> type, Level worldIn){
		super(type, worldIn);
	}

	public DragonSpikeEntity(EntityType<? extends AbstractArrow> type, Level world, LivingEntity entity){
		super(type, entity, world);
	}


	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(ARROW_LEVEL, 1);
	}


	protected void onHitEntity(EntityHitResult p_213868_1_){

		Entity entity = p_213868_1_.getEntity();
		Entity entity1 = this.getOwner();
		DamageSource damagesource;
		if(entity1 == null){
			damagesource = DamageSource.arrow(this, this);
		}else{
			damagesource = DamageSource.arrow(this, entity1);
			if(entity1 instanceof LivingEntity){
				((LivingEntity)entity1).setLastHurtMob(entity);
			}
		}
		float damage = (float)getBaseDamage();

		if(TargetingFunctions.attackTargets(getOwner(), ent -> ent.hurt(damagesource, damage), entity)){
			if(entity instanceof LivingEntity livingentity){
				if(!this.level.isClientSide){
					livingentity.setArrowCount(livingentity.getArrowCount() + 1);
				}

				if(!this.level.isClientSide && entity1 instanceof LivingEntity){
					EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
					EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity);
				}

				this.doPostHurtEffects(livingentity);

				if(entity1 != null && livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()){
					((ServerPlayer)entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
				}
			}

			if(this.getPierceLevel() <= 0){
				this.remove(RemovalReason.DISCARDED);
			}
		}else{
			this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
			this.setYRot(getYRot() + 180.0F);
			this.yRotO += 180.0F;

			if(!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D){
				this.remove(RemovalReason.DISCARDED);
			}
		}
	}

	@Override
	public double getBaseDamage(){
		return getArrow_level() * SpikeAbility.spikeDamage;
	}

	public int getArrow_level(){
		return this.entityData.get(ARROW_LEVEL);
	}

	public void setArrow_level(int arrow_level){
		this.entityData.set(ARROW_LEVEL, arrow_level);
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
	public Packet<?> getAddEntityPacket(){

		return NetworkHooks.getEntitySpawningPacket(this);
	}
}