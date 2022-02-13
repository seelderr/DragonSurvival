package by.jackraidenph.dragonsurvival.commands;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.SkinCustomization.OpenDragonCustomization;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import static net.minecraft.command.Commands.literal;

public class CustomizationCommand
{
	public static void register(CommandDispatcher<CommandSource> commandDispatcher)
	{
		RootCommandNode<CommandSource> rootCommandNode = commandDispatcher.getRoot();
		LiteralCommandNode<CommandSource> dragon = literal("dragon-creator").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
			return runCommand(context.getSource().getPlayerOrException());
		}).build();
		
		rootCommandNode.addChild(dragon);
	}
	
	private static int runCommand( ServerPlayerEntity serverPlayerEntity)
	{
		if(DragonStateProvider.isDragon(serverPlayerEntity)){
			NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity), new OpenDragonCustomization());
		}
		return 1;
	}
}
