package by.dragonsurvivalteam.dragonsurvival.commands;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DragonSizeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
        LiteralCommandNode<CommandSourceStack> dragonSetSize = literal("dragon-set-size").requires(commandSource -> commandSource.hasPermission(2)).build();

        ArgumentCommandNode<CommandSourceStack, Double> dragonSize = argument("dragon_size", DoubleArgumentType.doubleArg(0, 1000)).requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
            double size = Mth.clamp(context.getArgument("dragon_size", Double.TYPE), 1, ServerConfig.maxGrowthSize);
            ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
            DragonStateHandler handler = DragonStateProvider.getData(serverPlayer);

            if (handler.isDragon()) {
                handler.setSize(size, serverPlayer);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new SyncSize.Data(serverPlayer.getId(), size));
                DSAdvancementTriggers.BE_DRAGON.get().trigger(serverPlayer, handler.getSize(), handler.getTypeName());
            }

            return 1;
        }).build();

        rootCommandNode.addChild(dragonSetSize);
        dragonSetSize.addChild(dragonSize);
    }
}
