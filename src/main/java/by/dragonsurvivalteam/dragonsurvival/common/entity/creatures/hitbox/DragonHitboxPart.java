package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

public class DragonHitboxPart extends PartEntity<DragonHitBox>{
	public final by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox parentMob;
	public EntityDimensions size;
	public String name;

	public DragonHitboxPart(by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox parent, String name, float sizeX, float sizeY){
		super(parent);
		this.size = EntityDimensions.scalable(sizeX, sizeY);
		this.parentMob = parent;
		this.name = name;
	}

	@Override
	protected void defineSynchedData(){}

	public boolean hurt(DamageSource pSource, float pAmount){
		return !this.isInvulnerableTo(pSource) && this.parentMob.hurt(this, pSource, pAmount);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource pSource){
		return super.isInvulnerableTo(pSource) || pSource == DamageSource.IN_WALL || pSource == DamageSource.CRAMMING;
	}

	public EntityDimensions getDimensions(Pose p_213305_1_){
		return this.size;
	}

	@Override
	public Vec3 getDeltaMovement(){
		return parentMob.getDeltaMovement();
	}

	@Override
	public boolean isPickable(){
		return parentMob.isPickable();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag p_70037_1_){}

	@Override
	protected void addAdditionalSaveData(CompoundTag p_213281_1_){}
}