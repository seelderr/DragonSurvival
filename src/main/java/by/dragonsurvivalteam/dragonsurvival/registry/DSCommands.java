package by.dragonsurvivalteam.dragonsurvival.registry;

import static net.neoforged.neoforgespi.ILaunchContext.LOGGER;

import by.dragonsurvivalteam.dragonsurvival.commands.DragonAltarCommand;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonEditorCommand;
import by.dragonsurvivalteam.dragonsurvival.commands.DragonSizeCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class DSCommands {
	@SubscribeEvent
	public static void serverRegisterCommandsEvent(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
		DragonCommand.register(commandDispatcher);
		DragonEditorCommand.register(commandDispatcher);
		DragonAltarCommand.register(commandDispatcher);
		DragonSizeCommand.register(commandDispatcher);
		LOGGER.info("Registered commands");
	}
}
