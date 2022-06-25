package by.dragonsurvivalteam.dragonsurvival.commands;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.util.DragonUtils;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.status.SyncAltarCooldown;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class DragonCommand{
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher){
		RootCommandNode<CommandSourceStack> rootCommandNode = commandDispatcher.getRoot();
		LiteralCommandNode<CommandSourceStack> dragon = literal("dragon").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			return runCommand(type, 1, false, context.getSource().getPlayerOrException());
		}).build();

		ArgumentCommandNode<CommandSourceStack, String> dragonType = argument("dragon_type", StringArgumentType.string()).suggests((context, builder) -> builder.suggest("cave").suggest("sea").suggest("forest").suggest("human").buildFuture()).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
			return runCommand(type, 1, false, serverPlayer);
		}).build();

		ArgumentCommandNode<CommandSourceStack, Integer> dragonStage = argument("dragon_stage", IntegerArgumentType.integer(1, 3)).suggests((context, builder) -> builder.suggest(1).suggest(2).suggest(3).buildFuture()).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			int stage = context.getArgument("dragon_stage", Integer.TYPE);
			ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
			return runCommand(type, stage, false, serverPlayer);
		}).build();

		ArgumentCommandNode<CommandSourceStack, Boolean> giveWings = argument("wings", BoolArgumentType.bool()).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			int stage = context.getArgument("dragon_stage", Integer.TYPE);
			boolean wings = context.getArgument("wings", Boolean.TYPE);
			ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
			return runCommand(type, stage, wings, serverPlayer);
		}).build();

		ArgumentCommandNode<CommandSourceStack, EntitySelector> target = argument("target", EntityArgument.players()).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			int stage = context.getArgument("dragon_stage", Integer.TYPE);
			boolean wings = context.getArgument("wings", Boolean.TYPE);
			EntitySelector selector = context.getArgument("target", EntitySelector.class);
			List<ServerPlayer> serverPlayers = selector.findPlayers(context.getSource());
			serverPlayers.forEach((player) -> runCommand(type, stage, wings, player));
			return 1;
		}).build();

		rootCommandNode.addChild(dragon);
		dragon.addChild(dragonType);
		dragonType.addChild(dragonStage);
		dragonStage.addChild(giveWings);
		giveWings.addChild(target);
	}

	private static int runCommand(String type, int stage, boolean wings, ServerPlayer player){
		DragonStateHandler cap = DragonUtils.getHandler(player);
		DragonType dragonType1 = type.equalsIgnoreCase("human") ? DragonType.NONE : DragonType.valueOf(type.toUpperCase());

		if(dragonType1 == DragonType.NONE && cap.getType() != DragonType.NONE){
			reInsertClawTools(player, cap);
		}

		cap.setType(dragonType1);
		DragonLevel dragonLevel = DragonLevel.values()[stage - 1];
		cap.setHasWings(wings);
		cap.getMovementData().spinLearned = wings;
		cap.setSize(dragonLevel.size, player);
		cap.setPassengerId(0);
		cap.growing = true;

		NetworkHandler.CHANNEL.sendToServer(new SyncAltarCooldown(Minecraft.getInstance().player.getId(), Functions.secondsToTicks(ServerConfig.altarUsageCooldown)));
		NetworkHandler.CHANNEL.sendToServer(new SynchronizeDragonCap(player.getId(), cap.isHiding(), cap.getType(), cap.getSize(), cap.hasWings(), ServerConfig.caveLavaSwimmingTicks, 0));
		NetworkHandler.CHANNEL.sendToServer(new SyncSpinStatus(Minecraft.getInstance().player.getId(), cap.getMovementData().spinAttack, cap.getMovementData().spinCooldown, cap.getMovementData().spinLearned));
		ClientEvents.sendClientData(new RequestClientData(cap.getType(), cap.getLevel()));
		return 1;
	}

	public static void reInsertClawTools(Player player, DragonStateHandler dragonStateHandler){
		for(int i = 0; i < 4; i++){
			ItemStack stack = dragonStateHandler.getClawInventory().getClawsInventory().getItem(i);

			if(player instanceof ServerPlayer serverPlayer){
				if(!serverPlayer.addItem(stack)){
					serverPlayer.level.addFreshEntity(new ItemEntity(serverPlayer.level, serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, stack));
				}
			}
		}

		dragonStateHandler.getClawInventory().getClawsInventory().clearContent();
	}
}