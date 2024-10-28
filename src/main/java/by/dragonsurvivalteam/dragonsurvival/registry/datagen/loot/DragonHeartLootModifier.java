package by.dragonsurvivalteam.dragonsurvival.registry.datagen.loot;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.DSItems;
import by.dragonsurvivalteam.dragonsurvival.util.EnchantmentUtils;
import by.dragonsurvivalteam.dragonsurvival.util.ResourceHelper;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class DragonHeartLootModifier extends LootModifier {
	// No codec at the moment. This is just a formality.
	public static final Supplier<MapCodec<DragonHeartLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, DragonHeartLootModifier::new)));

	public DragonHeartLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	private static boolean canDropHeart(float health, float min, float max, List<String> entityList, Entity entity, boolean whiteList) {
		boolean meetsHealthRequirements = health >= min && health < max;
		boolean meetsListRequirements = entityList.isEmpty() || entityList.contains(ResourceHelper.getKey(entity).toString()) == whiteList;
		return meetsHealthRequirements && meetsListRequirements;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (!(entity instanceof LivingEntity) || entity instanceof Player) {
			return generatedLoot;
		}


		Player player = context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);

		// If it wasn't killed by a player, don't drop anything
		if (player == null) {
			return generatedLoot;
		}

		if (!DragonStateProvider.isDragon(player)) {
			return generatedLoot;
		}

		float health = ((LivingEntity) entity).getMaxHealth();

		boolean canDropWeakDragonHeart = canDropHeart(health, 14, 20, ServerConfig.weakDragonHeartEntityList, entity, ServerConfig.weakDragonHeartWhiteList);
		boolean canDropNormalDragonHeart = canDropHeart(health, 20, 50, ServerConfig.dragonHeartEntityList, entity, ServerConfig.dragonHeartWhiteList);
		boolean canDropElderDragonHeart = canDropHeart(health, 50, Float.MAX_VALUE, ServerConfig.elderDragonHeartEntityList, entity, ServerConfig.elderDragonHeartWhiteList);

		int lootingLevel = EnchantmentUtils.getLevel(player, Enchantments.LOOTING);

		if (canDropWeakDragonHeart) {
			if (context.getRandom().nextInt(100) <= ServerConfig.weakDragonHeartChance * 100 + lootingLevel * (ServerConfig.weakDragonHeartChance * 100 / 4)) {
				generatedLoot.add(new ItemStack(DSItems.WEAK_DRAGON_HEART));
			}
		}

		if (canDropNormalDragonHeart) {
			if (context.getRandom().nextInt(100) <= ServerConfig.dragonHeartShardChance * 100 + lootingLevel * (ServerConfig.dragonHeartShardChance * 100 / 4)) {
				generatedLoot.add(new ItemStack(DSItems.DRAGON_HEART_SHARD));
			}
		}

		if (canDropElderDragonHeart) {
			if (context.getRandom().nextInt(100) <= ServerConfig.elderDragonHeartChance * 100 + lootingLevel * (ServerConfig.elderDragonHeartChance * 100 / 4)) {
				generatedLoot.add(new ItemStack(DSItems.ELDER_DRAGON_HEART));
			}
		}

		return generatedLoot;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}
