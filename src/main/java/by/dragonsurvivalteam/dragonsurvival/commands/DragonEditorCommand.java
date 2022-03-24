package by.dragonsurvivalteam.dragonsurvival.commands;

import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonEditorPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import static net.minecraft.command.Commands.literal;

public class DragonEditorCommand{
	public static void register(CommandDispatcher<CommandSource> commandDispatcher){
		RootCommandNode<CommandSource> rootCommandNode = commandDispatcher.getRoot();
		LiteralCommandNode<CommandSource> dragon = literal("dragon-editor").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
			return runCommand(context.getSource().getPlayerOrException());
		}).build();

		rootCommandNode.addChild(dragon);
	}

	private static int runCommand(ServerPlayerEntity serverPlayerEntity){
		if(DragonUtils.isDragon(serverPlayerEntity)){
			NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity), new OpenDragonEditorPacket());
		}
		return 1;
	}
}