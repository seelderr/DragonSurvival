package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( EnchantmentHelper.class )
public class MixinEnchantmentHelper{
	@Inject( at = @At( "HEAD" ), method = "hasAquaAffinity", cancellable = true )
	private static void hasAquaAffinity(LivingEntity entity, CallbackInfoReturnable<Boolean> ci){
		if(!(entity instanceof PlayerEntity)){
			return;
		}

		PlayerEntity player = (PlayerEntity)entity;

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.getType() == DragonType.SEA){
				ci.setReturnValue(true);
			}
		});
	}

	@Inject( at = @At( "HEAD" ), method = "getEnchantmentLevel", cancellable = true )
	private static void getEnchantmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> ci){
		if(!entity.getMainHandItem().isEmpty()){
			if(entity.getMainHandItem().getItem() instanceof TieredItem){
				return;
			}
		}

		if(DragonUtils.isDragon(entity)){
			DragonStateHandler handler = DragonStateProvider.getCap(entity).orElse(null);

			if(handler != null){
				int highestLevel = 0;
				for(int i = 0; i < 4; i++){
					ItemStack stack = handler.getClawInventory().getClawsInventory().getItem(i);

					if(!stack.isEmpty()){
						int lev = EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);

						if(lev > highestLevel){
							highestLevel = lev;
						}
					}
				}

				ci.setReturnValue(highestLevel);
			}
		}
	}
}