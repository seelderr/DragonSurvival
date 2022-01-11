package by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.handlers.DragonSizeHandler;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class DragonHitBox extends Entity
{
	public static final DataParameter<Integer> PLAYER_ID = EntityDataManager.defineId(DragonHitBox.class, DataSerializers.INT);
	
	private final DragonHitboxPart[] subEntities;
	private final DragonHitboxPart head;
	private final DragonHitboxPart body;
	private final DragonHitboxPart tail1;
	private final DragonHitboxPart tail2;
	private final DragonHitboxPart tail3;
	
	private double lastSize;
	
	public DragonHitBox(EntityType<?> p_i48580_1_, World p_i48580_2_)
	{
		super(p_i48580_1_, p_i48580_2_);
		
		this.head = new DragonHitboxPart(this, "head", 0.5F, 0.5F);
		this.body = new DragonHitboxPart(this, "body", 3.0F, 3.0F);
		this.tail1 = new DragonHitboxPart(this, "tail1", 3.0F, 3.0F);
		this.tail2 = new DragonHitboxPart(this, "tail2", 3.0F, 3.0F);
		this.tail3 = new DragonHitboxPart(this, "tail3", 3.0F, 3.0F);
		
		this.subEntities = new DragonHitboxPart[]{this.head, this.body, this.tail1, this.tail2, this.tail3};
	}
	@Override
	protected void defineSynchedData() {
		this.entityData.define(PLAYER_ID, -1);
	}
	
	public int getPlayerId(){
		return this.entityData.get(PLAYER_ID);
	}
	
	public void setPlayerId(int id){
		this.entityData.set(PLAYER_ID, id);
	}
	
	
	public PlayerEntity player;
	
	@Override
	public boolean save(CompoundNBT p_70039_1_)
	{
		return false;
	}
	
	
	@Override
	public void tick()
	{
		super.tick();
		
		if(tickCount > 20) {
			if (player == null) {
				if(getPlayerId() != -1){
					Entity ent = level.getEntity(getPlayerId());
					
					if(ent instanceof PlayerEntity){
						player = (PlayerEntity)ent;
					}
				}else {
					if (!level.isClientSide) {
						this.remove();
					}
				}
				return;
			}else if(player.isDeadOrDying() || !DragonStateProvider.isDragon(player)){
				if (!level.isClientSide) {
					this.remove();
				}
			}
		}else{
			return;
		}
		
		Vector3d[] avector3d = new Vector3d[this.subEntities.length];
		
		for(int j = 0; j < this.subEntities.length; ++j) {
			avector3d[j] = new Vector3d(this.subEntities[j].getX(), this.subEntities[j].getY(), this.subEntities[j].getZ());
		}
		
		DragonStateHandler handler = DragonStateProvider.getCap(player).orElse(null);
		
		copyPosition(player);
		
		if(handler == null || handler.getMovementData() == null) return;
		Vector3f offset = DragonStateProvider.getCameraOffset(player);
		
		double headRot = handler.getMovementData().headYaw;
		double pitch = handler.getMovementData().headPitch*-1;
		Vector3f bodyRot = DragonStateProvider.getCameraOffset(player);
		
		bodyRot = new Vector3f(bodyRot.x() / 2, bodyRot.y() / 2, bodyRot.z() / 2);
		
		Point2D result = new Point2D.Double();
		Point2D result2 = new Point2D.Double();
		
		{
			Point2D point = new Double(player.position().x() + bodyRot.x(), player.position().y() + player.getEyeHeight());
			AffineTransform transform = new AffineTransform();
			double angleInRadians = ((MathHelper.clamp(pitch, -90, 90) * -1) * Math.PI / 360);
			transform.rotate(angleInRadians, player.position().x(), player.position().y() + player.getEyeHeight());
			transform.transform(point, result);
		}
		
		{
			Point2D point2 = new Double(player.position().x() + bodyRot.x(), player.position().z() + bodyRot.z());
			AffineTransform transform2 = new AffineTransform();
			double angleInRadians2 = ((MathHelper.clamp(headRot, -180, 180) * -1) * Math.PI / 360);
			transform2.rotate(angleInRadians2, player.position().x(), player.position().z());
			transform2.transform(point2, result2);
		}
		
		double dx = result2.getX();
		double dy = result.getY() - (Math.abs(headRot) / 180 * .5);
		double dz = result2.getY();
		
		head.setPos(dx, dy - (DragonSizeHandler.calculateDragonWidth(handler.getSize(), ConfigHandler.SERVER.hitboxGrowsPastHuman.get()) / 2), dz);
		body.setPos(getX() - offset.x(), getY(), getZ() - offset.z());
		tail1.setPos(getX() - offset.x() * 2, getY() + (player.getEyeHeight() / 2), getZ() - offset.z() * 2);
		tail2.setPos(getX() - offset.x() * 3, getY() + (player.getEyeHeight() / 2), getZ() - offset.z() * 3);
		tail3.setPos(getX() - offset.x() * 4, getY() + (player.getEyeHeight() / 2), getZ() - offset.z() * 4);
		
		double size = handler.getSize();

		if(lastSize != size) {
			body.setPose(body.getPose() == Pose.STANDING ? null : Pose.STANDING);
			head.setPose(head.getPose() == Pose.STANDING ? null : Pose.STANDING);
			tail1.setPose(tail1.getPose() == Pose.STANDING ? null : Pose.STANDING);
			tail2.setPose(tail2.getPose() == Pose.STANDING ? null : Pose.STANDING);
			tail3.setPose(tail3.getPose() == Pose.STANDING ? null : Pose.STANDING);
			lastSize = size;
		}
		
		for(int l = 0; l < this.subEntities.length; ++l) {
			this.subEntities[l].xo = avector3d[l].x;
			this.subEntities[l].yo = avector3d[l].y;
			this.subEntities[l].zo = avector3d[l].z;
			this.subEntities[l].xOld = avector3d[l].x;
			this.subEntities[l].yOld = avector3d[l].y;
			this.subEntities[l].zOld = avector3d[l].z;
		}
	}
	
	public boolean hurt(DragonHitboxPart part, DamageSource source, float damage)
	{
		return hurt(source, damage);
	}
	
	@Override
	public boolean hurt(DamageSource source, float damage)
	{
		return player.hurt(source, damage);
	}
	
	public boolean isPickable() {
		return false;
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundNBT nb1t) {
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt) {
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	public boolean isMultipartEntity()
	{
		return true;
	}
	
	@Nullable
	@Override
	public PartEntity<?>[] getParts()
	{
		return subEntities;
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
	
	public boolean is(Entity entity) {
		return this == entity || entity instanceof DragonHitboxPart && ((DragonHitboxPart)entity).parentMob == this || entity == player;
	}
	
	@Override
	public boolean skipAttackInteraction(Entity p_85031_1_)
	{
		return super.skipAttackInteraction(p_85031_1_) || is(p_85031_1_);
	}
}
