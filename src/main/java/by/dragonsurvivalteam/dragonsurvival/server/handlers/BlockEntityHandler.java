package by.dragonsurvivalteam.dragonsurvival.server.handlers;

import by.dragonsurvivalteam.dragonsurvival.registry.DSBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class BlockEntityHandler {
	@SubscribeEvent
	public static void addToBlockEntityType(BlockEntityTypeAddBlocksEvent e) {
		e.modify(BlockEntityType.VAULT, DSBlocks.DRAGON_VAULT_FRIENDLY.get());
		e.modify(BlockEntityType.VAULT, DSBlocks.DRAGON_VAULT_ANGRY.get());
		e.modify(BlockEntityType.VAULT, DSBlocks.DRAGON_VAULT_HUNTER.get());
	}
}
