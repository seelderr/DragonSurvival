package by.dragonsurvivalteam.dragonsurvival.util;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ClawToolHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public class ToolUtils {
	public static boolean shouldUseDragonTools(final ItemStack itemStack) {
		return !(itemStack.getItem() instanceof TieredItem) && !isHarvestTool(itemStack) && !isWeapon(itemStack);
	}

	public static boolean isHarvestTool(final ItemStack itemStack) {
		return isPickaxe(itemStack) || isAxe(itemStack) || isShovel(itemStack) || isHoe(itemStack) || isShears(itemStack);
	}

	public static boolean isWeapon(final ItemStack itemStack) {
		return itemStack.getItem() instanceof SwordItem || itemStack.canPerformAction(ItemAbilities.SWORD_DIG) || itemStack.canPerformAction(ItemAbilities.SWORD_SWEEP) || itemStack.is(ItemTags.SWORDS) || /* TODO :: Unsure if this is the correct check to make */ itemStack.canPerformAction(ItemAbilities.AXE_DIG);
	}

	public static boolean isPickaxe(final ItemStack itemStack) {
		return itemStack.getItem() instanceof PickaxeItem || itemStack.canPerformAction(ItemAbilities.PICKAXE_DIG) || itemStack.is(ItemTags.PICKAXES) || itemStack.isCorrectToolForDrops(Blocks.STONE.defaultBlockState());
	}

	public static boolean isAxe(final ItemStack itemStack) {
		return itemStack.getItem() instanceof AxeItem || itemStack.canPerformAction(ItemAbilities.AXE_STRIP) || itemStack.canPerformAction(ItemAbilities.AXE_DIG) || itemStack.canPerformAction(ItemAbilities.AXE_SCRAPE) || itemStack.is(ItemTags.AXES) || itemStack.isCorrectToolForDrops(Blocks.OAK_LOG.defaultBlockState());
	}

	public static boolean isShovel(final ItemStack itemStack) {
		return itemStack.getItem() instanceof ShovelItem || itemStack.canPerformAction(ItemAbilities.SHOVEL_FLATTEN) || itemStack.canPerformAction(ItemAbilities.SHOVEL_DIG) || itemStack.is(ItemTags.SHOVELS) || itemStack.isCorrectToolForDrops(Blocks.DIRT.defaultBlockState());
	}

	public static boolean isHoe(final ItemStack itemStack) {
		return itemStack.canPerformAction(ItemAbilities.HOE_DIG) || itemStack.canPerformAction(ItemAbilities.HOE_TILL) || itemStack.is(ItemTags.HOES);
	}

	public static boolean isShears(final ItemStack itemStack) {
		return itemStack.canPerformAction(ItemAbilities.SHEARS_CARVE) || itemStack.canPerformAction(ItemAbilities.SHEARS_DIG) || itemStack.canPerformAction(ItemAbilities.SHEARS_DISARM) || itemStack.canPerformAction(ItemAbilities.SHEARS_HARVEST) || itemStack.is(Items.SHEARS);
	}

	/**
	 * Puts the relevant claw tool in the main hand and stores said main hand in the dragon state handler<br>
	 * This way modded enchantments etc. which check the currently held item will be directly compatible<br>
	 * <br>
	 * When using this make sure you call {@link ToolUtils#swapFinish(Player)} to restore the initial state
	 */
	public static void swapStart(@Nullable final Player player, final BlockState blockState) {
		if (player == null || player.isCreative() || player.isSpectator()) {
			return;
		}

		DragonStateHandler handler = DragonStateProvider.getData(player);

		if (!handler.isDragon()) {
			return;
		}

		Pair<ItemStack, Integer> data = ClawToolHandler.getDragonHarvestToolAndSlot(player, blockState);
		ItemStack dragonHarvestTool = data.getFirst();
		int toolSlot = data.getSecond();

		ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);

		if (toolSlot != -1 && !handler.switchedTool) {
			player.setItemInHand(InteractionHand.MAIN_HAND, dragonHarvestTool);

			handler.getClawToolData().getClawsInventory().setItem(toolSlot, ItemStack.EMPTY);
			handler.storedMainHandTool = mainHand;
			handler.switchedTool = true;
			handler.switchedToolSlot = toolSlot;
		}

		handler.toolSwapLayer++;
	}

	/**
	 * Puts the stored main hand back into the main hand and the claw tool into its slot
	 */
	public static void swapFinish(@Nullable final Player player) {
		if (player == null || player.isCreative() || player.isSpectator()) {
			return;
		}

		DragonStateHandler handler = DragonStateProvider.getData(player);

		if (!handler.isDragon()) {
			return;
		}

		handler.toolSwapLayer--;

		if (handler.toolSwapLayer < 0) {
			DragonSurvivalMod.LOGGER.warn("Tool swap layer was lower than 0 - this should not happen");
			handler.toolSwapLayer = 0;
		}

		if (handler.switchedTool && handler.toolSwapLayer == 0) {
			ItemStack originalMainHand = handler.storedMainHandTool;
			ItemStack originalToolSlot = player.getItemInHand(InteractionHand.MAIN_HAND);

			player.setItemInHand(InteractionHand.MAIN_HAND, originalMainHand);

			handler.getClawToolData().getClawsInventory().setItem(handler.switchedToolSlot, originalToolSlot);
			handler.storedMainHandTool = ItemStack.EMPTY;
			handler.switchedTool = false;
			handler.switchedToolSlot = -1;
		}
	}

	public static @Nullable Pair<Player, BlockState> getEventData(final Event event) {
		Player player = null;
		BlockState blockState = null;

		if (event instanceof BlockEvent.BreakEvent breakEvent) {
			player = breakEvent.getPlayer();
			blockState = breakEvent.getState();
		} else if (event instanceof PlayerEvent.BreakSpeed breakSpeed) {
			player = breakSpeed.getEntity();
			blockState = breakSpeed.getState();
		} else if (event instanceof PlayerEvent.HarvestCheck harvestCheck) {
			player = harvestCheck.getEntity();
			blockState = harvestCheck.getTargetBlock();
		}

		if (player != null) {
			return Pair.of(player, blockState);
		}

		return null;
	}
}
