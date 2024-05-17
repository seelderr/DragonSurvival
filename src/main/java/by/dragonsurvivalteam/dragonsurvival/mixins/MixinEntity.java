package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonMovementData;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.mojang.math.Vector3f;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin( Entity.class )
public abstract class MixinEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity>{
	@Shadow
	private EntityDimensions dimensions;
	@Shadow 
	public abstract void onPassengerTurned(Entity $$0);

	protected MixinEntity(Class<Entity> baseClass){
		super(baseClass);
	}

	@Inject( at = @At( value = "HEAD" ), method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", cancellable = true )
	private void positionRider(Entity entity, Entity.MoveFunction move, CallbackInfo callbackInfo){
		Object self = this;

		if(DragonUtils.isDragon((Entity) self)){
			if(hasPassenger(entity)) {
				if ((Object)this instanceof Player player && entity instanceof Player passenger) {
					Vec3 offset = new Vec3(0, this.getPassengersRidingOffset(), -1.0);
					offset = offset.yRot(
							(float) Math.toRadians(-DragonUtils.getHandler((Entity) self).getMovementData().bodyYaw)
						).zRot(
							(float) Math.toRadians(DragonUtils.getHandler((Entity) self).getMovementData().prevZRot)
						);//.xRot(
						//	(float) Math.toRadians(DragonUtils.getHandler((Entity) self).getMovementData().prevXRot)
						//);
					//Vector3f cameraOffset = Functions.getDragonCameraOffset((Entity) self);
					//offset.add(new Vec3(cameraOffset).reverse());
					Vec3 passPos = player.position().add(offset);
					//System.out.println(offset);
					//System.out.println("" + DragonUtils.getHandler((Entity) self).getMovementData().bodyYaw + " and " + DragonUtils.getHandler((Entity) self).getMovementData().prevZRot);
					//passPos = passPos.add(-cameraOffset.x(), 0, -cameraOffset.z());
					move.accept(passenger, passPos.x(), passPos.y(), passPos.z());
					//double d0 = getY() + getPassengersRidingOffset() + entity.getMyRidingOffset();
					
					//move.accept(entity, getX() - cameraOffset.x(), d0, getZ() - cameraOffset.z());
					((Entity)(Object)this).onPassengerTurned(passenger);
					callbackInfo.cancel();
				}
			}
		}
	}
	
	@Inject(method = "onPassengerTurned(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
	private void onPassengerTurned(Entity passenger, CallbackInfo callbackInfo) {
		this.clampRotation(passenger);
	}
	
	private void clampRotation(Entity passenger) {
		Entity self = (Entity)(Object) this;
		DragonStateHandler selfHandler = DragonUtils.getHandler(self);
		DragonMovementData selfmd = selfHandler.getMovementData();
		if (DragonUtils.isDragon(passenger)) {
			DragonStateHandler handler = DragonUtils.getHandler(passenger);
			DragonMovementData md = handler.getMovementData();
			float facing = (float) Mth.wrapDegrees(passenger.getYRot() - selfmd.bodyYawLastTick);
			float facingClamped = Mth.clamp(facing, -150.0F, 150.0F);
			passenger.yRotO += facingClamped - facing + self.yRotO;
			//System.out.println("p.yRotO: " + passenger.yRotO + ", s.yRotO: " + self.yRotO + ", p.yRot: " + passenger.yRot + ", s.yRot: " + self.yRot);
			handler.setMovementData(selfmd.bodyYawLastTick, -facing, md.headPitchLastTick, md.bite);
			passenger.setYRot(passenger.getYRot() + facingClamped - facing + (self.yRot - self.yRotO));
			passenger.setYHeadRot(passenger.getYRot() + facingClamped - facing + (self.yRot - self.yRotO));
			if (passenger instanceof DragonEntity de) {
				de.prevZRot = ((DragonEntity) self).prevZRot;
			}
			
		}
		else {
			float facing = (float) Mth.wrapDegrees(passenger.getYRot() - selfmd.bodyYawLastTick);
			float facingClamped = Mth.clamp(facing, -30.0F, 30.0F);
			passenger.yRotO += facingClamped - facing + self.yRotO;
			passenger.setYBodyRot(passenger.getYRot() + facingClamped - facing + (self.yRot - self.yRotO));
			passenger.setYRot(passenger.getYRot() + facingClamped - facing + (self.yRot - self.yRotO));
			passenger.setYHeadRot(passenger.getYRot());
		}
	}

	@Shadow
	public boolean hasPassenger(Entity p_184196_1_){
		throw new IllegalStateException("Mixin failed to shadow hasPassenger()");
	}

	@Shadow
	public double getPassengersRidingOffset(){
		throw new IllegalStateException("Mixin failed to shadow getPassengersRidingOffset()");
	}

	@Shadow
	public double getX(){
		throw new IllegalStateException("Mixin failed to shadow getX()");
	}

	@Shadow
	public double getY(){
		throw new IllegalStateException("Mixin failed to shadow getY()");
	}

	@Shadow
	public double getZ(){
		throw new IllegalStateException("Mixin failed to shadow getZ()");
	}

	@Inject( at = @At( value = "HEAD" ), method = "Lnet/minecraft/world/entity/Entity;displayFireAnimation()Z", cancellable = true )
	private void hideCaveDragonFireAnimation(CallbackInfoReturnable<Boolean> ci){
		DragonStateProvider.getCap((Entity)(Object)this).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.isDragon() && Objects.equals(dragonStateHandler.getType(), DragonTypes.CAVE)){
				ci.setReturnValue(false);
			}
		});
	}

	@Inject( at = @At( value = "HEAD" ), method = "Lnet/minecraft/world/entity/Entity;getPassengersRidingOffset()D", cancellable = true )
	public void getDragonPassengersRidingOffset(CallbackInfoReturnable<Double> ci){
		if(DragonUtils.isDragon((Entity)(Object)this)){
			//DragonStateHandler handler = DragonUtils.getHandler((Entity)(Object)this);
			double height = DragonSizeHandler.getDragonHeight((Player)(Object)this);
			switch(((Entity)(Object)this).getPose()){
				case FALL_FLYING, SWIMMING, SPIN_ATTACK -> ci.setReturnValue((double)height * 0.65D);
				case CROUCHING -> ci.setReturnValue((double)height * 0.48D);
				default -> ci.setReturnValue((double)height * 0.52D);
			}
		}
	}

	@Inject( at = @At( value = "HEAD" ), method = "Lnet/minecraft/world/entity/Entity;isVisuallyCrawling()Z", cancellable = true )
	public void isDragonVisuallyCrawling(CallbackInfoReturnable<Boolean> ci){
		if(DragonUtils.isDragon((Entity)(Object)this)){
			ci.setReturnValue(false);
		}
	}

	@Inject( at = @At( value = "RETURN" ), method = "canRide", cancellable = true )
	public void canRide(Entity entity, CallbackInfoReturnable<Boolean> ci){
		if(ci.getReturnValue() && DragonUtils.isDragon((Entity)(Object)this) && !DragonUtils.isDragon(entity)){
			if(ServerConfig.ridingBlacklist){
				ci.setReturnValue(ServerConfig.allowedVehicles.contains(ResourceHelper.getKey(entity).toString()));
			}
		}
	}

	@Redirect( method = "canEnterPose(Lnet/minecraft/world/entity/Pose;)Z", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBoxForPose(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/phys/AABB;" ) )
	public AABB dragonPoseBB(Entity entity, Pose pose){
		if(DragonUtils.isDragon(entity) && ServerConfig.sizeChangesHitbox){
			boolean squish = false;
			double heightMult = 1.0;
			if (DragonUtils.getDragonBody(entity) != null) {
				squish = DragonUtils.getDragonBody(entity).isSquish();
				heightMult = DragonUtils.getDragonBody(entity).getHeightMult();
			}
			double size = DragonUtils.getHandler(entity).getSize();
			double height = DragonSizeHandler.calculateModifiedHeight(DragonSizeHandler.calculateDragonHeight(size, ServerConfig.hitboxGrowsPastHuman), pose, ServerConfig.sizeChangesHitbox, squish) * heightMult;
			double width = DragonSizeHandler.calculateDragonWidth(size, ServerConfig.hitboxGrowsPastHuman) / 2.0D;
			return DragonSizeHandler.calculateDimensions(width, height).makeBoundingBox(entity.position());
		}else
			return getBoundingBoxForPose(pose);
	}

	@ModifyVariable(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "STORE"), name = "itementity")
	public ItemEntity protectDrops(ItemEntity itemEntity) {
		Object self = this;

		if (self instanceof LivingEntity livingEntity) {
			if (!(livingEntity.level instanceof ServerLevel)) {
				return itemEntity;
			}

			if (livingEntity.lastHurtByPlayerTime > 0) {
				Player player = livingEntity.lastHurtByPlayer;

				// Prevent the dropped item from burning when player is a cave dragon
				if (DragonUtils.isDragonType(player, DragonTypes.CAVE)) {
					itemEntity = new ItemEntity(livingEntity.level, itemEntity.position().x, itemEntity.position().y, itemEntity.position().z, itemEntity.getItem()) {
						@Override
						public boolean fireImmune(){
							return true;
						}
					};

					return itemEntity;
				}
			}
		}

		return itemEntity;
	}

	@Shadow
	protected AABB getBoundingBoxForPose(Pose pose){
		throw new IllegalStateException("Mixin failed to shadow getBoundingBoxForPose()");
	}
}