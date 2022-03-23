package by.dragonsurvivalteam.dragonsurvival.common.entity.creatures.hitbox;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class DragonHitBox extends MobEntity{
	public static final DataParameter<Integer> PLAYER_ID = EntityDataManager.defineId(DragonHitBox.class, DataSerializers.INT);

	private final DragonHitboxPart[] subEntities;
	private final DragonHitboxPart head;
	private final DragonHitboxPart tail1;
	private final DragonHitboxPart tail2;
	private final DragonHitboxPart tail3;
	private final DragonHitboxPart tail4;
	private final DragonHitboxPart tail5;
	public EntitySize size;
	public PlayerEntity player;
	private double lastSize;
	private Pose lastPose;

	public DragonHitBox(EntityType<? extends MobEntity> p_i48577_1_, World p_i48580_2_){
		super(p_i48577_1_, p_i48580_2_);

		this.head = new DragonHitboxPart(this, "head", 0.5F, 0.5F);
		this.tail1 = new DragonHitboxPart(this, "tail1", 1.0F, 1.0F);
		this.tail2 = new DragonHitboxPart(this, "tail2", 1.0F, 1.0F);
		this.tail3 = new DragonHitboxPart(this, "tail3", 1.0F, 1.0F);
		this.tail4 = new DragonHitboxPart(this, "tail4", 1.0F, 1.0F);
		this.tail5 = new DragonHitboxPart(this, "tail5", 1.0F, 1.0F);

		this.subEntities = new DragonHitboxPart[]{this.head, this.tail1, this.tail2, this.tail3, this.tail4, this.tail5};

		this.size = EntitySize.scalable(1f, 1f);
		this.refreshDimensions();
	}

	@Override
	protected void defineSynchedData(){
		super.defineSynchedData();
		this.entityData.define(PLAYER_ID, -1);
	}

	@Override
	public void tick(){
		super.tick();
		this.checkInsideBlocks();
	}

	@Override
	public void aiStep(){
		if(level.isClientSide && tickCount > 20){
			if(player == null){
				if(getPlayerId() != -1){
					Entity ent = level.getEntity(getPlayerId());

					if(ent instanceof PlayerEntity){
						player = (PlayerEntity)ent;
					}
				}
				return;
			}
		}else{
			if(player == null || player.isDeadOrDying() || !DragonUtils.isDragon(player)){
				if(!level.isClientSide){
					this.remove();
				}
			}
		}

		if(player == null){
			return;
		}

		DragonStateHandler handler = DragonUtils.getHandler(player);

		if(handler.getMovementData() == null){
			return;
		}

		Vector3f offset = DragonUtils.getCameraOffset(player);

		double size = handler.getSize();
		double height = DragonSizeHandler.calculateDragonHeight(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());
		double width = DragonSizeHandler.calculateDragonWidth(size, ConfigHandler.SERVER.hitboxGrowsPastHuman.get());

		Pose overridePose = DragonSizeHandler.overridePose(player);
		height = DragonSizeHandler.calculateModifiedHeight(height, overridePose, true);

		double headRot = handler.getMovementData().headYaw;
		double pitch = handler.getMovementData().headPitch * -1;
		Vector3f bodyRot = DragonUtils.getCameraOffset(player);

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

		if(lastSize != size || lastPose != overridePose){
			this.size = EntitySize.scalable((float)width * 1.6f, (float)height);
			refreshDimensions();

			head.size = EntitySize.scalable((float)width, (float)width);
			head.refreshDimensions();

			tail1.size = EntitySize.scalable((float)width, (float)height / 3);
			tail1.refreshDimensions();

			tail2.size = EntitySize.scalable((float)width * 0.8f, (float)height / 3);
			tail2.refreshDimensions();

			tail3.size = EntitySize.scalable((float)width * 0.7f, (float)height / 3);
			tail3.refreshDimensions();

			tail4.size = EntitySize.scalable((float)width * 0.7f, (float)height / 3);
			tail4.refreshDimensions();

			tail5.size = EntitySize.scalable((float)width * 0.7f, (float)height / 3);
			tail5.refreshDimensions();

			lastSize = size;
			lastPose = overridePose;
			player.refreshDimensions();
		}else{
			setPos(player.getX() - offset.x(), player.getY(), player.getZ() - offset.z());
			xRot = (float)handler.getMovementData().headPitch;
			yRot = (float)handler.getMovementData().bodyYaw;

			//			double bodyYawChange = Functions.angleDifference((float)handler.getMovementData().bodyYawLastTick, (float)handler.getMovementData().bodyYaw);
			//
			//			ModifiableAttributeInstance gravity = player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
			//			double g = gravity.getValue();
			//
			//			double tailMotionUp = ServerFlightHandler.isFlying(player) ? 0 : (player.getDeltaMovement().y + g);
			//			double tailMotionSide = MathHelper.lerp(0.1, MathHelper.clamp(bodyYawChange, -50, 50), 0);
			//

			head.setPos(dx, dy - (DragonSizeHandler.calculateDragonWidth(handler.getSize(), ConfigHandler.SERVER.hitboxGrowsPastHuman.get()) / 2), dz);
			tail1.setPos(getX() - offset.x(), getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z());
			tail2.setPos(getX() - offset.x() * 1.5, getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z() * 1.5);
			tail3.setPos(getX() - offset.x() * 2, getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z() * 2);
			tail4.setPos(getX() - offset.x() * 2.5, getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z() * 2.5);
			tail5.setPos(getX() - offset.x() * 3, getY() + (player.getEyeHeight() / 2) - (height / 9), getZ() - offset.z() * 3);
		}
	}

	public int getPlayerId(){
		return this.entityData.get(PLAYER_ID);
	}

	public void setPlayerId(int id){
		this.entityData.set(PLAYER_ID, id);
	}

	public boolean hurt(DragonHitboxPart part, DamageSource source, float damage){
		return hurt(source, damage);
	}

	@Override
	public boolean hurt(DamageSource source, float damage){
		if(source.getEntity() == player || source.getDirectEntity() == player) return false;
		return player != null && !this.isInvulnerableTo(source) && player.hurt(source, damage);
	}

	@Override
	public boolean isPickable(){
		return level.isClientSide && !isOwner();
	}

	@OnlyIn( Dist.CLIENT )
	public boolean isOwner(){
		return player == Minecraft.getInstance().player;
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public EntitySize getDimensions(Pose pPose){
		return size;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource pSource){
		return super.isInvulnerableTo(pSource) || pSource == DamageSource.IN_WALL || pSource == DamageSource.LIGHTNING_BOLT || player.isInvulnerableTo(pSource);
	}

	@Override
	public int getAirSupply(){
		return getMaxAirSupply();
	}

	@Override
	public Vector3d getDeltaMovement(){
		return player != null ? player.getDeltaMovement() : super.getDeltaMovement();
	}

	@Override
	public boolean isMultipartEntity(){
		return true;
	}

	@Nullable
	@Override
	public PartEntity<?>[] getParts(){
		return subEntities;
	}

	@Override
	public boolean fireImmune(){
		return player != null ? player.fireImmune() : super.fireImmune();
	}

	@Override
	public boolean isOnFire(){
		return false;
	}

	public boolean is(Entity entity){
		return this == entity || entity.getId() == getPlayerId() || player != null && entity.getId() == player.getId();
	}
}