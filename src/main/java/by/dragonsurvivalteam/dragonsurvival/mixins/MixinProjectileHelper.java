package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin( ProjectileUtil.class )
public abstract class MixinProjectileHelper{

	// This hack exists because a normal inject at head was causing JAVA crashes. ¯\_(ツ)_/¯
	@Redirect( method = "getEntityHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;F)Lnet/minecraft/world/phys/EntityHitResult;", at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBoundingBox()Lnet/minecraft/world/phys/AABB;" ) )
	private static AABB dragonEntityHitboxHack(Entity entity){
		LocalPlayer player = Minecraft.getInstance().player;
		if(DragonStateProvider.getCap(player).isPresent() && entity instanceof Player){
			DragonStateHandler cap = DragonUtils.getHandler(player);
			if((player.getRootVehicle() == entity.getRootVehicle() && !entity.canRiderInteract()) || (cap.isDragon() && entity.getId() == cap.getPassengerId())){
				return new AABB(0, -1000, 0, 0, -1000, 0);
			}
		}
		return entity.getBoundingBox();
	}
}