package by.dragonsurvivalteam.dragonsurvival.common.entity.projectiles;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DSEntities;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DragonSpikeEntity extends AbstractArrowEntity{
	public static final DataParameter<Integer> ARROW_LEVEL = EntityDataManager.defineId(DragonSpikeEntity.class, DataSerializers.INT);

	public DragonSpikeEntity(World p_i50172_2_){
		super(DSEntities.DRAGON_SPIKE, p_i50172_2_);
	}

	public DragonSpikeEntity(EntityType<? extends AbstractArrowEntity> type, World worldIn){
		super(type, worldIn);
	}

	public DragonSpikeEntity(EntityType<? extends AbstractArrowEntity> type, World world, LivingEntity entity){
		super(type, entity, world);
	}

	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(ARROW_LEVEL, 1);
	}

	protected void onHitEntity(EntityRayTraceResult p_213868_1_){
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

		if(entity.hurt(damagesource, damage)){
			if(entity instanceof LivingEntity){
				LivingEntity livingentity = (LivingEntity)entity;
				if(!this.level.isClientSide){
					livingentity.setArrowCount(livingentity.getArrowCount() + 1);
				}

				if(!this.level.isClientSide && entity1 instanceof LivingEntity){
					EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
					EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity);
				}

				this.doPostHurtEffects(livingentity);
				if(entity1 != null && livingentity != entity1 && livingentity instanceof PlayerEntity && entity1 instanceof ServerPlayerEntity && !this.isSilent()){
					((ServerPlayerEntity)entity1).connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.ARROW_HIT_PLAYER, 0.0F));
				}
			}

			if(this.getPierceLevel() <= 0){
				this.remove();
			}
		}else{
			this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
			this.yRot += 180.0F;
			this.yRotO += 180.0F;
			if(!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D){
				this.remove();
			}
		}
	}

	@Override
	protected void onHitBlock(BlockRayTraceResult p_230299_1_){
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
	public double getBaseDamage(){
		return getArrow_level() * ConfigHandler.SERVER.spikeDamage.get();
	}

	public int getArrow_level(){
		return this.entityData.get(ARROW_LEVEL);
	}

	@Override
	public boolean isCritArrow(){
		return false;
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void setArrow_level(int arrow_level){
		this.entityData.set(ARROW_LEVEL, arrow_level);
	}
}