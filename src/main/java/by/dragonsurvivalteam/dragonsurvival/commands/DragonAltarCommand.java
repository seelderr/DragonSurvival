package by.dragonsurvivalteam.dragonsurvival.commands;

import by.dragonsurvivalteam.dragonsurvival.network.container.OpenDragonAltar;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import static net.minecraft.commands.Commands.literal;

public class DragonAltarCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
        LiteralCommandNode<CommandSourceStack> dragon = literal("dragon-altar").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> runCommand(context.getSource().getPlayerOrException())).build();
        rootCommandNode.addChild(dragon);
    }

    private static int runCommand(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, OpenDragonAltar.INSTANCE);
        return 1;
    }
}