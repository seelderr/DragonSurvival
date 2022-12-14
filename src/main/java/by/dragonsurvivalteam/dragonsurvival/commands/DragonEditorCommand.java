package by.dragonsurvivalteam.dragonsurvival.commands;

import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonEditorPacket;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import static net.minecraft.commands.Commands.literal;


public class DragonEditorCommand{
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
		RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
		LiteralCommandNode<CommandSourceStack> dragon = literal("dragon-editor").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
			return runCommand(context.getSource().getPlayerOrException());
		}).build();

		rootCommandNode.addChild(dragon);
	}

	private static int runCommand(ServerPlayer serverPlayer){
		if(DragonUtils.isDragon(serverPlayer)){
			NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new OpenDragonEditorPacket());
		}
		return 1;
	}
}