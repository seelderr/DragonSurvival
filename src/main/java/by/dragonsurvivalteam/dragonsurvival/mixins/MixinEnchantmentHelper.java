package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( EnchantmentHelper.class )
public abstract class MixinEnchantmentHelper{
	@Inject( at = @At( "HEAD" ), method = "hasAquaAffinity", cancellable = true )
	private static void hasAquaAffinity(LivingEntity entity, CallbackInfoReturnable<Boolean> ci){
		if(!(entity instanceof Player)){
			return;
		}

		Player player = (Player)entity;

		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.getType() == DragonType.SEA){
				ci.setReturnValue(true);
			}
		});
	}

	@Inject( at = @At( "HEAD" ), method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", cancellable = true)
	private static void getEnchantmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> ci){
		if(!entity.getMainHandItem().isEmpty()){
			if(entity.getMainHandItem().getItem() instanceof TieredItem){
				return;
			}
		}

		if(DragonUtils.isDragon(entity)){
			DragonStateHandler handler = DragonUtils.getHandler(entity);

			int highestLevel = 0;
			for(int i = 0; i < 4; i++){
				int lev = 0;
				ItemStack stack = handler.getClawInventory().getClawsInventory().getItem(i);

				if(!stack.isEmpty() && stack.isEnchanted()){
					ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(enchantment);
					ListTag listtag = stack.getEnchantmentTags();

					for(int x = 0; x < listtag.size(); ++x) {
						CompoundTag compoundtag = listtag.getCompound(x);

						if(!compoundtag.isEmpty() && compoundtag.hasUUID("id")){
							ResourceLocation resourcelocation1 = ResourceLocation.tryParse(compoundtag.getString("id"));

							if(!compoundtag.isEmpty() && compoundtag.hasUUID("lvl")){
								if(resourcelocation1 != null && resourcelocation1.equals(resourcelocation)){
									lev = Mth.clamp(compoundtag.getInt("lvl"), 0, 255);
								}
							}
						}
					}

					if(lev > highestLevel){
						highestLevel = lev;
					}
				}
			}

			ci.setReturnValue(highestLevel);
		}
	}
}