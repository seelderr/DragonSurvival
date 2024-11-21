package by.dragonsurvivalteam.dragonsurvival.commands;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DragonSizeCommand {
    public static void register(final RegisterCommandsEvent event) {
        RootCommandNode<CommandSourceStack> root = event.getDispatcher().getRoot();
        LiteralCommandNode<CommandSourceStack> command = literal("dragon-set-size").requires(source -> source.hasPermission(2)).build();

        ArgumentCommandNode<CommandSourceStack, Double> sizeArgument = argument("dragon_size", new DragonSizeArgument(event.getBuildContext())).requires(source -> source.hasPermission(2)).executes(context -> {
            double size = DragonSizeArgument.get(context);
            ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
            DragonStateHandler handler = DragonStateProvider.getData(serverPlayer);

            if (handler.isDragon()) {
                handler.setSize(size, serverPlayer);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new SyncSize.Data(serverPlayer.getId(), size));
                DSAdvancementTriggers.BE_DRAGON.get().trigger(serverPlayer, handler.getSize(), handler.getTypeName());
            }

            return 1;
        }).build();

        root.addChild(command);
        command.addChild(sizeArgument);
    }
}
