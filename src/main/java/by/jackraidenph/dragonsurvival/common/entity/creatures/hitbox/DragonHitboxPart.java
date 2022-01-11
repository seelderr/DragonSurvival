package by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.entity.PartEntity;

public class DragonHitboxPart extends PartEntity<DragonHitBox>
{
	public EntitySize size;
	public final DragonHitBox parentMob;
	public String name;
	
	public DragonHitboxPart(DragonHitBox parent, String name, float sizeX, float sizeY)
	{
		super(parent);
		this.size = EntitySize.scalable(sizeX, sizeY);
		this.parentMob = parent;
		this.name = name;
	}
	
	@Override
	protected void defineSynchedData() {}
	
	@Override
	protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {}
	
	@Override
	protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {}
	
	public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
		return !this.isInvulnerableTo(p_70097_1_) && this.parentMob.hurt(this, p_70097_1_, p_70097_2_);
	}
	
	@Override
	public boolean skipAttackInteraction(Entity p_85031_1_)
	{
		return super.skipAttackInteraction(p_85031_1_) || is(p_85031_1_);
	}
	
	public boolean is(Entity entity) {
		return this == entity || this.parentMob == entity || this.parentMob.player == entity || this.getParent().getPlayerId() == entity.getId();
	}
	
	
	public IPacket<?> getAddEntityPacket() {
		throw new UnsupportedOperationException();
	}
	
	public EntitySize getDimensions(Pose p_213305_1_) {
		return this.size;
	}
	@Override
	public boolean isColliding(BlockPos p_242278_1_, BlockState p_242278_2_)
	{
		return false;
	}
	
	@Override
	public boolean canCollideWith(Entity p_241849_1_)
	{
		return false;
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}
	
	@Override
	public void checkDespawn() {}
	
	@Override
	public boolean isPushable()
	{
		return false;
	}
	
	@Override
	public boolean isPushedByFluid()
	{
		return false;
	}
	
	
}
