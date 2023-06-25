package by.dragonsurvivalteam.dragonsurvival.magic.common.innate;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.ArrayList;

import static by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes.*;

public abstract class DragonClawsAbility extends InnateDragonAbility {
	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinLevel() {
		return 1;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ArrayList<Component> getInfo() {
		DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);

		Pair<Tier, Integer> harvestInfo = getHarvestInfo();
		Tier tier = harvestInfo != null ? harvestInfo.getFirst() : null;

		if (tier == Tiers.GOLD) {
			// FIXME :: Make a new text for gold? It's not a separate harvest level though (level is the same as wood)
			tier = Tiers.WOOD;
		}

		ArrayList<Component> components = super.getInfo();
		components.add(Component.translatable("ds.skill.tool_type." + getName()));

		if (tier != null) {
			components.add(Component.translatable("ds.skill.harvest_level", I18n.get("ds.skill.harvest_level." + ((Tiers) tier).name().toLowerCase())));
		}

		ItemStack swordStack = handler.getClawToolData().getClawsInventory().getItem(0);

		double ageBonus = handler.isDragon() ? handler.getLevel() == DragonLevel.ADULT ? ServerConfig.adultBonusDamage : handler.getLevel() == DragonLevel.YOUNG ? ServerConfig.youngBonusDamage : ServerConfig.babyBonusDamage : 0;
		double swordBonus = swordStack.isEmpty() ? 0 : swordStack.getItem() instanceof SwordItem ? ((SwordItem) swordStack.getItem()).getDamage() : 0; // FIXME :: The damage here is not accurate (no attribute or enchantment bonus is being considered)
		double bonus = Math.max(ageBonus, swordBonus - 1);

		if (bonus > 0.0) {
			components.add(Component.translatable("ds.skill.claws.damage", "+" + bonus));
		}

		return components;
	}

	@Override
	public int getLevel() {
		Pair<Tier, Integer> harvestInfo = getHarvestInfo();
		int textureId = harvestInfo != null ? harvestInfo.getSecond() : 0;

		return FMLEnvironment.dist == Dist.CLIENT ? textureId : 0;
	}

	@OnlyIn(Dist.CLIENT)
	public @Nullable Pair<Tier, Integer> getHarvestInfo() {
		DragonStateHandler handler = DragonUtils.getHandler(Minecraft.getInstance().player);

		if (handler.getType() == null) {
			return null;
		}

		ItemStack clawStack = null;

		if (SEA.equals(handler.getType())) {
			// Shovel
			clawStack = handler.getClawToolData().getClawsInventory().getItem(3);
		} else if(FOREST.equals(handler.getType())) {
			// Axe
			clawStack = handler.getClawToolData().getClawsInventory().getItem(2);
		} else if(CAVE.equals(handler.getType())) {
			// Pickaxe
			clawStack = handler.getClawToolData().getClawsInventory().getItem(1);
		}

		Tier tier = null;

		if (clawStack != null && clawStack.getItem() instanceof TieredItem tieredItem) {
			tier = tieredItem.getTier();

			/*
			Modded tiers will cause problems if you cast them to `Tiers`
			Some mods don't apply tiers to their tools (e.g. some MCreator generated code)
			*/
			if (!(tier instanceof Tiers)) {
				tier = DragonUtils.levelToVanillaTier(tier.getLevel());
			}
		}

		Tier bonusTier = handler.getDragonHarvestTier(handler.getType().slotForBonus);

		// Tier of item in claw tool slot is lower than the bonus the dragon should have
		if (tier == null || TierSortingRegistry.getTiersLowerThan(bonusTier).contains(tier)) {
			tier = bonusTier;
		}

		int textureId = 0;

		if (Tiers.WOOD.equals(tier)) {
			textureId = 1;
		} else if(Tiers.STONE.equals(tier)) {
			textureId = 2;
		} else if(Tiers.IRON.equals(tier)) {
			textureId = 3;
		} else if(Tiers.GOLD.equals(tier)) {
			/* TODO ::
			Gold would only be displayed if the player has not yet reached the requirements for the bonus harvest level
			Since the gold harvest level is 0 (lower than stone)
			*/
			textureId = 4;
		} else if(Tiers.DIAMOND.equals(tier)) {
			textureId = 5;
		} else if(Tiers.NETHERITE.equals(tier)) {
			textureId = 6;
		}

		// TODO :: What about the texture for 7?

		return Pair.of(tier, textureId);
	}
}