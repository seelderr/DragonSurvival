package by.jackraidenph.dragonsurvival.commands;

import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.OpenDragonCustomization;
import by.jackraidenph.dragonsurvival.util.DragonUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class CustomizationCommand
{
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher)
	{
		RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
		LiteralCommandNode<CommandSourceStack> dragon = Commands.literal("dragon-creator").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
			return runCommand(context.getSource().getPlayerOrException());
		}).build();
		
		rootCommandNode.addChild(dragon);
	}
	
	private static int runCommand( ServerPlayer serverPlayer)
	{
		if(DragonUtils.isDragon(serverPlayer)){
			NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new OpenDragonCustomization());
		}
		return 1;
	}
}
