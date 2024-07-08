package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.capability.objects.DragonMovementData;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Objects;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Entity.class )
public abstract class MixinEntity implements ICapabilityProvider<Entity, Void, DragonStateHandler> {
	@Inject( at = @At( value = "HEAD" ), method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", cancellable = true )
	private void positionRider(Entity entity, Entity.MoveFunction move, CallbackInfo callbackInfo){
		Object self = this;

		if(DragonStateProvider.isDragon((Entity) self)){
			if(hasPassenger(entity)) {
				if ((Entity) self instanceof Player player && entity instanceof Player passenger) {
					DragonMovementData md = DragonStateProvider.getOrGenerateHandler((Entity) self).getMovementData();

					Vec3 originalPassPos = player.getPassengerRidingPosition((Entity) self);
					double size = DragonStateProvider.getOrGenerateHandler(entity).getSize();
					double heightOffset = size > ServerConfig.DEFAULT_MAX_GROWTH_SIZE ? -0.55 : -0.15 - size / DragonLevel.ADULT.size * 0.2;

					Vec3 offsetFromBb = new Vec3(0, heightOffset, -1.4 * player.getBbWidth());
					Vec3 offsetFromCenter = originalPassPos.subtract(player.position());
					offsetFromCenter = offsetFromCenter.xRot((float) Math.toRadians(md.prevXRot * 1.5)).zRot(-(float) Math.toRadians(md.prevZRot * 90));
					//offsetFromCenter = offsetFromCenter.multiply(1, Math.signum(offsetFromCenter.y), 1);
					Vec3 totalOffset = offsetFromCenter.add(offsetFromBb).yRot(-(float) Math.toRadians(md.bodyYawLastFrame));
					Vec3 passPos = player.position().add(totalOffset);
					move.accept(passenger, passPos.x(), passPos.y(), passPos.z());

					((Entity) self).onPassengerTurned(passenger);
					callbackInfo.cancel();
				}
			}
		}
	}
	
	@Inject(method = "onPassengerTurned(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
	private void onPassengerTurned(Entity passenger, CallbackInfo callbackInfo) {
		if (passenger instanceof Player player && player.getVehicle() != null && DragonStateProvider.getOrGenerateHandler(player.getVehicle()).isDragon() && player.level().isClientSide()) {
			this.dragonSurvival$clampRotation(passenger);
		}
	}
	
	@Unique private void dragonSurvival$clampRotation(Entity passenger) {
		Entity self = (Entity)(Object) this;
		DragonStateHandler selfHandler = DragonStateProvider.getOrGenerateHandler(self);
		DragonMovementData selfmd = selfHandler.getMovementData();
		if (DragonStateProvider.isDragon(passenger)) {
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(passenger);
			DragonMovementData md = handler.getMovementData();
			float facing = (float) Mth.wrapDegrees(passenger.getYRot() - selfmd.bodyYawLastFrame);
			float facingClamped = Mth.clamp(facing, -150.0F, 150.0F);
			passenger.yRotO += facingClamped - facing + self.yRotO;
			md.bodyYaw = selfmd.bodyYawLastFrame;
			md.headYaw = -facing;
			passenger.setYRot((float) (passenger.getYRot() + facingClamped - facing + (selfmd.bodyYawLastFrame - selfmd.bodyYaw)));
			if (passenger instanceof DragonEntity de) {
				de.prevZRot = ((DragonEntity) self).prevZRot;
			}
		}
		else {
			float facing = (float) Mth.wrapDegrees(passenger.getYRot() - selfmd.bodyYawLastFrame);
			float facingClamped = Mth.clamp(facing, -120.0F, 120.0F);
			passenger.yRotO += facingClamped - facing + self.yRotO;
			passenger.setYBodyRot((float) (passenger.getYRot() + facingClamped - facing + (selfmd.bodyYawLastFrame - selfmd.bodyYaw)));
			passenger.setYRot((float) (passenger.getYRot() + facingClamped - facing + (selfmd.bodyYawLastFrame - selfmd.bodyYaw)));
			passenger.setYHeadRot(passenger.getYRot());
		}
	}

	@Shadow
	public boolean hasPassenger(Entity p_184196_1_){
		throw new IllegalStateException("Mixin failed to shadow hasPassenger()");
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

	@Inject( at = @At( value = "HEAD" ), method = "Lnet/minecraft/world/entity/Entity;isVisuallyCrawling()Z", cancellable = true )
	public void isDragonVisuallyCrawling(CallbackInfoReturnable<Boolean> ci){
		if(DragonStateProvider.isDragon((Entity)(Object)this)){
			ci.setReturnValue(false);
		}
	}

	@ModifyReturnValue( at = @At( value = "RETURN" ), method = "canRide")
	public boolean canRide(boolean original, @Local(argsOnly = true, ordinal = 0) Entity entity){
		if(DragonStateProvider.isDragon((Entity)(Object)this) && !DragonStateProvider.isDragon(entity)){
			if(ServerConfig.ridingBlacklist){
				return ServerConfig.allowedVehicles.contains(ResourceHelper.getKey(entity).toString());
			}
		}

		return original;
	}

	// TODO: I don't think this code is necessary. Will leave it here for a bit and if there are no issues will fully remove it.
	/*@Redirect( method = "canEnterPose(Lnet/minecraft/g/entity/Pose;)Z", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBoxForPose(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/phys/AABB;" ) )
	public AABB dragonPoseBB(Entity entity, Pose pose){
		if(DragonStateProvider.isDragon(entity) && ServerConfig.sizeChangesHitbox){
			boolean squish = DragonUtils.getDragonBody(entity) != null ? DragonUtils.getDragonBody(entity).isSquish() : false;
			double heightMult = 1.0;
			if (DragonUtils.getDragonBody(entity) != null) {
				squish = DragonUtils.getDragonBody(entity).isSquish();
				heightMult = DragonUtils.getDragonBody(entity).getHeightMult();
			}
			double size = DragonStateProvider.getOrGenerateHandler(entity).getSize();
			double height = DragonSizeHandler.calculateModifiedHeight(DragonSizeHandler.calculateDragonHeight(size, ServerConfig.hitboxGrowsPastHuman), pose, ServerConfig.sizeChangesHitbox, squish) * heightMult;
			double width = DragonSizeHandler.calculateDragonWidth(size, ServerConfig.hitboxGrowsPastHuman) / 2.0D;
			return DragonSizeHandler.calculateDimensions(width, height).makeBoundingBox(entity.position());
		}else
			return getBoundingBoxForPose(pose);
	}

	@Shadow
	protected AABB getBoundingBoxForPose(Pose pose){
		throw new IllegalStateException("Mixin failed to shadow getBoundingBoxForPose()");
	}*/
}