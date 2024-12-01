package by.dragonsurvivalteam.dragonsurvival.commands;

import by.dragonsurvivalteam.dragonsurvival.commands.arguments.DragonBodyArgument;
import by.dragonsurvivalteam.dragonsurvival.commands.arguments.DragonStageArgument;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ClawInventoryData;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.SpinData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStages;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DragonCommand {
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();

        RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
        LiteralCommandNode<CommandSourceStack> dragon = literal("dragon").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
            String type = context.getArgument("dragon_type", String.class);
            return runCommand(type, null, null, false, context.getSource().getPlayerOrException());
        }).build();

        ArgumentCommandNode<CommandSourceStack, String> dragonType = argument("dragon_type", StringArgumentType.string()).suggests((context, builder) -> {
            SuggestionsBuilder suggestions = null;

            for (String value : DragonTypes.getAllSubtypes()) {
                String val = value.toLowerCase(Locale.ENGLISH);
                suggestions = suggestions == null ? builder.suggest(val) : suggestions.suggest(val);
            }

            suggestions = suggestions == null ? builder.suggest("human") : suggestions.suggest("human");
            return suggestions.buildFuture();
        }).executes(context -> {
            String type = context.getArgument("dragon_type", String.class);
            ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
            return runCommand(type, null, null, false, serverPlayer);
        }).build();

        ArgumentCommandNode<CommandSourceStack, Holder<DragonBody>> dragonBody = argument(DragonBodyArgument.ID, new DragonBodyArgument(event.getBuildContext())).executes(context -> {
            String type = context.getArgument("dragon_type", String.class);
            Holder<DragonBody> body = DragonBodyArgument.get(context);
            ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
            return runCommand(type, body, null, false, serverPlayer);
        }).build();

        ArgumentCommandNode<CommandSourceStack, Holder<DragonStage>> dragonStage = argument(DragonStageArgument.ID, new DragonStageArgument(event.getBuildContext())).executes(context -> {
            String type = context.getArgument("dragon_type", String.class);
            Holder<DragonBody> body = DragonBodyArgument.get(context);
            Holder<DragonStage> level = DragonStageArgument.get(context);
            ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
            return runCommand(type, body, level, false, serverPlayer);
        }).build();

        ArgumentCommandNode<CommandSourceStack, Boolean> giveFlight = argument("flight", BoolArgumentType.bool()).executes(context -> {
            String type = context.getArgument("dragon_type", String.class);
            Holder<DragonBody> body = DragonBodyArgument.get(context);
            Holder<DragonStage> level = DragonStageArgument.get(context);
            boolean flight = context.getArgument("flight", Boolean.TYPE);
            ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
            return runCommand(type, body, level, flight, serverPlayer);
        }).build();

        ArgumentCommandNode<CommandSourceStack, EntitySelector> target = argument("target", EntityArgument.players()).executes(context -> {
            String type = context.getArgument("dragon_type", String.class);
            Holder<DragonBody> body = DragonBodyArgument.get(context);
            Holder<DragonStage> level = DragonStageArgument.get(context);
            boolean flight = context.getArgument("flight", Boolean.TYPE);
            EntitySelector selector = context.getArgument("target", EntitySelector.class);
            List<ServerPlayer> serverPlayers = selector.findPlayers(context.getSource());
            serverPlayers.forEach(player -> runCommand(type, body, level, flight, player));
            return 1;
        }).build();

        rootCommandNode.addChild(dragon);
        dragon.addChild(dragonType);
        dragonType.addChild(dragonBody);
        dragonBody.addChild(dragonStage);
        dragonStage.addChild(giveFlight);
        giveFlight.addChild(target);
    }

    private static int runCommand(String type, @Nullable Holder<DragonBody> dragonBody, @Nullable Holder<DragonStage> dragonStage, boolean flight, ServerPlayer player) {
        DragonStateHandler cap = DragonStateProvider.getData(player);
        AbstractDragonType dragonType = DragonTypes.getStaticSubtype(type);

        if (dragonType != null && dragonBody == null) {
            dragonBody = DragonBody.random(player.registryAccess());
        }

        if (dragonType != null && dragonStage == null) {
            dragonStage = player.registryAccess().holderOrThrow(DragonStages.newborn);
        }

        if (dragonType == null && cap.getType() != null) {
            reInsertClawTools(player);
        }

        cap.setType(dragonType, player);
        cap.setBody(dragonBody, player);
        cap.setSize(player, dragonStage, dragonStage != null ? dragonStage.value().sizeRange().min() : DragonStateHandler.NO_SIZE);

        cap.setHasFlight(flight);
        SpinData spin = SpinData.getData(player);
        spin.spinLearned = flight;
        cap.setPassengerId(-1);
        cap.isGrowing = true;

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new SyncComplete.Data(player.getId(), cap.serializeNBT(player.registryAccess())));
        player.refreshDimensions();
        return 1;
    }

    public static void reInsertClawTools(Player player) {
        SimpleContainer clawsContainer = ClawInventoryData.getData(player).getContainer();
        for (int i = 0; i < 4; i++) {
            ItemStack stack = clawsContainer.getItem(i);

            if (player instanceof ServerPlayer serverPlayer) {
                if (!serverPlayer.addItem(stack)) {
                    serverPlayer.level().addFreshEntity(new ItemEntity(serverPlayer.level(), serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, stack));
                }
            }
        }

        clawsContainer.clearContent();
    }
}