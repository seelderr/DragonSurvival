package by.dragonsurvivalteam.dragonsurvival.network.magic;

import by.dragonsurvivalteam.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ActivateClientAbility implements IMessage<ActivateClientAbility>{

	public int playerId;

	public ActivateClientAbility(){}

	public ActivateClientAbility(int playerId){
		this.playerId = playerId;
	}

	@Override
	public void encode(ActivateClientAbility message, PacketBuffer buffer){
		buffer.writeInt(message.playerId);
	}

	@Override
	public ActivateClientAbility decode(PacketBuffer buffer){
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
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if(thisPlayer != null){
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if(entity instanceof PlayerEntity){
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						ActiveDragonAbility ability = dragonStateHandler.getMagic().getCurrentlyCasting();

						if(ability == null){
							ability = dragonStateHandler.getMagic().getAbilityFromSlot(dragonStateHandler.getMagic().getSelectedAbilitySlot());
						}

						if(ability != null){
							ability.player = (PlayerEntity)entity;
							ability.onActivation((PlayerEntity)entity);
						}
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}