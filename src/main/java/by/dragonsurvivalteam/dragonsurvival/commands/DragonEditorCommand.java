package by.dragonsurvivalteam.dragonsurvival.commands;

import static net.minecraft.commands.Commands.literal;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonEditorPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;


public class DragonEditorCommand{
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
		RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
		LiteralCommandNode<CommandSourceStack> dragon = literal("dragon-editor").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
			return runCommand(context.getSource().getPlayerOrException());
		}).build();

		rootCommandNode.addChild(dragon);
	}

	private static int runCommand(ServerPlayer serverPlayer){
		if(DragonStateProvider.isDragon(serverPlayer)){
			NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new OpenDragonEditorPacket());
		}
		return 1;
	}
}