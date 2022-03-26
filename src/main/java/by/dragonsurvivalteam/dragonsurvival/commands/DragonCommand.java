package by.dragonsurvivalteam.dragonsurvival.commands;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.misc.DragonType;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.RequestClientData;
import by.dragonsurvivalteam.dragonsurvival.network.flight.SyncSpinStatus;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class DragonCommand{
	public static void register(CommandDispatcher<CommandSource> commandDispatcher){
		RootCommandNode<CommandSource> rootCommandNode = commandDispatcher.getRoot();
		LiteralCommandNode<CommandSource> dragon = literal("dragon").requires(commandSource -> commandSource.hasPermission(2)).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			return runCommand(type, 1, false, context.getSource().getPlayerOrException());
		}).build();

		ArgumentCommandNode<CommandSource, String> dragonType = argument("dragon_type", StringArgumentType.string()).suggests((context, builder) -> ISuggestionProvider.suggest(new String[]{"cave", "sea", "forest", "human"}, builder)).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			ServerPlayerEntity serverPlayerEntity = context.getSource().getPlayerOrException();
			return runCommand(type, 1, false, serverPlayerEntity);
		}).build();

		ArgumentCommandNode<CommandSource, Integer> dragonStage = argument("dragon_stage", IntegerArgumentType.integer(1, 3)).suggests((context, builder) -> ISuggestionProvider.suggest(new String[]{"1", "2", "3"}, builder)).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			int stage = context.getArgument("dragon_stage", Integer.TYPE);
			ServerPlayerEntity serverPlayerEntity = context.getSource().getPlayerOrException();
			return runCommand(type, stage, false, serverPlayerEntity);
		}).build();

		ArgumentCommandNode<CommandSource, Boolean> giveWings = argument("wings", BoolArgumentType.bool()).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			int stage = context.getArgument("dragon_stage", Integer.TYPE);
			boolean wings = context.getArgument("wings", Boolean.TYPE);
			ServerPlayerEntity serverPlayerEntity = context.getSource().getPlayerOrException();
			return runCommand(type, stage, wings, serverPlayerEntity);
		}).build();

		ArgumentCommandNode<CommandSource, EntitySelector> target = argument("target", EntityArgument.players()).executes(context -> {
			String type = context.getArgument("dragon_type", String.class);
			int stage = context.getArgument("dragon_stage", Integer.TYPE);
			boolean wings = context.getArgument("wings", Boolean.TYPE);
			EntitySelector selector = context.getArgument("target", EntitySelector.class);
			List<ServerPlayerEntity> serverPlayers = selector.findPlayers(context.getSource());
			serverPlayers.forEach((player) -> runCommand(type, stage, wings, player));
			return 1;
		}).build();

		rootCommandNode.addChild(dragon);
		dragon.addChild(dragonType);
		dragonType.addChild(dragonStage);
		dragonStage.addChild(giveWings);
		giveWings.addChild(target);
	}

	private static int runCommand(String type, int stage, boolean wings, ServerPlayerEntity serverPlayerEntity){
		serverPlayerEntity.getCapability(DragonStateProvider.DRAGON_CAPABILITY).ifPresent(dragonStateHandler -> {
			DragonType dragonType1 = type.equalsIgnoreCase("human") ? DragonType.NONE : DragonType.valueOf(type.toUpperCase());

			if(dragonType1 == DragonType.NONE && dragonStateHandler.getType() != DragonType.NONE){
				reInsertClawTools(serverPlayerEntity, dragonStateHandler);
			}

			dragonStateHandler.setType(dragonType1);
			DragonLevel dragonLevel = DragonLevel.values()[stage - 1];
			dragonStateHandler.setHasWings(wings);
			dragonStateHandler.getMovementData().spinLearned = wings;
			dragonStateHandler.setSize(dragonLevel.size, serverPlayerEntity);
			dragonStateHandler.setPassengerId(0);

			dragonStateHandler.growing = true;
			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayerEntity), new SyncSpinStatus(serverPlayerEntity.getId(), dragonStateHandler.getMovementData().spinAttack, dragonStateHandler.getMovementData().spinCooldown, dragonStateHandler.getMovementData().spinLearned));
			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayerEntity), new CompleteDataSync(serverPlayerEntity));
			NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayerEntity), new RequestClientData(dragonStateHandler.getType(), dragonStateHandler.getLevel()));
			serverPlayerEntity.refreshDimensions();
		});
		return 1;
	}

	public static void reInsertClawTools(ServerPlayerEntity serverPlayerEntity, DragonStateHandler dragonStateHandler){
		for(int i = 0; i < 4; i++){
			ItemStack stack = dragonStateHandler.getClawInventory().getClawsInventory().getItem(i);

			if(!serverPlayerEntity.addItem(stack)){
				if(serverPlayerEntity.level.addFreshEntity(new ItemEntity(serverPlayerEntity.level, serverPlayerEntity.position().x, serverPlayerEntity.position().y, serverPlayerEntity.position().z, stack))){
					dragonStateHandler.getClawInventory().getClawsInventory().removeItem(i, stack.getCount());
				}
			}else{
				dragonStateHandler.getClawInventory().getClawsInventory().removeItem(i, stack.getCount());
			}
		}
	}
}