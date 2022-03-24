package by.dragonsurvivalteam.dragonsurvival.network.magic;

<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/ActivateClientAbility.java
import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.network.IMessage;
=======
import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/ActivateClientAbility.java
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ActivateClientAbility implements IMessage<ActivateClientAbility>{

	public int playerId;

	public ActivateClientAbility(){}

	public ActivateClientAbility(int playerId){
		this.playerId = playerId;
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/ActivateClientAbility.java
	public void encode(ActivateClientAbility message, FriendlyByteBuf buffer) {
=======
	public void encode(ActivateClientAbility message, PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/ActivateClientAbility.java
		buffer.writeInt(message.playerId);
	}

	@Override
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/ActivateClientAbility.java
	public ActivateClientAbility decode(FriendlyByteBuf buffer) {
=======
	public ActivateClientAbility decode(PacketBuffer buffer){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/ActivateClientAbility.java
		int playerId = buffer.readInt();
		return new ActivateClientAbility(playerId);
	}

	@Override
	public void handle(ActivateClientAbility message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void run(ActivateClientAbility message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
<<<<<<< HEAD:src/main/java/by/jackraidenph/dragonsurvival/network/magic/ActivateClientAbility.java
			Player thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				Level world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof Player) {
=======
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
>>>>>>> v1.16.x:src/main/java/by/dragonsurvivalteam/dragonsurvival/network/magic/ActivateClientAbility.java
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						ActiveDragonAbility ability = dragonStateHandler.getMagic().getCurrentlyCasting();

						if(ability == null){
							ability = dragonStateHandler.getMagic().getAbilityFromSlot(dragonStateHandler.getMagic().getSelectedAbilitySlot());
						}

						if(ability != null){
							ability.player = (Player)entity;
							ability.onActivation((Player)entity);
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}