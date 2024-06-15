package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( Item.class )
public class MixinItem{

	@ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getFoodProperties(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"))
	private FoodProperties replaceFoodPropertiesInUse(FoodProperties original, Level pLevel, Player pPlayer, InteractionHand pUsedHand, @Local ItemStack itemStack){
		if(DragonStateProvider.isDragon(pPlayer))
		{
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(pPlayer);
			return DragonFoodHandler.getDragonFoodProperties(itemStack.getItem(), handler.getType());
		}

		return original;
	}

	@ModifyExpressionValue(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getFoodProperties(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"))
	private FoodProperties replaceFoodPropertiesInFinishUseItem(FoodProperties original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) ItemStack itemStack){
		if(DragonStateProvider.isDragon(entity))
		{
			DragonStateHandler handler = DragonStateProvider.getOrGenerateHandler(entity);
			return DragonFoodHandler.getDragonFoodProperties(itemStack.getItem(), handler.getType());
		}

		return original;
	}
}