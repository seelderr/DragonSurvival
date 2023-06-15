package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( EnchantmentHelper.class )
public abstract class MixinEnchantmentHelper{
	@Inject( at = @At( "HEAD" ), method = "hasAquaAffinity", cancellable = true )
	private static void hasAquaAffinity(LivingEntity entity, CallbackInfoReturnable<Boolean> ci){
		if(!(entity instanceof Player player)){
			return;
		}
		
		if(DragonUtils.isDragonType(player, DragonTypes.SEA)){
			ci.setReturnValue(true);
		}
	}

	// TODO :: doPostDamageEffects & doPostHurtEffects

	@Inject( at = @At( "HEAD" ), method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", cancellable = true)
	private static void getEnchantmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> ci) {
		// FIXME :: Test with different enchantments
		if (!(enchantment.category == EnchantmentCategory.DIGGER || enchantment.category == EnchantmentCategory.WEAPON)) {
			return;
		}

		if(!entity.getMainHandItem().isEmpty()){
			if(entity.getMainHandItem().getItem() instanceof TieredItem){
				return;
			}
		}

		if(DragonUtils.isDragon(entity) && entity instanceof Player player){
			ItemStack stack = ClawToolHandler.getDragonTools(player);

			if(stack != null){
				ci.setReturnValue(stack.getEnchantmentLevel(enchantment));
			}
		}
	}
}