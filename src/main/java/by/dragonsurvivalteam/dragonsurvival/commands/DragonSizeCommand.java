package by.dragonsurvivalteam.dragonsurvival.commands;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.player.SyncSize;
import by.dragonsurvivalteam.dragonsurvival.registry.DSAdvancementTriggers;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

public class DragonSizeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
        RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();

        LiteralCommandNode<CommandSourceStack> dragonSetSize = literal("dragon-set-size").requires(commandSource -> commandSource.hasPermission(2)).build();

        // Would prefer to restrict this between newborn size and ServerConfig.maxGrowthSize, but the maxGrowthSize is not initialized at the time of this command's registration
        ArgumentCommandNode<CommandSourceStack, Double> dragonSize = argument("dragon_size", DoubleArgumentType.doubleArg(DragonLevel.NEWBORN.size, 1000000.0)).requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
            double size = Mth.clamp(context.getArgument("dragon_size", Double.TYPE), 1.0, ServerConfig.maxGrowthSize);
            ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
            DragonStateHandler cap = DragonStateProvider.getOrGenerateHandler(serverPlayer);
            if(cap.isDragon()) {
                cap.setSize(size, serverPlayer);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new SyncSize.Data(serverPlayer.getId(), size));
                DSAdvancementTriggers.BE_DRAGON.get().trigger(serverPlayer, cap.getSize(), cap.getTypeName());
            }
            return 1;
        }).build();

        rootCommandNode.addChild(dragonSetSize);
        dragonSetSize.addChild(dragonSize);
    }
}
