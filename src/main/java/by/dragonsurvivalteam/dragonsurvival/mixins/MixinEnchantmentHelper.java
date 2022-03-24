package by.dragonsurvivalteam.dragonsurvival.mixins;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinEnchantmentHelper.java
import by.jackraidenph.dragonsurvival.common.capability.caps.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
=======
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
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinEnchantmentHelper.java
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( EnchantmentHelper.class )
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinEnchantmentHelper.java
public class MixinEnchantmentHelper
{
	@Inject( at = @At("HEAD"), method = "hasAquaAffinity", cancellable = true)
	private static void hasAquaAffinity(LivingEntity entity, CallbackInfoReturnable<Boolean> ci) {
		if (!(entity instanceof Player))
			return;
		
		Player player = (Player) entity;
		
=======
public class MixinEnchantmentHelper{
	@Inject( at = @At( "HEAD" ), method = "hasAquaAffinity", cancellable = true )
	private static void hasAquaAffinity(LivingEntity entity, CallbackInfoReturnable<Boolean> ci){
		if(!(entity instanceof PlayerEntity)){
			return;
		}

		PlayerEntity player = (PlayerEntity)entity;

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinEnchantmentHelper.java
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			if(dragonStateHandler.getType() == DragonType.SEA){
				ci.setReturnValue(true);
			}
		});
	}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinEnchantmentHelper.java
	
	@Inject( at = @At("HEAD"), method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", cancellable = true)
	private static void getEnchantmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> ci) {
=======

	@Inject( at = @At( "HEAD" ), method = "getEnchantmentLevel", cancellable = true )
	private static void getEnchantmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> ci){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinEnchantmentHelper.java
		if(!entity.getMainHandItem().isEmpty()){
			if(entity.getMainHandItem().getItem() instanceof TieredItem){
				return;
			}
		}
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/mixins/MixinEnchantmentHelper.java
		
=======

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/mixins/MixinEnchantmentHelper.java
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