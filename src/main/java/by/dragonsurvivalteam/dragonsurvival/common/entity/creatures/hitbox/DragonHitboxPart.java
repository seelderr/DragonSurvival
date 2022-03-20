package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.entity.PartEntity;

public class DragonHitboxPart extends PartEntity<DragonHitBox>{
	public final DragonHitBox parentMob;
	public EntitySize size;
	public String name;

	public DragonHitboxPart(DragonHitBox parent, String name, float sizeX, float sizeY){
		super(parent);
		this.size = EntitySize.scalable(sizeX, sizeY);
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
		return super.isInvulnerableTo(pSource) || pSource == DamageSource.IN_WALL;
	}

	public EntitySize getDimensions(Pose p_213305_1_){
		return this.size;
	}

	@Override
	public Vector3d getDeltaMovement(){
		return parentMob.getDeltaMovement();
	}

	@Override
	public boolean isPickable(){
		return parentMob.isPickable();
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT p_70037_1_){}

	@Override
	protected void addAdditionalSaveData(CompoundNBT p_213281_1_){}
}