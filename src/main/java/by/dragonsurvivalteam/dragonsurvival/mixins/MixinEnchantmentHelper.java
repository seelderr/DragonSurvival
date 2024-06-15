package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin( EnchantmentHelper.class )
public abstract class MixinEnchantmentHelper{

	// FIXME: THIS CAN BE DONE WITH A NEW PLAYER ATTRIBUTE INSTEAD
	/*@ModifyReturnValue(method = "hasAquaAffinity", at = @At("RETURN"))
	private static boolean modifyHasAquaAffinityForSeaDragon(boolean original, @Local(index = 0, argsOnly = true) LivingEntity pEntity){
		if(!(pEntity instanceof Player player)){
			return original;
		}

		if(DragonUtils.isDragonType(player, DragonTypes.SEA)){
			return true;
		}

		return original;
	}*/
}