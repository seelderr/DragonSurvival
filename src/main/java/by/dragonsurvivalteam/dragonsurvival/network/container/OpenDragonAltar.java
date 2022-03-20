package by.dragonsurvivalteam.dragonsurvival.network.container;

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import by.dragonsurvivalteam.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenDragonAltar implements IMessage<OpenDragonAltar>{

	public OpenDragonAltar(){}

	@Override
	public void encode(OpenDragonAltar message, PacketBuffer buffer){

	}

	@Override
	public OpenDragonAltar decode(PacketBuffer buffer){
		return new OpenDragonAltar();
	}

	@Override
	public void handle(OpenDragonAltar message, Supplier<NetworkEvent.Context> supplier){
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
	}

	@OnlyIn( Dist.CLIENT )
	public void runClient(OpenDragonAltar message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			Minecraft.getInstance().setScreen(new DragonAltarGUI());
			context.setPacketHandled(true);
		});
	}
}