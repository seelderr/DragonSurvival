package by.dragonsurvivalteam.dragonsurvival.network.claw;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/DragonClawsMenuToggle.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.server.containers.DragonContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/DragonClawsMenuToggle.java

import java.util.function.Supplier;

public class DragonClawsMenuToggle implements IMessage<DragonClawsMenuToggle>{
	public boolean state;

	public DragonClawsMenuToggle(){}

	public DragonClawsMenuToggle(boolean state){
		this.state = state;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/DragonClawsMenuToggle.java
	public void encode(DragonClawsMenuToggle message, FriendlyByteBuf buffer) {
=======
	public void encode(DragonClawsMenuToggle message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/DragonClawsMenuToggle.java
		buffer.writeBoolean(message.state);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/DragonClawsMenuToggle.java
	public DragonClawsMenuToggle decode(FriendlyByteBuf buffer) {
=======
	public DragonClawsMenuToggle decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/DragonClawsMenuToggle.java
		boolean state = buffer.readBoolean();
		return new DragonClawsMenuToggle(state);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/claw/DragonClawsMenuToggle.java
	public void handle(DragonClawsMenuToggle message, Supplier<NetworkEvent.Context> supplier) {
		ServerPlayer player = supplier.get().getSender();
		
=======
	public void handle(DragonClawsMenuToggle message, Supplier<NetworkEvent.Context> supplier){
		ServerPlayerEntity player = supplier.get().getSender();

>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/claw/DragonClawsMenuToggle.java
		DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
			dragonStateHandler.getClawInventory().setClawsMenuOpen(message.state);
		});

		if(player.containerMenu instanceof DragonContainer){
			DragonContainer container = (DragonContainer)player.containerMenu;
			container.update();
		}
	}
}