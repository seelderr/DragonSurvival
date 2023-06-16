package by.dragonsurvivalteam.dragonsurvival.mixins;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import net.minecraft.world.entity.Entity;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

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

	@Inject(method = "doPostHurtEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;runIterationOnInventory(Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;Ljava/lang/Iterable;)V", shift = At.Shift.AFTER, ordinal = 0))
	private static void postHurtDragonSword(LivingEntity target, Entity attacker, CallbackInfo ci) {
		ItemStack sword = ClawToolHandler.getDragonSword(target);

		if (sword == ItemStack.EMPTY) {
			return;
		}

		for (Map.Entry<Enchantment, Integer> entry : sword.getAllEnchantments().entrySet()) {
			entry.getKey().doPostHurt(target, attacker, entry.getValue());
		}
	}

	@Inject(method = "doPostDamageEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;runIterationOnInventory(Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;Ljava/lang/Iterable;)V", shift = At.Shift.AFTER, ordinal = 0))
	private static void postDamageDragonSword(LivingEntity attacker, Entity target, CallbackInfo ci) {
		ItemStack sword = ClawToolHandler.getDragonSword(attacker);

		if (sword == ItemStack.EMPTY) {
			return;
		}

		for (Map.Entry<Enchantment, Integer> entry : sword.getAllEnchantments().entrySet()) {
			entry.getKey().doPostAttack(attacker, target, entry.getValue());
		}
	}

	private static final List<EnchantmentCategory> IGNORED_CATEGORIES = List.of(
		EnchantmentCategory.ARMOR,
			EnchantmentCategory.ARMOR_HEAD,
			EnchantmentCategory.ARMOR_CHEST,
			EnchantmentCategory.ARMOR_LEGS,
			EnchantmentCategory.ARMOR_FEET
			// Leave these out I suppose in case players modify the mod and add support for these items
//			EnchantmentCategory.BOW,
//			EnchantmentCategory.CROSSBOW,
//			EnchantmentCategory.TRIDENT,
//			EnchantmentCategory.FISHING_ROD
	);

	@Inject(at = @At("HEAD"), method = "getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I", cancellable = true)
	private static void getEnchantmentLevel(Enchantment enchantment, LivingEntity entity, CallbackInfoReturnable<Integer> ci) {
		if (IGNORED_CATEGORIES.contains(enchantment.category)) {
			// Gain some performance by skipping these
			return;
		}

		if (entity.getMainHandItem().getItem() instanceof TieredItem) {
			return;
		}

		if (DragonUtils.isDragon(entity) && entity instanceof Player player) {
			ItemStack stack = ClawToolHandler.getDragonHarvestTool(player);

			if (!(stack.getItem() instanceof TieredItem)) {
				// No relevant tool found - get the sword
				stack = ClawToolHandler.getDragonSword(player);
			}

			if (stack != ItemStack.EMPTY) {
				ci.setReturnValue(stack.getEnchantmentLevel(enchantment));
			}
		}
	}
}