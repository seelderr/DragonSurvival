package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Item.class )
public class MixinItem{
	@ModifyExpressionValue (method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEdible()Z"))
	private boolean dragonIsEdible(boolean original, Level level, Player player, InteractionHand hand){
		return original && DragonStateProvider.getCap(player).map(dragonStateHandler -> !dragonStateHandler.isDragon() || DragonFoodHandler.isDragonEdible((Item)(Object)this, dragonStateHandler.getType())).orElse(true);
	}

	@ModifyExpressionValue (method ="use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canEat(Z)Z"))
	private boolean dragonCanEat(boolean original, Level level, Player player, InteractionHand hand){
		return original && DragonStateProvider.getCap(player).map(dragonStateHandler -> !dragonStateHandler.isDragon() || player.canEat(DragonFoodHandler.getDragonFoodProperties((Item)(Object)this, dragonStateHandler.getType()).canAlwaysEat())).orElse(true);
	}

	@ModifyExpressionValue (method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"))
	private boolean dragonFinishUsingItem(boolean original, ItemStack item, Level level, LivingEntity player){
		return original && DragonStateProvider.getCap(player).map(dragonStateHandler -> !dragonStateHandler.isDragon() || DragonFoodHandler.isDragonEdible((Item)(Object)this, dragonStateHandler.getType())).orElse(true);
	}
}